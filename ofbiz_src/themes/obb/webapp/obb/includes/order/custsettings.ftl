<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
						<div class="cart">
						    <div class="page-title title-buttons">
								<h3>${uiLabelMap.ObbYourNamePhoneAndEmail}</h3>
							</div>
								<form id="editCustomerNamePhoneAndEmail" name="${parameters.formNameValue}" method="post" action="<@ofbizUrl>processCustomerSettings</@ofbizUrl>">
								  <input type="hidden" name="partyId" value="${parameters.partyId?if_exists}"/>
								  <fieldset>
								    <div>
								    <span>
								      <label for="personalTitle">${uiLabelMap.CommonTitle}</label>
								      <select name="personalTitle">
								          <#if requestParameters.personalTitle?has_content >
								            <option>${parameters.personalTitle}</option>
								            <option value="${parameters.personalTitle}"> -- </option>
								          <#else>
								            <option value="">${uiLabelMap.CommonSelectOne}</option>
								          </#if>
								          <option>${uiLabelMap.CommonTitleMr}</option>
								          <option>${uiLabelMap.CommonTitleMrs}</option>
								          <option>${uiLabelMap.CommonTitleMs}</option>
								          <option>${uiLabelMap.CommonTitleDr}</option>
								      </select>
								    </span>
								    <span>
								      <label for="firstName">${uiLabelMap.PartyFirstName}</label>
								      <input type="text" class='input-text' name="firstName" value="${parameters.firstName?if_exists}" /> *
								    </span>
								    <span>
								      <label for="middleName">${uiLabelMap.PartyMiddleInitial}</label>
								      <input type="text" class='input-text' name="middleName" value="${parameters.middleName?if_exists}" />
								    </span>
								    <span>
								      <label for="lastName">${uiLabelMap.PartyLastName}</label>
								      <input type="text" class='input-text' name="lastName" value="${parameters.lastName?if_exists}" /> *
								    </span>
								    <span>
								      <label for="suffix">${uiLabelMap.PartySuffix}</label>
								      <input type="text" class='input-text' name="suffix" value="${parameters.suffix?if_exists}" />
								    </span>
								    </div>
								  </fieldset>

								  <table id="shopping-cart-table" class="data-table cart-table" summary="Tabular form for entering multiple telecom numbers for different purposes. Each row allows user to enter telecom number for a purpose">
								  <caption>${uiLabelMap.PartyPhoneNumbers}</caption>
								    <tr>
								      <th></th>
								      <th scope="col">${uiLabelMap.CommonCountry}</th>
								      <th scope="col">${uiLabelMap.PartyAreaCode}</th>
								      <th scope="col">${uiLabelMap.PartyContactNumber}</th>
								      <th scope="col">${uiLabelMap.PartyExtension}</th>
								      <th scope="col">${uiLabelMap.PartyAllowSolicitation}</th>
								    </tr>
								    <tr>
								    <th scope="row">${uiLabelMap.PartyHomePhone}</th>
								    <input type="hidden" name="homePhoneContactMechId" value="${parameters.homePhoneContactMechId?if_exists}"/>
								    <td><input type="text" class='input-text' name="homeCountryCode" value="${parameters.homeCountryCode?if_exists}" /></td>
								    <td><input type="text" class='input-text' name="homeAreaCode" value="${parameters.homeAreaCode?if_exists}" /></td>
								    <td><input type="text" class='input-text' name="homeContactNumber" value="${parameters.homeContactNumber?if_exists}" /></td>
								    <td><input type="text" class='input-text' name="homeExt" value="${parameters.homeExt?if_exists}" /></td>
								    <td>
								      <select name="homeSol">
								        <#if (((parameters.homeSol)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
								        <#if (((parameters.homeSol)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
								        <option></option>
								        <option value="Y">${uiLabelMap.CommonY}</option>
								        <option value="N">${uiLabelMap.CommonN}</option>
								      </select>
								    </td>
								  </tr>
								  <tr>
								    <th scope="row">${uiLabelMap.PartyBusinessPhone}</th>
								    <input type="hidden" name="workPhoneContactMechId" value="${parameters.workPhoneContactMechId?if_exists}"/>
								    <td><input type="text" class='input-text' name="workCountryCode" value="${parameters.workCountryCode?if_exists}" /></td>
								    <td><input type="text" class='input-text' name="workAreaCode" value="${parameters.workAreaCode?if_exists}" /></td>
								    <td><input type="text" class='input-text' name="workContactNumber" value="${parameters.workContactNumber?if_exists}" /></td>
								    <td><input type="text" class='input-text' name="workExt" value="${parameters.workExt?if_exists}" /></td>
								    <td>
								      <select name="workSol">
								        <#if (((parameters.workSol)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
								        <#if (((parameters.workSol)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
								        <option></option>
								        <option value="Y">${uiLabelMap.CommonY}</option>
								        <option value="N">${uiLabelMap.CommonN}</option>
								      </select>
								    </td>
								  </tr>
								</table>
								  <fieldset>
								    <div style="margin-top:10px;margin-bottom:10px;">
								    <span>
								      <label for="emailAddress">${uiLabelMap.PartyEmailAddress}</label>
								      <input type="hidden" name="emailContactMechId" value="${parameters.emailContactMechId?if_exists}"/>
								      <input type="text" class="input-text" name="emailAddress" value="${parameters.emailAddress?if_exists}"/> *
								    </span>
								    <span>
								      <label for="emailSol">${uiLabelMap.PartyAllowSolicitation}</label>
								      <select name="emailSol" class="selectBox">
								        <#if (((parameters.emailSol)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
								        <#if (((parameters.emailSol)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
								        <option></option>
								        <option value="Y">${uiLabelMap.CommonY}</option>
								        <option value="N">${uiLabelMap.CommonN}</option>
								      </select>
								    </span>
								   </div>
								  <div class="buttons">
								    <button type="submit" value="empty_cart" title="${uiLabelMap.CommonContinue}" class="button btn-empty" id="empty_cart_button">
								<span><span>${uiLabelMap.CommonContinue}</span></span>
				                    </button>
								  </div>
								  </fieldset>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>