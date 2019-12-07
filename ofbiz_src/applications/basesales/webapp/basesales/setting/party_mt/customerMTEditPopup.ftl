<div id="alterpopupWindowCustomerMTEdit" style="display:none">
	<div>${uiLabelMap.BSEdit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label>${uiLabelMap.BSGroupId}</label>
						</div>
						<div class='span9'>
							<input type="text" id="wn_partyCodeEdit" class="span12" maxlength="60" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label class="required" for="wn_MTNameEdit">${uiLabelMap.BSGroupName}</label>
						</div>
						<div class='span9'>
							<input type="text" id="wn_groupNameEdit" class="span12" maxlength="100" value=""/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label  >${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span9'>
							<textarea id="wn_descriptionEdit" ></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_alterSaveEdit" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancelEdit" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbCustomerMTEdit.init();
	});
	var OlbCustomerMTEdit = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initEvent();
			initValidator();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowCustomerMTEdit"), {width: 480, height: 230, cancelButton: $("#wn_alterCancelEdit")});
			
			$("#wn_partyCodeEdit").jqxInput({width: "295", height: "23px", theme: theme});
			$("#wn_groupNameEdit").jqxInput({width: "295", height: "23px", theme: theme});
			$("#wn_descriptionEdit").jqxTextArea({width: "295", height: "46px", theme: theme});
			
		};
		var initEvent = function(){
			$('#wn_alterSaveEdit').click(function(){
				if(!validatorVAL.validate()) return false;
				
				var rowData = {
					partyId : partyIdEditting,
					partyCode : $('#wn_partyCodeEdit').val(),
					groupName : $('#wn_groupNameEdit').val(),
					description : $('#wn_descriptionEdit').val(),
				};
				$("#jqxgrid").jqxGrid('updaterow', rowData.partyId, rowData);
				$("#alterpopupWindowCustomerMTEdit").jqxWindow('close');
			});
			
			$('#alterpopupWindowCustomerMTEdit').on('close', function(){
				$('#alterpopupWindowCustomerMTEdit').jqxValidator('hide');
				$('#jqxgrid').jqxGrid('refresh');
				$('#jqxgrid').jqxGrid('updatebounddata');
				$('#wn_partyCodeEdit').val(null);
				$('#wn_groupNameEdit').val(null);
				$('#wn_descriptionEdit').val(null);
			});
		};
		var initValidator = function(){
			var mapRules = [
					{input: "#wn_partyCodeEdit", type: "validCannotSpecialCharactor"},
					{input: "#wn_groupNameEdit", type: "validInputNotNull"}
				];
			validatorVAL = new OlbValidator($("#alterpopupWindowCustomerMTEdit"), mapRules);
		};
		
		return {
			init: init
		};
	}());
</script>