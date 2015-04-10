from django.db import models
from django_enumfield import enum
from django.template.defaultfilters import default
from django.contrib.auth.models import AbstractBaseUser
# Create your models here.

#https://github.com/5monkeys/django-enumfield
class UserType(enum.Enum):
    DOCTOR=1
    PATIENT=2
    NURSE=3
    
class Users(models.Model):
    username=models.TextField()
    password=models.TextField()
    user_id=models.CharField(max_length=200,default=None)
    first_name=models.CharField(max_length=100)
    last_name=models.CharField(max_length=100)
    email=models.EmailField(max_length=100)
    user_password=models.CharField(max_length=100)
    dob=models.DateField()
    address=models.CharField(max_length=200)
    gender=models.CharField(max_length=10)
    marital_status=models.CharField(max_length=20)
    height=models.CharField(max_length=20)
    weight=models.CharField(max_length=20)
    blood_type=models.CharField(max_length=20)
    role_type=enum.EnumField(UserType,default=UserType.DOCTOR)
    date_created=models.DateTimeField()
    date_modified=models.DateTimeField()
    ##http://stackoverflow.com/questions/19130942/whats-the-best-way-to-store-phone-number-in-django-model
    phone_number=models.CharField(max_length=20)
    class Meta:
        db_table="users" 
        


class SensorUser(models.Model):
    user_id=models.ForeignKey(Users,db_column="user_id")
    sensor_id=models.CharField(max_length=200)
    date_created=models.DateTimeField()
    class Meta:
        db_table="sensor_user"
        

class Allergies(models.Model):
    allergy_name=models.CharField(max_length=100)
    allergy_desc=models.CharField(max_length=300)
    date_created=models.DateTimeField()
    class Meta:
        db_table="allergies"
    
class UserAllergies(models.Model):
    user_id=models.ForeignKey(Users,db_column="user_id")
    allergy_id=models.ForeignKey(Allergies,db_column="allergy_id")
    date_created=models.DateTimeField()
    date_modified=models.DateTimeField()
    class Meta:
        db_table="user_allergies"
        
class UserVaccination(models.Model):
    user_id=models.ForeignKey(Users,db_column="user_id")
    vaccination_desc=models.CharField(max_length=1000)
    date_visited=models.DateTimeField()
    date_created=models.DateTimeField()
    date_modified=models.DateTimeField()
    class Meta:
        db_table="user_vaccination"
    
            