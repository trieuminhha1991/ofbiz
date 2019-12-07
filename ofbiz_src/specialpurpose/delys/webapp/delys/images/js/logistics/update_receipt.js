$(document).ready(function(){
	
	function init(){
		var list = $("select[name^='facilityId']");
		for(var x = 0; x < list.length; x++){
			(function(x){
				var cur = $(list[x]);
				update(cur,{
					facilityId: cur.val(),
					contactMechPurposeTypeId: "SHIPPING_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'facilityId', 'contactMechId');
				cur.change(function(){
					update(cur, {
						facilityId: cur.val(),
						contactMechPurposeTypeId: "SHIPPING_LOCATION",
						}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'facilityId', 'contactMechId');
				});;
			})(x);
		}
	}
	function update(obj, jsonObject, url, data, key, value, inputid, id) {
		var tmp = obj.attr("name");
		var end = tmp.substring(inputid.length, (id.length + 1));
		var outId = id + end;
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, outId);
	        }
	    });
	}
	function renderHtml(data, key, value, id){
		var y = "";
		var out = $("select[name='"+id+"']");
		for (var x in data){
			y += "<option value='" + data[x][key] + "'>";
			y += data[x][value] + "</option>";
			if(x == 0){
				out.val(data[x][key]);
			}
		}
		out.html(y);
	}
	init();
});