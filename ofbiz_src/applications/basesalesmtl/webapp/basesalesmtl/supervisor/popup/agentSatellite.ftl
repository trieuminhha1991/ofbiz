<script src="/crmresources/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<div id="jqxwindowChangeDistributor" style="display:none;">
	<div>${uiLabelMap.BSChangeDistributor}</div>
	<div>
		<div class="row-fluid" style="margin-top: 29px !important;">
			<div class="span5">
				<label class="text-right asterisk">${uiLabelMap.DADistributor}</label>
			</div>
			<div class="span7">
				<div id="divDistributor">
					<div style="border-color: transparent;" id="jqxgridDistributor" tabindex="5"></div>
				</div>
			</div>
		</div>
		
		<div class="row-fluid margin-top10">
			<div class="span5"><label class="text-right asterisk">${uiLabelMap.BSProductStore}</label></div>
			<div class="span7"><div id="txtProductStore"></div></div>
		</div>
	
		<input type="hidden" id="partyIdAvalible"/>
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancelChangeDistributor" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
				<button id="saveChangeDistributor" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
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

<div id="contextMenu" style="display:none;">
<ul>
	<#if hasOlbPermission("MODULE", "DIS_SALESORDER_VIEW", "")>
	<li id="viewOrder"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfOrders}</li>
	</#if>
	<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_VIEW", "")>
	<li id="viewAgreement"><i class="fa-eye"></i>&nbsp;&nbsp;${uiLabelMap.BSViewListOfAgreements}</li>
	</#if>
	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
	||Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
	<li id="changeDistributor"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeDistributor}</li>
	<li id="changeSalesman"><i class="fa-retweet"></i>&nbsp;&nbsp;${uiLabelMap.BSChangeSalesman}</li>
	<li id="rejectAgent"><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.CommonDelete}</li>
	</#if>
</ul>
</div>

<#if hasOlbPermission("MODULE", "PARTY_DISTRIBUTOR_VIEW", "")>
	<#assign urlDistributor="JQGetListDistributor&sD=N" />
</#if>
<#if security.hasEntityPermission("PARTYSALESMAN", "_VIEW", session)>
	<#assign urlSalesman="JQGetListSalesman" />
