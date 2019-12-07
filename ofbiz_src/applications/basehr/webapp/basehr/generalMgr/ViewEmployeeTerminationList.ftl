<!-- maybe delete -->
<#include "script/ViewEmplTerminationListScript.ftl"/>

<#assign datafield = "[{name: 'emplTerminationProposalId', type: 'string'},
					   {name: 'terminationTypeId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'dateTermination', type: 'date'},
					   {name: 'statusId', type: 'string'},
					   {name: 'terminationReasonId', type: 'string'},
					   {name: 'comment', type: 'string'},
					   {name: 'otherReason', type: 'string'},
					   {name: 'createdDate', type: 'date'}]"/>
<script type="text/javascript">

<#assign columnlist = "
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: 120},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: 150,
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HREmplReasonResign)}', datafield: 'terminationReasonId', width: 200,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0; i < terminationReasonArr.length; i++){
									if(value == terminationReasonArr[i].terminationReasonId){
										return '<span title=' + value + '>' + terminationReasonArr[i].description + '</span>';
									}
								}
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(data.otherReason){
									return '<span>' + data.otherReason + '</span>';									
								}
								return '<span>' + value + '</span>';
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HREmplResignDate)}', datafield: 'dateTermination', width: 130, editable: false, cellsformat: 'dd/MM/yyyy'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: 130, editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0; i < statusArr.length; i++){
									if(value == statusArr[i].statusId){
										return '<span title=' + value + '>' + statusArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.DateApplication)}', datafield: 'createdDate', width: 130, editable: false, cellsformat: 'dd/MM/yyyy'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRNotes)}', datafield: 'comment'},
						{datafield: 'emplTerminationProposalId', hidden: true},
						{datafield: 'otherReason', hidden: true},
						{datafield: 'terminationTypeId', hidden: true}"/>
	
							
</script>
<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist 
		clearfilteringbutton="false" showtoolbar="true" sourceId="emplTerminationProposalId"
		filterable="false" deleterow="false" editable="false" addrow="false" showlist="false"
		url="jqxGeneralServicer?hasrequest=Y&sname=getEmplTerminationPpslList" id="jqxgrid"
		removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />	
<script type="text/javascript">
$(document).ready(function () {
	<#if parameters.requestId?exists>
		
		$("#jqxgrid").on("bindingComplete", function (event) {
			var data = $('#jqxgrid').jqxGrid('getrowdatabyid', ${parameters.requestId});			
			if(data){
				fillDataInWindow(data);
				openJqxWindow($("#emplTerminationPpsl"));			
			}
		});
	</#if>	
});

</script>		
<div class="row-fluid">
	<div id="emplTerminationPpsl" class="hide">
		<div>${uiLabelMap.PageTitleResignationApplication}</div>
		<div class="form-window-container">
				<div id="notifyContainer">
					<div id="jqxNotificationPpsl">
						<div id="jqxNotificationContentPpsl"></div>
					</div>
				</div>
			
			<div class="row-fluid">
				<div id="jqxTab">
					<ul >
	                    <li><b>${uiLabelMap.TerminationInfo}</b></li>
	                    <li><b>${uiLabelMap.Invoice}</b></li>
	                    <li><b>${uiLabelMap.PaymentInfo}</b></li>
	                    <li><b>${uiLabelMap.TransferredAsset}</b></li>
	                </ul>
	                <div id="TerminationInfo"  style="margin-top: 20px">
	                	<div class="row-fluid">
	                		<div class="span12">
			                	<div class="span6">
			                		<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.EmployeeId}:
										</div>
										<div class="span7">
											<span id="employeeId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.EmployeeName}:
										</div>
										<div class="span7">
											<span id="employeeName"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.gender}:
										</div>
										<div class="span7">
											<span id="gender"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.DABirthday}:
										</div>
										<div class="span7">
											<span id="birthDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.Department}:
										</div>
										<div class="span7">
											<span id="department"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.FormFieldTitle_position}:
										</div>
										<div class="span7">
											<span id="emplPositionTypeId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.DateJoinCompany}:
										</div>
										<div class="span7">
											<span id="dateJoinCompany"></span>
										</div>
									</div>
			                	</div>
			                	<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.EmplAgreementId}:
										</div>
										<div class="span7">
											<span id="agreementId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.agreementTypeId}:
										</div>
										<div class="span7">
											<span id="agreementId"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.AvailableFromDate}:
										</div>
										<div class="span7">
											<span id="fromDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.AvailableThruDate}:
										</div>
										<div class="span7">
											<span id="thruDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.HREmplReasonResign}:
										</div>
										<div class="span7">
											<span id="reasonResign"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.HREmplResignDate}:
										</div>
										<div class="span7">
											<span id="resignDate"></span>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											${uiLabelMap.HRNotes}:
										</div>
										<div class="span7">
											<span id="comment"></span>
										</div>
									</div>
			                	</div>
		                	</div>
		                </div>	
	                </div>
	                <div id="accLiabilities">
	                	<div id="invoicePaid"></div>
	                </div>
	                <div>
	                	<div id="paymentPaid"></div>
	                </div>
	                <div id="transferredAsset">
	                	<div id="transferredAssetJqx"></div>
	                </div>
				</div>
			</div>
			<#if parameters.requestId?exists>
				<#assign checkApprvalPerm = Static["com.olbius.basehr.workflow.WorkFlowUtils"].checkApprvalPerm(delegator, userLogin.partyId, parameters.requestId)/>
				<#if checkApprvalPerm>
					<div class="form-action" id="submitAction">
						<button id="notApproval" type="button" class="btn btn-danger form-action-button pull-right icon-remove">${uiLabelMap.HrCommonNotApproval}</button>
						<button id="approval" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.HREmplApproval}</button>
					</div>
				</#if>
			</#if>
		</div>
	</div>
</div>						   

