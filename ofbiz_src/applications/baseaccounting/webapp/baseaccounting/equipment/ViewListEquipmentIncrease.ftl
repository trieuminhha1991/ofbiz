<#include "script/ViewListEquipmentIncreaseScript.ftl"/>
<#assign datafield = "[{name: 'equipmentIncreaseId', type: 'string'},
					   {name: 'dateArising', type: 'date'},
					   {name: 'voucherNbr', type: 'string'},
					   {name: 'comment', type: 'string'},
					   {name: 'equipmentId', type: 'string'},
					   {name: 'totalAmount', type: 'number'},
					   {name: 'isPosted', type: 'bool'},
					   {name: 'supplierId', type: 'string'},
					   {name: 'supplierName', type: 'string'},
					   {name: 'supplierCode', type: 'string'},
					   {name: 'address', type: 'string'},
					   {name: 'receiver', type: 'string'},
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DateArising)}', datafield: 'dateArising', width: '13%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}', datafield: 'voucherNbr', width: '14%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.AccountingComments)}', datafield: 'comment', width: '29%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}', datafield: 'equipmentId', width: '19%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}', datafield: 'totalAmount', width: '15%', 
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
		alternativeAddPopup="addNewEquipIncreaseWindow" addrow="true" addType="popup"
		url="" customControlAdvance="<div id='postedStatus'></div>" 
		editable="false" mouseRightMenu="true" contextMenuId="contextMenu"/>		
		
<div id="contextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="delete" id ="deleteEqIncrease">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>

<div id="addNewEquipIncreaseWindow" class="hide">
	<div>${uiLabelMap.BACCEquipmentIncrease}</div>
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
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.AccountingComments}</label>
								</div>
								<div class="span7">
									<input id="comment" type="text"/>
						   		</div>
							</div>
						</div><!-- ./span6 -->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.BSSupplier}</label>
								</div>
								<div class="span7">
									<div id="supplierDropDown">
										<div id="supplierGrid"></div>
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.CommonAddress1}</label>
								</div>
								<div class="span7">
									<input id="address" type="text"/>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.BSPartyReceive}</label>
								</div>
								<div class="span7">
									<input id="receiver" type="text"/>
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
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddEquipIncrease">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipIncrease">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
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
						<label class='asterisk'>${uiLabelMap.BACCAllowTimes}</label>
					</div>
					<div class="span8">
						<div id="allocationTimes"></div>
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
						<label class=''>${uiLabelMap.BACCInstrumentToolsAccount}</label>
					</div>
					<div class="span8">
						<div id="costAccDropDown">
							<div id="costAccGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.BACCQuantity}</label>
					</div>
					<div class="span8">
						<div id="quantity"></div>
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

<script type="text/javascript" src="/accresources/js/equipment/ViewListEquipmentIncrease.js?v=0.0.1"></script>						   
<script type="text/javascript" src="/accresources/js/equipment/editEquipmentIncrease.js?v=0.0.1"></script>						   