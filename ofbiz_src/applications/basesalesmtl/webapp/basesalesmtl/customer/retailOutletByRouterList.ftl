<#-- from ../supervisor/agents.ftl -->
<#if !hiddenDistributor?exists><#assign hiddenDistributor = false/></#if>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<#assign visitFrequencyTypes = delegator.findList("VisitFrequencyType", null, null, null, null, false) />

<script type="text/javascript">
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	   },</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};

	var mapVisitFrequencyType = {<#if visitFrequencyTypes?exists><#list visitFrequencyTypes as item>
			"${item.visitFrequencyTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};
    var listVisitFrequencyType = [<#if visitFrequencyTypes?exists><#list visitFrequencyTypes as item>{
		visitFrequencyTypeId: "${item.visitFrequencyTypeId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];

	var cellClass = function (row, columnfield, value) {
 		var data = $('#jqxgridListRetailOutlet${routerId?if_exists}').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PARTY_DISABLED" == data.statusId) {
 				return "background-cancel";
 			} else if ("PARTY_ENABLED" == data.statusId) {
 				return "";
 			} else {
 				return "background-important-nd";
 			}
 		}
    }
</script>

<#assign dataField="[
				{name: 'customerId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'routeId', type: 'string'},
				{name: 'distributorId', type: 'string'},
				{name: 'salesmanId', type: 'string'},
				{name: 'supervisorId', type: 'string'},
				{name: 'visitFrequencyTypeId', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "
				{text: '${uiLabelMap.BSSTT}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false, width: '5%', cellClassName: cellClass,
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentId)}', datafield: 'partyCode', width: '14%', cellClassName: cellClass,
					cellsrenderer: function(row, column, value, a, b, data){
				        var link = 'AgentDetail?partyId=' + data.customerId;
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'fullName', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSVisitFrequency)}', datafield: 'visitFrequencyTypeId', filtertype: 'checkedlist', width: '15%', editable: false, cellClassName: cellClass,
					cellsrenderer: function(row, colum, value){
						value?value=mapVisitFrequencyType[value]:value;
				        return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, htmlElement, editor) {
    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listVisitFrequencyType, displayMember: 'description', valueMember: 'visitFrequencyTypeId' ,
                            renderer: function (index, label, value) {
                            	if (index == 0) {
                            		return value;
								}
							    return mapVisitFrequencyType[value];
			                }
    		        	});
					}
				},
			 	{text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: '12%', editable: true, cellClassName: cellClass,
					cellsrenderer: function(row, colum, value){
						value?value=mapStatusItem[value]:value;
				        return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, htmlElement, editor) {
    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatusItem, displayMember: 'description', valueMember: 'statusId' ,
                            renderer: function (index, label, value) {
                            	if (index == 0) {
                            		return value;
								}
							    return mapStatusItem[value];
			                }
    		        	});
					}
				}
			"/>

<#if route?exists>
	<#assign url = "jqxGeneralServicer?sname=JQGetListAgentsByRouter&routerId=${route.routeId}"/>
</#if>

<#assign customcontrol2 = "fa fa-file-excel-o@${uiLabelMap.BSExportExcel}@javascript: void(0);@RetailOutletList.exportExcel()">
<@jqGrid id="jqxgridListRetailOutlet${routerId?if_exists}" url=url dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" 
		 defaultSortColumn="createdDate" sortdirection="desc" customTitleProperties="${customTitleProperties?if_exists}"
		 addrow="false" viewSize="5" showtoolbar="false"/>

