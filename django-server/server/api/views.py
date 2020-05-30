import abc
from calendar import monthrange
from datetime import datetime
from dateutil.parser import parse as timestamp_parse
from os.path import join as path_join
from pytz import utc

from django.core.files.storage import FileSystemStorage
from django.db.models import QuerySet
from django.utils.decorators import method_decorator
from django_filters import rest_framework as filters
from drf_yasg.openapi import Parameter, IN_HEADER, TYPE_STRING
from drf_yasg.utils import swagger_auto_schema

from rest_framework import views, viewsets, generics
from rest_framework.authentication import TokenAuthentication
from rest_framework.parsers import FileUploadParser
from rest_framework.permissions import IsAdminUser
from rest_framework.response import Response
from server import settings

from .models import *
from .serializers import *


accept_language_header = Parameter('Accept-Language', IN_HEADER, description='Language for response content', type=TYPE_STRING)
accept_language_decorator = swagger_auto_schema(manual_parameters=[accept_language_header])


class GetPostViewSet(viewsets.ModelViewSet):
    http_method_names = ['get', 'post']


class PostViewSet(viewsets.ModelViewSet):
    http_method_names = ['post']


class CafeteriaFilterSet(filters.FilterSet):
    opened_now = filters.BooleanFilter(method='get_opened_now')
    prefix_name = filters.CharFilter(method='get_name_prefix')
    owner_id = filters.NumberFilter(method='get_for_owner')

    class Meta:
        model = Cafeteria
        fields = ['opened_from', 'opened_to', 'opened_now', 'prefix_name', 'owner_id']

    def get_opened_now(self, queryset, field_name, value):
        opened = value
        now = datetime.now().time()
        if opened is True:
            return queryset.filter(opened_from__lte=now).filter(opened_to__gte=now)
        elif opened is False:
            return (queryset.filter(opened_from__gt=now) | queryset.filter(opened_to__lt=now)).distinct()
        else:
            return queryset

    def get_name_prefix(self, queryset, field_name, value):
        if value is None:
            return queryset

        prefix = value.strip()
        return queryset.filter(name__istartswith=prefix)

    def get_for_owner(self, queryset, field_name, value):
        owner_id = value
        if owner_id is None:
            return queryset
        return queryset.filter(owner_id=owner_id)


@method_decorator(name='list', decorator=accept_language_decorator)
class CafeteriaViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = Cafeteria.objects.all().order_by('id')
    serializer_class = CafeteriaSerializer
    filter_backends = [filters.DjangoFilterBackend]
    filterset_class = CafeteriaFilterSet


class CameraViewSet(viewsets.ModelViewSet):
    http_method_names = []
    queryset = Camera.objects.all()
    serializer_class = CameraSerializer


class CameraEventsViewSet(viewsets.ModelViewSet):
    http_method_names = ['post']
    serializer_class = CameraEventSerializer

    def get_queryset(self):
        if getattr(self, 'swagger_fake_view', False):
            # queryset just for schema generation metadata
            return CameraEvent.objects.none()
        return CameraEvent.objects.filter(camera_id=self.kwargs['camera_pk'])


@method_decorator(name='list', decorator=accept_language_decorator)
class FixedMenuOptionViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = FixedMenuOptionSerializer

    def get_queryset(self):
        if getattr(self, 'swagger_fake_view', False):
            # queryset just for schema generation metadata
            return FixedMenuOption.objects.none()
        return FixedMenuOption.objects.all().filter(cafeteria=self.kwargs['cafeteria_pk']).order_by('id')


class FixedMenuOptionReviewViewSet(PostViewSet):
    queryset = FixedMenuOptionReview.objects.all().order_by('id')
    serializer_class = FixedMenuOptionReviewSerializer


class CafeteriaFixedMenuOptionReviewViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = FixedMenuOptionReviewSerializer

    def get_queryset(self):
        if getattr(self, 'swagger_fake_view', False):
            # queryset just for schema generation metadata
            return FixedMenuOptionReview.objects.none()
        return FixedMenuOptionReview.objects.all().filter(option_id=self.kwargs['option_pk']).order_by('id')


