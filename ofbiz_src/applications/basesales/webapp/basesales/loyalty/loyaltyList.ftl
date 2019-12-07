<script type="text/javascript">
	var cellClassjqxLoyalty = function (row, columnfield, value) {
 		var data = $('#jqxLoyalty').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			var now = new Date();
 			if ("LOYALTY_ACCEPTED" == data.statusId) {
 				if (data.fromDate <= now) {
 					if (data.thruDate == null || (data.thruDate >= now)) return "background-running";
 				} else {
 					return "background-prepare";
 				}
 			} else if ("LOYALTY_CREATED" == data.statusId) {
 				return "background-waiting";
 			}
 		}
    }
</script>
<#assign dataField = "[
			{name: 'loyaltyId', type: 'string'}, 
			{name: 'loyaltyName', type: 'string'}, 
			{name: 'loyaltyTypeId', type: 'string'}, 
			{name: 'createdDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'}, 
			{name: 'thruDate', type: 'date', other: 'Timestamp', pattern:'HH:mm:ss dd/MM/yyyy'}, 
			{name: 'statusId', type: 'string'}, 
		]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSLoyaltyId}', dataField: 'loyaltyId', width: '180px', cellClassName: cellClassjqxLoyalty, 
    			cellsrenderer: function(row, colum, value) {
            		return '<span><a href=\"viewLoyalty?loyaltyId=' + value + '\">' + value + '</a></span>';
                }
			}, 
			{text: '${uiLabelMap.BSLoyaltyName}', dataField: 'loyaltyName', cellClassName: cellClassjqxLoyalty},
			{text: '${uiLabelMap.BSLoyaltyType}', dataField: 'loyaltyTypeId', cellClassName: cellClassjqxLoyalty, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					if (loyaltyTypeData.length > 0) {
						for(var i = 0 ; i < loyaltyTypeData.length; i++){
							if (value == loyaltyTypeData[i].enumId){
								return '<span title =\"' + loyaltyTypeData[i].description +'\">' + loyaltyTypeData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	}, 
			 	createfilterwidget: function (column, columnElement, widget) {
			 		if (loyaltyTypeData.length > 0) {
						var filterDataAdapter = new $.jqx.dataAdapter(loyaltyTypeData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'enumId',
							renderer: function(index, label, value){
								if (loyaltyTypeData.length > 0) {
									for(var i = 0; i < loyaltyTypeData.length; i++){
										if(loyaltyTypeData[i].enumId == value){
											return '<span>' + loyaltyTypeData[i].description + '</span>';
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
			{text: '${uiLabelMap.BSCreateDate}', dataField: 'createdDate', width: '14%', cellsformat: 'HH:mm:ss dd/MM/yyyy', cellClassName: cellClassjqxLoyalty, filtertype: 'range'}, 
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', width: '14%', cellsformat: 'HH:mm:ss dd/MM/yyyy', cellClassName: cellClassjqxLoyalty, filtertype: 'range'}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', width: '14%', cellsformat: 'HH:mm:ss dd/MM/yyyy', cellClassName: cellClassjqxLoyalty, filtertype: 'range'}, 
			{text: '${uiLabelMap.BSStatus}', dataField: 'statusId', width: '16%', filtertype: 'checkedlist', cellClassName: cellClassjqxLoyalty, 
				cellsrenderer: function(row, column, value){
					if (loyaltyStatusData.length > 0) {
						for(var i = 0 ; i < loyaltyStatusData.length; i++){
							if (value == loyaltyStatusData[i].statusId){
								return '<span title =\"' + loyaltyStatusData[i].description +'\">' + loyaltyStatusData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	}, 
			 	createfilterwidget: function (column, columnElement, widget) {
			 		if (loyaltyStatusData.length > 0) {
						var filterDataAdapter = new $.jqx.dataAdapter(loyaltyStatusData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								if (loyaltyStatusData.length > 0) {
									for(var i = 0; i < loyaltyStatusData.length; i++){
										if(loyaltyStatusData[i].statusId == value){
											return '<span>' + loyaltyStatusData[i].description + '</span>';
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
<#assign tmpCreateUrl = ""/>
<#if hasOlbPermission("MODULE", "SALES_LOYALTY_NEW", "")>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.wgaddnew}@newLoyalty"/>
</#if>
<#assign tmpDelete = "false"/>
<#if hasOlbPermission("MODULE", "SALES_LOYALTY_DELETE", "")>
	<#assign tmpDelete = "true"/>
</#if>
<#assign contextMenuItemId = "ctxmnuloyalist">
<@jqGrid id="jqxLoyalty" url="jqxGeneralServicer?sname=JQListLoyalty" columnlist=columnlist dataField=dataField 
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" clearfilteringbutton="true"
		removeUrl="jqxGeneralServicer?sname=deleteLoyaltyCustom&jqaction=C" deleteColumn="loyaltyId" deleterow=tmpDelete 
		customcontrol1=tmpCreateUrl mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemId}"/>

<div id='contextMenu_${contextMenuItemId}' style="display:none">
	<ul>
		<li id="${contextMenuItemId}_viewdetailnewtab"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
	    <li id="${contextMenuItemId}_viewdetail"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li id="${contextMenuItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	    <#if tmpCreateUrl != ""><li id="${contextMenuItemId}_createnew"><i class="fa-plus-circle open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSCreateNew)}</li></#if>
	    <#if tmpDelete?exists && tmpDelete == "true"><li id="${contextMenuItemId}_delete"><i class="fa-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDeleteSelectedRow)}</li></#if>
	</ul>
</div>

<#include "loyaltyListScript.ftl"/>
