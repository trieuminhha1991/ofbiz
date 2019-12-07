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

<#if orderTerms?has_content>
<div class="widget-box olbius-extra">
  <div class="widget-header widget-header-small header-color-blue2">
  <h6>${uiLabelMap.OrderOrderTerms}</h6>
      <div class="widget-toolbar">
        	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
  	</div>
	<div class="widget-body">
  	<div class="widget-body-inner">
    <div class="widget-main">
      <table class="table table-hover table-striped dataTable table-bordered" cellspacing='0'>
      <tr class="header-row">
        <td width="35%">${uiLabelMap.OrderOrderTermType}</td>
        <td width="15%" align="center">${uiLabelMap.OrderOrderTermValue}</td>
        <td width="15%" align="center">${uiLabelMap.OrderOrderTermDays}</td>
        <td width="35%" align="center">${uiLabelMap.CommonDescription}</td>
      </tr>
    <#list orderTerms as orderTerm>
      <tr>
        <td width="35%">${orderTerm.getRelatedOne("TermType", false).get("description", locale)}</td>
        <td width="15%" align="center">${orderTerm.termValue?default("")}</td>
        <td width="15%" align="center">${orderTerm.termDays?default("")}</td>
        <td width="35%" align="center">${orderTerm.textValue?default("")}</td>
      </tr>
    </#list>
      </table>
    </div>
     </div>
      </div>
</div>
</#if>