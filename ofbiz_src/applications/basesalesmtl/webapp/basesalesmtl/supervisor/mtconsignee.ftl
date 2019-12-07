<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script>
    var cellClass = function (row, columnfield, value) {
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
					 { name: 'groupName', type: 'string'},
					 { name: 'telecomName', type: 'string'},
					 { name: 'postalAddressName', type: 'string'},
					 { name: 'emailAddress', type: 'string'},
					 { name: 'officeSiteName', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,cellClassName: cellClass,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.DepartmentCode)}', datafield: 'partyCode', width: 150,cellClassName: cellClass,
							cellsrenderer: function(row, column, value, a, b, data){
						        var link = 'MTConsigneeDetail?partyId=' + data.partyId;
						        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSConsigneeName)}', datafield: 'groupName',width: 200, minWidth: 200,cellClassName: cellClass},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'telecomName', width: 120, sortable: true,cellClassName: cellClass},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'postalAddressName', minWidth: 200, sortable: true,cellClassName: cellClass},
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsStatus)}', datafield: 'statusId', filtertype: 'checkedlist', width: 150, editable: true,cellClassName: cellClass,
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
<div id="jqxwindowChangeAddress" style="display:none;">
    <div>${uiLabelMap.BSChangeAddress}</div>
    <div id="divAddress"></div>
    <button id="cancelChangeAddress" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
</div>
<div id="jqxNotificationNested">
    <div id="notificationContentNested">
    </div>
</div>
<#assign customcontrol1=""/>
<#assign mouseRightMenu="false"/>
<#if hasOlbPermission("MODULE", "PARTY_CUSTOMER_MT_CONSIGNEE_NEW", "CREATE")>
	<#assign customcontrol1="icon-plus open-sans@${uiLabelMap.BSAddNew}@MTConsignee?addConsignee='true'" />
</#if>
<#assign contextMenuItemIdRol = "ctxmnurol">
<div id="contextMenu" style="display:none;">
    <ul>
		<li id="changeAddress"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeAddress}</li>
        <li id="activeMTConsignee"><i class="fa-check"></i>&nbsp;&nbsp;${uiLabelMap.BSActiveMT}</li>
        <li id="rejectMTConsignee"><i class="fa-trash red"></i>&nbsp;&nbsp;${uiLabelMap.BSDeactiveMT}</li>
    </ul>
</div>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListMTConsignee" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" filtersimplemode="true" sortdirection="desc"
		 customcontrol1=customcontrol1 contextMenuId="contextMenu" mouseRightMenu="true"/>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />

<#assign listPartyType = delegator.findByAnd("PartyType", {"parentTypeId": "PARTY_GROUP_CUSTOMER"}, null, false)!/>

<script>
    var mainGrid = $("#jqxgrid");
	$(document).ready(function() {
		$(document).ready(function() {
			$('#jqxgrid').jqxGrid({ enabletooltips: true });
            if ($("#contextMenu").height() > 10) {
                $("#contextMenu").jqxMenu({ theme: "olbius", width: 220, autoOpenPopup: false, mode: "popup"});
            }
            $("#contextMenu").on("itemclick", function (event) {
                var args = event.args;
                var itemId = $(args).attr("id");
                switch (itemId) {
                    case "changeAddress":
                        var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                        var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                        if (rowData) {
                            partyId = rowData.partyId;
                            openChangeAddress(partyId);
                        }
                        break;
                    case "rejectMTConsignee":
                        jOlbUtil.confirm.dialog(multiLang.ConfirmDeactiveMT, function(result){
                            if (result) {
                                var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                                var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                                if (rowData) {
                                    DataAccess.execute({
                                        url: "rejectMTConsignee",
                                        data: {
                                            partyId: rowData.partyId}
                                    }, notify);
                                }
                            }
                        }, multiLang.CommonCancel, multiLang.CommonSubmit);
                        break;
                    case "activeMTConsignee":
                        jOlbUtil.confirm.dialog(multiLang.ConfirmActiveMT, function(result){
                            if (result) {
                                var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                                var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                                if (rowData) {
                                    DataAccess.execute({
                                        url: "setPartyStatus",
                                        data: {
                                            partyId: rowData.partyId,
                                            statusId: "PARTY_ENABLED",
                                            oldStatusId: "PARTY_DISABLED"
                                        }
                                    }, notify);
                                }
                            }
                        }, multiLang.CommonCancel, multiLang.CommonSubmit);
                        break;
                    default:
                        break;
                }
            });
            $("#contextMenu").on('shown', function (event) {
                var rowindex = mainGrid.jqxGrid('getSelectedRowindex');
                var dataRecord = mainGrid.jqxGrid('getRowData', rowindex);
                if ( dataRecord.statusId == 'PARTY_DISABLED') {
                    $("#contextMenu").jqxMenu('disable', 'rejectMTConsignee', true);
                    $("#contextMenu").jqxMenu('disable', 'activeMTConsignee', false);
                }
                else {
                    $("#contextMenu").jqxMenu('disable', 'rejectMTConsignee', false);
                    $("#contextMenu").jqxMenu('disable', 'activeMTConsignee', true);
                }
            });
            $("#jqxwindowChangeAddress").jqxWindow({
                theme: "olbius", width: 1000, maxWidth: 1845, height: 500, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
            });
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
    var notify = function(res) {
        $("#jqxNotificationNested").jqxNotification("closeLast");
        if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
            var errormes = "";
            res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
            $("#jqxNotificationNested").jqxNotification({ template: "error"});
            $("#notificationContentNested").text(errormes);
            $("#jqxNotificationNested").jqxNotification("open");
        }else {
            $("#jqxNotificationNested").jqxNotification({ template: "info"});
            $("#notificationContentNested").text(multiLang.updateSuccess);
            $("#jqxNotificationNested").jqxNotification("open");
            mainGrid.jqxGrid("updatebounddata");
        }
    };
    var openChangeAddress = function(partyId) {
        var wtmp = window;
        partyIdPram=partyId;
        var tmpwidth = $("#jqxwindowChangeAddress").jqxWindow("width");
        $("#jqxwindowChangeAddress").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
        $("#jqxwindowChangeAddress").jqxWindow("open");
        $('#jqxwindowChangeAddress').on('close', function (event) { location.reload() });
        $.ajax({
            type: 'POST',
            url: 'EditAddressAgent',
            data: {
                partyId:partyId
            },
            beforeSend: function(){
                /*$("#loader_page_common").show();*/
            },
            success: function(data){
                jOlbUtil.processResultDataAjax(data, "default", "default", function(){

                    $("#divAddress").html(data);
                });
            },
            error: function(data){
                alert("Send request is error");
            },
            complete: function(data){

                /*$("#loader_page_common").hide();*/
            },
        });
    };
</script>