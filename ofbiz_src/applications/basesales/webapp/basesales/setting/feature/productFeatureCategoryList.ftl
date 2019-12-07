<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField = "[
			{name: 'productFeatureCategoryId', type: 'string'}, 
			{name: 'description', type: 'string'}
		]"/>

<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSProductFeatureCategoryId)}', dataField: 'productFeatureCategoryId', width: '30%', editable: false,
				cellsrenderer: function(row, colum, value) {
					var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					return \"<span><a href='showFeatureDetail?productFeatureCategoryId=\" + data.productFeatureCategoryId + \"'>\" + data.productFeatureCategoryId + \"</a></span>\";
				},
				validation: function (cell, value) {
			        if (value == null) {
			            return { result: false, message: '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmptyK)}' };
			        }
			        return true;
			    }
			}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description',
				validation: function (cell, value) {
			        if (value == null) {
			            return { result: false, message: '${StringUtil.wrapString(uiLabelMap.BSValueIsNotEmptyK)}' };
			        }
			        return true;
			    }
			},
		"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
		viewSize="20" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListFeatureCategory" mouseRightMenu="false"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateFeature" editColumns="productFeatureCategoryId;description"/> 
		
<div>
	<#--${screens.render("component://basesales/widget/SettingScreens.xml#NewFeature")}-->
	<#include "productFeatureCategoryNewPopup.ftl">
</div>