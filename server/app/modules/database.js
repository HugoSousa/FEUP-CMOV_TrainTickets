/** database.js **/

var mysql = require('mysql');
var async = require('async');
var moment = require('moment');
var uuid = require('node-uuid');


var NodeRSA = require('node-rsa');
var fs = require('fs');

var key = new NodeRSA(fs.readFileSync('./data/private.pem'));
key.setOptions({signingScheme:'sha1'});

var connection = mysql.createConnection({
  host     : 'localhost',
  port     : 3306,
  user     : 'root',
  password : 'admin',
  database : 'trainsystem'
});


//data for checking capacity exceeding
var stations = [];
stations[1] = "A";
stations[2] = "A/CENTRAL";
stations[3] = "CENTRAL";
stations[4] = "B";
stations[5] = "B/CENTRAL";
stations[6] = "C";
stations[7] = "C/CENTRAL";


var route_affectances = [
{
  route:"A-A/CENTRAL",
  affects: ["A-A/CENTRAL"]
},
{
  route: "A-B",
  affects: ["A-A/CENTRAL", "A/CENTRAL-CENTRAL", "CENTRAL-B/CENTRAL", "B/CENTRAL-B"]
},
{
  route: "A-B/CENTRAL",
  affects: ["A-A/CENTRAL", "A/CENTRAL-CENTRAL", "CENTRAL-B/CENTRAL"]
},
{
  route: "A-CENTRAL",
  affects: ["A-A/CENTRAL", "A/CENTRAL-CENTRAL"]
},
{
  route: "A/CENTRAL-A",
  affects: ["A/CENTRAL-A"]
},
{
  route: "A/CENTRAL-B",
  affects: ["A/CENTRAL-CENTRAL", "CENTRAL-B/CENTRAL", "B/CENTRAL-B"]
},
{
  route: "A/CENTRAL-B/CENTRAL",
  affects: ["A/CENTRAL-CENTRAL", "CENTRAL-B/CENTRAL"]
},
{
  route: "A/CENTRAL-CENTRAL",
  affects: ["A/CENTRAL-CENTRAL"]
},
{
  route: "B-A",
  affects: ["B-B/CENTRAL", "B/CENTRAL-CENTRAL", "CENTRAL-A/CENTRAL", "A/CENTRAL-A"]
},
{
  route: "B-A/CENTRAL",
  affects: ["B-B/CENTRAL", "B/CENTRAL-CENTRAL", "CENTRAL-A/CENTRAL"]
},
{
  route: "B-B/CENTRAL",
  affects: ["B-B/CENTRAL"]
},
{
  route: "B-CENTRAL",
  affects: ["B-B/CENTRAL", "B/CENTRAL-CENTRAL"]
},
{
  route: "B/CENTRAL-A",
  affects: ["B/CENTRAL-CENTRAL", "CENTRAL-A/CENTRAL", "A/CENTRAL-A"]
},
{
  route: "B/CENTRAL-A/CENTRAL",
  affects: ["B/CENTRAL-CENTRAL", "CENTRAL-A/CENTRAL"]
},
{
  route: "B/CENTRAL-B",
  affects: ["B/CENTRAL-B"]
},
{
  route: "B/CENTRAL-CENTRAL",
  affects: ["B/CENTRAL-CENTRAL"]
},
{
  route: "C-C/CENTRAL",
  affects: ["C-C/CENTRAL"]
},
{
  route: "C-CENTRAL",
  affects: ["C-C/CENTRAL", "C/CENTRAL-CENTRAL"]
},
{
  route: "C/CENTRAL-C",
  affects: ["C/CENTRAL-C"]
},
{
  route: "C/CENTRAL-CENTRAL",
  affects: ["C/CENTRAL-CENTRAL"]
},
{
  route: "CENTRAL-A",
  affects: ["CENTRAL-A/CENTRAL", "A/CENTRAL-A"]
},
{
  route: "CENTRAL-A/CENTRAL",
  affects: ["CENTRAL-A/CENTRAL"]
},
{
  route: "CENTRAL-B",
  affects: ["CENTRAL-B/CENTRAL", "B/CENTRAL-B"]
},
{
  route: "CENTRAL-B/CENTRAL",
  affects: ["CENTRAL-B/CENTRAL"]
},
{
  route: "CENTRAL-C",
  affects: ["CENTRAL-C/CENTRAL", "C/CENTRAL-C"]
},
{
  route: "CENTRAL-C/CENTRAL",
  affects: ["CENTRAL-C/CENTRAL"]
}];


var simple_routes = [
{
  start:"A",
  end:"B",
  points: ["A","A/CENTRAL","CENTRAL","B/CENTRAL","B"]
},
{
  start:"B",
  end:"A",
  points: ["B","B/CENTRAL","CENTRAL","A/CENTRAL","A"]
},
{
  start:"C",
  end:"CENTRAL",
  points: ["C","C/CENTRAL","CENTRAL"]
},
{
  start:"CENTRAL",
  end:"C",
  points: ["CENTRAL","C/CENTRAL","C"]
}
];
/*-------------------------------------------------------------------------------------*/
sumSoldTickets = function(userId, datetime, affect, callback){
  connection.query('select * from ticket t join route r on t.route_id = r.id where user_id = ? and is_validated = (0);', [userId], function (err, rows, fields) {
    if (!err){
        cb(null, rows);
      }
      else{
        console.log('Error while performing Query.');
        cb(err,null);
      }
  });
};


