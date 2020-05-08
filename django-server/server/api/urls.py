from django.urls import include, path
from rest_framework_nested.routers import DefaultRouter, NestedSimpleRouter

from .views import *


router = DefaultRouter()
router.register(r'cafeterias', CafeteriaViewSet)
router.register(r'menu_option_tags', MenuOptionTagViewSet)
router.register(r'fixed_menu_reviews', FixedMenuOptionReviewViewSet)

cafeterias_router = NestedSimpleRouter(router, r'cafeterias', lookup='cafeteria')
cafeterias_router.register(r'fixed_menu_options',
                           FixedMenuOptionViewSet,
                           basename='cafeteria-fixed_menu_options')

fixed_menu_options_router = NestedSimpleRouter(cafeterias_router, r'fixed_menu_options', lookup='option')
fixed_menu_options_router.register(r'reviews',
                                   CafeteriaFixedMenuOptionReviewViewSet,
                                   basename='cafeteria-fixed_menu_options-review')

urlpatterns = [
    path('', include(router.urls)),
    path('cameras/<int:camera_id>/events', CameraEventsView.as_view()),
    path('', include(cafeterias_router.urls)),
    path('', include(fixed_menu_options_router.urls)),
    path('upload/artifacts/<str:filename>/', UploadArtifactsView.as_view()),
    path('upload/artifacts/beta/<str:filename>/', UploadArtifactsBetaView.as_view()),
]
