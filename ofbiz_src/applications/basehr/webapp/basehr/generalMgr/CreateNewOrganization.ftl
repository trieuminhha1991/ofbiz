<script type="text/javascript" src="/hrresources/js/generalMgr/CreateNewOrganization.js"></script>
<div id="newPartyGroupWindow" style="display: none;">
	<div id="newPartyGroupWindowHeader">${uiLabelMap.HrolbiusAddOrganizationUnit}</div>
	<div style="overflow: hidden;">
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<div id="newPartyGroupForm">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.OrgUnitId}</label>
						</div>
						<div class='span8'>
							<input type="text" id="orgUnitIdCreateNew"/>
						</div>
					</div>
					
					<div class='row-fluid margin-bottom10'>
		 				<div class='span4 align-right'>
		 					<label>${uiLabelMap.BelongOrgUnit}</label>
		 				</div>
		 				<div class='span8'>
		 					<div id="belongOrgUnitButtonCreateNew">
		 						 <div style="border: none;" id='jqxTreeOrgUnitCreateNew'>
		 						 </div>
		 					</div>
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.OrgUnitName}</label>
						</div>
						<div class='span8'>
							<input type="text" id="orgUnitNameCreateNew"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right '>
							<label class="asterisk">${uiLabelMap.CommonRole}</label>
						</div>
						<div class='span8'>
		 					<div id="roleTypeListDropDownButton">
		 						 <div style="border: none;" id='jqxTreeRoleTypeNew'>
		 						 </div>
		 					</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label>${uiLabelMap.CommonSymbol}</label>
						</div>
						<div class='span8'>
							<form class="no-margin" action="" class="row-fluid" id="upLoadFileForm"  method="post" enctype="multipart/form-data">
								<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
								<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
								<div class="rowf-fluid">
									<div class="span12" style="margin-bottom: 0px !important; height: 0px !important">
							 			<input type="file" id="uploadedFile" name="uploadedFile"/>
							 		</div>
								</div>
						 	</form>
						</div>
					</div>
		 			
		 			
				</div>
				<div class="span6">
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span4 align-right'>
		 					<label>${uiLabelMap.CommonCountry}</label>
		 				</div>
		 				<div class='span8'>
							<div id="countryPartyGroupNew"></div>	 					
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span4 align-right'>
		 					<label>${uiLabelMap.PartyState}</label>
		 				</div>
		 				<div class='span8'>
							<div id="statePartyGroupNew"></div>	 					
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span4 align-right'>
		 					<label>${uiLabelMap.PartyDistrictGeoId}</label>
		 				</div>
		 				<div class='span8'>
							<div id="districtPartyGroupNew"></div>	 					
						</div>
		 			</div>
					
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span4 align-right'>
		 					<label>${uiLabelMap.PartyWardGeoId}</label>
		 				</div>
		 				<div class='span8'>
							<div id="wardPartyGroupNew"></div>	 					
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span4 align-right'>
		 					<label>${uiLabelMap.CommonAddress}</label>
		 				</div>
		 				<div class='span8'>
							<input type="text" id="addressOrgUnitCreateNew"/>	 					
						</div>
		 			</div>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class='row-fluid margin-bottom10'>
							<div class='span2 align-right'>
			 					<label>${uiLabelMap.FunctionAndDuties}</label>
			 				</div>
			 				<div class='span10'>
								<textarea id="commentsNewPartyGroup"></textarea>	 					
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button class='btn btn-danger form-action-button pull-right' id="partyGroupCreateNewCancel"><i class='fa-remove'></i>${uiLabelMap.CommonCancel}</button>
					<button class='btn btn-primary form-action-button pull-right' id="partyGroupCreateNewSave"><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>	