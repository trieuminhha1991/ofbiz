<#escape x as x?xml>
<fo:block font-size="11" font-family="Times New Roman">
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" font-size="150%">PHIẾU XUẤT KHO</fo:block>
		        	<fo:block font-style="italic">Giao ngày.....Tháng.....Năm.....</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-left="40px">
	 	<fo:table-column/>
		<fo:table-column/>
	    <fo:table-body>
		    <fo:table-row >
			   <fo:table-cell width="150px" text-align="left">
			       <fo:block font-style="italic" font-weight="bold">Đơn vị:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">Diễn giải:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">Xuất tại kho:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">Kho chuyển đến:</fo:block>
			   </fo:table-cell>
			   <fo:table-cell >
			       <fo:block>${partyTo?if_exists.firstName?if_exists} ${partyTo?if_exists.middleName?if_exists} ${partyTo?if_exists.lastName?if_exists} ${partyTo?if_exists.groupName?if_exists} <fo:leader /> </fo:block>
			       <fo:block>${deliveryType?if_exists.get('description', locale)?if_exists} <fo:leader /> </fo:block>
			       <fo:block>${originFacility?if_exists.facilityName?if_exists} <fo:leader /> </fo:block>
			       <fo:block>${destFacility?if_exists.facilityName?if_exists} <fo:leader /> </fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.5mm black"> 
	 	<fo:table-column border="solid 0.5mm black" />
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-column border="solid 0.5mm black"/>
		<fo:table-header border="solid 0.5mm black">
          <fo:table-row border="solid 0.5mm black">
              <fo:table-cell text-align="center" number-rows-spanned="2" width="20px">
              	<fo:block font-weight="bold" margin-bottom="10px" >STT</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" number-rows-spanned="2">
              	<fo:block font-weight="bold">Tên, nhãn hiệu, quy cách, phẩm chất VLSPHH </fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" number-rows-spanned="2">
              	<fo:block font-weight="bold">Đơn vị</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" number-columns-spanned="2" >
              	<fo:block font-weight="bold" vertical-align="middle">Số lượng</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" number-rows-spanned="2">
              	<fo:block font-weight="bold">Đơn giá</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center" number-rows-spanned="2">
              	<fo:block font-weight="bold">Thành tiền</fo:block>
              </fo:table-cell>
          </fo:table-row>
          <fo:table-row >
              <fo:table-cell text-align="center" >
              	<fo:block>Loại/Mã</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block>Thực xuất</fo:block>
              </fo:table-cell>
          </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
	   		<#list listItem as item >
			    <fo:table-row border="solid 0.5mm black">
				    <fo:table-cell >
				       <fo:block text-align="center">${item_index + 1}</fo:block>
				    </fo:table-cell>
				  	<fo:table-cell  text-align="center">
				       <fo:block>${item.productName?if_exists}</fo:block>
				   	</fo:table-cell>
					<fo:table-cell  text-align="center">
				       <fo:block>${item.unit?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block>${item.code?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block>${item.actualExportedQuantity?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block>${item.unitPrice?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block>${item.total?if_exists}</fo:block>
				   </fo:table-cell>
			    </fo:table-row>
		    </#list>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt">
		 <fo:table-column/>
		 <fo:table-column/>
		 <fo:table-body>
			<fo:table-row >
				<fo:table-cell width="380px" text-align="left">
					<#if listConfig?has_content>
						<#list listConfig as item>
						    <fo:block>Quy cách:</fo:block>
						    <fo:block> - ${item.productName}: 1 (${item.uomFrom}) = ${item.quantityConvert} (${item.uomTo})</fo:block>
						</#list>
					</#if>
				</fo:table-cell>
				<fo:table-cell >
				    <fo:block font-style="italic" font-weight="bold">Cộng: ${total}</fo:block>
				    <fo:block><fo:leader /></fo:block>
				    <fo:block><fo:leader /></fo:block>
				    <fo:block font-style="italic" font-weight="bold">Lập, ngày ${day} tháng ${month} năm ${year}</fo:block>
				</fo:table-cell>
			 </fo:table-row>
		 </fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt">
		 <fo:table-column/>
		 <fo:table-column/>
		 <fo:table-column/>
		 <fo:table-column/>
		 <fo:table-column/>
		 <fo:table-body>
			<fo:table-row >
				<fo:table-cell>
				    <fo:block font-style="italic" font-weight="bold">Giám đốc</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block font-style="italic" font-weight="bold">Kế toán</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block font-style="italic" font-weight="bold">Thủ kho</fo:block>
				</fo:table-cell> 
				<fo:table-cell>    
				    <fo:block font-style="italic" font-weight="bold">Người giao</fo:block>
				</fo:table-cell> 
				<fo:table-cell>
				    <fo:block font-style="italic" font-weight="bold">Người nhận</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell >
				    <fo:block >(Ký, họ tên)</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block>(Ký, họ tên)</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block>(Ký, họ tên)</fo:block>
				</fo:table-cell>
				<fo:table-cell> 
				    <fo:block>(Ký, họ tên)</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				    <fo:block>(Ký, họ tên)</fo:block>
				</fo:table-cell>
			</fo:table-row>
		 </fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt">
		 <fo:table-column/>
		 <fo:table-body>
			<fo:table-row >
				<fo:table-cell>
				    <fo:block margin-top="70px">Họ tên lái xe:.............................................</fo:block>
				    <fo:block>Biển số xe:................................................</fo:block>
				    <fo:block>Điện thoại:................................................</fo:block>
				</fo:table-cell>
			 </fo:table-row>
		 </fo:table-body>
	</fo:table>
</fo:block>
</#escape>