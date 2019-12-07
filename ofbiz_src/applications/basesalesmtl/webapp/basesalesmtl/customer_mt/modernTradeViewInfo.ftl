<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<div id="partyoverview-tab" class="tab-pane<#if !activeTab?exists || activeTab == "" || activeTab == "partyoverview-tab"> active</#if>">
	<div style="position:relative"><!-- class="widget-body"-->
		<#if modernTradeInfo.statusId?exists>
			<#assign statusItem = delegator.findOne("StatusItem", {"statusId" : "${modernTradeInfo.statusId}"}, false)!/>
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
									<label>${uiLabelMap.BSCustomerId}:</label>
								</div>
								<div class="div-inline-block">
									<span><i>${modernTradeInfo.partyCode?if_exists}</i></span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCustomerName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${modernTradeInfo.groupName?if_exists}</span>
								</div>
							</div>
							
							<div class="row-fluid margin-top10">
								<div class="logo-company">
									<img width="300px" src="${modernTradeInfo.logoImageUrl?default("/salesmtlresources/logo/LOGO_demo.png")}"/>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span6">
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.FormFieldTitle_officeSiteName}:</label>
								</div>
								<div class="div-inline-block">
									<span>${modernTradeInfo.officeSiteName?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.PartyTaxAuthInfos}:</label>
								</div>
								<div class="div-inline-block">
									<span>${modernTradeInfo.taxAuthInfos?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSCurrencyUomId}:</label>
								</div>
								<div class="div-inline-block">
									<span>${modernTradeInfo.currencyUomId?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.EmailAddress}:</label>
								</div>
								<div class="div-inline-block">
									<span>${modernTradeInfo.infoString?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.PhoneNumber}:</label>
								</div>
								<div class="div-inline-block">
									<span>${modernTradeInfo.contactNumber?if_exists}</span>
								</div>
							</div>
							<div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSAddress}:</label>
								</div>
								<div class="div-inline-block">
									<span>
										<#assign arrayPurposes = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "POSTAL_ADDRESS"}, null, false), "contactMechPurposeTypeId", true)?default([])/>
										<#assign conditionList = [Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId", modernTradeInfo.partyId)]/>
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
									<span>${modernTradeInfo.comments?if_exists}</span>
								</div>
							</div>
                            <#assign visitFrequencyTypes = delegator.findList("VisitFrequencyType", null, null, null, null, false) />
                            <div class="row-fluid">
								<div class="div-inline-block">
									<label>${uiLabelMap.BSVisitFrequency}:</label>
								</div>
                                <#if modernTradeInfo.visitFrequencyTypeId?has_content>
								<div class="div-inline-block">
                                    <#list visitFrequencyTypes as item>
                                        <#if item.visitFrequencyTypeId == modernTradeInfo.visitFrequencyTypeId>
                                            ${item.description?if_exists}
                                        </#if>
                                    </#list>
									<#--<span>visitFrequencyTypes</span>-->
								</div>
                                </#if>
							</div>
						</div>
					</div>
                    <#-- get info of supervisor department -->
                    <#if modernTradeInfo.supervisorId?has_content>
                        <#assign supDepts = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : modernTradeInfo.supervisorId}, null, false)!/>
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
                                <#assign supPersonIds = Static["com.olbius.basesales.party.PartyWorker"].getPartyMgrByDept(delegator, modernTradeInfo.supervisorId)!/>
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
					<#-- get ingo of sales executive -->
					<#if modernTradeInfo.salesmanId?has_content>
						<#assign salesExecutives = delegator.findByAnd("PartyFullNameDetailSimple", {"partyId" : modernTradeInfo.salesmanId}, null, false)!/>
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
					<#if modernTradeInfo.representativeOfficeId?has_content>
                    <#assign representativeOffices = delegator.findByAnd("Party", {"partyId" : modernTradeInfo.representativeOfficeId}, null, false)!/>
                        <div class="row-fluid">
							<div class="span6">
								<div class="row-fluid">
									<div class="div-inline-block">
										<label>${uiLabelMap.CommonRepresentativeOffice}:</label>
									</div>
									<div class="div-inline-block">
										<span><#list representativeOffices as representativeOffice>${representativeOffice.description?if_exists} (${representativeOffice.partyCode?if_exists})</#list></span>
									</div>
								</div>
							</div>
						</div>
					</#if>
				</div>
			</div>
		</div>
	</div>
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