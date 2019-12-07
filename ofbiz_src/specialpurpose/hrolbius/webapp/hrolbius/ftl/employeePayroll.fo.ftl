<#if (requestAttributes.person)?exists><#assign person = requestAttributes.person></#if>
<#assign userName = person.firstName?if_exists + " " + person.middleName?if_exists + " " + person.lastName?if_exists>
<#assign puserName = context.userPayroll.lastName?if_exists + " " + context.userPayroll.middleName?if_exists + " " + context.userPayroll.firstName?if_exists>
<#assign currDateTime = .now > yesterday?datetime>
	<fo:block-container width="465px">
    	<fo:block text-align="center" border-bottom="dashed 1px black" border-width="1px" font-weight="bold" font-size="18px">
	      Payroll Summary
	    </fo:block>
    </fo:block-container>    
    <fo:block-container width="465px" margin-top="15px">
    	<fo:block border-bottom="dashed 1px black" border-width="1px" font-weight="bold">
	      ${uiLabelMap.GeneralInfo}
	    </fo:block>
    </fo:block-container>
    <fo:table table-layout="fixed" width="100%" border="0px" margin-left="5px" margin-top="5px" font-size="8px">
    	<fo:table-column column-width="40%"/>
    	<fo:table-column/>
    	<fo:table-header>
    		<fo:table-cell>
			    <fo:block>${uiLabelMap.fromDate}:</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block>${parameters.fromDate}</fo:block>
			</fo:table-cell>
    	</fo:table-header>
    	<fo:table-body>
      		<fo:table-row>
      			<fo:table-cell>
      				<fo:block >${uiLabelMap.thruDate}:</fo:block>
      			</fo:table-cell>
      			<fo:table-cell>
      				<fo:block >${parameters.thruDate}</fo:block>
      			</fo:table-cell>
      		</fo:table-row>
      		<fo:table-row>
      			<fo:table-cell>
      				<fo:block >${uiLabelMap.systemTime}:</fo:block>
      			</fo:table-cell>
      			<fo:table-cell>
      				<fo:block >${currDateTime}</fo:block>
      			</fo:table-cell>
      		</fo:table-row>
      		<fo:table-row>
      			<fo:table-cell>
      				<fo:block >${uiLabelMap.Reporter}:</fo:block>
      			</fo:table-cell>
      			<fo:table-cell>
      				<fo:block >${userName}(${userLogin.partyId})</fo:block>
      			</fo:table-cell>
      		</fo:table-row>
      		<fo:table-row>
      			<fo:table-cell>
      				<fo:block >${uiLabelMap.UsersPayroll}:</fo:block>
      			</fo:table-cell>
      			<fo:table-cell>
      				<fo:block >${puserName?if_exists}(${parameters.pdfPartyId})</fo:block>
      			</fo:table-cell>
      		</fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block-container width="465px" margin-top="20px">
	    <fo:block border-bottom="dashed 1px black" border-width="1px" font-weight="bold">
	      ${uiLabelMap.parameters}
	    </fo:block>
    </fo:block-container>
    <fo:table table-layout="fixed" margin-top="5px" font-size="8px">
    	<fo:table-column column-width="18%"/>
    	<fo:table-column column-width="18%"/>
        <fo:table-column/>
        <fo:table-column/>
        <fo:table-column/>
        <fo:table-header>
        	<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.parameterCode}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.parameterName}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.totalValue}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.actualValue}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.companyValue}</fo:block>
			</fo:table-cell>
        </fo:table-header>
        <fo:table-body>
        	<#list context.parametersList as parameter>
	          <fo:table-row>
	          		<fo:table-cell>
	          			<fo:block text-align="left">&#160;&#160;&#160;${parameter.code}</fo:block>
	          		</fo:table-cell>
	          		<fo:table-cell>
	          			<fo:block text-align="left">&#160;&#160;&#160;${context.listParameterName.get(parameter_index)}</fo:block>
	          		</fo:table-cell>
					<fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;
		              	${context.parametersTotalList.get(parameter_index)?if_exists}
		              </fo:block>
		            </fo:table-cell>
					<fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;
		              	${parameter.value?if_exists}
		              </fo:block>
		            </fo:table-cell>
		            <fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;
		              	${context.parametersCompanyList.get(parameter_index)?if_exists}
		              </fo:block>
		            </fo:table-cell>
	          </fo:table-row>
          	</#list>
          	<#list context.listGlobalParameter as parameter>
          		<fo:table-row>
	          		<fo:table-cell>
	          			<fo:block text-align="left">&#160;&#160;&#160;${parameter.code?if_exists}</fo:block>
	          		</fo:table-cell>
	          		<fo:table-cell>
	          			<fo:block text-align="left">&#160;&#160;&#160;${parameter.name?if_exists}</fo:block>
	          		</fo:table-cell>
					<fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;
		              	${parameter.defaultValue?if_exists}
		              </fo:block>
		            </fo:table-cell>
					<fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;
		              	${parameter.actualValue?if_exists}
		              </fo:block>
		            </fo:table-cell>
		            <fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;
		              	${context.listCompnayValue.get(parameter_index)}
		              </fo:block>
		            </fo:table-cell>
	          </fo:table-row>
          	</#list>
        </fo:table-body>
    </fo:table>
    <fo:block-container width="465px" margin-top="20px">
	    <fo:block border-bottom="dashed 1px black" border-width="1px" font-weight="bold">
	      ${uiLabelMap.formulaFunction}
	    </fo:block>
    </fo:block-container>
    <fo:table table-layout="fixed" margin-top="5px" font-size="8px">
        <fo:table-column column-width="20%"/>
        <fo:table-column column-width="20%"/>
        <fo:table-column/>
        <fo:table-header>
        	<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.formulaCode}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.formulaName}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.formulaFunction}</fo:block>
			</fo:table-cell>
        </fo:table-header>
        <fo:table-body>
        	<#list parameters.formulaList as header>
	          <fo:table-row>
	          		<fo:table-cell>
	          			<fo:block text-align="left">&#160;&#160;&#160;${header}</fo:block>
	          		</fo:table-cell>
					<fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;${context.listFunctionName.get(header_index)}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;${context.listFunction.get(header_index)}</fo:block>
		            </fo:table-cell>
	          </fo:table-row>
          	</#list>
        </fo:table-body>
    </fo:table>
        
    <fo:block-container width="465px" margin-top="20px">
	    <fo:block border-bottom="dashed 1px black" border-width="1px" font-weight="bold">
	      ${uiLabelMap.PayrollReport}
	    </fo:block>
    </fo:block-container>
    <fo:table table-layout="fixed" margin-top="5px" font-size="8px">
        <fo:table-column column-width="20%"/>
        <fo:table-column />
        <fo:table-header>
		  	<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.formulaFunction}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			    <fo:block font-weight="bold" text-align="left">${uiLabelMap.formulaAmount}</fo:block>
			</fo:table-cell>
		</fo:table-header>
        <fo:table-body>
        	<#list parameters.formulaList as header>
	          <fo:table-row>
	          		<fo:table-cell>
	          			<fo:block text-align="left">&#160;&#160;&#160;${header}</fo:block>
	          		</fo:table-cell>
					<fo:table-cell>
		              <fo:block text-align="left">&#160;&#160;&#160;<@ofbizCurrency amount=parameters.salaryAmountList.get(0).getListSalaryAmount().get(header_index).amount?if_exists isoCode=currencyUomId?if_exists/></fo:block>
		            </fo:table-cell>
	          </fo:table-row>
          	</#list>
        </fo:table-body>
    </fo:table>
