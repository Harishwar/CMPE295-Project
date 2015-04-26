from django.shortcuts import render
from doctorsView.models import Users
from django.db import transaction, connection,connections
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
    if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==2:
        user_email =request.GET.get('email')
        user_obj=Users.objects.get(email=request.GET.get('email'))
        context={'user':user_obj}
        print user_obj.dob
        
        return render(request,'usersView/editUser.html',context)
    elif request.method =='POST':
        email=request.POST.get('email')
        editProfile =Users.objects.get(email=request.GET.get('email'))
        editProfile.first_name = request.POST.get('FirstName')
        editProfile.last_name = request.POST.get('LastName')
        editProfile.email = request.POST.get('email')
        editProfile.user_id = request.POST.get('email')
        editProfile.dob = request.POST.get('dob')
        editProfile.address = request.POST.get('address')
        editProfile.gender=request.POST.get('gender')
        editProfile.marital_status=request.POST.get('inputMaritalStatus')
        editProfile.height= request.POST.get('inputHeight')
        editProfile.weight=request.POST.get('inputWeight')
        editProfile.blood_type=request.POST.get('inputBloodType')
        editProfile.phone_number=request.POST.get('inputPhone')
        editProfile.save()
        
        print email, editProfile.dob
        
        return JsonResponse({"status":200})     
    else:
        return render(request,'index.html')

def sensorHistory(request):
    print request.GET.get('email')
    if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.GET.get('email'))}
        return render(request,'usersView/sensorHistory.html', context)
    else:
        return render(request,'index.html')
    
def loadSensorHistory(request):
    #try:
        #print "entered load users"
        email = request.GET.get('email')
        date_logged = request.GET.get('date')+'%'
        print email
        print date_logged
        print "select date_logged, irt_body, humidity from SensorResults.SensorData where user_id=%s and date_logged like %s",[email,date_logged]
        cursor=connection.cursor()
        cursor.execute("select date_logged, humidity,irt_body from SensorResults.SensorData where user_id=%s and date_logged like %s",[email,date_logged])
        rows=cursor.fetchall()
        ##print rows
        rowsList=[]
        for row in rows:
            #print row
            sensor_obj=collections.OrderedDict()
            if isinstance(row[0], datetime.datetime):
                dateVal= str(row[0].time())       
            sensor_obj['time']=dateVal
            sensor_obj['humidity']=row[1]
            sensor_obj['irt_body']=row[2]
#             print graph_obj
            rowsList.append(sensor_obj)
            ##print row
        return JsonResponse(rowsList,safe=False)
        
    #except:
     #   return HttpResponse("test1")
def calcBmi(request):
    email= request.GET.get('email')
    user_obj=Users.objects.get(email=email)
    print user_obj.height
    ht = float(user_obj.height)
    wt = float(user_obj.weight)
    if ht!=0 and wt!=0:
        bmi =float( (wt*703)/(ht*ht))
    if bmi < 18.5:
        message="Under Weight"
    elif bmi>=18.5 and bmi <=24.9:
        message ="Normal"
    elif bmi>=24.9 and bmi <=29.9:
        message="Over Weight"
    else:
        message ="Obese"        
    return JsonResponse({"message":message},safe=False)
        
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