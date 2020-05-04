from django.contrib import admin
from .models import MenuOptionTag, Cafeteria, FixedMenuOption, FixedMenuOptionReview

admin.site.register(Cafeteria)
admin.site.register(FixedMenuOption)
admin.site.register(FixedMenuOptionReview)
admin.site.register(MenuOptionTag)
