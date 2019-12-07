<div id="upload-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "upload-tab"> active</#if>">
<div id="stocking-form">
	<div class="row-fluid form-window-content <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>hide</#if>">
		<div class="span4">
			<div class="row-fluid form-window-content margin-top10">
				<div class="span5" style="width: calc(40.17094017094017% - 10px);"><label class="text-right asterisk">${uiLabelMap.DmsCountEmpl}</label></div>
				<div class="span7"><div id="txtStockCount"></div></div>
			</div>
		</div>
		<div class="span4">
			<div class="row-fluid form-window-content margin-top10">
				<div class="span5" style="width: calc(40.17094017094017% - 10px);"><label class="text-right asterisk">${uiLabelMap.DmsSCanEmpl}</label></div>
				<div class="span7"><div id="txtStockScan"></div></div>
			</div>
		</div>
		<div class="span4">
			<div class="row-fluid form-window-content margin-top10">
				<div class="span5" style="width: calc(40.17094017094017% - 10px);"><label class="text-right asterisk">${uiLabelMap.DmsCheckEmpl}</label></div>
				<div class="span7"><div id="txtStockCheck"></div></div>
			</div>
		</div>
	</div>
	<div class="row-fluid form-window-content">
		<div class="span4 <#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>hide</#if>">
			<div class="row-fluid form-window-content margin-top10">
				<div class="span5" style="width: calc(40.17094017094017% - 10px);"><label class="text-right asterisk">${uiLabelMap.DmsInputEmpl}</label></div>
				<div class="span7"><div id="txtStockInput"></div></div>
			</div>
		</div>
		<div class="span4">
			<div class="row-fluid form-window-content margin-top10">
				<div class="span5" style="width: calc(40.17094017094017% - 10px);"><label class="text-right asterisk">${uiLabelMap.DmsFileScan}</label></div>
				<div class="span7">
					<input type="file" id="id-input-file-1" name="document" accept=".txt, .xls" <#if isThru>disabled</#if>/>
				</div>
			</div>
		</div>
		<div class="span4">
			<div class="row-fluid form-window-content margin-top10">
				<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
				<button id="btnUpload" type="button" class="btn btn-primary form-action-button disabled">
					${uiLabelMap.CommonUpload}
				</button>
				</#if>
			</div>
		</div>
	</div>
</div>

<#assign dataField="[{ name: 'eventId', type: 'string' },
					{ name: 'partyId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'idValue', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'expireDate', type: 'date', other: 'timestamp' },
					{ name: 'manufactureDate', type: 'date', other: 'timestamp' },
					{ name: 'lot', type: 'string' },
					{ name: 'location', type: 'string' }]"/>

