<#--
<#assign resultService = dispatcher.runSync("getProductPromoTypesByChannel", Static["org.ofbiz.base.util.UtilMisc"].toMap("salesMethodChannel","SALES_GT_CHANNEL"))/>
<#if resultService?exists && resultService.listProductPromoType?exists>
	<#assign listPromoType = resultService.listProductPromoType/>
</#if>
-->
<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "REG_PROMO_STTS"}, null, false) />
<script type="text/javascript">
	<#--
	var promoTypeData = new Array();
	<#list listPromoType as promoTypeItem>
		var row = {};
		row['typeId'] = "${promoTypeItem.productPromoTypeId}";
		row['description'] = "${StringUtil.wrapString(promoTypeItem.get("description", locale))}";
		promoTypeData[${promoTypeItem_index}] = row;
	</#list>
	-->
	
	var statusData = new Array();
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
	</#list>
</script>

<#assign dataField="[{name: 'productPromoRegisterId', type: 'string'}, 
						{name: 'createdDate', type: 'date', other: 'Timestamp'}, 
						{name: 'createdBy', type: 'string'}, 
						{name: 'isSequency', type: 'string'}, 
						{name: 'result', type: 'string'},
						{name: 'description', type: 'string'}
						]"/>
<#assign columnlist="{text: '${uiLabelMap.DAProductPromoRegisterId}', dataField: 'productPromoRegisterId', width: '180px'}, 
						{text: '${uiLabelMap.DACreatedDate}', dataField: 'createdDate', width: '160px', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
						{text: '${uiLabelMap.DACreatedBy}', dataField: 'createdBy', width: '160px'},
						{text: '${uiLabelMap.DAIsSequency}', dataField: 'isSequency', width: '160px'},
						{text: '${uiLabelMap.DAResult}', dataField: 'result', width: '160px'},
						{text: '${uiLabelMap.DADescription}', dataField: 'description'}
              		"/>
<#assign tmpCreateUrl = ""/>
<#if security.hasPermission("REGPROMO_ROLE_VIEW", session)>
	<#assign tmpCreateUrl = "icon-zoom-in open-sans@${uiLabelMap.DAAbbListExhibitedRegister}@listExhibitedRegister"/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" defaultSortColumn="productPromoRegisterId;createdDate;createdBy" sortdirection="desc" filtersimplemode="true" showstatusbar="false" 
		customcontrol1=tmpCreateUrl 
		url="jqxGeneralServicer?sname=JQGetListExhibitedMark&productPromoRegisterId=${parameters.productPromoRegisterId?if_exists}"/>
