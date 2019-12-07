<#include "script/ViewKPIAssessmentScript.ftl"/>
<#assign datafield = "[{name: 'perfCriteriaAssessmentId', type: 'string'},
					   {name: 'perfCriteriaAssessmentName', type: 'string'},	
					   {name: 'partyId', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'periodTypeId', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'}
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{datafield: 'perfCriteriaAssessmentId', hidden: true},
						{datafield: 'partyId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.PerfCriteriaAssessmentName)}', datafield: 'perfCriteriaAssessmentName', width: '25%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '22%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.AssessmentPeriod)}', datafield: 'periodTypeId', width: '16%', filtertype: 'checkedlist',
							columntype: 'dropdownlist', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								for(var i = 0; i < globalVar.periodTypeArr.length; i++){
									if(value == globalVar.periodTypeArr[i].periodTypeId){
										return '<span>' + globalVar.periodTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
									var source = {
									        localdata: globalVar.periodTypeArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'periodTypeId'});
								    if(dataSoureList.length > 8){
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }
							},
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: '15%', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
							columntype: 'datetimeinput', editable: false
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
							columntype: 'datetimeinput', editable: false	
						},
						"/>
</script>	
<#if security.hasEntityPermission("HR_KPIPERF", "_CREATE", session)>
	<#assign addrow = "true">
<#else>
	<#assign addrow = "false">
</#if>		
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
		clearfilteringbutton="true" addType="popup" editable="true" deleterow="false" 
		alternativeAddPopup="alterpopupWindow" addrow=addrow showlist="false"
		url="jqxGeneralServicer?sname=JQGetPerfCriteriaAssessment"
	 	createUrl="" jqGridMinimumLibEnable="false"
	 	addColumns="" mouseRightMenu="true" contextMenuId="contextMenu"
	 	updateUrl="jqxGeneralServicer?jqaction=U&sname="
		editColumns=""
		removeUrl="" 
	/>			  
	
<div id="contextMenu" class="hide">
	<ul>
		<li action="emplListInAssessment">
			<i class="fa-users"></i>${uiLabelMap.EmplListInPerfCriteriaAssessment}
        </li>
	</ul>
</div>	
<div id="contextMenuKPIAssess" class="hide">
	<ul>
		<li action="detailsOfReviewPoint">
			<i class="fa-info-circle"></i>${uiLabelMap.DetailsOfReviewPoint}
        </li>
		<li action="editReviewPoint">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
	</ul>
</div>	
<#if security.hasEntityPermission("HR_KPIPERF", "_CREATE", session)>
<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.AddNewPerfCriteriaAssessment}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.PerfCriteriaAssessmentName}</label>
				</div>
				<div class='span8'>
					<input type="text" id="perfCriteriaAssessmentName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.OrganizationalUnit}</label>
				</div>
				<div class='span8'>
					<div id="dropDownButtonEdit" class="">
						<div style="border: none;" id="jqxTreeEdit">
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.AssessmentPeriod}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span12">
							<div class="span10">
								<div id="periodTypeIdEdit"></div>
							</div>
							<div class="span2">
								<button class="btn btn-mini btn-primary" style="width: 90%" id="addAssessmentPeriod" title="">
									<i class="icon-only icon-list open-sans" style="font-size: 15px; position: relative; top: -2px;"></i>
								</button>
							</div>
						</div>				
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.CommonFromDate}</label>
				</div>
				<div class='span8'>
					<div id="fromDateEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.CommonThruDate}</label>
				</div>
				<div class='span8'>
					<div id="thruDateEdit"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label>${uiLabelMap.HRNotes}</label>
				</div>
				<div class='span8'>
					<textarea id="commentEdit"></textarea>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAssessment" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAssessment" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
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
<div id="kpiAssessmentPeriodWindow" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.CommonName}</label>
				</div>
				<div class="span8">
					<input type="text" id="kPIAssessmentPeriodName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.LengthOfTime}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="periodLength"></div>
							</div>
							<div class="span6">
								<div id="uomIdAssessPeriod"></div>
							</div>
						</div>
					</div>
				</div>
			</div>	
			<div class="row-fluid no-left-margin">
				<div id="loadingKPIAssessPeriod" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerKPIAssessPeriod"></div>
				</div>
			</div>		
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelKPIAssessPeriod" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveKPIAssessPeriod" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
</#if>

<div id="emplListInKPIAssessmentWindow" class="hide">
	<div>${uiLabelMap.EmployeeListInKPIAssessment}</div>
	<div class="form-window-container">
		<div id="emplListInKPIAssessPanel" style="border: none;">
			<div id="containeremplListInKPIAssessGrid"></div>
			<div id="jqxNotificationemplListInKPIAssessGrid">
		        <div id="notificationContentemplListInKPIAssessGrid">
		        </div>
		    </div>
			<div id="emplListInKPIAssessGrid"></div>
		</div>
	</div>
