<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/hrresources/js/shim.js" type="text/javascript"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownList=true/>
<script type="text/javascript">
	var cellClassCommon = function (row, dataField, cellText, rowData) {
 		if (OlbCore.isNotEmpty(rowData["totalEmployee"])) {
 			return "background-cancel-ze";
 		}
 		if ((rowData["statusId"]) == "PARTY_DISABLED") {
 			return "background-cancel";
 		}
    }
    var cellbeginedit = function (row, datafield, columntype, value) {
    	let rowData = $("#jqxSalesStatement").jqxGrid('getrowdata', row);
        if (rowData["statusId"] == "PARTY_DISABLED") return false;
    }

    var productListMap = {};
</script>
<style type="text/css">
	//#jqxSalesStatement .jqx-grid-header.jqx-grid-header-olbius {
		//height: 45px !important;
	    //white-space:normal !important;
	//}
	#jqxCatalogCategoryGrid .chkbox.jqx-widget.jqx-checkbox{
		margin-top:6px !important;
	}
	.jqx-widget-olbius .jqx-grid-column-header-olbius, .jqx-widget-olbius .jqx-grid-cell-olbius, .jqx-widget-olbius .jqx-grid-group-cell-olbius, .jqx-grid-cell-olbius {
		border-width: 1px;
	}
