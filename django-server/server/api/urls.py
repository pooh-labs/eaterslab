from django.urls import include, path
from rest_framework_nested.routers import DefaultRouter, NestedSimpleRouter

from .views import *


router = DefaultRouter()
router.register(r'cameras', CameraViewSet)
router.register(r'cafeterias', CafeteriaViewSet)
router.register(r'fixed_menu_reviews', FixedMenuOptionReviewViewSet)

cafeterias_router = NestedSimpleRouter(router, r'cafeterias', lookup='cafeteria')
cafeterias_router.register(r'fixed_menu_options',
                           FixedMenuOptionViewSet,
                           basename='cafeteria-fixed_menu_options')

fixed_menu_options_router = NestedSimpleRouter(cafeterias_router, r'fixed_menu_options', lookup='option')
fixed_menu_options_router.register(r'reviews',
                                   CafeteriaFixedMenuOptionReviewViewSet,
                                   basename='cafeteria-fixed_menu_options-review')

cameras_router = NestedSimpleRouter(router, r'cameras', lookup='camera')
cameras_router.register(r'events', CameraEventsViewSet, basename='camera_events')

urlpatterns = [
    path('', include(router.urls)),
    path('', include(cafeterias_router.urls)),
    path('', include(fixed_menu_options_router.urls)),
    path('', include(cameras_router.urls)),
    path('upload/artifacts/<str:filename>/', UploadArtifactsView.as_view()),
    path('upload/artifacts/beta/<str:filename>/', UploadArtifactsBetaView.as_view()),
]
urlpatterns.extend([
    path('cafeterias/<int:cafeteria_pk>/stats/{}'.format(name), view.as_view())
    for (name, view) in AVAILABLE_STATS
])
