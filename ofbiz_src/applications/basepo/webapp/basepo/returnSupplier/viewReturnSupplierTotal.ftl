<#include "script/viewReturnSupplierTotalScript.ftl"/>
<div style="position:relative"><!-- class="widget-body"-->
	<div class="title-status" id="statusTitle">
		<#list listStatusItemReturnHeader as listStatus>
			<#if returnHeader.statusId == listStatus.statusId>
				${StringUtil.wrapString(listStatus.get("description", locale)?if_exists)}
			</#if>
		</#list>
	</div>
	<div><!--class="widget-main"-->
		<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
			${uiLabelMap.BPOReturnPO}
		</h3>
		<div class="row-fluid">
			<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
				<div class="row-fluid" style="margin-top: 30px;">
					<div class="span6">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.POReturnId}:</label>
							</div>
							<div class="div-inline-block">
								<span><i>${returnId?if_exists}</i></span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.POReturnDate}:</label>
							</div>
							<div class="div-inline-block">
								<span><i>${returnHeader.entryDate?if_exists}</i></span>
							</div>
						</div>
						<div class="row-fluid" <#if !hasOlbPermission("MODULE", "RETURNPO_APPROVE", "APPROVE") || returnHeader.statusId != "SUP_RETURN_REQUESTED">style="display: none;"</#if>>
							<div class="span4 div-inline-block">
								<label>${uiLabelMap.POOrderId}:</label>
							</div>
							<div class="span8 div-inline-block">
								<div class="">
									<div id="orderHeaderBtn" style="width: 100%;">
										<div style="border-color: transparent;" id="orderHeaderGrid"></div>
									</div>
								</div>
							</div>
							<input type="hidden" id="orderHeaderId"/>
							<input type="hidden" id="returnId" value="${returnId?if_exists}"/>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.POSupplier}:</label>
							</div>
							<div class="div-inline-block">
								<#assign supplier = delegator.findOne("PartyGroup", {"partyId" : returnHeader.toPartyId?if_exists}, false) />
								<span><i>${supplier.groupName?if_exists}</i></span>
								<input type="hidden" id="supplierId" value="${returnHeader.toPartyId?if_exists}"/>
							</div>
						</div>
						
						<div class="row-fluid">
							<div class="div-inline-block">
								<label>${uiLabelMap.BPOCurrencyUomId}:</label>
							</div>
							<div class="div-inline-block">
								<span><i>${returnHeader.currencyUomId?if_exists}</i></span>
							</div>
						</div>
						
						<div class="row-fluid">
							<div class="span10"></div>
							<div class="span2" style="height:34px; text-align:right">
								<#if hasOlbPermission("MODULE", "RETURNPO_APPROVE", "APPROVE") && returnHeader.statusId == "SUP_RETURN_REQUESTED" && checkEmpty == false>
									<a data-rel="tooltip" id="acceptedReturn" style="cursor: pointer;"
										title="${uiLabelMap.BootBoxReturnAcceptedConfirm}" data-placement="left" class="button-action">
										<i class="icon-check-square-o open-sans open-sans"></i>
									</a>
								</#if>
							</div>
						</div>
					</div>
				</div>
			</div><!-- .form-horizontal -->
		</div><!--.row-fluid-->	
	</div><!--.widget-main-->
</div><!--.widget-body-->
<#include "viewReturnSupplierItem.ftl"/>
<script type="text/javascript" src="/poresources/js/returnSupplier/viewReturnSupplierTotal.js"></script>
<script>
	<#if hasOlbPermission("MODULE", "RETURNPO_APPROVE", "APPROVE")>
	$("#acceptedReturn").on("click", function(){
		var returnId = $("#returnId").val();
		bootbox.dialog( "${uiLabelMap.BootBoxReturnAcceptedConfirm}?" ,
			[{"label": "${uiLabelMap.wgcancel}", 
				"icon": "fa fa-remove", "class": "btn  btn-danger form-action-button pull-right",
				"callback": function() {bootbox.hideAll();}
			}, 
			{"label": "${uiLabelMap.wgok}",
				"icon": "fa-check", "class": "btn btn-primary form-action-button pull-right",
				"callback": function() {
					$.ajax({
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						complete: function(){
							$("#loader_page_common").hide();
							location.reload();
						},
						url: "updateReturnSupplier",
						type: "POST",
						data: {returnId: returnId, statusId: "SUP_RETURN_ACCEPTED", needsInventoryReceive: "N"},
						dataType: "json",
						success: function(data) {
							$.ajax({
								url: "sendNotifyFromPOToLogReturn",
								type: "POST",
								data: {returnId: returnId},
								success: function(data) {
									
								}
							});
						}
					});
	            }
	        }]);
	});
	</#if>
</script>