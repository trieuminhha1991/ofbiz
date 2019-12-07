<script type="text/javascript">
    <!-- function to add extra info for Timestamp format -->
    function TimestampSubmit(obj) {
       reservStartStr = jQuery(obj).find("input[name='reservStartStr']");
       val1 = reservStartStr.val();
       reservStart = jQuery(obj).find("input[name='reservStart']");
       if (reservStartStr.val().length == 10) {
           reservStart.val(reservStartStr.val() + " 00:00:00.000000000");
       } else {
           reservStart.val(reservStartStr.val());
       }
       jQuery(obj).submit();
    }

    function callDocumentByPaginate(info) {
        var str = info.split('~');
        var checkUrl = '<@ofbizUrl>showShoppingListAjaxFired</@ofbizUrl>';
        if(checkUrl.search("http"))
            var ajaxUrl = '<@ofbizUrl>showShoppingListAjaxFired</@ofbizUrl>';
        else
            var ajaxUrl = '<@ofbizUrl>showShoppingListAjaxFiredSecure</@ofbizUrl>';
        //jQuerry Ajax Request
        jQuery.ajax({
            url: ajaxUrl,
            type: 'POST',
            data: {"shoppingListId" : str[0], "VIEW_SIZE" : str[1], "VIEW_INDEX" : str[2]},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
                jQuery('#div3').html(msg);
            }
        });
     }
</script>
<br />
<#macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
      <#if (viewIndexMax?int > 0)>
        <div class="product-prevnext">
            <#-- Start Page Select Drop-Down -->
            <select name="pageSelect" onchange="callDocumentByPaginate(this[this.selectedIndex].value);">
                <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int} ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
                <#list 0..viewIndexMax as curViewNum>
                     <option value="${shoppingListId?if_exists}~${viewSize}~${curViewNum?int + 1}">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
                </#list>
            </select>
            <#-- End Page Select Drop-Down -->
            <#if (viewIndex?int > 1)>
                <a href="javascript: void(0);" onclick="callDocumentByPaginate('${shoppingListId?if_exists}~${viewSize}~${viewIndex?int - 1}');" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
            </#if>
            <#if ((listSize?int - viewSize?int) > 0)>
                <span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
            </#if>
            <#if highIndex?int < listSize?int>
             | <a href="javascript: void(0);" onclick="callDocumentByPaginate('${shoppingListId?if_exists}~${viewSize}~${viewIndex?int + 1}');" class="buttontext">${uiLabelMap.CommonNext}</a>
            </#if>
        </div>
    </#if>
</#macro>
<style type="text/css">
	td {
	    padding: 5px;
	}
	table td{
		vertical-align:middle !important;
	}
	h3{
		margin-bottom:10px;
	}
