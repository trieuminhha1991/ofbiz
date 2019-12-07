<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script>
	var prev = document.referrer;
	if(prev.indexOf("newSalesOrder")){
		localStorage.setItem("createOrderSuccess", true);
	}
</script>

<div style="position:relative">
	<div id="loader_page_common_loading" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DAAdding}...</span>
			</div>
		</div>
	</div>
</div>

<#include "viewOrderDetailOpenScript.ftl"/>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
					<div class="span7">
						<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if orderHeader?has_content>
										<li<#if !activeTab?exists || activeTab == "" || activeTab == "orderoverview-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#orderoverview-tab">${uiLabelMap.DAOverview}</a>
										</li>
										<#if !security.hasPermission("DELIVERY_VIEW", session) || security.hasPermission("ACC_PORD_PMN_VIEW", session)>
										<li<#if activeTab?exists && activeTab == "orderinfo-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.DAOrderGeneralInfo}</a>
										</li>
										
										<li<#if activeTab?exists && activeTab == "payment-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#payment-tab">${uiLabelMap.DAPayment}</a>
										</li>
										</#if>
										
										<#if security.hasPermission("DELIVERY_VIEW", session)>
											<li<#if activeTab?exists && activeTab == "deliveries-tab"> class="active"</#if>>
												<a data-toggle="tab" href="#deliveries-tab">${uiLabelMap.ReceiveNote}</a>
											</li>
										</#if>
										<#if !security.hasPermission("DELIVERY_VIEW", session) || security.hasPermission("ACC_PORD_PMN_VIEW", session)>
										<li<#if activeTab?exists && activeTab == "items-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#items-tab">${uiLabelMap.BSOrderItem}</a>
										</li>
										</#if>
									</#if>
									<#if inProcess?exists>
										<li>
											<a data-toggle="tab" href="#transitions-tab">${uiLabelMap.OrderProcessingTransitions}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span5" style="height:34px; text-align:right">
								<#--<#if orderHeader?has_content>
									<#if userLogin.userLoginId == orderHeader.createdBy>
							    		<a id="confrimId" title="${uiLabelMap.CommonApprove}" class="button-action"><i class="fa fa-check"></i></a>
							    	 </#if>
							    </#if>-->
							    <#--
								<#assign orderType = orderHeader.getRelatedOne("OrderType", false)/>
								<#if currentStatus.statusId != "ORDER_COMPLETED" && currentStatus.statusId != "ORDER_CANCELLED" && currentStatus.statusId != "ORDER_IN_TRANSIT" >
									<a href="javascript:changeOrderStatus('CANCEL')" data-rel="tooltip" title="${uiLabelMap.DACancel}" data-placement="bottom" class="button-action"><i class="fa fa-times-circle-o"></i></a>
					              	<form name="OrderCancel" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
						                <input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
						                <input type="hidden" name="setItemStatus" value="Y"/>
						                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
						                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
						                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
						                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
						                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
						                <input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						                <input type="hidden" name="changeReason" value="" />
					              	</form>
					            </#if>
					            -->
					            <#if orderHeader?has_content>
							    	<#if hasOlbPermission("MODULE", "PURCHASEORDER_APPROVE", "APPROVE") && (statusId =="ORDER_APPROVED" || statusId =="ORDER_COMPLETED")>
							    		<#assign acceptFile="image/*"/>
							    		<#assign entityName="OrderHeader"/>
							    		<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>
							    		<a href="javascript:Uploader.open({orderId: '${orderHeader.orderId?if_exists}'})" data-rel="tooltip" title="${uiLabelMap.UploadFileAttachment}" data-placement="bottom" class="button-action button-size"><i class="fa fa-upload"></i></a>
										<#if orderHeader.contentId?has_content>
											<a href="javascript:Viewer.open({orderId: '${orderHeader.orderId?if_exists}'})" data-rel="tooltip" title="${uiLabelMap.ViewFileAttachment}" data-placement="bottom" class="button-action button-size"><i class="fa fa-file-image-o"></i></a>
										</#if>
							    	
									</#if>
								</#if>
					            <#if hasOlbPermission("MODULE", "PURCHASEORDER_APPROVE", "APPROVE") && statusId =="ORDER_APPROVED" && soAssoc?exists>
									<a data-rel="tooltip" id="completeOrder"
						    			title="${uiLabelMap.DmsCompleteOrder}" data-placement="left" class="button-action button-size">
						    			<i class="icon-check-square-o open-sans open-sans"></i>
						    		</a>
								</#if>
								<#if security.hasPermission("ACC_POAPPROVED_ADMIN",session) && statusId =="ORDER_CREATED">
									<a data-rel="tooltip" id="updateSttOrder" 
						    			title="${uiLabelMap.Approved}" data-placement="left" class="button-action button-size">
						    			<i class="fa fa-check-circle-o"></i>
						    		</a>
								</#if>
					            <#if orderHeader?has_content>
						            <#if (hasOlbPermission("MODULE", "PURCHASEORDER_APPROVE", "APPROVE") || security.hasPermission("ACC_POAPPROVED_ADMIN",session)) && statusId !="ORDER_CANCELLED">
							            <a data-rel="tooltip"
							    			title="${uiLabelMap.POExportPDF}" data-placement="left" class="button-action button-size" id="expPdf">
							    			<i class="fa fa-file-pdf-o open-sans"></i>
							    		</a>	
						            	<a href="<@ofbizUrl>exportPurchaseOrderToExcel?${paramString}</@ofbizUrl>" data-rel="tooltip"
							    			title="${uiLabelMap.POExportExcel}" data-placement="left" class="button-action button-size">
							    			<i class="fa fa-file-excel-o open-sans"></i>
							    		</a>
							    	</#if>
						    		<#if orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" &&  orderHeader.statusId != "ORDER_IN_TRANSIT">
							    		<#if hasOlbPermission("MODULE", "PURCHASEORDER_EDIT", "UPDATE")>
								    		<a href="javascript:checkAndDirectEdit(${orderId})" data-rel="tooltip"  
								    			title="${uiLabelMap.DAEdit}" data-placement="left" class="button-action button-size">
								    			<i class="icon-edit open-sans"></i>
								    		</a>
							    		</#if>
							    	</#if>
							    	<#if hasOlbPermission("MODULE", "PURCHASEORDER_EDIT", "UPDATE")>
							    		<a data-rel="tooltip" id="copyOrder" 
							    			title="${uiLabelMap.CommonCopy}" data-placement="left" class="button-action button-size">
							    			<i class="fa fa-files-o"></i>
							    		</a>
						    		</#if>
							    	<#if hasOlbPermission("MODULE", "PURCHASEORDER_APPROVE", "APPROVE") && statusId !="ORDER_COMPLETED" && statusId !="ORDER_CANCELLED">
							    		<a data-rel="tooltip" id="cancelOrder"
							    			title="${uiLabelMap.DACancel}" data-placement="left" class="button-action red button-size">
							    			<i class="icon-trash"></i>
							    		</a>
							    	</#if>
							    	<#if checkReceived?has_content && checkReceived == true && orderHeader.statusId == "ORDER_APPROVED" && (hasOlbPermission("MODULE", "PURCHASEORDER", "ADMIN") || security.hasPermission("ACC_POAPPROVED_ADMIN",session))>
										<a data-rel="tooltip" id="completeOrderWithReceived" 
							    			title="${uiLabelMap.BPCompleteOrderWithReceivedNumber}" data-placement="left" class="button-action button-size">
							    			<i class="icon-check-square-o open-sans open-sans"></i>
							    		</a>
									</#if>
								</#if>
						</div>
					</div>
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
					</script>
					<style type="text/css">
						.button-action {
							font-size:18px; padding:0 0 0 8px;
						}
					</style>
				</div>
			</div>
			
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
<script>
	var btnClick = false;
	$("#expPdf").on("click", function(){
		alert('pdf');
		window.open("exportPurchaseOrderToPDF?orderId=${orderId}", "_blank");
	});
	
	$("#copyOrder").on("click", function(){
		window.open("newPurchaseOrder?copyOrderId=${orderId}", "_blank");
	});
	
	$("#completeOrderWithReceived").on("click", function(){
		bootbox.dialog("${uiLabelMap.AreYouSureExecuted}", 
		[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll(); btnClick = false;}
        }, 
        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	if (!btnClick){
					var orderId = "${orderId}";
					var statusId = "ORDER_CANCELLED";
					var setItemStatus = "Y";
					$.ajax({
						url: "autoCancelOrderItemRemaining",
						type: "POST",
						data: {orderId: ${orderId}},
						success: function(data) {
							if(data._ERROR_MESSAGE_ != undefined && data._ERROR_MESSAGE_ != null){
							 	jOlbUtil.alert.error(data._ERROR_MESSAGE_);
							 	return false;
						 	}
							location.reload();
						}
					});
					btnClick = true;
				}
            }
        }]);
	});
	
	$("#confrimId").on("click", function(){
		$.ajax({
			url: "confirmOlbiusOrder",
			type: "POST",
			data: {orderId: ${orderId}},
			success: function(data) {
				location.reload();
			}
		})
	});

	<#if hasOlbPermission("MODULE", "PURCHASEORDER_APPROVE", "APPROVE") && statusId !="ORDER_COMPLETED" && statusId !="ORDER_CANCELLED">
		$("#cancelOrder").on("click", function(){
			bootbox.dialog("${uiLabelMap.DmsBootboxCancelPO}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll(); btnClick = false;}
		        }, 
		        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	if (!btnClick){
							var orderId = "${orderId}";
							var statusId = "ORDER_CANCELLED";
							var setItemStatus = "Y";
							changeOrderStatusCancel(orderId, statusId, setItemStatus);
						}
		            }
		        }]);
		});
	</#if>
	
	function changeOrderStatusCancel(orderId, statusId, setItemStatus){
		$.ajax({
			url: "changeOrderStatusPOCustom",
			type: "POST",
			data: {orderId: orderId, statusId: statusId, setItemStatus: setItemStatus},
			success: function(data) {
			 	if(data._ERROR_MESSAGE_ != undefined && data._ERROR_MESSAGE_ != null){
				 	jOlbUtil.alert.error(data._ERROR_MESSAGE_);
				 	return false;
			 	}
				location.reload();
			}
		})
	}
	function changeOrderStatus(orderId, statusId, setItemStatus, shipByDate, shipAfterDateData){
		$.ajax({
			beforeSend: function(){
                $("#loader_page_common_loading").show();
            },
            complete: function(){
            	$("#loader_page_common_loading").hide();
            	location.reload();
            },
			url: "changeOrderStatusPOCustom",
			type: "POST",
			data: {orderId: orderId, statusId: statusId, setItemStatus: setItemStatus, shipByDate: shipByDate, shipAfterDate: shipAfterDateData},
			success: function(data) {
				
			}
		});
	}

	<#if hasOlbPermission("MODULE", "PURCHASEORDER_APPROVE", "APPROVE") && statusId =="ORDER_APPROVED" && soAssoc?exists>
	$("#completeOrder").on("click", function(){
		bootbox.dialog("${uiLabelMap.DmsBootboxCompletePO}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll(); btnClick = false;}
		        }, 
		        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	if (!btnClick) {
			            	var orderId = "${orderId}";
							completeOrder(orderId);
						}
		            }
		        }]);
	});
	
	function completeOrder(orderId){
		$.ajax({
			beforeSend: function(){
                $("#loader_page_common_loading").show();
            },
            complete: function(){
            	$("#loader_page_common_loading").hide();
            	location.reload();
            },
	 	     url: "changeOrderStatusCompletePOCustom",
	 	     type: "POST",
	 	     data: {orderId: orderId},
	 	     success: function(res) {
	 	     }
	 	 });
	}
	
</#if>
</script>			