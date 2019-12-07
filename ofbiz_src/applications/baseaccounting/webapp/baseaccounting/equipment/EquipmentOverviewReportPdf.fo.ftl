<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main-page" page-width="12in" page-height="9in" margin-top="0in" 
			margin-bottom="0.5in" margin-left="0.3in" margin-right="0.3in">
			<fo:region-body margin-top="0.2in" margin-bottom="0in"/>
			<fo:region-before extent="0in"/>
			<fo:region-after extent="0in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>
	<fo:page-sequence master-reference="main-page">
		<fo:static-content flow-name="xsl-region-after">
			<fo:block font-size="10pt" text-align="center" space-before="10pt">
                ${uiLabelMap.CommonPage} <fo:page-number /> ${uiLabelMap.CommonOf} <fo:page-number-citation ref-id="theEnd"/>
            </fo:block>
		</fo:static-content>
		<fo:flow flow-name="xsl-region-body">
			<fo:block>
				<fo:table margin-top="25px">
					<fo:table-column column-width="100%"/>
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:block font-size="15px" font-weight="bold" text-align="center" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.BACCEquipmentOverviewReport)}</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell>
								<fo:block text-align="center">
									<#if parameters.month?exists>
										<#assign month = parameters.month?number + 1/> 
									<#else>
										<#assign month = "....."/>
									</#if>
									${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${month} ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${StringUtil.wrapString(parameters.year?if_exists)}						
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
				<#assign listData = resultService.listData />
				<fo:table margin-top="5mm" font-size="9px" border="solid 0.5mm black" >
					<fo:table-column border="solid 0.5mm black" column-width="3%"/>
					<fo:table-column border="solid 0.5mm black" column-width="8%"/>
					<fo:table-column border="solid 0.5mm black" column-width="13%"/>
					<fo:table-column border="solid 0.5mm black" column-width="6%"/>
					<fo:table-column border="solid 0.5mm black" column-width="6%"/>
					<fo:table-column border="solid 0.5mm black" column-width="10%"/>
					<fo:table-column border="solid 0.5mm black" column-width="6%"/>
					<fo:table-column border="solid 0.5mm black" column-width="10%"/>
					<fo:table-column border="solid 0.5mm black" column-width="6%"/>
					<fo:table-column border="solid 0.5mm black" column-width="10%"/>
					<fo:table-column border="solid 0.5mm black" column-width="6%"/>
					<fo:table-column border="solid 0.5mm black" column-width="6%"/>
					<fo:table-column border="solid 0.5mm black" column-width="10%"/>
					<fo:table-body>
						<fo:table-row border="solid 0.5mm black">
							<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BSSTT)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCEquipmentId)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" number-rows-spanned="2" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCEquimentName)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" number-columns-spanned="3" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCIncrease)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCAllocated)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" number-columns-spanned="2" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDecrease)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" number-columns-spanned="3" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCRemain)}</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row border="solid 0.5mm black">
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCQuantity)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCAllowTimes)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonValue)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCTimes)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonValue)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCQuantity)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonValue)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCQuantity)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCAllowTimes)}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
								<fo:block font-weight="bold" text-align="center">${StringUtil.wrapString(uiLabelMap.CommonValue)}</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<#if (listData?size > 0)>
							<#assign currencyUomId = listData.get(0).currencyUomId/> 
							<#list listData as data>
								<fo:table-row border="solid 0.5mm black" keep-together.within-page="always" >
									<fo:table-cell text-align="center" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="center">${data_index + 1}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="left">${data.equipmentId}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="left" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="left">${data.equipmentName}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">${data.quantity}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">${data.allocationTimes}</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=data.totalPrice isoCode=data.currencyUomId/>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<#if (data.allocatedCount >= 0)>
												${data.allocatedCount}
											</#if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<#if (data.allocatedAmount >= 0)>
												<@ofbizCurrency amount=data.allocatedAmount isoCode=data.currencyUomId/>
											</#if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<#if (data.quantityDecrease >= 0)>
												${data.quantityDecrease}
											</#if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<#if (data.decreaseTotal >= 0)>
												<@ofbizCurrency amount=data.decreaseTotal isoCode=data.currencyUomId/>
											</#if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<#if (data.quantityRemain >= 0)>
												${data.quantityRemain}
											</#if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<#if (data.allocationTimeRemain >= 0)>
												${data.allocationTimeRemain}
											</#if>
										</fo:block>
									</fo:table-cell>
									<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
										<fo:block text-align="right">
											<@ofbizCurrency amount=data.amountRemain isoCode=data.currencyUomId/>
										</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</#list>
							<fo:table-row border="solid 0.5mm black" keep-together.within-page="always" >
								<fo:table-cell text-align="center" display-align="center" number-columns-spanned="3" padding="1mm 1mm 0">
									<fo:block font-weight="bold" text-align="left">${StringUtil.wrapString(uiLabelMap.BACCAmountTotal)}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumQuantity?exists && (resultService.sumQuantity > 0)>
											${resultService.sumQuantity}
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumAllocationTimes?exists && (resultService.sumAllocationTimes > 0)>
											${resultService.sumAllocationTimes}
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumTotalPrice?exists>
											<@ofbizCurrency amount=resultService.sumTotalPrice isoCode=currencyUomId/>
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumAllocatedCount?exists && (resultService.sumAllocatedCount > 0)>
											${resultService.sumAllocatedCount}
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumAllocatedAmount?exists && (resultService.sumAllocatedAmount > 0)>
											<@ofbizCurrency amount=resultService.sumAllocatedAmount isoCode=currencyUomId/>
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumQuantityDecrease?exists && (resultService.sumQuantityDecrease > 0)>
											${resultService.sumQuantityDecrease}
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumDecreaseTotal?exists && (resultService.sumDecreaseTotal > 0)>
											<@ofbizCurrency amount=resultService.sumDecreaseTotal isoCode=currencyUomId/>
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumQuantityRemain?exists && (resultService.sumQuantityRemain > 0)>
											${resultService.sumQuantityRemain}
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumAllocationTimeRemain?exists && (resultService.sumAllocationTimeRemain > 0)>
											${resultService.sumAllocationTimeRemain}
										</#if>
									</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="right" display-align="center" padding="1mm 1mm 0">
									<fo:block text-align="right" font-weight="bold">
										<#if resultService.sumAmountRemain?exists>
											<@ofbizCurrency amount=resultService.sumAmountRemain isoCode=currencyUomId/>
										</#if>
									</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</#if>
					</fo:table-body>
				</fo:table>
				
				<fo:table margin-top="2mm" font-size="10px">
					<fo:table-column />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell text-align="left" display-align="center">
								<fo:block text-align="left">
									<fo:inline>- ${StringUtil.wrapString(uiLabelMap.SoTaiSanCo)} <fo:page-number-citation ref-id="theEnd"/></fo:inline>
									<fo:inline text-transform="lowercase">${StringUtil.wrapString(uiLabelMap.CommonPage)}, ${StringUtil.wrapString(uiLabelMap.DanhSoTuTrang01DenTrang)} <fo:page-number-citation ref-id="theEnd"/></fo:inline>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>		
				</fo:table>
				<fo:table margin-top="3mm" font-size="9px" keep-together.within-page="always">
					<fo:table-column width="33%"/>
					<fo:table-column width="33%"/>
					<fo:table-column />
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell></fo:table-cell>
							<fo:table-cell></fo:table-cell>
							<fo:table-cell text-align="center" display-align="center">
								<fo:block text-align="center" font-style="italic">
									${StringUtil.wrapString(uiLabelMap.BACCDay)}....${StringUtil.wrapString(uiLabelMap.BSMonthLowercase)}....${StringUtil.wrapString(uiLabelMap.BSYearLowercase)}.....
								</fo:block>
							</fo:table-cell>
						</fo:table-row>	
					</fo:table-body>
				</fo:table>
				<fo:table margin-top="3mm" font-size="9px" keep-together.within-page="always">
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
									${StringUtil.wrapString(uiLabelMap.BACCDirector2)}
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
							<fo:table-cell text-align="center" display-align="center">
								<fo:block text-align="center" font-style="italic" font-size="8px">
									(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})
								</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center">
								<fo:block text-align="center" font-style="italic" font-size="8px">
									(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})
								</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="center" display-align="center">
								<fo:block text-align="center" font-style="italic" font-size="8px">
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
</fo:root>
</#escape>	