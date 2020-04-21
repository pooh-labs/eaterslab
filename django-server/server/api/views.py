from django.core.files.storage import FileSystemStorage
from rest_framework import viewsets
from rest_framework import views
from rest_framework.authentication import TokenAuthentication
from rest_framework.parsers import FileUploadParser
from rest_framework.permissions import IsAdminUser
from rest_framework.response import Response

from server import settings
from .serializers import CafeteriaSerializer

from .models import Cafeteria

from os.path import join as path_join


# TODO: handle different roles for API views

class CafeteriaViewSet(viewsets.ModelViewSet):
    queryset = Cafeteria.objects.all().order_by('id')
    serializer_class = CafeteriaSerializer


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
