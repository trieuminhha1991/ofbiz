<#include "script/ViewFixedAssetDecreaseScript.ftl"/>

<#assign datafield = "[{name: 'fixedAssetDecreaseId', type: 'string'},
					   {name: 'voucherDate', type: 'date'},
					   {name: 'voucherNumber', type: 'string'},
					   {name: 'decreaseReasonTypeId', type: 'string'},
					   {name: 'fixedAssetId', type: 'string'},
					   {name: 'accumulatedDepreciation', type: 'number'},
					   {name: 'remainValue', type: 'number'},
					   {name: 'currencyUomId', type: 'string'},
					   {name: 'description', type: 'string'},
					   {name: 'isPosted', type:'boolean'}
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherDate2)}', datafield: 'voucherDate', width: '12%', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype:'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}', datafield: 'voucherNumber', width: '12%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCDecrementReasonTypeId)}', datafield: 'decreaseReasonTypeId', width: '21%',
						   columntype: 'dropdownlist',filtertype: 'checkedlist',	
						   cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		      					for(i = 0; i < globalVar.decreaseReasonTypeArr.length; i++){
		      						if(value == globalVar.decreaseReasonTypeArr[i].decreaseReasonTypeId){
		      							return '<span>' + globalVar.decreaseReasonTypeArr[i].description + '</span>';
		      						}
		      					}
		      				},  
		      				createfilterwidget: function (column, columnElement, widget) {
						 		accutils.createJqxDropDownList(widget, globalVar.decreaseReasonTypeArr, {valueMember: 'decreaseReasonTypeId', displayMember: 'description'});
				   			}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCFixedAssetIdShort)}', datafield: 'fixedAssetId', width: '18%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.AccumulatedDepreciationValue)}', datafield: 'accumulatedDepreciation', width: '14%', 
						   	columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function(row, colum, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						  		if(typeof(value) == 'number' && rowData){
						  			return '<span style=\"text-align: right\">' + formatcurrency(value, rowData.currencyUomId) + '</value>';
						  		}
						  	},
						},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCRemainingValue)}', datafield: 'remainValue', width: '14%', 
						   	columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function(row, colum, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						  		if(typeof(value) == 'number' && rowData){
						  			return '<span style=\"text-align: right\">' + formatcurrency(value, rowData.currencyUomId) + '</value>';
						  		}
						  	},
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCPostted)}', datafield: 'isPosted', columntype: 'checkbox', width: '9%', filterable: false}
					   "/>
</script>	

<@jqGrid dataField=datafield columnlist=columnlist clearfilteringbutton="true" 
	 	width="100%" filterable="true"
		showtoolbar="true" jqGridMinimumLibEnable="false"
		alternativeAddPopup="addNewFADecreaseWindow" addrow="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListFixedAssetDecrease" 
		editable="false" mouseRightMenu="true" contextMenuId="contextMenu"/>
		
<div id="contextMenu" class="hide">
	<ul>
		<li action="postAcctgTransDescrease" id="postAcctgTransDescrease">
			<i class="icon-save"></i>${uiLabelMap.BACCPosting}
		</li>
		<li action="edit" id="editFixedAssetDecrease">
			<i class="icon-list"></i>${uiLabelMap.BSViewDetail}
        </li>
	</ul>
</div>			
		
<div id="addNewFADecreaseWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCAddFixedAssetDecrease)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom" id="generalInfo">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class='asterisk'>${uiLabelMap.BACCVoucherDate2}</label>
								</div>
								<div class="span8">
									<div id="voucherDate"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span4'>
									<label>${uiLabelMap.BACCVoucherNumber}</label>
								</div>
								<div class="span8">
									<input type="text" id="voucherNumber">
						   		</div>
							</div>
						</div><!-- ./span6 -->
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class=''>${uiLabelMap.BACCDecrementReasonTypeId}</label>
								</div>
								<div class="span8">
									<div id="decreaseReasonTypeId"></div>
									<a id="addDecreaseReasonType" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" 
										style="left: 97.5%; top: 9px" title="${StringUtil.wrapString(uiLabelMap.BACCAdingReason)}">
										<i class="fa fa-plus blue" aria-hidden="true"></i>
									</a>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span4'>
									<label class=''>${uiLabelMap.AccountingComments}</label>
								</div>
								<div class="span8">
									<input type="text" id="description">
						   		</div>
							</div>
						</div><!-- ./span6 -->
					</div><!-- ./span12 -->
				</form>
			</div>
			<hr style="margin: 0 0 10px"/>
			<div class="row-fluid">
				<div id="fixedAssetItemGrid"></div>
			</div>
			<div class="row-fluid" id="notedPosted">
				<div><strong>${StringUtil.wrapString(uiLabelMap.BACCNote)}:</strong> <span style="color:red">${StringUtil.wrapString(uiLabelMap.BACCNoteFixedAssetIncreaseItemPosted)}</span></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddFADecrease">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddFADecrease">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>	

<div id="addFixedAssetWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCCreateNew)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCFixedAssetIdShort}</label>
					</div>
					<div class="span7">
						<div id="fixedAssetDropDown">
							<div id="fixedAssetGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPurCostAcc}</label>
					</div>
					<div class="span7">
						<input type="text" id="costGlAccount">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCTKChiPhiThanhLy}</label>
					</div>
					<div class="span7">
						<input type="text" id="lossGlAccount">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCDepreciationGlAccount}</label>
					</div>
					<div class="span7">
						<div id="depreciationAccDropDown">
							<div id="depreciationAccGrid"></div>
						</div>
			   		</div>
				</div>
				<#--<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCRemainValueGlAccount}</label>
					</div>
					<div class="span7">
						<div id="remainValueAccDropDown">
							<div id="remainValueAccGrid"></div>
						</div>
			   		</div>
				</div>-->
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCPurchaseCost}</label>
					</div>
					<div class="span7">
						<div id="purchaseCost"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCDepreciation}</label>
					</div>
					<div class="span7">
						<div id="depreciationAmount"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.AccumulatedDepreciationValue}</label>
					</div>
					<div class="span7">
						<div id="accumulatedDepreciationValue"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCRemainingValue}</label>
					</div>
					<div class="span7">
						<div id='remainingValue'></div>
			   		</div>
				</div>
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

<div id="addDecreaseReasonTypeWindow" class="hide">
	<div>${uiLabelMap.BACCAdingReason}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class='span4'>
						<label class=''>${uiLabelMap.CommonId}</label>
					</div>
					<div class="span8">
						<input type="text" id="fixedAssetDecrReasonTypeId">
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4'>
						<label class='asterisk'>${uiLabelMap.CommonDescription}</label>
					</div>
					<div class="span8">
						<input type="text" id="descDecreaseReasonType">
			   		</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddDecreaseReasonType">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAddDecreaseReasonType">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddDecreaseReasonType">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>						   

<script type="text/javascript" src="/accresources/js/fixedAsset/ViewFixedAssetDecrease.js?v=0.0.1"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetDecreaseNew.js?v=0.0.4"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetDecrReasonType.js?v=0.0.1"></script>