class StatsDivider:
    @abc.abstractmethod
    def get_timestamp_delta(self, time: datetime):
        pass

    def get_timestamp_name(self, time: datetime):
        return time.isoformat()


class HourStatsDivider(StatsDivider):
    def get_timestamp_delta(self, time: datetime):
        return timedelta(hours=1)


class DayStatsDivider(StatsDivider):
    def get_timestamp_delta(self, time: datetime):
        return timedelta(days=1)


class WeekStatsDivider(StatsDivider):
    def get_timestamp_delta(self, time: datetime):
        return timedelta(weeks=1)


class MonthStatsDivider(StatsDivider):
    def get_timestamp_delta(self, time: datetime):
        return timedelta(days=monthrange(time.year, time.month)[1])


class YearStatsDivider(StatsDivider):
    def get_timestamp_delta(self, time: datetime):
        return timedelta(days=(time.replace(year=time.year + 1) - time).days)


class TimeStampedFilterSet(filters.FilterSet):
    start_timestamp = filters.DateTimeFilter(required=False)
    end_timestamp = filters.DateTimeFilter(required=False)
    count = filters.NumberFilter(required=False)
    group_by = filters.CharFilter(required=False)


LONGEST_SUPPORTED_STATS_LEN = 1024
DEFAULT_GROUP_BY = HourStatsDivider


class StatsView(generics.ListAPIView):
    filter_backends = [filters.DjangoFilterBackend]
    filterset_class = TimeStampedFilterSet

    @abc.abstractmethod
    def get_full_queryset(self, cafeteria_id):
        pass

    @abc.abstractmethod
    def timestamp_field_name(self):
        pass

    @abc.abstractmethod
    def init_value(self, before_queryset: QuerySet):
        pass

    @abc.abstractmethod
    def next_count_value(self, count_value, curr_queryset: QuerySet):
        pass

    @abc.abstractmethod
    def map_to_result_objects(self, index, value, timestamp, cafeteria_pk):
        pass

    def filter_queryset(self, queryset):
        return queryset

    def get_queryset(self):
        if getattr(self, 'swagger_fake_view', False):
            # queryset just for schema generation metadata
            return []
        cafeteria_pk = self.kwargs.get('cafeteria_pk')
        req_count = self.request.query_params.get('count')
        start_string = self.request.query_params.get('start_timestamp')
        end_string = self.request.query_params.get('end_timestamp')
        group_by_string = self.request.query_params.get('group_by')
        divider = get_divider(group_by_string)()

        if start_string is None:
            timestamp_start = datetime.min + divider.get_timestamp_delta(datetime.min)
        else:
            timestamp_start = timestamp_parse(start_string)

        if end_string is None:
            timestamp_end = datetime.max - divider.get_timestamp_delta(datetime.max)
        else:
            timestamp_end = timestamp_parse(end_string)

        if cafeteria_pk is None:
            raise ValueError('required params not specified')
        count = LONGEST_SUPPORTED_STATS_LEN if req_count is None else min(int(req_count), LONGEST_SUPPORTED_STATS_LEN)

        lookup_lte = '%s__lte' % self.timestamp_field_name()
        lookup_gt = '%s__gt' % self.timestamp_field_name()

        data = self.get_full_queryset(cafeteria_pk)
        before_data = data.filter(**{lookup_lte: timestamp_start}).order_by(self.timestamp_field_name())
        count_value = self.init_value(before_data)
        begin_stamp = timestamp_start
        end_stamp = timestamp_start + divider.get_timestamp_delta(timestamp_start)

        results = [(count_value, begin_stamp)]
        for interval_i in range(int(count) - 1):
            finish_now = False
            if end_stamp.astimezone() > datetime.now().astimezone() \
                    or end_stamp.astimezone() > timestamp_end.astimezone():
                end_stamp = datetime.now()
                finish_now = True

            curr_queryset = data.filter(**{lookup_gt: begin_stamp, lookup_lte: end_stamp}) \
                .order_by(self.timestamp_field_name())
            count_value = self.next_count_value(count_value, curr_queryset)
            begin_stamp += divider.get_timestamp_delta(begin_stamp)
            end_stamp += divider.get_timestamp_delta(begin_stamp)
            results.append((count_value, begin_stamp))

            if finish_now:
                break

        return [self.map_to_result_objects(index, value, timestamp, cafeteria_pk)
                for index, (value, timestamp) in enumerate(results)]