</style>
<div id="jm-container" class="jm-col1-layout wrap not-breadcrumbs clearfix">
	<div class="main clearfix">
		<div id="jm-mainbody" class="clearfix">
			<div id="jm-main">
				<div class="inner clearfix">
					<div id="jm-current-content" class="clearfix">
					  <div class="page-title" style="border-bottom: 1px solid #ebebeb;">
					<div style="width:80%;float:left;">
					<h1 style="border:none;">${uiLabelMap.ObbWishLists}</h1></div>
					<div style="width:20%;float:left;padding: 8px 0 15px 0;text-align:right;"><a href="<@ofbizUrl>createEmptyShoppingList?productStoreId=${productStoreId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a></div>
					  </div>
						<div class="screenlet">
						    <div class="screenlet-body">
						        <#if shoppingLists?has_content>
						          <form name="selectShoppingList" method="post" action="<@ofbizUrl>editShoppingList</@ofbizUrl>">
						            <select name="shoppingListId" class="selectBox">
						              <#if shoppingList?has_content>
						                <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
						                <option value="${shoppingList.shoppingListId}">--</option>
						              </#if>
						              <#list shoppingLists as list>
						                <option value="${list.shoppingListId}">${list.listName}</option>
						              </#list>
						            </select>
						            &nbsp;&nbsp;
						            <a href="javascript:document.selectShoppingList.submit();" class="buttontext">${uiLabelMap.CommonEdit}</a>
						          </form>
						        <#else>
						          <div>${uiLabelMap.ObbNoShoppingListsCreate}.</div>
						          <a href="<@ofbizUrl>createEmptyShoppingList?productStoreId=${productStoreId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
						        </#if>
						    </div>
						</div>
						<hr color="#ebebeb" style="margin-top:20px;"/>
						<#if shoppingList?has_content>
						    <#if canView>
								<div class="screenlet">
								    <div class="screenlet-title-bar">
								        <div style="padding:10px; float: right;">
								          <form name= "createCustRequestFromShoppingList" method= "post" action= "<@ofbizUrl>createCustRequestFromShoppingList</@ofbizUrl>">
								            <input type= "hidden" name= "shoppingListId" value= "${shoppingList.shoppingListId}"/>
								            <a href='javascript:document.createCustRequestFromShoppingList.submit()'>${uiLabelMap.OrderCreateCustRequestFromShoppingList}</a>
								          </form>
								          <form name="createQuoteFromShoppingList" method="post" action="<@ofbizUrl>createQuoteFromShoppingList</@ofbizUrl>">
								            <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
								            <span style="margin-left:5px;margin-right:5px;">|</span><a href='javascript:document.createQuoteFromShoppingList.submit()'>${uiLabelMap.OrderCreateQuoteFromShoppingList}</a>
								          </form>
								          <span style="margin-left:5px;margin-right:5px;">|</span><a href="javascript:document.updateList.submit();" >${uiLabelMap.CommonSave}</a>
								        </div>
								        <h3>${uiLabelMap.ObbShoppingListDetail} - ${shoppingList.listName}</h3>
								    </div>
								    <div class="screenlet-body">
								        <form name="updateList" method="post" action="<@ofbizUrl>updateShoppingList</@ofbizUrl>">
								            <input type="hidden" class="input-text" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
								            <input type="hidden" class="input-text" name="partyId" value="${shoppingList.partyId?if_exists}"/>
								            <table border="0" width="100%" cellspacing="10" cellpadding="10">
								              <tr>
								                <td><label for="listName">${uiLabelMap.ObbListName}</label></td>
								                <td><input type="text" class="input-text" size="25" name="listName" id="listName" value="${shoppingList.listName}" />
								              </tr>
								              <tr>
								                <td><label for="description">${uiLabelMap.CommonDescription}</label></td>
								                <td><input type="text" class="input-text" size="70" name="description" id="description" value="${shoppingList.description?if_exists}" />
								              </tr>
								              <tr>
								                <td><label for="shoppingListTypeId">${uiLabelMap.OrderListType}</label></td>
								                <td>
								                  <select name="shoppingListTypeId" id="shoppingListTypeId" class="selectBox">
								                      <#if shoppingListType?exists>
								                      <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.get("description",locale)?default(shoppingListType.shoppingListTypeId)}</option>
								                      <option value="${shoppingListType.shoppingListTypeId}">--</option>
								                    </#if>
								                    <#list shoppingListTypes as shoppingListType>
								                      <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.get("description",locale)?default(shoppingListType.shoppingListTypeId)}</option>
								                    </#list>
								                  </select>
								                </td>
								              </tr>
								              <tr>
								                <td><label for="isPublic">${uiLabelMap.ObbPublic}</label></td>
								                <td>
								                  <select name="isPublic" id="isPublic" class="selectBox">
								                    <#if (((shoppingList.isPublic)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
								                    <#if (((shoppingList.isPublic)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
								                    <option></option>
								                    <option value="Y">${uiLabelMap.CommonY}</option>
								                    <option value="N">${uiLabelMap.CommonN}</option>
								                  </select>
								                </td>
								              </tr>
								              <tr>
								                <td><label for="isActive">${uiLabelMap.ObbActive}</label></td>
								                <td>
								                  <select name="isActive" id="isActive" class="selectBox">
								                    <#if (((shoppingList.isActive)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
								                    <#if (((shoppingList.isActive)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
								                    <option></option>
								                    <option value="Y">${uiLabelMap.CommonY}</option>
								                    <option value="N">${uiLabelMap.CommonN}</option>
								                  </select>
								                </td>
								              </tr>
								              <tr>
								                <td><label for="parentShoppingListId">${uiLabelMap.ObbParentList}</label></td>
								                <td>
								                  <select name="parentShoppingListId" id="parentShoppingListId" class="selectBox">
								                      <#if parentShoppingList?exists>
								                      <option value="${parentShoppingList.shoppingListId}">${parentShoppingList.listName?default(parentShoppingList.shoppingListId)}</option>
								                    </#if>
								                    <option value="">${uiLabelMap.ObbNoParent}</option>
								                    <#list allShoppingLists as newParShoppingList>
								                      <option value="${newParShoppingList.shoppingListId}">${newParShoppingList.listName?default(newParShoppingList.shoppingListId)}</option>
								                    </#list>
								                  </select>
								                  <#if parentShoppingList?exists>
								                    <a href="<@ofbizUrl>editShoppingList?shoppingListId=${parentShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGotoParent} (${parentShoppingList.listName?default(parentShoppingList.shoppingListId)})</a>
								                  </#if>
								                </td>
								              </tr>
								              <tr>
								                <td><div class="tableheadtext">&nbsp;</div></td>
								                <td>
								                  <a href="javascript:document.updateList.submit();" class="buttontext">${uiLabelMap.CommonSave}</a>
								                </td>
								              </tr>
								            </table>
								        </form>
								    </div>
								</div>
								<hr color="#ebebeb" style="margin-top:10px;"/>
								<#if shoppingListType?exists && shoppingListType.shoppingListTypeId == "SLT_AUTO_REODR">
								  <#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].monthBegin()>
									<div class="screenlet" style="margin-top:10px;">
									    <div class="screenlet-title-bar">
									        <div style="padding-bottom:10px;">
									            <a href="javascript:document.reorderinfo.submit();" class="submenutextright">${uiLabelMap.CommonSave}</a>
									        </div>
									        <h3>
									            ${uiLabelMap.ObbShoppingListReorder} - ${shoppingList.listName}
									            <#if shoppingList.isActive?default("N") == "N">
									                <font color="yellow">${uiLabelMap.ObbOrderNotActive}</font>
									            </#if>
									        </h3>
									    </div>
									    <div class="screenlet-body">
									        <form name="reorderinfo" method="post" action="<@ofbizUrl>updateShoppingList</@ofbizUrl>">
									            <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
									            <table width="100%" cellspacing="0" cellpadding="1" border="0">
									              <tr>
									                <td><div class="tableheadtext">${uiLabelMap.ObbRecurrence}</div></td>
									                <td>
									                  <#if recurrenceInfo?has_content>
									                    <#assign recurrenceRule = recurrenceInfo.getRelatedOne("RecurrenceRule", false)?if_exists>
									                  </#if>
									                  <select name="intervalNumber" class="selectBox">
									                    <option value="">${uiLabelMap.ObbSelectInterval}</option>
									                    <option value="1" <#if (recurrenceRule.intervalNumber)?default(0) == 1>selected="selected"</#if>>${uiLabelMap.EcommerceEveryDay}</option>
									                    <option value="2" <#if (recurrenceRule.intervalNumber)?default(0) == 2>selected="selected"</#if>>${uiLabelMap.EcommerceEveryOther}</option>
									                    <option value="3" <#if (recurrenceRule.intervalNumber)?default(0) == 3>selected="selected"</#if>>${uiLabelMap.EcommerceEvery3rd}</option>
									                    <option value="6" <#if (recurrenceRule.intervalNumber)?default(0) == 6>selected="selected"</#if>>${uiLabelMap.EcommerceEvery6th}</option>
									                    <option value="9" <#if (recurrenceRule.intervalNumber)?default(0) == 9>selected="selected"</#if>>${uiLabelMap.EcommerceEvery9th}</option>
									                  </select>
									                  &nbsp;
									                  <select name="frequency" class="selectBox">
									                    <option value="">${uiLabelMap.ObbSelectFrequency}</option>
									                    <option value="4" <#if (recurrenceRule.frequency)?default("") == "DAILY">selected="selected"</#if>>${uiLabelMap.CommonDay}</option>
									                    <option value="5" <#if (recurrenceRule.frequency)?default("") == "WEEKLY">selected="selected"</#if>>${uiLabelMap.CommonWeek}</option>
									                    <option value="6" <#if (recurrenceRule.frequency)?default("") == "MONTHLY">selected="selected"</#if>>${uiLabelMap.CommonMonth}</option>
									                    <option value="7" <#if (recurrenceRule.frequency)?default("") == "YEARLY">selected="selected"</#if>>${uiLabelMap.CommonYear}</option>
									                  </select>
									                </td>
									                <td>&nbsp;</td>
									                <td><div class="tableheadtext">${uiLabelMap.CommonStartDate}</div></td>
									                <td>
									                  <@htmlTemplate.renderDateTimeField name="startDateTime" className="input-text" event="" action="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${(recurrenceInfo.startDateTime)?if_exists}" size="25" maxlength="30" id="startDateTime1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
									                </td>
									                <td>&nbsp;</td>
									                <td><div class="tableheadtext">${uiLabelMap.CommonEndDate}</div></td>
									                <td>
									                  <@htmlTemplate.renderDateTimeField name="endDateTime" className="input-text" event="" action="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${(recurrenceRule.untilDateTime)?if_exists}" size="25" maxlength="30" id="endDateTime1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
									                </td>
									                <td>&nbsp;</td>
									              </tr>
									              <tr>
									                <td><div class="tableheadtext">${uiLabelMap.OrderShipTo}</div></td>
									                <td>
									                  <select name="contactMechId" class="selectBox" onchange="javascript:document.reorderinfo.submit()">
									                    <option value="">${uiLabelMap.OrderSelectAShippingAddress}</option>
									                    <#if shippingContactMechList?has_content>
									                      <#list shippingContactMechList as shippingContactMech>
									                        <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress", false)>
									                        <option value="${shippingContactMech.contactMechId}"<#if (shoppingList.contactMechId)?default("") == shippingAddress.contactMechId> selected="selected"</#if>>${shippingAddress.address1}</option>
									                      </#list>
									                    <#else>
									                      <option value="">${uiLabelMap.OrderNoAddressesAvailable}</option>
									                    </#if>
									                  </select>
									                </td>
									                <td>&nbsp;</td>
									                <td><div class="tableheadtext">${uiLabelMap.OrderShipVia}</div></td>
									                <td>
									                  <select name="shippingMethodString" class="selectBox">
									                    <option value="">${uiLabelMap.OrderSelectShippingMethod}</option>
									                    <#if carrierShipMethods?has_content>
									                      <#list carrierShipMethods as shipMeth>
									                        <#assign shippingEst = shippingEstWpr.getShippingEstimate(shipMeth)?default(-1)>
									                        <#assign shippingMethod = shipMeth.shipmentMethodTypeId + "@" + shipMeth.partyId>
									                        <option value="${shippingMethod}"<#if shippingMethod == chosenShippingMethod> selected="selected"</#if>>
									                          <#if shipMeth.partyId != "_NA_">
									                            ${shipMeth.partyId?if_exists}&nbsp;
									                          </#if>
									                          ${shipMeth.description?if_exists}
									                          <#if shippingEst?has_content>
									                            &nbsp;-&nbsp;
									                            <#if (shippingEst > -1)>
									                              <@ofbizCurrency amount=shippingEst isoCode=listCart.getCurrency()/>
									                            <#else>
									                              ${uiLabelMap.OrderCalculatedOffline}
									                            </#if>
									                          </#if>
									                        </option>
									                      </#list>
									                    <#else>
									                      <option value="">${uiLabelMap.OrderSelectAddressFirst}</option>
									                    </#if>
									                  </select>
									                </td>
									                <td>&nbsp;</td>
									                <td><div class="tableheadtext">${uiLabelMap.OrderPayBy}</div></td>
									                <td>
									                  <select name="paymentMethodId" class="selectBox">
									                    <option value="">${uiLabelMap.OrderSelectPaymentMethod}</option>
									                    <#list paymentMethodList as paymentMethod>
									                      <#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
									                        <#assign creditCard = paymentMethod.getRelatedOne("CreditCard", false)>
									                        <option value="${paymentMethod.paymentMethodId}" <#if (shoppingList.paymentMethodId)?default("") == paymentMethod.paymentMethodId>selected="selected"</#if>>CC:&nbsp;${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</option>
									                      <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
									                        <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount", false)>
									                        <option value="${paymentMethod.paymentMethodId}">EFT:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</option>
									                      </#if>
									                    </#list>
									                  </select>
									                </td>
									                <td>&nbsp;</td>
									              </tr>
									              <tr>
									                <td align="right" colspan="9">
									                  <div>
									                    <a href="javascript:document.reorderinfo.submit();" class="buttontext">${uiLabelMap.CommonSave}</a>
									                    <hr color="#ebebeb" style="margin-top:10px;"/>
									                    <br/><a href="<@ofbizUrl>editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&amp;contactMechPurposeTypeId=SHIPPING_LOCATION&amp;DONE_PAGE=editShoppingList</@ofbizUrl>" class="buttontext">${uiLabelMap.PartyAddNewAddress}</a>
									                    <span style="margin-left:5px;margin-right:5px;">|</span><a href="<@ofbizUrl>editcreditcard?DONE_PAGE=editShoppingList</@ofbizUrl>" class="buttontext">${uiLabelMap.ObbNewCreditCard}</a>
									                    <span style="margin-left:5px;margin-right:5px;">|</span><a href="<@ofbizUrl>editeftaccount?DONE_PAGE=editShoppingList</@ofbizUrl>" class="buttontext">${uiLabelMap.ObbNewEFTAccount}</a>
									                  </div>
									                </td>
									              </tr>
									              <#if shoppingList.isActive?default("N") == "Y">
									                <tr><td colspan="9"><hr /></td></tr>
									                <tr>
									                  <td colspan="9">
									                    <#assign nextTime = recInfo.next(lastSlOrderTime)?if_exists>
									                    <#if nextTime?has_content>
									                      <#assign nextTimeStamp = Static["org.ofbiz.base.util.UtilDateTime"].getTimestamp(nextTime)?if_exists>
									                      <#if nextTimeStamp?has_content>
									                        <#assign nextTimeString = Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(nextTimeStamp)?if_exists>
									                      </#if>
									                    </#if>
									                    <#if lastSlOrderDate?has_content>
									                      <#assign lastOrderedString = Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(lastSlOrderDate)?if_exists>
									                    </#if>
									                    <div>
									                      <table cellspacing="2" cellpadding="2" border="0">
									                        <tr>
									                          <td><div class="tableheadtext">${uiLabelMap.OrderLastOrderedDate}</div></td>
									                          <td><div class="tableheadtext">:</div></td>
									                          <td><div>${lastOrderedString?default("${uiLabelMap.OrderNotYetOrdered}")}</div></td>
									                        </tr>
									                        <tr>
									                          <td><div class="tableheadtext">${uiLabelMap.ObbEstimateNextOrderDate}</div></td>
									                          <td><div class="tableheadtext">:</div></td>
									                          <td><div>${nextTimeString?default("${uiLabelMap.ObbNotYetKnown}")}</div></td>
									                        </tr>
									                      </table>
									                    </div>
									                  </tr>
									                </tr>
									              </#if>
									            </table>
									        </form>
									    </div>
									</div>
								</#if>

								<#if childShoppingListDatas?has_content>
									<div class="screenlet">
									    <div class="screenlet-title-bar">
									        <div class="boxlink">
									            &nbsp;<a href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}&amp;includeChild=yes</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceAddChildListsToCart}</a>
									        </div>
									        <h3>&nbsp;${uiLabelMap.ObbChildShoppingList} - ${shoppingList.listName}</h3>
									    </div>
									    <div class="screenlet-body">
									        <table width="100%" cellspacing="0" cellpadding="1" border="0">
									          <tr>
									            <td><div><b>${uiLabelMap.ObbListName}</b></div></td>
									            <td align="right"><div><b>${uiLabelMap.ObbTotalPrice}</b></div></td>
									            <td>&nbsp;</td>
									            <td>&nbsp;</td>
									          </tr>
									          <#list childShoppingListDatas as childShoppingListData>
									              <#assign childShoppingList = childShoppingListData.childShoppingList/>
									              <#assign totalPrice = childShoppingListData.totalPrice/>
									              <tr>
									                <td nowrap="nowrap">
									                  <a href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${childShoppingList.listName?default(childShoppingList.shoppingListId)}</a>
									                </td>
									                <td nowrap="nowrap" align="right">
									                  <div><@ofbizCurrency amount=totalPrice isoCode=currencyUomId/></div>
									                </td>
									                <td align="right">
									                  <a href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ObbGoToList}</a>
									                  <a href="<@ofbizUrl>addListToCart?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ObbAddListToCart}</a>
									                </td>
									              </tr>
									            </form>
									          </#list>
									          <tr><td colspan="6"><hr /></td></tr>
									          <tr>
									            <td><div>&nbsp;</div></td>
									            <td nowrap="nowrap" align="right">
									              <div class="tableheadtext"><@ofbizCurrency amount=shoppingListChildTotal isoCode=currencyUomId/></div>
									            </td>
									            <td><div>&nbsp;</div></td>
									          </tr>
									        </table>
									    </div>
									</div>
								</#if>

								<div class="screenlet">
								    <div class="screenlet-title-bar">
								        <div class="boxlink">
								            &nbsp;<a href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.ObbAddListToCart}</a>
								        </div>
								        <h3>&nbsp;${uiLabelMap.ObbListItems} - ${shoppingList.listName}</h3>
								    </div>
								    <div class="screenlet-body">
								        <#if shoppingListItemDatas?has_content>
								            <#-- Pagination -->
								            <@paginationControls/>
								            <br/>
								            <table id="shopping-cart-table" class="data-table cart-table">
								              <tr>
								                <td><div><b>${uiLabelMap.OrderProduct}</b></div></td>
								                <td><table><tr><td nowrap="nowrap" align="center"><b>- ${uiLabelMap.ObbStartDate} -</b></td><td nowrap="nowrap"><b>- ${uiLabelMap.ObbNbrOfDays} -</b></td></tr><tr><td nowrap="nowrap"><b>- ${uiLabelMap.ObbNbrOfPersons} -</b></td><td nowrap="nowrap" align="center"><b>- ${uiLabelMap.CommonQuantity} -</b></td></tr></table></td>
								                <#-- <td nowrap="nowrap" align="center"><div><b>Purchased</b></div></td> -->
								                <td align="right"><div><b>${uiLabelMap.ObbPrice}</b></div></td>
								                <td align="right"><div><b>${uiLabelMap.OrderTotal}</b></div></td>
								                <td>&nbsp;</td>
								              </tr>
								              <#list shoppingListItemDatas[lowIndex-1..highIndex-1] as shoppingListItemData>
								                <#assign shoppingListItem = shoppingListItemData.shoppingListItem/>
								                <#assign product = shoppingListItemData.product/>
								                <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)/>
								                <#assign unitPrice = shoppingListItemData.unitPrice/>
								                <#assign totalPrice = shoppingListItemData.totalPrice/>
								                <#assign productVariantAssocs = shoppingListItemData.productVariantAssocs?if_exists/>
								                <#assign isVirtual = product.isVirtual?exists && product.isVirtual.equals("Y")/>
								                  <tr>
								                    <td>
								                      <div>
								                         <a href="<@ofbizUrl>product?product_id=${shoppingListItem.productId}</@ofbizUrl>" class="buttontext">${shoppingListItem.productId} -
								                         ${productContentWrapper.get("PRODUCT_NAME")?default("No Name")}</a> : ${productContentWrapper.get("DESCRIPTION")?if_exists}
								                      </div>
								                    </td>
								                    <td nowrap="nowrap" align="center">
								                      <form method="post" action="<@ofbizUrl>updateShoppingListItem</@ofbizUrl>" name="listform_${shoppingListItem.shoppingListItemSeqId}" style="margin: 0;">
								                        <input type="hidden" name="shoppingListId" value="${shoppingListItem.shoppingListId}"/>
								                        <input type="hidden" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}"/>
								                        <input type="hidden" name="reservStart"/>
								                        <div>
								                           <#if product.productTypeId == "ASSET_USAGE" || product.productTypeId == "ASSET_USAGE_OUT_IN">
								                           <table border="0" width="100%">
								                                <tr>
								                                    <td width="1%">&nbsp;</td>
								                                    <td><@htmlTemplate.renderDateTimeField event="" action="" name="reservStartStr" className="input-text" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${shoppingListItem.reservStart?if_exists}" size="15" maxlength="30" id="reservStartStr_${shoppingListItem.shoppingListItemSeqId}" dateType="date" shortDateInput=true timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/></td>
								                                    <td><input type="text" class="input-text" size="2" name="reservLength" value="${shoppingListItem.reservLength?if_exists}"/></td>
								                                </tr>
								                                <tr>
								                                <#if product.productTypeId == "ASSET_USAGE">
								                                    <td>&nbsp;</td>
								                                    <td><input type="text" class="input-text" size="3" name="reservPersons" value="${shoppingListItem.reservPersons?if_exists}"/></td>
								                                <#else>
								                                    <td>&nbsp;</td>
								                                    <td>&nbsp;</td>
								                                </#if>
								                                    <td>
								                           <#else>
								                                <table width="100%">
								                                    <tr>
								                                        <td width="62%" align="center">--</td>
								                                        <td align="center">--</td>
								                                    </tr>
								                                    <tr>
								                                        <td align="center">--</td>
								                                        <td><input type="hidden" name="reservStartStr" value=""/>
								                           </#if>
								                        <input size="6" class="input-text" type="text" name="quantity" value="${shoppingListItem.quantity?string.number}"/>
								                        </td></tr></table>
								                        </div>
								                      </form>
								                    </td>
								                    <#--
								                    <td nowrap="nowrap" align="center">
								                      <div>${shoppingListItem.quantityPurchased?default(0)?string.number}</div>
								                    </td>
								                    -->
								                    <td nowrap="nowrap" align="right">
								                      <div><@ofbizCurrency amount=unitPrice isoCode=currencyUomId/></div>
								                    </td>
								                    <td nowrap="nowrap" align="right">
								                      <div><@ofbizCurrency amount=totalPrice isoCode=currencyUomId/></div>
								                    </td>
								                    <td align="right">
								                        <a href="#" onclick="javascript:TimestampSubmit(listform_${shoppingListItem.shoppingListItemSeqId});" class="buttontext">${uiLabelMap.CommonUpdate}</a>
								                        <a href="<@ofbizUrl>removeFromShoppingList?shoppingListId=${shoppingListItem.shoppingListId}&amp;shoppingListItemSeqId=${shoppingListItem.shoppingListItemSeqId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonRemove}</a>
								                      <#if isVirtual && productVariantAssocs?has_content>
								                        <#assign replaceItemAction = "/replaceShoppingListItem/" + requestAttributes._CURRENT_VIEW_?if_exists>
								                        <#assign addToCartAction = "/additem/" + requestAttributes._CURRENT_VIEW_?if_exists>
								                        <br />
								                        <form method="post" action="<@ofbizUrl>${addToCartAction}</@ofbizUrl>" name="listreplform_${shoppingListItem.shoppingListItemSeqId}" style="margin: 0;">
								                          <input type="hidden" name="shoppingListId" value="${shoppingListItem.shoppingListId}"/>
								                          <input type="hidden" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}"/>
								                          <input type="hidden" name="quantity" value="${shoppingListItem.quantity}"/>
								                          <select name="add_product_id" class="selectBox">
								                              <#list productVariantAssocs as productVariantAssoc>
								                                <#assign variantProduct = productVariantAssoc.getRelatedOne("AssocProduct", true)>
								                                <#if variantProduct?exists>
								                                <#assign variantProductContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(variantProduct, request)>
								                                  <option value="${variantProduct.productId}">${variantProductContentWrapper.get("PRODUCT_NAME")?default("No Name")} [${variantProduct.productId}]</option>
								                                </#if>
								                              </#list>
								                          </select>
								                          <br />
								                          <a href="javascript:document.listreplform_${shoppingListItem.shoppingListItemSeqId}.action='<@ofbizUrl>${replaceItemAction}</@ofbizUrl>';document.listreplform_${shoppingListItem.shoppingListItemSeqId}.submit();" class="buttontext">${uiLabelMap.ObbReplaceWithVariation}</a>
								                          <br />
								                          <a href="javascript:document.listreplform_${shoppingListItem.shoppingListItemSeqId}.action='<@ofbizUrl>${addToCartAction}</@ofbizUrl>';document.listreplform_${shoppingListItem.shoppingListItemSeqId}.submit();" class="buttontext">${uiLabelMap.CommonAdd}&nbsp;${shoppingListItem.quantity?string}&nbsp;${uiLabelMap.ObbVariationToCart}</a>
								                        </form>
								                      <#else>
								                        <a href="<@ofbizUrl>additem<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if>?shoppingListId=${shoppingListItem.shoppingListId}&amp;shoppingListItemSeqId=${shoppingListItem.shoppingListItemSeqId}&amp;quantity=${shoppingListItem.quantity}&amp;reservStart=${shoppingListItem.reservStart?if_exists}&amp;reservPersons=${shoppingListItem.reservPersons?if_exists}&amp;reservLength=${shoppingListItem.reservLength?if_exists}&amp;configId=${shoppingListItem.configId?if_exists}&amp;add_product_id=${shoppingListItem.productId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonAdd}&nbsp;${shoppingListItem.quantity?string}&nbsp;${uiLabelMap.OrderToCart}</a>
								                      </#if>
								                    </td>
								                  </tr>
								              </#list>
								              <tr>
								                <td><div>&nbsp;</div></td>
								                <td><div>&nbsp;</div></td>
								                <#--<td><div>&nbsp;</div></td>-->
								                <td><div>&nbsp;</div></td>
								                <td nowrap="nowrap" align="right">
								                  <div class="tableheadtext"><@ofbizCurrency amount=shoppingListItemTotal isoCode=currencyUomId/></div>
								                </td>
								                <td><div>&nbsp;</div></td>
								              </tr>
								            </table>
								        <#else>
								            <h3 style="margin-top: 10px;">${uiLabelMap.ObbShoppingListEmpty}.</h3>
								        </#if>
								    </div>
								</div>
								<hr color="#ebebeb" style="margin-top:10px;"/>
								<div class="screenlet">
								    <div class="screenlet-title-bar" style="margin-top:20px;">
								        <h3>${uiLabelMap.ObbShoppingListPriceTotals} - ${shoppingList.listName}</h3>
								    </div>
								    <div class="screenlet-body">
								      <table id="shopping-cart-table" class="data-table cart-table">
								        <tr>
								          <td width="5%" nowrap="nowrap">
								              <div>${uiLabelMap.ObbChildListTotalPrice}</div>
								          </td>
								          <td align="right" width="5%" nowrap="nowrap">
								              <div><@ofbizCurrency amount=shoppingListChildTotal isoCode=currencyUomId/></div>
								          </td>
								          <td width="90%"><div>&nbsp;</div></td>
								        </tr>
								        <tr>
								          <td nowrap="nowrap">
								              <div>${uiLabelMap.ObbListItemsTotalPrice}&nbsp;</div>
								          </td>
								          <td align="right" nowrap="nowrap">
								              <div><@ofbizCurrency amount=shoppingListItemTotal isoCode=currencyUomId/></div>
								          </td>
								          <td><div>&nbsp;</div></td>
								        </tr>
								        <tr>
								          <td nowrap="nowrap">
								              <div class="tableheadtext">${uiLabelMap.OrderGrandTotal}</div>
								          </td>
								          <td align="right" nowrap="nowrap">
								              <div class="tableheadtext"><@ofbizCurrency amount=shoppingListTotalPrice isoCode=currencyUomId/></div>
								          </td>
								          <td><div>&nbsp;</div></td>
								        </tr>
								      </table>
								    </div>
								</div>
								<hr color="#ebebeb" style="margin-top:10px;"/>
								<div class="screenlet">
								    <div class="screenlet-title-bar" style="margin-top:20px;">
								        <h3>${uiLabelMap.CommonQuickAddList}</h3>
								    </div>
								    <div class="screenlet-body">
								        <form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>">
								          <div class="margintop-10">
										  <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
									          <input type="text" class="input-text  marginright-10" name="productId" value="${requestParameters.add_product_id?if_exists}" class="marginright-10"/>
									          <#if reservStart?exists></td>
									          <td>${uiLabelMap.ObbStartDate}</td>
									          <td><input type="text" class="input-text" size="10" name="reservStart" value="${requestParameters.reservStart?default("")}" /></td>
									          <td> ${uiLabelMap.EcommerceLength}:</td><td><input type="text" class="input-text" size="2" name="reservLength" value="${requestParameters.reservLength?default("")}" /></td>
									          </tr><tr><td>&nbsp;</td><td>&nbsp;</td><td>${uiLabelMap.OrderNbrPersons}:</td>
									          <td><input type="text" class="input-text" size="3" name="reservPersons" value="${requestParameters.reservPersons?default("1")}" /></td>
									          <td nowrap="nowrap"></#if> ${uiLabelMap.CommonQuantity} :</td><td><input type="text" class="input-text marginleft-10 marginright-10" size="5" name="quantity" value="${requestParameters.quantity?default("1")}" /></td>
									          <td>
									          <button type="submit" title="Create an Account" class="button">
											<span><span>${uiLabelMap.OrderAddToShoppingList}</span></span>
										  </button>
									  </div>
								        </form>
								    </div>
								</div>
						    <#else>
						        <#-- shoppingList was found, but belongs to a different party -->
						        <h2>${uiLabelMap.ObbShoppingListError} ${uiLabelMap.CommonId} ${shoppingList.shoppingListId}) ${uiLabelMap.ObbListDoesNotBelong}.</h2>
						    </#if>
						</#if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>