import abc
from calendar import monthrange
from datetime import datetime
from dateutil.parser import parse
from os.path import join as path_join

from django.core.files.storage import FileSystemStorage
from django_filters import rest_framework as filters
from rest_framework import views, viewsets, generics
from rest_framework.authentication import TokenAuthentication
from rest_framework.parsers import FileUploadParser
from rest_framework.permissions import IsAdminUser
from rest_framework.response import Response
from server import settings

from .models import *
from .serializers import *


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


class FixedMenuOptionViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = FixedMenuOptionSerializer

    def get_queryset(self):
        if getattr(self, 'swagger_fake_view', False):
            # queryset just for schema generation metadata
            return FixedMenuOption.objects.none()
        return FixedMenuOption.objects.all().filter(cafeteria=self.kwargs['cafeteria_pk']).order_by('id')


class MenuOptionTagViewSet(GetPostViewSet):
    queryset = MenuOptionTag.objects.all().order_by('name')
    serializer_class = MenuOptionTagSerializer


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


class TimeStampedFilterSet(filters.FilterSet):
    timestamp_start = filters.DateTimeFilter(required=True)
    count = filters.NumberFilter(required=True)


class StatsView(generics.ListAPIView):
    serializer_class = OccupancyStatsSerializer
    filter_backends = [filters.DjangoFilterBackend]
    filterset_class = TimeStampedFilterSet

    def get_queryset(self):
        if getattr(self, 'swagger_fake_view', False):
            # queryset just for schema generation metadata
            return []
        id = self.kwargs.get('cafeteria_pk')
        count = self.request.query_params.get('count')
        timestamp_start = self.request.query_params.get('timestamp_start')
        if id is None or count is None or timestamp_start is None:
            raise ValueError('required params not specified')

        timestamp_start = parse(timestamp_start)
        events = CameraEvent.objects.filter(cafeteria_id=id)
        before = events.filter(timestamp__lte=timestamp_start).order_by('timestamp')
        overrides = before.filter(event_type=CameraEvent.EventType.OCCUPANCY_OVERRIDE.value)
        init_override_value = 0 if len(overrides) == 0 else overrides.latest('timestamp').event_value
        changed = self.count_people(before) if len(overrides) == 0 \
            else self.count_people(before.filter(timestamp__gt=overrides.latest('timestamp').timestamp))

        people_inside = init_override_value + changed
        begin = timestamp_start
        end = timestamp_start + self.get_timestamp_delta(timestamp_start)

        results = [(people_inside, begin)]
        for interval_i in range(int(count) - 1):
            curr_range = events.filter(timestamp__gt=begin, timestamp__lte=end).order_by('timestamp')
            curr_overrides = curr_range.filter(event_type=CameraEvent.EventType.OCCUPANCY_OVERRIDE.value)
            if len(curr_overrides) == 0:
                people_inside += self.count_people(curr_range)
            else:
                last_override = curr_overrides.latest('timestamp')
                people_inside = last_override + self.count_people(
                    curr_range.filter(timestamp__gt=last_override.timestamp))
            begin += self.get_timestamp_delta(begin)
            end += self.get_timestamp_delta(begin)
            results.append((people_inside, begin))

        occupancy = Cafeteria.objects.get(id=id).occupancy

        return [OccupancyStatsData(id=index, timestamp_name=self.get_timestamp_name(stamp), occupancy=inside,
                                   occupancy_relative=min(float(inside) / float(occupancy), 1.0))
                for index, (inside, stamp) in enumerate(results)]

    def count_people(self, queryset):
        counter = 0
        for event in queryset:
            if event.event_type == CameraEvent.EventType.PERSON_ENTERED.value:
                counter += 1
            elif event.event_type == CameraEvent.EventType.PERSON_LEFT.value:
                counter -= 1
        return counter

    def filter_queryset(self, queryset):
        return queryset

    @abc.abstractmethod
    def get_timestamp_delta(self, datetime: datetime):
        pass

    @abc.abstractmethod
    def get_timestamp_name(self, datetime: datetime):
        pass


class HourStatsView(StatsView):

    def get_timestamp_delta(self, datetime: datetime):
        return timedelta(hours=1)

    def get_timestamp_name(self, datetime: datetime):
        return '{}'.format(datetime.hour)


class DayStatsView(StatsView):

    def get_timestamp_delta(self, datetime: datetime):
        return timedelta(days=1)

    def get_timestamp_name(self, datetime: datetime):
        week_days = ['Mon', 'Tue', 'Wed', 'Thr', 'Fri', 'Sat', 'Sun']
        return week_days[datetime.weekday()]


class WeekStatsView(StatsView):

    def get_timestamp_delta(self, datetime: datetime):
        return timedelta(weeks=1)

    def get_timestamp_name(self, datetime: datetime):
        return '{} week'.format(datetime.isocalendar()[1])


class MonthStatsView(StatsView):

    def get_timestamp_delta(self, datetime: datetime):
        return timedelta(days=monthrange(datetime.year, datetime.month)[1])

    def get_timestamp_name(self, datetime: datetime):
        return '{}'.format(datetime.day)


class YearStatsView(StatsView):

    def get_timestamp_delta(self, datetime: datetime):
        return timedelta(days=(datetime.replace(year=datetime.year + 1) - datetime).days)

    def get_timestamp_name(self, datetime: datetime):
        return '{}'.format(datetime.year)


# Admin authenticated with token uploads can inherit from this class
class AdminUploadView(views.APIView):
    parser_classes = [FileUploadParser]
    permission_classes = (IsAdminUser,)
    authentication_classes = (TokenAuthentication,)

    def __init__(self, save_file_path, **kwargs):
        super().__init__(**kwargs)
        self.file_path = save_file_path

    def put(self, request, filename, format=None):
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
