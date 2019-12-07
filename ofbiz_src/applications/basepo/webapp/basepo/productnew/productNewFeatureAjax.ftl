<#if featureTypes?exists>
	<#list featureTypes as featureType>
		<div class="row-fluid">
			<div class="span4">
				<label>${featureType.description?if_exists}</label>
			</div>
			<div class="span8">
				<div id="featureProduct_${featureType.productFeatureTypeId}"></div>
			</div>
		</div>
	</#list>
	<#if !productFeatureIdsMap?exists>
		<#assign productFeatureIdsMap = {}/>
	</#if>
	<script type="text/javascript">
		<#list featureTypes as featureType>
			<#assign productFeatureTypeId = featureType.productFeatureTypeId/>
			<#assign productFeatures = delegator.findByAnd("ProductFeature", {"productFeatureTypeId": featureType.productFeatureTypeId}, null, false)!/>
			var productFeatureData_${productFeatureTypeId} = [
				<#if productFeatures?has_content>
					<#list productFeatures as productFeature>
					{	"productFeatureId": "${productFeature.productFeatureId}",
						"description": "${StringUtil.wrapString(productFeature.description?if_exists)}"
					},
					</#list>
				</#if>
			];
			var configProdFeatureType_${productFeatureTypeId} = {
				width:'100%',
				height: 25,
				key: "productFeatureId",
	    		value: "description",
	    		displayDetail: false,
				dropDownWidth: 'auto',
				placeHolder: "${uiLabelMap.BSClickToChoose}",
				autoDropDownHeight: true,
			};
			new OlbDropDownList($("#featureProduct_${productFeatureTypeId}"), productFeatureData_${productFeatureTypeId}, configProdFeatureType_${productFeatureTypeId}, [<#if productFeatureIdsMap[productFeatureTypeId]?exists>"${productFeatureIdsMap[productFeatureTypeId]}"</#if>]);
		</#list>
	</script>
</#if>