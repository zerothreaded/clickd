var answers;
var questionRef;
var userRef;

var userConnections;
var userCandidates;
var userCliques;

function loadUser() {
	var cookie1 = $.cookie("userSession");
	var cookie = jQuery.parseJSON(cookie1);

	userRef = cookie.userRef;
	var substr = userRef.split('/');
	userRef = substr[2];

	var getUserUrl = "/users/" + userRef;
	var getUserCall = $.ajax({
		url : getUserUrl,
		type : "GET",
		dataType : "json"
	});

	getUserCall.done(function(userdata) {
		var userFirstName = userdata["firstName"];
		var userLastName = userdata["lastName"];
		$('#user-image').attr("src", '/assets/images/members/facebook_' + userFirstName + '.jpg');
		$('#user-full-name').html('<strong>' + userFirstName + " " + userLastName + '</strong');
		$('#user-full-name-2').html('<strong>' + userFirstName + " " + userLastName + '</strong');

	});
}

function loadNextQuestion() {
	var cookie1 = $.cookie("userSession");
	var cookie = jQuery.parseJSON(cookie1);

	userRef = cookie.userRef;
	var substr = userRef.split('/');
	userRef = substr[2];

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
			$("#click-panel-question").html(questionText);
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

function getUserRef()
{
	var cookie1 = $.cookie("userSession");
	var cookie = jQuery.parseJSON(cookie1);
	if (cookie.hasOwnProperty('userRef')) {
		var userRef = cookie.userRef;
		var substr = userRef.split('/');
		userRef = substr[2];
		return userRef;
	}
	else
	{
		return false;
	}
}

function onAnswerClick(data) {
	var answer = answers[data - 1];
	var substr = answer.ref.split('/');
	var answerRef = substr[2];
	var userRef = getUserRef();
	var createChoiceUrl = "/choices/" + userRef + "/" + questionRef + "/" + answerRef + "";

	var createChoiceCall = $.ajax({
		url : createChoiceUrl,
		type : "POST",
		dataType : "json"
	});

	createChoiceCall.done(function(msg) {
		// alert(msg);
		loadNextQuestion();
	});
}



function updateConnections()
{
	//ajax call to getConnections
	var userRef = getUserRef();
	
	var getConnectionsUrl = "/users/"+userRef+"/connections";
	var getConnectionsCall = $.ajax({
		url : getConnectionsUrl,
		type : "GET",
		dataType : "json"
	});

	getConnectionsCall.done(function(msg) {
		

		$("#connection-list").html("");
		
		//TODO limit to X
		msg.forEach(function(entry) {
			var userRef = "/"+entry["_links"]["connection-other-user"]["href"];
		    console.log(userRef);
		    
			var getConnectionUserCall = $.ajax({
				url : userRef,
				type : "GET",
				dataType : "json"
			});
			
			getConnectionUserCall.done(function(msg) {
				var firstName = msg.firstName;
				$("#connection-list").append("<li><a href=\""+userRef+"\"><span class=\"submenu-label\">"+
						"<img class=\"small-profile-img\" src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\">"+firstName+"</span></a></li>")
			});
		});
	});
	
}

function updateCandidates()
{
	//ajax call to getConnections
	var userRef = getUserRef();
	
	var getConnectionsUrl = "/users/"+userRef+"/candidates";
	var getCandidatesCall = $.ajax({
		url : getConnectionsUrl,
		type : "GET",
		dataType : "json"
	});

	getCandidatesCall.done(function(msg) {
		

		$("#candidate-list").html("");
		
		//TODO limit to X
		msg.forEach(function(entry) {
			var thisUserRef = entry["user"]["ref"];
		    console.log(entry);

				var firstName = entry["user"]["firstName"];
				$("#candidate-list").append("<li><a href=\""+thisUserRef+"\"><span class=\"submenu-label\">"+
						"<img class=\"small-profile-img\" src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\">"+firstName+"</span></a></li>")
		});
	});
}

function updateCliques()
{
	//ajax call to getConnections
	var userRef = getUserRef();
	
	var getCliquesUrl = "/users/"+userRef+"/cliques";
	var getCliquesCall = $.ajax({
		url : getCliquesUrl,
		type : "GET",
		dataType : "json"
	});

	getCliquesCall.done(function(msg) {
		console.log(msg);
		alert(msg);

		$("#clique-list").html("");
		
		//TODO limit to X
		msg.forEach(function(entry) {
				var cliqueRef = entry["ref"];
				var cliqueName = entry["name"];
				$("#clique-list").append("<li><a href=\""+cliqueRef+"\"><span class=\"submenu-label\">"+cliqueName+"</span></a></li>");
			//			"<img class=\"small-profile-img\" src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\">"+firstName+"</span></a></li>")
		});
	});
}

$(document).ready(function() {
	// check cookie status
	var cookie1 = $.cookie("userSession");
	if (typeof cookie1 == "undefined") {
		window.location = "/home";
	} else {
		loadUser();
		
		updateConnections();
		updateCandidates();
		updateCliques();
		
		var cookie = jQuery.parseJSON(cookie1);
		if (cookie.hasOwnProperty('sessionRef')) {
			var validateSignIn = $.ajax({
				url : cookie.sessionRef,
				type : "GET",
				dataType : "json"
			});
			validateSignIn.done(function(msg) {
				if (!msg.isLoggedIn) {
					window.location = "/home";
				}
			});

			validateSignIn.fail(function(jqXHR, textStatus) {
				window.location = "/home";
			});
		}

		if (cookie.hasOwnProperty('userRef')) {
			$("#link-sign-out").click(function() {
				var signOutUrl = cookie.userRef + "/signout";

				var signOutCall = $.ajax({
					url : signOutUrl,
					type : "PUT",
					dataType : "json"
				});

				signOutCall.done(function(msg) {
					window.location = "/home";
				});
			});
		}

		loadNextQuestion();
	}

});