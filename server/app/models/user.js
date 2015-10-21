/** user.js **/

var User = function (data) {  
    this.data = data;
    console.log(this.data);
}

User.prototype.data = {}

User.prototype.isValid = function () {  
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

module.exports = User;