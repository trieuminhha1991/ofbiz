<#--TODO DELETE-->

<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script src="/salesmtlresources/js/agreement/agreementDetail.js"></script>
<style>
	.form-window-content-custom label {
	    margin-top: -4px;
	}
	.text-header {
		color: black !important;
	}
	.accordion-inner .row-fluid {
		border-bottom: 1px solid #ccc;
	}
	.accordion-inner {
		background: rgba(235, 235, 235, 0.5);
	}
	.boder-all-profile .label {
	    font-size: 14px;
	    text-shadow: none;
	    background-color: #3a87ad !important;
		margin: 0px;
		color: white !important;
    	line-height: 14px !important;
		margin-top: -20px;
	}
</style>

<#assign distributorTerm = delegator.findOne("TermTypeAttr", {"termTypeId" : "DISTRIBUTOR_TERM_TEXT", "attrName": "defaultValue"}, true)>
<#assign agentTerm = delegator.findOne("TermTypeAttr", {"termTypeId" : "AGENT_TERM_TEXT", "attrName": "defaultValue"}, true)>

<div style="text-align:right">
	<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_EDIT", "")>
	<a href="AddAgreement?sub=${parameters.sub?if_exists}&agreementId=${parameters.agreementId?if_exists}" style="margin-right: 7px;font-size: 20px;" data-rel="tooltip" title="" data-placement="left" class="button-action" data-original-title="${uiLabelMap.BSEdit}">
		<i class="icon-edit open-sans"></i>
	</a>
	</#if>
	<a href="agreement.pdf?agreementId=${parameters.agreementId?if_exists}" style="margin-right: 7px;font-size: 20px;" data-rel="tooltip" title="" data-placement="left" class="button-action" data-original-title="${uiLabelMap.BSPrint}">
		<i class="fa-print open-sans"></i>
	</a>
