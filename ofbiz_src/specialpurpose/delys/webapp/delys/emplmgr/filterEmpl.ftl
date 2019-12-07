<#if parameters.roleTypeId?has_content>

<#if (hasCreatedCSM && (parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == ''))
|| (hasCreatedRSM && (parameters.roleTypeId == 'DELYS_CSM'))
|| (hasCreatedASM && (parameters.roleTypeId == 'DELYS_RSM'))
|| (hasCreatedCUSTOMER && (parameters.roleTypeId == 'DELYS_ASM'))
>
<div class="widget-box olbius-extra" style="margin-top: 10px">
	
  	<div class="widget-header widget-header-small header-color-blue2">
  	<#if (parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && hasCreatedCSM><h6>${uiLabelMap.NewCSM}</h6></#if>
  	<#if (parameters.roleTypeId == 'DELYS_CSM' && hasCreatedRSM) || ((parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && !hasCreatedCSM && hasCreatedRSM)><h6>${uiLabelMap.NewRSM}</h6></#if>
  	<#if (parameters.roleTypeId == 'DELYS_RSM' && hasCreatedASM) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM') && !hasCreatedCSM && !hasCreatedRSM && hasCreatedASM)><h6>${uiLabelMap.NewASM}</h6></#if>
  	<#if (parameters.roleTypeId == 'DELYS_ASM' && hasCreatedROUTE) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_ASM') && !hasCreatedCSM && !hasCreatedRSM && !hasCreatedASM && hasCreatedROUTE)><h6>${uiLabelMap.NewRoute}</h6></#if>
  	<#if (parameters.roleTypeId == 'DELYS_CUSTOMER' && hasCreatedCUSTOMER) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_ASM' || parameters.roleTypeId == 'DELYS_CUSTOMER') && !hasCreatedCSM && !hasCreatedRSM && !hasCreatedASM && !hasCreatedROUTE && hasCreatedCUSTOMER)><h6>${uiLabelMap.NewCustomer}</h6></#if>
  	
    	<div class="widget-toolbar">
    		<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
  	</div>
	 <div class="widget-body">
	 <div class="widget-body-inner">
	 <div class="widget-main">
	<form name="filterEmpl" method="post" id="filterEmpl" action="<@ofbizUrl>CreatePartyGroup</@ofbizUrl>">
	<#assign roleType =  roleTypeId/>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0" name="searchArea">
		<#if parameters.role == 'DELYS_ADMIN' || parameters.role == 'DELYS_CSM'>
		<#if parameters.roleTypeId == 'DELYS_ASM' || parameters.roleTypeId == 'DELYS_CUSTOMER' || parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_CSM'>
		<#assign roleType =  'DELYS_CSM'/>
		<tr>
			<td align='right' valign='middle' nowrap="nowrap" style="padding-right: 12px"><div class='tableheadtext'>CSM</div></td>
			<td>&nbsp;</td>
			<td valign='middle'>
				<div id="csmId">
				
					 <select name="csmId" onchange="ajaxUpdateArea('rsmId', 'getRSMsEmpl', jQuery('#filterEmpl').serialize());">
					         <#if parameters.status == '1'>
					         <option value="NA"></option>
					         <#list parameters.listCSM as csm>
					         	<option value="${csm.partyIdTo}">${csm.groupName?if_exists}</option>
					         </#list>
					         <#else>
					         <#list parameters.listCSM as csm>
					         	<#if csm.partyIdTo == parameters.csmId>
					         	<option selected value="${csm.partyIdTo}">${csm.groupName?if_exists}</option>
					         	<#else>
					         	<option value="${csm.partyIdTo}">${csm.groupName?if_exists}</option>
					         	</#if>
					         </#list>
							</#if>					         
					 </select>
				</div>
			 </td>
		  </tr>
		  </#if>
		  </#if>
		  <#if parameters.role == 'DELYS_CSM' || parameters.role == 'DELYS_ADMIN' || parameters.role == 'DELYS_RSM'>
		  <#if parameters.roleTypeId == 'DELYS_ASM' || parameters.roleTypeId == 'DELYS_CUSTOMER' || parameters.roleTypeId == 'DELYS_RSM' || (parameters.roleTypeId == 'DELYS_CSM' && (hasCreatedASM || hasCreatedROUTE || hasCreatedCUSTOMER) && !hasCreatedRSM)>
		  <#assign roleType =  'DELYS_RSM'/>
		  <tr>
			<td align='right' valign='middle' nowrap="nowrap" style="padding-right: 12px"><div class='tableheadtext'>RSM</div></td>
			<td>&nbsp;</td>
			<td valign='middle'>
				<div id="rsmId">
					 <select name="rsmId" onchange="ajaxUpdateArea('asmId', 'getASMsEmpl', jQuery('#filterEmpl').serialize());">
						<#if parameters.status == '1'>
					         <option value="NA" selected></option>
					         <#list parameters.listRSM as rsm>
					         	<option value="${rsm.partyIdTo}">${rsm.groupName?if_exists}</option>
					         </#list>
					         <#else>
					         <#list parameters.listRSM as rsm>
					         	<#if rsm.partyIdTo == parameters.rsmId>
					         	<option selected value="${rsm.partyIdTo}">${rsm.groupName?if_exists}</option>
					         	<#else>
					         	<option value="${rsm.partyIdTo}">${rsm.groupName?if_exists}</option>
					         	</#if>
					         </#list>
								</#if>		
					 </select>
				</div>
			 </td>
		  </tr>
		  </#if>
		  </#if>
		  <#if parameters.role == 'DELYS_RSM' || parameters.role == 'DELYS_CSM' || parameters.role == 'DELYS_ADMIN' || parameters.role == 'DELYS_ASM'>
		  <#if parameters.roleTypeId == 'DELYS_ASM' || parameters.roleTypeId == 'DELYS_CUSTOMER' || ((parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_CSM') && (!hasCreatedASM && !hasCreatedRSM && (hasCreatedROUTE || hasCreatedCUSTOMER)))>
		  <#assign roleType =  'DELYS_ASM'/>
		  <tr>
			<td align='right' valign='middle' nowrap="nowrap" style="padding-right: 12px"><div class='tableheadtext'>ASM</div></td>
			<td>&nbsp;</td>
			<td valign='middle'>
				<div id="asmId">
					 <select name="asmId" onchange="ajaxUpdateArea('routeId', 'getRoutesEmpl', jQuery('#filterEmpl').serialize());">
					 	<#if parameters.status == '1'>
					         <option value="NA" selected></option>
					         <#list parameters.listASM as asm>
					         	<option value="${asm.partyIdTo}">${asm.groupName?if_exists}</option>
					         </#list>
					         <#else>
					         <#list parameters.listASM as asm>
					         	<#if asm.partyIdTo == parameters.asmId>
					         	<option selected value="${asm.partyIdTo}">${asm.groupName?if_exists}</option>
					         	<#else>
					         	<option value="${asm.partyIdTo}">${asm.groupName?if_exists}</option>
					         	</#if>
					         </#list>
								</#if>		
					 </select>
				</div>
			 </td>
		  </tr>
		  </#if>
		  </#if>
		  <#if parameters.roleTypeId == 'DELYS_CUSTOMER' || ((parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_ASM') && (!hasCreatedASM && !hasCreatedRSM && !hasCreatedASM && hasCreatedCUSTOMER))>
		  <#assign roleType =  'DELYS_CUSTOMER'/>
		  <tr>
			<td align='right' valign='middle' nowrap="nowrap" style="padding-right: 12px"><div class='tableheadtext'>Route</div></td>
			<td>&nbsp;</td>
			<td valign='middle'>
				<div id="routeId">
					 <select name="routeId">
					 <#if parameters.status == '1'>
				         <option value="NA" selected></option>
				         <#list parameters.listROUTE as route>
				         	<option value="${route.partyIdTo}">${route.groupName?if_exists}</option>
				         </#list>
				         <#else>
				         <#list parameters.listROUTE as route>
				         	<#if route.partyIdTo == parameters.routeId>
				         	<option selected="true" value="${route.partyIdTo}">${route.groupName?if_exists}</option>
				         	<#else>
				         	<option value="${route.partyIdTo}">${route.groupName?if_exists}</option>
				         	</#if>
				         </#list>
					</#if>		
					 </select>
				</div>
			 </td>
		  </tr>
		  </#if>
		  <tr>
		  <td align='right' valign='middle' class='tableheadtext'>
          	<#if parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == ''>${uiLabelMap.csmName}</#if>
          	<#if parameters.roleTypeId == 'DELYS_CSM'>${uiLabelMap.rsmName}</#if>
          	<#if parameters.roleTypeId == 'DELYS_RSM'>${uiLabelMap.asmName}</#if>
          	<#if parameters.roleTypeId == 'DELYS_ASM'>${uiLabelMap.routeName}</#if>
          	<#if parameters.roleTypeId == 'DELYS_CUSTOMER'>${uiLabelMap.custName}</#if>
          </td>
          <td>&nbsp;</td>
          <td align='left'>
            <input type='text' size='15' maxlength='100' name='groupName' value=""/>
          </td>
		  </tr>
		  <tr>
		  <td align='right' valign='middle' class='tableheadtext'>
          	${uiLabelMap.CommonDescription}
          </td>
          <td>&nbsp;</td>
          <td align='left'>
            <input type='text' size='15' maxlength='100' name='description' value=""/>
          </td>
		  </tr>
		  <tr>
		  <td>&nbsp;</td>
		  <td>&nbsp;</td>
		  	<input type='hidden' name='roleTypeId' value='${roleType}'/>
			<input type='hidden' name='role' value='${parameters.role}'/>
          <td>
          	<button class="btn btn-primary btn-small" type="submit"><i class="icon-save"></i> ${uiLabelMap.CommonSave}</button>
          </td>
		  </tr>
	</table>
