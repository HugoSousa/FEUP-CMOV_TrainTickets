/** database.js **/

var mysql = require('mysql');

var connection = mysql.createConnection({
  host     : 'localhost',
  port     : 3306,
  user     : 'root',
  password : 'admin',
  database : 'trainsystem'
});

exports.teste = function (callback) {
    connection.query('SELECT * from user', function(err, rows, fields) {
      if (!err){
        console.log('The solution is: ', rows);
        callback(err, rows);
      }
      else
        console.log('Error while performing Query.');
  });
}

exports.getTrainTimes = function (from, to, cb) {
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
                result.trips.push( { stations: [], times: [] } );
              }
              
              result.trips[result.trips.length - 1].stations.push(row['station_id']);
              result.trips[result.trips.length - 1].times.push(row['time']);
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
