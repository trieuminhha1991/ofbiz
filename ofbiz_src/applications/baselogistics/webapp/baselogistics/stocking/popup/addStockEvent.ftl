<script>
	var userLoginPartyId = "${userLogin.partyId}";
</script>
<div id="addStockEvent" class="hide popup-bound" data-update=false data-eventId=null>
	<div id="addStockEvent-title">${uiLabelMap.CommonAdd}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid margin-top10">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsStockEventName}</label></div>
				<div class="span8"><input type="text" id="txtEventName" style="width: 96.5%;" /></div>
			</div>
			<div class="row-fluid margin-top10">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.Facility}</label></div>
				<div class="span8"><div id="txtFacility"></div></div>
			</div>
			
			<div class="row-fluid margin-top10 <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>hide</#if>">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsInputEmpl}</label></div>
				<div class="span8"><div id="txtStockInput"></div></div>
			</div>
			<div class="row-fluid margin-top10 <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>hide</#if>">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsCountEmpl}</label></div>
				<div class="span8"><div id="txtStockCount"></div></div>
			</div>
			<div class="row-fluid margin-top10 <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>hide</#if>">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsSCanEmpl}</label></div>
				<div class="span8"><div id="txtStockScan"></div></div>
			</div>
			<div class="row-fluid margin-top10 <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>hide</#if>">
				<div class="span4"><label class="text-right asterisk">${uiLabelMap.DmsCheckEmpl}</label></div>
				<div class="span8"><div id="txtStockCheck"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancel" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
			<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script>
