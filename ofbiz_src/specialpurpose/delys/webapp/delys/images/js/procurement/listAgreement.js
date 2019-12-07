$.jqx.theme = 'olbius';  
var theme = $.jqx.theme;
var sourceP = {
	datafields : [ {
		name : 'productId',
		type : 'string'
	}, {
		name : 'productName',
		type : 'string'
	} ],
	cache : false,
	root : 'results',
	datatype : "json",
	updaterow : function(rowid, rowdata) {
		// synchronize with the server - send update command
	},
	beforeprocessing : function(data) {
		sourceP.totalrecords = data.TotalRows;
	},
	filter : function() {
		// update the grid and send a request to the server.
		$("#jqxProductGrid").jqxGrid('updatebounddata');
	},
	pager : function(pagenum, pagesize, oldpagenum) {
		// callback called when a page or page size is changed.
	},
	sort : function() {
		$("#jqxProductGrid").jqxGrid('updatebounddata');
	},
	sortcolumn : 'productId',
	sortdirection : 'asc',
	type : 'POST',
	data : {
		noConditionFind : 'Y',
		conditionsFind : 'N',
	},
	pagesize : 5,
	contentType : 'application/x-www-form-urlencoded',
	url : 'jqxGeneralServicer?sname=JQGetListProducts',
};
$(document).ready(function(){
	var outFilterCondition = "";
	initElements();
	//Create alterpopupWindow
	initProductGrid();
	initPartyGrid("To",$("#jqxPartyToGrid"));
	initPartyGrid("From",$("#jqxPartyFromGrid"));
});
var cellclass = function (row, columnfield, value) {
	var now = new Date();
	now.setHours(0,0,0,0);
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
        return 'background-red';
    }
};
function initElements(){
	$("#roleTypeIdFromAdd").jqxDropDownList({source: roleTypeData, width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "roleTypeId"});
	$("#roleTypeIdToAdd").jqxDropDownList({source: roleTypeData, width: 200, displayMember:"description",selectedIndex: 0, valueMember: "roleTypeId"});
	$("#agreementTypeIdAdd").jqxDropDownList({source: agreementTypeData, width: 200, displayMember:"description",selectedIndex: 0, valueMember: "agreementTypeId"});	
	$("#agreementDateAdd").jqxDateTimeInput({height: '25px', width: 200,  formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null, showFooter:true, clearString:'Clear'});
	$("#fromDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy : HH:mm:ss', showFooter:true, clearString:'Clear'});
	$("#thruDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null, showFooter:true, clearString:'Clear'});
	$("#descriptionAdd").jqxInput({height: 20, width: 195});
	$("#textDataAdd").jqxInput({height: 20, width: 195});
	$("#alterpopupWindow").jqxWindow({
	    width: 720, height: 405,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
	});
	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
		var agreementDateJS = "";
		if ($('#agreementDateAdd').jqxDateTimeInput('getDate') != undefined && $('#agreementDateAdd').jqxDateTimeInput('getDate') != null) {
			agreementDateJS = new Date($('#agreementDateAdd').jqxDateTimeInput('getDate').getTime());
		}
		var fromDateJS = "";
		if ($('#fromDateAdd').jqxDateTimeInput('getDate') != undefined && $('#fromDateAdd').jqxDateTimeInput('getDate') != null) {
			fromDateJS = new Date($('#fromDateAdd').jqxDateTimeInput('getDate').getTime());
		}
		var thruDateJS = "";
		if ($('#thruDateAdd').jqxDateTimeInput('getDate') != undefined && $('#thruDateAdd').jqxDateTimeInput('getDate') != null) {
			thruDateJS = new Date($('#thruDateAdd').jqxDateTimeInput('getDate').getTime());
		}
	    row = { 
	    		productId:$('#productIdAdd').val(),
	    		partyIdFrom:$('#partyIdFromAdd').val(),
	    		partyIdTo:$('#partyIdToAdd').val(),
	    		roleTypeIdFrom:$('#roleTypeIdFromAdd').val(),
	    		roleTypeIdTo:$('#roleTypeIdToAdd').val(),
	    		agreementTypeId:$('#agreementTypeIdAdd').val(),
	    		description:$('#descriptionAdd').val(),
	    		textData:$('#textDataAdd').val(),
	    		agreementDate: agreementDateJS,
	    		fromDate: fromDateJS,
	    		thruDate: thruDateJS,
	    		statusId:$('#statusIdAdd').val()
	    	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	    // select the first row and clear the selection.
	    $("#jqxgrid").jqxGrid('clearSelection');                        
	    $("#jqxgrid").jqxGrid('selectRow', 0);  
	    $("#alterpopupWindow").jqxWindow('close');
	});
}
function initProductGrid() {
	var dataAdapterP = new $.jqx.dataAdapter(sourceP, {
		autoBind : true,
		formatData : function(data) {
			if (data.filterscount) {
				var filterListFields = "";
				for (var i = 0; i < data.filterscount; i++) {
					var filterValue = data["filtervalue" + i];
					var filterCondition = data["filtercondition" + i];
					var filterDataField = data["filterdatafield" + i];
					var filterOperator = data["filteroperator" + i];
					filterListFields += "|OLBIUS|" + filterDataField;
					filterListFields += "|SUIBLO|" + filterValue;
					filterListFields += "|SUIBLO|" + filterCondition;
					filterListFields += "|SUIBLO|" + filterOperator;
				}
				data.filterListFields = filterListFields;
			}
			return data;
		},
		loadError : function(xhr, status, error) {
			alert(error);
		},
		downloadComplete : function(data, status, xhr) {
			if (!sourceP.totalRecords) {
				sourceP.totalRecords = parseInt(data['odata.count']);
			}
		}
	});

	// Create productId
	$('#productIdAdd').jqxDropDownButton({
		width : 200,
		height : 25
	});
	$("#jqxProductGrid").jqxGrid({
		width : 400,
		source : dataAdapterP,
		filterable : true,
		virtualmode : true,
		showfilterrow : true,
		sortable : true,
		editable : false,
		autoheight : true,
		pageable : true,
		rendergridrows : function(obj) {
			return obj.data;
		},
		columns : [ {
			text : uiLabelMap.accProductId,
			datafield : 'productId'
		}, {
			text : uiLabelMap.accProductName,
			datafield : 'productName'
		}, ]
	});
	$("#jqxProductGrid").on('rowselect',function(event) {
		var args = event.args;
		var row = $("#jqxProductGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">'
								+ row['productId'] + '</div>';
		$("#productIdAdd").jqxDropDownButton('setContent', dropDownContent);
	});
}
function initPartyGrid(dir, grid) {
	var dropdown = $("#partyId"+dir+"Add");
	var sourcePartyTo = {
		datafields : [ {
			name : 'partyId',
			type : 'string'
		}, {
			name : 'firstName',
			type : 'string'
		}, {
			name : 'lastName',
			type : 'string'
		}, {
			name : 'middleName',
			type : 'string'
		}, {
			name : 'groupName',
			type : 'string'
		}, ],
		cache : false,
		root : 'results',
		datatype : "json",
		updaterow : function(rowid, rowdata) {
			// synchronize with the server - send update command
		},
		beforeprocessing : function(data) {
			sourcePartyTo.totalrecords = data.TotalRows;
		},
		filter : function() {
			// update the grid and send a request to the server.
			grid.jqxGrid('updatebounddata');
		},
		pager : function(pagenum, pagesize, oldpagenum) {
			// callback called when a page or page size is changed.
		},
		sort : function() {
			grid.jqxGrid('updatebounddata');
		},
		sortcolumn : 'partyId',
		sortdirection : 'asc',
		type : 'POST',
		data : {
			noConditionFind : 'Y',
			conditionsFind : 'N',
		},
		pagesize : 5,
		contentType : 'application/x-www-form-urlencoded',
		url : 'jqxGeneralServicer?sname=JQGetListParties',
	};

	var dataAdapterPT = new $.jqx.dataAdapter(sourcePartyTo, {
		autoBind : true,
		formatData : function(data) {
			if (data.filterscount) {
				var filterListFields = "";
				for (var i = 0; i < data.filterscount; i++) {
					var filterValue = data["filtervalue" + i];
					var filterCondition = data["filtercondition" + i];
					var filterDataField = data["filterdatafield" + i];
					var filterOperator = data["filteroperator" + i];
					filterListFields += "|OLBIUS|" + filterDataField;
					filterListFields += "|SUIBLO|" + filterValue;
					filterListFields += "|SUIBLO|" + filterCondition;
					filterListFields += "|SUIBLO|" + filterOperator;
				}
				data.filterListFields = filterListFields;
			}
			return data;
		},
		loadError : function(xhr, status, error) {
			alert(error);
		},
		downloadComplete : function(data, status, xhr) {
			if (!sourcePartyTo.totalRecords) {
				sourcePartyTo.totalRecords = parseInt(data['odata.count']);
			}
		}
	});
	// create Party To
	dropdown.jqxDropDownButton({
		width : 200,
		height : 25
	});
	grid.jqxGrid({
		width : 600,
		source : dataAdapterPT,
		filterable : true,
		virtualmode : true,
		sortable : true,
		editable : false,
		autoheight : true,
		pageable : true,
		showfilterrow : true,
		rendergridrows : function(obj) {
			return obj.data;
		},
		columns : [{
			text : uiLabelMap.DAPartyId,
			datafield : 'partyId',
			width : '20%'
		}, {
			text : uiLabelMap.DAFirstName,
			datafield : 'firstName',
			width : '20%'
		}, {
			text : uiLabelMap.DAMiddleName,
			datafield : 'middleName',
			width : '20%'
		}, {
			text : uiLabelMap.DALastName,
			datafield : 'lastName',
			width : '20%'
		}, {
			text : uiLabelMap.DAGroupName,
			datafield : 'groupName'
		}]
	});
	grid.on('rowselect',function(event) {
		var args = event.args;
		var row = grid.jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">'
								+ row['partyId'] + '</div>';
		getRoleByParty(row['partyId'], $("#roleTypeId"+dir+"Add"));
		dropdown.jqxDropDownButton('setContent',dropDownContent);
		dropdown.jqxDropDownButton("close");
	});
}
function getRoleByParty(partyId, dropdown){
	$.ajax({
		url : "getPartyRole",
		type : "POST",
		data:{
			partyId: partyId
		},
		success: function(res){
			if(res.listRoleTypes){
				var tmp = [];
				var o;
				for(var x in res.listRoleTypes){
					o = res.listRoleTypes[x];
					for(var y in roleTypeData){
						if(roleTypeData[y].roleTypeId == o.roleTypeId){
							tmp.push(roleTypeData[y]);
						}
					}
				}
				dropdown.jqxDropDownList("source", tmp);
			}
		}
	});
}
