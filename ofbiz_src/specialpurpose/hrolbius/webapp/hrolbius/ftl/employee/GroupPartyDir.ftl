<script>
	var loadFormPA = false;
	var loadFormEmail = false;
	var loadFormTelecom = false;
</script>
<div class="widget-box transparent no-bottom-border" id="PartyGroupInformationPanel">
	<div class="widget-header">
		<h4>Thông tin tổ chức</h4><div class="loading-image" style="width:20px;height:20px;float:left;padding-top:8px;"></div>
		<span class="widget-toolbar none-content">
			<script type="text/javascript">
				function changeIconChev(elm) {
					if (elm.attr('class') == "icon-chevron-down") {
						elm.attr('class', 'icon-chevron-up');
					} else {
						elm.attr('class', 'icon-chevron-down');
					}
				}
			</script>
			<a href="javascript:showPopup()"><i class="icon-edit open-sans">Cập nhật</i></a> 
		</span>
	</div>
	<div class="widget-body">
		<div id="PartyGroupInformationPanel_col" class="widget-body-inner">
			<form class="basic-form form-horizontal" >
				<div class="row-fluid">
					<div class=" no-widget-header span6  no-left-margin boder-all-profile">
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_partyId" id="ViewPartyGroup_partyId_title">${uiLabelMap.HrolbiusPartyOrg}</label>
							<div class="controls">
								${partyId}&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_groupName" id="ViewPartyGroup_groupName_title">${uiLabelMap.HrolbiusGroupName}</label>
							<div class="controls">
								${partyInformation.groupName?if_exists}&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_description" id="ViewPartyGroup_description_title">${uiLabelMap.CommonDescription}</label>
							<div class="controls">
								${party.description?if_exists}&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_description" id="ViewPartyGroup_description_title">${uiLabelMap.FormFieldTitle_annualRevenue}</label>
							<div class="controls">
								${partyInformation.annualRevenue?if_exists}&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_description" id="ViewPartyGroup_description_title">${uiLabelMap.FormFieldTitle_numEmployees}</label>
							<div class="controls">
								${partyInformation.numEmployees?if_exists}&nbsp;
							</div>
						</div>
					</div>
					<div class=" no-widget-header span6 boder-all-profile">
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_address" id="ViewPartyGroup_address_title">${uiLabelMap.PartyAddressLine}</label>
							<div class="controls">
								<#if listAddress?exists>
								<#list listAddress as addr>
								<#if addr.geoName?exists><span>${addr.geoName}:&nbsp;</span></#if><#if addr.address1?exists><span>${addr.address1}</span></#if> - <#if addr.address2?exists><span>${addr.address2}</span></#if>
								<#if addr_has_next>
								<br/>
								</#if>
								</#list>
								</#if>
								&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_address" id="ViewPartyGroup_phone_title">${uiLabelMap.PartyPhoneNumber}</label>
							<div class="controls">
								<#if listPhoneNumber?exists>
								<#list listPhoneNumber as number>
								<#if number.countryCode?exists><span>(+${number.countryCode})-</span></#if><#if number.areaCode?exists><span>0${number.areaCode}</span>-</#if>${number.contactNumber}
								<#if number_has_next>
								<br/>
								</#if>
								</#list>
								</#if>
								&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_address" id="ViewPartyGroup_email_title">${uiLabelMap.EmailAddress}</label>
							<div class="controls">
								<#if listEmail?exists>
								<#list listEmail as addr>
								<#if addr.infoString?exists><span>${addr.infoString}</span></#if>
								<#if addr_has_next>
								<br/>
								</#if>
								</#list>
								</#if>
								&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group no-left-margin ">
							<label for="ViewPartyGroup_statusId" id="ViewPartyGroup_statusId_title">${uiLabelMap.FormFieldTitle_statusId}</label>
							<div class="controls">
								<#if party.statusId?exists || party.statudId == "PARTY_ENABLED">
								${uiLabelMap.FormFieldTitle_isActive}
								<#elseif party.statusId == "PARTY_DISABLED">
								${uiLabelMap.StatusItem.description.CTNT_DEACTIVATED}
								</#if>
								&nbsp;
							</div>
						</div>
					</div>
			</form>
			
		</div>
	</div>
