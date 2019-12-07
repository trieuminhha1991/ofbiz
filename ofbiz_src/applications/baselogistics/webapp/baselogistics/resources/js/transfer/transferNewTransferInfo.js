$(function(){
	TransferInfoObj.init();
});
var TransferInfoObj = (function() {
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
		$("#originFacility").jqxDropDownButton({width: 300}); 
		$("#destFacility").jqxDropDownButton({width: 300}); 
		$('#originFacility').jqxDropDownButton('setContent', '<div class="green-label button-label">'+uiLabelMap.PleaseSelectTitle+'</div>');
		$('#destFacility').jqxDropDownButton('setContent', '<div class="green-label button-label">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$('#transferTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: transferTypeData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'transferTypeId'});
		transferTypeId = $('#transferTypeId').jqxDropDownList('val');
		$('#shipmentMethodTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: shipmentMethodData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'shipmentMethodTypeId'});
		var partyTmpData = [];
		$('#carrierPartyId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: partyTmpData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'partyId'});
		$('#originContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: originContactData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
		$('#destContactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: destContactData, selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
		$("#shipBeforeDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm', disabled: false});
		$("#shipAfterDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm', disabled: false});
		$("#shipBeforeDate").jqxDateTimeInput('clear');
		$("#shipAfterDate").jqxDateTimeInput('clear');
		$("#description").jqxInput({ width: 300, height: 65});
		
		if ($("#shipmentMethodTypeId").length > 0){
			$("#shipmentMethodTypeId").val("GROUND_HOME");
			update({
				shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
				}, 'getPartyCarrierByShipmentMethodAndStore' , 'listParties', 'partyId', 'fullName', 'carrierPartyId');
		}
		
		$('#carrierPartyId').jqxDropDownList('val', 'DLOG');
	};
	
	var initFacilityGrid = function(grid, url){
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
      	];
      	var columnlist = [
				{text: uiLabelMap.BLFacilityId, datafield: 'facilityCode', width: '20%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = grid.jqxGrid('getrowdata', row);
							value = data.facilityId;
						}
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.FacilityName, datafield: 'facilityName', width: '80%',
					cellsrenderer: function (row, column, value) {
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
      	];
      	
      	var config = {
  			width: 500, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initElementComplex = function() {
		var url = "";
		if (transferTypeId){
			if ("TRANS_INTERNAL" == transferTypeId){
				url = "jqGetFacilities&facilityGroupId=FACILITY_INTERNAL";
				initFacilityGrid($("#jqxGridOriginFacility"), url);
				initFacilityGrid($("#jqxGridDestFacility"), url);
			} else if ("TRANS_DISTRIBUTOR" == transferTypeId) {
				url = "jqGetFacilities&facilityGroupId=FACILITY_INTERNAL";
				initFacilityGrid($("#jqxGridOriginFacility"), url);
				url = "jqGetFacilities&facilityGroupId=FACILITY_CONSIGN";
				initFacilityGrid($("#jqxGridDestFacility"), url);
			} else {
				url = "jqGetFacilities";
				initFacilityGrid($("#jqxGridOriginFacility"), url);
				initFacilityGrid($("#jqxGridDestFacility"), url);
			}
		}
	};
	var initEvents = function() {
		
		$("#jqxGridOriginFacility").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        originFacility = $.extend({}, rowData);
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (rowData) {
	        	if (rowData.facilityCode) {
	        		description = "[" + rowData.facilityCode + "] " + rowData.facilityName;
	        	} else {
	        		description = "[" + rowData.facilityId + "] " + rowData.facilityName;
	        	}
	        } 
	        var dropDownContent = '<div class="green-label button-label">'+ description +' </div>';
	        $('#originFacility').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$('#jqxGridOriginFacility').on('rowdoubleclick', function (event) { 
			$('#originFacility').jqxDropDownButton('close');
		});
		
		$("#jqxGridOriginFacility").on('bindingcomplete', function (event) {
			if (originFacility != null){
				var rows = $('#jqxGridOriginFacility').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == originFacility.facilityId){
							var index = $('#jqxGridOriginFacility').jqxGrid('getrowboundindexbyid', data1.uid);
							$('#jqxGridOriginFacility').jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		$('#originFacility').on('close', function (event) { 
			updateProductGridData();
			if (originFacility){
				update({
					facilityId: originFacility.facilityId,
					contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
			}
		});
		
		
		$("#jqxGridDestFacility").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        destFacility = $.extend({}, rowData);
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (rowData) {
	        	if (rowData.facilityCode) {
	        		description = "[" + rowData.facilityCode + "] " + rowData.facilityName;
	        	} else {
	        		description = "[" + rowData.facilityId + "] " + rowData.facilityName;
	        	}
	        } 
	        var dropDownContent = '<div class="green-label button-label">'+ description +' </div>';
	        $('#destFacility').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$('#jqxGridDestFacility').on('rowdoubleclick', function (event) { 
			$('#destFacility').jqxDropDownButton('close');
		});
		
		$("#jqxGridDestFacility").on('bindingcomplete', function (event) {
			if (originFacility != null){
				var rows = $('#jqxGridDestFacility').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == originFacility.facilityId){
							var index = $('#jqxGridDestFacility').jqxGrid('getrowboundindexbyid', data1.uid);
							$('#jqxGridDestFacility').jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		$('#destFacility').on('close', function (event) { 
			if (destFacility){
				update({
					facilityId: destFacility.facilityId,
					contactMechPurposeTypeId: "SHIPPING_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
			}
		});
		
		$("#shipmentMethodTypeId").on('change', function(event){
			update({
				shipmentMethodTypeId: $("#shipmentMethodTypeId").val(),
				}, 'getPartyCarrierByShipmentMethodAndStore' , 'listParties', 'partyId', 'fullName', 'carrierPartyId');
		});
		
		$("#transferTypeId").on('change', function(event){
			transferTypeId = $('#transferTypeId').jqxDropDownList('val');
			url = "jqxGeneralServicer?sname=jqGetFacilities";
			if (transferTypeId){
				if ("TRANS_INTERNAL" == transferTypeId){
					url = "jqxGeneralServicer?sname=jqGetFacilities&facilityGroupId=FACILITY_INTERNAL";
					updateFacilityGridData($("#jqxGridOriginFacility"), url);
					updateFacilityGridData($("#jqxGridDestFacility"), url);
				} else if ("TRANS_DISTRIBUTOR" == transferTypeId) {
					updateFacilityGridData($("#jqxGridOriginFacility"), url);
					url = "jqxGeneralServicer?sname=jqGetFacilities&facilityGroupId=FACILITY_CONSIGN";
					updateFacilityGridData($("#jqxGridDestFacility"), url);
				} else {
					updateFacilityGridData($("#jqxGridOriginFacility"), url);
					updateFacilityGridData($("#jqxGridDestFacility"), url);
				}
			}
		});
		
	};
	var initValidateForm = function(){
		var extendRules = [
		       {input: '#shipAfterDate', message: uiLabelMap.CannotBeforeNow , action: 'change', position: 'topcenter',
					rule: function(input, commit){
						var value = $('#shipAfterDate').jqxDateTimeInput('getDate');
						var nowDate = new Date();
						if(value < nowDate){
							return false;
						}
						return true;
					}
		       },
		       {input: '#shipAfterDate', message: uiLabelMap.BLTimeDistanceNotValid , action: 'change', position: 'topcenter',
					rule: function(input, commit){
						var value1 = $('#shipAfterDate').jqxDateTimeInput('getDate');
						var value2 = $('#shipBeforeDate').jqxDateTimeInput('getDate');
						if(value2 && value1 && value2 < value1){
							return false;
						}
						return true;
					}
		       },
		       {input: '#shipBeforeDate', message: uiLabelMap.CannotBeforeNow , action: 'change', position: 'topcenter',
					rule: function(input, commit){
						var value = $('#shipBeforeDate').jqxDateTimeInput('getDate');
						var nowDate = new Date();
						if(value < nowDate){
							return false;
						}
						return true;
					}
		       },
               {input: '#originFacility', message: uiLabelMap.FieldRequired, action: 'close', position: 'right',
					rule: function(input, commit){
						if (!originFacility){
					   		return false;
						}
						return true;
					}
				},
				{input: '#destFacility', message: uiLabelMap.FieldRequired, action: 'close', position: 'right',
					rule: function(input, commit){
						if (!destFacility){
							return false;
						}
						return true;
					}
				},
				{input: '#destFacility', message: uiLabelMap.BLCannotTransferSameFacility, action: 'close', position: 'right',
					rule: function(input, commit){
						if (destFacility && originFacility && originFacility.facilityId === destFacility.facilityId){
							return false;
						}
						return true;
					}
				},
              ];
   		var mapRules = [
   				{input: '#shipBeforeDate', type: 'validInputNotNull', action: 'valueChanged'},
   				{input: '#shipAfterDate', type: 'validInputNotNull', action: 'valueChanged'},
   				{input: '#originContactMechId', type: 'validObjectNotNull', objType: 'dropDownList' },
				{input: '#destContactMechId', type: 'validObjectNotNull', objType: 'dropDownList'},
   				{input: '#transferTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
   				{input: '#shipmentMethodTypeId', type: 'validObjectNotNull', objType: 'dropDownList', message: uiLabelMap.PleaseConfigShipmentMethodForSalesChannel, position: 'right',},
   				{input: '#carrierPartyId', type: 'validObjectNotNull', objType: 'dropDownList'},
               ];
   		validatorVAL = new OlbValidator($('#initTransfer'), mapRules, extendRules, {position: 'right'});
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
    };
    
    function updateProductGridData(){
    	var element = $("#jqxGridProduct");
		var facId = null;
		if (originFacility){
			facId = originFacility.facilityId;
			var tmpS = element.jqxGrid('source');
			if (tmpS){
				var curUrl = tmpS._source.url;
				var newUrl = "jqxGeneralServicer?sname=JQGetListProductByOrganiztion&inventoryInfo=Y&facilityId="+facId;
				if (newUrl != curUrl){
					tmpS._source.url = newUrl;
					element.jqxGrid('source', tmpS);
				}	
			}
		} 
    }
    
    function updateFacilityGridData(element, newUrl){
		var tmpS = element.jqxGrid('source');
		if (tmpS){
			var curUrl = tmpS._source.url;
			if (newUrl != curUrl){
				tmpS._source.url = newUrl;
				element.jqxGrid('source', tmpS);
			}	
		}
    }
    
	return {
		init: init,
		getValidator: getValidator,
	}
}());