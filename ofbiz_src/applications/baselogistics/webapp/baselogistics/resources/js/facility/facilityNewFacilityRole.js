$(function(){
	FacilityRoleObj.init();
});
var FacilityRoleObj = (function() {
	var validatorVAL;
	if (typeof listStoreKeeperParty == undefined) listStoreKeeperParty = [];
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		
		$("#fromDate").jqxDateTimeInput({width: '300px', height: '25px'});
		$("#thruDate").jqxDateTimeInput({width: '300px', height: '25px'});
		$("#thruDate").jqxDateTimeInput("clear");
		$("#fromDateManager").jqxDateTimeInput({width: '300px', height: '25px'});
		$("#thruDateManager").jqxDateTimeInput({width: '300px', height: '25px'});
		$("#thruDateManager").jqxDateTimeInput("clear");
		
		$("#storekeeperId").jqxDropDownButton({width: 300}); 
		$('#storekeeperId').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
	};
	
	var initElementComplex = function() {
		var configOwnerParty = {
				placeHolder: uiLabelMap.SearchByNameOrId,
				key: 'partyId',
				value: 'groupName',
				width: '300',
				dropDownHeight: 200,
				height: 25,
				autoDropDownHeight: false,
				displayDetail: true,
				autoComplete: true,
				useUrl: true,
				root: 'listParties',
				url: 'getFacilityOwnerables', 
				searchMode: 'containsignorecase',
				renderer : null,
				renderSelectedItem : null,
			};
			jLogCommon.initComboBox($("#ownerPartyId"), null, configOwnerParty, []);
			
			initGridPartyList();
	};
	
	var initGridPartyList = function(grid){	
		var grid = $("#managerPartyId");
		var datafield =  [
		                  { name: 'employeePartyId', type: 'string'},
		                  { name: 'employeePartyCode', type: 'string'},
		                  { name: 'fullName', type: 'string'},
		                  { name: 'emplPositionTypeId', type: 'string'},
		                  { name: 'emplPositionId', type: 'string'},
		                  { name: 'description', type: 'string'},
		                  ];
		var columnlist = [
		                  { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
		                	  groupable: false, draggable: false, resizable: false,
		                	  datafield: '', columntype: 'number', width: 50,
		                	  cellsrenderer: function (row, column, value) {
		                		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		                	  }
		                  },
		                  { text: uiLabelMap.BLEmployeeId, datafield: 'employeePartyCode', align: 'left', width: 120, pinned: true, editable: false,
		                	  cellsrenderer: function (row, column, value) {
		                		  var data = grid.jqxGrid('getrowdata', row);
		                		  if (!value){
		                			  value = data.partyId
		                			  return '<span>' + value + '</span>';
		                		  }
		                	  }
		                  },
		                  { text: uiLabelMap.BLEmployeeName, datafield: 'fullName', align: 'left', minwidth: 100, pinned: true, editable: false,},
		                  ];
		var config = {
				width: 450, 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'checkbox',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				rowdetails: false,
				useUrl: true,
				url: 'jqGetPartyAndPositionInCompany&emplPositionTypeId=LOG_STOREKEEPER',                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initEvents = function() {
		$("#managerPartyId").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (rowData){
	        	description = rowData.fullName + ' ['+rowData.employeePartyCode+']';
	        }
	        var rows = $("#managerPartyId").jqxGrid('getselectedrowindexes');
	        if (rows.length > 1){
	        	description = description + ", ... (" + rows.length + " " + uiLabelMap.BLEmployee + ")";
	        }
	        
	        var check = false;
	        for (var x in listStoreKeeperParty){
				if (rowData.employeePartyId == listStoreKeeperParty[x].partyId){
					check = true; 
					break;
				}
			}
	        if (!check){
	        	var obj = {
	        			partyId: rowData.employeePartyId,
	        	}
	        	listStoreKeeperParty.push(obj);
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#storekeeperId').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#managerPartyId").on('rowunselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			var rows = $("#managerPartyId").jqxGrid('getselectedrowindexes');
			var description = uiLabelMap.PleaseSelectTitle; 
			if (rows.length == 1) {
				description = rowData.fullName + ' ['+rowData.employeePartyCode+']';
			} else if (rows.length > 1) {
				description = rowData.fullName + ' ['+rowData.employeePartyCode+'], ... (' + rows.length + ' ' + uiLabelMap.BLEmployee +')';
			}
			for (var x in listStoreKeeperParty){
				if (rowData.employeePartyId == listStoreKeeperParty[x].partyId){
					listStoreKeeperParty.splice(0,1);
				}
			}
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#storekeeperId').jqxDropDownButton('setContent', dropDownContent);
	    });
	};
	var initValidateForm = function(){
		var extendRules = [
			{
			    input: '#thruDateManager', message: uiLabelMap.MustBeAfterManageFromDate, action: 'keyup, blur', rule: function (input, commit) {
			        var fromDate = $('#fromDateManager').jqxDateTimeInput('value');
			        var thruDate = $('#thruDateManager').jqxDateTimeInput('value');
			        if(thruDate == null || thruDate== undefined || fromDate == null || fromDate== undefined){
			     	   return true;
			        }
			        if(fromDate > thruDate){
			     	   return false;
			        }
			        return true;
			    }
			},
			{
			    input: '#thruDate', message: uiLabelMap.MustBeAfterOwnerFromDate, action: 'keyup, blur', rule: function (input, commit) {
			        var fromDate = $('#fromDate').jqxDateTimeInput('value');
			        var thruDate = $('#thruDate').jqxDateTimeInput('value');
			        if(thruDate == null || thruDate== undefined || fromDate == null || fromDate== undefined){
			     	   return true;
			        }
			        if(fromDate > thruDate){
			     	   return false;
			        }
			        return true;
			    }
			},
          ];
   		var mapRules = [
				{input: '#ownerPartyId', type: 'validInputNotNull'},
           ];
   		validatorVAL = new OlbValidator($('#initFacilityRole'), mapRules, extendRules, {position: 'right'});
	};
	var getValidator = function(){
		return validatorVAL;
	}
	return {
		init: init,
		getValidator: getValidator,
	}
}());