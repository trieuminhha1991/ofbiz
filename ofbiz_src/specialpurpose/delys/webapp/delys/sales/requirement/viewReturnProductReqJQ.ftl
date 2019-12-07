<#if requirement?exists>
<div class="row-fluid">
	<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
		<div class="row margin_left_10 row-desc">
			<div class="span6">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DARequirementId}:</label>
					<div class="controls-desc">
						<span>${requirement.requirementId}</span>
					</div>
				</div>
				<#if requirement.requirementTypeId == "RETURN_PRODDIS_REQ">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DACustomer}:</label>
					<div class="controls-desc">
						<span>${customerStr?if_exists}</span>
					</div>
				</div>
				</#if>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DADistributor}:</label>
					<div class="controls-desc">
						<span>${distributorStr?if_exists}</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DAAddress}:</label>
					<div class="controls-desc">
						<span>${postalAddressStr?if_exists}</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DAStatus}:</label>
					<div class="controls-desc">
						<#if currentStatus?exists>
							<#if currentStatus.description?has_content>${currentStatus.get("description",locale)}</#if>
		                </#if>
					</div>
				</div>
				<#if listStatus?exists>
					<div class="control-group">
						<div class="controls-desc">
		                  	<#list listStatus as reqStatusItem>
		                  		<#assign statusItem = reqStatusItem.statusItem />
			                    <div>
			                      	${statusItem.get("description",locale)} <#if statusItem.statusDate?has_content>- ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(statusItem.statusDate, "", locale, timeZone)?default("0000-00-00 00:00:00")}</#if>
			                      	&nbsp;
			                      	${uiLabelMap.CommonBy} - [${reqStatusItem.statusUserLogin}]
			                    </div>
		                  	</#list>
						</div>
					</div>
				</#if>
			</div><!--.span6-->
			<div class="span6">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DARequirementStartDateOrigin}:</label>
					<div class="controls-desc">
						<span>
							<#if requirement.requirementStartDate?has_content>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(requirement.requirementStartDate, "dd/MM/yyyy - HH:mm:ss:SSS", locale, timeZone)!}
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DARequiredByDateOrigin}:</label>
					<div class="controls-desc">
						<span>
							<#if requirement.requiredByDate?has_content>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(requirement.requiredByDate, "dd/MM/yyyy - HH:mm:ss:SSS", locale, timeZone)!}
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DAReason}:</label>
					<div class="controls-desc">
						<span>
							<#if requirement.reason?exists>
								<#assign reasonReq = delegator.findOne("ReturnReason", {"returnReasonId", requirement.reason}, false)/>
								<#if reasonReq?exists>
									${reasonReq.get("description", locale)}
								<#else>
									${requirement.reason}
								</#if>
							</#if>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DADescription}:</label>
					<div class="controls-desc">
						<span>${requirement.description?if_exists}</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DAOrderSoldRespective}:</label>
					<div class="controls-desc">
						<span>
						<#if orderSoldIds?exists>
							<#list orderSoldIds as orderId>
								<a href="<@ofbizUrl>orderView?orderId=${orderId}</@ofbizUrl>">${orderId}</a><#if orderId_has_next>, </#if>
							</#list>
						<#else>
							____
						</#if>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.DAReturnOrderRespective}:</label>
					<div class="controls-desc">
						<span>
						<#if returnIds?exists>
							<#list returnIds as returnId>
								<a href="<@ofbizUrl>viewReturnOrderGeneral?returnId=${returnId}</@ofbizUrl>">${returnId}</a><#if returnId_has_next>, </#if>
							</#list>
						<#else>
							____
						</#if>
						</span>
					</div>
				</div>
			</div><!--.span6-->
		</div><!--.row-fluid-->
		<div class="row-fluid">
			<div class="span12">
				<#assign dataFieldTwo="[{ name: 'reqItemSeqId', type: 'string' },
			               		{ name: 'productId', type: 'string' },
			               		{ name: 'expireDate', type: 'date', other: 'Timestamp'},
			               		{ name: 'quantityUomId', type: 'string'},
			               		{ name: 'quantity', type: 'number', formatter: 'integer'},
			               		{ name: 'quantityAccepted', type: 'number', formatter: 'integer'}
			                	]"/>
				<#assign columnlistTwo="{ text: '${uiLabelMap.DASeqId}', dataField: 'reqItemSeqId', width: '180px'},
								 { text: '${uiLabelMap.DAProductId}', dataField: 'productId'},
								 { text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', width: '180px', cellsformat: 'dd/MM/yyyy'},
								 { text: '${uiLabelMap.DAQuantityUomId}', dataField: 'quantityUomId', width: '180px'},
								 { text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', width: '180px', cellsalign: 'right'},
								 { text: '${uiLabelMap.DAQuantityAccepted}', dataField: 'quantityAccepted', width: '180px', cellsalign: 'right'}
			              		"/>
				<@jqGrid clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlistTwo dataField=dataFieldTwo
						viewSize="30" showtoolbar="false" editmode="click" filtersimplemode="false" 
						url="jqxGeneralServicer?sname=JQGetListRequirementItem&requirementId=${requirement.requirementId}" 
						mouseRightMenu="true" contextMenuId="contextMenu"/>
			</div>
		</div>
	</div>
</div>
<div id='contextMenu'>
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }
	});
</script>
<#else>
	<div class="alert alert-info">
		<label>${uiLabelMap.DAThisRequirementNotAvaiable}</label>
	</div>
</#if>