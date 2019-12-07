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
					<div style="border-color: transparent;" id="wcjqxgridDistributor" tabindex="5"></div>
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
					<div style="border-color: transparent;" id="wcjqxgridSalesman" tabindex="5"></div>
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

<div id="jqxwindowChangeAddress" style="display:none;">
    <div>${uiLabelMap.BSChangeAddress}</div>
	<div id="divAddress"></div>
	<button id="cancelChangeAddress" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
</div>

<div id="jqxwindowVisitingCalendar" class='hide'>
    <div>${uiLabelMap.BSViewVisitingCalendar}</div>
    <div style="overflow: hidden;">
        <div class='form-window-content' style="height: 400px;">
            <div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.BSAgents}:</div> <div class="agentInfo jqxwindowTitle" style="display: inline-block;"></div></div>
            <div id="jqxgridVisitingCalendar"></div>
        </div>
        <div class="form-action">
            <button id="cancelVisitingCalendar" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
        </div>
    </div>
</div>

<div id="jqxwindowRoutesOfCustomer" class='hide'>
    <div>${uiLabelMap.BSListRouteOfCustomer}</div>
    <div style="overflow: hidden;">
        <div class='form-window-content' style="height: 400px;">
            <div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.BSAgents}:</div> <div class="agentInfo jqxwindowTitle" style="display: inline-block;"></div></div>
            <div id="jqxgridRoutesOfCustomer"></div>
        </div>
        <div class="form-action">
            <button id="cancelRoutesOfCustomer" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
        </div>
    </div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<#if hasOlbPermission("MODULE", "PARTY_DISTRIBUTOR_VIEW", "")>
	<#assign urlDistributor="JQGetListDistributor&sD=N"/>
</#if>
<#if security.hasEntityPermission("PARTYSALESMAN", "_VIEW", session)>
	<#assign urlSalesman="JQGetListSalesman"/>
</#if>

