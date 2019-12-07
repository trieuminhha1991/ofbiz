function setNotification(message) {
	if(bootbox){
		bootbox.alert(message);	
	}else{
		alert(message);
	}
}
String.prototype.replaceAll = function(str1, str2, ignore) {
    return this.replace(new RegExp(str1.replace(/([\/\,\!\\\^\$\{\}\[\]\(\)\.\*\+\?\|\<\>\-\&])/g,"\\$&"),(ignore?"gi":"g")),(typeof(str2)=="string")?str2.replace(/\$/g,"$$$$"):str2);
};
function hint(obj){
	obj.addClass("alert-error");
	setTimeout(function(){
		obj.removeClass("alert-error");	
	}, 2000);
}
function renderSelection(source, select){
	select.jqxDropDownList({
   		theme: 'olbius',
		source: source,
    	displayMember: "supName",
    	selectedIndex: 0,
    	scrollBarSize: 5,
	    height: 27,
	    width: 270,
	    dropDownWidth: 270,
	    renderer: function (index, label, value) {
	    	var obj = source[index];
	    	var table = "";
	    	if(obj){
	    		table = "<div class='sup-select-row'><p class='sup-select-content'><i class='fa-user'></i>&nbsp;<b>" + obj.supName + "</b></p>"
    				+  "<p class='sup-select-content'><i class='fa-bus'></i>&nbsp;" + obj.disName + "</p>"
    				+  "<p class='sup-select-content'><i class='fa-home'></i>&nbsp;" + obj.address + "</p></div>";
	    	}
	        return table;
	    }
		
	});	
}
/*cost type is chosen*/
// var costChosen = [];
(function() {
	var date = {};
	function initDate() {
		var startDate = info && info.fromDate ? Utils.formatDateDMY(info.fromDate.split(" ").shift()) : "";
		var currentDate = startDate ? startDate : Utils.getCurrentDate();
		var endDate = info && info.thruDate ? Utils.formatDateDMY(info.thruDate.split(" ").shift()) : "";
		var obj = $('#fromDate');
		var dd = {
			format : "DD-MM-YYYY",
			minDate : currentDate,
			startDate : currentDate
		};
		if(endDate){
			dd.endDate = endDate;
		}
		date = {
			fromDate : currentDate,
			thruDate : endDate
		};
		obj.daterangepicker(dd, function(start, end, label) {
			date = {
				fromDate : start.format("YYYY-MM-DD"),
				thruDate : end.format("YYYY-MM-DD")
			};
		});
		if(endDate){
			obj.val(currentDate + " - " + endDate);	
		}
	}

	function initMarketing(marketing) {
		var info = marketing.info;
		var products = marketing.products;
		var costs = marketing.costs;
		console.log(info);
	}

	/*add costs category*/
	function submitForm() {		
		var message = uiLabelMap && uiLabelMap.confirmCreateMessage ? uiLabelMap.confirmCreateMessage : "Confirm create?" ;
		bootbox.confirm(message, function() {
			var action = "createSamplingCampaign";
			var costList = JSON.stringify(getCostList());
			var products = JSON.stringify(getProductList());
			var addresses = JSON.stringify(getAddressData());
			var data = {
				fromDate : date.fromDate,
				thruDate : date.thruDate,
				people : $("#people").val(),
				marketingPlace : $("#place").val(),
				estimatedCost : $("#estimatedCost").val(),
				isActive : $("#isActive").val(),
				campaignName : $("#campaignName").val(),
				campaignSummary : $("#campaignSummary").val(),
				products : products,
				costList : costList,
				places: addresses
			};
			if (info && info.marketingCampaignId) {
				action = "updateSamplingCampaign";
				data['marketingCampaignId'] = info.marketingCampaignId;
			}
			// return;/
			$.ajax({
				url : action,
				type : "POST",
				data : data,
				dataType : "json",
				success : function(res) {
					if (res.message && res.message == "success") {
						var su = uiLabelMap && uiLabelMap.sendRequestSuccess ? uiLabelMap.sendRequestSuccess : "success";
						setNotification(su);
					} else {
						var su = uiLabelMap && uiLabelMap.sendRequestError ? uiLabelMap.sendRequestError : "error";
						setNotification(su);
					}
				}
			});
		});
	}

	function resetForm() {
		window.location.reload();
	}
	function getDistrict(geoId){
		$("#addAddress").addClass("disabled");
		$.ajax({
			url : "getDistrictByProvince",
			type: "POST",
			data: {
				geoId : geoId
			}, 
			success: function(res){
				if(res && res.results){
					$("#addAddress").removeClass("disabled");
					renderDistrict(res.results);	
				}
			}
		});
	}
	function renderDistrict(data){
		var district = $("#district");
		var str = "";
		for(var x in data){
			str += "<option value='"+ data[x].geoId +"'>"
				+ data[x].geoName 
				+ "</option>";
		}
		district.html(str);
		district.val('').trigger("liszt:updated");
	}
	function initProvince(){
		var province = $("#chooseProvince");
		if($("#chooseProvince option[value='VN-HN']")){
			province.val("VN-HN");
		}else{
			province.val("");
		}
		
		province.chosen().change(function(e){
			var geoId = $(this).val();
			getDistrict(geoId);
		});
		$("#district").chosen();
		setTimeout(function(){
			var val = province.val();
			getDistrict(val);
		}, 100);
	}
	/*init address data from database*/
	function initAddress(data){
		var current = "";
		for(var x in data){
			var obj = data[x];
			if(!current || current != obj.countyGeoId){
				current = obj.countyGeoId;
				addPlace({
					id: obj.countyGeoId,
					name: obj.geoName,
					value: obj.address1 
				});	
			}else{
				addAddressRow(obj.countyGeoId, obj.address1);
			}
		}
	}
	/*add places sampling to each district*/
	function addPlace(type) {
		var form = $("#address-form");
		var id = type.id;
		var current = $(".address-row");
		for (var x = 0; x < current.length; x++) {
			var obj = $(current[x]).data("id");
			if (obj == id) {
				hint($("address-row"+id));
				return;
			}
		}
		if (!addRowLabel) {
			var addRowLabel = "Add new";
		}
		renderPlaceContainer(form, id, type.name);
		addAddressRow(id, type.value);
	}
	/*render place container for each district*/
	function renderPlaceContainer(form, id, name){
		// console.log(id);
		showLoading("loading1");
		var str = "<div class='row-al  paddingtop-10 address-row row-mk' id='address-row"+id+"' data-id='"+id+"'>"
				+"<div class='col-al3 aligncenter'><div id='title"+id+"' class='title-inner'>"+name+"</div></div><div class='col-al9'><div class='address-container' id='address-"+id+"'data-id='"+id+"'></div>"
				// +"<button class='add-new-row paddingtop-10' id='addPlace" + id + "'>+</button>"
				+"</div>";
		$.ajax({
			url:"getSupByDistrict",
			type: "POST",
			data :{
				geoId: id
			},
			success: function(res){
				console.log(hideLoading);
				if(res.results){
					hideLoading("loading1");
					sessionStorage.setItem("sup"+id, JSON.stringify(res.results));
				}
			}
		});
		form.append(str);
		$("#addPlace" + id).click(function() {
			addAddressRow(id);
		});
		return str;
	}
	/*add new product row to product list*/
	function addAddressRow(id, obj) {
		var par = $("#address-" + id);
		var len = par.children().length;
		if (!len) {
			len = 0;
		}
		var end = len + 1;
		var res = getAddressRow(id, end, obj);
		par.append(res.html);
		$("#remove-" + id + end).click(function() {
			removeRow($(this).parent());
		});
		reloadHeight(id);
		renderSelection(res.sup, $("#jqxSup-"+id+end));
		var input = $("#"+id+end).find("input.addressInput");
		initHotKey(input);
		bindChange(input);
	}
	/*bind address change*/
	function bindChange(obj){
		obj.focus(function(){
			obj.attr("data-old", obj.val());
		});
		obj.on("keydown", function(){
			var value = $(this).val();
			console.log(obj.data("old"));
			var option = $("select.addressChosen option");
			for(var x = 0; x < option.length; x++){
				var tmp = $(option[x]).text();
			}
		});
	}
	/*get address row value
	 render sup value + input address*/
	function getAddressRow(id, seq, obj) {
		var des = obj ? obj : "";
		var sup = $.parseJSON(sessionStorage.getItem("sup"+id));
		var str = "<div class='row-al address-content-row' id='"+id+seq+"'>"
				+ "<div class='col-al7 borderleft'><input class='fullwidth addressInput' type='text' name='address' value='"+des+"'/></div>"
				+ "<div class='col-al5 borderleft paddingtop-5'>"
				+ "<div id='jqxSup-"+id+seq+"' class='jqxsup'></div>"
				+ "</div>"
				+ "<div class='remove-row' id='remove-" + id + seq + "'>x</div>"
				+ "</div>";
		var arr = new Array();
		for(var x in sup){
			arr.push({
				partyId: sup[x].partyId,
				supName: sup[x].groupName,
				disName: sup[x].groupNameTo,
				address: sup[x].address1,
				county: sup[x].geoNameCounty,
				city: sup[x].geoNameProvince,
				geoId: sup[x].countyGeoId
			});
		}
			
		return {
			sup : arr,
			html: str
		};
	}
	/*init hot key quick insert remove row*/
	function initHotKey(obj){
		var par = obj.parents(".address-container");
		var id = par.data("id");
		obj.bind("keydown","Shift+return", function(e){
			e.preventDefault();
			addAddressRow(id);
		});
		obj.bind("keydown","ctrl+d", function(e){
			e.preventDefault();
			removeRow(obj.parents(".address-content-row"), id);
		});
	}
	
	function removeRow(obj, id) {
		if (obj) {
			var par = $("#address-"+id);
			var gr = $("#address-row"+id);
			if(par.children().length > 1){
				obj.remove();
				reloadHeight(id);	
			}else{
				gr.remove();
			}
			
		}
	}
	function reloadHeight(id){
		var height = $("#address-" + id).height();
		// console.log(height);
		var title = $("#title" + id);
		title.height(height);
	}
	function getAddressData(){
		var addr = $(".address-content-row");
		var res = new Array();
		if(addr.length){
			for(var x = 0; x < addr.length; x++){
				var obj = $(addr[x]);
				var adinput = obj.find("input[name='address']");
				var supSl = obj.find(".jqxsup");
				var item = supSl.jqxDropDownList('getSelectedItem');
				console.log(supSl.length);
				var party = item.originalItem;
				var data = {
					address: adinput.val(),
					sup: party.partyId,
					geoId: party.geoId
				};
				res.push(data);
			}
		}
		return res;
	}
	
	$(document).ready(function() {
		$("#formTab").tabs();
		$("#submit").click(submitForm);
		$("#reset").click(resetForm);
		initDate();
		initProvince();
		if(places && places.length){
			initAddress(places);	
		}		
		$('.dropdown-toggle').dropdown();
		$("#addAddress").click(function(){
			if($(this).hasClass("disabled")){
				return;
			}
			var obj = $("#district").val();
			var select = $("#district option:selected").text(); 
			if(obj && select){
				addPlace({
					id: obj,
					name: select
				});
			}
		});
	});
})();
		