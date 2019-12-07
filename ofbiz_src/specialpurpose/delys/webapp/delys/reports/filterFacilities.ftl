<form name="filterfacilitesform" id="filterfacilitesform" action="<@ofbizUrl>facilitiesReport</@ofbizUrl>">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<input type="hidden" name="action" value="Y">	
		<tr>
			<td align='right' valign='middle' nowrap="nowrap">${uiLabelMap.CommonUserLoginId}</td>
			<td valign="middle">
			 	<span class="label label-large label-success arrowed-right">
					<i class="icon-key"></i>
					${parameters.userLogin.userLoginId}
				</span>
			</td>
		</tr>
		  <#if listFacilities?has_content>
		  <tr>
			<td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.Facility}</div></td>
			<td valign='middle'>
				<div style="margin-left:7px;" id="salessupId">
					
					 <select name="facilityId" >
					          <option value="">&nbsp</option>
					         <#list listFacilities as facility>
					              <option value="${facility.facilityId}" <#if parameters.facilityId == facility.facilityId>selected</#if>>${facility.facilityName}</option>
					         </#list>
					 </select>
					 
				</div>
			 </td>
		  </tr>
		  
		  <tr>
			 <td></td>
			 <td>
			 	<a style="margin-left:7px;" class="btn btn-primary btn-small margin-left3" onclick="javascript: document.filterfacilitesform.submit();"> 
				 	<i class="icon-search"></i>
				 	${uiLabelMap.CommonFind}
				 </a>
			  </td>
		</tr>
		<#else>
			<tr>
			<td></td>
		 	<td>No facility found</td>
		 	</tr>
		 </#if>	
	</table>
</form>