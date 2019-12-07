<#include "script/ViewKPIRewardPunishmentPolicyScript.ftl"/>
<#assign datafield = "[{name: 'perfCriteriaPolicyId', type: 'string'},
                       {name: 'perfCriteriaRateGradeId', type: 'string'},
                       {name: 'salaryRate', type: 'number'},
                       {name: 'allowanceRate', type: 'number'},
                       {name: 'bonusAmount', type: 'number'},
                       {name: 'punishmentAmount', type: 'number'},
                       {name: 'fromDate', type: 'date'},
                       {name: 'thruDate', type: 'date'},
					  ]"/>
<script type="text/javascript">
<#assign columnlist = "{datafield: 'perfCriteriaPolicyId', hidden: true},
					   {text: '${StringUtil.wrapString(uiLabelMap.KPIRating)}', filtertype: 'checkedlist', datafield: 'perfCriteriaRateGradeId', editable: false, width : '26%',
						   	createfilterwidget: function(column, columnElement, widget){
									var source = {
									        localdata: globalVar.perfCriteriaRateGradeArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'perfCriteriaRateGradeId'});
								    if(dataSoureList.length > 8){
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }
							},
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   for(var i = 0; i < globalVar.perfCriteriaRateGradeArr.length; i++){
								  if(globalVar.perfCriteriaRateGradeArr[i].perfCriteriaRateGradeId == value){
									  return '<span>' + globalVar.perfCriteriaRateGradeArr[i].description + '</span>';
								  } 
							   }
							   return '<span>' + value + '</span>';
						   },
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.SalaryRate)}', datafield: 'salaryRate', width: '12%', cellsalign: 'right', 
					   	   columntype: 'numberinput', filtertype: 'number', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   if(typeof(value) != 'undefined'){
								   return '<span class=\"align-right\">' + value + '%<span>'; 
							   }
							   return '<span class=\"align-right\">' + value + '<span>';
						   },
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.AllowanceRate)}', datafield: 'allowanceRate', width: '12%', cellsalign: 'right', 
					   		columntype: 'numberinput', filtertype: 'number', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   if(typeof(value) != 'undefined'){
								   return '<span class=\"align-right\">' + value + '%<span>'; 
							   }
							   return '<span class=\"align-right\">' + value + '<span>';
						   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}', datafield: 'bonusAmount', width: '14%', cellsalign: 'right', 
					   		columntype: 'numberinput', filtertype: 'number', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.HRPunishmentAmount)}', datafield: 'punishmentAmount', width: '14%', cellsalign: 'right', 
					   		columntype: 'numberinput', filtertype: 'number', editable: false,
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.HREffectiveDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', 
						   editable: false, columntype: 'datetimeinput', width: '11%', filtertype: 'range' 
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', 
						   editable: false, columntype: 'datetimeinput', width: '11%', filtertype: 'range'
					   },
					"/>
</script>				  
<#if security.hasEntityPermission("HR_KPIPERF", "_ADMIN", session)>
	<#assign addrow = "true"/>
<#else>
	<#assign addrow = "false"/>	
</#if>
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
		clearfilteringbutton="true" addType="popup" editable="true" deleterow="false" 
		alternativeAddPopup="alterpopupWindow" addrow=addrow showlist="false"
		url=""
	 	createUrl="" jqGridMinimumLibEnable="false"
	 	addColumns="" customControlAdvance="<div id='dateTimeInput'></div>"
	 	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePerfCriteriaPolicy"
		editColumns="perfCriteriaPolicyId;perfCriteriaRateGradeId;salaryRate(java.math.BigDecimal);allowanceRate(java.math.BigDecimal);bonusAmount(java.math.BigDecimal);punishmentAmount(java.math.BigDecimal);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		removeUrl="" 
	/>	
	
<#if security.hasEntityPermission("HR_KPIPERF", "_ADMIN", session)>	
<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.CreateRewardPunishmentKPIPolicy}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.KPIRating}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="perfCriteriaRateGradeList"></div>
							</div>
							<div class="span2">
								<button class="btn btn-mini btn-primary" style="width: 95%" id="perfCriteriaRateGradeBtn" title="${uiLabelMap.PerfCriteriaRateLevelList}">
									<i class="icon-only icon-list open-sans" style="font-size: 15px; position: relative; top: -2px;"></i>
								</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.SalaryRate}</label>
				</div>
				<div class='span8'>
					<div id="salaryRateEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.AllowanceRate}</label>
				</div>
				<div class='span8'>
					<div id="allowanceRateEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.HRCommonBonus}</label>
				</div>
				<div class='span8'>
					<div id="bonusAmountEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.HRPunishmentAmount}</label>
				</div>
				<div class='span8'>
					<div id="punishmentAmountEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.EffectiveFromDate}</label>
				</div>
				<div class='span8'>
					<div id="fromDateEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.CommonThruDate}</label>
				</div>
				<div class='span8'>
					<div id="thruDateEdit"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelKPIPolicy" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveKPIPolicy" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
		<div class="row-fluid no-left-margin">
			<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
				<div class="loader-page-common-custom" id="spinnerAjax"></div>
			</div>
		</div>
	</div>		
</div>
<div id="perfCriteriaRateGradeWindow" class="hide">
	<div>${uiLabelMap.PerfCriteriaRateLevel}</div>
	<div class="form-window-container">
		<div id="containerperfCriteriaRateGradeGrid"></div>
		<div id="jqxNotificationperfCriteriaRateGradeGrid">
	        <div id="notificationContentperfCriteriaRateGradeGrid">
	        </div>
	    </div>
		<div id="perfCriteriaRateGradeGrid"></div>
	</div>
</div>
<div id="addPerfCriteriaRateGradeWindow" class="hide">
	<div>${uiLabelMap.AddPerfCriteriaRateGrade}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.HRCommonClassification}</label>
				</div>
				<div class='span8'>
					<input id="perfCriteriaRateGradeName" type="text">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.CommonFrom}</label>
				</div>
				<div class='span8'>
					<div id="fromRating"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.HRCommonToUppercase}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span12">
							<div class="span11">
								<div id="toRating"></div>
							</div>
							<div class="span1" style="margin: 0">
								<a href="javascript:void(0)" class="grid-action-button" style="padding: 0 0 0 5px; cursor: pointer;" id="clearToRatingValue" title="${uiLabelMap.Delete}">
									<i class="icon-only icon-remove open-sans" style="font-size: 15px; position: relative;"></i>
								</a>
							</div>
						</div>
					</div>				
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelKPIRate" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveKPIRate" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
		<div class="row-fluid no-left-margin">
			<div id="loadingAddKPIRating" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
				<div class="loader-page-common-custom" id="spinnerKPIRating"></div>
			</div>
		</div>
	</div>	
</div>
</#if>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewKPIRewardPunishmentPolicy.js"></script>
<#if security.hasEntityPermission("HR_KPIPERF", "_ADMIN", session)>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/CreateKPIRewardPunishmentPolicy.js"></script>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/PerfCriteriaRateGradeList.js"></script>
</#if>