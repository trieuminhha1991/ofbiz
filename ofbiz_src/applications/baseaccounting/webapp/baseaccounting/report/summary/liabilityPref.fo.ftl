<#escape x as x?xml>
<#assign listLiabilityPrefs = Static["com.olbius.acc.report.summary.LiabilityPref"].getAccruedLiabilityPref(parameters.partyId?if_exists, parameters.organizationPartyId?if_exists, parameters.prefDate?if_exists, delegator)?if_exists>

<#assign partyNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, parameters.organizationPartyId?if_exists, true, true)?if_exists>
<#assign partyAddressTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyPostalAddress(parameters.organizationPartyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
<#assign partyTelePhoneTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.organizationPartyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
<#assign faxNumber = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.organizationPartyId?if_exists, "FAX_NUMBER", delegator)?if_exists>
<#assign orgTo = delegator.findOne("Party", {"partyId" : parameters.organizationPartyId?if_exists}, false)>
<#assign repTo = Static["com.olbius.basehr.util.PartyUtil"].getManagerbyOrg(orgTo, delegator)?if_exists/>
<#assign repNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, repTo?if_exists, true, true)?if_exists>
<#assign emplPositionListTo = Static["com.olbius.basehr.util.PartyUtil"].getPositionTypeOfEmplAtTime(delegator, repTo?if_exists, nowTimestamp)?if_exists/>
<#if emplPositionListTo?exists && emplPositionListTo?has_content>
	<#assign emplPosTo = emplPositionListTo.get(0) />
</#if>

<#assign partyNameFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, parameters.partyId?if_exists, true, true)?if_exists>
<#assign partyAddressFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyPostalAddress(parameters.partyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
<#assign partyTelePhoneFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.partyId?if_exists, "PRIMARY_LOCATION", delegator)?if_exists>
<#assign faxNumberFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyTelephone(parameters.partyId?if_exists, "FAX_NUMBER", delegator)?if_exists>
<#assign orgFrom = delegator.findOne("Party", {"partyId" : parameters.partyId?if_exists}, false)>
<#assign repFrom = Static["com.olbius.basehr.util.PartyUtil"].getManagerbyOrg(orgTo, delegator)?if_exists/>
<#assign repNameFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, repTo?if_exists, true, true)?if_exists>
<#assign emplPositionListFrom = Static["com.olbius.basehr.util.PartyUtil"].getPositionTypeOfEmplAtTime(delegator, repTo?if_exists, nowTimestamp)?if_exists/>
<#if emplPositionListFrom?exists && emplPositionListFrom?has_content>
	<#assign emplPosFrom = emplPositionListFrom.get(0)?if_exists />
</#if>
<#assign openingBal = Static["com.olbius.acc.report.summary.LiabilityPref"].getOpeningBalLiability(parameters.partyId?if_exists, parameters.organizationPartyId?if_exists, parameters.prefDate?if_exists, delegator)?if_exists>
<#assign openingBalStr = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(openingBal?if_exists?double, "VND", locale, 2) />
<#assign paidAmount = Static["com.olbius.acc.report.summary.LiabilityPref"].getPaidLiability(parameters.partyId?if_exists, parameters.organizationPartyId?if_exists, parameters.prefDate?if_exists, delegator)?if_exists>
<#assign paidAmountStr = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(paidAmount?if_exists?double, "VND", locale, 2) />
<#assign notPaidAmount = Static["com.olbius.acc.report.summary.LiabilityPref"].getNotPaidLiability(parameters.partyId?if_exists, parameters.organizationPartyId?if_exists, parameters.prefDate?if_exists, delegator)?if_exists>
<#assign notPaidAmountStr = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(notPaidAmount?if_exists?double, "VND", locale, 2) />

<#assign nowStamp=Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp() />
<#assign day=Static["org.ofbiz.base.util.UtilDateTime"].getDayOfMonth(nowStamp, timeZone, locale) />
<#assign month=Static["org.ofbiz.base.util.UtilDateTime"].getMonth(nowStamp, timeZone, locale) + 1/>
<#assign year=Static["org.ofbiz.base.util.UtilDateTime"].getYear(nowStamp, timeZone, locale) />

