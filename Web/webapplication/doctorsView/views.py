from django.shortcuts import render
from django.core.context_processors import request
from urllib2 import HTTPError
from models import Users
from django.shortcuts import redirect
import logging
import datetime
from django.http.response import JsonResponse, HttpResponseRedirect,HttpResponse
from django.core import serializers
from django.core.mail import send_mail
from django.core.urlresolvers import reverse
import doctorsView
from doctorsView.models import SensorUser, Allergies,UserAllergies,UserVaccination
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import authenticate, login
from django.contrib.auth.decorators import login_required
from django.db import transaction, connection,connections
import collections
import json
from email import email
from httplib import HTTPResponse
import time
from django.core import serializers

logger = logging.getLogger()

# First View
def index(request):
    return render(request,"doctorsView/dashboard.html");

def search(request):
    return render(request,"list.html");

def viewUserChart(request):
    context={'user':request.GET.get('email')}
    return render(request,"doctorsView/viewUserChart.html",context)

def dashboard(request):
    if request.method=="GET" and request.session.get('user_id') and request.session.get('role_id')==1:
        return render(request,"doctorsView/dashboard.html")
    elif request.method=="GET" and request.session.get('user_id') and request.session.get('role_id')==2:
        return render(request,'usersView/dashboard.html')
    else:
        return render(request,'index.html')

def addPatient(request):
    try:
        if request.method=="GET" and request.session.get('user_id') and request.session.get('role_id')==1:
            return render(request,"doctorsView/registerUser.html");
        elif request.method=="POST" and request.session.get('user_id') and request.session.get('role_id')==1:
            logger.debug("Obtaining db fields")
            registration = Users()
            registration.first_name = request.POST.get('FirstName')
            registration.last_name = request.POST.get('LastName')
            registration.email = request.POST.get('email')
            registration.user_id = request.POST.get('email')
            registration.dob = request.POST.get('dob')
            registration.address = request.POST.get('address')
            registration.password = "test"
            registration.gender=request.POST.get('gender')
            registration.marital_status=request.POST.get('inputMaritalStatus')
            registration.height= request.POST.get('inputHeight')
            registration.weight=request.POST.get('inputWeight')
            registration.blood_type=request.POST.get('inputBloodType')
            registration.phone_number=request.POST.get('inputPhone')
            # need to convert to a timezone as it throws an exception
            registration.date_created = datetime.datetime.now()
            registration.date_modified = datetime.datetime.now()
            registration.save()
            saved_details = Users.objects.get(id=registration.id)
            send_mail('HealthCareWeb registration', 'Hi,\n You have successfully registered for Enhanced Health Care Web.Please find below the username:' + saved_details.email + ' \n password:', 'saninnovator@gmail.com', [saved_details.email], fail_silently=True)
                #saved_details=User.objects.filter(id=created_id).values()
                #serialized_obj = serializers.serialize('json', [ saved_details, ])
            #return JsonResponse({"email":saved_details.email,"id":saved_details.id})
            return HttpResponseRedirect(reverse('addSensor')+'?email='+saved_details.email)
        else:
            return render(request, 'index.html')
    except HTTPError:
                logger.debug("Error Handling Registration")
                return "Error"

#Method to handle the user sensor data
#@login_required(login_url='/doctorsView/login/')
def addSensor(request):
    try:
        if request.method =='GET' and request.session.get('user_id') and request.session.get('role_id')==1:
            return render(request,"doctorsView/addSensor.html");
        elif request.method=='POST' and request.session.get('user_id') and request.session.get('role_id')==1:
            sensor_details = SensorUser()
            email=request.POST.get('email')
            user_object=Users.objects.get(email=email)
            sensor_details.user_id=user_object
            sensor_details.sensor_id=request.POST.get('SensorID')
            sensor_details.date_created=datetime.datetime.now()
            sensor_details.save()
            #return JsonResponse({"status":201,"result":"Sensor Relation Added"})
            return getAllergiesList(request)
        else:
            return render(request,"index.html");
    except:
            return HttpResponse("Service Error!!!")

#Method for handling the profile view
#https://docs.djangoproject.com/en/1.7/topics/serialization/
#@login_required(login_url='/doctorsView/login/')
def viewUsers(request):
    try:
        if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==1:
            print {'users':Users.objects.filter(role_type='2')}
            context={'users':Users.objects.filter(role_type='2')}
            return render(request,'doctorsView/viewUsers.html',context)
        else:
            return render(request,'index.html')

    except:
        return HttpResponse("Service Error!!!")
    
#Fetches users by lastname
@csrf_exempt
def getUserByLastName(request):
    try:
        if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==1:
            last_name=request.GET.get('searchTerm')
            user_result=Users.objects.filter(last_name=last_name)
            context={'users':user_result}
            return render(request,'doctorsView/viewUsers.html',context)
        else:
            return render(request,'index.html')
    except:
        return HttpResponse("Service Error!!!")
#returns the list of allergies
def getAllergiesList(request):
    try:
        #if request.method=='GET' and request.session.get('user_id') and request.session.get('role_id')==1:
        context={'allergies':Allergies.objects.values('allergy_name')}
        return render(request,'doctorsView/addAllergies.html',context)
    except:
        return HttpResponse("Service ALL Error!!!")

@csrf_exempt
def addUserAllergies(request):
    try:
        if request.method=='POST' and request.session.get('user_id') and request.session.get('role_id')==1:
            email=request.POST.get('email')
            user_object=Users.objects.get(email=email)
            for allergy_name in request.POST.getlist('allergy[]'):
                user_allergies=UserAllergies();
                allergy_object=Allergies.objects.get(allergy_name=allergy_name)
                user_allergies.user_id=user_object
                user_allergies.allergy_id=allergy_object
                user_allergies.date_created=datetime.datetime.now()
                user_allergies.date_modified=datetime.datetime.now()
                user_allergies.save()
                #allergy_object=Allergies.objects.filter(allergy_name=list_allergies)
            return JsonResponse({"status":201,"result":"Allergies User Relation Added"})
        else:
            return render(request,'index.html')
    except:
        return HttpResponse("Service Error!!!")

def addUserVaccination(request):
    try:
        if request.method=="GET" and request.session.get('user_id') and request.session.get('role_id')==1:
            return render(request,'doctorsView/vaccination.html')
        elif(request.method=='POST' and request.session.get('user_id')):
            user_object=Users.objects.get(email=request.POST.get('email'))
            user_vaccination=UserVaccination()
            user_vaccination.vaccination_desc=request.POST.get('vaccination')
            user_vaccination.user_id=user_object
            user_vaccination.date_visited=request.POST.get('date_visited')
            user_vaccination.date_created=datetime.datetime.now()
            user_vaccination.date_modified=datetime.datetime.now()
            user_vaccination.save()
            return redirect('viewUsers')
            #JsonResponse({"status":201,"result":"User Vaccination Added"})
        else:
            return render(request,'index.html')
    except:
        return HttpResponse("Service Error!!!")

@csrf_exempt
def deleteUser(request):
    try:
        user_id=request.GET.get('email')
        cursor=connections['sensors'].cursor()
        cursor.execute("delete from SensorResults.crunched_results where user_id=%s",[user_id])
        cursor.execute("delete from SensorResults.SensorData where user_id=%s",[user_id])
        connections['sensors'].commit()
        connections['sensors'].close()
        Users.objects.filter(email=user_id).delete();
        #return redirect('viewUsers')
        return JsonResponse({'status':204,"result":"User Deleted Successfully"})
    except:
        return HttpResponse("Service Error!!!")


def deleteUserAllergy(request):
    user_id=request.POST.get('email')
    allergy_name=request.POST.get('allergy_name')
    usr_id=Users.objects.get(email=user_id).id
    allergy_id=Allergies.objects.get(allergy_name=allergy_name).id
    UserAllergies.objects.get(user_id=usr_id,allergy_id=allergy_id).delete();
    return JsonResponse({'status':204,"result":"User Allergy Deleted Successfully"})


def login_user(request):
    if request.session.get('user_id') and request.session.get('role_id')==1:
        context={'users':Users.objects.filter()}
        return render(request,'doctorsView/dashboard.html',context)
    elif request.session.get('user_id') and request.session.get('role_id')==2:
        context={'user':Users.objects.get(email=request.session.get('user_id'))}
        return render(request,'usersView/dashboard.html',context)
    else:
        username=request.POST['email']
        password=request.POST['password']
        user=authenticate(username=username,password=password)
        if user is not None and user.role_type==1:
            request.session['user_id']=user.email
            request.session['role_id']=1
            context={'users':Users.objects.filter()}
            return render(request,'doctorsView/dashboard.html',context)
        elif user is not None and user.role_type==2:
            request.session['user_id']=user.email
            request.session['role_id']=2
            context={'user':Users.objects.get(email=user.email)}
            return redirect('/usersView/',context)
        else:
            return HttpResponse("Invalid user");


def authenticate(username,password):
    try:
        return Users.objects.get(email=username,password=password)
    except Users.DoesNotExist:
        return None

def logout_user(request):
    try:
        del request.session['user_id']
        del request.session['role_id']
    except KeyError:
        pass
    return render(request,"logout.html");

def dashboard_req(request):
    if request.method=='GET' and request.session.get('user_id'):
        email=request.GET.get('email')
        return JsonResponse(load_user_data(email),safe=False)

@csrf_exempt
def dashboard_doc_req(request):
    if request.method=='GET' and request.session.get('user_id'):
        return JsonResponse(load_all_users_data(),safe=False)

