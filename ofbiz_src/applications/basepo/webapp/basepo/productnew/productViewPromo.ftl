<script type="text/javascript">
	var cellClassjqxPromotion = function (row, columnfield, value) {
 		var data = $('#jqxPromotion').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
 			if ("PROMO_ACCEPTED" == data.statusId) {
 				if (data.fromDate <= now) {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-running";
 				} else {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-prepare";
 				}
 			} else if ("PROMO_CREATED" == data.statusId) {
 				return "background-waiting";
 			}
 		}
    }
</script>
<div id="prodpromo-tab" class="tab-pane<#if activeTab?exists && activeTab == "prodpromo-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span12">
			<#assign dataField = "[
						{name: 'productPromoId', type: 'string'}, 
						{name: 'promoName', type: 'string'}, 
						{name: 'createdDate', type: 'date', other: 'Timestamp'}, 
						{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
						{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
						{name: 'statusId', type: 'string'}, 
						{name: 'organizationPartyId', type: 'string'}, 
						{name: 'ruleTexts', type: 'string'}, 
					]"/>
			<#assign columnlist = "
						{text: '${uiLabelMap.BSProductPromoId}', dataField: 'productPromoId', width: '180px', cellClassName: cellClassjqxPromotion, 
			    			cellsrenderer: function(row, colum, value) {
			            		return '<span><a href=\"viewPromotion?productPromoId=' + value + '${focusMenu?default('')}\">' + value + '</a></span>';
			                }
						}, 
						{text: '${uiLabelMap.BSPromoName}', dataField: 'promoName', cellClassName: cellClassjqxPromotion},
						{text: '${uiLabelMap.BSRuleText}', dataField: 'ruleTexts', width: '25%', cellClassName: cellClassjqxPromotion},
						{text: '${uiLabelMap.BSCreateDate}', dataField: 'createdDate', width: '14%', cellClassName: cellClassjqxPromotion, cellsformat: 'dd/MM/yyyy', filtertype:'range', 
							cellsrenderer: function(row, colum, value) {
								return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
							}
						}, 
						{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: '14%', cellClassName: cellClassjqxPromotion, cellsformat: 'dd/MM/yyyy', filtertype:'range', 
							cellsrenderer: function(row, colum, value) {
								return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
							}
						}, 
						{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: '14%', cellClassName: cellClassjqxPromotion, cellsformat: 'dd/MM/yyyy', filtertype:'range', 
							cellsrenderer: function(row, colum, value) {
								return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
							}
						}, 
					"/>
			<@jqGrid id="jqxPromotion" url="jqxGeneralServicer?sname=JQGetListPromosByProduct&productId=${product?if_exists.productId?if_exists}" columnlist=columnlist dataField=dataField 
					viewSize="20" showtoolbar="false" filtersimplemode="true" showstatusbar="false" 
					deleterow="" removeUrl="" deleteColumn="productPromoId" 
					customcontrol1="" mouseRightMenu="false" contextMenuId="" bindresize="false"/>
		</div>
	</div>
</div>