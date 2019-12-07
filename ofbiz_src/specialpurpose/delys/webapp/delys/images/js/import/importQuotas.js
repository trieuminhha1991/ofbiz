$(document).ready(function(){
	 getlistQuota(
				{quotaTypeId: "IMPORT_QUOTA",}
				,"getlistQuotaAjax", "listQuotas");
});

function getlistQuota(jsonObject, url, data) {
	var listQuotas = [];
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	listQuotas = res[data];
        }
    }).done(function() {
    	renderTableByJqx(listQuotas);    	
	});
}