$(document).ready(function() {
	AddStockEvent.init();
});
if (typeof (AddStockEvent) == "undefined") {
	var AddStockEvent = (function() {
		var jqxwindow, mainGrid, facilityStocked = ${StringUtil.wrapString(facilityStocked!"[]")};
		var initJqxElements = function() {
			jqxwindow.jqxWindow({ theme : theme, width : 800, height : <#if hasOlbPermission("MODULE", "LOGISTICS", "VIEW")>450<#else>200</#if>, resizable : false, isModal : true,
				autoOpen : false, cancelButton : $("#btnCancel"), modalOpacity : 0.7 });
			$("#txtFacility").jqxDropDownList({
				theme: theme, source: ${StringUtil.wrapString(facilities!"[]")}, displayMember: "text", valueMember: "value", width: '99%', height: 28,
				placeHolder: multiLang.filterchoosestring
			});
			
			$("#txtStockInput").jqxComboBox({ source: getEmployee("LOG_STOCK_INPUT"), displayMember: "partyName", valueMember: "partyId", width: '99%', height: 50,
				theme: theme, multiSelect: true });
			$("#txtStockCount").jqxComboBox({ source: getEmployee("LOG_STOCK_COUNT"), displayMember: "partyName", valueMember: "partyId", width: '99%', height: 50,
				theme: theme, multiSelect: true });
			$("#txtStockScan").jqxComboBox({ source: getEmployee("LOG_STOCK_SCAN"), displayMember: "partyName", valueMember: "partyId", width: '99%', height: 50,
				theme: theme, multiSelect: true });
			$("#txtStockCheck").jqxComboBox({ source: getEmployee("LOG_STOCK_CHECK"), displayMember: "partyName", valueMember: "partyId", width: '99%', height: 50,
				theme: theme, multiSelect: true });
		};
		var handleEvents = function() {
			$("#btnSave").click(function() {
				if (jqxwindow.jqxValidator("validate")) {
					Loading.show();
					DataAccess.execute({
						url: jqxwindow.data("update")?"updateStockEventAndRole":"createStockEventAndRole",
						data: getValue()
					}, AddStockEvent.notify);
				}
			});
			jqxwindow.on("open", function() {
				$("#txtStockInput").jqxComboBox("refresh");
				$("#txtStockCount").jqxComboBox("refresh");
				$("#txtStockScan").jqxComboBox("refresh");
				$("#txtStockCheck").jqxComboBox("refresh");
				if (jqxwindow.data("update")) {
					$("#addStockEvent-title").text("${StringUtil.wrapString(uiLabelMap.CommonUpdate)}");
					for ( var x in facilityStocked) {
						$("#txtFacility").jqxDropDownList("enableItem", facilityStocked[x]);
					}
					
				} else {
					$("#addStockEvent-title").text("${StringUtil.wrapString(uiLabelMap.CommonAdd)}");
					for ( var x in facilityStocked) {
						$("#txtFacility").jqxDropDownList("disableItem", facilityStocked[x]);
					}			
				}
			});
			jqxwindow.on("close", function() {
				jqxwindow.jqxValidator("hide");
				$("#txtFacility").jqxDropDownList({ disabled: false });
				$("#txtFacility").jqxDropDownList("clearSelection");
				$("#txtStockInput").jqxComboBox("clearSelection");
				$("#txtStockCount").jqxComboBox("clearSelection");
				$("#txtStockScan").jqxComboBox("clearSelection");
				$("#txtStockCheck").jqxComboBox("clearSelection");
				$("#txtEventName").val("");
				jqxwindow.data("update", false);
				jqxwindow.data("eventId", null);
			});
		};
		var initValidator = function() {
			jqxwindow.jqxValidator(
					{
						rules :
						[
							{ input: "#txtEventName", message: multiLang.fieldRequired, action: "keyup, blur", rule: "required" },
                            { input: "#txtEventName", message: multiLang.containSpecialSymbol, action: "change",
                                rule: function (input, commit) {
                                    var value = input.val();
                                    if(checkSpecialCharacters(value)) {
                                        return true;
                                    }
                                    return false;
                                }
                            },
							{ input: "#txtFacility", message: multiLang.fieldRequired, action: "change",
								rule: function (input, commit) {
									return !!input.val();
								}
							},
							<#if hasOlbPermission("MODULE", "LOGISTICS", "VIEW")>
								{ input: "#txtStockInput", message: multiLang.fieldRequired, action: "change",
									rule: function (input, commit) {
										return !!input.val();
									}
								},
								{ input: "#txtStockCount", message: multiLang.fieldRequired, action: "change",
									rule: function (input, commit) {
										return !!input.val();
									}
								},
								{ input: "#txtStockScan", message: multiLang.fieldRequired, action: "change",
									rule: function (input, commit) {
										return !!input.val();
									}
								},
								{ input: "#txtStockCheck", message: multiLang.fieldRequired, action: "change",
									rule: function (input, commit) {
										return !!input.val();
									}
								}
							</#if>
						],
						scroll : false
					});
		};
		var getEmployee = function(emplPositionTypeId) {
			var source =
			{
				datatype: "json",
				datafields: [
						{ name: "partyId" },
						{ name: "partyName" }
				],
				url: "getEmployeeByPositionType?emplPositionTypeId=" + emplPositionTypeId,
				async: true
			};
			return new $.jqx.dataAdapter(source);
		};
		var getValue = function() {
			var dis = [];
			dis.push(userLoginPartyId);
			var value = {
				eventId: jqxwindow.data("eventId"),
				eventName: $("#txtEventName").val(),
				facilityId: $("#txtFacility").jqxDropDownList("val"),
				
				stockInputIds: <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>dis<#else>_.pluck($("#txtStockInput").jqxComboBox("getSelectedItems"), "value")</#if>,
				stockCountIds: <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>dis<#else>_.pluck($("#txtStockCount").jqxComboBox("getSelectedItems"), "value")</#if>,
				stockScanIds: <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>dis<#else>_.pluck($("#txtStockScan").jqxComboBox("getSelectedItems"), "value")</#if>,
				stockCheckIds: <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>dis<#else>_.pluck($("#txtStockCheck").jqxComboBox("getSelectedItems"), "value")</#if>
			};
			return value;
		};
		var setValue = function(data) {
			if (data) {
				$("#txtEventName").val(data.eventName);
				for ( var x in data.stockInputIds) {
					$("#txtStockInput").jqxComboBox("selectItem", data.stockInputIds[x].partyId);
				}
				for ( var x in data.stockCountIds) {
					$("#txtStockCount").jqxComboBox("selectItem", data.stockCountIds[x].partyId);
				}
				for ( var x in data.stockScanIds) {
					$("#txtStockScan").jqxComboBox("selectItem", data.stockScanIds[x].partyId);
				}
				for ( var x in data.stockCheckIds) {
					$("#txtStockCheck").jqxComboBox("selectItem", data.stockCheckIds[x].partyId);
				}
				setTimeout(function() {
					$("#txtFacility").jqxDropDownList("val", data.facilityId);
					$("#txtFacility").jqxDropDownList({ disabled: true });
				}, 100);
			}
		};
		var open = function(eventId, facilityId, eventName) {
			if (eventId) {
				jqxwindow.data("update", true);
				jqxwindow.data("eventId", eventId);
				var wtmp = window;
				var tmpwidth = jqxwindow.jqxWindow("width");
				jqxwindow.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
				jqxwindow.jqxWindow("open");
				
				var stockInputIds = DataAccess.getData({
					url: "loadPartiesInEventByRole",
					data: { eventId: eventId, roleTypeId: "STOCKING_INPUT" },
					source: "parties"});
				var stockCountIds = DataAccess.getData({
					url: "loadPartiesInEventByRole",
					data: { eventId: eventId, roleTypeId: "STOCKING_COUNT" },
					source: "parties"});
				var stockScanIds = DataAccess.getData({
					url: "loadPartiesInEventByRole",
					data: { eventId: eventId, roleTypeId: "STOCKING_SCAN" },
					source: "parties"});
				var stockCheckIds = DataAccess.getData({
					url: "loadPartiesInEventByRole",
					data: { eventId: eventId, roleTypeId: "STOCKING_CHECK" },
					source: "parties"});
				AddStockEvent.setValue({ eventName: eventName, facilityId: facilityId, stockInputIds: stockInputIds, stockCountIds: stockCountIds, stockScanIds: stockScanIds, stockCheckIds: stockCheckIds });
			}
		};
		var notify = function(res) {
			Loading.hide();
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
				Grid.renderMessage(mainGrid.attr("id"), multiLang.updateError, {
					autoClose : true,
					template : "error",
					appendContainer : "#container" + mainGrid.attr("id"),
					opacity : 0.9
				});
			} else {
				Grid.renderMessage(mainGrid.attr("id"), multiLang.updateSuccess, {
					autoClose : true,
					template : "info",
					appendContainer : "#container" + mainGrid.attr("id"),
					opacity : 0.9
				});
				facilityStocked.push($("#txtFacility").jqxDropDownList("val"));
				jqxwindow.jqxWindow("close");
				mainGrid.jqxGrid("updatebounddata");
			}
		};
		return {
			init : function() {
				jqxwindow = $("#addStockEvent");
				mainGrid = $("#jqxgridStockEvents");
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			setValue: setValue,
			open: open,
			notify: notify
		};
	})();
}
if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
uiLabelMap.BLSpecialCharacterCheck = "${StringUtil.wrapString(uiLabelMap.BLSpecialCharacterCheck)}";
var checkSpecialCharacters = function(value) {
    if (OlbCore.isNotEmpty(value)) {
        var regexCheck = new RegExp(uiLabelMap.BLSpecialCharacterCheck);
        if(regexCheck.test(value)){
            return true;
        }
    }
    return false;
};
</script>