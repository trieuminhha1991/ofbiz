function anyOneChange() {
	$(window).bind('beforeunload', function(e) {
	    if (confirm) {
	        return "Are you sure?";
	    }
	});
}
function scan() {
	$(window).off('beforeunload');
	$("body").css("opacity", 0.2);
	$("#myImg").css({"visibility": "visible", "opacity": 1});
	var productQualityName = $("#txtProductQualityName").val();
	var productId = $("#productSelect").val();
	var shelfLife = $("#txtShelfLife").val();
	var fromDate = $("#txtFromDate").val();
	var thruDate = $("#txtThruDate").val();
	var shelfLifeUnit = $("#shelfLifeUnit").val();
	saveProductQualityService(
				{productQualityName: productQualityName,
					productId: productId,
					shelfLife: shelfLife,
					fromDate: fromDate,
					thruDate: thruDate,
					shelfLifeUnit: shelfLifeUnit,}
				,"SaveProductQualityService", "contentId");
}
var contentId = "";
var dataResourceId = "";
function saveProductQualityService(jsonObject, url, data) {
	listMonth = [];
	var maps = [];
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	contentId = res[data];
        	dataResourceId = res["dataResourceId"];
        }
    }).done(function() {
    	createFolder();
	});
}
function createFolder() {
	var data = {parentNodePath: "/delys", nodeName: contentId,}
	 jQuery.ajax({
	        url: "createProductQuantityPublishAjax",
	        type: "POST",
	        data: data,
	        success: function(res) {}
	    }).done(function() {
	    	saveImageScan();
		});
}
function saveImageScan() {
	var newFolder = "/delys/" + contentId;
	for ( var d in listImage) {
		var file = listImage[d];
		var dataResourceName = file.name;
		var path = "";
		var form_data= new FormData();
		form_data.append("uploadedFile", file);
		form_data.append("folder", newFolder);
		jQuery.ajax({
			url: "uploadDemo",
			type: "POST",
			data: form_data,
			cache : false,
			contentType : false,
			processData : false,
			success: function(res) {
				path = res.path;
	        }
		}).done(function() {
			
		});
	}
	updateDataSource(
			{dataResourceName: dataResourceName,
				dataResourceId: dataResourceId,
				objectInfo: "/DELYS" + newFolder,}
			,"updateDataSourceAjax");
}
function updateDataSource(jsonObject, url) {
	jQuery.ajax({
        url: url,
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	
        }
    }).done(function() {
//    	window.location.href = "ListProductQuality";
    	createQuotaNotification();
	});
}
function createQuotaNotification() {
	var thruDate = $("#txtThruDate").val();
	var exploded = thruDate.split("/");
    var d = new Date(exploded[2],exploded[1] - 1,exploded[0]);
	var partyId = "qaadmin";
	var targetLink = "contentId=" + contentId;
	var action = "ListProductQuality";
	var quotaName = $("#txtProductQualityName").val();
	var header = "Cong bo chat luong " + quotaName + " da het han";
	var newDate = d.getTime() - (10*86400000);
	var dateNotify = new Date(newDate);
	dateNotify = getDateTimestamp(dateNotify);
	console.log(dateNotify);
	var jsonObject = {partyId: partyId,
						header: header,
						openTime: dateNotify,
						action: action,
						targetLink: targetLink,};
	jQuery.ajax({
        url: "createQuotaNotification",
        type: "POST",
        data: jsonObject,
        success: function(res) {
        	
        }
    }).done(function() {
    	window.location.href = "ListProductQuality";
	});
}
function getDateTimestamp(dateNotify) {
	var getFullYear = dateNotify.getFullYear();
	var getDate = dateNotify.getDate();
	var getMonth = dateNotify.getMonth() + 1;
	if (getDate < 10) {
		getDate = '0' + getDate;
	}
	if (getMonth < 10) {
		getMonth = '0' + getMonth;
	}
	dateNotify = getFullYear + '-' + getMonth + '-' + getDate;
	return dateNotify;
}
