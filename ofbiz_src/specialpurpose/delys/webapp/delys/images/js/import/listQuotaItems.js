$(document).ready(function(){
	var quotaId = $("#quotaId").val();
	 getlistQuotaItems(
				{quotaId: quotaId,}
				,"getlistQuotaItemsAjax", "listQuotaItems");
});

function getlistQuotaItems(jsonObject, url, data) {
	var listQuotaItems = [];
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listQuotaItems = res[data];
        }
    }).done(function() {
    	renderTableByJqx(listQuotaItems);    	
	});
}