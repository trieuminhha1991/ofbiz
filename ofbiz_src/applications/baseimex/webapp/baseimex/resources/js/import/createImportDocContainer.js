$(function(){
	Container.init();
});
var Container = (function() {
	
	var gridFacility = $("#gridFacilityContainer");
	var validatorCONT = null;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	
	var initInputs = function (){
		
		$("#containerNumber").jqxInput({width: 295, height: 25, theme: theme}); 
		$("#sealNumber").jqxInput({width: 295, height: 25, theme: theme}); 
		$("#contDescription").jqxInput({width: 300, theme: theme}); 
		
		$("#facilityContainer").jqxDropDownButton({width: 300, theme: theme});
		$('#facilityContainer').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#containerTypeId").jqxDropDownList({placeHolder : uiLabelMap.PleaseSelectTitle, source: containerTypeData, displayMember: 'description', valueMember: 'containerTypeId', theme: theme, width: '300', height: '25'});
		
	}
	
	var initElementComplex = function (){
		initFacilityGrid(gridFacility);
	}
	
	var initEvents = function (){
		gridFacility.on('rowclick', function (event) {
	        var args = event.args;
	        var rowBoundIndex = args.rowindex;
	        var rowData = gridFacility.jqxGrid('getrowdata', rowBoundIndex);
	        facilitySelected = {};
	        facilitySelected = $.extend({}, rowData);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ rowData.facilityName +'</div>';
	        $('#facilityContainer').jqxDropDownButton('setContent', dropDownContent);
	        $("#facilityContainer").jqxDropDownButton('close');
		});
		
		gridFacility.on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = gridFacility.jqxGrid('getrows');
				if (rows && rows.length > 0){
					for (var i in rows){
						if (rows[i].facilityId == facilitySelected.facilityId){
							var index = gridFacility.jqxGrid('getrowboundindexbyid', rows[i].uid);
							facilitySelected.jqxGrid('selectrow', index);
							break;
						}
					}
				}
			}
		});
	}
	
	var initFacilityGrid = function(grid){
		var url = "jqGetFacilities&primaryFacilityGroupId=FACILITY_INTERNAL";
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
			];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				}
			},
			{text: uiLabelMap.FacilityId, datafield: 'facilityCode', width: '150',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			{text: uiLabelMap.FacilityName, datafield: 'facilityName', minwidth: '200',
				cellsrenderer: function (row, column, value) {
					return '<div style="cursor:pointer;">' + (value) + '</div>';
				}
			},
			];
		
		var config = {
				width: 450, 
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
	
	var initValidateForm = function (){
		var extendRules = [
			{
				input: '#containerTypeId', 
				message: uiLabelMap.FieldRequired, 
				action: 'blur', 
				position: 'right',
				rule: function (input) {
					var x = $("#containerTypeId").jqxDropDownList('val');
					if (x === undefined || x === null || x === ""){
						return false;
					}
					return true;
				}
			},
		];
   		var mapRules = [
			{input: '#containerNumber', type: 'validInputNotNull'},
        ];
   		validatorCONT = new OlbValidator($("#ContainerForm"), mapRules, extendRules, {position: 'right'});
	}
	
	var getValidate = function (){
		if (validatorCONT){
			return validatorCONT.validate();
		}
		return true;
	}
	return {
		init: init,
		getValidate: getValidate,
	}
}());