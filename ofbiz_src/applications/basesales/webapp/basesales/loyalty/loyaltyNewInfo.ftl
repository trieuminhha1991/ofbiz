<div style="position:relative">
	<form class="form-horizontal form-window-content-custom" id="initLoyaltyEntry" name="initLoyaltyEntry" method="post" action="#">
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label for="loyaltyId">${uiLabelMap.BSLoyaltyId}</label>
					</div>
					<div class="span7">
						<input class="span12" type="text" id="loyaltyId" name="loyaltyId" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="loyaltyName" class="required">${uiLabelMap.BSLoyaltyName}</label>
					</div>
					<div class="span7">
						<input class="span12" type="text" id="loyaltyName" name="loyaltyName" maxlength="100" value=""/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSLoyaltyType}</label>
					</div>
					<div class="span7">
						<div id="loyaltyTypeId"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="loyaltyText">${uiLabelMap.BSContent}</label>
					</div>
					<div class="span7">
						<textarea id="loyaltyText" data-maxlength="50" rows="2" style="resize: vertical;margin-bottom:0" class="span12"></textarea>
			   		</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label for="productStoreIds" class="required">${uiLabelMap.BSSalesChannel}</label>
					</div>
					<div class="span7">
						<div id="productStoreIds"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="roleTypeIds">${uiLabelMap.BSPartyApply}</label>
					</div>
					<div class="span7">
						<div id="roleTypeIds"></div>
			   		</div>
				</div>
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
				<#--<div class='row-fluid'>
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
							<a href="javascript:void(0);" class="add-quickly" onclick="OlbLoyaltyNewInfo.clearNumberInput('#useLimitPerOrder');"><i class="fa fa-trash"></i></a>
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
							<a href="javascript:void(0);" class="add-quickly" onclick="OlbLoyaltyNewInfo.clearNumberInput('#useLimitPerCustomer');"><i class="fa fa-trash"></i></a>
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
							<a href="javascript:void(0);" class="add-quickly" onclick="OlbLoyaltyNewInfo.clearNumberInput('#useLimitPerPromotion');"><i class="fa fa-trash"></i></a>
			   			</div>
			   		</div>
				</div>-->
			</div><!--.span6-->
		</div><!--.row-fluid-->
	</form>
</div>
<#include "loyaltyNewInfoScript.ftl"/>
