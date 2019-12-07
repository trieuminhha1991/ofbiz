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
        <title>${title}</title>
    </head>
    <body>
        <h2>${title}</h2>
		
		<p>
       		Phòng nhân sự Công Ty cổ phần OLBIUS trân trọng mời Ông/Bà: ${person.firstName?if_exists} ${person.lastName?if_exists} tham dự ${description} 
       		với vai trò là người kiểm tra.
       		<br/>
		</p>
		<p>
		</p>
		<p>
			-Thời gian: Static["com.olbius.util.DateUtil"]convertDate(fromDate) <br/>
			-Địa điểm: ${address} <br />
		</p>
		<p>
			Rất mong Ông/Bà có mặt đúng giờ.<br/>
			Xin chân thành cảm ơn và trân trọng kính chào!
		</p>
    </body>
</html>
