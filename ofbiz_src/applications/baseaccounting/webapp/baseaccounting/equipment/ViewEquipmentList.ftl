<#include "script/ViewEquipmentListScript.ftl"/>
<#assign datafield = "[{name: 'equipmentId', type: 'string'},
					   {name: 'equipmentName', type: 'string'},
					   {name: 'equipmentTypeId', type: 'string'},
					   {name: 'quantity', type: 'number'},
					   {name: 'unitPrice', type: 'number'},
					   {name: 'totalPrice', type: 'number'},
					   {name: 'quantityUom', type: 'number'},
					   {name: 'currencyUomId', type: 'number'},
					   {name: 'description', type: 'string'},
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}', datafield: 'equipmentId', width: '11%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCEquimentName)}', datafield: 'equipmentName', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCEquipmentTypeId)}', datafield: 'equipmentTypeId', width: '19%', 
							filtertype: 'checkedlist',  
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < globalVar.equipmentTypeArr.length; i++){
									if(value == globalVar.equipmentTypeArr[i].equipmentTypeId){
										return '<span title=' + value + '>' + globalVar.equipmentTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';		
							},
							createfilterwidget: function (column, columnElement, widget) {
								accutils.createJqxDropDownList(widget, globalVar.equipmentTypeArr, {valueMember: 'equipmentTypeId', displayMember: 'description'});
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCQuantity)}', datafield: 'quantity', width: '9%', filterType: 'number', columntype: 'numberinput',
							cellsrenderer: function(row, column, value){
								if(typeof(value) == 'number'){
									return '<span style=\"text-align: right\">' + value + '</span>';	
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}', dataField: 'unitPrice', width: '14%', 
							  filtertype: 'number', columntype: 'numberinput',
							 cellsrenderer: function(row, columns, value){
		                		  var data = $('#jqxgrid').jqxGrid('getrowdata',row);
		                		  if(typeof(value) == 'number'){
			                		  return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		  }
		                		  return '<span>' + value + '</span>';
		                	  }
						 },
						 {text: '${StringUtil.wrapString(uiLabelMap.BACCTotal)}', dataField: 'totalPrice', width: '15%', 
							  filtertype: 'number', columntype: 'numberinput',
							 cellsrenderer: function(row, columns, value){
		                		  var data = $('#jqxgrid').jqxGrid('getrowdata',row);
		                		  if(typeof(value) == 'number'){
			                		  return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
		                		  }
		                		  return '<span>' + value + '</span>';
		                	  }
						 },
						 {text: '${StringUtil.wrapString(uiLabelMap.BACCEquipQuantityUom)}', datafield: 'quantityUom', width: '12%'},
					   "/>
</script>

<@jqGrid filtersimplemode="true" editable="false" showtoolbar="true" clearfilteringbutton="true"
		 addrow="true" addType="popup" alternativeAddPopup="addNewEquipmentWindow"
		 url="jqxGeneralServicer?sname=JqxGetListEquipments" dataField=datafield columnlist=columnlist 
		 jqGridMinimumLibEnable="false"
		 mouseRightMenu="true" contextMenuId="contextMenu"
		 />
		 
<div id="contextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
	</ul>
</div>		 

<div id="addNewEquipmentWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class='asterisk'>${uiLabelMap.BACCEquipmentId}</label>
								</div>
								<div class="span8">
									<div class="row-fluid" style="margin-bottom: 0">
										<div class="span12">
											<div class="span6">
												<input type="text" id="equipmentId">
											</div>
											<div class="span6 align-left">
												<a href="javascript:void(0)" id="autoGenerateIdBtn"><i class="fa fa-arrow-down open-sans"></i>${StringUtil.wrapString(uiLabelMap.BACCGetAutoGenerateId)}</a>
											</div>
										</div>
									</div>
						   		</div>
							</div>	
							<div class='row-fluid'>
								<div class='span4'>
									<label class='asterisk'>${uiLabelMap.BACCEquimentName}</label>
								</div>
								<div class="span8">
									<input type="text" id="equipmentName">
						   		</div>
							</div>	
							<div class='row-fluid'>
								<div class='span4'>
									<label class='asterisk'>${uiLabelMap.BACCEquipmentTypeId}</label>
								</div>
								<div class="span8" style="position: relative;">
									<div id="equipmentTypeId"></div>
									<a id="addEquipmentTypeBtn" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
										style="left: 100%; top: 0px" title="${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}">
										<i class="fa fa-plus blue" aria-hidden="true"></i>
									</a>
						   		</div>
							</div>	
							<div class='row-fluid'>
								<div class='span4'>
									<label class=''>${uiLabelMap.AccountingComments}</label>
								</div>
								<div class="span8">
									<textarea id="description" class="text-popup" style="width: 93% !important; height: 40px"></textarea>
						   		</div>
							</div>
						</div><!-- ./span6 -->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class='asterisk'>${uiLabelMap.BACCQuantity}</label>
								</div>
								<div class="span8">
									<div id="quantity"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span4'>
									<label class='asterisk'>${uiLabelMap.BACCUnitPrice}</label>
								</div>
								<div class="span8">
									<div id="unitPrice" style="display: inline-block; float: left;"></div>
									<div id="currencyUomId" style="display: inline-block; float: left; margin-left: 5px"></div>
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
									<label class=''>${uiLabelMap.BACCEquipQuantityUom}</label>
								</div>
								<div class="span8">
									<input type="text" id="quantityUom">
						   		</div>
							</div>
						</div><!-- ./span6 -->
					</div><!-- ./span12 -->
				</form>
			</div>	
			<div class="legend-container">
				<span>${uiLabelMap.OrganizationUsed}</span>
				<hr/>
			</div>
			<div class="row-fluid">
				<ul class="nav nav-tabs padding-18">
					<#--<li class="active" id="equipmentStoreLi">-->
						<#--<a data-toggle="tab" href="#equipmentStoreTab" aria-expanded="true">-->
							<#--${uiLabelMap.BSProductStore}-->
						<#--</a>-->
					<#--</li>-->
					<li class="active"  id="equipmentPartyLi">
						<a data-toggle="tab" href="#equipmentPartyTab" aria-expanded="true">
							${uiLabelMap.BACCInternalOrganization}
						</a>
					</li>
					<#--<li class="" id="equipmentFranchising">-->
						<#--<a data-toggle="tab" href="#equipmentFranchisingTab" aria-expanded="false">-->
							<#--${uiLabelMap.BACCFranchising}-->
						<#--</a>-->
					<#--</li>-->
				</ul>
				<div class="tab-content overflow-visible" style="border: none !important; padding: 5px 0px">
					<div class="tab-pane active " id="equipmentStoreTab">
						<div id="equipmentStoreGrid"></div>
					</div>
					<div class="tab-pane" id="equipmentPartyTab">
						<div id="equipmentPartyGrid"></div>
					</div>
					<div class="tab-pane" id="equipmentFranchisingTab">
						<div id="equipmentFranchisingGrid"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid hide' id="equipmentEditNoteContainer">
				<span style="font-style: italic; font-size: 12px;"><b>${uiLabelMap.BACCEquipmentCannotFullEditBecausePosted}</b></span>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddEquipment">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right' id="saveAndContinueAddEquipment">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipment">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>	