def get_divider(group_by):
    available = {
        'hour': HourStatsDivider,
        'day': DayStatsDivider,
        'week': WeekStatsDivider,
        'month': MonthStatsDivider,
        'year': YearStatsDivider
    }
    return available.get(group_by, DEFAULT_GROUP_BY)


def count_people(queryset):
    counter = 0
    for event in queryset:
        if event.event_type == CameraEvent.EventType.PERSON_ENTERED.value:
            counter += 1
        elif event.event_type == CameraEvent.EventType.PERSON_LEFT.value:
            counter -= 1
    return counter


class OccupancyStatsView(StatsView):
    serializer_class = OccupancyStatsSerializer

    def get_full_queryset(self, cafeteria_id):
        return CameraEvent.objects.filter(cafeteria_id=cafeteria_id)

    def timestamp_field_name(self):
        return 'timestamp'

    def init_value(self, before_queryset: QuerySet):
        overrides = before_queryset.filter(event_type=CameraEvent.EventType.OCCUPANCY_OVERRIDE.value)
        if len(overrides) == 0:
            init_override_value = 0
            changed = count_people(before_queryset)
        else:
            init_override_value = overrides.latest(self.timestamp_field_name()).event_value
            changed = count_people(
                before_queryset.filter(timestamp__gt=overrides.latest(self.timestamp_field_name()).timestamp))
        return init_override_value + changed

    def next_count_value(self, count_value, curr_queryset: QuerySet):
        curr_overrides = curr_queryset.filter(event_type=CameraEvent.EventType.OCCUPANCY_OVERRIDE.value)
        if len(curr_overrides) == 0:
            count_value += count_people(curr_queryset)
        else:
            last_override = curr_overrides.latest(self.timestamp_field_name())
            after_override = curr_queryset.filter(timestamp__gt=last_override.timestamp)
            count_value = last_override.event_value + count_people(after_override)
        return count_value

    def map_to_result_objects(self, index, value, timestamp, cafeteria_pk):
        capacity = Cafeteria.objects.get(id=cafeteria_pk).capacity
        return OccupancyStatsData(id=index,
                                  timestamp=timestamp,
                                  occupancy=value,
                                  occupancy_relative=(float(value) / float(capacity)))


AVAILABLE_STATS = [
    ('occupancy', OccupancyStatsView),
]


# Admin authenticated with token uploads can inherit from this class
class AdminUploadView(views.APIView):
    parser_classes = [FileUploadParser]
    permission_classes = (IsAdminUser,)
    authentication_classes = (TokenAuthentication,)

    def __init__(self, save_file_path, **kwargs):
        super().__init__(**kwargs)
        self.file_path = save_file_path

    def put(self, request, *args, **kwargs):
        return handle_file_as_chunked(request, self.file_path)


class UploadArtifactsView(AdminUploadView):

    def __init__(self, **kwargs):
        super().__init__(path_join(settings.ARTIFACTS_ROOT, settings.ARTIFACT_NAME), **kwargs)


class UploadArtifactsBetaView(AdminUploadView):

    def __init__(self, **kwargs):
        super().__init__(path_join(settings.ARTIFACTS_ROOT_BETA, settings.ARTIFACT_NAME), **kwargs)


def handle_file_as_chunked(request, file_path):
    file_obj = request.data['file']
    with FileSystemStorage().open(file_path, 'wb+') as destination:
        for chunk in file_obj.chunks():
            destination.write(chunk)
    return Response(status=204)
