<#include "script/ViewListEquipmentAllocateScript.ftl"/>
<#assign datafield = "[{name: 'equipmentAllocateId', type: 'string'},
					   {name: 'voucherDate', type: 'date'},
					   {name: 'voucherNbr', type: 'string'},
					   {name: 'equipmentId', type: 'string'},
					   {name: 'comment', type: 'string'},
					   {name: 'allocatedAmount', type: 'number'},
					   {name: 'isPosted', type: 'bool'}
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherDate2)}', datafield: 'voucherDate', width: '12%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}', datafield: 'voucherNbr', width: '16%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}', datafield: 'equipmentId', width: '22%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.AccountingComments)}', datafield: 'comment', width: '30%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}', datafield: 'allocatedAmount', width: '12%', 
						   	columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function(row, colum, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						  		if(typeof(value) == 'number' && rowData){
						  			return '<span style=\"text-align: right\">' + formatcurrency(value, rowData.currencyUomId) + '</value>';
						  		}
						  	},
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCPostted)}', datafield: 'isPosted', columntype: 'checkbox', width: '8%', filterable: false},
					   "/>
</script>

<@jqGrid dataField=datafield columnlist=columnlist clearfilteringbutton="true" 
	 	width="100%" filterable="true"
		showtoolbar="true" jqGridMinimumLibEnable="false"
		alternativeAddPopup="equipmentAllocateTimeWindow" addrow="true" addType="popup"
		url="" 
		customControlAdvance="<div id='postedStatus'></div>"
		editable="false" mouseRightMenu="true" contextMenuId="contextMenu"/>		
		
<div id="contextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="delete" id="deleteEquipmentAllocate">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>

<div id="equipmentAllocateTimeWindow" class="hide">
	<div>${uiLabelMap.BACCAllocationPeriodSelection}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class="span4">
						<label class=''>${uiLabelMap.BACCAllocationPeriod}</label>
					</div>
					<div class="span8">
						<div id="allocateMonth" style="display: inline-block; float: left;"></div>
						<div id="allocatedYear" style="display: inline-block; float: left; margin-left: 5px"></div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEquipmentAllocTime">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEquipmentAllocTime">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<div id="editEquipAllocateWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
					<ul class="wizard-steps wizard-steps-square">
						<li data-target="#step1" class="active">
					        <span class="step">1. ${uiLabelMap.BACCGeneralInfo}</span>
					    </li>
					    <li data-target="#step2">
					        <span class="step">2. ${uiLabelMap.BACCAllocationSetting}</span>
					    </li>
					</ul>
				</div><!--#fuelux-wizard-->
				<div class="step-content row-fluid position-relative" id="step-container">
					<div class="step-pane active" id="step1">
						<div class="row-fluid" style="margin-top: 10px">
							<form class="form-horizontal form-window-content-custom">
								<div class="span12">
									<div class="span6">
										<div class='row-fluid'>
											<div class='span5'>
												<label class='asterisk'>${uiLabelMap.BACCVoucherDate2}</label>
											</div>
											<div class="span7">
												<div id="voucherDate"></div>
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
							<div id="equipmentAllocItemGrid"></div>
						</div>	
					</div>
					<div class="step-pane" id="step2">
						<div class="row-fluid" style="margin-top: 10px">
							<form class="form-horizontal form-window-content-custom">
								<div class='row-fluid'>
									<div class='span3'>
										<label class=''>${uiLabelMap.BACCEquipment}</label>
									</div>
									<div class="span6">
										<div id="equipmentList"></div>
							   		</div>
								</div>
							</form>
						</div>
						<div class="row-fluid">
							<div id="equipAllocItemPartyGrid"></div>
						</div>	
					</div>
				</div><!-- ./step-content -->
				<div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.BACCSave)}" id="btnNext">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div><!-- ./form-action -->
			</div><!-- ./row-fluid -->	
		</div><!-- ./form-window-content -->
	</div>	
</div>

