<div id="alterpopupWindowRoleTypeNew" style="display:none">
	<div>${uiLabelMap.BSAddNewRoleType}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="containerCTP" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationCTP" style="margin-bottom:5px">
		        <div id="notificationContentCustomTimePeriod">
		        </div>
		    </div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_roleTypeId" class="required">${uiLabelMap.BSRoleTypeId}</label>
						</div>
						<div class='span7'>
							<div id="wn_roleTypeId"></div>
				   		</div>
					</div>
					<#--
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_parentTypeId">${uiLabelMap.BSParentTypeId}</label>
						</div>
						<div class='span7'>
							<div id="wn_parentTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_description" class="required">${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<input type="text" id="wn_description" class="span12" value=""/>
				   		</div>
					</div>
					-->
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbRoleTypeNew.init();
	});
	var OlbRoleTypeNew = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			<#--
			jOlbUtil.input.create($("#wn_roleTypeId"), {width: '92%'});
			jOlbUtil.input.create($("#wn_description"), {width: '92%'});
			-->
			
			jOlbUtil.windowPopup.create($("#alterpopupWindowRoleTypeNew"), {width: 520, height: 140, cancelButton: $("#wn_alterCancel")});
		};
		var initElementComplex = function(){
			var configParentRoleType = {
				width:'99%',
				height: 25,
				key: "roleTypeId",
	    		value: "description",
	    		displayDetail: true,
				dropDownWidth: 'auto',
				multiSelect: false,
				autoDropDownHeight: false,
				placeHolder: "",
				useUrl: true,
				url: "jqxGeneralServicer?sname=JQGetListRoleType&pagesize=0",
			};
			new OlbComboBox($("#wn_roleTypeId"), null, configParentRoleType, []);
		};
		var initEvent = function(){
			$("#wn_alterSave").on("click", function(){
				if (!validatorVAL.validate()) return false;
				$.ajax({
					type: 'POST',
					url: "addRoleTypeGroupMember",
					data: {
						'roleTypeId': $('#wn_roleTypeId').val(),
						"roleTypeGroupId": "${roleTypeGroupId?default("")}"
					},
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
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
						        	
						        	$("#alterpopupWindowRoleTypeNew").jqxWindow("close");
						        	$("#jqxgridRoleType").jqxTreeGrid('updateBoundData');
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
			});
		};
		var initValidateForm = function(){
			var extendRules = [];
			var mapRules = [
					{input: '#wn_roleTypeId', type: 'validInputNotNull'},
	            ];
			<#--
			var mapRules = [
		            {input: '#wn_roleTypeId', type: 'validCannotSpecialCharactor'},
					{input: '#wn_roleTypeId', type: 'validInputNotNull'},
					{input: '#wn_description', type: 'validInputNotNull'},
	            ];
			-->
			validatorVAL = new OlbValidator($('#alterpopupWindowRoleTypeNew'), mapRules, extendRules, {position: 'bottom'});
		};
		var openQuickCreateNew = function(){
			$("#alterpopupWindowRoleTypeNew").jqxWindow("open");
		};
		return {
			init: init,
			openQuickCreateNew: openQuickCreateNew,
		};
	}());
	
</script>