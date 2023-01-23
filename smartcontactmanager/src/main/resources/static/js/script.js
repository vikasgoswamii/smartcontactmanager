console.log("This is script file Working")

const toggleSidebar = () => {
	
	if($(".sidebar").is(":visible")){
		//True
		//Band karna h
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	}
	else{
		//False
		//Show karna h
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
	
};

const search = () =>{
//	console.log("Searching.....")
	
	let query = $("#search-input").val();
//	console.log(query);
	
	if(query == ''){
		$(".search-result").hide();
	}
	else{
		//Search 
//		console.log(query);
		
		//Sending request to server
		
		let url = `http://localhost:8282/search/${query}`;
		
		fetch(url).then((response) => {
			return response.json();			
			
		})
		.then((data) =>{
			//Data
			console.log(data);
			
			let text = `<div class='list-group'>`;
			
			data.forEach((contact) => {
				
				text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> ${contact.name}</a>`
				
			});
			
			text += `</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
		
		
		});
		
		$(".search-result").show();
	}
	
};




