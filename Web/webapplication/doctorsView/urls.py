from django.conf.urls import patterns, url

from doctorsView import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'registerUser$',views.registerUser,name='registerUser'),
    url(r'addSensor$',views.addSensor,name='addSensor'),
    
)