</div>
</div>
<div id="popupEditParty" class='hide'>
	<div>${uiLabelMap.EditPartyForm}</div>
    <div style="overflow: scroll;">
    	<div class="row-fluid">
    		<div class="title-border" style="margin-top: 0;">
				<span>${uiLabelMap.generalInfo}</span>
			</div>
			<form class="basic-form form-horizontal" id="EditPartyGroup">
				<input type="hidden" value="<#if party.partyId?exists>${party.partyId}</#if>" name="partyId"/>
				<div class="row-fluid">
					<div class=" no-widget-header no-left-margin span6">
						<div class="control-group hrm-control-group2 no-left-margin margin-bottom8">
							<label for="partyId" id="partyId_title">${uiLabelMap.PartyParty}</label>
							<div class="controls">
								${partyId}&nbsp;<span class="tooltipob"></span>
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="groupName" class="asterisk" id="groupName_title">${uiLabelMap.PartyGroupName}</label>
							<div class="controls">
								<input type="text" name="groupName" value="Delys" size="40" maxlength="60" id="groupName" autocomplete="off" value="${partyInformation.groupName?if_exists}&nbsp;"/>
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="groupNameLocal" id="groupNameLocal_title">${uiLabelMap.FormFieldTitle_groupNameLocal}</label>
							<div class="controls">
								<input type="text" name="groupNameLocal" size="40" maxlength="60" id="groupNameLocal" autocomplete="off" value="${partyInformation.groupNameLocal?if_exists}&nbsp;" />
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="officeSiteName" id="officeSiteName_title">${uiLabelMap.FormFieldTitle_officeSiteName}</label>
							<div class="controls">
								<input type="text" name="officeSiteName" size="40" maxlength="60" id="officeSiteName" autocomplete="off">
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="annualRevenue" id="annualRevenue_title">${uiLabelMap.FormFieldTitle_annualRevenue}</label>
							<div class="controls">
								<input type="text" name="annualRevenue" size="6" id="annualRevenue" autocomplete="off" value="${partyInformation.annualRevenue?if_exists}">
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="numEmployees" id="numEmployees_title">${uiLabelMap.FormFieldTitle_numEmployees}</label>
							<div class="controls">
								<input type="text" name="numEmployees" size="6" id="numEmployees" autocomplete="off" value="${partyInformation.numEmployees?if_exists}">
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="description" id="description_title">${uiLabelMap.CommonDescription}</label>
							<div class="controls">
								<input type="text" name="description" size="25" id="description" value="${party.description?if_exists}&nbsp;">
							</div>
						</div>
					</div>
		
					<div class=" no-widget-header span6">
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="tickerSymbol" id="tickerSymbol_title">${uiLabelMap.FormFieldTitle_tickerSymbol}</label>
							<div class="controls" style="margin-top: -45px !important">
								<input type="text" name="tickerSymbol" size="6" maxlength="10" id="tickerSymbol" autocomplete="off" value="${party.tickerSymbol?if_exists}">
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin " style="margin-top: 10px;">
							<label for="comments" id="comments_title">Bình luận</label>
							<div class="controls">
								<input type="text" name="comments" size="60" maxlength="250" id="comments" autocomplete="off"  value="${party.comments?if_exists}">
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="logoImageUrl" id="logoImageUrl_title">${uiLabelMap.FormFieldTitle_logoImageUrl}</label>
							<div class="controls">
								<input type="text" name="logoImageUrl" size="60" maxlength="250" id="logoImageUrl" autocomplete="off"  value="${party.logoImageUrl?if_exists}">
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="preferredCurrencyUomId" id="preferredCurrencyUomId_title">${uiLabelMap.CurrencyUomId}</label>
							<script>var currencyUomId = "${party.preferredCurrencyUomId?if_exists}";</script>
							<div class="controls">
								<div id="preferredCurrencyUomId">
								</div>
								&nbsp;
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="externalId" id="externalId_title">${uiLabelMap.FormFieldTitle_externalId}</label>
							<div class="controls">
								<input type="text" name="externalId" size="25" id="externalId" autocomplete="off"  value="${party.externalId?if_exists}">
							</div>
						</div>
						<div class="control-group hrm-control-group2 no-left-margin ">
							<label for="statusId" id="statusId_title">${uiLabelMap.FormFieldTitle_statusId}</label>
							<div class="controls">
								<span class="ui-widget">
									<select name="statusId" id="statusId" size="1">
										<option value="PARTY_ENABLED" <#if party.statusId?exists && party.statusId == "PARTY_ENABLED">selected</#if>>${uiLabelMap.FormFieldTitle_isActive}</option>
										<option value="PARTY_DISABLED" <#if party.statusId?exists && party.statusId == "PARTY_ENABLED">selected</#if>>${uiLabelMap.notActive}</option>
									</select> 
								</span>
							</div>
						</div>
					</div>
		
					<div class="jqxwindow-action">
						<button type="button" class="btn btn-info btn-small" id="submitForm" style="z-index: 1000;">
							<i class="icon-save"></i>${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</form>
    	</div>
    </div>
</div>
<div id="popupEditAddress" class='hide'>
	<div class="title-border" style="margin-top: 0; margin-bottom: 10px;">
		${uiLabelMap.PartyAddressLine}
	</div>
	<div class="row-fluid">
		<#include "component://hrolbius/webapp/hrolbius/ftl/employee/updatePartyAddress.ftl"/>
	</div>
</div>
<div id="popupEditEmail" class='hide'>
	<div class="title-border" style="margin-top: 0; margin-bottom: 10px;">
		${uiLabelMap.FormFieldTitle_mailingListEmail}
	</div>
	<div class="row-fluid" style="margin-bottom: 10px;">	
		<#include "component://hrolbius/webapp/hrolbius/ftl/employee/updateEmail.ftl"/>
	</div>
</div>
<div id="popupEditTelecom" class='hide'>
	<div class="title-border" style="margin-top: 0; margin-bottom: 10px;">
		${uiLabelMap.PartyPhoneNumber}
	</div>
	<div class="row-fluid" style="margin-bottom: 10px;">
		<#include "component://hrolbius/webapp/hrolbius/ftl/employee/updateTelecomNumber.ftl"/>
	</div>
</div>

<@loadingContainer zIndex="18002"/>
<script>
	var listUom = [<#if listUom?exists><#list listUom as uom>{uomId: "${uom.uomId}",description: "${uom.description}",},</#list></#if>];
	$(document).ready(function(){
		var wd = $("#popupEditParty");
		wd.jqxWindow({
		    height: 450, 
		    width: 800,
		    // isModal: true,
		    theme: 'olbius'
		 });
		 var canClick = false;
		 wd.jqxValidator({
		   	rules: [{
				input: '#groupName',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			}, {
				input: '#numEmployees',
				message: '${StringUtil.wrapString(uiLabelMap.NumberRequired?default(''))}',
				action: 'blur',
				rule: function(input, commit){
					var val = input.val();
					if(!val || isNaN(val)){
						return false;
					}
					input.val(parseInt(val));
					return true;
				}
			}, {
				input: '#annualRevenue',
				message: '${StringUtil.wrapString(uiLabelMap.NumberRequired?default(''))}',
				action: 'blur',
				rule: function(input, commit){
					var val = input.val();
					if(!val || isNaN(val)){
						return false;
					}
					input.val(parseInt(val));
					return true;
				}
			}],
		 });
		 
		$("#ViewPartyGroup_address_title").click(function(){
			if(canClick){
				wdaddr.css("z-index", 9001);
				wdaddr.jqxWindow("open");
				showLoadingPopup();
			}
		});
		var wdaddr = $("#popupEditAddress");
		wdaddr.jqxWindow({
		    height: 800, 
		    width: 1000,
		    maxWidth: 1000,
		    theme: 'olbius',
		    // isModal: true,
		 });
		 initGridpartyGroupAddress();
		 $("#ViewPartyGroup_email_title").click(function(){
		 	if(canClick){
		 		wdaEmail.css("z-index", 9001);
			 	wdaEmail.jqxWindow("open");
			 	showLoadingPopup();	
		 	}
		 });
		 var wdaEmail = $("#popupEditEmail");
		 wdaEmail.jqxWindow({
		    height: 450, 
		    width: 800,
		    // isModal: true,
		    theme: 'olbius'
		 });
		 initGridpartyGroupEmail();
		 $("#ViewPartyGroup_phone_title").click(function(){
		 	if(canClick){
		 		wdaTelecom.css("z-index", 9001);
			 	wdaTelecom.jqxWindow("open");
			 	showLoadingPopup();	
		 	}
		 	
		 });
		 var wdaTelecom = $("#popupEditTelecom");
		 wdaTelecom.jqxWindow({
		   height: 450, 
		    width: 800,
		    // isModal: true,
		    theme: 'olbius'
		 });
		 initGridpartyGroupTelecom();
		 
		 $("#submitForm").click(function(){
		 	if(!wd.jqxValidator("validate")){
		 		return;
		 	}
		 	var data = {};
		 	$.each($('#EditPartyGroup').serializeArray(), function(_, kv) {
		 		if(kv.value && kv.value.replace(/\s/g, '').length){
		 			data[kv.name] = kv.value;	
		 		}
			});
			var id = $('#preferredCurrencyUomId').jqxDropDownList("getSelectedIndex");
			if(id != -1){
				var preferredCurrencyUomId = listUom[id].uomId;
				data.preferredCurrencyUomId = preferredCurrencyUomId;	
			}
		 	$.ajax({
		 		url : "updatePartyGroupJson",
		 		type: "POST",
		 		data: data,
		 		success: function(res){
		 			if(res && !res['_ERROR_MESSAGE_LIST_']){
		 				window.location.reload();
		 			}
		 		}
		 	});
		 });
	    var currency = $('#preferredCurrencyUomId');
		currency.jqxDropDownList({
			theme: 'olbius',
			source: listUom,
			width: 209,
			filterable: true,
			searchMode: 'containsignorecase',
			incrementalSearch: true,
			displayMember: "description"
		});
		
		var initPopup = function(wdaddr,wdaEmail, wdaTelecom){
			 wdaddr.css('z-index', "-1");
			 wdaEmail.css('z-index', "-1");
			 wdaTelecom.css('z-index', "-1");
			 wdaddr.jqxWindow("open");
			 wdaEmail.jqxWindow("open");
			 wdaEmail.jqxWindow("open");
			 setTimeout(function(){
			 	canClick = true;
			    wdaddr.jqxWindow("close");
			    wdaTelecom.jqxWindow("close");
			    wdaEmail.jqxWindow("close");
			 }, 100);
		}
		initPopup(wdaddr,wdaEmail, wdaTelecom);
	});
	function showPopup(){
		$("#popupEditParty").jqxWindow("open");		
	}
	function showLoadingPopup(time){
		var s = time ? time : 100;
		showLoading("loadingdefault");
		setTimeout(function(){
			hideLoading("loadingdefault");	
		}, s);
	}
	
</script>