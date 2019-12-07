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
<#escape x as x?xml>
	<fo:table>
	<fo:table-column column-width="45%"/>
	<fo:table-column column-width="55%"/>
	<fo:table-body>
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell>
				<fo:block number-columns-spanned="2" font-weight="bold">${orderHeader.getRelatedOne("OrderType", false).get("description",locale)}</fo:block>
			</fo:table-cell>
		</fo:table-row>
		
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell><fo:block margin-top="6px">${uiLabelMap.OrderDateOrdered}:</fo:block></fo:table-cell>
			<#assign dateFormat = Static["java.text.DateFormat"].LONG>
			<#assign orderDate = Static["java.text.DateFormat"].getDateInstance(dateFormat, locale).format(orderHeader.get("orderDate"))>
			<fo:table-cell><fo:block margin-top="6px">${orderDate}</fo:block></fo:table-cell>
		</fo:table-row>
		
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell><fo:block margin-top="6px">${uiLabelMap.BEOrderNumber}:</fo:block></fo:table-cell>
			<fo:table-cell><fo:block margin-top="6px">${orderId}</fo:block></fo:table-cell>
		</fo:table-row>
		
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell><fo:block margin-top="6px">${uiLabelMap.OrderCurrentStatus}:</fo:block></fo:table-cell>
			<fo:table-cell><fo:block margin-top="6px" font-weight="bold">${currentStatus.get("description",locale)}</fo:block></fo:table-cell>
		</fo:table-row>
		<#if orderItem.cancelBackOrderDate?exists>
		<fo:table-row border-bottom="1px solid #ccc">
			<fo:table-cell><fo:block margin-top="6px">${uiLabelMap.FormFieldTitle_cancelBackOrderDate}:</fo:block></fo:table-cell>
			<#assign dateFormat = Static["java.text.DateFormat"].LONG>
			<#assign cancelBackOrderDate = Static["java.text.DateFormat"].getDateInstance(dateFormat,locale).format(orderItem.get("cancelBackOrderDate"))>
			<fo:table-cell><#if cancelBackOrderDate?has_content><fo:block margin-top="6px">${cancelBackOrderDate}</fo:block></#if></fo:table-cell>
		</fo:table-row>
		</#if>
		</fo:table-body>
	</fo:table>
</#escape>
