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
					$("#login-email-group").addClass("has-error");
				 	$("#login-password-group").addClass("has-error");
				 	$("#invalid-username-password-field").removeClass("hidden");
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
					 	
					 }
				});
				 
				request.fail(function( jqXHR, textStatus ) {
					$("#login-email-input").addClass("has-error");
				 	$("#login-password-input").addClass("has-error");
				 	$("#invalid-username-password-field").show();
				});
			
			return false;
		});
		
	});