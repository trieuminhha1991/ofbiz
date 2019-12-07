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

<#assign username = requestParameters.USERNAME?default((sessionAttributes.autoUserLogin.userLoginId)?default(""))>

<div id="main-content">
	<div class="row-fluid">
		<div class="span12">						
			<div class="login-container">
				<div class="space-6"></div>
				<div class="row-fluid">
					<div class="position-relative">
						<div id="login-box" class="visible widget-box no-border">
							<div class="widget-body">
		 						<div class="widget-main">
									<h4 class="header red lighter bigger"><i class="icon-key"></i>${uiLabelMap.CommonPasswordChange}</h4>
									<div class="space-6"></div>
									<p>${uiLabelMap.CommonUsername}:&nbsp;${username}</p>
									<form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform">
								      <input type="hidden" name="requirePasswordChange" value="Y"/>
								      <input type="hidden" name="USERNAME" value="${username}"/>
								      <table cellspacing="0">
								        <tr>
								          <td class="label">${uiLabelMap.CommonCurrentPassword}</td>
								          <td><input type="password" name="PASSWORD" value="" size="20" class="span12"/></td>
								        </tr>
								        <tr>
								          <td class="label">${uiLabelMap.CommonNewPassword}</td>
								          <td><input type="password" name="newPassword" value="" size="20" class="span12"/></td>
								        </tr>
								        <tr>
								          <td class="label">${uiLabelMap.CommonNewPasswordVerify}</td>
								          <td><input type="password" name="newPasswordVerify" value="" size="20" class="span12"/></td>
								        </tr>
								        <tr>
								          <td colspan="2" align="center">
								            <button type="submit" class="btn btn-small btn-primary"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
								          </td>
								        </tr>
								      </table>
								    </form>
								 </div>
							</div>
						</div>
				</div>
			</div>
			</div>
		</div>
	</div>
</div>
<script language="JavaScript" type="text/javascript">
  document.loginform.PASSWORD.focus();
</script>