exports.teste = function () {
  //checkTrainCapacity(1,4,null,2);
  /*
  getRoutePossibilites(1,6,'14:00:00', function(err, data){
    async.each(data, function(ticket, cb){
      console.log("TICKET: " + JSON.stringify(ticket));
    });
    //console.log(data);
  });
  */

  //get small chunks of route for that big route and create array with those
  //for each small chunk, get tickets of affected routes and sum on the array
  //if some of the sum on the array exceeds the capacity train, ticket is sold_out!

  //from, to, datetime initial, train
  exports.checkTrainCapacity(1, 4, 1, function(err, data){
    console.log(JSON.stringify(data));
    console.log(data[0].route);
    //create array with small chunks
    var date = '2015-11-02';
    var times = ['09:00:00', '10:00:00', '10:45:00', '11:45:00', '12:30:00'];
    
    route_chunks = [];
    //console.log("DATA: " + data);
    async.forEachOf(data, function(small_route, index1, callback1){
      var capacity = 1;
      var tickets_sum = 0;

      var user_id = 1;
      //datetime
      var datetime = date + " " + times[index1];

      //console.log("DATETIME: " + datetime);

      //get the time it passes on each chunk of the routes
      var tickets_sold = 0;
      async.forEachOf(small_route.affected, function(affect, index2, callback2){

        var start_station = affect.from;
        var end_station = affect.to;

        var datetime = date + " " + times[start_station - 1];
        console.log("DATETIME: " + datetime + " / FROM: " + start_station + " / TO: " + end_station);

        connection.query('select count(*) as tickets_sold from ticket t join route r on t.route_id = r.id where user_id = ? and is_validated = (0) and t.route_date = ? and r.start_station = ? and r.end_station = ?', [user_id, datetime, start_station, end_station], function (err2, rows2, fields2) {
          if (!err2){
            console.log("TICKETS SOLD:" + rows2[0]['tickets_sold']);
            tickets_sold += rows2[0]['tickets_sold'];
          }
          else{
            console.log('Error while performing Query.');
          }
          callback2();
        });
      }
      , function (err) {
        if(! err){
          console.log("SMALL ROUTE: " + small_route);
          if(tickets_sold < capacity){
            route_chunks.push({route: small_route.route, sold_out: false});
          }else{
            route_chunks.push({route: small_route.route, sold_out: true});
          }
        }

        callback1();
      });
    },
    function (err) {
      /*
      if(! err){
        if("SOLD_OUT: " + route_chunks[index1].sold_out);
      }else{

      }
      */
      var sold_out = false;
      for(var i = 0; i < route_chunks.length; i++){
        if(route_chunks[i].sold_out){
          sold_out = true;
          break;
        }
      }

      console.log(sold_out);
    });

      //get the sum of the tickets of the affected_routes
      


    

    
  })
}

exports.getTrainTimesTest = function (from, to, cb){

  var result = { trips: [], distance: null, price: null, switch_central: null};

  async.series([
    function(callback) {
      console.log("ONE");
      connection.query('select * from route r where r.start_station = ? and r.end_station = ?;', [from, to], function (err, rows, fields) {
        if (!err) {

          var switch_central = rows[0]['switch_central'][0];
          if(switch_central == 0){

            //var result = { trips: [], distance: rows[0]['distance'], price: rows[0]['price'], switch_central: false };
            result.distance = rows[0]['distance'];
            result.price = rows[0]['price'];
            result.switch_central = false;


            connection.query('select * from route r join station_stop ss on ss.route_id = r.id where r.start_station = ? and r.end_station = ? order by ss.time;', [from, to], function (err1, rows1, fields1) {
              
              if(!err1){

                for(var i = 0; i < rows1.length; i++) {

                  var row = rows1[i];

                  if(row['order'] == 1){
                    result.trips.push( { stations: [], times: [], train: row['train_id'] } );
                  }
                  
                  result.trips[result.trips.length - 1].stations.push(row['station_id']);
                  result.trips[result.trips.length - 1].times.push(row['time']);
                }

                console.log("OI");
                callback(null, result);
              }

              else{
                callback(err1, null);
              }
              
              console.log(JSON.stringify(result));
            });       
          }
        }
        else{
          callback(err, null);
        }
      });
    },
    function(callback) {
      //check sold_out for each trip
      console.log("TWO");
      var callFunctions = [];
      for(var i = 0; i < result.trips.length; i++){
        /*
        checkTrainCapacity(from, to, result.trips[i].times[0], result.trips[i].train, i, function(data){
          console.log("CHANGE SOLD_OUT TRIP FOR INDEX " + data['index'] + " WITH " + data['result']);
          result.trips[data['index']]['sold_out'] = data['result'];

        });   
        */
        
      }
      callback(null, callFunctions);
    }],
    function(err, results){

      console.log("THREE")
      cb(null, result);
    }
  );
}


