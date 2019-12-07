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
	.email-header{
		margin-left: auto; margin-right: auto;
		width: 200px !important;
	}
</style>
<div class="email-date">
	<#assign currentYear = Static["com.olbius.util.DateUtil"].getCurrentYear() >
	<#assign currentDate = Static["com.olbius.util.DateUtil"].getCurrentDate() >
	<#assign currentMonth = Static["com.olbius.util.DateUtil"].getCurrentMonth() >
	Hà Nội, ngày ${currentDate} tháng ${currentMonth} năm ${currentYear}
</div>
<div class="email-body">
	<div class="email-header"><h2 style="margin-bottom: .25em">THƯ THÔNG BÁO</h2></div>
	<div class="email-header" style="padding-left: 80px"><h6><i>(Kết quả tuyển dụng)</i></h6></div>
	<div class="email-dear">Kính gửi: <b><#if candidateName?has_content>${candidateName}<#else>#candidateName#</#if></b></div>
	<div class="email-paragraph">Thay mặt Công ty Cổ phần Đầu tư Phát triển Thương mại Delys, chúng tôi chân thành cảm ơn anh/chị đã dành thời gian tham dự phỏng vấn vào vị trí <b><#if emplPositionTypeId?has_content>${emplPositionTypeId}<#else>#emplPositionTypeId#</#if></b> tại Công ty chúng tôi. Chúng tôi đánh giá cao sự quan tâm và chuẩn bị của anh/chị cho vị trí công việc tại Delys.</div>
	<div class="email-paragraph">Thông qua buổi trao đổi, chúng tôi đánh giá cao những kinh nghiệm hiện có của anh/chị, tuy nhiên chúng tôi nhận thấy những yêu cầu công việc của vị trí mà chúng tôi cần chưa phù hợp với năng lực và thế mạnh của anh/chị có thời điểm  này.</div>
	<div class="email-paragraph">Chúng tôi sẽ lưu hồ sơ của anh/chị vào cơ sở dữ liệu của chúng tôi và sẽ liên lạc lại với anh/chị ngay khi có một vị trí phù hợp có thể giúp anh/chị phát huy được hết khả năng làm việc của mình.</div>
	<div class="email-paragraph">Chúc anh/chị sức khỏe, thành công hơn nữa trong sự nghiệp của mình và hy vọng có cơ hội được hợp tác với anh/chị trong thời gian không xa.</div>
	<div class="email-paragraph">Trân trọng,</div>
	<div class="email-paragraph">Phòng Hành chính Nhân sự</div>
	<div class="email-paragraph">Công ty Cổ phần Đầu tư Phát triển Thương mại Delys</div>
<div>
