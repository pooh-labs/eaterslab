from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path('download/EatersLab.apk', views.artifact_response),
    path('download/EatersLabBeta.apk', views.artifact_beta_response),
]
