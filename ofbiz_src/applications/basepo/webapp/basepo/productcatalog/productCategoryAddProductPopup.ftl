<div id="alterpopupWindowAddProduct" style="display:none">
	<div>${uiLabelMap.BSAddProduct}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<input type="hidden" id="wn_prod_productCategoryId" value="${productCategory?if_exists.productCategoryId?if_exists}"/>
					<div id="wn_prod_productGrid"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="wn_prod_alterSave" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_prod_alterCancel" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbProdCategoryAddProduct.init();
	});
	var OlbProdCategoryAddProduct = (function(){
		var productGRID;
		var productIdsNotSuccess;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowAddProduct"), {width: 960, maxWidth: 960, height: 540, cancelButton: $("#wn_prod_alterCancel")});
		};
		var initElementComplex = function(){
			var configProduct = {
				autoshowloadelement: true,
				showdefaultloadelement: true,
				localization: getLocalization(),
				datafields: [
					{name: "productId", type: "string"},
					{name: "productCode", type: "string"},
					{name: "productName", type: "string"},
					{name: "primaryProductCategoryId", type: "string"},
				],
				columns: [
					{text: "${uiLabelMap.BSProductId}", datafield: "productCode", width: 150},
					{text: "${uiLabelMap.BSProductName}", datafield: "productName"},
					{text: "${uiLabelMap.BSPrimaryCategory}", datafield: "primaryProductCategoryId", width: 150},
				],
				useUrl: true,
				useUtilFunc: true,
				url: "JQGetListProductAddCateMember&hasVirtualProd=Y&currentCategoryId=${productCategory?if_exists.productCategoryId?if_exists}",
				//virtualmode: true,
				height: 425,
				autoheight: false,
				showfilterrow: true,
				filterable: true,
				editable: false,
				width: "100%",
				pagesize: 12,
				pageable: true,
				pagesizeoptions: [5, 10, 12, 15, 20, 25, 50, 100],
				bindresize: true,
				sortable: true,
				enabletooltips: true,
				selectionmode: "checkbox",
				showtoolbar:true,
				rendertoolbarconfig: {
					titleProperty: "${StringUtil.wrapString(uiLabelMap.BSListProduct)}",
				},
			};
			productGRID = new OlbGrid($("#wn_prod_productGrid"), null, configProduct, []);
		};
		var initEvent = function(){
			$("#wn_prod_alterSave").on("click", function(){
				var rowindexes = $("#wn_prod_productGrid").jqxGrid("getselectedrowindexes");
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				}
				var listProd = [];
				for (var i = 0; i < rowindexes.length; i++) {
					var dataItem = $("#wn_prod_productGrid").jqxGrid("getrowdata", rowindexes[i]);
					if (dataItem) {
						if (typeof(dataItem) != "undefined" && typeof(dataItem.productId) != "undefined") {
							listProd.push(dataItem.productId);
						}
					}
				}
				
				if (listProd.length > 0) {
					processAction(listProd);
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				}
			});
		};
		var processAction = function(listProd){
			var dataMap = {
				"productCategoryId": $("#wn_prod_productCategoryId").val(), 
				"productIds": listProd
			}; <#--"${productCategory?if_exists.productCategoryId?if_exists}",-->
			
			$.ajax({
				type: "POST",
				url: "addProductsToCategoryAjax",
				data: dataMap,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
						$("#container").empty();
						$("#jqxNotification").jqxNotification({ template: "info"});
						$("#jqxNotification").html(errorMessage);
						$("#jqxNotification").jqxNotification("open");
						return false;
					}, function(){
						var listEventMsg = data._EVENT_MESSAGE_LIST_;
						if (listEventMsg != null && listEventMsg.length > 0) {
							productIdsNotSuccess = data.productIdsNotSuccess;
							
							var responseMessage = listEventMsg[0];
							if (listEventMsg.length > 1) {
								responseMessage += "<ol>";
								for (var i = 1; i < listEventMsg.length; i++) {
					        		responseMessage += "<li>" + listEventMsg[i] + '&nbsp;&nbsp;&nbsp;<a href="viewProduct?productId=' + productIdsNotSuccess[i-1] + '" target="_blank">Xem san pham</a>' + "</li>";
					        	}
					        	responseMessage += "</ol>";
					        	responseMessage += '<a href="javascript:void(0);" onClick="OlbProdCategoryAddProduct.openToEditProduct();">Mo tat ca san pham trong tab moi</a>';
							}
				        	
				        	$("#alterpopupWindowAddProduct").jqxWindow("close");
							$("#jqxgridProducts").jqxGrid("updatebounddata");
							productGRID.updateBoundData();
							
							bootbox.dialog(responseMessage, [
				                {"label": "${uiLabelMap.CommonCancel}", "icon": 'fa fa-remove', "class": 'btn btn-danger form-action-button pull-right',
						            "callback": function() {bootbox.hideAll();}
						        }, 
						        {"label": "${uiLabelMap.BSTryAgain}", "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
						            "callback": function(){
						            	processAction(productIdsNotSuccess);
						            }
								}
						    ]);
						} else {
							$("#container").empty();
							$("#jqxNotification").jqxNotification({ template: "info"});
							$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
							$("#jqxNotification").jqxNotification("open");
							
							$("#alterpopupWindowAddProduct").jqxWindow("close");
							$("#jqxgridProducts").jqxGrid("updatebounddata");
							productGRID.updateBoundData();
						}
					});
				},
				error: function(data){
					alert("Send request is error");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		};
		var openToEditProduct = function(){
			if (productIdsNotSuccess != null && productIdsNotSuccess.length > 0) {
				for (var i = 0; i < productIdsNotSuccess.length; i++) {
					window.open('viewProduct?productId=' + productIdsNotSuccess[i], '_blank');
				}
			}
		};
		var openWindow = function(){
			$("#alterpopupWindowAddProduct").jqxWindow("open");
		};
		return {
			init: init,
			openWindow: openWindow,
			openToEditProduct: openToEditProduct,
		};
	}());
</script>