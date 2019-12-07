$(document).ready(function() {
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });

	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 200, autoOpenPopup: false, mode: 'popup'});

	contextMenu.on('itemclick', function (event) {
        var args = event.args;
        var itemId = $(args).attr('id');
        switch (itemId) {
		case "activate":
			var rowIndexSelected = $('#ListFAQ').jqxGrid('getSelectedRowindex');
			var rowData = $('#ListFAQ').jqxGrid('getrowdata', rowIndexSelected);
			var contentId = rowData.contentId;
			var statusId = rowData.statusId;
		if (statusId == 'CTNT_PUBLISHED') {
			$("#ListFAQ").jqxGrid('setcellvalue', rowIndexSelected, "statusId", "CTNT_DEACTIVATED");
			} else {
				$("#ListFAQ").jqxGrid('setcellvalue', rowIndexSelected, "statusId", "CTNT_PUBLISHED");
			}
			break;
		default:
			break;
		}
	});
	contextMenu.on('shown', function () {
		var rowIndexSelected = $('#ListFAQ').jqxGrid('getSelectedRowindex');
	var rowData = $('#ListFAQ').jqxGrid('getrowdata', rowIndexSelected);
	var statusId = rowData.statusId;
	if (statusId == 'CTNT_PUBLISHED') {
		$("#activate").html("<i class='fa-frown-o'></i>&nbsp;&nbsp;" + multiLang.DmsDeactivate);
		} else {
			$("#activate").html("<i class='fa-smile-o'></i>&nbsp;&nbsp;" + multiLang.DmsActive);
		}
	});
});
var cellclassname = function (row, column, value, data) {
	if (data.partyRole) {
		return 'green';
	}
};
if (typeof (Answer) == "undefined") {
	var Answer = (function() {
		var send = function(index, contentIdTo) {
			if ($("#contentId" + index).val()) {
				DataAccess.execute({
					url: "updateReply",
					data: {
						index: index,
						contentId: $("#contentId" + index).val(),
						longDescription: $("#editor" + index).jqxEditor('val')}
					}, Answer.updateMode);
			} else {
				DataAccess.execute({
					url: "createReply",
					data: {
						index: index,
						contentIdTo: contentIdTo,
						longDescription: $("#editor" + index).jqxEditor('val')}
					}, Answer.updateMode);
			}
		};
		updateMode = function(res) {
			var index = res['index'];
			var contentId = res['contentId'];
			$("#contentId" + index).val(contentId);
			$("#input" + index).html("<i class='fa fa-check'></i>" + multiLang.BSUpdate);
			if (typeof (index) == 'string') {
				$('#jqxNotificationNested').jqxNotification('closeLast');
				if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
					$("#jqxNotificationNested").jqxNotification({ template: 'error'});
				$("#notificationContentNested").text(multiLang.updateError);
				$("#jqxNotificationNested").jqxNotification("open");
				}else {
					$("#jqxNotificationNested").jqxNotification({ template: 'info'});
				$("#notificationContentNested").text(multiLang.updateSuccess);
				$("#jqxNotificationNested").jqxNotification("open");
				}
			}
		};
		return {
			send: send,
			updateMode: updateMode
		};
	})();
}
if (typeof (TimeAgo) == "undefined") {
	var TimeAgo = (function() {
		var convert = function(createdStamp) {
			var minute = (1000*60);
			var hour = minute*60;
			var day = hour*24;
			var week = day*7;
			var month = week*4;
			var year = month*12;

			var timeAgo = "";
			var current = new Date().getTime();
			var ago = current - createdStamp;

			if (ago > day*2) {
				timeAgo = new Date(createdStamp);
			} else {
				if (ago > day) {
					timeAgo = analysTime(ago, day, "BSDaysAgo");
				} else {
					if (ago > hour) {
						timeAgo = analysTime(ago, hour, "BSHoursAgo");
					} else {
						timeAgo = analysTime(ago, minute, "BSMinutesAgo");
					}
				}
			}
			return timeAgo;
		};
		var analysTime = function(ago, time, timeUnit) {
			ago = Math.round(ago/time);
			if (ago == 0) {
				ago = 1;
			}
			return ago + " " + multiLang[timeUnit];
		};
		return {
			convert: convert
		};
	})();
}