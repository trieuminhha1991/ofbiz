<#assign prodCatalogId = parameters.prodCatalogId?default("")/>
<style type="text/css">
	#statusbarjqxListProductStore {
		width: 0 !important;
	}
	#pagerjqxListProductStore${prodCatalogId}{display:none}
</style>

<div id="jqxListProductStore${prodCatalogId}"></div>

<script type="text/javascript">
	var OlbProductStoreList${prodCatalogId} = (function(){
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var configCategory = {
				width: '100%',
				filterable: false,
				showfilterrow: false,
				localization: getLocalization(),
				datafields: [
					{name: 'productStoreId', type: 'string'},
					{name: 'storeName', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSPSChannelId)}', dataField: 'productStoreId', width: '20%', editable: false,
						cellsrenderer: function(row, colum, value) {
							<#if hasOlbPermission("MODULE", "SALES_PRODUCTSTOREST_VIEW", "")>
					    	return "<span><a href='showProductStore?productStoreId=" + value + "' target='_blank'>" + value + "</a></span>";
					    	<#else>
					    	return "<span>" + value + "</span>";
					    	</#if>
					    }
					}, 
					{text: '${StringUtil.wrapString(uiLabelMap.BSPSChannelName)}', dataField: 'storeName'},
				],
				useUrl: true,
				url: 'jqxGeneralServicer?sname=getListProductStore&pagesize=0<#if prodCatalogId?exists>&prodCatalogId=${prodCatalogId}</#if>',
            	showtoolbar: false,
            	pageable: false,
            	bindresize: true,
            	height: 300,
			};
			new OlbGrid($("#jqxListProductStore${prodCatalogId}"), null, configCategory, []);
		};
		return {
			init: init,
		}
	}());
	$(function(){
		OlbProductStoreList${prodCatalogId}.init();
	});
</script>