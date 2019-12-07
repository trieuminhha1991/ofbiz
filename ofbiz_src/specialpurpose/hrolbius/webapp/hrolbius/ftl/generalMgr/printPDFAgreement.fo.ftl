<#escape x as x?xml>
<fo:block font-family="Arial" font-size="11">
	<fo:table table-layout="fixed" font-weight="bold" width="100%" border= "1px solid #C0C0C0" >
		<fo:table-column column-width="50%" border= "1px solid #C0C0C0"/>
		<fo:table-column column-width="50%" border= "1px solid #C0C0C0"/>
		<fo:table-body >
			<fo:table-row border= "1px solid #C0C0C0">
				<fo:table-cell>
					<fo:block text-align="center">CÔNG TY CỔ PHẦN ĐẦU TƯ</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="center">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row border= "1px solid #C0C0C0">
				<fo:table-cell>
					<fo:block text-align="center">PHÁT TRIỂN THƯƠNG MẠI DELYS</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="center">Độc lập – Tự do – Hạnh phúc</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row border= "1px solid #C0C0C0">
				<fo:table-cell>
					<fo:block text-align="center">*****************</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="center">*****************</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row border= "1px solid #C0C0C0">
				<fo:table-cell>
					<fo:block text-align="center">Số: 11/HĐLĐ/DELYS-2015</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="center"> </fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block text-align="center" margin-top="25px" font-weight="bold" font-size="16px">HỢP ĐỒNG LAO ĐỘNG</fo:block>
	<fo:block text-align="center" margin-top="5px" font-weight="bold" font-style="italic" >(Ban hành kèm theo TT số 21/2003/TT-BLDDTBXH Ngày 22/9/2003 của Bộ Lao động – Thương binh và Xã hội)</fo:block>
	<fo:table table-layout="fixed" width="100%" margin-top="25px">
		<fo:table-column column-width="30%"/>
		<fo:table-column column-width="70%"/>
		<fo:table-body >
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Chúng tôi, một bên là ông(bà)</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block >
						<fo:inline text-align="left" font-weight="bold" font-style="italic" padding-right="50px">: ${partyNameFrom?if_exists}</fo:inline>
						<fo:inline text-align="right">Quốc tịch: ${nationality?if_exists}</fo:inline>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Chức vụ</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left" font-weight="bold" font-style="italic">: ${roleTypeNameFrom?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Đại diện cho</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left" font-style="italic">: ${partyNameRep?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Địa chỉ</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left" font-style="italic">:${addressRep?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Điện thoại</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left" font-style="italic">:  ${telephoneRep?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" width="100%" margin-top="25px">
		<fo:table-column column-width="30%"/>
		<fo:table-column column-width="70%"/>
		<fo:table-body >
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Và một bên là ông(bà)</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block >
						<fo:inline text-align="left" font-weight="bold" font-style="italic" padding-right="50px">: ${partyNameTo?if_exists}</fo:inline>
						<fo:inline text-align="right">Quốc tịch: ${partyNationalityTo?if_exists}</fo:inline>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Sinh ngày</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left" font-weight="bold" font-style="italic">: ${birthDate?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left">Địa chỉ thường trú</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left" font-style="italic">: ${partyAddressTo?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="left"><fo:inline>Số CMND: ${idNumber?if_exists}</fo:inline></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left">
						<fo:inline>Ngày cấp: ${idIssueDate}</fo:inline>
						<fo:inline padding-left="50px">Nơi cấp: ${idIssuePlace?if_exists}</fo:inline>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block margin-top="5px">Thỏa thuận ký kết hợp đồng lao động và cam kết đúng những điều khoản sau đây:</fo:block>
	<fo:block font-weight="bold" margin-top="5px">
	 	<fo:inline text-decoration="underline">Điều 1:</fo:inline> Thời hạn và công việc hợp đồng:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Hợp đồng lao động: ${agreementType?if_exists}</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Từ ngày: ${fromDate?if_exists} / Đến ngày: ${thruDate?if_exists}</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	           	  <fo:block>Địa điểm làm việc: Theo sự phân công của Công ty</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Chức danh chuyên môn: ${emplPositionType?if_exists} / Đơn vị: ${partyWork?if_exists}</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Công việc phải làm: Theo mô tả công việc cho vị trí được phân công </fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	     </fo:list-block>
	 </fo:block>
	 
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 2:</fo:inline> Chế độ làm việc:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Thời giờ làm việc: Theo quy định của Công ty.</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Được cấp phát những dụng cụ làm việc gồm: Trang bị bảo hộ lao động, dụng cụ lao động  theo yêu cầu của công việc được giao</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	     </fo:list-block>
	 </fo:block>
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 3:</fo:inline> Nghĩa vụ và quyền hạn người lao động:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block font-weight="bold" font-style="italic">1.</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
           	 	  <fo:block font-weight="bold" font-style="italic">Quyền lợi:</fo:block>
	              <fo:block>- Phương tiện làm việc: Tự túc</fo:block>
				  <fo:block>- Mức lương chính hoặc tiền công: ${salary?if_exists}</fo:block>
				  <fo:block>- Hình thức trả lương: Tiền mặt hoặc chuyển khoản</fo:block>
				  <fo:block>- Phụ cấp gồm: Theo quy định của Công ty hàng năm</fo:block>
				  <fo:block>- Được trả lương 01 lần: Vào đầu tháng theo quy định của Công ty</fo:block>
				  <fo:block>- Tiền thưởng: Theo qui định của Công ty</fo:block>
				  <fo:block>- Chế độ nâng lương: Theo qui định của Công ty</fo:block>
				  <fo:block>- Chế độ nghỉ ngơi ( nghỉ hàng tuần, phép năm, lễ tết..): Theo quy định của luật lao động và theo quy định của Công ty</fo:block>
				  <fo:block>- Bảo hiểm xã hội và bảo hiểm y tế: Theo quy định của luật lao động và quy chế của Công ty</fo:block>
				  <fo:block>- Chế độ đào tạo: Theo qui định của Công ty</fo:block>
				  <fo:block>- Những thỏa thuận khác: Theo quy định của Công ty</fo:block>
			  </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block font-weight="bold" font-style="italic">2. </fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	           		<fo:block font-weight="bold" font-style="italic">Nghĩa vụ</fo:block>
	              	<fo:block>- Thực hiện công việc theo mô tả công việc cho vị trí công tác</fo:block>
	              	<fo:block>- Hoàn thành những cam kết đã ký trong hợp đồng lao động (HĐLĐ).</fo:block>
	              	<fo:block>- Chịu trách hiệm đóng các khoản thuế liên quan : Thuế TNCN,...</fo:block>
	              	<fo:block>- Chấp hành lệnh điều động và phân công công việc của Công ty.</fo:block>
	              	<fo:block>- Chấp hành đầy đủ quy định về giờ công, ngày công, trang phục và các nội dung khác trong nội quy Công ty.</fo:block>
	              	<fo:block>- Chấp hành tốt các quy chế, quy trình, hướng dẫn thực hiện công việc của Công ty</fo:block>
	              	<fo:block>- Chấp hành các bồi thường vi phạm và vật chất: Theo quy định của Công ty và Pháp luật </fo:block>
	              	<fo:block>- Hoàn tất toàn bộ các khoản công nợ liên quan ngay khi và sau khi chấm dứt HĐLĐ.</fo:block>
	              	<fo:block>- Chấm dứt HĐLĐ: Theo Bộ luật lao động được ban hành và quy chế xử lý kỷ luật, hướng dẫn chấm dứt HĐLĐ của Công ty.</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	     </fo:list-block>
	 </fo:block>
	 
	<fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 4:</fo:inline> Nghĩa vụ và quyền hạn của người sử dụng lao động:
	</fo:block>
 	<fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block font-weight="bold" font-style="italic">1.</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
           	 	  <fo:block font-weight="bold" font-style="italic">Nghĩa vụ:</fo:block>
	              <fo:block>- Bảo đảm việc làm và thực hiện đầy đủ những điều đã cam kết trong hợp đồng lao động.</fo:block>
				  <fo:block>- Thanh toán đầy đủ, đúng thời hạn các chế độ và quyền lợi cho người lao động theo hợp đồng lao động.</fo:block>
				  <fo:block>- Trong trường hợp Công ty chấm dứt hợp đồng người lao động trước thời hạn: Báo cho người lao động trước ba mươi (30) ngày. Nhưng trong trường hợp vi phạm qui định của công ty ở mức độ nặng, công ty có quyền ra quyết định kỷ luật buộc thôi việc ngay sau khi ra quyết định kỷ luật. </fo:block>
			  </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block font-weight="bold" font-style="italic">2. </fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	           		<fo:block font-weight="bold" font-style="italic">Nghĩa vụ</fo:block>
	              	<fo:block>- Điều hành người lao động hoàn thành công việc theo hợp đồng (bố trí, điều chuyển, tạm ngừng việc…).</fo:block>
	              	<fo:block>- Tạm hoãn, chấm dứt hợp đồng lao động, kỷ luật người lao động theo quy định của pháp luật, thỏa ước lao động tập thể (nếu có) và nội quy lao động của Công ty.</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	     </fo:list-block>
	 </fo:block>
	 
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 5:</fo:inline> Điều khoản thi hành:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Những vấn đề về lao động không ghi trong hợp đồng này thì áp dụng quy định của thỏa ước lao động tập thể, trường hợp chưa có thỏa ước lao động tập thể thì áp dụng quy định của pháp luật lao động.</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Tất cả các quyết định, quy định  của Công ty liên quan đến các điều khoản của hợp đồng lao động này được xem như là phụ lục không thể tách rời của hợp đồng lao động này và đương nhiên các điều khoản tương tự trong hợp đồng lao động sẽ hết hiệu lực.</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	        <fo:list-item>
	           <fo:list-item-label end-indent="label-end()">
	              <fo:block>-</fo:block>
	           </fo:list-item-label>
	           <fo:list-item-body start-indent="body-start()">
	              <fo:block>Hợp đồng lao động được lập thành hai (02) bản có giá trị như nhau, mỗi bên giữ một (01) bản và có hiệu lực kể từ ngày ${fromDate?if_exists}. Nếu khi hai bên ký kết phụ lục hợp đồng lao động thì nội dung phụ lục hợp đồng lao động cũng có giá trị như các nội dung của bản hợp đồng lao động này.</fo:block>
	           </fo:list-item-body>
	        </fo:list-item>
	     </fo:list-block>
	 </fo:block>
	 
	 <fo:table table-layout="fixed" width="100%" margin-top="25px">
		<fo:table-column column-width="50%"/>
		<fo:table-column column-width="50%"/>
		<fo:table-body >
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="center">NGƯỜI LAO ĐỘNG</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="center">NGƯỜI SỬ DỤNG LAO ĐỘNG</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
</fo:block>
</#escape>