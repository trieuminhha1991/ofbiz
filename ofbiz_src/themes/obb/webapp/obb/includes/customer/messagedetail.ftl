<#assign delegator = requestAttributes.delegator>
<#if communicationEvent.partyIdFrom?exists>
    <#assign fromName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, communicationEvent.partyIdFrom, true)>
</#if>
<#if communicationEvent.partyIdTo?exists>
    <#assign toName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, communicationEvent.partyIdTo, true)>
</#if>
<div id="jm-main">
	<div class="inner clearfix">
		<div id="jm-current-content" class="clearfix dashboard">
			<div class="page-title">
		        <h1>${uiLabelMap.CommonMessages}</h1>
		    </div>
		    <div class="box">
		        <div class="box-title">
		            <h3>${uiLabelMap.ObbReadMessage}</h3>
		            <#if (communicationEvent.partyIdFrom?if_exists != (userLogin.partyId)?if_exists)>
		              <a href="<@ofbizUrl>newmessage?communicationEventId=${communicationEvent.communicationEventId}</@ofbizUrl>" class="submenutext">${uiLabelMap.PartyReply}</a> <span style="padding-left:5px;padding-right:5px;">|</span>
		            </#if>
		            <a href="<@ofbizUrl>messagelist</@ofbizUrl>" class="submenutextright">${uiLabelMap.ObbViewList}</a>
		        </div>
		    </div>
			<div class="screenlet">
			    <div class="screenlet-body">
				<style type="text/css">
					table td{
						padding-top:6px;
					}
				</style>
			        <table width="100%" border="0" cellpadding="1">
			          <tr>
			              <td align="right"><div class="tableheadtext">${uiLabelMap.CommonFrom}:</div></td>
			              <td><div>${fromName?if_exists}</div></td>
			          </tr>
			          <tr>
			              <td align="right"><div class="tableheadtext">${uiLabelMap.CommonTo}:</div></td>
			              <td><div>${toName?if_exists}</div></td>
			          </tr>
			          <tr>
			              <td align="right"><div class="tableheadtext">${uiLabelMap.CommonDate}:</div></td>
			              <td><div>${communicationEvent.entryDate}</div></td>
			          </tr>
			          <tr>
			              <td align="right"><div class="tableheadtext">${uiLabelMap.ObbSubject}:</div></td>
			              <td><div>&nbsp;${(communicationEvent.subject)?default("[${uiLabelMap.ObbNoSubject}]")}</div></td>
			          </tr>
			          <tr><td>&nbsp;</td></tr>
			          <tr>
			            <td>&nbsp;Content</td>
			            <td>
			              <div>${StringUtil.wrapString(communicationEvent.content)?default("[${uiLabelMap.ObbEmptyBody}]")}</div>
			            </td>
			          </tr>
			        </table>
			    </div>
			</div>
		</div>
	</div>
</div>