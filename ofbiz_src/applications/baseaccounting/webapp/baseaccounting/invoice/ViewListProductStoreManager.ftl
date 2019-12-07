<#include "script/ViewListProductStoreManagerScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'productStoreId', type: 'string'},
					   {name: 'storeName', type: 'string'},
					   {name: 'fromDate', type: 'date'},	
					   {name: 'thruDate', type: 'date'},
					   {name: 'totalTerminal', type: 'number'},
					   {name: 'cashHandoverAmount', type: 'number'}
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '10%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BSEmployeeName)}', datafield: 'fullName', width: '15%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.ProductStoreManaged)}', datafield: 'storeName', width: '16%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}', datafield: 'productStoreId', width: '9%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: '12%', cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.AccountingThruDate)}', datafield: 'thruDate', width: '12%', cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.NumberOfTerminal)}', datafield: 'totalTerminal', width: '12%', 
						   columntype: 'numberinput', filtertype: 'number',
						   cellsrenderer: function(row, column, value) {
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + value + '</span>';
								}						
							}   
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.TotalCashHandover)}', datafield: 'cashHandoverAmount', width: '14%', 
						   columntype: 'numberinput', filtertype: 'number',
						   cellsrenderer: function(row, column, value) {
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + formatcurrency(value) + '</span>';
								}						
							}   
					   },
					   "/>
</script>			

<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow" sortable="true"
				addrow="false" mouseRightMenu="true" contextMenuId="contextMenu"
				url="jqxGeneralServicer?sname=JQGetListProductStoreManager" jqGridMinimumLibEnable="false"/>		   

<div id='contextMenu' class="hide">
	<ul>
		<li action="viewListTerminal" >
			<i class="icon-list open-sans"></i>${uiLabelMap.POSTerminalList}
        </li>        
		<li action="cashHanover" >
			<i class="fa-money open-sans"></i>${uiLabelMap.CashHandoverBeginShift}
        </li>        
	</ul>
</div>					

<div id="ListPosTerminalWindow" class="hide">
	<div>${uiLabelMap.POSTerminalList}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="form-horizontal form-window-content-custom label-text-left content-description">
					<div class='span6'>
						<div class="row-fluid">
							<div class="span5 text-algin-right">
								<span style="float: right">${uiLabelMap.BSStoreName}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;">
									<span id="storeNameView"></span>
								</div>
							</div>
						</div>
					</div>
					<div class='span6'>
						<div class="row-fluid ">
							<div class="span6 text-algin-right">
								<span style="float: right">${uiLabelMap.BSProductStoreId}</span>
							</div>
							<div class="span6">
								<div class="green-label" style="text-align: left;">
									<span id="productStoreIdView"></span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="hr hr8 hr-double hr-dotted" style="margin-top: 0px"></div>
			<div class="row-fluid">
				<div id="posTerminalListGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="closeListPosTerminal">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>	
		</div>
	</div>	
</div>

<div id="cashHandoverTerminalWindow" class="hide">
	<div>${uiLabelMap.CashHandoverBeginShift}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCPOSTerminal)}</label>
				</div>
				<div class="span7">
					<div id="posTemrinalDropDown">
						<div id="posTerNotCashHanoverGrid"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CashHandoverAmount)}</label>
				</div>
				<div class="span7">
					<div id="cashHandoverTerminal"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.TotalCashHandover)}</label>
				</div>
				<div class="span7">
					<div id="toalCashHandoverTerminal"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCDateHandover)}</label>
				</div>
				<div class="span7">
					<div id="dateCashHandover"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelCreateCashHandover">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveCreateCashHandover">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/invoice/ViewListProductStoreManager.js"></script>