var CallCampaign = (function() {
	var idExisted = false;
	var issueForm;
	var marketingCampaignId = "";
	var initElement = function() {
		var width = "70%";
		var cp = $("#campaignCampaignId");
		cp.jqxInput({
			width : "35%",
			height : 25,
			theme : "olbiuseditor"
		});
		var cpid = cp.data("value");
		if (cpid) {
			cp.jqxInput("val", cpid);
			cp.jqxInput({
				disabled : true
			});
		}
		var cpn = $("#campaignName");
		cpn.jqxInput({
			width : width,
			height : 25,
			theme : "olbiuseditor"
		});

		var cps = $("#campaignSummary");

		if (isThruDate) {
			var cpsummary = cps.data("value");
			cps.html(cpsummary);
		} else {
			cps.jqxEditor({
				width : width,
				height : "230px",
				theme : "olbiuseditor"
			});
			var cpsummary = cps.data("value");
			if (cpsummary) {
				cps.jqxEditor("val", cpsummary);
			}
		}

		var fdInput = $("#fromDate");
		fdInput.jqxDateTimeInput({
			width : "200px"
		});
		var fd = fdInput.data("value");
		if (fd) {
			var d = new Date(fd);
			fdInput.jqxDateTimeInput("setDate", d);

		}
		var tdInput = $("#thruDate");
		tdInput.jqxDateTimeInput({
			width : "200px"
		});
		var td = tdInput.data("value");
		if (td) {
			var d1 = new Date(td);
			var cur = new Date();
			tdInput.jqxDateTimeInput("setDate", d1);

			if (cur > d1) {
				isThruDate = true;
			}
		} else {
			tdInput.jqxDateTimeInput("setDate", null);
		}
		if ($("#isActiveContainer").length) {
			$("#isActiveContainer").jqxTooltip({
				content : uiLabelMap.NotifyClickActive,
				name : "Active"
			});
		}
		if ($("#isDoneContainer").length) {
			$("#isDoneContainer").jqxTooltip({
				content : uiLabelMap.NotifyClickDone,
				name : "Done"
			});
		}
		if (isEnableCondition) {
			enableEdited();
		} else {
			disableEdited();
		}
	};
	var enableEdited = function() {
		$("#fromDate").jqxDateTimeInput({
			disabled : false
		});
		$("#thruDate").jqxDateTimeInput({
			disabled : false
		});
		$("#campaignSummary").jqxEditor({
			disabled : false
		});
		$("#campaignName").jqxInput({
			disabled : false
		});
	};
	var disableEdited = function() {
		$("#fromDate").jqxDateTimeInput({
			disabled : true
		});
		$("#thruDate").jqxDateTimeInput({
			disabled : true
		});
		$("#campaignSummary").jqxEditor({
			disabled : true
		});
		$("#campaignName").jqxInput({
			disabled : true
		});
	};
	var save = function() {
		if (idExisted) {
			$("#campaignCampaignId").notify(uiLabelMap.CampaignIdExist, {
				position : "right",
				className : "error"
			});
			return;
		}
		if (!issueForm.jqxValidator("validate")) {
			setTimeout(function() {
				issueForm.jqxValidator("hide");
			}, 2000);
			return false;
		}
		var d1 = $("#fromDate").jqxDateTimeInput("getDate");
		var date1 = d1 ? d1.format("yyyy-mm-dd") : "";
		var d2 = $("#thruDate").jqxDateTimeInput("getDate");
		var date2 = d2 ? d2.format("yyyy-mm-dd") : "";
		var active = $("#isActive").is(":checked") ? "Y" : "N";
		var done = $("#isDone").is(":checked") ? "Y" : "N";
		var data = {
			marketingCampaignId : $("#campaignCampaignId").val(),
			campaignName : $("#campaignName").val(),
			marketingTypeId : "CALLCAMPAIGN",
			fromDate : date1,
			thruDate : date2,
			isActive : active,
			isDone : done,
			campaignSummary : $("#campaignSummary").jqxEditor("val")
		};
		var send = function(url, data) {
			$.ajax({
				url : url,
				type : "POST",
				data : data,
				success : function(res) {
					if (url == "createCallCampaignAndContact") {
						var id = res.marketingCampaignId;
						if (id) {
							window.location.href = "CreateCallCampaign?id="
									+ id;
						} else {
							bootbox.alert(uiLabelMap.CreateError);
						}
					} else {
						if (!res["_ERROR_MESSAGE_"]
								&& !res["_ERROR_MESSAGE_LIST_"]) {
							if (!$("#isActive").is(":checked")
									&& !$("#isDone").is(":checked")) {
								isEnableCondition = true;
								enableEdited();
							} else {
								isEnableCondition = false;
								disableEdited();
							}
							$("#notifyUpdateGeneral").notify(
									uiLabelMap.UpdateSuccessfully, {
										position : "left",
										className : "success"
									});
						} else {
							$("#notifyUpdateGeneral").notify(
									uiLabelMap.UpdateError, {
										position : "left",
										className : "error"
									});
						}
					}
				}
			});
		};
		if (url == "createCallCampaignAndContact") {
			bootbox.dialog(uiLabelMap.ConfirmCreateCampaign, [ {
				"label" : uiLabelMap.Cancel,
				"icon" : "fa fa-remove",
				"class" : "btn  btn-danger form-action-button pull-right",
				"callback" : function() {
					bootbox.hideAll();
				}
			}, {
				"label" : uiLabelMap.OK,
				"icon" : "fa-check",
				"class" : "btn btn-primary form-action-button pull-right",
				"callback" : function() {
					send(url, data);
				}
			} ]);
		} else {
			send(url, data);
		}
		return true;
	};
	var remove = function() {
		var send = function() {
			$
					.ajax({
						url : "removeCallCampaign",
						type : "POST",
						data : {
							marketingCampaignId : marketingCampaignId
						},
						success : function(res) {
							if (!res["_ERROR_MESSAGE_"]
									&& !res["_ERROR_MESSAGE_LIST_"]) {
								window.location.href = "ListCallCampaign";
							} else {
								$("#notifyUpdateGeneral").notify(
										uiLabelMap.RemoveError, {
											position : "left",
											className : "error"
										});
							}
						}
					});
		};
		bootbox.dialog(uiLabelMap.ConfirmRemoveCampaign, [ {
			"label" : uiLabelMap.Cancel,
			"icon" : "fa fa-remove",
			"class" : "btn  btn-danger form-action-button pull-right",
			"callback" : function() {
				bootbox.hideAll();
			}
		}, {
			"label" : uiLabelMap.OK,
			"icon" : "fa-check",
			"class" : "btn btn-primary form-action-button pull-right",
			"callback" : function() {
				send();
			}
		} ]);
	};
	var initDisabledTab = function() {
		var list = $(".tab-disabled");
		list.click(function(e) {
			e.preventDefault();
		});
		for (var x = 0; x < list.length; x++) {
			(function(x) {
				$(list[x]).jqxTooltip({
					content : uiLabelMap.MustCreateCampaignFirst,
					name : "tab"
				});
			})(x);
		}
	};
	var checkCampaignExist = function() {
		$("#campaignCampaignId").on(
				"change",
				function() {
					var val = $(this).val();
					$.ajax({
						url : "checkCampaignExist",
						type : "POST",
						data : {
							marketingCampaignId : val
						},
						success : function(res) {
							if (res.result && res.result == "EXIST") {
								idExisted = true;
								$("#campaignCampaignId").notify(
										uiLabelMap.CampaignIdExist, {
											position : "right",
											className : "error"
										});
							} else {
								idExisted = false;
							}
						}
					});
				});
	};
	var bindEvent = function() {
		$("#saveCampaign").click(function() {
			save();
		});
		$("#cancelCampaign").click(function() {
			window.location.reload();
		});
		$("#removeCampaign").click(function() {
			remove();
		});
	};

	var initRule = function() {
		issueForm.jqxValidator({
			rules : [ {
				input : "#campaignCampaignId",
				message : uiLabelMap.IdMustHaveAtLeastAnCharacter,
				action : "keyup, blur",
				rule : function(input, commit) {
					var val = input.val();
					for ( var x in val) {
						if (isNaN(val[x])) {
							return true;
						}
					}
					return false;
				}
			},
			// {input: "#campaignName", message: uiLabelMap.InvalidFieldValue,
			// action: "keyup, blur",
			// rule: function(input, commit){
			// var val = input.val();
			// if(checkHtml(val)){
			// return false;
			// }
			// return true;
			// }
			// },
			{
				input : "#campaignName",
				message : uiLabelMap.FieldRequired,
				action : "keyup, blur",
				rule : "required"
			}, {
				input : "#fromDate",
				message : uiLabelMap.FieldRequired,
				action : "keyup, blur",
				rule : function(input, commit) {
					var index = input.jqxDateTimeInput("getDate");
					if (index) {
						return true;
					}
					return false;
				}
			},
			// {input: "#thruDate", message: uiLabelMap.FieldRequired, action:
			// "keyup, blur",
			// rule: function (input, commit) {
			// var index = input.jqxDateTimeInput("getDate");
			// if(index){
			// return true;
			// }
			// return false;
			// }
			// }
			]
		});
	};
	var init = function() {
		marketingCampaignId = campaignId;
		issueForm = $("#campaignInfo");
		initElement();
		initDisabledTab();
		bindEvent();
		initRule();
		checkCampaignExist();
	};
	return {
		init : init
	};
})();
$(document).ready(function() {
	CallCampaign.init();
});
var entityMap = {
	"&" : "&amp;",
	"<" : "&lt;",
	">" : "&gt;",
	'"' : "&quot;",
	'"' : "&#39;",
	"/" : "&#x2F;"
};
var CampaignUtil = {

	renderName : function(obj) {
		var first = obj.firstName;
		var middle = obj.middleName;
		var last = obj.lastName;
		var str = last + " " + middle + " " + first;
		return str;
	},

	escapeHtml : function(string) {
		return String(string).replace(/[&<>""\/]/g, function(s) {
			return entityMap[s];
		});
	}
};
var checkHtml = function(val) {
	return /<[a-z][\s\S]*>/i.test(val);
};
// var decodeEntities = (function() {
// // this prevents any overhead from creating the object each time
// var element = document.createElement("div");
//
// function decodeHTMLEntities (str) {
// if(str && typeof str === "string") {
// // strip script/html tags
// str = str.replace(/<script[^>]*>([\S\s]*?)<\/script>/gmi, "");
// str = str.replace(/<\/?\w(?:[^"">]|"[^"]*"|"[^"]*")*>/gmi, "");
// element.innerHTML = str;
// str = element.textContent;
// element.textContent = "";
// }
//
// return str;
// }
//
// return decodeHTMLEntities;
// })();
// function decodeHtml(html) {
// var txt = document.createElement("textarea");
// txt.innerHTML = html;
// return txt.value;
// }
