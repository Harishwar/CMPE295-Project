from django.shortcuts import render
from doctorsView.models import Users
from django.db import transaction, connection
import collections
import datetime
import json
from django.http.response import JsonResponse,HttpResponse


# Create your views here.

def viewDashBoard(request):
    print request.session.get('user_id')
    context={'user':Users.objects.get(email=request.session.get('user_id'))}
    return render(request,'dashboard_user.html',context)


def viewUserProfile(request):
    user_email =request.GET.get('email')
    #try:
    if request.method=='GET' and request.session.get('user_id'):
        context={'users':Users.objects.get(email=request.GET.get('email'))}
        print context
        print "context obj"
        #return JsonResponse(context);
        
        return render(request,'viewUser.html',context)
    else:
        return render(request,'index.html')
        
#     except:
#         return HttpResponse("Service Error!!!")

# def dashboard_req(request):
#         print request.method
#         print request.session.get('user_id')
#         email=request.GET.get('email')
#         print '-------->'
#         print load_user_data(email)
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
#             print row
#         connection.close()    
#         return json.dumps(rowsList)
#     except:
#         return "Could not process"