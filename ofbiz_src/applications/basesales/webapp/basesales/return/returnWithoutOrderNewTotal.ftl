<script type="text/javascript">
	<#assign productStoresBySeller = Static['com.olbius.basesales.product.ProductStoreWorker'].getListProductStoreSell(delegator, userLogin)!/>
	var productStoreData = [
	<#if productStoresBySeller?exists>
		<#list productStoresBySeller as productStore>
			{	storeName : "${productStore.storeName?default('')}",
				productStoreId : "${productStore.productStoreId}"
			},
		</#list>
	</#if>
	];
</script>

<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent">
    </div>
</div>

<div class="row-fluid">
	<div>
		<div>
			<#include "returnWithoutOrderNewInfo.ftl"/>
		</div>
		<div class="form-window-content-custom">
			<#include "returnWithoutOrderNewItemsProd.ftl"/>
		</div>
	</div>
	
	<div class="pull-right margin-top10">
		<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BSResetEdit}</button>
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.core.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.util.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.grid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.combobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.validator.js"></script>

<script type="text/javascript">
	$(function(){
		OlbReturnWithoutOrderTotal.init();
	});
	
	var OlbReturnWithoutOrderTotal = (function(){
		var orderListDDB;
		
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.notification.create("#container", "#jqxNotification");
		};
		var initEvent = function(){
			$("#alterCancel").on("click", function(){
				jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSThisActionWillClearAllTypingDataAreYouSure)}", 
					function(){
						location.reload();
					}
				);
			});
			$("#alterSave").on("click", function(){
				if (!OlbReturnWithoutOrderInfo.getValidator().validate()) {
					return false;
				};
				jOlbUtil.confirm.dialog("${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCreate)}", 
					function(){
						finishCreateReturn();
					}
				);
			});
		};
		var finishCreateReturn = function(){
			var dataMap = OlbReturnWithoutOrderInfo.getValue();
			var listProd = OlbReturnAddProductItems.getListProductAll();
			
			if (listProd.length <= 0) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}");
				$("#alterSave").removeClass("disabled");
				$("#alterCancel").removeClass("disabled");
				return false;
			} else {
				dataMap.listProduct = JSON.stringify(listProd);
				$.ajax({
					type: 'POST',
					url: 'createReturnWithoutOrder',
					data: dataMap,
					beforeSend: function(){
						$("#alterSave").addClass("disabled");
						$("#alterCancel").addClass("disabled");
						$("#loader_page_common").show();
					},
					success: function(data){
						jOlbUtil.processResultDataAjax(data, "default", function(data){
							$('#container').empty();
				        	$('#jqxNotification').jqxNotification({ template: 'info'});
				        	$("#jqxNotification").html("${uiLabelMap.wgcreatesuccess}");
				        	$("#jqxNotification").jqxNotification("open");
				        	var returnId = data.returnId;
				        	if (OlbCore.isNotEmpty(returnId)) {
				        		window.location.href = "viewReturnOrder?returnId=" + returnId;
				        	}
						});
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#alterSave").removeClass("disabled");
						$("#alterCancel").removeClass("disabled");
						$("#loader_page_common").hide();
					},
				});
			}
		};
		return {
			init: init
		};
	}());
</script>