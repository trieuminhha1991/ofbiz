<script type="text/javascript">
	var cellClassCommon = function (row, dataField, cellText, rowData) {
        var levelValue = rowData["levelTree"];
        if (OlbCore.isNotEmpty(levelValue)) {
        	if (levelValue == -1) {
	        	return "background-root0";
	        } else if (levelValue == 0) {
	        	return "background-root";
	        } else if (levelValue == 1) {
	        	return "background-one";
	        } else if (levelValue == 2) {
	        	return "background-two";
	        } else if (levelValue == 3) {
	        	return "background-three";
	        } else if (levelValue == 4) {
	        	return "background-four";
	        } else if (levelValue == 5) {
	        	return "background-five";
	        }
        }
    }
    var cellClassCommonHeader = function (row, dataField, cellText, rowData) {
        var levelValue = rowData["levelTree"];
        if (OlbCore.isNotEmpty(levelValue)) {
        	if (levelValue == -1) {
	        	return "background-root0";
	        }
        }
    }
</script>
<style type="text/css">
	#jqxCatalogCategoryGrid .chkbox.jqx-widget.jqx-checkbox{
		margin-top:6px !important;
	}
	.background-root0 {
		color: #459A0D !important;
		background-color: #B4EC90 !important;
		border-color: #82CC52 !important;
		font-weight: bold !important;
		//background-color: rgba(105, 189, 51, 0.28) !important;
		//border-color: rgba(104, 188, 49, 0.48) !important;
	}
	#statusbarjqxSalesStatement {
		width: 0 !important;
	}
</style>
<div id="container" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>
<#if salesStatement.statusId == "SALES_SM_CREATED">
<div>
	<form name="SalesStatementAccept" method="post" action="<@ofbizUrl>changeSalesStatementStatus</@ofbizUrl>">
		<input type="hidden" name="statusId" value="SALES_SM_APPROVED">
		<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
	    <input type="hidden" name="salesStatementId" value="${salesStatement.salesStatementId?if_exists}">
	</form>
	<form name="SalesStatementCancel" method="post" action="<@ofbizUrl>changeSalesStatementStatus</@ofbizUrl>">
		<input type="hidden" name="statusId" value="SALES_SM_CANCELLED">
		<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
	    <input type="hidden" name="salesStatementId" value="${salesStatement.salesStatementId?if_exists}">
	    <input type="hidden" name="changeReason" id="changeReason" value="" />
	</form>
