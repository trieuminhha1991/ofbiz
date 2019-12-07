<#include "script/ViewListEquipmentDecreaseScript.ftl"/>
<#assign datafield = "[{name: 'equipmentDecreaseId', type: 'string'},
					   {name: 'dateArising', type: 'date'},
					   {name: 'voucherNbr', type: 'string'},
					   {name: 'comment', type: 'string'},
					   {name: 'equipmentId', type: 'string'},
					   {name: 'isPosted', type: 'bool'},
					   {name: 'totalPrice', type: 'number'},
					   {name: 'remainValue', type: 'number'},
					   {name: 'currencyUomId', type: 'string'}
					 ]"/>
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DateArising)}', datafield: 'dateArising', width: '11%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}', datafield: 'voucherNbr', width: '12%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}', datafield: 'equipmentId', width: '15%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.AccountingComments)}', datafield: 'comment', width: '22%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}', datafield: 'totalPrice', width: '15%',
						   	columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function(row, colum, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						  		if(typeof(value) == 'number' && rowData){
						  			return '<span style=\"text-align: right\">' + formatcurrency(value, rowData.currencyUomId) + '</value>';
						  		}
						  	},
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)} ${StringUtil.wrapString(uiLabelMap.BACCRemain)}', datafield: 'remainValue', width: '15%', 
						   	columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function(row, colum, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						  		if(typeof(value) == 'number' && rowData){
						  			return '<span style=\"text-align: right\">' + formatcurrency(value, rowData.currencyUomId) + '</value>';
						  		}
						  	},
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCPostted)}', datafield: 'isPosted', columntype: 'checkbox', width: '10%', filterable: false},
				"/>
						
</script>

<@jqGrid dataField=datafield columnlist=columnlist clearfilteringbutton="true" 
	 	width="100%" filterable="true"
		showtoolbar="true" jqGridMinimumLibEnable="false"
		alternativeAddPopup="addNewEquipDecreaseWindow" addrow="true" addType="popup"
		url="" 
		customControlAdvance="<div id='postedStatus'></div>" 
		editable="false" mouseRightMenu="true" contextMenuId="contextMenu"/>		
		
<div id="contextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
         <li action="delete" id="deleteEqDecrease">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>

<div id="addNewEquipDecreaseWindow" class="hide">
	<div>${uiLabelMap.BACCEquipmentDecrement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label class='asterisk'>${uiLabelMap.DateArising}</label>
								</div>
								<div class="span7">
									<div id="dateArising"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.BACCVoucherNumber}</label>
								</div>
								<div class="span7">
									<input type="text" id="voucherNbr">
						   		</div>
							</div>
						</div><!-- ./span6 -->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class=''>${uiLabelMap.AccountingComments}</label>
								</div>
								<div class="span8">
									<textarea id="comment" class="text-popup" style="width: 93% !important; height: 55px"></textarea>
						   		</div>
							</div>
						</div><!-- ./span6 -->
					</div><!-- ./span12 -->
				</form>
			</div>
			<hr style="margin: 0 0 5px 0"/>
			<div class="row-fluid">
				<div id="equipmentItemGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddEquipDecrease">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipDecrease">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			<button type="button" class='hide btn btn-warning form-action-button pull-right' id="unpostedBtn">
				<i class='fa fa-unlock'></i>&nbsp;${uiLabelMap.BACCUnPosted}</button>
			<button type="button" class='hide btn btn-success form-action-button pull-right' id="postingBtn">
				<i class='fa fa-lock'></i>&nbsp;${uiLabelMap.BACCPosting}</button>	
		</div>
	</div>	
</div>

<div id='newEquipmentWindow' class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.BACCEquipmentId}</label>
					</div>
					<div class="span8">
						<div id="equipmentDropDown">
							<div id="equipmentGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCQuantityInUse}</label>
					</div>
					<div class="span8">
						<div id="quantity"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.BACCQuantityDecreased}</label>
					</div>
					<div class="span8">
						<div id="quantityDecrease"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCPrepaidExpensesAccount}</label>
					</div>
					<div class="span8">
						<div id="debitAccDropDown">
							<div id="debitAccGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCEquipmentDecrementAccount}</label>
					</div>
					<div class="span8">
						<div id="decrementAccDropDown">
							<div id="decrementAccGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCUnitPrice}</label>
					</div>
					<div class="span8">
						<div id="unitPrice"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCTotal}</label>
					</div>
					<div class="span8">
						<div id="totalPrice"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCAllocatedValue}</label>
					</div>
					<div class="span8">
						<div id="allocatedValue"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCRemainingValue}</label>
					</div>
					<div class="span8">
						<div id="remainingValue"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCDecrementReasonTypeId}</label>
					</div>
					<div class="span8">
						<input type="text" id="decreaseReason">
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddEquipment">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddEquipment">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipment">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<div id="contextMenuEquipmentItem" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
	</ul>
</div>	

<script type="text/javascript" src="/accresources/js/equipment/ViewListEquipmentDecrease.js?v=0.0.3"></script>						   
<script type="text/javascript" src="/accresources/js/equipment/editEquipmentDecrease.js?v=0.0.3"></script>							   					   