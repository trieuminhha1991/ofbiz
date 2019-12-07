<#if orderHeader?exists>
<#--Attach Payment Order-->
<#if hasOlbEntityPermission("SALESORDER", "ATTACH_PAYMENT_VIEW")>
	<#if orderHeader.statusId == "ORDER_NPPAPPROVED">
		<span style="color:#F00; display:block; margin-bottom:20px">${uiLabelMap.BSDistributorHadPaid}</span>
	<#elseif orderHeader.statusId == "ORDER_SADAPPROVED">
		<#if hasOlbEntityPermission("SALESORDER", "ATTACH_PAYMENT_CREATE")>
			<style type="text/css">
				.ace-file-multiple label span [class*="icon-"] {
					line-height: 32px !important;
				    font-size: 32px !important;
				}
			</style>
			<div class="row-fluid">
				<div class="span12">
					<h6 style="margin:0"><b>${uiLabelMap.BSAttachPaymentVouchers}</b></h6>
					<div class="row-fluid">
						<div class="span10">
							<form name="attachPaymentOrder" id="attachPaymentOrder">	
								<#--<input type="file" id="uploadedFile" name="uploadedFile[]" multiple="multiple"/>-->
								<input type="file" id="uploadedFilePaymentOrder" multiple="multiple"/>
							</form>
						</div>
						<div class="span2">
							<button id="btnAttachPaymentOrder" class="btn btn-small btn-primary width100pc">
					 			<i class="fa-floppy-o open-sans"></i> ${uiLabelMap.CommonSave}
					 		</button>
						</div>
					</div>
				</div>
			</div>
			<@jqOlbCoreLib />
			<script src="/aceadmin/assets/js/jquery.inputlimiter.1.3.1.min.js"></script>
			<script src="/aceadmin/assets/js/jquery.maskedinput.min.js"></script>
			<script type="text/javascript">
				$(function(){
					$('#uploadedFilePaymentOrder').ace_file_input({
						style:'well',
						btn_choose: '${StringUtil.wrapString(uiLabelMap.DropFilesHereOrClickToChoose)}',
						btn_change: null,
						no_icon: 'icon-cloud-upload',
						droppable: true,
						thumbnail: 'small',
						preview_error : function(filename, error_code) {
							//name of the file that failed
							//error_code values
							//1 = 'FILE_LOAD_FAILED',
							//2 = 'IMAGE_LOAD_FAILED',
							//3 = 'THUMBNAIL_FAILED'
							//alert(error_code);
						}
					});
					
					$("#btnAttachPaymentOrder").on("click", function(){
						var fileList = $('#uploadedFilePaymentOrder').data('ace_input_files');
						if (fileList != undefined && fileList != null && fileList.length > 0) {
							var formData = new FormData();
							formData.append("orderId", "${orderHeader.orderId}");
							for (var i = 0; i < fileList.length; i++) {
								formData.append("uploadedFile_o_" + i, fileList[i]);
							};
							$.ajax({
								type: 'POST',
								url: 'attachFilesPaymentOrder',
								data: formData,
								cache : false,
								contentType : false,
								processData : false,
								beforeSend: function(){
									$("#info_loader").show();
								},
								success: function(data){
									jOlbUtil.processResultDataAjax(data, "default", "default", function(){
							    		$("#paymentOrderContainer").html(data);
									});
								},
								error: function(data){
									alert("Send request is error");
								},
								complete: function(data){
									$("#info_loader").hide();
								},
							});
						} else {
							jOlbUtil.alert.error("You haven't choose file yet!");
						}
					});
				});
			</script>
		</#if>
	</#if>

	<#assign paymentOrderList = delegator.findList("OrderContentPaymentDataResource", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", orderHeader.orderId), Static["org.ofbiz.entity.condition.EntityOperator"].AND, Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()), null, null, null, false)!>
	<h6><b>${uiLabelMap.BSListPaymentVouchers} (<#if paymentOrderList?exists>${paymentOrderList?size}<#else>0</#if>)</b></h6>
	<div class="span12 no-left-margin" style="margin-bottom:20px">
		<#if paymentOrderList?has_content>
			<div style="overflow: auto; width: auto; height:auto; max-height: 200px;overflow-y: scroll;">
				<#list paymentOrderList as paymentOrder>
					<div class="itemdiv commentdiv">
						<div class="user">
							<a href="${paymentOrder.objectInfo?if_exists}" target="_blank" style="max-width:42px; max-height:42px">
								<img alt="${paymentOrder.dataResourceName?if_exists}" src="${paymentOrder.objectInfo?if_exists}" style="max-width:42px; max-height:42px" />
							</a>
						</div>

						<div class="body">
							<div class="name" style="min-height:20px">
								<a href="${paymentOrder.objectInfo?if_exists}" target="_blank">${paymentOrder.dataResourceName?if_exists}</a>
							</div>
							<div class="text">
								<i class="icon-quote-left"></i>
								${uiLabelMap.BSPersonCreate}: [${paymentOrder.createdByUserLogin}]
								<div class="time" class="pull-right" style="display:inline-block; float:right; margin-right:50px">
									<i class="icon-time"></i>
									<span class="green">${paymentOrder.createdDate?string("yyyy-MM-dd HH:mm:ss.SSS")}</span>
								</div>
							</div>
						</div>

						<div class="tools">
							<#if orderHeader.statusId == "ORDER_SADAPPROVED" && hasOlbEntityPermission("SALESORDER", "ATTACH_PAYMENT_DELETE")>
								<a href="javascript:void(0);" class="btn btn-minier btn-danger" 
										onClick="removePaymentOrder('${paymentOrder.orderId}', '${paymentOrder.contentId}', '${paymentOrder.orderContentTypeId}', '${paymentOrder.fromDate}')">
									<i class="icon-only icon-trash"></i>
								</a>
								<script type="text/javascript">
									function removePaymentOrder(orderId, contentId, typeId, fromDate) {
										var dataMap = {
											"orderId": orderId,
											"contentId": contentId,
											"orderContentTypeId": typeId,
											"fromDate": fromDate
										};
										$.ajax({
								            type: "POST",                        
								            url: "removePaymentOrderAjax",
								            data: dataMap,
								            beforeSend: function () {
												$("#info_loader").show();
											}, 
								            success: function (data) {
								            	jOlbUtil.processResultDataAjax(data, "default", "default", function(){
										    		$("#paymentOrderContainer").html(data);
												});
								            },
								            error: function () {
								                //commit(false);
								            },
								            complete: function() {
										        $("#info_loader").hide();
										    }
								        });
									}
								</script>
							</#if>
						</div>
					</div>
				</#list>
			</div><!--.comments-->
		<#else>
			${uiLabelMap.BSNotFile}
		</#if>
	</div><!--.span12-->
	<div style="clear:both; margin-bottom: 20px"></div>
</#if>
</#if>