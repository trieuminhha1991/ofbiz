<#if orderId?exists>
<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.DAAddProductToOrder}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<input type="hidden" name="orderId" id="orderId_w" value="${orderId}"/>
			<#if !catalogCol?has_content>
				<input type="hidden" name="prodCatalogId" id="wn_prodCatalogId" value=""/>
			</#if>
			<#if catalogCol?has_content && catalogCol?size == 1>
				<input type="hidden" name="prodCatalogId" id="wn_prodCatalogId" value="${catalogCol.first}"/>
			</#if>
			<#if shipGroups?size == 1>
				<input type="hidden" name="shipGroupSeqId" id="wn_shipGroupSeqId" value="${shipGroups.first.shipGroupSeqId}"/>
			</#if>
			<div id="containerAppendItem" style="background-color: transparent; overflow: auto;"></div>
			<div id="jqxNotificationAppendItem" style="margin-bottom:5px">
				<div id="notificationContentContactMech">
				</div>
			</div>
			<div class="row-fluid" style="display:none;">
				<div class="span6 form-window-content-custom">
					<div class="row-fluid">
						<div class="span5">
							<label for="wn_itemDesiredDeliveryDate" class="required">${uiLabelMap.DADesiredDeliveryDate}</label>
						</div>
						<div class="span7">
							<div id="wn_itemDesiredDeliveryDate"></div>
						</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class="row-fluid">
						<div class="span5">
							<label for="wn_shipBeforeDate">${uiLabelMap.DAShipBeforeDate}</label>
						</div>
						<div class="span7">
							<div id="wn_shipBeforeDate"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<label for="wn_shipAfterDate">${uiLabelMap.DAShipAfterDate}</label>
						</div>
						<div class="span7">
							<div id="wn_shipAfterDate"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="wn_jqxGridProduct"></div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="alterCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<#assign configwn_jqxGridProduct = ""/>
<#assign gridProductItemsId = "wn_jqxGridProduct"/>
<#assign idExisted = "true"/>
<#assign viewSize = "10"/>
<#assign otherParamUrl = "productStoreId=${productStore?if_exists.productStoreId?if_exists}&hasrequest=Y"/>

<div class="container_loader">
	<div id="loader_page_common_popup" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib />
