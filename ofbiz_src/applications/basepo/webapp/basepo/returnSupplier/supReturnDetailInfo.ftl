<#include "script/viewReturnSupplierTotalScript.ftl"/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">			
<div style="position:relative"><!-- class="widget-body"-->
	<div class="title-status" id="statusTitle">
		<#list listStatusItemReturnHeader as listStatus>
			<#if returnHeader.statusId == listStatus.statusId>
				${StringUtil.wrapString(listStatus.get("description", locale)?if_exists)}
				<#break>
			</#if>
		</#list>
	</div>
	<div><!--class="widget-main"-->
		<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
			${uiLabelMap.BPOReturnPO}
		</h3>
		<div class="row-fluid">
			<div class="form-horizontal form-window-content-custom span-text-left content-description">
				<div class="row-fluid margin-top20">
					<div class="span6">
						<div class="row-fluid">
							<div class="span5">
								<span>${uiLabelMap.POReturnId}:</span>
							</div>
							<div class="span7 align-left">
								<span>${returnId?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span5">
								<span>${uiLabelMap.POSupplier}:</span>
							</div>
							<div class="span7 align-left">
								<#assign supplier = delegator.findOne("PartyGroup", {"partyId" : returnHeader.toPartyId?if_exists}, false) />
								<span>${supplier.groupName?if_exists}</span>
								<input type="hidden" id="supplierId" value="${returnHeader.toPartyId?if_exists}"/>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="row-fluid">
							<div class="span5">
								<span>${uiLabelMap.POReturnDate}:</span>
							</div>
							<div class="span7 align-left">
								<#assign entryDate = Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(returnHeader.entryDate?if_exists, "dd/MM/yyyy - HH:mm:ss", locale, timeZone) />
								<span>${entryDate?if_exists}</span>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span5">
								<span>${uiLabelMap.Description}:</span>
							</div>
							<div class="span7 align-left">
								<span>${returnHeader.description?if_exists}</span>
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
</div>