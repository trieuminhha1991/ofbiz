<@jqGridMinimumLib />
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script src="/crmresources/js/notify.js"></script>
<script src="/basemarketingresources/js/createCampaign.js"></script>
<style>
	#pagerMarketingProductGrid, #pagerMarketingCost, #pagerMarketingPlace, #pagerMarketingRole {
		display: none;
	}
</style>
<script>
	var uiLabelMap = {
		UpdateSuccessfully : "${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}",
		UpdateError : "${StringUtil.wrapString(uiLabelMap.UpdateError)}",
		CommonRequired : "${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}",
		ConfirmCreateCampaign: "${StringUtil.wrapString(uiLabelMap.ConfirmCreateCampaign?default(''))}",
		Cancel: "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
		OK: "${StringUtil.wrapString(uiLabelMap.wgok)}"
	};
	var planid = "${parameters.id?if_exists}";
	var url = "createMarketingCampaignAndItem";
	if(planid){
		url = "updateMarketingCampaignAndItem";
	}
	var products, costs, places, roles, listProsucts, listEmployee;
	<#if parameters.id?exists>
		<#assign marketing = delegator.findOne("MarketingCampaign",
							Static["org.ofbiz.base.util.UtilMisc"].toMap("marketingCampaignId", parameters.id), false)!/>
		<#assign products = delegator.findList("MarketingProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("marketingCampaignId", parameters.id), null, null, null, false)/>
		products = [<#list products as item>{
			marketingCampaignId : "${item.marketingCampaignId}",
			marketingProductId : "${item.marketingProductId}",
			productId : "${item.productId?if_exists}",
			quantity : "${item.quantity?if_exists}",
			productTypeId : "${item.productTypeId?if_exists}",
			uomId : "${item.uomId?if_exists}",
			marketingPlaceId : "${item.marketingPlaceId?if_exists}",
		},</#list>];
		<#assign places = delegator.findList("MarketingPlaceDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("marketingCampaignId", parameters.id), null, null, null, false)/>
		places = [<#list places as item>{
			marketingCampaignId : "${item.marketingCampaignId}",
			marketingPlaceId : "${item.marketingPlaceId}",
			geoId : "${item.geoId?if_exists}",
			geoName : "${item.geoName?if_exists}",
			organizationId: "${item.organizationId?if_exists}",
			groupName: "${item.groupName?if_exists}",
			contactMechId : "${item.contactMechId?if_exists}",
		},</#list>];
		<#assign costs = delegator.findList("MarketingCostDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("marketingCampaignId", parameters.id), null, null, null, false)/>
		costs = [<#list costs as item>{
			marketingCampaignId : "${item.marketingCampaignId}",
			marketingCostId : "${item.marketingCostId}",
			marketingCostTypeId : "${item.marketingCostTypeId?if_exists}",
			unitPrice: "${item.unitPrice?if_exists}",
			description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
			currencyUomId : "${item.currencyUomId?if_exists}",
			quantity : "${item.quantity?if_exists}",
			quantityUomId : "${item.quantityUomId?if_exists}",
		},</#list>];
		<#assign roles = delegator.findList("MarketingRoleAndPerson", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("marketingCampaignId", parameters.id), null, null, null, false)/>
		roles = [<#list roles as item>{
			marketingCampaignId : "${item.marketingCampaignId}",
			marketingRoleId : "${item.marketingRoleId?if_exists}",
			partyId: "${item.partyId?if_exists}",
			roleTypeId : "${item.roleTypeId?if_exists}",
			firstName : "${item.firstName?if_exists}",
			middleName : "${item.middleName?if_exists}",
			lastName : "${item.lastName?if_exists}",
			marketingPlaceId : "${item.marketingPlaceId?if_exists}",
		},</#list>];
	</#if>
	<#assign productType = delegator.findList("ProductType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "MARKETING_PKG"), null, null, null, false)/>
	var productType= [<#list productType as item>
		{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
			'productTypeId' : '${item.productTypeId}',
			'description' : "${description}"
		},
	</#list>];
	<#assign quantityUomData = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)/>
	var quantityUomData = [<#list quantityUomData as item>
		{<#assign description = StringUtil.wrapString(item.description?if_exists) />
			'uomId' : '${item.uomId}',
			'description' : "${description}"
		},
	</#list>];
	<#assign currencyUomData = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)/>
	var currencyUomData = [<#list currencyUomData as item>
		{<#assign description = StringUtil.wrapString(item.description?if_exists) />
			'uomId' : '${item.uomId}',
			'description' : "${description}"
		},
	</#list>];
	<#assign marketingCostTypeData = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "MARKETING_COST"), null, null, null, false)/>
	var marketingCostTypeData = [<#list marketingCostTypeData as item>
		{<#assign description = StringUtil.wrapString(item.description?if_exists) />
			'marketingCostTypeId' : '${item.enumId}',
			'name' : "${description}"
		},
	</#list>];
	<#assign marketingType = delegator.findList("MarketingType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "MARKETING"), null, null, null, false)/>
	var marketingType = [<#list marketingType as item>
		{<#assign description = StringUtil.wrapString(item.name?if_exists) />
			'marketingTypeId' : '${item.marketingTypeId}',
			'description' : "${description}"
		},
	</#list>];
	<#assign uomData = delegator.findList("Uom",
							Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId",
							Static["org.ofbiz.entity.condition.EntityOperator"].NOT_EQUAL, "CURRENCY_MEASURE"),
							null, null, null, false)/>
	var uomData = [<#list quantityUomData as item>
		{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
			'uomId' : '${item.uomId}',
			'description' : "${description}"
		},
	</#list>];
	var marketingPlace = [];
	<#assign listProsucts = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false)/>
	listProsucts = [<#list listProsucts as item>{
		productId : "${item.productId?if_exists}",
		productName : "${StringUtil.wrapString(item.productName?if_exists)}",
	},</#list>];
	listEmployee = [<#list listEmployee as item>{
		partyId : "${item.partyId?if_exists}",
		partyFullName : "${StringUtil.wrapString(item.partyFullName?if_exists)}",
	},</#list>];
	var mapEmployee = {<#list listEmployee as item>"${item.partyId?if_exists}": "${StringUtil.wrapString(item.partyFullName?if_exists)}",</#list>};
</script>
<div class="tabbable" style="padding-bottom: 30px;">
	<ul class="nav nav-tabs" id="campaignDetailTabs">
		<li class="active">
			<a data-toggle="tab" href="#general">
				<i class="fa fa-info"></i>&nbsp;${uiLabelMap.generalInfo}
			</a>
		</li>
		<li>
			<a data-toggle="tab" href="#place">
				<i class="fa fa-map-marker"></i>&nbsp;${uiLabelMap.CommonPlace}
			</a>
		</li>
		<li>
			<a data-toggle="tab" href="#product">
				<i class="fa fa-th-list"></i>&nbsp;${uiLabelMap.DmsProduct}
			</a>
		</li>
		<li>
			<a data-toggle="tab" href="#cost">
				<i class="fa fa-money"></i>&nbsp;${uiLabelMap.costTitle}
			</a>
		</li>
		<li>
			<a data-toggle="tab" href="#role">
				<i class="fa fa-users"></i>&nbsp;${uiLabelMap.CommonRole}
			</a>
		</li>
	</ul>
	<div class="tab-content" id="contentCampaign">
		<div class="tab-pane in active" id="general">
			<#include "headerCampaign.ftl"/>
		</div>
		<div class="tab-pane" id="place">
			<#include "marketingPlace.ftl"/>
		</div>
		<div class="tab-pane" id="product">
			<#include "marketingProduct.ftl"/>
		</div>
		<div class="tab-pane" id="cost">
			<#include "marketingCost.ftl"/>
		</div>
		<div class="tab-pane" id="role">
			<#include "marketingRole.ftl"/>
		</div>
	</div>
</div>
<div class="control-action">
	<button id="cancelCampaign" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
	<button id="saveCampaign" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
</div>
<#include "popupAddMarketingProduct.ftl"/>
<#include "popupAddMarketingCost.ftl"/>
<#include "popupAddMarketingRole.ftl"/>
