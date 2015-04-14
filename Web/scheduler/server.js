var express = require('express');
var schedule = require('./scheduler');

var app = express();
//var scheduleObj=schedule();
//schedule.scheduleForTimePeriod(1, function(err, data) {
//    if (err) {
//        console.log(err);
//    } else {
//        console.log("Job is processed " + data);
//    }
//
//});
schedule.scheduleRuleEngine();
app.get('/', function(req, res) {
    res.send('Hello World!');
});

var server = app.listen(3000, function() {

    var host = server.address().address;
    var port = server.address().port;

    console.log('Example app listening at http://%s:%s', host, port);

});