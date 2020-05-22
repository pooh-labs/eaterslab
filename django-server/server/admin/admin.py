from django.apps import apps
from django.conf.urls import url
from django.contrib.admin import AdminSite

from .views import StatsView

from api.models import *

class MyAdminSite(AdminSite):
    index_template = 'admin/index_override.html'

    def get_urls(self):
        urls = super(MyAdminSite, self).get_urls()
        custom_urls = [
            url('stats/$', self.admin_view(StatsView.as_view(admin_site=self)), name='stats'),
        ]
        return urls + custom_urls


admin_site = MyAdminSite(name='admin')
admin_site.register(Cafeteria)
admin_site.register(CameraEvent)
admin_site.register(Camera)
admin_site.register(FixedMenuOption)
admin_site.register(FixedMenuOptionReview)
admin_site.register(MenuOptionTag)
