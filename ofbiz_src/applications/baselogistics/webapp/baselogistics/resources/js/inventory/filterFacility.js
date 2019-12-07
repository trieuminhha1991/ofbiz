var FilterFacility = (function(){
	if (listFacilitySelected === undefined) var listFacilitySelected = [];
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
	};
	
	var initElementComplex = function (){
		initFacilityGrid();
	};
	
	var initInputs = function(){
		$("#facility").jqxDropDownButton({width: 300}); 
		var descTmp =  uiLabelMap.Facility;
		$('#facility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+descTmp+'</div>');
	};

	var initEvents = function(){
		$('#facility').on('close', function (event){
			if (listFacilitySelected.length > 0){
				var listFacilities = JSON.stringify(listFacilitySelected);
				var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
			 	tmpS._source.url = "jqxGeneralServicer?sname=getInventory&ownerPartyId="+company+"&listFacilities="+listFacilities;
			 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
			 	$("#detailItems").hide();
			 	$("#groupByProduct").show();
			} else {
				var tmpS = $("#jqxgridInvGroupByProduct").jqxGrid('source');
				if (deposit){
					tmpS._source.url = "jqxGeneralServicer?sname=getInventory&deposit="+deposit;
				} else {
					tmpS._source.url = "jqxGeneralServicer?sname=getInventory";
				}
			 	$("#jqxgridInvGroupByProduct").jqxGrid('source', tmpS);
			 	$("#detailItems").hide();
			 	$("#groupByProduct").show();
			}
		});
		
		$("#jqxgridFacility").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        var rows = $("#jqxgridFacility").jqxGrid('selectedrowindexes');
	        var description = null; 
	        if (rows.length <= 1) {
	        	if (rowData.facilityCode){
	        		description = rowData.facilityName + ' ['+rowData.facilityCode+']';
	        	} else {
	        		description = rowData.facilityName + ' ['+rowData.facilityId+']';
	        	}
	        } else {
	        	if (rowData.facilityCode){
	        		description = rowData.facilityName + ' ['+rowData.facilityCode+'], ... (' + rows.length + ' ' + uiLabelMap.Facility +')';
	        	} else {
	        		description = rowData.facilityName + ' ['+rowData.facilityId+'], ... (' + rows.length + ' ' + uiLabelMap.Facility +')';
	        	}
	        }
	        var check = false;
	        for (var x in listFacilitySelected){
	        	if (listFacilitySelected[x].facilityId == rowData.facilityId){
	        		check = true;
	        		break;
	        	}
	        }
	        if (!check){
	        	var item = {facilityId: rowData.facilityId};
	        	listFacilitySelected.push(item);
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#facility').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$("#jqxgridFacility").on('rowunselect', function (event) {
			var args = event.args;
			var rowData = args.row; 
			var rows = $("#jqxgridFacility").jqxGrid('selectedrowindexes');
			var description = uiLabelMap.Facility; 
			if (rows.length == 1) {
				description = rowData.facilityName + ' ['+rowData.facilityId+']';
			} else if (rows.length > 1) {
				description = rowData.facilityName + ' ['+rowData.facilityId+'], ... (' + rows.length + ' ' + uiLabelMap.Facility +')';
			}
	        for (var x in listFacilitySelected){
	        	if (listFacilitySelected[x].facilityId == rowData.facilityId){
	        		listFacilitySelected.splice(x, 1);
	        	}
	        }
			var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
			$('#facility').jqxDropDownButton('setContent', dropDownContent);
		});
	};

	var initFacilityGrid = function(){
		var url = "jqGetFacilities";
		if (deposit && deposit == 'Y'){
			url = "jqGetFacilities&deposit=Y";
		}
		if(listFacility) {
		    url="jqGetFacilitiesForSup";
        }
		var datafield =  [
				{ name: 'facilityId', type: 'string'},
				{ name: 'facilityCode', type: 'string'},
				{ name: 'facilityName', type: 'string'},
      	];
      	var columnlist = [
				{text: uiLabelMap.FacilityId, datafield: 'facilityCode', width: '25%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value){
							var data = $("#jqxgridFacility").jqxGrid('getrowdata',row);
							value = data.facilityId;
						}
				        return '<div style=margin:4px;>' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.FacilityName, datafield: 'facilityName', width: '75%',
					cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value) + '</div>';
				    }
				}
      	];

        var configPartyGroup = {
            useUrl: true,
            width: 500,
            widthButton: '100%',
            showdefaultloadelement: false,
            autoshowloadelement: false,
            selectionmode: 'checkbox',
            datafields: datafield,
            columns: columnlist,
            url: url,
            useUtilFunc: true,
            key: 'facilityId',
            autoCloseDropDown: true,
            filterable: true,
            sortable: true
        };
        new OlbGrid($("#jqxgridFacility"), null, configPartyGroup, []);
	};
	var getFacilities = function() {
		return listFacilitySelected;
	};
	return {
		init: init,
		getFacilities: getFacilities
	}
}());