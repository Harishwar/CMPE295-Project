import MySQLdb
from flask import Flask, request, make_response, jsonify
import datetime

UPLOAD_FOLDER = '\uploads'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif','zip','tar','rar'])

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

db = MySQLdb.connect(host="cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com", user="root", passwd="!passw0rd", db="CMPE295B")
#db = MySQLdb.connect(host="localhost", user="root", passwd="root", db="CMPE295B")
results_db = MySQLdb.connect(host="cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com", user="root", passwd="!passw0rd", db="SensorResults")

#results_db = MySQLdb.connect(host="localhost", user="root", passwd="root", db="SensorResults")


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
                    for user_id in cursor:
                        return make_response(jsonify(result="success",user_id=user_id),200)
                else:
                    return user_not_found_response()
            except Exception,e:
                return error_response(e.message)
        make_response(200)


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
                if(cursor.rowcount!=0):
                    for (first_name, last_name, height, weight, blood_type, gender, address, phone_number) in cursor:
                        response ={'FirstName':first_name,'LastName':last_name,'Height':height,'Weight':weight,'BloodType':blood_type, 'Gender':gender, 'Address':address,'PhoneNumber':phone_number}
                        return make_response(jsonify(response),200)
                else:
                    return user_not_found_response()
            except Exception, e:
                return error_response(e.message)
        else:
            return error_response("")

@app.route("/v1.0/user/results/",methods=['GET'])
def get_user_results():
    """
    For SensorId given in the query parameter,
    :return json result:
    """
    if request.method == 'GET':
        sensor_id = request.args.get('SensorId',0)
        cursor = results_db.cursor()
        if sensor_id != 0:
            try:
                sql = """SELECT user_id, date_logged,irt_ambient_avg,humidity_avg, irt_body_avg,acc_x_avg,acc_y_avg,acc_z_avg,mag_x_avg,mag_y_avg,mag_z_avg,gyro_x_avg,gyro_y_avg,gyro_z_avg,pressure_avg,sensor_id  from crunched_results where sensor_id='"""+str(sensor_id)+"""' order by date_logged desc"""
                print sql
                cursor.execute(sql)
                results_db.commit()
                for (user_id, date_logged,irt_ambient_avg,humidity_avg, irt_body_avg,acc_x_avg,acc_y_avg,acc_z_avg,mag_x_avg,mag_y_avg,mag_z_avg,gyro_x_avg,gyro_y_avg,gyro_z_avg,pressure_avg,sensor_id) in cursor:
                    response ={'user_id':user_id, 'date_logged':date_logged,'irt_ambient_avg':irt_ambient_avg,'humidity_avg':humidity_avg, 'irt_body_avg':irt_body_avg,'acc_x_avg':acc_x_avg,'acc_y_avg':acc_y_avg,'acc_z_avg':acc_z_avg,'mag_x_avg':mag_x_avg,'mag_y_avg':mag_y_avg,'mag_z_avg':mag_z_avg,'gyro_x_avg':gyro_x_avg,'gyro_y_avg':gyro_y_avg,'gyro_z_avg':gyro_z_avg,'pressure_avg':pressure_avg,'sensor_id':sensor_id}
                    return make_response(jsonify(response),200)
            except Exception,e:
                return error_response(e.message)

