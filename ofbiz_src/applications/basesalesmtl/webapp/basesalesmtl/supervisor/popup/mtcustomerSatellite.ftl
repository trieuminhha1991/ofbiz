<script src="/crmresources/js/generalUtils.js"></script>

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

<div id="jqxwindowChangeSalesman" style="display:none;">
	<div>${uiLabelMap.BSChangeSalesman}</div>
	<div>
		<div class="row-fluid" style="margin-top: 29px !important;">
			<div class="span5">
				<label class="text-right asterisk">${uiLabelMap.BSSalesman}</label>
			</div>
			<div class="span7">
				<div id="divSalesman">
					<div style="border-color: transparent;" id="jqxgridSalesman" tabindex="5"></div>
				</div>
			</div>
		</div>
	
		<input type="hidden" id="partyIdAvalible"/>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelChangeSalesman" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveChangeSalesman" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>
<#include "mtviewCustomerOnMap.ftl"/>
<div id="contextMenu" style="display:none;">
<ul>
	<#if hasOlbEntityPermission("SALESORDER", "VIEW")>
	<li id="viewOrder"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfOrders}</li>
	</#if>
	<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_VIEW", "")>
	<li id="viewAgreement"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfAgreements}</li>
	</#if>
    <li id="viewLocationCustomerMT"><i class="fa-map-marker"></i>&nbsp;&nbsp;${uiLabelMap.BSViewAgentLocationOnMap}</li>
	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
		|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
		<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_ASM_GT", userLogin, true)
		|| Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALES_ASM_MT", userLogin, true)>
		<li id="changeSupervisor"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeSupervisor}</li>
		</#if>
			<li id="changeSalesman"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeSalesman}</li>
			<li id="rejectMTCustomer"><i class="fa-trash red"></i>&nbsp;&nbsp;${uiLabelMap.BSDeactiveMT}</li>
			<li id="activeMTCustomer"><i class="fas fa-check"></i>&nbsp;&nbsp;${uiLabelMap.BSActiveMT}</li>
	</#if>
