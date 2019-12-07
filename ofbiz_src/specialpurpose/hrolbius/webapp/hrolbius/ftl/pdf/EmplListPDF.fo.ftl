<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xml.apache.org/fop/extensions">
		<fo:layout-master-set>
		  <fo:simple-page-master margin-right="2.0cm" margin-left="1.5cm" 
		  	margin-bottom="1mm" margin-top="0.5cm" page-width="23cm" page-height="29.7cm" master-name="main">
		    <fo:region-body margin-bottom="1.5cm" margin-top="1.5cm" padding-left="1cm" />
		    <fo:region-before extent="1.5cm"/>
		  </fo:simple-page-master>
		</fo:layout-master-set>
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Arial">
				<fo:block-container width="100%">
			    	<fo:block text-align="center" font-weight="bold" font-size="18px" margin-bottom="0.6cm" margin-top="0.3cm">
			    		<#assign internalOrgName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, internalOrgId, false)>
				    	${uiLabelMap.HREmplList} ${uiLabelMap.CommonOf} ${internalOrgName?if_exists}   
				    </fo:block>
			    </fo:block-container>
			    <fo:block-container width="100%">
			    	<#if emplList?has_content>
			    	<fo:table table-layout="fixed" width="100%" margin-top="5px" space-before="3cm" border-collapse="separate"  
			    		border-style="solid" font-size="10px" border="0.1mm black" >
			    		<fo:table-column column-width="1cm"/>
				    	<fo:table-column/>
				    	<fo:table-column/>
				    	<fo:table-column/>
				    	<fo:table-column/>
				    	<fo:table-column/>
				    	<fo:table-header>
				    		 <fo:table-row>
	                             <fo:table-cell border="0.1mm black solid" padding="3mm">
	                             	<fo:block font-weight="bold"> ${uiLabelMap.HRSequenceNbr}</fo:block>
	                             </fo:table-cell>
	                             <fo:table-cell border="0.1mm black solid" padding="3mm">
	                                <fo:block font-weight="bold">${uiLabelMap.EmployeeName}</fo:block>
	                             </fo:table-cell>
	                             <fo:table-cell border="0.1mm black solid" padding="3mm">
	                                <fo:block font-weight="bold">${uiLabelMap.PartyBirthDate}</fo:block>
	                             </fo:table-cell>
	                             <fo:table-cell border="0.1mm black solid" padding="3mm">
	                                <fo:block font-weight="bold">
	                                	${uiLabelMap.HREmployeePosition}
	                                </fo:block>
	                             </fo:table-cell>
	                             <fo:table-cell border="0.1mm black solid" padding="3mm">
	                                <fo:block font-weight="bold">
	                                	${uiLabelMap.EmployeeCurrentDept}
	                                </fo:block>
	                             </fo:table-cell>
	                             <fo:table-cell border="0.1mm black solid" padding="3mm">
	                                <fo:block font-weight="bold">
	                                	${uiLabelMap.PhoneMobile}
	                                </fo:block>
	                             </fo:table-cell>
                             </fo:table-row>
				    	</fo:table-header>
				    	<fo:table-body>
				    		<#list emplList as empl>
				    			<fo:table-row>
				    				<fo:table-cell border="0.1mm black solid" padding="3mm">
				    					<fo:block text-align="center">
				    						${empl_index + 1}
				    					</fo:block>
				    				</fo:table-cell>
				    				<fo:table-cell border="0.1mm black solid" padding="3mm">
				    					<fo:block>
				    						${empl.firstName?if_exists} ${empl.middleName?if_exists} ${empl.lastName?if_exists}
				    					</fo:block>
				    				</fo:table-cell>
				    				<fo:table-cell border="0.1mm black solid" padding="3mm">
				    					<fo:block>
				    						${empl.birthDate?if_exists}
				    					</fo:block>
				    				</fo:table-cell>
				    				<fo:table-cell border="0.1mm black solid" padding="3mm">
				    					<fo:block >
				    						
				    					</fo:block>
				    				</fo:table-cell>
				    				<fo:table-cell border="0.1mm black solid" padding="3mm">
				    					<fo:block>
				    						<#assign deptName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, empl.partyIdFrom, false)>
				    						${deptName?if_exists}
				    					</fo:block>
				    				</fo:table-cell>
				    				<fo:table-cell border="0.1mm black solid" padding="3mm">
				    					<#assign phoneMobile = dispatcher.runSync("getPartyTelephone", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", empl.partyIdFrom, "contactMechPurposeTypeId", "PHONE_MOBILE"))>
				    					<#if phoneMobile?has_content>
					    					${phoneMobile.areaCode?if_exists} ${phoneMobile.contactNumber?if_exists} ${phoneMobile.extension?if_exists}
					    				</#if>
				    				</fo:table-cell>
				    			</fo:table-row>
				    		</#list>
				    	</fo:table-body>
			    	</fo:table>
			    	<#else>
			    		<fo:block text-align="left" font-weight="bold" font-size="14px" margin-bottom="0.6cm" margin-top="0.3cm">
							${uiLabelMap.NoEmplInOrg}			    		
			    		</fo:block>
			    	</#if>
			    </fo:block-container>
			</fo:flow>
		</fo:page-sequence>
	</fo:root>
</#escape>