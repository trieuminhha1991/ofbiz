<script>
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, false) />
	<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, false) />
	<#assign roleTypes = delegator.findList("RoleType", null, null, null, null, false) />
	<#assign fixedAssets = delegator.findList("FixedAsset", null, null, null, null, false) />
	<#assign glJournals = delegator.findList("GlJournal", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, false) />
</script>
<style>
	.custom-style-word{
			font-size : 10pt;
			font-weight : bold;
			line-height : 20px;
		    color: #037c07;
	}
</style>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.PageTitleEditTransaction}</h4>
	</div>
	<div class='widget-body'>
		<form action="<@ofbizUrl>updateAcctgTrans</@ofbizUrl>" method="post" id="updateAcctgTrans">
			<div class="row-fluid">
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.acctgTransId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="acctgTransId" class="custom-style-word">${acctgTrans.acctgTransId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">
								${uiLabelMap.FormFieldTitle_acctgTransTypeId}
							</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="acctgTransTypeId"  class="custom-style-word">
								<#assign acctgTransTypeId = ""/>
								<#list acctgTransTypes as type>
									<#if acctgTrans.acctgTransTypeId?exists && type.acctgTransTypeId == acctgTrans.acctgTransTypeId>
										<#assign acctgTransTypeId = type.description/><#break>
									</#if>
								</#list>
								<#if acctgTransTypeId =="">
									${acctgTrans.acctgTransTypeId?if_exists}
								<#else>
									${acctgTransTypeId}
								</#if>
							</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.glJournalId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="glJournalId" class="custom-style-word">
								<#assign glJournalId = ""/>
								<#list glJournals as type>
									<#if acctgTrans.glJournalId?exists && type.glJournalId == acctgTrans.glJournalId>
										<#assign glJournalId = type.glJournalName/><#break>
									</#if>
								</#list>
								<#if glJournalId =="">
									${acctgTrans.glJournalId?if_exists}
								<#else>
									${glJournalId}
								</#if>
							</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.accPartyId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="partyId" class="custom-style-word">${acctgTrans.partyId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk" >${uiLabelMap.transactionDate}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="transactionDate" class="custom-style-word">${acctgTrans.transactionDate?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.isPosted}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="isPosted" class="custom-style-word">${acctgTrans.isPosted?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_physicalInventoryId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="physicalInventoryId" class="custom-style-word">${acctgTrans.partyId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_receiptId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="receiptId" class="custom-style-word">${acctgTrans.receiptId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_voucherRef}</label>
						</div>
						<div class="span7 highlight-color">
							<div id="voucherRef" class="custom-style-word">${acctgTrans.voucherRef?if_exists}</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_invoiceId}</label>
						</div>
						<div class="span7 highlight-color">
					       <div id="invoiceId" class="custom-style-word">${acctgTrans.invoiceId?if_exists}</div>
					    </div>
				    </div>
				    <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.accProductId}</label>
						</div>
						<div class="span7 highlight-color" >
					       <span>
					       		<div id="productId" name="productId" class="custom-style-word">${acctgTrans.productId?if_exists}</div>
					       </span>
					    </div>
				    </div>
				    <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_shipmentId}</label>
						</div>
						<div class="span7 highlight-color">
					       <span>
					       		<div id="shipmentId" name="shipmentId" class="custom-style-word">${acctgTrans.shipmentId?if_exists}</div>
					       </span>
					    </div>
				    </div>
				
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.description}</label>
						</div>
						<div class="span7 highlight-color">
							<div id="description" class="custom-style-word">${acctgTrans.description?if_exists}</div>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class="span7 highlight-color">&nbsp;
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.glFiscalTypeId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="glFiscalTypeId" class="custom-style-word">
								<#assign glFiscalTypeId = ""/>
								<#list glFiscalTypes as type>
									<#if glFiscalTypes.glFiscalTypeId?exists && type.glFiscalTypeId == acctgTrans.glFiscalTypeId>
										<#assign glFiscalTypeId = type.glJournalName/><#break>
									</#if>
								</#list>
								<#if glFiscalTypeId =="">
									${acctgTrans.glFiscalTypeId?if_exists}
								<#else>
									${glFiscalTypeId}
								</#if>	
							${acctgTrans.glFiscalTypeId?if_exists}
							</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_groupStatusId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="groupStatusId" class="custom-style-word">${acctgTrans.groupStatusId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.roleTypeId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="roleTypeId" class="custom-style-word">${acctgTrans.roleTypeId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_scheduledPostingDate}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="scheduledPostingDate" class="custom-style-word">${acctgTrans.scheduledPostingDate?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.postedDate}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="postedDate" class="custom-style-word">${acctgTrans.postedDate?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_inventoryItemId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="inventoryItemId" class="custom-style-word">${acctgTrans.inventoryItemId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_theirAcctgTransId}</label>
						</div>  
						<div class="span7 highlight-color">
							<div id="theirAcctgTransId" class="custom-style-word">${acctgTrans.inventoryItemId?if_exists}</div>
				   		</div>		
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_voucherDate}</label>
						</div>
						<div class="span7 highlight-color">
							<div id="voucherDate" class="custom-style-word">${acctgTrans.voucherDate?if_exists}</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
				    	<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.paymentId}</label>
						</div>
						<div class="span7 highlight-color">
							<span>
								<div id="paymentId" class="custom-style-word">${acctgTrans.paymentId?if_exists}</div>
				       		</span>
				       	</div>
			       </div>
			       <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_workEffortId}</label>
						</div>
						<div class="span7 highlight-color">
							<div id="workEffortId" class="custom-style-word">${acctgTrans.workEffortId?if_exists}</div>
				       	</div>
			        </div>
			        <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FixedAssetId}</label>
						</div>
						<div class="span7 highlight-color">
							<div id="fixedAssetId" class="custom-style-word">${acctgTrans.fixedAssetId?if_exists}</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.FormFieldTitle_finAccountTransId}</label>
						</div>
						<div class="span7 highlight-color">
							<div id="finAccountTransId" class="custom-style-word">${acctgTrans.finAccountTransId?if_exists}</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
