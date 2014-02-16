	$(document).ready(function() {
		
		
		$("#login-nav").submit(function() {
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
					 	window.location = '/member';
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
					 	window.location = '/member';
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
		
	});