<form name="filtersalesmanform" id="filtersalesmanform" action="<@ofbizUrl>filtersalesmanform</@ofbizUrl>">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align='right' valign='middle' nowrap="nowrap">${uiLabelMap.CommonUserLoginId}</td>
			<td valign="middle">
			 	<span class="label label-large label-success arrowed-right">
					<i class="icon-key"></i>
					${parameters.userLogin.userLoginId}
				</span>
			</td>
		</tr>
		
		  <#if parameters.rsmRole>
		  <tr>
			<td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.RSM}</div></td>
			<td valign='middle'>
				<div style="margin-left:7px;" id="rsmId">
					 <select name="rsmID" onchange="ajaxUpdateArea('asmId', 'getASMs', jQuery('#filtercustomerform').serialize());">
					         <option value="">&nbsp</option>
					         <#list parameters.rsmList as rsm>
					              <option value="${rsm.partyId}">${rsm.groupName?if_exists}</option>
					         </#list>
					 </select>
				</div>
			 </td>
		  </tr>
		  </#if>
		  
		  <#if parameters.asmRole>
		  <tr>
			<td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.ASM}</div></td>
			<td valign='middle'>
				<div style="margin-left:7px;" id="asmId">
					 <select name="asmID" onchange="ajaxUpdateArea('salessupId', 'getSalessups', jQuery('#filtercustomerform').serialize());">
					 		<option value="">&nbsp</option>
					         <#list parameters.asmList as asm>
					              <option value="${asm.partyId}">${asm.groupName?if_exists}</option>
					         </#list>
					 </select>
				</div>
			 </td>
		  </tr>
		  </#if>
		  
		  <#if parameters.salessupRole>
		  <tr>
			<td align='right' valign='middle' nowrap="nowrap"><div class='tableheadtext'>${uiLabelMap.Salessup}</div></td>
			<td valign='middle'>
				<div style="margin-left:7px;" id="salessupId">
					 <select name="salessupID" >
					          <option value="">&nbsp</option>
					         <#list parameters.salessupList as salessup>
					              <option value="${salessup.partyId}">${salessup.groupName?if_exists}</option>
					         </#list>
					 </select>
				</div>
			 </td>
		  </tr>
		  </#if>
		  
		  <tr>
			 <td></td>
			 <td>
			 	<a style="margin-left:7px;" class="btn btn-primary btn-small margin-left3" onclick="ajaxUpdateArea('salesmanList', 'filtersalesmanform', jQuery('#filtersalesmanform').serialize());"> 
				 	<i class="icon-search"></i>
				 	${uiLabelMap.CommonFind}
				 </a>
			  </td>
			</tr>
	</table>
</form>