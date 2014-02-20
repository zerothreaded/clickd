var answers;
var questionRef;
var userRef;

function loadNextQuestion()
{
	var cookie1 = $.cookie("userSession");
	var cookie = jQuery.parseJSON(cookie1);

	userRef = cookie.userRef;
	var substr = userRef.split('/');
	userRef = substr[2];

	var nextQuestionUrl = "/questions/next/" + userRef;
	var nextQuestionCall = $.ajax({
		  url: nextQuestionUrl,
		  type: "GET",
		  dataType: "json"
		});
		 
	nextQuestionCall.done(function(msg) {
		if (typeof msg["status"] == 'undefined' ) {
			answers = msg["_embedded"]["question-answer-list"];
			questionRef = msg["ref"];
			questionRef = questionRef.split("/")[2];
			var questionText = msg.questionText;
			$("#click-panel-question").html(questionText);
			for (var i = 0; i < answers.length; i++)
			{
				var j = i + 1;
				var answer = answers[i];
				var image = '<img  src="/assets/images/answers/' + answer["imageName"] + '.jpg" />';
				$("#click-panel-answer-" + j).html(image + 	answer.answerText);
				// $("#click-panel-answer-" + j).html(image);
			}
		} else {
			// No more answers
			$("#click-panel-question").html("You're so clickd out!");
			$("#click-panel-answers").html("");
			$('#button-skip-question').hide();
		}
	});
}

function onAnswerClick(data) 
{
	var answer = answers[data - 1];
	var substr = answer.ref.split('/');
	var answerRef  = substr[2];
	var cookie1 = $.cookie("userSession");
	var cookie = jQuery.parseJSON(cookie1);
	if (cookie.hasOwnProperty('userRef'))
	{
		userRef = cookie.userRef;
		var substr = userRef.split('/');
		userRef = substr[2];
	}
	var createChoiceUrl = "/choices/" + userRef + "/" + questionRef + "/" + answerRef + "";
	
	var createChoiceCall = $.ajax({
		  url: createChoiceUrl,
		  type: "POST",
		  dataType: "json"
		});
		 
	createChoiceCall.done(function(msg) {
		// alert(msg);
		loadNextQuestion();
	});
}

$(document).ready(function() {
		//check cookie status
		var cookie1 = $.cookie("userSession");
		if (typeof cookie1 == "undefined")
		{
			window.location="/home";
		} else {
			var cookie = jQuery.parseJSON(cookie1);
			if (cookie.hasOwnProperty('sessionRef'))
			{	var validateSignIn = $.ajax({
				  url: cookie.sessionRef,
				  type: "GET",
				  dataType: "json"
				});
				validateSignIn.done(function( msg ) {
					if (!msg.isLoggedIn)
					{
						window.location="/home";
					}
				});
				
				validateSignIn.fail(function( jqXHR, textStatus ){
						window.location="/home";
				});
			}
			
			if (cookie.hasOwnProperty('userRef'))
			{
				$("#link-sign-out").click(function() {
					var signOutUrl = cookie.userRef + "/signout";
					
					var signOutCall = $.ajax({
						  url: signOutUrl,
						  type: "PUT",
						  dataType: "json"
						});
						 
					signOutCall.done(function( msg ) {
								window.location="/home";
						});
				});
			}
			$('#user-image').attr("src", '/assets/images/members/facebook_' + 'ralph' + '.jpg');
			loadNextQuestion();
		}
		
	});