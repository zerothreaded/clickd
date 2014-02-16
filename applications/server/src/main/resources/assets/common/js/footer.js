$(document).ready(function () {
	footerUpdate();
});

function footerUpdate()
{
	 $.getJSON('/users/signedinuser', function(user) {
         $('#signed-in-user').html(user.email);
      });

	 $.getJSON('/users/numberofsignedinusers', function(count) {
	         $('#total-users-online').html(count.value);
	 });
	 
	 $.getJSON('/users/numberofregisteredusers', function(count) {
         $('#total-registered-users').html(count.value);

	 });
	 setTimeOut(footerUpdate, 5*1000);
}
