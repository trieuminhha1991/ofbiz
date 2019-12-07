<#include "script/ViewSummarizingBonusDistributorScript.ftl"/>
<#assign datafield = "[{name: 'salesBonusSummaryId', type: 'string'},
					   {name: 'salesBonusSummaryName', type: 'string'},	
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'totalTurnover', type: 'number'},
					   {name: 'totalBonus', type: 'number'},
					   {name: 'salesBonusPolicyName', type: 'string'},
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.SummarizingBonusDistributorName)}', datafield: 'salesBonusSummaryName', 
							width: '25%', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							   	var salesBonusSummaryId = rowData.salesBonusSummaryId;
							   	return '<a href=\"ViewDistributorBonusSummaryParty?salesBonusSummaryId='+ salesBonusSummaryId + '\" title=\"${StringUtil.wrapString(uiLabelMap.ViewDetails)}\">' + value + '</a>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: '13%', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
							columntype: 'datetimeinput', editable: false},						
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: '13%', cellsformat: 'dd/MM/yyyy',  filterType : 'range',
							columntype: 'datetimeinput', editable: false},						
						{text: '${StringUtil.wrapString(uiLabelMap.DABudgetTotal)}', datafield: 'totalTurnover', width: '15%', columntype: 'numberinput',
							filtertype: 'number', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   	}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalBonus)}', datafield: 'totalBonus', width: '12%', columntype: 'numberinput',
							filtertype: 'number', editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
						   	}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BonusPolicyNameAppl)}', datafield: 'salesBonusPolicyName', width: '22%', editable: false}
						"/>
</script>						  
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
		clearfilteringbutton="true" addType="popup" editable=editable deleterow="false"
		alternativeAddPopup="popupAddRow" addrow=addrow showlist="true"
		url="jqxGeneralServicer?sname=JQGetListSalesBonusSummary&salesBonusSummaryTypeId=BONUS_SUMMARY_DISTRIBUTOR"
	 	jqGridMinimumLibEnable="false" mouseRightMenu="true" contextMenuId="contextMenu"
	 	updateUrl=""
		editColumns=""
		removeUrl="" 
	/>
	
<div id="contextMenu" class="hide">
	<ul>
		<li action="updateData" id="updateData"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.RefreshData)}</li>
	</ul>
</div>

<div id="popupAddRow" class="hide">
	<div>${uiLabelMap.AddSummarizingBonusDistributorName}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.SummarizingBonusDistributorName}</label>
				</div>
				<div class='span8'>
					<input type="text" id="salesBonusSummaryName">
				</div>			
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.CommonTime}</label>
				</div>
				<div class='span8'>
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="monthCustomTime"></div>
							</div>
							<div class="span4">
								<div id="quarterCustomTime"></div>
							</div>
							<div class="span4">
								<div id="yearCustomTime"></div>
							</div>
						</div>
					</div>
				</div>			
			</div>
			<div class='row-fluid margin-bottom10'>
				<div id="saleBonusPolicyGrid"></div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreate"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelAdd" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveAdd" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewSummarizingBonusDistributor.js"></script>	