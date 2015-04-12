from django.shortcuts import render
from django.core.context_processors import request
from urllib2 import HTTPError
from models import Users
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
from django.db import transaction, connection
import collections
import json
from email import email

# Create your views here.

logger = logging.getLogger()

#first view to show the index
def index(request):
    return render(request,"dashboard.html");

def search(request):
    return render(request,"list.html");

def dashboard(request):
    if request.method=="GET" and request.session.get('user_id'):
        return render(request,"dashboard.html")
    else:
        return render(request,'index.html')
# def addPatient(request):
#     return render(request,"registerUser.html");

def addPatient(request):
    try:
        if request.method=="GET" and request.session.get('user_id'):
            return render(request,"registerUser.html");
        elif request.method=="POST" and request.session.get('user_id'):
            logger.debug("Obtaining db fields")
            registration = Users()
            registration.first_name = request.POST.get('FirstName')
            registration.last_name = request.POST.get('LastName')
            registration.email = request.POST.get('email')
            registration.user_id = request.POST.get('email')
            registration.dob = request.POST.get('dob')
            registration.address = request.POST.get('address')
            registration.password = "test"
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

    

#redirect(doctorsView.views.addSensor, saved_details.email)


#Method to handle the user sensor data
#@login_required(login_url='/doctorsView/login/')
def addSensor(request): 
    try:
        if request.method =='GET' and request.session.get('user_id'):
            return render(request,"addSensor.html");
        elif request.method=='POST' and request.session.get('user_id'):
            sensor_details = SensorUser()
            email=request.POST.get('email')
            print email
            print'Entered Here'
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
        if request.method=='GET' and request.session.get('user_id'):
            context={'users':Users.objects.filter()}
            return render(request,'viewUsers.html',context)
        else:
            return render(request,'index.html')
        
    except:
        return HttpResponse("Service Error!!!")
#Fetches users by lastname
@csrf_exempt
def getUserByLastName(request):
    try:
        if request.method=='GET' and request.session.get('user_id'):
            last_name=request.GET.get('searchTerm')
            user_result=Users.objects.filter(last_name=last_name)
            context={'users':user_result}
            return render(request,'viewUsers.html',context)
        else:
            return render(request,'index.html')
    except:
        return HttpResponse("Service Error!!!")
#returns the list of allergies
def getAllergiesList(request):
    try:
            #print serializers.serialize("json", Allergies.objects.all(),fields=('allergy_name'))
        context={'allergies':Allergies.objects.values('allergy_name')}
        print 'allergies'
        return render(request,'addAllergies.html',context)
    except:
        return HttpResponse("Service Error!!!")

@csrf_exempt
def addUserAllergies(request):
    try:
        if request.method=='POST' and request.session.get('user_id'):
            print request.POST.get('email')
            email=request.POST.get('email')
            user_object=Users.objects.get(email=email)
            print "db",user_object.email
            print request.POST.getlist('allergy')
            for allergy_name in request.POST.getlist('allergy'):
                user_allergies=UserAllergies();
                print Allergies.objects.get(allergy_name=allergy_name)
                allergy_object=Allergies.objects.get(allergy_name=allergy_name)
                user_allergies.user_id=user_object
                user_allergies.allergy_id=allergy_object
                user_allergies.date_created=datetime.datetime.now()
                user_allergies.date_modified=datetime.datetime.now()
                user_allergies.save()
                #allergy_object=Allergies.objects.filter(allergy_name=list_allergies)
                #for allergy in list_allergies:
                return JsonResponse({"status":201,"result":"Allergies User Relation Added"})
            else:
                return render(request,'index.html')
    except:
        return HttpResponse("Service Error!!!")

def addUserVaccination(request):
    try:
        if request.method=="GET" and request.session.get('user_id'):
            return render(request,'vaccination.html')
        elif(request.method=='POST' and request.session.get('user_id')):
            user_object=Users.objects.get(email=request.POST.get('email'))
            print 'email from add sensor',request.POST.get('email')
            user_vaccination=UserVaccination()
            user_vaccination.vaccination_desc=request.POST.get('vaccination')
            user_vaccination.user_id=user_object
            user_vaccination.date_visited=request.POST.get('date_visited')
            user_vaccination.date_created=datetime.datetime.now()
            user_vaccination.date_modified=datetime.datetime.now()
            user_vaccination.save()
            return JsonResponse({"status":201,"result":"User Vaccination Added"})
        else:
            return render(request,'index.html')
    except:
        return HttpResponse("Service Error!!!")
#def updateUser(request):

def deleteUser(request):
    try:
        user_id=request.POST.get('email')
        Users.objects.filter(email=user_id).delete();
        print user_id
        return JsonResponse({'status':204,"result":"User Deleted Successfully"})
    except:
        return HttpResponse("Service Error!!!")
        

def deleteUserAllergy(request):
    user_id=request.POST.get('email')
    allergy_name=request.POST.get('allergy_name')
    usr_id=Users.objects.get(email=user_id).id
    allergy_id=Allergies.objects.get(allergy_name=allergy_name).id
    print usr_id
    print allergy_id
    UserAllergies.objects.get(user_id=usr_id,allergy_id=allergy_id).delete();
    return JsonResponse({'status':204,"result":"User Allergy Deleted Successfully"})


def login_user(request):
    if(request.session.get('user_id')):
        print "session"+request.session.get('user_id')
        context={'users':Users.objects.filter()}
        return render(request,'viewUsers.html',context)
    else:
        if request.method=='GET':
            return render(request,"list.html");

        else:
            username=request.POST['email']
            print username
            password=request.POST['password']
            print password
            user=authenticate(username=username,password=password)
            print user
            if user is not None:
                request.session['user_id']=user.email;
                print "session"+request.session.get('user_id')
                context={'users':Users.objects.filter()}
                return render(request,'dashboard.html',context)
            else:
                return HttpResponse("Invalid user");


def authenticate(username,password):
    try:
        return Users.objects.get(email=username,password=password)
    except Users.DoesNotExist:
        return None
    #print Users.objects.get(email=username,password=password)


def logout_user(request):
    try:
        del request.session['user_id']
    except KeyError:
        pass
    return HttpResponse("You're logged out.")

def dashboard_req(request):
    if request.method=='GET' and request.session.get('user_id'):
        email=request.GET.get('email')
        print '-------->'
        print load_user_data(email)
        return JsonResponse(load_user_data(email),safe=False)


def dashboard_doc_req(request):
    if request.method=='GET' and request.session.get('user_id'):
        print '-------->'
        print"Hiiii"
        print load_all_users_data()
        return JsonResponse(load_all_users_data(),safe=False)
    
def load_user_data(email):
    try:
        cursor=connection.cursor()
        cursor.execute("select * from SensorResults.crunched_results where user_id=%s",[email])
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
            print row
        connection.close()    
        return json.dumps(rowsList)
    except:
        return "Could not process"
    
def load_all_users_data():
    try:
        print "entered load users"
        cursor=connection.cursor()
        cursor.execute("select * from SensorResults.crunched_results")
        rows=cursor.fetchall()
        print rows
        rowsList=[]
        for row in rows:
            graph_obj=collections.OrderedDict()
            if isinstance(row[1], datetime.datetime):
                dateVal= row[1].isoformat()
            graph_obj['x']=dateVal
            graph_obj['y']=row[2]
            graph_obj['user_id']=row[0]
            rowsList.append(graph_obj)
            print row
        return json.dumps(rowsList)
    except:
        return "Could not process"


#def updateUserProfile(request):

#edit users
#patient visit history
#login module
#password encryption


