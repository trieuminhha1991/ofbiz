<style type="text/css">
	#messageContainer {
		font-size:14px;
	}
	#productDetailContainer span {
		color: #69aa46;
		font-weight: bold;
	}
	#stateContainer {
		font-size:14px;
	}
</style>
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>

<div class="row-fluid">
	<div class="span4">
		<h4>${uiLabelMap.BSInSalesCategory}</h4>
		<div>
			<div id="isMissTaxCategory" style="text-align: left"><span>${uiLabelMap.BSMissTaxCategory}</span></div>
			<div id="isMissRefCategory" style="text-align: left"><span>${uiLabelMap.BSMissMMSCategory}</span></div>
			<div id="isMissUpc" style="text-align: left"><span>${uiLabelMap.BSMissUPC}</span></div>
			<div id="isMissUpcPrimary" style="text-align: left"><span>${uiLabelMap.BSMissUPCPrimary}</span></div>
			<div id="isMissSalesPrice" style="text-align: left"><span>${uiLabelMap.BSMissSalesPrice}</span></div>
			<div id="isMissPurchasePrice" style="text-align: left"><span>${uiLabelMap.BSMissPurchasePrice}</span></div>
		</div>
		<h4>${uiLabelMap.BSInAll}</h4>
		<div>
			<div id="isAllMissTaxCategory" style="text-align: left"><span>${uiLabelMap.BSMissTaxCategory}</span></div>
			<div id="isAllMissRefCategory" style="text-align: left"><span>${uiLabelMap.BSMissMMSCategory}</span></div>
			<div id="isAllMissUpc" style="text-align: left"><span>${uiLabelMap.BSMissUPC}</span></div>
			<div id="isAllMissUpcPrimary" style="text-align: left"><span>${uiLabelMap.BSMissUPCPrimary}</span></div>
			<div id="isAllMissSalesPrice" style="text-align: left"><span>${uiLabelMap.BSMissSalesPrice}</span></div>
			<div id="isAllMissPurchasePrice" style="text-align: left"><span>${uiLabelMap.BSMissPurchasePrice}</span></div>
		</div>
		<h4>${uiLabelMap.BSProduct}</h4>
		<div>
			<div id="isCheckProductDetail" style="text-align: left"><span>${uiLabelMap.BSProductDetail}</span></div>
			<div id="productCheckDetail" class="margin-bottom10">
				<input id="productIdCheckDetail" type="input"/>
			</div>
		</div>
		<div class="row-fluid">
			<div class="pull-left">
				<button type="button" id="btnCheck" class="btn btn-small btn-primary"><i class="fa fa-search"></i>&nbsp;${uiLabelMap.BSActionCheck}</button>
			</div>
		</div>
	</div>
	<div class="span8">
		<div id="messageContainer" class="margin-bottom20">
			<h3 class="no-left-margin no-left-padding"><span id="messageFindType" class="green"></span></h3>
			<b>${uiLabelMap.BSFindIn}</b>: <span id="messageFindIn" class="green"></span>
		</div>
		<div id="gridContainer">
			<div id="gridProduct"></div>
		</div>
		<div id="productDetailContainer" class="hide">
			<div class="row-fluid stateContainer">
				<div class="span6">${uiLabelMap.BSState}: <span id="productCKState"></span></div>
				<div class="span6"></div>
			</div>
			<hr class="small-margin"/>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSProductId}: <span id="productCKCode"></span></div>
				<div class="span6"></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSProductName}: <span id="productCKName"></span></div>
				<div class="span6"></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSMissPrimaryCategory}: <span id="productCKPrimaryCategory"></span></div>
				<div class="span6"><span id="productCKPrimaryCategoryId"></span></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSMissTaxCategory}: <span id="productCKTaxCategory"></span></div>
				<div class="span6"><span id="productCKTaxCategoryIds"></span></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSMissMMSCategory}: <span id="productCKRefCategory"></span></div>
				<div class="span6"><span id="productCKRefCategoryIds"></span></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSMissUPC}: <span id="productCKUPC"></span></div>
				<div class="span6"><span id="productCKUPCIds"></span></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSMissUPCPrimary}: <span id="productCKUPCPrimary"></span></div>
				<div class="span6"><span id="productCKUPCPrimaryIds"></span></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSMissSalesPrice}: <span id="productCKSalesPrice"></span></div>
				<div class="span6"><span id="productCKSalesPriceIds"></span></div>
			</div>
			<div class="row-fluid">
				<div class="span6">${uiLabelMap.BSMissPurchasePrice}: <span id="productCKPurchasePrice"></span></div>
				<div class="span6"><span id="productCKPurchasePriceIds"></span></div>
			</div>
		</div>
	</div>
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<@jqOlbCoreLib hasGrid=true/>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.isMissTaxCategory = "${StringUtil.wrapString(uiLabelMap.BSMissTaxCategory)}";
	uiLabelMap.isMissRefCategory = "${StringUtil.wrapString(uiLabelMap.BSMissMMSCategory)}";
	uiLabelMap.isMissUpc = "${StringUtil.wrapString(uiLabelMap.BSMissUPC)}";
	uiLabelMap.isMissUpcPrimary = "${StringUtil.wrapString(uiLabelMap.BSMissUPCPrimary)}";
	uiLabelMap.isMissSalesPrice = "${StringUtil.wrapString(uiLabelMap.BSMissSalesPrice)}";
	uiLabelMap.isMissPurchasePrice = "${StringUtil.wrapString(uiLabelMap.BSMissPurchasePrice)}";
	uiLabelMap.isCheckProductDetail = "${StringUtil.wrapString(uiLabelMap.BSProduct)}";
	uiLabelMap.BSInSalesCategory = "${StringUtil.wrapString(uiLabelMap.BSInSalesCategory)}";
	uiLabelMap.BSInAll = "${StringUtil.wrapString(uiLabelMap.BSInAll)}";
	
	$(function(){
		OlbProductCheckError.init();
	});
	var OlbProductCheckError = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			$("#isMissTaxCategory").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isMissRefCategory").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isMissUpc").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isMissUpcPrimary").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isMissSalesPrice").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isMissPurchasePrice").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isAllMissTaxCategory").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isAllMissRefCategory").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isAllMissUpc").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isAllMissUpcPrimary").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isAllMissSalesPrice").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isAllMissPurchasePrice").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			$("#isCheckProductDetail").jqxRadioButton({groupName: 'checkType', theme: 'olbius', width: 250, height: 25});
			
			jOlbUtil.input.create("#productIdCheckDetail", {"disabled": true, "placeHolder": "${StringUtil.wrapString(uiLabelMap.BSEnterProductIdOrUpc)}"});
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initElementComplex = function(){
			<#assign customcontrol1 = "fa fa-file-excel-o@@javascript: void(0);@OlbProductCheckError.exportExcel()">
			var configProductItems = {
				datafields: [
					{name: 'productId', type: 'string'},
					{name: 'productCode', type: 'string'},
		       		{name: 'productName', type: 'string'},
		    	],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '16%',
						cellsrenderer: function(row, colum, value){
							var productId;
							var rowData = $('#gridProduct').jqxGrid('getrowdata', row);
							if (rowData) {productId = rowData.productId}
							return '<div style="margin:4px"><a href="viewProduct?productId=' + productId + '">' + value + '</a></div>';
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', minWidth: '16%'},
				],
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: true,
				pageable: true,
				pagesize: 10,
				pagesizeoptions: [5, 10, 15, 20, 25, 50, 100],
				showfilterrow: true,
				useUtilFunc: false,
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQCheckProductError',
				showtoolbar:true,
				rendertoolbarconfig: {
					titleProperty: "",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
				},
				bindresize: true,
			};
			productGRID = new OlbGrid($("#gridProduct"), null, configProductItems, []);
		};
		var initEvent = function(){
			$("#isCheckProductDetail").on("checked", function(){
				$("#productIdCheckDetail").jqxInput("disabled", false);
				$("#gridContainer").hide();
				$("#productDetailContainer").show();
			});
			$("#isCheckProductDetail").on("unchecked", function(){
				$("#productIdCheckDetail").jqxInput("disabled", true);
				$("#gridContainer").show();
				$("#productDetailContainer").hide();
			});
			
			$("#btnCheck").click(function(){
				var otherParam = getOtherParam();
				if ($("#isCheckProductDetail").val()) {
					// find product detail
					$("#btnCheck").addClass("disabled");
					
					$("#productCKCode").html("");
	        		$("#productCKName").html("");
	        		$("#productCKState").html("");
	        		$("#productCKPrimaryCategory").html("");
	        		$("#productCKPrimaryCategoryId").html("");
	        		$("#productCKTaxCategory").html("");
	        		$("#productCKTaxCategoryIds").html("");
	        		$("#productCKRefCategory").html("");
	        		$("#productCKRefCategoryIds").html("");
	        		$("#productCKUPC").html("");
	        		$("#productCKUPCIds").html("");
	        		$("#productCKUPCPrimary").html("");
	        		$("#productCKUPCPrimaryIds").html("");
	        		$("#productCKSalesPrice").html("");
	        		$("#productCKSalesPriceIds").html("");
	        		$("#productCKPurchasePrice").html("");
	        		$("#productCKPurchasePriceIds").html("");
	        		
					$.ajax({
						type: 'POST',
						url: 'checkProductErrorDetailAjax',
						data: {
							productId: $("#productIdCheckDetail").val()
						},
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
									$("#btnCheck").removeClass("disabled");
									
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	return false;
								}, function(){
									//$('#container').empty();
						        	//$('#jqxNotification').jqxNotification({ template: 'info'});
						        	//$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
						        	//$("#jqxNotification").jqxNotification("open");
						        	
						        	var results = data.results;
						        	if (results) {
						        		var stateId = results.stateId; // ENOUGH_DATA, MISS_DATA
						        		if (stateId == "ENOUGH_DATA") {
						        			$(".stateContainer").css("color", "");
						        			$(".stateContainer span").css("color", "");
						        		} else {
						        			$(".stateContainer").css("color", "#dd5a43", "important");
						        			$(".stateContainer span").css("color", "#dd5a43", "important");
						        		}
						        		
						        		$("#productCKCode").html(results.productCode);
						        		$("#productCKName").html(results.productName);
						        		$("#productCKState").html(results.stateMsg);
						        		$("#productCKPrimaryCategory").html(results.primaryCategoryState);
						        		$("#productCKPrimaryCategoryId").html(results.primaryCategoryId);
						        		$("#productCKTaxCategory").html(results.taxCategoryState);
						        		$("#productCKTaxCategoryIds").html(results.taxCategoryIds);
						        		$("#productCKRefCategory").html(results.refCategoryState);
						        		$("#productCKRefCategoryIds").html(results.refCategoryIds);
						        		$("#productCKUPC").html(results.upcState);
						        		$("#productCKUPCIds").html(results.upcIds);
						        		$("#productCKUPCPrimary").html(results.upcPrimaryState);
						        		$("#productCKUPCPrimaryIds").html(results.upcPrimaryIds);
						        		$("#productCKSalesPrice").html(results.salesPriceState);
						        		$("#productCKSalesPriceIds").html(results.salesPriceIds);
						        		$("#productCKPurchasePrice").html(results.purchPriceState);
						        		$("#productCKPurchasePriceIds").html(results.purchPriceIds);
						        	}
								}
							);
						},
						error: function(data){
							alert("Send request is error");
							$("#btnCheck").removeClass("disabled");
						},
						complete: function(data){
							$("#loader_page_common").hide();
							$("#btnCheck").removeClass("disabled");
						},
					});
				} else {
					productGRID.updateSource("jqxGeneralServicer?sname=JQCheckProductError&" + otherParam);
				}
			});
		};
		var exportExcel = function(){
			var isExistData = productGRID.isExistData();
			if (!isExistData) {
				jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
				return false;
			}
			
			var otherParam = getOtherParam();
			window.location.href = "exportProductErrorExcel?" + otherParam;
		};
		var getOtherParam = function(){
			var filterType = "";
			var findType = "";
			var findIn = "";
			if ($("#isMissTaxCategory").val()) {
				filterType = "MISS_TAX_CATEGORY";
				findType = "isMissTaxCategory";
				findIn = "SALES_CATEGORY";
			}
			if ($("#isMissRefCategory").val()) {
				filterType = "MISS_REF_CATEGORY";
				findType = "isMissRefCategory";
				findIn = "SALES_CATEGORY";
			}
			if ($("#isMissUpc").val()) {
				filterType = "MISS_UPC";
				findType = "isMissUpc";
				findIn = "SALES_CATEGORY";
			}
			if ($("#isMissUpcPrimary").val()) {
				filterType = "MISS_UPC_PRIMARY";
				findType = "isMissUpcPrimary";
				findIn = "SALES_CATEGORY";
			}
			if ($("#isMissSalesPrice").val()) {
				filterType = "MISS_SALES_PRICE";
				findType  = "isMissSalesPrice";
				findIn = "SALES_CATEGORY";
			}
			if ($("#isMissPurchasePrice").val()) {
				filterType = "MISS_PURCHASE_PRICE";
				findType  = "isMissPurchasePrice";
				findIn = "SALES_CATEGORY";
			}
			// find in all
			if ($("#isAllMissTaxCategory").val()) {
				filterType = "ALL_MISS_TAX_CATEGORY";
				findType = "isMissTaxCategory";
				findIn = "IN_ALL";
			}
			if ($("#isAllMissRefCategory").val()) {
				filterType = "ALL_MISS_REF_CATEGORY";
				findType = "isMissRefCategory";
				findIn = "IN_ALL";
			}
			if ($("#isAllMissUpc").val()) {
				filterType = "ALL_MISS_UPC";
				findType = "isMissUpc";
				findIn = "IN_ALL";
			}
			if ($("#isAllMissUpcPrimary").val()) {
				filterType = "ALL_MISS_UPC_PRIMARY";
				findType = "isMissUpcPrimary";
				findIn = "IN_ALL";
			}
			if ($("#isAllMissSalesPrice").val()) {
				filterType = "ALL_MISS_SALES_PRICE";
				findType  = "isMissSalesPrice";
				findIn = "IN_ALL";
			}
			if ($("#isAllMissPurchasePrice").val()) {
				filterType = "ALL_MISS_PURCHASE_PRICE";
				findType  = "isMissPurchasePrice";
				findIn = "IN_ALL";
			}
			// find product detail
			if ($("#isCheckProductDetail").val()) {
				filterType = "CHECK_PRODUCT_DETAIL";
				findType  = "isCheckProductDetail";
				findIn = "PRODUCT_DETAIL";
			}
			
			if (findType) {
				$("#messageFindType").html(uiLabelMap[findType]);
			} else {
				$("#messageFindType").html("");
			}
			if (findIn == "SALES_CATEGORY") {
				$("#messageFindIn").html(uiLabelMap["BSInSalesCategory"]);
			} else if (findIn == "IN_ALL") {
				$("#messageFindIn").html(uiLabelMap["BSInAll"]);
			} else if (findIn == "PRODUCT_DETAIL") {
				$("#messageFindIn").html(uiLabelMap["isCheckProductDetail"]);
			} else {
				$("#messageFindIn").html("");
			}
			
			var otherParam = "filterType=" + filterType;
			if (filterType == "CHECK_PRODUCT_DETAIL") {
				otherParam += "&productId=" + $("#productIdCheckDetail").val();
			}
			return otherParam;
		};
		return {
			init: init,
			exportExcel: exportExcel
		}
	}());
</script>