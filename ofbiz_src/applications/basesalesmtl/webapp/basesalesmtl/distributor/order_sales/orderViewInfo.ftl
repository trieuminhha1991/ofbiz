<#assign hasPermissionEdit = hasOlbEntityPermission("SALESORDER", "UPDATE")/>
<style type="text/css">
	.profile-user-info-striped .profile-info-value {
  		border-top: 1px solid #ddd;
	}
	.profile-user-info-striped .profile-info-name {
  		color: #FFF;
  		background: none repeat scroll 0 0 #438eb9;
	}
	.profile-user-info-striped {
	  	border: 1px solid #ddd;
	}
</style>
<#assign listPriority = delegator.findByAnd("Enumeration", {"enumTypeId": "ORDER_PRIORITY"}, ["sequenceId"], false)/>
<script type="text/javascript">
	var priorityData = [
	<#if listPriority?exists>
		<#list listPriority as priorityItem>
		{	enumId: "${priorityItem.enumId}",
			enumCode: "${priorityItem.enumCode?if_exists}",
			description: "${StringUtil.wrapString(priorityItem.description?default(""))}"
		},
		</#list>
	</#if>
	];
</script>
<div id="orderinfo-tab" class="tab-pane<#if activeTab?exists && activeTab == "orderinfo-tab"> active</#if>">
	<div class="row-fluid">
		<div class="span7">
			<h4 class="smaller green" style="display:inline-block">
				${uiLabelMap.BSOrderGeneralInfo}
			</h4>
			
			<div class="profile-user-info profile-user-info-striped" style="margin-left:0;margin-right:0">
				<#--
				<div class="profile-info-row">
					<div class="profile-info-name"> Username </div>
					<div class="profile-info-value">
						<span class="editable" id="username">alexdoe</span>
					</div>
				</div>
				-->
				<#if orderHeader.orderName?has_content>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.OrderOrderName}</div>
						<div class="profile-info-value">
							<span>${orderHeader.orderName?if_exists}</span>
						</div>
					</div>
				</#if>
				<#-- order status history -->
				<div class="profile-info-row">
					<div class="profile-info-name">${uiLabelMap.OrderStatusHistory}</div>
					<div class="profile-info-value">
						<span class="current-status">${uiLabelMap.OrderCurrentStatus}: ${currentStatus.get("description",locale)}</span>
						<#if orderHeaderStatuses?has_content>
		                  	<hr style="margin: 5px 0"/>
		                  	<#list orderHeaderStatuses as orderHeaderStatus>
		                    	<#assign loopStatusItem = orderHeaderStatus.getRelatedOne("StatusItem", false)>
		                    	<#--<#assign userlogin = orderHeaderStatus.getRelatedOne("UserLogin", false)>
		                    		${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}-->
		                    	<div class="row-fluid">
	                    			${loopStatusItem.get("description",locale)} - [${orderHeaderStatus.statusUserLogin}] - 
	                    				<#if orderHeaderStatus.statusDatetime?has_content>
		                    				<div class="time" style="display:inline-block;font-size:12px">
												<i class="icon-time"></i>
												<span>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeaderStatus.statusDatetime, "dd/MM/yyyy HH:mm:ss", locale, timeZone)?default("00/00/0000 00:00:00")}</span>
											</div>
		                    			</#if>
		                    	</div>
		                  	</#list>
		                </#if>
					</div>
				</div>
				<div class="profile-info-row">
					<div class="profile-info-name">${uiLabelMap.BSCreateDate}</div>
					<div class="profile-info-value">
						<span><#if orderHeader.orderDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</#if></span>
					</div>
				</div>
				<#--
				<div class="profile-info-row">
					<div class="profile-info-name">${uiLabelMap.CommonCurrency}</div>
					<div class="profile-info-value">
						<span>${orderHeader.currencyUom?default("???")}</span>
					</div>
				</div>
				-->
				<#if orderHeader.internalCode?has_content>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.OrderInternalCode}</div>
						<div class="profile-info-value">
							<span>${orderHeader.internalCode}</span>
						</div>
					</div>
				</#if>
				<#--
				<div class="profile-info-row">
					<div class="profile-info-name">${uiLabelMap.OrderSalesChannel}</div>
					<div class="profile-info-value">
						<span>
						<#if orderHeader.salesChannelEnumId?has_content>
		                    <#assign channel = orderHeader.getRelatedOne("SalesChannelEnumeration", false)>
		                    ${(channel.get("description",locale))?default("N/A")}
	                  	<#else>
	                    	${uiLabelMap.CommonNA}
	                  	</#if>
						</span>
					</div>
				</div>
				<#if productStore?has_content>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.OrderProductStore}</div>
						<div class="profile-info-value">
							${productStore.storeName!} [${productStore.productStoreId}]
						</div>
					</div>
				</#if>
				-->
				<#--<tr>
	              	<td align="right" valign="top" >&nbsp;${uiLabelMap.OrderOriginFacility}</td>
	              	<td valign="top">
	                  	<#if orderHeader.originFacilityId?has_content>
	                    	<a href="inventoryItemList?facilityId=${orderHeader.originFacilityId}${externalKeyParam}" target="facilitymgr" class="btn btn-mini btn-primary">${orderHeader.originFacilityId}</a>
	                  	<#else>
	                    	${uiLabelMap.CommonNA}
	                  	</#if>
	              	</td>
	            </tr>-->
	            <div class="profile-info-row">
					<div class="profile-info-name">${uiLabelMap.CommonCreatedBy}</div>
					<div class="profile-info-value">
						<span>
							<#if orderHeader.createdBy?has_content>
		                    	${orderHeader.createdBy}<#if createdByPartyName?exists> - ${createdByPartyName}</#if>
		                 	<#else>
		                    	${uiLabelMap.CommonNotSet}
		                  	</#if>
						</span>
					</div>
				</div>
				<#if orderItem.cancelBackOrderDate?exists>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.FormFieldTitle_cancelBackOrderDate}</div>
						<div class="profile-info-value">
							<span><#if orderItem.cancelBackOrderDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderItem.cancelBackOrderDate, "", locale, timeZone)!}</#if></span>
						</div>
					</div>
				</#if>
				<#if distributorId?exists>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.OrderDistributor}</div>
						<div class="profile-info-value">
							<span>
								<#assign distPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", distributorId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
		                  		${distPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
		                  	</span>
						</div>
					</div>
				</#if>
				<#if affiliateId?exists>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.OrderAffiliate}</div>
						<div class="profile-info-value">
							<span>
								<#assign affPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", affiliateId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
		                  		${affPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
							</span>
						</div>
					</div>
				</#if>
				<#if orderContentWrapper?exists && orderContentWrapper.get("IMAGE_URL")?has_content>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.OrderImage}</div>
						<div class="profile-info-value">
							<a href="<@ofbizUrl>viewimage?orderId=${orderId}&amp;orderContentTypeId=IMAGE_URL</@ofbizUrl>" target="_orderImage" class="btn btn-mini btn-primary">${uiLabelMap.OrderViewImage}</a>
						</div>
					</div>
				</#if>
				<#--
				<#if "SALES_ORDER" == orderHeader.orderTypeId>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.FormFieldTitle_priority}</div>
						<div class="profile-info-value">
							<div id="spanOrderReservationPriority">
								<span>
									<#if orderHeader.priority?exists && listPriority?exists>
		                    			<#list listPriority as priorityItem>
		                    				<#if orderHeader.priority == priorityItem.enumId>
		                    					${priorityItem.get("description", locale)}
		                    				</#if>
		                    			</#list>
									<#else>
										${uiLabelMap.CommonNormal}
									</#if>
								</span>
								<#if hasPermissionEdit><a href="javascript:orderCommon.openEditPriority();" style="margin-left:5px"><i class="fa fa-pencil-square"></i></a></#if>
							</div>
							<#if hasPermissionEdit>
							<form id="setOrderReservationPriority" name="setOrderReservationPriority" method="post" action="<@ofbizUrl>setOrderReservationPriority</@ofbizUrl>" style="display:none">
		                 		<input type = "hidden" name="orderId" value="${orderId}"/>
		                    	<select name="priority" style="margin-bottom:0; font-size:13px;padding: 2px 6px;height: 26px;">
		                    		<#if listPriority?exists>
		                    			<#list listPriority as priorityItem>
		                    				<option value="${priorityItem.enumId}" <#if (orderHeader.priority)?if_exists == priorityItem.enumId>selected="selected" </#if>>${StringUtil.wrapString(priorityItem.get("description", locale))}</option>
		                    			</#list>
		                    		</#if>
		                    	</select>
		                		<a href="javascript:orderCommon.saveEditPriority(priorityData);" id="btnSavePriority" data-rel="tooltip" title="${uiLabelMap.CommonUpdate}: ${uiLabelMap.FormFieldTitle_priority}" data-placement="bottom" class="btn btn-mini btn-info"><i class="fa-floppy-o"></i></a>
		                		<a href="javascript:orderCommon.cancelEditPriority();" id="btnCancelSavePriority" data-rel="tooltip" title="${uiLabelMap.BSCancel}" data-placement="bottom" class="btn btn-mini"><i class="icon-remove"></i></a>
		                	</form>
		                	</#if>
						</div>
					</div>
				</#if>
				-->
				<#--
		        <tr>
		          	<td align="right" valign="top" >&nbsp;${uiLabelMap.AccountingInvoicePerShipment}</td>
		          	<td valign="top">
		             	<form name="setInvoicePerShipment" method="post" action="<@ofbizUrl>setInvoicePerShipment</@ofbizUrl>">
		             		<input type = "hidden" name="orderId" value="${orderId}"/>
		            		<select name="invoicePerShipment">
		              			<option value="Y" <#if (orderHeader.invoicePerShipment)?if_exists == "Y">selected="selected" </#if>>${uiLabelMap.CommonYes}</option>
		              			<option value="N" <#if (orderHeader.invoicePerShipment)?if_exists == "N">selected="selected" </#if>>${uiLabelMap.CommonNo}</option>
		            		</select>
		            		<button type="submit" class="btn btn-primary btn-mini margin-top-nav-10">
		            			<i class="icon-ok"></i>${uiLabelMap.CommonUpdate}
		        			</button>
		            	</form>
		          	</td>
		        </tr>
		        -->
		        <#--
		        <#if hasPermissionEdit>
			        <#if orderHeader.isViewed?has_content && orderHeader.isViewed == "Y">
				        <div class="profile-info-row" id="isViewed" style="display: none;">
							<div class="profile-info-name">${uiLabelMap.OrderMarkViewed}</div>
							<div class="profile-info-value">
								<form id="orderViewed" action="">
									<input type="checkbox" name="checkViewed" id="checkViewed" onclick="javascript:orderCommon.markOrderViewed();"/>
									<span class="lbl" style="vertical-align: bottom;">&nbsp;${uiLabelMap.BSMark}</span>
			                  		<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			                  		<input type="hidden" name="isViewed" value="Y"/>
			                	</form>
							</div>
						</div>
						<div class="profile-info-row" id="viewed">
							<div class="profile-info-name">${uiLabelMap.OrderViewed}</div>
							<div class="profile-info-value">
								<span style="margin-right:10px; display:inline-block">${uiLabelMap.CommonYes}</span>
			                	<form id="orderUnViewed" action="" style="display:inline-block">
		                  			<a href="javascript:orderCommon.markOrderUnViewed();" data-rel="tooltip" title="${uiLabelMap.BSUnMark}" data-placement="bottom">
		                  				<i class="fa-times-circle"></i>
		                  			</a>
							  		<input type="hidden" name="checkViewed" value=""/>
			                  		<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			                  		<input type="hidden" name="isViewed" value="N"/>
			                	</form>
							</div>
						</div>
					<#else>	
						<div class="profile-info-row" id="isViewed">
							<div class="profile-info-name">${uiLabelMap.OrderMarkViewed}</div>
							<div class="profile-info-value">
								<form id="orderViewed" action="">
									<input type="checkbox" name="checkViewed" id="checkViewed" onclick="javascript:orderCommon.markOrderViewed();"/>
									<span class="lbl" style="vertical-align: bottom;">&nbsp;${uiLabelMap.BSMark}</span>
			                  		<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			                  		<input type="hidden" name="isViewed" value="Y"/>
			                	</form>
							</div>
						</div>
						<div class="profile-info-row" id="viewed" style="display: none;">
							<div class="profile-info-name">${uiLabelMap.OrderViewed}</div>
							<div class="profile-info-value">
								<span style="margin-right:10px;display:inline-block">${uiLabelMap.CommonYes}</span>
			                	<form id="orderUnViewed" action="" style="display:inline-block">
			                  		<a href="javascript:orderCommon.markOrderUnViewed();" data-rel="tooltip" title="${uiLabelMap.BSUnMark}" data-placement="bottom">
		                  				<i class="fa-times-circle"></i>
		                  			</a>
							  		<input type="hidden" name="checkViewed" value=""/>
			                  		<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
			                  		<input type="hidden" name="isViewed" value="N"/>
			                	</form>
							</div>
						</div>
					</#if>
				<#else>
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.OrderMarkViewed}</div>
						<div class="profile-info-value">
							<span class="lbl" style="vertical-align: bottom;">${uiLabelMap.BSMark}</span>
						</div>
					</div>
				</#if>
		        -->
			</div><!--.profile-user-info.profile-user-info-striped-->
		</div><!--.span7-->
		<div class="span5">
			<h4 class="smaller green" style="display:inline-block">
				${uiLabelMap.OrderContactInformation}
			</h4>
			<#if displayParty?has_content || orderContactMechValueMaps?has_content>
				<div class="profile-user-info profile-user-info-striped" style="margin-left:0;margin-right:0">
					<div class="profile-info-row">
						<div class="profile-info-name">${uiLabelMap.CommonName}</div>
						<div class="profile-info-value">
							<#if displayParty?has_content>
		                		<#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
		                		${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
		              		</#if>
		              		<#if partyId?exists>
		                		&nbsp;(${partyId})&nbsp;&nbsp;&nbsp;<#--<a href="${customerDetailLink?if_exists}${partyId}${externalKeyParam}" target="partymgr">${partyId}</a>-->
		                		<#if orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL">
		                			<div style="display:inline-block;">
										<#if hasOlbEntityPermission("SALESORDER", "CREATE")>
			                				<a href="<@ofbizUrl>newSalesOrder?partyId=${partyId}</@ofbizUrl>">
			                					<i class="fa-plus-circle"></i>
			                					${uiLabelMap.BSCreateNewOrder}
			                				</a>&nbsp;&nbsp;&nbsp;
			                			</#if>
			                   			<a href="<@ofbizUrl>listSalesOrderDis?partyId=${partyId}</@ofbizUrl>">
			                   				<i class="fa-search-plus"></i>${uiLabelMap.OrderOtherOrders}
			                   			</a>
                					</div>
                					<#--
                						<a href="javascript:document.searchOtherOrders.submit()" target="_blank">
			                   				<i class="fa-search-plus"></i>${uiLabelMap.OrderOtherOrders}
			                   			</a>
			                   			<form name="searchOtherOrders" method="post" action="<@ofbizUrl>searchorders</@ofbizUrl>">
						                    <input type="hidden" name="lookupFlag" value="Y"/>
						                    <input type="hidden" name="hideFields" value="Y"/>
						                    <input type="hidden" name="partyId" value="${partyId}" />
						                    <input type="hidden" name="viewIndex" value="1"/>
						                    <input type="hidden" name="viewSize" value="20"/>
					                  	</form>
                					-->
				                  	<div style="clear:both;"></div>
		                		</#if>
								
								<#assign displayPartyPhones = delegator.findByAnd("PartyAndTelecomNumberOrder", {"partyId" : partyId}, null, false)!/>
								<#if displayPartyPhones?has_content>
									${uiLabelMap.BSPhoneShipping}: 
									<#list displayPartyPhones as displayPartyPhone>
										${displayPartyPhone.contactNumber?string}<#if displayPartyPhone_has_next>, </#if>
									</#list>
								</#if>
		              		</#if>
						</div>
					</div>
					<#list orderContactMechValueMaps as orderContactMechValueMap>
			          	<#assign contactMech = orderContactMechValueMap.contactMech>
			          	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
						<div class="profile-info-row">
							<div class="profile-info-name">${contactMechPurpose.get("description",locale)}</div>
							<div class="profile-info-value">
								<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
			                		<#assign postalAddress = orderContactMechValueMap.postalAddress>
			                		<#if postalAddress?has_content>
			                			<div class="row-fluid">
			                				<div class="span12">
					                     		<div id="displayUpdateOrderContactMech">
					                     			${setContextField("postalAddress", postalAddress)}
					                     			${screens.render("component://party/widget/partymgr/PartyScreens.xml#postalAddressHtmlFormatter")}
					                     		</div>
					                     		<#--<div id="divUpdateOrderContactMech" style="display:none">
					                     			<@updateOrderContactMech orderHeader=orderHeader?if_exists contactMechTypeId=contactMech.contactMechTypeId 
				                						contactMechList=postalContactMechList?if_exists contactMechPurposeTypeId=contactMechPurpose.contactMechPurposeTypeId?if_exists 
				                						contactMechAddress=postalAddress?if_exists title=contactMechPurpose.get("description",locale)/>
					                     		</div>-->
				                			</div>
				                			<#--<div class="span1">
				                				<a href="javascript:openEditOrderContactMech();" id="btnEditOrderContactMech" style="margin-left:5px"><i class="fa fa-pencil-square"></i></a>
				                			</div>-->
			                			</div>
			                		</#if>
			              		<#elseif contactMech.contactMechTypeId == "TELECOM_NUMBER">
			                		<#assign telecomNumber = orderContactMechValueMap.telecomNumber>
			                		<div>
			                  			${telecomNumber.countryCode?if_exists}
			                  			<#if telecomNumber.areaCode?exists>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber}
		                  				<#--<#if partyContactMech.extension?exists>ext&nbsp;${partyContactMech.extension}</#if>-->
		                  				<#if !telecomNumber.countryCode?exists || telecomNumber.countryCode == "011" || telecomNumber.countryCode == "1">
		                    				<a target="_blank" href="${uiLabelMap.CommonLookupAnywhoLink}" class="btn btn-mini btn-primary">${uiLabelMap.CommonLookupAnywho}</a>
		                   					<a target="_blank" href="${uiLabelMap.CommonLookupWhitepagesTelNumberLink}" class="btn btn-mini btn-primary">${uiLabelMap.CommonLookupWhitepages}</a>
		                  				</#if>
	            					</div>
			                		<#--<@updateOrderContactMech orderHeader=orderHeader?if_exists contactMechTypeId=contactMech.contactMechTypeId contactMechList=telecomContactMechList?if_exists contactMechPurposeTypeId=contactMechPurpose.contactMechPurposeTypeId?if_exists contactMechAddress=telecomNumber?if_exists />-->
			              		<#elseif contactMech.contactMechTypeId == "EMAIL_ADDRESS">
			                		<div>
			                 	 		${contactMech.infoString}
			                  			<#--<#if security.hasEntityPermission("ORDERMGR", "_SEND_CONFIRMATION", session)>
			                     			(<a href="<@ofbizUrl>confirmationmailedit?orderId=${orderId}&amp;partyId=${partyId}&amp;sendTo=${contactMech.infoString}</@ofbizUrl>" class="btn btn-mini btn-primary">${uiLabelMap.OrderSendConfirmationEmail}</a>)
			                  			<#else>
					                     	<a href="mailto:${contactMech.infoString}" class="btn btn-mini btn-primary">(${uiLabelMap.OrderSendEmail})</a>
					                  	</#if>-->
			                		</div>
			                		<#--<@updateOrderContactMech orderHeader=orderHeader?if_exists contactMechTypeId=contactMech.contactMechTypeId contactMechList=emailContactMechList?if_exists contactMechPurposeTypeId=contactMechPurpose.contactMechPurposeTypeId?if_exists contactMechAddress=contactMech?if_exists />-->
			              		<#elseif contactMech.contactMechTypeId == "WEB_ADDRESS">
			                		<div>
			                  			${contactMech.infoString}
			                  			<#assign openString = contactMech.infoString>
			                  			<#if !openString?starts_with("http") && !openString?starts_with("HTTP")>
			                    			<#assign openString = "http://" + openString>
			                  			</#if>
			                  			<a target="_blank" href="${openString}" class="btn btn-mini btn-primary">(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
			                		</div>
			              		<#else>
			                		<div>
			                  			${contactMech.infoString?if_exists}
			                		</div>
			              		</#if>
							</div>
						</div>
					</#list>
				</div><!--.profile-user-info.profile-user-info-striped-->
			</#if>
		</div><!--.span5-->
	</div><!--.row-fluid-->
	<div class="row-fluid">
		<div class="span12">
			<#if orderHeader?has_content>
		    	<div id="order-notes-container">
		    		${setRequestAttribute("orderId", orderHeader.orderId?default(""))}
		    		${screens.render("component://basesalesmtl/widget/distributor/OrderScreens.xml#OrderSalesOrderNoteAjax")}
		    	</div>
			</#if>
		</div>
	</div><!--.row-fluid-->
</div>
