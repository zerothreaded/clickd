var clickdApplication = angular.module('clickdApplication', ['ngCookies', 'ngResource']);

clickdApplication.controller('AppController', function($scope, $cookies, $resource, $http) {
	
	
	$scope.model = {
		"currentSelection" : "candidates",
		"cookie" : "",
		"sessionRef" : "",
		"selectedUser": {},
		"selectedClique" : {},
		
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
			"currentQuestion" : { },
			"currentAnswers" : [ ]
		}
	}; 

	// Form Data
	$scope.signInFormData = { };
	$scope.registerFormData = { };
	$scope.controlFlags = {
		"requestMemberBio" : false
	};
	
	$scope.resetModel = function ()
	{
		$scope.model.currentUser.candidatesShowMenu = true;
		$scope.model.currentUser.connectionsShowMenu = true;
		$scope.model.currentUser.cliquesShowMenu = true;
	}
	
	// UPDATE CCC FROM SERVER 
	$scope.updateCCC = function ()
	{
		// REST call to get candidates
		var userRef = $scope.model.currentUser.userRef;
		console.log('User Ref :' + userRef);
		if (userRef != false) {
			// alert('WTF');
			var getCandidatesUrl = "/users/" + userRef + "/candidates";
			$http({ method  : 'GET', url     : getCandidatesUrl }) .success(function(data) { console.log(data); $scope.model.currentUser.candidates = data; });
			
			$scope.model.currentUser.connections = [];
			
			// REST call to get connections
			var getConnectionsUrl = "/users/" + userRef + "/connections"; 
			$http({ method  : 'GET', url : getConnectionsUrl, })
		    .success(function(data) {
		        console.log("got connections");
		    	console.log(data);
		    	
		    	if (typeof(data) != 'undefined' && data != null) {
		    		console.log('GET CONNECTIONS RESULT NOT NULL');
			        data.forEach(function(connection) {
			        	console.log("connection");
			        	console.log(connection);
			        	if (typeof(connection) != 'undefined' &&  connection != null) {
					    	var userRef = connection["_links"]["connection-other-user"]["href"];
					    	console.log(userRef);
					    	// Get the USER data for this connection
							$http({ url : userRef, method : "GET" })
							.success(function(user) { $scope.model.currentUser.connections = $scope.model.currentUser.connections.concat(user); } );
			        	}
			        });
		    	}
		    
		    });
		
			// REST call to get cliques
			var getCliquesUrl = "/users/" + userRef + "/cliques"; $http({ method : 'GET', url : getCliquesUrl, }).success(function(data) { console.log(data); $scope.model.currentUser.cliques = data; });
			
			$scope.$apply();
		        
			
		}
	}
		
	$scope.loadNextQuestion = function() {
		var userRef = $scope.model.currentUser.userRef;
		console.log('loadNextQuestion Called With User Ref :' + userRef);
		
		var nextQuestionUrl = "/questions/next/" + userRef;
		var nextQuestionCall = $.ajax({
			url : nextQuestionUrl,
			type : "GET",
			dataType : "json"
		});

		nextQuestionCall.done(function(msg) {
			if (typeof(msg["status"]) == 'undefined') {
				answers = msg["_embedded"]["question-answer-list"];
				questionRef = msg["ref"];
				questionRef = questionRef.split("/")[2];
				var questionText = msg.questionText;
				
				console.log('CURRENT QUESTION TXT :' + msg.questionText);
				console.log('CURRENT USER:' + $scope.model.currentUser);			
				
				$scope.model.currentUser.currentQuestion = msg;
				$scope.model.currentUser.currentAnswers = answers;
				$scope.$apply();
				console.log('POST loadNextQuestion()');
				console.log($scope.model);
				$scope.updateCCC();
				
			} else {
				// No more answers
				$("#click-panel-question").html("You're so clickd out!");
				$("#click-panel-answers").html("");
				$('#button-skip-question').hide();
				$scope.updateCCC();
			}
		
		});
	}
	
	$scope.onSelectAnswer = function(question, answer) {
		var userRef = $scope.model.currentUser.userRef;
		var questionRef = question.ref.split("/")[2];
		var answerRef = answer.ref.split("/")[2];
		console.log('onSelectAnswer()');
		console.log('qref=' + questionRef);
		console.log('aref=' + answerRef);
		var createChoiceUrl = "/choices/" + userRef + "/" + questionRef + "/" + answerRef;
		console.log(createChoiceUrl);
		
		var createChoiceCall = $.ajax({
			url : createChoiceUrl,
			type : "POST",
			dataType : "json"
		});

		createChoiceCall.done(function(msg) {
			$scope.loadNextQuestion();
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
			$scope.$apply
		});
	};
	
	// APPLICATION INITIALIZATION CODE
	// FIRST TIME ONLY - or on refresh
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
		$scope.model.currentUser.userRef = shortUserRef;
		$scope.loadNextQuestion();
	}

	$scope.isUserLoggedIn = function() { 
		// alert('isuserLoggedIn()');
		// console.log($scope.model);
		// console.log("checking login: " + $scope.model.currentUser.isLoggedIn);
		return $scope.model.currentUser.isLoggedIn;
		//return false;
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
	        $scope.model.currentUser.userRef = theUserRef;
			$scope.loadUserByRef(theUserRef);
			$scope.loadNextQuestion();
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
			
			$scope.signInFormData.email = '';
			$scope.signInFormData.password = '';
			$scope.registerFormData = { };
			
			$scope.resetModel();
			
			$scope.model.selectedUser = {};
			$scope.controlFlags.requestMemberBio = false;
			
			// TODO: Proper state deletion

			delete $cookies["userSession"];
			// TODO: TBA
			// $window.location.href = 'localhost:8080/angular';
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
		// console.log("req member bio");
		return $scope.controlFlags.requestMemberBio;	
	}
	
	$scope.onClickCandidates = function()
	{
		$scope.model.currentSelection = 'candidates';
		if ($scope.model.currentUser.candidatesShowMenu == true) {
			$scope.model.currentUser.candidatesShowMenu = false;
		} else {
			$scope.model.currentUser.candidatesShowMenu = true;
		}
	}
	
	$scope.onClickConnections = function()
	{
		$scope.model.currentSelection = 'connections';
		if ($scope.model.currentUser.connectionsShowMenu == true) {
			$scope.model.currentUser.connectionsShowMenu = false;
		} else {
			$scope.model.currentUser.connectionsShowMenu = true;
		}
	}
	
	$scope.onClickCliques = function()
	{
		$scope.model.currentSelection = 'cliques';
		if ($scope.model.currentUser.cliquesShowMenu == true) {
			$scope.model.currentUser.cliquesShowMenu = false;
		} else {
			$scope.model.currentUser.cliquesShowMenu = true;
		}
	}
	
	$scope.onClickCandidate = function(candidate)
	{
		console.log('CANDIDATE');
		console.log(candidate.firstName);
		$scope.model.currentSelection = 'Candidate : ' + candidate.firstName;
		$scope.model.selectedUser = candidate;
		console.log($scope.model.selectedUser);
	}
	
	$scope.onClickConnection = function(connection)
	{
		console.log('CONNECTION');
		console.log(connection);
		$scope.model.currentSelection = 'Connection : ' + connection.firstName;
		$scope.model.selectedUser = connection;
	}
	
	$scope.onClickClique = function(clique)
	{
		console.log('CLIQUE');
		console.log(clique.name);
		$scope.model.currentSelection = 'Clique : ' + clique.name;
		$scope.model.selectedClique = clique;
	}
	
	$scope.isUserSelected = function (otherUser)
	{
		console.log("checking if users are the same");
		return otherUser == $scope.model.selectedUser.ref;
	}
	
	$scope.isCliqueSelected = function (otherClique)
	{
		console.log("checking if cliques are the same");
		return otherClique == $scope.model.selectedClique.ref;
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