exports.getTrainTimes = function (from, to, date, cb) {

  //TODO: check if the capacity is exceeded for each trip (return 'sold_out' boolean field)

  connection.query('select * from route r where r.start_station = ? and r.end_station = ?;', [from, to], function (err, rows, fields) {
    if (!err) {

      var switch_central = rows[0]['switch_central'][0];
      if(switch_central == 0){

        var result = { trips: [], distance: rows[0]['distance'], price: rows[0]['price'], switch_central: false };

        connection.query('select * from route r join station_stop ss on ss.route_id = r.id where r.start_station = ? and r.end_station = ? order by ss.time;', [from, to], function (err1, rows1, fields1) {
          
          if(!err1){

            for(var i = 0; i < rows1.length; i++) {

              var row = rows1[i];

              if(row['order'] == 1){
                result.trips.push( { stations: [], times: [], train: row['train_id'] } );
              }
              
              result.trips[result.trips.length - 1].stations.push(row['station_id']);
              result.trips[result.trips.length - 1].times.push(row['time']);
            }

            //check sold_out for each trip
            async.forEachOf(result.trips, function(trip, index, callback){
              var datetime = date + " " + trip.times[0];

              checkRouteSoldOut(from, to, datetime, trip.stations, trip.times, trip.train, function(err, data){
                if(!err){
                  console.log("UPDATE SOLD OUT TO " + data);
                  result.trips[index]['sold_out'] = data;
                }
                else{
                  //console.log("DONT UPDATE SOLD OUT");
                  //console.log(data);
                  //result.trips['sold_out'] = data;
                }
                callback();
              });
            },
            function(err){
              if(!err){
                console.log("returning here");
                //result.trips[0]['sold_out'] = false;
                cb(null, result);
              }else{
                cb(err,null);
              }
            });
            //cb(null, result);
          }

          else{
            cb(err1, null);
          }
        });       
      }
      else{
        
        central_station_times = [];
        var route_1 = rows[0]['route_1'];
        var route_2 = rows[0]['route_2'];

        var result = { trips: [], distance: rows[0]['distance'], price: rows[0]['price'], switch_central: true };

        connection.query('select * from route r join station_stop ss on ss.route_id = r.id where r.id = ? union select * from route r join station_stop ss on ss.route_id = r.id where r.id = ?;', [route_1, route_2], function (err1, rows1, fields1) {    

          if(!err1){

            for(var i = 0; i < rows1.length; i++) {

              var row = rows1[i];

              if(row['order'] == 1 && row['route_id'] == route_1){
                result.trips.push( { stations: [], times: [] } );
              }
              else if(row['order'] == 1 && row['route_id'] == route_2){
                //guardar os tempos da central station num array
                central_station_times.push( { first_station_time: row['time'], next_stations: [], next_times: [] });
              }
              else if(row['order'] > 1 && row['route_id'] == route_2){
                //assumir que vem na ordem correta - adicionar ao ultimo array
                central_station_times[central_station_times.length - 1].next_stations.push(row['station_id']);
                central_station_times[central_station_times.length - 1].next_times.push(row['time']);
              }
              
              result.trips[result.trips.length - 1].stations.push(row['station_id']);
              result.trips[result.trips.length - 1].times.push(row['time']);
            }

            //for each trip in results (route_1), append the stations of route_2 with less waiting time. If not possible (to catch the central station in the same day), that trip is removed.
            for(var i = 0; i < result.trips.length; i++) {

              var last_time = result.trips[i].times[result.trips[i].times.length - 1];
              var append_index = getLessWaitingTime(central_station_times, last_time);

              //se retornar -1, apagar essa trip (não existe horário compatível de espera na central_station no mesmo dia)
              if(append_index == -1)
                result.trips.splice(i,1);
              else{
                //console.log(central_station_times[append_index].first_station_time);
                //console.log(result.trips[i].times[result.trips[i].times.length - 1]);              

                var cs_start_time = central_station_times[append_index].first_station_time;
                var cs_end_time = result.trips[i].times[result.trips[i].times.length - 1];

                var cs_start_split = cs_start_time.split(":");
                var cs_end_split = cs_end_time.split(":");

                //use a fixed random date - just the time matters
                var cs_start_time_date = new Date(2000, 1, 1, cs_start_split[0], cs_start_split[1], cs_start_split[2]);
                var cs_end_time_date = new Date(2000, 1, 1, cs_end_split[0], cs_end_split[1], cs_end_split[2]);

                var diff = cs_start_time_date - cs_end_time_date; //difference in miliseconds

                var hh = Math.floor(diff / 1000.0 / 60.0 / 60.0);
                //diff -= hh * 1000.0 * 60.0 * 60.0;

                //difference in minutes
                var mm = Math.floor(diff / 1000.0 / 60.0);


                result.trips[i].waiting_time = mm;
                result.trips[i].stations = result.trips[i].stations.concat(central_station_times[append_index].next_stations);
                result.trips[i].times = result.trips[i].times.concat(central_station_times[append_index].next_times);


              }
            }
            
            async.forEachOf(result.trips, function(trip, index, callback){
              connection.query('select train_id from route r join station_stop ss on ss.route_id = r.id where r.id = ? and ss.time = ?', [route_1, trip.times[0]], function (err1, rows1, fields1) {
                if (!err1){
                    console.log("INDEX: " + index);
                    result.trips[index]['train_1'] = rows1[0]['train_id'];

                    var central_index = -1;
                    //get the index of the central station
                    for(var i = 0; i < trip.stations.length; i++){
                      if(trip.stations[i] == 3)
                        central_index = i;
                    }
                    console.log("CENTRAL INDEX: " + central_index);
                    //time when route leaves the central station
                    var arrival_central = trip.times[central_index];
                    var waiting_time = trip.waiting_time;
                    var arrival_central_date = new Date(date + " " + arrival_central);
                    var departure_central_date = new Date(arrival_central_date.getTime() + waiting_time*60000);
                    var departure_central = departure_central_date.getHours() + ":" + departure_central_date.getMinutes() + ":" + departure_central_date.getSeconds();
                    
                    connection.query('select train_id from route r join station_stop ss on ss.route_id = r.id where r.id = ? and ss.time = ?', [route_2, departure_central], function (err2, rows2, fields2) {
                      if (!err2){
                        console.log("ROWS2 " + rows2[0]);
                        result.trips[index]['train_2'] = rows2[0]['train_id'];
                        callback();
                      }
                      else{
                        console.log('Error while performing Query.');
                      }
                    });
                    
                  }
                  else{
                    console.log('Error while performing Query.');
                  }
              });
            },
            function(err, data){

              //TODO NAO ESTA A ESCREVER SOLD_OUT NAS TRIPS TODAS....CALLBACK SHIT..?

              async.forEachOf(result.trips, function(trip1, index1, callback1){
              var datetime = date + " " + trip1.times[0];

              checkRouteSoldOut(from, 3, datetime, trip1.stations, trip1.times, trip1.train_1, function(err, data){
                if(!err){
                  if(data == false){
                    checkRouteSoldOut(3, to, datetime, trip1.stations, trip1.times, trip1.train_2, function(err1, data1){
                      if(!err1){
                        console.log("UPDATE SOLD OUT TO " + data1);
                        result.trips[index1]['sold_out'] = data1;
                        callback1();
                      }
                      else{         }
                        //callback();
                    });
                  }else{
                    result.trips[index1]['sold_out'] = true;
                    callback1();
                  }
                  //console.log("UPDATE SOLD OUT TO " + data);
                  //result.trips[index]['sold_out'] = data;
                }
                else{
                }
                });
              },
              function(err){
                if(!err){
                  console.log("returning here");
                  //result.trips[0]['sold_out'] = false;
                  cb(null, result);
                }else{
                  cb(err,null);
                }
              });
            });

            //console.log(JSON.stringify(result));
            //for each trip, check if sold_out
            //check from "from" to "central" and from "central" to "to"
            
            /*
            async.forEachOf(result.trips, function(trip, index, callback){
              var datetime = date + " " + trip.times[0];

              checkRouteSoldOut(from, 3, datetime, trip.stations, trip.times, trip.train_1, function(err, data){
                if(!err){
                  if(data == false){
                    checkRouteSoldOut(3, to, datetime, trip.stations, trip.times, trip.train_2, function(err1, data1){
                      if(!err1){
                        console.log("UPDATE SOLD OUT TO " + data1);
                        result.trips[index]['sold_out'] = data1;
                      }
                      else{
                      }
                    });
                  }else{
                    result.trips[index]['sold_out'] = true;
                  }
                  //console.log("UPDATE SOLD OUT TO " + data);
                  //result.trips[index]['sold_out'] = data;
                }
                else{
                }
                callback();
              });
            },
            function(err){
              if(!err){
                console.log("returning here");
                //result.trips[0]['sold_out'] = false;
                cb(null, result);
              }else{
                cb(err,null);
              }
            });
            */

            //check sold_out for each trip
            //cb(null, result);
          }
          else{
            console.log('Error while performing Query.');
            cb(err,null);
          }
        });  
      }
    }
    
    
    else{
      console.log('Error while performing Query.');
      cb(err,null);
    }
  });

}

