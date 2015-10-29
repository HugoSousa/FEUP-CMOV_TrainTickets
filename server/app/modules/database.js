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

exports.teste = function () {
  //checkTrainCapacity(1,4,null,2);
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


exports.getTrainTimes = function (from, to, cb) {

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
            for(var i = 0; i < result.trips.length; i++){
              /*
              checkTrainCapacity(from, to, result.trips[i].times[0], result.trips[i].train, i, function(data){
                //result.trips[i]['sold_out'] = data;
              });    
              */

            }

            cb(null, result);
          }

          else{
            cb(err1, null);
          }
          
          //console.log(JSON.stringify(result));
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
            
            console.log(JSON.stringify(result));

            //check sold_out for each trip
            cb(null, result);
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


exports.getUnusedTickets = function (user, cb) {

  //TODO: instead of user parameter, get user from the authentication

  connection.query('select * from ticket where user_id = ? and is_validated = (0);', [user], function (err, rows, fields) {
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

exports.checkTrainCapacity = function(from, to, datetime, train_id, index, cb){

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

                affects.push({ route: affected_routes[j], affected: affected_ticket_routes });
              }
            }

            break;
          }
        }

        console.log("AFFECTS: " + JSON.stringify(affects));

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

        exports.getTrainTimes(train.start_id, train.end_id, function(err, data){
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
   exports.getTrainTimes(from, to, function(err, data){
      if (err) {
              cb(null,err);          
          } else { 
            var found = false;
            data.trips.forEach(function(trip, index) {
              console.log(trip.times[0]);
              console.log(time);
              if (trip.times[0].toString() == time.toString()) found = trip;
               
            });
            if (!found) cb('Couldnt find trip',null);
            else  {
              var combo_array = [];
              found.stations.forEach(function(station, index, array) {
                console.log(station);
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
                   // console.log(combo_array);
                   cb(null, combo_array);
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
          var obj = {code: code, route: route_id, user: user_id, date: route_date};
          console.log(obj);
          var signature = key.sign(JSON.stringify(obj));
          connection.query('insert into ticket(route_id, user_id, is_validated, route_date) values (?, ?, 0, ?)', [route_1, user, datetime], function (err1, result1) {
            if (!err1){
                console.log(JSON.stringify(result1));
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

            connection.query('insert into ticket(route_id, user_id, is_validated, route_date) values (?, ?, 0, ?)', [route_1, user, datetime], function(err1, result1){
              if(err1){
                connection.rollback(function() {
                  cb(err1, null);
                });
              }else{
                var time2 = data['ticket_2'][0]['time'];
                var datetime2 = date + " " + time2;

                connection.query('insert into ticket(route_id, user_id, is_validated, route_date) values (?, ?, 0, ?)', [route_2, user, datetime2],function(err2, result2){
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