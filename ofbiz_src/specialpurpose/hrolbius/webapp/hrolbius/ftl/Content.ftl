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
  <div id="partyContent" class="widget-box transparent no-bottom-border">
  	<div class="widget-box collapsed">
  <div class="widget-header widget-header-small header-color-blue2">
   <h6>${uiLabelMap.PartyAttachContent}</h6>
   <div class="widget-toolbar">
   		<a href="#" data-action="collapse">
   			<i class="icon-chevron-up"></i>
   		</a>
   </div>
  </div>
    <div class="widget-body">
          ${screens.render("component://hrolbius/widget/EmployeeScreens.xml#ContentList")}
      <hr />
      <h3 class="header small lighter blue" style="padding-left:5px;">${uiLabelMap.PartyAttachContent}</h3>
      <form id="uploadPartyContent" method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadPartyContent</@ofbizUrl>">
      	<div class="margin-left10">
	        <input type="hidden" name="dataCategoryId" value="PERSONAL"/>
	        <input type="hidden" name="contentTypeId" value="DOCUMENT"/>
	        <input type="hidden" name="statusId" value="CTNT_PUBLISHED"/>
	        <input type="hidden" name="partyId" value="${partyId}" id="contentPartyId"/> 
	        <div>
	        <div class="ace-file-input" style="display: block;margin-top:10px; width:222px; float:left;">
	            <input type="file" name="uploadedFile" id="uploadedFile" class="required error" size="25"/>
	            <label style="width: 218px" data-title="Choose" for="uploadedFile"></label>
	            <a class="remove" href="#"><i class="icon-remove"></i></a>
	        </div>
	        <select name="partyContentTypeId" class="required error" style="margin-top:10px; width: 283px;">
	          <option value="">${uiLabelMap.PartySelectPurpose}</option>
	          <#list partyContentTypes as partyContentType>
	            <option value="${partyContentType.partyContentTypeId}">${partyContentType.get("description", locale)?default(partyContentType.partyContentTypeId)}</option>
	          </#list>
	        </select>
	        </div>
	        <span style="font-size:14px; margin-left: -222px;">${uiLabelMap.PartyIsPublic}</span>
			<div style="margin-top:5px;">
	        <select name="isPublic">
	            <option value="N">${uiLabelMap.CommonNo}</option>
	            <option value="Y">${uiLabelMap.CommonYes}</option>
	        </select>
	        <select name="roleTypeId" style="width: 283px;">
	          <option value="">${uiLabelMap.PartySelectRole}</option>
	          <#list roles as role>
	            <option value="${role.roleTypeId}" <#if role.roleTypeId == "_NA_">selected="selected"</#if>>${role.get("description", locale)?default(role.roleTypeId)}</option>
	          </#list>
	        </select>
			</div>
	        <br/>
	        <button type="submit" class="btn btn-small btn-purple" style="margin-top: -20px;">
	        	<i class="icon-cloud-upload"></i>
	        	${uiLabelMap.CommonUpload}
	        </button>
        </div>
      </form>
    </div>
  </div>
  </div>
  <script type="text/javascript">
    jQuery("#uploadPartyContent").validate({
        submitHandler: function(form) {
            <#-- call upload scripts - functions defined in PartyProfileContent.js -->
            uploadPartyContent();
            getUploadProgressStatus();
            form.submit();
        }
    });
  </script>
