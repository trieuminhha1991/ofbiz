<script type="text/javascript">
    var cellClassPartyRelContact = function (row, columnfield, value) {
        var data = $('#jqxPartyRelContact').jqxGrid('getrowdata', row);
        var returnValue = "";
        if (typeof(data) != 'undefined') {
            var now = new Date();
            if (data.thruDate != null && data.thruDate < now) {
                return "background-cancel";
            } else if (data.fromDate >= now) {
                return "background-prepare";
            }
        }
    }
</script>
<#assign listConditions = [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("roleTypeGroupId", "SALES_CUSTOMERMT_CONTACT_ROLE")]/>
<#assign listConditions = listConditions + [Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()]/>
<#assign listRoleType = delegator.findList("RoleTypeGroupMemberDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listConditions) , null, ["sequenceNum"], null, false)!>
<script type="text/javascript">
    var roleTypeData = [
	<#if listRoleType?exists>
        <#list listRoleType as roleTypeItem>
		{	roleTypeId: "${roleTypeItem.roleTypeId}",
            description: "${StringUtil.wrapString(roleTypeItem.get("description", locale))}"
        },
        </#list>
    </#if>
    ];
	<#assign genderList = delegator.findList("Gender", null , null, orderBy,null, false)!>
    var genderData = [
	<#if genderList?exists>
        <#list genderList as item>
		{"genderId": "${item.genderId}", "description": "${item.description?if_exists}"},
        </#list>
    </#if>
    ];
</script>
<div id="partyrel-tab" class="tab-pane<#if activeTab?exists && activeTab == "partyrel-tab"> active</#if>">
    <div class="row-fluid">
        <div class="span12">
            <div id="jqxPartyRelContact"></div>
        </div>
    </div>
</div>
<#assign dataField = "[
			{name: 'partyIdFrom', type: 'string'},
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'firstName', type: 'string'},
			{name: 'middleName', type: 'string'},
			{name: 'lastName', type: 'string'},
			{name: 'fullName', type: 'string'},
			{name: 'gender', type: 'string'},
			{name: 'roleTypeIdTo', type: 'string'},
			{name: 'phoneNumber', type: 'string'},
			{name: 'emailAddress', type: 'string'},
			{name: 'fromDate', type: 'date', other: 'Timestamp'},
			{name: 'thruDate', type: 'date', other: 'Timestamp'}
		]"/>
