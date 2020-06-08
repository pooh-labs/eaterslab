from os.path import join as path_join
from django.shortcuts import render

from django.core.files.storage import FileSystemStorage
from django.http import FileResponse, HttpResponse

from server import settings


def index(request):
    return render(request, 'home/home.html', {'artifact_name': settings.ARTIFACT_NAME})


def artifact_response(response):
    return existing__binary_file_response(path_join(settings.ARTIFACTS_ROOT, settings.ARTIFACT_NAME))


def existing__binary_file_response(path):
    try:
        file = FileSystemStorage().open(path, 'rb')
        return FileResponse(file, as_attachment=True)
    except FileNotFoundError:
        return HttpResponse("No file on server")
