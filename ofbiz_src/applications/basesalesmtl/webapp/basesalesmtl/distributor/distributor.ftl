<#-- TODO deleted -->
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'partyCode', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'groupName', type: 'string'},
					 { name: 'supervisorId', type: 'string'},
					 { name: 'supervisor', type: 'string'},
					 { name: 'address1', type: 'string'},
					 { name: 'contactNumber', type: 'string'},
					 { name: 'emailAddress', type: 'string'},
					 { name: 'officeSiteName', type: 'string'},
					 { name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.DADistributorId)}', datafield: 'partyCode', width: 150,
							cellsrenderer: function(row, column, value, a, b, data){
						        var link = 'DistributorDetail?me=distributorDSA&sub=distributorList&partyId=' + data.partyId;
						        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.DADistributorName)}', datafield: 'groupName', minwidth: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 200, sortable: false},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', width: 250, sortable: false},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: 200, sortable: false},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSSupervisor)}', datafield: 'supervisor', sortable: false, width: 200, hidden: hiddenSupervisor},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: 150, editable: true,
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
						}"/>

<#assign customcontrol1=""/>
<#if hasOlbPermission("MODULE", "PARTY_DISTRIBUTOR_NEW", "")>
	<#assign customcontrol1="icon-plus open-sans@${uiLabelMap.BSAddNew}@AddDistributor" />
</#if>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListDistributor" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
		 defaultSortColumn="createdDate" sortdirection="desc" customcontrol1=customcontrol1
		 addrow="false" contextMenuId="contextMenu" mouseRightMenu="true"/>

<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/popup/distributorSatellite.ftl"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<script>
	$(document).ready(function() {
		DistributorSatellite.init($("#jqxgrid"));
	});
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	   },</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};
	
	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, false)
	|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, false)>
		var hiddenSupervisor = true;
	<#else>
		var hiddenSupervisor = false;
	</#if>
</script>