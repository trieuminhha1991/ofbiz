<#if orderTerms?has_content>
<div id="terms-tab" class="tab-pane active">
	<h4 class="smaller lighter green" style="display:inline-block">
		<#-- <i class="fa-file"></i> -->
		${uiLabelMap.OrderOrderTerms}
	</h4>

	      <table class="table table-hover table-striped dataTable table-bordered" cellspacing='0'>
	      <tr class="header-row">
	        <td width="35%">${uiLabelMap.OrderOrderTermType}</td>
	        <td width="15%" align="center">${uiLabelMap.OrderOrderTermValue}</td>
	        <td width="15%" align="center">${uiLabelMap.OrderOrderTermDays}</td>
	        <td width="35%" align="center">${uiLabelMap.CommonDescription}</td>
	      </tr>
	    <#list orderTerms as orderTerm>
	      <tr>
	        <td width="35%">${orderTerm.getRelatedOne("TermType", false).get("description", locale)}</td>
	        <td width="15%" align="center">${orderTerm.termValue?default("")}</td>
	        <td width="15%" align="center">${orderTerm.termDays?default("")}</td>
	        <td width="35%" align="center">${orderTerm.textValue?default("")}</td>
	      </tr>
	    </#list>
	      </table>
</div> <!--#terms-tab-->
</#if>