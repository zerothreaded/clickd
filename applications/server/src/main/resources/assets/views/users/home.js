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
		}
		
		
		
	});