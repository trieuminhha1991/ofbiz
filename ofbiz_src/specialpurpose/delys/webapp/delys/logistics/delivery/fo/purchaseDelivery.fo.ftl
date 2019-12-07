<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
	<#if defaultFontFamily?has_content>font-family="${defaultFontFamily}"</#if>
>
<#assign localeStr = "VI" />
<#if locale = "en">
	<#assign localeStr = "EN" />
</#if>
    <fo:layout-master-set>
	    <fo:simple-page-master master-name="main-page-landscape" page-width="29.7cm" page-height="21.0cm" 
			margin-top="1cm" margin-bottom="2cm" margin-left="2.5cm" margin-right="2.5cm">
		    <#-- main body -->
		    <fo:region-body margin-bottom="1cm"/>
		    <#-- the header -->
		    <fo:region-before extent="2cm"/>
		    <#-- the footer -->
		    <fo:region-after extent="2cm"/>
		</fo:simple-page-master>
    </fo:layout-master-set>
    <fo:page-sequence master-reference="main-page-landscape">
    <fo:flow flow-name="xsl-region-body">
    	<fo:block font-size="18pt" text-align="center" text-transform="uppercase" font-weight="bold">
	    	${uiLabelMap.CargoUnloadingReport}
    	</fo:block>
    	<fo:block font-size="10pt" text-align="center" margin-top="0.5cm">
	    	<fo:table>
				<fo:table-column/>
				<fo:table-column/>
				<fo:table-column/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block>${uiLabelMap.Template} . . . </fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>${uiLabelMap.PageNumber} . . .</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>${uiLabelMap.ReceiptType}:    <#if receiveType?has_content> ${receiveType}</#if></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	<#if parameters.deliveryId?has_content>
		<fo:block font-size="10pt" text-align="center" margin-top="1cm">
	    	<fo:table>
				<fo:table-column  column-width="20%" />
				<fo:table-column  column-width="20%" />
				<fo:table-column  column-width="25%" />
				<fo:table-column  column-width="35%" />
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<#if deliveryGood.destFacilityId?has_content>
						<#assign facility = delegator.findOne("Facility", {"facilityId" : deliveryGood.destFacilityId}, false)/>
					</#if>
					<#if deliveryGood.destContactMechId?has_content>
						<#assign facAddress = delegator.findOne("PostalAddress", {"contactMechId" : deliveryGood.destContactMechId}, false)/>
					</#if>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-weight="bold">${uiLabelMap.ReceiptDate}:   <#if deliveryGood.deliveryDate?has_content>${deliveryGood.deliveryDate?string["dd/MM/yyyy"]}</#if></fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-weight="bold" margin-left="1.25cm" margin-top="0.02cm">${uiLabelMap.PurchaseOrder}:   ${deliveryGood.orderId?if_exists}</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-weight="bold" text-align="left" margin-left="2cm">${uiLabelMap.Vehicle}:</fo:block>
							<fo:block font-weight="bold" text-align="left" margin-left="2cm">${uiLabelMap.NumberPlate}:</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-weight="bold" text-align="left" margin-left="1cm">${uiLabelMap.FacilityToReceive}:  <#if facility?has_content> ${facility.facilityName?if_exists}</#if></fo:block>
							<fo:block font-weight="bold" text-align="left" margin-left="1cm">${uiLabelMap.FacilityAddress}:   <#if facAddress?has_content> ${facAddress.address1?if_exists}</#if></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block font-size="12pt" text-align="left" margin-top="1cm">
	    	${uiLabelMap.DetailReceiveDelivery}
		</fo:block>
		<fo:block font-size="10pt" margin-top="0.125cm">
			<fo:table border="solid 0.3mm black">
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="20%" border="solid 0.1mm black"/>
				<fo:table-column column-width="7%" border="solid 0.1mm black"/>
				<fo:table-column column-width="8%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-header>
					<fo:table-row border="solid 0.2mm black">
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="10%">${uiLabelMap.ProductId}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="5%">${StringUtil.wrapString(uiLabelMap.ProductName)}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="10%">${uiLabelMap.Unit}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="10%">${uiLabelMap.Packing}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-columns-spanned="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ImportPackingList}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-columns-spanned="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Inspection}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-columns-spanned="2" border="solid 0.2mm black"> 
							<fo:block>${uiLabelMap.Quantity}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.2mm black">
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProduceDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ExpireDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProduceDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ExpireDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ImportPackingList}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Inspection}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<#if listGoodItems?has_content>
						<#list listGoodItems as item>
							<#assign quantityUomId = Static["com.olbius.util.ProductUtil"].getQuantityUomBySupplier(delegator, item.productId, item.orderId)>
							<#if quantityUomId?has_content>
								<#assign uomEntity = delegator.findOne("Uom", {"uomId" : quantityUomId}, false)/>
							</#if>
							<#assign convertNumber = Static["com.olbius.util.ProductUtil"].getConvertPackingNumber(delegator, item.productId, quantityUomId, item.baseQuantityUomId)/>
							<fo:table-row border="solid 0.1mm black" height="15px">
								<fo:table-cell text-align="left" margin-left="1mm">
									<fo:block>${item.productId}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="1mm">
									<fo:block>${item.productName}</fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if uomEntity?has_content>${StringUtil.wrapString(uomEntity.get("description", locale))}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if convertNumber?has_content>1x${convertNumber}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if item.datetimeManufactured?has_content>${item.datetimeManufactured?string["dd/MM/yyyy"]}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if item.expireDate?has_content>${item.expireDate?string["dd/MM/yyyy"]}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if item.actualManufacturedDate?has_content>${item.actualManufacturedDate?string["dd/MM/yyyy"]}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if item.actualExpireDate?has_content>${item.actualExpireDate?string["dd/MM/yyyy"]}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if item.quantity?has_content>${item.quantity?string.number}</#if></fo:block>
								</fo:table-cell>
								<fo:table-cell text-align="left" margin-left="2mm">
									<fo:block><#if item.actualDeliveredQuantity?has_content>${item.actualDeliveredQuantity?string.number}</#if></fo:block>
								</fo:table-cell>
							</fo:table-row>
						</#list>
					</#if>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block font-size="12pt" text-align="left" margin-top="0.5cm">
	    	${uiLabelMap.NoteOfInspection}
		</fo:block>
		<fo:block font-size="10pt" margin-top="0.125cm">
			<fo:table border="solid 0.3mm black">
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="20%" border="solid 0.1mm black"/>
				<fo:table-column column-width="7%" border="solid 0.1mm black"/>
				<fo:table-column column-width="8%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="40%" border="solid 0.1mm black"/>
				<fo:table-header>
					<fo:table-row border="solid 0.2mm black">
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProductId}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${StringUtil.wrapString(uiLabelMap.ProductName)}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Unit}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Quantity}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProduceDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ExpireDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Note}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<#list listOtherItems as item>
						<#assign quantityUomId = Static["com.olbius.util.ProductUtil"].getQuantityUomBySupplier(delegator, item.productId, item.orderId)>
						<#if quantityUomId?has_content>
							<#assign uomEntity = delegator.findOne("Uom", {"uomId" : quantityUomId}, false)/>
						</#if>
						<#assign deliveryType = delegator.findOne("DeliveryType", {"deliveryTypeId" : item.deliveryTypeId}, false)/>
						<fo:table-row border="solid 0.2mm black">
							<fo:table-cell text-align="left" margin-left="1mm">
								<fo:block>${item.productId?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="left" margin-left="1mm">
								<fo:block>${item.productName?if_exists}</fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="left" margin-left="2mm">
								<fo:block><#if uomEntity?has_content>${StringUtil.wrapString(uomEntity.get("description", locale))}</#if></fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="left" margin-left="2mm">
								<fo:block><#if item.actualExportedQuantity?has_content>${item.actualExportedQuantity?string.number}</#if></fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="left" margin-left="2mm">
								<fo:block><#if item.datetimeManufactured?has_content>${item.datetimeManufactured?string["dd/MM/yyyy"]}</#if></fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="left" margin-left="2mm">
								<fo:block><#if item.expireDate?has_content>${item.expireDate?string["dd/MM/yyyy"]}</#if></fo:block>
							</fo:table-cell>
							<fo:table-cell text-align="left" margin-left="2mm">
								<fo:block><#if deliveryType?has_content>${StringUtil.wrapString(deliveryType.get("description", locale))}</#if></fo:block>
							</fo:table-cell>
						</fo:table-row>
					</#list>
				</fo:table-body>
			</fo:table>
		</fo:block>
	<#else>
		<fo:block font-size="10pt" text-align="center" margin-top="1cm">
	    	<fo:table>
				<fo:table-column  column-width="20%" />
				<fo:table-column  column-width="20%" />
				<fo:table-column  column-width="25%" />
				<fo:table-column  column-width="35%" />
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
						<fo:table-cell/>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block font-weight="bold">${uiLabelMap.ReceiptDate}: . . . </fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-weight="bold" margin-left="1.25cm" margin-top="0.02cm">${uiLabelMap.PurchaseOrder}:   ${parameters.orderId} </fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-weight="bold" text-align="left" margin-left="2cm">${uiLabelMap.Vehicle}: . . . </fo:block>
							<fo:block font-weight="bold" text-align="left" margin-left="2cm">${uiLabelMap.NumberPlate}: . . . </fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-weight="bold" text-align="left" margin-left="1cm">${uiLabelMap.FacilityToReceive}: . . . </fo:block>
							<fo:block font-weight="bold" text-align="left" margin-left="1cm">${uiLabelMap.FacilityAddress}: . . . </fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block font-size="12pt" text-align="left" margin-top="1cm">
	    	${uiLabelMap.DetailReceiveDelivery}
		</fo:block>
		<fo:block font-size="10pt" margin-top="0.125cm">
			<fo:table border="solid 0.3mm black">
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="20%" border="solid 0.1mm black"/>
				<fo:table-column column-width="7%" border="solid 0.1mm black"/>
				<fo:table-column column-width="8%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-header>
					<fo:table-row border="solid 0.2mm black">
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="10%">${uiLabelMap.ProductId}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="5%">${StringUtil.wrapString(uiLabelMap.ProductName)}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="10%">${uiLabelMap.Unit}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-rows-spanned="2" border="solid 0.2mm black">
							<fo:block margin-top="10%">${uiLabelMap.Packing}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-columns-spanned="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ImportPackingList}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-columns-spanned="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Inspection}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" number-columns-spanned="2" border="solid 0.2mm black"> 
							<fo:block>${uiLabelMap.Quantity}</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.2mm black">
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProduceDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ExpireDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProduceDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ExpireDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ImportPackingList}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" colspan="2" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Inspection}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
					<fo:table-cell text-align="left" margin-left="1mm">
						<fo:block></fo:block>
					</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block font-size="12pt" text-align="left" margin-top="0.5cm">
	    	${uiLabelMap.NoteOfInspection}
		</fo:block>
		<fo:block font-size="10pt" margin-top="0.125cm">
			<fo:table border="solid 0.3mm black">
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="20%" border="solid 0.1mm black"/>
				<fo:table-column column-width="7%" border="solid 0.1mm black"/>
				<fo:table-column column-width="8%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="10%" border="solid 0.1mm black"/>
				<fo:table-column column-width="40%" border="solid 0.1mm black"/>
				<fo:table-header>
					<fo:table-row border="solid 0.2mm black">
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProductId}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${StringUtil.wrapString(uiLabelMap.ProductName)}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Unit}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Quantity}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ProduceDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.ExpireDate}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center" border="solid 0.2mm black">
							<fo:block>${uiLabelMap.Note}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row border="solid 0.1mm black" height="15px">
						<fo:table-cell text-align="left" margin-left="1mm">
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>						
	</#if>
		<fo:block font-size="10pt" margin-top="1cm" font-weight="bold">
			<fo:table>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.LogisticsManager}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${StringUtil.wrapString(uiLabelMap.QAStaff)}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.Transporter}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.StoreKeeper}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.DeliveryRepresentative}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell text-align="center" >
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block font-size="10pt" font-style="italic" margin-top="1.5cm">
			<fo:table>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-column column-width="20%"/>
				<fo:table-header>
					<fo:table-row>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.DateTimeDetail}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.DateTimeDetail}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.DateTimeDetail}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.DateTimeDetail}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="center">
							<fo:block>${uiLabelMap.DateTimeDetail}</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell text-align="center" >
							<fo:block></fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>