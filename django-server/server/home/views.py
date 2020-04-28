from django.core.files.storage import FileSystemStorage
from django.http import HttpResponse, FileResponse
from os.path import join as path_join

from server import settings


def index(request):
    return HttpResponse("Welcome to EatersLab made by PoohLab.")


def artifact_response(response):
    return existing__binary_file_response(path_join(settings.ARTIFACTS_ROOT, settings.ARTIFACT_NAME))


def artifact_beta_response(response):
    return existing__binary_file_response(path_join(settings.ARTIFACTS_ROOT_BETA, settings.ARTIFACT_NAME))


def existing__binary_file_response(path):
    try:
        file = FileSystemStorage().open(path, 'rb')
        return FileResponse(file, as_attachment=True)
    except FileNotFoundError:
        return HttpResponse("No file on server")

