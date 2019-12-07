$(function(){
	ShipmentInfoObj.init();
});
var ShipmentInfoObj = (function() {
	var validatorVAL;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		var originContactData = [];
		var destContactData = [];
		var currencySymbol = "VNĐ";
		$('#originFacilityId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source: facilityData, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
		$('#destinationFacilityId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source: facilityData, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
		$('#originContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: originContactData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
		$('#destinationContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: destContactData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
//		$('#shipmentMethodTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: shipmentMethodData, selectedIndex: 0, width: 300, theme: theme, 
//			renderer: function (index, label, value){
//			    var datarecord = data[index];
//			    return datarecord.firstname + " " + datarecord.lastname;
//			}
//		});
		$("#estimatedShipCost").jqxNumberInput({ width: 300, height: 25, min: 0,  spinButtons: true });
		$('#currencyUomId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, selectedIndex: 0, source: currencyUomData, theme: theme, displayMember: 'abbreviation', valueMember: 'uomId',});
		$('#currencyUomId').jqxDropDownList('val', 'VND');
		
		var now = new Date();
		var h = now.getHours(); 
    	now.setHours(h + 1);
    	now.setMinutes(0);
    	now.setSeconds(0);
		$("#estimatedShipDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
		$("#estimatedShipDate").jqxDateTimeInput('val', now);
		$("#estimatedArrivalDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: false});
		$("#estimatedArrivalDate").jqxDateTimeInput('val', now);
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#originFacilityId").on('change', function(event){
			var tmpS = $("#jqxgridInventory").jqxGrid('source');
		 	var curFacilityId = $("#originFacilityId").val();
		 	tmpS._source.url = "jqxGeneralServicer?sname=getInventoryItemAndProduct&ownerPartyId="+company+"&facilityId="+curFacilityId;
		 	$("#jqxgridInventory").jqxGrid('source', tmpS);
		 	
		 	update({
				facilityId: $("#originFacilityId").val(),
				contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		 	
		 	if ($("#destinationFacilityId").val() == $("#originFacilityId").val()){
				var currentIndex = $("#destinationFacilityId").jqxDropDownList('getSelectedIndex');
				var item = $("#destinationFacilityId").jqxDropDownList('getItem', currentIndex + 1);
				if (item){
					$("#destinationFacilityId").jqxDropDownList({selectedIndex: currentIndex + 1});
				} else {
					item = $("#destinationFacilityId").jqxDropDownList('getItem', currentIndex - 1);
					if (item){
						$("#destinationFacilityId").jqxDropDownList({selectedIndex: currentIndex - 1});
					} else {
						$("#destinationFacilityId").jqxDropDownList('clear');
					}
				}
		 	}
		 	listInvChanged = [];
		});
		$("#destinationFacilityId").on('change', function(event){
		 	update({
				facilityId: $("#destinationFacilityId").val(),
				contactMechPurposeTypeId: "SHIPPING_LOCATION",
				}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destinationContactMechId');
		 	
		 	if ($("#originFacilityId").val() == $("#destinationFacilityId").val()){
				var currentIndex = $("#originFacilityId").jqxDropDownList('getSelectedIndex');
				var item = $("#originFacilityId").jqxDropDownList('getItem', currentIndex + 1);
				if (item){
					$("#originFacilityId").jqxDropDownList({selectedIndex: currentIndex + 1});
				} else {
					item = $("#originFacilityId").jqxDropDownList('getItem', currentIndex - 1);
					if (item){
						$("#originFacilityId").jqxDropDownList({selectedIndex: currentIndex - 1});
					} else {
						$("#originFacilityId").jqxDropDownList('clear');
					}
				}
		 	}
		});
	};
	var initValidateForm = function(){
		var extendRules = [
   				{input: '#estimatedShipDate, #estimatedArrivalDate', message: uiLabelMap.CannotBeforeNow, action: 'valueChanged', 
   					rule: function(input, commit){
   						var estimatedShipDate = $('#estimatedShipDate').jqxDateTimeInput('getDate');
   		     		   	var nowDate = new Date();
   					   	if ((typeof(estimatedShipDate) != 'undefined' && estimatedShipDate != null && !(/^\s*$/.test(estimatedShipDate)))) {
   				 		    if (estimatedShipDate < nowDate) {
   				 		    	return false;
   				 		    } 
   				 		    return true;
   					   	}
   					}
   				},
   				{input: '#estimatedShipDate', message: uiLabelMap.CanNotAfterEstimatedArrivalDate, action: 'valueChanged', 
   					rule: function(input, commit){
   						var estimatedShipDate = $('#estimatedShipDate').jqxDateTimeInput('getDate');
   						var estimatedArrivalDate = $('#estimatedArrivalDate').jqxDateTimeInput('getDate');
   					   	if ((typeof(estimatedArrivalDate) != 'undefined' && estimatedArrivalDate != null && !(/^\s*$/.test(estimatedArrivalDate))) && (typeof(estimatedShipDate) != 'undefined' && estimatedShipDate != null && !(/^\s*$/.test(estimatedShipDate)))) {
   				 		    if (estimatedShipDate > estimatedArrivalDate) {
   				 		    	return false;
   				 		    }
   				 		    return true;
   					   	}
   					}
   				},
   				{input: '#estimatedArrivalDate', message: uiLabelMap.CanNotBeforeEstimatedShipDate, action: 'valueChanged', 
   					rule: function(input, commit){
   						var estimatedShipDate = $('#estimatedShipDate').jqxDateTimeInput('getDate');
   						var estimatedArrivalDate = $('#estimatedArrivalDate').jqxDateTimeInput('getDate');
   					   	if ((typeof(estimatedArrivalDate) != 'undefined' && estimatedArrivalDate != null && !(/^\s*$/.test(estimatedArrivalDate))) && (typeof(estimatedShipDate) != 'undefined' && estimatedShipDate != null && !(/^\s*$/.test(estimatedShipDate)))) {
   				 		    if (estimatedShipDate > estimatedArrivalDate) {
   				 		    	return false;
   				 		    }
   				 		    return true;
   					   	}
   					}
   				},
   				{input: '#estimatedShipCost', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'valueChanged', 
   					rule: function(input, commit){
			 		    if ($('#estimatedShipCost').val() < 0) {
			 		    	return false;
			 		    }
			 		    return true;
   					}
   				},
   				{input: '#currencyUomId', message: uiLabelMap.ValueMustBeGreateThanZero, action: 'valueChanged', 
   					rule: function(input, commit){
			 		    if ($('#currencyUomId').val() == '' || $('#currencyUomId').val() == null || $('#currencyUomId').val() == undefined) {
			 		    	return false;
			 		    }
			 		    return true;
   					}
   				},
              ];
   		var mapRules = [
   	            {input: '#originFacilityId', type: 'validInputNotNull'},
   				{input: '#destinationFacilityId', type: 'validInputNotNull'},
   				{input: '#originContactMechId', type: 'validInputNotNull'},
   				{input: '#destinationContactMechId', type: 'validInputNotNull'},
   				{input: '#estimatedShipDate', type: 'validInputNotNull'},
   				{input: '#estimatedArrivalDate', type: 'validInputNotNull'},
               ];
   		validatorVAL = new OlbValidator($('#initShipment'), mapRules, extendRules, {position: 'topcenter'});
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
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
    var getValidator = function(){
    	return validatorVAL;
    }
	return {
		init: init,
		getValidator: getValidator,
	}
	var entityMap = {
	    "&": "&amp;",
	    "<": "&lt;",
	    ">": "&gt;",
	    '"': '&quot;',
	    "'": '&#39;',
	    "/": '&#x2F;'
	 };

	 function escapeHtml(string) {
	    return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	 }
	 function unescapeHTML(escapedStr) {
	     var div = document.createElement('div');
	     div.innerHTML = escapedStr;
	     var child = div.childNodes[0];
	     return child ? child.nodeValue : '';
	 };
}());