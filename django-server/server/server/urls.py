"""server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from admin.admin import admin_site
from django.contrib import admin
from django.urls import include, path
from rest_framework import permissions
from django.conf.urls.i18n import i18n_patterns

from drf_yasg import openapi
from drf_yasg.views import get_schema_view

# Code to generate API view
schema_view = get_schema_view(
   openapi.Info(
      title="EatersLab API",
      default_version='v1',
   ),
   public=True,
   permission_classes=(permissions.AllowAny,),
)

urlpatterns = i18n_patterns(
    path('', include('home.urls')),
    path('admin/', admin_site.urls),

    # Turn of /pl/ prefix
    prefix_default_language=False
)

urlpatterns += [
    path('api/beta/', include('api.urls')),
    # For documentation generation with drf_yasg
    path(r'api.yaml', schema_view.without_ui(cache_timeout=0), name='schema-json'),
    path(r'doc', schema_view.with_ui('swagger', cache_timeout=0), name='schema-swagger-ui'),
]
