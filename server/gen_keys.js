var NodeRSA = require('node-rsa');
var fs = require('fs');

var key = new NodeRSA({b: 368});
var publicDer = key.exportKey('public');
var privateDer = key.exportKey('private');
console.log(publicDer);
console.log(privateDer);

fs.writeFileSync("./data/public.pub", publicDer);
fs.writeFileSync("./data/private.pem", privateDer);
console.log('Key generation complete');