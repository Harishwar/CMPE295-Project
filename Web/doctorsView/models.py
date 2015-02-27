from django.db import models
from django_enumfield import enum
from django.template.defaultfilters import default
# Create your models here.

#https://github.com/5monkeys/django-enumfield
class UserType(enum.Enum):
    DOCTOR=1
    PATIENT=2
    NURSE=3
    
class User(models.Model):
    #user_id=models.CharField(max_length=200)
    first_name=models.CharField(max_length=100)
    last_name=models.CharField(max_length=100)
    email=models.EmailField(max_length=100)
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
        db_table="user" 
        


class SensorUser(models.Model):
    user_id=models.ForeignKey(User,db_column="user_id")
    sensor_id=models.CharField(max_length=200)
    class Meta:
        db_table="sensor_user"
        

        