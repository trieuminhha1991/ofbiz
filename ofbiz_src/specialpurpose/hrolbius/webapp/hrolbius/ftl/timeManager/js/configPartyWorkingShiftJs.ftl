<script type="text/javascript">
$(document).ready(function(){
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
        	var container = $("<div id='toolbarcontainer' class='widget-header'><h4>${uiLabelMap.OrganizationUnit}</h4></div>");
        	toolBar.append(container);
        }
	});
	$("#configPartyShiftTreeGrid").on('rowDoubleClick', function(event){
		 var rowData = event.args.row;
		 if(globalVar.getAllWorkingShift){			
			$.ajax({
				url: 'getAllWorkingShift',
				type: 'POST',
				success: function(response){
					if(response.responseMessage == 'success'){
						globalVar.getAllWorkingShift = false;	
						globalVar.allWorkingShiftArr = response.listReturn; 
						var source = {
	                		   localdata: globalVar.allWorkingShiftArr,
	                           datatype: "array"
	                   	}
	                   	var dataAdapter = new $.jqx.dataAdapter(source);
	                   	$("#workingShiftDropdownlist").jqxDropDownList({source: dataAdapter});
	                   	fillDataInConfigWSWindow(rowData);
					}
				},
				complete:  function(jqXHR, textStatus){
					$("#configPartyShiftTreeGrid").jqxTreeGrid({disabled: false});
				}
			});
		}else{
			fillDataInConfigWSWindow(rowData);
		}
		 openJqxWindow($("#editConfigPartyWSWindow"));		 
	});
});

function fillDataInConfigWSWindow(rowData){
	$("#configWSPartyId").val(rowData.partyId)
	if(rowData.partyName){
		$("#configWSPartyName").val(rowData.partyName);
	}
	if(rowData.workingShiftId){
		$("#workingShiftDropdownlist").val(rowData.workingShiftId);
	}
}

function createTreeGridSource(){
	var partyGroupsSource = {
			dataType: "json",
			dataFields: [
               {name: 'partyId', type: 'string' },
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
}

function createTreeGridColumn(){
	var columns = [
			{text: '${uiLabelMap.OrgUnitName}', datafield: 'partyName', width: 400, editable: false},			
			{text: '${uiLabelMap.OrgUnitId}', datafield: 'partyId', width: 180, editable: false},
			{text: '${uiLabelMap.HrCommonWorkingShift}', datafield: 'workingShiftName', editable: false, columnType: "template"},
			{datafield: 'workingShiftId', hidden: true}
	]
	return columns; 
}
	
</script>