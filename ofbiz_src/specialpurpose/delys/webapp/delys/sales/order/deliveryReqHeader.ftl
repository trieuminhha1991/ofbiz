<div class="widget-box transparent no-bottom-border" id="screenlet_1">
	<div class="widget-header">
		<h4>${uiLabelMap.DAViewDeliveryProposal}
		<#if deliveryReq.requirementId?exists>
			(<a href="<@ofbizUrl>viewDeliveryProposal?requirementId=${deliveryReq.requirementId}</@ofbizUrl>">${deliveryReq.requirementId}</a>)
		</h4>
		<span class="widget-toolbar none-content">
			<a href="<@ofbizUrl>newDeliveryProposal</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.DACreateNewDeliveryProposal}</i>
			</a>
		</span>
		<#else>
		</h4>
		<span class="widget-toolbar none-content">
			<a href="<@ofbizUrl>newDeliveryProposal</@ofbizUrl>">
				<i class="icon-plus open-sans">${uiLabelMap.DACreateNewDeliveryProposal}</i>
			</a>
		</span>
		</#if>
	</div>
	<div class="widget-body">
		<div id="screenlet_1_col" class="widget-body-inner">
			<div class="row-fluid">
				
			<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DADeliveryProposalId}:</label>
							<div class="controls-desc">
								<b>${deliveryReq.requirementId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAStatus}:</label>
							<div class="controls-desc">
								<#assign currentStatusId = deliveryReq.statusId?if_exists />
								<#if currentStatusId?exists && currentStatusId?has_content>
									<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
									<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
				                </#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DADescription}:</label>
							<div class="controls-desc">
								${deliveryReq.description?if_exists}
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DADeliveryDate}:</label>
							<div class="controls-desc">
								<#if deliveryReq.requirementStartDate?exists>${deliveryReq.requirementStartDate?string("dd/MM/yyyy")}<#else>___</#if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DARequiredByDate}:</label>
							<div class="controls-desc">
								<#if deliveryReq.requiredByDate?exists>${deliveryReq.requiredByDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
							</div>
						</div>
						
					</div><!--.span6-->
				</div><!--.row-->
			</div>
			
			<div style="clear:both"></div>
			<hr/>
			
			<div>
				<#if deliveryReqItems?exists && deliveryReqItems?has_content && deliveryReqItems?size &gt; 0>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th rowspan="2" style="width:10px">${uiLabelMap.DANo}</th>
								<th rowspan="2" class="center">${uiLabelMap.DAOrderId}</th>
								<th rowspan="2" class="center">${uiLabelMap.DADescription}</th>
							</tr>
						</thead>
						<tbody>
							<#list deliveryReqItems as deliveryReqItem>
								<tr>
									<td>${deliveryReqItem_index + 1}</td>
									<td>${deliveryReqItem.orderId?if_exists}</td>
									<td>${deliveryReqItem.description?if_exists}</td>
								</tr>
							</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoQuotationItemsToDisplay}</div>
				</#if>
			</div>
		</div>
	</div>
</div>
