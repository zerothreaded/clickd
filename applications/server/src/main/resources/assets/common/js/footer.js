$(document).ready(function () {
	footerUpdate();
});


function footerUpdate()
{
	

	 $.getJSON('/clickd/app/user/signedinuser', function(user) {
         $('#signed-in-user').html(user.email);
      });

	 $.getJSON('/clickd/app/user/numberofsignedinusers', function(count) {
	         $('#total-users-online').html(count.value);
	 });
	 
	 
	 $.getJSON('/clickd/app/user/numberofregisteredusers', function(count) {
         $('#total-registered-users').html(count.value);

	 });
 
	 setTimeout(footerUpdate, 50*1000);
	    
}
