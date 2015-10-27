var jwt = require('jwt-simple');
var database = require('../modules/database.js');

//setup for jwt auth
/**
 * Auth handling module using jwt secrets. Responsible for parsing and generating all access tokens and giving user data to next processor
 * @module jwt-auth
 * @exports jwt-auth
 * @param {request} req - http request
 * @param {response} res - http response
 * @param {function} next - next function to handle request after auth has been handled
 */
module.exports = function (req, res, next) {
        var token = (req.body && req.body.access_token) || (req.query && req.query.access_token) || req.headers['x-access-token'];

        if (token) {
            try {
                console.log("got token");
                //to change secret also change in engine.js
                var decoded = jwt.decode(token, 'hastodosecrettosecrettootell');

                console.log("decoded token");
                if (decoded.exp <= Date.now()) {
                        return res.status(403).json( { error: "Access token has expired" });
                } else {
                    var username = decoded.iss;

                    database.getUserByUsername(username, function (err, user) {
                        if (err || !user)
                            return res.status(403).json({ error: err });
                        delete user.password; // line can be removed, only here to avoid sending password back where it should no longer be needed
                        req.user = user;
                        return next();
                    });


                }

            } catch (err) {
                return res.status(403).json( {error: "Error parsing token", info : err});
            }
        } else {
            return res.status(403).json( {error: "Missing token"});
        }

    //check if request is made by employee
    /*function permission_employee(req, res, next) {
        jwtTokenAuthenticator(req, res, function () {
            if (!req.user.is_manager) return res.status(403).json({ error: "Permission denied" });
            else return next();
        });
    }*/

    //app.all("/api/logout");
    //app.all("/api/visits/:store");
}


