var answers;
var questionRef;
var userRef;

var userData;

var activePage = '';

function loadUser()
{
	var userRef = getUserRef();

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
		userData = userdata;
	});
}

function loadNextQuestion() {
	var userRef = getUserRef();

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
	
	updateConnections();
	updateCliques();
	updateCandidates();

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
	console.log("in update connections");
	//ajax call to getConnections
	var userRef = getUserRef();
	
	var getConnectionsUrl = "/users/"+userRef+"/connections";
	var getConnectionsCall = $.ajax({
		url : getConnectionsUrl,
		type : "GET",
		dataType : "json"
	});
	

	$("#connection-list").html("");

	getConnectionsCall.done(function(msg) {
		
		console.log("got user connections");
		
		$("#connection-list").html("");
		
		var listHtml = '';
		
		//TODO limit to X
		msg.forEach(function(entry) {
			var userRef = entry["_links"]["connection-other-user"]["href"];
		    console.log("connected to : "+userRef);
		    
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
			var candidateLongRef = entry["user"]["ref"];
			var shortRef = candidateLongRef.split("/");
			shortRef = shortRef[2];
		    console.log(entry);

				var firstName = entry["user"]["firstName"];
				$("#candidate-list").append("<li><a href=\"#\" id=\"candidate-list-link-"+shortRef+"\"><span class=\"submenu-label\">"+
						"<img class=\"small-profile-img\" src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\">"+firstName+"</span></a></li>")

					$("#content-well").append("<li><a href=\""+candidateLongRef+"\"><span class=\"submenu-label\">"+
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

function getAge(birthDay) {
	  var now = new Date();

	  function isLeap(year) {
	    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
	  }
	  
	  var birthDate = new Date(birthDate);

	  // days since the birthdate    
	  var days = Math.floor((now.getTime() - birthDate.getTime())/1000/60/60/24);
	  var age = 0;
	  // iterate the years
	  for (var y = birthDate.getFullYear(); y <= now.getFullYear(); y++){
	    var daysInYear = isLeap(y) ? 366 : 365;
	    if (days >= daysInYear){
	      days -= daysInYear;
	      age++;
	      // increment the age only if there are available enough days for the year.
	    }
	  }
	  return age;
	}


function showComparisonPageFor(candidateRef)
{
	var myUserRef = getUserRef();

	var candidate;
	
	
	var getCandidatesUrl = "/users/"+myUserRef+"/candidates";
	var getCandidatesCall = $.ajax({
		url : getCandidatesUrl,
		type : "GET",
		dataType : "json"
	});
	
	getCandidatesCall.done(function(userCandidates)
			{
		

	
	//find the candidate
	userCandidates.forEach(function (candidateRow){
		var candidateLongRef = candidateRow["user"]["ref"];
		if (candidateLongRef == candidateRef)
			candidate = candidateRow;
	});
	
	//load the vars
	var firstName = candidate["user"]["firstName"];
	var lastName = candidate["user"]["lastName"];
	var age = getAge(candidate["user"]["dateOfBirth"]);
	var location = candidate["user"]["postCode"].toUpperCase();
	var score = candidate["score"];
	
	//create the page object
	var shortRef = candidateRef.split("/");
	shortRef = shortRef[2];
	var candidateShortRef = shortRef;
	
	//set the window content
	$("#content-window").html("<div class=\"col-md-6\" id=\"me-col-"+candidateShortRef+"\"></div><div class=\"col-md-6\" id=\"you-col-"+candidateShortRef+"\"></div>");
		
		$("#me-col-"+candidateShortRef).html("<h4><img src=\"/assets/images/members/facebook_"+userData["firstName"].toLowerCase()+".jpg\" class=\"member-sm-image\">Me</h4>");
		$("#me-col-"+candidateShortRef).append("<h5>"+userData["firstName"]+" "+userData["lastName"]+"</h5>"+
	"<strong>Age: </strong>25<br> <strong>Location: </strong>"+userData["postCode"]+"<br>"
	);
		
	
	$("#you-col-"+candidateShortRef).html("<h4><img src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\" class=\"member-sm-image\">"+firstName+"</h4>");
	$("#you-col-"+candidateShortRef).append("<h5>"+firstName+" "+lastName+"</h5>"+
			"<strong>Age: </strong>25<br> <strong>Location: </strong>"+candidate["user"]["postCode"]+"<br>"
			);
		

	//get the comparison data
	var getComparisonUrl = "/users/"+myUserRef+"/candidates/comparison/"+shortRef;
	console.log(getComparisonUrl);
	var getComparisonCall = $.ajax({
		url : getComparisonUrl,
		type : "GET",
		dataType : "json"
	});

	$("#you-col-"+candidateShortRef).append("You both like:");
	
	
	//once we have the comparison data create the info and add buttons
	getComparisonCall.done(function(msg) {
			console.log(msg);
			$("#you-col-"+candidateShortRef).append("<ul>");
			
			msg.forEach(function (entry){console.log(entry);
			
				$("#you-col-"+candidateShortRef).append("<li>"+entry+"</li>");
			});
			
			$("#you-col-"+candidateShortRef).append("</ul>");
			
			$("#you-col-"+candidateShortRef).append("<br/><br/><button class=\"btn btn-primary\" id=\"btn-add-connection-"+candidateShortRef+"\">Add Connection</button>");
			
			//add connection button
			$("#btn-add-connection-"+candidateShortRef).click(function()
			{
				$("#btn-add-connection-"+candidateShortRef).addClass("disabled");
				$("#btn-add-connection-"+candidateShortRef).html("Connected");
				
				var addConnectionUrl = "/users/"+myUserRef+"/connections/add/"+shortRef;
				var addConnectionCall = $.ajax({
					url : addConnectionUrl,
					type : "POST",
					dataType : "json"
				});
				
				console.log(addConnectionUrl);
				
				addConnectionCall.done(function(msg) {
					console.log("done add connection");
					console.log(addConnectionUrl);
					console.log(msg);
				});
			}); //end add connection
			
		}); //end comparison ajax on done

		//("<h4 id=\"content-window-header\">candidates : "+firstName+" "+lastName+"  </h4><div class=\"container\" id=\"candidate-container-"+candidateShortRef+"\"></div>")

		$("#candidates-container-"+candidateShortRef).html();
		
			});
}

function candidatesPage()
{
	activePage = 'candidates';
	
	$("#content-window").html("<h4 id=\"content-window-header\">candidates</h4><div class=\"container-fluid\" id=\"candidates-container\"></div>")
	
	var myUserRef = getUserRef();
	
	var getCandidatesUrl = "/users/"+myUserRef+"/candidates";
	var getCandidatesCall = $.ajax({
		url : getCandidatesUrl,
		type : "GET",
		dataType : "json"
	});
	
	console.log("candidates page");
	console.log(getCandidatesUrl);

	getCandidatesCall.done(function(msg) {
		console.log(msg);

	msg.forEach(function (candidate){
		var candidateLongRef = candidate["user"]["ref"];
		
		var shortRef = candidateLongRef.split("/");
		shortRef = shortRef[2];

		var firstName = candidate["user"]["firstName"];
		var lastName = candidate["user"]["lastName"];
		var age = getAge(candidate["user"]["dateOfBirth"]);
		var location = candidate["user"]["postCode"].toUpperCase();
		var score = candidate["score"];
		
		console.log(candidate);

		var candidateShortRef = shortRef;
		
		$("#candidates-container").append("<div id=\"candidate-btn-"+candidateShortRef+"\" data-user-ref=\""+candidateLongRef+"\" class=\"panel member-md-panel\">"+
		"<img src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\" class=\"member-md-image\">"+
		"<h5>"+firstName+" "+lastName+"</h5>"+
		"<strong>Age: </strong>"+age+"<br> <strong>Location: </strong>"+location+"<br>"+
		"<strong>Score: </strong>"+score+"</div>");
		
		
		$("#candidate-btn-"+candidateShortRef).unbind('click');
		$("#candidate-btn-"+candidateShortRef).click(function()
		{
			showComparisonPageFor(candidateLongRef);
		}); //end onClick canddiate btn
		
		$("#candidate-list-link-"+candidateShortRef).unbind('click');
		$("#candidate-list-link-"+candidateShortRef).click(function ()
		{
			showComparisonPageFor(candidateLongRef);
		});
				
	}); //end foreach
	}); //end update done
}


function connectionsPage()
{
	activePage = 'connections';
	
	console.log("connections page");
	$("#content-window").html("<h4 id=\"content-window-header\">connections</h4><div class=\"container-fluid\" id=\"connections-container\"></div>")
	
	var myUserRef = getUserRef();
	
	var getConnectionsUrl = "/users/"+myUserRef+"/connections";
	var getConnectionsCall = $.ajax({
		url : getConnectionsUrl,
		type : "GET",
		dataType : "json"
	});
	
	console.log("candidates page");
	console.log(getConnectionsUrl);

	getConnectionsCall.done(function(msg) {
		console.log(msg);
		
	msg.forEach(function (connection){
		var otherUserRef = connection["_links"]["connection-other-user"]["href"];
		

		
		var getUserUrl  = otherUserRef;
		var getUserCall = $.ajax({
			url : getUserUrl,
			type : "GET",
			dataType : "json"
		});
		
		console.log("getting connection user for "+otherUserRef)
		
		getUserCall.done(function(otherUser) {
			var connectionRef = connection.ref;
			connectionShortRef = connectionRef.split("/");
			connectionShortRef = connectionShortRef[4];
			
			var firstName = otherUser.firstName;
			var lastName = otherUser.lastName;
			
			var connectionUserShortRef = otherUserRef.split("/");
			connectionUserShortRef = connectionUserShortRef[2];
			
			
			$("#connections-container").append("<div id=\"connection-btn-"+connectionShortRef+"\" data-user-ref=\""+otherUserRef+"\" class=\"panel member-md-panel\" style=\"width:40%\">"+
			"<img src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\" class=\"member-md-image\">"+
			"<h5>"+firstName+" "+lastName+"</h5><strong>Status: </strong>"+connection.status+"</div>");
			
			if (connection.status == "pending")
				$("#connection-btn-"+connectionShortRef).append("<br/><button class=\"btn btn-small\" id=\"accept-connection-"+connectionShortRef+"\">Accept</button> <button class=\"btn btn-small\" id=\"reject-connection-"+connectionShortRef+"\">Reject</button>");
			
			$("#accept-connection-"+connectionShortRef).unbind('click');
			$("#accept-connection-"+connectionShortRef).click(function ()
			{
				console.log("accepting");
				var acceptConnectionUrl  = "/users/"+myUserRef+"/connections/"+connectionShortRef+"/accept";
				console.log(acceptConnectionUrl);
				var acceptConnectionCall = $.ajax({
					url : acceptConnectionUrl,
					type : "GET",
					dataType : "json"
				});
				
				acceptConnectionCall.done(function(){
					updateConnections();
					setTimeout("connectionsPage()", 600);
				});
			});
			
			$("#reject-connection-"+connectionShortRef).unbind('click');
			$("#reject-connection-"+connectionShortRef).click(function ()
			{
				console.log("rejecting");
				var rejectConnectionUrl  = "/users/"+myUserRef+"/connections/"+connectionShortRef+"/reject";
				console.log(rejectConnectionUrl);
				var rejectConnectionCall = $.ajax({
					url : rejectConnectionUrl,
					type : "GET",
					dataType : "json"
				});
				
				rejectConnectionCall.done(function(){
					updateConnections();
					setTimeout("connectionsPage()", 600);
				});
			});
			
			$("#connections-btn-"+connectionShortRef).click(function()
			{
				showComparisonPageFor(connectionLongRef);
			}); //end onClick canddiate btn
			
			$("#connections-list-link-"+connectionShortRef).click(function ()
			{
				showComparisonPageFor(connectionLongRef);
			});
		});
		
	
	
				
	}); 
	});//end foreach
}

function cliquesPage()
{
	activePage = 'cliques';
	$("#content-window-header").html("cliques");
}

function showHomePage()
{
	$("#home-wrapper").removeClass("hidden");
	$("#user-wrapper").addClass("hidden");
}

function showUserPage()
{
	$("#home-wrapper").addClass("hidden");
	$("#user-wrapper").removeClass("hidden");
}

function initLoginSignUpForm()
{
	$("#login-nav").submit(function() {
		var data = $("#login-nav").serialize();
		var url = "/users/signin";

		var request = $.ajax({
			url : url,
			type : "POST",
			data : data,
			dataType : "json"
		});

		request.done(function(msg) {
			if (msg.isLoggedIn == true) {
				initUserPage();
			} else {
				$("#login-email-group").addClass("has-error");
				$("#login-password-group").addClass("has-error");
				$("#invalid-username-password-field").removeClass("hidden");
			}
		});

		request.fail(function(jqXHR, textStatus) {
			$("#login-email-group").addClass("has-error");
			$("#login-password-group").addClass("has-error");
			$("#invalid-username-password-field").removeClass("hidden");
		});

		return false;
	});

	$("#dateOfBirth").datepicker();

	$("#sign-up-form-page-1-submit").click(function() {
		$("#form-page-1").addClass("hidden");
		$("#form-page-2").removeClass("hidden");
	});

	$("#sign-up-form").submit(function() {
		var data = $("#sign-up-form").serialize();
		var url = "/users/register";

		var request = $.ajax({
			url : url,
			type : "POST",
			data : data,
			dataType : "json"
		});

		request.done(function(msg) {
			if (msg.status == "ok") {
				var request2 = $.ajax({
					url : "/users/signin",
					type : "POST",
					data : data,
					dataType : "json"
				});

				request2.done(function(msg2) {
					if (msg2.isLoggedIn == true) {
						initUserPage();
					}

				});
			}
		});

		request.fail(function(jqXHR, textStatus) {
			$("#email").addClass("has-error");
			$("#sign-up-failed").removeClass("hidden");
		});

		return false;
	});
	
}

function initUserPage()
{
	var userRef = getUserRef();
	
	if (userRef == null)
		return;
	
	loadUser();
	
	$("#content-window").html("");

	
	var candidatesLinkHandler = function(){ candidatesPage();}
	var connectionsLinkHandler = function(){ connectionsPage();}
	var cliquesLinkHandler = function(){ cliquesPage();}
	
	$( "#candidates-link" ).unbind('click', candidatesLinkHandler);
	$( "#candidates-link" ).bind('click', candidatesLinkHandler);

	$( "#connections-link" ).unbind('click', connectionsLinkHandler);
	$( "#connections-link" ).bind('click', connectionsLinkHandler);
	
	$( "#cliques-link" ).unbind('click', cliquesLinkHandler);
	$( "#cliques-link" ).bind('click', cliquesLinkHandler);
	
	$("#home-wrapper").addClass("hidden");
	$("#user-wrapper").removeClass("hidden");
	
	var cookie1 = $.cookie("userSession");
	var cookie = jQuery.parseJSON(cookie1);

	console.log("sessionRef: "+cookie.sessionRef);
	
		var validateSignIn = $.ajax({
			url : cookie.sessionRef,
			type : "GET",
			dataType : "json"
		});
		validateSignIn.done(function(msg) {
			if (!msg.isLoggedIn) {
				showUserPage();
			//	window.location = "/home";
			}
		});

		validateSignIn.fail(function(jqXHR, textStatus) {
			showHomePage();
		//	window.location = "/home";
		});



		$("#link-sign-out").click(function() {
			var signOutUrl = cookie.userRef + "/signout";

			console.log(signOutUrl);
			
			var signOutCall = $.ajax({
				url : signOutUrl,
				type : "PUT",
				dataType : "json"
			});
			
			signOutCall.done(function(msg) {
				$.removeCookie("userSession");
			//	showHomePage();
				window.location = "/clickd";
			});
		});

	loadNextQuestion();
}

$(document).ready(function() {
	initLoginSignUpForm();
	initUserPage();

});



