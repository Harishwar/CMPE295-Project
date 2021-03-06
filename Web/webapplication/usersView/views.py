from django.shortcuts import render
from doctorsView.models import Users, SensorUser
from django.db import transaction, connection,connections
import collections
import datetime
import json
from django.http.response import JsonResponse,HttpResponse

def index(request):
    if request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.session.get('user_id'))}
        return render(request,'usersView/home.html',context)
    else:
        return render(request,'index.html')

def viewCharts(request):
    if request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.session.get('user_id'))}
        return render(request,'usersView/charts.html',context)
    else:
        return render(request,'index.html')

def viewUserProfile(request):
    user_email =request.GET.get('email')
    if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.GET.get('email'))}
        return render(request,'usersView/viewUser.html',context)
    else:
        return render(request,'index.html')

def editUserProfile(request):
    if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==2:
        user_email =request.GET.get('email')
        user_obj=Users.objects.get(email=request.GET.get('email'))
        context={'user':user_obj}
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
        context={'user':Users.objects.get(email=email)}
        return render(request,'usersView/viewUser.html',context)
    else:
        return render(request,'index.html')

def sensorHistory(request):
    if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.GET.get('email'))}
        return render(request,'usersView/sensorHistory.html', context)
    else:
        return render(request,'index.html')

def loadSensorHistory(request):
        email = request.GET.get('email')
        date_logged = request.GET.get('date')+'%'
        cursor=connection.cursor()
        cursor.execute("select date_logged, humidity,irt_body from SensorResults.SensorData where user_id=%s and date_logged like %s",[email,date_logged])
        rows=cursor.fetchall()
        rowsList=[]
        for row in rows:
            sensor_obj=collections.OrderedDict()
            if isinstance(row[0], datetime.datetime):
                dateVal= str(row[0].time())
            sensor_obj['time']=dateVal
            sensor_obj['humidity']=row[1]
            sensor_obj['irt_body']=row[2]
            rowsList.append(sensor_obj)
        return JsonResponse(rowsList,safe=False)

def loadBMI(request):
    email= request.GET.get('email')
    user_obj=Users.objects.get(email=email)
    ht = float(user_obj.height)
    wt = float(user_obj.weight)
    if ht!=0 and wt!=0:
        bmi =float( (wt*703)/(ht*ht))
    else:
        bmi = 0
    return JsonResponse(bmi,safe=False)

def loadTemperature(request):
    email = request.GET.get('email')
    cursor=connection.cursor()
    cursor.execute("select irt_body_avg from SensorResults.crunched_results where user_id=%s order by date_logged desc",[email])
    rows=cursor.fetchall()
    return JsonResponse(rows,safe=False)

def loadAlerts(request):
    email= request.GET.get('email')
    user_obj = Users.objects.get(email=email)
    sensor_obj =SensorUser.objects.get(user_id=user_obj.id)
    cursor=connections['sensors'].cursor()
    cursor.execute("select message from SensorResults.Alerts where SensorId=%s order by datetime_logged desc limit 10",[sensor_obj.sensor_id])
    rows=cursor.fetchall()
    return JsonResponse(json.dumps(rows),safe=False)
