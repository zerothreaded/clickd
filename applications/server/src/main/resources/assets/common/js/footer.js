$(document).ready(function () {
	footerUpdate();
});

function footerUpdate()
{
	 $.getJSON('/members/signedinuser', function(user) {
         $('#signed-in-user').html(user.email);
      });

	 $.getJSON('/members/numberofsignedinmembers', function(count) {
	         $('#total-users-online').html(count.value);
	 });
	 
	 $.getJSON('/members/numberofregisteredmembers', function(count) {
         $('#total-registered-users').html(count.value);

	 });
	 
	 setTimeOut("footerUpdate", 5*1000);
}
