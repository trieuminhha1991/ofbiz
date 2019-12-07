<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<div id="partyoverview-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "partyoverview-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<#if agentInfo.statusId?exists>
			<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : "${agentInfo.statusId}"}, false)!/>
			<div class="title-status" id="statusTitle">
				<#if statusItem?exists>${statusItem.get("description", locale)}</#if>
			</div>
		</#if>
		<div><!--class="widget-main"-->
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSAgentId}:</label>
								</div>
								<div class="div-inline-block">
									<span><i>${agentInfo.partyCode?if_exists}</i></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSAgentName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${agentInfo.groupName?if_exists}<#-- agentInfo.groupNameLocal --></span>
								</div>
							</div>
							
							<div class="row-fluid margin-top10">
								<div class="logo-company">
									<img width="300px" src="${agentInfo.logoImageUrl?default("/salesmtlresources/logo/LOGO_demo.png")}"/>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.FormFieldTitle_officeSiteName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${agentInfo.officeSiteName?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.PartyTaxAuthInfos}:</label>
								</div>
								<div class="div-inline-block">
									<span>${agentInfo.taxAuthInfos?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCurrencyUomId}:</label>
								</div>
								<div class="div-inline-block">
									<span>${agentInfo.currencyUomId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.EmailAddress}:</label>
								</div>
								<div class="div-inline-block">
									<span>${agentInfo.infoString?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.PhoneNumber}:</label>
								</div>
								<div class="div-inline-block">
									<span>${agentInfo.contactNumber?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSAddress}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#assign arrayPurposes = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, null, false), "contactMechPurposeTypeId", true)?default([])/>
										<#assign conditionList = [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", agentInfo.partyId)]/>
										<#assign conditionList = conditionList + [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("contactMechPurposeTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, arrayPurposes)]/>
										<#assign conditionList = conditionList + [Static["org.ofbiz.entity.util.EntityUtil"].getFilterByDateExpr()]/>
										<#assign partyContactMechPurposes = delegator.findList("PartyContactMechPurpose", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(conditionList), null, null, null, false)!/>
										<#if partyContactMechPurposes?has_content>
											<#assign contactMechIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(partyContactMechPurposes, "contactMechId", true)!/>
											<#assign contactMeches = delegator.findList("PostalAddressFullNameDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("contactMechId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, contactMechIds), null, null, null, false)!/>
											<#if contactMeches?has_content>
												<ul class="unstyled spaced" style="margin: 0 0 0 0">
												<#list contactMeches as contactItem>
								                	<li style="margin-bottom:0; margin-top:0">
												      	${contactItem.fullName?if_exists}
													</li>
								                </#list>
								                </ul>
											</#if>
										</#if>
									</span>

								</div>
                                <a href="javascript:openChangeAddress('${parameters.partyId}');"><i class="fa fa-pencil-square"></i></a>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSDescription}:</label>
								</div>
								<div class="div-inline-block">
									<span>${agentInfo.comments?if_exists}</span>
								</div>
							</div>
                            <#assign visitFrequencyTypes = delegator.findList("VisitFrequencyType", null, null, null, null, false) />
                            <div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSVisitFrequency}:</label>
								</div>
                                <#if agentInfo.visitFrequencyTypeId?has_content>
								<div class="div-inline-block">
                                    <#list visitFrequencyTypes as item>
                                        <#if item.visitFrequencyTypeId == agentInfo.visitFrequencyTypeId>
                                            ${item.description?if_exists}
                                        </#if>
                                    </#list>
									<#--<span>visitFrequencyTypes</span>-->
								</div>
                                </#if>
							</div>
						</div>
					</div>
					<#-- get info of distributor -->
					<#if agentInfo.distributorId?has_content>
                        <#assign distributors = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : agentInfo.distributorId}, null, false)!>
                        <#if distributors?has_content>
                            <div class="row-fluid">
                                <div class="span6">
                                    <div class="row-fluid">
                                        <div class="div-inline-block">
                                            <label>${uiLabelMap.BSDistributor}:</label>
                                        </div>
                                        <div class="div-inline-block">
                                            <span><#list distributors as distributor>${distributor.fullName?if_exists} (${distributor.partyCode?if_exists})</#list></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#if>
					</#if>
					
					<#-- get info of supervisor department -->
					<#if agentInfo.distributorId?exists>
						<#assign supDeptIds = Static["com.olbius.basesales.party.PartyWorker"].getDeptOrgByParty(delegator, false, agentInfo.distributorId, "SALESSUP_DEPT", "DISTRIBUTOR", "DISTRIBUTION")!/>
						<#if supDeptIds?has_content>
							<#assign supDeptId = supDeptIds?first/>
							<#assign supDepts = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : supDeptId}, null, false)!/>
							<div class="row-fluid">
								<div class="span6">
									<div class="row-fluid">
										<div class="div-inline-block">
											<label>${uiLabelMap.BSSupDept}:</label>
										</div>
										<div class="div-inline-block">
											<span><#list supDepts as supDept>${supDept.fullName?if_exists} (${supDept.partyCode?if_exists})</#list></span>
										</div>
									</div>
									<#assign supPersonIds = Static["com.olbius.basesales.party.PartyWorker"].getPartyMgrByDept(delegator, supDeptId)!/>
									<#if supPersonIds?has_content>
										<#assign supPersonId = supPersonIds?first/>
										<#assign supPersons = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : supPersonId}, null, false)!/>
										<div class="row-fluid">
											<div class="div-inline-block">
												<label>${uiLabelMap.BSSupervisor}:</label>
											</div>
											<div class="div-inline-block">
												<span><#list supPersons as supPerson>${supPerson.fullName?if_exists} (${supPerson.partyCode?if_exists})</#list></span>
											</div>
										</div>
									</#if>
								</div>
							</div>
						</#if>
					</#if>

					<#-- get ingo of sales executive -->
					<#if agentInfo.salesmanId?has_content>
						<#assign salesExecutiveId = agentInfo.salesmanId/>
						<#assign salesExecutives = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : salesExecutiveId}, null, false)!/>
						<div class="row-fluid">
							<div class="span6">
								<div class="row-fluid">
									<div class="div-inline-block">
										<label>${uiLabelMap.BSSalesExecutive}:</label>
									</div>
									<div class="div-inline-block">
										<span><#list salesExecutives as salesExecutive>${salesExecutive.fullName?if_exists} (${salesExecutive.partyCode?if_exists})</#list></span>
									</div>
								</div>
							</div>
						</div>
					</#if>
					<#if agentInfo.statusId == "PARTY_DISABLED" && security.hasEntityPermission("AGENT", "_APPROVE", session)>
	<div class="row-fluid" id="accept-wrapper">
		<div class="span12 margin-top10">
			<button id="btnAccept" class="btn btn-primary form-action-button pull-right"><i class="icon-ok"></i>${uiLabelMap.BSActiveRetailer}</button>
		</div>
	</div>
