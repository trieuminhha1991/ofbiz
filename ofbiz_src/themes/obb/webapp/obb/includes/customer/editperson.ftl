<style type="text/css">
	table td{
		padding-left:5px;
		padding-top:10px;
		vertical-align: middle;
	}
	table td:first-child{
		width:200px;
	}
	table th{
		padding-left:5px;
	}
	table input[type=text]{
		width:200px;
	}
	table select{
		width:222px;
	}
</style>
<div id="jm-main">
	<div class="inner clearfix">
		<div id="jm-current-content" class="clearfix">
<#if person?exists>
  <h2>${uiLabelMap.PartyEditPersonalInformation}</h2>
    &nbsp;<form id="editpersonform1" method="post" action="<@ofbizUrl>updatePerson</@ofbizUrl>" name="editpersonform">
<#else>
  <h2>${uiLabelMap.PartyAddNewPersonalInformation}</h2>
    &nbsp;<form id="editpersonform2" method="post" action="<@ofbizUrl>createPerson/${donePage}</@ofbizUrl>" name="editpersonform">
</#if>
<div>
	  &nbsp;<a href='<@ofbizUrl>${donePage}</@ofbizUrl>' class="button">${uiLabelMap.CommonGoBack}</a>
	  <p/>
	  <input type="hidden" name="partyId" value="${person.partyId?if_exists}" />
	  <table width="90%" border="0" cellpadding="2" cellspacing="0" class="form-list">
	  <tr>
	    <td align="right"><label for="personalTitle">${uiLabelMap.CommonTitle}</label></td>
	    <td>
	      <select name="personalTitle" id="personalTitle" class="selectBox">
	        <#if personData.personalTitle?has_content >
	          <option>${personData.personalTitle}</option>
	          <option value="${personData.personalTitle}"> -- </option>
	        <#else>
	          <option value="">${uiLabelMap.CommonSelectOne}</option>
	        </#if>
	        <option>${uiLabelMap.CommonTitleMr}</option>
	        <option>${uiLabelMap.CommonTitleMrs}</option>
	      </select>
	    </td>
	  </tr>
	  <tr>
	    <td align="right"><label for="firstName" class="required"><em>*</em>${uiLabelMap.PartyFirstName}</label></td>
	      <td>
	        <input type="text" class='input-text required-entry' size="30" maxlength="30" name="firstName" id="firstName" value="${personData.firstName?if_exists}"/>
	      </td>
	    </tr>
	    <tr>
	      <td align="right"><label for="middleName">${uiLabelMap.PartyMiddleInitial}</label></td>
	      <td>
	        <input type="text" class='input-text' size="4" maxlength="4" name="middleName" id="middleName" value="${personData.middleName?if_exists}"/>
	      </td>
	    </tr>
	    <tr>
	      <td align="right"><label for="lastName" class="required"><em>*</em>${uiLabelMap.PartyLastName}</label></td>
	      <td>
	        <input type="text" class='input-text required-entry' size="30" maxlength="30" name="lastName" id="lastName" value="${personData.lastName?if_exists}"/>
	      </td>
	    </tr>
	    <tr>
	      <td align="right"><label for="gender">${uiLabelMap.PartyGender}</label></td>
	      <td>
	        <select name="gender" class='selectBox' id="gender">
	          <#if personData.gender?has_content >
	            <option value="${personData.gender}">
	                <#if personData.gender == "M" >${uiLabelMap.CommonMale}</#if>
	                <#if personData.gender == "F" >${uiLabelMap.CommonFemale}</#if>
	            </option>
	            <option value="${personData.gender}"> -- </option>
	          <#else>
	            <option value="">${uiLabelMap.CommonSelectOne}</option>
	          </#if>
	          <option value="M">${uiLabelMap.CommonMale}</option>
	          <option value="F">${uiLabelMap.CommonFemale}</option>
	        </select>
	      </td>
	    </tr>
	    <tr>
	      <td align="right"><label for="birthDate">${uiLabelMap.PartyBirthDate}</label></td>
	      <td>
	        <input type="text" class='input-text' size="11" maxlength="20" name="birthDate" id="birthDate" value="${(personData.birthDate.toString())?if_exists}"/>
	        <div>${uiLabelMap.CommonFormatDate}</div>
	      </td>
	    </tr>
	</table>
		<div class="buttons-set">
	        <p class="required">* Required Fields</p>
	        <p class="back-link"><a href='<@ofbizUrl>${donePage}</@ofbizUrl>' class="button">${uiLabelMap.CommonGoBack}</a></p>
	        <button type="submit" title="${uiLabelMap.CommonSave}" class="button"><span><span>${uiLabelMap.CommonSave}</span></span></button>
	    </div>
	</div>
	</form>
	<script type="text/javascript">
	    //<![CDATA[
		<#if person?exists>
			  var dataFormTmp = new VarienForm('editpersonform1', true);
			<#else>
			  var dataFormTmp = new VarienForm('editpersonform2', true);
			</#if>
	    //]]>
	</script>
</div>
</div>
</div>