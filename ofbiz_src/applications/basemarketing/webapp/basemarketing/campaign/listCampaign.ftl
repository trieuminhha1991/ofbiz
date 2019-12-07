<script>
	var uiLabelMap = {}
	function showCampaign(id){
		window.location.href=  "<@ofbizUrl>CreateCampaignMarketing</@ofbizUrl>?id=" + id;
	}
	var createCampaign = function(){
		window.location.href=  "<@ofbizUrl>CreateCampaignMarketing</@ofbizUrl>";
	};
	var lc = "${locale}".split('_')[0];
	<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "MKTG_CAMP_STATUS"), null, null, null, false) />
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
							statusId: '${item.statusId?if_exists}',
				description: '${StringUtil.wrapString(item.get("description", locale)?if_exists)}'
						},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
						'${item.statusId?if_exists}': '${StringUtil.wrapString(item.get("description", locale)?if_exists)}',
					</#list></#if>};
	<#assign listMarketingType = delegator.findList("MarketingType", null, null, null, null, false) />
	var listMarketingType = [<#if listMarketingType?exists><#list listMarketingType as item>{
							marketingTypeId: '${item.marketingTypeId?if_exists}',
				description: '${StringUtil.wrapString(item.get("name", locale)?if_exists)}'
						},</#list></#if>];
	var mapMarketingType = {<#if listMarketingType?exists><#list listMarketingType as item>
						'${item.marketingTypeId?if_exists}': '${StringUtil.wrapString(item.get("name", locale)?if_exists)}',
					</#list></#if>};
	function fixSelectAll(dataList) {
		var sourceST = {
		        localdata: dataList,
		        datatype: "array"
	    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
	    var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}

</script>
<div class="">

	<#assign dataField="[{ name: 'marketingCampaignId', type: 'string' },
						 { name: 'campaignName', type: 'string'},
						 { name: 'campaignSummary', type: 'string'},
						 { name: 'marketingTypeId', type: 'string'},
						 { name: 'fromDate', type: 'date', other: 'Timestamp'},
						 { name: 'thruDate', type: 'date', other: 'Timestamp'},
						 { name: 'budgetedCost', type: 'number'},
						 { name: 'estimatedCost', type: 'number'},
						 { name: 'actualCost', type: 'string'},
						 { name: 'people', type: 'string'},
						 { name: 'statusId', type: 'string'}]"/>
	<#assign columnlist="{ text: '${uiLabelMap.CampaignId}', datafield: 'marketingCampaignId', width: '160px', filterable: true, editable: false,
							cellsrenderer: function(row, colum, value){
								var data = $('#ListCampaign').jqxGrid('getrowdata', row);
								var str = '<a href=\"CreateCampaignMarketing?id='+data.marketingCampaignId+'\" target=\"_blank\">' + data.marketingCampaignId+ '</a>';
								return str;
							}
						 },
						 { text: '${uiLabelMap.CampaignName}', datafield: 'campaignName', filterable: true, width: '120px'},
						 { text: '${uiLabelMap.CampaignSummary}', datafield: 'campaignSummary', filterable: true, hidden: true},
						 { text: '${uiLabelMap.marketingTypeId}', datafield: 'marketingTypeId', filtertype: 'checkedlist', width: '200px', filterable: true,
							 cellsrenderer: function(row, colum, value){
									value?value=mapMarketingType[value]:value;
									return '<span>' + value + '</span>';
							 },createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listMarketingType), displayMember: 'marketingTypeId', valueMember: 'marketingTypeId' ,
			                            renderer: function (index, label, value) {
							if (index == 0) {
								return value;
											}
										    return mapMarketingType[value];
						                }
							});
							editor.jqxDropDownList('checkAll');
							 }
						 },
						 { text: '${uiLabelMap.DAFromDate}', datafield: 'fromDate', width: '200px', filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy'},
						 { text: '${uiLabelMap.DAThruDate}', datafield: 'thruDate', width: '200px', filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy'},
						 { text: '${uiLabelMap.budgetedCost}', datafield: 'budgetedCost', cellsalign: 'right',width: 200,
							cellsrenderer: function(row, colum, value){
								var val = value.toLocaleString(lc);
								return '<div class=\"cell-grid-custom\"><div class=\"pull-right\">'+val+'</div></div>';
							}
						 },
						 { text: '${uiLabelMap.estimatedCost}', datafield: 'estimatedCost', cellsalign: 'right', width: 200,
							cellsrenderer: function(row, colum, value){
								var val = value.toLocaleString(lc);
								return '<div class=\"cell-grid-custom\"><div class=\"pull-right\">'+val+'</div></div>';
							}
						 },
						 { text: '${uiLabelMap.actualCost}', datafield: 'actualCost', cellsalign: 'right',width: 200,
							cellsrenderer: function(row, colum, value){
								var val = value.toLocaleString(lc);
								return '<div class=\"cell-grid-custom\"><div class=\"pull-right\">'+val+'</div></div>';
							}
						 },
						 { text: '${uiLabelMap.DAStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 200,
							 cellsrenderer: function(row, colum, value){
									value?value=mapStatusItem[value]:value;
									return '<span>' + value + '</span>';
							 },createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listStatusItem), displayMember: 'statusId', valueMember: 'statusId' ,
			                            renderer: function (index, label, value) {
							if (index == 0) {
								return value;
											}
										    return mapStatusItem[value];
						                }
							});
							editor.jqxDropDownList('checkAll');
							 }
						 }"/>

	<@jqGrid id="ListCampaign" addType="popup" dataField=dataField columnlist=columnlist
			clearfilteringbutton="true" showtoolbar="true"
			url="jqxGeneralServicer?sname=JQGetMarketingCampaign"
			customcontrol1="fa fa-plus-circle@${uiLabelMap.KCreateCampaign}@javascript: void(0);@createCampaign()"
			deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteMarketingCampaign&jqaction=D"
			deleteColumn="marketingCampaignId" />
</div>
