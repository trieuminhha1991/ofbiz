<div style="position:relative">
	<form class="form-horizontal form-window-content-custom" id="initPromotionEntry" name="initPromotionEntry" method="post" action="#">
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label for="productPromoId">${uiLabelMap.BSProductProId}</label>
					</div>
					<div class="span7">
						<input class="span12" type="text" id="productPromoId" name="productPromoId" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="promoName" class="required">${uiLabelMap.BSProgramName}</label>
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
				-->
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required" for="productPromoTypeId">${uiLabelMap.BSProgramType}</label>
					</div>
					<div class="span7">
						<div id="productPromoTypeId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="productStoreIds" class="required">${uiLabelMap.BSPSProductStore}</label>
					</div>
					<div class="span7">
						<div id="productStoreIds"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="roleTypeIds">${uiLabelMap.BSRoleTypeId}</label>
					</div>
					<div class="span7">
						<div id="roleTypeIds"></div>
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
						<label>${uiLabelMap.BSAbbUseLimitPerProgram}</label>
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
<#include "script/promotionExtNewInfoScript.ftl"/>