checkRouteSoldOut = function(from, to, datetime, stations, times, train, callback){

  console.log(stations);
  console.log(times);

  exports.checkTrainCapacity(from, to, train, function(err, data){
    console.log(JSON.stringify(data));
    console.log(data[0].route);
    //create array with small chunks
    var date = datetime.split(" ")[0];
    //var times = ['09:00:00', '10:00:00', '10:45:00', '11:45:00', '12:30:00'];
    var capacity = data.capacity;
    var route_chunks = [];
    //console.log("DATA: " + data);
    async.forEachOf(data, function(small_route, index1, callback1){
      //var capacity = 1;
      var tickets_sum = 0;

      //datetime
      //var datetime = date + " " + times[index1];

      //console.log("DATETIME: " + datetime);

      //get the time it passes on each chunk of the routes
      var tickets_sold = 0;
      async.forEachOf(small_route.affected, function(affect, index2, callback2){

        var start_station = affect.from;
        var end_station = affect.to;

        var time;
        for(var i = 0; i < stations.length; i++){
          //console.log("stations[i] " + stations[i]);
          //console.log("start_station " + start_station);

          if(stations[i] == start_station){
            //console.log("YES");
            time = times[i];
            break;
          }
        }

        var datetime = date + " " + time;
        console.log("DATETIME: " + datetime + " / FROM: " + start_station + " / TO: " + end_station);

        connection.query('select count(*) as tickets_sold from ticket t join route r on t.route_id = r.id where is_validated = (0) and t.route_date = ? and r.start_station = ? and r.end_station = ?', [datetime, start_station, end_station], function (err2, rows2, fields2) {
          if (!err2){
            console.log("TICKETS SOLD:" + rows2[0]['tickets_sold']);
            tickets_sold += rows2[0]['tickets_sold'];
          }
          else{
            console.log('Error while performing Query.');
          }
          callback2();
        });
      }
      , function (err) {
        if(! err){
          console.log("SMALL ROUTE: " + small_route.route + " / " + tickets_sold);
          console.log("TRAIN " + train + " / CAPACITY " + capacity);
          if(tickets_sold < capacity){
            route_chunks.push({route: small_route.route, sold_out: false});
          }else{
            route_chunks.push({route: small_route.route, sold_out: true});
          }
        }

        callback1();
      });
    },
    function (err) {
      if(!err){
        var sold_out = false;
        for(var i = 0; i < route_chunks.length; i++){
          if(route_chunks[i].sold_out){
            sold_out = true;
            break;
          }
        }
        console.log("ROUTE_CHUNKS " + JSON.stringify(route_chunks));
        callback(null, sold_out);

      }else{
        callback(err, null);
      }      
    });
  })
}


