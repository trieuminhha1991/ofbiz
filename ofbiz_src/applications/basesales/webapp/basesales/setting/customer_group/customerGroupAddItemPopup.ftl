<div id="alterpopupWindowCustomerGroupAddItem" style="display:none">
	<div>${uiLabelMap.BSAddMemberIntoGroup}</div>
	<div class='form-window-container'>
		<input type="hidden" id="wn_additem_groupId" value=""/>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required" for="wn_additem_groupCode">${uiLabelMap.BSGroupId}</label>
						</div>
						<div class='span9'>
							<input type="text" id="wn_additem_groupCode" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required" for="wn_additem_memberId">${uiLabelMap.BSMemberId}</label>
						</div>
						<div class='span9'>
							<div id="wn_additem_memberId">
								<div id="wn_additem_memberGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_additem_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_additem_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbCustomerGroupAddItem.init();
	});
	var OlbCustomerGroupAddItem = (function(){
		var validatorVAL;
		var memberDDB;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidator();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowCustomerGroupAddItem"), {width: 500, height: 200, cancelButton: $("#wn_additem_alterCancel")});
			
			jOlbUtil.input.create("#wn_additem_groupCode", {width: '97%', maxLength: 60, disabled: true});
		};
		var initElementComplex = function(){
			var configMember = {
				useUrl: true,
				root: 'results',
				widthButton: '99%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
				columns: [
					{text: "${uiLabelMap.BSMemberId}", datafield: 'partyCode', width: '30%'},
					{text: "${uiLabelMap.BSFullName}", datafield: 'fullName'}
				],
				url: 'JQGetListParties',
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true,
			};
			memberDDB = new OlbDropDownButton($("#wn_additem_memberId"), $("#wn_additem_memberGrid"), null, configMember, []);
		};
		var initEvent = function(){
			$('#wn_additem_alterSave').click(function () {
				if(!validatorVAL.validate()) return false;
				
				var success = "${StringUtil.wrapString(uiLabelMap.Successful)}";
				var memberId = memberDDB.getValue();
				var aRoleType = new Array();
					var map = {};
					map['memberId'] = memberId;
					map['groupId'] = $('#wn_additem_groupId').val();
					aRoleType = map;
				if (aRoleType.length <= 0){
					return false;
				} else {
					aRoleType = JSON.stringify(aRoleType);
					$.ajax({
						type: 'POST',
						url: 'createRelaCustomerGroup',
						data: {"aRoleType": aRoleType},
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
										$("#btnPrevWizard").removeClass("disabled");
										$("#btnNextWizard").removeClass("disabled");
										
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	memberDDB.clearAll(false);
							        	$('#jqxgrid').jqxGrid('updatebounddata');
							        	$('#alterpopupWindowCustomerGroupAddItem').jqxWindow('hide');
		       							$('#alterpopupWindowCustomerGroupAddItem').jqxWindow('close');
									}
							);
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
						},
					});
				}
		    });
		};
		var initValidator = function(){
			var mapRules = [
					{input: "#wn_additem_groupCode", type: "validInputNotNull"},
					{input: "#wn_additem_memberId", type: "validObjectNotNull", objType: "dropDownButton"}
				];
			validatorVAL = new OlbValidator($("#alterpopupWindowCustomerGroupAddItem"), mapRules);
		};
		return {
			init: init
		};
	}());
</script>