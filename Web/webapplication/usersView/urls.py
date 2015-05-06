from django.conf.urls import patterns, url

from usersView import views
import doctorsView

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'viewCharts$',views.viewCharts,name='viewCharts'),
    url(r'load_graph',doctorsView.views.load_user_temp),
    url(r'load_humidity',doctorsView.views.load_user_humidity),
    url(r'load_heat_map',doctorsView.views.dashboard_req),
    url(r'viewProfile',views.viewUserProfile,name='viewUserProfile'),
    url(r'editProfile',views.editUserProfile,name='editUserProfile'),
    url(r'sensorHistory',views.sensorHistory,name='sensorHistory'),
    url(r'loadSensorHistory',views.loadSensorHistory,name='loadSensorHistory'),
    url(r'logout',doctorsView.views.logout_user,name='logout_user'),
    url(r'loadBMI',views.loadBMI,name='loadBMI'),
    url(r'loadTemperature',views.loadTemperature,name='loadTemperature'),
    url(r'loadalerts',views.loadAlerts,name='loadAlerts'),
    url(r'loadAllergies',doctorsView.views.loadAllergies,name='loadAllergies'),
    url(r'loadVaccinations',doctorsView.views.loadVaccinations,name='loadVaccinations'),
    )
