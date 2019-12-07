<a style="cursor:pointer;"><span>${uiLabelMap.RecentlyCommunication}:</span> <span id="txtRecently"></span></a>
<div class="pull-right margin-bottom-10" style="margin-right: 5px;">
	<a class="blue fa-refresh" style="cursor:pointer" id="reloadComm">${uiLabelMap.Refresh}</a>
	<a class="blue margin-left10 fa-plus" style="cursor:pointer" id="addMember">${uiLabelMap.DmsAddNewMember}</a>
	<a class="blue margin-left10 fa-check" style="cursor:pointer" id="saveMember" onclick="saveMember()">${uiLabelMap.CommonSave}</a>
</div>
<div id="jqxgridFamily"></div>
<h3 class="title-com">
	${uiLabelMap.CommunicationHistory}
</h3>
<div id="jqxgridCommunicate"></div>

<div id="jqxwindowAddMember" style="display:none;">
	<div id="addMemberTilte">${uiLabelMap.DmsAddNewMember}</div>
	<div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsMemberName}</label></div>
					<div class="span6"><input type="text" id="txtMemberName" style="width: 169px;height: 16px;"/></div>
				</div>
			</div>
			<div class="span5">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsMemberType}</label></div>
					<div class="span5"><div id="txtMemberType"></div></div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsPartyGender}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span6"><div id="txtMemberGender"></div></div>
				</div>
			</div>
			<div class="span5">
				<div class="row-fluid margin-bottom10">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsPartyBirthDate}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span5"><div id="txtMemberBirthDate"></div></div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelEditMember" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveEditMember" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="contextMenuMember" style="display:none;">
	<ul>
		<li id="editMember"><i class="fa-pencil"></i>&nbsp;&nbsp;${uiLabelMap.CommonEdit}</li>
		<li id="deleteMember"><i class="red fa-trash-o"></i><a class="red">&nbsp;&nbsp;${uiLabelMap.CommonDelete}</a></li>
	</ul>
</div>

