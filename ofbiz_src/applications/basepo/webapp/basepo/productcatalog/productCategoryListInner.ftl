<script type="text/javascript">
	<#assign prodCatalogCategoryTypeList = delegator.findByAnd("ProdCatalogCategoryType", null, null, false)!/>
	var prodCatalogCategoryTypeData = [
	<#if prodCatalogCategoryTypeList?exists>
		<#list prodCatalogCategoryTypeList as item>
		{	id: "${item.prodCatalogCategoryTypeId}",
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
	</#if>
	];
</script>

<#assign prodCatalogId = parameters.prodCatalogId?default("")/>
<style type="text/css">
	#statusbarjqxListCategory {
		width: 0 !important;
	}
	#pagerjqxListCategory${prodCatalogId}{display:none}
</style>

<div id="jqxListCategory${prodCatalogId}"></div>

<#assign addType = "popup"/>
<#assign alternativeAddPopup="alterpopupWindow"/>
<#assign titleProperty="BSListCategory"/>

<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSQuickCreateNew}@javascript:OlbProductCategoryList.openQuickCreateNew();"/>

<script type="text/javascript">
	var OlbProductCategoryList${prodCatalogId} = (function(){
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var configCategory = {
				width: '100%',
				filterable: false,
				showfilterrow: false,
				key: 'productCategoryId',
				parentKeyId: 'parentCategoryId',
				localization: getLocalization(),
				datafields: [
					{name: 'productCategoryId', type: 'string'},
					{name: 'productCategoryTypeId', type: 'string'},
					{name: 'parentCategoryId', type: 'string'},
					{name: 'description', type: 'string'},
					{name: 'categoryName', type: 'string'},
					{name: 'prodCatalogCategoryTypeId', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductCategoryId)}', datafield: 'productCategoryId', width: '20%',
						cellsrenderer: function(row, colum, value) {
					    	return "<span><a href='viewCategory?productCategoryId=" + value + "' target='_blank'>" + value + "</a></span>";
					    }
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSRelationshipType)}', datafield: 'prodCatalogCategoryTypeId', width: '20%',  
						cellsRenderer: function (row, column, value, rowData) {
							if (prodCatalogCategoryTypeData.length > 0) {
								for(var i = 0 ; i < prodCatalogCategoryTypeData.length; i++){
									if (value == prodCatalogCategoryTypeData[i].id){
										return '<span title =\"' + prodCatalogCategoryTypeData[i].description +'\">' + prodCatalogCategoryTypeData[i].description + '</span>';
									}
								}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}
	   				},
	   				{text: '${StringUtil.wrapString(uiLabelMap.BSCategoryName)}', datafield: 'categoryName'},
				],
				useUrl: true,
				root: 'results',
				url: 'jqxGeneralServicer?sname=JQListProductCategory&pagesize=0<#if prodCatalogId?exists>&prodCatalogId=${prodCatalogId}</#if>',
				rendertoolbarconfig: {
					//titleProperty: "${StringUtil.wrapString(uiLabelMap[titleProperty])}",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
					expendButton: true,
				},
            	showtoolbar: false,
            	pageable: false,
            	bindresize: true,
            	height: 300,
			};
			new OlbGrid($("#jqxListCategory${prodCatalogId}"), null, configCategory, []);
		};
		var openQuickCreateNew = function(){
			$("#alterpopupWindowQuick").jqxWindow('open');
		};
		return {
			init: init,
			openQuickCreateNew: openQuickCreateNew,
		}
	}());
	$(function(){
		OlbProductCategoryList${prodCatalogId}.init();
	});
</script>