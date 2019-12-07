<#escape x as x?xml>
<fo:block font-size="11" font-family="Arial">
	
	<fo:block font-weight="bold" text-align="center" font-size="12pt" margin-top="10px">${uiLabelMap.BSPDFVietNamNational}</fo:block>
	<fo:block font-weight="bold" text-align="center" font-size="12pt">${uiLabelMap.BSPDFVietNamTargetNational}</fo:block>
	<fo:block font-weight="bold" text-align="center" font-size="12pt" margin-bottom="10px">---------------------------</fo:block>
	<fo:block font-weight="bold" text-align="center" font-size="15pt" text-transform="uppercase">${uiLabelMap.BSAgreementWithAgent}</fo:block>
	<fo:block font-weight="bold" text-align="center" font-size="12pt" font-style="italic" margin-bottom="10px">(${uiLabelMap.BSPDFNumberContract} ${(agreement.agreementCode)?if_exists}/${uiLabelMap.BSPDFHDKT})</fo:block>
	
	<fo:block font-weight="bold" font-size="12pt" font-style="italic" margin-bottom="5px">- ${uiLabelMap.BSPDFOrdinanceOnEconomicContracts}.</fo:block>
	<fo:block font-weight="bold" font-size="12pt" font-style="italic" margin-bottom="5px">- ${uiLabelMap.BSPDFDetailOrdinanceOnEconomicContracts}.</fo:block>
	<fo:block font-weight="bold" font-size="12pt" font-style="italic" margin-bottom="10px">- ${uiLabelMap.BSPDFNeedsAndResponsivenessParties}.</fo:block>
	<fo:block font-size="12pt" margin-bottom="10px">${uiLabelMap.BSPDFDateTimeHave}:</fo:block>

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
	
	<fo:block font-weight="bold" font-size="12pt" font-style="italic" margin-bottom="10px">${uiLabelMap.BSPDFDiscussingAgreeToSignContract}:</fo:block>
	<!--
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU I <fo:inline text-decoration="none">- KHU VỰC BÁN HÀNG.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">1.	Bên B là nhà phân phối độc quyền, phân phối mặt hàng CIVIC tại thị trường ............ do bên A cung cấp.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">2.	Bên B không được bán ra ngoài khu vực khi chưa có sự đồng ý của bên A. Nếu bên A phát hiện ra bên B bán ra ngoài khu vực chỉ định với bất kỳ hình thức nào để hưởng chênh lệch giá hoặc với mục đích  khác thì bên A có quyền chấm dứt hợp đồng nhà phân phối đối với bên B.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU II <fo:inline text-decoration="none">- HÀNG HOÁ.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">1.	Hàng hoá bao gồm các mặt hàng mang nhãn hiệu CIVIC được bán bởi bên A. Nếu bên B mua, bán các loại hàng hoá giống như trên nhưng không rõ nguồn gốc xuất xứ hoặc không phải từ bên A thì sẽ bị mất quyền làm nhà phân phối của bên A.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">2.	Bên A cam kết cung cấp cho Bên B các sản phẩm có chất lượng tốt, nguồn gốc xuất xứ, thông tin sản phẩm hàng hoá đúng tiêu chuẩn theo qui định của Nhà nước.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU III <fo:inline text-decoration="none">- GIÁ MUA VÀ GIÁ BÁN.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">1.	Bên B sẽ được mua hàng của bên A theo một giá thống nhất được áp dụng chung cho các nhà phân phối trên khu vực Miền Bắc.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">2.	Mọi sự thay đổi về giá mua, giá bán sẽ được bên A thông báo cho bên B bằng văn bản trước 07 ngày kể từ ngày áp dụng.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU IV <fo:inline text-decoration="none">- CƠ CẤU TỔ CHỨC BÁN HÀNG.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">1.	Bên B phải đảm bảo có đủ nhân viên bán các mặt hàng của bên A nhằm đạt chỉ tiêu về doanh số hàng tháng mà bên A giao cho bên B. Các khoản lương, thưởng của đội ngũ nhân viên bán hàng bên B chịu trách nhiệm  chi trả theo sự hướng dẫn của bên A.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">2.	Bên B phải báo cáo cho bên A về danh sách đầy đủ khách hàng, các số liệu bán hàng và tồn kho hàng tuần, hàng tháng theo quy định của bên A.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">3.	Việc tổ chức bán hàng trong khu vực sẽ do bên B triển khai trên tinh thần dựa vào sự bàn bạc, thống nhất giữa bên A và bên B.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">4.	Chỉ tiêu bán hàng của bên B sẽ được giao theo tháng – quý – năm dựa trên tình hình phát triển thị trường theo như yêu cầu của bên A.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU V <fo:inline text-decoration="none">- DỰ TRỮ HÀNG HOÁ VÀ CHÍNH SÁCH KHIẾU NẠI SẢN PHẨM.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">1.	Bên B chịu trách nhiệm bố trí kho thích hợp và bảo quản, phân phối hàng hoá trong điều kiện tốt, đảm bảo nguyên tắc “nhập trước xuất trước” Bên A chỉ đồng ý về các khiếu nại sản phẩm ngay khi bên A giao hàng. Khi phát hiện hàng hoá bị lỗi bên B phải lập biên bản và thông báo ngay cho bên A trong vòng 03 ngày.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">2.	Bên B được phép đổi những hàng hoá không phù hợp trên thị trường hoặc hàng hoá bị lỗi do lỗi của nhà sản xuất sang những hàng hoá phù hợp hơn.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">3.	Lượng hàng hoá dự trữ của bên B tối thiểu phải đủ bán trong vòng 15 ngày.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU VI <fo:inline text-decoration="none">- GIAO NHẬN VÀ THANH TOÁN.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">1.	Tất cả các hoá đơn mua hàng đều phải được thanh toán trước (bằng tiền mặt hoặc chuyển khoản). Ngay sau khi bên A nhận được tiền bên A sẽ giao hàng ngay. Hàng hoá sẽ được giao tại địa điểm mà bên B yêu cầu tại Hà nội. Chi phí vận chuyển đến kho bên B, bên A chịu trách nhiệm chi trả có sự tư vấn giúp đỡ của bên B.</fo:block>
		<fo:block font-size="11pt" margin-left="20px">2.	Bên A sẽ được miễn trách nhiệm khi thực hiện việc giao hàng chậm vì lý do bất khả kháng. Sự bất khả kháng được xác định theo quy định tại nghị định số 17 – HĐBT ban hành ngày 16/01/1990 của Hội Đồng Bộ Trưởng quy định chi tiết thi hành pháp lệnh hợp đồng kinh tế.</fo:block>
	
	<fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">ĐIỀU VII <fo:inline text-decoration="none">- ĐIỀU KHOẢN CHUNG.</fo:inline></fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">1.	Bên B không được phép trực tiếp kinh doanh các sản phẩm cạnh tranh trực tiếp với những sản phẩm mang nhãn hiệu CIVIC do bên A cung cấp.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">2.	Bên B phải thông báo kịp thời những thông tin về thị trường, hàng hoá và các hoạt động của đối thủ cạnh tranh trên địa bàn kinh doanh của mình cũng như không được phép tiết lộ những thông tin nội bộ có lợi cho đối thủ cạnh tranh.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">3.	Nếu bên B không tổ chức tốt các hoạt động bán hàng dẫn đến việc hai tháng liên tiếp không đạt chỉ tiêu thì bên A có quyền đơn phương chấm dứt hợp đồng nhà phân phối đối với bên B.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">4.	Trong mọi trường hợp thì hợp đồng này đều không có giá trị pháp lý để bên B nhân danh công ty Trường An giao dịch với các doanh nghiệp khác.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">5.	Hai bên đồng ý và cam kết thực hiện đầy đủ các điều khoản ghi trong hợp đồng. Mọi thay đổi trong nội dung hợp đồng phải được hai bên thỏa thuận bằng văn bản. Khi có phát sinh hoặc tranh chấp trong quá trình thực hiện hợp đồng, hai bên cùng nhau bàn bạc giải quyết trên tinh thần hợp tác, bình đẳng và cùng có lợi. Nếu không thương lượng hòa giải được mới đưa ra trọng tài kinh tế Thành phố Hà Nội để phân xử theo luật định. Chi phí và thiệt hại phát sinh từ việc tranh chấp hợp đồng này do bên vi phạm chịu.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">6.	Hợp đồng được lập thành 02 bản, mỗi bên giữ  01 bản có giá trị pháp lý như nhau.</fo:block>
		<fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">7.	Hợp đồng có hiệu lực từ ngày ký đến hết ngày    tháng   năm  2006. Sau khi hết hạn hợp đồng Hai bên có thoả thuận gia hạn hợp đồng bằng phụ lục đính kèm  (Phụ lục là phần không thể tách rời hợp đồng này)</fo:block>
    -->

    <#list agreementInfo as termAgreement>
        <fo:block font-weight="bold" font-size="13pt" margin-bottom="10px" margin-top="10px" text-decoration="underline">${termAgreement.attrValue} <fo:inline text-decoration="none">- ${termAgreement.attrValueTree2}</fo:inline></fo:block>
        <#assign cond1 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("attrName", Static["org.ofbiz.entity.condition.EntityJoinOperator"].LIKE, termAgreement.attrName+"___")/>
        <#assign cond2 =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("termTypeId", "AGREEMENT_DIS_TERM_TREE_TEXT")/>
        <#assign agreementTree2Infos = delegator.findList("TermTypeAttrAgreementViewBold", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(cond1, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, cond2), null, null, null, false)/>
        <#if agreementTree2Infos?has_content>
            <#list agreementTree2Infos as agreementTree2>
            <#if agreementTree2.attrValueBold?has_content>
                <fo:block font-size="11pt" margin-left="20px" font-weight="bold" margin-bottom="5px">${agreementTree2.attrValueBold} <fo:inline font-weight="normal"><#if agreementTree2.attrValue?has_content>${agreementTree2.attrValue}</#if></fo:inline></fo:block>
            <#else>
                <fo:block font-size="11pt" margin-left="20px" margin-bottom="5px"><#if agreementTree2.attrValue?has_content>${agreementTree2.attrValue}</#if></fo:block>
            </#if>
            </#list>
        <#else>
            <#assign agreementTree2Infos = delegator.findList("TermTypeAttr", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(cond1, Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND, cond2), null, null, null, false)/>
            <#list agreementTree2Infos as agreementTree2>
                <fo:block font-size="11pt" margin-left="20px" margin-bottom="5px">${agreementTree2.attrValue}</fo:block>
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