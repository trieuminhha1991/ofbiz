<#escape x as x?xml>
<fo:block font-size="11" font-family="Arial">
	
	<fo:block font-weight="bold" text-align="center" font-size="12pt" margin-top="10px">${uiLabelMap.BSPDFVietNamNational}</fo:block>
	<fo:block font-weight="bold" text-align="center" font-size="12pt">${uiLabelMap.BSPDFVietNamTargetNational}</fo:block>
	<fo:block font-weight="bold" text-align="center" font-size="12pt" margin-bottom="10px">---------------------------</fo:block>
	<fo:block font-weight="bold" text-align="center" font-size="15pt" margin-bottom="10px">${uiLabelMap.BSPDFAgreementWithAgentDistributionExclusive}</fo:block>
	
	<fo:block font-size="12pt" margin-bottom="10px">${uiLabelMap.BSPDFContractAgentDistributor}:</fo:block>

	<fo:block font-weight="bold" font-size="13pt" margin-bottom="5px" text-decoration="underline">${uiLabelMap.BSPartyA}: ${uiLabelMap.BSAgent} ${(agreement.partyNameFrom)?if_exists}</fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSAddress}: <fo:inline font-size="11pt">${(agreement.partyFromInfo.address)?if_exists}</fo:inline></fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSTaxCode}: <fo:inline font-size="11pt">${(agreement.partyFromInfo.taxAuthInfos)?if_exists}</fo:inline></fo:block>
	<fo:table>
		<fo:table-column column-width="50%"/>
	    <fo:table-column column-width="50%"/>
		<fo:table-body>
		  	<fo:table-row>
			  	<fo:table-cell>
			  		<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSPhone}: <fo:inline font-size="11pt">${(agreement.partyFromInfo.contactNumber)?if_exists}</fo:inline></fo:block>
			  	</fo:table-cell>
			  	<fo:table-cell>
			  		<fo:block font-size="12pt" margin-bottom="5px">Fax: <fo:inline font-size="11pt">${(agreement.partyFromInfo.faxNumber)?if_exists}</fo:inline></fo:block>
			  	</fo:table-cell>
		  	</fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSPDFBankAccount}: <fo:inline font-size="11pt"></fo:inline></fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSRepresentedBy}: <fo:inline font-size="11pt">${(agreement.partyFromInfo.representative.partyFullName)?if_exists}</fo:inline></fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSPosition}: <fo:inline font-size="11pt">${(agreement.partyFromInfo.position)?if_exists}</fo:inline></fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSAndU}</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="5px" text-decoration="underline">${uiLabelMap.BSPartyB}: ${(agreement.partyNameTo)?if_exists}</fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSAddress}: <fo:inline font-size="11pt">${(agreement.partyToInfo.address1)?if_exists}</fo:inline></fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSTaxCode}: <fo:inline font-size="11pt">${(agreement.partyToInfo.taxAuthInfos)?if_exists}</fo:inline></fo:block>
	<fo:table>
		<fo:table-column column-width="50%"/>
	    <fo:table-column column-width="50%"/>
		<fo:table-body>
		  	<fo:table-row>
			  	<fo:table-cell>
			  		<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSPhone}: <fo:inline font-size="11pt">${(agreement.partyToInfo.contactNumber)?if_exists}</fo:inline></fo:block>
			  	</fo:table-cell>
			  	<fo:table-cell>
			  		<fo:block font-size="12pt" margin-bottom="5px">Fax: <fo:inline font-size="11pt">${(agreement.partyToInfo.faxNumber)?if_exists}</fo:inline></fo:block>
			  	</fo:table-cell>
		  	</fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSPDFBankAccount}: <fo:inline font-size="11pt"></fo:inline></fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSRepresentedBy}: <fo:inline font-size="11pt">${(agreement.partyToInfo.representativeName)?if_exists}</fo:inline></fo:block>
	<fo:block font-size="12pt" margin-bottom="5px">${uiLabelMap.BSPosition}: <fo:inline font-size="11pt">${(agreement.partyToInfo.position)?if_exists}</fo:inline></fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="5px">${uiLabelMap.BSPDFXR}</fo:block>
	
	<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">${uiLabelMap.BSPDFXRA}</fo:block>
	<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">${uiLabelMap.BSPDFXRB}</fo:block>
	<fo:block font-size="11pt" margin-left="20px" margin-bottom="10px">${uiLabelMap.BSPDFXRC}</fo:block>
	
	<fo:block font-size="11pt">${uiLabelMap.BSPDFCondsXR}:</fo:block>
	
	<fo:block page-break-before="always"></fo:block>
	<!--
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 1 <fo:inline text-decoration="none">- ĐỊNH NGHĨA VÀ GIẢI THÍCH</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">Trừ khi có quy định cụ thể khác trong Hợp đồng này hoặc ngữ cảnh bắt buộc phải giải thích theo một ý nghĩa khác, các thuật ngữ dưới đây sẽ được hiểu và diễn giải như sau:</fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Lãnh thổ độc quyền” <fo:inline font-weight="normal">có nghĩa là toàn bộ diện tích lãnh thổ tỉnh/thành phố ____ được phân định theo chỉ giới hành chính cấp tỉnh, thành phố trực thuộc trung ương do cơ quan nhà nước có thẩm quyền công bố vào từng thời kỳ;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Hạn mức tín dụng” <fo:inline font-weight="normal">là mức dư nợ tối đa mà Bên B được phép duy trì đối với Bên A trong một khoảng thời gian nhất định do Bên A ấn định theo từng thời điểm cụ thể không phụ thuộc vào số tiền đặt cọc và/hoặc tài sản ký quỹ của Bên B cho Bên A;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Sản phẩm cạnh tranh trực tiếp” <fo:inline font-weight="normal">là các hàng hóa cùng chức năng, chủng loại và có khả năng ảnh hưởng tiêu cực đến sức tiêu thụ của sản phẩm được phân phối theo quy định tại Hợp đồng này;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Kiểm soát” <fo:inline font-weight="normal">tác là khả năng chỉ đạo việc quản lý hay các chính sách một cách trực tiếp hoặc gián tiếp, dù thông qua việc sở hữu trên 50% cổ phần/phần vốn góp hoặc Hợp đồng đại lý phân phối độc quyền;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Giá bán lẻ” <fo:inline font-weight="normal">có nghĩa là giá bán lẻ Sản phẩm cho Người tiêu dùng cuối cùng trên thị trường;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Cơ sở kinh doanh” <fo:inline font-weight="normal">là nơi Bên B sẽ thực hiện hoạt động phân phối bán buôn, bán lẻ Sản phẩm cho khách hàng;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Nhà phân phối” <fo:inline font-weight="normal">nghĩa là các đơn vị, tổ chức và/hoặc cá nhân thực hiện kinh doanh bán buôn Sản phẩm trên thị trường Việt Nam hoặc nước ngoài;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Chuỗi phân phối” <fo:inline font-weight="normal">là Nhà phân phối do Bên A chỉ định sở hữu hệ thống siêu thị hoặc cửa hàng bán lẻ hoặc tương tự trên địa bàn của từ hai tỉnh, thành phố trực thuộc trung ương trở lên.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Nhà sản xuất” <fo:inline font-weight="normal">là đơn vị có chức năng sản xuất Sản phẩm được phân phối theo quy định tại Hợp đồng này;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">“Pháp luật Việt Nam” <fo:inline font-weight="normal">có nghĩa là bất kỳ và tất cả các luật, nghị định, quyết định, thông tư, quy chế và văn bản pháp luật có liên quan khác do bất kỳ Cơ quan Nhà nước nào của nước Cộng hòa Xã hội Chủ nghĩa Việt Nam ban hành, được sửa đổi và bổ sung vào từng thời điểm;</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" font-weight="bold">“Ngày” <fo:inline font-weight="normal">có nghĩa là ngày tính theo dương lịch, bao gồm ngày làm việc và ngày nghỉ. Trừ khi có quy định rõ ràng và cụ thể trong Hợp đồng này, mọi số chỉ dẫn đến thời hạn trong Hợp đồng này đều tính theo ngày dương lịch và bất kỳ thời hạn nào kết thúc hoặc hết hạn vào ngày Thứ bảy hoặc Chủ nhật hoặc ngày nghỉ lễ theo quy định của pháp luật Việt Nam, ngày kết thúc thời hạn sẽ được kéo dài tới ngày làm việc tiếp theo;</fo:inline></fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 2 <fo:inline text-decoration="none">- ĐẠI LÝ ĐỘC QUYỀN</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">2.1.	Bên A sau đây chỉ định và Bên B đồng ý nhận làm Đại lý độc quyền phát triển, phân phối Sản phầm quy định tại Phụ lục 1 của Hợp đồng này trong vùng lãnh thổ độc quyền.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">2.2. 	Nhằm tránh hiểu nhầm, các Bên xác nhận và đồng ý rằng, quyền phân phối độc quyền của Bên B trên trong vùng lãnh thổ độc quyền sẽ không hạn chế quyền phân phối sản phẩm tại các cơ sở kinh doanh của các chuỗi phân phối do Bên A hợp tác, phát triển trên vùng lãnh thổ độc quyền.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">2.3. 	Bên A bảo lưu quyền điều chỉnh, sửa đổi danh mục Sản phẩm quy định tại Phụ lục 1 của Hợp đồng này. Việc điều chỉnh, sửa đổi danh mục sản phẩm sẽ được thông báo cho Bên B bằng văn bản và sẽ có hiệu lực ràng buộc các Bên thực hiện trong thời hạn 10 ngày làm việc kể từ ngày Thông báo.</fo:block>
	
	<fo:block page-break-before="always"></fo:block>
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 3 <fo:inline text-decoration="none">- KẾ HOẠCH KINH DOANH</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">3.1. 	Trong thời hạn 15 ngày làm việc kể từ ngày ký kết hợp đồng này, Bên B sẽ đệ trình kế hoạch kinh doanh phát triển thị trường phân phối Sản phẩm trong thời hạn 12 tháng (sau đây gọi tắt là “Kế hoạch kinh doanh”) trên lãnh thổ độc quyền cho Bên A. Kế hoạch kinh doanh sẽ bao gồm, nhưng không giới hạn ở các nội dung chính như sau:</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">a.	Bộ máy tổ chức hoạt động kinh doanh của Bên B, bao gồm bộ phận bán hàng, bộ phận marketing, bộ phận kỹ thuật, bộ phận chăm sóc khách hàng hậu mãi;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">b.	Các cơ sở kinh doanh của Bên B, nguồn lực tài chính và các bước thực hiện kế hoạch kinh doanh để bảo đảm đáp ứng yêu cầu đặt hàng tối thiểu theo quy định tại Điều 4 dưới đây của Hợp đồng này;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">c.	Nguồn Khách hàng tiềm năng có xu hướng tiêu dùng cao đối với Sản phẩm và các chiến lược để tiếp cận nguồn Khách hàng này trên Lãnh thổ độc quyền;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">d.	Bảng giá bán buôn và bán lẻ Sản phẩm;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">e.	Chương trình khuyến mại, xúc tiến thương mại để thúc đẩy doanh số bán Sản phẩm.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">3.2. 	Trong các năm tiếp theo, vào ngày làm việc cuối cùng của tuần thứ hai tháng 12, Bên B sẽ đệ trình Kế hoạch kinh doanh bằng văn bản cho Bên A;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">3.3.	Trong thời hạn 10 ngày làm việc kể từ ngày nhận được Kế hoạch kinh doanh của Bên B, Bên A sẽ trả lời bằng văn bản về việc đồng ý phê duyệt hoặc đề xuất sửa đổi Kế hoạch kinh doanh của Bên B. Trong thời hạn 05 ngày làm việc kể từ ngày nhận được ý kiến của Bên A về đề xuất sửa đổi Kế hoạch kinh doanh, nếu Bên B không có ý kiến phản hồi, Kế hoạch kinh doanh sẽ có hiệu lực ràng buộc đối với Bên B;</fo:block>
		<fo:block font-size="11pt" margin-left="20px">3.4.	Các Bên cam kết và xác nhận rằng, kết thúc ngày làm việc cuối cùng của tuần thứ ba tháng thứ 12 của năm, trong trường hợp Bên B đáp ứng được dưới 80% Kế hoạch kinh doanh đã được phê duyệt, Bên A có quyền xem xét đơn phương chấm dứt Hợp đồng trước thời hạn mà không phải chịu trách nhiệm bồi thường thiệt hại cho Bên B;</fo:block>
			
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 4 <fo:inline text-decoration="none">- YÊU CẦU ĐẶT HÀNG</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">4.1.	Chậm nhất vào ngày 10 hàng tháng, Bên B sẽ gửi yêu cầu đặt hàng bằng văn bản cho Bên A trong đó nêu rõ tên, chủng loại, số lượng Sản phẩm cần đặt hàng, thời gian giao hàng và phương thức giao hàng. Trong thời hạn 02 ngày làm việc kể từ ngày nhận được văn bản yêu cầu đặt hàng của Bên B, Bên A sẽ trả lời thông báo bằng văn bản về việc chấp thuận hoặc không chấp thuận yêu cầu đặt hàng của Bên B. Trong trường hợp Bên A chấp thuận hoặc không phản hồi Văn bản yêu cầu đặt hàng của Bên B trong thời hạn 04 ngày làm việc theo quy định tại Khoản 4.1 này, yêu cầu đặt hàng sẽ có giá trị ràng buộc các Bên phải thực hiện và được coi là một phần không thể tách rời của Hợp đồng này;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">4.2.	Trong trường hợp có sự mâu thuẫn giữa các điều khoản trong Yêu cầu đặt hàng và các quy định trong Hợp đồng này, các điều khoản trong Yêu cầu đặt hàng sẽ được ưu tiên áp dụng. Quy định này không áp dụng đối với các thỏa thuận liên quan đến các nội dung nêu tại Điều 6 của Hợp đồng này;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">4.3.	Các Bên cam kết và xác nhận rằng, trong thời hạn 12 tháng kể từ ngày ___ tháng 12 năm 2015 đến hết ngày ___ tháng 12 năm 2016, Bên B sẽ đặt hàng mua từ Bên A tối thiểu ___ sản phẩm (Sau đây gọi tắt là “Số đặt hàng tối thiểu”). Thời hạn tiếp theo kể từ ngày ___ tháng 12 năm 2016 đến hết ngày ___ tháng 12 năm 2017, số đặt hàng tối thiểu sẽ là ___ sản phẩm. Số đặt hàng tối thiểu trong các năm tiếp theo sẽ tăng ít nhất 10%, trừ khi các Bên có thỏa thuận khác bằng văn bản;</fo:block>
		<fo:block font-size="11pt" margin-left="20px">4.4.	Định kỳ sáu (6) tháng hàng năm, các Bên sẽ rà soát lại khả năng của Bên B trong việc đáp ứng Số đặt hàng tối thiểu. Trong trường hợp Bên B không đáp ứng được số đặt hàng tối thiểu theo quy định tại Khoản 4.3 trên đây, Bên A có quyền đơn phương chấm dứt Hợp đồng trước thời hạn mà không phải chịu trách nhiệm bồi thường thiệt hại cho Bên B.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 5 <fo:inline text-decoration="none">- GIÁ CẢ VÀ PHƯƠNG THỨC THANH TOÁN</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">5.1.	Bên B sẽ được hưởng mức giá  và tỷ lệ chiết khấu dành riêng cho Đại lý cấp 1 của Bên A theo chính sách do Bên A ban hành vào từng thời điểm cụ thể;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">5.2.	Mọi thay đổi về giá sẽ được Bên A thông báo trước bằng văn bản. Thay đổi về giá sẽ không áp dụng cho các Lệnh đặt hàng đã có hiệu lực trước ngày Bên A áp dụng chính sách thay đổi giá;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">5.3.	Giá bán buôn và bán lẻ Sản phẩm của Bên B cho Khách hàng và đối tác của Bên B sẽ do Bên A quyết định trong từng thời điểm khác nhau;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">5.4.	Bên B sẽ thanh toán cho Bên A bằng tiền mặt hoặc bằng cách chuyển khoản đến tài khoản do Bên A chỉ định trong thời hạn 07 ngày làm việc kể từ ngày nhận được đề nghị thanh toán của Bên A;</fo:block>
		<fo:block font-size="11pt" margin-left="20px">5.5.	Đồng tiền thanh toán sẽ là Việt Nam Đồng.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 6 <fo:inline text-decoration="none">- ĐỐI CHIẾU CÔNG NỢ VÀ HẠN MỨC GIÁ TRỊ ĐƠN HÀNG</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">6.1.	Vào ngày làm việc thứ 02 của mỗi tháng, các Bên sẽ tiến hành đối chiếu công nợ (nếu có) của tháng trước đó. Việc đối chiếu công nợ phải được lập thành biên bản có đầy đủ chữ ký và đóng dấu của Giám đốc/Tổng giám đốc và kế toán công nợ. Trong trường hợp, các Bên không đồng ý thống nhất được công nợ do chưa khớp số dư thì Bên B vẫn phải ký quyết toán và chốt công nợ, đồng thời ghi ý kiến của mình lên bản xác nhận công nợ đó. Các tranh chấp hay vướng mắc về công nợ phải được giải quyết dứt điểm trong vòng 5 ngày sau đó. Khi việc giải quyết khiếu nại hoặc cân số dư công nợ hoàn thành thì việc mua bán hàng hoá mới được tiếp tục;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">6.2.	Bên B sẽ được hưởng hạn mức giá trị đơn hàng là ____ VNĐ (Bằng chữ: _____ Việt Nam Đồng).</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">6.3.	Hết thời hạn thanh toán, nếu Bên B vẫn chưa thanh toán được số tiền hàng của tháng trước (nếu có), Bên B sẽ bị áp dụng mức lãi suất trả chậm tương ứng với mức lãi suất trả chậm do Ngân hàng cổ phần thương mại Ngoại thương Việt Nam (Vietcombank) công bố ở cùng thời điểm, tính trên số ngày chậm trả và số tiền chậm trả. Ngoài ra, Bên A có quyền từ chối giao hàng cho Bên B cho đến khi Bên B đã hoàn tất nghĩa vụ thanh toán;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">6.4.	Trong trường hợp Bên B vượt quá hạn mức giá trị đơn hàng theo quy định tại Khoản 6.2 và/hoặc 6.3 trên đây, Bên B có nghĩa vụ đặt cọc cho Bên A một khoản tiền tương ứng với 20% giá trị lô hàng trong thời hạn 05 ngày làm việc kể từ ngày Lệnh đặt hàng có hiệu lực. Trong trường hợp Bên B không thực hiện nghĩa vụ đặt cọc theo quy định tại Khoản này, Bên A có quyền từ chối thực hiện nghĩa vụ giao hàng.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">6.5.	Nhằm tránh hiểu nhầm, các Bên cam kết và xác nhận rằng, trong trường hợp Bên B chậm thực hiện nghĩa vụ đặt cọc, thời hạn giao hàng sẽ được tính lại từ thời điểm Bên B hoàn tất nghĩa vụ đặt cọc cho Lệnh đặt hàng tương ứng, trừ khi Bên A có thông báo khác bằng văn bản;</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 7 <fo:inline text-decoration="none">- QUYỀN VÀ NGHĨA VỤ CỦA CÁC BÊN</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px" font-style="italic" font-weight="bold">7.1.	Quyền và nghĩa vụ của Bên A</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">Ngoài các quyền và nghĩa vụ được quy định trong các điều khoản khác của Hợp đồng này, Bên A còn có các quyền và nghĩa vụ sau đây:</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Được thanh toán đầy đủ và đúng hạn tiền hàng;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Cung cấp bản sao các hồ sơ pháp lý doanh nghiệp và Sản phẩm cho Bên B khi Bên B có yêu cầu;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Đảm bảo cung cấp Sản phẩm đúng chủng loại, chất lượng và tiêu chuẩn kỹ thuật của Nhà cung cấp/Nhà sản xuất;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Định kỳ cung cấp cho Bên B các thông tin về Sản phẩm như: Danh mục và Catalogue sản phẩm hiện có, giá cả sản phẩm, dịch vụ đối với khách hàng;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Căn cứ vào lệnh đặt hàng của Bên B, Bên A giao hàng và hoá đơn đến địa điểm Bên B chỉ định trong thời hạn mà hai bên thỏa thuận;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Không ký kết hợp đồng mua bán Sản phẩm với các nhà phân phối khác trên phạm vi lãnh thổ độc quyền, ngoại trừ các chuỗi phân phối theo quy định tại Điều 2.2 của Hợp đồng này;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Thực hiện các chương trình hỗ trợ, xúc tiến bán hàng phù hợp định hướng phát triển kinh doanh của Bên A;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Thông báo bằng văn bản đến Bên B khi thực hiện các chương trình hỗ trợ, xúc tiến bán hàng hoặc khi thay đổi giá bán các sản phẩm của Bên A;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Yêu cầu Bên B tiến hành cung cấp các thông tin liên quan đến doanh số bán hàng, số lượng hàng tồn kho, các chương trình xúc tiến thương mại, quảng cáo, thông tin đánh giá về các đối thủ cạnh tranh trên lãnh thổ độc quyền của mỗi Quý;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Nhận hàng hoá hoàn trả nếu hàng hoá không đạt yêu cầu do lỗi Bên A;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Bồi thường thiệt hại và chịu phạt vi phạm theo quy định của pháp luật trong trường hợp Bên A vi phạm hợp đồng;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Thực hiện đúng các cam kết được ghi trong Hợp đồng.</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px"></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px" font-style="italic" font-weight="bold">7.2.	Quyền và nghĩa vụ của Bên B</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">Ngoài các quyền và nghĩa vụ được quy định trong các điều khoản khác của Hợp đồng này, Bên B còn có các quyền và nghĩa vụ sau đây:</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Cung cấp bản sao các hồ sơ pháp lý doanh nghiệp của Bên B;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Không mua, bán, phân phối các Sản phẩm cạnh tranh trực tiếp do Bên A thông báo bằng văn bản trong từng thời điểm cụ thể;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Không tiến hành mua, bán các Sản phẩm với các Nhà phân phối khác hoặc với Nhà sản xuất, trừ trường hợp được Bên A cho phép bằng văn bản;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Bán và phân phối sản phẩm Bên A theo giá bán lẻ và/hoặc bán buôn đã được Bên A quy định theo từng thời điểm cụ thể, giao hàng nhanh và thuận tiện đến khách hàng. Hợp tác góp phần thúc đấy doanh số bán sản phẩm của Bên A trong phạm vi lãnh thổ độc quyền;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Xin phê duyệt của Bên A trước khi tiến hành các chương trình khuyến mại đối với các Sản phẩm được phân phối theo Hợp đồng này;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Theo yêu cầu của Bên A, tiến hành cung cấp các thông tin liên quan đến doanh số bán hàng, số lượng hàng tồn kho, các chương trình xúc tiến thương mại, quảng cáo, thông tin đánh giá về các đối thủ cạnh tranh, phát triển hệ thống đại lý phân phối cấp 2 trên lãnh thổ độc quyền của mỗi Quý;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Phát triển hệ thống phân phối sản phẩm thông qua các cơ sở kinh doanh, công ty, hệ thống phân phối do Bên B nắm quyền kiểm soát trên vùng lãnh thổ độc quyền;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Hoàn trả sản phẩm không đạt yêu cầu do lỗi Bên A;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Bồi thường thiệt hại và chịu phạt vi phạm theo quy định của pháp luật trong trường hợp Bên B vi phạm hợp đồng;</fo:block>
			<fo:block font-size="11pt" margin-left="40px">-		Thực hiện đúng các cam kết được ghi trong Hợp đồng.</fo:block>
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 8 <fo:inline text-decoration="none">- VI PHẠM HỢP ĐỒNG</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">8.1.	Trong trường hợp một Bên vi phạm các quy định tại Hợp đồng này, Bên bị vi phạm có quyền thông báo bằng văn bản (Sau đây gọi tắt là “Thông báo vi phạm”) cho Bên vi phạm yêu cầu Bên vi phạm khắc phục hành vi vi phạm trong một thời hạn do Bên bị vi phạm ấn định. Thời hạn khắc phục hành vi vi phạm tối thiểu là 15 ngày kể từ ngày nhận được Thông báo vi phạm. Hết thời hạn khắc phục hành vi vi phạm do Bên bị vi phạm ấn định theo quy định tại Khoản này, nếu Bên vi phạm không khắc phục, sửa chữa hành vi vi phạm, Bên bị vi phạm có quyền đơn phương chấm dứt Hợp đồng trước thời hạn;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">8.2.	Không ảnh hưởng đến hiệu lực của Khoản 8.1 trên đây, Bên bị vi phạm có quyền áp dụng một khoản phạt vi phạm hợp đồng đối với Bên vi phạm tương ứng với 8% giá trị của phần Hợp đồng bị vi phạm và yêu cầu bồi thường thiệt hại (nếu có);</fo:block>
		<fo:block font-size="11pt" margin-left="20px">8.3.	Nhằm tránh hiểu lầm, thiệt hại thực tế để làm căn cứ tính mức bồi thường thiệt hại theo quy định tại Khoản 8.2 trên đây không bao gồm các khoản bồi thường thiệt hại mà Bên bị vi phạm phải thanh toán cho Bên thứ ba, các khoản lợi nhuận hoặc lợi thế thương mại bị bỏ lỡ.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 9 <fo:inline text-decoration="none">- SỬA ĐỔI VÀ CHẤM DỨT HỢP ĐỒNG</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">9.1.	Hợp Đồng này và các Phụ lục của Hợp Đồng này có thể sửa đổi theo thoả thuận bằng văn bản của các Bên;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">9.2.	Hợp Đồng này sẽ chấm dứt trong trường hợp sau:</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Hợp Đồng hết hạn mà không được gia hạn; hoặc</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Một trong hai Bên bị giải thể, phá sản hoặc tạm ngừng hoặc bị đình chỉ hoạt động kinh doanh; hoặc</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Hai Bên thỏa thuận chấm dứt Hợp đồng trước thời hạn;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Bên A đơn phương chấm dứt Hợp đồng trước thời hạn theo quy định tại các Khoản 3.4 và/hoặc 4.4 của Hợp đồng này bằng cách báo trước cho Bên B 15 ngày làm việc;</fo:block>
			<fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">-		Một trong hai Bên đơn phương chấm dứt Hợp đồng trước thời hạn theo quy định tại Khoản 8.1 của Hợp đồng này bằng cách báo trước cho Bên còn lại 15 ngày làm việc.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">9.3.	Nhằm tránh hiểu nhầm, việc chấm dứt Hợp đồng này không làm thay đổi quyền và nghĩa vụ của các Bên phát sinh hiệu lực trước ngày chấm dứt Hợp đồng và nghĩa vụ bảo mật thông tin theo quy định tại Điều 10 của Hợp đồng này, trừ khi các Bên có thỏa thuận khác bằng văn bản.</fo:block>
		
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 10 <fo:inline text-decoration="none">- BẢO MẬT THÔNG TIN</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">10.1.	Các Bên có trách nhiệm phải giữ kín tất cả những thông tin mà mình nhận được từ phía bên kia trong suốt thời hạn và sau khi hết hạn của Hợp Đồng này tối thiểu là 01 năm và phải thực hiện mọi biện pháp cần thiết duy trì tính bí mật của thông tin này;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">10.2.	Mỗi Bên không được tiết lộ cho bất cứ bên thứ ba nào bất kỳ thông tin nói trên trừ trường hợp được chấp thuận bằng văn bản của bên kia hoặc do cơ quan quản lý Nhà nước có thẩm quyền yêu cầu;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">10.3.	Mỗi bên phải tiến hành mọi biện pháp cần thiết để đảm bảo rằng không một nhân viên nào hay bất cứ ai thuộc sự quản lý của mình sẽ làm điều đó. Các nghĩa vụ nói trên vẫn sẽ kéo dài sau khi hết hạn Hợp Đồng tối thiểu là 01 năm.</fo:block>
	
	<fo:block page-break-before="always"></fo:block>
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 11 <fo:inline text-decoration="none">- GIẢI QUYẾT TRANH CHẤP</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">11.1.	Mọi tranh chấp phát sinh liên quan đến Hợp Đồng này trước hết sẽ được giải quyết thông qua thương lượng giữa các Bên.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">11.2.	Hết thời hạn 05 ngày kể từ ngày bắt đầu thương lượng, hòa giải, nếu việc giải quyết không đạt được bằng thương lượng, hòa giải thì một trong các Bên có quyền đưa tranh chấp ra tòa án có thẩm quyền để giải quyết. Phán quyết của Toà án sẽ là quyết định cuối cùng buộc hai bên phải thực hiện. Án phí và các chi phí khác do Bên thua kiện chịu.</fo:block>
		
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU 12 <fo:inline text-decoration="none">- ĐIỀU KHOẢN CHUNG</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">12.1.	Hợp đồng này có hiệu lực kể từ ngày đại diện theo pháp luật của Bên cuối cùng ký và đóng dấu vào Hợp đồng này cho đến ngày ___ tháng ___ năm___;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">12.2.	Trong thời hạn 01 tháng trước ngày hết hiệu lực theo quy định tại Điều 12.1 nêu trên, các Bên sẽ thông báo cho nhau quyết định về việc gia hạn hiệu lực của Hợp đồng này;</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">12.3.	Nếu một trong các Bên không thể thực thi được toàn bộ hay một phần nghĩa vụ của mình theo Hợp Đồng này do Sự Kiện Bất Khả Kháng là các sự kiện xảy ra một cách khách quan không thể lường trước được và không thể khắc phục được, mặc dù đã áp dụng mọi biện pháp cần thiết mà khả năng cho phép, bao gồm nhưng không giới hạn ở các sự kiện như thiên tai, hoả hoạn, lũ lụt, động đất, tai nạn, thảm hoạ, hạn chế về dịch bệnh, nhiễm hạt nhân hoặc phóng xạ, chiến tranh, nội chiến, khởi nghĩa, đình công hoặc bạo loạn, can thiệp của Cơ quan Chính phủ, hệ thống thiết bị của các bên gặp sự cố kỹ thuật trong quá trình vận hành khai thác hoặc do hạn chế về khả năng kỹ thuật các hệ thống thiết bị thì bên đó sẽ phải nhanh chóng thông báo cho bên kia bằng văn bản về việc không thực hiện được nghĩa vụ của mình do Sự Kiện Bất Khả Kháng, và sẽ, trong thời gian 15 ngày kể từ ngày xảy ra Sự Kiện Bất Khả Kháng, chuyển trực tiếp bằng thư bảo đảm cho Bên kia các bằng chứng về việc xảy ra Sự Kiện Bất Khả Kháng và khoảng thời gian xảy ra Sự Kiện Bất Khả Kháng đó.  Bên thông báo việc thực hiện Hợp đồng của họ trở nên không thể thực hiện được do Sự Kiện Bất Khả Kháng có trách nhiệm phải thực hiện mọi nỗ lực để hoặc giảm thiểu ảnh hưởng của Sự Kiện Bất Khả Kháng đó. Khi Sự Kiện Bất Khả Kháng xảy ra, thì nghĩa vụ của các Bên tạm thời không thực hiện và sẽ ngay lập tức phục hồi lại các nghĩa vụ của mình theo Hợp Đồng khi chấm dứt Sự Kiện Bất Khả Kháng hoặc khi Sự Kiện Bất Khả Kháng đó bị loại bỏ.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">12.4.	Mọi thông báo và thông tin liên lạc chính thức liên quan đến Hợp Đồng này sẽ được gửi tới địa chỉ của các Bên như nêu tại phần đầu của Hợp Đồng. Mọi thông báo và thông tin liên lạc khác hoặc thừa nhận được thực hiện theo Hợp Đồng này sẽ chỉ có hiệu lực nếu được lập thành văn bản, bao gồm cả fax và telex, và chỉ được coi là đã gửi và nhận hợp lệ: (i) khi giao bằng tay có giấy biên nhận; (ii) nếu gửi bưu điện thì 7 ngày làm việc sau khi gửi thư bảo đảm cước phí trả trước, có xác nhận bằng văn bản; (iii) trong trường hợp gửi bằng fax hay telex, khi gửi trong giờ làm việc bình thường tới địa điểm kinh doanh của người nhận, nếu có tín hiệu hay giấy xác nhận đã chuyển; và (iv) nếu gửi bằng dịch vụ giao nhận bảo đảm, khi thực tế đã nhận, và trong bất kỳ trường hợp nào, cũng phải gửi cho các bên tại địa chỉ đã được thông báo bằng văn bản tuỳ từng thời điểm.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">12.5.	Hợp đồng này được lập thành hai (02) bản, có hiệu lực ngang nhau. Mỗi bên giữ một (01) bản để thực hiện.</fo:block>
	-->

	<#list agreementInfo as termAgreement>
        <fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">${termAgreement.attrValue} <fo:inline text-decoration="none">- ${termAgreement.attrValueTree2}</fo:inline></fo:block>
        <#assign cond1 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("attrName", Static["org.ofbiz.entity.condition.EntityJoinOperator"].LIKE, termAgreement.attrName+"___")/>
        <#assign cond2 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("termTypeId", "AGREEMENT_AGENT_TERM_TREE_TEXT")/>
        <#assign agreementTree2Infos = delegator.findList("TermTypeAttrAgreementViewBold", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(cond1, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, cond2), null, null, null, false)/>
        <#if agreementTree2Infos?has_content>
            <#list agreementTree2Infos as agreementTree2>
            <#if agreementTree2.attrValueBold?has_content>
                <fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">${agreementTree2.attrValueBold} <fo:inline font-weight="normal"><#if agreementTree2.attrValue?has_content>${agreementTree2.attrValue}</#if></fo:inline></fo:block>
            <#else>
                <fo:block font-size="11pt" margin-left="20px" margin-bottom="5px"><#if agreementTree2.attrValue?has_content>${agreementTree2.attrValue}</#if></fo:block>
            </#if>
                <#assign cond31 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("attrName", Static["org.ofbiz.entity.condition.EntityJoinOperator"].LIKE, agreementTree2.attrName+"___")/>
                <#assign cond32 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("termTypeId", "AGREEMENT_AGENT_TERM_TREE_TEXT")/>
                <#assign agreementTree3Infos = delegator.findList("TermTypeAttr", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(cond31, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, cond32), null, null, null, false)/>
                <#list agreementTree3Infos as agreementTree3>
                    <fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">${agreementTree3.attrValue}</fo:block>
                </#list>
            </#list>
        <#else>
            <#assign agreementTree2Infos = delegator.findList("TermTypeAttr", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(cond1, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, cond2), null, null, null, false)/>
            <#list agreementTree2Infos as agreementTree2>
                <fo:block font-size="11pt" margin-left="20px" margin-bottom="5px"><#if agreementTree2.attrValue?has_content>${agreementTree2.attrValue}</#if></fo:block>
                <#assign cond31 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("attrName", Static["org.ofbiz.entity.condition.EntityJoinOperator"].LIKE, agreementTree2.attrName+"___")/>
                <#assign cond32 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("termTypeId", "AGREEMENT_AGENT_TERM_TREE_TEXT")/>
                <#assign agreementTree3Infos = delegator.findList("TermTypeAttr", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(cond31, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, cond32), null, null, null, false)/>
                <#list agreementTree3Infos as agreementTree3>
                    <fo:block font-size="11pt" margin-left="40px" margin-bottom="5px">${agreementTree3.attrValue}</fo:block>
                </#list>
            </#list>
        </#if>
    </#list>

	<fo:table>
		<fo:table-column column-width="50%"/>
	    <fo:table-column column-width="50%"/>
		<fo:table-body>
		  	<fo:table-row>
			  	<fo:table-cell>
			  		<fo:block font-weight="bold" text-align="center" font-size="13pt" text-transform="uppercase">${uiLabelMap.BACCARep}</fo:block>
			  	</fo:table-cell>
			  	<fo:table-cell>
			  		<fo:block font-weight="bold" text-align="center" font-size="13pt" text-transform="uppercase">${uiLabelMap.BACCBRep}</fo:block>
			  	</fo:table-cell>
		  	</fo:table-row>
		</fo:table-body>
	</fo:table>
</fo:block>
</#escape>