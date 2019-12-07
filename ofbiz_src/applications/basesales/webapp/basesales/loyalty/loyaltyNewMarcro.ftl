<#macro buildCondition ruleIndex=0 condIndex=0 loyaltyCond="" sizeCond=0>
	<#assign loyaltyRule_index = ruleIndex?default(0) />
	<#assign loyaltyCond_index = condIndex?default(0) />
	<#if loyaltyCond?has_content><input type="hidden" name="loyaltyCondSeqId_c_${loyaltyCond_index}_o_${loyaltyRule_index}" value="${loyaltyCond.loyaltyCondSeqId}"/></#if>
	<input type="hidden" name="isRemoveCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}" value="N"/>
	<div class="form-legend" id="form-legend_c_${loyaltyCond_index}_o_${loyaltyRule_index}">
		<div class="contain-legend">
			<span class="content-legend text-normal">
				${uiLabelMap.BSCondition} <#if sizeCond &gt; 1>${loyaltyCond_index + 1} </#if>&nbsp;
				<a href="javascript:OlbLoyaltyRules.deleteCond(${loyaltyRule_index}, ${loyaltyCond_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
			</span>
		</div>
		<div class="contain">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCondition}</label>
				</div>
				<div class="span7">
					<div class="span10"><div id="inputParamEnumId_c_${loyaltyCond_index}_o_${loyaltyRule_index}" name="inputParamEnumId_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div></div>
					<div class="span2"><div id="operatorEnumId_c_${loyaltyCond_index}_o_${loyaltyRule_index}" name="operatorEnumId_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSProductName}</label>
				</div>
				<div class="span7">
					<div id="productIdListCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}" class="close-box-custom"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCategoryName}</label>
				</div>
				<div class="span7">
					<input type="hidden" name="loyaltyApplEnumId_c_${loyaltyCond_index}_o_${loyaltyRule_index}" value="LPPA_INCLUDE"/>
                  	<input type="hidden" name="includeSubCategories_c_${loyaltyCond_index}_o_${loyaltyRule_index}" value="Y"/>
                  	<div id="productCatIdListCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}" class="close-box-custom"></div>
				</div>
			</div>
			<div class="row-fluid" style="display:none">
				<div class="span5">
					<label>${uiLabelMap.BSParty}</label>
				</div>
				<div class="span7">
					<div id="ddbcpartyIdCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}">
						<div id="partyGridCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid" style="display:none">
				<div class="span5">
					<label>${uiLabelMap.BSPartyGroup}</label>
				</div>
				<div class="span7">
					<div id="ddbcpartyGrpMemberIdCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}">
						<div id="partyGrpMemberGridCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid" style="display:none">
				<div class="span5">
					<label>${uiLabelMap.BSRoleType}</label>
				</div>
				<div class="span7">
					<div id="ddbcroleTypeIdCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}">
						<div id="roleTypeGridCond_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.ProductConditionValue}</label>
				</div>
				<div class="span7">
					<input type="hidden" size="25" id="condValue_c_${loyaltyCond_index}_o_${loyaltyRule_index}" name="condValue_c_${loyaltyCond_index}_o_${loyaltyRule_index}" value="<#if loyaltyCond?has_content>${loyaltyCond.condValue?if_exists}</#if>" class="span12"/>
					<div id="condValueTmp_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSUsePriceWithTax}</label>
				</div>
				<div class="span7">
					<div id="usePriceWithTax_c_${loyaltyCond_index}_o_${loyaltyRule_index}" name="usePriceWithTax_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSIsReturnOrder}</label>
				</div>
				<div class="span7">
					<div id="isReturnOrder_c_${loyaltyCond_index}_o_${loyaltyRule_index}" name="isReturnOrder_c_${loyaltyCond_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
		</div><!--.contain-->
	</div><!--.form-legend-->
