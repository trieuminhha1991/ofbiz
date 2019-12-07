<#include "script/ViewAllocationCostPeriodListScript.ftl"/>
<style>
.disableCellEditor{
	color: rgba(58, 42, 42, 0.82) !important; 
	background-color: #843a3a !important; 
	border-color: #bbb !important; 
	background: #EEE !important
}
</style>
<#assign datafield = "[{name: 'allocCostPeriodId', type: 'string'},
                       {name: 'allocCostPeriodCode', type: 'string'},
                       {name: 'allocCostPeriodName', type: 'string'},
					   {name: 'allocationCostTypeId', type: 'string'},	                       
                       {name: 'fromDate', type: 'date'},
                       {name: 'thruDate', type: 'date'},
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.AllocCostPeriodCodeShort)}', datafield: 'allocCostPeriodCode', width: '15%',
							cellsrenderer: function(row, columns, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								return '<a href=\"javascript:void(0)\" onclick=\"javascript:editAllocCostPeriodItemObj.openWindow(' + rowData.allocCostPeriodId + ')\">' + value + '</a>'	
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.AllocCostPeriodName)}', datafield: 'allocCostPeriodName', width: '25%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.AllocationCostType)}', datafield: 'allocationCostTypeId', width: '23%',
						   cellsrenderer: function(row, columns, value){
							   for(var i = 0; i < globalVar.allocationCostTypeArr.length; i++){
								   if(value == globalVar.allocationCostTypeArr[i].allocationCostTypeId){
									   return '<span>' + globalVar.allocationCostTypeArr[i].description + '</span>';
								   }
							   }
						   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', columntype: 'datetimeinput',
						   cellsformat: 'dd/MM/yyyy', width: '18%', filterType : 'range',
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', columntype: 'datetimeinput',
						   cellsformat: 'dd/MM/yyyy', width: '19%', filterType : 'range',
					   }
					   "/>
</script>		
			  
<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow" sortable="true"
				addrow="true" addType="popup" alternativeAddPopup="AddAllocationCostPeriodWindow"
				url="jqxGeneralServicer?sname=JQGetListAllocationCostPeriod" jqGridMinimumLibEnable="false"/>
				
<div id="AddAllocationCostPeriodWindow" class="hide">
	<div>${uiLabelMap.AddAllocationCostByParty}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class="row-fluid margin-top10">
				<div class="form-legend" style="margin-bottom: 10px">
					<div class="contain-legend">
						<span class="content-legend" >
							${StringUtil.wrapString(uiLabelMap.BACCGeneralInfo)}
						</span>
					</div>
					<div class="row-fluid">
						<div class='span12 margin-top5'>
							<div class='span6'>
								<div class='row-fluid margin-bottom5'>
									<div class="span5 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AllocCostPeriodCodeShort)}</label>
									</div>
									<div class="span7">
										<input id="addAllocCostPeriodCode" type="text">
									</div>
								</div>	
								<div class='row-fluid margin-bottom5'>
									<div class="span5 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AllocCostPeriodName)}</label>
									</div>
									<div class="span7">
										<input id="addAllocCostPeriodName" type="text">
									</div>
								</div>	
								<div class='row-fluid'>
									<div class="span5 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AllocationCostType)}</label>
									</div>
									<div class="span7">
										<div id="addAllocationCostTypeId"></div>
									</div>
								</div>	
							</div>
							<div class="span6">
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonFromDate)}</label>
									</div>
									<div class="span8">
										<div id="addFromDate"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom5'>
									<div class="span4 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonThruDate)}</label>
									</div>
									<div class="span8">
										<div id="addThruDate"></div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class=""></label>
									</div>
									<div class="span8">
										<a href="javascript:void(0)" id="updateGridData">
											<span class="open-sans" style="font-size: 15px"><i class="fa fa-hand-o-right"></i>${uiLabelMap.BACCGetData}</span>
										</a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div id="allocationCostItemGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddAlloc">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddAlloc">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddAlloc">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>	

<div id="EditAllocCostPeriodWindow" class="hide">
	<div>${uiLabelMap.CostAllocationDetail}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="editAllocationCostItemGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditAlloc">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditAlloc">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>		

<script type="text/javascript" src="/accresources/js/setting/createAllocationCostPeriod.js"></script>
<script type="text/javascript" src="/accresources/js/setting/editAllocationCostPeriod.js"></script>
	