<script type="text/javascript">
	multiLang = _.extend(multiLang, {
		salesmanId: "${StringUtil.wrapString(uiLabelMap.salesmanId)}",
		DADistributorId: "${StringUtil.wrapString(uiLabelMap.DADistributorId)}",
		DADistributorName: "${StringUtil.wrapString(uiLabelMap.DADistributorName)}",
	});
	
	if (typeof (AgentSatellite) == "undefined") {
		var AgentSatellite = (function() {
			var mainGrid;
			var visitingCalendarGrid;
			var routesOfCustomerGrid;
			var initJqxElements = function() {
				$("#jqxwindowChangeDistributor").jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, height: 200, resizable: false, isModal: true, autoOpen: false,
					cancelButton: $("#cancelChangeDistributor"), modalOpacity: 0.7
				});
				$("#jqxwindowChangeSalesman").jqxWindow({
					theme: "olbius", width: 500, maxWidth: 1845, height: 160, resizable: false,  isModal: true, autoOpen: false,
					cancelButton: $("#cancelChangeSalesman"), modalOpacity: 0.7
				});
                $("#jqxwindowChangeAddress").jqxWindow({
                    theme: "olbius", width: 1000, maxWidth: 1845, height: 500, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
                });
                $("#jqxwindowVisitingCalendar").jqxWindow({
                    theme: "olbius", width: 750, maxWidth: 1845, minHeight: 310, height: 450, resizable: false,  isModal: true, autoOpen: false,
                    cancelButton: $("#cancelVisitingCalendar"), modalOpacity: 0.7
                });
                $("#jqxwindowRoutesOfCustomer").jqxWindow({
                    theme: "olbius", width: 750, maxWidth: 1845, minHeight: 310, height: 450, resizable: false,  isModal: true, autoOpen: false,
                    cancelButton: $("#cancelRoutesOfCustomer"), modalOpacity: 0.7
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
									$("#wcjqxgridDistributor").jqxGrid("clearfilters");
									return true;
								}
							}, dropdown: {width: 218, height: 30}, clearOnClose: 'Y'
					}, datafields, columns, null, grid, dropdown, "partyId", function(row){
						var str = row.groupName + "[" + row.partyCode + "]";
						return str;
					});
				};
				initDistributorDrDGrid($("#divDistributor"),$("#wcjqxgridDistributor"), 600);
				
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
										$("#wcjqxgridSalesman").jqxGrid("clearfilters");
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
				initSalesmanDrDGrid($("#divSalesman"),$("#wcjqxgridSalesman"), 600);
				
				$("#txtProductStore").jqxComboBox({ theme: 'olbius', source: [], width: 218, height: 30, displayMember: "storeName", valueMember: "productStoreId", multiSelect: true, dropDownHeight: 150});

                var configGridVisitingCalendar = {
                    datafields: [
                        {name: 'date', type: 'date'},
                        {name: 'salesmanId', type: 'string'},
                        {name: 'routeId', type: 'string'},
                        {name: 'salesRouteScheduleId', type: 'string'},
                        {name: 'scheduleRoute', type: 'string'},
                        {name: 'salesmanCode', type: 'string'},
                        {name: 'salesmanName', type: 'string'},
                    ],
                    columns: [
                        {text: '${StringUtil.wrapString(uiLabelMap.CommonDate)}', datafield: 'date', width: '20%', editable: false,
                            cellsformat: 'dd/MM/yyyy', filterType : 'range'},
                        {text: '${StringUtil.wrapString(uiLabelMap.BSScheduleDescription)}', dataField: 'scheduleRoute', width: '20%',filterable: true, filtertype: 'checkedlist',
                            cellsrenderer: function(row, colum, value){
                                value?value=dayMap[value]:value;
                                return '<span>' + value + '</span>';
                            },
                            createfilterwidget: function (column, columnElement, widget) {
                                widget.jqxDropDownList({ source: days, displayMember: 'description', valueMember: 'value' });
                            }
                        },
                        {text: '${StringUtil.wrapString(uiLabelMap.BSSalesmanCode)}', dataField: 'salesmanCode', width: '20%'},
                        {text: '${StringUtil.wrapString(uiLabelMap.BSSalesman)}', dataField: 'salesmanName'},

                    ],
                    width: '100%',
                    height: 'auto',
                    sortable: true,
                    filterable: true,
                    pageable: true,
                    pagesize: 10,
                    showfilterrow: true,
                    useUtilFunc: false,
                    useUrl: true,
                    url: '',
                    groupable: true,
                    showdefaultloadelement:true,
                    autoshowloadelement:true,
                    selectionmode:'singlerow',
                    virtualmode: true,
                };
                visitingCalendarGrid = new OlbGrid($("#jqxgridVisitingCalendar"), null, configGridVisitingCalendar, []);

                //routes of customer
                var configGridRoutesOfCustomer = {
                    datafields: [
                        {name: "routeId"}, {name: "routeCode"}, {name: "routeName"}, {name: "description"}, {name: "scheduleRoute"},
                    ],
                    columns: [
                        { text: '${StringUtil.wrapString(uiLabelMap.BsRouteId)}', datafield: 'routeCode', width: 140 },
                        { text: '${StringUtil.wrapString(uiLabelMap.BSRouteName)}', datafield: 'routeName', width: 170},
                        { text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: 160 },
                        { text: '${StringUtil.wrapString(uiLabelMap.BSScheduleDescription)}', datafield: 'scheduleRoute', width: 'auto', filterable: false,
                            cellsrenderer: function(row, column, value, a, b, data){
                                var val;
                                if(!!value) {
                                    var regexp = /\w+/gi;
                                    var matches = value.match(regexp);
                                    var dayWeekConverted = matches.map(function (v,i,a){
                                        return dayMap[v];
                                    });
                                    dayWeekConverted.sort();
                                    val = dayWeekConverted.join(", ")
                                } else {
                                    val = "";
                                }
                                return '<span>' + val + '</span>';
                            }
                        }
                    ],
                    width: '100%',
                    height: 'auto',
                    sortable: true,
                    filterable: true,
                    pageable: true,
                    pagesize: 10,
                    showfilterrow: true,
                    useUtilFunc: false,
                    useUrl: true,
                    url: '',
                    groupable: true,
                    showdefaultloadelement:true,
                    autoshowloadelement:true,
                    selectionmode:'singlerow',
                    virtualmode: true,
                };
                routesOfCustomerGrid = new OlbGrid($("#jqxgridRoutesOfCustomer"), null, configGridRoutesOfCustomer, []);
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
                $('#jqxwindowChangeAddress').on('close', function (event) { $("#jqxgridListRetailOutlet").jqxGrid("updatebounddata"); });

				/*
				$("body").on("click", function() {
					$("#contextMenu").jqxMenu("close");
				});
				*/
				
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
					var adapter = $("#wcjqxgridSalesman").jqxGrid('source');
					if(adapter){
						var url = "jqxGeneralServicer?sname=JQGetListSalesmanAssigned&partyIdFrom=" + partyIdFrom;
						adapter.url = url;
						adapter._source.url = url;
						$("#wcjqxgridSalesman").jqxGrid('source', adapter);
					}
				}
				
				var wtmp = window;
				var tmpwidth = $("#jqxwindowChangeSalesman").jqxWindow("width");
				$("#jqxwindowChangeSalesman").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
				$("#jqxwindowChangeSalesman").jqxWindow("open");
			};

            var openChangeAddress = function(partyId) {
                var wtmp = window;
                partyIdPram=partyId;
                var tmpwidth = $("#jqxwindowChangeAddress").jqxWindow("width");
				$("#jqxwindowChangeAddress").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
                $("#jqxwindowChangeAddress").jqxWindow("open");
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

            var openVisitingCalendar = function(partyId) {
                if (partyId) {
                    visitingCalendarGrid.updateSource("jqxGeneralServicer?sname=JQGetScheduleDatesOfCustomer&customerId=" + partyId);
                }
                var wtmp = window;
                var tmpwidth = $("#jqxwindowVisitingCalendar").jqxWindow("width");
                $("#jqxwindowVisitingCalendar").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
                $("#jqxwindowVisitingCalendar").jqxWindow("open");
            };
            var openRoutesOfCustomer = function(partyId) {
                if (partyId) {
                    routesOfCustomerGrid.updateSource("jqxGeneralServicer?sname=JQGetRoutesOfACustomer&customerId=" + partyId);
                }
                var wtmp = window;
                var tmpwidth = $("#jqxwindowRoutesOfCustomer").jqxWindow("width");
                $("#jqxwindowRoutesOfCustomer").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
                $("#jqxwindowRoutesOfCustomer").jqxWindow("open");
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
				openVisitingCalendar: openVisitingCalendar,
                openRoutesOfCustomer: openRoutesOfCustomer,
				openChangeAddress:openChangeAddress,
				notify: notify
			};
		})();
	}
</script>