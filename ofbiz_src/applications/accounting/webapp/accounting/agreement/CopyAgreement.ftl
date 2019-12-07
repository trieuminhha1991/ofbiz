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
<#if agreement?exists>
<div class="widget-box transparent no-bottom-border">
  <div class="widget-header">
  <h3>${uiLabelMap.PageTitleCopyAgreement}</h3>
    <span class="widget-toolbar">
    </span>
    <br class="clear"/>
  </div>
  <div class="widget-body">
    <form action="<@ofbizUrl>copyAgreement</@ofbizUrl>" method="post">
        <input type="hidden" name="agreementId" value="${agreementId}"/>
        <div>
            <input type="checkbox" name="copyAgreementTerms" value="Y" checked="checked" /> &nbsp; <span class="lbl">${uiLabelMap.AccountingAgreementTerms}</span>
            <input type="checkbox" name="copyAgreementProducts" value="Y" checked="checked" /> &nbsp; <span class="lbl">${uiLabelMap.ProductProducts}</span>
            <input type="checkbox" name="copyAgreementParties" value="Y" checked="checked" /> &nbsp; <span class="lbl">${uiLabelMap.Party}</span>
            <input type="checkbox" name="copyAgreementFacilities" value="Y" checked="checked" />&nbsp; <span class="lbl">${uiLabelMap.ProductFacilities}</span>
            
        </div>
        <div class="button-bar">
            <button type="submit" class="btn btn-mini btn-small btn-primary">
            	<i class="icon-copy"></i>
            	${uiLabelMap.CommonCopy}
            </button>
        </div>
    </form>
  </div>
</div>
</#if>