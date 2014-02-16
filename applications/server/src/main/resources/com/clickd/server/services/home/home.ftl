<#-- @ftlvariable name="" type="com.clickd.server.services.home.HomeView" -->
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

<title>home</title>

<!-- Bootstrap core JavaScript -->
<script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="/assets/dist/js/bootstrap.min.js"></script>

<!--  jquery form validation -->
<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery-form-validator/2.1.38/jquery.form-validator.min.js"></script>

<!-- Bootstrap core CSS -->
<link href="/assets/dist/css/bootstrap.css" rel="stylesheet">

<!-- Custom styles for this template -->
<link href="/assets/common/css/navbar.css" rel="stylesheet">
<link href="/assets/common/css/sticky-footer-navbar.css" rel="stylesheet">

<link href="/assets/views/home/home.css" rel="stylesheet">
<link href="/assets/components/nav-bar-with-popup-sign-in/login.css" rel="stylesheet">
<link href="/assets/components/modified-modal-buttons/modal.css" rel="stylesheet">
<link href="/assets/components/responsive-quote-carousel/carousel.css" rel="stylesheet">
<script src="/assets/components/nav-bar-with-popup-sign-in/login.js"></script>
<script src="/assets/components/responsive-quote-carousel/carousel.js"></script>
<script src="/assets/views/home/login.js"></script>
<script src="/assets/common/js/footer.js"></script>

<script>
function fadeOut()
{
	$("#overlay").addClass("overlay2");
	$("#overlay").removeClass("overlay");
	window.setTimeOut(1, 'fadeOut2');
}
	
function fadeOut2()
{
	$("#overlay2").removeClass("overlay2");	
}
	
	$(document).ready(function() {
		var hash = window.location.hash;
		if (hash=='#signupfailed')
			$('span#signupfailed').removeClass('hidden');
		
		
		window.setTimeOut(3, 'fadeOut');
		
		$("#btn-form-sign-in").click(function() {
			//change the form target
			$("#sign-up-form").attr("action", "/assets/app/user/signin");
			//then submit the form
			$("#sign-up-form").submit();
		})
	});
</script>


</head>

<body>
<!-- Static navbar -->

	<!-- Wrap all page content here -->
	<div id="wrap">
		<#include "../../../common/home_header.ftl">
		<div class="container" >

			<div class="row">
				<div class="col-md-5 col-md-offset-1 hidden-xs hidden-sm carousel-group hidden">
					<h1>Clickd.<br/><small>Click to connect.</small></h1>
				</div>
				
				<div class="col-xs-12 col-md-4 col-md-offset-1 col-sm-6 col-sm-offset-6 signup-group">
					<form role="form" action="/members/register" method="post" id="sign-up-form" class="has-validation-callback">
						<h2>
							Sign Up<small> to see who you click with</small>
						</h2>
						<hr>
						<span id="sign-up-failed" class="label label-danger hidden">Sorry, your sign up failed.</span>
						<div class="row">
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<input type="text" name="first_name" id="first_name" class="form-control input-md valid" placeholder="First Name" tabindex="1" data-validation="length" data-validation-length="min2" style="">
								</div>
							</div>
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<input type="text" name="last_name" id="last_name" class="form-control input-md valid" placeholder="Last Name" tabindex="2" data-validation="length" data-validation-length="min2" style="">
								</div>
							</div>
						</div>
						<div class="form-group">
							<input type="email" name="email" id="email" class="form-control input-md valid" placeholder="Email Address" tabindex="4" data-validation="email">
						</div>
						<div class="row">
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<input type="password" name="password_confirmation" id="password_confirmation" class="form-control input-md" placeholder="Password" tabindex="5" data-validation="strength" data-validation-strength="1">
								</div>
							</div>
							<div class="col-xs-6 col-sm-6 col-md-6">
								<div class="form-group">
									<input type="password" name="password" id="password" class="form-control input-md" placeholder="Confirm Password" tabindex="6" data-validation="confirmation">
								</div>	
							</div>
						</div>
						<div class="row">
							<div class="col-xs-12 col-sm-12 col-md-12">
								By clicking <strong class="label label-primary">Register</strong>, you agree to the <a href="http://localhost:8080/assets/app/home#" data-toggle="modal" data-target="#t_and_c_m">Terms
									and Conditions</a> set out by this site, including our Cookie Use.
							</div>
						</div>

						<hr>
						<div class="row">
							<div class="col-xs-6 col-md-12">
								<input type="submit" value="Register" class="btn btn-primary btn-block btn-lg" tabindex="7">
							</div>

						</div>
					</form>
				</div>


			</div>
			<div class="row">&nbsp;</div>
		</div>
		<!-- /container -->


	</div>

	<div class="container">
		<div class="row">
			<div id="t_and_c_m" class="modal fade in" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog">
					<div class="modal-content">

						<div class="modal-header">
							<a class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span></a>
							<h4 class="modal-title" id="myModalLabel">Terms and Conditions</h4>
						</div>
						<div class="modal-body">
							<h4>You need to agree to the Clickd terms and conditions to register</h4>
							<p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula. We own you.</p>
						</div>
						<div class="modal-footer">
							<div class="btn-group">
								<button class="btn btn-primary" data-dismiss="modal">OK</button>
							</div>
						</div>

					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dalog -->
			</div>
			<!-- /.modal -->
		</div>
	</div>

	<#include "../../../common/home_footer.ftl">
	<script>
		$("sign-up-form").submit(function() {

		});

		$.validate({
			modules : 'security',
			validateOnBlur : false,
			borderColorOnError : '#F00',
		 	onError : function() {
		  //    alert('Validation failed');
		    },
		    onSuccess : function(response) {
		    //	alert(response.valid+" msg: "+response.message);
		   //   alert('The form is valid!');
		     // return false; // Will stop the submission of the form
		     return true;
		    },
		});
	</script>
</body>
</html>
