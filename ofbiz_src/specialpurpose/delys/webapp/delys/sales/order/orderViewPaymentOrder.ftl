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
					<div class="name">
						<a href="${paymentOrder.objectInfo?if_exists}" target="_blank">${paymentOrder.dataResourceName?if_exists}</a>
					</div>
					<div class="text">
						<i class="icon-quote-left"></i>
						<#assign personAttachPaymentOrder = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", paymentOrder.createdByUserLogin, "compareDate", paymentOrder.createdDate, "userLogin", userLogin))/>
						${uiLabelMap.DAPersonCreate}: ${personAttachPaymentOrder.fullName?if_exists} [${paymentOrder.createdByUserLogin}]
						<div class="time" class="pull-right" style="display:inline-block; float:right; margin-right:50px">
							<i class="icon-time"></i>
							<span class="green">${paymentOrder.createdDate?string("yyyy-MM-dd HH:mm:ss.SSS")}</span>
						</div>
					</div>
				</div>

				<div class="tools">
					<#if !(currentStatus.statusId?has_content && currentStatus.statusId == "ORDER_NPPAPPROVED")>
						<input type="hidden" name="dataResourceId_btn_${paymentOrder_index}" value="${paymentOrder.dataResourceId?if_exists}"/>
						<input type="hidden" name="orderId_btn_${paymentOrder_index}" value="${parameters.orderId?if_exists}"/>
						<a href="javascript:void(0);" id="btn_${paymentOrder_index}" class="btn btn-minier btn-danger" onClick="removePaymentOrder('btn_${paymentOrder_index}')">
							<i class="icon-only icon-trash"></i>
						</a>
					</#if>
				</div>
			</div>
		</#list>
	</div><!--.comments-->
<#else>
	${uiLabelMap.DANotFile}
</#if>
