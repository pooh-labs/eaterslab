from modeltranslation.translator import translator, register, TranslationOptions
from .models import Camera, Cafeteria, FixedMenuOption


class CafeteriaTranslationOptions(TranslationOptions):
    fields = ('name', 'description', 'sub_description')


class FixedMenuOptionTranslationOptions(TranslationOptions):
    fields = ('name', )


translator.register(Cafeteria, CafeteriaTranslationOptions)
translator.register(FixedMenuOption, FixedMenuOptionTranslationOptions)
