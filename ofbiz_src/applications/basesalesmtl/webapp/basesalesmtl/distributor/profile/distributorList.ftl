<#-- from ../distributor.ftl -->
<script>
    var cellclass = function (row, columnfield, value) {
        var data = $('#jqxgridDistributor').jqxGrid('getrowdata', row);
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
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'groupName', type: 'string'},
				{name: 'supervisorId', type: 'string'},
				{name: 'supervisor', type: 'string'},
				{name: 'address1', type: 'string'},
				{name: 'latitude', type: 'number'},
				{name: 'longitude', type: 'number'},
				{name: 'geoPointId', type: 'string'},
				{name: 'contactNumber', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'}
			]"/>
<#assign columnlist = "
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false, width: '5%',
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    },
				cellclassname: cellclass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSId)}', datafield: 'partyCode', width: '10%',
					cellsrenderer: function(row, column, value, a, b, data){
				        var link = 'DistributorDetail?me=distributorDSA&sub=distributorList&partyId=' + data.partyId;
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					},
					cellclassname: cellclass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'groupName', minwidth: '20%', cellclassname: cellclass},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: '10%', cellsalign: 'right', sortable: false, cellclassname: cellclass},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', width: '20%', sortable: false, cellclassname: cellclass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSLocation)}', datafield: 'geoPointId', width: '5%', sortable: false, cellClassName: cellclass,
					cellsrenderer: function(row, column, value, a, b, data){
					       	if(!!value) {
                                return '<div class=\"jqx-grid-cell-left-align\" title=\"${uiLabelMap.BSViewDistLocationOnMap}\">'
                                + '<a style=\"margin-left:10px\" onClick=\"DistributorSatellite.openDistOnMap();\"><i class=\"fa fa-globe\"></i></a>'
                                + '</div>';
					       	}else {
					       	    return '<div></div>';
					       	}
						}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: '8%', sortable: false, cellclassname: cellclass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSSupervisor)}', datafield: 'supervisor', width: '17%', sortable: false, hidden: hiddenSupervisor, cellclassname: cellclass},
			 	{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', width: '10%', filtertype: 'checkedlist',
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
					}, cellclassname: cellclass
				}
			"/>

<#assign customcontrol1=""/>
<#if hasOlbPermission("MODULE", "PARTY_DISTRIBUTOR_NEW", "")>
	<#assign customcontrol1="icon-plus open-sans@${uiLabelMap.BSAddNew}@AddDistributor" />
</#if>

<@jqGrid id="jqxgridDistributor" url="jqxGeneralServicer?sname=JQGetListDistributor" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
		 customcontrol1=customcontrol1
		 addrow="false" contextMenuId="contextMenu" mouseRightMenu="true" bindresize="true"/>

<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/popup/distributorSatellite.ftl"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/popup/viewDistributorOnMap.ftl"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<script>
	$(document).ready(function() {
		DistributorSatellite.init($("#jqxgridDistributor"));
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