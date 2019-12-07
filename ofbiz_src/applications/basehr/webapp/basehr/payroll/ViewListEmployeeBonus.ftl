<#include "script/ViewListEmployeeBonusScript.ftl" />
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'department', type: 'string'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'value', type: 'number'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'periodTypeId', type: 'string'}]"/>
<script type="text/javascript">
	
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', width: 120},
							{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 130},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', width: 140},
							{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionType', width: 160},
							{text: '${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}', datafield: 'value', width: 120,
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}	
							},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput', width: 130},
							{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput' },
							{datafield:'periodTypeId', hidden: true}"/>
							
	<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
		var partyId = datarecord.partyId;
		var fromDate = datarecord.fromDate.getTime();
		var thruDate;
		if(datarecord.thruDate){
			thruDate = datarecord.thruDate.getTime(); 
		}
		var urlStr = 'getEmplParamCharacteristic';
		var id = datarecord.uid.toString();
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridDetail_' + id);
		var payrollEmplParameterSource = {
				datafield:[
					{name: 'code', type: 'string'},				           
					{name: 'value', type: 'number'}				           
				],
				cache: false,
				datatype: 'json',
				type: 'POST',
				data: {partyId: partyId, fromDate: fromDate, thruDate: thruDate, paramCharacteristicId: 'THUONG'},
				url: urlStr,
		        root: 'payrollEmplParamDetails',
		};
		var nestedGridAdapter = new $.jqx.dataAdapter(payrollEmplParameterSource);
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
		        	{text: '${StringUtil.wrapString(uiLabelMap.TypeBonus)}', width: 230, datafield: 'code',
		        		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		        			for(var i = 0; i < codeArr.length; i++){
		        				if(codeArr[i].code == value){
		        					return '<span title=\"' + value + '\">' + codeArr[i].description + '</span>';
		        				}
		        			}
		        			return '<span>' + value + '</span>';
		        		}
		        	},
		        	{text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'value',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
						}	
		        	}
				]
			});
		}
	}"/>							
</script>

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ListEmployeeBonus}</h4>
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
			<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist 
				clearfilteringbutton="false"
				filterable="false" deleterow="false" editable="false" addrow="false" showtoolbar="false"
				url="" initrowdetails="true" initrowdetailsDetail=rowDetails
				id="jqxgrid" removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />
		</div>
	</div>
</div>	
<script type="text/javascript" src="/hrresources/js/payroll/ViewListEmployeeBonusScript.js"></script>