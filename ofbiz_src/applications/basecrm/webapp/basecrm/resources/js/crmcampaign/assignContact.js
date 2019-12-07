var Assign = (function() {
	var grid;
	var isEditable = true;
	var initElement = function() {
		width = "calc(100% - 2px)";
		var height = 16;
		var conditions = {};
		initDataSource();
		$("#AgeRangeFrom").jqxDateTimeInput({
			width : "calc(50% - 22px)",
			height : 25,
			theme : theme,
			showFooter : true,
			todayString : uiLabelMap.today,
			clearString : uiLabelMap.clear
		});
		$("#AgeRangeFrom").jqxDateTimeInput("setDate", null);
		$("#AgeRangeTo").jqxDateTimeInput({
			width : "calc(50% - 22px)",
			height : 25,
			theme : theme,
			showFooter : true,
			todayString : uiLabelMap.today,
			clearString : uiLabelMap.clear
		});
		$("#AgeRangeTo").jqxDateTimeInput("setDate", null);

		$("#entryFrom").jqxDateTimeInput({
			width : "calc(50% - 7px)",
			height : 25,
			theme : theme,
			showFooter : true,
			todayString : uiLabelMap.today,
			clearString : uiLabelMap.clear
		});
		$("#entryFrom").jqxDateTimeInput("setDate", null);
		$("#entryTo").jqxDateTimeInput({
			width : "calc(50% - 7px)",
			height : 25,
			theme : theme,
			showFooter : true,
			todayString : uiLabelMap.today,
			clearString : uiLabelMap.clear
		});
		var sourceRegion = {
			datatype : "json",
			datafields : [ {
				name : "geoId"
			}, {
				name : "geoName"
			} ],
			url : "autoCompleteGeoAjax?geoTypeId=SUBREGION&geoId=VNM"
		};
		var dataAdapterRegion = new $.jqx.dataAdapter(sourceRegion);
		$("#entryTo").jqxDateTimeInput("setDate", null);
		$("#Region").jqxDropDownList({
			source : dataAdapterRegion,
			width : width,
			theme : theme,
			valueMember : "geoId",
			displayMember : "geoName",
			placeHolder : "",
			autoDropDownHeight : true,
			placeHolder : multiLang.filterchoosestring
		});
		$("#ResultEnumId").jqxComboBox({
			multiSelect : true,
			source : reasonType,
			theme : theme,
			displayMember : "description",
			valueMember : "enumTypeId",
			width : width,
			height : 25,
			"autoDropDownHeight" : false,
			renderer : function(index, label, value) {
				var da = reasonType[index];
				var seq = da.enumTypeId ? "[" + da.enumTypeId + "] " : "";
				var valueStr = seq + label;
				return valueStr;
			},
			renderSelectedItem : function(index, item) {
				var item = reasonType[index];
				if (item != null) {
					var label = item.enumTypeId;
					return label;
				}
				return "";
			}
		});

		$(".clear-dropdown").click(function() {
			var obj = $(this);
			var sib = obj.siblings();
			for (var x = 0; x < sib.length; x++) {
				var that = $(sib[x]);
				that.jqxDropDownList("clearSelection");
				that.jqxComboBox("clearSelection");
			}

		});
		initEmployee($("#Employee"));
	};
	var initDataSource = function() {
		var obj = $("#DataSource");
		var source = {
			datatype : "json",
			type : "POST",
			datafields : [ {
				name : "dataSourceId"
			} ],
			url : "getDataResources"
		};
		var dataAdapter = new $.jqx.dataAdapter(source, {
			formatData : function(data) {
				if (obj.jqxComboBox("searchString") != undefined) {
					data.searchKey = obj.jqxComboBox("searchString");
					return data;
				}
			}
		});
		obj.jqxComboBox({
			remoteAutoComplete : true,
			source : dataAdapter,
			theme : theme,
			displayMember : "dataSourceId",
			valueMember : "dataSourceId",
			width : "calc(100% - 2px)",
			height : 25,
			"autoDropDownHeight" : true,
			placeHolder : "",
			search : function(searchString) {
				dataAdapter.dataBind();
			}
		});
	};
	var initEmployee = function(element) {
		var source = {
			datatype : "json",
			datafields : [ {
				name : "partyId"
			}, {
				name : "partyFullName"
			}, {
				name : "partyDetail"
			} ],
			url : "getCallCenterEmployee"
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		element.jqxComboBox({
			multiSelect : true,
			source : dataAdapter,
			theme : theme,
			displayMember : "partyDetail",
			valueMember : "partyId",
			width : "calc(100% - 2px)",
			height : 25,
			"autoDropDownHeight" : true,
			placeHolder : ""
		});
		element.on("bindingComplete", function() {
			var data = element.jqxComboBox("source");
			var source = data.records;
			initContextEmployee(source);
			Assigned.initContextEmployee(source);
		});
	};
	var initContextEmployee = function(data) {
		var contextmenu = $("#ContactContext");
		var element = $("#ContactContext ul");
		for ( var x in data) {
			var obj = $("<li></li>");
			obj.attr("id", "Employee-" + data[x].partyId);
			obj.attr("data-id", data[x].partyId);
			obj.html(data[x].partyFullName);
			element.append(obj);
		}
		contextmenu.jqxMenu({
			theme : theme,
			width : 230,
			autoOpenPopup : false,
			mode : "popup"
		});
		var firstAssign = true;
		contextmenu.on("itemclick", function(event) {
			var args = event.args;
			var itemId = $(args).data("id");
			firstAssign = false;
			CampaignTerms.save();
			assignContactToParty(itemId);
		});
		contextmenu.on("shown", function() {
			if (!isEditable) {
				for ( var x in data) {
					contextmenu.jqxMenu("disable", "Employee-"
							+ data[x].partyId, true);
				}
			} else {
				for ( var x in data) {
					contextmenu.jqxMenu("disable", "Employee-"
							+ data[x].partyId, false);
				}
			}
		});
	};
	var bindTabClick = function() {
		$("#AssignResourceTab").on("shown.bs.tab", function(e) {
			var href = $(e.target).attr("href");
			switch (href) {
			case "#assignContact":
				isEditable = isEnableCondition;
				grid = $("#ListResource");
				if (!grid.data("init")) {
					grid.attr("data-init", true);
					initGridListResource();
					grid.jqxGrid({
						enabletooltips : true
					});
					if (campaignId) {
						CampaignTerms.load(campaignId);
					}
				}
				grid.jqxGrid("updatebounddata");
				checkDataHasAssigned();
				if (isEditable) {
					$("#Employee").jqxComboBox({
						disabled : false
					});
				} else {
					$("#Employee").jqxComboBox({
						disabled : true
					});
				}
				break;
			case "#assignedContact":
				$("#ListResourceAssigned").jqxGrid("updatebounddata");
				break;
			}
		});
	};
	var reloadConditionGrid = function() {
		conditions = {};
		var url = "jqxGeneralServicer?sname=GetContactResource";
		var dataSourceId = $("#DataSource").val();
		var entry = getEntryDate();
		var birth = getBirthDate();
		var area = $("#Region").val();
		var result = $("#ResultEnumId").jqxComboBox("getSelectedItems");

		if (dataSourceId) {
			url += "&dataSourceId=" + dataSourceId;
			conditions.dataSourceId = dataSourceId;
		}
		if (entry.entryDateFrom) {
			url += "&entryDateFrom=" + entry.entryDateFrom;
			conditions.entryDateFrom = entry.entryDateFrom;
		}
		if (entry.entryDateTo) {
			url += "&entryDateTo=" + entry.entryDateTo;
			conditions.entryDateTo = entry.entryDateTo;
		}
		if (birth) {
			if (birth.birthDateFrom) {
				url += "&birthDateFrom=" + birth.birthDateFrom;
				conditions.birthDateFrom = birth.birthDateFrom;
			}
			if (birth.birthDateTo) {
				url += "&birthDateTo=" + birth.birthDateTo;
				conditions.birthDateTo = birth.birthDateTo;
			}
		}
		if (area) {
			url += "&areaGeoId=" + area;
			conditions.areaGeoId = area;
		}
		if (result && result.length) {
			var tmp = [];
			for ( var x in result) {
				tmp.push(result[x].value);
			}
			url += "&resultEnumId=" + JSON.stringify(tmp);
			conditions.resultEnumId = result;
		}
		if (url.indexOf("&") == -1)
			return;
		var adapter = grid.jqxGrid("source");
		if (adapter) {
			adapter.url = url;
			adapter._source.url = adapter.url;
			grid.jqxGrid("source", adapter);
		}
	};
	var getEntryDate = function() {
		var fromDate = $("#entryFrom").jqxDateTimeInput("getDate");
		var from = fromDate ? fromDate.getTime() : "";
		var thruDate = $("#entryTo").jqxDateTimeInput("getDate");
		var thru = thruDate ? (thruDate.getTime() + 86399000) : "";
		var obj = {};
		if (from) {
			obj.entryDateFrom = from;
		}
		if (thru) {
			obj.entryDateTo = thru;
		}
		return obj;
	};
	var setEntryDate = function(entryDateFrom, entryDateTo) {
		if (entryDateFrom) {
			var d = new Date(parseInt(entryDateFrom));
			$("#entryFrom").jqxDateTimeInput("setDate", d);
		}
		if (entryDateTo) {
			var d2 = new Date(parseInt(entryDateTo));
			$("#entryTo").jqxDateTimeInput("setDate", d2);
		}
	};
	var getBirthDate = function() {
		var from = $("#AgeRangeFrom").jqxDateTimeInput("getDate");
		var to = $("#AgeRangeTo").jqxDateTimeInput("getDate");
		var obj = {};
		if (from) {
			obj.birthDateFrom = from.getTime();
		}
		if (to) {
			obj.birthDateTo = to.getTime();
		}
		return obj;
	};
	var substractDate = function(d, s) {
		var mi = d.getTime();
		var off = s * 86400000;
		var sub = mi - off;
		var date = new Date(sub);
		return date;
	};
	var getFromThruDate = function() {
		var fr = $("#fromDate").jqxDateTimeInput("getDate");
		var fromD = "";
		var obj = {};
		if (fr) {
			fromD = fr.getTime();
			obj.fromDate = fromD;
		}
		var thru = $("#thruDate").jqxDateTimeInput("getDate");
		var thruD = "";
		if (thru) {
			thru.setHours(23);
			thru.setMinutes(59);
			thru.setSeconds(59);
			thruD = thru.getTime();
			obj.thruDate = thruD;
		}
		return obj;
	};
	var checkDataHasAssigned = function() {
		$.ajax({
			url : "checkDataHasAssigned",
			data : {
				marketingCampaignId : campaignId
			},
			type : "POST",
			success : function(res) {
				setTimeout(function() {
					if (res.hasData) {
						CampaignTerms.disableTermElements(true);
						$("#NoticeTerm").show();
					} else {
						CampaignTerms.enableTermElements(false);
						$("#NoticeTerm").hide();
					}
				}, 300);
			}
		});
	};
	var autoAssignContact = function(quantity) {
		var time = getFromThruDate();
		if (campaignId && typeof (conditions) != "undefined") {
			quantity = _.pick(quantity, function(value, key, object) {
				return value > 0;
			});
			if (!_.isEmpty(quantity)) {
				var obj = $.extend({}, conditions, {
					marketingCampaignId : campaignId,
					fromDate : time.fromDate,
					thruDate : time.thruDate,
					quantity : JSON.stringify(quantity)
				});
				$.ajax({
					url : "autoAssignContact",
					data : obj,
					type : "POST",
					success : function(res) {
						Loading.hide("loadingMacro");
						assignSuccess(res);
					},
					beforeSend : function() {
						Loading.show("loadingMacro");
					},
				});
			}
		}
	};
	var assignContactToParty = function(partyId) {
		if (campaignId) {
			var contacts = getContactSelected();
			if (!_.isEmpty(contacts)) {
				var time = getFromThruDate();
				var obj = {
					marketingCampaignId : campaignId,
					fromDate : time.fromDate,
					thruDate : time.thruDate,
					partyId : partyId,
					contacts : JSON.stringify(contacts)
				};
				$.ajax({
					url : "assignContactToParty",
					data : obj,
					type : "POST",
					success : function(res) {
						Loading.hide("loadingMacro");
						assignSuccess(res);
					},
					beforeSend : function() {
						Loading.show("loadingMacro");
					}
				});
			}
		}
	};
	var getContactSelected = function() {
		var all = grid.jqxGrid("getselectedrowindexes");
		var contacts = [];
		for ( var x in all) {
			var obj = grid.jqxGrid("getrowdata", all[x]);
			if (obj) {
				var date = obj["fromDate"];
				contacts.push({
					partyId : obj["partyIdFrom"],
					fromDate : date.getTime()
				});
			}
		}
		return contacts;
	};
	var assignSuccess = function(res) {
		checkDataHasAssigned();
		if (!res["_ERROR_MESSAGE_"] && !res["_ERROR_MESSAGE_LIST_"]) {
			grid.jqxGrid("updatebounddata");
			grid.jqxGrid("clearSelection");
			$("#notifyAssign").notify(uiLabelMap.assignSuccess, {
				position : "left",
				className : "success"
			});
		} else {
			$("#notifyAssign").notify(uiLabelMap.assignError, {
				position : "left",
				className : "error"
			});
		}
	};
	var bindEvent = function() {
		$("#searchCustomer").click(function() {
			reloadConditionGrid();
		});
		$("#autoAssign").click(function() {
			var data = $("#ListResource").jqxGrid("getboundrows");
			if (!data.length) {
				return;
			}
			AssignContact.initJqxSlider();
		});
		grid.on("pagechanged", function() {
			$(this).jqxGrid("clearSelection");
		});
		grid.on("bindingcomplete", function() {
			$(window).resize();
		});
	};
	var init = function() {
		isEditable = isEnableCondition;
		grid = $("#ListResource");
		initElement();
		bindEvent();
		bindTabClick();
	};
	return {
		init : init,
		getEntryDate : getEntryDate,
		setEntryDate : setEntryDate,
		getFromThruDate : getFromThruDate,
		autoAssignContact : autoAssignContact
	};
})();
$(document).ready(function() {
	Assign.init();
	AssignContact.init();
});
if (typeof (CampaignTerms) == "undefined") {
	var CampaignTerms = (function() {
		var save = function() {
			$.ajax({
				url : "saveCampaignTerms",
				data : getValue(),
				type : "POST",
				success : function(res) {
					Loading.hide("loadingMacro");
				},
				beforeSend : function() {
					Loading.show("loadingMacro");
				}
			});
		};
		var load = function(marketingCampaignId) {
			if (marketingCampaignId) {
				$.ajax({
					url : "loadCampaignTerms",
					data : {
						marketingCampaignId : marketingCampaignId
					},
					type : "POST",
					success : function() {
					}
				}).done(function(res) {
					setValue(res["campaignTerms"]);
				});
			}
		};
		var disableTermElements = function(enableButton) {
			$("#DataSource").jqxComboBox({
				disabled : true
			});
			$("#AgeRangeFrom").jqxDateTimeInput({
				disabled : true
			});
			$("#AgeRangeTo").jqxDateTimeInput({
				disabled : true
			});
			$("#entryFrom").jqxDateTimeInput({
				disabled : true
			});
			$("#entryTo").jqxDateTimeInput({
				disabled : true
			});
			$("#Region").jqxDropDownList({
				disabled : true
			});
			$("#ResultEnumId").jqxComboBox({
				disabled : true
			});
			if (!enableButton) {
				$("#searchCustomer").addClass("disabled");
				$("#autoAssign").addClass("disabled");
			}
		};
		var enableTermElements = function(disableButton) {
			$("#DataSource").jqxComboBox({
				disabled : false
			});
			$("#AgeRangeFrom").jqxDateTimeInput({
				disabled : false
			});
			$("#AgeRangeTo").jqxDateTimeInput({
				disabled : false
			});
			$("#entryFrom").jqxDateTimeInput({
				disabled : false
			});
			$("#entryTo").jqxDateTimeInput({
				disabled : false
			});
			$("#Region").jqxDropDownList({
				disabled : false
			});
			$("#ResultEnumId").jqxComboBox({
				disabled : false
			});
			if (!disableButton) {
				$("#searchCustomer").removeClass("disabled");
				$("#autoAssign").removeClass("disabled");
			}
		};
		var getValue = function() {
			var value = new Object();
			value.marketingCampaignId = campaignId;
			value.dataSourceId = $("#DataSource").val();
			var AgeRangeFrom = "";
			$("#AgeRangeFrom").jqxDateTimeInput("getDate") ? AgeRangeFrom = $(
					"#AgeRangeFrom").jqxDateTimeInput("getDate").getTime()
					: AgeRangeFrom;
			value.birthDateFrom = AgeRangeFrom;
			var AgeRangeTo = "";
			$("#AgeRangeTo").jqxDateTimeInput("getDate") ? AgeRangeTo = $(
					"#AgeRangeTo").jqxDateTimeInput("getDate").getTime()
					: AgeRangeTo;
			value.birthDateTo = AgeRangeTo;
			value.areaGeoId = $("#Region").jqxDropDownList("val");
			var results = $("#ResultEnumId").jqxComboBox("getSelectedItems");
			if (results && results.length) {
				var tmp = [];
				for ( var x in results) {
					tmp.push(results[x].value);
				}
				value.resultEnumId = JSON.stringify(tmp);
			}
			var entryDateFrom = "";
			if (Assign.getEntryDate().entryDateFrom) {
				entryDateFrom = Assign.getEntryDate().entryDateFrom;
			}
			value.entryDateFrom = entryDateFrom;
			var entryDateTo = "";
			if (Assign.getEntryDate().entryDateTo) {
				entryDateTo = Assign.getEntryDate().entryDateTo;
			}
			value.entryDateTo = entryDateTo;
			return value;
		};
		var setValue = function(data) {
			if (!isEnableCondition) {
				disableTermElements();
			} else {
				enableTermElements();
			}
			if (_.isEmpty(data)) {
				return;
			}
			if (data.dataSourceId) {
				$("#DataSource").jqxComboBox("val", data.dataSourceId);
			}
			if (data.birthDateFrom && !isNaN(data.birthDateFrom)) {
				$("#AgeRangeFrom").jqxDateTimeInput("setDate",
						new Date((parseInt(data.birthDateFrom))));
			}
			if (data.birthDateTo && !isNaN(data.birthDateTo)) {
				$("#AgeRangeTo").jqxDateTimeInput("setDate",
						new Date((parseInt(data.birthDateTo))));
			}
			$("#Region").jqxDropDownList("val", data.areaGeoId);
			if (data.resultEnumId) {
				if (data.resultEnumId.indexOf("[") != -1) {
					var results = JSON.parse(data.resultEnumId);
					for ( var x in results) {
						$("#ResultEnumId").jqxComboBox("val", results[x]);
					}
				} else {
					$("#ResultEnumId").jqxComboBox("val", data.resultEnumId);
				}
			}
			Assign.setEntryDate(data.entryDateFrom, data.entryDateTo);
			if (!$("#searchCustomer").data("init")) {
				$("#searchCustomer").data("init", true);
				setTimeout(function() {
					$("#searchCustomer").click();
				}, 500);
			}
		};
		return {
			save : save,
			load : load,
			getValue : getValue,
			setValue : setValue,
			disableTermElements : disableTermElements,
			enableTermElements : enableTermElements
		};
	})();
}
if (typeof (AssignContact) == "undefined") {
	var AssignContact = (function() {
		var initJqxElements = function() {
			$("#jqxwindowAssignContact").jqxWindow({
				theme : theme,
				width : 750,
				height : 340,
				resizable : false,
				isModal : true,
				autoOpen : false,
				cancelButton : $("#cancelAssignContact"),
				modalOpacity : 0.7
			});
		};
		var listEmployee = [], totalContact = 0, listEmployeeAssigned = [], contactAssigned = 0, arrayEmployee = [], changeable = new Object(), employeeAndContact = new Object(), maxOfEmployee = new Object();
		var initJqxSlider = function() {
			listEmployee = $("#Employee").jqxComboBox("getSelectedItems");
			totalContact = $("#ListResource").jqxGrid("source").totalrecords;
			if (totalContact == 0) {
				bootbox.alert(uiLabelMap.MustSearchContactFirst);
				return;
			}
			if (_.isEmpty(listEmployee)) {
				bootbox.alert(uiLabelMap.MustChooseEmployeeFirst);
				return;
			}
			$("#autoAssign").attr("disabled", true);
			var htmlContent = "<table class='table-border' style='width:100%'><tr class='table-border'><td class='table-border' style='width: 20%;text-align: center;'>"
					+ uiLabelMap.PersonCommunicate
					+ "</td><td><span style='float: right;margin-right: 10px;'>"
					+ uiLabelMap.quantity + "</span></td></tr>";
			for ( var x in listEmployee) {
				htmlContent += "<tr class='table-border'><td class='table-border' style='text-align: center;'>";
				htmlContent += listEmployee[x].label;
				htmlContent += "</td><td style='padding-top: 10px;' id='td"
						+ listEmployee[x].value
						+ "'><div style='float: right;margin-right: 10px;'>"
						+ "<div id='jqxSlider" + listEmployee[x].value
						+ "' style='float: left;margin-right: 20px;'></div>"
						+ "<input class='xxx' type='number' id='txt"
						+ listEmployee[x].value
						+ "' min=1 style='width: 90px;'/>" + "</div></td></tr>";
				arrayEmployee.push(listEmployee[x].value);
			}
			htmlContent += "</table>";
			$("#divAssignContent").html(htmlContent);
			$("#spTotal").text(totalContact);
			open();
		};
		var handleEvents = function() {
			$("#jqxwindowAssignContact").on(
					"open",
					function(event) {
						for ( var x in arrayEmployee) {
							changeable[arrayEmployee[x]] = false;
							if (arrayEmployee.length == 3) {
								bindEvents(arrayEmployee[x]);
								$("#jqxSlider" + arrayEmployee[x]).jqxSlider({
									theme : theme,
									tooltip : true,
									min : 0,
									max : totalContact,
									value : 100,
									step : 10,
									ticksFrequency : 20,
									rangeSlider : false,
									mode : "fixed",
									width : "400px"
								});
							} else {
								$("#jqxSlider" + arrayEmployee[x]).jqxSlider({
									theme : theme,
									tooltip : true,
									min : 0,
									max : totalContact,
									value : 100,
									step : 10,
									ticksFrequency : 20,
									rangeSlider : false,
									mode : "fixed",
									width : "400px"
								});
								bindEvents(arrayEmployee[x]);
							}
						}
						ModernCalculator.partake(arrayEmployee, totalContact,
								AssignContact.assign);
					});
			$("#jqxwindowAssignContact").on(
					"close",
					function(event) {
						for ( var x in listEmployee) {
							$("#jqxSlider" + listEmployee[x].value).jqxSlider(
									"destroy");
						}
						listEmployeeAssigned = [];
						arrayEmployee = [];
						listEmployee = [];
						totalContact = 0;
						contactAssigned = 0;
						changeable = {};
						employeeAndContact = {};
						maxOfEmployee = {};
						$("#jqxwindowAssignContact").jqxValidator("hide");
					});
			$("#saveAssignContact").click(function() {
				var values = _.values(employeeAndContact);
				var total = 0;
				for ( var x in values) {
					total += values[x];
				}
				if (total > totalContact) {
					bootbox.alert("error");
				}
				CampaignTerms.save();
				Assign.autoAssignContact(employeeAndContact);
				$("#jqxwindowAssignContact").jqxWindow("close");
			});
		};
		var bindEvents = function(id) {
			$("#jqxSlider" + id)
					.on(
							"change",
							function(event) {
								$("#txt" + id).val(event.args.value);
								employeeAndContact[id] = event.args.value;
								listEmployeeAssigned.push(id);
								listEmployeeAssigned = _
										.uniq(listEmployeeAssigned);
								contactAssigned = 0;
								for ( var x in listEmployeeAssigned) {
									contactAssigned += employeeAndContact[listEmployeeAssigned[x]];
								}
								if (contactAssigned > totalContact) {
									var ortherContact = 0;
									for ( var x in listEmployeeAssigned) {
										if (id != listEmployeeAssigned[x]) {
											ortherContact += employeeAndContact[listEmployeeAssigned[x]];
										}
									}
									if (changeable[id]) {
										$("#jqxSlider" + id).jqxSlider(
												"setValue",
												totalContact - ortherContact);
										setTimeout(
												function() {
													ortherContact = 0;
													for ( var x in listEmployeeAssigned) {
														if (id != listEmployeeAssigned[x]) {
															ortherContact += employeeAndContact[listEmployeeAssigned[x]];
														}
													}
													$("#jqxSlider" + id)
															.jqxSlider(
																	"setValue",
																	totalContact
																			- ortherContact);
												}, 300);
									}
								}
							});
			$("#txt" + id)
					.on(
							"change",
							function(event) {
								if ($("#txt" + id).val() > 0) {
									$("#jqxSlider" + id).jqxSlider("setValue",
											$("#txt" + id).val());
								} else {
									$("#txt" + id).val(1);
									$("#jqxSlider" + id).jqxSlider("setValue",
											1);
								}
								employeeAndContact[id] = $("#txt" + id).val()
										.toInt();
								listEmployeeAssigned.push(id);
								listEmployeeAssigned = _
										.uniq(listEmployeeAssigned);
								var contactAssigned = 0;
								for ( var x in listEmployeeAssigned) {
									contactAssigned += employeeAndContact[listEmployeeAssigned[x]];
								}
								if (contactAssigned > totalContact) {
									var ortherContact = 0;
									for ( var x in listEmployeeAssigned) {
										if (id != listEmployeeAssigned[x]) {
											ortherContact += employeeAndContact[listEmployeeAssigned[x]];
										}
									}
									if (changeable[id]) {
										$("#txt" + id).attr("max",
												totalContact - ortherContact);
										setTimeout(
												function() {
													ortherContact = 0;
													for ( var x in listEmployeeAssigned) {
														if (id != listEmployeeAssigned[x]) {
															ortherContact += employeeAndContact[listEmployeeAssigned[x]];
														}
													}
													$("#txt" + id)
															.attr(
																	"max",
																	totalContact
																			- ortherContact);
													$("#txt" + id)
															.val(
																	totalContact
																			- ortherContact);
												}, 300);
									}
								}
							});
			$("#td" + id).mouseenter(function() {
				changeable[id] = true;
			}).mouseleave(function() {
				changeable[id] = false;
			});
		};
		var open = function() {
			$("#jqxwindowAssignContact").jqxWindow("open");
		};
		var assign = function(data) {
			var assignToEmployee = _.keys(data);
			for ( var x in assignToEmployee) {
				$("#jqxSlider" + assignToEmployee[x]).jqxSlider("setValue",
						data[assignToEmployee[x]]);
			}
		};
		var renderSearchResult = function(index, description, value) {
			if (!value.hasOwnProperty("dataSourceId")) {
				return uiLabelMap.NotFound;
			}
			var str = value.dataSourceId;
			return str;
		};
		return {
			init : function() {
				$(window).on("resize", function() {
					$("#DataSource").jqxComboBox("refresh");
				});
				initJqxElements();
				handleEvents();
			},
			initJqxSlider : initJqxSlider,
			assign : assign,
			employeeAndContact : employeeAndContact,
			renderSearchResult : renderSearchResult
		};
	})();
}
if (typeof (ModernCalculator) == "undefined") {
	var ModernCalculator = (function() {
		var partake = function(Employee, totalContact, callback) {
			var result = new Object();
			var medium = Math.floor(totalContact / Employee.length);
			var overbalance = totalContact % Employee.length;
			for ( var x in Employee) {
				if (x == (Employee.length - 1)) {
					result[Employee[x]] = medium + overbalance;
				} else {
					result[Employee[x]] = medium;
				}
			}
			callback(result);
		};
		return {
			partake : partake
		};
	})();
}