</#macro>
<#macro buildAction ruleIndex=0 actionIndex=0 loyaltyAction="" sizeAction=0>
	<#assign loyaltyRule_index = ruleIndex?default(0) />
	<#assign loyaltyAction_index = actionIndex?default(0) />
	<#if loyaltyAction?has_content><input type="hidden" name="loyaltyActionSeqId_a_${loyaltyAction_index}_o_${loyaltyRule_index}" value="${loyaltyAction.loyaltyActionSeqId}"/></#if>
	<input type="hidden" name="isRemoveAction_a_${loyaltyAction_index}_o_${loyaltyRule_index}" value="N"/>
	<div class="form-legend" id="form-legend_a_${loyaltyAction_index}_o_${loyaltyRule_index}">
		<div class="contain-legend">
			<span class="content-legend text-normal">
				${uiLabelMap.BSAction} <#if sizeAction &gt; 1>${loyaltyAction_index + 1} </#if>&nbsp;
				<a href="javascript:OlbLoyaltyRules.deleteAction(${loyaltyRule_index}, ${loyaltyAction_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
			</span>
		</div>
		<div class="contain">
			<#--<input type="hidden" name="orderAdjustmentTypeId_a_${loyaltyAction_index}_o_${loyaltyRule_index}" value="<#if loyaltyAction?has_content>${loyaltyAction.orderAdjustmentTypeId?default('PROMOTION_ADJUSTMENT')}<#else>PROMOTION_ADJUSTMENT</#if>" />-->
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSAction}</label>
				</div>
				<div class="span7">
					<div id="loyaltyActionEnumId_a_${loyaltyAction_index}_o_${loyaltyRule_index}" name="loyaltyActionEnumId_a_${loyaltyAction_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSProductName}</label>
				</div>
				<div class="span7">
					<div id="productIdListAction_a_${loyaltyAction_index}_o_${loyaltyRule_index}" class="close-box-custom"></div>
				</div>
			</div>
			<input type="hidden" name="loyaltyApplEnumIdAction_a_${loyaltyAction_index}_o_${loyaltyRule_index}" value="LPPA_INCLUDE"/>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCategoryName}</label>
				</div>
				<div class="span7">
					<input type="hidden" name="includeSubCategoriesAction_a_${loyaltyAction_index}_o_${loyaltyRule_index}" value="Y"/>
	              	<div id="productCatIdListAction_a_${loyaltyAction_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSRatingType}</label>
				</div>
				<div class="span7">
	              	<div id="ratingTypeId_a_${loyaltyAction_index}_o_${loyaltyRule_index}" name="ratingTypeId_a_${loyaltyAction_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSValue}</label>
				</div>
				<div class="span7">
					<input type="hidden" id="quantity_a_${loyaltyAction_index}_o_${loyaltyRule_index}" name="quantity_a_${loyaltyAction_index}_o_${loyaltyRule_index}" value="<#if loyaltyAction?has_content>${loyaltyAction.quantity?if_exists}</#if>" class="span12"/>
					<div id="quantityTmp_a_${loyaltyAction_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSAmountOrPercent}</label>
				</div>
				<div class="span7">
					<input type="hidden" id="amount_a_${loyaltyAction_index}_o_${loyaltyRule_index}" name="amount_a_${loyaltyAction_index}_o_${loyaltyRule_index}" value="<#if loyaltyAction?has_content>${loyaltyAction.amount?if_exists}</#if>" class="span12"/>
					<div id="amountTmp_a_${loyaltyAction_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSOperator}</label>
				</div>
				<div class="span7">
					<div id="loyaltyActionOperEnumId_a_${loyaltyAction_index}_o_${loyaltyRule_index}" name="loyaltyActionOperEnumId_a_${loyaltyAction_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>
			<#--<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCheckInventoryItem}</label>
				</div>
				<div class="span7">
					<div id="isCheckInv_a_${loyaltyAction_index}_o_${loyaltyRule_index}" name="isCheckInv_a_${loyaltyAction_index}_o_${loyaltyRule_index}"></div>
				</div>
			</div>-->
		</div>
	</div><!--.form-legend-->
</#macro>