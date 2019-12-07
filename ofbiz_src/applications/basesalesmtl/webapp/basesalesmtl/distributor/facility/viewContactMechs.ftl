<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<#if contactMeches?exists && contactMeches?has_content>
				<table class="table table-striped table-bordered table-hover">
					<thead>
						<tr>
							<th class="center">${uiLabelMap.DAType}</th>
							<th class="center">${uiLabelMap.DAInformation}</th>
							<th class="center">${uiLabelMap.DAAction}</th>
						</tr>
					</thead>
					<tbody>
					<#list contactMeches as contactMechMap>
						<#assign contactMech = contactMechMap.contactMech>
      					<#assign facilityContactMech = contactMechMap.facilityContactMech>
						<tr>
							<td><b class="blue">${contactMechMap.contactMechType.get("description",locale)}</b></td>
							<td>
								<#list contactMechMap.facilityContactMechPurposes as facilityContactMechPurpose>
				                  	<#assign contactMechPurposeType = facilityContactMechPurpose.getRelatedOne("ContactMechPurposeType", true)>
		                      		<#if contactMechPurposeType?has_content>
				                        <b>${contactMechPurposeType.get("description",locale)}</b>
			                      	<#else>
				                        <b>${uiLabelMap.ProductPurposeTypeNotFoundWithId}: "${facilityContactMechPurpose.contactMechPurposeTypeId}"</b>
			                      	</#if>
			                      	<#if facilityContactMechPurpose.thruDate?has_content>
				                      	(${uiLabelMap.CommonExpire}: ${facilityContactMechPurpose.thruDate.toString()})
			                      	</#if>
			                      	<br />
				              </#list>
				              <#if "POSTAL_ADDRESS" = contactMech.contactMechTypeId>
				                  	<#assign postalAddress = contactMechMap.postalAddress>
			                    	<#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b> ${postalAddress.toName}<br /></#if>
				                    <#if postalAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b> ${postalAddress.attnName}<br /></#if>
				                    ${postalAddress.address1?if_exists}<br />
				                    <#if postalAddress.address2?has_content>${postalAddress.address2?if_exists}<br /></#if>
				                    ${postalAddress.city?if_exists},
				                    ${postalAddress.stateProvinceGeoId?if_exists}
				                    ${postalAddress.postalCode?if_exists}
				                    <#if postalAddress.countryGeoId?has_content><br />${postalAddress.countryGeoId}</#if>
				                  	<#if (postalAddress?has_content && !postalAddress.countryGeoId?has_content) || postalAddress.countryGeoId = "USA">
				                      	<#assign addr1 = postalAddress.address1?if_exists>
				                      	<#if (addr1.indexOf(" ") > 0)>
				                        	<#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
				                        	<#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
				                        	<br /><a class="btn btn-info btn-mini" target='_blank' href='${uiLabelMap.CommonLookupWhitepagesAddressLink}' >${uiLabelMap.CommonLookupWhitepages}</a>
				                      	</#if>
				                  	</#if>
				                  	<#if postalAddress.geoPointId?has_content>
				                    	<#if contactMechPurposeType?has_content>
				                      		<#assign popUptitle = contactMechPurposeType.get("description",locale) + uiLabelMap.CommonGeoLocation>
				                   		 </#if>
			                    		<br /><a class="btn btn-info btn-mini" href="javascript:popUp('<@ofbizUrl>geoLocation?geoPointId=${postalAddress.geoPointId}</@ofbizUrl>', '${popUptitle?if_exists}', '450', '550')">${uiLabelMap.CommonGeoLocation}</a>
				                  	</#if>
				              	<#elseif "TELECOM_NUMBER" = contactMech.contactMechTypeId>
				                  	<#assign telecomNumber = contactMechMap.telecomNumber>
				                    ${telecomNumber.countryCode?if_exists}
				                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber?if_exists}
				                    <#if facilityContactMech.extension?has_content>${uiLabelMap.CommonExt} ${facilityContactMech.extension}</#if>
				                    <#if (telecomNumber?has_content && !telecomNumber.countryCode?has_content) || telecomNumber.countryCode = "011">
				                      	<br /><a class="btn btn-info btn-mini" target='_blank' href='${uiLabelMap.CommonLookupAnywhoLink}' >${uiLabelMap.CommonLookupAnywho}</a>
				                      	<a class="btn btn-info btn-mini" target='_blank' href='${uiLabelMap.CommonLookupWhitepagesTelNumberLink}' >${uiLabelMap.CommonLookupWhitepages}</a>
				                    </#if>
				              	<#elseif "EMAIL_ADDRESS" = contactMech.contactMechTypeId>
				                    ${contactMech.infoString?if_exists}
				                    (&nbsp;<a href='mailto:${contactMech.infoString?if_exists}'><i class="fa-envelope-o"></i>&nbsp;${uiLabelMap.CommonSendEmail}</a>&nbsp;)
				              	<#elseif "WEB_ADDRESS" = contactMech.contactMechTypeId>
				                    ${contactMech.infoString?if_exists}
				                    <#assign openAddress = contactMech.infoString?default("")>
				                    <#if !openAddress?starts_with("http") && !openAddress?starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
				                    <a class="btn btn-info btn-mini" target='_blank' href='${openAddress}' >((${uiLabelMap.CommonOpenPageNewWindow})</a>
				              	<#else>
				                    ${contactMech.infoString?if_exists}
				              	</#if>
				              	<br />(${uiLabelMap.CommonUpdated}: ${facilityContactMech.fromDate.toString()})
				              	<#if facilityContactMech.thruDate?has_content><br /><b>${uiLabelMap.CommonUpdatedEffectiveThru}:&nbsp;${facilityContactMech.thruDate.toString()}</b></#if>
				            </td>
							<td>
				              	<#if security.hasEntityPermission("DIS_FACILITY", "_UPDATE", session)>
					                <a class="open-sans btn btn-mini btn-primary icon-refresh" href='<@ofbizUrl>editContactMechDis?facilityId=${facilityId}&amp;contactMechId=${contactMech.contactMechId}</@ofbizUrl>'>${uiLabelMap.CommonUpdate}</a>
				              	</#if>
				              	<#if security.hasEntityPermission("DIS_FACILITY", "_DELETE", session)>
					                <form action="<@ofbizUrl>deleteContactMechDis/viewContactMechsDis</@ofbizUrl>" name="deleteContactForm_${contactMechMap_index}" method="post">
					                  	<input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
					                  	<input type="hidden" name="contactMechId" value="${contactMech.contactMechId?if_exists}"/>
					                </form>
					                <a class="btn btn-warning btn-mini icon-remove open-sans" href="javascript:document.deleteContactForm_${contactMechMap_index}.submit()">${uiLabelMap.CommonExpire}</a>
				              	</#if>
				              	<#--
				              	<div class="hidden-phone visible-desktop btn-group">	
					              	<#if security.hasEntityPermission("DIS_FACILITY", "_UPDATE", session)>
					              		<button type="button" class="btn btn-mini btn-info" onclick="window.location.href='<@ofbizUrl>editContactMechDis?facilityId=${facilityId}&amp;contactMechId=${contactMech.contactMechId}</@ofbizUrl>';">
											<i class="icon-edit bigger-120"></i>
										</button>
					              	</#if>
					              	<#if security.hasEntityPermission("DIS_FACILITY", "_DELETE", session)>
						                <button class="btn btn-mini btn-danger" onclick="javascript:document.deleteContactForm_${contactMechMap_index}.submit()">
											<i class="icon-trash bigger-120"></i>
										</button>
										<form action="<@ofbizUrl>deleteContactMech/ViewContactMechs</@ofbizUrl>" name="deleteContactForm_${contactMechMap_index}" method="post">
						                  	<input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
						                  	<input type="hidden" name="contactMechId" value="${contactMech.contactMechId?if_exists}"/>
						                </form>
					              	</#if>
				              	</div>
				              	-->
							</td>
						</tr>
					</#list>
					</tbody>
				</table>
			<#else>
				<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
			</#if>
		</div>
	</div>
</div>