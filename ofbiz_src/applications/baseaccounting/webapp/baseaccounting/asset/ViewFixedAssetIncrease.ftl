<#include "script/ViewFixedAssetIncreaseScript.ftl"/>
<#assign datafield = "[{name: 'fixedAssetIncreaseId', type: 'string'},
					   {name: 'dateArising', type: 'date'},
					   {name: 'description', type: 'string'},
					   {name: 'fixedAssetId', type: 'string'},
					   {name: 'supplierId', type: 'string'},
					   {name: 'supplierName', type: 'string'},
					   {name: 'supplierCode', type: 'string'},
					   {name: 'totalPuchaseCost', type: 'number'},
					   {name: 'paymentMethodTypeEnumId', type: 'string'},
					   {name: 'dueDate', type: 'date'},
					   {name: 'currencyUomId', type: 'string'},
					   {name: 'address', type: 'string'},
					   {name: 'contactPerson', type: 'string'},
					   {name: 'description', type: 'string'},
					   {name: 'employeeBuyerId', type: 'string'},
					   {name: 'emplCode', type: 'string'},
					   {name: 'emplName', type: 'string'},
					   {name: 'moneyReceiver', type: 'string'},
					   {name: 'accountPayer', type: 'string'},
					   {name: 'accountReceiver', type: 'string'},
					   {name: 'paymentVoucherNbr', type: 'string'},
					   {name: 'isPosted', type:'boolean'}
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DateArising)}', datafield: 'dateArising', width: '11%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', datafield: 'description', width: '20%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCFixedAssetIdShort)}', datafield: 'fixedAssetId', width: '15%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.POSupplier)}', datafield: 'supplierName', width: '15%',
						   cellsrenderer: function (row, column, value) {
							   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							   if(rowData && value){
								   return '<span>' + value + ' [' + rowData.supplierCode + ']</span>';
							   }
						   }
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCPurchaseCost)}', datafield: 'totalPuchaseCost', width: '14%', 
						   	columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function(row, colum, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						  		if(typeof(value) == 'number' && rowData){
						  			return '<span style=\"text-align: right\">' + formatcurrency(value, rowData.currencyUomId) + '</value>';
						  		}
						  	},
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BACCPayments)}', datafield: 'paymentMethodTypeEnumId', width: '18%',
							columntype: 'dropdownlist',filtertype: 'checkedlist',
		      				cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		      					for(i = 0; i < globalVar.paymentMethodTypeEnumArr.length; i++){
		      						if(value == globalVar.paymentMethodTypeEnumArr[i].enumId){
		      							return '<span>' + globalVar.paymentMethodTypeEnumArr[i].description + '</span>';
		      						}
		      					}
		      				},
		      				createfilterwidget: function (column, columnElement, widget) {
						 		accutils.createJqxDropDownList(widget, globalVar.paymentMethodTypeEnumArr, {valueMember: 'enumId', displayMember: 'description'});
				   			}
		                 },
		                 {text: '${StringUtil.wrapString(uiLabelMap.BACCDueDate)}', datafield: 'dueDate', width: '13%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
	                    <!-- {text: '${StringUtil.wrapString(uiLabelMap.BACCPostted)}', datafield: 'isPosted', columntype: 'checkbox', width: '9%', filterable: false}, -->
					  "/>
</script>	

<@jqGrid dataField=datafield columnlist=columnlist clearfilteringbutton="true" 
	 	width="100%" filterable="true"
		showtoolbar="true" jqGridMinimumLibEnable="false"
		alternativeAddPopup="addNewFAIncreaseWindow" addrow="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListFixedAssetIncrease" 
		editable="false" mouseRightMenu="true" contextMenuId="contextMenu"/>		
		
<div id="contextMenu" class="hide">
	<ul>
	<!--	<li action="postAcctgTrans" id="postAcctgTrans">
			<i class="icon-save"></i>${uiLabelMap.BACCPosting}
		</li> -->
		<li action="edit" id="editFixedAssetIncrease">
			<i class="icon-list"></i>${uiLabelMap.BSViewDetail}
        </li>
	</ul>
</div>		
		
<div id="addNewFAIncreaseWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCNewFixedAsset)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
					<ul class="wizard-steps wizard-steps-square">
						<li data-target="#step1" class="active">
					        <span class="step">1. ${uiLabelMap.BACCGeneralInfo}</span>
					    </li>
					    <li data-target="#step2">
					        <span class="step">2. ${uiLabelMap.BACCFixedAsset}</span>
					    </li>
					</ul>
				</div><!--#fuelux-wizard-->
				<div class="step-content row-fluid position-relative" id="step-container">
					<div class="step-pane active" id="step1">
						<div class="row-fluid" style="margin-top: 15px">
							<#include "fixedAssetIncreaseStep1.ftl"/>
						</div>
					</div>
					<div class="step-pane" id="step2">
						<div class="row-fluid">
							<div id="fixedAssetItemGrid"></div>
						</div>
						<div class="row-fluid" id="notedPosted">
							<div><strong>${StringUtil.wrapString(uiLabelMap.BACCNote)}:</strong> <span style="color:red">${StringUtil.wrapString(uiLabelMap.BACCNoteFixedAssetIncreaseItemPosted)}</span></div>
						</div>
					</div>
				</div><!-- ./step-container -->
				<div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.BACCSave)}" id="btnNext">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
			</div><!-- ./row-fluid -->
		</div>
	</div>	
</div>

<div id="newFixedAssetWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.BACCFixedAssetIdShort}</label>
					</div>
					<div class="span8">
						<div id="fixedAssetDropDown">
							<div id="fixedAssetGrid"></div>
						</div>
			   		</div>
				</div>
<!-- 				 <div class='row-fluid'>
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.DebitAccount}</label>
					</div>
					<div class="span8">
						<div id="debitAccDropDown">
							<div id="debitAccGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.CreditAccount}</label>
					</div>
					<div class="span8">
						<div id="creditAccDropDown">
							<div id="creditAccGrid"></div>
						</div>
			   		</div>
				</div> -->
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddFixedAsset">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddFixedAsset">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddFixedAsset">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<script type="text/javascript" src="/accresources/js/fixedAsset/ViewFixedAssetIncrease.js?v=1.0.1"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetIncreaseNew.js?v=1.0.1"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetIncreaseNewStep1.js?v=1.0.1"></script>				  
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetIncreaseNewStep2.js?v=1.0.3"></script>				  