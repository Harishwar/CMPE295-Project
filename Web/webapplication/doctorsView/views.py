from django.shortcuts import render
from django.core.context_processors import request
from urllib2 import HTTPError
from models import User
import logging
import datetime
from django.http.response import JsonResponse, HttpResponseRedirect,HttpResponse
from django.core import serializers
from django.core.mail import send_mail
from django.core.urlresolvers import reverse
import doctorsView
from doctorsView.models import SensorUser, Allergies

# Create your views here.

logger = logging.getLogger()

#first view to show the index
def index(request):
    return render(request,"registerUser.html");

def search(request):
    return render(request,"search.html");

def registerUser(request):
    try:
        logger.debug("Obtaining db fields")
        registration=User()
        registration.first_name=request.POST.get('FirstName')
        registration.last_name=request.POST.get('LastName')
        registration.email=request.POST.get('email')
        registration.user_id=request.POST.get('email')
        registration.dob=request.POST.get('dob')
        registration.address=request.POST.get('address')
        #need to convert to a timezone as it throws an exception
        registration.date_created=datetime.datetime.now()
        registration.date_modified=datetime.datetime.now()
        registration.save()
        saved_details=User.objects.get(id=registration.id)
        send_mail('HealthCareWeb registration', 'Hi,\n You have successfully registered for Enhanced Health Care Web.Please find below the username:'+saved_details.email+' \n password:', 'saninnovator@gmail.com',[saved_details.email], fail_silently=True)
        #saved_details=User.objects.filter(id=created_id).values()
        #serialized_obj = serializers.serialize('json', [ saved_details, ])
    except HTTPError:
        logger.debug("Error Handling Registration")
        return "Error"


    #return JsonResponse({"email":saved_details.email,"id":saved_details.id})
    return HttpResponseRedirect(reverse('addSensor')+'?email='+saved_details.email)


#redirect(doctorsView.views.addSensor, saved_details.email)


#Method to handle the user sensor data
def addSensor(request):
    if request.method=='GET':
        return render(request,"addSensor.html");
    else:
        sensor_details=SensorUser()
        email=request.POST.get('email')
        print email
        user_object=User.objects.get(email=email)
        sensor_details.user_id=user_object
        sensor_details.sensor_id=request.POST.get('SensorID')
        sensor_details.date_created=datetime.datetime.now()
        sensor_details.save()
        return JsonResponse({"status":201,"result":"Sensor Relation Added"})

#Method for handling the profile view
#https://docs.djangoproject.com/en/1.7/topics/serialization/
def viewUsers(request):
    #print User.objects.all().values()
    print serializers.serialize("json", User.objects.all(),fields=('first_name','last_name'))
    return HttpResponse(serializers.serialize("json", User.objects.all(),fields=('first_name','last_name')));

#Fetches users by lastname
def getUserByLastName(request):
        last_name=request.GET.get('LastName')
        user_result=User.objects.filter(last_name=last_name)
        print user_result
        return HttpResponse(user_result.values())
    
#returns the list of allergies     
def getAllergiesList(request):
    print serializers.serialize("json", Allergies.objects.all(),fields=('allergy_name'))
    return HttpResponse(serializers.serialize("json", Allergies.objects.all(),fields=('allergy_name')));

#def insertAllergyUser(request):
    
#edit users
#pushAllergyPatient
#Add vaccinations-Patient
#patient visit history
#login module
#password encryption
#deleteUser
#deleteVaccination
