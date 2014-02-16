	$(document).ready(function() {
		
		$("#login-nav").submit(function() {
			var data = $("#login-nav").serialize();
			var url = "/members/signin";
			
			var request = $.ajax({
				  url: url,
				  type: "POST",
				  data: data,
				  dataType: "json"
				});
				 
				request.done(function( msg ) {
				 if (msg.values.status == "ok")
					 {
					 	var user_token = msg.values.user_token;
					 	window.location = "/users/"+user_token+"/home";
					 }
				 else
					 {
					 	alert("Bad username/password");
					 }
				});
				 
				request.fail(function( jqXHR, textStatus ) {
				  alert( "Request failed: " + textStatus );
				});
			
			return false;
		});
		
		$("#register-form").submit(function() {
			var data = $("#login-nav").serialize();
			var url = "/users/signin";
			
			var request = $.ajax({
				  url: url,
				  type: "POST",
				  data: data,
				  dataType: "json"
				});
				 
				request.done(function( msg ) {
				 if (msg.values.status == "ok")
					 {
					 	window.location = '/users';
					 }
				 else
					 {
					 	alert("Bad username/password");
					 }
				});
				 
				request.fail(function( jqXHR, textStatus ) {
					alert("Bad username/password");
				});
			
			return false;
		});
		
	});