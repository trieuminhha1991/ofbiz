<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<style>
	.email-paragraph{
		margin-top: 10px;
	}
	.email-date{
		 text-align: right;
		 margin-top: 20px;
		 margin-bottom: 20px;
		 margin-right: 20px;
	}
</style>
<div class="email-date">
	<#assign currentYear = Static["com.olbius.util.DateUtil"].getCurrentYear() >
	<#assign currentDate = Static["com.olbius.util.DateUtil"].getCurrentDate() >
	<#assign currentMonth = Static["com.olbius.util.DateUtil"].getCurrentMonth() >
	Hà Nội, ngày ${currentDate} tháng ${currentMonth} năm ${currentYear}
</div>
<div class="email-body">
	<div class="email-dear">Dear,</div>
	<div class="email-paragraph">Công ty Cổ phần Đầu tư Phát triển Thương mại Delys là công ty chuyên nhập khẩu và phân phối thực phẩm cao cấp của châu Âu, đặc biệt là các sản phẩm về sữa như váng sữa và sữa chua nhập khẩu. Sản phẩm váng sữa nổi tiếng nhất Việt Nam với tên gọi Monte được công ty nhập khẩu và phân phối độc quyền từ đối tác Zott GMBH & Co. KG (CHLB Đức) – doanh nghiệp sản xuất sữa hàng đầu châu Âu với bề dầy lịch sử từ năm 1926.</div>
	<div class="email-paragraph">Phòng Nhân sự, Công ty Delys đã xem CV của anh/chị và thấy rằng vị trí mà anh/chị  đang mong muốn phù hợp với nhu cầu tuyển dụng của Công ty chúng tôi hiện tại. </div>
	<div class="email-paragraph">Chúng tôi trân trọng kính mời anh/chị đến tham dự phỏng vấn với các thông tin sau:</div>
	<div class="email-paragraph">
		<ol>
			<li><h4><b>Vị trí tuyển dụng: <#if emplPositionTypeId?has_content>${emplPositionTypeId}<#else>#emplPositionTypeId#</#if></b></h4></li>
			<li><h4><b>Thời gian: <#if fromDate?has_content>${fromDate}<#else>#fromDate#</#if></b></h4></li>
			<li><h4><b>Địa điểm: <#if address?has_content>${address}<#else>#address#</#if></b></h4></li>
			<li><h4><b>Liên hệ: <#if contact?has_content>${contact}<#else>#contact#</#if></b></h4></li>
		</ol>
	</div>
	<div class="email-paragraph">Anh/chị vui lòng xác nhận những thông trên bằng cách reply email này để chúng tôi tiến hành các bước tiếp theo.</div>
	<div class="email-paragraph">Trân trọng!</div>
<div>
