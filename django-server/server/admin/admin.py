from django.apps import apps
from django.conf.urls import url
from django.contrib.admin import AdminSite
from django.contrib.admin import ModelAdmin
from django.utils.html import format_html

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


class CameraAdmin(ModelAdmin):
    list_display = ['id', 'state_with_icon']

    def state_with_icon(self, obj):
        fill_color = '#000000'
        if obj.state == Camera.State.ONLINE:
            fill_color = '#00AA00'
        elif obj.state == Camera.State.OFFLINE:
            fill_color = '#CCCCCC'
        elif obj.state == Camera.State.LOST_CONNECTION:
            fill_color = '#FF9900'
        return format_html(
            '<svg viewbox="0 0 26 26" style="height: 0.8em; width: 0.8em;"><circle fill="{}" cx="13" cy="14" r="10"/></svg> {}',
            fill_color, obj.get_state_display()
        )

    state_with_icon.short_description = 'State'


admin_site = MyAdminSite(name='admin')
admin_site.register(Cafeteria)
admin_site.register(CameraEvent)
admin_site.register(Camera, CameraAdmin)
admin_site.register(FixedMenuOption)
admin_site.register(FixedMenuOptionReview)
admin_site.register(MenuOptionTag)
