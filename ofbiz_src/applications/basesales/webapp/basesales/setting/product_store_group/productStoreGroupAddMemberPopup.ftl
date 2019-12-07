<div id="popupProductStoreGroupAddMember" style="display:none">
	<div>${uiLabelMap.BSPSAddProductStore}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<input type="hidden" id="wn_productStoreGroupId" value="${productStoreGroup?if_exists.productStoreGroupId?if_exists}"/>
					<div id="wn_productStoreGrid"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class="pull-right form-window-content-custom">
				<button id="wn_alterSave" class="btn btn-primary form-action-button"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_alterCancel" class="btn btn-danger form-action-button"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		OlbProdStoreGroupAddMember.init();
	});
	var OlbProdStoreGroupAddMember = (function(){
		var productStoreGRID;
		var listProdStore;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#popupProductStoreGroupAddMember"), {width: 960, maxWidth: 960, height: 540, cancelButton: $("#wn_alterCancel")});
		};
		var initElementComplex = function(){
			var configProductStore = {
				autoshowloadelement: true,
				showdefaultloadelement: true,
				datafields: [
					{name: "productStoreId", type: "string"},
					{name: "storeName", type: "string"},
				],
				columns: [
					{text: "${uiLabelMap.BSProductStoreId}", datafield: "productStoreId", width: '26%'},
					{text: "${uiLabelMap.BSStoreName}", datafield: "storeName", width: '70%'},
				],
				useUrl: true,
				useUtilFunc: true,
				url: "JQGetListProductStoreAddToGroup",
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
					titleProperty: "${StringUtil.wrapString(uiLabelMap.BSListProductStore)}",
				},
			};
			productStoreGRID = new OlbGrid($("#wn_productStoreGrid"), null, configProductStore, []);
		};
		var initEvent = function(){
			$("#wn_alterSave").on("click", function(){
				var rowindexes = $("#wn_productStoreGrid").jqxGrid("getselectedrowindexes");
				if (typeof(rowindexes) == "undefined" || rowindexes.length < 1) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProductStore}");
				}
				var listProdStore = [];
				for (var i = 0; i < rowindexes.length; i++) {
					var dataItem = $("#wn_productStoreGrid").jqxGrid("getrowdata", rowindexes[i]);
					if (dataItem) {
						if (typeof(dataItem) != "undefined" && typeof(dataItem.productStoreId) != "undefined") {
							listProdStore.push(dataItem.productStoreId);
						}
					}
				}
				
				if (listProdStore.length > 0) {
					processAction(listProdStore);
				} else {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProductStore}");
				}
			});
			
			$("#popupProductStoreGroupAddMember").on("open", function(){
				productStoreGRID.updateBoundData();
			});
		};
		var processAction = function(listProdStore){
			var dataMap = {
				"productStoreGroupId": $("#wn_productStoreGroupId").val(), 
				"productStoreIds": listProdStore
			};
			
			$.ajax({
				type: "POST",
				url: "addProductStoresToStoreGroup",
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
						$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
			        	$("#jqxNotification").jqxNotification("open");
			        	closeWindow();
			        	$("#jqxgridStoreMember").jqxGrid('updatebounddata');
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
		var openWindow = function(){
			$("#popupProductStoreGroupAddMember").jqxWindow("open");
		};
		var closeWindow = function(){
			$("#popupProductStoreGroupAddMember").jqxWindow("close");
		};
		return {
			init: init,
			openWindow: openWindow,
		};
	}());
</script>