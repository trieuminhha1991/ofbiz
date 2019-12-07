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
<#if quote?exists>
<form action="<@ofbizUrl>copyQuote</@ofbizUrl>" method="post">
    <input type="hidden" name="quoteId" value="${quoteId}"/>
    
    <div class="margin-left15">
        <label>
			<input name="copyQuoteItems" type="checkbox" value="Y" checked="checked"><span class="lbl">${uiLabelMap.OrderOrderQuoteItems}</span>
		</label>
        <label>
			<input name="copyQuoteAdjustments" type="checkbox" value="Y" checked="checked"><span class="lbl">${uiLabelMap.OrderOrderQuoteAdjustments}</span>
		</label>
        <label>
			<input name="copyQuoteRoles" type="checkbox" value="Y" checked="checked"><span class="lbl">${uiLabelMap.OrderOrderQuoteRoles}</span>
		</label>
        <label>
			<input name="copyQuoteAttributes" type="checkbox" value="Y" checked="checked"><span class="lbl">${uiLabelMap.OrderOrderQuoteAttributes}</span>
		</label>
        <label>
			<input name="copyQuoteCoefficients" type="checkbox" value="Y" checked="checked"><span class="lbl">${uiLabelMap.OrderOrderQuoteCoefficients}</span>
		</label>
        <label>
			<input name="copyQuoteTerms" type="checkbox" value="Y" checked="checked"><span class="lbl">${uiLabelMap.OrderOrderQuoteTerms}</span>
		</label>
    </div>
    <button type="submit" class="btn btn-primary btn-small margin-left8 margin-bottom8" name="submitButton"><i class="icon-copy"></i>${uiLabelMap.CommonCopy}</button>
</form>
</#if>