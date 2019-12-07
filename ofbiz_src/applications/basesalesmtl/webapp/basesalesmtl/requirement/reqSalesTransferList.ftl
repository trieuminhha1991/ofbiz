<#assign gridProductItemsId = "jqxgridReqDeliveryOrder">
<script type="text/javascript">
	<#assign orderStatuses = delegator.findByAnd("StatusItem", {"statusTypeId" : "REQUIREMENT_STATUS"}, null, false)!/>
	var reqStatusData = [
	<#if orderStatuses?exists>
		<#list orderStatuses as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	<#assign requirementTypeIds = ["TRANSFER_REQUIREMENT", "BORROW_REQUIREMENT", "PAY_REQUIREMENT", "CHANGEDATE_REQUIREMENT"]/>
	<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, requirementTypeIds), null, null, null, false)!/>
	var requirementTypeData = [
   	   	<#if requirementTypes?exists>
   	   		<#list requirementTypes as item>
   	   		{	requirementTypeId: "${item.requirementTypeId}",
   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
   	   		},
   	   		</#list>
   	   	</#if>
   	];
   	<#assign requirementReasons = delegator.findList("RequirementEnumType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, requirementTypeIds), null, null, null, false)!/>
	<#assign enumTypeIds = []>
	<#list requirementReasons as reason>
		<#assign enumTypeIds = enumTypeIds + [reason.enumTypeId?if_exists]>
	</#list>
   	<#assign reasonEnums = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, enumTypeIds), null, null, null, false)!>
	var reasonEnumData = [
   	<#if reasonEnums?has_content>
   		<#list reasonEnums as item>
		{	enumId: "${item.enumId}",
			description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
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
				{ name: 'requirementTypeId', type: 'string'},
				{ name: 'reasonEnumId', type: 'string'},
				{ name: 'requiredByDate', type: 'date', other: 'Timestamp'},
				{ name: 'requirementStartDate', type: 'date', other: 'Timestamp'},
				{ name: 'createdByUserLogin', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'description', type: 'string'},
				{ name: 'destFacilityId', type: 'string'},
			]"/>
<#assign columnlist = "
				{text: '${StringUtil.wrapString(uiLabelMap.BSRequirementId)}', dataField: 'requirementId', pinned: true, width: '13%', 
					cellsrenderer: function(row, colum, value) {
						return \"<span><a href='viewReqSalesTransfer?requirementId=\" + value + \"'>\" + value + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.BSRequirementType}', dataField: 'requirementTypeId', width: '13%', cellClassName: cellClass, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (requirementTypeData.length > 0) {
							for(var i = 0 ; i < requirementTypeData.length; i++){
    							if (value == requirementTypeData[i].requirementTypeId){
    								return '<span title =\"' + requirementTypeData[i].description +'\">' + requirementTypeData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (requirementTypeData.length > 0) {
				 			var filterDataAdapter = new $.jqx.dataAdapter(requirementTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'requirementTypeId', valueMember: 'requirementTypeId',
								renderer: function(index, label, value){
									if (requirementTypeData.length > 0) {
										for(var i = 0; i < requirementTypeData.length; i++){
											if(requirementTypeData[i].requirementTypeId == value){
												return '<span>' + requirementTypeData[i].description + '</span>';
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
				{ text: '${uiLabelMap.BSReason}', dataField: 'reasonEnumId', width: '10%', cellClassName: cellClass, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (reasonEnumData.length > 0) {
							for(var i = 0 ; i < reasonEnumData.length; i++){
    							if (value == reasonEnumData[i].enumId){
    								return '<span title =\"' + reasonEnumData[i].description +'\">' + reasonEnumData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (reasonEnumData.length > 0) {
				 			var filterDataAdapter = new $.jqx.dataAdapter(reasonEnumData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
								renderer: function(index, label, value){
									if (reasonEnumData.length > 0) {
										for(var i = 0; i < reasonEnumData.length; i++){
											if(reasonEnumData[i].enumId == value){
												return '<span>' + reasonEnumData[i].description + '</span>';
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
				{text: '${StringUtil.wrapString(uiLabelMap.FacilityTo)}', dataField: 'destFacilityId', width: '14%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSCreatedBy)}', dataField: 'createdByUserLogin', width: '12%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description', width: '14%', cellClassName: cellClass},
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '12%', cellClassName: cellClass, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (reqStatusData.length > 0) {
							for(var i = 0 ; i < reqStatusData.length; i++){
    							if (value == reqStatusData[i].statusId){
    								return '<span title =\"' + reqStatusData[i].description +'\">' + reqStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (reqStatusData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(reqStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (reqStatusData.length > 0) {
										for(var i = 0; i < reqStatusData.length; i++){
											if(reqStatusData[i].statusId == value){
												return '<span>' + reqStatusData[i].description + '</span>';
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
		viewSize=viewSize showtoolbar="false" editmode="click" selectionmode="singleRow" width="100%" bindresize="true" groupable="false" 
		url="jqxGeneralServicer?sname=JQGetListReqSalesTransfer" 
	/>

<@jqOlbCoreLib />