exports.getUnusedTickets = function (user, cb) {

  //TODO: instead of user parameter, get user from the authentication

  connection.query('select * from ticket t join route r on t.route_id = r.id where user_id = ? and is_validated = (0);', [user], function (err, rows, fields) {
    if (!err){
        cb(null, rows);
      }
      else{
        console.log('Error while performing Query.');
        cb(err,null);
      }
  });


}



//returns the index of central_station_times where there is the less waiting time
getLessWaitingTime = function (central_station_times, last_time) {
  var less_waiting = '23:59:00';
  var index = -1;

  for(var i = 0; i < central_station_times.length; i++){

    var time = central_station_times[i].first_station_time;

    if(time > last_time && time < less_waiting){
      less_waiting = time;
      index = i;
    }
  }

  return index;
}

exports.checkTrainCapacity = function(from, to, train_id, cb){

  var capacity;

  connection.query('select * from train where id = ?', [train_id], function (err, rows, fields) {
      if(!err){
        capacity = rows[0].capacity;
        console.log(rows[0].capacity);
        console.log("CAPACITY: ", capacity);

        //get the affected routes from routes_affectance. 
        //for each affected route, get the other routes with that affected route
        //get the sold tickets for those routes and check if the sum exceeds the train capacity
        //set the sold_out parameter in result
        var affects = [];

        for(var i = 0; i < route_affectances.length; i++){
          if(route_affectances[i].route == (stations[from] + "-" + stations[to])){
            var affected_routes = route_affectances[i].affects;

            for(var j = 0; j < affected_routes.length; j++){

              var affected_ticket_routes = [];

              for(var k = 0; k < route_affectances.length; k++){

                if(route_affectances[k].affects.indexOf(affected_routes[j]) != -1){
                  affected_ticket_routes.push({route_name: route_affectances[k].route});

                }
              }

              if(affected_ticket_routes.length > 0){

                for(var l = 0; l < affected_ticket_routes.length; l++){

                  var route_split = affected_ticket_routes[l].route_name.split("-");
                  var from_station_name = route_split[0];
                  var to_station_name = route_split[1];

                  affected_ticket_routes[l]["from"] = stations.indexOf(from_station_name);
                  affected_ticket_routes[l]["to"] = stations.indexOf(to_station_name);
                }

                affects.push({route: affected_routes[j], affected: affected_ticket_routes });
              }
            }

            break;
          }
        }
        affects['capacity'] = capacity;
        console.log("AFFECTS: " + JSON.stringify(affects));
        cb(null, affects);
        /*
        for(var i = 0; i < affects.length; i++){

          //TODO: change user
          //TODO change date
          var sql_query = "select count(*) as occupied from ticket t join route r join station_stop ss on t.route_id = r.id and ss.route_id = r.id and ss.time = time(t.route_date) where user_id = 1 and is_validated = (0) and date(t.route_date) = '2015-10-18' and time(t.route_date) = ss.time"; 
          
          for( var j = 0; j < affects[i].affected.length; j++){
            
            if(j != 0)
              sql_query += " or";
            else
              sql_query += " and (";

            sql_query += " (r.start_station = " + affects[i].affected[j].from + " and r.end_station = " + affects[i].affected[j].to + " )";
          }

          sql_query += " )";

          //console.log("QUERY: " + sql_query);
          
          connection.query(sql_query, function (err, rows, fields) {
            if(!err){
              console.log("CAPACITY: " + capacity);
              console.log("OCCUPIED: " + rows[0].occupied);
              if(parseInt(rows[0].occupied) >= parseInt(capacity)){
                console.log("RETURNED TRUE");
                cb({index: index, result: true});
              }
              else{
                cb({index: index, result: false});
              }
            }
            else{
              console.log("Error in query: ", err);
            }
          });

        }
        */
      }
      else{
        console.log("Error in query: ", err);
      }
    });
}


