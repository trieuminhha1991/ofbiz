$(document).ready(function() {
	$("#jqxmenu").jqxMenu({
		width : '120px',
		height : '140px',
		theme : 'olbius',
		autoOpenPopup : false,
		mode : 'popup'
	});
	var data;
	$("#listRequest").on('rowSelect', function(event) {
		var args = event.args;
		var rowBoundIndex = args.rowindex;
		data = args.row;
		console.log(data);
	});
	$("#edit").click(function() {
		if (data && data.marketingCampaignId) {
			window.location.href = "EditTradeCampaign?id=" + data.marketingCampaignId;
		}
	});
	$("#del").click(function() {
		if (data && data.marketingCampaignId) {
			$.ajax({
				url : "deleteMarketingCampaign",
				type: "POST",
				data : {
					id :data.marketingCampaignId
				},
				success: function(res){
					
				}
			});
		}
	});
});

