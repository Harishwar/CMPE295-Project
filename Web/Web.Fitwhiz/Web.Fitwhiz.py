import os
import MySQLdb
from flask import Flask, request, url_for
import json
import datetime
from werkzeug.utils import secure_filename, redirect
from pprint import pprint

#C:\Users\RKGampa\Documents\GitHub\CMPE295-Project\Web\uploads

UPLOAD_FOLDER = '\uploads'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif','zip','tar','rar'])

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

#db = MySQLdb.connect(host="cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com", user="root", passwd="*******", db="CMPE295B")
db = MySQLdb.connect(host="localhost", user="root", passwd="root", db="CMPE295B")
results_db = MySQLdb.connect(host="localhost", user="root", passwd="root", db="SensorResults")


def allowed_file(filename):
    result = '.' in filename and \
           filename.rsplit('.',1)[1] in ALLOWED_EXTENSIONS
    return result

@app.route('/')
def hello_world():
    return 'Hello from Team Fitwhiz'

@app.route("/v1.0/user/login/",methods=['GET'])
def check_user():
    """
    For SensorId given in the query parameter,
    :return json result:
    """
    if request.method == 'GET':
        sensor_id = request.args.get('SensorId',0)
        cursor = db.cursor()
        if sensor_id != 0:
            try:
                cursor.execute("""select user_id from sensor_user where sensor_id='"""+str(sensor_id)+"""'""")
                db.commit()
                count = cursor.rowcount
                if(count!=0):
                    return "Success"
                else:
                    return "Error"
            except Exception,e:
                print e.message
                print "exception"


@app.route("/v1.0/user/profile/",methods=['GET'])
def get_user():
    """
    For SensorId given in the query parameter,
    :return json result:
    """
    if request.method == 'GET':
        sensor_id = request.args.get('SensorId',0)
        cursor = db.cursor()
        if sensor_id != 0:
            try:
                cursor.execute("""SELECT first_name, last_name, height, weight, blood_type, gender, address, phone_number from users where id = (select user_id from sensor_user where sensor_id='"""+str(sensor_id)+"""')""")
                db.commit()
                for (first_name, last_name, height, weight, blood_type, gender, address, phone_number) in cursor:
                    response ={'FirstName':first_name,'LastName':last_name,'Height':height,'Weight':weight,'BloodType':blood_type, 'Gender':gender, 'Address':address,'PhoneNumber':phone_number}
                    print response
                    print json.dumps(response)
                    return json.dumps(response)
            except Exception, e:
                print e.message
                print "exception"
    return True

@app.route("/v1.0/user/results/",methods=['GET'])
def get_user_results():
    """
    For SensorId given in the query parameter,
    :return json result:
    """
    if request.method == 'GET':
        sensor_id = request.args.get('SensorId',0)
        cursor = db.cursor()
        if sensor_id != 0:
            try:
                cursor.execute("""SELECT xValue, yValue, zValue, hValue, tValue from SensorResults where SensorId='"""+str(sensor_id)+"""'""")
                db.commit()
                for (xValue, yValue, zValue, hValue, tValue) in cursor:
                    print xValue, yValue, zValue, hValue, tValue
                    response ={'xValue':xValue,'yValue':yValue,'zValue':zValue,'hValue':hValue,'tValue':tValue}
                    return json.dumps(response)
            except Exception,e:
                print e.message
                print "exception"

# TODO: Test whether the file is extracted and saved.
@app.route("/v1.0/user/sensor_details",methods=['POST'])
def analyze_sensor_data():
    """
    For a given sensor ID and file with sensor details extract the details
    :return: json result
    """
    if request.method == 'POST':
        json=request.json
        sensorId=json.get('SensorId')
        xVal=json.get('x_val')
        yVal=json.get('y_val')
        zVal=json.get('z_val')
        hVal=json.get('h_val')
        tVal=json.get('t_val')
        StepCount=json.get('StepCount')
        cursor = db.cursor()
        if sensorId != 0:
            try:
                cursor.execute("""select user_id from sensor_user where sensor_id='"""+str(sensorId)+"""'""")
                db.commit()
                for user_id in cursor:
                    csr = results_db.cursor()
                    csr.execute("""INSERT INTO SensorData (user_id,temperature,humidity,acc_x,acc_y,acc_z,date_logged,step_count) VALUES ("""+str(user_id[0])+""","""+str(tVal)+""","""+str(hVal)+""","""+str(xVal)+""","""+str(yVal)+""","""+str(zVal)+""",'"""+str(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))+"""',"""+str(StepCount)+""")""")
                    results_db.commit()
            except Exception,e:
                print e.message
                print "exception"

# GET all the allergies of a particular user
@app.route("/v1.0/user/allergies",methods= ['GET'])
def get_allergies():
    """
    For a given sensor ID
    :return: json result (the list of allergies suggested by the doctor)
    """
    if request.method == 'GET':
        sensor_id = request.args.get('sensor_id',0)
        cursor = db.cursor()
        if sensor_id != 0:
            try:
                query = "SELECT allergy_name from allergies where id IN (SELECT allergy_id from user_allergies where"+\
                        " user_id IN (SELECT user_id from sensor_user where sensor_id ='"+sensor_id+"'))"
                print query
                cursor.execute(query)
                print cursor.rowcount
                result = []
                db.commit()
                for allergy_name in cursor:
                    print allergy_name,len(allergy_name),type(allergy_name)
                    result.append(allergy_name[0])
                response ={'allergies':result}
                return json.dumps(response)
            except Exception:
                print "exception "+Exception

# GET all the vaccinations of a particular user
@app.route("/v1.0/user/vaccinations",methods= ['GET'])
def get_vaccines():
    """
    For a given sensor ID
    :return: json result (the list of vaccines suggested by the doctor)
    """
    if request.method == 'GET':
        sensor_id = request.args.get('sensor_id',0)
        cursor = db.cursor()
        if sensor_id != 0:
            try:
                cursor.execute("SELECT vaccination_desc from user_vaccination where user_id = "+
                               "(select user_id from sensor_user where sensor_id='"+sensor_id+"')")
                db.commit()
                for (vaccination_desc) in cursor:
                    print vaccination_desc
                    response ={'vaccination_desc':vaccination_desc}
                    return json.dumps(response)
            except Exception:
                print "exception "+Exception

if __name__ == '__main__':
    app.run()
