from django.urls import include, path
from rest_framework import routers
from . import views

router = routers.DefaultRouter()
router.register(r'cafeterias', views.CafeteriaViewSet)

urlpatterns = [
    path('', include(router.urls)),
    path('cameras', views.events_batch),  # temporary
    path('cameras/<int:camera_id>/events', views.event),
]
