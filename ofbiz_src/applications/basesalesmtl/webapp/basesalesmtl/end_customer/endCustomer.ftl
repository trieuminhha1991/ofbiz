<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script>
    var cellclass = function (row, columnfield, value) {
        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
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
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'partyCode', type: 'string'},
					 { name: 'partyTypeId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'fullName', type: 'string'},
					 { name: 'address1', type: 'string'},
					 { name: 'contactNumber', type: 'string'},
					 { name: 'emailAddress', type: 'string'},
					 { name: 'officeSiteName', type: 'string'},
					 { name: 'salesmanName', type: 'string'},
					 { name: 'salesmanId', type: 'string'},
					 { name: 'latitude', type: 'number'},
                     { name: 'longitude', type: 'number'},
                     { name: 'supervisorId', type: 'number'},
                     { name: 'geoPointId', type: 'string'},
					 { name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,

							cellclassname: cellclass,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.CustomerID)}', datafield: 'partyCode', width: 150,cellclassname: cellclass,
							cellsrenderer: function(row, column, value, a, b, data){
						        var link = 'EndCustomerDetail?partyId=' + data.partyId;
						        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'supervisorId', minWidth: 200,cellclassname: cellclass,hidden:true},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'fullName', cellclassname: cellclass, minWidth: 200},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: 120, cellclassname: cellclass, sortable: true},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', minWidth: 200, cellclassname: cellclass, sortable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.BSLocation)}', datafield: 'geoPointId', width: '10%', cellclassname: cellclass, sortable: false, filterable: false,
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
						{ text: '${StringUtil.wrapString(uiLabelMap.accApInvoice_partyTypeId)}', datafield: 'partyTypeId', cellclassname: cellclass, filtertype: 'checkedlist', width: 150, editable: true,
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
						{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesmanId)}', datafield: 'salesmanId', cellclassname: cellclass, width: 150, sortable: false},
					 	{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesmanName)}', datafield: 'salesmanName', cellclassname: cellclass, width: 150, sortable: false},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', cellclassname: cellclass, filtertype: 'checkedlist', width: 150, editable: true,
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
<#assign customcontrol2=""/>
<#assign mouseRightMenu="false"/>
<#if hasOlbPermission("MODULE", "PARTY_CUSTOMER_MT_NEW", "CREATE")>
    <#assign customcontrol1="icon-plus open-sans@${uiLabelMap.BSAddNew}@addEndCustomer" />
</#if>
<#--  <#if hasOlbPermission("MODULE", "PARTY_CUSTOMER_MT", "UPDATE")>
    <#assign customcontrol2="fa-user open-sans@${uiLabelMap.BSChangeSalesman}@javascript:window.location.href='MTCustomerChangeSalesman';"/>
</#if> -->

<@jqGrid url="jqxGeneralServicer?sname=JQGetListEndCustomers" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
sortdirection="desc" customcontrol1=customcontrol1 customcontrol2=customcontrol2
addrow="false" contextMenuId="contextMenu" mouseRightMenu="true"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/supervisor/popup/mtcustomerSatellite.ftl"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />

<#assign listPartyType = delegator.findByAnd("PartyType", {"parentTypeId": "PARTY_GROUP_CUSTOMER"}, null, false)!/>

<script>
    $(document).ready(function() {
        MTCustomerSatellite.init($("#jqxgrid"));
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