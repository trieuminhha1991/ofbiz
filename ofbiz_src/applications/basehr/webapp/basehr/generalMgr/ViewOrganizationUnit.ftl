<#include "script/ViewOrganizationUnitScript.ftl"/>
<script type="text/javascript" src="/hrresources/js/generalMgr/viewOrganizationUnit.js"></script>
<#if hasOlbEntityPermission("ORG_MGR", "CREATE")>
	<#assign hasCreatePermission = true/>
<#else>
	<#assign hasCreatePermission = false/>
</#if>
<#if hasCreatePermission>
	<script type="text/javascript" src="/hrresources/js/generalMgr/EditOrganizationUnit.js?v=0.0.1"></script>
</#if>
<#if hasCreatePermission>
	<#include "CreateNewOrganization.ftl"/>
</#if>

<div class="widget-box transparent no-bottom-border">
	<div id="jqxNotifyContainer">
	</div>
	<div id="jqxNotification">
		<div id="notificationContentNtf"></div>
	</div>
	<div class="widget-header">
		<h4>${uiLabelMap.OrganizationUnit}</h4>
		<span class="widget-toolbar none-content">
			<#if hasCreatePermission>
			<button id="addrowbutton" class="pull-right grid-action-button icon-plus open-sans">
				${uiLabelMap.accAddNewRow}</button>
			</#if>	
		</span>
	</div>
	<div class="widget-body">
		<div id="treePartyGroupGrid"></div>		
	</div>
</div>
<div id="partyGroupDetailWindow" class="hide">
	<div>${uiLabelMap.CommonEdit} ${uiLabelMap.OrganizationUnit}</div>
	<div class='form-window-container'>
	 	<div class='form-window-content'>
 			<div class='row-fluid margin-bottom10'>
 				<div class='span4 align-right'>
 					<label>
	 					${uiLabelMap.OrgUnitId}
 					</label>
 				</div>
 				<div class='span8'>
 					<input type="text" id="orgUnitId"/>
				</div>
 			</div>
 			<div class='row-fluid margin-bottom10'>
 				<div class='span4 align-right'>
 					<label>
	 					${uiLabelMap.OrgUnitName}
 					</label>
 				</div>
 				<div class='span8'>
 					<input type="text" id="orgUnitName"/>
				</div>
 			</div>
 			<div class='row-fluid margin-bottom10'>
 				<div class='span4 align-right'>
 					<label>
	 					${uiLabelMap.BelongOrgUnit}
 					</label>
 				</div>
 				<div class='span8'>
 					<div id="belongOrgUnitButton">
 						 <div style="border: none;" id='jqxTreeOrgUnit'>
 						 </div>
 					</div>
				</div>
 			</div>
 			<div class='row-fluid margin-bottom10'>
 				<div class='span4 align-right'>
 					<label>
	 					${uiLabelMap.CommonAddress}
 					</label>
 				</div>
 				<div class='span8'>
 					<div id="orgAddress"></div>	 		
 					
				</div>
 			</div>
 			<div class='row-fluid margin-bottom10'>
 				<div class='span4 align-right'>
 					<label>
	 					${uiLabelMap.FunctionAndDuties}
 					</label>
 				</div>
 				<div class='span8'>
 					<textarea id="comments"></textarea>
				</div>
 			</div>
	 		
	 	</div>
	 	<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button class='btn btn-danger form-action-button pull-right' id="partyGroupDetailClose"><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
					<button class='btn btn-primary form-action-button pull-right' id="partyGroupSave"><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id='contextMenu' class="hide">
    <ul>
        <li action="editCommentInfo">
            <i class="icon-edit open-sans"></i>${uiLabelMap.HREditComment}
        </li>
        <li action="emplInOrgList">
            <i class="fa-list-ol"></i>${uiLabelMap.HREmplList}
        </li>
    </ul>
</div>
<div id="popupWindowEmplListInOrg" class="hide">
    <div>${uiLabelMap.HREmplList}</div>
    <div class='form-window-container'>
        <div class='form-window-content'>
            <div class="row-fluid">
                <div id="emplListInOrgGrid"></div>
            </div>
        </div>
        <div class="form-action">
            <button type="button" class='btn btn-danger form-action-button pull-right' id="closeEmplListInOrg">
                <i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
        </div>
    </div>
</div>
<div id="partyContactMechWindow" style="display: none;">
	<div id="contactMechWindowHeader"></div>
	<div style="overflow: hidden;">
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<form name="editContactMech" id="editContactMech">
				<div class='row-fluid margin-bottom10'>
					<div class='span12'>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label>${uiLabelMap.CommonCountry}</label>
							</div>
							<div class='span8'>
								<div id="prCountry"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label>${uiLabelMap.PartyState}</label>
							</div>
							<div class='span8'>
								<div id="prProvince"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label>${uiLabelMap.PartyDistrictGeoId}</label>
							</div>
							<div class='span8'>
								<div id="prDistrict"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label>${uiLabelMap.PartyWardGeoId}</label>
							</div>
							<div class='span8'>
								<div id="prWard"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.PartyAddressLine}</label>
							</div>
							<div class='span8'>
								<input type="text" name="prAddress" id="prAddress"/>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button class='btn btn-danger form-action-button pull-right' id="contactMechCancel"><i class='fa-remove'></i>${uiLabelMap.CommonCancel}</button>
					<button class='btn btn-primary form-action-button pull-right' id="contactMechSave"><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

