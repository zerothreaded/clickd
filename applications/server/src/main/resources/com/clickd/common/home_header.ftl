	<div class="navbar navbar-default" role="navigation">
	<div class="col-md-3 col-sm-6 col-md-offset-1">
				<a class="navbar-brand">dating &nbsp;@&nbsp; clickd</a>
	</div>
	
	<script>
		function signinRalph() {
			$("#login-email-input").attr("value", "ralph.masilamani@clickd.org");
			$("#login-password-input").attr("value", "rr00");
			$("#login-nav").submit();
		}
		function signinJohn() {
			$("#login-email-input").attr("value", "john.dodds@clickd.org");
			$("#login-password-input").attr("value", "jj00");
			$("#login-nav").submit();
		}
		function signinEdward() {
			$("#autosignin").attr("action", "/clickd/app/user/signin");
			$("#login-email-input").attr("value", "edward.dodds@clickd.org");
			$("#login-password-input").attr("value", "ee00");
			$("#login-nav").submit();
		}
		function signinToby() {
			$("#login-email-input").attr("value", "toby.weiss@clickd.org");
			$("#login-password-input").attr("value", "tt00");
			$("#login-nav").submit();
		}
		function signinSimone() {
			$("#login-email-input").attr("value", "simone@simonewagener.com");
			$("#login-password-input").attr("value", "ss00");
			$("#login-nav").submit();
		}
		function signinSuzanne() {
			$("#login-email-input").attr("value", "suzanne.noble@clickd.org");
			$("#login-password-input").attr("value", "ss00");
			$("#login-nav").submit();
		}
	</script>
	
	<div class="col-md-3">

			<img class="img-circle login-img" type="image"  onclick="signinRalph()" src="/assets/images/members/facebook_ralph.jpg" />
			<img class="img-circle login-img" type="image"  onclick="signinJohn()" src="/assets/images/members/facebook_john.jpg" />
			<img class="img-circle login-img" type="image"  onclick="signinEdward()" src="/assets/images/members/facebook_edward.jpg" />
			<img class="img-circle login-img" type="image"  onclick="signinToby()" src="/assets/images/members/facebook_toby.jpg" />
			<img class="img-circle login-img" type="image"  onclick="signinSimone()" src="/assets/images/members/facebook_simone.jpg" />
			<img class="img-circle login-img" type="image"  onclick="signinSuzanne()" src="/assets/images/members/facebook_suzanne.jpg" />
	</div>
	
	<div class="col-md-2 col-md-offset-1 text-right navbar-error hidden" id="invalid-username-password-field"><div class="label label-danger" >Invalid username/password</div></div>
	<div class="col-xs-12 col-sm-6 col-md-4  login-group">
		<h2 class="visible-xs">
			Sign in <small>to clickd.</small>
		</h2>
		
		<form class="form-group login-form has-validation-callback" role="form" method="post" action="/users/signin" accept-charset="UTF-8" id="login-nav">
			<div class="input-group">
				<div class="col-md-5 col-sm-5 form-group navbar-form-group" id="login-email-group">
					<input type="email" class="form-control col-md-3 col-sm-3" placeholder="Email" name="email" id="login-email-input">
				</div>
				<div class="col-md-5 col-sm-5 form-group navbar-form-group" id="login-password-group">
					<input type="password" class="form-control" placeholder="Password" name="password" id="login-password-input">
				</div>

				<div class="col-md-1 col-sm-1 form-group navbar-form-group">
					<button class="btn btn-default" type="submit" id="login-submit">Sign In</button>
				</div>
			</div>
		</form>
	</div>
	</div>