</div>
</#if>
<div class="row-fluid">
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-header">
                <h4>
                	<#assign customTitleProperty = "${salesStatementType?if_exists.description?if_exists} (${salesStatement.salesStatementId?if_exists})"/>
	            	<#if salesStatement.customTimePeriodId?exists>
	            		<#assign customTimeParent = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : salesStatement.customTimePeriodId}, false)!/>
                		<#if customTimeParent?exists>
							<#assign customTitleProperty = customTitleProperty + " ${customTimeParent.periodName?if_exists} <span style='font-size:14px;'>("/>
							<#assign customTitleProperty = customTitleProperty + Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(customTimeParent.fromDate, "dd/MM/yyyy", locale, timeZone)/>
							<#assign customTitleProperty = customTitleProperty + " - " + Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(customTimeParent.thruDate, "dd/MM/yyyy", locale, timeZone) + ")</span>"/>
                		</#if>
                	</#if>
                	<#--
                	<#if salesStatement.statusId == "SALES_SM_CREATED" || salesStatement.statusId == "SALES_SM_CANCELLED">
                		<button class="btn btn-mini <#if salesStatement.statusId == "SALES_SM_CANCELLED">btn-danger<#else>btn-primary</#if>" 
                			onClick="javascript:window.location.href='newSalesStatementDetail?salesStatementId=${salesStatement.salesStatementId?if_exists}';">
                			<i class="icon-pencil open-sans"></i>&nbsp;${uiLabelMap.BSEdit}</button>
					<#elseif salesStatement.statusId == "SALES_SM_APPROVED">
                		<button class="btn btn-mini btn-primary" onClick="javascript:OlbCRUDSalesStatement.calculateSalesStatement();"><i class="fa fa-calculator"></i>&nbsp;${uiLabelMap.BSCalculate}</button>
                		<#if salesStatement.salesStatementTypeId?exists && "SALES_IN" == salesStatement.salesStatementTypeId><button id="btnPushToResource" class="btn btn-mini btn-primary" onClick="javascript:void(0);"><i class="fa fa-download"></i>&nbsp;${uiLabelMap.BSPushToKPI}</button></#if>
                		<button id="btnPushTargetToResource" class="btn btn-mini btn-primary" onClick="javascript:void(0);"><i class="fa fa-download"></i>&nbsp;${uiLabelMap.BSPushTargetToKPI}</button>
					</#if>

                	<button id="btnXmlExport" class="btn btn-mini btn-primary" onClick="javascript:void(0);"><i class="fa fa-file-excel-o"></i>&nbsp;${uiLabelMap.BSExportExcel}</button>
            		-->
					${uiLabelMap.BSViewDetailReport}
            		<#if hasOlbPermission("MODULE", "SALESSTATEMENT_APPROVE", "") && salesStatement.statusId == "SALES_SM_CREATED">
	            		&nbsp;&nbsp;&nbsp;&nbsp;
	            		<a class="btn btn-primary btn-mini" href="javascript:document.SalesStatementAccept.submit()"><i class="icon-ok open-sans">${uiLabelMap.BSApproveAccept}</i></a>
						<a class="btn btn-danger btn-mini" href="javascript:OlbCRUDSalesStatement.enterCancelQuotation();"><i class="icon-remove open-sans">${uiLabelMap.BSApproveCancel}</i></a>
                	</#if>
                </h4>
                <div class="widget-toolbar no-border">
                    <div id='jqxCatalogCategoryId' style='display:inline-block; vertical-align: middle;'></div>
                    <button type="button" id="openFilterCatalogCategory" class="btn btn-mini btn-primary"  style='display:inline-block'><i class="fa fa-bars"></i></button>
                </div>
            </div>
            <div class="widget-body">
                <div class="widget-main padding-12 no-padding-left no-padding-right" style="position:relative">
                	<div class="title-status" id="statusTitle" style="margin-top:-20px">
                		<#assign statusItem = delegator.findOne("StatusItem", {"statusId": salesStatement.statusId}, false)!/>
                		<#if statusItem?exists>${statusItem.get("description", locale)}</#if>
					</div>
			    	<#--<#assign listProduct = Static["com.olbius.salesmtl.SalesStatementServices"].getListProduct(delegator)!/>-->
					<#assign listProduct = []/>
					<#assign dataField = "[
									{ name: 'partyId', type: 'string' },
									{ name: 'partyCode', type: 'string' },
									{ name: 'partyIdFrom', type: 'string' },
									{ name: 'partyName', type: 'string' },
					               	{ name: 'totalEmployee', type: 'string'},
					               	{ name: 'levelTree', type: 'string'},
					           	"/>
					<#assign columnlist = "
									{text: '${uiLabelMap.OrgUnitName}', datafield: 'partyName', width: '24%', pinned: true, cellClassName: cellClassCommonHeader},			
						       		{text: '${uiLabelMap.OrgUnitId}', datafield: 'partyCode', width: '140px', pinned: true, cellClassName: cellClassCommonHeader},
						       		{text: '${uiLabelMap.TotalEmpl}', datafield: 'totalEmployee', width: '70px', cellClassName: cellClassCommon},
							 	"/>
					<#assign columnGroups = "["/>
					
					<#if listProduct?exists>
						<#list listProduct as product>
							<#assign dataField = dataField + "
										{ name: 'prodCode_${product.productId}', type: 'number', formatter: 'integer'},
										{ name: 'actual_${product.productId}', type: 'number', formatter: 'integer'},
										{ name: 'percent_${product.productId}', type: 'number', formatter: 'integer'},
						   			"/>
						   	
							<#assign columnlist = columnlist + "
										{text: 'Target', dataField: 'prodCode_${product.productId}', width: '100px', cellsalign: 'right', cellClassName: cellClassCommon, columnGroup: 'colGrp_${product.productCode}'},
										{text: 'Actual', dataField: 'actual_${product.productId}', width: '100px', cellsalign: 'right', cellClassName: cellClassCommon, columnGroup: 'colGrp_${product.productCode}'},
										{text: 'Percent', dataField: 'percent_${product.productId}', width: '70px', cellsalign: 'right', cellClassName: cellClassCommon, columnGroup: 'colGrp_${product.productCode}', cellsformat: 'p2'},
									"/>
							<#assign columnGroups = columnGroups + "
										{name: 'colGrp_${product.productCode}', text: '${product.productCode}'}
									"/>
						</#list>
					</#if>
					<#assign dataField = dataField + "]"/>
					<#assign columnGroups = columnGroups + "]"/>
					
					<#assign uiLabelMapTotalEmpl = '${uiLabelMap.BSNumEmployee}'>
					<#if "SALES_IN" == salesStatement.salesStatementTypeId>
						<#assign uiLabelMapTotalEmpl = '${uiLabelMap.BSNumDistributor}'>
					</#if>
					
					<div id="jqxSalesStatement" class="jqx-tree-grid-olbius"></div>
			    	
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
<div id="alterpopupWindowPushToResource" style="display:none">
	<div>${uiLabelMap.BSChooseKPIPushToResource}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span3'>
							<label for="wn_ptr_criteriaId">${uiLabelMap.BSChooseKPI}</label>
						</div>
						<div class='span9'>
							<div id="wn_ptr_criteriaId">
								<div id="wn_ptr_criteriaGrid"></div>
							</div>
				   		</div>
					</div>
				</div><!--.span12-->
			</div><!--.row-fluid-->
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
				<button id="wn_ptr_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_ptr_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#assign contextMenuSsdvItemId = "ctxmnussdv">
