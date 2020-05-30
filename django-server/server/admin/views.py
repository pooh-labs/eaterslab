from django.contrib import admin
from django.utils.translation import gettext_lazy as _
from django.views.generic.base import TemplateView

class StatsView(TemplateView):
    admin_site = None
    template_name = "admin/stats.html"

    def get_context_data(self, **kwargs):
        context = super().get_context_data(**{
            **kwargs,
            **self.admin_site.each_context(self.request),
        })
        context['title'] = _('Statistics')
        return context
