from django.conf import settings
from django.utils import translation
from django.utils.translation import trans_real


class ApiCallLanguageMiddleware:
    supported_langs = [entry[0] for entry in settings.LANGUAGES]
    api_prefix = '/' + settings.API_PATH_PREFIX

    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        # Set language from request for API calls
        if request.path_info.startswith(self.api_prefix):
            request_lang = self.get_lang_from_http_header(request)
            if request_lang:
                translation.activate(request_lang)

        response = self.get_response(request)
        return response

    def get_lang_from_http_header(self, request):
        accept = request.META.get("HTTP_ACCEPT_LANGUAGE", "")
        for accept_lang, __ in trans_real.parse_accept_lang_header(accept):
            if accept_lang in self.supported_langs:
                return accept_lang
        return None
