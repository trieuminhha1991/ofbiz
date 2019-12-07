<#include "script/ViewSalesPolicyDistributorScript.ftl"/>
<#assign datafield = "[{name: 'salesBonusPolicyId', type: 'string'},
						{name: 'salesBonusPolicyName', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
						{name: 'createdDate', type: 'date'},
						{name: 'description', type: 'string'},
						]"/>
						
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BonusPolicyName)}', datafield: 'salesBonusPolicyName', width: '25%'},
						{text: '${StringUtil.wrapString(uiLabelMap.AvailableFromDate)}', datafield: 'fromDate', width: '15%', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
							columntype: 'datetimeinput', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: '15%', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
								columntype: 'datetimeinput', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CreatedDate)}', datafield: 'createdDate', width: '15%', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
							columntype: 'datetimeinput', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', datafield: 'description', width: '30%'},
						"/>
<#assign initrowdetailsDetail = "viewListBonusPolicy.initrowdetails"/>
</script>
<@jqGrid filtersimplemode="true"  dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
	 filterable="true" deleterow="false" editable="false" addrow=addrow addType="popup" alternativeAddPopup="popupAddRow"
	 url="jqxGeneralServicer?sname=JQGetListSalesPolicyDistributor" id="jqxgrid" showlist="true" filterable="true" sortable="true" deleterow="false"
	 initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="150"
	selectionmode="singlerow" jqGridMinimumLibEnable="false" />						
	
<div id="popupAddRow" class="hide">
	<div>${uiLabelMap.AddBonusPolicyName}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.BonusPolicyNameShort}</label>
							</div>
							<div class='span8'>
								<input type="text" id="salesBonusPolicyName">
							</div>			
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.EffectiveFromDate}</label>
							</div>
							<div class="span8">
								<div id="fromDate"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonDescription}</label>
							</div>
							<div class='span8'>
								<input type="text" id="description">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonThruDate}</label>
							</div>
							<div class="span8">
								<div id="thruDate"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid" style="margin-top: 15px">
				<div class="span12">
					<div class="span6">
						<div id="turnoverRuleGrid"></div>
					</div>
					<div class="span6">
						<div id="skuRuleGrid"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreateNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreateNew"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAdd" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAdd" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>	
<div id="AddDistPolicyRuleWindow" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="" id="conditionLabel">${StringUtil.wrapString(uiLabelMap.ActualTargetPercent)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span2">
								<div id="operatorInputFrom"></div>
							</div>
							<div class="span4">
								<div id="valueFrom"></div>
							</div>
							<div class="span6" style="margin-top: 5px">
								<div id="ignoreFrom" style="margin-left: 0px !important">
									<span style="font-size: 14px">${uiLabelMap.wgcancel}</span>
								</div>
							</div>						
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonAnd)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span2">
								<div id="operatorInputTo"></div>
							</div>
							<div class="span4">
								<div id="valueTo"></div>
							</div>
							<div class="span6" style="margin-top: 5px">
								<div id="ignoreTo" style="margin-left: 0px !important">
									<span style="font-size: 14px">${uiLabelMap.wgcancel}</span>
								</div>
							</div>						
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BonusLevel)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="valueAction"></div>
							</div>
							<div class="span6" style="margin-top: 5px">
								<label>&nbsp;&#42;&nbsp;${uiLabelMap.TurnoverActual}</label>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreateNewRule" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreateNewRule"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddCond">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddCond">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<div id="EditDistPolicyRuleWindow" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CalculateBonusBaseOn)}</label>
				</div>
				<div class="span8">
					<div id="enumRuleList"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.HRCondition)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span2">
								<div id="operatorInputFromEdit"></div>
							</div>
							<div class="span4">
								<div id="valueFromEdit"></div>
							</div>
							<div class="span6" style="margin-top: 5px">
								<div id="ignoreFromEdit" style="margin-left: 0px !important">
									<span style="font-size: 14px">${uiLabelMap.wgcancel}</span>
								</div>
							</div>						
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonAnd)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span2">
								<div id="operatorInputToEdit"></div>
							</div>
							<div class="span4">
								<div id="valueToEdit"></div>
							</div>
							<div class="span6" style="margin-top: 5px">
								<div id="ignoreToEdit" style="margin-left: 0px !important">
									<span style="font-size: 14px">${uiLabelMap.wgcancel}</span>
								</div>
							</div>						
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.BonusLevel)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="valueActionEdit"></div>
							</div>
							<div class="span6" style="margin-top: 5px">
								<label>&nbsp;&#42;&nbsp;${uiLabelMap.TurnoverActual}</label>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingEditRule" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerEditRule"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditCond">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditCond">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/bonusPolicy/ViewSalesPolicyDistributor.js"></script>	
<script type="text/javascript" src="/hrresources/js/bonusPolicy/AddSalesPolicyDistributor.js"></script>	