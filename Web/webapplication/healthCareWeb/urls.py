from django.conf.urls import patterns, include, url
from django.contrib import admin
from healthCareWeb import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'^doctorsView/', include('doctorsView.urls')),
    url(r'^admin/', include(admin.site.urls)),
)