# TODO: Test whether the file is extracted and saved.
@app.route("/v1.0/user/sensor_details",methods=['POST'])
def analyze_sensor_data():
    """
    For a given sensor ID and json with sensor details extract the details
    :return: json result
    """
    if request.method == 'POST':
        json=request.json
        sensorId=str(json.get('SensorId'))
        acc_xVal=str(json.get('acc_x'))
        acc_yVal=str(json.get('acc_y'))
        acc_zVal=str(json.get('acc_z'))
        hVal=str(json.get('h_val'))
        body_tVal=str(json.get('irt_body_val'))
        amb_tVal=str(json.get('irt_ambient_val'))
        mag_xVal=str(json.get('mag_x'))
        mag_yVal=str(json.get('mag_x'))
        mag_zVal=str(json.get('mag_x'))
        gyro_xVal=str(json.get('gyro_x'))
        gyro_yVal=str(json.get('gyro_x'))
        gyro_zVal=str(json.get('gyro_x'))
        pressure_val=str(json.get('pressure'))

        StepCount=str(json.get('StepCount'))
        cursor = db.cursor()
        if sensorId != 0:
            try:
                cursor.execute("""select email from users where id=(select user_id from sensor_user where sensor_id='"""+str(sensorId)+"""')""")
                db.commit()
                for email in cursor:
                    csr = results_db.cursor()
                    query = str.format("""INSERT INTO SensorData (sensor_id, user_id, acc_x, acc_y,acc_z,step_count,irt_body, irt_ambient,gyro_x,gyro_y,gyro_z ,mag_x,mag_y,mag_z, pressure, date_logged, humidity) VALUES (\"{0}\",\"{1}\",\"{2}\",\"{3}\",\"{4}\",\"{5}\",\"{6}\",\"{7}\",\"{8}\",\"{9}\",\"{10}\",\"{11}\",\"{12}\",\"{13}\",\"{14}\",\"{15}\",\"{16}\")""",
                                sensorId,email[0],acc_xVal,acc_yVal,acc_zVal,StepCount,body_tVal,amb_tVal,gyro_xVal,gyro_yVal,gyro_zVal,mag_xVal,mag_yVal,mag_zVal,pressure_val,str(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')),hVal)
                    csr.execute(query)
                    results_db.commit()
                    resp = make_response()
                    resp.status_code = 201
                    return resp
            except Exception,e:
                return error_response(e.message)

# GET all the allergies of a particular user
@app.route("/v1.0/user/allergies",methods= ['GET'])
def get_allergies():
    """
    For a given sensor ID
    :return: json result (the list of allergies suggested by the doctor)
    """
    if request.method == 'GET':
        sensor_id = request.args.get('SensorId',0)
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
                return make_response(jsonify(response),200)
            except Exception:
                return Exception.message

# GET all the vaccinations of a particular user
@app.route("/v1.0/user/vaccinations",methods= ['GET'])
def get_vaccines():
    """
    For a given sensor ID
    :return: json result (the list of vaccines suggested by the doctor)
    """
    if request.method == 'GET':
        sensor_id = request.args.get('SensorId',0)
        cursor = db.cursor()
        if sensor_id != 0:
            try:
                cursor.execute("SELECT vaccination_desc from user_vaccination where user_id = "+
                               "(select user_id from sensor_user where sensor_id='"+sensor_id+"')")
                db.commit()
                for (vaccination_desc) in cursor:
                    print vaccination_desc
                    response ={'vaccination_desc':vaccination_desc}
                    return make_response(jsonify(response),200)
            except Exception:
                return Exception.message

@app.route("/v1.0/user/alerts",methods=['POST'])
def insert_alerts():
    """
    For a given sensor ID and json with sensor details extract the details
    :return: json result
    """
    if request.method == 'POST':
        sensorId = request.args.get("SensorId",0)
        json=request.json
        msg=str(json.get('message'))
        cursor = results_db.cursor()
        if sensorId != 0:
            try:
                query = str.format("""INSERT into Alerts (SensorId,message,datetime_logged) VALUES (\"{0}\",\"{1}\",\"{2}\")""",sensorId,msg,str(datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')))
                print query
                cursor.execute(query)
                results_db.commit()
                resp = make_response()
                resp.status_code = 201
                return resp

            except Exception,e:
                return make_response(error_response(e.message),500)


def error_response(msg):
    if msg is not None and len(msg) > 0:
        return make_response(jsonify(result="error",error=msg),400)
    else:
        return make_response(jsonify(result="error",error="Invalid request"),400)


def user_not_found_response():
    return make_response(jsonify(result="error",error="User not found"),401)

if __name__ == '__main__':
    app.run(host="0.0.0.0",port=80)