</#if>
<#if agentInfo.statusId == "PARTY_ENABLED" && security.hasEntityPermission("AGENT", "_APPROVE", session)>
	<div class="row-fluid" id="reject-wrapper">
		<div class="span12 margin-top10">
			<button id="btnReject" class="btn btn-primary form-action-button pull-right"><i class="fa-trash"></i>${uiLabelMap.BSDeactiveRetailer}</button>
		</div>
	</div>
</#if>
				</div>
			</div>
		</div>
	</div>
	<#--comment by Huyen then trans data SQL-->
	<#--<#if agentInfo.partyId?exists>
		<div class="row-fluid margin-top10">
			<div class="span12">
				${screens.render("component://basesalesmtl/widget/BaseSalesMtlReportScreens.xml#EvaluateAgencyInner")}
			</div>
		</div>
	</#if>-->
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>


<div id="jqxwindowChangeAddress" style="display:none;">
    <div>${uiLabelMap.BSChangeAddress}</div>
    <div id="divAddress"></div>
</div>

<script type="text/javascript">

    var openChangeAddress = function(partyId) {
        $("#jqxwindowChangeAddress").jqxWindow({
            theme: "olbius", width: 1000, maxWidth: 1845, height: 500, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
        });
        $('#jqxwindowChangeAddress').on('close', function (event) { location.reload() });
        var wtmp = window;
        partyIdPram=partyId;
        var tmpwidth = $("#jqxwindowChangeAddress").jqxWindow("width");
        $("#jqxwindowChangeAddress").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
        $("#jqxwindowChangeAddress").jqxWindow("open");
        $.ajax({
            type: 'POST',
            url: 'EditAddressAgent',
            data: {
                partyId:partyId
            },
            beforeSend: function(){
                /*$("#loader_page_common").show();*/
            },
            success: function(data){
                jOlbUtil.processResultDataAjax(data, "default", "default", function(){

                    $("#divAddress").html(data);
                });
            },
            error: function(data){
                alert("Send request is error");
            },
            complete: function(data){

                /*$("#loader_page_common").hide();*/
            },
        });
    };
</script>