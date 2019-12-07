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

<#if techDataCalendar?has_content>
<div class="widget-box  transparent no-border-bottom" >
  <div class="widget-header">
     <h3 style="padding: 0px !important; margin: 0px !important;">${uiLabelMap.ManufacturingUpdateCalendar}</h3>
     <div style="float:right; padding-top:10px; padding-right:10px" ><a href="<@ofbizUrl>EditCalendar</@ofbizUrl>" class="icon-plus-sign open-sans">${uiLabelMap.ManufacturingNewCalendar}</a></div>
    <br class="clear"/>
  </div>
  <div class="widget-body">
  <form name="calendarform" style="margin-left:0;" method="post" action="<@ofbizUrl>UpdateCalendar</@ofbizUrl>">
    <input type="hidden" name="calendarId" value="${techDataCalendar.calendarId}" />
<#else>
<div class="widget-box no-border-bottom transparent">
  <div class="widget-header">
      <h3 style="padding: 0px !important; margin: 0px !important;">${uiLabelMap.ManufacturingCreateCalendar}</h3>
      <br class="clear"/>
  </div>
  <div class="widget-body">
  <div class="padding-top8 padding-bottom8 padding-left8">
  <!--<a href="<@ofbizUrl>EditCalendar</@ofbizUrl>" class="btn btn-primary btn-mini icon-plus-sign">  ${uiLabelMap.ManufacturingNewCalendar}</a>
  </div>-->
  <form name="calendarform" style="margin-left:0;" method="post" action="<@ofbizUrl>CreateCalendar</@ofbizUrl>">
</#if>
  <table class="basic-table " cellspacing="0" style="margin-top:30px">
    <#if techDataCalendar?has_content>
    <tr>
      <td width='16%' align='right' valign='top'>${uiLabelMap.ManufacturingCalendarId}</td>
      <td width="5">&nbsp;</td>
      <td width="84%" valign="top"><input type="text" size="12" name="calendarId" value="${calendarData.calendarId?if_exists}" disabled/><span class="tooltip">(${uiLabelMap.CommonNotModifRecreat})</span></td>
    </tr>
    <#else>
    <tr>
      <td width='16%' align='right' valign="top">${uiLabelMap.ManufacturingCalendarId}</td>
      <td width="5">&nbsp;</td>
      <td width="84%"><input type="text" size="12" name="calendarId" value="${calendarData.calendarId?if_exists}" /></td>
    </tr>
    </#if>
    <tr>
      <td width='16%' align='right' valign='top'>${uiLabelMap.CommonDescription}</td>
      <td width="5">&nbsp;</td>
      <td width="84%"><input type="text" size="40" name="description" value="${calendarData.description?if_exists}" /></td>
    </tr>
    <tr>
      <td width='16%' align='right' valign='top'>${uiLabelMap.ManufacturingCalendarWeekId}</td>
      <td width="5">&nbsp;</td>
      <td width="84%">
         <select name="calendarWeekId">
          <#list calendarWeeks as calendarWeek>
          <option value="${calendarWeek.calendarWeekId}" <#if calendarData?has_content && calendarData.calendarWeekId?default("") == calendarWeek.calendarWeekId>SELECTED</#if>>${(calendarWeek.get("description",locale))?if_exists}</option>
          </#list>
        </select>
      </td>
    </tr>
    <tr>
      <td width="16%" align="right" valign="top"></td>
      <td width="5">&nbsp;</td>
      <td width="84%"><button type="submit" class = "btn btn-small btn-info">
      <i class="icon-check"></i>
      ${uiLabelMap.CommonUpdate}
      </button>
      </td>
    </tr>
  </table>
  </form>
  </div>
</div>