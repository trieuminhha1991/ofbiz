<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'partyCode', type: 'string'},
					 { name: 'partyTypeId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'groupName', type: 'string'},
					 { name: 'telecomName', type: 'string'},
					 { name: 'postalAddressName', type: 'string'},
					 { name: 'emailAddress', type: 'string'},
					 { name: 'officeSiteName', type: 'string'},
					 { name: 'latitude', type: 'number'},
                     { name: 'longitude', type: 'number'},
                     { name: 'geoPointId', type: 'string'},
					 { name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BDPartyId)}', datafield: 'partyCode', width: 150,
							cellsrenderer: function(row, column, value, a, b, data){
						        var link = 'MTRepresentativeOfficeDetail?partyId=' + data.partyId;
						        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BDSupplierName)}', datafield: 'groupName', minWidth: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'telecomName', width: 120, sortable: true},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'postalAddressName', minWidth: 200, sortable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.BSLocation)}', datafield: 'geoPointId', width: '10%', sortable: false, filterable: false,
                            cellsrenderer: function(row, column, value, a, b, data){
                                var local = \"\",
                                localNoFixed = \"\";
                                if(!!value) {
                                    local = [data.latitude.toFixed(3), data.longitude.toFixed(3)].join(\", \");
                                    localNoFixed = [data.latitude, data.longitude].join(\", \");
                                }
                                return '<div class=\"jqx-grid-cell-left-align\" style=\"margin-top: 4px;\" title=\"'+localNoFixed+'\">'+local+'</div>';
                            }
                        },
						{ text: '${StringUtil.wrapString(uiLabelMap.accApInvoice_partyTypeId)}', datafield: 'partyTypeId', filtertype: 'checkedlist', width: 150, editable: true,
							cellsrenderer: function(row, colum, value){
								value?value=mapPartyType[value]:value;
						        return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, htmlElement, editor) {
		    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: listPartyType, displayMember: 'description', valueMember: 'partyTypeId' ,
		                            renderer: function (index, label, value) {
		                            	if (index == 0) {
		                            		return value;
										}
									    return mapPartyType[value];
					                }
		    		        	});
							}
						},
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
<#assign mouseRightMenu="false"/>
<#if hasOlbPermission("MODULE", "PARTY_CUSTOMER_MT_OFFICE", "VIEW")>
	<#assign customcontrol1="icon-plus open-sans@${uiLabelMap.BSAddNew}@MTRepresentativeOffice?addRepresentativeOffice='true'" />
</#if>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListMTRepresentativeOffices" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" filtersimplemode="true" sortdirection="desc"
		 customcontrol1=customcontrol1 contextMenuId="contextMenu" mouseRightMenu="true"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />

<#assign listPartyType = delegator.findByAnd("PartyType", {"parentTypeId": "PARTY_GROUP_CUSTOMER"}, null, false)!/>

<script>
	$(document).ready(function() {
		$(document).ready(function() {
			$('#jqxgrid').jqxGrid({ enabletooltips: true });
		});
	});
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
		statusId: '${item.statusId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	   },</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
			"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
		</#list></#if>};
	var listPartyType = [<#if listPartyType?exists><#list listPartyType as item>{
		partyTypeId: '${item.partyTypeId?if_exists}',
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapPartyType = {<#if listPartyType?exists><#list listPartyType as item>
	"${item.partyTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};

	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, false)
	|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, false)>
		var hiddenSupervisor = true;
	<#else>
		var hiddenSupervisor = false;
	</#if>
</script>