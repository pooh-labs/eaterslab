from django.urls import path

from . import views
from server import settings

urlpatterns = [
    path('', views.index, name='index'),
    path('download/{}'.format(settings.ARTIFACT_NAME), views.artifact_response),
]
