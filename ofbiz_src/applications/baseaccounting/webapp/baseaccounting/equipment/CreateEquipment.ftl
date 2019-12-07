<#include "script/CreateEquipmentScript.ftl"/>
<div class="row-fluid" id="newEquipment">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-square">
			<li data-target="#eqipmentInfo" class="active">
		        <span class="step">1. ${uiLabelMap.BACCEquipmentInfo}</span>
		    </li>
		    <li data-target="#equipAlloc">
		        <span class="step">2. ${uiLabelMap.BACCEquipAllocInfo}</span>
		    </li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
    	<div class="step-pane active" id="eqipmentInfo">
    		<div class="row-fluid" style="margin-top: 15px">
    			<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCEquipmentId}</label>
						</div>
						<div class="span8">
							<input id="equipmentId" style="padding: 0px !important;"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCEquipmentTypeId}</label>
						</div>
						<div class="span8">
							<div id="equipmentTypeId"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class="asterisk">${uiLabelMap.BACCEquimentName}</label>
						</div>
						<div class="span8">
							<input id="equipmentName" style="padding: 0px !important;"></input>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCPartyId}</label>
						</div>
						<div class="span8">
							<div id="wn_partyId">
								<div id="wn_partyTree"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label>${uiLabelMap.BACCRoleTypeId}</label>
						</div>
						<div class="span8">
							<div id="roleTypeId">
							</div>
				   		</div>
					</div>
				</div>
				
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCUnitPrice}</label>
						</div>
						<div class="span8">
							<div class="row-fluid">
								<div class="span8">
									<div id="unitPrice"></div>
								</div>
								<div class="span4">
									<div id="currencyUomId"></div>
								</div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCQuantity}</label>
						</div>
						<div class="span8">
							<div id="quantity">
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCQuantityUomId}</label>
						</div>
						<div class="span8">
							<div id="quantityUomId"></div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCAllowTimes}</label>
						</div>
						<div class="span8">
							<div id="allowTimes">
							</div>
				   		</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BACCDateAcquired}</label>
						</div>
						<div class="span8">
							<div id="dateAcquired"></div>
				   		</div>
					</div>
				</div>
    		</div>
    	</div>
    	<div class="step-pane" id="equipAlloc">
    		<div id="newAllocGrid"></div>
    	</div>
    </div>	
    <div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev" id="btnPrev">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.BSPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNext" data-last="${uiLabelMap.BSFinish}">
			${uiLabelMap.BSNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>

<div id="newAllocParty" class="hide">
	<div>${uiLabelMap.BACCNewAllocParty}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BACCAllocPartyId)}</label>
				</div>
				<div class="span8">
					<div id="allocPartyDropDownBtn">
						<div id="allocPartyTree"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="required">${uiLabelMap.BACCAllocRate}</label>
				</div>
				<div class='span8'>
					<div id="allocRate"></div>
		   		</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label>${uiLabelMap.BACCAllocGlAccoutId}</label>
				</div>
				<div class="span8">
					<div id="allocGlAccountId">
						<div id="allocGlAccountGrid"></div>
					</div>
		   		</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAllocParty">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAllocParty">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAllocParty">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/accresources/js/equipment/CreateEquipment.js"></script>
