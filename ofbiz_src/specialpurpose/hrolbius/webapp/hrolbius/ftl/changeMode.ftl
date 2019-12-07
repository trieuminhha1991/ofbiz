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

<div class="widget-box">

	<div class="widget-body">
		<form method="post" name="changeMode" action="<@ofbizUrl>changeMode</@ofbizUrl>">
			<div class="widget-main">
			<table cellspacing="0">
				<tr >
					<td > 
						<label class="padding-bottom5 padding-right15">${uiLabelMap.FormFieldTitle_PartyName}</label>
					</td>
					<td>
					<@htmlTemplate.lookupField formName="changeMode" name="authId" id="authId" fieldFormName="LookupPartyName"/>
					</td>
				</tr>
				<tr >
					<td > 
						<label class="padding-bottom5 padding-right15">${uiLabelMap.FormFieldTitle_Privilege}</label>
					</td>
					<td>
						<select name="privilege">
							<option value="JCR_READ">${uiLabelMap.READ}</option>
							<option value="JCR_WRITE">${uiLabelMap.WRITE}</option>
							<option value="JCR_ALL">${uiLabelMap.ALL}</option>
						</select>
					</td>
				</tr>
				<tr >
					<td > 
						<label class="padding-bottom5 padding-right15">${uiLabelMap.FormFieldTitle_Allow}</label>
					</td>
					<td>
						<select name="allow">
							<option value="true">${uiLabelMap.ALLOW}</option>
							<option value="false">${uiLabelMap.DENY}</option>
						</select>
					</td>
				</tr>
				<input type="hidden" name="path" value="${path}" />
				<tr >
					<td></td>
					<td > 
						<button class="btn btn-primary btn-small" type="submit" name="submit">
							<i class="icon-ok"></i>
							${uiLabelMap.CommonSubmit}
						</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>