<div id='contextMenu_${contextMenuSsdvItemId}' style="display:none">
	<ul>
	    <li id="${contextMenuSsdvItemId}_expand"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpand)}</li>
	    <li id="${contextMenuSsdvItemId}_collapse"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapse)}</li>
		<li id="${contextMenuSsdvItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li id="${contextMenuSsdvItemId}_openCSM"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewLevelCSM)}</li>
		<li id="${contextMenuSsdvItemId}_openRSM"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewLevelRSM)}</li>
		<li id="${contextMenuSsdvItemId}_openASM"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewLevelASM)}</li>
		<li id="${contextMenuSsdvItemId}_openSUP"><i class="fa fa-folder-open-o"></i>${StringUtil.wrapString(uiLabelMap.BSViewLevelSUP)}</li>
		<li id="${contextMenuSsdvItemId}_expandAll"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpandAll)}</li>
		<li id="${contextMenuSsdvItemId}_collapseAll"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapseAll)}</li>
	</ul>
</div>
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.export.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true/>
<script type="text/javascript">
	$(function(){
		OlbCRUDSalesStatement.init();
	});
	var OlbCRUDSalesStatement = (function(){
		var localDataCatalogCategory = [];
		var productCatalogCategoryDDL;
		var catalogIds;
		var categoryIds;
		var criteriaDDB;
		var productIdsAllStr = "";
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initData();
			initElementWindow();
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
		        updateAllDataGrid();
			} else {
				updateGridStatementSource();
			}
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowFilter"), {width: 820, height: 460, cancelButton: $("#wf_alterCancel")});
            jOlbUtil.notification.create("#container", "#jqxNotification", null, {width: 'auto', autoClose: true});
            jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuSsdvItemId}"));
		};
		var initElementComplex = function(){
			var configCatalogCategoryList = {
				datatype: 'array',
				width: '220px',
				placeHolder: "${StringUtil.wrapString(uiLabelMap.BSNoCategory)}",
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
            
    		
            <#assign customcontrol1 = ""/>
            <#assign customcontrol2 = ""/>
            <#assign customcontrol3 = ""/>
            <#assign customcontrol4 = "fa fa-file-excel-o open-sans@${uiLabelMap.BSExportExcel}@javascript:OlbCRUDSalesStatement.exportExcel();"/>
            <#if hasOlbPermission("MODULE", "SALESSTATEMENT_EDIT", "")>
				<#if salesStatement.statusId == "SALES_SM_CREATED" || salesStatement.statusId == "SALES_SM_CANCELLED">
					<#assign customcontrol1 = "icon-pencil open-sans"/>
	            	<#if salesStatement.statusId == "SALES_SM_CANCELLED">
	            		<#assign customcontrol1 = customcontrol1 + " red"/>
	            	</#if>
	        		<#assign customcontrol1 = customcontrol1 + "@${uiLabelMap.BSEdit}@javascript:window.location.href='newSalesStatementDetail?salesStatementId=${salesStatement.salesStatementId?if_exists}';"/>
				<#elseif salesStatement.statusId == "SALES_SM_APPROVED">
					<#assign customcontrol1 = "fa fa-calculator open-sans@${uiLabelMap.BSCalculate}@javascript:OlbCRUDSalesStatement.calculateSalesStatement();"/>
	        		
					<#--<#if salesStatement.salesStatementTypeId?exists && "SALES_IN" == salesStatement.salesStatementTypeId>
						<#assign customcontrol2 = "fa fa-download open-sans@${uiLabelMap.BSPushToKPI}@javascript:OlbCRUDSalesStatement.openWindowPushToResource();"/>
					</#if>-->
					<#assign customcontrol2 = "fa fa-download open-sans@${uiLabelMap.BSPushToKPI}@javascript:OlbCRUDSalesStatement.openWindowPushToResource();"/>
					<#assign customcontrol3 = "fa fa-download open-sans@${uiLabelMap.BSPushTargetToKPI}@javascript:OlbCRUDSalesStatement.pushTargetToResource();"/>
				</#if>
			</#if>
			var configProductList = {
				width: '100%',
				height: "auto",
				datafields: ${dataField},
				columns: [${columnlist}],
				columnGroups: ${columnGroups},
				editable: false,
				editmode: 'click',
				selectionmode: 'singlecell',
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: false,
				useUrl: true,
				url: "",
				key: 'partyId',
				parentKeyId: 'partyIdFrom',
				altRows: false,
				
				pageable: true,
				pagesize: 15,
				showtoolbar: true,
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(customTitleProperty)}",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
					customcontrol2: "${StringUtil.wrapString(customcontrol2)}",
					customcontrol3: "${StringUtil.wrapString(customcontrol3)}",
					customcontrol4: "${StringUtil.wrapString(customcontrol4)}",
					expendButton: true,
				},
				contextMenu: "contextMenu_${contextMenuSsdvItemId}",
			};
			new OlbTreeGrid($("#jqxSalesStatement"), null, configProductList, []);
			<#--//"jqxGeneralServicer?sname=JQListOrganizationUnitManagerReport&pagesize=0&salesStatementId=${parameters.salesStatementId?if_exists}"-->
		};
		var initEvent = function() {
			$("#btnXmlExport").click(function () {
				exportExcel();
            });
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
		   	
	        $("#contextMenu_${contextMenuSsdvItemId}").on('itemclick', function (event) {
	            var args = event.args;
		        // var tmpKey = $.trim($(args).text());
		        var tmpId = $(args).attr('id');
		        var idGrid = "#jqxSalesStatement";
		        var rowData;
		        var id;
		        var thisLevel;
	        	var selection = $(idGrid).jqxTreeGrid('getSelection');
	        	if (selection.length > 0) rowData = selection[0];
	        	if (rowData) {
	        		id = rowData.partyId;
	        		thisLevel = rowData["levelTree"];
        		}
	        	switch(tmpId) {
	        		case "${contextMenuSsdvItemId}_refesh": { 
	        			$(idGrid).jqxTreeGrid('updateBoundData');
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_expandAll": { 
	        			$(idGrid).jqxTreeGrid('expandAll', true);
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_collapseAll": { 
	        			$(idGrid).jqxTreeGrid('collapseAll', true);
	        			break;
        			};
	        		case "${contextMenuSsdvItemId}_expand": { 
	        			if(id) $(idGrid).jqxTreeGrid('expandRow', id);
	        			break;
	        		};
	        		case "${contextMenuSsdvItemId}_collapse": { 
	        			if(id) $(idGrid).jqxTreeGrid('collapseRow', id);
	        			break;
        			};
	        		case "${contextMenuSsdvItemId}_openCSM": { 
	        			expandOrCollapseRow(idGrid, rowData, thisLevel, 1);
	        			break;
        			};
        			case "${contextMenuSsdvItemId}_openRSM": { 
        				expandOrCollapseRow(idGrid, rowData, thisLevel, 2);
	        			break;
        			};
        			case "${contextMenuSsdvItemId}_openASM": { 
        				expandOrCollapseRow(idGrid, rowData, thisLevel, 3);
	        			break;
        			};
        			case "${contextMenuSsdvItemId}_openSUP": { 
        				expandOrCollapseRow(idGrid, rowData, thisLevel, 4);
	        			break;
        			};
	        		default: break;
	        	}
	        });
		};
		var expandOrCollapseRow = function(idTree, rowData, thisLevel, targetLevel){
			$("#loader_page_common").show();
			setTimeout(function(){
				if (thisLevel < targetLevel) {
					$(idTree).jqxTreeGrid('collapseAll', true);
					var rowsGrid = $(idTree).jqxTreeGrid('getRows');
					if (rowsGrid) {
						for (var i = 0; i < rowsGrid.length; i++) {
							if (OlbCore.isEmpty(rowsGrid.parent)) {
								expandRowByLevel(idTree, rowsGrid[i], targetLevel);
							}
						}
					}
				} else {
					collapseRowByLevel(idTree, rowData, targetLevel);
				}
				$("#loader_page_common").hide();
			}, 200);
		};
		var expandRowByLevel = function(idTree, rowData, level){
			if (rowData) {
				var levelTree = rowData["levelTree"];
				if (levelTree < level) {
					var rowId = rowData.uid;
		    		if(rowId) $(idTree).jqxTreeGrid('expandRow', rowId);
		    		
		    		var rows = rowData.records;
		    		if (rows) {
		    			var rowSize = rows.length;
						for (var i = 0; i < rowSize; i++) {
				    		expandRowByLevel(idTree, rows[i], level);
						}
		    		}
				}
			}
		};
		var collapseRowByLevel = function(idTree, rowData, level){
			if (rowData) {
				var levelTree = rowData["levelTree"];
				if (levelTree > level) {
					var parent = rowData.parent;
					if (parent) {
						var parentLevel = parent["levelTree"];
						if (parentLevel <= level) {
							var parentId = parent.uid;
							if(parentId) $(idTree).jqxTreeGrid('collapseRow', parentId);
						} else {
							collapseRowByLevel(idTree, parent, level);
						}
					}
				} else if (levelTree == level) {
					var rowId = rowData.uid;
					if(rowId) $(idTree).jqxTreeGrid('collapseRow', rowId);
				}
			}
		};
		
		var initElementWindow = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowPushToResource"), {width: 400, height: 160, cancelButton: $("#wn_ptr_alterCancel")});
			
			var configCriteria = {
				useUrl: true,
				root: 'listPerfCriteria',
				widthButton: '98%',
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [
					{name: 'criteriaId', type: 'string'}, 
					{name: 'criteriaName', type: 'string'}, 
					{name: 'description', type: 'string'},
					{name: 'perfCriteriaTypeId', type: 'string'},
					{name: 'periodTypeId', type: 'string'},
					{name: 'uomId', type: 'string'},
					{name: 'target', type: 'string'},
					{name: 'statusId', type: 'string'},
				],
				columns: [
					{text: "${uiLabelMap.BSCriteriaId}", datafield: 'criteriaId', width: '20%'},
					{text: "${uiLabelMap.BSCriteriaName}", datafield: 'criteriaName', width: '20%'},
					{text: "${uiLabelMap.BSDescription}", datafield: 'description', width: '20%'},
					{text: "${uiLabelMap.BSPerfCriteriaTypeId}", datafield: 'perfCriteriaTypeId', width: '20%'},
					{text: "${uiLabelMap.BSPeriodTypeId}", datafield: 'periodTypeId', width: '20%'},
					{text: "${uiLabelMap.BSUom}", datafield: 'uomId', width: '20%'},
					{text: "${uiLabelMap.BSTarget}", datafield: 'target', width: '20%'},
					{text: "${uiLabelMap.BSStatus}", datafield: 'statusId', width: '20%'},
				],
				pageable:true,
				url: 'getPerfCriteriaByTypeAjax',
				useUtilFunc: false,
				dataMap: {perfCriteriaTypeId: "KPI_SALE"},
				
				key: 'criteriaId',
				description: ['criteriaName'],
				autoCloseDropDown: true,
				filterable: true
			};
			criteriaDDB = new OlbDropDownButton($("#wn_ptr_criteriaId"), $("#wn_ptr_criteriaGrid"), null, configCriteria, []);
			
			$("#btnPushToResource").on("click", function(){
				openWindowPushToResource();
			});
			
			$("#wn_ptr_alterSave").on("click", function(){
				$.ajax({
					type: 'POST',
					url: 'pushDataSaleToCriteria', 
					data: {
						salesStatementId: "${salesStatement.salesStatementId}",
						criteriaId: criteriaDDB.getValue()
					},
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
						        	$("#jqxNotification").jqxNotification("open");
						        	
						        	$("#alterpopupWindowPushToResource").jqxWindow("close");
						        	return true;
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
			});
			
			$("#btnPushTargetToResource").on("click", function(){
				pushTargetToResource();
			});
		};
		
		var updateAllDataGrid = function(){
			var str = "";
	       	var items = $('#jqxCatalogCategoryGrid').jqxTree('getCheckedItems');
	       	catalogIds = new Array();
	       	categoryIds = new Array();
	       	localDataCatalogCategory = [];
	       	localDataCatalogCategory[0] = {"id": "_NA_", "label": "${uiLabelMap.BSNoCategory}", "type": "_NA_"};
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
	       	//updateJqxSalesStatement(catalogIds, categoryIds);
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
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
					        	if (data.listProductIds != undefined && data.listProductIds != null && data.listProductIds.length > 0) {
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
			var columnGroupListEdit = new Array();
			
			datafieldListEdit.push({ name: 'partyId', type: 'string' });
			datafieldListEdit.push({ name: 'partyCode', type: 'string' });
			datafieldListEdit.push({ name: 'partyIdFrom', type: 'string' });
			datafieldListEdit.push({ name: 'partyName', type: 'string' });
			datafieldListEdit.push({ name: 'totalEmployee', type: 'string'});
			datafieldListEdit.push({ name: 'levelTree', type: 'string'});
			
			columnListEdit.push({text: '${uiLabelMap.OrgUnitName}', datafield: 'partyName', width: '24%', pinned: true, cellClassName: cellClassCommonHeader});
			columnListEdit.push({text: '${uiLabelMap.OrgUnitId}', datafield: 'partyCode', width: '140px', pinned: true, cellClassName: cellClassCommonHeader});
			columnListEdit.push({text: '${uiLabelMapTotalEmpl}', datafield: 'totalEmployee', width: '70px', cellClassName: cellClassCommon});
			
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
					datafieldListEdit.push({ name: 'actual_' + productId, type: 'number', formatter: 'integer'});
					datafieldListEdit.push({ name: 'percent_' + productId, type: 'number', formatter: 'integer'});
					columnListEdit.push({text: "Target", dataField: 'prodCode_' + productId, width: '100px', cellsalign: 'right', cellClassName: cellClassCommon, columnGroup: 'colGrp_' + productId});
					columnListEdit.push({text: "Actual", dataField: 'actual_' + productId, width: '100px', cellsalign: 'right', cellClassName: cellClassCommon, columnGroup: 'colGrp_' + productId});
					columnListEdit.push({text: "Percent", dataField: 'percent_' + productId, width: '70px', cellsalign: 'right', cellClassName: cellClassCommon, columnGroup: 'colGrp_' + productId, cellsformat: 'p2'});
					columnGroupListEdit.push({name: 'colGrp_' + productId, text: columnLabel});
				}
			}
			
			var productIdsStr = JSON.stringify(productIds);
			$("#jqxSalesStatement").jqxTreeGrid('columnGroups', columnGroupListEdit);
			$("#jqxSalesStatement").jqxTreeGrid('columns', columnListEdit);
	       	
	       	updateGridStatementSource(datafieldListEdit, productIdsStr);
		};
		
		var updateGridStatementSource = function(datafieldListEdit, productIdsStr){
			var tmpSource = jQuery("#jqxSalesStatement").jqxTreeGrid('source');
			if (tmpSource) {
				if (datafieldListEdit != null && datafieldListEdit != undefined) tmpSource._source.datafields = datafieldListEdit;
				//tmpSource._source.columnGroups = columnGroupListEdit;
				var urlNew = "jqxGeneralServicer?sname=JQListOrganizationUnitManagerReport&pagesize=0&salesStatementId=${parameters.salesStatementId?if_exists}";
				if (productIdsStr != null && productIdsStr != undefined) {
					urlNew += "&productIds=" + productIdsStr;
					productIdsAllStr = productIdsStr;
				}
				tmpSource._source.url = urlNew;
				$("#jqxSalesStatement").jqxTreeGrid('source', tmpSource);
				$("#jqxSalesStatement").jqxTreeGrid('updateBoundData');
				//$("#jqxSalesStatement").jqxTreeGrid('expandAll');
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
		var calculateSalesStatement = function(){
			$.ajax({
				type: 'POST',
				url: 'calculateSalesStatement',
				data: {salesStatementId: "${parameters.salesStatementId?if_exists}"},
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({template: 'error'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
								$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	location.reload();
					        	//$("#jqxSalesStatement").jqxGrid("updatebounddata");
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
		var enterCancelQuotation = function(){
			jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCancelNotAccept)}", 
				function(){
					document.SalesStatementCancel.submit();
				}, "${StringUtil.wrapString(uiLabelMap.wgcancel)}", "${StringUtil.wrapString(uiLabelMap.wgok)}"
			);
			<#--
			bootbox.prompt("<span style='font-size:13px; padding:0; margin: -10px; display:block; height:25px'>${uiLabelMap.BSReasonCancelQuotation}:</span>", function(result) {
				if(result === null) {
				} else {
					document.getElementById('changeReason').value = "" + result;
					document.SalesStatementCancel.submit();
				}
			});
			-->
		};
		var exportExcel = function(){
			var fileName = "${salesStatement.salesStatementTypeId}_${salesStatement.salesStatementId}_" + new Date();
            //$("#jqxSalesStatement").jqxTreeGrid('exportData', 'xls', fileName);
            
            window.location.href = "exportSales4cExcel?salesStatementId=${parameters.salesStatementId?if_exists}&productIds=" + productIdsAllStr;
		};
		var openWindowPushToResource = function(){
			$("#alterpopupWindowPushToResource").jqxWindow("open");
		};
		var pushTargetToResource = function(){
			$.ajax({
				type: 'POST',
				url: 'pushDataSaleToCriteriaTarget', 
				data: {
					salesStatementId: "${salesStatement.salesStatementId}",
				},
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html(errorMessage);
					        	$("#jqxNotification").jqxNotification("open");
					        	return false;
							}, function(){
					        	$('#container').empty();
					        	$('#jqxNotification').jqxNotification({ template: 'info'});
					        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
					        	$("#jqxNotification").jqxNotification("open");
					        	
					        	$("#alterpopupWindowPushToResource").jqxWindow("close");
					        	return true;
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
		return {
			init: init,
			calculateSalesStatement: calculateSalesStatement,
			enterCancelQuotation: enterCancelQuotation,
			exportExcel: exportExcel,
			openWindowPushToResource: openWindowPushToResource,
			pushTargetToResource: pushTargetToResource,
		};
	}());
</script>
