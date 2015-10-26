// BASE SETUP
// =============================================================================

// call the packages we need
var express    = require('express');
var bodyParser = require('body-parser');
var app        = express();
var morgan     = require('morgan');

var async = require('async');

// configure app
app.use(morgan('dev')); // log requests to the console

// configure body parser
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port     = process.env.PORT || 8080; // set our port

var User     = require('./app/models/user');
var database = require('./app/modules/database.js');

// GENERAL ROUTING
// =============================================================================

var router = express.Router();

router.use(function(req, res, next) {
	console.log('Request arrived.');
	next();
});


router.get('/', function(req, res) {
	res.json({ result: {message:'Welcome to Train Tickets API!' } });	
});

// USER ROUTING
// =============================================================================

router.route('/register')
	.post(function(req, res) {
		/*
		var new_user = new User(req.body);
		if (!new_user.isValid()) res.json({ result: {message:'Invalid' } });
		
		else */
		res.json({ result: {message:'Sucess' } });
	})

router.route('/login')
	.post(function(req, res) {
		// TODO check if req has login data
		// TODO preform auth and return ids
		res.json({ result: {message:'Sucess' , token:'xyz'} });
})

router.route('/teste')
	.get(function(req, res) {

		database.teste();
		res.json({ result: {message:'Sucess' , tickets:[]} });
})

router.route('/stations')
	.get(function(req, res) {
		// TODO check if req has login enabled
		// TODO return stations from auth user
		database.getStations(function(err, data){
			if (err) {
	            console.log("ERROR : ",err);            
	        } else {            
	            res.json({stations: data});   
	        }  
		});
		//res.json({ result: {message:'Sucess' , tickets:[]} });
})

//return unused tickets of a user
router.route('/tickets')
	.get(function(req, res) {
		//database.teste();
		// TODO check if req has login enabled
		// TODO return tickets from auth user
		var user = req.query.user;

		database.getUnusedTickets(user, function(err, data){
			if (err) {
	            // error handling code goes here
	            console.log("ERROR : ",err);            
	        } else {            
	            // code to execute on data retrieval
	            console.log("result from db is : ", data);
	            res.json(data);   
	        }    
		});
		//res.json({ result: {message:'Sucess' , tickets:[]} });
})

router.route('/route')
	.get(function(req, res) {
		// TODO check if req has login enabled
		// TODO return tickets from auth user
		var from = req.query.from;
		var to = req.query.to;
		var time = req.query.time;
		var date = req.query.date;

		database.getRoute(from, to, time, date, function(err, data){
			if (err) {
	            console.log("ERROR : ",err);            
	        } else {            
	            res.json(data);   
	        }    
		});
		//res.json({ result: {message:'Sucess' , tickets:[]} });
})

//returns the distance, price and starting times, given a starting and ending station
router.route('/routes')
	.get(function(req,res) {
		var from = req.query.from;
		var to = req.query.to;
		var date = req.query.date; //needed to check if tickets are sold_out
		/*
		database.teste(function(err, data){
			if (err) {
	            // error handling code goes here
	            console.log("ERROR : ",err);            
	        } else {            
	            // code to execute on data retrieval
	            console.log("result from db is : ",data);   
	        }    
		});*/

		database.getTrainTimes(from, to, function(err, data){
			if (err) {
	            // error handling code goes here
	            console.log("ERROR : ",err);            
	        } else {            
	            // code to execute on data retrieval
	            res.status(200);
	            console.log("result from db is : ",data);   
	            //res.json({ result: {message:'Sucess' , tickets:[]} });
	            res.json(data);
	            /*
	            async.forEachOf(data.trips, function(value, key, callback){
	            	database.checkTrainCapacity(from, to, value.times[0], value.train, key, function(data){

						console.log("AAAAAA");
	            		console.log(JSON.stringify(data));
	            		//data.trips[key]['sold_out'] = data['result'];
	            		
	            	})
	            	callback();
            	},
	            function(err){

	            	res.json({ result: {message:'Sucess' , tickets:[]} });
	            } 
            	);
				*/
	            /*
	            for(var i = 0; i < data.trips.length; i++){
	              checkTrainCapacity(from, to, data.trips[i].times[0], data.trips[i].train, function(data){
	                //result.trips[i]['sold_out'] = data;
	                console.log("SAME SHIT");
	              });    
	              
	              console.log("JA FOSTE");

	            }*/

	            
	        }    
		});		
})
	
// TRAIN ROUTING
// =============================================================================	

router.route('/schedule')
.get(function(req, res) {
	// TODO check if req has station 1 and 2 data
	res.json({ result: {message:'Sucess' , data:[]} });
})


//may not be needed, /schedule can send all the info?

router.route('/schedule/detail')
.get(function(req, res) {
	// TODO check if req has station 1 and 2 data
	res.json({ result: {message:'Sucess' , data:[]} });
})

// OPERATIONAL ROUTING
// =============================================================================	

router.route('/tickets/purchase')
	.post(function(req, res) {
		// TODO check if req has login enabled
		// TODO check if trip data is present
		res.json({ result: {message:'Sucess' , tickets:[]} });
})


router.route('/tickets/validate')
	.post(function(req, res) {
		// TODO check if req has login enabled
		// TODO check if ticket data is present
		res.json({ result: {message:'Sucess'} });
})


router.route('/tickets/listing')
	.post(function(req, res) {
		// TODO check if req has employee AUTH
		// TODO check if req has trip detail (like the purchase?)
		res.json({ result: {message:'Sucess'} });
})


// REGISTER OUR ROUTES -------------------------------
app.use('/api', router);

// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Listening on port ' + port);
