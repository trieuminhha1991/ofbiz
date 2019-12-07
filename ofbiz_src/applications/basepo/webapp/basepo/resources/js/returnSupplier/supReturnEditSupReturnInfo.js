$(function() {
	supReturnInfo.init();
});

var supReturnInfo = (function() {
	var validatorVAL;

	var init = function() {
		initElement();
		initEvent();
		initElementComplex();
		initValidateForm();
	};
	
	var initElement = function() {
		$("#partyId").jqxDropDownList({
			source : [],
			disabled : true,
			theme : theme,
			width : 300,
			placeHolder : toPartyName,
			autoDropDownHeight : true
		});
		
		$("#entryDate").jqxDateTimeInput({
			formatString : "dd/MM/yyyy HH:mm",
			width : 300,
			showFooter : true,
			allowNullDate : false,
			value : entryDate
		});
		$("#currencyUomId").jqxDropDownList({
			source : [],
			disabled : true,
			theme : theme,
			width : 300,
			placeHolder : currencyUomId,
			autoDropDownHeight : true
		});

		$("#description").jqxInput({
			width : 300,
			height : 65,
		});
		
		if (description) {
			$("#description").jqxInput('val', description);
		}
		
		$("#facility").jqxDropDownButton({width: 300, theme: 'olbius'}); 
		$('#facility').jqxDropDownButton('setContent', '<div class="green-label button-label">'+uiLabelMap.PleaseSelectTitle+'</div>');
		var description = uiLabelMap.PleaseSelectTitle; 
        if (facilitySelected) {
        	if (facilitySelected.facilityCode != null){
        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
        	} else {
        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
        	}
        }
		
        var dropDownContent = '<div class="green-label button-label">'+ description +' </div>';
        $('#facility').jqxDropDownButton('setContent', dropDownContent);

	};

	var initElementComplex = function() {
		initFacilityGrid($("#jqxGridFacility"));
	};

	var initEvent = function() {
		$("#jqxGridFacility").on('rowselect', function (event) {
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
			
	        var dropDownContent = '<div class="green-label button-label">'+ description +' </div>';
	        $('#facility').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$('#jqxGridFacility').on('rowdoubleclick', function (event) { 
			$('#facility').jqxDropDownButton('close');
		});
		
		$("#jqxGridFacility").on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = $('#jqxGridFacility').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == facilitySelected.facilityId){
							var index = $('#jqxGridFacility').jqxGrid('getrowboundindexbyid', data1.uid);
							$('#jqxGridFacility').jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
	};

	var initValidateForm = function() {
		var mapRules = [ 
		 ];
		var extendRules = [
			{input: '#facility', message: uiLabelMap.FieldRequired, action: 'blur', position: 'right',
				rule: function(input, commit){
					var value = $(input).val();
					if(value){
						if (value.trim() === uiLabelMap.PleaseSelectTitle.trim()){
							return false;
						}
						else{
							return true
						}
					}else{
						return false;
					}
				}
			},
		];
		validatorVAL = new OlbValidator($("#editReturnSupplier"), null,
		extendRules, {
			position : "right"
		});
	};
	var getValidator = function() {
		return validatorVAL;
	};
	
	var initFacilityGrid = function(grid){
		var url = "jqGetFacilities";
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
	
	return {
		init : init,
		getValidator : getValidator,
	};
}());