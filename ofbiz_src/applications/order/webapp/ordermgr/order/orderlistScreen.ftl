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

<#-- order list -->
<div id="orderLookup" >
	<div class="row-fluid">
    <div class="widget-main">
      <form method="post" name="findorder" style="margin: -12px !important" action="<@ofbizUrl>orderlist</@ofbizUrl>">
        <input type="hidden" name="changeStatusAndTypeState" value="Y" />
          <#--input type="checkbox" name="viewsent" value="Y" <#if state.hasStatus('viewsent')>checked="checked"</#if> />${uiLabelMap.CommonSent}-->
                  
       <#--<table class="table" cellspacing='0'>
          <tr>
             <td align="right" style="font-weight:bold;font-size:13px" class="width-table-column10">${uiLabelMap.CommonStatus}</td>
            <td nowrap="nowrap">
                <div>
                    <label>
						<input type="checkbox" name="viewall" value="Y" onclick="javascript:setCheckboxes()" <#if state.hasAllStatus()>checked="checked"</#if>><span class="lbl">${uiLabelMap.CommonAll}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcreated" value="Y" <#if state.hasStatus('viewcreated')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCreated}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewprocessing" value="Y" <#if state.hasStatus('viewprocessing')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonProcessing}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewapproved" value="Y" <#if state.hasStatus('viewapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonApproved}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewhold" value="Y" <#if state.hasStatus('viewhold')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonHeld}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcompleted" value="Y" <#if state.hasStatus('viewcompleted')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCompleted}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewrejected" value="Y" <#if state.hasStatus('viewrejected')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonRejected}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcancelled" value="Y" <#if state.hasStatus('viewcancelled')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCancelled}</span>
					</label>
                </div>
            </td>
            	    	 <td style="width:50px;font-weight:bold;font-size:13px" align="right" class="width-table-column10">${uiLabelMap.CommonType}</td>
			            <td nowrap="nowrap">
			                <div>
			                    <label>
									<input type="checkbox" name="view_SALES_ORDER" value="Y" <#if state.hasType('view_SALES_ORDER')>checked="checked"</#if>/><span class="lbl">${descr_SALES_ORDER}</span>
								</label>
			                    <label>
									<input type="checkbox" name="view_PURCHASE_ORDER" value="Y" <#if state.hasType('view_PURCHASE_ORDER')>checked="checked"</#if>/><span class="lbl">${descr_PURCHASE_ORDER}</span>
								</label>
			                </div>
			            </td>
	            	
	            	
	            		 <td align="right" style="width:50px;font-weight:bold;font-size:13px" class="width-table-column10">${uiLabelMap.CommonFilter}</td>
					            <td nowrap="nowrap">
					                <div>
					                    <label>
											<input type="checkbox" name="filterInventoryProblems" value="Y"
					                        <#if state.hasFilter('filterInventoryProblems')>checked="checked"</#if>/>
					                        <span class="lbl">${uiLabelMap.OrderFilterInventoryProblems}</span>
										</label>
					                    <label>
											<input type="checkbox" name="filterAuthProblems" value="Y"
					                        <#if state.hasFilter('filterAuthProblems')>checked="checked"</#if>/>
					                        <span class="lbl">${uiLabelMap.OrderFilterAuthProblems}</span>
										</label>
					                </div>
					            </td>	
	            	 <td align="right" style="font-weight:bold;font-size:13px" class="width-table-column10">${uiLabelMap.CommonFilter} (${uiLabelMap.OrderFilterPOs})</td>
            <td nowrap="nowrap">
                <div>
                    <label>
						<input type="checkbox" name="filterPartiallyReceivedPOs" value="Y"
                        <#if state.hasFilter('filterPartiallyReceivedPOs')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterPartiallyReceivedPOs}</span>
					</label>
                    <label>
						<input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y"
                        <#if state.hasFilter('filterPOsOpenPastTheirETA')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</span>
					</label>
                    <label>
						<input type="checkbox" name="filterPOsWithRejectedItems" value="Y"
                        <#if state.hasFilter('filterPOsWithRejectedItems')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterPOsWithRejectedItems}</span>
					</label>
                </div>
            </td>
        </table>-->
        <div class="span12">
        	<div class="span6">
        		<div style="margin-left:250px">
        		<h4>${uiLabelMap.CommonStatus}</h4>
        		 <div >
                    <label>
						<input type="checkbox" name="viewall" value="Y" onclick="javascript:setCheckboxes()" <#if state.hasAllStatus()>checked="checked"</#if>><span class="lbl">${uiLabelMap.CommonAll}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcreated" value="Y" <#if state.hasStatus('viewcreated')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCreated}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewprocessing" value="Y" <#if state.hasStatus('viewprocessing')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonProcessing}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewapproved" value="Y" <#if state.hasStatus('viewapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonApproved}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewhold" value="Y" <#if state.hasStatus('viewhold')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonHeld}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcompleted" value="Y" <#if state.hasStatus('viewcompleted')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCompleted}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewrejected" value="Y" <#if state.hasStatus('viewrejected')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonRejected}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcancelled" value="Y" <#if state.hasStatus('viewcancelled')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCancelled}</span>
					</label>
                </div>
        		</div>
        	</div>
        	<div class="span6">
        		 <h4>${uiLabelMap.CommonType}<h4>
			            <div>
		                    <label>
								<input type="checkbox" name="view_SALES_ORDER" value="Y" <#if state.hasType('view_SALES_ORDER')>checked="checked"</#if>/><span class="lbl">${descr_SALES_ORDER}</span>
							</label>
		                    <label>
								<input type="checkbox" name="view_PURCHASE_ORDER" value="Y" <#if state.hasType('view_PURCHASE_ORDER')>checked="checked"</#if>/><span class="lbl">${descr_PURCHASE_ORDER}</span>
							</label>
		                </div>
		         
	            		 <h4>${uiLabelMap.CommonFilter}<h4>
			                <div>
			                    <label>
									<input type="checkbox" name="filterInventoryProblems" value="Y"
			                        <#if state.hasFilter('filterInventoryProblems')>checked="checked"</#if>/>
			                        <span class="lbl">${uiLabelMap.OrderFilterInventoryProblems}</span>
								</label>
			                    <label>
									<input type="checkbox" name="filterAuthProblems" value="Y"
			                        <#if state.hasFilter('filterAuthProblems')>checked="checked"</#if>/>
			                        <span class="lbl">${uiLabelMap.OrderFilterAuthProblems}</span>
								</label>
			                </div>
			             <h4> ${uiLabelMap.CommonFilter}(${uiLabelMap.OrderFilterPOs})</h4>
			             	    <div>
				                    <label>
										<input type="checkbox" name="filterPartiallyReceivedPOs" value="Y"
				                        <#if state.hasFilter('filterPartiallyReceivedPOs')>checked="checked"</#if>/>
				                        <span class="lbl">${uiLabelMap.OrderFilterPartiallyReceivedPOs}</span>
									</label>
				                    <label>
										<input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y"
				                        <#if state.hasFilter('filterPOsOpenPastTheirETA')>checked="checked"</#if>/>
				                        <span class="lbl">${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</span>
									</label>
				                    <label>
										<input type="checkbox" name="filterPOsWithRejectedItems" value="Y"
				                        <#if state.hasFilter('filterPOsWithRejectedItems')>checked="checked"</#if>/>
				                        <span class="lbl">${uiLabelMap.OrderFilterPOsWithRejectedItems}</span>
									</label>
				                </div>
				</div>
        </div>
        
        
        <div ><a href="javascript:document.findorder.submit()" class="btn btn-primary btn-small icon-search open-sans"style="margin-left:470px"> ${uiLabelMap.CommonFind}</a></div>
      </form>
    </div>
    </div>
    </div>
