  function doLoginFb()
  {
	  FB.login(function(response)
	  {
		  console.log('Login Done.')
		  console.log('Starting Import from Facebook..');
		  var currentdate = new Date();
		  var datetime = currentdate.getDay() + "/"+currentdate.getMonth() + "/" + currentdate.getFullYear() + " @ " + currentdate.getHours() + ":" + currentdate.getMinutes() + ":" + currentdate.getSeconds();
		  console.log('Starting Time: ' + datetime);
		 
		  
		  var userId = response.authResponse.userID;
		  
		  // alert(userId);
		  var userRef = '';
		  <!-- GET USER STUFF -->
		  FB.api('/me', function(response)
		  {
				var registerWithFacebookUrl = "/users/register/source/facebook";
				$.ajax({
				    type: "POST",
				    url: registerWithFacebookUrl,
				    // The key needs to match your method's input parameter
					// (case-sensitive).
				    data: "facebookData="+ encodeURIComponent(JSON.stringify(response)),
				    dataType: "json",
				    success: function(data)
				    {
				    	var jsonResponse = JSON.stringify(data);
				    	// console.log('/users/reg response =' + jsonResponse);
				    	userRef = data["ref"];
				    	console.log('USER REF = ' + userRef);
						
				    	/*
						 * FB.api('/me/albums', function(response) {
						 * console.log('ALBUMS=' + JSON.stringify(response));
						 * });
						 */
				    	  
				  	
				  		  
				  		  FB.api('/me/likes', function(response)
				  		  {
				  			var likeDetails = JSON.stringify(response);
				  			var registerLikeDataUrl = "/users/register/likes";
				  			var likeData = new FormData();
				  			likeData.append('likeData', likeDetails);
				  			$.ajax({
				  			    type: "POST",
				  			    url: registerLikeDataUrl,
				  			    // The key needs to match your method's input
								// parameter (case-sensitive).
				  			    data: {"likeData" : likeDetails, "userRef" : userRef.split("/")[2] },
				  			    dataType: "json",
				  				    success: function(data){
				  			    	console.log('/users/register/likes =' + JSON.stringify(data));
				  			    },
				  			    failure: function(errMsg) {
				  			    	console.log('ERROR=' + errMsg);
				  			    }
				  			});
						 }); //end likes
				  		
				  		  
				  		  
					  	 FB.api('/me/friends', function(response)
					  	 {
					  			 // console.log("FRIENDS="+JSON.stringify(response));
					  			var friendsArray = response.data;
					  			var i = 0;
					  			
					  			friendsArray.forEach(function(entry)
					  			{
					  				//if (i++ > 10)
					  					//return;
					  				
					  				var friendId = entry["id"];
					  				
						  			// register the user
					  				FB.api('/'+friendId, function(friendResponse)
					  				{
										var registerWithFacebookUrl = "/users/register/source/facebook";
										// console.log(friendResponse);
										// hacky -- fix
										friendResponse.email = friendResponse.first_name.toLowerCase() + "." + friendResponse.last_name.toLowerCase() + "@clickd.org";
										
										$.ajax({
										    type: "POST",
										    url: registerWithFacebookUrl,
										    // The key needs to match your
											// method's input parameter
											// (case-sensitive).
										    data: "facebookData="+encodeURIComponent(JSON.stringify(friendResponse)),
										    dataType: "json",
										    success: function(data)
										    {
											    	var jsonResponse = JSON.stringify(data);
											    	// console.log('registering user =' + jsonResponse);
											    	var friendRef = data["ref"];
											    	// console.log(friendRef);
										    		
											    	 FB.api('/'+friendId+'/likes', function(likesResponse)
											    	 {
												  			var likeDetails = encodeURIComponent(JSON.stringify(likesResponse));
												  			// likeDetails =
															// likeDetails.replace(/&/g,
															// '');
												  			// console.log('\n\n
															// TRANSFORMED=' +
															// likeDetails);
												  			
												  			var registerLikeDataUrl = "/users/register/likes";
												  			var likeData = new FormData();
												  			likeData.append('likeData', likeDetails);
												  			$.ajax({
												  			    type: "POST",
												  			    url: registerLikeDataUrl,
												  			    data: {"likeData" : likeDetails, "userRef" : friendRef.split("/")[2] },
												  			    dataType: "json",
												  				success: function(data){
												  			    	// console.log('/users/register/likes =' + JSON.stringify(data));
												  			    },
												  			    failure: function(errMsg) {
												  			    	console.log('ERROR=' + errMsg);
												  			    }
												  			});
											  		}); //end /likes
											    	 
										  		  FB.api('/' + friendId + '/checkins', function(response) {
											  			var checkinDetails = encodeURIComponent(JSON.stringify(response));
											  			console.log('CHECKINS=' + checkinDetails);
											  			var registerCheckinUrl = "/users/register/checkins";
											  			$.ajax({
											  			    type: "POST",
											  			    url: registerCheckinUrl,
											  			    data: {"checkinData" : checkinDetails, "userRef" : friendRef.split("/")[2] },
											  			    dataType: "json",
											  				    success: function(data){
											  			    	console.log('/users/register/checkins =' + JSON.stringify(data));
											  			    },
											  			    failure: function(errMsg) {
											  			    	console.log('ERROR=' + errMsg);
											  			    }
											  			});
										  		  }); //end checkins
											    	 
											   	 FB.api('/' + friendId + '/movies', function(response)
											   	 {
											  			console.log('\nMOVIES=' + JSON.stringify(response));
											  			var movieDetails = JSON.stringify(response);
											  			var registerMoviesUrl = "/users/register/movies";
											  			var movieData = new FormData();
											  			movieData.append('movieData', movieDetails);
											  			$.ajax({
											  			    type: "POST",
											  			    url: registerMoviesUrl,
											  			    // The key needs to
															// match your
															// method's input
															// parameter
															// (case-sensitive).
											  			    data: {"movieData" : encodeURIComponent(movieDetails), "userRef" : friendRef.split("/")[2] },
											  			    dataType: "json",
											  				    success: function(data){
											  			    	console.log('/users/register/movies =' + JSON.stringify(data));
											  			    },
											  			    failure: function(errMsg) {
											  			    	alert('register movies failed');
											  			    	console.log('ERROR=' + errMsg);
											  			    }
											  			});	
											  	  }); //end movies
											   	 	
											   	 FB.api('/' + friendId + '/television', function(response)
													   	 {
													  			console.log('\nTELEVISION=' + JSON.stringify(response));
													  			var tvDetails = JSON.stringify(response);
													  			var registerTvUrl = "/users/register/tv";
													  			var tvData = new FormData();
													  			tvData.append('tvData', tvDetails);
													  			$.ajax({
													  			    type: "POST",
													  			    url: registerTvUrl,
													  			    // The key needs to
																	// match your
																	// method's input
																	// parameter
																	// (case-sensitive).
													  			    data: {"tvData" : encodeURIComponent(tvDetails), "userRef" : friendRef.split("/")[2] },
													  			    dataType: "json",
													  				    success: function(data){
													  			    	console.log('/users/register/tv =' + JSON.stringify(data));
													  			    },
													  			    failure: function(errMsg) {
													  			    	alert('register elevision  failed');
													  			    	console.log('ERROR=' + errMsg);
													  			    }
													  			});	
													  	  }); //end movies

												  datetime = currentdate.getDay() + "/"+currentdate.getMonth() + "/" + currentdate.getFullYear() + " @ " + currentdate.getHours() + ":" + currentdate.getMinutes() + ":" + currentdate.getSeconds();
												  console.log('END FB IMPORT TIME for Friend : ' + friendId + ' at ' + datetime);
											   	 
											   	 
										    }, //end friend reg successs
											failure: function(errMsg)
											{
												console.log("ERROR REGISTER");
												console.log(errMsg);
												console.log(friendResponse);
											}
										});	 //end register friend ajax
										
					  			  });	// end fb api get friend
					  				
					  			}); // end foreach
					  		  
					  	 }); //end me/friends
					  	 
						  datetime = currentdate.getDay() + "/"+currentdate.getMonth() + "/" + currentdate.getFullYear() + " @ " + currentdate.getHours() + ":" + currentdate.getMinutes() + ":" + currentdate.getSeconds();
						  console.log('END ME REGISTER TIME : ' + datetime);

														  		  
														  		  
														  		/*
																 * FB.api('/me/friendlists', function(response) {
																 * console.log('FRIENDLISTS=' +
																 * JSON.stringify(response)); });
																 * 
																 * FB.api('/me/interests', function(response) {
																 * console.log('\nINTERESTS=' +
																 * JSON.stringify(response)); });
																 * 
																 * 
																 * 
																 * 
																 * 
																 * 
																 * });
																 * 
																 * 
																 * FB.api('/me/picture', function(response) {
																 * console.log('PICTURE=' +
																 * JSON.stringify(response)); });
																 */
				  		  
				    }, //end me register success
				    failure: function(errMsg) {
				    	console.log('ERROR=' + errMsg);
				    }
				}); //end register me
		    }); //end get me
		
	}, {scope: 'user_location, friends_location, user_about_me, friends_about_me, user_birthday, friends_birthday, email, user_interests, friends_interests, user_likes, friends_likes,user_photos, friends_photos, user_checkins, friends_checkins, read_friendlists'});
  } //end do login fb
  
      window.fbAsyncInit = function() {
        FB.init({
          appId      : '585166461570415',
          status     : true,
          xfbml      : true
        });
        
        FB.Event.subscribe('auth.authResponseChange', function(response) {
    	    if (response.status === 'connected') {
    	      console.log('Logged in');
    	    } else {
    	    }
    	  });
      };
      
     

      (function(d, s, id){
         var js, fjs = d.getElementsByTagName(s)[0];
         if (d.getElementById(id)) {return;}
         js = d.createElement(s); js.id = id;
         js.src = "//connect.facebook.net/en_US/all.js";
         fjs.parentNode.insertBefore(js, fjs);
       }(document, 'script', 'facebook-jssdk'));
      