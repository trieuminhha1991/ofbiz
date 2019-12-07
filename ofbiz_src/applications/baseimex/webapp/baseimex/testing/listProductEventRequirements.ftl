<div id="requirement-tab" class="tab-pane<#if activeTab?exists && activeTab == "requirement-tab"> active</#if>">
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script>
	var requirementTypeId = "${parameters.requirementTypeId?if_exists}";
	<#assign company = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	<#assign departments = Static["com.olbius.basehr.util.PartyUtil"].getDepartmentOfEmployee(delegator, userLogin.get("partyId"), nowTimestamp)!/>;
	var deptTmp = null;
	<#if departments?has_content>
		deptTmp = "${departments.get(0)}";
	</#if>
	<#assign conditions = 
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toList(
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQRETURN_STATUS")),
				Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQUIREMENT_STATUS"))
		), Static["org.ofbiz.entity.condition.EntityJoinOperator"].OR)/>
	<#assign reqStatus2 = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditions), null, null, null, false)>
	var statusData2 = [<#if reqStatus2?exists><#list reqStatus2 as item>{
				statusId: "${item.statusId?if_exists}",
				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
			},</#list></#if>];
	<#assign reqStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "REQUIREMENT_STATUS")), null, null, null, false)>
	var statusData = [
  	   	<#if reqStatus?exists>
  	   		<#list reqStatus as item>
  	   			{
  	   				statusId: "${item.statusId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},
  	   		</#list>
  	   	</#if>
  	];
	
	var statusIdParam = "${parameters.statusId?if_exists}";
	
	<#if parameters.requirementTypeId?has_content>
		<#if 'EXPORT_REQUIREMENT' == parameters.requirementTypeId>
			<#assign requirementReasons = delegator.findList("RequirementEnumType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, [parameters.requirementTypeId?if_exists, "PAY_REQUIREMENT"]), null, null, null, false) />		
		<#elseif 'RECEIVE_REQUIREMENT' == parameters.requirementTypeId>
			<#assign requirementReasons = delegator.findList("RequirementEnumType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, [parameters.requirementTypeId?if_exists, "BORROW_REQUIREMENT"]), null, null, null, false) />
		<#else>
			<#assign requirementReasons = delegator.findList("RequirementEnumType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("requirementTypeId", parameters.requirementTypeId?if_exists), null, null, null, false) />
		</#if>
	<#else>
		<#assign requirementReasons = delegator.findList("RequirementEnumType", null, null, null, null, false) />
	</#if>
	
	<#assign enumTypeIds = []>
	
	<#list requirementReasons as reason>
		<#assign enumTypeIds = enumTypeIds + [reason.enumTypeId?if_exists]>
	</#list>
	<#assign hasReason = "Y">
	<#assign reasonEnums = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, enumTypeIds), null, null, null, false)>
	var reasonEnumData = [
  	   	<#if reasonEnums?has_content>
  	   		<#assign hasReason = "Y">
  	   		<#list reasonEnums as item>
  	   			{
  	   				enumId: "${item.enumId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},
  	   		</#list>
		<#else>
  	   		<#assign hasReason = "N">
  	   	</#if>
  	];
	<#assign requirementTypes = delegator.findList("RequirementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "PRODUCT_REQUIREMENT")), null, null, null, false) />
	var requirementTypeData = [
  	   	<#if requirementTypes?exists>
  	   		<#list requirementTypes as item>
  	   			{
  	   				requirementTypeId: "${item.requirementTypeId?if_exists}",
  	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
  	   			},
  	   		</#list>
  	   	</#if>
  	];
	
	<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false) />
	var quantityUomData = [
	   	<#if quantityUoms?exists>
	   		<#list quantityUoms as item>
	   			{
	   				uomId: "${item.uomId?if_exists}",
	   				description: "${StringUtil.wrapString(item.get('description', locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
</script>

<#assign filedShipment="[{ name: 'requirementId', type: 'string'},
					{ name: 'requirementTypeId', type: 'string'},
					{ name: 'requirementStartDate', type: 'date', other: 'Timestamp'},
					{ name: 'requiredByDate', type: 'date', other: 'Timestamp'},
					{ name: 'statusId', type: 'string'},
					{ name: 'estimatedBudget', type: 'number'},
					{ name: 'grandTotal', type: 'number'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'reasonEnumId', type: 'string'},
					{ name: 'createdDate', type: 'date', other: 'Timestamp'},
					{ name: 'fullName', type: 'string'},
					{ name: 'reasonDescription', type: 'string'},
					{ name: 'destFacilityName', type: 'string'},
					{ name: 'originFacilityName', type: 'string'},
					{ name: 'facilityId', type: 'string'},
					{ name: 'destFacilityId', type: 'string'},
					{ name: 'eventId', type: 'string'},
					{ name: 'eventCode', type: 'string'},
			   ]"/>
<#assign columnShipment="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.RequirementId)}', datafield: 'requirementId', pinned: true, width:150,
		cellsrenderer: function(row, colum, value){
			var link = 'imexViewRequirementDetail?requirementId=' + value;
	    	return '<span><a href=\"' + link + '\">' + value + '</a></span>';
		}
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.BIETestQuaranCodeSum)}', datafield: 'eventCode', pinned: true, width:150,
		cellsrenderer: function(row, colum, value){
			var data = $('#jqxgridRequirement').jqxGrid('getrowdata', row);
			var link = 'getDetailQualityTestEvent?eventId=' + data.eventId;
	    	return '<span><a href=\"' + link + '\">' + value + '</a></span>';
		}
	},
		{ text: '${StringUtil.wrapString(uiLabelMap.RequirementType)}', datafield: 'requirementTypeId', width:150, filterable: false, filtertype: 'checkedlist',
			cellsrenderer: function (row, colum, value){
		 		var data = $('#jqxgridRequirement').jqxGrid('getrowdata', row);
		 		if (value){
		 			for (var i = 0; i < requirementTypeData.length; i++){
		 				if (value == requirementTypeData[i].requirementTypeId){
		 					return '<span>'+requirementTypeData[i].description+'</span>';
		 				}
		 			}
		 		} else {
	 				return '<span>_NA_</span>';
		 		}
		 	},
		 	createfilterwidget: function (column, columnElement, widget) {
				var tmp = requirementTypeData;
				var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'requirementTypeId', valueMember: 'requirementTypeId',
					renderer: function(index, label, value){
			        	if (tmp.length > 0) {
							for(var i = 0; i < tmp.length; i++){
								if(tmp[i].requirementTypeId == value){
									return '<span>' + tmp[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
			},
		},
		{ text: '${uiLabelMap.FacilityFrom}', datafield: 'originFacilityName', align: 'left', width: 150,
		},
		{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'left', width: 150, filtertype: 'checkedlist',
			cellsrenderer: function(row, colum, value){
				for(i=0; i < statusData2.length; i++){
		            if(statusData2[i].statusId == value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData2[i].description + '</span>';
		            }
		        }
			},
			createfilterwidget: function (column, columnElement, widget) {
				var tmp = statusData;
				var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
					renderer: function(index, label, value){
			        	if (tmp.length > 0) {
							for(var i = 0; i < tmp.length; i++){
								if(tmp[i].statusId == value){
									return '<span>' + tmp[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
			},
		},
		{ text: '${uiLabelMap.RemainingSubTotal}', dataField: 'grandTotal', width: 150, editable:false, cellsformat: 'd', cellsalign: 'right',
			cellsrenderer: function(row, column, value){
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.RequirementPurpose)}', datafield: 'reasonEnumId', minwidth:150, filtertype: 'checkedlist',
			cellsrenderer: function(row, colum, value){
				for(i=0; i < reasonEnumData.length; i++){
		            if(reasonEnumData[i].enumId == value){
		            	return '<span style=\"text-align: left;\" title='+value+'>' + reasonEnumData[i].description + '</span>';
		            }
		        }
			},
			createfilterwidget: function (column, columnElement, widget) {
				var tmp = reasonEnumData;
				var filterDataAdapter = new $.jqx.dataAdapter(tmp, {
					autoBind: true
				});
				var records = filterDataAdapter.records;
				widget.jqxDropDownList({source: records, displayMember: 'enumId', valueMember: 'enumId',
					renderer: function(index, label, value){
			        	if (tmp.length > 0) {
							for(var i = 0; i < tmp.length; i++){
								if(tmp[i].enumId == value){
									return '<span>' + tmp[i].description + '</span>';
								}
							}
						}
						return value;
					}
				});
				widget.jqxDropDownList('checkAll');
			},
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.LogRequiredByDate)}', dataField: 'requiredByDate', align: 'left', width: 180, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+DatetimeUtilObj.formatFullDate(value)+'</span>';
				 }
			 }, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.LogRequirementStartDate)}', dataField: 'requirementStartDate', align: 'left', width: 180, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+DatetimeUtilObj.formatToMinutes(value)+'</span>';
				 }
			 }, 
		},
		{ hidden: true, text: '${StringUtil.wrapString(uiLabelMap.CreatedDate)}', dataField: 'createdDate', align: 'left', width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right',
			 cellsrenderer: function(row, column, value){
				 if (!value){
					 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
				 } else {
					 return '<span style=\"text-align: right\">'+DatetimeUtilObj.formatFullDate(value)+'</span>';
				 }
			 }, 
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.CreatedBy)}', datafield: 'fullName', width:150,
		},
	"/>

<@jqGrid filtersimplemode="true" id="jqxgridRequirement" addType="popup" dataField=filedShipment columnlist=columnShipment clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" addrefresh="true"
	url="jqxGeneralServicer?sname=getRequirements&requirementTypeId=${parameters.requirementTypeId?if_exists}&reasonEnumId=${parameters.reasonEnumId?if_exists}&eventId=${parameters.eventId?if_exists}" addColumns=""
	createUrl="" mouseRightMenu="true" contextMenuId="" jqGridMinimumLibEnable="true"
	showlist="true" customTitleProperties="BIERequirementExportTesting" selectionmode="singlecell" useCache="true" keyCache="requirementId" cacheMode="selection"	
/>
</div>