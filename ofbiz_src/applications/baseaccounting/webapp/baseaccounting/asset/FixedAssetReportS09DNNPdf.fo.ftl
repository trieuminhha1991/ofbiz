<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main-page" page-width="10in" page-height="9in" margin-top="0in" 
			margin-bottom="0in" margin-left="0.3in" margin-right="0.3in">
			<fo:region-body margin-top="1in" margin-bottom="0in"/>
			<fo:region-before extent="0in"/>
			<fo:region-after extent="0.4in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>

	<#assign fontsize="10px"/>
	<#assign right="100px"/>
	<#assign line="4.5px"/>
	<#assign row="16px"/>
	<#assign fixedAssetAndTypeMap = resultService.fixedAssetAndTypeMap/>
	<#assign fixedAssetTypeIdList = resultService.fixedAssetTypeIdList/>
	<#assign companyName = resultService.companyName/>
	<#assign address = resultService.address/>
	
	<#if (fixedAssetTypeIdList?size > 0)>
		<#list fixedAssetTypeIdList as fixedAssetTypeId>
			<#assign tempListFixedAsset = fixedAssetAndTypeMap.get(fixedAssetTypeId)/>
			<fo:page-sequence master-reference="main-page" initial-page-number="1" force-page-count="no-force">
				<fo:static-content flow-name="xsl-region-after">
					<fo:block font-size="10pt" text-align="center" space-before="10pt">
		                ${uiLabelMap.CommonPage} <fo:page-number /> ${uiLabelMap.CommonOf} <fo:page-number-citation ref-id="theEnd${fixedAssetTypeId}"/>
		            </fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<#assign fixedAssetType = delegator.findOne("FixedAssetType", Static["org.ofbiz.base.util.UtilMisc"].toMap("fixedAssetTypeId", fixedAssetTypeId), false)/>
						<fo:table font-size="${fontsize}">
							<fo:table-column column-width="75%"/>
							<fo:table-column />
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-weight="bold" text-align="left">${StringUtil.wrapString(uiLabelMap.BACCOrganization)}: ${companyName}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.S09DNNTemplate)}</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-weight="bold" text-align="left">${StringUtil.wrapString(uiLabelMap.BSAddress)}: ${address}</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="center">(${StringUtil.wrapString(uiLabelMap.TT133_2016_TT_BTC)})</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
						<fo:table font-size="${fontsize}" margin-top="25px">
							<fo:table-column column-width="100%"/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell>
										<fo:block font-size="15px" font-weight="bold" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.BACCFixedAsset)}</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block text-align="center">
											<#if parameters.monthQuarterValue?exists>
												<#assign monthQuarterValue = parameters.monthQuarterValue?number + 1/> 
											<#else>
												<#assign monthQuarterValue = "....."/>
											</#if>
											<#if parameters.dateType?exists>
												<#if parameters.dateType == "month">
													${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${monthQuarterValue?if_exists}
												<#elseif parameters.dateType == "quarter">
													${StringUtil.wrapString(uiLabelMap.CommonQuarter)} ${monthQuarterValue?if_exists}
												</#if>
											</#if>
											 ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${StringUtil.wrapString(parameters.year?if_exists)}						
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell>
										<fo:block text-align="center">
											<fo:block>
												<fo:inline font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCFixedAssetTypeId)}:</fo:inline>
												<fo:inline>${StringUtil.wrapString(fixedAssetType.description?if_exists)}</fo:inline>
											</fo:block>
										</fo:block>
									</fo:table-cell>	
								</fo:table-row>
							</fo:table-body>	
						</fo:table>
						<fo:table margin-top="5mm" font-size="8px" border="solid 0.5mm black" >
							<fo:table-column border="solid 0.5mm black" column-width="3%"/>
							<fo:table-column border="solid 0.5mm black" column-width="7%"/>
							<fo:table-column border="solid 0.5mm black" column-width="7%"/>
							<fo:table-column border="solid 0.5mm black" column-width="8%"/>
							<fo:table-column border="solid 0.5mm black" column-width="8%"/>
							<fo:table-column border="solid 0.5mm black" column-width="6%"/>
							<fo:table-column border="solid 0.5mm black" column-width="7%"/>
							<fo:table-column border="solid 0.5mm black" column-width="10%"/>
							<fo:table-column border="solid 0.5mm black" column-width="5%"/>
							<fo:table-column border="solid 0.5mm black" column-width="10%"/>
							<fo:table-column border="solid 0.5mm black" column-width="10%"/>
							<fo:table-column border="solid 0.5mm black" column-width="7%"/>
							<fo:table-column border="solid 0.5mm black" column-width="7%"/>
							<fo:table-column border="solid 0.5mm black" column-width="6%"/>
							<fo:table-body>
								<fo:table-row border="solid 0.5mm black">
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="3" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BSSTT)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-columns-spanned="7" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCNewFixedAsset)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-columns-spanned="3" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCFADepreciation)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-columns-spanned="3" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCFADecrement)}</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row border="solid 0.5mm black">
									<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonVoucher)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCFixedAssetIdShort)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.FixedAssetNameSpecSign)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.FixedAssetMadeIn)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.UsingFromMonthYear)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCPurchaseCost)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDepreciation)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.FAAccumulatedDepreciation)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonVoucher)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCFixedAssetDecrementReason)}</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row border="solid 0.5mm black">
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCVoucherID)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDateMonth)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.DepreciationPercentage)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.DepreciationLevel)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCVoucherID)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDateMonthYear)}</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row border="solid 0.5mm black">
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">A</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">B</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">C</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">D</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">E</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">G</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">H</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">1</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">2</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">3</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">4</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">I</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">K</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block font-weight="bold" text-align="center">L</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<#assign purchaseCostTotal = 0/>
								<#assign depreciationAmountTotal = 0/>
								<#assign accumulatedDepTotal = 0/>
								<#assign currencyUomId = tempListFixedAsset.get(0).uomId/> 
								<#list tempListFixedAsset as fixedAsset>
									<fo:table-row border="solid 0.5mm black" keep-together.within-page="always" >
										<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="center">${fixedAsset_index + 1}</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="left">
												${fixedAsset.serialNumber?if_exists}
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="center">
												<#if fixedAsset.receiptDate?exists>
													${fixedAsset.receiptDate?string["dd/MM/yyyy"]}
												</#if>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="left">
												${StringUtil.wrapString(fixedAsset.fixedAssetId?if_exists)}
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="left">
												${StringUtil.wrapString(fixedAsset.fixedAssetName)}
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="left">
												${StringUtil.wrapString(fixedAsset.countryOrigin?if_exists)}
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="center">
												<#if fixedAsset.dateAcquired?exists>
													${fixedAsset.dateAcquired?string["dd/MM/yyyy"]}
												</#if>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="right">
												<@ofbizCurrency amount=fixedAsset.purchaseCost isoCode=fixedAsset.uomId/>
												<#assign purchaseCostTotal = purchaseCostTotal + fixedAsset.purchaseCost/> 	
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="right">
												${fixedAsset.monthlyDepRate}%
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="right">
												<#assign fixedAssetDepreciationCalcItemList = delegator.findByAnd("FixedAssetDepreciationCalcAndItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("fixedAssetId", fixedAsset.fixedAssetId), Static["org.ofbiz.base.util.UtilMisc"].toList("-voucherDate"), false)/>
												<#if (fixedAssetDepreciationCalcItemList?size > 0)>
													<#assign fixedAssetDepreciationCalcItem = fixedAssetDepreciationCalcItemList.get(0)/>
												</#if>
												<#if fixedAssetDepreciationCalcItem?exists>
													<@ofbizCurrency amount=fixedAssetDepreciationCalcItem.depreciationAmount isoCode=fixedAsset.uomId/>
													<#assign depreciationAmountTotal = depreciationAmountTotal + fixedAssetDepreciationCalcItem.depreciationAmount/>
												</#if>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="right">
												<@ofbizCurrency amount=fixedAsset.accumulatedDep isoCode=fixedAsset.uomId/>
												<#assign accumulatedDepTotal = accumulatedDepTotal + fixedAsset.accumulatedDep/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
											<#assign fixedAssetDecreaseItemList = delegator.findByAnd("FixedAssetDecreaseAndItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("fixedAssetId", fixedAsset.fixedAssetId), Static["org.ofbiz.base.util.UtilMisc"].toList("-voucherDate"), false)/>
											<#if (fixedAssetDecreaseItemList?size > 0)>
												<#assign fixedAssetDecreaseItem = fixedAssetDecreaseItemList.get(0)/>
											</#if>
											<fo:block text-align="left" >
												<#if fixedAssetDecreaseItem?exists>
													${fixedAssetDecreaseItem.voucherNumber?if_exists}
												</#if>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="center">
												<#if fixedAssetDecreaseItem?exists>
													${fixedAssetDecreaseItem.voucherDate?string["dd/MM/yyyy"]}
												</#if>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
											<fo:block text-align="left">
												<#if fixedAssetDecreaseItem?exists && fixedAssetDecreaseItem.decreaseReasonTypeId?exists>
													<#assign decreaseReasonType = delegator.findOne("FixedAssetDecrReasonType", Static["org.ofbiz.base.util.UtilMisc"].toMap("decreaseReasonTypeId", fixedAssetDecreaseItem.decreaseReasonTypeId), false)/>
													${StringUtil.wrapString(decreaseReasonType.description)}
												</#if>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</#list>
								<fo:table-row border="solid 0.5mm black" keep-together.within-page="always" >
									<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0" number-columns-spanned="7">
										<fo:block text-align="left" font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCFixedAssetTotal)}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right" font-weight="bold">
											<@ofbizCurrency amount=purchaseCostTotal isoCode=currencyUomId/>
										</fo:block>
									</fo:table-cell>	
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right" font-weight="bold">
											<@ofbizCurrency amount=depreciationAmountTotal isoCode=currencyUomId/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right" font-weight="bold">
											<@ofbizCurrency amount=accumulatedDepTotal isoCode=currencyUomId/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
						<fo:table margin-top="2mm" font-size="9px">
							<fo:table-column />
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell text-align="left" display-align="center">
										<fo:block text-align="left">
											<fo:inline>- ${StringUtil.wrapString(uiLabelMap.SoTaiSanCo)} <fo:page-number-citation ref-id="theEnd${fixedAssetTypeId}"/></fo:inline>
											<fo:inline text-transform="lowercase">${StringUtil.wrapString(uiLabelMap.CommonPage)}, ${StringUtil.wrapString(uiLabelMap.DanhSoTuTrang01DenTrang)} <fo:page-number-citation ref-id="theEnd${fixedAssetTypeId}"/></fo:inline>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell text-align="left" display-align="center">
										<fo:block text-align="left" margin-top="2mm">
											- ${StringUtil.wrapString(uiLabelMap.NgayMoSo)}:..................
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>		
						</fo:table>
						<fo:table margin-top="3mm" font-size="9px">
							<fo:table-column width="33%"/>
							<fo:table-column width="33%"/>
							<fo:table-column />
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell></fo:table-cell>
									<fo:table-cell></fo:table-cell>
									<fo:table-cell text-align="center" display-align="center">
										<fo:block text-align="center">
											${StringUtil.wrapString(uiLabelMap.BACCDay)}....${StringUtil.wrapString(uiLabelMap.BSMonthLowercase)}....${StringUtil.wrapString(uiLabelMap.BSYearLowercase)}.....
										</fo:block>
									</fo:table-cell>
								</fo:table-row>	
							</fo:table-body>
						</fo:table>
						<fo:table margin-top="3mm" font-size="9px">
							<fo:table-column width="33%"/>
							<fo:table-column width="33%"/>
							<fo:table-column/>
							<fo:table-body>
								<fo:table-row>
									<fo:table-cell text-align="center" display-align="center">
										<fo:block text-align="center" font-weight="bold">
											${StringUtil.wrapString(uiLabelMap.NguoiLapBieu)}
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center">
										<fo:block text-align="center" font-weight="bold">
											${StringUtil.wrapString(uiLabelMap.BACCChiefAccount)}
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center">
										<fo:block text-align="center" font-weight="bold">
											${StringUtil.wrapString(uiLabelMap.NguoiDaiDienTheoPhapLuat)}
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell text-align="center" display-align="center">
										<fo:block text-align="center">
											(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center">
										<fo:block text-align="center">
											(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="center" display-align="center">
										<fo:block text-align="center">
											(${StringUtil.wrapString(uiLabelMap.KyHoTenDongDau)})
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
					</fo:block>
					<fo:block id="theEnd${fixedAssetTypeId}"></fo:block>
				</fo:flow>	
			</fo:page-sequence>
		</#list>
	<#else>
		<fo:page-sequence master-reference="main-page">
			<fo:static-content flow-name="xsl-region-after">
				<fo:block font-size="10pt" text-align="center" space-before="10pt">
	                ${uiLabelMap.CommonPage} <fo:page-number /> ${uiLabelMap.CommonOf} <fo:page-number-citation ref-id="theEnd"/>
	            </fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body">
				<fo:block>
					<fo:table font-size="${fontsize}">
						<fo:table-column column-width="75%"/>
						<fo:table-column />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-weight="bold" text-align="left">${StringUtil.wrapString(uiLabelMap.BACCOrganization)}: ${companyName}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.S09DNNTemplate)}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-weight="bold" text-align="left">${StringUtil.wrapString(uiLabelMap.BSAddress)}: ${address}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="center">(${StringUtil.wrapString(uiLabelMap.TT133_2016_TT_BTC)})</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:table font-size="${fontsize}" margin-top="25px">
						<fo:table-column column-width="100%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="15px" font-weight="bold" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.BACCFixedAsset)}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="center">
										<#if parameters.monthQuarterValue?exists>
											<#assign monthQuarterValue = parameters.monthQuarterValue?number + 1/> 
										<#else>
											<#assign monthQuarterValue = "....."/>
										</#if>
										<#if parameters.dateType?exists>
											<#if parameters.dateType == "month">
												${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${monthQuarterValue?if_exists}
											<#elseif parameters.dateType == "quarter">
												${StringUtil.wrapString(uiLabelMap.CommonQuarter)} ${monthQuarterValue?if_exists}
											</#if>
										</#if>
										${StringUtil.wrapString(uiLabelMap.BACCYear)} ${StringUtil.wrapString(parameters.year?if_exists)}	
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block text-align="center">
										<fo:block>
											<fo:inline font-weight="bold">${StringUtil.wrapString(uiLabelMap.BACCFixedAssetTypeId)}:....................................</fo:inline>
										</fo:block>
									</fo:block>
								</fo:table-cell>	
							</fo:table-row>
						</fo:table-body>	
					</fo:table>
					<fo:table margin-top="5mm" font-size="8px" border="solid 0.5mm black" >
						<fo:table-column border="solid 0.5mm black" column-width="2%"/>
						<fo:table-column border="solid 0.5mm black" column-width="7%"/>
						<fo:table-column border="solid 0.5mm black" column-width="7%"/>
						<fo:table-column border="solid 0.5mm black" column-width="9%"/>
						<fo:table-column border="solid 0.5mm black" column-width="5%"/>
						<fo:table-column border="solid 0.5mm black" column-width="9%"/>
						<fo:table-column border="solid 0.5mm black" column-width="9%"/>
						<fo:table-column border="solid 0.5mm black" column-width="9%"/>
						<fo:table-column border="solid 0.5mm black" column-width="4.5%"/>
						<fo:table-column border="solid 0.5mm black" column-width="8.5%"/>
						<fo:table-column border="solid 0.5mm black" column-width="8.5%"/>
						<fo:table-column border="solid 0.5mm black" column-width="7%"/>
						<fo:table-column border="solid 0.5mm black" column-width="7%"/>
						<fo:table-column border="solid 0.5mm black" column-width="7.5%"/>
						<fo:table-body>
							<fo:table-row border="solid 0.5mm black">
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="3" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BSSTT)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-columns-spanned="7" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCNewFixedAsset)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-columns-spanned="3" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCFADepreciation)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-columns-spanned="3" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCFADecrement)}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row border="solid 0.5mm black">
								<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonVoucher)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.FixedAssetNameSpecSign)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.FixedAssetMadeIn)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.UsingFromMonthYear)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.FixedAssetSerialNumber)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCPurchaseCost)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDepreciation)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.FAAccumulatedDepreciation)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonVoucher)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCFixedAssetDecrementReason)}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row border="solid 0.5mm black">
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCVoucherID)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDateMonth)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.DepreciationPercentage)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.DepreciationLevel)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCVoucherID)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDateMonthYear)}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row border="solid 0.5mm black">
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">A</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">B</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">C</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">D</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">E</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">G</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">H</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">1</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">2</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">3</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">4</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">I</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">K</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="center">L</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
					<fo:table margin-top="2mm" font-size="9px">
						<fo:table-column />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell text-align="left" display-align="center">
									<fo:block text-align="left">
										<fo:inline>- ${StringUtil.wrapString(uiLabelMap.SoTaiSanCo)} ... </fo:inline> 
										<fo:inline text-transform="lowercase">${StringUtil.wrapString(uiLabelMap.CommonPage)}, ${StringUtil.wrapString(uiLabelMap.DanhSoTuTrang01DenTrang)} ...</fo:inline>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell text-align="left" display-align="center">
									<fo:block text-align="left" margin-top="2mm">
										- ${StringUtil.wrapString(uiLabelMap.NgayMoSo)}:..................
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>		
					</fo:table>
					<fo:table margin-top="3mm" font-size="9px">
						<fo:table-column width="33%"/>
						<fo:table-column width="33%"/>
						<fo:table-column />
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell></fo:table-cell>
								<fo:table-cell></fo:table-cell>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block text-align="center">
										${StringUtil.wrapString(uiLabelMap.BACCDay)}....${StringUtil.wrapString(uiLabelMap.BSMonthLowercase)}....${StringUtil.wrapString(uiLabelMap.BSYearLowercase)}.....
									</fo:block>
								</fo:table-cell>
							</fo:table-row>	
						</fo:table-body>
					</fo:table>
					<fo:table margin-top="3mm" font-size="9px">
						<fo:table-column width="33%"/>
						<fo:table-column width="33%"/>
						<fo:table-column/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block text-align="center" font-weight="bold">
										${StringUtil.wrapString(uiLabelMap.NguoiLapBieu)}
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block text-align="center" font-weight="bold">
										${StringUtil.wrapString(uiLabelMap.BACCChiefAccount)}
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block text-align="center" font-weight="bold">
										${StringUtil.wrapString(uiLabelMap.NguoiDaiDienTheoPhapLuat)}
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block text-align="center">
										(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block text-align="center">
										(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="center" display-align="center">
									<fo:block text-align="center">
										(${StringUtil.wrapString(uiLabelMap.KyHoTenDongDau)})
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
				<fo:block id="theEnd"></fo:block>
			</fo:flow>	
		</fo:page-sequence>
	</#if>
</fo:root>	
</#escape>