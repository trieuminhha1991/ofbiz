<div class="widget-box margin-bottom10 collapsed">
	<div class="widget-header widget-header-small">
		<h5>${uiLabelMap.DMSMoveDataCustomer}</h5>
		<div class="widget-toolbar">
			<a data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
	</div>

	<div class="widget-body" id="moveDataCustomerArea">
		<div class="widget-main">
			<div class="row-fluid margin-bottom10">
				<div class="span4">
					<div class="span5">
						<label class="text-right">${uiLabelMap.DmsEmployeeFrom}&nbsp;</label>
					</div>
					<div class="span7">
						<div id="EmployeeFrom"></div>
					</div>
				</div>
				<div class="span4">
					<div class="span5">
						<label class="text-right">${uiLabelMap.DmsEmployeeTo}&nbsp;</label>
					</div>
					<div class="span7">
						<div id="EmployeeTo"></div>
					</div>
				</div>
				<div class="span4">
					<button id="moveAllCustomer" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.DMSMoveAllCustomer}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		MoveCustomer.init();
	});
	if (typeof (MoveCustomer) == "undefined") {
		var MoveCustomer = (function() {
			var initJqxElements = function() {
				var source = {
					datatype: "json",
					datafields: [
						{ name: "partyId" },
						{ name: "partyDetail" }
					],
					url: "getCallCenterEmployee"
				};
				var dataAdapter = new $.jqx.dataAdapter(source);

				$("#EmployeeTo").jqxDropDownList({ theme: theme, width: "95%", height: 25, source: dataAdapter,
					displayMember: "partyDetail", valueMember: "partyId", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true
				});
				$("#EmployeeFrom").jqxDropDownList({ theme: theme, width: "95%", height: 25, source: dataAdapter,
					displayMember: "partyDetail", valueMember: "partyId", placeHolder: multiLang.filterchoosestring, autoDropDownHeight: true
				});
			};
			var handleEvents = function() {
				$("#moveAllCustomer").click(function() {
					if ($("#moveDataCustomerArea").jqxValidator("validate")) {
						DataAccess.execute({
						url: "moveDataCustomer",
						data: {
							partyIdFrom: $("#EmployeeFrom").jqxDropDownList("val"),
							partyIdTo: $("#EmployeeTo").jqxDropDownList("val"),
							marketingCampaignId: campaignId}
						}, MoveCustomer.reload);
					}
				});
			};
			var initValidator = function() {
				$("#moveDataCustomerArea").jqxValidator({
				    rules: [{ input: "#EmployeeTo", message: multiLang.fieldRequired, action: "change",
								rule: function (input, commit) {
									var value = input.val();
									if (value) {
										return true;
									}
									return false;
								}
							},
							{ input: "#EmployeeFrom", message: multiLang.fieldRequired, action: "change",
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
			var reload = function(res) {
				if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
					$("#moveDataCustomerArea").notify(multiLang.updateError, { position: "bottom", className: "error" });
				} else {
					var customers = res["customers"];
					var message = customers + " " + "${StringUtil.wrapString(uiLabelMap.DmsMoveCustomerSuccess)}";
					$("#moveDataCustomerArea").notify(message, { position: "bottom", className: "success" });
				}
				$("#ListResourceAssigned").jqxGrid("updatebounddata");
			};
			return {
				init: function() {
					initJqxElements();
					handleEvents();
					initValidator();
				},
				reload: reload
			};
		})();
	}
</script>