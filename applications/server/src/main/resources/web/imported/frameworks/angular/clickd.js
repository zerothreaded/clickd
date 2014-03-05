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
	$scope.signInFormData = { signInFailed : false};
	$scope.registerFormData = { registerFailed : false };
	$scope.controlFlags = {
		"requestMemberBio" : false,
		"moreQuestionsToAsk" : true
	};
	
	$scope.resetModel = function ()
	{
		$scope.model.currentUser.candidatesShowMenu = true;
		$scope.model.currentUser.connectionsShowMenu = true;
		$scope.model.currentUser.cliquesShowMenu = true;
        $scope.model.currentSelection = "candidates";
		$scope.model.currentUser.isLoggedIn = false;
		$scope.model.currentUser.user = "";
		$scope.model.currentUser.userRef = "";
		$scope.signInFormData.email = '';
		$scope.signInFormData.password = '';
		$scope.model.selectedUser = {};
		$scope.controlFlags.requestMemberBio = false;
		$scope.controlFlags.moreQuestionsToAsk = true;
		
	}
	
	// UPDATE CCC FROM SERVER 
	$scope.updateCCC = function ()
	{
		// REST call to get candidates
		var userRef = $scope.model.currentUser.userRef;
		if (userRef != false) {
			// alert('WTF');
			var getCandidatesUrl = "/users/" + userRef + "/candidates";
			
			$http({ method  : 'GET', url     : getCandidatesUrl })
			.success(function(data) { $scope.model.currentUser.candidates = data; });
			
			$scope.model.currentUser.connections = [];
			// REST call to get connections
			var getConnectionsUrl = "/users/" + userRef + "/connections"; 
			$http({ method  : 'GET', url : getConnectionsUrl, })
		    .success(function(data) {	
		    	if (typeof(data) != 'undefined' && data != null) {
			        data.forEach(function(connection) {
			        	if (typeof(connection) != 'undefined' &&  connection != null) {
					    	var otherUserRef = connection["_links"]["connection-user"][0]["href"];
					    	if (otherUserRef == $scope.model.currentUser.user.ref)
					    		otherUserRef = connection["_links"]["connection-user"][1]["href"];
					    	
					    	// Get the USER data for this connection
							$http({ url : otherUserRef, method : "GET" })
								.success(function(user) {
									user.connectionData = connection;
									$scope.model.currentUser.connections = $scope.model.currentUser.connections.concat(user);
								} );
			        	}
			        });
		    	}
		    });
			// REST call to get cliques
			var getCliquesUrl = "/users/" + userRef + "/cliques";
			$http({ method : 'GET', url : getCliquesUrl, })
				.success(function(data) { $scope.model.currentUser.cliques = data; });
		}
	}
		
	$scope.moreQuestionsToAsk = function() {
		return $scope.controlFlags.moreQuestionsToAsk;
	}
	
	$scope.loadNextQuestion = function() {
		var userRef = $scope.model.currentUser.userRef;
		var nextQuestionUrl = "/questions/next/" + userRef;
		$http({
			url : "/questions/next/" + userRef,
			method : "GET",
			dataType : "json"
		})
		.success(function(msg) {
			if (typeof(msg["status"]) == 'undefined') {
				answers = msg["_embedded"]["question-answer-list"];
				questionRef = msg["ref"];
				questionRef = questionRef.split("/")[2];
				var questionText = msg.questionText;
				$scope.model.currentUser.currentQuestion = msg;
				$scope.model.currentUser.currentAnswers = answers;
				$scope.updateCCC();
			} else {
				// No more answers
				$scope.controlFlags.moreQuestionsToAsk = false;
				$scope.updateCCC();
			}
		});
	}
	
	$scope.onSelectAnswer = function(question, answer) {
		var userRef = $scope.model.currentUser.userRef;
		var questionRef = question.ref.split("/")[2];
		var answerRef = answer.ref.split("/")[2];
		var createChoiceUrl = "/choices/" + userRef + "/" + questionRef + "/" + answerRef;
		$http({
			url : createChoiceUrl,
			method : "POST",
			dataType : "json"
		})
		.success(function(msg) {
			$scope.loadNextQuestion();
			$scope.controlFlags.blockSelectAnswer = false;
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
		});
	};
	
	//////////////////////////////////////////////////////////////////////////////////
	// APPLICATION INITIALIZATION CODE
	// FIRST TIME ONLY - or on refresh
	// Check for cookie and valid user session
	//////////////////////////////////////////////////////////////////////////////////
	var cookie = $cookies.userSession;
	if (cookie !== undefined)
	{
		var cookie1 = JSON.parse(JSON.parse(cookie));
		$scope.model.sessionRef = cookie1.sessionRef;
		
		var getSessionUrl = cookie1.sessionRef;
		$http({
			url : getSessionUrl,
			method : "GET",
			dataType : "json"
		})
		.success(function(msg) {
			console.log("success");
			var shortUserRef = cookie1.userRef.split("/");
			shortUserRef = shortUserRef[2];
			$scope.loadUserByRef(shortUserRef);
			$scope.model.currentUser.userRef = shortUserRef;
			$scope.loadNextQuestion();
		}).error(function (data) { 
			console.log(data);
			$scope.model.currentUser.isLoggedIn = false;
    	 //   $scope.signInFormData.signInFailed = true;
       });
		
		
	}

	$scope.isUserLoggedIn = function() { 
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
			$scope.model.currentUser.isLoggedIn = true;
	        $scope.model.currentUser.userRef = theUserRef;
			$scope.loadUserByRef(theUserRef);
			$scope.loadNextQuestion();
       }).error(function (data) { 
    	   
    	   $scope.signInFormData.signInFailed = true;
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
			delete $cookies["userSession"];
        });
	}
	
	$scope.processRegisterForm = function()
	{
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
        }).error(function (data) {
        	$scope.registerFormData.registerFailed = true;
        });
	}
	
	
	$scope.showMemberBioRequest = function()
	{
		$scope.controlFlags.requestMemberBio = true;	
	}
	
	$scope.requestMemberBio = function()
	{
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
		var userRef = $scope.model.currentUser.userRef;
		var getComparisonUrl = "/users/" + userRef + "/candidates/comparison/"+ getRefParam(candidate.ref,2);
		
		console.log(getComparisonUrl);
		
		$http({ method  : 'GET', url : getComparisonUrl })
		.success(function(data) { $scope.model.selectedUserComparison = data; });
		
		$scope.model.currentSelection = 'candidates.user';
		$scope.model.selectedUser = candidate;
	}
	
	$scope.onAddConnection = function(connection) { 
		var userRef = $scope.model.currentUser.userRef;
		var candidateRef = getRefParam($scope.model.selectedUser.ref);
		var addConnectionUrl = "/users/" + userRef + "/connections/add/"+ getRefParam($scope.model.selectedUser.ref,2);
		console.log(addConnectionUrl);
		$http({ method  : 'POST', url : addConnectionUrl })
		.success(function(data) { 
			$scope.updateCCC();
		});
	
	}

	
	$scope.onClickConnection = function(connection) {
		$scope.model.currentSelection = 'connections.user';
		
		console.log(connection);
		
		$scope.model.selectedUser = connection;
	}
	
	$scope.onClickClique = function(clique) { $scope.model.currentSelection = 'Clique : ' + clique.name; $scope.model.selectedClique = clique; }
	$scope.isUserSelected = function (otherUser) { return otherUser == $scope.model.selectedUser.ref; }
	$scope.isCliqueSelected = function (otherClique) { return otherClique == $scope.model.selectedClique.ref; }
	$scope.isCandidatesMenuOn = function() { return $scope.model.currentUser.candidatesShowMenu == true; }
	$scope.isConnectionsMenuOn = function() { return $scope.model.currentUser.connectionsShowMenu == true; }
	$scope.isCliquesMenuOn = function() { return $scope.model.currentUser.cliquesShowMenu == true; }
	
	$scope.onAcceptConnection = function(connection)
	{
		var acceptConnectionUrl = connection.ref+"/accept";
		$http({ url : acceptConnectionUrl, method : "GET" })
		.success(function(connectionData) {
			$scope.model.selectedUser.connectionData = connectionData;
			console.log(connectionData);
			$scope.updateCCC();
		} );
	}
	
	$scope.onRejectConnection = function(connection)
	{
		var rejectConnectionUrl = connection.ref+"/reject";
		$http({ url : rejectConnectionUrl, method : "GET" })
		.success(function(connectionData) {
			$scope.model.selectedUser.connectionData = connectionData;
			console.log(connectionData);
			$scope.updateCCC();
		} );
	}
	
	function getRefParam(ref, param)
	{
		ref = ref.split("/");
		return ref[param];
	}
  });