</style>
<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>
<div class="row-fluid">
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-header">
                <h4>${salesStatementType?if_exists.description?if_exists} (${salesStatement.salesStatementId?if_exists})
	            	<#if salesStatement.customTimePeriodId?exists>
	            		<#assign customTimeParent = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : salesStatement.customTimePeriodId}, false)!/>
                		${customTimeParent.periodName?if_exists}
                	</#if>
                	&nbsp;&nbsp;&nbsp;
                	<button class="btn btn-mini btn-primary" onClick="javascript:OlbCRUDSalesStatement.createOrUpdateSalesStatementDetail()"><i class="icon-save"></i>${uiLabelMap.CommonSave}</button>
                	<button class="btn btn-mini btn-primary" onClick="javascript:window.location.href='newSalesStatementDetail?salesStatementId=${salesStatement.salesStatementId?if_exists}';"><i class="fa-refresh open-sans"></i>&nbsp;${uiLabelMap.CommonReset}</button>
                    <button class="btn btn-mini btn-primary" onClick="javascript:OlbCRUDSalesStatement.exportExcel();"><i class="fa-download open-sans"></i>&nbsp;${uiLabelMap.BSDownloadFileTemplateSalesStatement}</button>
                    <button class="btn btn-mini btn-primary" onClick="javascript:OlbCRUDSalesStatement.uploadFileSalesStatementExcel();"><i class="fa-upload open-sans"></i>&nbsp;${uiLabelMap.BSUploadFileSalesStatementEdited}</button>
                    <button class="btn btn-mini btn-danger" onClick="javascript:window.location.href='viewSalesStatementDetail?salesStatementId=${salesStatement.salesStatementId?if_exists}';"><i class="fa-remove open-sans"></i>&nbsp;${uiLabelMap.BSPrev}</button>

                </h4>
                <div class="widget-toolbar no-border">
                    <div id='jqxCatalogCategoryId' style='display:inline-block; vertical-align: middle;'></div>
                    <button type="button" id="openFilterCatalogCategory" class="btn btn-mini btn-primary"  style='display:inline-block'><i class="fa fa-bars"></i></button>
                </div>
            </div>
            <div class="widget-body">
                <div class="widget-main padding-12 no-padding-left no-padding-right">
			        <#include "salesStatementDetailNewContentGrid.ftl"/>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="alterpopupWindowFilter" style="display:none">
	<div>${uiLabelMap.BSFilterOption}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxCatalogCategoryGrid"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wf_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wf_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbCRUDSalesStatement.init();
	});
	var OlbCRUDSalesStatement = (function(){
		var localDataCatalogCategory = [];
		var productCatalogCategoryDDL;
		var catalogIds;
		var categoryIds;
		var currentListProductIds;
		var currentListProducts;

		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initData();
		};
		var initData = function(){
			// get local storage
			var localStorageSaveJson = localStorage.getItem('statement_new_clog_cate');
			if (OlbCore.isNotEmpty(localStorageSaveJson)) {
				var localStorageSave = JSON.parse(localStorageSaveJson);
				var localCatalogIds = localStorageSave.catalogIds;
				var localCategoryIds = localStorageSave.categoryIds;
				
				if (OlbCore.isNotEmpty(localCatalogIds)) {
					$.each(localCatalogIds, function(key, item){
						$("#jqxCatalogCategoryGrid").jqxTree('checkItem', 'CLOG_' + item, true);
					});
				}
				if (OlbCore.isNotEmpty(localCategoryIds)) {
					$.each(localCategoryIds, function(key, item){
						$("#jqxCatalogCategoryGrid").jqxTree('checkItem', $("#CATE_" + item)[0], true);
					});
				}
		        updateDataGridCategory();
			} else {
				updateGridStatementSource();
			}
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowFilter"), {width: 820, height: 460, cancelButton: $("#wf_alterCancel")});
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initElementComplex = function(){
			var configCatalogCategoryList = {
				datatype: 'array',
				width: '220px',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSNoItem)}",
				useUrl: false,
				key: 'id',
				value: 'label',
				autoDropDownHeight: false,
				indexSelected: 0
			}
			productCatalogCategoryDDL = new OlbDropDownList($("#jqxCatalogCategoryId"), productCatalogCategoryDDL, configCatalogCategoryList, []);
			
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'id'},
                    { name: 'parentId'},
                    { name: 'dataType'},
                    { name: 'label'},
                    { name: 'value'},
                    { name: 'expanded'}
                ],
                root: "results",
                id: 'id',
                url: 'listCatalogAndCategory',
                async: false
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            dataAdapter.dataBind();
            //var records = dataAdapter.getGroupedRecords(['Country', 'City'], 'items', 'label', [{ name: 'uid', map: 'value' }, { name: 'CompanyName', map: 'label'}], 'row', 'value');
            var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'value', map: 'value'}, { name: 'label', map: 'label'}]);
            $('#jqxCatalogCategoryGrid').jqxTree({
            	theme: 'energyblue',
            	source: records, 
            	height: '360px', 
            	width: '770px',
            	hasThreeStates: true,
            	checkboxes: true,
            });
		};
		
		var initEvent = function() {
			$('#wf_alterSave').click(function () {
		       	updateAllDataGrid();
		   	});
		   	$('#openFilterCatalogCategory').click(function () {
				$("#alterpopupWindowFilter").jqxWindow("open");
		   	});
		   	
		   	productCatalogCategoryDDL.selectListener(function(itemData, index){
		   		if (index != undefined) {
		   			if (index > 0) {
		   				var originalItem = itemData.originalItem;
		   				if ("CATE" == originalItem.type) updateJqxSalesStatement([], [originalItem.id]);
		   				else if ("CLOG" == originalItem.type) updateJqxSalesStatement([originalItem.id], []);
		   			} else {
		   				updateJqxSalesStatement(catalogIds, categoryIds);
		   			}
		   		}
		   	});
		};
		
		var updateDataGridCategory = function(){
			var str = "";
	       	var items = $('#jqxCatalogCategoryGrid').jqxTree('getCheckedItems');
	       	catalogIds = new Array();
	       	categoryIds = new Array();
	       	localDataCatalogCategory = [];
	       	localDataCatalogCategory[0] = {"id": "_NA_", "label": "${uiLabelMap.BSNoItem}", "type": "_NA_"};
	       	for (var i = 0; i < items.length; i++) {
	           	var item = items[i];
	           	var idArr = item.id.split("_");
	           	if (idArr != undefined) {
	           		if ("CLOG" == idArr[0]) {
		           		catalogIds.push(item.value);
		           		localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CLOG'});
		           	} else if ("CATE" == idArr[0]) {
		           		categoryIds.push(item.value);
		           		localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CATE'});
		           	}
	           	}
	       	}
	       	updateJqxDDLCatalogCategory(catalogIds, categoryIds, localDataCatalogCategory);
		};
		
		var updateAllDataGrid = function(){
			var str = "";
	       	var items = $('#jqxCatalogCategoryGrid').jqxTree('getCheckedItems');
	       	catalogIds = new Array();
	       	categoryIds = new Array();
	       	localDataCatalogCategory = [];
	       	localDataCatalogCategory[0] = {"id": "_NA_", "label": "${uiLabelMap.BSNoItem}", "type": "_NA_"};
	       	for (var i = 0; i < items.length; i++) {
	           	var item = items[i];
	           	var idArr = item.id.split("_");
	           	if (idArr != undefined) {
	           		if ("CLOG" == idArr[0]) {
		           		catalogIds.push(item.value);
		           		localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CLOG'});
		           	} else if ("CATE" == idArr[0]) {
		           		categoryIds.push(item.value);
		           		localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CATE'});
		           	}
	           	}
	       	}
	       	updateJqxDDLCatalogCategory(catalogIds, categoryIds, localDataCatalogCategory);
	       	updateJqxSalesStatement(catalogIds, categoryIds);
		};
		
		var updateJqxDDLCatalogCategory = function(catalogIds, categoryIds, localData){
			if (!OlbCore.isNotEmpty(catalogIds)) catalogIds = [];
			if (!OlbCore.isNotEmpty(categoryIds)) categoryIds = [];
			var catalogSize = catalogIds.length;
			var categorySize = categoryIds.length;
			var labelRoot = "";
			if (catalogSize > 0) labelRoot += catalogSize + " ${uiLabelMap.BSCatalog}, ";
			if (categorySize > 0) labelRoot += categorySize + " ${uiLabelMap.BSCategory}";
			localData[0] = {"id": "_NA_", "label": labelRoot, "type": "_NA_"};
			productCatalogCategoryDDL.updateSource(null, localData, function(){
				productCatalogCategoryDDL.selectItem(null, 0);
			});
			localStorage.setItem('statement_new_clog_cate', JSON.stringify({"catalogIds": catalogIds, "categoryIds": categoryIds}));
		};
		
		var updateJqxSalesStatement = function(catalogIds, categoryIds){
			$.ajax({
				type: 'POST',
				url: 'getListProductIdAdvanceByCatalogOrCategory', // url: 'getListProductIdByCatalogOrCategory',
				data: {
					catalogIds: JSON.stringify(catalogIds),
					categoryIds: JSON.stringify(categoryIds)
				},
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
								$("#btnPrevWizard").removeClass("disabled");
								$("#btnNextWizard").removeClass("disabled");
								
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
					        	if (data.listProductIds != undefined && data.listProductIds != null && data.listProductIds.length > 0) {
                                    currentListProducts = data.listProductIds;
                                    updateJqxSalesStatementAll(data.listProductIds);
					        	} else {
					        		updateGridStatementSource();
					        	}
					        	$("#alterpopupWindowFilter").jqxWindow("close");
							}
					);
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		};
		
		var updateJqxSalesStatementAll = function(productIdsListMap){
			var columnListEdit = new Array();
			var datafieldListEdit = new Array();
			
			datafieldListEdit.push({ name: 'partyId', type: 'string' });
			datafieldListEdit.push({ name: 'partyCode', type: 'string' });
			datafieldListEdit.push({ name: 'partyIdFrom', type: 'string' });
			datafieldListEdit.push({ name: 'partyName', type: 'string' });
			datafieldListEdit.push({ name: 'totalEmployee', type: 'string'});
			datafieldListEdit.push({ name: 'statusId', type: 'string'});
			
			columnListEdit.push({text: '${uiLabelMap.OrgUnitName}', datafield: 'partyIdFrom', width: '12%', pinned: true, editable: false,  cellClassName: cellClassCommon});
			columnListEdit.push({text: '${uiLabelMapPartyId}', datafield: 'partyCode', width: '12%', pinned: true, editable: false,  cellClassName: cellClassCommon});
			columnListEdit.push({text: '${uiLabelMap.BSFullName}', datafield: 'partyName', width: '14%', pinned: true, editable: false,  cellClassName: cellClassCommon});
			
			var productIds = new Array();
			var productItem;
			var productId;
			var productCode;
			var quantityUomIdDesc;
			var columnLabel;
			for (var i = 0; i < productIdsListMap.length; i++) {
				productId = null;
				productCode = null;
				quantityUomId = null;
				productItem = productIdsListMap[i];
				productId = productItem.productId;
				productCode = productItem.productCode;
				quantityUomIdDesc = productItem.quantityUomIdDesc;
				columnLabel = "" + productCode + " (" + quantityUomIdDesc + ")";
				
				if (productId != null) {
					productIds.push(productId);
					datafieldListEdit.push({ name: 'prodCode_' + productId, type: 'number', formatter: 'integer'});
					columnListEdit.push({text: columnLabel, dataField: 'prodCode_' + productId, width: '120px', cellsalign: 'right', cellClassName: cellClassCommon, editable: true, cellbeginedit: cellbeginedit});
				}
			}
            currentListProductIds = productIds;
			var productIdsStr = JSON.stringify(productIds);
			$("#jqxSalesStatement").jqxGrid('columns', columnListEdit);
			
			updateGridStatementSource(datafieldListEdit, productIdsStr);
		};
		
		var updateGridStatementSource = function(datafieldListEdit, productIdsStr){
			var tmpSource = jQuery("#jqxSalesStatement").jqxGrid('source');
			if (tmpSource) {
				if (datafieldListEdit != null && datafieldListEdit != undefined) tmpSource._source.datafields = datafieldListEdit;
				var urlNew = "jqxGeneralServicer?sname=JQListOrganizationUnitManager&pagesize=0&salesStatementId=${parameters.salesStatementId?if_exists}";
				if (productIdsStr != null && productIdsStr != undefined) urlNew += "&productIds=" + productIdsStr;
				tmpSource._source.url = urlNew;
				$("#jqxSalesStatement").jqxGrid('source', tmpSource);
				$("#jqxSalesStatement").jqxGrid('updatebounddata');
				//$("#jqxSalesStatement").jqxGrid('expandAll');
			}
		};
		
		var joinArray = function(arrDest, arrTemp){
			$.each(arrTemp, function(key, value){
				if (OlbCore.isNotEmpty(value)) {
					arrDest.push(value);
				}
			});
		};
		var processRowData = function(rowsData) {
			var dataList = [];
			for (var i = 0; i < rowsData.length; i++) {
				var rowData = rowsData[i];
				if (rowData != window) {
					if (rowData.totalEmployee == undefined || rowData.totalEmployee == null) {
						var partyId = rowData.partyId;
						$.each(rowData, function(key, value){
							if (OlbCore.isNotEmpty(value)) {
								if (key.indexOf("_") > -1) {
									var itemArr = key.split("_");
									if ("prodCode" == itemArr[0]) {
										var productId = itemArr[1];
										if (OlbCore.isNotEmpty(productId)) {
											var itemMap = {};
											itemMap["partyId"] = partyId;
											itemMap["productId"] = productId;
											itemMap["quantity"] = value;
											dataList.push(itemMap);
										}
									}
								}
							}
						});
					}
					if (rowData.records != undefined || rowData.records != null) {
						joinArray(dataList, processRowData(rowData.records));
					}
				}
			}
			return dataList;
		};
		var processRowData2 = function(data) {
			var dataList = [];
			$.each(data, function (key, value){
				if (typeof(value) != 'undefined') {
					var prodItem = {
						partyId: value.partyId,
						productId: typeof(value.productId) != 'undefined' ? value.productId : '',
						quantity: typeof(value.quantity) != 'undefined' ? value.quantity : 0,
					};
					dataList.push(prodItem);
				}
			});
			return dataList;
		};
		var createOrUpdateSalesStatementDetail = function(){
			var jqxGridId = "jqxSalesStatement";
			//var dataRows = $("#" + jqxGridId).jqxGrid("getRows");
			//var dataList = [];
			//if (typeof(dataRows) != 'undefined') {
				//dataList = processRowData(dataRows);
			//}
			var dataList = processRowData2(productListMap);
			if (dataList.length > 0) {
				$.ajax({
					type: 'POST',
					url: 'createUpdateSalesStatementAdvanceJson',
					datatype: 'json',
					data: {
						salesStatementId: "${salesStatement.salesStatementId}",
						productList : JSON.stringify(dataList),
					},
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", function(){
											$('#container').empty();
								        	$('#jqxNotification').jqxNotification({ template: 'info'});
								        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
								        	$("#jqxNotification").jqxNotification("open");
							        		
							        		window.location.href = 'viewSalesStatementDetail?salesStatementId=${salesStatement.salesStatementId}';
										}
								);
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			} else {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}!");
				return false;
			}
		};
		var getListProducts = function () {
            if (OlbCore.isNotEmpty(currentListProducts)) return currentListProducts;
            return [];
        };
		var getSalesStatementId = function () {
            return "${salesStatement.salesStatementId}";
        }

		var exportExcel = function () {
            if(OlbCore.isEmpty(currentListProductIds)){
                OlbCore.alert.error("${uiLabelMap.BSNoDataToExport}");
                return false;
            }
            var salesStatementId = ${parameters.salesStatementId};
            var form = document.createElement("form");
            form.setAttribute("method", "POST");
            form.setAttribute("action", "exportSalesStatementDetailExcel");

            var hiddenField0 = document.createElement("input");
            hiddenField0.setAttribute("type", "hidden");
            hiddenField0.setAttribute("name", "salesStatementId");
            hiddenField0.setAttribute("value", salesStatementId);
            form.appendChild(hiddenField0);

            var hiddenField1 = document.createElement("input");
            hiddenField1.setAttribute("type", "hidden");
            hiddenField1.setAttribute("name", "productIds");
            hiddenField1.setAttribute("value", JSON.stringify(currentListProductIds));
            form.appendChild(hiddenField1);

            document.body.appendChild(form);
            form.submit();
        };
		var uploadFileSalesStatementExcel = function(){
            addSalesStatementSheetDetailObj.openWindow();
        };
		return {
			init: init,
			createOrUpdateSalesStatementDetail: createOrUpdateSalesStatementDetail,
            exportExcel:exportExcel,
            uploadFileSalesStatementExcel: uploadFileSalesStatementExcel,
            getListProducts: getListProducts,
            getSalesStatementId: getSalesStatementId,
		};
	}());
uiLabelMap.OrgUnitName = "${StringUtil.wrapString(uiLabelMap.OrgUnitName)}";
uiLabelMapPartyId = "${uiLabelMapPartyId}";
uiLabelMap.CommonChooseFile = "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}";
uiLabelMap.ColumnDataInSystem = "${StringUtil.wrapString(uiLabelMap.ColumnDataInSystem)}";
uiLabelMap.ColumnDataInImportFile = "${StringUtil.wrapString(uiLabelMap.ColumnDataInImportFile)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
uiLabelMap.ConfirmCreateFileSalesStatementSheetDetail = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateFileSalesStatementSheetDetail)}";
uiLabelMap.ColumnMapAuto = "${StringUtil.wrapString(uiLabelMap.ColumnMapAuto)}";
uiLabelMap.BSCommonReset = "${StringUtil.wrapString(uiLabelMap.BSCommonReset)}";
uiLabelMap.JoinColumnDataExcel = "${StringUtil.wrapString(uiLabelMap.JoinColumnDataExcel)}";
</script>
<#include "UploadFileSalesStatementEditedPopup.ftl"/>
