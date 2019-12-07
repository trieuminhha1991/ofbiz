<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign listProductFeatureType = delegator.findList("ProductFeatureType", null , null, orderBy, null, false)!>
<#assign listProductFeatureCategory = delegator.findList("ProductFeatureCategory", null , null, orderBy, null, false)!>
<script type="text/javascript">
	var pFeatureTypeData = [
	<#if listProductFeatureType?exists>
		<#list listProductFeatureType as listProductFeatureItem>
		{	productFeatureTypeId: "${listProductFeatureItem.productFeatureTypeId}",
			description: "${StringUtil.wrapString(listProductFeatureItem.get("description", locale))}"
		},
		</#list>
	</#if>
	];
	var pFeatureCategoryData = [
	<#if listProductFeatureCategory?exists>
		<#list listProductFeatureCategory as listProductFeatureCategoryItem>
		{	productFeatureCategoryId: "${listProductFeatureCategoryItem.productFeatureCategoryId}",
			description: "${StringUtil.wrapString(listProductFeatureCategoryItem.get("description", locale))}"
		},
		</#list>
	</#if>
	];
</script>

<#assign dataField = "[
			{name: 'productFeatureId', type: 'string'},
			{name: 'productFeatureTypeId', type: 'string'},
			{name: 'productFeatureCategoryId', type: 'string'},
			{name: 'description', type: 'string'},
			{name: 'uomId', type: 'string'},
			{name: 'numberSpecified', type: 'number'},
			{name: 'defaultAmount', type: 'number'},
			{name: 'defaultSequenceNum', type: 'number'},
			{name: 'abbrev', type: 'string'},
			{name: 'idCode', type: 'string'}
		]"/>

<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductFeatureId)}', dataField: 'productFeatureId', width: '14%', editable: false,}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductFeatureTypeId)}', dataField: 'productFeatureTypeId', width: '12%', editable: false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					for(var i = 0 ; i < pFeatureTypeData.length; i++){
						if (value == pFeatureTypeData[i].productFeatureTypeId){
							return '<span title = ' + pFeatureTypeData[i].description +'>' + pFeatureTypeData[i].description + '</span>';
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
				}, 
				createfilterwidget: function (column, columnElement, widget) {
					var filterBoxAdapter2 = new $.jqx.dataAdapter(pFeatureTypeData,
			        {
			            autoBind: true
			        });
			        var uniqueRecords2 = filterBoxAdapter2.records;
					widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'productFeatureTypeId', valueMember : 'description', renderer: function (index, label, value) 
					{
						for(i=0;i < pFeatureTypeData.length; i++){
							if(pFeatureTypeData[i].productFeatureTypeId == value){
								return pFeatureTypeData[i].description;
							}
						}
					    return value;
					}});
					widget.jqxDropDownList('checkAll');
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductFeatureCategoryId)}', dataField: 'productFeatureCategoryId', width: '12%', editable: false, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					for(var i = 0 ; i < pFeatureCategoryData.length; i++){
						if (value == pFeatureCategoryData[i].productFeatureCategoryId){
							return '<span title = ' + pFeatureCategoryData[i].description +'>' + pFeatureCategoryData[i].description + '</span>';
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
					var filterBoxAdapter2 = new $.jqx.dataAdapter(pFeatureCategoryData,
			        {
			            autoBind: true
			        });
			        var uniqueRecords2 = filterBoxAdapter2.records;
					widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'productFeatureCategoryId', valueMember : 'description', renderer: function (index, label, value) 
					{
						for(i=0;i < pFeatureCategoryData.length; i++){
							if(pFeatureCategoryData[i].productFeatureCategoryId == value){
								return pFeatureCategoryData[i].description;
							}
						}
					    return value;
					}});
					widget.jqxDropDownList('checkAll');
				}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSUomId)}', dataField: 'uomId', width: '14%', hidden: true},
			{text: '${StringUtil.wrapString(uiLabelMap.BSNumberSpecified)}', dataField: 'numberSpecified', width: '10%', cellsalign: 'right'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSAmount)}', dataField: 'defaultAmount', width: '10%', cellsalign: 'right'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSDefaultSequenceNum)}', dataField: 'defaultSequenceNum', width: '10%', columntype: 'numberinput', cellsalign: 'right',
				validation: function (cell, value) {
			        if (value <= 0) {
			            return { result: false, message: '${StringUtil.wrapString(uiLabelMap.BSValidateSequence)}' };
			        }
			        return true;
			    }
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSAbbrev)}', dataField: 'abbrev', width: '8%'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSIdCode)}', dataField: 'idCode', width: '8%'},
		"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListFeature&productFeatureCategoryId=${parameters.productFeatureCategoryId}" mouseRightMenu="false"
		createUrl="jqxGeneralServicer?sname=createFeatureChild&jqaction=C" addColumns="productFeatureId;productFeatureTypeId;productFeatureCategoryId;description;uomId;numberSpecified(java.math.BigDecimal);defaultAmount(java.math.BigDecimal);defaultSequenceNum(java.lang.Long);abbrev;idCode"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=editFeatureChild" editColumns="productFeatureId;productFeatureTypeId;productFeatureCategoryId;description;uomId?if_exists;numberSpecified(java.math.BigDecimal);defaultAmount(java.math.BigDecimal);defaultSequenceNum(java.lang.Long);abbrev;idCode" 
	/>

<#include "productFeatureNewPopup.ftl"/>