</div>
<div id="editEmplKPIAssessmentWindow" class="hide">
	<div>${uiLabelMap.EditEmplKPIAssessment}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.CommonEmployee}</label>
				</div>
				<div class='span8'>
					<label id="partyIdAssessment"></label>
				</div>
			</div>
			<#--<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<#--<label class="asterisk">${uiLabelMap.KPIRating}</label>
                        <label>${uiLabelMap.KPIRating}</label>
				</div>
				<div class='span8'>
					<div id="perfCriRateGradeIdAssessment"></div>
				</div>
			</div>-->
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.SalaryRate}</label>
				</div>
				<div class='span8'>
					<div id="salaryRateAssessment"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.AllowanceRate}</label>
				</div>
				<div class='span8'>
					<div id="allowanceRateAssessment"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRCommonBonus}</label>
				</div>
				<div class='span8'>
					<div id="bonusAmountAssessment"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRPunishmentAmount}</label>
				</div>
				<div class='span8'>
					<div id="punishmentAmountAssessment"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.PaymentPeriod}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="monthCustomTimeAssessment"></div>
							</div>
							<div class="span6">
								<div id="yearCustomTimeAssessment"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="">${uiLabelMap.HRCommonCurrStatus}</label>
				</div>
				<div class="span8">
					<input type="text" id="partyAssessmentStatus">
				</div>
			</div>
			<div class='row-fluid '>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.HRApprove)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="acceptPartyAssessment">${uiLabelMap.HRCommonAccept}</div>		
							</div>
							<div class="span6">
								<div id="rejectPartyAssessment">${uiLabelMap.HRReject}</div>
							</div>
						</div>
					</div>
				</div>
			</div>	
			<div class="row-fluid no-left-margin">
				<div id="loadingPartyAssessment" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerPartyAssessment"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelPartyAssessment" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="savePartyAssessment" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="emplKPIAssessmentDetailWindow" class="hide">
	<div>${uiLabelMap.DetailsOfReviewPoint}</div>
	<div class="form-window-container">
		<div id="emplKPIAssessDetailPanel" style="border: none;">
			<div class="row-fluid">
				<div class="span12">
					<div id="detailTab">
						<div id="containeremplKPIAssessGeneralGrid"></div>
						<div id="jqxNotificationemplKPIAssessGeneralGrid">
					        <div id="notificationContentemplKPIAssessGeneralGrid">
					        </div>
					    </div>
						<div id="emplKPIAssessDetailGrid"></div>
					</div>
				</div>
				<#--<ul class="nav nav-tabs">
					<li class="active">
						<a data-toggle="tab" href="#generalTab">${uiLabelMap.HRCommonGeneral}</a>
					</li>
					<li>
						<a data-toggle="tab" href="#detailTab">${uiLabelMap.CommonDetail}</a>
					</li>
				</ul>
				<div class="tab-content overflow-visible" style="border: none !important">
					<div id="generalTab" class="tab-pane active">
						<div id="containeremplKPIAssessGeneralGrid"></div>
						<div id="jqxNotificationemplKPIAssessGeneralGrid">
					        <div id="notificationContentemplKPIAssessGeneralGrid">
					        </div>
					    </div>
						<div id="emplKPIAssessGeneralGrid"></div>
					</div>
					<div id="detailTab" class="tab-pane">
						<div id="emplKPIAssessDetailGrid"></div>
					</div>
				</div>-->
			</div>
		</div>
	</div>
</div>

<#if security.hasEntityPermission("HR_KPIPERF", "_UPDATE", session)>
<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div class='form-window-container'>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	               		<div class='form-window-content'>
		                   <div id="EmplListInOrg">
		                   </div>
	               		</div>
	               </div>
	        	</div>
			</div>
			<div class="form-action">
	    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelChooseEmpl">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
	    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveChooseEmpl">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	    	</div>
		</div>
	</div>
</div>


<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
<script type="text/javascript">
function jqxTreeEmplListSelect(event){
	refreshBeforeReloadGrid($('#EmplListInOrg'));
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
<#else>
	<#assign expandTreeId="">
</#if>
</script>
</#if>
<#if security.hasEntityPermission("HR_KPIPERF", "_CREATE", session)>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewKPIAssessmentCreate.js"></script>
</#if>
<#if security.hasEntityPermission("HR_KPIPERF", "_UPDATE", session)>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/EditEmplInKPIAssessment.js?v=0.0.2"></script>
</#if>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewKPIAssessment.js"></script>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewEmplInKPIAssessment.js"></script>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewEmplKPIAssessmentDetails.js"></script>