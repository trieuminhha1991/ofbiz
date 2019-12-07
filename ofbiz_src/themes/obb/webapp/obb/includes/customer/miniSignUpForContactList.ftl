<div class="block-title hide">
    <strong><span>${uiLabelMap.ObbSignUpForContactList}</span></strong>
</div>
<div class="block-content hide">
    <#if sessionAttributes.autoName?has_content>
	  <#-- The visitor potentially has an account and party id -->
	    <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
	    <#-- They are logged in so lets present the form to sign up with their email address -->
	      <form method="post" action="<@ofbizUrl>createContactListParty</@ofbizUrl>" name="signUpForContactListForm" id="signUpForContactListForm">
	        <fieldset class="subs-form">
	          <#assign contextPath = request.getContextPath()>
	          <#assign partyId = userLogin.partyId/>
	          <input type="hidden" name="baseLocation" value="${contextPath}"/>
	          <input type="hidden" name="partyId" value="${partyId}"/>
	          <input type="hidden" id="statusId" name="statusId" value="CLPT_PENDING"/>
	          <div class="form-subscribe-header">
			<label>${uiLabelMap.ObbSignUpForContactListComments}</label>
	          </div>
	          <div class="float-left">
			<#if publicEmailContactLists?has_content>
			<@contactList publicEmailContactLists=publicEmailContactLists/><br/>
		</#if>
	          </div>
	          <div class="float-left">
	            <select name="preferredContactMechId" id="preferredContactMechId" class="required-entry subs-select">
	              <#list partyAndContactMechList as partyAndContactMech>
	                <option value="${partyAndContactMech.contactMechId}"><#if partyAndContactMech.infoString?has_content>${partyAndContactMech.infoString}<#elseif partyAndContactMech.tnContactNumber?has_content>${partyAndContactMech.tnCountryCode?if_exists}-${partyAndContactMech.tnAreaCode?if_exists}-${partyAndContactMech.tnContactNumber}<#elseif partyAndContactMech.paAddress1?has_content>${partyAndContactMech.paAddress1}, ${partyAndContactMech.paAddress2?if_exists}, ${partyAndContactMech.paCity?if_exists}, ${partyAndContactMech.paStateProvinceGeoId?if_exists}, ${partyAndContactMech.paPostalCode?if_exists}, ${partyAndContactMech.paPostalCodeExt?if_exists} ${partyAndContactMech.paCountryGeoId?if_exists}</#if></option>
	              </#list>
	            </select>
	          </div>
	          <div>
	            <button type="submit" style="background: #F04E46;" title="${uiLabelMap.ObbSubscribe}" class="button"><span><span>${uiLabelMap.ObbSubscribe}</span></span></button>
	            <button type="submit" style="background: #666;" onclick="javascript:unsubscribeByContactMech();" title="${uiLabelMap.ObbUnsubscribe}" class="button"><span><span>${uiLabelMap.ObbUnsubscribe}</span></span></button>
	          </div>
	        </fieldset>
	      </form>
	    <#else>
	    <#-- Not logged in so ask them to log in and then sign up or clear the user association -->
	      <p>${uiLabelMap.ObbSignUpForContactListLogIn}</p>
	      <p><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a> ${sessionAttributes.autoName}</p>
	      <p>(${uiLabelMap.CommonNotYou}? <a href="<@ofbizUrl>autoLogout</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)</p>
	    </#if>
	  <#else>
	  <#-- There is no party info so just offer an anonymous (non-partyId) related newsletter sign up -->
	    <form method="post" action="<@ofbizUrl>signUpForContactList</@ofbizUrl>" name="signUpForContactListForm" id="signUpForContactListForm">
	      <fieldset class="subs-form">
	        <#assign contextPath = request.getContextPath()>
	        <input type="hidden" name="baseLocation" value="${contextPath}"/>
	        <input type="hidden" id="statusId" name="statusId"/>
	        <div class="form-subscribe-header">
	          <label>${uiLabelMap.ObbSignUpForContactListComments}</label>
	        </div>
	        <div class="subs-select-box">
			<#if publicEmailContactLists?has_content>
				<@contactList publicEmailContactLists=publicEmailContactLists/><br/>
			</#if>
		</div>
	        <div class="input-box subs-input-box">
			<input name="email" id="email" style="margin:0px;" class="input-text required-entry validate-email" type="email"/>
		</div>
	        <div class="button-subs">
	          <button type="submit" style="background: #F04E46;margin-left:5px;" title="${uiLabelMap.ObbSubscribe}" class="button"><span><span>${uiLabelMap.ObbSubscribe}</span></span></button>
	          <!-- <button type="submit" style="background: #F04E46;" onclick="javascript:unsubscribe();" title="${uiLabelMap.ObbUnsubscribe}" class="button"><span><span>${uiLabelMap.ObbUnsubscribe}</span></span></button> -->
	        </div>
	      </fieldset>
	    </form>
	  </#if>
</div>


<#-- A simple macro that builds the contact list -->
<#macro contactList publicEmailContactLists>
  <select name="contactListId" id="contactListId" class="required-entry subs-select">
    <#list publicEmailContactLists as publicEmailContactList>
      <#assign publicContactMechType = publicEmailContactList.contactList.getRelatedOne("ContactMechType", true)?if_exists>
        <option value="${publicEmailContactList.contactList.contactListId}">${publicEmailContactList.contactListType.description?if_exists} - ${publicEmailContactList.contactList.contactListName?if_exists}</option>
    </#list>
  </select>
</#macro>

<script type="text/javascript" language="JavaScript">
    function unsubscribe() {
	if(!document.getElementById("email").value || document.getElementById("email").value=="" || validateEmail(document.getElementById("email").value)){
		return;
	}
	if(!document.getElementById("contactListId").value || document.getElementById("contactListId").value==""){
		return;
	}
        var form = document.getElementById("signUpForContactListForm");
        form.action = "<@ofbizUrl>unsubscribeContactListParty</@ofbizUrl>"
        document.getElementById("statusId").value = "CLPT_UNSUBS_PENDING";
        form.submit();
    }
    function validateEmail(email) {
	    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	    return re.test(email);
	}
    function unsubscribeByContactMech() {
	if(!document.getElementById("preferredContactMechId").value || document.getElementById("preferredContactMechId").value==""){
		return;
	}
	if(!document.getElementById("contactListId").value || document.getElementById("contactListId").value==""){
		return;
	}
        var form = document.getElementById("signUpForContactListForm");
        form.action = "<@ofbizUrl>unsubscribeContactListPartyContachMech</@ofbizUrl>"
        document.getElementById("statusId").value = "CLPT_UNSUBS_PENDING";
        form.submit();
    }
</script>
