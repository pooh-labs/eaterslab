from django.urls import include, path
from rest_framework import routers
from . import views

router = routers.DefaultRouter()
router.register(r'cafeterias', views.CafeteriaViewSet)
router.register(r'menu_option_tags', views.MenuOptionTagViewSet)

urlpatterns = [
    path('', include(router.urls)),
    path('upload/artifacts/<str:filename>/', views.UploadArtifactsView.as_view()),
    path('upload/artifacts/beta/<str:filename>/', views.UploadArtifactsBetaView.as_view()),
]
