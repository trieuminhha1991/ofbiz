<#if orderTerms?has_content>
<div id="terms-tab" class="tab-pane active">
	<h4 class="smaller lighter green" style="display:inline-block">
		${uiLabelMap.DAOrderTerms}
	</h4>
	<table class="table table-hover table-striped dataTable table-bordered" cellspacing='0'>
		<tr class="header-row">
			<td width="35%">${uiLabelMap.DAOrderTermType}</td>
	        <td width="15%" align="center">${uiLabelMap.DAOrderTermValue}</td>
	        <td width="20%" align="center">${uiLabelMap.OrderOrderTermDays}</td>
	        <td width="30%" align="center">${uiLabelMap.CommonDescription}</td>
		</tr>
	    <#list orderTerms as orderTerm>
			<tr>
	        	<td width="35%">${orderTerm.getRelatedOne("TermType", false).get("description", locale)}</td>
	        	<td width="15%" align="center">${orderTerm.termValue?default("")}</td>
	        	<td width="20%" align="center">
	        		<#assign dateTerm = orderTerm.termDays?default("")>
	        		<#if dateTerm?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(dateTerm, "dd/MM/yyyy - HH:mm:ss:SSS", locale, timeZone)!}</#if>
        		</td>
	        	<td width="30%" align="center">${orderTerm.textValue?default("")}</td>
	      	</tr>
	    </#list>
  	</table>
</div><!--#terms-tab-->
</#if>