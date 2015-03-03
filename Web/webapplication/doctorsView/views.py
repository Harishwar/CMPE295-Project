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
from doctorsView.models import SensorUser, Allergies,UserAllergies,UserVaccination
from django.views.decorators.csrf import csrf_exempt

# Create your views here.

logger = logging.getLogger()

#first view to show the index
def index(request):
    return render(request,"registerUser.html");

def search(request):
    return render(request,"list.html");

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
    context={'users':User.objects.filter()}
    return render(request,'viewUsers.html',context)
    
#Fetches users by lastname
@csrf_exempt
def getUserByLastName(request):
    last_name=request.GET.get('searchTerm')
    user_result=User.objects.filter(last_name=last_name)
    context={'search_term':user_result}
    return render(request,'viewUser.html',context)
    
#returns the list of allergies     
def getAllergiesList(request):
    #print serializers.serialize("json", Allergies.objects.all(),fields=('allergy_name'))
    context={'allergies',Allergies.objects.values('allergy_name')}
    return render(request,'addAllergies.html',context)

def addUserAllergies(request):
    user_object=User.objects.get(email=request.POST.get('email'))
    print request.POST.getlist('allergy')
    for allergy_name in request.POST.getlist('allergy'):
        user_allergies=UserAllergies();
        allergy_object=Allergies.objects.get(allergy_name=allergy_name)
        user_allergies.user_id=user_object
        user_allergies.allergy_id=allergy_object
        user_allergies.date_created=datetime.datetime.now()
        user_allergies.date_modified=datetime.datetime.now()
        user_allergies.save()
    #allergy_object=Allergies.objects.filter(allergy_name=list_allergies)
    #for allergy in list_allergies:
    return JsonResponse({"status":201,"result":"Allergies User Relation Added"})


def addUserVaccination(request):
    user_object=User.objects.get(email=request.POST.get('email'))
    user_vaccination=UserVaccination()
    user_vaccination.vaccination_desc=request.POST.get('vaccination')
    user_vaccination.user_id=user_object
    user_vaccination.date_visited=request.POST.get('date_visited')
    user_vaccination.date_created=datetime.datetime.now()
    user_vaccination.date_modified=datetime.datetime.now()
    user_vaccination.save()
    return JsonResponse({"status":201,"result":"User Vaccination Added"})
#def updateUser(request):
        
def deleteUser(request):
    user_id=request.POST.get('email')
    User.objects.filter(email=user_id).delete();
    print user_id
    return JsonResponse({'status':204,"result":"User Deleted Successfully"})
    
'''def deleteUserAllergy(request):
    user_id=request.POST.get('email')
    allergy_name=request.POST.get('allergy_name')
    usr_id=User.objects.get(email=user_id).id
    allergy_id=Allergies.objects.get(allergy_name=allergy_name).id
    UserAllergies.objects.get(id=usr_id,allergy_id=allergy_id).delete();
    return JsonResponse({'status':204,"result":"User Allergy Deleted Successfully"}) '''      
#edit users
#patient visit history
#login module
#password encryption
#deleteUser

