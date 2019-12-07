<style type="text/css">
	#roleTypeIds {
		margin-bottom:0;
	}
	#productStoreId_chzn, #roleTypeIds_chzn {
		margin-bottom:0;
		width:100% !important;
	}
	#productStoreId_chzn .chzn-drop, #roleTypeIds_chzn .chzn-drop{
		width:100% !important;
	}
	.div-size-small .field-lookup input[type="text"], 
	.div-size-small .view-calendar input[type="text"]{
		padding: 0 6px;
		min-height: 25px;
		margin-bottom:0;
	}
	.div-size-small .view-calendar button {
		height: 27px;
		margin-left: -5px;
		margin-top:2px;
	}
	.div-size-small .field-lookup a:before {
		height: 22px;
		margin-top:0;
	}
	.exhibited-group {
		<#if productPromo?exists && productPromo.productPromoTypeId?exists && (productPromo.productPromoTypeId == "EXHIBITED")>
			display: block;
		<#else>
			display: none;
		</#if>
	}
	.containNumber > div {display:inline-block}
	.containNumber > span, .containNumber > a {vertical-align:top}
</style>
<#assign salesMethodChannels = delegator.findByAnd("Enumeration", {"enumTypeId" : "SALES_METHOD_CHANNEL"}, null, false)/>
<script type="text/javascript">
	<#if salesMethodChannels?exists>
		var salesMethodChannelData = [
		<#list salesMethodChannels as item>
		{
			channelId: "${item.enumId}",
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
		];
	<#else>
		var salesMethodChannelData = [];
	</#if>
	
	var dataPromoType = [];
	<#--
	<#if promoTypes?exists>
		<#list promoTypes as promoTypeItem>
			var mapItem = {};
			<#assign description = StringUtil.wrapString(promoTypeItem.get("description", locale))/>
			mapItem.productPromoTypeId = "${promoTypeItem.productPromoTypeId}";
			mapItem.description = "${description}";
			dataPromoType[${promoTypeItem_index}] = mapItem;
		</#list>
	</#if>
	-->
	var productStoreData = [];
	<#--
	<#list productStores as productStoreItem>
		var row = {};
		row['productStoreId'] = '${productStoreItem.productStoreId}';
		row['description'] = '${productStoreItem.get("storeName", locale)}';
		productStoreData[${productStoreItem_index}] = row;
	</#list>
	-->
	var roleTypeData = [];
	<#--
	<#list roleTypes as roleTypeItem>
		var row = {};
		row['roleTypeId'] = '${roleTypeItem.roleTypeId}';
		row['description'] = '${StringUtil.wrapString(roleTypeItem.get("description", locale))}';
		roleTypeData[${roleTypeItem_index}] = row;
	</#list>
	-->
</script>
<div class="row-fluid">
	<#if productPromoId?has_content && productPromo?exists>
		<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>updateProductPromoAdvance</@ofbizUrl>" name="editProductPromo" id="editProductPromo">
			<input type="hidden" name="productPromoId" value="${productPromoId}">
	<#else>
		<form method="post" class="form-horizontal basic-custom-form" action="<@ofbizUrl>createProductPromoAdvance</@ofbizUrl>" name="editProductPromo" id="editProductPromo">
			<input type="hidden" name="productPromoStatusId" value="PROMO_CREATED">
	</#if>
		<div class="row-fluid">
			<div class="span6">
				<div class="control-group">
					<label class="control-label" for="productPromoId">${uiLabelMap.DAAbbPromotionId}</label>
					<div class="controls">
						<input type="text" class="span12" name="productPromoId" id="productPromoId" value="<#if productPromo?exists>${productPromo.productPromoId?if_exists}<#else>${parameters.productPromoId?if_exists}</#if>"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label required" for="promoName">${uiLabelMap.DAAbbPromotionName}</label>
					<div class="controls">
						<input type="text" class="span12" name="promoName" id="promoName" value="<#if productPromo?exists>${productPromo.promoName?if_exists}<#else>${parameters.promoName?if_exists}</#if>" size="100"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label required" for="salesMethodChannelEnumId">${uiLabelMap.DAChannel}</label>
					<div class="controls">
						<div id="salesMethodChannelEnumId" name="salesMethodChannelEnumId"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label required" for="productPromoTypeId">${uiLabelMap.DelysPromotionType}</label>
					<div class="controls">
						<div id="productPromoTypeId" name="productPromoTypeId"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label required" for="productStoreIds">${uiLabelMap.DelysPromotionStore}</label>
					<div class="controls">
						<div id="productStoreIds"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label required" for="roleTypeIds">${uiLabelMap.DelysRoleTypeApply}</label>
					<div class="controls">
						<div id="roleTypeIds"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="promoText">${uiLabelMap.DAContent}</label>
					<div class="controls">
						<textarea id="promoText" name="promoText" data-maxlength="50" rows="2" style="resize: vertical;margin-bottom:0" class="span12"><#if productPromo?exists>${productPromo.promoText?if_exists}</#if></textarea>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="promoSalesTargets">${uiLabelMap.DelysSalesTargets}</label>
					<div class="controls">
						<input type="text" name="promoSalesTargets" id="promoSalesTargets" class="span12" value="<#if productPromo?exists>${productPromo.promoSalesTargets?if_exists}</#if>"/>														
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.DAFinalizationMethod}</label>
					<div class="controls">
						<textarea id="paymentMethod" data-maxlength="50" name="paymentMethod" class="span12" style="resize: vertical;margin-bottom:0"><#if productPromo?exists>${productPromo.paymentMethod?if_exists}</#if></textarea>
					</div>
				</div>
			</div><!--.span6-->
			<div class="span6">
				<div class="control-group">
					<label class="control-label required" for="fromDate">${uiLabelMap.DAFromDate}</label>
					<div class="controls">
						<div id="fromDate"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="thruDate">${uiLabelMap.DAThruDateOld}</label>
					<div class="controls">
						<div id="thruDate"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="showToCustomer">${uiLabelMap.DAPublic}</label>
					<div class="controls">
						<div id="showToCustomer" name="showToCustomer"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.DelysBudgetTotal}</label>
					<div class="controls">
						<#if promoBudgetDist?exists>
							<#assign budgetTotal = promoBudgetDist.budgetId?if_exists> 
						</#if>
						<div class="div-size-small">
							<#if productPromoId?has_content && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">
								<#if promoBudgetDist?has_content>
									${promoBudgetDist.budgetId}
								<#else>
									${uiLabelMap.DelysNoBudgetDistributionApply}
								</#if>
							<#else>
								<#if productPromo?exists>
									<@htmlTemplate.lookupField formName="editProductPromo" name="budgetId" id="budgetId"
										value="${budgetTotal?if_exists}" fieldFormName="lookupBudgetPromotion" showDescription="true"/>	
								<#else>
									<@htmlTemplate.lookupField formName="editProductPromo" name="budgetId" id="budgetId"
										value="${parameters.budgetId?if_exists}" fieldFormName="lookupBudgetPromotion" showDescription="true"/>	
								</#if>
							</#if>
						</div>
					</div>
				</div>
				<div class="control-group">
					<div class="span12">
						<label class="control-label">${uiLabelMap.DelysMiniRevenue}</label>
						<div class="controls">
							<div class="div-size-small">
								<#if promoMiniRevenue?exists>
									<#assign miniRevenue = promoMiniRevenue.budgetId?if_exists>
								</#if>															
								<@htmlTemplate.lookupField formName="editProductPromo" name="miniRevenueId" id="miniRevenueId"																
									value="${miniRevenue?if_exists}" fieldFormName="lookupBudgetPromotion" showDescription="true"/>
							</div>
						</div>
					</div>
				</div>	
				<#--
				<#if productPromo?exists && productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId != "PROMO_CREATED">							
					<#if productPromo.productPromoTypeId?exists && (productPromo.productPromoTypeId == "EXHIBITED" || productPromo.productPromoTypeId == "PROMOTION")>										
						<div class="control-group">
							<label class="control-label">${uiLabelMap.DelysMiniRevenue}</label>
							<div class="controls">
								<div class="div-size-small">															
									<#if promoMiniRevenue?has_content>
										${promoMiniRevenue.budgetId?if_exists}
									<#else>
										${uiLabelMap.DelysNoMiniRevenueApply}	
									</#if>				
								</div>
							</div>
						</div>
					<#elseif productPromo?exists && productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId=="ACCUMULATE">
						<div class="control-group">
							<label class="control-label" for="promoSalesTargets">${uiLabelMap.DelysSalesTargets}</label>
							<div class="controls">
								<div class="span12 input-prepend">														
									<input type="text" name="promoSalesTargets" id="promoSalesTargets" value="${productPromo.promoSalesTargets?if_exists}" disabled="disabled">
								</div>
							</div>
						</div>
					</#if>
				<#else>
					<div class="control-group">
						<div class="span12">
							<label class="control-label">${uiLabelMap.DelysMiniRevenue}</label>
							<div class="controls">
								<div class="span12">
									<div class="div-size-small">
										<#if promoMiniRevenue?exists>
											<#assign miniRevenue = promoMiniRevenue.budgetId?if_exists>
										</#if>															
										<@htmlTemplate.lookupField formName="editProductPromo" name="miniRevenueId" id="miniRevenueId"																
											value="${miniRevenue?if_exists}" fieldFormName="lookupBudgetPromotion" showDescription="true"/>
									</div>
								</div>
							</div>
						</div>
					</div>	
					<div class="control-group">
						<label class="control-label" for="promoSalesTargets">${uiLabelMap.DelysSalesTargets}</label>
						<div class="controls">
							<div class="span12 input-prepend">														
								<input type="text" name="promoSalesTargets" id="promoSalesTargets" value="<#if productPromo?exists>${productPromo.promoSalesTargets?if_exists}</#if>" id="salesTarget"
									<#if productPromo?exists && productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId != "ACCUMULATE">disabled="disabled"</#if>>														
							</div>
						</div>
					</div>
				</#if>
				<#if productPromo?exists && productPromo.createdDate?exists>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.DACreatedDate}</label>
						<div class="controls" style="padding-top: 3px;">
							<div class="span12">
								 ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productPromo.createdDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}
							</div>
						</div>
					</div>
				</#if>
				-->
				<div class="control-group">
					<label class="control-label" for="useLimitPerOrder">${uiLabelMap.DAAbbUseLimitPerOrder}</label>
					<div class="controls containNumber">
						<#--
						<input type="text" name="useLimitPerOrder" id="useLimitPerOrder" value="<#if productPromo?exists>${productPromo.useLimitPerOrder?if_exists}<#else>${parameters.useLimitPerOrder?if_exists}</#if>" size="20"/>
						-->
						<div id="useLimitPerOrder"></div>
						<span class="help-button" data-rel="popover" data-trigger="hover" data-placement="left" data-content="${uiLabelMap.DAUseLimitPerOrder}" title="">?</span>
						<a href="javascript:void(0)" onClick="removeNumberInputValue('useLimitPerOrder')" class="help-button">/</a>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="useLimitPerCustomer">${uiLabelMap.DAAbbUseLimitPerCustomer}</label>
					<div class="controls containNumber">
						<#--
						<input type="text" name="useLimitPerCustomer" id="useLimitPerCustomer" value="<#if productPromo?exists>${productPromo.useLimitPerCustomer?if_exists}<#else>${parameters.useLimitPerCustomer?if_exists}</#if>" size="20"/>
						-->
						<div id="useLimitPerCustomer"></div>
						<span class="help-button" data-rel="popover" data-trigger="hover" data-placement="left" data-content="${uiLabelMap.DAUseLimitPerCustomer}" title="">?</span>
						<a href="javascript:void(0)" onClick="removeNumberInputValue('useLimitPerCustomer')" class="help-button">/</a>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label" for="useLimitPerPromotion">${uiLabelMap.DAAbbUseLimitPerPromotion}</label>
					<div class="controls containNumber">
						<#--
						<input type="text" name="useLimitPerPromotion" id="useLimitPerPromotion" value="<#if productPromo?exists>${productPromo.useLimitPerPromotion?if_exists}<#else>${parameters.useLimitPerPromotion?if_exists}</#if>" size="20"/>
						-->
						<div id="useLimitPerPromotion"></div>
						<span class="help-button" data-rel="popover" data-trigger="hover" data-placement="left" data-content="${uiLabelMap.DAUseLimitPerPromotion}" title="">?</span>
						<a href="javascript:void(0)" onClick="removeNumberInputValue('useLimitPerPromotion')" class="help-button">/</a>
					</div>
				</div>
			</div><!--.span6-->
		</div><!--.row-->
		
		<#--
		<div class="row-fluid wizard-actions">
			<#if productPromo?exists && productPromoId?has_content && productPromo.productPromoStatusId?exists>
				<#if productPromo.productPromoStatusId == "PROMO_CREATED">
					<button type="submit" id="editPromotion" class="btn btn-primary btn-small">
						<i class="icon-ok open-sans"></i>${uiLabelMap.CommonUpdate} 
					</button>
				</#if>
			<#else>
				<a class="btn btn-small" href="productPromoList">
					<i class="icon-remove open-sans"></i>${uiLabelMap.DACancel}
				</a>
				<button type="submit" id="editPromotion" class="btn btn-primary btn-small">
					<i class="icon-ok open-sans"></i>${uiLabelMap.DACreate}
				</button>
			</#if>
		</div>
		-->
	</form>
