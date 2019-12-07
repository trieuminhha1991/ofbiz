<script src="/crmresources/js/generalUtils.js"></script>

<div id="jqxwindowChangeStaffContract" style="display:none;">
	<div>${uiLabelMap.DmsChangeStaffContract}</div>
	<div>
		
		<div class="row-fluid" style="margin-top: 29px !important;">
			<div class="span5">
				<label class="text-right asterisk">${uiLabelMap.DmsStaffContract}</label>
			</div>
			<div class="span7">
				<div id="divStaffContract">
					<div style="border-color: transparent;" id="jqxgridStaffContract" tabindex="5"></div>
				</div>
			</div>
		</div>
	
		<input type="hidden" id="partyIdAvalible" />
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelChange" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveChange" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<div id="contextMenu" style="display:none;">
	<ul>
		<li id="changeSaler"><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.DmsChangeStaffContract}</li>
	</ul>
</div>

<script>
	$(document).ready(function() {
		ChangeSaler.init();
	});
	if (typeof (ChangeSaler) == "undefined") {
		var ChangeSaler = (function() {
			var partyId;
			var initJqxElements = function() {
				$("#jqxwindowChangeStaffContract").jqxWindow({
					theme: theme, width: 500, maxWidth: 1845, resizable: false,  isModal: true, autoOpen: false,
					cancelButton: $("#cancelChange"), modalOpacity: 0.7
				});
				$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
				var initStaffContractDrDGrid = function(dropdown, grid, width){
					var datafields =
						[
							{ name: "partyId", type: "string" },
							{ name: "firstName", type: "string" },
							{ name: "middleName", type: "string" },
							{ name: "lastName", type: "string" }
						];
					var columns =
						[
							{ text: multiLang.DmsPartyId, datafield: "partyId", width: 150 },
							{ text: multiLang.DmsPartyLastName, datafield: "lastName", width: 150 },
							{ text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 150 },
							{ text: multiLang.DmsPartyFirstName, datafield: "firstName" }
						];
					GridUtils.initDropDownButton({
						url: "JQGetListStaffContract", autorowheight: true, filterable: true, showfilterrow: true, 
						width: width ? width : 600, source: {id: "partyId", pagesize: 5},
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									$("#jqxgridStaffContract").jqxGrid("clearfilters");
									return true;
								}
							}
					}, datafields, columns, null, grid, dropdown, "partyId", function(row){
						var first = row.firstName ? row.firstName : "";
						var mid = row.middleName ? row.middleName : "";
						var last = row.lastName ? row.lastName : "";
						var str = last + " " + mid + " " + first;
						return str;
					});
				};
				initStaffContractDrDGrid($("#divStaffContract"),$("#jqxgridStaffContract"), 600);
				
				$("#contextMenu").jqxMenu({ theme: "olbius", width: 200, height: 30, autoOpenPopup: false, mode: "popup"});
			};
			var handleEvents = function() {
				$("#saveChange").click(function() {
					var salesExecutiveId = Grid.getDropDownValue($("#divStaffContract"));
					DataAccess.execute({
						url: "checkAndUpdateRelSalesExecutive",
						data: {
							partyId: partyId,
							salesExecutiveId: salesExecutiveId}
						}, ChangeSaler.notify);
					$("#jqxwindowChangeStaffContract").jqxWindow("close");
				});
				
				$("#contextMenu").on("itemclick", function (event) {
					var args = event.args;
					var itemId = $(args).attr("id");
					switch (itemId) {
					case "changeSaler":
						var rowIndexSelected = $("#jqxgrid").jqxGrid("getSelectedRowindex");
						var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							partyId = rowData.partyId;
							if (rowData.salerId) {
								Grid.setDropDownValue($("#divStaffContract"), rowData.salerId, rowData.saler);
							} else {
								Grid.cleanDropDownValue($("#divStaffContract"));
							}
							open();
						}
						break;
					default:
						break;
					}
				});
				$("body").on("click", function() {
					$("#contextMenu").jqxMenu("close");
				});
			};
			var open = function() {
				var wtmp = window;
				var tmpwidth = $("#jqxwindowChangeStaffContract").jqxWindow("width");
				$("#jqxwindowChangeStaffContract").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
				$("#jqxwindowChangeStaffContract").jqxWindow("open");
			};
			var notify = function(res) {
				$("#jqxNotificationNested").jqxNotification("closeLast");
				if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
					var errormes = "";
					res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
					$("#jqxNotificationNested").jqxNotification({ template: "error"});
					$("#notificationContentNested").text(errormes);
					$("#jqxNotificationNested").jqxNotification("open");
				} else {
					$("#jqxNotificationNested").jqxNotification({ template: "info"});
					$("#notificationContentNested").text(multiLang.updateSuccess);
					$("#jqxNotificationNested").jqxNotification("open");
					$("#jqxgrid").jqxGrid("updatebounddata");
				}
			};
			return {
				init: function() {
					initJqxElements();
					handleEvents();
				},
				open: open,
				notify: notify
			};
		})();
	}
</script>