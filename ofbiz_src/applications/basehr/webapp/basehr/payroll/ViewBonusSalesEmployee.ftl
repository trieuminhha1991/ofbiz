<#include "script/ViewBonusSalesEmployeeScript.ftl"/>

<#assign datafield = "[{name: 'salesCommissionId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'department', type: 'string'},
					   {name: 'amount', type: 'number'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'statusId', type: 'string'}]"/>
<script type="text/javascript">
	<#assign columnlist = "{datafield: 'salesCommissionId', hidden: true},
							{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', width: 120},
							{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: 140},
							{text: '${uiLabelMap.CommonDepartment}', datafield: 'department', width: 140},
							{text: '${uiLabelMap.HrCommonPosition}', datafield: 'emplPositionType', width: 160},
							{text: '${uiLabelMap.HRCommonBonus}', datafield: 'amount', width: 120,
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}
							},
							{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput', width: 130 },
							{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', editable: false, 
								columntype: 'datetimeinput', width: 130 },
							{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', 
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									for(var i = 0; i < statusArr.length; i++){
										if(value == statusArr[i].statusId){
											return '<span title=' + value + '>' + statusArr[i].description + '</span>';
										}
									}
									return '<span title=' + value + '>' + value+ '</span>';				
								}
							}
							"/>
							
<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
	var salesCommissionId = datarecord.salesCommissionId;
	var urlStr = 'getSalesCommnissionAdj';
	var id = datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail_' + id);
	var salesCommissionAdjSource = {
		datafield:[
			{name: 'salesCommissionId', type: 'string'},
			{name: 'salesPolicyId', type: 'string'},
			{name: 'salesPolicyRuleId', type: 'string'},
			{name: 'salesPolicyActionSeqId', type: 'string'},
			{name: 'amount', type: 'number'},
			{name: 'description', type: 'string'},
			{name: 'inputParamEnumId', type: 'string'}
		],	
		cache: false,
		datatype: 'json',
		type: 'POST',
		data: {salesCommissionId: salesCommissionId},
        url: urlStr,
        root: 'salesCommissionDetail',
	};
	var nestedGridAdapter = new $.jqx.dataAdapter(salesCommissionAdjSource);
	if(grid != null){
		grid.jqxGrid({
			source: nestedGridAdapter, width: '96%', height: 170,
			showheader: true,
			showtoolbar: false,
			theme: 'olbius',
	 		pageSizeOptions: ['15', '30', '50', '100'],
	        pagerMode: 'advanced',
	        pageable: true,
	        columns:[
	        	{datafield: 'salesCommissionId', hidden: true},         
	        	{datafield: 'salesPolicyId', hidden: true},         
	        	{datafield: 'salesPolicyRuleId', hidden: true},         
	        	{datafield: 'salesPolicyActionSeqId', hidden: true},   
	        	{datafield: 'inputParamEnumId', text: '${StringUtil.wrapString(uiLabelMap.TypeBonus)}', width: 180},
	        	{datafield: 'amount', text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', width: 160,
	        		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
	        			return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
	        		}
	        	},
	        	{datafield: 'description', text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}'}
			]
		});
	}
}"/>							
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtfSalesCommission">
			<div id="jqxNtfSalesCommissionContent"></div>
		</div>
	</div>
</div>		
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ConfirmBonusEmplInSales}</h4>
		<div class="widget-toolbar none-content">
			<button id="confirmSalesCommission" class="grid-action-button icon-ok" style="margin-top: 8px">${uiLabelMap.HRCommonConfirm}</button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div id="dateTimeInput"></div>						
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>						
						<div class="span12" style="margin-right: 15px">
							<div id="jqxDropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist selectionmode="checkbox"
				clearfilteringbutton="false" showtoolbar="true" sourceId="salesCommissionId"
				filterable="false" deleterow="false" editable="false" addrow="false" showtoolbar="false"
				url="" initrowdetails="true" initrowdetailsDetail=rowDetails
				id="jqxgrid" removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />
		</div>		
	</div>			   
</div>		
<script type="text/javascript" src="/hrresources/js/payroll/ViewBonusSalesEmployeeScript.js"></script>