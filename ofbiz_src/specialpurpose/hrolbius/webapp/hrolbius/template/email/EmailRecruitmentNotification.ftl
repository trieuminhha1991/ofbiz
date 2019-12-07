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
        <h2	style="text-align: center;">${title}</h2>
        <p>Kính gửi: ${person.firstName?if_exists} ${person.lastName?if_exists}</p>
        <p>
       		Chúng tôi xin chân thành cảm ơn sự quan tâm của anh/chị đối với công ty cũng như chức danh công việc mà anh/chị đã dự tuyển. 
		</p>
		
		<p>
       		${result}
		</p>
		<p>
			Chúng tôi chúc anh/chị gặt hái được nhiều thành công trong cuộc sống.
		</p>
		<p>
			Xin chân thành cảm ơn và trân trọng kính chào!
		</p>
    </body>
</html>
