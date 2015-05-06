"""
Django settings for healthCareWeb project.

For more information on this file, see
https://docs.djangoproject.com/en/1.7/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.7/ref/settings/
"""

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
from django.conf.global_settings import EMAIL_HOST, EMAIL_PORT, LOGIN_URL,\
    LOGIN_REDIRECT_URL
from urllib import localhost
import doctorsView
BASE_DIR = os.path.dirname(os.path.dirname(__file__))


# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.7/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '$mz7g8_*(6q%2unpee)(lq(dg1+zu3l&3627a01me4#%2r#1hq'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True
TEMPLATE_DIRS = [os.path.join(BASE_DIR, 'templates')]
ALLOWED_HOSTS = ['*']
#AUTH_USER_MODEL='doctorsView.Users'

# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'doctorsView',
    'usersView'
)

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'healthCareWeb.urls'

WSGI_APPLICATION = 'healthCareWeb.wsgi.application'

# Database
# https://docs.djangoproject.com/en/1.7/ref/settings/#databases

DATABASES = {
    'default': {
        'HOST':'cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com',
        'PORT':'3306',
        'NAME': 'CMPE295B',
        'ENGINE': 'django.db.backends.mysql',
        'USER': 'root',
        'PASSWORD': '!passw0rd',
    },
    'sensors': {
        'HOST':'cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com',
        'PORT':'3306',
        'NAME': 'SensorResults',
        'ENGINE': 'django.db.backends.mysql',
        'USER': 'root',
        'PASSWORD': '!passw0rd',
    }
}

# Internationalization
# https://docs.djangoproject.com/en/1.7/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True
#http://stackoverflow.com/questions/11916754/djangosetting-up-email
EMAIL_USE_TLS = True
EMAIL_HOST = 'smtp.gmail.com'
EMAIL_HOST_USER = 'fitwhiz@gmail.com'
EMAIL_HOST_PASSWORD = '!passw0rd'
EMAIL_PORT = 587
LOGIN_URL='/doctorsView/login'
#LOGOUT_URL='/doctorsView/logout'
#LOGIN_REDIRECT_URL='/doctorsView/*'

# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.7/howto/static-files/
STATIC_ROOT = os.path.join(BASE_DIR, "var", "www")

STATIC_URL = '/static/'

STATICFILES_DIRS = (
    os.path.join(BASE_DIR, "static"),
    '/var/www/',
)

#Logging enabled
#refered from http://stackoverflow.com/questions/1598823/elegant-setup-of-python-logging-in-django
LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'handlers': {
        'file': {
            'level': 'DEBUG',
            'class': 'logging.FileHandler',
            'filename': os.path.join(BASE_DIR, 'logs/django.log'),
        },
    },
    'loggers': {
        'django.request': {
            'handlers': ['file'],
            'level': 'DEBUG',
            'propagate': True,
        },
    },
}