<div id="alterpopupWindow1" style="display:none">
	<div>${StringUtil.wrapString(uiLabelMap.BSAddNewFeatureCategtory)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_productFeatureCategoryId" class="required">${uiLabelMap.BSProductFeatureCategoryId}</label>
						</div>
						<div class='span7'>
							<input id="wn_productFeatureCategoryId"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_description" class="required">${uiLabelMap.BSDescription}</label>
						</div>
						<div class='span7'>
							<input id="wn_description"/>
				   		</div>
					</div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave1" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel1" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#include "script/productFeatureCategoryNewScript.ftl"/>