</form>


</div>
</div>
</div>
</div>
</#if>
<#if hasViewPermission>
<div class="widget-box olbius-extra" style="margin-top: 10px">
  	
  	<div class="widget-header widget-header-small header-color-blue2">
  	<#if (parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && hasCSMPermission><h6>${uiLabelMap.listCSM}</h6>
  	<#assign id = uiLabelMap.csmId/>
  	<#assign name = uiLabelMap.csmName/>
  	</#if>
  	<#if (parameters.roleTypeId == 'DELYS_CSM' && hasRSMPermission) || ((parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && !hasCSMPermission && hasRSMPermission)><h6>${uiLabelMap.listRSM}</h6>
  	<#assign id = uiLabelMap.rsmId/>
  	<#assign name = uiLabelMap.rsmName/>
  	</#if>
  	<#if (parameters.roleTypeId == 'DELYS_RSM' && hasASMPermission) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM') && !hasCSMPermission && !hasRSMPermission && hasASMPermission)><h6>${uiLabelMap.listASM}</h6> 
  	<#assign id = uiLabelMap.asmId/>
  	<#assign name = uiLabelMap.asmName/>
  	</#if>
  	<#if (parameters.roleTypeId == 'DELYS_ASM' && hasROUTEPermission) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_ASM') && !hasCSMPermission && !hasRSMPermission && !hasASMPermission && hasROUTEPermission)><h6>${uiLabelMap.listRoute}</h6> 
  	<#assign id = uiLabelMap.routeId/>
  	<#assign name = uiLabelMap.routeName/>
  	</#if>
  	<#if (parameters.roleTypeId == 'DELYS_CUSTOMER' && hasCUSTOMERPermission) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_ASM' || parameters.roleTypeId == 'DELYS_CUSTOMER') && !hasCSMPermission && !hasRSMPermission && !hasASMPermission && !hasROUTEPermission && hasCUSTOMERPermission)><h6>${uiLabelMap.listCust}</h6> 
  	<#assign id = uiLabelMap.custId/>
  	<#assign name = uiLabelMap.custName/>
  	</#if>
  	
    	<div class="widget-toolbar">
    		<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
  	</div>
	 <div class="widget-body">
	 <div class="widget-body-inner">
	 <div class="widget-main">
	 
	<table width="100%" border="0" cellspacing="0" cellpadding="0" name="searchArea" class="table table-hover table-striped table-bordered dataTable">
		<tr>
			<td>
				${id}
			</td>
			<td>
				${name}
			</td>
			<td>
				${uiLabelMap.CommonFromDate}
			</td>
			<td>
				${uiLabelMap.CommonThruDate}
			</td>
			<td>
				${uiLabelMap.CommonDescription}
			</td>
			<#if ((parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && hasDeletedCSM) || (parameters.roleTypeId == 'DELYS_CSM' && hasDeletedRSM) || (parameters.roleTypeId == 'DELYS_RSM' && hasDeletedASM) || (parameters.roleTypeId == 'DELYS_ASM' && hasDeletedROUTE) || (parameters.roleTypeId == 'DELYS_CUSTOMER' && hasDeletedCUSTOMER)>
			<td>
			</td>
			</#if>
		</tr>
	
	<#if (parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && hasCSMPermission><#assign listData = parameters.listCSM/></#if>
  	<#if (parameters.roleTypeId == 'DELYS_CSM' && hasRSMPermission) || ((parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && !hasCSMPermission && hasRSMPermission)><#assign listData = parameters.listRSM/></#if>
  	<#if (parameters.roleTypeId == 'DELYS_RSM' && hasASMPermission) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM') && !hasCSMPermission && !hasRSMPermission && hasRSMPermission)><#assign listData = parameters.listASM/></#if>
  	<#if (parameters.roleTypeId == 'DELYS_ASM' && hasROUTEPermission) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_ASM') && !hasCSMPermission && !hasRSMPermission && !hasASMPermission && hasROUTEPermission)><#assign listData = parameters.listROUTE/></#if>
  	<#if (parameters.roleTypeId == 'DELYS_CUSTOMER' && hasCreatedCUSTOMER) || ((parameters.roleTypeId == 'DELYS_CSM' || parameters.roleTypeId == 'DELYS_RSM' || parameters.roleTypeId == 'DELYS_ASM' || parameters.roleTypeId == 'DELYS_CUSTOMER') && !hasCSMPermission && !hasRSMPermission && !hasASMPermission && !hasROUTEPermission && hasCreatedCUSTOMER)><#assign listData = parameters.listCUSTOMER/></#if>
	<#if listData?has_content>
	
	<#list listData as item>
	<tr>
	<#if ((parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && hasUpdatedCSM) || (parameters.roleTypeId == 'DELYS_CSM' && hasUpdatedRSM) 
	|| (parameters.roleTypeId == 'DELYS_RSM' && hasUpdatedASM) || (parameters.roleTypeId == 'DELYS_ASM' && hasUpdatedROUTE) || (parameters.roleTypeId == 'DELYS_CUSTOMER' && hasUpdatedCUSTOMER)>
	<td >
		<a class="btn btn-mini btn-info" href="/partymgr/control/viewprofile?partyId=${item.partyIdTo?if_exists}">${item.partyIdTo?if_exists}</a>
	</td>
	<#else>
	<td >
		${item.partyIdTo?if_exists}
	</td>
	</#if>
	<td>
	 ${item.groupName?if_exists}
	</td>
	<td>
	 ${item.fromDate?if_exists}
	</td>
	<td>
	 ${item.thruDate?if_exists}
	</td>
	<td>
	${item.comments?if_exists}
	</td>
	
	<#if ((parameters.roleTypeId == 'DELYS_ADMIN' || parameters.roleTypeId == '') && hasDeletedCSM) || (parameters.roleTypeId == 'DELYS_CSM' && hasDeletedRSM) 
	|| (parameters.roleTypeId == 'DELYS_RSM' && hasDeletedASM) || (parameters.roleTypeId == 'DELYS_ASM' && hasDeletedROUTE) || (parameters.roleTypeId == 'DELYS_CUSTOMER' && hasDeletedCUSTOMER)>
		<td style="width:10%">
			<form style="display:hidden" method="post" action="<@ofbizUrl>deletePartyRelationshipDelys</@ofbizUrl>">
				<input type='hidden' name='partyIdFrom' value='${item.partyIdFrom?if_exists}'/>
				<input type='hidden' name='partyIdTo' value='${item.partyIdTo?if_exists}'/>
				<input type='hidden' name='roleTypeIdFrom' value='${item.roleTypeIdFrom?if_exists}'/>
				<input type='hidden' name='roleTypeId' value='${parameters.roleTypeId}'/>
				<input type='hidden' name='role' value='${parameters.role}'/>
				<input type='hidden' name='roleTypeIdTo' value='${item.roleTypeIdTo?if_exists}'/>
				<input type='hidden' name='fromDate' value='${item.fromDate?if_exists}'/>
				<button class="btn btn-danger btn-mini"  type="submit"><i class="icon-trash"></i> ${uiLabelMap.CommonDelete}</button>
			</form>
		</td>
	</#if>
    </tr>
     
	</#list>
	
	<#else>
	</#if>
	
	</table>
	
</div>
</div>
</div>
</div>	
</#if>
</#if>
