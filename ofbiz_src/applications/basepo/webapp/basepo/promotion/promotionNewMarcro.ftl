<#macro buildCondition ruleIndex=0 condIndex=0 promoCond="" sizeCond=0>
	<#assign promoRule_index = ruleIndex?default(0) />
	<#assign promoCond_index = condIndex?default(0) />
	<#if promoCond?has_content><input type="hidden" name="productPromoCondSeqId_c_${promoCond_index}_o_${promoRule_index}" value="${promoCond.productPromoCondSeqId}"/></#if>
	<input type="hidden" name="isRemoveCond_c_${promoCond_index}_o_${promoRule_index}" value="N"/>
	<div class="form-legend" id="form-legend_c_${promoCond_index}_o_${promoRule_index}">
		<div class="contain-legend">
			<span class="content-legend text-normal">
				${uiLabelMap.BSCondition} <#if sizeCond &gt; 1>${promoCond_index + 1} </#if>&nbsp;
				<a href="javascript:OlbPromoRules.deleteCond(${promoRule_index}, ${promoCond_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
			</span>
		</div>
		<div class="contain">
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCondition}</label>
				</div>
				<div class="span7">
					<div class="span10"><div id="inputParamEnumId_c_${promoCond_index}_o_${promoRule_index}" name="inputParamEnumId_c_${promoCond_index}_o_${promoRule_index}"></div></div>
					<div class="span2"><div id="operatorEnumId_c_${promoCond_index}_o_${promoRule_index}" name="operatorEnumId_c_${promoCond_index}_o_${promoRule_index}"></div></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSProductName}</label>
				</div>
				<div class="span7">
					<div class="container-add-minus">
						<div id="productIdListCond_c_${promoCond_index}_o_${promoRule_index}" class="close-box-custom"></div>
					</div>
					<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowProductPromo('productIdListCond_c_${promoCond_index}_o_${promoRule_index}');"><i class="fa fa-bars"></i></a>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCategoryName}</label>
				</div>
				<div class="span7">
					<input type="hidden" name="productPromoApplEnumId_c_${promoCond_index}_o_${promoRule_index}" value="PPPA_INCLUDE"/>
                  	<input type="hidden" name="includeSubCategories_c_${promoCond_index}_o_${promoRule_index}" value="Y"/>
                  	<div class="container-add-minus">
                  		<div id="productCatIdListCond_c_${promoCond_index}_o_${promoRule_index}" class="close-box-custom"></div>
                  	</div>
	              	<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowCategoryPromo('productCatIdListCond_c_${promoCond_index}_o_${promoRule_index}');"><i class="fa fa-bars"></i></a>
				</div>
			</div>
			<#--
			<div class="row-fluid" style="display:none">
				<div class="span5">
					<label>${uiLabelMap.BSParty}</label>
				</div>
				<div class="span7">
					<div id="ddbcpartyIdCond_c_${promoCond_index}_o_${promoRule_index}">
						<div id="partyGridCond_c_${promoCond_index}_o_${promoRule_index}"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid" style="display:none">
				<div class="span5">
					<label>${uiLabelMap.BSPartyGroup}</label>
				</div>
				<div class="span7">
					<div id="ddbcpartyGrpMemberIdCond_c_${promoCond_index}_o_${promoRule_index}">
						<div id="partyGrpMemberGridCond_c_${promoCond_index}_o_${promoRule_index}"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid" style="display:none">
				<div class="span5">
					<label>${uiLabelMap.BSRoleType}</label>
				</div>
				<div class="span7">
					<div id="ddbcroleTypeIdCond_c_${promoCond_index}_o_${promoRule_index}">
						<div id="roleTypeGridCond_c_${promoCond_index}_o_${promoRule_index}"></div>
					</div>
				</div>
			</div>
			-->
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.ProductConditionValue}</label>
				</div>
				<div class="span7">
					<input type="hidden" size="25" id="condValue_c_${promoCond_index}_o_${promoRule_index}" name="condValue_c_${promoCond_index}_o_${promoRule_index}" value="<#if promoCond?has_content>${promoCond.condValue?if_exists}</#if>" class="span12"/>
					<div id="condValueTmp_c_${promoCond_index}_o_${promoRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSUsePriceWithTax}</label>
				</div>
				<div class="span7">
					<div id="usePriceWithTax_c_${promoCond_index}_o_${promoRule_index}" name="usePriceWithTax_c_${promoCond_index}_o_${promoRule_index}"></div>
				</div>
			</div>
		</div><!--.contain-->
	</div><!--.form-legend-->
