<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<#assign datafield = "[{name: 'partyId', type: 'string'},
					  {name: 'partyName', type: 'string'},
					  {name: 'partyGroupId', type: 'string'},
					  {name: 'insuranceSalary', type: 'number'},
					  {name: 'insuranceSocialNbr', type: 'string'},
					  {name: 'emplPositionTypeId', type: 'string'},
					  {name: 'fromDate', type: 'date'},
					  {name: 'statusId', type: 'string'}
					  ]"/>

<script type="text/javascript">
var statusArr = [
	<#if statusInsuranceList?has_content>
		<#list statusInsuranceList as status>
			{
				statusId: '${status.statusId}',
				description: '${StringUtil.wrapString(status.description)}'
			},
		</#list>
	</#if>
];

var emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
			{
				emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
				description: '${StringUtil.wrapString(emplPositionType.description?if_exists)}'
			},
		</#list>
	</#if>
];
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', cellsalign: 'left', width: 120, editable: false, filterable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', cellsalign: 'left', width: 130, editable: false, filterable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'partyGroupId', cellsalign: 'left', editable: false, filterable: false, width: 160},
						{text: '${StringUtil.wrapString(uiLabelMap.EmplPositionTypeName)}', datafield: 'emplPositionTypeId', editable: false, filterable: false, width: 140,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < emplPositionTypeArr.length; i++){
									if(emplPositionTypeArr[i].emplPositionTypeId == value){
										return '<span>' + emplPositionTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.TotalInsuranceSocialSalary)}', datafield: 'insuranceSalary', width: 150, editable: false, filterable: false,
						   cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
							}
					    },
					    {text: '${StringUtil.wrapString(uiLabelMap.SocialInsuranceNbrIdentify)}', datafield: 'insuranceSocialNbr', width: 130, editable: true, filterable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', editable: false, filterable: false, width: 150,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < statusArr.length; i++){
									if(statusArr[i].statusId == value){
										return '<span>' + statusArr[i].description + '</span>';
									}
								}			
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceDeclarationDate)}', filterable: false, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', editable: false}
						"/>
</script>					  
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.EmplInsuranceList}</h4>
		<div class="widget-toolbar none-content">
				
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span2' style="text-align: center;">
							<b>${uiLabelMap.Time}</b>
						</div>
						<div class="span7">
							<div id="dateTimeInput"></div>						
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="dropDownButton" style="margin-top: 5px;" class="pull-right">
								<div style="border: none;" id="jqxTree">
										
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>	
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
				 filterable="true"  deleterow="false" editable="false" addrow="false"
				 url="" id="jqxgrid"  
				 removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />	
		</div>
	</div>	
</div> 
<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
$(document).ready(function () {
	initJqxDateTime();
	
});
function initJqxDateTime(){
	$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
	$("#dateTimeInput").on('change', function(event){
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		var fromDate = selection.from.getTime();
	    var thruDate = selection.to.getTime();
	    //var item = $("#jqxTree").jqxTree('getSelectedItem');
	    //var partyId = item.value;
	    refreshGridData(fromDate, thruDate);
	});
	var fromDate = new Date(${monthStart.getTime()});
	var thruDate = new Date(${monthEnd.getTime()});
	$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
}

function refreshGridData(fromDate, thruDate){
	var tmpS = $("#jqxgrid").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=JQListEmplInsuranceOverview&hasrequest=Y&fromDate=" + fromDate + "&thruDate=" + thruDate;
	$("#jqxgrid").jqxGrid('source', tmpS);
}
</script>