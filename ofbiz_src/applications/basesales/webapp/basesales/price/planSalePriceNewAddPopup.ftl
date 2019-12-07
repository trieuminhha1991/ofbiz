<div id="popupPlansSalePriceAdd" style="display:none;" data-add=true data-update=false data-jobId=null>
	<div id="popupPlansSalePriceAdd-title">${uiLabelMap.CommonAdd}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div class="row-fluid">
						<div class="span6 form-window-content-custom">
							<div class="row-fluid">
								<div class="span5"><label class="text-right">${uiLabelMap.BSProduct}</label></div>
								<div class="span7">
									<div id="jqxProductSearch"></div>
								</div>
							</div>
						</div>
						<div class="span6 form-window-content-custom">
							<div class="row-fluid margin-top10">
								<div class="span3"><label class="text-right asterisk">${uiLabelMap.SGCExecutionDate}</label></div>
								<div class="span9"><div id="wn_planItems_executionDate" tabindex="5"></div></div>
							</div>
							
							<div class="row-fluid margin-top10">
								<div class="span3"><label class="text-right asterisk">${uiLabelMap.BSNote}</label></div>
								<div class="span9"><input type="text" id="wn_planItems_note" tabindex="6" /></div>
							</div>
						</div>
					</div>
					
					<div class="row-fluid">
						<div class="span12">
							<div id="jqxgridProductPrices"></div>
						</div>
					</div>
				</div>
			</div><!-- .row-fluid -->
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="wn_planItems_alterCancel" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'> </i> ${uiLabelMap.CommonCancel}
						</button>
						<button type="button" id="wn_planItems_alterSave" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-check'> </i> ${uiLabelMap.CommonSave}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<#include "planSalePriceNewItemsProdSearch.ftl">

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.search.remote.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasCore=false hasGrid=true/>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BPOSSearchProduct = " ${StringUtil.wrapString(uiLabelMap.BPOSSearchProduct)} (F1)";
	uiLabelMap.BSProductProductNotFound = "${StringUtil.wrapString(uiLabelMap.BSProductProductNotFound)}";
	uiLabelMap.BSCurrentPrice = "${StringUtil.wrapString(uiLabelMap.BSCurrentPrice)}";
	uiLabelMap.BSNewPrice = "${StringUtil.wrapString(uiLabelMap.BSNewPrice)}";
	uiLabelMap.BSDefaultPrice = "${StringUtil.wrapString(uiLabelMap.BSDefaultPrice)}";
	uiLabelMap.BSListPrice = "${StringUtil.wrapString(uiLabelMap.BSListPrice)}";
	uiLabelMap.BSListProduct = "${StringUtil.wrapString(uiLabelMap.BSListProduct)}";
	uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}!";
	
	<#assign currencyUomId = Static['com.olbius.basesales.util.SalesUtil'].getCurrentCurrencyUom(delegator)!/>
	var defaultCurrencyUomId = <#if currencyUomId?exists>"${currencyUomId}"<#else>null</#if>;
