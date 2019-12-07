<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main-page" page-width="8.5in" page-height="11in" margin-top="0in" margin-bottom="0in" margin-left="0.5in" margin-right="0.5in">
			<fo:region-body margin-top="0.5in" margin-bottom="0in"/>
			<fo:region-before extent="0in"/>
			<fo:region-after extent="0in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>

	<#assign fontSize="10px"/>
	<#assign right="60px"/>
	<#assign line="4px"/>
	
	<fo:page-sequence master-reference="main-page">
		<fo:flow flow-name="xsl-region-body">
		<#escape x as x?xml>

		<fo:block font-size="12" font-weight="bold">${organizationName}</fo:block>
		<fo:block font-size="12" font-weight="bold">${uiLabelMap.HRCommonUnit}: ${stockEvent.facilityId}</fo:block>
		<fo:block font-size="18" font-weight="bold" text-align="center" text-transform="uppercase" margin="10px 0px 0px 0px">${uiLabelMap.DmsPhieuKiemKe}</fo:block>
	
		<fo:table margin="30px 0px 0px 0px">
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
			<fo:table-body>
			
				<fo:table-row>
					<fo:table-cell width="100px">
						<fo:block font-size="${fontSize}">${uiLabelMap.PhysicalInventoryId}:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="${fontSize}" font-weight="bold">${stockEvent.eventId}</fo:block>
					</fo:table-cell>
					<fo:table-cell width="110px">
						<fo:block font-size="${fontSize}">${uiLabelMap.DmsStockEventName}:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="${fontSize}" font-weight="bold">${stockEvent.eventName}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell width="110px">
						<fo:block font-size="${fontSize}" margin-top="10px">${uiLabelMap.DmsInputEmpl}:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="${fontSize}" font-weight="bold" margin-top="10px">${partyInput?if_exists}</fo:block>
					</fo:table-cell>
					<fo:table-cell width="100px">
						<fo:block font-size="${fontSize}" margin-top="10px">${uiLabelMap.DmsThongTinDinhVi}:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="${fontSize}" font-weight="bold" margin-top="10px">${location?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell width="100px">
						<fo:block font-size="${fontSize}" margin-top="10px">${uiLabelMap.DmsNgayKiemKe}:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="${fontSize}" margin-top="10px" font-weight="bold">${entryDate}</fo:block>
					</fo:table-cell>
					<fo:table-cell width="100px">
						<fo:block font-size="${fontSize}" margin-top="10px">${uiLabelMap.DmsTai}:</fo:block>
					</fo:table-cell>
					<fo:table-cell number-columns-spanned="2">
						<fo:block font-size="${fontSize}" margin-top="10px" font-weight="bold">${facilityName?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>

			</fo:table-body>
		</fo:table>	
		<fo:table margin="30px 0px 0px 0px">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<#if stockEventItemStatus.statusId != "STOCKING_CREATED">
			<fo:table-column/>
			</#if>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell width="25px" text-align="center" border="1px solid black">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${uiLabelMap.BSSTT}</fo:block>
					</fo:table-cell>
					<fo:table-cell width="120px" text-align="center" border="1px solid black">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.UPC)}</fo:block>
					</fo:table-cell>
					<fo:table-cell width="100px" text-align="center" border="1px solid black">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.BSProductId)}</fo:block>
					</fo:table-cell>
					<fo:table-cell width="200px" text-align="center" border="1px solid black">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.BSProductName)}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center" border="1px solid black">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.DmsSoLuongKiem)}</fo:block>
					</fo:table-cell>
					<#if stockEventItemStatus.statusId != "STOCKING_CREATED">
					<fo:table-cell text-align="center" border="1px solid black">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.DmsSoLuongKiemCheo)}</fo:block>
					</fo:table-cell>
					</#if>
				</fo:table-row>
				
				<#list stockEventItem as event>
					<fo:table-row>
						<fo:table-cell width="25px" text-align="center" border="1px solid black">
							<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${(event_index) + 1}</fo:block>
						</fo:table-cell>
						<fo:table-cell width="120px" text-align="center" border="1px solid black">
							<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(event.idValue?if_exists)}</fo:block>
						</fo:table-cell>
						<fo:table-cell width="100px" text-align="center" border="1px solid black">
							<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(event.productCode?if_exists)}</fo:block>
						</fo:table-cell>
						<fo:table-cell width="200px" text-align="center" border="1px solid black">
							<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(event.productName?if_exists)}</fo:block>
						</fo:table-cell>
						<fo:table-cell text-align="right" border="1px solid black">
							<fo:block font-size="${fontSize}" margin="${line} 5px 0px 0px">${event.quantity?if_exists}</fo:block>
						</fo:table-cell>
						<#if stockEventItemStatus.statusId != "STOCKING_CREATED">
						<fo:table-cell text-align="right" border="1px solid black">
							<fo:block font-size="${fontSize}" margin="${line} 5px 0px 0px">${event.quantityRecheck?if_exists}</fo:block>
						</fo:table-cell>
						</#if>
					</fo:table-row>
				</#list>
				
			</fo:table-body>
		</fo:table>	

		<fo:table margin="30px 0px 0px 0px">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell text-align="center">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.DmsCountEmpl)}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.DmsSCanEmpl)}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(uiLabelMap.DmsCheckEmpl)}</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:table margin="50px 0px 0px 0px">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell text-align="center">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(partyCount?if_exists)}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(partyScan?if_exists)}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block font-size="${fontSize}" margin="${line} 0px 0px 0px">${StringUtil.wrapString(partyCheck?if_exists)}</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		</#escape>
		</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>