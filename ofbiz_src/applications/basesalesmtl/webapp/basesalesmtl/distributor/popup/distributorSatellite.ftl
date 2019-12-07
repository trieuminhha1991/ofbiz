<script src="/crmresources/js/generalUtils.js"></script>

<script type="text/javascript">
    if (uiLabelMap == undefined) var uiLabelMap = {};
    uiLabelMap.ConfirmDeactiveDistributor = "${StringUtil.wrapString(uiLabelMap.ConfirmDeactiveDistributor)}";
    uiLabelMap.ConfirmActiveDistributor = "${StringUtil.wrapString(uiLabelMap.ConfirmActiveDistributor)}";
    uiLabelMap.BSActive = "${StringUtil.wrapString(uiLabelMap.BSActive)}";
    uiLabelMap.BSDeactive = "${StringUtil.wrapString(uiLabelMap.BSDeactive)}";
    uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
    uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
</script>

<div id="jqxwindowChangeSupervisor" style="display:none;">
	<div>${uiLabelMap.BSChangeSupervisor}</div>
	<div>
		<div class="row-fluid" style="margin-top: 29px !important;">
			<div class="span5">
				<label class="text-right asterisk">${uiLabelMap.BSSupervisor}</label>
			</div>
			<div class="span7">
				<div id="divSupervisor">
					<div style="border-color: transparent;" id="jqxgridSupervisor" tabindex="5"></div>
				</div>
			</div>
		</div>
	
		<input type="hidden" id="partyIdAvalible"/>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelChange" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveChange" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<div id="contextMenu" style="display:none;">
<ul>
    <li id="mnitemViewdetailnewtab"><i class="fa fa-folder-open-o"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewDetailInNewTab)}</li>
    <li id="mnitemViewdetail"><i class="fa fa-folder-open-o"></i>&nbsp;&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	<#if hasOlbEntityPermission("SALESORDER", "VIEW")>
	<li id="viewOrder"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfOrders}</li>
	</#if>
	<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_VIEW", "")>
	<li id="viewAgreement"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfAgreements}</li>
	</#if>
	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
		|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
		<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true)
		|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)>
			<#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)
			|| security.hasEntityPermission("PARTYMGR", "_ADMIN", session)>
			<li id="changeSupervisor"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeSupervisor}</li>
			</#if>
		</#if>
			<li id="viewSalesMan"><i class="fa-motorcycle"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListSalesmanShort}</li>
	</#if>
    <#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_MANAGER", userLogin, true)
        || Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESADMIN_MANAGER", userLogin, true)>
        <li id="rejectDistributor"><i class="fa-trash red"></i>&nbsp;&nbsp;${uiLabelMap.BSDeactiveDistributor}</li>
        <li id="activeDistributor"><i class="fas fa-check"></i>&nbsp;&nbsp;${uiLabelMap.BSActiveDistributor}</li>
    </#if>
    <li id="viewLocationDistributor"><i class="fa-map-marker"></i>&nbsp;&nbsp;${uiLabelMap.BSViewDistLocationOnMap}</li>