</div>
<div class="form-horizontal form-window-content-custom label-text-left content-description">
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsAgreementInfo}</span>
			<span id="statusId" class="pull-right label label-info label-large arrowed-in-right arrowed"></span>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top10" style="height: 60px;">
						<div class="span5">
							<label class="text-right">${uiLabelMap.DmsAgreementCode}:</label>
						</div>
						<div class="span7">
							<span id="agreementCode"></span>
						</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span5'>
							<label class="text-right">${uiLabelMap.AgreementDate}:</label>
						</div>
						<div class='span7'>
							<span id="divAgreementDate"></span>
						</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span5'>
							<label class="text-right">${uiLabelMap.DmsFromDate}:</label>
						</div>
						<div class='span7'>
							<span id="divFromDate"></span>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-top10 style="height: 60px;">
						<div class="span5">
							<label class="text-right">${uiLabelMap.DmsDescription}:</label>
						</div>
						<div class="span7">
							<span id="tarDescription"></span>
						</div>
					</div>
					<div class="row-fluid margin-top10" style="height: 60px;"><div class="span5 align-right"></div><div class="span7"></div></div>
					<div class='row-fluid margin-top10'>
						<div class='span5'>
							<label class="text-right">${uiLabelMap.DmsThruDate}:</label>
						</div>
						<div class='span7'>
							<span id="divThruDate"></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsPartyA}</span>
			<div class="accordion-group">
				<div class="accordion-heading">
					<div class="row-fluid margin-top10">
						<div class="span6">
							<div class="row-fluid margin-top10">
								<div class="span5">
									<label class="text-right">${uiLabelMap.CommonDepartment}:</label>
								</div>
								<div class="span7">
									<span id="divPartyTo"></span>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="row-fluid margin-top10">
								<div class="span5">
									<label class="text-right">${uiLabelMap.DmsPersonRepresent}:</label>
								</div>
								<div class="span7">
									<span id="divFindRepresentTo"></span>
								</div>
							</div>
						</div>
					</div>
					<a href="#collapsePartyA" data-toggle="collapse" class="accordion-toggle">${uiLabelMap.DmsDetailInformation}</a>
				</div>
				<div class="accordion-body collapse in" id="collapsePartyA">
					<div class="accordion-inner">
						<div class="row-fluid margin-top10">
							<div class="span6">
								<div class="row-fluid margin-top10" style="height: 50px;">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsAddress}:</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divAAddress"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsTelecom}:</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divATelecom"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsAccountNumber}:</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divAAccountNumber"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.BSBusinessRegistration}</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divADKKD"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.BSRepresentedBy}:</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divARepresentedBy"></div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div class="row-fluid margin-top10" style="height: 50px;"><div class="span5 align-right"></div><div class="span7"></div></div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsFaxNumber}:</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divAFax"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10"><div class="span5 align-right"></div><div class="span7"></div></div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsTaxCode}:</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divATaxCode"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10"><div class="span5 align-right"></div><div class="span7"></div></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsPartyB}</span>
			
			<div class="accordion-group">
	        	<div class="accordion-heading">
	        		<div class="row-fluid margin-top10">
						<div class="span6">
							<div class="row-fluid margin-top10">
								<div class="span5">
									<label class="text-right">${uiLabelMap.BSPartner}:</label>
								</div>
								<div class="span7">
									<span id="divPartyFrom"></span>
								</div>
							</div>
						</div>
	        		</div>
	        		<a href="#collapsePartyB" data-toggle="collapse" class="accordion-toggle">${uiLabelMap.DmsDetailInformation}</a>
	        	</div>
	    	<div class="accordion-body collapse in" id="collapsePartyB">
	        	<div class="accordion-inner">
	            	<div class="row-fluid margin-top10">
						<div class="span6">
		    				<div class="row-fluid margin-top10" style="height: 50px;">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsAddress}:</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBAddress"></div>
								</div>
							</div>
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsTelecom}:</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBTelecom"></div>
								</div>
							</div>
							<#--
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsAccountNumber}:</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBAccountNumber"></div>
								</div>
							</div>
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.BSBusinessRegistration}</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBDKKD"></div>
								</div>
							</div>
							-->
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.BSRepresentedBy}:</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBRepresentedBy"></div>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="row-fluid margin-top10" style="height: 50px;"><div class="span5 align-right"></div><div class="span7"></div></div>
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsFaxNumber}:</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBFax"></div>
								</div>
							</div>
							<#--<div class="row-fluid margin-top10"><div class="span5 align-right"></div><div class="span7"></div></div>-->
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsTaxCode}:</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBTaxCode"></div>
								</div>
							</div>
							<div class="row-fluid margin-top10"><div class="span5 align-right"></div><div class="span7"></div></div>
						</div>
					</div>
	        	</div>
	    	</div>
		</div>
	</div>

	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
		<span class="text-header">${uiLabelMap.DmsTerms}</span>
			<div class="accordion-group">
				<div class="accordion-heading">
					<a href="#collapseThree" data-parent="#accordion2" data-toggle="collapse" class="accordion-toggle collapsed">${uiLabelMap.DmsListTerms}</a>
				</div>
				<div class="accordion-body collapse" id="collapseThree">
					<div class="accordion-inner">
						<div class="row-fluid margin-top10">
							<div id="agent">
								<div style="text-align:right">
									<a href="AgreementTermDetail?termTypeId=AGENT_TERM_TEXT" style="margin-right: 7px;font-size: 20px;" data-rel="tooltip" title="" data-placement="left" class="button-action" data-original-title="${uiLabelMap.BSEdit}">
										<i class="icon-edit open-sans"></i>
									</a>
								</div>
								<div class="accordion-bg">
									${StringUtil.wrapString((agentTerm.attrValue)?if_exists)}
								</div>
							</div>
							<div id="distributor" class="hide">
								<div style="text-align:right">
									<a href="AgreementTermDetail?termTypeId=DISTRIBUTOR_TERM_TEXT" style="margin-right: 7px;font-size: 20px;" data-rel="tooltip" title="" data-placement="left" class="button-action" data-original-title="${uiLabelMap.BSEdit}">
										<i class="icon-edit open-sans"></i>
									</a>
								</div>
								<div class="accordion-bg">
									${StringUtil.wrapString((distributorTerm.attrValue)?if_exists)}
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<#if hasOlbPermission("MODULE", "SALES_AGREEMENT_APPROVE", "")>
		<div class="row-fluid hide" id="accept-wrapper">
			<div class="span12 margin-top10">
				<button id="btnAccept" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.DAAccept}</button>
			</div>
		</div>
	</#if>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested"></div>
</div>

<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />
<script>
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
	"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	<#if parameters.agreementId?exists>
		var agreementIdParam = "${parameters.agreementId?if_exists}";
		$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSAgreementDetail)}");
		$('[data-rel=tooltip]').tooltip();
	</#if>
</script>