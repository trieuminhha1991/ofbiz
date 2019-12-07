<@jqGridMinimumLib/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<script src="/crmresources/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<@jqOlbCoreLib hasDropDownList=true/>
<script src="/salesresources/js/agreement/createAgreement.js"></script>
<style>
	#horizontalScrollBarwe_jqxgridAddessDelivery {
		visibility: inherit !important;
	}
</style>

<div id="container"></div>
<div class="row-fluid" id="agreementInfo">
	<div class="span12">
		<div class="widget-box transparent no-bottom-border">
			<div class="widget-body">
			<div class="span12 no-left-margin boder-all-profile">
				<span class="text-header">${uiLabelMap.DmsAgreementInfo}</span>
				<div class='row-fluid form-window-content' style="overflow: hidden;">
		    		<div class='span6'>
			    		<div class='row-fluid margin-top10'>
							<div class='span6'>
								<label class="text-right asterisk">${uiLabelMap.DmsAgreementCode}</label>
							</div>
							<div class='span6'>
			    				<input type="text" id="agreementCode" style="width: 188px;" tabindex="2" />
							</div>
						</div>
						<div class='row-fluid' style="margin-top: 29px !important;">
							<div class='span6'>
								<label class="text-right asterisk">${uiLabelMap.DmsStaffContract}</label>
							</div>
							<div class='span6'>
			    				<div id="divStaffContract">
									<div style="border-color: transparent;" id="jqxgridStaffContract" tabindex="5"></div>
								</div>
							</div>
						</div>
		    		</div>
		    		<div class='span5'>
			    		<div class='row-fluid margin-top10'>
							<div class='span6'>
								<label class="text-right">${uiLabelMap.DmsDescription}</label>
							</div>
							<div class='span6'>
								<textarea rows="2" cols="50" id="tarDescription" style="resize: none;margin-top: 0px !important;width: 188px;" tabindex="3"></textarea>
							</div>
						</div>
						<div class='row-fluid margin-top10'>
							<div class='span6'>
								<label class="text-right asterisk">${uiLabelMap.DmsSalesChannel}</label>
							</div>
							<div class='span6'>
			    				<div id="divStore" tabindex="6"></div>
							</div>
						</div>
		    		</div>
	    		</div>
			</div>
				<div class="span12 no-left-margin boder-all-profile">
					<span class="text-header">${uiLabelMap.DmsPartyA}</span>
					<div class="accordion-group">
	                  <div class="accordion-heading">
		                <div class='row-fluid form-window-content' style="overflow: hidden;">
			    			<div class='span6'>
				    			<div class='row-fluid margin-top10'>
				    				<div class='span6'>
				    					<label class="text-right asterisk">${uiLabelMap.DmsAgreementType}</label>
				    				</div>
				    				<div class='span6'>
					    				<div id="divAgreementType" tabindex="7"></div>
				    				</div>
								</div>
			    			</div>
			    			<div class='span5'>
				    			 <div class='row-fluid margin-top10'>
				    				<div class='span6'>
				    					<label class="text-right asterisk">${uiLabelMap.DmsFindPartner}</label>
				    				</div>
				    				<div class='span6'>
					    				<div id="divPartyFrom">
											<div style="border-color: transparent;" id="jqxgridPartyFrom" tabindex="8"></div> 
										</div>
				    				</div>
								</div>
			    			</div>
			    		</div>
						 <a href="#collapseOne" data-parent="#accordion2" data-toggle="collapse" class="accordion-toggle collapsed">
						 ${uiLabelMap.DmsDetailInformation}</a>
	                  </div>
	                  <div class="accordion-body collapse" id="collapseOne">
	                    <div class="accordion-inner">
	                    <div class='row-fluid form-window-content' style="overflow: hidden;">
				    		<div class='span6'>
								<div class='row-fluid margin-top10'>
				    				<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsRepresent}: ${uiLabelMap.DmsMr}/${uiLabelMap.DmsMrs}</label>
				    				</div>
				    				<div class='span6'>
				    					<div class="green-label" id="divRepresentFrom"></div>
				    				</div>
								</div>
								<div class='row-fluid margin-top10'>
									<div class='span6'>
										<label class="text-right">${uiLabelMap.DmsIdentification}/${uiLabelMap.DmsPassport}</label>
									</div>
									<div class='span6'>
										<div class="green-label" id="divIdentification"></div>
									</div>
								</div>
								<div class='row-fluid margin-top10'>
									<div class='span6'>
										<label class="text-right">${uiLabelMap.DmsProvideDate}</label>
									</div>
									<div class='span6'>
										<div class="green-label" id="divNgayCap"></div>
									</div>
								</div>
								<div class='row-fluid margin-top10' style="position: relative">
									<div class='span6'>
										<label class="text-right asterisk">${uiLabelMap.DmsPAddessDeliveryAndRecieveMerchandise} ${uiLabelMap.DmsPartyA}(1)</label>
									</div>
									<div class='span5'>
										<div id="we_divAddessDelivery">
											<div style="border-color: transparent;" id="we_jqxgridAddessDelivery"></div>
										</div>
									</div>
									<div class='' style="position: absolute; right:5px"><i class="green fa-pencil hide" id="editAddessDelivery" title="${uiLabelMap.DmsClickToEditAddessDelivery}"></i></div>
								</div>
								<div class='row-fluid margin-top10'>
									<div class='span6'>
										<label class="text-right">${uiLabelMap.DmsTelecom}</label>
									</div>
									<div class='span6'>
										<div class="green-label" id="divTelecomFrom"></div>
									</div>
								</div>
				    		</div>
				    		<div class='span5'>
					    		<div class='row-fluid margin-top10'><div class='span6 align-right'></div><div class='span6'></div></div>
					    		<div class='row-fluid margin-top10'><div class='span6 align-right'></div><div class='span6'></div></div>
				    			<div class='row-fluid margin-top10'>
									<div class='span6'>
										<label class="text-right">${uiLabelMap.DmsProvidePlace}</label>
									</div>
									<div class='span6'>
										<div class="green-label" id="divNoiCap"></div>
									</div>
								</div>
								<div class='row-fluid margin-top10'><div class='span6 align-right'></div><div class='span6'></div></div>
								<div class='row-fluid margin-top10'>
									<div class='span6'>
										<label class="text-right">${uiLabelMap.DmsEmail}</label>
									</div>
									<div class='span6'>
										<div class="green-label" id="divEmailFrom"></div>
									</div>
								</div>
				    		</div>
			    		</div>
	                    </div>
	                  </div>
	                </div>
				</div>
				<div class="span12 no-left-margin boder-all-profile">
					<span class="text-header">${uiLabelMap.DmsPartyB}</span>
					<div class="accordion-group">
	                  <div class="accordion-heading">
	                <div class='row-fluid form-window-content' style="overflow: hidden;">
		    			<div class='span6'>
			    			<div class='row-fluid margin-top10'>
			    				<div class='span6'>
			    					<label class="text-right asterisk">${uiLabelMap.DmsFinddepartment}</label>
			    				</div>
			    				<div class='span6'>
				    				<div id="divPartyTo">
										<div style="border-color: transparent;" id="jqxgridPartyTo"></div>
									</div>
			    				</div>
							</div>
		    			</div>
		    			<div class='span5'>
			    			<div class='row-fluid margin-top10'>
			    				<div class='span6'>
			    					<label class="text-right asterisk">${uiLabelMap.DmsFindRepresent}</label>
			    				</div>
			    				<div class='span6'>
				    				<div id="divFindRepresentTo">
										<div style="border-color: transparent;" id="jqxgridFindRepresentTo"></div>
									</div>
			    				</div>
							</div>
		    			</div>
					</div>
						<a href="#collapseTwo" data-parent="#accordion2" data-toggle="collapse" class="accordion-toggle collapsed">
	                    ${uiLabelMap.DmsDetailInformation}
	                    </a>
	                  </div>
	                  <div class="accordion-body collapse" id="collapseTwo">
	                    <div class="accordion-inner">
	                    <div class='row-fluid form-window-content' style="overflow: hidden;">
			    		<div class='span6'>
							<div class='row-fluid margin-top10'>
			    				<div class='span6'>
								<label class="text-right">${uiLabelMap.DmsRepresent} ${uiLabelMap.DmsMr}</label>
			    				</div>
			    				<div class='span6'>
			    					<div class="green-label" id="divRepresentTo"></div>
			    				</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsJobTitle}</label>
								</div>
								<div class='span6'>
									<div class="green-label" id="divJobTitle"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsAccountNumber}</label>
								</div>
								<div class='span6'>
									<div class="green-label" id="divAccountNumber"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsAddress}</label>
								</div>
								<div class='span6'>
									<div class="green-label" id="divAddress"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsTelecom}</label>
								</div>
								<div class='span6'>
									<div class="green-label" id="divTelecomTo"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsEmail}</label>
								</div>
								<div class='span6'>
									<div class="green-label" id="divEmailTo"></div>
								</div>
							</div>
						</div>
						<div class='span4'>
							<div class='row-fluid margin-top10'><div class='span6 align-right'></div><div class='span6'></div></div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsTaxCode}</label>
								</div>
								<div class='span6'>
									<div class="green-label" id="divTaxCode"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'><div class='span6 align-right'></div><div class='span6'></div></div>
							<div class='row-fluid margin-top10'><div class='span6 align-right'></div><div class='span6'></div></div>
							<div class='row-fluid margin-top10'><div class='span6 align-right'></div><div class='span6'></div></div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right">${uiLabelMap.DmsFaxNumber}</label>
								</div>
								<div class='span6'>
									<div class="green-label" id="divFax"></div>
								</div>
							</div>
						</div>
					</div>
	                    </div>
	                  </div>
	                </div>
				</div>
				
				<div class="span12 no-left-margin boder-all-profile">
				<span class="text-header">${uiLabelMap.DmsService}</span>
					<div class='row-fluid'>
						<div class='span6'>
							<div class='row-fluid margin-top10'>
								<div class='span5'>
									<label class="text-right asterisk">${uiLabelMap.DmsNumberOfMonthsUse}</label>
								</div>
								<div class='span7'>
									<div id="divNumberOfMonthsUse" class='pull-left'></div>
									<div class='pull-left margin-left10'>(${uiLabelMap.CommonMonth})</div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span5'>
									<label class="text-right asterisk">${uiLabelMap.DmsTheMinimumSubscriptionValue}</label>
								</div>
								<div class='span7'>
									<div id="divTheMinimumSubscriptionValue" class='pull-left'></div>
									<div class='pull-left margin-left10'>${uiLabelMap.DmsCoin}/${uiLabelMap.CommonMonth}</div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span5'>
									<label class="text-right asterisk">${uiLabelMap.DmsDeliveryTime}</label>
								</div>
								<div class='span5'>
									<div id="deliveryMethod">&nbsp;</div>
								</div>
								<div class='span2 no-left-margin'>
									<div id="divDeliveryTime"></div>
								</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
								</div>
								<div class='span5'>
								</div>
								<div class='span2 no-left-margin' >
									<div id="divDeliveryTime2"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span5'>
									<label class="text-right asterisk">${uiLabelMap.AgreementDate}</label>
								</div>
								<div class='span7'>
									<div id="divAgreementDate"></div>
								</div>
							</div>
						</div>
						<div class='span6'>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right asterisk">${uiLabelMap.DmsPaymentMethodOf} ${uiLabelMap.DmsPartyA}</label>
								</div>
								<div class='span6' >
									<div id="divPaymentMethod"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right asterisk">${uiLabelMap.DmsPaymentFormalityOf} ${uiLabelMap.DmsPartyA}</label>
								</div>
								<div class='span6' >
									<div id="divPaymentFormality"></div>
								</div>
							</div>
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right asterisk">${uiLabelMap.DmsReceiveInfomationMethod}</label>
								</div>
								<div class='span6' >
									<div id="divReceiveInfomationMethod"></div>
								</div>
							</div>
							<div class='row-fluid'>
								<div class='span6'></div>
								<div class='span6'><label class="green" id="lblPeriod2"></label></div>
							</div>
							
							<div class='row-fluid margin-top10'>
								<div class='span6'>
									<label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label>
								</div>
								<div class='span6'>
									<div id="divFromDate"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
				
				<div class="span12 no-left-margin boder-all-profile">
				<span class="text-header">${uiLabelMap.DmsTerms}</span>
				
				<div class="accordion-group">
	                <div class="accordion-heading">
	                  <a href="#collapseThree" data-parent="#accordion2" data-toggle="collapse" class="accordion-toggle collapsed">
	                  ${uiLabelMap.DmsListTerms}
	                  </a>
	                </div>
	                <div class="accordion-body collapse" id="collapseThree">
	                  <div class="accordion-inner">
	                  		<div class='row-fluid margin-top10'>
								<div id="divTerms">${StringUtil.wrapString(agreementTerm?if_exists)}</div>
							</div>
	                  </div>
	                </div>
	              </div>
					
				</div>
				
				<div class="span12 no-left-margin boder-all-profile">
				<span class="text-header">${uiLabelMap.DmsListProducts}</span>
					<div class='row-fluid margin-top10'>
						<div id="jqxgridProduct"></div>
					</div>
				</div>
				
			</div>
		</div>
	</div>
</div>
<div class='row-fluid'>
	<div class="span12 margin-top10">
		<button id="btnSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonCreate}</button>
	</div>
</div>

<div id="jqxNotification">
	<div id="notificationContent">
	</div>
</div>

<script>
	const agreementIdParam = "${parameters.agreementId?if_exists}";
	const partyTypeIdParam = "${StringUtil.wrapString((party.partyTypeId)?if_exists)}";
	const partyIdParam = "${StringUtil.wrapString((party.partyId)?if_exists)}";
	
	const organizationId = "${StringUtil.wrapString(organizationId?if_exists)}";
	const productStores = ${StringUtil.wrapString(productStores?if_exists)};
</script>