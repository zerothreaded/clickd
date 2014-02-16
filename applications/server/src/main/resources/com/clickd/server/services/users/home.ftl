<#-- @ftlvariable name="" type="com.clickd.server.services.member.MemberHomeView" -->
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
			Welcome Member : ${memberEmail?html} <hr/>
		</div>
		<!-- /container -->


	</div>

	<div class="container">
		<div class="row">
			Welcome Member : 
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
