from django.conf.urls import url
from django.contrib.admin import AdminSite

from .models import *
from .views import StatsView

class MyAdminSite(AdminSite):
    site_header = 'EatersLab administration'

    def get_urls(self):
        urls = super(MyAdminSite, self).get_urls()
        custom_urls = [
            #url(r'stats/$', self.admin_view(stats), name="stats"),
            url('stats/$', self.admin_view(StatsView.as_view()), name='stats'),
        ]
        return urls + custom_urls

admin_site = MyAdminSite(name='myadmin')
admin_site.register(Cafeteria)
admin_site.register(CameraEvent)
admin_site.register(Camera)
admin_site.register(FixedMenuOption)
admin_site.register(FixedMenuOptionReview)
admin_site.register(MenuOptionTag)
