<div id="emplFamilyWindow" class="hide">
	<div>${uiLabelMap.HREmployeeFamilyInfo}</div>
	<div class='form-window-container'>
		<div id="containerviewDetailFamilyGrid" style="background-color: transparent; overflow: auto;">
	    </div>
	    <div id="jqxNotificationviewDetailFamilyGrid">
	        <div id="notificationContentviewDetailFamilyGrid">
	        </div>
	    </div>
		<div id="viewDetailFamilyGrid"></div>
	</div>
</div>
${setContextField("addNewWindowId", "addEmplFamilyWindow")}
<#include "component://basehr/webapp/basehr/profile/AddPersonFamilyBackground.ftl"/>


<div id='contextMenuFamily' class="hide">
	<ul>
		<li action="approval" id="approvalFamilyDependent">
			<i class="fa-paint-brush"></i>${uiLabelMap.HRApprove}
        </li>        
	</ul>
</div>

<div id="apprFamilyDependentWindow" class="hide">
	<div>${uiLabelMap.HRApprove}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.HRFullName}</label>
				</div>
				<div class="span7">
					<input type="text" id="familyFullName"/>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.HRRelationship}</label>
				</div>
				<div class="span7">
					<div id="familyRelation"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.BirthDate}</label>
				</div>
				<div class="span7">
					<div id="familyBirthDate"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.DependentDeductionStart}</label>
				</div>
				<div class="span7">
					<div id="familyDeductionStart"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.DependentDeductionEnd}</label>
				</div>
				<div class="span7">
					<div id="familyDeductionEnd"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.CommonStatus}</label>
				</div>
				<div class="span7">
					<div id="familyCurrStatus"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.HRApprove}</label>
				</div>
				<div class="span7">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="acceptAppr">${uiLabelMap.HRCommonAccept}</div>		
							</div>
							<div class="span4">
								<div id="rejectAppr">${uiLabelMap.HRReject}</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row-fluid no-left-margin">
				<div id="loadingFamilyAppr" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerFamilyAppr"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="cancelFamilyAppr"><i class='icon-remove'></i>${uiLabelMap.CommonClose}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="saveFamilyAppr"><i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<#if security.hasEntityPermission("HR_DIRECTORY", "_UPDATE", session)>
	<script type="text/javascript" src="/hrresources/js/generalMgr/EditEmployeeFamilyInfo.js"></script>
<#else>
	<script type="text/javascript" src="/hrresources/js/generalMgr/EditEmployeeFamilyInfoSalesModule.js"></script>
</#if>