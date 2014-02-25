var clickdApplication = angular.module('clickdApplication', ['ngCookies', 'ngResource']);

clickdApplication.controller('AppController', function($scope, $cookies, $resource, $http) {
	
	$scope.model = {
		"currentSelection" : "candidates",
		"cookie" : "",
		"sessionRef" : "",
		// Current User view of domain
		"currentUser" : {
			"isLoggedIn" : false,
			"user" : "",
			"userRef" : "",
			// Users Candidates
			"candidates" : [ ],
			"candidatesShowMenu" : true,
			// Users Connections
			"connections" : [ ],
			"connectionsShowMenu" : true,
			// Users Cliques
			"cliques" : [ ],
			"cliquesShowMenu" : true,
			// Questions and Answers
			"currentQuestionText" : "Ask me Blud"
		}
	}; 

	// Form Data
	$scope.signInFormData = { };
	$scope.registerFormData = { };
	$scope.controlFlags = {
			"requestMemberBio" : false
	};
	
	// UPDATE CCC FROM SERVER 
	$scope.updateCCC = function ()
	{
		// REST call to get candidates
		var userRef = $scope.model.currentUser.userRef;
		console.log('User Ref :' + userRef);
		if (userRef != false)
		{
			// alert('WTF');
			var getCandidatesUrl = "/users/" + userRef + "/candidates";
			$http({
		        method  : 'GET',
		        url     : getCandidatesUrl,
		    })
		    .success(function(data) {
		        console.log(data);
		        $scope.model.currentUser.candidates = data;
		    });
			
			$scope.model.currentUser.connections = [];

			
			// REST call to get connections
			var getConnectionsUrl = "/users/" + userRef + "/connections";
			$http({
		        method  : 'GET',
		        url     : getConnectionsUrl,
		    })
		    
		    .success(function(data) {
		        console.log("got connections");
		    	console.log(data);
		        
		        data.forEach(function(connection) {
		        	console.log("connection");
		        	console.log(connection);

			    	var userRef = connection["_links"]["connection-other-user"]["href"];
			    	console.log(userRef);
			    	// Get the USER data for this connection
					$http({
						url : userRef,
						method : "GET",
					}).success(function(user) {
						$scope.model.currentUser.connections = $scope.model.currentUser.connections.concat(user);
					});

			    });
		    });
		
			// REST call to get cliques
			var getCliquesUrl = "/users/" + userRef + "/cliques";
			$http({
		        method  : 'GET',
		        url     : getCliquesUrl,
		    })
		    .success(function(data) {
		        console.log(data);
		        $scope.model.currentUser.cliques = data;
		    });
		
		} else {
			// alert('GRRR');
		}
	}
	

	$scope.loadNextQuestion = function() {
		alert('DUDE');
		var userRef = $scope.model.currentUser.userRef;
		console.log('loadNextQuestion Called With User Ref :' + userRef);
		
		var nextQuestionUrl = "/questions/next/" + userRef;
		var nextQuestionCall = $.ajax({
			url : nextQuestionUrl,
			type : "GET",
			dataType : "json"
		});

		nextQuestionCall.done(function(msg) {
			if (typeof msg["status"] == 'undefined') {
				answers = msg["_embedded"]["question-answer-list"];
				questionRef = msg["ref"];
				questionRef = questionRef.split("/")[2];
				var questionText = msg.questionText;
				
				$scope.currentUser.currentQuestionText = questionText;
				
				// $("#click-panel-question").html(questionText);
				
				for ( var i = 0; i < answers.length; i++) {
					var j = i + 1;
					var answer = answers[i];
					if (answer == null)
						break;

					$("#click-panel-answer-" + j).parent().parent().show();
					
					var image = '<img  src="/assets/images/answers/'+questionRef+'/' + answer["imageName"] + '.jpg" />';
					
					if (null == answer["imageName"] || answer["imageName"].length == 0)
						$("#click-panel-answer-" + j).html(image + answer.answerText);
					else
						$("#click-panel-answer-" + j).html(image);
				}
				
				for (var i = answers.length; i < 9; i++)
				{
					var j = i+1;
					$("#click-panel-answer-" + j).parent().parent().hide();
				}
			} else {
				// No more answers
				$("#click-panel-question").html("You're so clickd out!");
				$("#click-panel-answers").html("");
				$('#button-skip-question').hide();
			}
		});

	}
	
	// GET USER + CCC From Server
	$scope.loadUserByRef = function(theUserRef)
	{
		var userResource = $resource("/users/:userRef");
		var user = userResource.get({userRef : theUserRef}, function()
		{
			// Set the current user and the signed in status - defaults are FALSE
			$scope.model.currentUser.user = user;
			$scope.model.currentUser.userRef = theUserRef;
			$scope.model.currentUser.isLoggedIn = true;
			console.log($scope.model);
			$scope.updateCCC();
		});
	};
	
	// Check for cookie and valid user session
	var cookie = $cookies.userSession;
	// $scope.model.cookie = cookie;
	if (cookie !== undefined)
	{
		var cookie1 = JSON.parse(JSON.parse(cookie));
		$scope.model.sessionRef = cookie1.sessionRef;
		var shortUserRef = cookie1.userRef.split("/");
		shortUserRef = shortUserRef[2];
		console.log('SHORT REF + ' + shortUserRef);
		$scope.loadUserByRef(shortUserRef);
	}

	$scope.isUserLoggedIn = function() { 
		// alert('isuserLoggedIn()');
		console.log($scope.model);
		console.log("checking login: " + $scope.model.currentUser.isLoggedIn);
		return $scope.model.currentUser.isLoggedIn;
	}
	
	$scope.signInFormSubmit = function () {
		// Form Data
		$http({
	        method  : 'POST',
	        url     : '/users/signin',
	        data    : $.param($scope.signInFormData),  // pass in data as strings
	        headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
	    })
        .success(function(data) {
        	var userRefTokens = data.ref.split("/");
        	var theUserRef = userRefTokens[2];
        	var userUrl = "/users/" + theUserRef;
            console.log('signIn theUserRef: ' + theUserRef);
            console.log('$scope.model.currentUser.isLoggedIn = ' + $scope.model.currentUser.isLoggedIn);
			$scope.model.currentUser.isLoggedIn = true;
	        console.log('$scope.model.currentUser.isLoggedIn = ' + $scope.model.currentUser.isLoggedIn);
	        
			$scope.loadUserByRef(theUserRef);
	        $scope.loadNextQuestion();
			$scope.updateCCC();
       });
	}

	$scope.signOut = function ()
	{
		var signOutUrl = $scope.model.currentUser.user.ref + "/signout";
		$http({
	        method  : 'PUT',
	        url     : signOutUrl,
	    })
        .success(function(data) {
            console.log('Signed Out ' + data);
            $scope.model.currentSelection = "candidates";
			$scope.model.currentUser.isLoggedIn = false;
			$scope.model.currentUser.user = "";
			$scope.model.currentUser.userRef = "";
			delete $cookies["userSession"];
			// TODO: TBA
			$window.location.href = 'localhost:8080/angular';
        });
	}
	
	$scope.processRegisterForm = function()
	{
		console.log("register form");
		// Form processor
		$http({
	        method  : 'POST',
	        url     : '/users/register',
	        data    : $.param($scope.registerFormData),  // pass in data as strings
	        headers : { 'Content-Type': 'application/x-www-form-urlencoded' } 
	    })
        .success(function(data) {
        	$scope.signInFormData.email = $scope.registerFormData.email;
        	$scope.signInFormData.password = $scope.registerFormData.password;
 
        	$scope.signInFormSubmit();
        	
        	console.log("register done");
            console.log(data);
            
            
        });
	}
	
	
	$scope.showMemberBioRequest = function()
	{
		$scope.controlFlags.requestMemberBio = true;	
		console.log("show member bio request :" + $scope.controlFlags.requestMemberBio);
	}
	
	$scope.requestMemberBio = function()
	{
		console.log("req member bio");
		return $scope.controlFlags.requestMemberBio;	
	}
	
	$scope.onClickCandidates = function()
	{
		$scope.model.currentSelection = 'candidates';
		if ($scope.model.currentUser.candidatesShowMenu == true) {
			$scope.model.currentUser.candidatesShowMenu = true;
		} else {
			$scope.model.currentUser.candidatesShowMenu = true;
		}
	}
	
	$scope.onClickConnections = function()
	{
		$scope.model.currentSelection = 'connections';
		if ($scope.model.currentUser.connectionsShowMenu == true) {
			$scope.model.currentUser.connectionsShowMenu = true;
		} else {
			$scope.model.currentUser.connectionsShowMenu = true;
		}
	}
	
	$scope.onClickCliques = function()
	{
		$scope.model.currentSelection = 'cliques';
		if ($scope.model.currentUser.cliquesShowMenu == true) {
			$scope.model.currentUser.cliquesShowMenu = true;
		} else {
			$scope.model.currentUser.cliquesShowMenu = true;
		}
	}
	
	$scope.onClickCandidate = function(candidate)
	{
		console.log('CANDIDATE');
		console.log(candidate.firstName);
		$scope.model.currentSelection = 'Candidate : ' + candidate.firstName;
	}
	
	$scope.onClickConnection = function(connection)
	{
		console.log('CONNECTION');
		console.log(connection.firstName);
		$scope.model.currentSelection = 'Connection : ' + connection.firstName;
	}
	
	$scope.onClickClique = function(clique)
	{
		console.log('CLIQUE');
		console.log(clique.name);
		$scope.model.currentSelection = 'Clique : ' + clique.name;
	}
	
	$scope.isCandidatesMenuOn = function() { return $scope.model.currentUser.candidatesShowMenu == true; }
	$scope.isConnectionsMenuOn = function() { return $scope.model.currentUser.connectionsShowMenu == true; }
	$scope.isCliquesMenuOn = function() { return $scope.model.currentUser.cliquesShowMenu == true; }

	
  });

clickdApplication.directive('clickdHome', function() {
	return {
		restrict : "A",
		templateUrl : '/assets/angular/includes/home.html'
	}
});

clickdApplication.directive('userHome', function() {
	return {
		restrict : "A",
		templateUrl : '/assets/angular/includes/userhome.html'
	};
});


