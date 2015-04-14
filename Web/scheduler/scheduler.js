var fs = require('fs');
var mysql = require('mysql');
var linerReader = require('line-reader');
var i = 0;
var arrayData = [];


function scheduleForTimePeriod(minuteTime, callback) {
    var d = new Date();
    //    if (error) {
    //        callback(err, null);
    //    } else {
    console.log("Job Triggered!!!", d.getHours() + ":" + (parseInt(d.getMinutes()) + parseInt(minuteTime)) + ":" + d.getSeconds());
    setInterval(function() {
        var connection = mysql.createConnection({
            host: 'cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com',
            port: 3306,
            database: 'SensorResults',
            user: 'root',
            password: '!passw0rd'
        });
        //fs.readFile('./public/abc.txt', 'utf-8', handleFile);
        connection.connect();
        var dest = fs.createWriteStream('./public/archive/' + 'abc' + (new Date()).getTime() + ".txt");
		linerReader.eachLine('./public/abc.txt', function(line, last) {
            console.log(line);
			
            arrayData.push(line);
			
            var sensor_data = {
                temperature: line,
                humidity: "2"
            };
			line.pipe(dest);
            var query = connection.query('INSERT INTO Rule_Engine_Results SET ?', sensor_data, function(err, result) {
                if (err) {
                    console.log("refused", err);
                }
                console.log("row inserted");
            });
            //console.log(query.sql);
            if (last) {
                console.log("file read");
                connection.end();
                return false;
            }
        });
        //var src = fs.createReadStream('./public/abc.txt');

        
        //src.pipe(dest);

//        src.on('end', function() {
//            console.log("copied");
//        });
//        src.on('error', function() {
//            console.log("cannot copy");
//        });
        //        fs.unlink('./public/abc.txt', function(err) {
        //            if (err) throw err;
        //            console.log("File deleted");
        //        });

        //console.log("I can read the file");
        callback(null, "processed successfully");
    }, parseInt(minuteTime) * 60 * 1000);

    // }
}

function handleFile(err, data) {
    if (err) throw err;
    console.log(data);
}



function scheduleRuleEngine() {
    var today = new Date();
	    console.log("Job Started and will run at ", today.getHours() + ":" + (parseInt(today.getMinutes()) + parseInt(1)) + ":" + today.getSeconds());

    setInterval(function() {
        var connection = mysql.createConnection({
            host: 'cmpe295b.cynriidclhwl.us-west-1.rds.amazonaws.com',
            port: 3306,
            database: 'SensorResults',
            user: 'root',
            password: '!passw0rd'
        });
        connection.connect();
        //SELECT * FROM SensorData where date_logged >= ? and date_logged< ?
        //console.log('select user_id, date_logged, (avg(temperature)),avg(humidity) from SensorData where date_logged like ' //+today.getFullYear()+"-"+04+"-"+03+'%'+' group by user_id, date_logged');
        //var dd = "\'"+getFormatDate(new Date())+'%'+"\'";
        var dd = getFormatDate(new Date()) + '%';
        //var dd='2015-04-03%';
//        console.log('select user_id, date_logged, (avg(temperature)),avg(humidity) from SensorData where date_logged like ' + '?' + ' group by user_id, date_logged');
		console.log('select user_id, date_logged,sensor_id, sum(step_count) as step_count_tot, avg(mag_x)  as avg_mag_x,avg(mag_y) as avg_mag_y,avg(mag_z) as avg_mag_z, avg(irt_body) as avg_body_temp, avg(irt_ambient) as avg_ambient, avg(humidity) as humid_avg, avg(gyro_x) as avg_gyro_x,avg(gyro_y) as avg_gyro_y,avg(gyro_z) as avg_gyro_z,avg(acc_x) as avg_acc_x,avg(acc_y) as avg_acc_y,avg(acc_z) as avg_acc_z, avg(pressure) as pressure_avg from SensorData where date_logged like ' + '?' + ' group by user_id, sensor_id, date_logged');
		
        var query = connection.query('select user_id, date_logged,sensor_id, sum(step_count) as step_count_tot, avg(mag_x)  as avg_mag_x,avg(mag_y) as avg_mag_y,avg(mag_z) as avg_mag_z, avg(irt_body) as avg_body_temp, avg(irt_ambient) as avg_ambient, avg(humidity) as humid_avg, avg(gyro_x) as avg_gyro_x,avg(gyro_y) as avg_gyro_y,avg(gyro_z) as avg_gyro_z,avg(acc_x) as avg_acc_x,avg(acc_y) as avg_acc_y,avg(acc_z) as avg_acc_z, avg(pressure) as pressure_avg from SensorData where date_logged like ' + '?' + ' group by user_id, sensor_id, date_logged', dd, function(err, rows, fields) {
            if (err) {
                console.log("refused", err);
            }
            for (row in rows) {
				console.log("processing row");
                var crunched_data = {
                    user_id: rows[row].user_id,
                    date_logged: rows[row].date_logged ,
					pressure_avg:rows[row].pressure_avg,
					mag_z_avg:rows[row].avg_mag_z,
					mag_y_avg:rows[row].avg_mag_y,
					mag_x_avg:rows[row].avg_mag_x,	
					irt_body_avg:rows[row].avg_body_temp,
					irt_ambient_avg:rows[row].avg_ambient,
					humidity_avg:rows[row].humid_avg,
					gyro_z_avg:rows[row].avg_gyro_z,
					gyro_y_avg:rows[row].avg_gyro_y,
					gyro_x_avg:rows[row].avg_gyro_x,
					acc_z_avg:rows[row].avg_acc_x,
					acc_y_avg:rows[row].avg_acc_y,
					acc_x_avg:rows[row].avg_acc_z,
					sensor_id:rows[row].sensor_id
					
					
                }
                connection.query('insert into crunched_results set ?',crunched_data, function(err, result){
					if(err){
					console.log("refused", err);
					}
					else{
					console.log("data retrived and inserted");
					}
				})

                console.log("reading rows", JSON.stringify(rows));
            }
			connection.end();
		});
        
//        console.log('select user_id, date_logged, (avg(temperature)),avg(humidity) from SensorData where date_logged like ' + dd + ' group by user_id, date_logged');

    }, 2000);

}

function getFormatDate(d) {
    var yr = d.getFullYear();
    var mm = d.getMonth() + 1;
    var dd = d.getDate() - 1;
    mm = mm >= 10 ? mm : '0' + mm;
    dd = dd >= 10 ? dd : '0' + dd;
    console.log(yr + "-" + mm + "-" + dd);
    return yr + "-" + mm + "-" + dd;
}
exports.scheduleForTimePeriod = scheduleForTimePeriod;
//exports.scheduleForTimePeriod = scheduleForTimePeriod;
exports.scheduleRuleEngine = scheduleRuleEngine;