</ul>
</div>
<#if security.hasEntityPermission("PARTYSALESMAN", "_VIEW", session)>
<#assign urlSalesman="JQGetListSalesman" />
</#if>
<script>
	multiLang = _.extend(multiLang, {
	salesmanId: "${StringUtil.wrapString(uiLabelMap.salesmanId)}",
	BSSupervisorId: "${StringUtil.wrapString(uiLabelMap.BSSupervisorId)}",
	BSSupervisor: "${StringUtil.wrapString(uiLabelMap.BSSupervisor)}",
	});
	var BSNotSalesmanSelected = "${StringUtil.wrapString(uiLabelMap.BSNotSalesmanSelected)}";
	if (typeof (MTCustomerSatellite) == "undefined") {
		var MTCustomerSatellite = (function() {
			var partyId, mainGrid;
			var initJqxElements = function() {
				$("#jqxwindowChangeSupervisor").jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, resizable: false,  isModal: true, autoOpen: false,
					cancelButton: $("#cancelChange"), modalOpacity: 0.7
				});
				
				$("#jqxwindowChangeSalesman").jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, resizable: false,  isModal: true, autoOpen: false,
					cancelButton: $("#cancelChangeSalesman"), modalOpacity: 0.7
				});
				
				$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
				var initSupervisorDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "partyId", type: "string" },
					                  { name: "partyCode", type: "string" },
					                  { name: "fullName", type: "string" }];
					var columns = [{text: multiLang.BSSupervisorId, datafield: "partyCode", width: 200},
					               {text: multiLang.BSSupervisor, datafield: "fullName"}];
					GridUtils.initDropDownButton({
						url: "JQGetListMTSupervisor", autorowheight: true, filterable: true, showfilterrow: true,
						width: width ? width : 600, source: {id: "partyId", pagesize: 5},
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
				
				var initSalesmanDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "partyId", type: "string" },
					                  { name: "partyCode", type: "string" },
					                  { name: "fullName", type: "string" },
					                  { name: "middleName", type: "string" },
					                  { name: "lastName", type: "string" },
					                  { name: "department", type: "string" }];
					var columns = [{text: multiLang.salesmanId, datafield: "partyCode", width: 150},
					               {text: multiLang.DmsPartyFullName, datafield: "fullName"}
					               // {text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 100},
					               // {text: multiLang.DmsPartyFirstName, datafield: "firstName", width: 100},
					               // {text: multiLang.CommonDepartment, datafield: "department", width: 150}
                        ];
					GridUtils.initDropDownButton({
						url: "", autorowheight: true, filterable: true, showfilterrow: true,
						width: width ? width : 600, source: {id: "partyId", pagesize: 5},
								handlekeyboardnavigation: function (event) {
									var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
									if (key == 70 && event.ctrlKey) {
										$("#jqxgridSalesman").jqxGrid("clearfilters");
										return true;
									}
								}
					}, datafields, columns, null, grid, dropdown, "partyId", function(row){
						var first = row.firstName ? row.firstName : "";
						var mid = row.middleName ? row.middleName : "";
						var last = row.lastName ? row.lastName : "";
						var str = last + " " + mid + " " + first + "[" + row.partyCode + "]";
						return str;
					});
				};
				initSalesmanDrDGrid($("#divSalesman"),$("#jqxgridSalesman"), 600);
				
				if ($("#contextMenu").height() > 10) {
					$("#contextMenu").jqxMenu({ theme: "olbius", width: 220, autoOpenPopup: false, mode: "popup"});
				}
			};
			var handleEvents = function() {
				$("#saveChange").click(function() {
					var supervisorId = (Grid.getDropDownValue($("#divSupervisor"))+"").trim();
					if (supervisorId) {
						DataAccess.execute({
							url: "updateSupervisorMTCustomer",
							data: {
								partyIdTo: supervisorId,
								partyIdFrom: partyId}
							}, MTCustomerSatellite.notify);
					}
					$("#jqxwindowChangeSupervisor").jqxWindow("close");
				});
				
				$("#saveChangeSalesman").click(function() {
					var salesmanId = (Grid.getDropDownValue($("#divSalesman"))+"").trim();
					if (salesmanId) {
						DataAccess.execute({
							url: "updateSalesmanProvideCustomerMT",
							data: {
								partyIdTo: partyId,
								partyIdFrom: salesmanId}
						}, MTCustomerSatellite.notify);
					}
					$("#jqxwindowChangeSalesman").jqxWindow("close");
				});
				
				$("#contextMenu").on('shown', function (event) {
					var rowindex = mainGrid.jqxGrid('getSelectedRowindex');
	        		var dataRecord = mainGrid.jqxGrid('getRowData', rowindex);
	        		if ( dataRecord.statusId == 'PARTY_DISABLED') {
	        			$("#contextMenu").jqxMenu('disable', 'rejectMTCustomer', true);
	        			$("#contextMenu").jqxMenu('disable', 'activeMTCustomer', false);
	        		}
	       	 		else {
	        			$("#contextMenu").jqxMenu('disable', 'rejectMTCustomer', false);
	        			$("#contextMenu").jqxMenu('disable', 'activeMTCustomer', true);
	        		}
				});
				
				$("#contextMenu").on("itemclick", function (event) {
			        var args = event.args;
			        var itemId = $(args).attr("id");
			        switch (itemId) {
					case "changeSupervisor":
						$("#jqxgridSupervisor").jqxGrid("clearSelection");
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				    	var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				    	if (rowData) {
				    		partyId = rowData.partyId;
				    		if (rowData.partyIdTo) {
				    			Grid.setDropDownValue($("#divSupervisor"), rowData.partyIdTo, rowData.supervisorMT);
							} else {
								Grid.cleanDropDownValue($("#divSupervisor"));
							}
				    		MTCustomerSatellite.open();
						}
						break;
					case "changeSalesman":
						$("#jqxgridSalesman").jqxGrid("clearSelection");
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							partyId = rowData.partyId;
							if (rowData.salesmanId) {
								Grid.setDropDownValue($("#divSalesman"), rowData.salesmanId, rowData.salesman);
							} else {
								Grid.cleanDropDownValue($("#divSalesman"));
							}
							MTCustomerSatellite.openChangeSalesman(rowData.supervisorId);
						}
						break;
					case "rejectMTCustomer":
						bootbox.confirm("${uiLabelMap.ConfirmDeactiveMT}", multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
							if (result) {
								var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
								var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
								if (rowData) {
									DataAccess.execute({
										url: "setMTStatus",
										data: {
											partyId: rowData.partyId,
											statusId: 'PARTY_DISABLED'
											}
										}, MTCustomerSatellite.notify);
								}
							}
						});
						break;
					case "activeMTCustomer":
						bootbox.confirm("${uiLabelMap.ConfirmActiveMT}", multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
							if (result) {
								var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
								var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
								if (rowData) {
									DataAccess.execute({
										url: "setMTStatus",
										data: {
											partyId: rowData.partyId,
											statusId: 'PARTY_ENABLED'
											}
										}, MTCustomerSatellite.notify);
								}
							}
						});
						break;
					case "viewOrder":
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							window.open("listOrder?me=customerMT&sub=MTCustomers&partyId=" + rowData.partyId, '_blank');
						}
						break;
					case "viewAgreement":
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							window.open("AgreementWithAgent?me=customerMT&sub=MTCustomers&partyIdFrom=" + rowData.partyId, '_blank');
						}
						break;
					case "viewLocationCustomerMT":
                        var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
                        var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
                        if (rowData) {
                            $(".agentInfo").text(rowData.fullName + " [" + rowData.partyId + "]");
                            MTCustomerOnMap.open(rowData);
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
			var openChangeSalesman = function(supervisorId) {
				if (supervisorId) {
					var adapter = $("#jqxgridSalesman").jqxGrid('source');
					if(adapter){
						var url = "jqxGeneralServicer?sname=JQGetListSalesman&supervisorId=" + supervisorId;
						adapter.url = url;
						adapter._source.url = url;
						$("#jqxgridSalesman").jqxGrid('source', adapter);
					}
				}
				
				var wtmp = window;
				var tmpwidth = $("#jqxwindowChangeSalesman").jqxWindow("width");
				$("#jqxwindowChangeSalesman").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
				$("#jqxwindowChangeSalesman").jqxWindow("open");
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
				notify: notify,
				openChangeSalesman: openChangeSalesman
			};
		})();
	}
</script>