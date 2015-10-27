/** user.js **/
var database = require('../modules/database.js');
var moment = require('moment');
var jwt = require('jwt-simple');

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
	if ('name' in this.data && 'username' in this.data && 'password' in this.data && 'creditcard_type' in this.data && 'creditcard_number' in this.data && 'creditcard_validity' in this.data) {
		this.name = this.data.name;
		this.username = this.data.username;
		this.password = this.data.password;
		this.creditcard_type = this.data.creditcard_type;
		this.creditcard_number = this.data.creditcard_number;
		this.creditcard_validity = this.data.creditcard_validity;

        var d = moment(new Date(this.data.creditcard_validity));
        if(d == null || !d.isValid()) return false;
        if(moment().diff(d,'months', true) < 0) return false;

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

            if (err) {
                return res.status(400).json({
                    error: err
                });
            }

            if (!user || password != user.password) {
                return res.status(401).json({
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


module.exports = User;