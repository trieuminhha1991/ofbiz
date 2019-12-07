$(function(){
	PhysicalInvDetailInfoObj.init();
});
var PhysicalInvDetailInfoObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#physicalInventoryId").text(physicalInvObj["physicalInventoryId"]);
		$("#facilityId").text(jQuery('<div />').html(physicalInvObj["facilityName"]).text());
		$("#partyId").text(jQuery('<div />').html(partyFullName).text());
		$("#physicalInventoryDate").text(DatetimeUtilObj.formatFullDate(new Date(physicalInvObj["physicalInventoryDate"])));
		if (physicalInvObj["generalComments"] != null && physicalInvObj["generalComments"] != undefined && physicalInvObj["generalComments"] != ""){
			$("#generalComments").text(physicalInvObj["generalComments"]);
		} else {
//			$("#generalComments").text("_NA_");
		}
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
	};
	var initValidateForm = function(){
	};
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	return {
		init: init,
	}
}());