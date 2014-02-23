var clickdApplication = angular.module('clickdApplication', []);

function AppController($scope) {
    $scope.app = {	
    	name : "Da Clickd Platform",
    	version : "0.1-ALPHA", 
    	motd : " Click To Connect"
    };
}
