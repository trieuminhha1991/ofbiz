<style>
	.highlightCell{
		background-color: #DDEE90 !important;
		color: #009900 !important
	}
</style>

<script type="text/javascript">
if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}
uiLabelMap.partyId = "${StringUtil.wrapString(uiLabelMap.partyId)}";
uiLabelMap.HRFullName = "${StringUtil.wrapString(uiLabelMap.HRFullName)}";
uiLabelMap.HRRelationship = "${StringUtil.wrapString(uiLabelMap.HRRelationship)}";
uiLabelMap.BirthDate = "${StringUtil.wrapString(uiLabelMap.BirthDate)}";
uiLabelMap.HROccupation = "${StringUtil.wrapString(uiLabelMap.HROccupation)}";
uiLabelMap.placeWork = "${StringUtil.wrapString(uiLabelMap.placeWork)}";
uiLabelMap.PartyPhoneNumber = "${StringUtil.wrapString(uiLabelMap.PartyPhoneNumber)}";
uiLabelMap.PersonDependent = "${StringUtil.wrapString(uiLabelMap.PersonDependent)}";
<#assign partyRelationshipType = delegator.findByAnd("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "FAMILY"), null, false)>
var partyRelationshipType = [
		<#list partyRelationshipType as partyRelationship>
       		{
       			partyRelationshipTypeId : "${partyRelationship.partyRelationshipTypeId}",
       			partyRelationshipName : "${StringUtil.wrapString(partyRelationship.partyRelationshipName?default(''))}"
       		},
       	</#list>	
];
</script>
<script type="text/javascript" src="/hrresources/js/generalMgr/ViewEmplDetail.js"></script>
<div id="viewEmplDetailsWindow" class="hide">
	<div>${uiLabelMap.HREmployeeDetailInfo}</div>
	<div class='form-window-container'>
		<div id='emplDetail_generalInfo' class="expanedGroup" style="margin-top: 10px; position: relative;">
			<div>${uiLabelMap.GeneralInformation}</div>
            <div>
            	<div class="row-fluid">
	            	<div class="span12" style="margin-top: 5px">
	            		<div class="span6">
							<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.EmployeeId}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailEmployeeId"></label>
						   		</div>
							</div> 
							<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.EmployeeName}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailEmplFullName"></label>
						   		</div>
							</div>           		
							<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.PartyGender}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailGeneder"></label>
						   		</div>
							</div>           		
							<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.PartyBirthDate}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailBirthDate"></label>
						   		</div>
							</div>           		
							<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.certProvisionId}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailCertProvisionId"></label>
						   		</div>
							</div>           		
							<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HrolbiusidIssuePlace}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailIssuePlace"></label>
						   		</div>
							</div>           		
							<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HrolbiusidIssueDate}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailIssueDate"></label>
						   		</div>
							</div>           		
	            		</div>
	            		<div class="span6">
	            			<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.NativeLand}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailNativeLand"></label>
						   		</div>
							</div>
	            			<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.EthnicOrigin}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailEthnicOrigin"></label>
						   		</div>
							</div>
	            			<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HrolbiusReligion}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailReligion"></label>
						   		</div>
							</div>
	            			<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HrolbiusNationality}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailNationality"></label>
						   		</div>
							</div>
	            			<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.MaritalStatus}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailMaritalStatus"></label>
						   		</div>
							</div>
	            			<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.NumberChildren}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailNumberChildren"></label>
						   		</div>
							</div>
	            		</div>
	            	</div>
            	</div>
            	<div class="row-fluid no-left-margin">
					<div id="ajaxLoading_generalInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinner-ajax_generalInfo"></div>
					</div>
				</div>
            </div>
		</div>
		<div id='emplDetail_contactInfo' class="expanedGroup" style="position: relative; margin-top: 10px">
			<div>${uiLabelMap.ContactInformation}</div>
            <div>
            	<div class="row-fluid">
            		<div class="span12" style="margin-top: 5px">
            			<div class='row-fluid margin-bottom5'>
							<div class='span3 text-algin-right'>
								<label class=""><b>${uiLabelMap.PermanentResidence}</b></label>
							</div>  
							<div class="span9">
								<label class="" id="viewDetailPermanentResidence"></label>
					   		</div>
						</div>
            			<div class='row-fluid margin-bottom5'>
							<div class='span3 text-algin-right'>
								<label class=""><b>${uiLabelMap.ContactAddress}</b></label>
							</div>  
							<div class="span9">
								<label class="" id="viewDetailCurrentResidence"></label>
					   		</div>
						</div>
            			<div class='row-fluid margin-bottom5'>
							<div class='span3 text-algin-right'>
								<label class=""><b>${uiLabelMap.CommonEmail}</b></label>
							</div>  
							<div class="span9">
								<label class="" id="viewDetailEmail"></label>
					   		</div>
						</div>
            			<div class='row-fluid margin-bottom5'>
							<div class='span3 text-algin-right'>
								<label class=""><b>${uiLabelMap.PhoneMobile}</b></label>
							</div>  
							<div class="span9">
								<label class="" id="viewDetailPhoneMobile"></label>
					   		</div>
						</div>
            		</div>
            	</div>
            	<div class="row-fluid no-left-margin">
					<div id="ajaxLoading_contactInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinner-ajax_contactInfo"></div>
					</div>
				</div>
            </div>
		</div>
		<div id='emplDetail_employeeWork' class="expanedGroup" style="position: relative;margin-top: 10px">
			<div>${uiLabelMap.EmployeeWorkInformation}</div>
            <div>
            	<div class="row-fluid">
            		<div class="span12" style="margin-top: 5px">
            			<div class="span6">
            				<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.PartyIdWork}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailPartyIdFrom"></label>
						   		</div>
							</div> 
            				<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HrCommonPosition}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailPosition"></label>
						   		</div>
							</div> 
            				<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HRCommonCurrStatus}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailWorkStatus"></label>
						   		</div>
							</div> 
            			</div>
            			<div class="span6">
            				<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.DateJoinCompany}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailDateJoinCompany"></label>
						   		</div>
							</div> 
            				<!-- <div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class="">${uiLabelMap.SalaryBaseFlat}</label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailSalaryBaseFlat"></label>
						   		</div>
							</div>  -->
            				<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HREmplReasonResign}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailReasonResign"></label>
						   		</div>
							</div>
            				<div class='row-fluid margin-bottom5'>
								<div class='span5 text-algin-right'>
									<label class=""><b>${uiLabelMap.HREmplResignDate}</b></label>
								</div>  
								<div class="span7">
									<label class="" id="viewDetailResignDate"></label>
						   		</div>
							</div>
            			</div>
            		</div>
            	</div>	
            	<div class="row-fluid no-left-margin">
					<div id="ajaxLoading_employeeWork" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinner-ajax_employeeWork"></div>
					</div>
				</div>
            </div>
		</div>
		<div id='emplDetail_family' class="expanedGroup" style="margin-top: 10px">
			<div>${uiLabelMap.HREmployeeFamilyInfo}</div>
            <div>
            	<div id="viewDetailFamilyGrid"></div>
            </div>
		</div>
	</div>
</div>