<div id="addEquipmentTypeWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label class=''>${uiLabelMap.CommonId}</label>
						</div>
						<div class="span8">
							<input type="text" id="addEquimentTypeId">
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class='asterisk'>${uiLabelMap.CommonDescription}</label>
						</div>
						<div class="span8">
							<input type="text" id="addEquimentTypeDesc">
				   		</div>
					</div>	
				</form>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddEquipType">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right' id="saveAndContinueAddEquipType">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipType">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>			
</div>

<div id="newEquipmentPartyWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label class='asterisk'>${uiLabelMap.OrganizationUsed}</label>
						</div>
						<div class="span8">
							<div id="partyDropDown">
								<div id="partyTree"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCQuantity}</label>
						</div>
						<div class="span8">
							<div id="quantityUsed"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class='asterisk'>${uiLabelMap.BACCFromDate}</label>
						</div>
						<div class="span8">
							<div id="fromDateParty"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCThruDate}</label>
						</div>
						<div class="span8">
							<div id="thruDateParty"></div>
				   		</div>
					</div>
				</form>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddEquipParty">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right' id="saveAndContinueAddEquipParty">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipParty">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="newEquipmentStoreWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label class='asterisk'>${uiLabelMap.BSProductStore}</label>
						</div>
						<div class="span8">
							<div id="productStoreDropDown">
								<div id="productStoreGrid"></div>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCQuantity}</label>
						</div>
						<div class="span8">
							<div id="quantityStoreUsed"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class='asterisk'>${uiLabelMap.BACCFromDate}</label>
						</div>
						<div class="span8">
							<div id="fromDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCThruDate}</label>
						</div>
						<div class="span8">
							<div id="thruDate"></div>
				   		</div>
					</div>
				</form>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddEquipStore">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right' id="saveAndContinueAddEquipStore">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipStore">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="newEquipmentPartnerWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom">
					<div class='row-fluid'>
						<div class='span4'>
							<label class='asterisk'>${uiLabelMap.BACCFranchisingUsed}</label>
						</div>
						<div class="span8">
							<div id="partnerDropDown">
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCQuantity}</label>
						</div>
						<div class="span8">
							<div id="partnerUsed"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class='asterisk'>${uiLabelMap.BACCFromDate}</label>
						</div>
						<div class="span8">
							<div id="fromDatePartner"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span4'>
							<label class=''>${uiLabelMap.BACCThruDate}</label>
						</div>
						<div class="span8">
							<div id="thruDatePartner"></div>
				   		</div>
					</div>
				</form>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddEquipPartner">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right' id="saveAndContinueAddEquipPartner">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddEquipPartner">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="contextMenuEquipmentParty" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="delete">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>	
<div id="contextMenuEquipmentStore" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="delete">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>
<div id="contextMenuPartner" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="delete">
			<i class="icon-trash"></i>${uiLabelMap.BACCDelete}
        </li>
	</ul>
</div>	

<script type="text/javascript" src="/accresources/js/equipment/ViewEquipmentList.js?v=0.0.1"></script>				  
<script type="text/javascript" src="/accresources/js/equipment/equipmentEdit.js?v=0.0.5"></script>				  
<script type="text/javascript" src="/accresources/js/equipment/createEquipmentType.js?v=0.0.1"></script>				  