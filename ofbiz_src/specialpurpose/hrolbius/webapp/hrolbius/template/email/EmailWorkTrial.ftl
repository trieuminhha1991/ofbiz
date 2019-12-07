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
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>${title?if_exists}</title>
    </head>
    <body>
        <h2>${title?if_exists}</h2>
		<h5>Kính gửi: Anh/Chị ${person.firstName?if_exists} ${person.lastName?if_exists}</h5>
		<p>Chúng tôi chân thành cảm ơn sự quan tâm của Anh/Chị đối với công ty, cũng như chức danh mà <br/>
		Anh/Chị đã dự tuyển. Chúng tôi trân trọng thông báo Anh/Chị đã trúng tuyển đợt tuyển dụng vừa qua<br/>
		Anh/Chị sẽ:<br/>
		<#assign org = delegator.findOne("PartyGroup", {"partyId" : workingPartyId?if_exists}, true)>
		<#assign emplPositionType = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionTypeId?if_exists}, true)>
		- Làm việc tại: ${org.groupName?if_exists}<br/>
		- Chức danh công việc: ${emplPositionType.description?if_exists}<br/>
		- Ngày nhận việc: ${fromDate?if_exists}</br>
		- Thời gian thử việc: ${trialTime?if_exists}<br/>
		</p>
		<h5>Lương và chế độ khác như sau:</h5>
		<p>
		- Lương: ${salary?if_exists}<br/>
		- Phần trăm lương thử việc: ${trialSalaryRate?if_exists}<br/>
		- Các khoản phụ cấp: ${allowance?if_exists}<br/>
		- Các chế độ khác: Theo luật lao động Việt Nam, theo nội quy lao động và quy định 
		tài chính của công ty</br>
		</p>
		<p>
		Chúng tôi hoan nghênh sự gia nhập của Anh/Chị vào công ty và hy vọng chúng ta sẽ có được <br/>
		một sự hợp tác tốt đẹp lâu bền.
		</p>
		<h4>Trân trọng.</h4>
    </body>
</html>