<#assign columnlist = "
			{text: '${uiLabelMap.BSSTT}', dataField: '', width: '8%', columntype: 'number', cellClassName: cellClassPartyRelContact,
            	cellsrenderer: function (row, column, value) {
           			return '<div class=\"cellClassPartyRelContact\">' + (value + 1) + '</div>';
            	}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', dataField: 'fullName', minWidth: '10%', cellClassName: cellClassPartyRelContact},
			{text: '${StringUtil.wrapString(uiLabelMap.BSRoleType)}', dataField: 'roleTypeIdTo', width: '16%', filtertype: 'checkedlist', cellClassName: cellClassPartyRelContact,
				cellsrenderer: function(row, column, value){
					if (roleTypeData.length > 0) {
						for(var i = 0 ; i < roleTypeData.length; i++){
							if (value == roleTypeData[i].roleTypeId){
								return '<span title = ' + roleTypeData[i].description +'>' + roleTypeData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
					if (roleTypeData.length > 0) {
		   				var filterBoxAdapter2 = new $.jqx.dataAdapter(roleTypeData, {autoBind: true});
		                var uniqueRecords2 = filterBoxAdapter2.records;
		   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'roleTypeId', renderer: function (index, label, value)
						{
							for(i=0;i < roleTypeData.length; i++){
								if(roleTypeData[i].roleTypeId == value){
									return roleTypeData[i].description;
								}
							}
						    return value;
						}});
						widget.jqxDropDownList('checkAll');
					}
	   			}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSRepresentativeGender)}', dataField: 'gender', width: '14%', filtertype: 'checkedlist', cellClassName: cellClassPartyRelContact,
				cellsrenderer: function(row, column, value){
					if (genderData.length > 0) {
						for(var i = 0 ; i < genderData.length; i++){
							if (value == genderData[i].genderId){
								return '<span title = ' + genderData[i].description +'>' + genderData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
				},
				createfilterwidget: function (column, columnElement, widget) {
					if (genderData.length > 0) {
		   				var filterBoxAdapter2 = new $.jqx.dataAdapter(genderData, {autoBind: true});
		                var uniqueRecords2 = filterBoxAdapter2.records;
		   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'genderId', renderer: function (index, label, value)
						{
							for(i=0;i < genderData.length; i++){
								if(genderData[i].genderId == value){
									return genderData[i].description;
								}
							}
						    return value;
						}});
						widget.jqxDropDownList('checkAll');
					}
	   			}
			},
			{text: '${StringUtil.wrapString(uiLabelMap.BSPhoneNumber)}', dataField: 'phoneNumber', width: '14%', cellClassName: cellClassPartyRelContact},
			{text: '${StringUtil.wrapString(uiLabelMap.BSFromDate)}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', filtertype: 'range', cellClassName: cellClassPartyRelContact},
			{text: '${StringUtil.wrapString(uiLabelMap.BSThruDate)}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss', filtertype: 'range', cellClassName: cellClassPartyRelContact}
		"/>
<#assign contextMenuItemIdRole = "ctxmnupsrolelst">
<#assign permitCreate = false>
<#assign permitUpdate = false/>
<#assign permitDelete = false>
<#if hasOlbPermission("MODULE", "${permission}", "UPDATE")>
    <#assign permitCreate = true>
    <#assign permitUpdate = true>
</#if>
<#if hasOlbPermission("MODULE", "SALES_STOREROLETYPE_DELETE", "")><#assign permitDelete = true></#if>
<@jqGrid id="jqxPartyRelContact" isShowTitleProperty="false"
clearfilteringbutton="true" editable="false" alternativeAddPopup="popupPartyRelContactNew" columnlist=columnlist dataField=dataField
viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addType="popup" addrefresh="true"
jqGridMinimumLibEnable="true" deleterow="false"
url="jqxGeneralServicer?sname=JQGetListCustomerRelContact&partyId=${endCustomerInfo.partyId?if_exists}"
addrow="${permitCreate?string}" createUrl="jqxGeneralServicer?sname=createCustomerRelContact&jqaction=C" addColumns="customerId;roleTypeId;firstName;middleName;lastName;gender;phoneNumber"
deleterow="${permitDelete?string}" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteCustomerRelContact" deleteColumn="partyIdFrom;partyId"
mouseRightMenu="true" contextMenuId="contextMenu_${contextMenuItemIdRole}"/>

<div id='contextMenu_${contextMenuItemIdRole}' style="display:none">
    <ul>
        <li id="${contextMenuItemIdRole}_refresh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<#if permitUpdate><li id="${contextMenuItemIdRole}_update"><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li></#if>
		<#if permitDelete>
			<li id="${contextMenuItemIdRole}_delete"><i class="fa-ban open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li>
			<li id="${contextMenuItemIdRole}_reopen"><i class="fa-play open-sans"></i>${StringUtil.wrapString(uiLabelMap.BEReopen)}</li>
        </#if>
    </ul>
</div>

<#include "endCustomerViewPartyRelNew.ftl">
<#include "endCustomerViewPartyRelEdit.ftl">

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
    <#if hasOlbPermission("MODULE", "${permission}", "UPDATE")>
	$(function(){
        $('body').on("addSellerPosComplete", function(){
            $("#jqxPartyRelContact").jqxGrid("updatebounddata");
        });
    });
    </#if>
    var contextMenuItemIdRole = "${contextMenuItemIdRole}";
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.BSClickToChoose = '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}';
    uiLabelMap.BSPartyId = '${StringUtil.wrapString(uiLabelMap.BSPartyId)}';
    uiLabelMap.BSFullName = '${StringUtil.wrapString(uiLabelMap.BSFullName)}';

    $(function(){
        OlbProductStoreListRole.init();
    });

    var OlbProductStoreListRole = (function(){
        var init = (function(){
            initElement();
            initEvent();
        });
        var initElement = function(){
            jOlbUtil.contextMenu.create($("#contextMenu_" + contextMenuItemIdRole));
        };
        var initEvent = function(){
            $("#contextMenu_" + contextMenuItemIdRole).on('itemclick', function (event) {
                var args = event.args;
                var tmpId = $(args).attr('id');
                var idGrid = "#jqxPartyRelContact";

                var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
                var rowData = $(idGrid).jqxGrid("getrowdata", rowindex);

                switch(tmpId) {
                    case contextMenuItemIdRole + "_delete": {
                        $("#deleterowbuttonjqxPartyRelContact").click();
                        break;
                    };
                    case contextMenuItemIdRole + "_update": {
                        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
                        var data = $(idGrid).jqxGrid('getrowdata',rowindex);
                        OlbCustomerMTRelContactEdit.openWindow(data);
                        break;
                    };
                    case contextMenuItemIdRole + "_reopen": {
                        var rowindex = $(idGrid).jqxGrid('getselectedrowindex');
                        var data = $(idGrid).jqxGrid('getrowdata',rowindex);

                        jQuery.ajax({
                            url: 'reopenCustomerRelContact',
                            type: 'POST',
                            async: true,
                            data: {
                                "partyIdFrom": data.partyIdFrom,
                                "partyId": data.partyId,
                                "fromDate": (new Date(data.fromDate)).getTime(),
                            },
                            success: function(data) {
                                jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                                            $('#container').empty();
                                            $('#jqxNotification').jqxNotification({ template: 'error'});
                                            $("#jqxNotification").html(errorMessage);
                                            $("#jqxNotification").jqxNotification("open");
                                            return false;
                                        }, function(){
                                            $('#container').empty();
                                            $('#jqxNotification').jqxNotification({ template: 'info'});
                                            $("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.BSEditSuccess)}");
                                            $("#jqxNotification").jqxNotification("open");

                                            if ($("#jqxPartyRelContact").length > 0) {
                                                $("#jqxPartyRelContact").jqxGrid('updatebounddata');
                                                $("#jqxPartyRelContact").jqxGrid('clearselection');
                                            } else {
                                                location.reload();
                                            }
                                        }
                                );
                            },
                            error: function(e){
                                alert("Send request is error");
                            }
                        });

                        break;
                    };
                    case contextMenuItemIdRole + "_refesh": {
                        $(idGrid).jqxGrid('updatebounddata');
                        break;
                    };
                    default: break;
                }
            });

            $("#contextMenu_" + contextMenuItemIdRole).on('shown', function () {
                var rowIndexSelected = $("#jqxPartyRelContact").jqxGrid('getSelectedRowindex');
                var rowData = $("#jqxPartyRelContact").jqxGrid('getrowdata', rowIndexSelected);
                if (rowData != null && rowData.thruDate != null && rowData.thruDate != undefined) {
                    var thruDate = new Date(rowData.thruDate).getTime();
                    var nowDate = new Date().getTime();
                    if (thruDate < nowDate) {
                        $("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_delete', true);
                        $("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_reopen', false);
                    } else {
                        $("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_delete', false);
                        $("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_reopen', true);
                    }
                } else {
                    $("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_delete', false);
                    $("#contextMenu_" + contextMenuItemIdRole).jqxMenu('disable', contextMenuItemIdRole + '_reopen', true);
                }
            });
        };

        return {
            init: init,
        }
    }());
</script>
