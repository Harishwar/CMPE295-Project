import MySQLdb
from flask import Flask, request
import json

app = Flask(__name__)
#db = MySQLdb.connect(host="cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com", user="root", passwd="*******", db="CMPE295B")
db = MySQLdb.connect(host="localhost", user="root", passwd="root", db="CMPE295B")

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
                cursor.execute("""select user_id from sensor_user where sensor_id='"""+sensor_id+"""'""")
                db.commit()
                count = cursor.rowcount
                if(count!=0):
                    return "Success"
                else:
                    return "Error"
            except Exception:
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
                cursor.execute("""SELECT first_name, last_name, height, weight, blood_type, gender, address, phone_number from user where id = (select user_id from sensor_user where sensor_id='"""+sensor_id+"""')""")
                db.commit()
                for (first_name, last_name, height, weight, blood_type, gender, address, phone_number) in cursor:
                    response ={'FirstName':first_name,'LastName':last_name,'Height':height,'Weight':weight,'BloodType':blood_type, 'Gender':gender, 'Address':address,'PhoneNumber':phone_number}
                    print response
                    print json.dumps(response)
                    return json.dumps(response)
            except Exception:
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
                cursor.execute("""SELECT xValue, yValue, zValue, hValue, tValue from SensorResults where SensorId='"""+sensor_id+"""'""")
                db.commit()
                for (xValue, yValue, zValue, hValue, tValue) in cursor:
                    print xValue, yValue, zValue, hValue, tValue
                    response ={'xValue':xValue,'yValue':yValue,'zValue':zValue,'hValue':hValue,'tValue':tValue}
                    return json.dumps(response)
            except Exception:
                print "exception"


if __name__ == '__main__':
    app.run()
