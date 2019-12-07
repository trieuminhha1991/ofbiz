<div style="position:relative">
    <form class="form-horizontal form-window-content-custom" id="initRequirementEntry" name="initRequirementEntry" method="post" action="#">
        <div class="row-fluid">
            <div class="span6">
                <div class='row-fluid'>
                    <div class='span3'>
                        <label class="required">${uiLabelMap.BDProductStoreGroupName}</label>
                    </div>
                    <div class="span9">
                        <input id="productStoreGroupName" name="productStoreGroupName" style="padding-left: 3px; padding-right: 3px; width: 336px; left: 0px; top: 0px; margin-top: 4px; text-align: left;"/>
                    </div>
                </div>
            </div>
            <div class="span6">
                <div class='row-fluid'>
                    <div class='span3'>
                        <label>${uiLabelMap.BSDescription}</label>
                    </div>
                    <div class="span9">
                        <textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
                    </div>
                </div>
            </div>
        </div><!--.row-fluid-->
    </form>
</div>

<div style="position:relative" class="form-window-content-custom">
<#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'groupName', type: 'string'},
				{name: 'supervisorId', type: 'string'},
				{name: 'supervisor', type: 'string'},
				{name: 'address1', type: 'string'},
				{name: 'contactNumber', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'},
				{name: 'productStoreGroupName', type: 'string'}
			]"/>
<#assign columnlist = "
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false, width: '5%',
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSId)}', datafield: 'partyCode', width: '10%',
					cellsrenderer: function(row, column, value, a, b, data){
				        var link = 'DistributorDetail?me=distributorDSA&sub=distributorList&partyId=' + data.partyId;
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'groupName', minwidth: '20%'},
				{text: '${StringUtil.wrapString(uiLabelMap.BDProductStoreGroupName)}', datafield: 'productStoreGroupName', minwidth: '20%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: '10%', cellsalign: 'right', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', width: '20%', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: '8%', sortable: false},
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
					}
				}
			"/>

<@jqGrid  id="jqxgridOrder"  url="jqxGeneralServicer?sname=JQGetListDistributorAndGroup" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
showtoolbar="true" alternativeAddPopup="alterpopupWindow" editmode="click" selectionmode="checkbox" filtersimplemode="true" addType="popup" groupable="true"
<#--groups="productStoreGroupName" groupsexpanded="true"-->
defaultSortColumn="partyId" sortdirection="desc" customcontrol1=customcontrol1 contextMenuId="contextMenu" mouseRightMenu="true" bindresize="true"/>
</div>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />
<script>
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
<#include "script/newDistributorGroupInfoScript.ftl"/>