<div id="newEquipAllocItemWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCEquipment}</label>
					</div>
					<div class="span7">
						<div id="equipmentDropDown">
							<div id="equipmentGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCTotal}</label>
					</div>
					<div class="span7">
						<div id="allocatedAmountItem"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCAllocationAmountOfUsingEquipment}</label>
					</div>
					<div class="span7">
						<div id="allocationAmountUsingItem"></div>
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddEquipAllocateItem">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddEquipAllocateItem">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipAllocateItem">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<div id="newEquipAllocItemPtyWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCEquipment}</label>
					</div>
					<div class="span7">
						<div id="equipmentListItemPty"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCAllocPartyId}</label>
					</div>
					<div class="span7">
						<div id="orgUseTypeDropDown"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''></label>
					</div>
					<div class="span7">
						<div id="partyDropDown" class="hide">
							<div id="partyTree"></div>
						</div>
						<div id="productStoreDropDown" class="hide">
							<div id="productStoreGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCTotalCost}</label>
					</div>
					<div class="span7">
						<div id="allocatedAmountItemPty"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPercent}</label>
					</div>
					<div class="span7">
						<div id="allocatedPercentItemPty"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCAmount}</label>
					</div>
					<div class="span7">
						<div id="amountItemPty"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCCostGlAccount}</label>
					</div>
					<div class="span7">
						<div id="debitAccDropDown">
							<div id="debitAccGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPrepaidExpensesAccount}</label>
					</div>
					<div class="span7">
						<div id="creditAccDropDown">
							<div id="creditAccGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCInstrumentToolsAccount}</label>
					</div>
					<div class="span7">
						<div id="costAccDropDown">
							<div id="costAccGrid"></div>
						</div>
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddEquipAllocateItemPty">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddEquipAllocateItemPty">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipAllocateItemPty">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<div id="updateEquipmentAllocateWindow" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<label class='asterisk'>${uiLabelMap.BACCVoucherDate2}</label>
								</div>
								<div class="span7">
									<div id="updateVoucherDate"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label class=''>${uiLabelMap.BACCVoucherNumber}</label>
								</div>
								<div class="span7">
									<input type="text" id="updateVoucherNbr">
						   		</div>
							</div>
						</div><!-- ./span6 -->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class=''>${uiLabelMap.AccountingComments}</label>
								</div>
								<div class="span8">
									<textarea id="updateComment" class="text-popup" style="width: 93% !important; height: 55px"></textarea>
						   		</div>
							</div>
						</div><!-- ./span6 -->
					</div><!-- ./span12 -->	
				</form>
			</div>
			<hr style="margin: 0 0 5px 0"/>
			<div class="row-fluid">
				<div id="updateEquipAllocItemPartyGrid"></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelUpdateEquipAllocate">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveUpdateEquipAllocate">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			<button type="button" class='hide btn btn-success form-action-button pull-right' id="postingBtn">
				<i class='fa fa-lock'></i>&nbsp;${uiLabelMap.BACCPosting}</button>		
		</div>
	</div>		
</div>

<div id="contextMenuEquipAllocItemParty" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="delete">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>	
<div id="menuUpdateEquipAllocItemParty" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="delete">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>	

<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAdd}@javascript: void(0);@editEquipmentAllocStep1.openPopupAdd()">
<#assign customcontrol2 = "icon-trash open-sans@${uiLabelMap.BSDelete}@javascript: void(0);@editEquipmentAllocStep1.removeItemFromGrid()">
<#assign customcontrol3 = "icon-plus open-sans@${uiLabelMap.BSAdd}@javascript: void(0);@editEquipmentAllocStep2.openPopupAdd()">
<#assign customcontrol4 = "icon-plus open-sans@${uiLabelMap.BSAdd}@javascript: void(0);@updateEquipmentAllocateObj.openPopupAdd()">
<script type="text/javascript">
	var customcontrol1 = "${StringUtil.wrapString(customcontrol1)}";
	var customcontrol2 = "${StringUtil.wrapString(customcontrol2)}";
	var customcontrol3 = "${StringUtil.wrapString(customcontrol3)}";
	var customcontrol4 = "${StringUtil.wrapString(customcontrol4)}";
</script>

<script type="text/javascript" src="/accresources/js/equipment/ViewListEquipmentAllocate.js?v=0.0.5"></script>					  
<script type="text/javascript" src="/accresources/js/equipment/editEquipmentAllocateStep1.js?v=0.0.7"></script>					  
<script type="text/javascript" src="/accresources/js/equipment/editEquipmentAllocateStep2.js?v=0.0.5"></script>					  
<script type="text/javascript" src="/accresources/js/equipment/editEquipmentAllocate.js?v=0.0.5"></script>					  
<script type="text/javascript" src="/accresources/js/equipment/updateEquipmentAllocate.js?v=0.0.5"></script>					  