</script>
<script type="text/javascript">
	if (typeof (OlbAddProductItems) == "undefined") {
		var OlbAddProductItems = (function() {
			var jqxwindow;
			var productGridJQ = $("#jqxgridProductPrices");
			var productSearchCBBS;
			var validatorVAL;
			var itemProductTodo;
			var productPricesMap = {};
			
			var init = function(){
				jqxwindow = $("#popupPlansSalePriceAdd");
				initElement();
				initComplexElement();
				handleEvents();
				initValidator();
			};
			
			var initElement = function() {
				jOlbUtil.windowPopup.create($("#popupPlansSalePriceAdd"), {maxWidth: 1500, width: 1080, height: 470, cancelButton: $("#wn_planItems_alterCancel"), keyboardCloseKey: ''});
				jOlbUtil.input.create("#wn_planItems_note", {width: '74%'});
				jOlbUtil.dateTimeInput.create("#wn_planItems_executionDate", {width: '75%', allowNullDate: true});
				
				$("#wn_planItems_executionDate").val(null);
			};
			
			var initComplexElement = function(){
				var datafield = [
					{ name: "productId", type: "string" },
					{ name: "productCode", type: "string" },
					{ name: "productName", type: "string" },
					{ name: "currentDefaultPrice", type: "number" },
					{ name: "defaultPrice", type: "number" },
					{ name: "currentListPrice", type: "number" },
					{ name: "listPrice", type: "number" },
					{ name: "quantityUomId", type: "string" },
					{ name: "currencyUomId", type: "string" },
					{ name: "isPriceIncludedVat", type: "string" }
				];
				var columnlist = [
					{ text: multiLang.DmsSequenceId, width: 50, sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
						cellsrenderer: function (row, column, value) {
							return "<div style=margin:4px;>" + (row + 1) + "</div>";
						}
					},
					{ text: multiLang.BSProductId, datafield: "productCode", width: 120, editable: false },
					{ text: multiLang.ProductProductName, datafield: "productName", minwidth: 100, editable: false },
					{ text: multiLang.DmsQuantityUomId, datafield: "quantityUomId", filtertype: "checkedlist", width: 120, editable: false,
						cellsrenderer: function(row, colum, value) {
							value?value=mapQuantityUom[value]:value;
							return "<span>" + value + "</span>";
						},
						createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: listQuantityUom, displayMember: "description", valueMember: "uomId" });
						}
					},
					{ text: multiLang.BSCurrencyUomId, dataField: "currencyUomId", width: 80, editable: false},
					{ text: "<span style='margin-top:-2.5px; display:block'>" + uiLabelMap.BSDefaultPrice + "<br/>(" + uiLabelMap.BSCurrentPrice + ")</span>", datafield: "currentDefaultPrice", width: 120, columntype: "numberinput", filtertype: "number", editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text: "<span style='margin-top:-2.5px; display:block'>" + uiLabelMap.BSDefaultPrice + "<br/>(" + uiLabelMap.BSNewPrice + ")</span>", datafield: "defaultPrice", width: 120, columntype: "numberinput", filtertype: "number", cellClassName: cellClassName,
						cellsrenderer: function(row, column, value, a, b, data) {
							return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({ theme: theme, inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:0, decimalDigits: 2 });
						},
						validation: function (cell, value) {
							if (value > 0) {
								return true;
							}
							return { result: false, message: multiLang.DmsPriceNotValid };
						}
					},
					{ text: "<span style='margin-top:-2.5px; display:block'>" + uiLabelMap.BSListPrice + "<br/>(" + uiLabelMap.BSCurrentPrice + ")</span>", datafield: "currentListPrice", width: 120, columntype: "numberinput", filtertype: "number", editable: false,
						cellsrenderer: function(row, column, value, a, b, data) {
							return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
						}
					},
					{ text: "<span style='margin-top:-2.5px; display:block'>" + uiLabelMap.BSListPrice + "<br/>(" + uiLabelMap.BSNewPrice + ")</span>", datafield: "listPrice", width: 120, columntype: "numberinput", filtertype: "number", cellClassName: cellClassName,
						cellsrenderer: function(row, column, value, a, b, data) {
							return "<div class=\"text-right\">" + value.toLocaleString(locale) + "</div>";
						},
						createeditor: function (row, cellvalue, editor) {
							editor.jqxNumberInput({ theme: theme, inputMode: "simple", spinMode: "simple", groupSeparator: ".", min:0, decimalDigits: 2 });
						},
						validation: function (cell, value) {
							if (value > 0) {
								return true;
							}
							return { result: false, message: multiLang.DmsPriceNotValid };
						}
					},
				];
				
				<#assign customcontrol1 = "icon-minus open-sans@${uiLabelMap.BSDelete}@javascript: void(0);@OlbAddProductItems.removeItemFromGrid()">
				var customcontrol1 = "${customcontrol1}";
				var configGridProduct = {
					datafields: datafield,
					columns: columnlist,
					width: '100%',
					height: 'auto',
					sortable: true,
					filterable: true,
					editable: true,
					editmode: 'click',
					pageable: true,
					pagesize: 15,
					columnsheight: 45,
					showfilterrow: true,
					root: 'rows',
					useUtilFunc: false,
					useUrl: true,
					url: '',
					showdefaultloadelement:true,
					autoshowloadelement:true,
					//selectionmode:'multiplerows',
					virtualmode: false,
					showtoolbar: true,
					rendertoolbarconfig: {
						titleProperty: uiLabelMap.BSListProduct,
						customcontrol1: customcontrol1,
						//customcontrol2: customcontrol2
					},
				};
				productGRID = new OlbGrid(productGridJQ, null, configGridProduct, []);
			}
			
			var handleEvents = function() {
				$("#wn_planItems_alterSave").click(function() {
					if (!validatorVAL.validate()) return false;
					
					var dataRows = productGRID.getAllRowData();
					var listProd = new Array();
					if (dataRows) {
						for (var i = 0; i < dataRows.length; i++) {
							var dataItem = dataRows[i];
							dataItem = _.omit(dataItem, 'uid', '');
							listProd.push(dataItem);
						}
					}
					if (listProd.length > 0) {
						var dataMap = {};
						dataMap.jobId = jqxwindow.data("jobId");
						dataMap.jobName = $("#wn_planItems_note").val();
						dataMap.runTime = $("#wn_planItems_executionDate").jqxDateTimeInput("getDate").getTime();
						dataMap.plansPrice = JSON.stringify(listProd);
						
						$("#wn_planItems_alterSave").addClass("disabled");
						$("#wn_planItems_alterCancel").addClass("disabled");
						$.ajax({
							type: 'POST',
							url: jqxwindow.data("add") ? "transferPlansSalePriceToJobSandbox" : "reTransferPlansSalePriceToJobSandbox",
							data: dataMap,
							beforeSend: function(){
								$("#loader_page_common").show();
							},
							success: function(data){
								jOlbUtil.processResultDataAjax(data, "default", function(){
									$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(uiLabelMap.wgcreatesuccess);
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	//close window
						        	closeWindowAdd();
						        	
						        	$("#jqxgridPlansSalePrice").jqxGrid("updatebounddata");
								});
							},
							error: function(data){
								alert("Send request is error");
							},
							complete: function(data){
								$("#wn_planItems_alterSave").removeClass("disabled");
								$("#wn_planItems_alterCancel").removeClass("disabled");
								$("#loader_page_common").hide();
							},
						});
					} else {
						jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
						return false;
					}
				});
				
				jqxwindow.on("open", function() {
					if (jqxwindow.data("add")) {
						$("#popupPlansSalePriceAdd").jqxWindow("title", multiLang.CommonAdd);
						$("#wn_planItems_alterSave").show();
					} else if (jqxwindow.data("update")) {
						$("#popupPlansSalePriceAdd").jqxWindow("title", multiLang.CommonUpdate + " [ID: " + jqxwindow.data("jobId") + "]");
						$("#wn_planItems_alterSave").show();
					} else {
						$("#popupPlansSalePriceAdd").jqxWindow("title", multiLang.BSListProduct + " [ID: " + jqxwindow.data("jobId") + "]");
						$("#wn_planItems_alterSave").hide();
					}
				});
				jqxwindow.on("close", function() {
					jqxwindow.jqxValidator("hide");
					$("#wn_planItems_note").val("");
					$("#wn_planItems_executionDate").val(null);
					
					jqxwindow.data("update", false);
					jqxwindow.data("add", true);
					jqxwindow.data("jobId", null);
					
					productGRID.updateBoundData();
				});
			};
			
			var cellClassName = function() {
				return (jqxwindow.data("add") || jqxwindow.data("update"))?"background-prepare":"";
			};
			var setValue = function(data) {
				if (data) {
					$("#wn_planItems_note").val(data.jobName);
					$("#wn_planItems_executionDate").jqxDateTimeInput("setDate", new Date(data.runTime));
				}
			};
			var open = function(jobId, jobName, runTime, add, update) {
				if (jobId) {
					jqxwindow.data("add", add);
					jqxwindow.data("update", update);
					jqxwindow.data("jobId", jobId);
					setValue({ jobName: jobName, runTime: runTime });
					
					openWindowAdd();
					productGRID.addDynamicParams("jobId", jobId);
					productGRID.updateSource("bufferPlansSalePriceTempData");
					resetProdGrid();
					
					if (add || update) { 
						if (new Date(runTime) > new Date()) {
							productGRID.getGridObj().jqxGrid('editable', true);
						} else {
							productGRID.getGridObj().jqxGrid('editable', false);
						}
					} else {
						productGRID.getGridObj().jqxGrid('editable', false);
					}
				}
			};
			var resetProdGrid = function(){
				productGRID.setDynamicParams({});
				var tmpSource = productGRID.getGridObj().jqxGrid('source');
				if (tmpSource) tmpSource._source.url = '';
			}
			
			var addItemsToGridPopup = function(listData){
				for (var i = 0; i < listData.length; i++) {
					var data = listData[i];
					if (OlbCore.isEmpty(data.productId)) {
						continue;
					}
					// new, update item
			   		var idStr = data.productId + "@" + data.quantityUomId;
		    		if (typeof(productPricesMap[idStr]) != "undefined") {
		    			// delete row
		    			var isFound = false;
		    			var allRowTmp = $("#jqxgridProductPrices").jqxGrid("getboundrows");
		    			if (typeof(allRowTmp) != 'undefined') {
		    				for (var j = 0; j < allRowTmp.length; j++) {
		    					var itemTmp = allRowTmp[j];
		    					if (itemTmp != window && itemTmp.productId == data.productId 
		    							&& itemTmp.quantityUomId == data.quantityUomId && itemTmp.uid != null) {
		    						var rowBoundIndex = $('#jqxgridProductPrices').jqxGrid('getrowboundindexbyid', itemTmp.uid);
		    						// update
		    						$('#jqxgridProductPrices').jqxGrid('setcellvalue', rowBoundIndex, 'quantityUomId', data.quantityUomId);
		    						$('#jqxgridProductPrices').jqxGrid('setcellvalue', rowBoundIndex, 'currencyUomId', data.currencyUomId);
		    		    			$('#jqxgridProductPrices').jqxGrid('setcellvalue', rowBoundIndex, 'currentDefaultPrice', data.defaultPriceVAT);
		    			    		$('#jqxgridProductPrices').jqxGrid('setcellvalue', rowBoundIndex, 'currentListPrice', data.listPriceVAT);
		    			    		$('#jqxgridProductPrices').jqxGrid('setcellvalue', rowBoundIndex, 'defaultPrice', data.defaultPriceNew);
		    			    		$('#jqxgridProductPrices').jqxGrid('setcellvalue', rowBoundIndex, 'listPrice', data.listPriceNew);
		    			    		isFound = true;
		    						break;
		    					}
		    				}
		    			}
		    			if (!isFound) {
		    				// add row
		    				addItemToGridPopup(idStr, data);
		    			} else {
		    				// update data
		        			var itemValue = productPricesMap[idStr];
		        			itemValue.quantityUomId = data.quantityUomId;
		        			itemValue.listPrice = data.listPrice;
		        			itemValue.listPriceVAT = data.listPriceVAT;
		        			itemValue.selected = true;
		        			productPricesMap[idStr] = itemValue;
		    			}
		    		} else {
		    			addItemToGridPopup(idStr, data);
		    		}
				}
			};
			var addItemToGridPopup = function(idStr, data){
				// add row
				var currencyUomIdPr = data.currencyUomId;
				if (!currencyUomIdPr) currencyUomIdPr = defaultCurrencyUomId;
 				
				var itemValue = {};
				itemValue.productId = data.productId;
				itemValue.productCode = data.productCode;
				itemValue.quantityUomId = data.quantityUomId;
				itemValue.taxPercentage = data.taxPercentage;
				itemValue.productName = data.productName;
				itemValue.currencyUomId = currencyUomIdPr;
				itemValue.currentDefaultPrice = data.defaultPriceVAT;
				itemValue.currentListPrice = data.listPriceVAT;
				itemValue.defaultPrice = data.defaultPriceNew;
				itemValue.listPrice = data.listPriceNew;
				itemValue.isPriceIncludedVat = 'Y';
				itemValue.selected = true;
				
				$("#jqxgridProductPrices").jqxGrid('addRow', null, itemValue, "last");
				productPricesMap[idStr] = $.extend({}, itemValue);
			};
			var removeItemFromGrid = function(){
				var rowindexes = productGridJQ.jqxGrid("getselectedrowindexes");
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					jOlbUtil.alert.error(uiLabelMap.BSYouNotYetChooseProduct);
					return false;
				}
				for (var i = 0; i < rowindexes.length; i++) {
					var dataItem = productGridJQ.jqxGrid("getrowdata", rowindexes[i]);
					if (dataItem) {
						if (typeof(dataItem) != "undefined" && typeof(dataItem.productId) != "undefined") {
				    		var idStr = dataItem.productId + "@" + dataItem.quantityUomId;
				    		if (typeof(productPricesMap[idStr]) != "undefined") {
				    			var itemValue = productPricesMap[idStr];
				    			itemValue.selected = false;
				    			productPricesMap[idStr] = itemValue;
				    		}
							
							productGridJQ.jqxGrid('deleterow', dataItem.uid);
						}
					}
				}
			};
			
			function openWindowAdd(){
				$("#popupPlansSalePriceAdd").jqxWindow("open");
			}
			function closeWindowAdd(){
				$("#popupPlansSalePriceAdd").jqxWindow("close");
				validatorVAL.hide();
			}
			
			var initValidator = function() {
				var extendRules = [];
				var mapRules = [
						{input: '#wn_planItems_note', type: 'validInputNotNull'},
						{input: '#wn_planItems_executionDate', type: 'validDateTimeInputNotNull'},
						{input: '#wn_planItems_executionDate', type: 'validDateTimeCompareToday'},
		            ];
				validatorVAL = new OlbValidator($('#popupPlansSalePriceAdd'), mapRules, extendRules, {position: 'bottom', scroll: true});
			};
			
			return {
				init: init,
				setValue: setValue,
				open: open,
				addItemsToGridPopup: addItemsToGridPopup,
				removeItemFromGrid: removeItemFromGrid,
				openWindowAdd: openWindowAdd,
			};
		})();
	}
</script>
