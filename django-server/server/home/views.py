from django.http import HttpResponse, FileResponse
from os.path import join as path_join

from server import settings


def index(request):
    return HttpResponse("Welcome to EatersLab made by PoohLab.")


def artifact_response(response):
    file = open(path_join(settings.ARTIFACTS_ROOT, settings.ARTIFACT_NAME), 'rb')
    return FileResponse(file)


def artifact_beta_response(response):
    file = open(path_join(settings.ARTIFACTS_ROOT_BETA, settings.ARTIFACT_NAME), 'rb')
    return FileResponse(file)