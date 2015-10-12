/** database.js **/

module.exports = function (url) {
    var database = {};
	database.url = url;
	
	
    database.auth = function (req, res) {
        // This will be available 'outside'.
        // Authy stuff that can be used outside...
    };

    // Other stuff...
    database.pickle = function(cucumber, herbs, vinegar) {
        // This will be available 'outside'.
        // Pickling stuff...
    };

    function jarThemPickles(pickle, jar) {
        // This will be NOT available 'outside'.
        // Pickling stuff...

        return pickleJar;
    };

    return module;
};