$(function(){
	ReceiveReturnInfoObj.init();
});
var ReceiveReturnInfoObj = (function() {
	
	var init = function() {
		initElements();
		initEvents();
	};
	
	var initElements = function (){
		if ($("#destFacility").length > 0){
			$("#destFacility").jqxDropDownButton({width: 350, theme: theme});
			$('#destFacility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
			initFacilityGrid($("#jqxgridFacility"));
		}
	} 
	
	var initEvents = function (){
		if ($("#destFacility").length > 0){
			$("#jqxgridFacility").on('rowselect', function (event) {
		        var args = event.args;
		        var rowData = args.row;
		        facilitySelected = $.extend({}, rowData);
		        var description = uiLabelMap.PleaseSelectTitle; 
		        if (facilitySelected) {
		        	if (facilitySelected.facilityCode != null){
		        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
		        	} else {
		        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
		        	}
		        }
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
		        $('#destFacility').jqxDropDownButton('setContent', dropDownContent);
		    });
			
			$('#jqxgridFacility').on('rowdoubleclick', function (event) { 
				$('#destFacility').jqxDropDownButton('close');
			});
			
			$("#jqxgridFacility").on('bindingcomplete', function (event) {
				if (facilitySelected != null){
					var rows = $('#jqxgridFacility').jqxGrid('getrows');
					if (rows.length > 0){
						for (var i in rows){
							var data1 = rows[i];
							var index = $('#jqxgridFacility').jqxGrid('getrowboundindexbyid', data1.uid);
							if (data1.facilityId == facilitySelected.facilityId){
								$('#jqxgridFacility').jqxGrid('selectrow', index);
							} else {
								$('#jqxgridFacility').jqxGrid('unselectrow', index);
							}
						}
					}
				}
			});
		}
	} 
	
	var initFacilityGrid = function(grid){
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
	
	return {
		init: init,
	}
}());