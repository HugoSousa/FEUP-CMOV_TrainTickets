// BASE SETUP
// =============================================================================

// call the packages we need
var express    = require('express');
var bodyParser = require('body-parser');
var app        = express();
var morgan     = require('morgan');

// configure app
app.use(morgan('dev')); // log requests to the console

// configure body parser
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port     = process.env.PORT || 8081; // set our port

// GENERAL ROUTING
// =============================================================================

var router = express.Router();

router.use(function(req, res, next) {
	console.log('Request arrived.');
	next();
});


router.get('/', function(req, res) {
	res.json({ result: {message:'Welcome to Credit card check API!' } });	
});

// USER ROUTING
// =============================================================================

router.route('/validate')
	.post(function(req, res) {
		//cc_number
		//cc_type
		//cc_validity
		var n = random(0,10);
		if (n === 1)
			res.json( {error:'true'});
		else
			res.json( {message:'Sucess'});

	});
	
function random (low, high) {
    return Math.round(Math.random() * (high - low) + low);
}


// REGISTER OUR ROUTES -------------------------------
app.use('/api', router);

// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Listening on port ' + port);