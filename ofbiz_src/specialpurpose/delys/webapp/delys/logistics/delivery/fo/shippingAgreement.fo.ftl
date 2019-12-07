<#escape x as x?xml>
<fo:block font-size="12" font-family="Times New Roman">
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</fo:block>
		        	<fo:block font-weight="bold">Độc Lập - Tự Do - Hạnh Phúc</fo:block>
		        	<fo:block font-weight="bold">--------o0o---------</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="right">
		        	<fo:block font-style="italic" margin-top="20px">Hà Nội, Ngày...Tháng...Năm...</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" margin-top="20px" font-size="15px">HỢP ĐỒNG VẬN CHUYỂN HÀNG HÓA</fo:block>
		        	<fo:block font-style="italic" >Số: 005/HĐVC/NETLINK-DELYS/2014</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
	      		<fo:table-cell text-align="left" padding="10 40 10 40">
						<fo:list-block font-style="italic" provisional-distance-between-starts="24pt" space-before="1in" space-after=".1in">
				            <fo:list-item margin-top="5px">
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  	<fo:block>Căn cứ Bộ luật dân sự số 33/2005/QH11 của Quốc hội nước Cộng hoà xã hội chủ nghĩa
											Việt nam thông qua ngày 14 tháng 6 năm 2005;
									</fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				            <fo:list-item margin-top="5px">
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                 	<fo:block>Căn cứ Luật Thương mại số 36/2005/QH11 của Quốc hội nước Cộng hoà xã hội chủ
											nghĩa Việt nam thông qua ngày 14 tháng 6 năm 2005;
									</fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				            <fo:list-item margin-top="5px">
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  	<fo:block>Căn cứ yêu cầu của Công ty Cổ phần Đầu tư và phá triển Thương mại Delys và khả năng
											cung cấp dịch vụ của Công ty Cổ phần Đầu Tư Kinh Doanh Công Nghệ NETLINK;
									</fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				         </fo:list-block>
				         <fo:block>
				         	Hôm nay, ngày tháng năm 2014, tại trụ sở Công ty Cổ phần Đầu tư và phá triển
							Thương mại Delys , chúng tôi gồm: 
				         </fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="left" width="50px">
		        	<fo:block font-weight="bold">Bên A</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="800">
		        	<fo:block text-transform="uppercase">${partyFromName.groupName?if_exists} ${partyFromName.firstName?if_exists} ${partyFromName.middleName?if_exists} ${partyFromName.lastName?if_exists}</fo:block>
		        </fo:table-cell>
		   </fo:table-row>
	    </fo:table-body>
	 </fo:table>
	
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Người đại diện:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">Ông ${repFromName.firstName?if_exists} ${repFromName.middleName?if_exists} ${repFromName.lastName?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Chức vụ:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${emplPosTypeFromDes?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Địa chỉ:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${postalAddressFrom?if_exists.address1?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Điện thoại:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${telecomNumberFrom?if_exists.contactNumber?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
			<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Fax:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${faxNumberFrom?if_exists.faxNumber?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Mã số thuế:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${taxFrom?if_exists.partyTaxId?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table> 
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="left" width="50px">
		        	<fo:block font-weight="bold">Bên A</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left" width="800">
				<fo:block>${partyToName.groupName?if_exists} ${partyToName.firstName?if_exists} ${partyToName.middleName?if_exists} ${partyToName.lastName?if_exists}</fo:block>
		        </fo:table-cell>
		   </fo:table-row>
	    </fo:table-body>
	 </fo:table>
	
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Người đại diện:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${repToName.firstName?if_exists} ${repToName.middleName?if_exists} ${repToName.lastName?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Chức vụ:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${emplPosTypeToDes?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Địa chỉ:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${postalAddressTo?if_exists.address1?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Điện thoại:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${telecomNumberTo?if_exists.contactNumber?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
			<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Fax:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${faxNumberTo?if_exists.faxNumber?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Mã số thuế:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
				<fo:block font-weight="bold">${taxTo?if_exists.partyTaxId?if_exists}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Tài khoản số:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
		        	<fo:block font-weight="bold">0102653512</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
		        <fo:table-cell text-align="left" margin-left="50px" width="130px">
		        	<fo:block>Mở tại:</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="left">
		        	<fo:block font-weight="bold">Ngân hàng TMCP Đầu Tư &amp; Phát Triển Việt Nam - CN Thăng Long</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 
	 <fo:block>
	 	Hai bên thoả thuận và đi đến thống nhất ký kết hợp đồng vận chuyển hàng hóa với các điều
		khoản sau:
	 </fo:block>
	 
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 1:</fo:inline> Hàng hóa vận chuyển, địa điểm vận chuyển, lộ trình vận chuyển và phương thức vận
		chuyển:
	 </fo:block>
	 <fo:block margin-left="25px">
	 	<fo:block margin-top="5px">Bên B đồng ý thuê bên A vận chuyển hàng hóa với chi tiết như sau:</fo:block>
		<fo:block margin-top="5px"><fo:inline text-decoration="underline">Phương thức vận chuyển</fo:inline></fo:block>
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block> Hàng hóa vận chuyển bằng Đường Bộ.</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Vận chuyển bằng xe tải lạnh và đảm bảo nhiệt độ lạnh từ + 3oC tới +8o trong suốt quá trình vận chuyển.</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block> Bên A chịu trách nhiệm về kỹ thuật cho phương tiện vận tải nói trên để đảm bảo hàng hóa được vận chuyển an toàn, thông suốt</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>  Phương tiện vận chuyển phải đầy đủ giấy tờ theo luật Giao Thông Đường Bộ hiện hành.</fo:block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>
	 </fo:block>
	 
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 2:</fo:inline> Phương thức giao nhận và thời gian vận chuyển:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Thời gian vận chuyển: Theo lệnh giao hàng của Bên B</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Giao hàng theo hình thức: Hàng hóa được giao nhận theo số lượng kiểm đếm thực tế tại nơi nhận hàng và
					nơi giao hàng</fo:block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>
	 </fo:block>
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 3:</fo:inline> Giá cước vận chuyển và phương thức thanh toán:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>1.</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Giá cước vận chuyển: </fo:block>
                  <fo:block margin-top="5px">- Theo từng chặng đường và theo bảng báo giá đính kèm. Chưa bao gồm thuế GTGT (VAT)</fo:block>
				  <fo:block margin-top="5px">- Bất kỳ sự thay đổi nào về đơn giá vận chuyển phải được hai bên xác nhận bằng văn bản trước 05 ngày.</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>2.</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  	<fo:block>Hình thức thanh toán:</fo:block>
				         <fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
				            <fo:list-item>
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  <fo:block>Bên A sẽ lập bảng kê số lượng vận chuyển vào cuối tháng và gửi cho bên B từ ngày mùng 1
											đến mùng 5 hàng tháng và giao kèm cho bên B giấy tờ giao nhận hàng có chữ ký xác nhận
											của bộ phận nội địa Bên B và khách hàng đã nhận và giao nhận đủ hàng. <fo:inline text-decoration="underline">Trường hợp những
											chuyến không đủ chứng từ thanh toán như nêu trên thì bên A phải thống kê các chuyến bị thiếu
											đó và gửi cho bên B kèm theo lý do để xác nhận. Các chuyến bị thiếu chứng từ sẽ được thanh
											toán vào tháng kế tiếp sau khi bên B đã bổ sung đầy đủ chứng từ.</fo:inline></fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				            <fo:list-item>
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  <fo:block>Bên B sẽ thanh toán cho Bên A sau khi nhận được bảng kê và hóa đơn chậm nhất là ngày mùng
											10 hàng tháng, bằng chuyển khoản hoặc bằng tiền mặt.
								  </fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				         </fo:list-block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>
	 </fo:block>
	 
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 4:</fo:inline> Các điều kiện khác:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".5in">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Bên A phải đảm bảo xe sẵn sàng hoạt động theo lệnh giao hàng của Bên B.</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Bên B phải báo lịch giao hàng cho Bên A trước ít nhất là 8 đến 10 tiếng đồng hồ.</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Trong trường hợp xe tải của Bên A bị hỏng đột xuất trên đường vận chuyển, Bên A phải thông
							báo ngay cho Bên B biết và có phương án sửa chữa kịp thời để không làm ảnh hưởng đến công
							việc của Bên B.
				  </fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Lái xe của Bên A phải có điện thoại di động (luôn trong tình trạng sẵn sàng nhận cuộc gọi đến)
							và là người nhận lệnh điều xe của cả Bên A và Bên B.
				  </fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Lái xe tự giao nhận hàng với khách hàng.
				  </fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Lái xe tự giao nhận hàng với khách hàng.
				  </fo:block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>
	 </fo:block>

	<fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 5:</fo:inline> Trách nhiệm của mỗi bên:
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".5in">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>1.</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Trách nhiệm của bên B:</fo:block>
                  <fo:block>
				      <fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
				            <fo:list-item>
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  <fo:block>Chuẩn bị hàng hoá đầy đủ như đã thoả thuận và các giấy tờ hợp lệ, hợp pháp cần thiết cho việc
											vận chuyển trong nước cũng như chịu trách nhiệm toàn bộ về tính pháp lý và hợp lệ của hàng
											hoá; không vận chuyển hàng hoá không có giấy tờ xuất xứ và vi phạm pháp luật.</fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				            <fo:list-item>
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  	<fo:block>
										Cung cấp cho Bên A đầy đủ những thông số kỹ thuật, tính chất của hàng hoá, điều kiện xếp dỡ
										cũng như bảo quản hàng đặc biệt (nếu có).
									</fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				            <fo:list-item>
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  <fo:block>
				                  	Cung cấp cho bên A bộ chứng từ giao nhận và đó được xem là bản chuẩn cho hai Bên trong
									quá trình nhận hàng và giao hàng. Xác nhận cùng Bên A lên biên bản giao nhận hàng hóa kiêm
									vận chuyển khi tiến hành giao hàng cũng như nhận hàng tại điểm đến.
				                  </fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				            <fo:list-item>
				               <fo:list-item-label end-indent="label-end()">
				                  <fo:block>-</fo:block>
				               </fo:list-item-label>
				               <fo:list-item-body start-indent="body-start()">
				                  <fo:block>
				                  	Thanh toán cho Bên A đúng thời gian
				                  </fo:block>
				               </fo:list-item-body>
				            </fo:list-item>
				         </fo:list-block>
                  </fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>2.</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  <fo:block>Trách nhiệm của Bên A.</fo:block>
                  <fo:block>
			         <fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
			            <fo:list-item>
			               <fo:list-item-label end-indent="label-end()">
			                  <fo:block>-</fo:block>
			               </fo:list-item-label>
			               <fo:list-item-body start-indent="body-start()">
			                  	<fo:block>
			                  		Trong quá trình vận chuyển cho Bên B, Bên A phải bảo quản hàng theo đúng quy định, đảm
									bảo về nhiệt độ bảo quản (như nêu ở Điều 1), vệ sinh an toàn hàng hoá, không để sản phẩm bị
									nắng chiếu trực tiếp, không bị mưa tạt ẩm ướt làm ảnh hưởng đến chất lượng hàng hoá.
							   </fo:block>
			               </fo:list-item-body>
			            </fo:list-item>
			            <fo:list-item>
			               <fo:list-item-label end-indent="label-end()">
			                  <fo:block>-</fo:block>
			               </fo:list-item-label>
			               <fo:list-item-body start-indent="body-start()">
			                  <fo:block>Giao hàng đủ số lượng, chất lượng, chủn loại đã nhận từ Bên B.</fo:block>
			               </fo:list-item-body>
			            </fo:list-item>
			            <fo:list-item>
			               <fo:list-item-label end-indent="label-end()">
			                  <fo:block>-</fo:block>
			               </fo:list-item-label>
			               <fo:list-item-body start-indent="body-start()">
			                  <fo:block>
			                  		Nếu trong quá trình vận chuyển hàng hoá bị mất mát toàn bộ thì bồi thường toàn bộ. Hàng hoá
									bị hư hỏng thiếu hụt, mất mát một phần thì bồi thường phần hư hỏng mất mát theo giá mà bên
									B đang cung cấp cho nhà phân phối tại thời điểm xảy ra mất, hỏng hàng.
			                  </fo:block>
			               </fo:list-item-body>
			            </fo:list-item>
			            <fo:list-item>
			               <fo:list-item-label end-indent="label-end()">
			                  <fo:block>-</fo:block>
			               </fo:list-item-label>
			               <fo:list-item-body start-indent="body-start()">
			                  <fo:block>
			                  		Trong trường hợp bất khả kháng như phương tiện bị sự cố do thiên tai gây chậm trễ hay hư
									hỏng, gây mất mát hàng hoá thì hai bên cùng bàn bạc để khắc phục hậu quả thiên tai.
			                  </fo:block>
			               </fo:list-item-body>
			            </fo:list-item>
			         </fo:list-block>
                  </fo:block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>
	 </fo:block>
	 
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 6:</fo:inline> Thời hạn hợp đồng
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  	<fo:block>Thời hạn hợp đồng: Hợp đồng có hiệu lực kể từ ngày ký và có giá trị trong thời hạn 01 (một)
							năm. Khi đến thời điểm hết hiệu lực hai bên không có tranh chấp và khiếu nại thì hợp động này
							sẽ được tự động gia hạn cho các năm tiếp theo. 
					</fo:block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>
	 </fo:block>
	 
	 <fo:block font-weight="bold">
	 	<fo:inline text-decoration="underline">Điều 7:</fo:inline> Điều khoản chung
	 </fo:block>
	 <fo:block margin-left="25px">
		<fo:list-block provisional-distance-between-starts="24pt" space-before=".1in" space-after=".1in">
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  	<fo:block>Hai bên cam kết thực hiện theo các điều khoản đã nêu trong hợp đồng này. 
					</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  	<fo:block>Hai bên cam kết không được tự ý huỷ bỏ hợp đồng trong thời gian hợp đồng còn hiệu lực, nếu
							bên nào tự ý huỷ bỏ hợp đồng thì sẽ phải đền bù mọi tổn thất do mình gây ra cho bên kia.
					</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  	<fo:block>Trong quá trình thực hiện hợp đồng nếu xảy ra bất đồng, hai bên sẽ bàn bạc giải quyết theo tình
								thần hợp tác bình đẳng, trường hợp không đi đến thoả thuận sẽ trình lên Toà án Kinh tế Hà Nội
								giải quyết. Quyết định của Toà án là quyết định cuối cùng, án phí do bên thua chịu.  
					</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  	<fo:block>Mọi thay đổi bổ sung phải được thể hiện bằng văn bản và được sự đồng ý từ hai bên.
					</fo:block>
               </fo:list-item-body>
            </fo:list-item>
            <fo:list-item>
               <fo:list-item-label end-indent="label-end()">
                  <fo:block>-</fo:block>
               </fo:list-item-label>
               <fo:list-item-body start-indent="body-start()">
                  	<fo:block>Hợp đồng này lập thành 04 (bốn) bản, mỗi bên giữ 02 (hai) bản có nội dung pháp lý như nhau
							và có hiệu lực kể từ ngày kí.
					</fo:block>
               </fo:list-item-body>
            </fo:list-item>
         </fo:list-block>
	 </fo:block>
	 
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row >
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold">BÊN THUÊ VẬN CHUYỂN</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" margin-left="30px">BÊN VẬN CHUYỂN</fo:block>
		        </fo:table-cell>
		   </fo:table-row>
	    </fo:table-body>
	 </fo:table>
</fo:block>
</#escape>