<script>
	var listRoleType = [<#if listRoleType?exists><#list listRoleType as item>{
		roleTypeId: "${item.roleTypeId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
var roleType = [<#if roleType?exists>{roleTypeId: "${(roleType.roleTypeId)?if_exists}", description: "${StringUtil.wrapString(roleType.get("description", locale))}"}</#if>];
if (typeof (Family) == "undefined") {
	var Family = (function () {
		var initJqxElements = function () {
			
			var columns = [{ text: "${uiLabelMap.PersonCommunicate}", datafield: "firstNameTo", width: 200,
								cellsrenderer: function (row, column, value, a, b, data) {
									var partyIdFrom = data.partyIdFrom;
									var partyIdTo = data.partyIdTo;
									var str = "<div class='custom-cell-grid'>";
									if (partyIdFrom != CookieLayer.getCurrentParty().partyId) {
										var first = data.firstNameFrom;
										var middle = data.middleNameFrom;
										var last = data.lastNameFrom;
										str += last + " " + middle + " " + first;
									} else {
										var first = data.firstNameTo;
										var middle = data.middleNameTo;
										var last = data.lastNameTo;
										str += last + " " + middle + " " + first;
									}
									str += "</div>";
									return str;
								}
							},
							{ text: "${uiLabelMap.CommunicationDirection}", datafield: "direction", width: 100,
								cellsrenderer: function(row, column, value, a, b, data){
									var partyIdFrom = data.partyIdFrom;
									var partyIdTo = data.partyIdTo;
									var str = "<div class='custom-cell-grid'>";
									if (partyIdFrom != CookieLayer.getCurrentParty().partyId) {
										str += "<b>${uiLabelMap.callout}</b>";
									} else {
										str += "${uiLabelMap.callin}";
									}
									str += "</div>";
									return str;
								}
							},
							{ text: "${uiLabelMap.CalledDate}", datafield: "entryDate", width: 150, cellsformat: "HH:mm:ss dd/MM/yyyy", filtertype: "range" },
							{ text: "${uiLabelMap.ResultEnumId}", datafield: "resultEnumTypeId", width: 100 },
							{ text: "${uiLabelMap.ReasonEnumId}",datafield: "reasonEnumCode", width: 100 },
							{ text: "${uiLabelMap.CommunicationEventType}", datafield: "communicationEventTypeId", filtertype: "checkedlist", width: 100,
								cellsrenderer: function (row, column, value) {
									if (typeof(CommEventType) != "undefined") {
										for(var x in CommEventType){
											if(CommEventType[x].communicationEventTypeId==value){
												return "<div class='custom-cell-grid'><b>" + CommEventType[x].description + "</b></div>";
											}
										}
									}
									return "<div class='custom-cell-grid'><b>" + value + "</b></div>";
								}, createfilterwidget: function (column, htmlElement, editor) {
									editor.jqxDropDownList({ autoDropDownHeight: true, source: commmEventType, displayMember: "description", valueMember: "communicationEventTypeId" });
								}
							},
							{ text: "${uiLabelMap.ProductDiscussing}", datafield: "productDiscussedName", width: 200 },
							{ text: "${uiLabelMap.Subject}", datafield: "subjectEnumId", width: 100 },
							{ text: "${uiLabelMap.DAContent}", datafield: "content", width: 200 }];
			
			var datafields = [{ name: "communicationEventId", type: "string" },
							{ name: "entryDate", type: "date", other:"Timestamp", pattern:"HH:mm:ss dd/MM/yyyy" },
							{ name: "communicationEventTypeId", type: "string" },
							{ name: "direction", type: "string" },
							{ name: "partyIdFrom", type: "string" },
							{ name: "firstNameFrom", type: "string" },
							{ name: "middleNameFrom", type: "string" },
							{ name: "lastNameFrom", type: "string" },
							{ name: "partyIdTo", type: "string" },
							{ name: "middleNameTo", type: "string" },
							{ name: "lastNameTo", type: "string" },
							{ name: "firstNameTo", type: "string" },
							{ name: "groupNameTo", type: "string" },
							{ name: "groupNameFrom", type: "string" },
							{ name: "content", type: "string" },
							{ name: "subject", type: "string" },
							{ name: "subjectEnumId", type: "string" },
							{ name: "reasonEnumCode", type: "string" },
							{ name: "resultEnumTypeId", type: "string" },
							{ name: "communicationEventTypeId", type: "string" },
							{ name: "currentBrandName", type: "string" },
							{ name: "productDiscussedId", type: "string" },
							{ name: "productDiscussedName", type: "string" },
							{ name: "currentProductName", type: "string" }];
			
			Grid.initGrid({
							url: "JqxGetListCommunication&partyId=",
							width: "100%",
							autorowheight: true,
							showtoolbar:false,
							virtualmode: true,
							autoheight: true,
							source: {pagesize: 5}
							}, datafields, columns, null, $("#jqxgridCommunicate"));
			
			
			var sourceFamily = {
					datatype: "json",
					datafields:
						[{ name: "familyId", type: "string" },
						{ name: "partyId", type: "string" },
						{ name: "partyFullName", type: "string" },
						{ name: "gender", type: "string" },
						{ name: "roleTypeFrom", type: "string" },
						{ name: "roleTypeIdFrom", type: "string" },
						{ name: "birthDate", type: "date", other: "Date" },
						{ name: "currentBrandName", type: "string" },
						{ name: "currentProductName", type: "string" },
						{ name: "previousBrandName", type: "string" },
						{ name: "previousProductName", type: "string" },
						{ name: "currentProductId", type: "string" },
						{ name: "currentBrandId", type: "string" }],
					id: "partyId",
					url: "jqxGetChildrenInFamily?includeRepresentative=true&partyId="
			};
			var dataAdapterFamily = new $.jqx.dataAdapter(sourceFamily);
			$("#jqxgridFamily").jqxGrid({
				width: "100%",
				localization: getLocalization(),
				source: dataAdapterFamily,
				columnsresize: true,
				pageable: false,
				autoheight: true,
				showdefaultloadelement: false,
				autoshowloadelement: false,
				autorowheight: true,
				columns: [
						{ text: multiLang.DmsMemberName, datafield: "partyFullName", minWidth: 200 },
						{ text: multiLang.DmsMemberType, datafield: "roleTypeFrom", width: 100 },
						{ text: multiLang.DmsPartyGender, datafield: "gender", width: 80,
							cellsrenderer: function (row, column, value) {
								value?value=mapGender[value]:value;
								return "<div style=margin:4px;>" + value + "</div>";
							}
						},
						{ text: multiLang.DmsPartyBirthDate, datafield: "birthDate", cellsformat: "dd/MM/yyyy", width: 200, filtertype: "range", columntype: "datetimeinput",
							cellsrenderer: function (row, column, value) {
								value?value=new Date(value).toTimeOlbius()+getPersonAge(value):value;
								return "<div style=margin:4px;>" + value + "</div>";
							}
						},
						{ text: "${StringUtil.wrapString(uiLabelMap.CurrentBrandUsing)}", datafield: "currentBrandName", width: 150 },
						{ text: "${StringUtil.wrapString(uiLabelMap.CurrentProductUsing)}", datafield: "currentProductName", width: 150 },
						{ text: "${StringUtil.wrapString(uiLabelMap.PreviousBrandUsing)}", datafield: "previousBrandName", width: 150 },
						{ text: "${StringUtil.wrapString(uiLabelMap.PreviousProductUsing)}", datafield: "previousProductName", width: 150 }]
			});
			$("#jqxwindowAddMember").jqxWindow({ theme: theme,
				width: 700, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#cancelEditMember"), modalOpacity: 0.7
			});
			$("#contextMenuMember").jqxMenu({ width: 180, height: 58, autoOpenPopup: false, mode: "popup"});
			$("#txtMemberType").jqxDropDownList({ theme: theme, width: 180, dropDownHeight: 200, height: 25, source: listRoleType,
				displayMember: "description", valueMember: "roleTypeId", placeHolder: multiLang.filterchoosestring, filterable: true});
			var listGender = [{value: "M", label: multiLang.male}, {value: "F", label: multiLang.female}];
			$("#txtMemberGender").jqxDropDownList({ theme: theme, width: 180, height: 25, source: listGender, displayMember: "label", valueMember: "value", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true});
			$("#txtMemberBirthDate").jqxDateTimeInput({theme: theme, width: 180 });
			$("#txtMemberBirthDate").jqxDateTimeInput("val", null);
		};
		var handleEvents = function () {
			$("#jqxgridFamily").on("contextmenu", function () {
				return false;
			});
			$("#jqxgridFamily").on("rowclick", function (event) {
				if (event.args.rightclick) {
					$("#jqxgridFamily").jqxGrid("clearSelection");
					$("#jqxgridFamily").jqxGrid("selectrow", event.args.rowindex);
					var scrollTop = $(window).scrollTop();
					var scrollLeft = $(window).scrollLeft();
					$("#contextMenuMember").jqxMenu("open", parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
					return false;
				}
			});
			$("#jqxgridFamily").on("bindingcomplete", function(){
				var source = $(this).jqxGrid("source");
				var data = source.records;
				for ( var x in data) {
					if (data[x].roleTypeIdFrom == "REPRESENTATIVE") {
						currentBrandId = data[x].currentBrandId;
						currentProductId = data[x].currentProductId;
						var pr = $("#PreviousBrandUsing");
						var cu = $("#CurrentBrandUsing");
						if(pr.length){
							pr.jqxDropDownList("val", currentBrandId);
						}
						if(cu.length){
							cu.jqxDropDownList("val", currentBrandId);
						}
					}
				}
			});
			$("#jqxgridCommunicate").on("bindingcomplete", function(){
				var dataDetails = $("#jqxgridCommunicate").jqxGrid("getboundrows");
				if (!_.isEmpty(dataDetails)) {
					if (dataDetails[0].entryDate) {
						$("#txtRecently").text(dataDetails[0].entryDate.toTimeDateOlbius());
					}
				}
			});
			$("#contextMenuMember").on("shown", function () {
				var rowIndex = $("#jqxgridFamily").jqxGrid("getSelectedRowindex");
				var rowData = $("#jqxgridFamily").jqxGrid("getrowdata", rowIndex);
				if (rowData.roleTypeIdFrom == "REPRESENTATIVE") {
					$("#contextMenuMember").jqxMenu("disable", "editMember", true);
					$("#contextMenuMember").jqxMenu("disable", "deleteMember", true);
				} else {
					$("#contextMenuMember").jqxMenu("disable", "editMember", false);
					$("#contextMenuMember").jqxMenu("disable", "deleteMember", false);
				}
			});
			$("#contextMenuMember").on("itemclick", function (event) {
				var args = event.args;
				var itemId = $(args).attr("id");
				switch (itemId) {
				case "editMember":
					UpdateMember = true;
					editMember();
					break;
				case "deleteMember":
					deleteMember();
					break;
				default:
					break;
				}
			});
			$("#addMember").on("click", function() {
				$("#addMemberTilte").text("${StringUtil.wrapString(uiLabelMap.DmsAddNewMember)}");
				openPopupAddMember();
				UpdateMember = false;
			});
			$("#jqxwindowAddMember").on("close", function (event) {
				$("#jqxwindowAddMember").jqxValidator("hide");
				setTimeout(function() {
					$("#txtMemberName").val("");
					$("#txtMemberType").jqxDropDownList("clearSelection");
					$("#txtMemberGender").jqxDropDownList("clearSelection");
					$("#txtMemberBirthDate").jqxDateTimeInput("val", null);
				}, 1000);
			});
			$("#saveEditMember").click(function () {
				if ($("#jqxwindowAddMember").jqxValidator("validate")) {
					var rowId = $("#jqxwindowAddMember").attr("rowId");
					if (rowId) {
						$("#jqxwindowAddMember").attr("rowId", "");
						$("#jqxgridFamily").jqxGrid("updaterow", rowId, getValuePopupAddMember());
					} else {
						$("#jqxgridFamily").jqxGrid("addrow", null, getValuePopupAddMember());
					}
					$("#jqxwindowAddMember").jqxWindow("close");
				}
			});
			$("#reloadComm").click(function(){
				updateGridFamily();
			});
		};
		var updateGridFamily = function() {
			$("#jqxgridFamily").jqxGrid("updatebounddata");
			$("#jqxgridCommunicate").jqxGrid("updatebounddata");
		};
		var openPopupAddMember = function () {
			var wtmp = window;
			var tmpwidth = $("#jqxwindowAddMember").jqxWindow("width");
			$("#jqxwindowAddMember").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 120 }, height: 200 });
			$("#jqxwindowAddMember").jqxWindow("open");
		};
		var getValuePopupAddMember = function () {
			var value = new Object();
			value.familyId = globalFamilyId;
			value.partyFullName = $("#txtMemberName").val();
			if ($("#txtMemberType").jqxDropDownList("getSelectedItem")) {
				value.roleTypeIdFrom = $("#txtMemberType").jqxDropDownList("getSelectedItem").value;
				value.roleTypeFrom = $("#txtMemberType").jqxDropDownList("getSelectedItem").label;
			}
			if ($("#txtMemberGender").jqxDropDownList("getSelectedItem")) {
				value.gender = $("#txtMemberGender").jqxDropDownList("getSelectedItem").value;
			}
			value.birthDate = $("#txtMemberBirthDate").jqxDateTimeInput("getDate");
			return value;
		};
		var setValuePopupAddMember = function (data) {
			$("#jqxwindowAddMember").attr("rowId", data.uid);
			$("#txtMemberName").val(data.partyFullName);
			$("#txtMemberType").jqxDropDownList("val", data.roleTypeIdFrom);
			$("#txtMemberGender").jqxDropDownList("val", data.gender);
			data.birthDate?data.birthDate=new Date(data.birthDate):data.birthDate;
			$("#txtMemberBirthDate").jqxDateTimeInput("setDate", data.birthDate);
		};
		var editMember = function () {
			var rowIndex = $("#jqxgridFamily").jqxGrid("getSelectedRowindex");
			var rowData = $("#jqxgridFamily").jqxGrid("getrowdata", rowIndex);
			if (rowData.roleTypeIdFrom == "REPRESENTATIVE") {
				$("#txtMemberType").jqxDropDownList({ source: roleType });
			} else {
				$("#txtMemberType").jqxDropDownList({ source: listRoleType });
			}
			setValuePopupAddMember(rowData);
			$("#addMemberTilte").text("${StringUtil.wrapString(uiLabelMap.CommonEdit)}");
			openPopupAddMember();
		};
		var deleteMember = function () {
			bootbox.confirm(multiLang.ConfirmDeleteMember, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
				if (result) {
					var rowIndex = $("#jqxgridFamily").jqxGrid("getSelectedRowindex");
					var rowData = $("#jqxgridFamily").jqxGrid("getrowdata", rowIndex);
					$("#jqxgridFamily").jqxGrid("deleterow", rowData.uid);
				}
			});
		};
		var initValidator = function () {
			$("#jqxwindowAddMember").jqxValidator({
				rules:
					[{ input: "#txtMemberName", message: multiLang.fieldRequired, action: "change, blur", rule: "required"},
					{ input: "#txtMemberType", message: multiLang.fieldRequired, action: "change",
						rule: function (input, commit) {
							var value = input.val();
							if (value) {
								return true;
							}
							return false;
						}
					}],
					scroll: false
			});
		};
		var getPersonAge = function (birthDate) {
			var birthYear = new Date(birthDate).getFullYear();
			var currentYear = new Date().getFullYear();
			var partyAge = currentYear - birthYear;
			if (partyAge < 0) {
				return "";
			} else if (partyAge < 2) {
				var birthMonth = new Date(birthDate).getMonth();
				var currentMonth = new Date().getMonth();
				partyAge = currentMonth - birthMonth + 1 + partyAge*12;
				return "<span class='green'> (" + partyAge + ") " + multiLang.DmsMonths;
			}
			partyAge += 1;
			return "<span class='green'> (" + partyAge + ") " + multiLang.age;
		};
		var getValue = function () {
			var data = $("#jqxgridFamily").jqxGrid("getrows");
			for ( var x in data) {
				data[x].birthDate?data[x].birthDate=new Date(data[x].birthDate).getTime():data[x].birthDate;
			}
			return JSON.stringify(data);
		};
		var setValue = function (partyId) {
			var adapter = $("#jqxgridFamily").jqxGrid("source");
			var adapter2 = $("#jqxgridCommunicate").jqxGrid("source");
			var open = function(){
				if (adapter) {
					adapter.url = "jqxGetChildrenInFamily?includeRepresentative=true&partyId=" + partyId;
					adapter._source.url = "jqxGetChildrenInFamily?includeRepresentative=true&partyId=" + partyId;
					$("#jqxgridFamily").jqxGrid("source", adapter);
				}
				if (adapter2) {
					adapter2.url = "jqxGeneralServicer?sname=JqxGetListCommunication&partyId=" + partyId;
					adapter2._source.url = "jqxGeneralServicer?sname=JqxGetListCommunication&partyId=" + partyId;
					$("#jqxgridCommunicate").jqxGrid("source", adapter2);
				}
			};
			if ((!adapter || !adapter2) && partyId) {
				$("#memberUsingCollapse").collapse("show");
				setTimeout(function(){
					adapter = $("#jqxgridFamily").jqxGrid("source");
					adapter2 = $("#jqxgridCommunicate").jqxGrid("source");
					open();
				}, 100);
			} else {
				open();
			}
		};
		return {
			init: function () {
				initJqxElements();
				handleEvents();
				initValidator();
			},
			getValue: getValue,
			setValue: setValue,
			updateGridFamily: updateGridFamily,
			getPersonAge: getPersonAge
		};
	})();
}
var UpdateMember = true;
function saveMember() {
	var result;
	if (CreateMode) {
		result = Processor.saveCustomer();
	} else {
		result = DataAccess.execute({
			url: "saveMember",
			data: {children: Family.getValue(),
				representativeMemberId: CookieLayer.getCurrentParty().partyId,
				familyId: globalFamilyId}
			}, Family.updateGridFamily);
	}
	if (result) {
		if (UpdateMember) {
			UpdateMember = false;
			$("#saveMember").notify(multiLang.updateSuccess, { position: "left", className: "success" });
		} else {
			UpdateMember = true;
			$("#saveMember").notify(multiLang.addSuccess, { position: "left", className: "success" });
			RepresentativeMember.bindDataToDropdown(CookieLayer.getCurrentParty().partyId);
		}
	} else {
		$("#saveMember").notify(multiLang.updateError, { position: "left", className: "error" });
	}
}
</script>