</ul>
</div>
<#if security.hasEntityPermission("PARTYSALESMAN", "_VIEW", session)>
<#include "component://basesalesmtl/webapp/basesalesmtl/distributor/popup/salesMans.ftl"/>
</#if>
<script>
	multiLang = _.extend(multiLang, {
	BSSupervisorId: "${StringUtil.wrapString(uiLabelMap.BSSupervisorId)}",
	BSSupervisor: "${StringUtil.wrapString(uiLabelMap.BSSupervisor)}",
	});
	var roleTypeIdFrom = "DISTRIBUTOR";
	var partyRelationshipTypeId = "SALES_EMPLOYMENT";
	var BSNotSalesmanSelected = "${StringUtil.wrapString(uiLabelMap.BSNotSalesmanSelected)}";
	if (typeof (DistributorSatellite) == "undefined") {
		var DistributorSatellite = (function() {
			var partyId, partyCode, mainGrid;
			var initJqxElements = function() {
				$("#jqxwindowChangeSupervisor").jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, resizable: false, isModal: true, autoOpen: false,
					cancelButton: $("#cancelChange"), modalOpacity: 0.7
				});
				$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
				var initSupervisorDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "partyId", type: "string" },
					                  { name: "partyCode", type: "string" },
					                  { name: "fullName", type: "string" }];
					var columns = [{text: multiLang.BSSupervisorId, datafield: "partyCode", width: 200},
					               {text: multiLang.BSSupervisor, datafield: "fullName"}];
					GridUtils.initDropDownButton({
						url: "JQGetListGTSupervisor", autorowheight: true, filterable: true, showfilterrow: true,
						width: width ? width : 600, source: {id: "supervisorId", pagesize: 5},
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									$("#jqxgridSupervisor").jqxGrid("clearfilters");
									return true;
								}
							}
					}, datafields, columns, null, grid, dropdown, "partyId", function(row){
							var str = row.fullName + "[" + row.partyCode + "]";
							return str;
						});
				};
				initSupervisorDrDGrid($("#divSupervisor"),$("#jqxgridSupervisor"), 600);
				
				if ($("#contextMenu").height() > 10) {
					$("#contextMenu").jqxMenu({ theme: "olbius", width: 220, autoOpenPopup: false, mode: "popup"});
				}
			};
			var handleEvents = function() {
				$("#saveChange").click(function() {
					var supervisorId = (Grid.getDropDownValue($("#divSupervisor"))+"").trim();
					if (supervisorId) {
						DataAccess.execute({
							url: "updateSupervisorDistributor",
							data: {
								partyIdTo: partyId,
								partyIdFrom: supervisorId}
							}, DistributorSatellite.notify);
					}
					$("#jqxwindowChangeSupervisor").jqxWindow("close");
				});
				$("#contextMenu").on('shown', function (event) {
					var rowindex = $("#jqxgridDistributor").jqxGrid('getSelectedRowindex');
	        		var dataRecord = $("#jqxgridDistributor").jqxGrid('getRowData', rowindex);
	        		if ( dataRecord.statusId == 'PARTY_DISABLED') {
	        			$("#contextMenu").jqxMenu('disable', 'rejectDistributor', true);
	        			$("#contextMenu").jqxMenu('disable', 'activeDistributor', false);
	        		}
	       	 		else {
	        			$("#contextMenu").jqxMenu('disable', 'rejectDistributor', false);
	        			$("#contextMenu").jqxMenu('disable', 'activeDistributor', true);
	        		}
				});
				$("#contextMenu").on("itemclick", function (event) {
			        var args = event.args;
			        var itemId = $(args).attr("id");
			        switch (itemId) {
                    case "mnitemViewdetailnewtab":
                        var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                        var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                        if (rowData) {
                            var url = 'DistributorDetail?me=distributorDSA&sub=distributorList&partyId=' + rowData.partyId;
                            var win = window.open(url, "_blank");
                            win.focus();
                        }
                        break;
                    case "mnitemViewdetail":
                        var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                        var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                        if (rowData) {
                            var url = 'DistributorDetail?me=distributorDSA&sub=distributorList&partyId=' + rowData.partyId;
                            var win = window.open(url, "_self");
                            win.focus();
                        }
                        break;
					case "changeSupervisor":
						$("#jqxgridSupervisor").jqxGrid("clearSelection");
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				    	var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				    	if (rowData) {
				    		partyId = rowData.partyId;
				    		if (rowData.supervisorId) {
				    			Grid.setDropDownValue($("#divSupervisor"), rowData.supervisorId, rowData.supervisor);
							} else {
								Grid.cleanDropDownValue($("#divSupervisor"));
							}
				    		DistributorSatellite.open();
						}
						break;
					case "viewSalesMan":
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							partyId = rowData.partyId;
                            partyCode = rowData.partyCode;
							$(".distributorInfo").text(rowData.groupName + " [" + partyCode + "]");
							$("#jqxwindowViewListSalesman").data("partyId", partyId);
							$("#jqxwindowViewListSalesman").data("supervisorId", rowData.supervisorId);
							Salesman.open();
						}
						break;
					case "rejectDistributor":
                        bootbox.dialog("${uiLabelMap.ConfirmDeactiveDistributor}",
                            [
                                {"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                                    "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                                    "callback": function() {bootbox.hideAll();}
                                },
                                {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                                    "callback": function() {
                                        var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                                        var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                                        if (rowData) {
                                            DataAccess.execute({
                                                url: "setDistributorStatus",
                                                data: {
                                                    partyId: rowData.partyId,
                                                    statusId: 'PARTY_DISABLED'
                                                    }
                                            }, DistributorSatellite.notify);
                                        }
                                    }
                                }
                            ]
                        );
						break;
					case "activeDistributor":
                        bootbox.dialog("${uiLabelMap.ConfirmActiveDistributor}",
                            [
                                {"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                                    "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                                    "callback": function() {bootbox.hideAll();}
                                },
                                {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                                    "callback": function() {
                                        var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                                        var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                                        if (rowData) {
                                            DataAccess.execute({
                                                url: "setDistributorStatus",
                                                data: {
                                                    partyId: rowData.partyId,
                                                    statusId: 'PARTY_ENABLED'
                                                    }
                                            }, DistributorSatellite.notify);
                                        }
                                    }
                                }
                            ]
                        );
						break;
					case "viewOrder":
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							window.open("listOrder?me=distributorDSA&sub=distributorList&partyId=" + rowData.partyId, '_blank');
						}
						break;
					case "viewAgreement":
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							window.open("AgreementWithAgent?me=distributorDSA&sub=distributorList&partyIdFrom=" + rowData.partyId, '_blank');
						}
						break;
                    case "viewLocationDistributor":
                        var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                        var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                        if (rowData) {
                            $(".distInfo").text(rowData.groupName + " [" + rowData.partyId + "]");
                            DistributorOnMap.open(rowData);
                        }
                        break;
					default:
						break;
					}
				});
				$("body").on("click", function() {
					$("#contextMenu").jqxMenu("close");
				});
			};
			var open = function() {
				var wtmp = window;
		    	var tmpwidth = $("#jqxwindowChangeSupervisor").jqxWindow("width");
		        $("#jqxwindowChangeSupervisor").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
		    	$("#jqxwindowChangeSupervisor").jqxWindow("open");
			};
			var openDistOnMap = function() {
                var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                if (rowData) {
                    $(".distInfo").text(rowData.groupName + " [" + rowData.partyId + "]");
                    DistributorOnMap.open(rowData);
                }
			};
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
			return {
				init: function(grid) {
					mainGrid = grid;
					initJqxElements();
					handleEvents();
					if (typeof (Salesman) != "undefined") {
						Salesman.init();
					}
				},
				open: open,
                openDistOnMap: openDistOnMap,
				notify: notify
			};
		})();
	}
</script>