<#assign columnlist = "
			{ text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (row + 1) + '</div>';
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productCode', width: 150 },
			{ text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', minwidth: 200 },
			{ text: '${StringUtil.wrapString(uiLabelMap.UPC)}', datafield: 'idValue', width: 150 },
			{ text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity', filtertype: 'number', width: 150,
				cellsrenderer: function (row, column, value, a, b, data) {
					return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
				}
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', datafield: 'expireDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', datafield: 'manufactureDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range' },
			{ text: '${StringUtil.wrapString(uiLabelMap.Batch)}', datafield: 'lot', width: 150 },
			{ text: '${StringUtil.wrapString(uiLabelMap.Location)}', datafield: 'location', width: 200 }"/>
	
<@jqGrid id="jqxgridStocking" url="" customTitleProperties="BSPreview"
	dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	showtoolbar="true" filtersimplemode="true" addrow="false" editable="false" deleterow="false"/>

<div class="row-fluid margin-top10">
	<div class="span12">
		<#if hasOlbPermission("MODULE", "INVENTORY_STOCKING", "UPDATE")>
		<button id="btnCommit" type="button" class="btn btn-primary form-action-button pull-right hidden">
			<i class="fa fa-check"></i>${uiLabelMap.POCommonOK}
		</button>
		<button id="btnDelete" type="button" class="btn btn-danger form-action-button pull-right hidden">
			<i class="fa fa-remove"></i>${uiLabelMap.DmsDeleteAll}
		</button>
		</#if>
	</div>
</div>

<div id="jqxNotification">
<div id="notificationContent"></div>
</div>

<script>
	const eventId = "${stockEvent.eventId}";
	multiLang = _.extend(multiLang, {
		FormatWrong: "${StringUtil.wrapString(uiLabelMap.FormatWrong)}",
		SGCDaDuocUpload: "${StringUtil.wrapString(uiLabelMap.DmsUploaded)}",
	});
	$(document).ready(function () {
		Uploader.init();
	});
	var Uploader = (function () {
		var mainGrid;
		var initElements = function () {
			$("#id-input-file-1").ace_file_input({
				no_file: "${StringUtil.wrapString(uiLabelMap.EcommerceNoFiles)} ...",
				btn_choose: "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}",
				btn_change: "${StringUtil.wrapString(uiLabelMap.CommonChange)}",
				droppable:false,
				onchange: null,
				thumbnail:true,
				before_change:function (file, dropped) {
					if ($("#id-input-file-1").val()) {
						if ($("#btnUpload").hasClass("disabled")) {
							$("#btnUpload").removeClass("disabled");
						}
					} else {
						if (!$("#btnUpload").hasClass("disabled")) {
							$("#btnUpload").addClass("disabled");
						}
					}
					return true;
				}
			});
			$("#jqxNotification").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
			
			$("#txtStockInput").jqxDropDownList({ source: getEmployee("STOCKING_INPUT"), selectedIndex: 0, displayMember: "partyName", valueMember: "partyId", width: "100%", height: 25,
				theme: theme, placeHolder: multiLang.filterchoosestring });
			$("#txtStockCount").jqxDropDownList({ source: getEmployee("STOCKING_COUNT"), selectedIndex: 0, displayMember: "partyName", valueMember: "partyId", width: "100%", height: 25,
				theme: theme, placeHolder: multiLang.filterchoosestring });
			$("#txtStockScan").jqxDropDownList({ source: getEmployee("STOCKING_SCAN"), selectedIndex: 0, displayMember: "partyName", valueMember: "partyId", width: "100%", height: 25,
				theme: theme, placeHolder: multiLang.filterchoosestring });
			$("#txtStockCheck").jqxDropDownList({ source: getEmployee("STOCKING_CHECK"), selectedIndex: 0, displayMember: "partyName", valueMember: "partyId", width: "100%", height: 25,
				theme: theme, placeHolder: multiLang.filterchoosestring });
		};
		var getEmployee = function (roleTypeId) {
			var source =
			{
				datatype: "json",
				datafields: [
					{ name: "partyId" },
					{ name: "partyName" }
				],
				url: "loadPartiesInEventByRole?eventId=" + eventId + "&roleTypeId=" + roleTypeId,
				async: true
			};
			return new $.jqx.dataAdapter(source);
		};
		var validator = function (file) {
			var validate = false;
			var name = file.name;
			if (name && file.size) {
				var dummy = name.split(".");
				if (dummy.length == 2 && ((dummy[1] && dummy[1].toUpperCase() == "TXT") || (dummy[1] && dummy[1].toUpperCase() == "XLS"))) {
					validate = true;
				}
			}
			return validate;
		};
		var handleEvents = function () {
			$("#btnUpload").click(function () {
				if ($("#stocking-form").jqxValidator("validate") && $("#id-input-file-1").val()) {
					
					var files = $("#id-input-file-1")[0].files;
					var validate = true;
					for ( var x in files) {
						var name = files[x].name;
						if (name && files[x].size) {
							var dummy = name.split(".");
							if (!(dummy.length == 2 && ((dummy[1] && dummy[1].toUpperCase() == "TXT") || (dummy[1] && dummy[1].toUpperCase() == "XLS")))) {
								bootbox.alert(multiLang.FormatWrong + ": " + name);
								validate = false;
							}
						}
					}
					if (validate) {
						validate = true;
						
						if (dummy.length == 2 && dummy[1] && dummy[1].toUpperCase() == "TXT") {
						
						
							for ( var x in files) {
								if (validator(files[x])) {
									
									var check = DataAccess.getData({
										url: "checkLocationExistsInStockEvent",
										data: { eventId: "${stockEvent.eventId}", location: files[x].name.split(".")[0] },
										source: "check"});
									
									if ("false" == check) {
										bootbox.alert("File: " + files[x].name + " " + multiLang.SGCDaDuocUpload);
										validate = false;
									}
								}
							}
							if (validate) {
								var form_data= new FormData();
								for ( var x in files) {
									if (validator(files[x])) {
										form_data.append(files[x].name, files[x]);
									}
								}
								form_data.append("eventId", "${stockEvent.eventId}");
								form_data.append("partyInput", $("#txtStockInput").jqxDropDownList("val"));
								form_data.append("partyCount", $("#txtStockCount").jqxDropDownList("val"));
								form_data.append("partyScan", $("#txtStockScan").jqxDropDownList("val"));
								form_data.append("partyCheck", $("#txtStockCheck").jqxDropDownList("val"));
								$.ajax({
									url: "uploadStockingFiles",
									type: "POST",
									data: form_data,
									cache : false,
									contentType : false,
									processData : false,
									success: function () {}
								}).done(function (res) {
									if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
										var errormes = "";
										res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
										$("#jqxNotification").jqxNotification({ template: "error"});
										$("#notificationContent").text(errormes);
										$("#jqxNotification").jqxNotification("open");
									} else {
										var message = "";
										res["_MESSAGE_"]?message=res["_MESSAGE_"]:message="${StringUtil.wrapString(uiLabelMap.uploadSuccessfully)}";
										$("#jqxNotification").jqxNotification({ template: "info"});
										$("#notificationContent").text(message);
										$("#jqxNotification").jqxNotification("open");
										updateGrid();
										mainGrid.jqxGrid("updatebounddata");
										$(".ace-file-input i.icon-remove").click();
									}
								});
							}
						} else {
							var form_data= new FormData();
							form_data.append("uploadedFile", $("#id-input-file-1")[0].files[0]);
							form_data.append("fileName", $("#id-input-file-1").val());
							
							form_data.append("eventId", "${stockEvent.eventId}");
							form_data.append("partyInput", $("#txtStockInput").jqxDropDownList("val"));
							form_data.append("partyCount", $("#txtStockCount").jqxDropDownList("val"));
							form_data.append("partyScan", $("#txtStockScan").jqxDropDownList("val"));
							form_data.append("partyCheck", $("#txtStockCheck").jqxDropDownList("val"));
							$.ajax({
								url: "uploadStockingExcelFiles",
								type: "POST",
								data: form_data,
								cache : false,
								contentType : false,
								processData : false,
								success: function() {}
							}).done(function(res) {
								if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]) {
									var errormes = "";
									res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
									$("#jqxNotification").jqxNotification({ template: "error"});
									$("#notificationContent").text(errormes);
									$("#jqxNotification").jqxNotification("open");
								} else {
									var message = "";
									res["_MESSAGE_"]?message=res["_MESSAGE_"]:message="${StringUtil.wrapString(uiLabelMap.uploadSuccessfully)}";
									$("#jqxNotification").jqxNotification({ template: "info"});
									$("#notificationContent").text(message);
									$("#jqxNotification").jqxNotification("open");
									updateGrid();
									mainGrid.jqxGrid("updatebounddata");
									$(".ace-file-input i.icon-remove").click();
								}
							});
						}
					}
				}
			});
			$("#txtStockInput, #txtStockCount, #txtStockScan, #txtStockCheck").on("change", function (event) {
				var args = event.args;
				if (args) {
					var item = args.item;
					if (item) {
						updateGrid();
					}
				}
			});
			mainGrid.on("bindingcomplete", function (event) {
				if (!_.isEmpty(mainGrid.jqxGrid("getrows"))) {
					$("#btnCommit").removeClass("hidden");
					$("#btnDelete").removeClass("hidden");
				} else {
					if (!$("#btnCommit").hasClass("hidden")) {
						$("#btnCommit").addClass("hidden");
					}
					if (!$("#btnDelete").hasClass("hidden")) {
						$("#btnDelete").addClass("hidden");
					}
				}
			});
			$("#btnDelete").click(function () {
				DataAccess.executeAsync({ url: "deleteAllStockEventItemTempData",
					data: {
						eventId: "${stockEvent.eventId}",
						partyInput: $("#txtStockInput").jqxDropDownList("val"),
						partyCount: $("#txtStockCount").jqxDropDownList("val"),
						partyScan: $("#txtStockScan").jqxDropDownList("val"),
						partyCheck: $("#txtStockCheck").jqxDropDownList("val")
					} }, function () {
					mainGrid.jqxGrid("updatebounddata");
				});
			});
			$("#btnCommit").click(function () {
				DataAccess.executeAsync({ url: "transferToStockEventItem",
					data: {
						eventId: "${stockEvent.eventId}",
						partyInput: $("#txtStockInput").jqxDropDownList("val"),
						partyCount: $("#txtStockCount").jqxDropDownList("val"),
						partyScan: $("#txtStockScan").jqxDropDownList("val"),
						partyCheck: $("#txtStockCheck").jqxDropDownList("val")
					} }, function () {
					mainGrid.jqxGrid("updatebounddata");
				});
			});
		};
		var initValidator = function () {
			$("#stocking-form").jqxValidator({
				rules:
				[
					{ input: "#txtStockInput", message: multiLang.fieldRequired, action: "change",
						rule: function (input, commit) {
							return !!input.val();
						}
					},
					{ input: "#txtStockCount", message: multiLang.fieldRequired, action: "change",
						rule: function (input, commit) {
							return !!input.val();
						}
					},
					{ input: "#txtStockScan", message: multiLang.fieldRequired, action: "change",
						rule: function (input, commit) {
							return !!input.val();
						}
					},
					{ input: "#txtStockCheck", message: multiLang.fieldRequired, action: "change",
						rule: function (input, commit) {
							return !!input.val();
						}
					}
				],
				position: "top",
				scroll : false
			});
		};
		var updateGrid = function () {
			var partyInput = $("#txtStockInput").jqxDropDownList("val");
			var partyCount = $("#txtStockCount").jqxDropDownList("val");
			var partyScan = $("#txtStockScan").jqxDropDownList("val");
			var partyCheck = $("#txtStockCheck").jqxDropDownList("val");
			if (partyInput && partyCount && partyScan && partyCheck) {
				var adapter = mainGrid.jqxGrid("source");
				if (adapter) {
					adapter.url = "jqxGeneralServicer?sname=JQGetListStockEventItemTempData&eventId=${stockEvent.eventId}&partyInput="
						+ partyInput + "&partyCount=" + partyCount + "&partyScan=" + partyScan + "&partyCheck=" + partyCheck;
					adapter._source.url = adapter.url;
					mainGrid.jqxGrid("source", adapter);
				}
			}
		};
		return {
			init: function () {
				mainGrid = $("#jqxgridStocking");
				initElements();
				handleEvents();
				initValidator();
			}
		};
	})();
</script>
</div>