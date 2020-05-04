from django.urls import include, path
from rest_framework import routers
from . import views

router = routers.DefaultRouter()
router.register(r'cafeterias', views.CafeteriaViewSet)

urlpatterns = [
    path('', include(router.urls)),
    path('cameras', views.events_batch),  # temporary
    path('cameras/<int:camera_id>/events', views.event),
    path('upload/artifacts/<str:filename>/', views.UploadArtifactsView.as_view()),
    path('upload/artifacts/beta/<str:filename>/', views.UploadArtifactsBetaView.as_view()),
]
