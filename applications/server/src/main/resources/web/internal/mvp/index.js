var clickdApplication = angular.module('clickdApplication', ['ngCookies', 'ngResource']);

clickdApplication.controller('AppController', function($scope, $cookies, $resource, $http, $timeout) {
	
	
	$scope.model = {
			"activeTab" : "0",
			"dateView" : "list",
			"what" : 
				{
					selectedFoodType : []
				}
	}; 

	$scope.selectActiveTab = function(activeTab)
	{
		$scope.model.activeTab = activeTab;
		console.log("setting active tab "+$scope.model.activeTab);

	}
	
	$scope.init = function () {
		console.log("loading");
	}
	
  });

