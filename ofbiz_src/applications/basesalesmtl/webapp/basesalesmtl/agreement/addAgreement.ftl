<style>
	.accordion-inner .row-fluid {
		border-bottom: 1px solid #ccc;
	}
	label {
		margin-top: 5px;
	}
	.green-label {
	    margin-top: 5px;
	}
	.accordion-bg {
		background: rgba(235, 235, 235, 0.5);
    	padding: 15px;
	}
</style>

<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<#assign currencyOrganizationId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)!/>

<script type="text/javascript">
	var defaultDataMap = {
		currencyOrganizationId: "${currencyOrganizationId?if_exists}",
	};
	
	<#if parameters.agreementId?exists>
		$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSUpdateAgreementDetail)}");
		$("#btnSave").html("<i class='fa-check'></i> ${uiLabelMap.CommonSave}");
	</#if>
		var agreementIdParam = "${parameters.agreementId?if_exists}";
		var parnerTypes = [
					{key: "Agents", value: "${StringUtil.wrapString(uiLabelMap.BSRetailOutlet)}"},
                   	{key: "Distributor", value: "${StringUtil.wrapString(uiLabelMap.BSDistributor)}"}
        		];
</script>
<script src="/salesmtlresources/js/agreement/addAgreement.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>

<#assign distributorTerm = delegator.findOne("TermTypeAttr", {"termTypeId" : "DISTRIBUTOR_TERM_TEXT", "attrName": "defaultValue"}, true)>
<#assign agentTerm = delegator.findOne("TermTypeAttr", {"termTypeId" : "AGENT_TERM_TEXT", "attrName": "defaultValue"}, true)>

<div id="container"></div>

<div id="jqxNotificationNestedSlide">
<div id="notificationContentNestedSlide"></div>
</div>

<div id="agreementInfo">
	<div class="row-fluid">
		<div class="span12 no-left-margin boder-all-profile">
			<span class="text-header">${uiLabelMap.DmsAgreementInfo}</span>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5">
							<label class="text-right asterisk">${uiLabelMap.DmsAgreementCode}</label>
						</div>
						<div class="span7">
		    				<input type="text" id="agreementCode" class="no-space" tabindex="2" />
						</div>
					</div>
					<div class="row-fluid margin-top10 style="height: 60px;"">
						<div class="span5">
							<label class="text-right">${uiLabelMap.DmsDescription}</label>
						</div>
						<div class="span7">
							<textarea rows="2" cols="50" id="tarDescription" class="span12" style="resize: none;margin-top: 0px!important" tabindex="3"></textarea>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-top10'>
						<div class='span5'>
							<label class="text-right asterisk">${uiLabelMap.AgreementDate}</label>
						</div>
						<div class='span7'>
							<div id="divAgreementDate"></div>
						</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span5'>
							<label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label>
						</div>
						<div class='span7'>
							<div id="divFromDate"></div>
						</div>
					</div>
					<div class='row-fluid margin-top10'>
						<div class='span5'>
							<label class="text-right asterisk">${uiLabelMap.DmsThruDate}</label>
						</div>
						<div class='span7'>
							<div id="divThruDate"></div>
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
									<label class="text-right asterisk">${uiLabelMap.BSOrganization}</label>
								</div>
								<div class="span7">
									<div id="divPartyTo">
										<div style="border-color: transparent;" id="jqxgridPartyTo"></div>
									</div>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="row-fluid margin-top10">
								<div class="span5">
									<label class="text-right asterisk">${uiLabelMap.DmsFindRepresent}</label>
								</div>
								<div class="span7">
									<div id="divFindRepresentTo">
										<div style="border-color: transparent;" id="jqxgridFindRepresentTo"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<a href="#collapsePartyA" data-toggle="collapse" class="accordion-toggle collapsed">${uiLabelMap.DmsDetailInformation}</a>
				</div>
				<div class="accordion-body collapse" id="collapsePartyA">
					<div class="accordion-inner accordion-bg">
						<div class="row-fluid margin-top10">
							<div class="span6">
								<div class="row-fluid margin-top10" style="height: 50px;">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsAddress}</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divAAddress"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsTelecom}</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divATelecom"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsAccountNumber}</label>
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
										<label class="text-right">${uiLabelMap.BSRepresentedBy}</label>
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
										<label class="text-right">${uiLabelMap.DmsFaxNumber}</label>
									</div>
									<div class="span8">
										<div class="green-label" id="divAFax"></div>
									</div>
								</div>
								<div class="row-fluid margin-top10"><div class="span5 align-right"></div><div class="span7"></div></div>
								<div class="row-fluid margin-top10">
									<div class="span4">
										<label class="text-right">${uiLabelMap.DmsTaxCode}</label>
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
									<label class="text-right asterisk">${uiLabelMap.BSParnerType}</label>
								</div>
								<div class="span7"><div id="divParnerType"></div></div>
            				</div>
            			</div>
						<div class="span6">
							<div class="row-fluid margin-top10">
								<div class="span5">
									<label class="text-right asterisk">${uiLabelMap.DmsFindPartner}</label>
								</div>
								<div class="span7">
									<div id="divPartyFrom">
										<div style="border-color: transparent;" id="jqxgridPartyFrom" tabindex="8"></div> 
									</div>
								</div>
							</div>
						</div>
            		</div>
            		<a href="#collapsePartyB" data-toggle="collapse" class="accordion-toggle collapsed">${uiLabelMap.DmsDetailInformation}</a>
            	</div>
        	<div class="accordion-body collapse" id="collapsePartyB">
            	<div class="accordion-inner accordion-bg">
	            	<div class="row-fluid margin-top10">
						<div class="span6">
		    				<div class="row-fluid margin-top10" style="height: 50px;">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsAddress}</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBAddress"></div>
								</div>
							</div>
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsTelecom}</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBTelecom"></div>
								</div>
							</div>
							<#--
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsAccountNumber}</label>
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
									<label class="text-right">${uiLabelMap.BSRepresentedBy}</label>
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
									<label class="text-right">${uiLabelMap.DmsFaxNumber}</label>
								</div>
								<div class="span8">
									<div class="green-label" id="divBFax"></div>
								</div>
							</div>
							<#--<div class="row-fluid margin-top10"><div class="span5 align-right"></div><div class="span7"></div></div>-->
							<div class="row-fluid margin-top10">
								<div class="span4">
									<label class="text-right">${uiLabelMap.DmsTaxCode}</label>
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
	
	<div class="row-fluid">
		<div class="span12 margin-top10">
			<button id="btnSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonCreate}</button>
		</div>
	</div>
</div>