</div><!--.row-fluid-->

<script type="text/javascript">
	$(function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		$('[data-rel=tooltip]').tooltip();
		$('[data-rel=popover]').popover({container:'body'});
		$(".chzn-select").chosen({search_contains: true});
		
		$("#productPromoId").jqxInput({height: 25, maxLength:20});
		$("#promoName").jqxInput({height:25, maxLength:100});
		$('#promoName').jqxInput('focus');
		$("#promoSalesTargets").jqxInput({height:25});
		<#if productPromo?exists>
			$("#productPromoId").jqxInput({disabled: true});
		</#if>
		$("#useLimitPerOrder").jqxNumberInput({width: 218, height: 25, spinButtons:true, decimalDigits: 0});
		$("#useLimitPerCustomer").jqxNumberInput({width: 218, height: 25,  spinButtons:true, decimalDigits: 0});
		$("#useLimitPerPromotion").jqxNumberInput({width: 218, height: 25, spinButtons:true, decimalDigits: 0});
		$("#useLimitPerOrder").jqxNumberInput('val', null);
		$("#useLimitPerCustomer").jqxNumberInput('val', null);
		$("#useLimitPerPromotion").jqxNumberInput('val', null);
		
		$("#fromDate").jqxDateTimeInput({width: '218px', height: '25px', allowNullDate: true, value: null, formatString: 'yyyy-MM-dd HH:mm:ss'});
		<#if productPromo?exists && productPromo.fromDate?exists>
			$('#fromDate').jqxDateTimeInput('setDate', "${productPromo.fromDate}");
		</#if>
		$("#thruDate").jqxDateTimeInput({width: '218px', height: '25px', allowNullDate: true, value: null, formatString: 'yyyy-MM-dd HH:mm:ss'});
		<#if productPromo?exists && productPromo.thruDate?exists>
			$('#thruDate').jqxDateTimeInput('setDate', "${productPromo.thruDate}");
		</#if>
		
		var config = {
    		width: "100%",
    		key: "channelId",
    		value: "description",
    		autoDropDownHeight: true,
    		displayDetail: false,
    		disabled: <#if productPromo?exists>true<#else>false</#if>,
    		placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}"
    	};
		jSalesCommon.initComboBox($("#salesMethodChannelEnumId"), salesMethodChannelData, config, [<#if channelId?exists>"${channelId}"</#if>]);
		
		<#--<#if productPromo?exists && productPromo.productPromoTypeId?exists>true<#else>false</#if>-->
		var configPromoType = {
			width: "100%",
    		key: "productPromoTypeId",
    		value: "description",
    		autoDropDownHeight: true,
    		disabled: true,
    		placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
    		useUrl: true,
    		url: "<#if channelId?exists>getListProductPromoTypeAjax?channelId=${channelId}<#else>#</#if>",
    		root: "listPromoType",
    		datafields: [
    			{name: 'productPromoTypeId'},
    			{name: 'description'}
    		]
		}
		jSalesCommon.initDropDownList($('#productPromoTypeId'), dataPromoType, configPromoType, [<#if productPromo?exists && productPromo.productPromoTypeId?exists>"${productPromo.productPromoTypeId}"</#if>]);
		
		var dataShowToCustomer = [
			{id : "N", description : "${StringUtil.wrapString(uiLabelMap.DACHNo)}"},
			{id : "Y", description : "${StringUtil.wrapString(uiLabelMap.DACHYes)}"}
		];
		var configShowToCustomer = {
			width: "218px",
    		key: "id",
    		value: "description",
    		autoDropDownHeight: true,
    		disabled: false,
    		placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
		}
		jSalesCommon.initDropDownList($('#showToCustomer'), dataShowToCustomer, configShowToCustomer, ["Y"]);
		
		// list ProductStore =====================================================================================
		var listProductStoreSelected = [];
		<#if productPromo?exists && productStorePromoApplIso?exists>
		    <#if productStorePromoApplIso?is_collection>
				<#list productStorePromoApplIso as productStoreIdApply>
					listProductStoreSelected.push("${productStoreIdApply.productStoreId}");
				</#list>
		    <#else>
				listProductStoreSelected.push("${productStorePromoApplIso.productStoreId}");
		    </#if>
	    </#if>
	    var configProductStore = {
    		width: "100%",
    		height: 25,
    		key: "productStoreId",
    		value: "storeName",
    		displayDetail: true,
    		disabled: true,
    		placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
    		dropDownWidth: 'auto',
    		multiSelect: true,
    		useUrl: true,
    		url: "<#if channelId?exists>getListProductStoreAjax?channelId=${channelId}<#else>#</#if>",
    		root: "listProductStore",
    		datafields: [
	            { name: 'productStoreId' },
	            { name: 'storeName' }
	        ]
    	};
	    jSalesCommon.initComboBox($("#productStoreIds"), productStoreData, configProductStore, listProductStoreSelected);
		
		// list roleTypes ==========================================================================================
		var listRoleTypeSelected = [];
		<#if productPromo?exists && promoRoleTypeApplyIso?exists>
		    <#if promoRoleTypeApply?is_collection>
				<#list promoRoleTypeApplyIso as partyRoleTypesApply>
					listRoleTypeSelected.push("${partyRoleTypesApply.roleTypeId}");
				</#list>
		    <#else>
				listRoleTypeSelected.push("${promoRoleTypeApplyIso.roleTypeId}");
		    </#if>
	    </#if>
	    var configRoleType = {
    		width: "100%",
    		height: 25,
    		key: "roleTypeId",
    		value: "description",
    		displayDetail: true,
    		disabled: true,
    		placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}",
    		dropDownWidth: 'auto',
    		multiSelect: true,
    		useUrl: true,
    		url: "<#if channelId?exists>getListRoleTypeByGroupAjax?channelId=${channelId}<#else>#</#if>",
    		root: "listRoleType",
    		datafields: [
	            { name: 'roleTypeId' },
	            { name: 'description' }
	        ]
    	};
	    jSalesCommon.initComboBox($("#roleTypeIds"), roleTypeData, configRoleType, listRoleTypeSelected);
	    
	// Event listening ==========================================================================================
	
		$("#salesMethodChannelEnumId").on('change', function (event) {
	        var valueSelected = $("#salesMethodChannelEnumId").jqxComboBox('getSelectedItem');
	        if (valueSelected != null && valueSelected != undefined && valueSelected.value != undefined) {
	        	$("#info_loader").show();
	        	var tmpAddress = $("#productPromoTypeId").jqxDropDownList('source');
			 	tmpAddress._source.url = "getListProductPromoTypeAjax?channelId="+valueSelected.value;
			 	$("#productPromoTypeId").jqxDropDownList('source', tmpAddress);
			 	$("#productPromoTypeId").jqxDropDownList({disabled : false});
			 	
			 	var tmpAddress2 = $("#productStoreIds").jqxComboBox('source');
			 	tmpAddress2._source.url = "getListProductStoreAjax?channelId="+valueSelected.value;
			 	$("#productStoreIds").jqxComboBox('source', tmpAddress2);
			 	$("#productStoreIds").jqxComboBox({disabled : false});
			 	
			 	var tmpAddress3 = $("#roleTypeIds").jqxComboBox('source');
			 	tmpAddress3._source.url = "getListRoleTypeByGroupAjax?channelId="+valueSelected.value;
			 	$("#roleTypeIds").jqxComboBox('source', tmpAddress3);
			 	$("#roleTypeIds").jqxComboBox({disabled : false});
			 	$("#info_loader").hide();
	        }
	    });
		
		$('#productPromoTypeId').on("change", function(){
			var productPromoTypeId = $('#productPromoTypeId').val();
			if (productPromoTypeId == "EXHIBITED") {
				$(".exhibited-group").css("display", "block");
			} else {
				$(".exhibited-group").css("display", "none");
			}
			if (productPromoTypeId != null && !(/^\s*$/.test(productPromoTypeId))) {
				$.ajax({
				type: 'POST',
				url: 'getPromoConfigByTypeAjax',
				data: {
					productPromoTypeId: productPromoTypeId,
				},
				beforeSend: function(){
					$("#info_loader").show();
				},
				success: function(data){
					var listPromoConfig = data.listPromoConfig;
					var isSeted = false;
					if (listPromoConfig != null) {
						for (var i = 0; i < listPromoConfig.length; i++) {
							var item = listPromoConfig[i];
							if (item.fieldName == "useLimitPerOrder") {
								$("#useLimitPerOrder").jqxNumberInput('val', item.value);
								if (item.editable == "N") {
									$("#useLimitPerOrder").jqxNumberInput({disabled : true});
									$("#useLimitPerOrder").closest("div.containNumber").children("a").hide();
								}
								isSeted = true;
							} else if (item.fieldName == "useLimitPerCustomer") {
								$("#useLimitPerCustomer").jqxNumberInput('val', item.value);
								if (item.editable == "N") {
									$("#useLimitPerCustomer").jqxNumberInput({disabled : true});
									$("#useLimitPerCustomer").closest("div.containNumber").children("a").hide();
								}
								isSeted = true;
							} else if (item.fieldName == "useLimitPerPromotion") {
								$("#useLimitPerPromotion").jqxNumberInput('val', item.value);
								if (item.editable == "N") {
									$("#useLimitPerPromotion").jqxNumberInput({disabled : true});
									$("#useLimitPerPromotion").closest("div.containNumber").children("a").hide();
								}
								isSeted = true;
							}
						}
					}
					if (!isSeted) {
						resetAllField();
					}
				},
				error: function(){},
				complete: function(){
					$("#info_loader").hide();
				}
			});
			}
		});
		<#--<#if productPromo?exists && productPromo.productPromoTypeId?exists && (productPromo.productPromoTypeId == "EXHIBITED")>
			$(".exhibited-group").css("display", "block");
		</#if>-->
	});
	
	function removeNumberInputValue(id) {
		$("#" + id).jqxNumberInput('val', null);
	}
	
	function resetAllField() {
		$("#useLimitPerOrder").jqxNumberInput({disabled : false});
		$("#useLimitPerOrder").jqxNumberInput('val', null);
		$("#useLimitPerOrder").closest("div.containNumber").children("a").show();
		
		$("#useLimitPerCustomer").jqxNumberInput({disabled : false});
		$("#useLimitPerCustomer").jqxNumberInput('val', null);
		$("#useLimitPerCustomer").closest("div.containNumber").children("a").show();
		
		$("#useLimitPerPromotion").jqxNumberInput({disabled : false});
		$("#useLimitPerPromotion").jqxNumberInput('val', null);
		$("#useLimitPerPromotion").closest("div.containNumber").children("a").show();
	}
	<#--
	/*
	$("#productPromoTypeId").change(function(){
		var selectPromoType = $(this).val();
		if(selectPromoType == "ACCUMULATE"){
			$("input[name='miniRevenueId']").prop('disabled', true);
			$("input[name='promoSalesTargets']").prop('disabled', false);
		}else if(selectPromoType == "EXHIBITED" || selectPromoType == "PROMOTION"){
			$("input[name='promoSalesTargets']").prop('disabled', true);
			$("input[name='miniRevenueId']").prop('disabled', false);
		}
	});
	*/
	<#if productPromo?exists && productPromo.productPromoTypeId?exists && productPromo.productPromoTypeId == "ACCUMULATE">			
		if($("input[name='miniRevenueId']").length > 0){
			$("input[name='miniRevenueId']").prop('disabled', true);
		}
	</#if>
	-->
</script>