var deleteRow = function(grid, url, data, commit, parentId){
	var id = parentId ? parentId : "jqxgrid";
	$.ajax({
        type: "POST",
        url: url,
        data:  data,
        success: function (data, status, xhr) {
            if(data.responseMessage == "error"){
            	commit(false);
            	$('#jqxNotification'+id).jqxNotification({ template: 'error'});
            	$("#notificationContent"+id).text(data.errorMessage);
            	$("#jqxNotification"+id).jqxNotification("open");
            }else{
            	if(commit){
            		commit(true);	
            	}
            	$('#container'+id).empty();
            	grid.jqxGrid('updatebounddata');
            	$('#jqxNotification'+id).jqxNotification({ template: 'info'});
            	$("#notificationContent"+id).text(uiLabelMap.wgaddsuccess);
            	$("#jqxNotification"+id).jqxNotification("open");
            }
        },
        error: function () {
        	if(commit){
        		commit(false);	
        	}
        }
    });   
};
var updateRow = function(grid, url, data, commit, parentId){
	var id = parentId ? parentId : "jqxgrid";
	$.ajax({
        type: "POST",                        
        url: url,
        data: data,
        success: function (data, status, xhr) {
            // update command is executed.
            if(data.responseMessage == "error"){
            	if(commit){commit(false)}
            	grid.jqxGrid('updatebounddata');
            	$('#jqxNotification' + id).jqxNotification({ template: 'error'});
            	$("#notificationContent" + id).text(data.errorMessage);
            	$("#jqxNotification" + id).jqxNotification("open");
            }else{
            	$('#container' + id).empty();
            	$('#jqxNotification' + id).jqxNotification({ template: 'info'});
            	$("#notificationContent" + id).text(uiLabelMap.wgaddsuccess);
            	$("#jqxNotification" + id).jqxNotification("open");
           		grid.jqxGrid('updatebounddata');
            }
        },
        error: function () {
            if(commit){commit(false)}
        }
    });
};
var addRow = function(grid, url, data, commit, parentId){
	var id = parentId ? parentId : "jqxgrid";
	$.ajax({
        type: "POST",
        url: url,
        data: data,
        success: function (data, status, xhr) {
            if(data && data.results && data.results.successMessage){
            	// update command is executed.
            	commit(true);
            	$('#container' + id).empty();
            	$('#jqxNotification' + id).jqxNotification({ template: 'info'});
            	$("#notificationContent" + id).text(uiLabelMap.wgaddsuccess);
            	$("#jqxNotification" + id).jqxNotification("open");
           		grid.jqxGrid('updatebounddata');
            }else{
            	commit(false);
            	$('#jqxNotification' + id).jqxNotification({ template: 'error'});
            	$("#notificationContent" + id).text(data.errorMessage);
            	$("#jqxNotification" + id).jqxNotification("open");
            }
        },
        error: function () {
            commit(false);
        }
    });     
};