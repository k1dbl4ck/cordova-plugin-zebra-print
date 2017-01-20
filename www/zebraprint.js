var exec = require('cordova/exec');

exports.print = function(mac, str, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'print', [mac, str]);
};

exports.list = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'ZebraBluetoothPrinter', 'list', []);
};