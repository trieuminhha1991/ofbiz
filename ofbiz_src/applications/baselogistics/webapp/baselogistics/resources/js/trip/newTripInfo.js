$(function(){
	TripInfo.init();
});
var TripInfo = (function() {
	var validatorVAL;

	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#estimatedTimeStart").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString : 'dd/MM/yyyy HH:mm'});
		$("#estimatedTimeStart").jqxDateTimeInput('clear');
		$("#estimatedTimeEnd").jqxDateTimeInput({width: 300, theme: theme, height: '24px', formatString : 'dd/MM/yyyy HH:mm'});
		$("#estimatedTimeEnd").jqxDateTimeInput('clear');
		$("#shipperPartyId").jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, theme: theme, source: shipperPartyData, valueMember:'partyId', displayMember:'description', height: '24px', dropDownHeight: 200});
		$("#tripCost").jqxNumberInput({ width: 295, height: 25, spinButtons: true });
		$("#costCustomerPaid").jqxNumberInput({ width: 295, height: 25, spinButtons: true });
		$("#description").jqxInput({ width: 293});
		var tempFrom;
		var tempThru;
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$("#shipperPartyId").on("change", function(event) {
			var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
			var shipperPartyId = $("#shipperPartyId").jqxDropDownList('val');
			var packTypeId = "PACK_CREATED";
			tmpS._source.url = "jqxGeneralServicer?sname=JQGetListPackByShipper&packTypeId="+packTypeId+"&shipperPartyId="+shipperPartyId;
			$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
		});
		$("#jqxgridfilterGrid").on("rowunselect", function(event){	
		});

		//add row when the user clicks the 'Save' button.
	    $("#addSaveButton").click(function () {
	    	var row;
	    	var selectedIndexs = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
	    	if(selectedIndexs.length == 0 || selectedIndexs == undefined){
	    		bootbox.dialog(uiLabelMap.DAYouNotYetChooseProduct, [{
	                "label" : "OK",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                }]
	            );
	            return false;
	    	} else{
				// bootbox.dialog(uiLabelMap.AreYouSureCreate,
				// [{"label": uiLabelMap.CommonCancel,
				// 	"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    //         "callback": function() {bootbox.hideAll();}
		    //     },
		    //     {"label": uiLabelMap.OK,
		    //         "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    //         "callback": function() {
		    //         	var listShipmentItems = new Array();
		    //         	var listShipments = new Array();
			  //   		for(var i = 0; i < selectedIndexs.length; i++){
			  //   			var childIndexs = $('#jqxgridDetail'+ selectedIndexs[i]).jqxGrid('getselectedrowindexes');
			  //   			for(var j = 0; j < childIndexs.length; j ++){
			  //   				var data = $('#jqxgridDetail'+ selectedIndexs[i]).jqxGrid('getrowdata', childIndexs[j]);
			  //   				var map = {};
				//     			map['shipmentId'] = data.shipmentId;
				//     			map['shipmentItemSeqId'] = data.shipmentItemSeqId;
				//     			map['quantity'] = data.quantityCreate;
				//     			map['quantityUomId'] = data.quantityUomId;
				//     			listShipmentItems.push(map);
			  //   			}
			  //   		}
			  //   		listShipmentItems = JSON.stringify(listShipmentItems);
			  //           row = {
			  //           		weightUomId:$('#defaultWeightUomId').val(),
			  //           		weight:totalWeight,
			  //           		delivererPartyId:$('#delivererPartyId').val(),
			  //           		fromDate: new Date($('#estimatedTimeStart').jqxDateTimeInput('getDate')),
			  //           		thruDate: new Date($('#estimatedTimeEnd').jqxDateTimeInput('getDate')),
			  //           		listShipmentItems:listShipmentItems
			  //           	  };
			  //   	    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		    //         }
				// }]);
	    	}
	    });
	};

	var initValidateForm = function(){
		var extendRules = [
			{
				input: '#estimatedTimeEnd',
			    message: uiLabelMap.EndDateMustBeAfterStartDate,
			    action: 'keyup, blur',
			    position: 'topcenter',
			    rule: function (input) {
			    	var fromDateTmp = $('#estimatedTimeStart').jqxDateTimeInput('getDate');
			    	var thruDateTmp = $('#estimatedTimeEnd').jqxDateTimeInput('getDate');
				   	if ((typeof(fromDateTmp) != 'undefined' && fromDateTmp != null && !(/^\s*$/.test(fromDateTmp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
			 		    if (fromDateTmp > thruDateTmp) {
			 		    	return false;
			 		    }
				   	}
				   	return true;
			    }
			},
			{
				input: '#estimatedTimeEnd',
			    message: uiLabelMap.CannotBeforeNow,
			    action: 'keyup, blur',
			    position: 'topcenter',
			    rule: function (input) {
			    	var temp = new Date();
			    	var thruDateTmp = $('#estimatedTimeEnd').jqxDateTimeInput('getDate');
				   	if ((typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
			 		    if (thruDateTmp < temp ) {
			 		    	return false;
			 		    }
				   	}
				   	return true;
			    }
			},
			// {
			// 	input: '#estimatedTimeEnd',
			//     message: uiLabelMap.CannotAfterNow,
			//     action: 'keyup, blur',
			//     position: 'topcenter',
			//     rule: function (input) {
			//     	var temp = new Date();
			//     	var thruDateTmp = $('#estimatedTimeEnd').jqxDateTimeInput('getDate');
			// 	   	if ((typeof(temp) != 'undefined' && temp != null && !(/^\s*$/.test(temp))) && (typeof(thruDateTmp) != 'undefined' && thruDateTmp != null && !(/^\s*$/.test(thruDateTmp)))) {
			//  		    if (thruDateTmp > temp && curPackStatusId == 'PACK_CREATED') {
			//  		    	return false;
			//  		    }
			// 	   	}
			// 	   	return true;
			//     }
			// },
			{
				input: '#tripCost',
					message: uiLabelMap.CostRequired,
					action: 'keyup, blur',
					position: 'topcenter',
					rule: function (input) {
						var tripCost = $('#tripCost').val;
						if (tripCost < 0) {
							return false;
						}
				   	return true;
			    }
			},
			{
				input: '#costCustomerPaid',
					message: uiLabelMap.CostRequired,
					action: 'keyup, blur',
					position: 'topcenter',
					rule: function (input) {
						var costCustomerPaid = $('#costCustomerPaid').val;
						if (costCustomerPaid < 0) {
							return false;
						}
				   	return true;
			    }
			}
          ];
   		var mapRules = [
				 {input: '#shipperPartyId', type: 'validInputNotNull'},
   			 {input: '#tripCost', type: 'validInputNotNull'},
				 {input: '#costCustomerPaid', type: 'validInputNotNull'},
				 {input: '#estimatedTimeStart', type: 'validInputNotNull'},
				 {input: '#estimatedTimeEnd', type: 'validInputNotNull'},
          ];
   		validatorVAL = new OlbValidator($('#initTrip'), mapRules, extendRules, {position: 'topcenter'});
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
    var getValidator = function(){
    	return validatorVAL;
    }
	return {
		init: init,
		getValidator: getValidator,
	}
}());
