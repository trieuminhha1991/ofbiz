<#if !copyMode?exists><#assign copyMode = false/></#if>
<div style="position:relative">
	<form class="form-horizontal form-window-content-custom" id="initPromotionEntry" name="initPromotionEntry" method="post" action="#">
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label for="productPromoId">${uiLabelMap.BSProductPromoId}</label>
					</div>
					<div class="span7">
						<input class="span12" type="text" id="productPromoId" name="productPromoId" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="promoName" class="required">${uiLabelMap.BSPromoName}</label>
					</div>
					<div class="span7">
						<input class="span12" type="text" id="promoName" name="promoName" maxlength="100" value=""/>
			   		</div>
				</div>
				<#--
				<div class='row-fluid'>
					<div class='span5'>
						<label for="salesMethodChannelEnumId">${uiLabelMap.BSSalesChannel}</label>
					</div>
					<div class="span7">
						<div id="salesMethodChannelEnumId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="productPromoTypeId">${uiLabelMap.BSProductPromoTypeId}</label>
					</div>
					<div class="span7">
						<div id="productPromoTypeId"></div>
			   		</div>
				</div>
				-->
				<div class='row-fluid'>
					<div class='span5'>
						<label for="productStoreIds" class="required">${uiLabelMap.BSPSSalesChannel}</label>
					</div>
			   		<div class="span7">
						<div class="container-add-minus">
							<div id="productStoreIds" class="close-box-custom"></div>
						</div>
						<a id="btnShowProductStoreList" href="javascript:void(0)" class="btn btn-mini"><i class="fa fa-bars"></i></a>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="roleTypeIds">${uiLabelMap.BSRoleTypeId}</label>
					</div>
					<div class="span7">
						<div class="container-add-minus">
							<div id="roleTypeIds" class="close-box-custom"></div>
						</div>
						<a id="btnShowRoleTypeList" href="javascript:void(0)" class="btn btn-mini"><i class="fa fa-bars"></i></a>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="promoText">${uiLabelMap.BSContent}</label>
					</div>
					<div class="span7">
						<textarea id="promoText" data-maxlength="50" rows="2" style="resize: vertical;margin-bottom:0" class="span12">${productPromo?if_exists.promoText?if_exists}</textarea>
			   		</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSFromDate}</label>
					</div>
					<div class="span7">
						<div id="fromDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSThruDate}</label>
					</div>
					<div class="span7">
						<div id="thruDate"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSPublic}</label>
					</div>
					<div class="span7">
						<div id="showToCustomer"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSRequireVoucherCode}</label>
					</div>
					<div class="span7">
						<div id="requireCode"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSAbbUseLimitPerOrder}</label>
					</div>
					<div class="span7">
						<div class="container-add-plus">
							<div id="useLimitPerOrder"></div>
							<a href="javascript:void(0);" class="add-quickly" onclick="OlbPromoNewInfo.clearNumberInput('#useLimitPerOrder');"><i class="fa fa-trash"></i></a>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSAbbUseLimitPerCustomer}</label>
					</div>
					<div class="span7">
						<div class="container-add-plus">
							<div id="useLimitPerCustomer"></div>
							<a href="javascript:void(0);" class="add-quickly" onclick="OlbPromoNewInfo.clearNumberInput('#useLimitPerCustomer');"><i class="fa fa-trash"></i></a>
			   			</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSAbbUseLimitPerPromotion}</label>
					</div>
					<div class="span7">
						<div class="container-add-plus">
							<div id="useLimitPerPromotion"></div>
							<a href="javascript:void(0);" class="add-quickly" onclick="OlbPromoNewInfo.clearNumberInput('#useLimitPerPromotion');"><i class="fa fa-trash"></i></a>
			   			</div>
			   		</div>
				</div>
			</div><!--.span6-->
		</div><!--.row-fluid-->
	</form>
</div>

<div id="alterpopupWindowProductStore" style="display:none">
	<div>${uiLabelMap.BSListProductStore}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxgridProductStore"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_ps_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_ps_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<div id="alterpopupWindowRoleType" style="display:none">
	<div>${uiLabelMap.BSListRoleType}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position:relative">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxgridRoleType"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_rt_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_rt_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#include "script/promotionNewInfoScript.ftl"/>
