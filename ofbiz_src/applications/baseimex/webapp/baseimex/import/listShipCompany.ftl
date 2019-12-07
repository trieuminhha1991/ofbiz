<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<link rel="stylesheet" type="text/css" href="/imexresources/css/bl-css.1.0.0.css">
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtextarea.js"></script>

<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, true) />
<#assign tmpAdd = "false"/>
<#assign mouseRightMenu = "false"/>
<#if hasOlbPermission("MODULE", "IMEX_SHIPCOMPANY", "ADMIN")>
	<#assign tmpAdd = "true"/>
	<#assign mouseRightMenu = "true"/>
</#if>
<script>
var mapStatusItem = {<#if listStatus?exists><#list listStatus as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};
var statusData = [
	<#if listStatus?exists>
		<#list listStatus as statusItem>
		{	statusId: '${statusItem.statusId}',
			description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},
		</#list>
	</#if>
	];
var companyEdittingId
var cellClassShipCompany = function (row, columnfield, value) {
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("PARTY_DISABLED" == data.statusId) {
 				return "background-cancel-nd";
 			} else if ("PARTY_ENABLED" == data.status) {
 				return "background-running";
 			} 
 		}
    }
</script>
<#assign columnlist="{ text: '${uiLabelMap.CompanyId}', dataField: 'partyCode', width: 300, cellClassName: cellClassShipCompany,},
					 { text: '${uiLabelMap.ShipCompanyName}', dataField: 'groupName', cellClassName: cellClassShipCompany, },
					 { text: '${uiLabelMap.CompanyDesciption}', dataField: 'description', width: 400, cellClassName: cellClassShipCompany, },
					 { text: '${uiLabelMap.CompanyStatus}', dataField: 'statusId', width: 300, cellClassName: cellClassShipCompany,filtertype: 'checkedlist',
					 	cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
					        return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
					 		if (statusData.length > 0) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
									renderer: function(index, label, value){
										if (statusData.length > 0) {
											for(var i = 0; i < statusData.length; i++){
												if(statusData[i].statusId == value){
													return '<span>' + statusData[i].description + '</span>';
												}
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
							}
			   			}
					 }
					 "/>
<#assign dataField="[{ name: 'partyCode', type: 'string'},
					{ name: 'groupName', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'statusId', type: 'string'},
					]"/>


<div>
		
<@jqGrid filtersimplemode="true" alternativeAddPopup="alterpopupWindow" addType="popup"  dataField=dataField  defaultSortColumn="statusId"
		editable="true" columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow=tmpAdd editrefresh ="true"
	 	url="jqxGeneralServicer?sname=JQGetListShipCompany" rowdetailsheight="255"
	 	createUrl="jqxGeneralServicer?sname=addShipCompany&jqaction=C"
	 	addColumns="companyId;companyName;description"
 		contextMenuId="contextMenu" mouseRightMenu=mouseRightMenu/>

</div>
<div id="contextMenu" style="display:none;">
	<ul>
		<li id='contextMenu_add' action="addShipCompany"><i class="fa fa-plus"></i> ${uiLabelMap.AddNewShipCompany} </li>
		<li id='contextMenu_edit' action="editShipCompany"><i class="fa fa-pencil-square-o" ></i> ${uiLabelMap.EditShipCompany}</li>
		<li id='contextMenu_remove' action="removeShipCompany"> <i class="fa fa-trash" ></i>${uiLabelMap.RemoveShipCompany}</li>
		<li id='contextMenu_refresh' action="refresh"><i class="fa fa-refresh"></i> ${uiLabelMap.RefreshShipCompany}</li>
 
	</ul>
</div>
<script>

	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureRemoveShipCompany = "${StringUtil.wrapString(uiLabelMap.AreYouSureRemoveShipCompany)}";
	uiLabelMap.RemoveSuccess = "${StringUtil.wrapString(uiLabelMap.RemoveSuccess)}";
	
</script>
	
<#include "popup/addShipCompany.ftl"/>

<#include "popup/editShipCompany.ftl"/>

<script type="text/javascript" src="/imexresources/js/import/contextMenuShipCompany.js"></script>
