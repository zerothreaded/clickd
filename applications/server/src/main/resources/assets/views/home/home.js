	$(document).ready(function() {
		var n = Math.floor(Math.random()*10+1);
		var s = "/assets/images/home/background"+n+".jpg";
		$("#wrap").css('background-image', 'url('+s+')');
		
		//alert(s);
		
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
				// alert(msg);
				 if (msg.isLoggedIn == true)
					 {
					 	window.location = "/users/home";
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
		
		

			
		$("#dateOfBirth").datepicker();
		
		$("#sign-up-form-page-1-submit").click(function () {
			$("#form-page-1").addClass("hidden");
			$("#form-page-2").removeClass("hidden");
		});
		
		$("#sign-up-form").submit(function() {
			var data = $("#sign-up-form").serialize();
			var url = "/users/register";
			
			var request = $.ajax({
				  url: url,
				  type: "POST",
				  data: data,
				  dataType: "json"
				});
				 
				request.done(function( msg )
				{
					if (msg.status == "ok")
					{
						var request2 = $.ajax({
						  url: "/users/signin",
						  type: "POST",
						  data: data,
						  dataType: "json"
						});
						 
						request2.done(function( msg2 ) {
						 if (msg2.isLoggedIn == true)
							 {
							 	window.location = "/users/home";
							 }
					
						});
					 }
				});
				 
				request.fail(function( jqXHR, textStatus ) {
					$("#email").addClass("has-error");
				 	$("#sign-up-failed").removeClass("hidden");
				});
			
			return false;
		});
		
	});