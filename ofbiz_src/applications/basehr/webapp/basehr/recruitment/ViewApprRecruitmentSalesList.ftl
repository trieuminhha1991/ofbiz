<#include "script/ViewApprRecruitmentSalesListScript.ftl"/>

<#assign datafield = "[{name: 'recruitmentSalesOfferId', type: 'string'},
						{name: 'customTimePeriodId', type: 'string'},
						{name: 'partyIdOffer', type: 'string'},
						{name: 'partyIdOfferName', type: 'string'},
						{name: 'periodName', type: 'string'},
						{name: 'statusId', type: 'string'},
						{name: 'quantityOffer', type: 'number'}
					   ]"/>
<script type="text/javascript">
<#assign columnlist = "{datafield: 'recruitmentSalesOfferId', hidden: true},
						{datafield: 'customTimePeriodId', hidden: true},
						{datafield: 'partyIdOffer', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.OrganizationOffer)}', datafield: 'partyIdOfferName', width: '30%'},
						{text: '${StringUtil.wrapString(uiLabelMap.TimeRecruitmentPlan)}', datafield: 'periodName', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: '25%',
							columntype: 'dropdownlist', filtertype: 'checkedlist', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(globalVar.statusArr[i].statusId == value){
										return '<span>' + globalVar.statusArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.statusArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.NumberOfOffer)}', datafield: 'quantityOffer', width: '25%', cellsalign: 'right',
							columntype: 'numberinput', filtertype: 'number', editable: false}
						"/>
</script>	
<#if !Static["com.olbius.basehr.util.SecurityUtil"].hasRole(Static["com.olbius.basehr.util.PropertiesUtil"].CSM_ROLE, userLogin.partyId, delegator)>	
<#assign customcontrol = "fa fa-file-text-o open-sans@${uiLabelMap.ProposalApproval}@javascript: void(0);@propsalSalesEmplObj.openWindow()">
<#else>
<#assign customcontrol = ""/>		
</#if>	   
<#assign customcontrol2="fa fa-list-ol open-sans@${uiLabelMap.SummarySalesEmplListOfferdShort}@javascript: void(0);@apprRecSalesEmplSumObj.openWindow()"/>
<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow"
				addrow="false" showlist="false" sortable="true"  mouseRightMenu="true" 
				customcontrol1= customcontrol customcontrol2=customcontrol2
				customControlAdvance="<div id='monthCustomTimePeriod' style='display: inline-block; margin-right: 5px'></div><div id='yearCustomTimePeriod' style='display: inline-block;'></div>"
				contextMenuId="contextMenu" url="" jqGridMinimumLibEnable="false"/>

<div id="contextMenu" class="hide">
	<ul>
		<li action="listEmplOffer">
			<i class="fa fa-list"></i>${uiLabelMap.ListOffer}
        </li>
		<li action="approvalList">
			<i class="fa fa-pencil"></i>${uiLabelMap.WaitingApprovalList}
        </li>
	</ul>
</div>

<#if !Static["com.olbius.basehr.util.SecurityUtil"].hasRole(Static["com.olbius.basehr.util.PropertiesUtil"].CSM_ROLE, userLogin.partyId, delegator)>				
	<div id="listSalesEmplPropsalWindow" class="hide">
		<div>${uiLabelMap.ListSalesmanPropossal}</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class="row-fluid">
					<div id="emplSalesListProposalGrid"></div>
				</div>
			</div>
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button id="cancelProposalRecSalesEmpl" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
						<button id="saveProposalRecSalesEmpl" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.HRCommonProposal}</button>
					</div>
				</div>
			</div>
		</div>		
	</div>
	<script type="text/javascript" src="/hrresources/js/recruitment/ViewRecruitmentSalesProposalList.js"></script>
</#if>
<#include "ApprRecruitmentSalesEmplList.ftl"/>
<#include "ApprRecruitmentSalesNotApprList.ftl"/>
<#include "ApprRecruitmentSalesEmplSummary.ftl"/>
<script type="text/javascript" src="/hrresources/js/recruitment/ViewApprRecruitmentSalesList.js"></script>