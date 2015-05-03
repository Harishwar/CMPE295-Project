from django.conf.urls import patterns, url

from doctorsView import views

urlpatterns = patterns('',
    #url(r'^$', views.index, name='index'),
    url(r'addPatient$',views.addPatient,name='addPatient'),
    url(r'registerUser',views.addPatient,name='registerUser'),
    url(r'addSensor',views.addSensor,name='addSensor'),
    url(r'viewUsers$',views.viewUsers,name='viewUsers'),
    url(r'getUserByLastName$',views.getUserByLastName,name='getUserByLastName'),
    url(r'search',views.search,name='search'),
    url(r'listAllergies',views.getAllergiesList,name='getAllergiesList'),
    url(r'addUserAllergies',views.addUserAllergies,name='addUserAllergies'),
    url(r'addUserVaccination',views.addUserVaccination,name='createVaccination'),
    url(r'deleteUser$',views.deleteUser,name='deleteUser'),
    url(r'deleteUserAllergy',views.deleteUserAllergy,name='deleteUserAllergy'),
    url(r'login',views.login_user,name='login_user'),
    url(r'logout',views.logout_user,name='logout_doctor'),
    url(r'getData',views.load_user_temp,name='graph_load'),
    url(r'dashboard',views.dashboard,name='dashboard'),
    url(r'getUsersData',views.dashboard_doc_req,name='graph_load_all'),
    url(r'alertUser',views.sendAlert,name='alertUser'),
    url(r'settings',views.settings,name='settings'),
    url(r'asn',views.dashboard_user,name='asn')
)