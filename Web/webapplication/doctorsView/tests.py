#from django.test import TestCase
from doctorsView.models import Users
from django.test import Client
from django.test import TestCase
import doctorsView
import unittest
class UserTestCase(unittest.TestCase):
    def setUp(self):
        #Users.objects.create(username='testuser',password='test',user_id='test@test.com',first_name='test',last_name='test',email='test@test.com')
        self.client = Client()

    def test_reg_user(self):
        "unit test register user"
        response =self.client.post('/doctorsView/addPatient', {'username':'testuser','password':'test','user_id':'test@test.com','first_name':'test','last_name':'test','email':'test@test.com'})
        self.assertEqual(response.status_code, 200)

    def test_add_sensor(self):
        "unit test add sensor"
        response = self.client.post('/doctorsView/addSensor',{'email':'test@test.com','sensor_id':'1245677'})
        self.assertEqual(response.status_code, 200)

#     def test_search_user(self):
#         "unit test search user"
#         response = self.client.get('/doctorsView/search',{'searchTerm':'murali'})
#         print response
#         self.assertEqual(response,'Service Error!!!')
#
#     def test_login(self):
#         "unit test login "
#         response= self.client.post('/doctorsView/login',{'testuser','test'})
#         self.assertEqual(response, 200)

# Create your tests here.

# class SiteTests(TestCase):
#     #This is the fixture:
#     #-   fields: {content: lots of stuff, query: test, title: test, url: 'http://google.com'}
#     #model: doctorsView.Users
#     #id: 1
#     fixtures = ['doctorsView.json']
#
#     def testAddUser(self):
#         s = Users.objects.get(id=1)
#         self.assertEquals(s.query, 'test')
#         s.query = 'who cares'
#         s.save()
