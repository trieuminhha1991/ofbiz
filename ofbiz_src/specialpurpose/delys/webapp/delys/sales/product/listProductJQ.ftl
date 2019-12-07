<script>
	<#assign quantityUomList = delegator.findList("Uom", null, null, null, null, false) />
	var quData = new Array();
	<#list quantityUomList as itemUom >
		var row = {};
		row['quantityUomId'] = '${itemUom.uomId?if_exists}';
		row['weightUomId'] = '${itemUom.uomId?if_exists}';
		row['description'] = '${itemUom.description?if_exists}';
		quData[${itemUom_index}] = row;
	</#list>
	
	<#assign facilityList = delegator.findList("Facility", null, null, null, null, false) />
	var fData = new Array();
	<#list facilityList as itemFac >
		var row = {};
		row['facilityId'] = '${itemFac.facilityId?if_exists}';
		row['facilityName'] = '${itemFac.facilityName?if_exists}';
		fData[${itemFac_index}] = row;
	</#list>
	
	<#assign productTypeList = delegator.findList("ProductType", null, null, null, null, false) />
	var productTypeData = new Array();
	<#list productTypeList as productType>
		<#assign description = StringUtil.wrapString(productType.description) />
		var row = {};
		row['description'] = "${description}";
		row['productTypeId'] = '${productType.productTypeId}';
		productTypeData[${productType_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'productId', type: 'string' },
					 { name: 'productTypeId', type: 'string'},
					 { name: 'internalName', type: 'string'},
					 { name: 'brandName', type: 'string'},
					 { name: 'productName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'quantityUomId', type: 'string'},
					 { name: 'weight', type: 'string'},
					 { name: 'weightUomId', type: 'string'},
					 { name: 'createdByUserLogin', type: 'string'},
					 ]"/>
<#--{ name: 'facilityId', type: 'string'}, { name: 'quantityOnHandTotal', type: 'string'},-->
<#assign columnlist="{ text: '${uiLabelMap.ProductProductId}', datafield: 'productId', width: '160px', editable: false,
						cellsrenderer: function(row, colum, value){
						 	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        				return '<a style = \"margin-left: 10px\" href=' + 'editProduct?productId=' + data.productId + '>' +  data.productId + '</a>'	
					 	}
					 },
					 { text: '${uiLabelMap.ProductTypeId}', datafield: 'productTypeId', width: '120px', editable: false,   
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
    						for(i = 0 ; i < productTypeData.length; i++){
    							if (value == productTypeData[i].productTypeId){
    								return '<span title = ' + productTypeData[i].description +'>' + productTypeData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
					 	}
					 },
					 { text: '${uiLabelMap.DAInternalName}', datafield: 'internalName', width: '150px', editable: false},
					 { text: '${uiLabelMap.BrandName}', datafield: 'brandName', width: '100px', editable: false},
					 { text: '${uiLabelMap.ProductName}', datafield: 'productName', width: '200px', editable: false},
					 { text: '${uiLabelMap.description}', datafield: 'description', width: '100px', editable: false},
					 { text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', width: '110px', editable: false,
					 	cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < quData.length; i++){
					 			if(value == quData[i].quantityUomId){
					 				return \"<span>\" + quData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.DAWeight}', datafield: 'weight', width: '100px', editable: false},
					 { text: '${uiLabelMap.WeightUomId}', datafield: 'weightUomId', width: '150px', editable: false,
					 	cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < quData.length; i++){
					 			if(value == quData[i].weightUomId){
					 				return \"<span>\" + quData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.createdByUserLogin}', datafield: 'createdByUserLogin', width: '150px', editable: false},
					 "/>
<#--
{ text: '${uiLabelMap.facilityId}', datafield: 'facilityId', width: '100px', editable: false,
					 	cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < fData.length; i++){
					 			if(value == fData[i].facilityId){
					 				return \"<span>\" + fData[i].facilityName + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}
					 },
{ text: '${uiLabelMap.quantityOnHandTotal}', datafield: 'quantityOnHandTotal', width: '150px', editable: false},-->
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false" deleterow="false"
		 	url="jqxGeneralServicer?sname=JQGetListProduct"
		 />
		 
