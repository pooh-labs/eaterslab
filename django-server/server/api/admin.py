from django.apps import apps
from django.conf.urls import url
from django.contrib.admin import AdminSite

from .views import StatsView

class MyAdminSite(AdminSite):
    site_header = 'EatersLab administration'

    def get_urls(self):
        urls = super(MyAdminSite, self).get_urls()
        custom_urls = [
            url('stats/$', self.admin_view(StatsView.as_view()), name='stats'),
        ]
        return urls + custom_urls

admin_site = MyAdminSite(name='myadmin')
models = apps.get_models()
for model in models:
    try:
        admin_site.register(model)
    except admin.sites.AlreadyRegistered:
        pass
