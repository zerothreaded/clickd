var answers;
var questionRef;
var userRef;

var userConnections;
var userCandidates;
var userCliques;
var userData;

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
	});
}

function getUserData()
{
	var userRef = getUserRef();

	var getUserUrl = "/users/" + userRef;
	var getUserCall = $.ajax({
		url : getUserUrl,
		type : "GET",
		dataType : "json"
	});

	getUserCall.done(function(msg) {
		userData = msg;
	});
}

function postLogin()
{
	loadUser();
	loadNextQuestion();
	updateConnections();
	updateCandidates();
	updateCliques();
	getUserData();
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
	
	updateCliques();
	updateCandidates()
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
		
		userConnections = msg;
		
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
		
		userCandidates = msg;

		$("#candidate-list").html("");
		
		//TODO limit to X
		msg.forEach(function(entry) {
			var thisUserRef = entry["user"]["ref"];
		    console.log(entry);

				var firstName = entry["user"]["firstName"];
				$("#candidate-list").append("<li><a href=\""+thisUserRef+"\"><span class=\"submenu-label\">"+
						"<img class=\"small-profile-img\" src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\">"+firstName+"</span></a></li>")

					$("#content-well").append("<li><a href=\""+thisUserRef+"\"><span class=\"submenu-label\">"+
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

		userCliques = msg;
		
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


function getComparison( user1ref,  user2ref)
{

}

function candidatesPage()
{
	$("#content-window").html("<h4 id=\"content-window-header\">candidates</h4><div class=\"container-fluid\" id=\"candidates-container\"></div>")
	
	var myUserRef = getUserRef();
	getUserData();
	
	userCandidates.forEach(function (candidate){
		var thisUserRef = candidate["user"]["ref"];
		
		var shortRef = thisUserRef.split("/");
		shortRef = shortRef[2];

		var firstName = candidate["user"]["firstName"];
		var lastName = candidate["user"]["lastName"];
		var age = getAge(candidate["user"]["dateOfBirth"]);
		var location = candidate["user"]["postCode"].toUpperCase();
		var score = candidate["score"];
		
		console.log(candidate);


		var candidateBtnId = shortRef;
		
		$("#candidates-container").append("<div id=\"candidate-btn-"+candidateBtnId+"\" data-user-ref=\""+thisUserRef+"\" class=\"panel member-md-panel\">"+
		"<img src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\" class=\"member-md-image\">"+
		"<h5>"+firstName+" "+lastName+"</h5>"+
		"<strong>Age: </strong>"+age+"<br> <strong>Location: </strong>"+location+"<br>"+
		"<strong>Score: </strong>"+score+"</div>");
		
		$("#candidate-btn-"+candidateBtnId).click(function()
		{
			$("#content-window").html("<div class=\"col-md-6\" id=\"me-col-"+candidateBtnId+"\"></div><div class=\"col-md-6\" id=\"you-col-"+candidateBtnId+"\"></div>");
			
			$("#me-col-"+candidateBtnId).html("<h4><img src=\"/assets/images/members/facebook_"+userData["firstName"].toLowerCase()+".jpg\" class=\"member-sm-image\">Me</h4>");
			$("#me-col-"+candidateBtnId).append("<h5>"+userData["firstName"]+" "+userData["lastName"]+"</h5>"+
		"<strong>Age: </strong>25<br> <strong>Location: </strong>"+userData["postCode"]+"<br>"
		);
			
			
			$("#you-col-"+candidateBtnId).html("<h4><img src=\"/assets/images/members/facebook_"+firstName.toLowerCase()+".jpg\" class=\"member-sm-image\">"+firstName+"</h4>");
			$("#you-col-"+candidateBtnId).append("<h5>"+firstName+" "+lastName+"</h5>"+
					"<strong>Age: </strong>25<br> <strong>Location: </strong>"+candidate["user"]["postCode"]+"<br>"
					);
			

			
			var getComparisonUrl = "/users/"+myUserRef+"/candidates/comparison/"+shortRef;
			console.log(getComparisonUrl);
			var getComparisonCall = $.ajax({
				url : getComparisonUrl,
				type : "GET",
				dataType : "json"
			});

			$("#you-col-"+candidateBtnId).append("You both like:");
		
			getComparisonCall.done(function(msg) {
				console.log(msg);
				$("#you-col-"+candidateBtnId).append("<ul>");
				
				msg.forEach(function (entry){console.log(entry);
				
					$("#you-col-"+candidateBtnId).append("<li>"+entry+"</li>");
				});
				
				$("#you-col-"+candidateBtnId).append("</ul>");
				
				
				$("#you-col-"+candidateBtnId).append("<br/><br/><button class=\"btn btn-primary\" id=\"btn-add-connection-"+candidateBtnId+"\">Add Connection</button>");
				
				$("#btn-add-connection-"+candidateBtnId).click(function()
				{
					$("#btn-add-connection-"+candidateBtnId).addClass("disabled");
					$("#btn-add-connection-"+candidateBtnId).html("Connected");
					
					var addConnectionUrl = "/users/"+myUserRef+"/connections/add/"+shortRef;
					var addConnectionCall = $.ajax({
						url : addConnectionUrl,
						type : "POST",
						dataType : "json"
					});
					
					console.log(addConnectionUrl);
					
					addConnectionCall.done(function(msg) {
						console.log(msg);
					
					});
				});
				
			});
			
			
			
			
			//("<h4 id=\"content-window-header\">candidates : "+firstName+" "+lastName+"  </h4><div class=\"container\" id=\"candidate-container-"+candidateBtnId+"\"></div>")

			$("#candidates-container-"+candidateBtnId).html();
		});
	});
}

function connectionsPage()
{
	$("#content-window-header").html("connections");
}

function cliquesPage()
{
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
				postLogin();
				showUserPage();
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
						postLogin();
						showUserPage();
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
	
	updateConnections();
	updateCandidates();
	updateCliques();
	
	$( "#candidates-link" ).click(function()
	{
		 candidatesPage();
	});
	
	$("#connections-link").click(function()
	{
		connectionsPage();
	});
	
	
	$("#cliques-link").click(function()
	{
		cliquesPage();
	});
	
	$("#home-wrapper").addClass("hidden");
	$("#user-wrapper").removeClass("hidden");
	
	var cookie1 = $.cookie("userSession");
	var cookie = jQuery.parseJSON(cookie1);
	if (cookie.hasOwnProperty('sessionRef')) {
		var validateSignIn = $.ajax({
			url : cookie.sessionRef,
			type : "GET",
			dataType : "json"
		});
		validateSignIn.done(function(msg) {
			if (!msg.isLoggedIn) {
				showUserPage();
				postLogin();
			//	window.location = "/home";
			}
		});

		validateSignIn.fail(function(jqXHR, textStatus) {
		//	window.location = "/home";
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
			
			console.log(signOutUrl);

			signOutCall.done(function(msg) {
				$.removeCookie("userSession");
				$("#home-wrapper").removeClass("hidden");
				$("#user-wrapper").addClass("hidden");
				//window.location = "/home";
			});
		});
	}

	loadNextQuestion();
}

$(document).ready(function() {
	initLoginSignUpForm();
	initUserPage();

});



