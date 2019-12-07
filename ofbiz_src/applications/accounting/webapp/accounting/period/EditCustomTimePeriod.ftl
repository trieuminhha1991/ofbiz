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


<br />
<#if security.hasPermission("PERIOD_MAINT", session)>
   <div class="widget-box transparent no-bottom-border">
     <div class="widget-header">
     	<h4>${uiLabelMap.AccountingShowOnlyPeriodsWithOrganization}</h4>
     </div>
     <div class="widget-body">
     <form method="post" action="<@ofbizUrl>EditCustomTimePeriod</@ofbizUrl>" name="setOrganizationPartyIdForm">
         <input type="hidden" name="currentCustomTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
         <span style="margin-left:20px">${uiLabelMap.AccountingShowOnlyPeriodsWithOrganization}</span>
         <input type="text" size="20" name="findOrganizationPartyId" style="margin-top: 10px" value="${findOrganizationPartyId?if_exists}" />
         <button class="btn btn-mini btn-primary" type="submit" style="margin-top: 10px">
         	<i class="icon-ok" ></i>
         	${uiLabelMap.CommonUpdate}
         </button>
     </form>
     </div>
   </div>

  <div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
      <#if currentCustomTimePeriod?has_content>
      <h4>${uiLabelMap.AccountingCurrentCustomTimePeriod}</h4>
        <span class="widget-toolbar">
          <li><a href="<@ofbizUrl>EditCustomTimePeriod?findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>">${uiLabelMap.CommonClearCurrent}</a></li>
        </span>
      <#else>
        <h4>${uiLabelMap.AccountingCurrentCustomTimePeriod}</h4>
      </#if>
    </div>
    <#if currentCustomTimePeriod?has_content>
    <div class="widget-body">
        <form method="post" action="<@ofbizUrl>updateCustomTimePeriod</@ofbizUrl>" name="updateCustomTimePeriodForm">
          <input type="hidden" name="findOrganizationPartyId" value="${findOrganizationPartyId?if_exists}" />
          <input type="hidden" name="customTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
          <div style="overflow-x: scroll !important; border: 0.5px solid rgb(204, 204, 204) !important;">
      <table class="table table-striped table-bordered table-hover dataTable" cellspacing="0">
        <tr class="header-row">
          <td>${uiLabelMap.CommonId}</td>
          <td>${uiLabelMap.CommonParent}</td>
          <td>${uiLabelMap.AccountingOrgPartyId}</td>
          <td>${uiLabelMap.AccountingPeriodType}</td>
          <td>${uiLabelMap.CommonNbr}</td>
          <td>${uiLabelMap.AccountingPeriodName}</td>
          <td>${uiLabelMap.CommonFromDate}</td>
          <td>${uiLabelMap.CommonThruDate}</td>
          <td>&nbsp;</td>
        </tr>
          <tr>
            <td>${currentCustomTimePeriod.customTimePeriodId}</td>
            <td>
              <select name="parentPeriodId">
                <option value=''>&nbsp;</option>
                <#list allCustomTimePeriods as allCustomTimePeriod>
                  <#assign allPeriodType = allCustomTimePeriod.getRelatedOne("PeriodType", true)>
                  <#assign isDefault = false>
                  <#if (currentCustomTimePeriod.parentPeriodId)?exists>
                    <#if currentCustomTimePeriod.customTimePeriodId = allCustomTimePeriod.customTimePeriodId>
                      <#assign isDefault = true>
                    </#if>
                  </#if>
                  <option value='${allCustomTimePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
                    ${allCustomTimePeriod.organizationPartyId}
                    <#if allPeriodType??>${allPeriodType.description}:</#if>
                    ${allCustomTimePeriod.periodNum}
                    [${allCustomTimePeriod.customTimePeriodId}]
                  </option>
                </#list>
              </select>
              <#if (currentCustomTimePeriod.parentPeriodId)?exists>
                <a href='<@ofbizUrl>EditCustomTimePeriod?currentCustomTimePeriodId=${currentCustomTimePeriod.parentPeriodId}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>'>
                ${uiLabelMap.CommonSetAsCurrent}</a>
              </#if>
            </td>
            <td><input type="text" size='12' name="currentCustomTimePeriod" value="${currentCustomTimePeriod.organizationPartyId?if_exists}" /></td>
            <td>
              <select name="periodTypeId">
                <#list periodTypes as periodType>
                  <#assign isDefault = false>
                  <#if (currentCustomTimePeriod.periodTypeId)?exists>
                    <#if currentCustomTimePeriod.periodTypeId = periodType.periodTypeId>
                      <#assign isDefault = true>
                    </#if>
                  </#if>
                  <option value='${periodType.periodTypeId}'<#if isDefault> selected="selected"</#if>>
                    ${periodType.description} [${periodType.periodTypeId}]
                  </option>
                </#list>
              </select>
            </td>
            <td><input type="text" size='4' name="periodNum" value="${currentCustomTimePeriod.periodNum?if_exists}" /></td>
            <td><input type="text" size='10' name="periodName" value="${currentCustomTimePeriod.periodName?if_exists}" /></td>
            <td>
              <#assign hasntStarted = false>
              <#assign compareDate = currentCustomTimePeriod.getDate("fromDate")>
              <#if compareDate?has_content>
                <#if nowTimestamp.before(compareDate)><#assign hasntStarted = true></#if>
              </#if>
              <input type="text" size='13' name="fromDate" value="${currentCustomTimePeriod.fromDate?string("yyyy-MM-dd")}"<#if hasntStarted> class="alert"</#if> />
            </td>
            <td>
              <#assign hasExpired = false>
              <#assign compareDate = currentCustomTimePeriod.getDate("thruDate")>
              <#if compareDate?has_content>
                <#if nowTimestamp.after(compareDate)><#assign hasExpired = true></#if>
              </#if>
              <input type="text" size='13' name="thruDate" value="${currentCustomTimePeriod.thruDate?string("yyyy-MM-dd")}"<#if hasntStarted> class="alert"</#if> />
            </td>
            <td>
              <button class="btn btn-mini btn-primary" type="submit">
              <i class="icon-ok"></i>
              ${uiLabelMap.CommonUpdate}
              </button>
              <a class="btn btn-mini btn-danger " href='<@ofbizUrl>deleteCustomTimePeriod?customTimePeriodId=${currentCustomTimePeriod.customTimePeriodId}</@ofbizUrl>'>
              &nbsp${uiLabelMap.CommonDelete}</a>
            </td>
          </tr>
      </table>
      </div>
        </form>
        </div>
    <#else>
      <div class="widget-body"><p class="alert alert-info">${uiLabelMap.AccountingNoCurrentCustomTimePeriodSelected}</p></div>
    </#if>
  </div>
  <div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
    <h4>${uiLabelMap.AccountingChildPeriods}</h4>
      <span class="widget-toolbar>
      </span>
      <br class="clear"/>
    </div>
    <#if customTimePeriods?has_content>
    <div class="widget-body" >
    <div style="overflow-x: scroll !important; border: 0.5px solid rgb(204, 204, 204) !important; width:100%; ">
      <table class="table table-striped table-bordered table-hover dataTable" cellspacing="0">
        <tr class="header-row">
          <td>${uiLabelMap.CommonId}</td>
          <td>${uiLabelMap.CommonParent}</td>
          <td>${uiLabelMap.AccountingOrgPartyId}</td>
          <td>${uiLabelMap.AccountingPeriodType}</td>
          <td>${uiLabelMap.CommonNbr}</td>
          <td>${uiLabelMap.AccountingPeriodName}</td>
          <td>${uiLabelMap.CommonFromDate}</td>
          <td>${uiLabelMap.CommonThruDate}</td>
          <td>&nbsp;</td>
        </tr>
        <#assign line = 0>
        <#list customTimePeriods as customTimePeriod>
          <#assign line = line + 1>
          <#assign periodType = customTimePeriod.getRelatedOne("PeriodType", true)>
          <tr>
            <form method="post" action='<@ofbizUrl>updateCustomTimePeriod</@ofbizUrl>' name='lineForm${line}'>
              <input type="hidden" name="findOrganizationPartyId" value="${findOrganizationPartyId?if_exists}" />
              <input type="hidden" name="currentCustomTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
              <input type="hidden" name="customTimePeriodId" value="${customTimePeriodId?if_exists}" />
            <td>${customTimePeriod.customTimePeriodId}</td>
            <td>
              <select name="parentPeriodId">
                <option value=''>&nbsp;</option>
                <#list allCustomTimePeriods as allCustomTimePeriod>
                  <#assign allPeriodType = allCustomTimePeriod.getRelatedOne("PeriodType", true)>
                  <#assign isDefault = false>
                  <#if (currentCustomTimePeriod.parentPeriodId)?exists>
                    <#if currentCustomTimePeriod.customTimePeriodId = allCustomTimePeriod.customTimePeriodId>
                      <#assign isDefault = true>
                    </#if>
                  </#if>
                  <option value='${allCustomTimePeriod.customTimePeriodId}'<#if isDefault> selected="selected"</#if>>
                    ${allCustomTimePeriod.organizationPartyId}
                    <#if allPeriodType??> ${allPeriodType.description}: </#if>
                    ${allCustomTimePeriod.periodNum}
                    [${allCustomTimePeriod.customTimePeriodId}]
                  </option>
                </#list>
              </select>
            </td>
            <td><input type="text" size='12' name="organizationPartyId" value="${customTimePeriod.organizationPartyId?if_exists}" /></td>
            <td>
              <select name="periodTypeId">
                <#list periodTypes as periodType>
                  <#assign isDefault = false>
                  <#if (customTimePeriod.periodTypeId)?exists>
                    <#if customTimePeriod.periodTypeId = periodType.periodTypeId>
                     <#assign isDefault = true>
                    </#if>
                  </#if>
                  <option value='${periodType.periodTypeId}'<#if isDefault> selected="selected"</#if>>${periodType.description} [${periodType.periodTypeId}]</option>
                </#list>
              </select>
            </td>
            <td><input type="text" size='4' name="periodNum" value="${customTimePeriod.periodNum?if_exists}" /></td>
            <td><input type="text" size='10' name="periodName" value="${customTimePeriod.periodName?if_exists}" /></td>
            <td>
              <#assign hasntStarted = false>
              <#assign compareDate = customTimePeriod.getDate("fromDate")>
              <#if compareDate?has_content>
                <#if nowTimestamp.before(compareDate)><#assign hasntStarted = true></#if>
              </#if>
              <input type="text" size='13' name="fromDate" value="${customTimePeriod.fromDate?if_exists}"<#if hasntStarted> class="alert"</#if> />
            </td>
            <td>
              <#assign hasExpired = false>
              <#assign compareDate = customTimePeriod.getDate("thruDate")>
              <#if compareDate?has_content>
                <#if nowTimestamp.after(compareDate)><#assign hasExpired = true></#if>
              </#if>
              <input type="text" size='13' name="thruDate" value="${customTimePeriod.thruDate?if_exists}"<#if hasExpired> class="alert"</#if> />
             </td>
             <td class="button-col">
              <button class="btn btn-primary btn-mini icon-ok open-sans" type="submit" >
              ${uiLabelMap.CommonUpdate}
              </button>
            </td>
            <td class="button-col">
              <a class="btn btn-mini btn-danger icon-trash open-sans" href='<@ofbizUrl>deleteCustomTimePeriod?customTimePeriodId=${customTimePeriod.customTimePeriodId?if_exists}&amp;currentCustomTimePeriodId=${currentCustomTimePeriodId?if_exists}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>'>
              &nbsp${uiLabelMap.CommonDelete}</a>
            </td>
            <td class="button-col">
              <a class="btn btn-mini btn-primary open-sans" href='<@ofbizUrl>EditCustomTimePeriod?currentCustomTimePeriodId=${customTimePeriod.customTimePeriodId?if_exists}&amp;findOrganizationPartyId=${findOrganizationPartyId?if_exists}</@ofbizUrl>'>
              &nbsp${uiLabelMap.CommonSetAsCurrent}</a>
            </td>
            </form>
          </tr>
        </#list>
      </table>
      </div>
      </div>
    <#else>
      <div class="widget-body"><p class="alert alert-info">${uiLabelMap.AccountingNoChildPeriodsFound}</p></div>
    </#if>
  </div>
  <div class="widget-box transparent no-bottom-border">
    <div class="widget-header">
    <h4>${uiLabelMap.AccountingAddCustomTimePeriod}</h4>
      <span class="widget-toolbar">
      </span>
      <br class="clear"/>
    </div>
    <div class="widget-body">
    
      <form method="post" action="<@ofbizUrl>createCustomTimePeriod</@ofbizUrl>" name="createCustomTimePeriodForm">
        <input type="hidden" name="findOrganizationPartyId" value="${findOrganizationPartyId?if_exists}" />
        <input type="hidden" name="currentCustomTimePeriodId" value="${currentCustomTimePeriodId?if_exists}" />
        <input type="hidden" name="useValues" value="true" />
        <div class="divEditCustomTimePeriod">
        <table style="width: 100%">
        <tr>
        <td style="padding-bottom:10px;width:50%;"> 
          <span class="control-label">${uiLabelMap.CommonParent}</span>
          
          </td>
          <td  class="floatRightTableContent">
          <select name="parentPeriodId">
            <option value=''>&nbsp;</option>
            <#list allCustomTimePeriods as allCustomTimePeriod>
                <#assign allPeriodType = allCustomTimePeriod.getRelatedOne("PeriodType", true)>
              <#if currentCustomTimePeriod?exists>
                <#if currentCustomTimePeriod.customTimePeriodId = allCustomTimePeriod.customTimePeriodId>
                  <#assign isDefault = true>
                </#if>
              </#if>
              <option value="${allCustomTimePeriod.customTimePeriodId}"<#if isDefault> selected="selected"</#if>>
                ${allCustomTimePeriod.organizationPartyId}
                <#if (allCustomTimePeriod.parentPeriodId)?exists>Par:${allCustomTimePeriod.parentPeriodId}</#if>
                <#if allPeriodType??> ${allPeriodType.description}:</#if>
                ${allCustomTimePeriod.periodNum}
                [${allCustomTimePeriod.customTimePeriodId}]
              </option>
            </#list>
          </select>
          </td>
          </tr>
          </table>
        </div>
        <div class="divEditCustomTimePeriod">
        <table style="width: 100%">
        <thread>
        <tr>
        <td style="padding-bottom:10px;width:50%;">
        	<span>${uiLabelMap.AccountingOrgPartyId}</span>
        </td>
        
        <td  class="floatRightTableContent">
        	<input type="text" size='20' name='organizationPartyId' />
        </td>  
        <tr>
        <td style="padding-bottom:10px;width:50%;">
        	 <span>${uiLabelMap.AccountingPeriodType}</span>
        </td>
        <td  class="floatRightTableContent">
        	 <select name="periodTypeId">
            <#list periodTypes as periodType>
              <#assign isDefault = false>
              <#if newPeriodTypeId?exists>
                <#if newPeriodTypeId = periodType.periodTypeId>
                  <#assign isDefault = true>
                </#if>
              </#if>
              <option value="${periodType.periodTypeId}" <#if isDefault>selected="selected"</#if>>${periodType.description} [${periodType.periodTypeId}]</option>
            </#list>
          </select>
        </td> 
        </tr>
        <tr>
        <td style="padding-bottom:10px;width:50%;">
        	 <span>${uiLabelMap.AccountingPeriodNumber}</span>
        </td>
        <td  class="floatRightTableContent">
        	<input type="text" size='10' name='periodName' />
        </td>                  
          </tr>
          </thread>
          </table>
        </div>
        <div class="divEditCustomTimePeriod">
        <table style="width: 100%">
        	<tr>
        		<td style="padding-bottom:10px;width:50%;">
          			<span>${uiLabelMap.CommonFromDate}</span>
          		</td>
          		<td  class="floatRightTableContent">
          			<input type="text" size='14' name='fromDate' />
          		</td>
         	</tr>
          	<tr>
          		<td style="padding-bottom:10px;width:50%;">
          			<span>${uiLabelMap.CommonThruDate}</span>
          		</td>
          		<td class="floatRightTableContent">
          			<input type="text" size='14' name='thruDate' />
          		</td>
          	</tr>
          	<tr>
          	<td>
          	</td>
          		<td style="width: 100%">
          			<button type="submit" class="btn btn-small btn-primary">
          				<i class="icon-ok"></i>
         	 		${uiLabelMap.CommonAdd}
         	 	</button>
         	 	</td>
          	</tr>
          </table>
        </div>
      </form>
    </div>
  </div>
<#else>
  <p class="alert alert-info">${uiLabelMap.AccountingPermissionPeriod}.</p>
</#if>