exports.getStations = function (cb) {

  connection.query('select * from station', function (err, rows, fields) {
    if (!err){
        cb(null, rows);
      }
      else{
        console.log('Error while performing Query.');
        cb(err,null);
      }
  });
}

exports.getSimpleTrains = function(cb) {
  var simpleTrains = [];
  simple_routes.forEach(function(value, index) {
    var route = {};
    route.start = value.start;
    route.end = value.end;
    route.start_id = stations.indexOf(route.start);
    route.end_id = stations.indexOf(route.end);
    simpleTrains.push(route);
  });


  async.each(simpleTrains, function(train, callback) {

        exports.getTrainTimes(train.start_id, train.end_id, null, function(err, data){
          if (err) {
              console.log("ERROR : ",err);            
          } else { 
              var trips = [];
              data.trips.forEach(function(trip,ind) {
                trips.push( {
                  start_time: trip.times[0],
                  end_time: trip.times[trip.times.length-1],
                  train: trip.train
                });
              })           
              train.trips = trips;   
          }
          callback();    
    });
    
  },
  function (err) {
    cb(null, simpleTrains);
  }
  );
 
}

function getRoutePossibilites(from, to, time, cb) {
   exports.getTrainTimes(from, to, null, function(err, data){
      if (err) {
              cb(null,err);          
          } else { 
            var found = false;
            data.trips.forEach(function(trip, index) {
              //console.log(trip.times[0]);
              //console.log(time);
              if (trip.times[0].toString() == time.toString()) found = trip;
               
            });
            if (!found) cb('Couldnt find trip',null);
            else  {
              var combo_array = [];
              found.stations.forEach(function(station, index, array) {
                //console.log(station);
                for (var i = index +1; i < array.length; i++) {
                  combo_array.push({
                    start: station,
                    end: array[i],
                    time: found.times[index]
                  })
                }
              });
              cb(null,combo_array);
            }
          }
    });
}

