<style type="text/css">
	#isSelectAllProductStore {
		text-align: left; margin-left:0 !important; height:15px !important; margin-top:10px
	}
</style>

<#assign updateMode = false/>
<#if productQuotation?exists>
	<#assign updateMode = true/>
</#if>
<#if !copyMode?exists><#assign copyMode = false/></#if>
<#if !quotationAllowEditSpecial?exists><#assign quotationAllowEditSpecial = false/></#if>
<#assign isQuotationEditSpecial = false/>
<#if quotationAllowEditSpecial && !copyMode>
	<#if productQuotation.statusId == "QUOTATION_ACCEPTED" || productQuotation.statusId == "QUOTATION_MODIFIED">
		<#assign isQuotationEditSpecial = true/>
	</#if>
</#if>
<div style="position:relative">
	<form class="form-horizontal form-window-content-custom" id="initQuotationEntry" name="initQuotationEntry" method="post" action="#">
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5'>
						<label for="productQuotationId">${uiLabelMap.BSQuotationId}</label>
					</div>
					<div class="span7">
						<input class="span12" type="text" id="productQuotationId" name="productQuotationId" maxlength="20" value=""/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label for="quotationName" class="required">${uiLabelMap.BSQuotationName}</label>
					</div>
					<div class="span7">
						<input class="span12" type="text" id="quotationName" name="quotationName" maxlength="100" value=""/>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSQuotationType}</label>
					</div>
					<div class="span7">
						<div id="productQuotationTypeId"></div>
			   		</div>
				</div>
				<#--
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSSalesChannelType}</label>
					</div>
					<div class="span7">
						<div id="salesMethodChannelEnumId"></div>
			   		</div>
				</div>
				-->
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSPSProductStoreGroup}</label>
					</div>
					<div class="span7">
						<div id="productStoreGroupIds"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSPSSalesChannel}</label>
					</div>
					<div class="span7">
						<div id="productStoreIds" class="close-box-custom"></div>
						<div id="isSelectAllProductStore"><span>${uiLabelMap.BSSelectAll}</span></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSDescription}</label>
					</div>
					<div class="span7">
						<textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
			   		</div>
				</div>
			</div>
			<div class="span6">
				<#--
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSRoleTypeF}</label>
					</div>
					<div class="span7">
						<div id="roleTypeId"></div>
			   		</div>
				</div>
				-->
				<div class='row-fluid'>
					<div class='span5'>
						<label class="required">${uiLabelMap.BSCurrencyUomId}</label>
					</div>
					<div class="span7">
						<div id="currencyUomId"></div>
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
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BSCustomer}/${uiLabelMap.BSAbbCustomerGroup}</label>
					</div>
					<div class="span7">
						<div id="partyId">
							<div id="partyGrid"></div>
						</div>
			   		</div>
				</div>
			</div>
		</div><!--.row-fluid-->
	</form>
	<#--<a href="#ap" class="btn btn-primary" style="position:absolute;bottom:0;right:0;font-size:24px"><i class="fa fa-arrow-circle-down"></i></a>-->
</div>
<#--<a name="ap"></a>-->

<#include "script/quotationNewInfoScript.ftl"/>
