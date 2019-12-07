<#if !newKPIWindow?has_content>
	<#assign newKPIWindow = "newKPIWindow"/>
</#if>
<#include "script/kpiCreateNewWindowScript.ftl"/>
<div class="row-fluid" style="position: relative;">
	<div id="${newKPIWindow}" class="hide">
		<div>${uiLabelMap.CreateNewKPI}</div>
		<div class='form-window-container' >
			<div class="form-window-content">
				<div class="row-fluid">
					<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
						<ul class="wizard-steps wizard-steps-square">
							<li data-target="#createNewKpi${newKPIWindow}" class="active">
								<span class="step">1. ${uiLabelMap.KPIInfoSetup}</span>
							</li>
							<li data-target="#kpiPolicySetup${newKPIWindow}">
								<span class="step">2. ${uiLabelMap.KPIPolicySetup}</span>
							</li>
						</ul>
					</div>
					<div class="step-content row-fluid position-relative" id="step-container">
						<div class="step-pane active" id="createNewKpi${newKPIWindow}">
							<div id ="contentPanel">
							<div class="span12" style="margin-top: 20px">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 align-right'>
										<label class="asterisk">${uiLabelMap.HRCommonFields}</label>
									</div>
									<div class='span8'>
										<div class="row-fluid">
											<div class="span12">
												<div class="span10">
													<div id="CriteriaType"></div>
												</div>			
												<div class="span2">
													<button class="btn btn-mini btn-primary" style="width: 80%" type="button" id="viewListCriteriaTypeBtn" 
														title="${StringUtil.wrapString(uiLabelMap.ViewListCriteriaType)}">
														<i class="icon-only icon-align-justify"></i>
													</button>
												</div>			
											</div>
										</div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 align-right'>
										<label class="asterisk">${uiLabelMap.HRCommonKPIName}</label>
									</div>
									<div class='span8'>
										<input type="text" id="CriteriaName">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 align-right'>
										<label class="">${uiLabelMap.HRDescriptionKPI}</label>
									</div>
									<div class='span8'>
										<textarea id="descriptionKPI"></textarea>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 align-right'>
										<label class="asterisk">${uiLabelMap.HRFrequency}</label>
									</div>
									<div class='span8'>
										<div id="periodTypeNew"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 align-right'>
										<label class="asterisk">${uiLabelMap.HRTarget}</label>
									</div>
									<div class='span8'>
										<div id="targetNumberNew"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 align-right'>
										<label class="asterisk">${uiLabelMap.HRCommonUnit}</label>
									</div>
									<div class='span8'>
										<div class="row-fluid">
											<div class="span12">
												<div class="span10">
													<div id="uomIdNew"></div>
												</div>
												<div class="span2">
														<button id="addNewUomId" style="width: 80%" title="${uiLabelMap.AddNew}" class="btn btn-mini btn-primary">
															<i class="icon-only icon-plus open-sans" style="font-size: 15px"></i>
														</button>
												</div>							
											</div>
										</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class='span4 align-right'>
										<label class="asterisk">${uiLabelMap.KPIDevelopmentTrend}</label>
									</div>
									<div class='span8'>
										<div id="perfCriDevelopmetTypeNew"></div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class='span4 align-right'>
										<label class=""></label>
									</div>
									<div class='span8'>
										<#if perfCriDevelopmentTypeList?has_content>
											<#list perfCriDevelopmentTypeList as perfCriDevelopmentType>
												<div class="row-fluid" style="margin-right: 3px">
													<b>+ ${perfCriDevelopmentType.perfCriDevelopmetName}: </b>${StringUtil.wrapString(perfCriDevelopmentType.description)}
												</div>
												<div class="row-fluid">
													<span style="color: crimson">${uiLabelMap.HRFormula}: ${perfCriDevelopmentType.formula}</span>
												</div>
											</#list>
										</#if>
									</div>
								</div>
							</div>
							</div>
						</div>
						<div class="step-pane" id="kpiPolicySetup${newKPIWindow}">
							<div class="row-fluid">
								<div class="span12">
									<div class="span6">
										<div class='span4 align-right'>
											<label class="asterisk">${uiLabelMap.CommonFromDate}</label>
										</div>
										<div class="span8">
											<div id="fromDate"></div>
										</div>
									</div>
									<div class="span6">
										<div class="span4 align-right">
											<label class="asterisk">${uiLabelMap.CommonThruDate}</label>
										</div>
										<div class="span8">
											<div id="thruDate"></div>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span12">
									<div id="setupGrid"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="form-action wizard-actions">
						<button class="btn btn-next btn-success form-action-button pull-right" data-last="${uiLabelMap.CommonCreate}" id="btnNext">
							${uiLabelMap.CommonNext}
							<i class="icon-arrow-right icon-on-right"></i>
						</button>
						<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
							<i class="icon-arrow-left"></i>
							${uiLabelMap.CommonPrevious}
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="uomIdKPIWindow" class="hide">
	<div>${uiLabelMap.AddNewUOM}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.UomName}</label>
				</div>
				<div class='span8'>
					<input id="uomAbbreviation" type="text">
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingUom" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerUom"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddUom" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddUom" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<div id="listCriteriaTypeWindow" class="hide">
	<div>${uiLabelMap.ListCriteriaTypes}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div id="containergridCriteriaType" style="background-color: transparent; overflow: auto; width: 100%;">
	    	</div>
	    	<div id="jqxNotificationgridCriteriaType">
		        <div id="notificationContentgridCriteriaType">
		        </div>
		    </div>
			<div id="gridCriteriaType"></div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelSelectListCriteriaType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="chooseCriteriaTypeSelected" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSelect}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="createCriteriaTypeWindow" class="hide" >
	<div>${uiLabelMap.CreateNewCriteriaType}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content' style="height: auto">
			<div class='row-fluid'>
				<div class='span4'>
					<label class="asterisk">${uiLabelMap.IdKPIType}</label>
				</div>
				<div class='span8'>
					<input id="CriteriaTypeId" type="text" />
				</div>
			</div>
			<div class='row-fluid'>
				<div class='span4'>
					<label class="asterisk">${uiLabelMap.DescriptionKPIType}</label>
				</div>
				<div class='span8'>
					<input id="CriteriaTypeName" type="text" />
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelCreateKpiType" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveCreateKpiType" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="setupKpiPolicyWindow" class="hide">
	<div>${uiLabelMap.SetupKPIPolicy}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right">
					<label class="asterisk">${uiLabelMap.KPIFromRating}</label>
				</div>
				<div class="span8">
					<div id="fromRating"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right">
					<label>${uiLabelMap.KPIToRating}</label>
				</div>
				<div class="span8">
					<div id="toRating"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right">
					<label class="asterisk">${uiLabelMap.HRCommonAmount}</label>
				</div>
				<div class="span8">
					<div id="amount"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span4 align-right">
					<label class="asterisk">${uiLabelMap.RewardPunishment}</label>
				</div>
				<div class="span8">
					<div id="status"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelSetupKpi" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinueSetup" class="btn btn-success form-action-button pull-right">${uiLabelMap.SaveAndContinue}</button>
					<button id="saveSetupKpi" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="DetailWindow" class="hide">
	<div>${uiLabelMap.KpiInfo}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div id="contentPanel_Detail">
                <div class='row-fluid margin-bottom10' style="margin-top: 15px">
                    <div class='span4 align-right'>
                        <label class="asterisk">${uiLabelMap.KPIId}</label>
                    </div>
                    <div class='span8'>
                        <input type="text" id="CriteriaId_Detail">
                    </div>
                </div>
				<div class='row-fluid margin-bottom10' style="margin-top: 15px">
					<div class='span4 align-right'>
						<label class="asterisk">${uiLabelMap.HRCommonFields}</label>
					</div>
					<div class='span8'>
						<div class="row-fluid">
							<div class="span12">
								<div class="span10">
									<div id="CriteriaType_Detail"></div>
								</div>			
								<div class="span2">
									<button class="btn btn-mini btn-primary" style="width: 80%" type="button" id="viewListCriteriaTypeBtn_Detail"
										title="${StringUtil.wrapString(uiLabelMap.ViewListCriteriaType)}">
										<i class="icon-only icon-align-justify"></i>
									</button>
								</div>			
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 align-right'>
						<label class="asterisk">${uiLabelMap.HRCommonKPIName}</label>
					</div>
					<div class='span8'>
						<input type="text" id="CriteriaName_Detail">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 align-right'>
						<label class="">${uiLabelMap.HRDescriptionKPI}</label>
					</div>
					<div class='span8'>
						<textarea id="descriptionKPI_Detail"></textarea>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 align-right'>
						<label class="asterisk">${uiLabelMap.HRFrequency}</label>
					</div>
					<div class='span8'>
						<div id="periodTypeNew_Detail"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 align-right'>
						<label class="asterisk">${uiLabelMap.HRTarget}</label>
					</div>
					<div class='span8'>
						<div id="targetNumberNew_Detail"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 align-right'>
						<label class="asterisk">${uiLabelMap.HRCommonUnit}</label>
					</div>
					<div class='span8'>
						<div class="row-fluid">
							<div class="span12">
								<div class="span10">
									<div id="uomIdNew_Detail"></div>
								</div>
								<div class="span2">
										<button id="addNewUomId_Detail" style="width: 80%" title="${uiLabelMap.AddNew}" class="btn btn-mini btn-primary">
											<i class="icon-only icon-plus open-sans" style="font-size: 15px"></i>
										</button>
								</div>							
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<label class="asterisk">${uiLabelMap.KPIDevelopmentTrend}</label>
					</div>
					<div class='span8'>
						<div id="perfCriDevelopmetTypeNew_Detail"></div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<label class=""></label>
					</div>
					<div class='span8'>
						<#if perfCriDevelopmentTypeList?has_content>
							<#list perfCriDevelopmentTypeList as perfCriDevelopmentType>
								<div class="row-fluid" style="margin-right: 3px">
									<b>+ ${perfCriDevelopmentType.perfCriDevelopmetName}: </b>${StringUtil.wrapString(perfCriDevelopmentType.description)}
								</div>
								<div class="row-fluid">
									<span style="color: crimson">${uiLabelMap.HRFormula}: ${perfCriDevelopmentType.formula}</span>
								</div>
							</#list>
						</#if>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/KPICreateNew.js?v=0.0.2"></script>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/KPIDetail.js?v=0.0.1"></script>
