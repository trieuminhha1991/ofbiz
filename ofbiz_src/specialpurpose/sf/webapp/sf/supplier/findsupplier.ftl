
<#assign sortField = parameters.sortField?if_exists/>
<#-- Only allow the search fields to be hidden when we have some results -->
<!--<#if partyList?has_content>
  <#assign hideFields = parameters.hideFields?default("N")>
<#else>
  <#assign hideFields = "N">
</#if>-->
<div class="widget-box olbius-extra">
  <div >
   <div class="widget-toolbar">
   </div>
  </div>
	<div >
	<div >
	<div class="widget-main">
  <!-- 	<#if partyList?has_content>
	<#if hideFields == "Y">
	<a class="icon-chevron-down" href="<@ofbizUrl>findsupplier?hideFields=N&sortField=${sortField?if_exists}${paramList}</@ofbizUrl>" title="${uiLabelMap.CommonShowLookupFields}">&nbsp;</a>
	<#else>
	<a class="icon-chevron-up" href="<@ofbizUrl>findsupplier?hideFields=Y&sortField=${sortField?if_exists}${paramList}</@ofbizUrl>" title="${uiLabelMap.CommonHideFields}">&nbsp;</a>
	</#if>
	</#if>-->
    <div id="findPartyParameters"> <!--<#if hideFields != "N"> style="display:none" </#if> -->
      <#-- NOTE: this form is setup to allow a search by partial partyId or userLoginId; to change it to go directly to
          the viewprofile page when these are entered add the follow attribute to the form element:

           onsubmit="javascript:lookupParty('<@ofbizUrl>viewprofile</@ofbizUrl>');"
       -->
      <form method="post" name="lookupparty" action="<@ofbizUrl>findsupplier</@ofbizUrl>" class="basic-form form-padding">
        <input type="hidden" name="lookupFlag" value="Y"/>
        <input type="hidden" name="hideFields" value="Y"/>
        <table class="basic-table" cellspacing="0">
          <tr>
            <td >${uiLabelMap.PartyPartyId}</td>
            <td><input type="text" name="partyId" value="${parameters.partyId?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyUserLogin}</td>
            <td><input type="text" name="userLoginId" value="${parameters.userLoginId?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyLastName}</td>
            <td><input type="text" name="lastName" value="${parameters.lastName?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.PartyFirstName}</td>
            <td><input type="text" name="firstName" value="${parameters.firstName?if_exists}"/></td>
          </tr>
          <tr>
            <td >${uiLabelMap.sfFacebookId}</td>
            <td><input type="text" name="inventoryItemId" value="${parameters.facebookId?if_exists}"/></td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>
              <button type="submit" class="btn btn-small btn-primary" onclick="javascript:document.lookupparty.submit();">
              <i class="icon-search"></i>
              ${uiLabelMap.CommonFind}
              </button>
            </td>
          </tr>
        </table>
      </form>
    </div>
    <script language="JavaScript" type="text/javascript">
      document.lookupparty.partyId.focus();
    </script>
</div>
</div>
</div>
</div>