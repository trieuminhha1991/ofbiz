<#--
<row-actions>            
    <set field="hasVariants" value="${groovy: org.ofbiz.entity.util.EntityUtil.filterByDate(delegator.findByAnd('ProductAssoc', org.ofbiz.base.util.UtilMisc.toMap('productId', productId, 'productAssocTypeId', 'PRODUCT_VARIANT'), null, true)).size() > 0}" type="Boolean"/>
</row-actions>
<field name="searchVariants" title=" " widget-style="btn btn-mini btn-warning" use-when="hasVariants" sort-field="true">
    <hyperlink also-hidden="false" target-type="plain" description="${uiLabelMap.ProductVariants}" target="LookupVariantProduct">
        <parameter param-name="productId"/>
    </hyperlink>
</field>
-->
<#assign columnlist="{text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '150px', 
						cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='javascript:void(0);' onclick='javascript:set_value(&#39;\" + data.productId + \"&#39;)'>\" + data.productId + \"</a></span>\";
                    	}
                     }, 
					 {text: '${uiLabelMap.DABrandName}', dataField: 'brandName', width: '180px'},
					 {text: '${uiLabelMap.DAInternalName}', dataField: 'internalName', width: '180px'},
					 {text: '${uiLabelMap.DAProductTypeId}', dataField: 'productTypeId', width: '180px'},
					 "/>
					 
<#assign dataField="[{ name: 'productId', type: 'string'},
					{ name: 'brandName', type: 'string'},
					{ name: 'internalName', type: 'string'},
					{ name: 'productTypeId', type: 'string'}
					]"/>

<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" 
		 url="jqxGeneralServicer?sname=JQListProduct" />

