<script>
	var uiLabelMap = {}
	function showCampaign(id){
		window.location.href=  "<@ofbizUrl>CreateCallCampaign</@ofbizUrl>?id=" + id;
	}
	var createCampaign = function() {
		window.location.href=  "<@ofbizUrl>CreateCallCampaign</@ofbizUrl>";
	};
	<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "MKTG_CAMP_STATUS"), null, null, null, false) />
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
							statusId: "${item.statusId?if_exists}",
				description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
						},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
						"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
					</#list></#if>};
	<#assign listMarketingType = delegator.findList("MarketingType", null, null, null, null, false) />
	var listMarketingType = [<#if listMarketingType?exists><#list listMarketingType as item>{
							marketingTypeId: "${item.marketingTypeId?if_exists}",
				description: "${StringUtil.wrapString(item.get("name", locale)?if_exists)}"
						},</#list></#if>];
	var mapMarketingType = {<#if listMarketingType?exists><#list listMarketingType as item>
						"${item.marketingTypeId?if_exists}": "${StringUtil.wrapString(item.get("name", locale)?if_exists)}",
					</#list></#if>};
	var checkHtml = function(val) {
		return /<[a-z][\s\S]*>/i.test(val);
	};
	var entityMap = {
		"&": "&amp;",
		"<": "&lt;",
		">": "&gt;",
		'"': '&quot;',
		"'": '&#39;',
		"/": '&#x2F;'
	};
    var escapeHtml = function(string) {
	return String(string).replace(/[&<>"'\/]/g, function (s) {
		return entityMap[s];
	});
	};
</script>
<div class="">

<#assign dataField="[{ name: 'marketingCampaignId', type: 'string' },
					 { name: 'campaignName', type: 'string' },
					 { name: 'campaignSummary', type: 'string' },
					 { name: 'marketingTypeId', type: 'string' },
					 { name: 'fromDate', type: 'date', other: 'Timestamp' },
					 { name: 'thruDate', type: 'date', other: 'Timestamp' },
					 { name: 'statusId', type: 'string' }]"/>
<#assign columnlist="{ text: '${uiLabelMap.CampaignId}', datafield: 'marketingCampaignId', width: 160, filterable: true, editable: false,
						cellsrenderer: function(row, colum, value){
							var data = $('#ListCampaign').jqxGrid('getrowdata', row);
							var str = '<a href=\"CreateCallCampaign?id='+data.marketingCampaignId+'\" target=\"_blank\">' + data.marketingCampaignId+ '</a>';
							return str;
						}
					},
					{ text: '${uiLabelMap.CampaignName}', datafield: 'campaignName', filterable: true, minWidth: 250,
						cellsrenderer: function(row, colum, value){
							var str = '';
							if(!checkHtml(value)){
								str = '<div class=\"cell-custom-grid\">'+value+'</div>';
							}else{
								str = '<div class=\"cell-custom-grid\">'+escapeHtml(value)+'</div>';
							}
							return str;
						}
					},
					{ text: '${uiLabelMap.CampaignSummary}', datafield: 'campaignSummary', filterable: true, width: 200 },
					{ text: '${uiLabelMap.DmsStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 200,
						cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
							return '<span>' + value + '</span>';
						}, createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'statusId', valueMember: 'statusId' ,
								renderer: function (index, label, value) {
									if (index == 0) {
										return value;
									}
									return mapStatusItem[value];
								}
							});
						}
					},
					{ text: '${uiLabelMap.DmsFromDate}', datafield: 'fromDate', width: 180, filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy' },
					{ text: '${uiLabelMap.DmsThruDate}', datafield: 'thruDate', width: 180, filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy' }"/>

<#assign customcontrol1=""/>
<#if hasOlbEntityPermission("ENTITY_CRM_CAMPAIGN", "CREATE")>
	<#assign customcontrol1="fa fa-plus@${uiLabelMap.CommonAddNew}@javascript: void(0);@createCampaign()"/>
</#if>
<#if hasOlbEntityPermission("ENTITY_CRM_CAMPAIGN", "DELETE")>
<@jqGrid id="ListCampaign" addType="popup" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" showtoolbar="true" customcontrol1=customcontrol1
	url="jqxGeneralServicer?sname=JQGetCallCampaign"
	deleterow="true" removeUrl="jqxGeneralServicer?sname=deleteMarketingCampaign&jqaction=D"
	deleteColumn="marketingCampaignId"/>
<#else>
<@jqGrid id="ListCampaign" addType="popup" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" showtoolbar="true" customcontrol1=customcontrol1
	url="jqxGeneralServicer?sname=JQGetCallCampaign"/>
</#if>
</div>
