var clickdApplication = angular.module('clickdApplication', ['ngCookies', 'ngResource']);

clickdApplication.controller('AppController', function($scope, $cookies, $resource, $http, $timeout) {
	
	$scope.model = {
		"currentSelection" : "candidates",
		"currentSelectionTitle" : "Your candidates",
		"selectedQuestionTag" : "fb.movies",
		"cookie" : "",
		"sessionRef" : "",
		"selectedUser": {},
		"selectedClique" : {},
		"currentCandidateRef" : {},
		"currentCliqueRef" : {},		
		"selectedChatTab" : "map",
		"selectedChatroom" : { "ref" : -1},
		"questionTags" : {"fb.movies" : "movies"},
		"showFbLoginLabel" : false,
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
			"connectionsShowMenu" : false,
			
			// Users Cliques
			"cliques" : [ ],
			"cliquesShowMenu" : false,
			
			// Questions and Answers
			"currentQuestion" : { "ref": -1 },
			"currentAnswers" : [ ]
		},
		
		"selectedUserPresentation" : 
			{
			
			}
	}; 

	
	$scope.init = function () {
//			$scope.updateChatroom();
//			$scope.loadNextQuestion();
		
		$scope.model.showFbLoginLabel = window.location.hash == '#fbsuccess';
//			
			var getQuestionTagsUrl = "/questions/tags/all";
			$http({ method  : 'GET', url : getQuestionTagsUrl })
			.success(function(data) { 
	
				$scope.model.questionTags = data; 
			});
			
			$scope.questionTimer = $timeout(function(){
				$scope.nextQuestionTimer();
			},60000);
	}
	
	$scope.nextQuestionTimer = function()
	{ 
		console.log("nextQuestionTimer");
		if ($scope.model.currentUser.currentQuestion.ref != -1)
		{
			$scope.onSelectAnswer($scope.model.currentUser.currentQuestion, 'skip');
		}
			

	}
	
	$scope.updateChatroom = function() {
			console.log("chat interval");

			if ($scope.model.selectedChatroom.ref != -1)
			{
				var getChatroomUrl = '';
				if ($scope.model.selectedChatroom.chatroomType == 'clique')
				{
					getChatroomUrl = "/chatrooms/get/clique/" + $scope.model.selectedChatroom.name;
				}
				else
				{
					getChatroomUrl = "/chatrooms/get/" + getRefParam($scope.model.selectedChatroom.ref,2);
				}
				console.log(getChatroomUrl);
				$http({ method  : 'POST', url : getChatroomUrl })
				.success(function(data) { 
					console.log("got chatroom "+data.ref);
					data.messages = data["_embedded"]["message-list"];
		
					$scope.model.selectedChatroom = data; 
		         //   var $id= $("#" + attr.scrollBottom);
		            $("#messages-row").scrollTop(100000);
				//	$scope.apply();
				});
			}
			
			$timeout(function(){
				$scope.updateChatroom();
			},90000);	
	};
	
	
	// Form Data
	$scope.signInFormData = { signInFailed : false};
	$scope.registerFormData = { registerFailed : false };
	$scope.chatroomPostData = { postText : "" };

	$scope.controlFlags = {
		"requestMemberBio" : false,
		"moreQuestionsToAsk" : true
	};
	
	$scope.resetModel = function ()
	{
		$scope.model.currentUser.candidatesShowMenu = true;
		$scope.model.currentUser.connectionsShowMenu = false;
		$scope.model.currentUser.cliquesShowMenu = false;
        $scope.model.currentSelection = "candidates";
        $scope.model.currentSelectionTitle = "Your candidates";
		$scope.model.currentUser.isLoggedIn = false;
		$scope.model.currentUser.user = "";
		$scope.model.currentUser.userRef = "";
		$scope.signInFormData.email = '';
		$scope.signInFormData.password = '';
		$scope.model.selectedUser = {};
		$scope.controlFlags.requestMemberBio = false;
		$scope.controlFlags.moreQuestionsToAsk = true;
		
	}
	
	$scope.loadMap = function() {
		var myLatlng = new google.maps.LatLng(51.537812325599, -0.14480018556184);
		
		var mapOptions = {
		  zoom: 12,
		  center: myLatlng
		}
		
	 	var map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
		// alert('loadMap');
		
		var urlSelection = $scope.model.currentSelection;
		if ($scope.model.currentSelection == 'candidates.user') {
			urlSelection = 'candidate=' + $scope.model.currentCandidateRef;
		}
		if ($scope.model.currentSelection == 'cliques.clique') {
			urlSelection = 'clique=' + $scope.model.currentCliqueRef;
		}
		var mapUrl = '/users/places/map/' + $scope.model.currentUser.userRef + "/" +  urlSelection;
		//console.log("getting "+mapUrl);
		// alert(mapUrl);
    	$.ajax({
			    type: "GET",
			    url: mapUrl,
			    dataType: "json",
			    success: function(data) {
			    	var checkinsArray = data;
		  			checkinsArray.forEach(function(checkin) {
			  		//	console.log(JSON.stringify(checkin));
			  			var user = checkin["_embedded"]["the-user"];
			  			var userId = user["ref"].split("/")[2];
			  			var place = checkin["_embedded"]["the-place"];
		  				var city = checkin["_embedded"]["the-place"]["city"];
		    		//	console.log('City = ' + JSON.stringify(city));
		    			addMarker(map, user["firstName"] + ' ' + user['lastName'] + ' was @ ' + place["name"], place["latitude"], place["longitude"], '/profile-img/users/' + userId + '.jpg');
		  			});
			    },
			    failure: function(errMsg) {
			    	alert('register movies failed');
			    	console.log('ERROR=' + errMsg);
			    }
    		});	
	}
	
    // Testing the addMarker function
    function addMarker(theMap, name, latitude, longitude, imageUrl) {
		var placeIcon = {
			    url: imageUrl,
			    // This marker is 20 pixels wide by 32 pixels tall.
			    size: new google.maps.Size(40, 40),
			    scaledSize : new google.maps.Size(40, 40),
			    // The origin for this image is 0,0.
			    origin: new google.maps.Point(0,0),
			    // The anchor for this image is the base of the flagpole at 0,32.
			    anchor: new google.maps.Point(0, 0)
		};
		var placePosition = new google.maps.LatLng(latitude, longitude);
		
	//	console.log('Place Position = ' + JSON.stringify(placePosition));
	
		var placeMarker = new google.maps.Marker({
		    position: placePosition,
		    map: theMap,
		    icon : placeIcon,
		    title: name
		});
	//	console.log('Marker = ' + placeMarker);
		
		// CIRCLE
	    var circleOptions = {
	    	      strokeColor: '#FF0000',
	    	      strokeOpacity: 0.5,
	    	      strokeWeight: 1,
	    	      fillColor: '#FF0000',
	    	      fillOpacity: 0.35,
	    	      map: theMap,
	    	      center: placePosition,
	    	      radius: 200
	    	    };
	    	    // Add the circle for this city to the map.
	    	    cityCircle = new google.maps.Circle(circleOptions);
    }
    
    
	// google.maps.event.addDomListener(window, 'load', $scope.loadMap());
	
	// UPDATE CCC FROM SERVER 
	$scope.updateCCC = function ()
	{
		// REST call to get candidates
		var userRef = $scope.model.currentUser.userRef;
		if (userRef != false) {
			// alert('WTF');
			console.log("updating ccc");
			var getCandidatesUrl = "/users/" + userRef + "/candidates";
			console.log(getCandidatesUrl);
			$http({ method  : 'GET', url     : getCandidatesUrl })
			.success(function(data) { $scope.model.currentUser.candidates = data; 
			console.log(data);});
			
			$scope.model.currentUser.connections = [];
			// REST call to get connections
			var getConnectionsUrl = "/users/" + userRef + "/connections"; 
			$http({ method  : 'GET', url : getConnectionsUrl, })
		    .success(function(data) {	
		    	if (typeof(data) != 'undefined' && data != null) {
			        data.forEach(function(connection) {
			        	if (typeof(connection) != 'undefined' &&  connection != null) {
			        		console.log(connection);
					    	var otherUserRef = connection["_linkLists"]["connection-user"][0]["href"];
					    	if (otherUserRef == $scope.model.currentUser.user.ref)
					    		otherUserRef = connection["_linkLists"]["connection-user"][1]["href"];
					    	
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
				.success(function(data) { 
					$scope.model.currentUser.cliques = data; 
					});
			
			// Load Google Maps
		//	$scope.loadMap();
			
			
		}
	}
	
	$scope.selectChatTab = function(tab)
	{
		$scope.model.selectedChatTab=tab;
	}
		
	$scope.moreQuestionsToAsk = function() {
		return $scope.controlFlags.moreQuestionsToAsk;
	}
	
	$scope.onClickQuestionTag = function(tag)
	{
		$scope.model.selectedQuestionTag = tag;
		$scope.loadNextQuestion();
		console.log($scope.model.selectedQuestionTag);
	}
	
	$scope.loadNextQuestion = function() {
		var userRef = $scope.model.currentUser.userRef;
		var nextQuestionUrl = "/questions/next/" + userRef + "/"+$scope.model.selectedQuestionTag;
		$http({
			url : nextQuestionUrl,
			method : "GET",
			dataType : "json"
		})
		.success(function(msg) {
			console.log("next q: "+JSON.stringify(msg));
			if (typeof(msg["status"]) == 'undefined') {
				var answerRule = msg["answerRule"];
				questionRef = msg["ref"];
				console.log(JSON.stringify(msg));
				console.log('QQQQQQ=' + questionRef);
				questionRef = questionRef.split("/")[2];
				var answers = answerRule.split("|");
				var questionText = msg.questionText;
				$scope.model.currentUser.currentQuestion = msg;
				$scope.model.currentUser.currentAnswers = answers;
				//$scope.updateCCC();
			} else {
				// No more answers
				$scope.controlFlags.moreQuestionsToAsk = false;
				//$scope.updateCCC();
			}
		});
	}
	
	$scope.onSelectAnswer = function(question, answer) {
		var userRef = $scope.model.currentUser.userRef;
		var questionRef =  $scope.model.currentUser.currentQuestion.ref.split("/")[2];
		console.log("question: "+questionRef);
		var answer = answer;
		console.log("answer: "+answer);
		var createChoiceUrl = "/choices/" + userRef + "/" + questionRef + "/answerText/" + answer;
		console.log(createChoiceUrl);
		$http({
			url : createChoiceUrl,
			method : "POST",
			dataType : "json"
		})
		.success(function(msg) {
			$scope.loadNextQuestion();
			$scope.controlFlags.blockSelectAnswer = false;
			
			$("#countdown-img").attr("src", "/web/internal/home/images/countdown.gif?x="+Math.random());

			$timeout.cancel($scope.questionTimer);
			$scope.questionTimer = $timeout(function(){
				$scope.nextQuestionTimer();
			},60000);	
			
			$scope.updateCCC();
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
			
			$scope.updateCCC();
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
            $scope.model.currentSelection = "Your candidates";

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
        $scope.model.currentSelectionTitle = "Your candidates";
		$scope.model.currentUser.candidatesShowMenu = true;
		$scope.model.currentUser.connectionsShowMenu = false;
		$scope.model.currentUser.cliquesShowMenu = false;

		$scope.loadMap();
	}
	
	$scope.onClickConnections = function()
	{
		$scope.model.currentSelection = 'connections';
        $scope.model.currentSelectionTitle = "Your connections";
        
		$scope.model.currentUser.candidatesShowMenu = false;
		$scope.model.currentUser.connectionsShowMenu = true;
		$scope.model.currentUser.cliquesShowMenu = false;
		
		$scope.loadMap();
	}
	
	$scope.onClickCliques = function()
	{
		$scope.model.currentSelection = 'cliques';
        $scope.model.currentSelectionTitle = "Your cliques";

		$scope.model.currentUser.candidatesShowMenu = false;
		$scope.model.currentUser.connectionsShowMenu = false;
		$scope.model.currentUser.cliquesShowMenu = true;
		
		$scope.loadMap();
	}
	
	$scope.onClickCandidate = function(candidate)
	{
		var userRef = $scope.model.currentUser.userRef;
		var getComparisonUrl = "/users/" + userRef + "/candidates/comparison/"+ getRefParam(candidate.ref,2);
		$scope.model.currentCandidateRef = getRefParam(candidate.ref,2);
		
		console.log(getComparisonUrl);
		
		$http({ method  : 'GET', url : getComparisonUrl })
		.success(function(data) { $scope.model.selectedUserComparison = data; });
		
		$scope.model.currentSelection = 'candidates.user';
		
		$scope.model.selectedUser = candidate;
		$scope.model.currentSelectionTitle = $scope.model.selectedUser.firstName+" "+$scope.model.selectedUser.lastName;

		$scope.model.selectedUserPresentation.connectionRequestSent = false;
		
		var getChatroomUrl = "/chatrooms/get/user/" + userRef + "/"+ getRefParam(candidate.ref,2);

		$http({ method  : 'POST', url : getChatroomUrl })
		.success(function(data) { 
			console.log("got chatroom "+data.ref);
			data.messages = data["_embedded"]["message-list"];

			$scope.model.selectedChatroom = data; 
			});
		
	
		$scope.loadMap();
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
		$scope.model.selectedUserPresentation.connectionRequestSent = true;
	}

	
	$scope.onClickConnection = function(connection) {
		$scope.model.currentSelection = 'connections.user';

		
		console.log(connection);
		
		$scope.model.selectedUser = connection;
		$scope.model.selectedUserPresentation.hasBeenRejectedByUser = false;
        $scope.model.currentSelectionTitle = $scope.model.selectedUser.firstName+" "+$scope.model.selectedUser.lastName;

		
		var connectionUserList = connection.connectionData["_linkLists"]["connection-user"];
		
		var otherUserRef =  "";
		
		$scope.model.selectedUserPresentation.isConnectionRecipient = true;
		connectionUserList.forEach(function(connectionUser) {
			console.log(connectionUser);
			console.log ($scope.model.currentUser.user.ref);
			if (connectionUser.href != $scope.model.currentUser.user.ref)
				otherUserRef = connectionUser.href;

			
			if (connectionUser.rel == "from-user" && connectionUser.href == $scope.model.currentUser.user.ref)
				$scope.model.selectedUserPresentation.isConnectionRecipient = false;
			
		});
		
		var getComparisonUrl = $scope.model.currentUser.user.ref + "/candidates/comparison/"+ getRefParam($scope.model.selectedUser.ref,2);
		
		console.log(getComparisonUrl);
		
		$http({ method  : 'GET', url : getComparisonUrl })
		.success(function(data) { $scope.model.selectedUserComparison = data; });
		$scope.loadMap();
		
		var getChatroomUrl = "/chatrooms/get/user/" + getRefParam($scope.model.currentUser.user.ref,2) + "/"+ getRefParam($scope.model.selectedUser.ref,2);

		$http({ method  : 'POST', url : getChatroomUrl })
		.success(function(data) { 
			console.log("got chatroom "+data.ref);
			data.messages = data["_embedded"]["message-list"];

			$scope.model.selectedChatroom = data; 
			});
	}
	
	$scope.onClickClique = function(clique) { 
		$scope.model.currentSelection = "cliques.clique";
		$scope.model.currentCliqueRef = getRefParam(clique.ref,2);
		var getCliqueUrl = $scope.model.currentUser.user.ref + clique.ref;

		console.log("get clique url: " + getCliqueUrl);

		var getChatroomUrl = "/chatrooms/get/clique/" + encodeURIComponent(clique.name);
		console.log("getting chatroom "+getChatroomUrl);
		$http({ method  : 'POST', url : getChatroomUrl })
		.success(function(data) { 
			data.messages = data["_embedded"]["message-list"];
			$scope.model.selectedChatroom = data; 
			});
		
		
		var getCliqueUrl = $scope.model.currentUser.user.ref+clique.ref;
		console.log("get clique url: "+getCliqueUrl);
		$http({ method  : 'GET', url : getCliqueUrl })
		.success(function(data) { 
			 $scope.model.selectedClique = data;
			 $scope.model.currentSelectionTitle =  $scope.model.selectedClique["_embedded"]["clique-name"];
			 $scope.model.selectedClique.cliqueMembers = $scope.model.selectedClique["_embedded"]["clique-members"];
			 
			 console.log("clique data:");
			 console.log(data);
		});
		$scope.loadMap();
	}
	
	$scope.getUserNameForMessage = function(message)
	{
		var userRef = message["links"]["user"]["href"];
		
		var getUserUrl = userRef;
		$http({ method  : 'GET', url : getUserUrl })
			.success(function(data) { 
				return data.firstName;
		});
	}
	
	$scope.postToChatroom = function() { 
		
		
		var postToChatroomUrl = $scope.model.selectedChatroom.ref+"/"+getRefParam($scope.model.currentUser.user.ref,2)+"/posts";

		$http({ 
			method  : 'POST', 
			url : postToChatroomUrl,
		    data    : $.param($scope.chatroomPostData),  // pass in data as strings
	        headers : { 'Content-Type': 'application/x-www-form-urlencoded' }})
		.success(function(data) { 
			$scope.chatroomPostData.postText = '';
			console.log("got chatroom "+data.ref);
			//data.messages = data["_embedded"]["message-list"];
			console.log(JSON.stringify(data));
			// $scope.model.selectedChatroom = data;
		});
	}
	
	$scope.isUserSelected = function (otherUser) { return otherUser == $scope.model.selectedUser.ref; }
	$scope.isCliqueSelected = function (otherClique) { return otherClique == $scope.model.selectedClique.ref; }
	$scope.isCandidatesMenuOn = function() { return $scope.model.currentUser.candidatesShowMenu == true; }
	$scope.isConnectionsMenuOn = function() { return $scope.model.currentUser.connectionsShowMenu == true; }
	$scope.isCliquesMenuOn = function() { return $scope.model.currentUser.cliquesShowMenu == true; }
	
	$scope.onAcceptConnection = function(connection)
	{
		// 	@Path("/{userRef}/connections/{connectionRef}/accept")
		var acceptConnectionUrl = "/users/" + $scope.model.currentUser.userRef + connection.ref + "/accept";
		$http({ url : acceptConnectionUrl, method : "GET" })
		.success(function(connectionData) {
			$scope.model.selectedUser.connectionData = connectionData;

			console.log(connectionData);
			$scope.updateCCC();
		} );
	}
	
	$scope.onRejectConnection = function(connection)
	{
		// 	@Path("/{userRef}/connections/{connectionRef}/accept")
		var rejectConnectionUrl = "/users/" + $scope.model.currentUser.userRef + connection.ref + "/reject";
		
		console.log("getting reject result");
		$http({ url : rejectConnectionUrl, method : "GET" })
		.success(function(connectionData) {
			$scope.model.selectedUser.connectionData = connectionData;
			$scope.model.selectedUserPresentation.hasBeenRejectedByUser = true;
			$scope.updateCCC();
		} );
	}
	
	function getRefParam(ref, param)
	{
		ref = ref.split("/");
		return ref[param];
	}
  });

