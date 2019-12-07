<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<div id="CallScheduleForm">
	<div>
		${uiLabelMap.CreateScheduleCall}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid' id='contentContainer'>
				<div class='span12'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='' style=" margin-top: 4px;">${uiLabelMap.OrderCallSchedule}</label>
						</div>
						<div class='span7'>
							<div id="CallScheduleInput"></div>
						</div>
					</div>
				</div>
			</div>
			<div class='form-action'>
				<button id="cancelcreateScheduleCall" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="createScheduleCall" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<@jqOlbCoreLib />
<script>
	var CallSchedule = (function(){
		var ScheduleForm;
		var theme = 'olbius';
		var partyId =  "";
		var setPartyId = function(id){
			partyId = id;
		};
		var getPartyId = function(){
			return partyId;
		};
		var initElement = function(){
			$("#CallScheduleInput").jqxDateTimeInput({
				width: 200,
				height: 25,
				theme: 'olbius',
				showFooter: true,
				formatString: 'HH:mm:ss dd/MM/yyyy',
				todayString: multiLang.today,
				clearString: multiLang.clear
			});
			$("#CallScheduleInput").jqxDateTimeInput('setDate', null);
		};

		var initScheduleWindow = function() {
			ScheduleForm.jqxWindow({
				width : 420,
				height : 150,
				resizable : false,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				cancelButton: '#cancelcreateScheduleCall',
				theme : theme,
				initContent: function(){
					initElement();
				}
			});
		};
		var bindEvent = function(){
			$("#createScheduleCall").click(function() {
				var partyId = jOlbUtil.getAttrDataValue('customerId');
				if (!partyId) {
					bootbox.alert("${uiLabelMap.OrderErrorSelectCustomer}");
				}
				if (ScheduleForm.jqxValidator('validate')) {
					var subjectEnumId = "COM_SCHEDULE_ORD";
					var nextCall = $('#CallScheduleInput').jqxDateTimeInput('getDate');
					var row = {
						subjectEnumId: subjectEnumId,
						partyId : partyId,
						entryDate : nextCall ? nextCall.toSQLTimeStamp() : ''
					};
					$.ajax({
						url: "scheduleCommunication",
						data: row,
						type: "POST",
						success: function(res){
							if(res.communicationEventId){
								clearForm();
								$("#user_info").notify(multiLang.CreateScheduleCallSuccess, { position: "bottom right", className: "success" });
							}else{
								$("#user_info").notify(multiLang.CreateError, { position: "bottom right", className: "success" });
							}
						}
					});
					ScheduleForm.jqxWindow('close');
				}
			});
		};
		var clearForm = function(){
			$('#CallScheduleInput').jqxDateTimeInput('setDate', null);
		};
		var initScheduleFormRule = function(){
			ScheduleForm.jqxValidator({
				position: 'right',
				rules: [
					{input: '#CallScheduleInput', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = input.jqxDateTimeInput('getDate');
							if (value > 0) {
								return true;
							}
		                    return false;
		                }
					},
				]
			});
		};
		var checkAccessWithCustomer = function() {
			var access = DataAccess.getData({
					url: "checkPermissionWithCustomer",
					data: {partyId: CookieLayer.getCurrentParty().partyId},
					source: "access"});
			if (access) {
				$("#directionComm").jqxDropDownList({ disabled: false });
			} else {
				$("#directionComm").jqxDropDownList({ disabled: true });
				setTimeout(function() {
					$("#directionComm").jqxDropDownList('val', 'receive');
				}, 200);
			}
		};
		var open = function() {
			if (!jOlbUtil.getAttrDataValue('customerId')) {
				bootbox.alert("${uiLabelMap.OrderErrorSelectCustomer}");
			} else {
				if (jOlbUtil.getAttrDataValue('customerId') == "_NA_") {
					bootbox.alert("${uiLabelMap.NotForNACustomers}");
				} else {
					ScheduleForm.jqxWindow('open');
				}
			}
		};
		return {
			init: function(){
				ScheduleForm = $("#CallScheduleForm");
				initScheduleWindow();
				initElement();
				bindEvent();
				initScheduleFormRule();
			},
			setPartyId : setPartyId,
			getPartyId : getPartyId,
			open: open
		};
	})();

	$(document).ready(function(){
		CallSchedule.init();
	});
</script>
