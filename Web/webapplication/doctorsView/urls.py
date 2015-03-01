from django.conf.urls import patterns, url

from doctorsView import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
    url(r'registerUser$',views.registerUser,name='registerUser'),
    url(r'addSensor$',views.addSensor,name='addSensor'),
    url(r'viewUsers$',views.viewUsers,name='viewUsers'),
    url(r'getUserByLastName$',views.getUserByLastName,name='getUserByLastName'),
    url(r'search',views.search,name='search'),
    url(r'listAllergies',views.getAllergiesList,name='getAllergiesList'),
    url(r'addUserAllergies',views.addUserAllergies,name='addUserAllergies'),
    url(r'addUserVaccination',views.addUserVaccination,name='createVaccination'),
)