<#-- TODOCHANGE deleted -->
<div id="popupDeleteMember" style="display : none;">
	<div>
		${uiLabelMap.CommonDelete}
	</div>
	<div style="overflow: hidden;">
		<form id="DeleteMemberForm" class="form-horizontal">
			<input type="hidden" value="${parameters.roleTypeId?if_exists}" id="roleTypeId" name="roleTypeId">
			<label class="">${uiLabelMap.BSAreYouSureYouWantToDelete}</label>
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel3" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'></i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave3" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-pencil'></i> ${uiLabelMap.OK}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script>
	var productIdd = '${productStoreId?if_exists}';
	var choose = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var successK = '${StringUtil.wrapString(uiLabelMap.BSSuccessK)}';
	var addNew1 = "${StringUtil.wrapString(uiLabelMap.BSAddNewRoleTypeForStore)}";
	var updatePopup2 = "${StringUtil.wrapString(uiLabelMap.BSUpdateShipmentMethodForStore)}";
</script>
<script type="text/javascript" src="/salesresources/js/setting/settingDeleteProductStoreRoleType.js"></script>