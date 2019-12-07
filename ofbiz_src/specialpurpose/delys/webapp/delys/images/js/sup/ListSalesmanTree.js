$(document).ready(function(){
//	updateBuzz();
});


function updateBuzz() {
	updateListMessage(
		{partyIdFrom: 'ROUTE2'}, 'getRouteOfParty', 'getListRouteAndCustomer', 'test');
}

var listRouteByParty = [];

function updateListMessage(jsonObject, url, list, id) {
	listRouteByParty = [];
	jQuery.ajax({
		url : url,
		type : "POST",
		data : jsonObject,
		success : function(res){
			listRouteByParty = res[list];
			renderMessageHtml(listRouteByParty, id);
		}
	
	});
}
	
function renderMessageHtml(data, id) {
	var buzz = "";
	for ( var x in data) {
		buzz += "<li><a style='color:red'>" + data[x].partyIdFrom + ": </a>" + data[x].partyIdTo + "</li>";
	}
	$("#" + id).html(buzz);
}	


	
	