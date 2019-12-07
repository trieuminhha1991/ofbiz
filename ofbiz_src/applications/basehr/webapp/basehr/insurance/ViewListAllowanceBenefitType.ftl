<style>
.form-window-content .row-fluid{
	line-height: 28px;
}

.form-legend{
	margin-bottom: 25px;
}
</style>
<#include "script/ViewListAllowanceBenefitTypeScript.ftl"/>
<#assign datafield = "[{name: 'benefitTypeId', type: 'string'},
					   {name: 'description', type: 'string'},
						{name: 'isIncAnnualLeave', type: 'bool'},
						{name: 'isAccumulated', type: 'bool'},
						{name: 'frequenceId', type: 'string'},
						{name: 'benefitClassTypeId', type: 'string'},
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{datafield: 'benefitTypeId', hidden: true},
						{datafield: 'description', text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowanceBenefitType)}', width: '30%'},
						{datafield: 'benefitClassTypeId', text: '${StringUtil.wrapString(uiLabelMap.InsuranceBenefitType)}', width: '20%',
							editable: false, filtertype: 'checkedlist', columntype: 'dropdownlist',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.insAllowanceBenefitClassTypeArr.length; i++){
									if(value == globalVar.insAllowanceBenefitClassTypeArr[i].benefitClassTypeId){
										return '<span>' + globalVar.insAllowanceBenefitClassTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
								var source = {
								        localdata: globalVar.insAllowanceBenefitClassTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'benefitClassTypeId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						{datafield: 'isAccumulated', text: '${StringUtil.wrapString(uiLabelMap.InsuranceAccumulated)}', columntype: 'checkbox', 
							editable: false,filtertype: 'checkedlist', width: '14%',
							createfilterwidget: function (column, columnElement, widget) {
								var source = {
								        localdata: [{value: 'Y', description: '${StringUtil.wrapString(uiLabelMap.CommonYes)}'}, 
								                    {value: 'N', description: '${StringUtil.wrapString(uiLabelMap.CommonNo)}'}],
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'value'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						{datafield: 'isIncAnnualLeave', text: '${StringUtil.wrapString(uiLabelMap.IncAnnualLeaveBenefitTypeShort)}', columntype: 'checkbox', 
							editable: false, filtertype: 'checkedlist', width: '20%',
							createfilterwidget: function (column, columnElement, widget) {
								var source = {
								        localdata: [{value: 'Y', description: '${StringUtil.wrapString(uiLabelMap.CommonYes)}'}, 
								                    {value: 'N', description: '${StringUtil.wrapString(uiLabelMap.CommonNo)}'}],
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'value'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						{datafield: 'frequenceId', text: '${StringUtil.wrapString(uiLabelMap.InsuranceBenefitTypeFreq)}', 
							editable: false, filtertype: 'checkedlist', width: '16%', columntype: 'dropdownlist',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.insBenefitTypeFreqArr.length; i++){
									if(value == globalVar.insBenefitTypeFreqArr[i].frequenceId){
										return '<span>' + globalVar.insBenefitTypeFreqArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function (column, columnElement, widget) {
								var source = {
								        localdata: globalVar.insBenefitTypeFreqArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'frequenceId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						"/>
</script>
<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist  showtoolbar="true" clearfilteringbutton="true"
					 filterable="true" alternativeAddPopup="popupAddrow" deleterow="false" editable="false" addrow="true"
					 url="jqxGeneralServicer?hasrequest=Y&sname=JQGetListAllowanceBenefitType" id="jqxgrid" jqGridMinimumLibEnable="false"
					 removeUrl="" deleteColumn="" addType="popup"
					 updateUrl="" mouseRightMenu="true" contextMenuId="contextMenu" 
					 editColumns="" showlist="false"
					 selectionmode="singlerow" customcontrol1 = customcontrol />	
			
<div class="row-fluid">
	<div id="contextMenu" class="hide">
		<ul>
			<li action="viewBenefitRules">
				<i class="fa fa-search"></i>${uiLabelMap.ViewBenefitRules}
	        </li>        
		</ul>
	</div>
</div>			
<div class="row-fluid">
	<div id="benefitTypeRulesWindow" class="hide">
		<div>${uiLabelMap.BenefitReceived}</div>
		<div class='form-window-container'>
			<div id="benefitTypeRulesPanel" style="border: none;">
				<div class="row-fluid">
					<div id="containerjqxInsuranceBenefitTypeGrid">
					</div>
					<div id="jqxNotificationjqxInsuranceBenefitTypeGrid">
						<div id="notificationContentjqxInsuranceBenefitTypeGrid"></div>
					</div>
				</div>
				<div id="jqxInsuranceBenefitTypeGrid"></div>
			</div>
		</div>
	</div>
</div>			

<div class="row-fluid">
	<div id="createBenefitRuleWindow" class="hide">
		<div>${uiLabelMap.AddBenefitRules}</div>
		<div class='form-window-container'>
			<div class="form-horizontal basic-custom-form form-window-content">
				<div class="row-fluid" style="margin-top: 15px">
					<div class="span12">
						<div class="span6" id="benefitCond">
							<div class="form-legend">
								<div class="contain-legend">
									<span class="content-legend text-normal">${uiLabelMap.IBCondition}</span>
								</div>
								<div class="contain">
									<div class="row-fluid">
										<div class="span12" id="conditionContainer">
											<div class="row-fluid">
												<div class="span12">
													<div class="span7">
														<div id="insBenInParam_0"></div>
													</div>
													<div class="span2">
														<div id="insBenefitCond_0"></div>
													</div>
													<div class="span2">
														<div id="insBenefitCondNumberInput_0"></div>
													</div>
													<div class="span1">
														<button class="btn-mini grid-action-button" id="deleteCond_0"><i class="icon-only icon-trash"></i></button>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="row-fluid">
										<a href="javascript:void(0);" id="addNewCondition"><i class="fa-plus-circle open-sans open-sans-index"></i>${uiLabelMap.HRAddCondition}</a>
									</div>
								</div>
							</div>
						</div>						
						<div class="span6" id="benefitAction">
							<div class="form-legend">
								<div class="contain-legend">
									<span class="content-legend text-normal">${uiLabelMap.BenefitReceived}</span>
								</div>
								<div class="contain">
									<div class="row-fluid">
										<div class="span4">
											${uiLabelMap.TimeBenefitReceived}
										</div>
										<div class="span8">
											<div class="row-fluid">
												<div class="span12">
													<div class="span7">
														<div id="insBeMaxLeave"></div>
													</div>
													<div class="span2">
														<div id="maxDayLeaveNumberInput"></div>
													</div>
													<div class="span3">
														<div id="uomId"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="row-fluid">
										<div class="span4">
											${uiLabelMap.SalaryBenefitReceived}
										</div>
										<div class="span8">
											<div id="insBenActSal"></div>
										</div>
									</div>
									<div class="row-fluid">
										<div class="span4">
											${uiLabelMap.CommonValue} ${uiLabelMap.CommonOf} X
										</div>
										<div class="span8">
											<div id="rateBenefit"></div>
										</div>
									</div>
									<div class="row-fluid" id="amountValueBenefitSal" style="display: none;">
										<div class="span4">
											${uiLabelMap.CommonValue} ${uiLabelMap.CommonOf} Y
										</div>
										<div class="span8">
											<div id="monthCalcBenefit"></div>
										</div>
									</div>
								</div>
							</div>
						</div>						
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="btnCancelRules" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSaveRules">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<div id="popupAddrow" class="hide">
	<div>${uiLabelMap.AddNewAllowanceBenefitType}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-left">
					<label class="asterisk">${uiLabelMap.InsuranceAllowanceBenefitType}</label>
				</div>
				<div class="span7">
					<input type="text" id="allowanceBenefitTypeDesc">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-left">
					<label class="asterisk">${uiLabelMap.InsuranceBenefitType}</label>
				</div>
				<div class="span7">
					<div id="benefitClassTypeId"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-left">
					<label class="asterisk">${uiLabelMap.InsuranceBenefitTypeFreq}</label>
				</div>
				<div class="span7">
					<div id="insBenefitTypeFreq"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span12 text-algin-left">
					<div style="margin-left: 20px; margin-top: 4px">
						<div id="InsuranceAccumulated"><span style="font-size: 14px">${uiLabelMap.InsuranceAccumulated}</span></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span12 text-algin-left">
					<div style="margin-left: 20px;">
						<div id="isIncAnnualLeaveBenefitType">
							<span style="font-size: 14px; white-space: normal;">${StringUtil.wrapString(uiLabelMap.IncAnnualLeaveBenefitType)}</span></div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreateBenefitType" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreateBenefitType"></div>
				</div>
			</div>
			
		</div>
		<div class="form-action">
			<button id="cancelAllowanceBenefitType" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAllowanceBenefitType">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/insurance/CreateAllowanceBenefitType.js"></script>
<script type="text/javascript" src="/hrresources/js/insurance/ViewListAllowanceBenefitType.js"></script>