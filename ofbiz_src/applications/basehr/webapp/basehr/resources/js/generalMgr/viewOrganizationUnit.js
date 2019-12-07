var organizationObject = (function(){
	var init = function(){
		initJqxTreeGrid();
	};
	
	var initJqxTreeGrid = function(){
		var source = getTreeGridSource();
		var columns = getTreeGridColumn();
		$("#treePartyGroupGrid").jqxTreeGrid({
			width: "100%",
			icons: true,
			source: source,
	        sortable: true,
	        columns: columns,
	        theme: 'olbius',
	        columnsResize: true,
	        showToolbar: false,
	        renderToolbar: function(toolBar){
	        	var container = $("<div id='toolbarcontainer' class='widget-header'><h4>" + uiLabelMap.OrganizationUnit + "</h4></div>");
	        	toolBar.append(container);
	        }
		});
	};
	
	var getTreeGridSource = function(){
		var partyGroupsSource = {
				dataType: "json",
				dataFields: [
	               { name: 'partyId', type: 'string' },
	               { name: 'partyCode', type: 'string' },
	               { name: 'partyIdFrom', type: 'string' },
	               { name: 'partyName', type: 'string' },
	               { name: 'postalAddress', type: 'string'},
	               { name: 'contactMechId', type: 'string'},
	               { name: 'totalEmployee', type: 'number' },                   
	               { name: 'comments', type: 'string' },                   
	               { name: 'expanded',type: 'bool'},
	           ],
	           hierarchy:
	           {
	               keyDataField: { name: 'partyId' },
	               parentDataField: { name: 'partyIdFrom' }
	           },
	           id: 'partyId',
	           url: "getOrganizationUnit",
	           root: 'listReturn'
		};
		var dataAdapter = new $.jqx.dataAdapter(partyGroupsSource);
		return dataAdapter;
	};
	
	var getTreeGridColumn = function(){
		var columns = [
       		{text: uiLabelMap.OrgUnitName, datafield: 'partyName', width: 400,},			
       		{text: uiLabelMap.OrgUnitId, datafield: 'partyCode', width: 180},
       		{text: uiLabelMap.NumEmployees, datafield: 'totalEmployee', width: 120, cellsalign: 'right'},
       		{text: uiLabelMap.CommonAddress, datafield: 'postalAddress'},
       		{datafield: 'partyId', hidden: true},
       		{datafield: 'contactMechId', hidden: true},
       		{datafield: 'comments', hidden: true},
       	];
		return columns;
	};
	
	return{
		init: init
	}
}());

$(document).ready(function () {
	organizationObject.init();
});

