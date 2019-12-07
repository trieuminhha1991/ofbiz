<#include 'script/listAllFacilityPartyRoleScript.ftl'/>
<div id="jqxgridFacilityRole">
</div>

<div id="alterPopup">
</div>

<div id='contextMenuParent' class="hide">
	<ul>
	    <li><i class="fa fa-plus"></i>${StringUtil.wrapString(uiLabelMap.AddRole)}</li>       
	</ul>
</div>

<div id='contextMenuChild' class="hide">
	<ul>
	    <li><i class="fa red fa-trash"></i>${StringUtil.wrapString(uiLabelMap.ThruDateRole)}</li>       
	</ul>
</div>
<form id= "initAddFacilityParty">
	<div id="AddPartyRoleFacility" class="hide popup-bound">
		<div>${uiLabelMap.AddFacilityPartyRole}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class="row-fluid margin-top20">
		    		<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div class="asterisk">${uiLabelMap.Facility}</div>
						</div>
						<div class="span7">	
							<input type="hidden" value=""></input>
							<div id="facilityName" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div class="asterisk">${uiLabelMap.CommonParty}</div>
						</div>
						<div class="span7">	
							<div id="partyId" class="green-label">
								<div id="jqxgridListParty">
					            </div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div class="asterisk">${uiLabelMap.Role}</div>
						</div>
						<div class="span7">	
							<div id="roleTypeId" class="green-label">
								<div id="jqxgridListRoleType">
					            </div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div class="asterisk">${uiLabelMap.BLEffectiveDate}</div>
						</div>
						<div class="span7">	
							<div id="roleTypeId" class="green-label">
								<div id="fromDate">
					            </div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span4" style="text-align: right">
							<div>${uiLabelMap.BLExpiryDate}</div>
						</div>
						<div class="span7">	
							<div id="roleTypeId" class="green-label">
								<div id="thruDate">
					            </div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
		        <button id="addCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    	<button id="addSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</form>