<div class="row-fluid ">
	<div class="boder-all-profile">
		<!--PAGE CONTENT BEGINS-->
		<div class="tab-content no-border padding-24">
			<div id="home" class="tab-pane active">
				<div class="row-fluid">
					<div class="span3 center">
						<span class="profile-picture">
							<#if  partyContent?? >
								<#if partyContent?has_content>
									<img class="cssImage" alt="NoAvata" id="NoAvatar" src="${partyContent}">
								<#else>
									<#assign gender= lookupPerson.gender?if_exists>
									<#switch gender>
										<#case "M">
											<img class="cssImage" alt="NoAvata" id="NoAvatar" src="/aceadminHtml/assets/avatars/avatar4.png">
										<#break>
										<#case "F">
											<img class="cssImage" alt="NoAvata" id="NoAvatar" src="/aceadminHtml/assets/avatars/avatar3.png">
										<#break>
										<#default>
											<img class="cssImage" alt="NoAvata" id="NoAvatar" src="/aceadminHtml/assets/avatars/avatar2.png">
									</#switch>
								</#if>
							</#if>
						</span>
						<h5 class="blue">
							<span class="msg-body">
								<span class="msg-title"> 
									<span class="blue">
										${lookupPerson.lastName?if_exists} ${lookupPerson.middleName?if_exists} ${lookupPerson.firstName?if_exists}
									</span> 
								</span>
							</span>
						</h5>
						<div class="space space-4"></div>
						
					</div><!--/span-->

					<div class="span9">
						

						<div class="profile-user-info">
							<div class="profile-info-row">
								<div class="profile-info-name"> ${uiLabelMap.EmployeeId}</div>

								<div class="profile-info-value">
									<span>
										<b>${lookupPerson.partyId?if_exists}</b>
									</span>
								</div>
							</div>
							<div class="profile-info-row">
								<div class="profile-info-name" style="width: 120px"> ${uiLabelMap.PermanentResidence}</div>
								<div class="profile-info-value">
									<i class="icon-map-marker light-orange bigger-110"></i>
									<#if permResiContactMech?exists>										
										${permResiContactMech.address1?if_exists} 
										<#if permResiContactMech.stateProvinceGeoId?exists>
										 	<#assign stateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permResiContactMech.stateProvinceGeoId), false)>
										 	, ${stateProvinceGeo.geoName?if_exists}
										</#if>
										<#if permResiContactMech.countryGeoId?exists>
										 	<#assign countryGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", permResiContactMech.countryGeoId), false)>
										 	, ${countryGeo.geoName?if_exists}
										</#if>
									<#else>
										<span>&nbsp;</span>
									</#if>
								</div>
							</div>	
							<div class="profile-info-row">
								<div class="profile-info-name"> ${uiLabelMap.CurrentResidence}</div>
								<div class="profile-info-value">
									<i class="icon-map-marker light-orange bigger-110"></i>
									<#if currResiContactMech?exists>
										${currResiContactMech.address1?if_exists} 
										<#if currResiContactMech.stateProvinceGeoId?exists>
										 	<#assign stateProvinceGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", currResiContactMech.stateProvinceGeoId), false)>
										 	,${stateProvinceGeo.geoName?if_exists}
										</#if>
										<#if currResiContactMech.countryGeoId?exists>
										 	<#assign countryGeo = delegator.findOne("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId", currResiContactMech.countryGeoId), false)>
										 	,${countryGeo.geoName?if_exists}
										</#if>
									<#else>
										<span>&nbsp;</span>
									</#if>
								</div>
								<#--<!-- <#if contactMeches?has_content>
									 <#list contactMeches as contactMechMap>
									 	  <#assign contactMech = contactMechMap.contactMech>
									 	  <#if "POSTAL_ADDRESS" = contactMech.contactMechTypeId>
									 		  <#if contactMechMap.postalAddress?has_content>
   											    <#assign postalAddress = contactMechMap.postalAddress>
   												<div class="profile-info-value">
													<i class="icon-map-marker light-orange bigger-110"></i>
													<span>${postalAddress.address1?if_exists}&nbsp</span>
												</div>
   											  </#if>
   											<#else>
		 										<div class="profile-info-value">
													<i class="icon-map-marker light-orange bigger-110"></i>
													<span>&nbsp</span>
												</div>  
										  </#if>
					 			     </#list>
								
								<#else>
										<div class="profile-info-value">
											<i class="icon-map-marker light-orange bigger-110"></i>
											<span>&nbsp</span>
										</div>
								</#if> -->
							</div>

							<div class="profile-info-row">
								<div class="profile-info-name"> ${uiLabelMap.BirthDate} </div>

								<div class="profile-info-value">
									<span>${lookupPerson.birthDate?if_exists}&nbsp</span>
								</div>
							</div>

							<div class="profile-info-row">
								<div class="profile-info-name"> ${uiLabelMap.Gender} </div>

								<div class="profile-info-value">
									<#assign gender= lookupPerson.gender?if_exists>
										<#switch gender>
											<#case "F">
												<span>Nữ</span>
											<#break>
											<#case "M">
												<span>Nam</span>
											<#break>
											<#default>
												<span>&nbsp</span>
										</#switch>
								</div>
							</div>
							
							<div class="profile-info-row">
								<div class="profile-info-name">${uiLabelMap.OrgUnitName}</div>

								<#if employmentData?has_content>	
									<#if employmentData.employment?has_content>
										<#assign partyId= employmentData.employment.partyIdFrom?if_exists>
											<#if partyId?has_content>		
												<#assign desc=delegator.findOne("PartyGroup",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",partyId),false) >
												<#if desc?has_content>
													<div class="profile-info-value">
														<span>	${desc.get("groupName")}&nbsp</span>
													</div>
												</#if>
											<#else>
												<div class="profile-info-value">
													<span>&nbsp</span>
												</div>
											</#if>	
										<#else>
										<div class="profile-info-value">
											<span>&nbsp</span>
										</div>
									</#if>
											
								<#else>
									<div class="profile-info-value">
										<span>&nbsp</span>
									</div>
									
								</#if>
							</div>

							<div class="profile-info-row">
								<div class="profile-info-name">${uiLabelMap.HREmplFromPositionType}</div>
								<#if employmentData?has_content>	
									<#if employmentData.emplPosition?has_content>

										<#assign emplPositionTypeId= employmentData.emplPosition.emplPositionTypeId?if_exists>
											<#if emplPositionTypeId?has_content>		
												<#assign desc=delegator.findOne("EmplPositionType",Static["org.ofbiz.base.util.UtilMisc"].toMap("emplPositionTypeId",emplPositionTypeId),false) >
												<#if desc?has_content>
													<div class="profile-info-value">
														<span>	${desc.get("description")}&nbsp</span>
													</div>
												</#if>
											<#else>
												<div class="profile-info-value">
													<span>&nbsp</span>
												</div>
											</#if>
									<#else>
										<div class="profile-info-value">
											<span>&nbsp</span>
										</div>
									</#if>
											
								<#else>
									<div class="profile-info-value">
										<span>&nbsp</span>
									</div>
								</#if>
							</div>
						</div>
						<div class="hr hr-8 dotted"></div>
					</div><!--/span-->
				</div><!--/row-fluid-->
				<div class="space-20"></div>
			</div>
		</div>
	</div>
</div>
					