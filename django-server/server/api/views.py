from datetime import datetime
from os.path import join as path_join

from django_filters import rest_framework as filters
from django.core.files.storage import FileSystemStorage

from rest_framework import views, viewsets
from rest_framework.authentication import TokenAuthentication
from rest_framework.parsers import FileUploadParser
from rest_framework.permissions import IsAdminUser
from rest_framework.response import Response

from .serializers import *
from .models import *

from os.path import join as path_join
from server import settings


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


# Admin authenticated with token uploads can inherit from this class
class AdminUploadView(views.APIView):
    parser_classes = [FileUploadParser]
    permission_classes = (IsAdminUser,)
    authentication_classes = (TokenAuthentication,)

    def __init__(self, save_file_path, **kwargs):
        super().__init__(**kwargs)
        self.file_path = save_file_path

    def put(self, request, filename, format=None):
        print(request.body)
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
