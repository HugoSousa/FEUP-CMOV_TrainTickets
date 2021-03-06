/** user.js **/
var database = require('../modules/database.js');
var moment = require('moment');
var jwt = require('jwt-simple');
var fs = require('fs');

var User = function (data) {  
    this.data = data;
    //console.log(this.data);
}

User.prototype.data = {};
User.prototype.name = {}
User.prototype.username = {}
User.prototype.password = {}
User.prototype.creditcard_type = {}
User.prototype.creditcard_number = {}
User.prototype.creditcard_validity = {}


User.prototype.validate = function () {
    console.log(this.data);  
	if ('name' in this.data && 'username' in this.data && 'password' in this.data && 'creditcard_type' in this.data && 'creditcard_number' in this.data && 'creditcard_validity' in this.data) {
		this.name = this.data.name;
		this.username = this.data.username;
		this.password = this.data.password;
		this.creditcard_type = this.data.creditcard_type;
		this.creditcard_number = this.data.creditcard_number;
		this.creditcard_validity = this.data.creditcard_validity;

        var dateSplit = this.data.creditcard_validity.split("/");
        var date = new Date(dateSplit[1], dateSplit[0]-1, 1);
        var d = moment(date);
        console.log(d.isValid());
        if(d == null || !d.isValid()) return false;
        if(moment().diff(d,'months', true) >= 1) return false;
		return true;
	}
	return false;
}

User.prototype.changeName = function (name) {  
    this.data.name = name;
}

User.findById = function (id, callback) {  
    db.get('users', {id: id}).run(function (err, data) {
        if (err) return callback(err);
        callback(null, new User(data));
    });
}

User.login = function (username, password, res, app) {
        database.getUserByUsername(username, function (err, user) {

            if (err  || typeof user == 'undefined') {
                return res.status(400).json({
                    error: 'User not found'
                });
            }

            if (!user || password != user.password) {
                return res.status(400).json({
                    error: "Wrong Credentials"
                });
            }

            var expires = moment().add(7, 'days').valueOf();
            var token = jwt.encode({
                iss: user.username,
                exp: expires
            }, app.get('jwtTokenSecret'));

            delete user.password;
            res.json({
                token  : token,
                expires: expires,
                user   : user
            });
        })
};

User.loginemployee = function (email, password, res, app) {
        database.getEmployeeByEmail(email, function (err, user) {
            console.log(err);
            console.log(user);

            if (err || typeof user == 'undefined') {
                return res.status(400).json({
                    error: 'Employee not found'
                });
            }

            if (!user || password != user.password) {
                return res.status(400).json({
                    error: "Wrong Credentials"
                });
            }

            var expires = moment().add(7, 'days').valueOf();
            var token = jwt.encode({
                iss: user.email,
                exp: expires
            }, app.get('jwtTokenSecret'));

            delete user.password;
            res.json({
                token  : token,
                expires: expires,
                user   : user,
                pub    : fs.readFileSync('./data/public.pub').toString()
            });
        })
};


module.exports = User;