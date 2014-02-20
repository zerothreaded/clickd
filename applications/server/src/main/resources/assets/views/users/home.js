function loadNextQuestion()
{
	var nextQuestionUrl = "/questions/next/1";
	
	var nextQuestionCall = $.ajax({
		  url: nextQuestionUrl,
		  type: "GET",
		  dataType: "json"
		});
		 
	nextQuestionCall.done(function( msg ) {
			var answers = msg["_embedded"]["question-answer-list"];
			
			var questionText = msg.questionText;
			
			$("#click-panel-question").html(questionText);
			
			for (var i = 0; i < answers.length; i++)
			{
				var j = i+1;
				var answer = answers[i];
				
				$("#click-panel-answer-"+j).html(answer.answerText);
			}
		});
}

function onAnswerClick() 
{
	alert("RALPH");
}

$(document).ready(function() {
		//check cookie status
		var cookie1 = $.cookie("userSession");
		
		if (typeof cookie1 == "undefined")
		{
			window.location="/home";
		}
		else
		{
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
					var signOutUrl = cookie.userRef+"/signout";
					
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
			
			loadNextQuestion();
		}
		
		
		
	});