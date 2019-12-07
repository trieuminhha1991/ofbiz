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
	<div class="email-dear">Kính gửi anh/chị,</div>
	<div class="email-paragraph">Công ty Delys trân trọng cảm ơn anh/chị đã dành thời gian tham dự phỏng vấn tuyển vị trí <b><#if emplPositionTypeId?has_content>${emplPositionTypeId}<#else>#emplPositionTypeId#</#if></b> của công ty. Chúng tôi đánh giá cao sự quan tâm và chuẩn bị của anh/chị chị cho vị trí công việc tại Delys.</div>
	<div class="email-paragraph">Chúng tôi trân trọng kính mời anh/chị đến tham dự phỏng vấn vòng 2 với các thông tin sau:</div>
	<div class="email-paragraph">
		<ol>
			<li><h4><b>Vị trí tuyển dụng: <#if emplPositionTypeId?has_content>${emplPositionTypeId}<#else>#emplPositionTypeId#</#if></b></h4></li>
			<li><h4><b>Thời gian: <#if fromDate?has_content>${fromDate}<#else>#fromDate#</#if></b></h4></li>
			<li><h4><b>Địa điểm: <#if address?has_content>${address}<#else>#address#</#if></b></h4></li>
			<li><h4><b>Liên hệ: <#if contact?has_content>${contact}<#else>#contact#</#if></b></h4></li>
		</ol>
	</div>
	<div class="email-paragraph">Anh/chị vui lòng xác nhận những thông trên bằng cách reply email này để chúng tôi tiến hành các bước tiếp theo.</div>
	<div class="email-paragraph">Trân trọng,</div>
	<div class="email-paragraph">Phòng Hành chính Nhân sự</div>
	<div class="email-paragraph">Công ty Cổ phần Đầu tư Phát triển Thương mại Delys</div>
<div>
