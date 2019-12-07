<script>
var uiLabelMap = {}
function showCampaign(id){
	window.location.href=  "<@ofbizUrl>EditTradeCampaign</@ofbizUrl>?id=" + id;
}
	
</script>
<div class="">
	<#assign dataField="[{ name: 'marketingCampaignId', type: 'string' },
						 { name: 'campaignName', type: 'string'},
						 { name: 'campaignSummary', type: 'string'},
						 { name: 'marketingTypeId', type: 'string'},
						 { name: 'marketingPlace', type: 'string'},
						 { name: 'fromDate', type: 'date'},
						 { name: 'thruDate', type: 'date'},
						 { name: 'budgetedCost', type: 'string'},
						 { name: 'estimatedCost', type: 'string'},
						 { name: 'people', type: 'string'},
						 { name: 'statusId', type: 'string'}]"/>
	<#assign columnlist="{ text: '${uiLabelMap.marketingCampaignId}', datafield: 'marketingCampaignId', width: '160px', filterable: true, editable: false, 
							cellsrenderer: function(row, colum, value){
						 		var data = $('#listRequest').jqxGrid('getrowdata', row);
	        					return '<div class=\"click\" style=\"margin-left: 10px;\" onclick=' 
	        							+ 'showCampaign(\"' + data.marketingCampaignId + '\"' + ')' + '>' 
	        							+  data.marketingCampaignId + '</div>'	
					 		}
						 },
						 { text: '${uiLabelMap.campaignName}', datafield: 'campaignName', filterable: true, width: '120px'},
						 { text: '${uiLabelMap.campaignSummary}', datafield: 'campaignSummary', filterable: true, hidden: true},
						 { text: '${uiLabelMap.marketingTypeId}', datafield: 'marketingTypeId', width: '200px', filterable: true},
						 { text: '${uiLabelMap.marketingPlace}', datafield: 'marketingPlace', filterable: true},
						 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: '200px', filtertype: 'date', filterable: true, cellsformat:'dd-MM-yyyy'},
						 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: '120px', filtertype: 'date', filterable: true, cellsformat:'dd-MM-yyyy'},
						 { text: '${uiLabelMap.budgetedCost}', datafield: 'budgetedCost', hidden: true},
						 { text: '${uiLabelMap.estimatedCost}', datafield: 'estimatedCost', hidden: true},
						 { text: '${uiLabelMap.people}', datafield: 'people', hidden: true},
						 { text: '${uiLabelMap.statusId}', datafield: 'statusId'}"/>
	
	<@jqGrid id="listRequest" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false" deleterow="false"
			 	url="jqxGeneralServicer?sname=JQGetListTradeMarketing&type=res" contextMenuId="jqxmenu" mouseRightMenu="true"/>
</div>
<div id='jqxmenu'  style="display:none;">
    <ul>
        <li id="edit"><a href="javascript:void(0)"><i class='fa fa-edit'></i>&nbsp;Edit</a></li>
        <li id="del"><a href="javascript:void(0)"><i class='fa fa-trash'></i>&nbsp;Delete</a></li>
    </ul>
</div>