</#macro>
<#macro buildAction ruleIndex=0 actionIndex=0 promoAction="" sizeAction=0>
	<#assign promoRule_index = ruleIndex?default(0) />
	<#assign promoAction_index = actionIndex?default(0) />
	<#if promoAction?has_content><input type="hidden" name="productPromoActionSeqId_a_${promoAction_index}_o_${promoRule_index}" value="${promoAction.productPromoActionSeqId}"/></#if>
	<input type="hidden" name="isRemoveAction_a_${promoAction_index}_o_${promoRule_index}" value="N"/>
	<div class="form-legend" id="form-legend_a_${promoAction_index}_o_${promoRule_index}">
		<div class="contain-legend">
			<span class="content-legend text-normal">
				${uiLabelMap.BSAction} <#if sizeAction &gt; 1>${promoAction_index + 1} </#if>&nbsp;
				<a href="javascript:OlbPromoRules.deleteAction(${promoRule_index}, ${promoAction_index});"><i class="fa-times-circle open-sans open-sans-index"></i></a>
			</span>
		</div>
		<div class="contain">
			<input type="hidden" name="orderAdjustmentTypeId_a_${promoAction_index}_o_${promoRule_index}" value="<#if promoAction?has_content>${promoAction.orderAdjustmentTypeId?default('PROMOTION_ADJUSTMENT')}<#else>PROMOTION_ADJUSTMENT</#if>" />
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSAction}</label>
				</div>
				<div class="span7">
					<div id="productPromoActionEnumId_a_${promoAction_index}_o_${promoRule_index}" name="productPromoActionEnumId_a_${promoAction_index}_o_${promoRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSProductName}</label>
				</div>
				<div class="span7">
					<div class="container-add-minus">
						<div id="productIdListAction_a_${promoAction_index}_o_${promoRule_index}" class="close-box-custom"></div>
					</div>
					<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowProductPromo('productIdListAction_a_${promoAction_index}_o_${promoRule_index}');"><i class="fa fa-bars"></i></a>
				</div>
			</div>
			<input type="hidden" name="productPromoApplEnumIdAction_a_${promoAction_index}_o_${promoRule_index}" value="PPPA_INCLUDE"/>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSCategoryName}</label>
				</div>
				<div class="span7">
					<input type="hidden" name="includeSubCategoriesAction_a_${promoAction_index}_o_${promoRule_index}" value="Y"/>
					<div class="container-add-minus">
	              		<div id="productCatIdListAction_a_${promoAction_index}_o_${promoRule_index}"></div>
	              	</div>
	              	<a href="javascript:void(0)" class="btn btn-mini" onClick="OlbPromoRulesScript.showWindowCategoryPromo('productCatIdListAction_a_${promoAction_index}_o_${promoRule_index}');"><i class="fa fa-bars"></i></a>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.ProductQuantity}</label>
				</div>
				<div class="span7">
					<input type="hidden" id="quantity_a_${promoAction_index}_o_${promoRule_index}" name="quantity_a_${promoAction_index}_o_${promoRule_index}" value="<#if promoAction?has_content>${promoAction.quantity?if_exists}</#if>" class="span12"/>
					<div id="quantityTmp_a_${promoAction_index}_o_${promoRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSAmountOrPercent}</label>
				</div>
				<div class="span7">
					<input type="hidden" id="amount_a_${promoAction_index}_o_${promoRule_index}" name="amount_a_${promoAction_index}_o_${promoRule_index}" value="<#if promoAction?has_content>${promoAction.amount?if_exists}</#if>" class="span12"/>
					<div id="amountTmp_a_${promoAction_index}_o_${promoRule_index}"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<label>${uiLabelMap.BSOperator}</label>
				</div>
				<div class="span7">
					<div id="productPromoActionOperEnumId_a_${promoAction_index}_o_${promoRule_index}" name="productPromoActionOperEnumId_a_${promoAction_index}_o_${promoRule_index}"></div>
				</div>
			</div>
		</div>
	</div><!--.form-legend-->
</#macro>