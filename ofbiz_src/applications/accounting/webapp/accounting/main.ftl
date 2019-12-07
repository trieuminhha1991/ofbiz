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
<div>
<table>
  <tr>
     <td colspan="3"><h2 class="header smaller lighter blue" style="margin-left: 15px">${uiLabelMap.AccountingAgreements}</h2></td>
  </tr>

  <tr>
     <td colspan="3">
<ul class="unstyled spaced">
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>FindAgreement</@ofbizUrl>">${uiLabelMap.AccountingAgreementAvailable}</a></li>
</ul>
<br />
     </td>
  </tr>
  <tr>
     <td colspan="3"><h2 class="header smaller lighter blue" style="margin-left: 15px">${uiLabelMap.AccountingBillingMenu}</h2></td>
  </tr>

  <tr>
     <td colspan="3">
<ul class="unstyled spaced">
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>FindBillingAccount</@ofbizUrl>">${uiLabelMap.CommonShow} ${uiLabelMap.AccountingCustomer} ${uiLabelMap.AccountingBillingAccount}</a></li>
</ul>
<br />
     </td>
  </tr>

  <tr>
     <td colspan="3"><h2 class="header smaller lighter blue" style="margin-left: 15px">${uiLabelMap.AccountingFixedAssets}</h2></td>
  </tr>

  <tr>
     <td colspan="3">
<ul class="unstyled spaced">
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>ListFixedAssets</@ofbizUrl>">${uiLabelMap.AccountingShowAllFixedAssets}</a></li>
</ul>
<br />
     </td>
  </tr>

  <tr>
     <td colspan="3"><h2 class="header smaller lighter blue" style="margin-left: 15px">${uiLabelMap.AccountingInvoicesMenu}</h2></td>
  </tr>

  <tr valign="top">
<td>
<ul class="unstyled spaced">
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>findInvoices?noConditionFind=Y&amp;lookupFlag=Y</@ofbizUrl>">${uiLabelMap.AccountingShowAllInvoices}</a></li>
</ul>
</td>

<td>
<ul class="unstyled spaced">
<#list invoiceTypes as invoiceType>
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>findInvoices?lookupFlag=Y&amp;invoiceTypeId=${invoiceType.invoiceTypeId}</@ofbizUrl>">${uiLabelMap.AccountingShowInvoices} ${invoiceType.get("description",locale)!invoiceType.invoiceTypeId}</a></li>
</#list>
</ul>
</td>
<td>
<ul class="unstyled spaced">
<#list invoiceStatus as status>
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>findInvoices?lookupFlag=Y&amp;statusId=${status.statusId}</@ofbizUrl>">${uiLabelMap.AccountingShowInvoices} ${status.get("description",locale)!status.statusId}</a></li>
</#list>
</ul>
</td>
</tr>

  <tr>
     <td colspan="3"><h2 class="header smaller lighter blue" style="margin-left: 15px">${uiLabelMap.AccountingPaymentsMenu}</h2></td>
  </tr>

<tr valign="top">
<td>
<ul class="unstyled spaced">
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>findPayments?noConditionFind=Y&amp;lookupFlag=Y</@ofbizUrl>">${uiLabelMap.AccountingShowAllPayments}</a></li>
</ul>
</td>
<td>

<ul class="unstyled spaced">
<#list paymentTypes as paymentType>
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>findPayments?lookupFlag=Y&amp;paymentTypeId=${paymentType.paymentTypeId}</@ofbizUrl>">${uiLabelMap.AccountingShowPayments} ${paymentType.get("description",locale)!paymentType.paymentTypeId}</a></li>
</#list>
</ul>
</td>


<td>
<ul class="unstyled spaced">
<#list paymentMethodTypes as paymentMethodType>
<li style="margin-left: 30px;"><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>findPayments?lookupFlag=Y&amp;paymentMethodTypeId=${paymentMethodType.paymentMethodTypeId}</@ofbizUrl>">${uiLabelMap.AccountingShowPayments} ${paymentMethodType.get("description",locale)!paymentMethodType.paymentMethodTypeId}</a></li>
</#list>
</ul>
</td>

<td>
<ul class="unstyled spaced">
<#list paymentStatus as status>
<li style="margin-left: 30px;" ><i class="icon-caret-right blue"></i><a class="btn btn-mini btn-info" href="<@ofbizUrl>findPayments?lookupFlag=Y&amp;statusId=${status.statusId}</@ofbizUrl>">${uiLabelMap.AccountingShowPayments} ${status.get("description",locale)!status.statusId}</a></li>
</#list>
</ul>
</td>
</tr>
</table>
</div>
