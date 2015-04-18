from django.conf.urls import patterns, url

from usersView import views
import doctorsView

urlpatterns = patterns('',
    #url(r'^$', views.index, name='index'),
    url(r'viewDashBoard$',views.viewDashBoard,name='viewDashboard'),
    url(r'load_graph',doctorsView.views.dashboard_req),
    url(r'viewProfile',views.viewUserProfile,name='viewUserProfile'),
    url(r'editProfile',views.editUserProfile,name='editUserProfile'),
    url(r'logout',doctorsView.views.logout_user,name='logout_user'),
    )
