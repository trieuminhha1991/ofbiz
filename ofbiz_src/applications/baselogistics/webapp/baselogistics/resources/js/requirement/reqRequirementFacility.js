$(function() {  
	OlbReqFa.init();
});

var OlbReqFa = (function() {
	var grid = $('#gridFacility');
	var validatorVAL;
	var init = function() {
		initInput();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	
	var initInput = function() { 
		$("#facility").jqxDropDownButton({width: 300, theme: theme}); 
		$('#facility').jqxDropDownButton('setContent', '<div class="green-label button-label">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#selectFacilityWindow").jqxWindow({
			maxWidth: 800, minWidth: 300, width: 600, height: 220, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#faCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		$('#contactMechId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, source: [], selectedIndex: 0, width: 300, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
	}
	
	var initElementComplex = function() {
		initGridFacility(grid);
	}
	
	var initEvents = function (){
		grid.on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        if (rowData){
	        	facilitySelected = $.extend({}, rowData);
		        var description = uiLabelMap.PleaseSelectTitle; 
		        if (facilitySelected) {
		        	if (facilitySelected.facilityCode != null){
		        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
		        	} else {
		        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
		        	}
		        }
		        update({
					facilityId: facilitySelected.facilityId,
					contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
					}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'contactMechId');
		        
		        var dropDownContent = '<div class="green-label button-label">'+ description +' </div>';
		        $('#facility').jqxDropDownButton('setContent', dropDownContent);
	        } else {
	        	setTimeout(function(){
	        		grid.jqxGrid('clearselection');
	        	}, 100);
	        }
	    });
		
		grid.on('rowdoubleclick', function (event) { 
			$('#facility').jqxDropDownButton('close');
		});
		
		grid.on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = grid.jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == facilitySelected.facilityId){
							var index = grid.jqxGrid('getrowboundindexbyid', data1.uid);
							grid.jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		$("#faSave").on('click', function (event) { 
			if (!validatorVAL.validate()){
				return false;
			}
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureSave, function() {
				Loading.show('loadingMacro');
            	setTimeout(function(){
					$.ajax({
						url: "updateFacilityToRequirement",
				        type: "POST",
				        async: false,
				        data: {
				        	requirementId: requirementId,
				        	facilityId: facilitySelected.facilityId,
				        	contactMechId: $("#contactMechId").val(),
				        },
				        success: function(res) {
				        	if (ReqDetailObj != undefined){
				        		ReqDetailObj.prepareShipmentFromRequirement(requirementId);
				        	} else {
				        		window.location.reload();
				        	}
				        },
					});
				}); 
            }, uiLabelMap.CommonCancel, uiLabelMap.OK, function(){
            });
		});
	}
	
	var initValidateForm = function(){
		var extendRules = [
                {input: '#facility', message: uiLabelMap.FieldRequired, action: 'valueChanged', position: 'right',
					rule: function(input, commit){
						if (!facilitySelected){
					   		return false;
						}
						return true;
					}
				},
              ];
   		var mapRules = [
   				{input: '#contactMechId', type: 'validObjectNotNull', objType: 'dropDownList' },
               ];
   		validatorVAL = new OlbValidator($('#selectFacilityWindow'), mapRules, extendRules, {position: 'right'});
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
    
	var initGridFacility = function(grid){
		var url = "jqGetFacilities&facilityGroupId=FACILITY_INTERNAL";
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
				{text: uiLabelMap.BLFacilityName, datafield: 'facilityName', width: '80%',
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
	
	var open = function (){
		$("#selectFacilityWindow").jqxWindow('open');
	}
	return {
		init : init,
		open: open,
	}
}());