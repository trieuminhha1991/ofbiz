<#if productPromoCode?exists>
<div id="windowEditPromoCodeContainerParent">
	<#include "component://common/webcommon/includes/messages.ftl">
	<div class="row-fluid">
		<div class="span6 form-window-content-custom">
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_productPromoId" class="required">${uiLabelMap.BSProductPromoId}</label>
				</div>
				<div class='span7'>
					<input type="text" id="we_productPromoId" value="${productPromoCode.productPromoId}">
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_productPromoCodeId" class="required">${uiLabelMap.BSVoucherCode}</label>
				</div>
				<div class='span7'>
					<input type="text" id="we_productPromoCodeId" value="${productPromoCode.productPromoCodeId}">
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_userEntered">${uiLabelMap.BSPromoUserEntered}</label>
				</div>
				<div class='span7'>
					<div id="we_userEntered"></div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_requireEmailOrParty">${uiLabelMap.BSRequireEmailOrParty}</label>
				</div>
				<div class='span7'>
					<div id="we_requireEmailOrParty"></div>
		   		</div>
			</div>
		</div>
		<div class="span6 form-window-content-custom">
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_useLimitPerCode">${uiLabelMap.BSAbbUseLimitPerOrder}</label>
				</div>
				<div class='span6'>
					<div class="container-add-plus">
						<div id="we_useLimitPerCode"></div>
						<a href="javascript:void(0);" class="add-quickly" onclick="jOlbUtil.numberInput.clear('#we_useLimitPerCode');"><i class="fa fa-trash"></i></a>
		   			</div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_useLimitPerCustomer">${uiLabelMap.BSAbbUseLimitPerCustomer}</label>
				</div>
				<div class='span6'>
					<div class="container-add-plus">
						<div id="we_useLimitPerCustomer"></div>
						<a href="javascript:void(0);" class="add-quickly" onclick="jOlbUtil.numberInput.clear('#we_useLimitPerCustomer');"><i class="fa fa-trash"></i></a>
		   			</div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_fromDate">${uiLabelMap.BSFromDate}</label>
				</div>
				<div class='span6'>
					<div id="we_fromDate"></div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label for="we_thruDate">${uiLabelMap.BSThruDate}</label>
				</div>
				<div class='span6'>
					<div id="we_thruDate"></div>
		   		</div>
			</div>
		</div>
	</div>
	<#--
	<div class="row-fluid margin-top10">
		<div class="pull-right form-window-content-custom">
			<button id="we_alterSavePromoCode" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSave}</button>
		</div>
	</div>
	-->
	
	<script type="text/javascript">
		var promotionCode = {
			useLimitPerCode: "${productPromoCode.useLimitPerCode?if_exists}",
			useLimitPerCustomer: "${productPromoCode.useLimitPerCustomer?if_exists}",
			userEntered: "${productPromoCode.userEntered?default("Y")}",
			requireEmailOrParty: "${productPromoCode.requireEmailOrParty?default("N")}",
			fromDate: "${productPromoCode.fromDate?if_exists}",
			thruDate: "${productPromoCode.thruDate?if_exists}",
		}
	</script>
	<@jqOlbCoreLib />
	<script type="text/javascript" src="/salesresources/js/promotion/promotionCodeViewEdit.js"></script>
</div>
</#if>