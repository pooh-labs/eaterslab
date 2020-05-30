"""
Django settings for server project.

Generated by 'django-admin startproject' using Django 3.0.4.

For more information on this file, see
https://docs.djangoproject.com/en/3.0/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/3.0/ref/settings/
"""

import os

import environ
import sys
import logging

from django.utils.translation import gettext_lazy as _

env = environ.Env()
environ.Env.read_env()

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/3.0/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = env.str('SECRET_KEY')

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = env.bool('DEBUG', default=False)

if DEBUG:
    logging.basicConfig(stream=sys.stderr, level=logging.INFO)

ALLOWED_HOSTS = []

if env.str('ALLOWED_HOSTS', default=""):
    ALLOWED_HOSTS = [env.str('ALLOWED_HOSTS')]

# Application definition

INSTALLED_APPS = [
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',

    'modeltranslation', # Model translations
    'django_filters',
    'rest_framework',
    'rest_framework.authtoken',  # token auth for artifacts upload
    'drf_yasg',  # for API documentation and generation

    'admin.apps.AdminConfig',  # Admin
    'home.apps.HomeConfig',  # Home app
    'api.apps.ApiConfig',  # API app
]

MIDDLEWARE = [
    'whitenoise.middleware.WhiteNoiseMiddleware',  # for static files handling on deploy
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.locale.LocaleMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    'django.middleware.security.SecurityMiddleware',  # for ssl handling in Django
    'api.middleware.ApiCallLanguageMiddleware', # For setting language in API calls
]

ROOT_URLCONF = 'server.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

WSGI_APPLICATION = 'server.wsgi.application'

# Configuration for rest_framework to get only the json data
# from the api calls to specified urls as default

REST_FRAMEWORK = {
    'DEFAULT_RENDERER_CLASSES': [
        'rest_framework.renderers.JSONRenderer',
        # Uncomment to get the API renderer for browser view
        # 'rest_framework.renderers.BrowsableAPIRenderer',
    ],
    'DEFAULT_FILTER_BACKENDS': [
        'url_filter.integrations.drf.DjangoFilterBackend',
    ]
}


# Database
# https://docs.djangoproject.com/en/3.0/ref/settings/#databases

def select_database():
    if env.bool('DB_DEPLOY', default=False):
        return env.db()
    if env.bool('USE_POSTGRES', default=False):
        if DEBUG:
            logging.info("Using postgres")
        return env.db()
    if DEBUG:
        logging.info("Using sqlite")
    return {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
    }


DATABASES = {
    'default': select_database()
}

# Password validation
# https://docs.djangoproject.com/en/3.0/ref/settings/#auth-password-validators

AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]

# Internationalization
# https://docs.djangoproject.com/en/3.0/topics/i18n/

LANGUAGES = (
    ('pl', _('Polish')),
    ('en', _('English')),
)

LANGUAGE_CODE = 'pl'
MODELTRANSLATION_DEFAULT_LANGUAGE = 'pl'

TIME_ZONE = 'Europe/Warsaw'

USE_I18N = True

USE_L10N = True

USE_TZ = True

LOCALE_PATHS = (
    os.path.join(BASE_DIR, 'locale'),
)

# API configuration
API_PATH_PREFIX = 'api/'

# SSL enable configuration for server

ENABLE_SSL = env.bool('SSL', default=True)
SECURE_SSL_REDIRECT = ENABLE_SSL
SESSION_COOKIE_SECURE = ENABLE_SSL
CSRF_COOKIE_SECURE = ENABLE_SSL
SECURE_PROXY_SSL_HEADER = ('HTTP_X_FORWARDED_PROTO', 'https')

# Heroku static files configuration to get it working on deploy

STATIC_ROOT = os.path.join(BASE_DIR, 'static')
STATIC_URL = '/static/'
STATICFILES_STORAGE = 'whitenoise.storage.CompressedManifestStaticFilesStorage'
os.makedirs(STATIC_ROOT, exist_ok=True)

# Configuration for artifacts of mobile client uploading

ARTIFACT_NAME = 'EatersLab.apk'
ARTIFACTS_ROOT = os.path.join(BASE_DIR, 'artifact')
ARTIFACTS_ROOT_BETA = os.path.join(ARTIFACTS_ROOT, 'beta')

DATA_UPLOAD_MAX_MEMORY_SIZE = 10485760  # set max limit of uploaded file to 10 MB
