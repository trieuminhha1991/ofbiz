<#include "script/fixedAssetViewScript.ftl"/>
<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.FixedAssetDetail}</h4>
		<div class="widget-toolbar"></div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="row-fluid" style="margin-top: 15px;"><!-- fixed asset info -->
				<div class="span12" style="position: relative;">
					<div class="form-legend" style="margin-bottom: 15px;">
						<div class="contain-legend">
							<span class="content-legend" style="font-size: 15px">
								<a href="javascript:void(0)" title="${StringUtil.wrapString(uiLabelMap.ClickToEdit)}" id="editFixedAssetInfoBtn">
									${StringUtil.wrapString(uiLabelMap.BACCGeneralInfo)}&nbsp;&nbsp;<i class="icon-edit"></i></a>
							</span>
						</div>
						<div class="row-fluid">
							<div class="span12" style="margin-top: 15px; padding-left: 15px">
								<div class="span5">
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCFixedAssetId}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetIdView">${StringUtil.wrapString(fixedAsset.fixedAssetId)}</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCFixedAssetName}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetNameView">${StringUtil.wrapString(fixedAsset.fixedAssetName)}</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCFixedAssetTypeId}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetTypeView">${StringUtil.wrapString(fixedAsset.fixedAssetTypeDesc)}</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.OrganizationUsed}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetPartyView">
												<#if fixedAsset.fullName?exists>
													${fixedAsset.fullName}
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCQuantity}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetQtyView">
												<#if fixedAsset.quantity?exists>
													${fixedAsset.quantity}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCYearMade}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetYearMadeView">
												<#if fixedAsset.yearMade?exists>
													${fixedAsset.yearMade}
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCMark}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetSerialView">
												<#if fixedAsset.serialNumber?exists>
													${StringUtil.wrapString(fixedAsset.serialNumber)}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCReceiptDate}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetReceiptDateView">
												<#if fixedAsset.receiptDate?exists>
													${fixedAsset.receiptDate?string["dd/MM/yyyy"]}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
								</div><!-- ./span5 -->
								<div class="span5">
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCWarrantyPeriod}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetWarrantyPeriodView">
												<#if fixedAsset.warrantyPeriod?exists>
													${StringUtil.wrapString(fixedAsset.warrantyPeriod)}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCWarrantyCondition}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetConditionView">
												<#if fixedAsset.warrantyCondition?exists>
													${StringUtil.wrapString(fixedAsset.warrantyCondition)}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BSSupplier}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetSupplierView">
												<#if fixedAsset.supplierId?exists>
													<#assign supplier = delegator.findOne("PartyGroup", {"partyId": fixedAsset.supplierId}, false)/>
													${StringUtil.wrapString(supplier.groupName)}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.ProductCountryOfOrigin}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetCountryOriginView">
												<#if fixedAsset.countryOrigin?exists>
													${StringUtil.wrapString(fixedAsset.countryOrigin)}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.CommonStatus}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetStatusView">
												<#if fixedAsset.statusId?exists>
													${fixedAsset.statusDesc}
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCDescription}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetDescriptionView">
												<#if fixedAsset.description?exists>
													${StringUtil.wrapString(fixedAsset.description)}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCReceiptNumber}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetReceiptNumberView">
												<#if fixedAsset.receiptNumber?exists>
													${StringUtil.wrapString(fixedAsset.receiptNumber)}
												<#else>
													_________________
												</#if>
											</span>
								   		</div>
									</div>
								</div><!-- ./span5 -->
							</div><!-- ./span12 -->
						</div><!-- ./row-fluid -->
					</div>
				</div>
			</div><!-- ./ fixed asset info -->
			<!-- ======================================  Origin cost and depreciation ================================-->
			<div class="row-fluid" style="margin-top: 15px;"><!-- origin cost and depreciation -->
				<div class="span12" style="position: relative;">
					<div class="form-legend" style="margin-bottom: 15px;">
						<div class="contain-legend">
							<span class="content-legend" style="font-size: 15px">
								<a href="javascript:void(0)" title="${StringUtil.wrapString(uiLabelMap.ClickToEdit)}" id="editDepreciationBtn">
									${StringUtil.wrapString(uiLabelMap.OriginCostAndDepreciation)}&nbsp;&nbsp;<i class="icon-edit"></i></a>
							</span>
						</div>
						<div class="row-fluid">
							<div class="span12" style="margin-top: 15px; padding-left: 15px">
								<div class="span5">
									<div class='row-fluid '>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCDatePurchase}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetDatePurchaseView">
												<#if fixedAsset.datePurchase?exists>
													${fixedAsset.datePurchase?string["dd/MM/yyyy"]}
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCPurCostAcc}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetCostGlAccView">
												<#if fixedAsset.costGlAccountId?exists>
													${fixedAsset.costGlAccountId} - ${fixedAsset.costGlAccountName}
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCPurchaseCost}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetPurchaseCostView">
												<@ofbizCurrency amount=fixedAsset.purchaseCost?if_exists isoCode=fixedAsset.uomId/>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCDepreciation}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetLifeDepreView">
												<@ofbizCurrency amount=fixedAsset.lifeDepAmount?if_exists isoCode=fixedAsset.uomId/>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCUsedTime}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetUsefulLiveView">
												${fixedAsset.usefulLives}&nbsp;${StringUtil.wrapString(uiLabelMap.BSMonthLowercase)}
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCYearlyDepRate}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetYearlyDepRateView">
												<#if fixedAsset.yearlyDepRate?exists>
													${fixedAsset.yearlyDepRate}%
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCMonthlyDepRate}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetMonthlyDepRateView">
												<#assign monthlyDepRate = (fixedAsset.monthlyDepRate * 100)?round/>
												${monthlyDepRate/100}%
											</span>
								   		</div>
									</div>
								</div><!-- ./span5 -->
								<div class="span5">
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCDateAcquired}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetDateAcquiredView">
												<#if fixedAsset.dateAcquired?exists>
													${fixedAsset.dateAcquired?string["dd/MM/yyyy"]}
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid '>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCDepAccount}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetDepreGlAccView">
												<#if fixedAsset.accDepGlAccountId?exists>
													${fixedAsset.accDepGlAccountId} - ${fixedAsset.accDepGlAccountName}
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid '>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCAllocGlAccoutId}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetDepreGlAccView">
												<#if fixedAsset.depGlAccountId?exists>
													${fixedAsset.depGlAccountId} - ${fixedAsset.depGlAccountName}
												</#if>
											</span>
								   		</div>
							   		</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCAnnualDepAmount}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetYearlyDepAmountView">
												<#assign yearlyDepAmount = (fixedAsset.monthlyDepAmount * 12)/>
												<#if fixedAsset.yearlyDepAmount?exists>
													<@ofbizCurrency amount=fixedAsset.yearlyDepAmount isoCode=fixedAsset.uomId/>
												</#if>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCMonthlyDepAmount}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetMonthlyDepAmountView">
												<@ofbizCurrency amount=fixedAsset.monthlyDepAmount isoCode=fixedAsset.uomId/>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCAccumulatedDep}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetAccumulatedDepView">
												<@ofbizCurrency amount=fixedAsset.accumulatedDep isoCode=fixedAsset.uomId/>
											</span>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom5'>
										<div class='span5 align-left'>
											<label><b>${uiLabelMap.BACCRemainingValue}:</b></label>
										</div>
										<div class="span7 green-label">
											<span style="font-size: 14px" id="fixedAssetRemainValueView">
												<@ofbizCurrency amount=fixedAsset.remainingValue isoCode=fixedAsset.uomId/>
											</span>
								   		</div>
									</div>
								</div><!-- ./span5 -->
							</div><!-- ./span12 -->
						</div>
					</div>
				</div>
			</div><!-- ./ origin cost and depreciation -->
			<div class="hr hr-dotted"></div>
			<!-- ======================================   ================================-->
			<div class="row-fluid">
				<div class="span12">
					<#assign datafieldAccompany = "[{name: 'fixedAssetId', type: 'string'},
													{name: 'accompanySeqId', type: 'string'},
													{name: 'componentName', type: 'string'},
													{name: 'unit', type: 'string'},
													{name: 'quantity', type: 'number'},
													{name: 'value', type: 'number'}
													]"/>
					<#assign coliumnAccompany = "{text: '${StringUtil.wrapString(uiLabelMap.FixedAssetAccompanyName)}', filterable : false, datafield: 'componentName', width: '40%',
										        	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
										        		editor.jqxInput({width: cellwidth, height: cellheight});
										        	},
										        	initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
										        		if(typeof(cellvaue) != 'undefined'){
										        			editor.val(cellvaue)
										        		}
										        	}
												},
												{text: '${StringUtil.wrapString(uiLabelMap.BSCalculateUomId)}', datafield: 'unit', width: '15%',
													createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
										        		editor.jqxInput({width: cellwidth, height: cellheight});
										        	},
										        	initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
										        		if(typeof(cellvaue) != 'undefined'){
										        			editor.val(cellvaue)
										        		}
										        	}
												},
												{text: '${StringUtil.wrapString(uiLabelMap.BACCQuantity)}', datafield: 'quantity', columntype: 'numberinput', width: '15%',
													createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
														editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 0});
													},
													initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
														if(typeof(cellvaue) != 'undefined'){
										        			editor.val(cellvaue)
										        		}
										        	},
										        	cellsrenderer: function(row, colum, value){
												  		if(typeof(value) == 'number'){
												  			return '<span style=\"text-align: right\">' + (value) + '</value>';
												  		}
												  	},
												},
												{text: '${StringUtil.wrapString(uiLabelMap.BSValue)}', datafield: 'value', columntype: 'numberinput',
													cellsrenderer: function(row, colum, value){
												  		if(typeof(value) == 'number'){
												  			return '<span style=\"text-align: right\">' + formatcurrency(value) + '</value>';
												  		}
												  	},
												  	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
												  		editor.jqxNumberInput({width: cellwidth, height: cellheight, spinButtons: true, decimalDigits: 2, max: 999999999999, digits: 12, inputMode: 'advanced'});
													},
													initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
														if(typeof(cellvaue) != 'undefined'){
										        			editor.val(cellvaue)
										        		}
										        	}
												}, "/>
					<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.FixedAssetAccompanyList)/>
					<@jqGrid filtersimplemode="false" filterable="false" showtoolbar="true" dataField=datafieldAccompany columnlist=coliumnAccompany
							clearfilteringbutton="false"  editable="false" deleterow="false"
							addrow="false"
							showlist="true" sortable="true" id="gridFixedAccompany"
							customTitleProperties=customTitleProperties
							url="jqxGeneralServicer?sname=JQGetListFixedAssetAccompany&fixedAssetId=${parameters.fixedAssetId}" jqGridMinimumLibEnable="false"/>
				</div>
			</div>
		</div>
	</div>
</div>

<#--<!-- <#include "fixedAssetViewCreatePartyAssign.ftl"/> -->

<#include "fixedAssetEditGeneralInfo.ftl"/>
<#include "fixedAssetEditDepreciation.ftl"/>