def load_user_data(email):
    try:
        cursor=connections['sensors'].cursor()
        cursor.execute("select user_id, date_logged,irt_body_avg from SensorResults.crunched_results where user_id=%s",[email])
        rows=cursor.fetchall()
        rowsList=[]
        for row in rows:
            graph_obj=collections.OrderedDict()
            if isinstance(row[1], datetime.datetime):
                dateVal= row[1].isoformat()
            graph_obj['x']= dateVal
            graph_obj['y']=row[2]
            graph_obj['user_id']=row[0]
            rowsList.append(graph_obj)
        connections['sensors'].close()
        return json.dumps(rowsList)
    except:
        return "Could not process"

def load_user_temp(request):
    if request.method=='GET' and request.session.get('user_id'):
        email=request.GET.get('email')
        cursor=connections['sensors'].cursor()
        cursor.execute("select date_logged,irt_body_avg from SensorResults.crunched_results where user_id=%s order by date_logged asc limit 1000",[email])
        rows=cursor.fetchall()
        rowsList=[]
        for row in rows:
            graph_obj=collections.OrderedDict()
            if isinstance(row[1], datetime.datetime):
                dateVal= row[1].isoformat()
            graph_obj['x']=int(time.mktime(row[0].timetuple()) * 1000)
            graph_obj['y']=round(float(row[1]),2)
            #graph_obj['user_id']=row[0]
            rowsList.append(graph_obj)
        connections['sensors'].close()
    return JsonResponse(json.dumps(rowsList),safe=False)

def load_all_users_data():
    try:
        cursor=connections['sensors'].cursor()
        cursor.execute("select * from SensorResults.crunched_results")
        rows=cursor.fetchall()
        rowsList=[]
        for row in rows:
            graph_obj=collections.OrderedDict()
            if isinstance(row[1], datetime.datetime):
                dateVal= row[1].isoformat()
            graph_obj['x']=dateVal
            graph_obj['y']=row[2]
            graph_obj['user_id']=row[0]
            rowsList.append(graph_obj)
        connections['sensors'].close()
        return json.dumps(rowsList)
    except:
        return "Could not process"

@csrf_exempt
def sendAlert(request):
    try:
        email=request.POST.get('email')
        message=request.POST.get('message')
        #send_mail('HealthCareWeb Alert',message,+"'"+request.session.get('user_id')+"'",[email],fail_silently=True)
        send_mail('HealthCareWeb Alert',message,'sanatom.sjsu@gmail.com',[email],fail_silently=False)
        user = Users.objects.get(email=email).id
        #usr_id=Users.objects.get(email=user_id).id
        sensor_user_id= SensorUser.objects.get(user_id=user).sensor_id
        cursor = connection.cursor()
        cursor.execute("insert into SensorResults.Alerts values(default,'"+sensor_user_id+"','"+message+"','"+str(datetime.datetime.now())+"')")
        #transaction.set_dirty()
        #last_id = connection.insert_id()
        #return HttpResponse(str(last_id))
        return JsonResponse({"status":200,"result":"Alert sent to Patient"})
    except:
        return JsonResponse({"status":400,"result":"Failure"})

def settings(request):
    return render(request,'doctorsView/settings.html')

def loadAllergies(request):
    email= request.GET.get('email')
    user_allergy_id= Users.objects.get(email=email).id
    user_allergies = UserAllergies.objects.filter(user_id=user_allergy_id).values()
    all_list=[];
    for i in user_allergies:
        all_list.append(Allergies.objects.get(id=i['allergy_id_id']).allergy_name)
    #serialized_obj = serializers.serialize('json', user_allergies)
    #jsonObj=json.loads(serialized_obj)
    #data = json.dumps(jsonObj[0])
    return JsonResponse(json.dumps(all_list), safe=False)

def loadVaccinations(request):
    email= request.GET.get('email')
    user_vaccination_id= Users.objects.get(email=email).id
    user_vaccinations = UserVaccination.objects.filter(user_id=user_vaccination_id).values()
    all_list=[];
    for i in user_vaccinations:
        vacc_obj=collections.OrderedDict()
        if isinstance(i['date_visited'], datetime.datetime):
            date_visited= i['date_visited'].isoformat()
        vacc_obj['desc'] = i['vaccination_desc']
        vacc_obj['date_visited'] = date_visited
        all_list.append(vacc_obj)
    return JsonResponse(json.dumps(all_list), safe=False)

def load_user_humidity(request):
    if request.method=='GET' and request.session.get('user_id'):
        email=request.GET.get('email')
        cursor=connections['sensors'].cursor()
        cursor.execute("select date_logged,humidity from SensorResults.SensorData where user_id=%s order by date_logged desc limit 100",[email])
        rows=cursor.fetchall()
        rowsList=[]
        for row in reversed(rows):
            graph_obj_humid=collections.OrderedDict()
            if isinstance(row[1], datetime.datetime):
                dateVal= row[1].isoformat()
            graph_obj_humid['x']=int(time.mktime(row[0].timetuple()) * 1000)
            graph_obj_humid['y']=round(float(row[1]),2)
            #graph_obj['user_id']=row[0]
            rowsList.append(graph_obj_humid)
            
        connections['sensors'].close()
    return JsonResponse(json.dumps(rowsList),safe=False)



