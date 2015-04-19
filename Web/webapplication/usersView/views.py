from django.shortcuts import render
from doctorsView.models import Users
from django.db import transaction, connection
import collections
import datetime
import json
from django.http.response import JsonResponse,HttpResponse

def viewDashBoard(request):
    if request.session.get('user_id') and request.session.get('role_id')==2:
        #print "session id",request.session.get('role_id')
        context={'user':Users.objects.get(email=request.session.get('user_id'))}
        return render(request,'usersView/dashboard.html',context)
    else:
        return render(request,'index.html')

def viewUserProfile(request):
    
    user_email =request.GET.get('email')
    #try:
    if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.GET.get('email'))}
        #print context
        #print "context obj"
        #return JsonResponse(context);
        return render(request,'usersView/viewUser.html',context)
    else:
        return render(request,'index.html')

#     except:
#         return HttpResponse("Service Error!!!")

def editUserProfile(request):
    user_email =request.GET.get('email')
    if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.GET.get('email'))}
        return render(request,'usersView/editUser.html',context)
    else:
        return render(request,'index.html')

def sensorHistory(request):
        return render(request,'usersView/sensorHistory.html')

# def dashboard_req(request):
#         #print request.method
#         #print request.session.get('user_id')
#         email=request.GET.get('email')
#         #print '-------->'
#         #print load_user_data(email)
#         return JsonResponse(load_user_data(email),safe=False)
#
# def load_user_data(email):
#     try:
#         cursor=connection.cursor()
#         cursor.execute("select * from SensorResults.crunched_results where user_id=%s",[email])
#         rows=cursor.fetchall()
#         rowsList=[]
#         for row in rows:
#             graph_obj=collections.OrderedDict()
#             if isinstance(row[1], datetime.datetime):
#                 dateVal= row[1].isoformat()
#             graph_obj['x']=dateVal
#             graph_obj['y']=row[2]
#             graph_obj['user_id']=row[0]
#             rowsList.append(graph_obj)
#             #print row
#         connection.close()
#         return json.dumps(rowsList)
#     except:
#         return "Could not process"