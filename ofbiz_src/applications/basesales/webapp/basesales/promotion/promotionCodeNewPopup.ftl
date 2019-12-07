<#if productPromo?exists>
<div id="alterpopupWindowNewPromoCode" style="display:none">
	<div>${uiLabelMap.ProductPromotionAddSetOfPromotionCodes}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
		    <input type="hidden" id="wn_productPromoId" value="${productPromo.productPromoId}"/>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_quantity" class="required">${uiLabelMap.CommonQuantity}</label>
						</div>
						<div class='span7'>
							<div class="container-add-plus">
								<div id="wn_quantity"></div>
								<a href="javascript:void(0);" class="add-quickly" onclick="OlbPromoCodeNew.clearNumberInput('#wn_quantity');"><i class="fa fa-trash"></i></a>
				   			</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_codeLength" class="required">${uiLabelMap.ProductPromoCodeLength}</label>
						</div>
						<div class='span7'>
							<div class="container-add-plus">
								<div id="wn_codeLength"></div>
								<a href="javascript:void(0);" class="add-quickly" onclick="OlbPromoCodeNew.clearNumberInput('#wn_codeLength');"><i class="fa fa-trash"></i></a>
				   			</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_promoCodeLayout" class="required">${uiLabelMap.ProductPromoCodeLayout}</label>
						</div>
						<div class='span7'>
							<div id="wn_promoCodeLayout"></div>
		                    <span class="tooltipob">${uiLabelMap.BSPromoCodeLayoutTooltip}</span>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_userEntered">${uiLabelMap.ProductPromoUserEntered}</label>
						</div>
						<div class='span7'>
							<div id="wn_userEntered"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_requireEmailOrParty">${uiLabelMap.BSRequireEmailOrParty}</label>
						</div>
						<div class='span7'>
							<div id="wn_requireEmailOrParty"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_useLimitPerCode">${uiLabelMap.BSAbbUseLimitPerCode}</label>
						</div>
						<div class='span6'>
							<div class="container-add-plus">
								<div id="wn_useLimitPerCode"></div>
								<a href="javascript:void(0);" class="add-quickly" onclick="OlbPromoCodeNew.clearNumberInput('#wn_useLimitPerCode');"><i class="fa fa-trash"></i></a>
				   			</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_useLimitPerCustomer">${uiLabelMap.BSAbbUseLimitPerCustomer}</label>
						</div>
						<div class='span6'>
							<div class="container-add-plus">
								<div id="wn_useLimitPerCustomer"></div>
								<a href="javascript:void(0);" class="add-quickly" onclick="OlbPromoCodeNew.clearNumberInput('#wn_useLimitPerCustomer');"><i class="fa fa-trash"></i></a>
				   			</div>
				   		</div>
					</div>
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

<#include "script/promotionCodeNewScript.ftl">
</#if>