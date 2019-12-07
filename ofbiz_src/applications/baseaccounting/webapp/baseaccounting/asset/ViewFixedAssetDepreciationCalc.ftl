<#include "script/ViewFixedAssetDepreciationCalcScript.ftl"/>
<#assign datafield = "[{name: 'depreciationCalcId', type: 'string'},
					   {name: 'voucherDate', type: 'date'},
					   {name: 'voucherNumber', type: 'string'},
					   {name: 'description', type: 'string'},
					   {name: 'currencyUomId', type: 'string'},
					   {name: 'depreciationAmount', type: 'number'},
					   {name: 'month', type: 'number'},
					   {name: 'year', type: 'number'},
					   {name: 'isPosted', type:'boolean'}
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherDate2)}', datafield: 'voucherDate', width: '15%', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', filtertype:'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}', datafield: 'voucherNumber', width: '15%'},
						{text: '${StringUtil.wrapString(uiLabelMap.AccountingComments)}', datafield: 'description', width: '44%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCAmount)}', datafield: 'depreciationAmount', width: '17%', 
						   	columntype: 'numberinput', filtertype: 'number',
						   	cellsrenderer: function(row, colum, value){
								var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						  		if(typeof(value) == 'number' && rowData){
						  			return '<span style=\"text-align: right\">' + formatcurrency(value, rowData.currencyUomId) + '</value>';
						  		}
						  	},   	
						}, 
						{text: '${StringUtil.wrapString(uiLabelMap.BACCPostted)}', datafield: 'isPosted', columntype: 'checkbox', width: '9%', filterable: false},
					"/>
</script>	

<@jqGrid dataField=datafield columnlist=columnlist clearfilteringbutton="true" 
	 	width="100%" filterable="true"
		showtoolbar="true" jqGridMinimumLibEnable="false"
		alternativeAddPopup="addNewFADepreciationCalcWindow" addrow="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListFixedAssetDepreciationCalc" 
		editable="false" mouseRightMenu="true" contextMenuId="contextMenu"/>	
		
<div id="contextMenu" class="hide">
	<ul>
		<li action="postAcctgTransDepreciation" id="postAcctgTransDepreciation">
			<i class="icon-save"></i>${uiLabelMap.BACCPosting}
		</li>
		<li action="edit">
			<i class="icon-list"></i>${uiLabelMap.BSViewDetail}
        </li>
	</ul>
</div>					
		
<div id="addNewFADepreciationCalcWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.BACCFADepreciation)}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<form class="form-horizontal form-window-content-custom" id="generalInfo">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span4'>
									<label class=''>${uiLabelMap.BACCMonth}</label>
								</div>
								<div class="span8">
									<div class="row-fluid" style="margin-bottom: 0">
										<div class="span5">
											<div id="month"></div>
										</div>
										<div class="span7">
											<div class="row-fluid" style="margin-bottom: 0">
												<div class="span5">
													<label class=''>${uiLabelMap.BACCYear}</label>
												</div>
												<div class="span7">
													<div id="year"></div>
												</div>
											</div>		
										</div>
									</div>
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
									<label class=''>${uiLabelMap.BACCVoucherNumber}</label>
								</div>
								<div class="span8">
									<input type="text" id="voucherNumber">
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
			<div class ="row-fluid" id="notedPostDepreciation">
				<div><strong>${StringUtil.wrapString(uiLabelMap.BACCNote)}:</strong> <span style="color:red">${StringUtil.wrapString(uiLabelMap.BACCNoteFixedAssetIncreaseItemPosted)}</span></div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddFADepreciation">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddFADepreciation">
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
						<label class='asterisk'>${uiLabelMap.BACCAllocGlAccoutId}</label>
					</div>
					<div class="span7">
						<div id="debitAccDropDown">
							<div id="debitAccGrid"></div>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='asterisk'>${uiLabelMap.BACCdepGlAccountId}</label>
					</div>
					<div class="span7">
						<div id="creditAccDropDown">
							<div id="creditAccGrid"></div>
						</div>
			   		</div>
				</div>
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
						<label class=''>${uiLabelMap.BACCMonthlyDepRate}</label>
					</div>
					<div class="span7">
						<div id="monthlyDepRate"></div>
			   		</div>
				</div>
				
				<div class='row-fluid'>
					<div class='span5'>
						<label class=''>${uiLabelMap.BACCDepreciationAmount}</label>
					</div>
					<div class="span7">
						<div id="depreciationAmount"></div>
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

<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSAdd}@javascript: void(0);@fixedAssetDepreciationCalcNewObj.openPopupAdd()">
<#assign customcontrol2 = "icon-trash open-sans@${uiLabelMap.BSDelete}@javascript: void(0);@fixedAssetDepreciationCalcNewObj.removeItemFromGrid()">
<script type="text/javascript">
	var customcontrol1 = "${StringUtil.wrapString(customcontrol1)}";
	var customcontrol2 = "${StringUtil.wrapString(customcontrol2)}";
</script>

<script type="text/javascript" src="/accresources/js/fixedAsset/ViewFixedAssetDepreciationCalc.js?v=1.0.5"></script>
<script type="text/javascript" src="/accresources/js/fixedAsset/fixedAssetDepreciationCalcNew.js?v=1.0.5"></script>