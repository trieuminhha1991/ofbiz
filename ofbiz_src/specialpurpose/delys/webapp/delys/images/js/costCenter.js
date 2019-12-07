function saveCostCenters(listGlCategory){
	var griddata = $('#jqxgrid').jqxGrid('getdatainformation');
	listGlCategory = listGlCategory.trim();	
	var listGLC = listGlCategory.split(" ");
	
	var rowCount = griddata.rowscount;
	var costCenters = new Array();
	
	if (rowCount > 0) {
		for (var i = 0; i < rowCount; i++) {
			var row = $("#jqxgrid").jqxGrid('getrowdata', i);
			costCenters.push(setDataCostCenter(row,listGLC) ? setDataCostCenter(row,listGLC) : null);
		}
	}
	var param = {
			costCenters : JSON.stringify(costCenters)
	}
	sendRequest(param);
}


var processData = function(data){
		vat dataList = [];
		for(var i = 0;i < data.length;i++){
			var dataTmp = {};
			for(var key in data[i]){
				if(key.indexOf('_amount') != -1){
					dataTmp['orderAmount'] = data[i][key];
				}else if(key.indexOf('__reference') != -1){
					dataTmp['orderReference'] = data[i][key];
				}else {
					dataTmp[key] = data[i][key];
				}
			}
			dataList.push(dataTmp);
		}
		return dataList;
	};


function setDataCostCenter(row,listGLC){
	var costCenter = new Object;
		costCenter.organizationPartyId = row.organizationPartyId ? row.organizationPartyId : null;
		if(listGLC){
			for (var z = 0; z < listGLC.length; z++) {
				var glCategoryId = listGLC[z];
				costCenter[''+glCategoryId] = row[''+glCategoryId];
			}
		}
		costCenter.glAccountId = row.glAccountId ? row.glAccountId : null;
		costCenter.accountCode = row.accountCode ? row.accountCode : null;
		costCenter.accountName = row.accountName ? row.accountName : null;
	return costCenter;	
}

function sendRequest(param){
	$.ajax({
		url : 'updateCostCenters',
		data : param,
		type : 'post',
		async : false,

		success : function(data) {
			getResultOfSaveCostCenters(data);
		},
		error : function(data) {
			getResultOfSaveCostCenters(data);
		}
	});
}

function getResultOfSaveCostCenters(data) {
    $('#jqxgrid').jqxGrid('updatebounddata');
    $('#jqxNotificationjqxgrid').jqxNotification('closeLast');
    $('#containerjqxgrid').empty();
    if (data._ERROR_MESSAGE_ && !data._EVENT_MESSAGE_) {
    	$('#jqxNotificationjqxgrid').jqxNotification({ template: 'error'});
    	$("#notificationContentjqxgrid").text(data._ERROR_MESSAGE_);

    } else if(data._EVENT_MESSAGE_ && !data._ERROR_MESSAGE_){
    	$('#jqxNotificationjqxgrid').jqxNotification({ template: 'info'});
    	$("#notificationContentjqxgrid").text(data._EVENT_MESSAGE_);
    }
	 $("#jqxNotificationjqxgrid").jqxNotification("open");
}
