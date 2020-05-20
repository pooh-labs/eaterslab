from django.apps import AppConfig


class AdminConfig(AppConfig):
    name = 'admin'
    label= 'myadmin'
    default_site = 'admin.MyAdminSite'
