$(document).ready(function () {
	footerUpdate();
});

function footerUpdate()
{
	var urlChunks = location.href.split('/');
	var token = urlChunks[urlChunks.length-2];

	 $.getJSON('/users/'+token+'/details', function(user) {
         $('#signed-in-user').html(user.values.member_email);
      });

	 $.getJSON('/members/numberofsignedinmembers', function(count) {
	         $('#total-users-online').html(count.value);
	 });
	 
	 $.getJSON('/members/numberofregisteredmembers', function(count) {
         $('#total-registered-users').html(count.value);

	 });
	 
	 setTimeOut("footerUpdate", 5*1000);
}