<script type="text/javascript">
	$(function(){
		pageCommonAddItemsPopup.init();
	});
	var pageCommonAddItemsPopup = (function(){
		var formatString = "dd/MM/yyyy HH:mm:ss";
		var dataSelected = [];
		
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			$("#alterpopupWindow").jqxWindow({
				maxWidth: 1300, width: 1200, height: 490, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: theme
			});
			$("#wn_itemDesiredDeliveryDate").jqxDateTimeInput({width: "100%", height: 25, allowNullDate: true, value: null, formatString: formatString, disabled: true});
			$("#wn_shipBeforeDate").jqxDateTimeInput({width: "98%", height: 25, allowNullDate: true, value: null, formatString: formatString, disabled: true});
			$("#wn_shipAfterDate").jqxDateTimeInput({width: "98%", height: 25, allowNullDate: true, value: null, formatString: formatString, disabled: true});
			<#if defaultItemDeliveryDate?exists>$("#wn_itemDesiredDeliveryDate").jqxDateTimeInput("setDate", "${defaultItemDeliveryDate}");</#if>
			<#if defaultShipBeforeDate?exists>$("#wn_shipBeforeDate").jqxDateTimeInput("setDate", "${defaultShipBeforeDate}");</#if>
			<#if defaultShipAfterDate?exists>$("#wn_shipAfterDate").jqxDateTimeInput("setDate", "${defaultShipAfterDate}");</#if>
			
			$("#containerAppendItem").width("100%");
			$("#jqxNotificationAppendItem").jqxNotification({ 
				icon: { width: 25, height: 25, url: "/aceadmin/assets/images/info.jpg"}, 
				width: "100%", 
				appendContainer: "#containerAppendItem", 
				opacity: 1, autoClose: true, template: "success" 
			});
		};
		var initEvent = function(){
			$("#alterSave").on("click", function(){
				bootbox.dialog("${uiLabelMap.AreYouSureUpdate}", 
				[{"label": "${uiLabelMap.CommonCancel}", 
					"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
					"callback": function() {bootbox.hideAll();}
				}, 
				{"label": "${uiLabelMap.OK}",
					"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
					"callback": function() {
						var dataMap = {
							orderId: "${orderHeader.orderId}",
						};
						
						checkValidManual();
						if (!checkValidManual) return checkValidManual;
						
						if (dataSelected.length > 0) {
							dataMap.productList = JSON.stringify(dataSelected);
							$.ajax({
								type: "POST",
								url: "appendOrderItemsSalesAdvance",
								data: dataMap,
								beforeSend: function(){
									$("#loader_page_common_popup").show();
								},
								success: function(data){
									processResultAppendItems(data);
								},
								error: function(data){
									alert("Send request is error");
								},
								complete: function(data){
									$("#loader_page_common_popup").hide();
								},
							});
						} else {
							return false;
						}
					}
				}]);
			});
			
			$("#alterpopupWindow").on("open", function(event){
				$.ajax({
					url: "getProductBySupplier",
					type: "POST",
					data: {supplier: "${partySupplier}", orderId: "${orderId}"},
					dataType: "json"
				}).done(function(data) {
					var listSupplierProduct = data.listProductBySupplier;
					var currencyUomId = data.currencyUomId;
					$("#wn_jqxGridProduct").jqxGrid("clearselection");
					loadProductDataSumToJqx(listSupplierProduct);
				});
			});
			
			$("#wn_jqxGridProduct").on("rowselect", function (event) {
				var args = event.args;
				var rowBoundIndex = args.rowindex;
				var rowData = args.row;
				var minimumOrderQuantity = rowData.minimumOrderQuantity;
				var quantity = rowData.quantity;
				var minimumOrderQuantityInt = parseInt(minimumOrderQuantity);
				if (quantity <= 0) {
					$("#wn_jqxGridProduct").jqxGrid("unselectrow", rowBoundIndex);
					$("#quantityTooltipCheck"+rowBoundIndex).jqxTooltip({ content: '<span style="color: red;">${uiLabelMap.DmsRestrictQuantityPO}!</span>', theme: "customtooltip", position: "top", name: "movieTooltip"});
					$("#quantityTooltipCheck"+rowBoundIndex).jqxTooltip("open");
				}
			});
		};
		
		var loadProductDataSumToJqx = function(valueDataSoure) {
			var cellclassname = function (row, columnfield, value) {
				return "green1";
			};
			var sourceProduct =
			{
				datafields:
				[
					{ name: "productId", type: "string" },
					{ name: "productCode", type: "string" },
					{ name: "productPlanId", type: "string" },
					{ name: "customTimePeriodId", type: "string" },
					{ name: "quantity", type: "number" },
					{ name: "quantityUomId", type: "string" },
					{ name: "lastPrice", type: "number" },
					{ name: "totalValue", type: "number" },
					{ name: "description", type: "string" },
					{ name: "productName", type: "string" },
					{ name: "minimumOrderQuantity", type: "string" }
				],
				localdata: valueDataSoure,
				datatype: "array"
			};
			var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
			$("#wn_jqxGridProduct").jqxGrid({
				source: dataAdapterProduct,
				localization: getLocalization(),
				filterable: true,
				showfilterrow: true,
				theme: theme,
				rowsheight: 30,
				width: "100%",
				height: 390,
				enabletooltips: true,
				autoheight: false,
				pageable: true,
				columnsresize: true,
				pagesize: 10,
				editable: true,
				pagesizeoptions: ["5", "10", "15", "20", "25", "30"],
				selectionmode: "checkbox",
				columns:
				[
					{ text: "${uiLabelMap.SequenceId}", sortable: false, filterable: false, editable: false, pinned: true,
						groupable: false, draggable: false, resizable: false,
						datafield: "", columntype: "number", width: 50,
						cellsrenderer: function (row, column, value) {
							return "<div style=margin:4px;>" + (value + 1) + "</div>";
						}
					},
					{ text: uiLabelMapPOProductId, datafield: "productId", width: 100, editable: false, cellclassname: cellclassname, hidden:true },
					{ text: uiLabelMapPOProductId, datafield: "productCode", width: 120, editable: false, cellclassname: cellclassname },
					{ text: "${uiLabelMap.ProductName}", datafield: "productName", minwidth: 200, editable: false, cellclassname: cellclassname },
					{ text: "${uiLabelMap.Unit}", dataField: "quantityUomId", editable: false, columntype: "dropdownlist", width: 100, cellclassname: cellclassname, filtertype: "checkedlist",
						cellsrenderer: function (row, column, value){
							for (var i = 0; i < uomData.length; i ++){
								if (uomData[i].uomId == value){
									return "<span style=\"text-align: right\">" + uomData[i].description +"</span>";
								}
							}
						}, createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(uomData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: "uomId", valueMember: "uomId", dropDownWidth: "auto", autoDropDownHeight: "auto",
								renderer: function(index, label, value){
									if (uomData.length > 0) {
										for(var i = 0; i < uomData.length; i++){
											if(uomData[i].uomId == value){
												return "<span>" + uomData[i].description + "</span>";
											}
										}
									}
									return value;
								}
							});
						}
					},
					{ text: uiLabelMapMOQ, dataField: "minimumOrderQuantity", filterable: false, editable: false , columntype: "dropdownlist", width: 80, hidden: true,
						cellsrenderer: function(row, column, value){
							if (value){
								var id = "moqTooltip" + row;
								return '<span id=\"'+id+'\" style=\"text-align: right\">' + value.toLocaleString(locale) +"</span>";
							}
						}, cellclassname: function (row, columnfield, value) {
							return "cell-green-color";
						}
					},
					{ text: uiLabelMapOrderQuantityEdit, datafield: "quantity", width: 100, filterable: false, cellsalign: "right", columntype: "numberinput",
						cellsrenderer: function(row, column, value){
							var id = "quantityTooltipCheck" + row;
							return '<span id=\"'+id+'\" style=\"text-align: right\">' + value.toLocaleString(locale) +"</span>";
						}, createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:0 });
						}, cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
							if (newvalue != oldvalue){
								var rowsdata = $("#wn_jqxGridProduct").jqxGrid("getrowdata", row);
								var total = parseFloat(newvalue) * parseFloat(rowsdata.lastPrice);
								var minimumOrderQuantity = rowsdata.minimumOrderQuantity;
								$("#wn_jqxGridProduct").jqxGrid("setcellvalue", row, "quantity", newvalue);
								if(newvalue >= minimumOrderQuantity){
									$("#wn_jqxGridProduct").jqxGrid("setcellvalue", row, "totalValue", total);
									$("#wn_jqxGridProduct").jqxGrid("selectrow", row);
								}
							}
						}, validation: function (cell, value) {
							if (value < 0) {
								return { result: false, message: "${uiLabelMap.BPOCheckGreaterThan}" };
							}
							var data = $("#wn_jqxGridProduct").jqxGrid("getrowdata", cell.row);
							if (value < data.minimumOrderQuantity && value > 0){
								return { result: false, message: "${uiLabelMap.BPORestrictMOQ}." + " MOQ = " + data.minimumOrderQuantity };
							}
							return true;
						}
					},
					{ text: uiLabelMapunitPrice, datafield: "lastPrice", width: 100, editable: false,filterable: false, cellsalign: "right",  align: "left",
						cellsrenderer: function(row, column, value){
							if (value){
								return "<span style=\"text-align: right\">" + value.toLocaleString(locale) +"</span>";
							}
						}, cellclassname: cellclassname
					},
					{ text: uiLabelMapPOTotal, datafield: "totalValue", width: 150, editable: false, filterable: false, cellsalign: "right",  align: "left",
						cellsrenderer: function(row, column, value){
							var rowsdata = $("#wn_jqxGridProduct").jqxGrid("getrowdata", row);
							value = parseFloat(rowsdata.lastPrice)*parseFloat(rowsdata.quantity);
							return "<span style=\"text-align: right\">" + value.toLocaleString(locale) +"</span>";
						}, cellclassname: cellclassname
					}]
				});
		}

		var checkValidManual = function(){
			var rowindexes = $("#wn_jqxGridProduct").jqxGrid("getselectedrowindexes");
			var orderItems=[];
			for(var i=0; i<rowindexes.length; i++){
				var row = $("#wn_jqxGridProduct").jqxGrid("getrowdata", rowindexes[i]);
				delete row.productName;
				if(row.quantity >= row.minimumOrderQuantity){
					orderItems.push(row);
				}
			}
			var prodCatalogId = "" + $("#wn_prodCatalogId").val();
			var shipGroupSeqId = "" + $("#wn_shipGroupSeqId").val();
			var itemDesiredDeliveryDate = "";
			var shipBeforeDate = $("#wn_shipBeforeDate").jqxDateTimeInput("getDate") != null ? $("#wn_shipBeforeDate").jqxDateTimeInput("getDate").getTime() : "";
			var shipAfterDate = $("#wn_shipAfterDate").jqxDateTimeInput("getDate") != null ? $("#wn_shipAfterDate").jqxDateTimeInput("getDate").getTime() : "";
			dataSelected = [];
			if (orderItems.length == 0) {
				var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAYouNotYetChooseProduct}!</span>";
				bootbox.dialog(messageError, [{
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
				}]);
			} else {
				for(var i=0; i < orderItems.length; i++){
					var prodItem = {
						prodCatalogId: prodCatalogId,
						shipGroupSeqId: shipGroupSeqId,
						productId: orderItems[i].productId,
						quantityUomId: orderItems[i].quantityUomId,
						quantity: orderItems[i].quantity,
						itemDesiredDeliveryDate: itemDesiredDeliveryDate,
						shipBeforeDate: shipBeforeDate,
						shipAfterDate: shipAfterDate,
						amount: "",
						overridePrice: "",
						reasonEnumId: "",
						orderItemTypeId: "",
						changeComments: "",
						itemAttributesMap: "",
						calcTax: "",
						quantityUomId: "",
						expireDate: "",
					};
					dataSelected.push(prodItem);
				}
			}
			if (dataSelected.length > 0) {
				return true;
			} else {
				return false;
			}
			return true;
		};
		var processResultAppendItems = function(data){
			if (data.thisRequestUri == "json") {
				var errorMessage = "";
				if (data._ERROR_MESSAGE_LIST_ != null) {
					for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
						errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
					}
				}
				if (data._ERROR_MESSAGE_ != null) {
					errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
				}
				if (errorMessage != "") {
					$("#containerAppendItem").empty();
					$("#jqxNotificationAppendItem").jqxNotification({ template: "error"});
					$("#jqxNotificationAppendItem").html(errorMessage);
					$("#jqxNotificationAppendItem").jqxNotification("open");
					return false;
				} else {
					var orderId = data.orderId;
					if (OlbCore.isNotEmpty(orderId)) {
						window.location.reload();
					}
					return true;
				}
			} else {
				return true;
			}
		};
		var DmsRestrictMOQ = "${uiLabelMap.DmsRestrictMOQ}";
		var wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
		var wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
		var bootBoxCreate = "${uiLabelMap.POAreYouSureCreate}";
		var checkValidateIsEmpty = "${uiLabelMap.POCheckIsEmptyCreateLocationFacility}";
		var selectValidate = "${uiLabelMap.PODestinationSelectNotifi}";

		//grid
		var noGrid = "${uiLabelMap.BPOSequenceId}";
		var DmscontactMechId = "${uiLabelMap.DmscontactMechId}";
		var POtoName = "${uiLabelMap.POtoName}";
		var attnName =  "${uiLabelMap.attnName}";
		var address1 = "${uiLabelMap.address1}";
		var city = "${uiLabelMap.city}";
		var uiLabelMapDmsProduct = "${uiLabelMap.DmsProduct}";
		var uiLabelMapPOProductId = "${uiLabelMap.POProductId}";
		var uiLabelMapUnitsProduct = "${uiLabelMap.UnitsProduct}";
		var uiLabelMapMOQ = "${uiLabelMap.MOQ}";
		var uiLabelMapOrderQuantityEdit = "${uiLabelMap.OrderQuantityEdit}";
		var uiLabelMapunitPrice = "${uiLabelMap.unitPrice}";
		var uiLabelMapPOTotal = "${uiLabelMap.BPOTotal}";
		var uiLabelMapDMSCtmNull = "${uiLabelMap.DMSCtmNull}";
		var uiLabelMapDAAreYouSureYouWantCreate = "${uiLabelMap.DAAreYouSureYouWantCreate}";
		var checkValidateAndCurrent = "${uiLabelMap.checkValidateAndCurrent}";

		return {
			init: init,
		};
	}());
</script>
</#if>