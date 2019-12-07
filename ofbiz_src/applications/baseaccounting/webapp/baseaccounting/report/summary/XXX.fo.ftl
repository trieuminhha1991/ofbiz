<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="Arial">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main-page" page-width="8.5in" page-height="11in" margin-top="0in" margin-bottom="0in" margin-left="0.3in" margin-right="0.3in">
			<fo:region-body margin-top="1.5in" margin-bottom="0in"/>
			<fo:region-before extent="0in"/>
			<fo:region-after extent="0in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>

	<#assign fontsize="9px"/>
	<#assign right="100px"/>
	<#assign line="4.5px"/>
	<#assign row="16px"/>
	
	<fo:page-sequence master-reference="main-page">
		<fo:flow flow-name="xsl-region-body">
		<#escape x as x?xml>
		<fo:block>
	
		<fo:table font-size="${fontsize}">
			<fo:table-column column-width="70%"/>
			<fo:table-column />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="15px" font-weight="bold" text-align="center" text-transform="uppercase">Bang Ke Đính Kèm Hoá Đơn</fo:block>
					</fo:table-cell>
	
					<fo:table-cell>
						<fo:block margin-top="4px" font-weight="bold">Số: 5512323</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:block font-size="${fontsize}" margin-left="120px">Ngày 20 Tháng 12 Năm 2016</fo:block>
		
		<fo:table font-size="${fontsize}" margin-top="20px">
			<fo:table-column column-width="60%"/>
			<fo:table-column />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>Họ và Tên Người mua hàng: Viet TB</fo:block>
					</fo:table-cell>

					<fo:table-cell>
						<fo:block>Mã Khách hàng: vyt231</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:block font-size="${fontsize}">Đơn vị: </fo:block>
		<fo:block font-size="${fontsize}">Địa chỉ: </fo:block>
		<fo:block font-size="${fontsize}">Mã số thuế: </fo:block>

		<fo:table font-size="${fontsize}">
			<fo:table-column column-width="40%"/>
			<fo:table-column />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block>Bảng kê đính kèm hoá đơn số: </fo:block>
					</fo:table-cell>
	
					<fo:table-cell>
						<fo:block>- Ngày 12 Tháng 09 Năm 2016</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:table font-size="${fontsize}" margin-top="10px">
			<fo:table-column column-width="25px"/>
			<fo:table-column column-width="80px"/>
			<fo:table-column column-width="120px"/>
			<fo:table-column column-width="45px"/>
			<fo:table-column column-width="45px"/>
			<fo:table-column column-width="55px"/>
			<fo:table-column column-width="70px"/>
			<fo:table-column column-width="50px"/>
			<fo:table-column />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">STT</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">SKU</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">TEN HANG HOA</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">DON VI TINH</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">SO LUONG</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">DON GIA</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">THANH TIEN CHUA THUE GTGT</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">THUE SUAT</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">THANH TIEN DA CO THUE GTGT</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">1</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">00300247</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">D.goi b.ha clear men 12x1</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="center">EA</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">100</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">12,000.00</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">11,000.00</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">10%</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">12,500.00</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell border="solid 1px black" number-columns-spanned="2" number-rows-spanned="6">
						<fo:block margin-top="12px">Loại không chịu thuế: </fo:block>
						<fo:block margin-top="1px">Loại thuế suất 05 %: </fo:block>
						<fo:block margin-top="1px">Loại thuế suất 10 %: </fo:block>
						<fo:block margin-top="2px"></fo:block>
						<fo:block margin-top="11px">Loại thuế suất: </fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" background-color="yellow">
						<fo:block>Thành Tiền chưa thuế GTGT</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" background-color="yellow" number-columns-spanned="4">
						<fo:block text-align="center">Thuế GTGT</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" background-color="yellow" number-columns-spanned="2">
						<fo:block text-align="center">Thành tiền có thuế VAT</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="4">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="2">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="4">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="2">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="4">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="2">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="4">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="2">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell border="solid 1px black">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="4">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
					
					<fo:table-cell border="solid 1px black" number-columns-spanned="2">
						<fo:block text-align="right">000</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
			</fo:table-body>
		</fo:table>
		
		<fo:block font-size="${fontsize}" margin-top="10px">Số tiền bằng chữ: </fo:block>
		
		<fo:table font-size="${fontsize}">
			<fo:table-column column-width="50%"/>
			<fo:table-column />
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell>
						<fo:block font-size="${fontsize}" font-weight="bold" text-align="center" text-transform="uppercase">Người mua hàng</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block font-size="${fontsize}" font-weight="bold" text-align="center" text-transform="uppercase">Người bán hàng</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		
		<fo:block font-size="${fontsize}" margin-top="80px" text-align="right">Số bảng kê: .................../Trang.......</fo:block>
		
		</fo:block>
		</#escape>
		</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>