exports.getAllTickets = function(from,to,time,date,cb) {
 exports.getSimpleTrains(function(err, simpleTrains){
    if (err) {
            cb(err,null);          
        } else {       
            var found = false;
            simpleTrains.forEach(function (train, index) {
              if (train.start_id == from && train.end_id == to) {
                train.trips.forEach(function(trip) {
                  if (trip.start_time == time) {
                    found = true;
                  }
                });
              }
            });
            if (!found) cb('Unrecognized trip', null);
            else {
              getRoutePossibilites(from, to, time, function(err, combo_array) {
                  if (err) cb(err, null);
                  else {
                    console.log(combo_array);
                    var download_tickets = [];
                   async.each(combo_array, function(combo, callback) {

                      connection.query(
                        'select * from ticket, route where ticket.route_id = route.id and ticket.route_date = ? and route.start_station = ? and route.end_station = ?',
                        [date + ' ' + combo.time, combo.start, combo.end],
                        function (err, rows, fields) {
                          console.log(date + ' ' + combo.time);
                          console.log(rows);
                          for (var i = 0; i < rows.length; i++) {
                            //rows[i].uuid = Array.prototype.slice.call(rows[i].uuid, 0);
                            rows[i].is_validated = Array.prototype.slice.call(rows[i].is_validated, 0);
                            //rows[i].signature = Array.prototype.slice.call(rows[i].signature, 0);
                            rows[i].switch_central = Array.prototype.slice.call(rows[i].switch_central, 0);


                            download_tickets.push(rows[i]);
                          }
                        callback(); 
                        });
                    },
                    function (err) {
                      cb(null, download_tickets);
                    }
                    );
                   // select * from ticket, route where ticket.route_id = route.id and ticket.route_date = '2015-10-29 14:00:00' and route.start_station = 1 and route.end_station = 3;
                  }
              });
            }
        }  
  });
}
exports.getRoute = function (from, to, time, date, cb) {

  //se switch_central = 1, tem de buscar de from -> 3 + 3 -> to
  connection.query('select * from route r where r.start_station = ? and r.end_station = ?', [from, to], function (err0, rows0, fields0) {
    if(!err0){

      if(rows0[0]['switch_central'][0] == 0)
      {
        connection.query('select ss.id, ss.station_id, ss.time, ss.order, ss.train_id from route r join station_stop ss on ss.route_id = r.id where r.start_station = ? and r.end_station = ? and ss.time >= ?', [from, to, time], function (err, rows, fields) {
      
        if (!err){

          var max_order = 1;
          var start = 0; //for cases when the first returned rows don't start in order 1

          for(var i = 0; i < rows.length; i++){
            if(rows[i].order > 1)
              start = i+1;
            else
              break;
          }

          for(var i = 0; i < rows.length; i++){
            if(rows[i].order > max_order)
              max_order = rows[i].order;
          }

          //limit to the first max_order rows
          var result = [];
          for(var i = start; i-start < max_order; i++){
            result.push(rows[i]);
          }

          var route = rows0[0]['id'];

          cb(null, {ticket_1: result, ticket_2: null, route_1: route, route_2: null, price: rows0[0]['price'], distance: rows0[0]['distance'], from: from, to: to, time: time, date: date, sold_out: false});
        }

        else{
          console.log('Error while performing Query 1.');
          cb(err,null);
        }

        });
      }else{
        //necessario retornar bilhete de from->3 e 3->to
          var route1 = rows0[0]['route_1'];
          var route2 = rows0[0]['route_2'];
          var result = {ticket_1: null, ticket_2: null, route_1: route1, route_2: route2, price: rows0[0]['price'], distance: rows0[0]['distance'], from: from, to: to, time: time, date: date, sold_out: false};

          connection.query('select ss.id, ss.station_id, ss.time, ss.order, ss.train_id from route r join station_stop ss on ss.route_id = r.id where r.start_station = ? and r.end_station = 3 and ss.time >= ? ', [from, time], function (err1, rows1, fields1) {
          
            if (!err1){
              var max_order = 1;
              var start = 0; //for cases when the first returned rows don't start in order 1

              for(var i = 0; i < rows1.length; i++){
                if(rows1[i].order > 1)
                  start = i+1;
                else
                  break;
              }

              for(var i = 0; i < rows1.length; i++){
                if(rows1[i].order > max_order)
                  max_order = rows1[i].order;
              }

              //limit to the first max_order rows
              var ticket_1 = [];
              for(var i = start; i-start < max_order; i++){
                ticket_1.push(rows1[i]);
              }

              result.ticket_1 = ticket_1;

              console.log("AQUI!" + ticket_1[ticket_1.length - 1].time);

              connection.query('select ss.id, ss.station_id, ss.time, ss.order, ss.train_id from route r join station_stop ss on ss.route_id = r.id where r.start_station = 3 and r.end_station = ? and ss.time >= ?', [to, ticket_1[ticket_1.length - 1].time], function (err2, rows2, fields2) {
                if(!err2){
                  var max_order = 1;
                  var start = 0; //for cases when the first returned rows don't start in order 1

                  for(var i = 0; i < rows2.length; i++){
                    if(rows2[i].order > 1)
                      start = i+1;
                    else
                      break;
                  }

                  for(var i = 0; i < rows2.length; i++){
                    if(rows2[i].order > max_order)
                      max_order = rows2[i].order;
                  }

                  //limit to the first max_order rows
                  var ticket_2 = [];

                  for(var i = start; i-start < max_order; i++){
                    ticket_2.push(rows2[i]);
                  }

                  result.ticket_2 = ticket_2;

                  cb(null, result);
                }
                else{
                  console.log('Error while performing Query 2.', err2);
                  cb(err2,null);
                }
              });
            }
            else{
              console.log('Error while performing Query 3.');
              cb(err1,null);
            }
          });
      }
    }
    else{
      console.log('Error while performing Query 4.');
      cb(err0,null);
    }
  });
}


exports.registeruser = function (user, cb) {
//TODO add credit card supp
//TODO remove id number

connection.beginTransaction(function(err) {
  if (err) cb(err,null);
  else {
    connection.query('insert into credit_card(type,number,validity) values(?,?,?)', 
      [user.creditcard_type, user.creditcard_number, moment(new Date(user.creditcard_validity)).format("YYYY-MM-DD HH:mm:ss")], function(err, result) {
        if (err) {
         connection.rollback(function() {
          cb(err,null);
        });
       }
       else {
        var credit_id = result.insertId;
        connection.query('insert into user(name, username, password, cc_id) values (?,?,?,?)',[user.name, user.username, user.password,credit_id], function (err, rows, fields) {
          if (!err){
            connection.commit(function(err) {
              if (err) {
               connection.rollback(function() {
                cb(err,null);
              });
             }
             else
               cb(null, rows);
           });
          }
          else{
           connection.rollback(function() {
            cb(err,null);
          });

         }
       }
       );

      }
    });
  }
});
}

exports.getUserByUsername = function (username, cb) {
  connection.query('select * from user,credit_card where username = ? and credit_card.id = user.cc_id',[username], function (err, rows, fields) {
    if (!err){
        console.log(rows[0]);
        cb(null, rows[0]);
      }
      else{
        console.log('Error while performing Query.', err);
        cb(err,null);
      }
  });
}

