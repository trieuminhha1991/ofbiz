<#assign gridProductItemsId = "jqxgridReqDeliveryOrder">
<script type="text/javascript">
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "REQUIREMENT_STATUS"}, null, false)/>
	var orderStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	
	var cellClass = function (row, columnfield, value) {
 		var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("REQ_CANCELLED" == data.statusId) {
 				return "background-cancel";
 			} else if ("REQ_CREATED" == data.statusId) {
 				return "background-important-nd";
 			} else if ("REQ_APPROVED" == data.statusId) {
 				return "background-prepare";
 			}
 		}
    }
</script>
<#assign dataField = "[
				{ name: 'requirementId', type: 'string'},
				{ name: 'requiredByDate', type: 'date', other: 'Timestamp'},
				{ name: 'requirementStartDate', type: 'date', other: 'Timestamp'},
				{ name: 'createdByUserLogin', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'description', type: 'string'},
			]"/>
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSRequirementId)}', dataField: 'requirementId', pinned: true, width: '13%', 
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='viewReqDeliveryOrder?requirementId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.BSRequiredByDate}', dataField: 'requiredByDate', width: '14%', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.BSRequirementStartDate}', dataField: 'requirementStartDate', width: '14%', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCreatedBy)}', dataField: 'createdByUserLogin', width: '12%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', cellClassName: cellClass},
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '12%', cellClassName: cellClass, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (orderStatusData.length > 0) {
							for(var i = 0 ; i < orderStatusData.length; i++){
    							if (value == orderStatusData[i].statusId){
    								return '<span title =\"' + orderStatusData[i].description +'\">' + orderStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (orderStatusData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(orderStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (orderStatusData.length > 0) {
										for(var i = 0; i < orderStatusData.length; i++){
											if(orderStatusData[i].statusId == value){
												return '<span>' + orderStatusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
		   			}
				}, 
			"/>
<@jqGrid id=gridProductItemsId idExisted=idExisted clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
		viewSize=viewSize showtoolbar="true" editmode="click" selectionmode="singleRow" width="100%" bindresize="true" groupable="false"
		url="jqxGeneralServicer?sname=JQGetListReqDeliveryOrder" isShowTitleProperty="true" customTitleProperties="BSListRequirementDeliveryOrder"
	/>

<@jqOlbCoreLib />