<fo:block font-size="11" font-family="Times" border="solid 0.5mm black">
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
	    <fo:table-column />
	    <fo:table-column />
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold">${partyNameTo}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center" width="300px">
		        	<fo:block font-weight="bold">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="center">
		        	<fo:block font-style="bold">${parameters.organizationPartyId?if_exists}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center" width="300px">
		        	<fo:block font-weight="bold">Độc Lập - Tự Do - Hanh Phúc</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="center">
		        </fo:table-cell>
		        <fo:table-cell text-align="center" width="300px">
		        	<fo:block font-style="italic">Hà Nội, ${StringUtil.wrapString(uiLabelMap.BACCDay)} ${day} ${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${month + 1} ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${year} </fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
	    <fo:table-column />
	 	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="center">
		        	<#assign partyNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, parameters.organizationPartyId?if_exists, true, true)?if_exists>
		        	<fo:block font-weight="bold" font-size="120%">BIÊN BẢN ĐỐI CHIẾU CÔNG NỢ</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell>
			        <fo:list-block><#-- space-after="5mm"-->
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		-
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Căn cứ vào biên bản giao nhận hàng hóa.</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		-
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Căn cứ vào thỏa thuận giữa hai bên.</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
					</fo:list-block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
	    <fo:table-column />
	 	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="left" start-indent="0.5cm">
		        	<fo:block>Hôm nay, ngày ${day} tháng ${month} năm ${year}. Tại văn phòng Công ty ${partyNameTo}, chúng tôi gồm có:</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
	    <fo:table-column />
	 	<fo:table-body>
	      	<fo:table-row>
		        <fo:table-cell>
			        <fo:list-block><#-- space-after="5mm"-->
						<fo:list-item start-indent="0.5cm" font-weight="bold">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		1.
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Bên A (Bên mua): ${partyNameFrom}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Địa chỉ: ${partyAddressFrom}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Điện thoại: ${partyTelePhoneFrom}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Đại diện: ${repNameFrom}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Fax: ${faxNumberFrom}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Chức vụ: ${emplPosFrom?if_exists}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm" font-weight="bold">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		2.
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Bên B (Bên bán): ${partyNameTo?if_exists}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Địa chỉ: ${partyAddressTo?if_exists}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Điện thoại: ${partyTelePhoneTo?if_exists}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Đại diện: ${repNameTo?if_exists}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Fax: ${faxNumberTo?if_exists}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm">
						  	<fo:list-item-label end-indent="label-end()">
								  	<fo:block>
								  		-
								  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Chức vụ: ${emplPosTo?if_exists}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
					</fo:list-block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
	    <fo:table-column />
	 	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="left" start-indent="0.5cm">
		        	<fo:block>Cùng nhau đối chiếu khối lượng và giá trị cụ thể như sau:</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
	    <fo:table-column />
	 	<fo:table-body>
	      	<fo:table-row>
		        <fo:table-cell>
			        <fo:list-block><#-- space-after="5mm"-->
						<fo:list-item start-indent="0.5cm" font-weight="bold" margin-top="10px">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		1.
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Công nợ đầu kỳ: ${openingBalStr}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm" font-weight="bold"  margin-top="10px">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		2.
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Số phát sinh trong kỳ:</fo:block>
						       	<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px"  border="solid 0.5mm black">
								    <fo:table-column column-width="0.5in" border="solid 0.5mm black"/>
								    <fo:table-column border="solid 0.5mm black"/>
								    <fo:table-column column-width="1in" border="solid 0.5mm black"/>
								    <fo:table-column column-width="1in" border="solid 0.5mm black"/>
								    <fo:table-column column-width="1in" border="solid 0.5mm black"/>
								    <fo:table-column column-width="1in" border="solid 0.5mm black"/>
								    <fo:table-header border="solid 0.5mm black">
							          <fo:table-row border="solid 0.5mm black">
							              <fo:table-cell text-align="center">
							              	<fo:block font-weight="bold">STT</fo:block>
							              </fo:table-cell>
							              <fo:table-cell text-align="center">
							              	<fo:block font-weight="bold">Tên sản phẩm</fo:block>
							              </fo:table-cell>
							              <fo:table-cell text-align="center">
							              	<fo:block font-weight="bold">Đơn vị tính</fo:block>
							              </fo:table-cell>
							              <fo:table-cell text-align="center">
							              	<fo:block font-weight="bold">Số lượng</fo:block>
							              </fo:table-cell>
							              <fo:table-cell text-align="center">
							              	<fo:block font-weight="bold">Đơn giá</fo:block>
							              </fo:table-cell>
							              <fo:table-cell text-align="center">
							              	<fo:block font-weight="bold">Thành tiền</fo:block>
							              </fo:table-cell>
							          </fo:table-row>
							        </fo:table-header>
								 	<fo:table-body>
								 		<#list listLiabilityPrefs as item>
									 		<fo:table-row>
										        <fo:table-cell text-align="left" start-indent="0.5cm">
										        	<fo:block>${item.seqId}</fo:block>
										        </fo:table-cell>
										        <fo:table-cell text-align="left" start-indent="0.5cm">
										        	<fo:block>${item.productId}</fo:block>
										        </fo:table-cell>
										        <fo:table-cell text-align="left" start-indent="0.5cm">
										        	<fo:block>${item.uomId}</fo:block>
										        </fo:table-cell>
										        <fo:table-cell text-align="left" start-indent="0.5cm">
										        	<fo:block>${item.quantity}</fo:block>
										        </fo:table-cell>
										        <fo:table-cell text-align="left" start-indent="0.5cm">
										        	<#assign unitCost = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.unitCost?if_exists?double, "VND", locale, 2) />
										        	<fo:block>${unitCost}</fo:block>
										        </fo:table-cell>
										        <fo:table-cell text-align="left" start-indent="0.5cm">
											        <#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(item.amount?if_exists?double, "VND", locale, 2) />
										        	<fo:block>${amount}</fo:block>
										        </fo:table-cell>
									      	</fo:table-row>
								 		</#list>
								    </fo:table-body>
								 </fo:table>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm" font-weight="bold" margin-top="10px">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		3.
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Số tiền bên A đã thanh toán: ${paidAmountStr}</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm"  margin-top="10px">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		4.
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block><fo:inline font-weight="bold">Kết luận:</fo:inline> Tính đến hết ngày ${day}/${month}/${year} bên A phải
						       	thanh toán cho Công ty ${partyNameTo?if_exists} số tiền là: ${notPaidAmountStr?if_exists}
						       	</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
						<fo:list-item start-indent="0.5cm" margin-top="10px">
						  	<fo:list-item-label end-indent="label-end()">
							  	<fo:block>
							  		-
							  	</fo:block>
						  	</fo:list-item-label>
						  	<fo:list-item-body start-indent="body-start()">
						       	<fo:block>Biên bản này được thành lập thành 02 bản có giá trị như nhau. Mỗi bên giữ 01 bản làm cơ sở cho việc
						       	thanh toán sau này giữa hai bên. Trong vòng 03 ngày làm việc kể từ ngày nhận được biên bản đối chiếu công nợ này
						       	mà Công ty ${partyNameTo?if_exists} không nhận được phản hồi từ quý công ty thì công nợ trên coi như được chấp nhận.
						       	</fo:block>
						  	</fo:list-item-body>
						</fo:list-item>
					</fo:list-block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px" margin-bottom="50px">
	    <fo:table-column />
	    <fo:table-column />
	 	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="center" start-indent="0.5cm">
		        	<fo:block>ĐẠI DIỆN BÊN A</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center" start-indent="0.5cm">
		        	<fo:block>ĐẠI DIỆN BÊN B</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
</fo:block>
</#escape>