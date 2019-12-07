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
<#if productPromoId?exists && productPromo?exists>
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <h3 class="header smaller lighter blue">${uiLabelMap.PageTitleEditProductPromoStores}</h3>
        </div>
        <div class="screenlet-body">
            <table cellspacing="0" class="table table-striped table-bordered table-hover dataTable">
                <tr class="header-row">
                    <td><b>${uiLabelMap.DelysPromoStoreNameId}</b></td>
                    <td><b>${uiLabelMap.DelysPromoFromDateTime}</b></td>
                    <td align="center"><b>${uiLabelMap.DelysPromoThruDateTimeSequence}</b></td>
                    <#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED"> 
                    	<td><b>&nbsp;</b></td>
                    </#if>
                </tr>
                <#assign line = 0>
                <#assign rowClass = "2">
                <#list productStorePromoAppls as productStorePromoAppl>
                <#assign line = line + 1>
                <#assign productStore = productStorePromoAppl.getRelatedOne("ProductStore", false)>
                <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
                    <td>
                    	<a href="<@ofbizUrl>editProductStore?productStoreId=${productStorePromoAppl.productStoreId}</@ofbizUrl>"><#-- class="btn btn-mini btn-primary"-->
                    		<#if productStore?exists>${(productStore.storeName)?if_exists}</#if> [${productStorePromoAppl.productStoreId}]
                    	</a>
                	</td>
                    <#assign hasntStarted = false>
                    <#if (productStorePromoAppl.getTimestamp("fromDate"))?exists && nowTimestamp.before(productStorePromoAppl.getTimestamp("fromDate"))> <#assign hasntStarted = true></#if>
                    <td <#if hasntStarted>style="color: red;"</#if>>
                    	<#if productStorePromoAppl.fromDate?exists>
                    		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productStorePromoAppl.fromDate, "dd/MM/yyyy - HH:mm:ss:SSS", locale, timeZone)!}
                		</#if>
                    </td>
                    <td align="center">
                        <#assign hasExpired = false>
                        <#if (productStorePromoAppl.getTimestamp("thruDate"))?exists && nowTimestamp.after(productStorePromoAppl.getTimestamp("thruDate"))> <#assign hasExpired = true></#if>
                        <#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
	                        <form method="post" action="<@ofbizUrl>promo_updateProductStorePromoAppl</@ofbizUrl>" name="lineForm${line}">
	                            <input type="hidden" name="productStoreId" value="${productStorePromoAppl.productStoreId}" />
	                            <input type="hidden" name="productPromoId" value="${productStorePromoAppl.productPromoId}" />
	                            <input type="hidden" name="fromDate" value="${productStorePromoAppl.fromDate}" />
	                            <#if hasExpired><#assign class="alert"></#if>
	                            <@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" className="${class!''}" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${(productStorePromoAppl.thruDate)?if_exists}" size="25" maxlength="30" id="thruDate_${productStorePromoAppl_index}" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>                            
	                            <button class="btn btn-mini btn-primary" type="submit"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>
	                        </form>
	                     <#else>
	                     	${productStorePromoAppl.thruDate?if_exists}   
                        </#if>
                    </td>
                    <#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
                    <td align="center">
                       <form method="post" action="<@ofbizUrl>promo_deleteProductStorePromoAppl</@ofbizUrl>">
                           <input type="hidden" name="productStoreId" value="${productStorePromoAppl.productStoreId}" />
                           <input type="hidden" name="productPromoId" value="${productStorePromoAppl.productPromoId}" />
                           <input type="hidden" name="fromDate" value="${productStorePromoAppl.fromDate}" />
                           <button class="btn btn-mini btn-danger" type="submit"><i class="icon-remove"></i>${uiLabelMap.CommonDelete}</button>
                       </form>
                    </td>
                    </#if>
                </tr>
                <#-- toggle the row color -->
                <#if rowClass == "2">
                    <#assign rowClass = "1">
                <#else>
                    <#assign rowClass = "2">
                </#if>
                </#list>
            </table>
        </div>
    </div>
    <#if productPromo.productPromoStatusId?exists && productPromo.productPromoStatusId == "PROMO_CREATED">
	    <div class="screenlet">
	        <div class="screenlet-title-bar">
	            <h3 class="header smaller lighter blue">${uiLabelMap.DelysPromoAddStorePromo}</h3>
	        </div>
	        <div class="screenlet-body">
	            <form method="post" action="<@ofbizUrl>promo_createProductStorePromoAppl</@ofbizUrl>" name="addProductPromoToCatalog" class="form-horizontal basic-custom-form">
	                <input type="hidden" name="productPromoId" value="${productPromoId}"/>
	                <input type="hidden" name="tryEntity" value="true"/>
	                <div class="control-group">
	                	<label class="control-label">${uiLabelMap.DelysPromoProductStoreName}</label>
		                <div class="controls">
			                <select name="productStoreId">
			                <#list productStores as productStore>
			                    <option value="${(productStore.productStoreId)?if_exists}">${(productStore.storeName)?if_exists} [${(productStore.productStoreId)?if_exists}]</option>
			                </#list>
			                </select>
		                </div>
	                </div>
	                <div class="control-group">
	                	<label class="control-label">${uiLabelMap.CommonFromDate}</label>
	                	<div class="controls">
	                		<@htmlTemplate.renderDateTimeField name="fromDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="fromDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
	                	</div>
	                </div>
	                <div class="control-group">
	                	<label class="control-label">${uiLabelMap.CommonThruDate}</label>
	                	<div class="controls">
	                		<@htmlTemplate.renderDateTimeField name="thruDate" event="" action="" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="" size="25" maxlength="30" id="thruDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
	                	</div>
	                </div>
	                <div class="control-group">
	                	<label class="control-group">&nbsp;</label>
	                	<div class="controls">
	                		<button class="btn btn-mini btn-primary" type="submit"><i class="icon-ok"></i>${uiLabelMap.CommonAdd}</button>
	                	</div>
	                </div>
	            </form>
	        </div>
	    </div>
    </#if>
</#if>