</#if>
<script>
	multiLang = _.extend(multiLang, {
		salesmanId: "${StringUtil.wrapString(uiLabelMap.salesmanId)}",
		DADistributorId: "${StringUtil.wrapString(uiLabelMap.DADistributorId)}",
		DADistributorName: "${StringUtil.wrapString(uiLabelMap.DADistributorName)}",
		});
	if (typeof (AgentSatellite) == "undefined") {
		var AgentSatellite = (function() {
			var partyId, mainGrid;
			var initJqxElements = function() {
				$("#jqxwindowChangeDistributor").jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, height: 200, resizable: false, isModal: true, autoOpen: false,
					cancelButton: $("#cancelChangeDistributor"), modalOpacity: 0.7
				});
				$("#jqxwindowChangeSalesman").jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, resizable: false,  isModal: true, autoOpen: false,
					cancelButton: $("#cancelChangeSalesman"), modalOpacity: 0.7
				});
				$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
				var initDistributorDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "partyId", type: "string" },
					                  { name: "partyCode", type: "string" },
					                  { name: "groupName", type: "string" }];
					var columns = [{text: multiLang.DADistributorId, datafield: "partyCode", width: 150},
					               {text: multiLang.DADistributorName, datafield: "groupName"}];
					GridUtils.initDropDownButton({
						url: "${urlDistributor?if_exists}", autorowheight: true, filterable: true, showfilterrow: true,
						width: width ? width : 600, source: {id: "partyId", pagesize: 5},
							handlekeyboardnavigation: function (event) {
								var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
								if (key == 70 && event.ctrlKey) {
									$("#jqxgridDistributor").jqxGrid("clearfilters");
									return true;
								}
							}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
					}, datafields, columns, null, grid, dropdown, "partyId", function(row){
						var str = row.groupName + "[" + row.partyCode + "]";
						return str;
					});
				};
				initDistributorDrDGrid($("#divDistributor"),$("#jqxgridDistributor"), 600);
				
				var initSalesmanDrDGrid = function(dropdown, grid, width){
					var datafields = [{ name: "partyId", type: "string" },
					                  { name: "partyCode", type: "string" },
					                  { name: "firstName", type: "string" },
					                  { name: "middleName", type: "string" },
					                  { name: "lastName", type: "string" },
					                  { name: "department", type: "string" }];
					var columns = [{text: multiLang.salesmanId, datafield: "partyCode", width: 150},
					               {text: multiLang.DmsPartyLastName, datafield: "lastName", width: 100},
					               {text: multiLang.DmsPartyMiddleName, datafield: "middleName", width: 100},
					               {text: multiLang.DmsPartyFirstName, datafield: "firstName", width: 100},
					               {text: multiLang.CommonDepartment, datafield: "department", width: 150}];
					GridUtils.initDropDownButton({
						url: "", autorowheight: true, filterable: true, showfilterrow: true,
						width: width ? width : 600, source: {id: "partyId", pagesize: 5},
								handlekeyboardnavigation: function (event) {
									var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
									if (key == 70 && event.ctrlKey) {
										$("#jqxgridSalesman").jqxGrid("clearfilters");
										return true;
									}
								}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
					}, datafields, columns, null, grid, dropdown, "partyId", function(row) {
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
				
				$("#txtProductStore").jqxComboBox({ theme: 'olbius', source: [], width: 218, height: 30, displayMember: "storeName", valueMember: "productStoreId", multiSelect: true, dropDownHeight: 150});
			};
			var handleEvents = function() {
				$("#saveChangeDistributor").click(function() {
					var distributorId = Grid.getDropDownValue($("#divDistributor")).trim();
					if (distributorId) {
						DataAccess.execute({
							url: "updateDistributorProvideAgent",
							data: {
								partyIdFrom: partyId,
								partyIdTo: distributorId,
								productStores: LocalUtil.getValueSelectedJqxComboBox($("#txtProductStore"))}
							}, AgentSatellite.notify);
					}
					$("#jqxwindowChangeDistributor").jqxWindow("close");
				});
				
				$("#saveChangeSalesman").click(function() {
					var salesmanId = Grid.getDropDownValue($("#divSalesman")).trim();
					if (salesmanId) {
						DataAccess.execute({
							url: "updateSalesmanProvideAgent",
							data: {
								partyIdTo: partyId,
								partyIdFrom: salesmanId}
						}, AgentSatellite.notify);
					}
					$("#jqxwindowChangeSalesman").jqxWindow("close");
				});
				
				$("#contextMenu").on("itemclick", function (event) {
			        var args = event.args;
			        var itemId = $(args).attr("id");
			        switch (itemId) {
					case "changeDistributor":
						$("#jqxgridDistributor").jqxGrid("clearSelection");
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
				    	var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
				    	if (rowData) {
				    		partyId = rowData.partyId;
				    		if (rowData.distributorId) {
				    			Grid.setDropDownValue($("#divDistributor"), rowData.distributorId, rowData.distributor);
					    		var source = { datatype: "json",
										datafields: [{ name: "productStoreId" },
										             { name: "storeName" }],
										             url: "loadProductStores?getAll=N&payToPartyId=" + rowData.distributorId};
								var dataAdapter = new $.jqx.dataAdapter(source);
								$("#txtProductStore").jqxComboBox({ source: dataAdapter });
							} else {
								Grid.cleanDropDownValue($("#divDistributor"));
							}
				    		AgentSatellite.openChangeDistributor();
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
							AgentSatellite.openChangeSalesman(rowData.distributorId);
						}
						break;
					case "rejectAgent":
						bootbox.confirm(multiLang.ConfirmDelete, multiLang.CommonCancel, multiLang.CommonSubmit, function(result) {
							if (result) {
								var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
								var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
								if (rowData) {
									DataAccess.execute({
										url: "rejectAgent",
										data: {
											partyId: rowData.partyId}
										}, AgentSatellite.notify);
								}
							}
						});
						break;
					case "viewOrder":
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							window.open("listSalesOrderDis?me=agent&sub=Agents&partyId=" + rowData.partyId, '_blank');
						}
						break;
					case "viewAgreement":
						var rowIndexSelected = mainGrid.jqxGrid("getSelectedRowindex");
						var rowData = mainGrid.jqxGrid("getrowdata", rowIndexSelected);
						if (rowData) {
							window.open("AgreementWithAgent?partyIdFrom=" + rowData.partyId, '_blank');
						}
						break;
					default:
						break;
					}
				});
				$("body").on("click", function() {
					$("#contextMenu").jqxMenu("close");
				});
				
				$("#divDistributor").on("close", function (event) {
					var value = Grid.getDropDownValue($("#divDistributor")).trim();
					if (value) {
						var source = { datatype: "json",
								datafields: [{ name: "productStoreId" },
								             { name: "storeName" }],
								             url: "loadProductStores?getAll=N&payToPartyId=" + value};
						var dataAdapter = new $.jqx.dataAdapter(source);
						$("#txtProductStore").jqxComboBox({ source: dataAdapter });
					}
				});
			};
			var openChangeDistributor = function() {
				var wtmp = window;
		    	var tmpwidth = $("#jqxwindowChangeDistributor").jqxWindow("width");
		        $("#jqxwindowChangeDistributor").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
		    	$("#jqxwindowChangeDistributor").jqxWindow("open");
		    	$("#txtProductStore").jqxComboBox("refresh");
			};
			var openChangeSalesman = function(partyIdFrom) {
				if (partyIdFrom) {
					var adapter = $("#jqxgridSalesman").jqxGrid('source');
					if(adapter){
						var url = "jqxGeneralServicer?sname=JQGetListSalesmanAssigned&partyIdFrom=" + partyIdFrom;
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
				},
				openChangeDistributor: openChangeDistributor,
				openChangeSalesman: openChangeSalesman,
				notify: notify
			};
		})();
	}
</script>