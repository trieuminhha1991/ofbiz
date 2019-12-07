var configPartyWorkingShifObject = (function(){
	var init = function(){
		initJqxWindow();
		initJqxTreeGrid();
	};
	
	var initJqxTreeGrid = function(){
		var dataAdapter = createTreeGridSource();
		var columns = createTreeGridColumn();
		$("#configPartyShiftTreeGrid").jqxTreeGrid({
			width: 780,
			height: 440,
			icons: true,
			source: dataAdapter,
	        sortable: true,
	        editable: false,
	        columns: columns,
	        columnsResize: true,
	        theme: 'olbius',        
	        showToolbar: false,
	        renderToolbar: function(toolBar){
	        	var container = $("<div id='toolbarcontainer' class='widget-header'><h4>" + uiLabelMap.OrganizationUnit + "</h4></div>");
	        	toolBar.append(container);
	        }
		});
	};
	
	var initJqxWindow = function(){
		$("#configPartyWorkingShiftWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
			width: 850, height: 510, isModal: true, theme:'olbius',
			initContent: function(){
				
			}	
		});		
	};
	
	var createTreeGridSource = function (){
		var partyGroupsSource = {
				dataType: "json",
				dataFields: [
	               {name: 'partyId', type: 'string' },
	               {name: 'partyCode', type: 'string' },
	               {name: 'partyIdFrom', type: 'string' },
	               {name: 'partyName', type: 'string' },
	               {name: 'workingShiftId', type: 'string'},                   
	               {name: 'workingShiftName', type: 'string'},                   
	               {name: 'expanded',type: 'bool'},
	           ],
	           hierarchy:
	           {
	               keyDataField: { name: 'partyId' },
	               parentDataField: { name: 'partyIdFrom' }
	           },
	           id: 'partyId',
	           url: "getPartyWorkingShift",
	           root: 'listReturn'
		};
		var dataAdapter = new $.jqx.dataAdapter(partyGroupsSource);
		return dataAdapter;
	};
	
	var createTreeGridColumn = function(){
		var columns = [
				{text: uiLabelMap.OrgUnitName, datafield: 'partyName', width: 400, editable: false},			
				{text: uiLabelMap.OrgUnitId, datafield: 'partyCode', width: 180, editable: false},
				{text: uiLabelMap.HrCommonWorkingShift, datafield: 'workingShiftName', editable: false, columnType: "template"},
				{datafield: 'workingShiftId', hidden: true}
		]
		return columns; 
	};
	
	return{
		init: init,
	}
}());

$(document).ready(function(){
	configPartyWorkingShifObject.init();
});

function configPartyWorkingShift(){
	openJqxWindow($("#configPartyWorkingShiftWindow"));
}