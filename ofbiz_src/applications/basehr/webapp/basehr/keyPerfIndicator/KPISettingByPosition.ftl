<#if !setKPIByPosWindow?has_content>
	<#assign setKPIByPosWindow = "setKPIByPosWindow"/>
</#if>
<#include "script/KPISettingByPositionScript.ftl"/>
<div class="row-fluid" style="position: relative;">
	<div id="${setKPIByPosWindow}" class="hide">
		<div>${uiLabelMap.AddKPIForEmplBaseOnPosition}</div>
		<div class='form-window-container' >
			<div class="form-window-content" >
				<div class="row-fluid">
					<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
				        <ul class="wizard-steps wizard-steps-square">
				                <li data-target="#generalInfo${setKPIByPosWindow}" class="active">
				                    <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
				                </li>
				                <li data-target="#KPISelect${setKPIByPosWindow}">
				                    <span class="step">2. ${uiLabelMap.SelectKPI}</span>
				                </li>
				                <li data-target="#emplSelect${setKPIByPosWindow}">
				                    <span class="step">3. ${uiLabelMap.SelectEmployee}</span>
				                </li>
				    	</ul>
				    </div><!--#fuelux-wizard-->
				    <div class="step-content row-fluid position-relative" id="step-container">
				    	<div class="step-pane active" id="generalInfo${setKPIByPosWindow}">
				    		<div class="span12" style="margin-top: 20px">
				    			<div class='row-fluid margin-bottom10'>
				    				<div class="span3 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}</label>
									</div>
									<div class="span9">
										<div id="emplPositionType${setKPIByPosWindow}"></div>
									</div>
				    			</div>
				    			<div class='row-fluid margin-bottom10'>
				    				<div class="span3 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HREffectiveDate)}</label>
									</div>
									<div class="span9">
										<div id="fromDate${setKPIByPosWindow}"></div>
									</div>
				    			</div>
				    			<div class='row-fluid margin-bottom10'>
				    				<div class="span3 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRExpireDate)}</label>
									</div>
									<div class="span9">
										<div id="thruDate${setKPIByPosWindow}"></div>
									</div>
				    			</div>
				    		</div>
				    	</div>
				    	<div class="step-pane" id="KPISelect${setKPIByPosWindow}">
				    		<div class="row-fluid">
				    			<div class="span12">
				    				<div id="gridListKPI${setKPIByPosWindow}"></div>
				    			</div>
				    		</div>
				    		<div class="row-fluid" style="margin-top: 20px">
				    			<div class="span12">
				    				<div id="gridKPIChoose${setKPIByPosWindow}"></div>
				    			</div>
				    		</div>
				    	</div>
				    	<div class="step-pane" id="emplSelect${setKPIByPosWindow}">
				    		<div class="row-fluid">
				    			<div class="span12">
				    				<div class="span8">
				    					<div id="gridPartyList${setKPIByPosWindow}"></div>
				    				</div>
				    				<div class="span1" style="margin-top: 25%">
				    					<div class="row-fluid" style="text-align: center;">
											<button class="btn btn-mini btn-primary" type="button" id="addParty${setKPIByPosWindow}" title="${uiLabelMap.clickToAddParty}">
												<i class="icon-only fa-forward bigger-110"></i>
											</button>
										</div>
										<div class="row-fluid" style="text-align: center; margin-top: 4px"  id="removeParty${setKPIByPosWindow}">
											<button class="btn btn-mini btn-primary" type="button" title="${uiLabelMap.clickRemovePartySelected}">
												<i class="icon-only fa-backward bigger-110"></i>
											</button>
										</div>
				    				</div>
				    				<div class="span3">
				    					<div class="row-fluid">
				    						<div class="span12">
				    							<div class="">
				    								<h5 class="open-sans blue">${uiLabelMap.EmployeeSelectedShort}</h5>
				    							</div>
				    						</div>
				    					</div>
				    					<div class="row-fluid">
					    					<div id="listBoxParty${setKPIByPosWindow}"></div>
				    					</div>
				    				</div>
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
					<div class="row-fluid no-left-margin">
						<div id="ajaxLoading${setKPIByPosWindow}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
							<div class="loader-page-common-custom" id="spinnerAjax${setKPIByPosWindow}"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/KPISettingByPosition.js?v=0.0.1"></script>