from django.contrib import admin

from .models import (Cafeteria, FixedMenuOption, FixedMenuOptionReview,
                     MenuOptionTag)

admin.site.register(Cafeteria)
admin.site.register(FixedMenuOption)
admin.site.register(FixedMenuOptionReview)
admin.site.register(MenuOptionTag)