exports.getEmployeeByEmail = function (email, cb) {
  connection.query('select * from employee where email = ?',[email], function (err, rows, fields) {
    if (!err){
        //console.log(rows[0]);
        cb(null, rows[0]);
      }
      else{
        console.log('Error while performing Query.', err);
        cb(err,null);
      }
  });
}
exports.buyTickets = function (user, from, to, date, time, cb){

  //TODO validate the credit card of the user, using the fake external service!

  var datetime = date + " " + time;
 
  this.getRoute(from, to, time, date, function(err, data){
    if(!err){
      if(data['sold_out'] == false){
        if(data['ticket_2'] == null){
          
          //buy only ticket_1

          var route_1 = data['route_1'];
          var code = uuid.v4();
          var obj = code + " " + route_1 + " " + user + " " + datetime;
          var signature = key.sign(obj);
          
          connection.query('insert into ticket(route_id, user_id, is_validated, route_date, uuid, signature) values (?, ?, 0, ?, ?, ?)', [route_1, user, datetime, code, signature.toString('base64')], function (err1, result1) {
            if (!err1){
                cb(null, {message: "Successfully inserted ticket " + result1.insertId });
            }
            else{
              console.log('Error while performing Query 1.', err1);
              cb(err1,null);
            }
          });

        }else{
          
          var route_1 = data['route_1'];
          var route_2 = data['route_2'];

          //buy 2 tickets in transaction
          connection.beginTransaction(function(err){
            if(err){ cb({error: "Trnsaction error"}, null); }

            var code = uuid.v4();
            var obj = code + " " + route_1 + " " + user + " " + datetime;
            var signature = key.sign(obj);

            connection.query('insert into ticket(route_id, user_id, is_validated, route_date, uuid, signature) values (?, ?, 0, ?, ?, ?)', [route_1, user, datetime, code, signature.toString('base64')], function(err1, result1){
              if(err1){
                connection.rollback(function() {
                  cb(err1, null);
                });
              }else{
                var time2 = data['ticket_2'][0]['time'];
                var datetime2 = date + " " + time2;
                var code = uuid.v4();

                var obj = code + " " + route_2 + " " + user + " " + datetime2;
                var signature = key.sign(obj);
                connection.query('insert into ticket(route_id, user_id, is_validated, route_date, uuid, signature) values (?, ?, 0, ?, ?, ?)', [route_2, user, datetime2, code, signature.toString('base64')],function(err2, result2){
                  if(err2){
                    connection.rollback(function() {
                      cb(err2, null);
                    });
                  }else{
                    connection.commit(function(err_commit){
                      if(err_commit){
                        connection.rollback(function() {
                          cb(err_commit, null);
                        });
                      }else{
                        cb(null, {message: "Sucessfully inserted tickets " + result1.insertId + " and " + result2.insertId});
                      }
                    })
                    
                  }
                })
              }
            })
          })
        }
      }
      else{
        cb({error: "Ticket sold out!"}, null);
      }
    }
    else{
      cb(err,null);
    }
  })

}

exports.uploadTickets = function(tickets, cb){

  async.each(tickets, function(ticket, callback){
      
      if(ticket['is_validated'][0] == 1){
          console.log("update ticket");
          connection.query('update ticket set is_validated = (1) where route_id = ? and user_id = ? and route_date = ?', [ticket['route_id'], ticket['user_id'], ticket['route_date']], function (err, rows, fields) {
            if (!err){
                callback();
              }
              else{
                console.log('Error while performing Query.');
              }
          });
      }else{
        callback();
      }
  },function(err){
    if(!err){
      cb(null, {message: 'Upload successful'});
    }else{
      cb(err, null);
    }
  });
}

exports.getStatistics = function(employee, cb){
  connection.query('select uploaded_routes, uploaded_tickets, validated_tickets, fraudulent_tickets, no_shows from employee where idemployee = ?', [employee], function (err, rows, fields) {
    if (!err){
        cb(null, rows[0]);
      }
      else{
        console.log('Error while performing Query.');
        cb(err, null);
      }
  });
}

exports.updateStatistics = function(employee, body, cb){

  var uploaded_routes = parseInt(body.uploaded_routes);
  var uploaded_tickets = parseInt(body.uploaded_tickets);
  var validated_tickets = parseInt(body.validated_tickets);
  var fraudulent_tickets = parseInt(body.fraudulent_tickets);
  var no_shows = parseInt(body.no_shows);

  connection.query('update employee set uploaded_routes = uploaded_routes + ?, uploaded_tickets = uploaded_tickets + ?, validated_tickets = validated_tickets + ?, fraudulent_tickets = fraudulent_tickets + ?, no_shows = no_shows + ? where idemployee = ?', [uploaded_routes, uploaded_tickets, validated_tickets, fraudulent_tickets, no_shows, employee], function (err, rows, fields) {
    if (!err){
        cb(null, {message: 'Update successful'});
      }
      else{
        console.log('Error while performing Query.');
        cb(err, null);
      }
  });
}