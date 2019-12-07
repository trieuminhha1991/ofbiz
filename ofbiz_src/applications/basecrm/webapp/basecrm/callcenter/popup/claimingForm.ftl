<div id="issueFormWindow">
	<div class="form-window-container">
		<div class="form-window-content">
			<div id="claimNotify" style="position: absolute; right: 0">&nbsp;</div>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.CommunicationDirection}</label>
						</div>
						<div class="span7">
							<div id="directionComm"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.ResultEnumId}</label>
						</div>
						<div class="span7">
							<div id="reasonTypeEnumId"></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.DmsStatus}</label>
						</div>
						<div class="span7">
							<div id="divStatus"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.ReasonEnumId}</label>
						</div>
						<div class="span7">
							<div id="reasonEnumId"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label>${uiLabelMap.ProductDiscussing}</label>
						</div>
						<div class="span7">
							<div id="productDiscussing">
								<div style="border-color: transparent;" id="jqxgridProductDiscussing"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label>${uiLabelMap.NextCallSchedule}&nbsp;<i class="schedule-asterisk asterisk" style="display: none"></i></label>
						</div>
						<div class="span7">
							<div id="NextCallSchedule"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="">${uiLabelMap.Subject}</label>
						</div>
						<div class="span7">
							<div id="subject"></div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right">
							<label class="asterisk">${uiLabelMap.CommunicationEventType}</label>
						</div>
						<div class="span7">
							<div id="CommunicationEventType"></div>
						</div>
					</div>
				</div>
			</div>

			<div class="row-fluid" id="contentContainer">
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<div class="span5 text-algin-right" id="contentLabel">
							<label class="">${uiLabelMap.DAContent}</label>
						</div>
						<div class="span7 relative">
							<textarea rows="2" cols="50" id="issueContent" style="resize: none;margin-top: 0px !important;width: 271%;"></textarea>
						</div>
					</div>
				</div>
			</div>
			<div style="width: 99%;height: 33px;">
				<button id="cancelCreateIssue" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="createIssue" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<script>
	var reasonType = [<#if reasonEnums?exists><#list reasonEnums as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var reasonContacted = [<#if reasonContacted?exists><#list reasonContacted as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var reasonUnContacted = [<#if reasonUnContacted?exists><#list reasonUnContacted as reason>{enumTypeId: "${reason.enumTypeId}", description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var listRCTypes = [<#if listRCType?exists><#list listRCType as rCType>{enumId: "${rCType.enumId}", description: "${StringUtil.wrapString(rCType.description)?default("")}"},</#list></#if>];
	var subjects = [<#if subjects?exists><#list subjects as rCType>{enumId: "${rCType.enumId}", sequenceId: "${rCType.sequenceId?if_exists}", description: "${StringUtil.wrapString(rCType.description)?default("")}"},</#list></#if>];
	var brands = [<#if brands?exists><#list brands as rCType>{partyId: "${rCType.partyId}", groupName: "${StringUtil.wrapString(rCType.groupName)?default("")}"},</#list></#if>];
	var CommEventType = [<#if communicationEventTypes?exists><#list communicationEventTypes as eventType>{communicationEventTypeId: "${eventType.communicationEventTypeId}", description: "${StringUtil.wrapString(eventType.description)?default("")}"},</#list></#if>];
	var genderData = [{
		gender: "M",
		description : "${uiLabelMap.Male}"
	}, {
		gender : "F",
		description : "${uiLabelMap.Female}"
	}];
	var reasons = _.union(reasonType, reasonContacted, reasonUnContacted);
	var currentBrandId, currentProductId;
	//add issue for each customer
	var issueObj = (function() {
		var issueForm;
		var theme = theme;
		var initElementIssue = function(){
			var width = "95%";
			var tmp = [{
				value : "send",
				description: "${uiLabelMap.callout}"
			}, {
				value : "receive",
				description: "${uiLabelMap.callin}"
			}];
			$("#directionComm").jqxDropDownList({
				theme: theme,
				source: tmp,
				displayMember: "description",
				valueMember: "value",
				autoDropDownHeight: true,
				width: width,
				height: "25",
				selectedIndex: 0,
				placeHolder: multiLang.filterchoosestring
			});
			$("#divStatus").jqxDropDownList({
				theme: theme,
				source: reasonType,
				displayMember: "description",
				valueMember: "enumTypeId",
				dropDownHorizontalAlignment: "right",
				width: width,
				dropDownWidth: 400,
				autoDropDownHeight: true,
				height: "25",
				renderer: function (index, label, value) {
					if ($("#directionComm").val() == "receive") {
						seq = "[COMM_INBOUND_RESULT] ";
					} else {
						var da;
						for ( var x in reasons) {
							if (reasons[x].enumTypeId == value) {
								da = reasons[x];
								break;
							}
						}
						if (da) {
							var seq = da.enumTypeId ? "[" + da.enumTypeId + "] " : "";
						}
					}
					var valueStr = seq?seq+label:label;
					return valueStr;
				},
				placeHolder: multiLang.filterchoosestring
			});
			$("#reasonTypeEnumId").jqxDropDownList({
				theme: theme,
				source: [],
				displayMember: "enumTypeId",
				valueMember: "enumTypeId",
				dropDownHorizontalAlignment: "right",
				width: width,
				dropDownWidth: 400,
				dropDownHeight: 200,
				autoDropDownHeight: true,
				height: "25",
				renderer: function (index, label, value) {
					var source = $("#reasonTypeEnumId").jqxDropDownList("source");
					if (source) {
						var da;
						for ( var x in source) {
							if (source[x].enumTypeId == value) {
								da = source[x];
								break;
							}
						}
						if (da) {
							var seq = da.description ? da.description : "";
						}
					}
					var valueStr = "[" + value + "]" + seq;
					return valueStr;
				},
				placeHolder: multiLang.filterchoosestring
			});
			$("#subject").jqxDropDownList({
				selectedIndex: 0,
				source: subjects,
				valueMember: "enumId",
				displayMember: "description",
				autoDropDownHeight: true,
				dropDownHorizontalAlignment: "right",
				width: width,
				placeHolder: multiLang.filterchoosestring
			});
			$("#CommunicationEventType").jqxDropDownList({
				filterable: true,
				source: CommEventType,
				valueMember: "communicationEventTypeId",
				displayMember: "description",
				dropDownHorizontalAlignment: "right",
				width: width,
				dropDownHeight: "200px",
				placeHolder: multiLang.filterchoosestring
			});
			setTimeout(function(){
				initCommunicationEventType();
			}, 100);
			$("#reasonEnumId").jqxDropDownList({
				selectedIndex: 0,
				source: [],
				valueMember: "enumId",
				displayMember: "enumCode",
				autoDropDownHeight: true,
				dropDownHorizontalAlignment: "right",
				width: width,
				filterable: true,
				placeHolder: multiLang.filterchoosestring
			});
			$("#ClaimPartyId").jqxDropDownList({
				width: width,
				source: [],
				displayMember: "partyFullName",
				valueMember: "partyId",
				placeHolder: "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}",
				autoDropDownHeight: true,
			});
			$("#NextCallSchedule").jqxDateTimeInput({
				width: "95%",
				height: 25,
				theme: theme,
				formatString: "HH:mm:ss dd/MM/yyyy",
				showFooter: true,
				todayString: multiLang.today,
				clearString: multiLang.clear,
				min: new Date((new Date().getTime() - 86400000))
			});
			$("#NextCallSchedule").jqxDateTimeInput("setDate", null);
			initProductDiscuss();
		};
		var initCommunicationEventType = function() {
			for (var i in CommEventType) {
				if(CommEventType[i].communicationEventTypeId == "PHONE_COMMUNICATION"){
					$("#CommunicationEventType").jqxDropDownList("selectedIndex", i);
					break;
				}
			}
		};
		var initProductDiscuss = function() {
			var initProductDiscussingDrDGrid = function(dropdown, grid, width){
				var datafields =
					[{ name: "productId", type: "string" },
					 { name: "productCode", type: "string" },
					 { name: "productName", type: "string" }];
				var columns =
					[{text: "${uiLabelMap.ProductProductId}", datafield: "productCode", width: 200},
					 {text: "${uiLabelMap.ProductProductName}", datafield: "productName"}];
				GridUtils.initDropDownButton({url: "JQGetListProductDiscuss", filterable: true, showfilterrow: true, width: width ? width : 600, source: {id: "productId", pagesize: 5},
						handlekeyboardnavigation: function (event) {
							var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
							if (key == 70 && event.ctrlKey) {
								$("#jqxgridProductDiscussing").jqxGrid("clearfilters");
								return true;
							}
					 	}, clearOnClose: "Y", dropdown: {dropDownHorizontalAlignment: "left", width: 161}
				}, datafields, columns, null, grid, dropdown, "productId", "productName");
			};
			initProductDiscussingDrDGrid($("#productDiscussing"), $("#jqxgridProductDiscussing"), 600);
		}
		var initIssueWindow = function(popup) {
			popup.jqxWindow({
				width : 750,
				height : 470,
				resizable : false,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				cancelButton: "#cancelCreateIssue",
				theme : theme,
				initContent: function(){
					initElementIssue();
				}
			});
		};
		var bindEvent = function() {
			$("#directionComm").on("change", function (event){     
				$("#divStatus").jqxDropDownList("clearSelection");
				$("#reasonTypeEnumId").jqxDropDownList("clearSelection");
				$("#reasonTypeEnumId").jqxDropDownList({source: [] });
				var args = event.args;
				if (args) {
					var index = args.index;
					var item = args.item;
					var value = item.value;
					switch (value) {
					case "receive":
						$("#divStatus").jqxDropDownList({source: [{enumTypeId: "COMM_INBOUND_RESULT", description: "${StringUtil.wrapString(uiLabelMap.CallInbound)}"}] });
						$("#divStatus").jqxDropDownList("val", "COMM_INBOUND_RESULT");
						$("#reasonTypeEnumId").jqxDropDownList({autoDropDownHeight: true, source: [{enumTypeId: "COMM_INBOUND_RESULT", description: "${StringUtil.wrapString(uiLabelMap.CallInbound)}"}] });
						$("#reasonTypeEnumId").jqxDropDownList("val", "COMM_INBOUND_RESULT");
						break;
					case "send":
						$("#divStatus").jqxDropDownList({ source: reasonType });
						break;
					default:
						break;
					}
				}
			});
			$("#divStatus").on("change", function (event){
				checkRequireScheduleCall();
				$("#reasonTypeEnumId").jqxDropDownList("clearSelection");
				var args = event.args;
				if (args) {
					var index = args.index;
					var item = args.item;
					var value = item.value;
					switch (value) {
					case "CONTACTED":
						$("#reasonTypeEnumId").jqxDropDownList({autoDropDownHeight: false, source: reasonContacted });
						break;
					case "UNCONTACTED":
						$("#reasonTypeEnumId").jqxDropDownList({autoDropDownHeight: true, source: reasonUnContacted });
						break;
					default:
						break;
					}
				} 
			});
			$("#createIssue").click(function() {
				if (CreateMode) {
					Processor.saveCustomer();
				}
				setTimeout(function() {
					var currentGrid = $("#communicationHistory");
					if(!issueForm.jqxValidator("validate")){
						return;
					}
					if(isRequireScheduleCall){
						var schedule = $("#NextCallSchedule")
						var date = schedule.jqxDateTimeInput("getDate");
						if(!date){
							$("#NextCallSchedule").notify("${StringUtil.wrapString(uiLabelMap.ChooseNextScheduleTime)}", { position: "left", className: "error" });
							return;
						}
					}
					var nextCall = $("#NextCallSchedule").jqxDateTimeInput("getDate");
					var index = $("#CommunicationEventType").jqxDropDownList("getSelectedItem");
					var commEventTypeId = index ? index.value : "";
					index = $("#subject").jqxDropDownList("getSelectedItem");
					var subjectId = index ? index.value : "";
					var productDiscussedId = Grid.getDropDownValue($("#productDiscussing")).toString();
					index = $("#reasonTypeEnumId").jqxDropDownList("getSelectedItem");
					var rstId = index ? index.value : "";
					index = $("#reasonEnumId").jqxDropDownList("getSelectedItem");
					var reId = index ? index.value : "";
					index = $("#directionComm").jqxDropDownList("getSelectedItem");
					var dir =  index ? index.value : "";
					var row = {
						communicationEventTypeId : commEventTypeId,
						content : $("#issueContent").val(),
						subjectEnumId: subjectId,
						productDiscussedId: productDiscussedId,
						nextCallSchedule: nextCall? nextCall.getTime() : "",
						subjectSchedule: "COM_SCHEDULE_NEXT",
						resultEnumTypeId : rstId,
						resultEnumId : reId,
						type : dir,
						partyId : CookieLayer.getCurrentParty().partyId
					};
					if (marketingCampaignId) {
						row = _.extend(row, marketingCampaignId);
					}
					$.ajax({
						url: "raiseCustomerIssue",
						data: row,
						type: "POST",
						success: function(res){
							if (res.status == "success") {
								clearForm();
								$("#claimNotify").notify(multiLang.CreateSuccessfully, { position: "left", className: "success" });
								Family.updateGridFamily();
							} else {
								$("#claimNotify").notify(multiLang.CreateError, { position: "left", className: "error" });
							}
						}
					});
				}, 500);
			});
		};
		var checkRequireScheduleCall = function() {
			var status = $("#divStatus").jqxDropDownList("val");
			var asterisk = $(".schedule-asterisk");
			if (status == "CONTACTED") {
				isRequireScheduleCall = true;
				asterisk.show();
			} else {
				asterisk.hide();
				isRequireScheduleCall = false;
			}
		};
		var clearForm = function() {
			var index = $("#directionComm").jqxDropDownList("getSelectedIndex");
			Grid.clearForm($("#issueFormWindow"));
			$("#issueContent").val("");
			$("#ClaimPartyId").jqxDropDownList("selectedIndex", 0);
			$("#directionComm").jqxDropDownList("selectedIndex", index);
			initCommunicationEventType();
		};
		var initIssueFormRule = function(){
			issueForm.jqxValidator({
				position: "left",
				scroll: false,
				rules: [
					{ input: "#directionComm", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var index = input.jqxDropDownList("getSelectedIndex");
							return index != -1;
						}
					},
					{ input: "#subject", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var data = input.jqxDropDownList("getItems");
							if (data.length) {
								var index = input.jqxDropDownList("getSelectedIndex");
								return index != -1;
							}
							return true;
						}
					},
					{ input: "#reasonTypeEnumId", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var index = input.jqxDropDownList("getSelectedIndex");
							return index != -1;
						}
					},
					{ input: "#reasonEnumId", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var index = input.jqxDropDownList("getSelectedIndex");
							return index != -1;
						}
					},
					{ input: "#CommunicationEventType", message: "${uiLabelMap.FieldRequired}", action: "change",
						rule: function (input, commit) {
							var index = input.jqxDropDownList("getSelectedIndex");
							return index != -1;
						}
					},
					{ input: "#NextCallSchedule", message: "${uiLabelMap.DateNotValid}", action: "valueChanged",
						rule: function (input, commit) {
							var value = input.jqxDateTimeInput("getDate");
							if (value) {
								var currentTime = new Date().getTime();
								if (value.getTime() < currentTime) {
									return false;
								}
							}
							return true;
						}
					}
				]
			});
		};
		var checkAccessWithCustomer = function() {
			var permission = DataAccess.getData({
					url: "checkPermissionWithCustomer",
					data: {partyId: CookieLayer.getCurrentParty().partyId},
					source: "permission"});
			if (permission["access"]) {
				$("#directionComm").jqxDropDownList({ disabled: false });
				marketingCampaignId = permission;
			} else {
				$("#directionComm").jqxDropDownList({ disabled: true });
				setTimeout(function() {
					$("#directionComm").jqxDropDownList("val", "receive");
				}, 200);
			}
		};
		var marketingCampaignId = new Object();
		return {
			init: function() {
				issueForm = $("#issueFormWindow");
				initElementIssue();
				bindEvent();
				initIssueFormRule();
			},
			checkAccessWithCustomer: checkAccessWithCustomer
		};
	})();

	$(document).ready(function(){
		issueObj.init();
	});
	var tmpReason = [];
	$("#reasonTypeEnumId").on("change", function (event) {
		if (event.args) {
			var item = event.args.item;
		}
		var resultEnumId = item.value;
		var reloadData = function(listReasonClaimm){
		var source = {
			localdata: listReasonClaimm,
				datatype: "array"
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			var obj = {
				selectedIndex: 0,
				source: dataAdapter,
				dropDownWidth : 400,
				renderer: function (index, label, value) {
					var da = listReasonClaimm[index];
					if(da){
						var seq = da.enumCode ? "[" + da.enumCode + "] " : "";
						var valueStr = seq + da.description;
						return valueStr;
					}
					return label;
				}
			};
			if (listReasonClaimm.length > 10) {
				obj.autoDropDownHeight = false;
				obj.dropDownHeight = 250;
			} else {
				obj.autoDropDownHeight = true;
			}
			$("#reasonEnumId").jqxDropDownList(obj);
		};
		var date = new Date();
		var dd = date.getHours() + "." + date.getDate() + "." + date.getMonth() + "." + date.getYear();
		var key = "data-" + resultEnumId + dd;
		var localData = localStorage.getItem(key);
		if (localData) {
			var data = JSON.parse(localData);
			reloadData(data);
			return;
		}
		var request = $.ajax({
			url: "loadReasonListByReasonTypeId",
			type: "POST",
			data: {resultEnumId : resultEnumId},
			dataType: "json",
			success: function(data) {
				var listReasonClaimm = data["listReasonClaim"];
				tmpReason = listReasonClaimm;
				if (!listReasonClaimm.length){
					$("#reasonEnumId").jqxDropDownList({source: [], dropDownWidth : 200 });
				} else {
					reloadData(listReasonClaimm);
					localStorage.setItem(key, JSON.stringify(listReasonClaimm));
				}
			}
		});
	});
</script>
