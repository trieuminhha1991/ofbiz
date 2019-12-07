<#macro showMessage communicationEvent isSentMessage index>
  <#if communicationEvent.partyIdFrom?has_content>
    <#assign partyNameFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, communicationEvent.partyIdFrom, true)>
  <#else/>
    <#assign partyNameFrom = "${uiLabelMap.CommonNA}">
  </#if>
  <#if communicationEvent.partyIdTo?has_content>
    <#assign partyNameTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, communicationEvent.partyIdTo, true)>
  <#else/>
    <#assign partyNameTo = "${uiLabelMap.CommonNA}">
  </#if>
		<tr>
			<td><div>${partyNameFrom}</div></td>
			<td><div>${partyNameTo}</div></td>
			<td><div>${communicationEvent.subject?default("")}</div></td>
			<td><div>${communicationEvent.entryDate}</div></td>
			<td align="right">
				<form method="post" action="<@ofbizUrl>readmessage</@ofbizUrl>" name="ecomm_read_mess${index}">
					<input name="communicationEventId" value="${communicationEvent.communicationEventId}" type="hidden"/>
				</form>
					<a href="javascript:document.ecomm_read_mess${index}.submit()">${uiLabelMap.ObbRead}</a>
				<#if isSentMessage>
				<form method="post" action="<@ofbizUrl>newmessage</@ofbizUrl>" name="ecomm_sent_mess${index}">
					<input name="communicationEventId" value="${communicationEvent.communicationEventId}" type="hidden"/>
				</form>
				<span style="padding-left:5px;padding-right:5px;">|</span><a href="javascript:document.ecomm_sent_mess${index}.submit()">${uiLabelMap.PartyReply}</a>
				</#if>
			</td>
		</tr>
</#macro>
<div id="jm-main">
	<div class="inner clearfix">
		<div id="jm-current-content" class="clearfix dashboard account-create">
			<div class="page-title no-margin-bottom">
		        <h1 class='account-title'>${uiLabelMap.CommonMessages}</h1>
		    </div>
		    <div class="account-form">
			    <div class="box">
			        <div class="box-title">
			            <h3><#if parameters.showSent?has_content && parameters.showSent=="true">${uiLabelMap.BEMessageSent}<#else>${uiLabelMap.BEMessageReceived}</#if></h3>
			            <#if parameters.showSent?if_exists == "true">
			              <a href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.ObbViewReceivedOnly}</a>
			            <#else>
			              <a href="<@ofbizUrl>messagelist?showSent=true</@ofbizUrl>">${uiLabelMap.ObbViewSent}</a>
			            </#if>
			        </div>
			    </div>
			    <div class="screenlet-body">
					<style type="text/css">
						table td{
							padding-top:6px;
						}
					</style>
				        <table width="100%" border="0" cellpadding="1">
				          <#if (!receivedCommunicationEvents?has_content && !sentCommunicationEvents?has_content)>
				            <tr><td><div>${uiLabelMap.ObbNoMessages}.</div></td></tr>
				          <#else/>
				            <tr>
				              <td><div class="tableheadtext">${uiLabelMap.CommonFrom}</div></td>
				              <td><div class="tableheadtext">${uiLabelMap.CommonTo}</div></td>
				              <td><div class="tableheadtext">${uiLabelMap.ObbSubject}</div></td>
				              <td><div class="tableheadtext">${uiLabelMap.ObbSentDate}</div></td>
				              <td>&nbsp;</td>
				            </tr>
				            <tr><td colspan="5"><hr /></td></tr>
				            <#list receivedCommunicationEvents?if_exists as receivedCommunicationEvent>
				              <@showMessage communicationEvent=receivedCommunicationEvent isSentMessage=false index=receivedCommunicationEvent_index/>
				            </#list>
				            <#list sentCommunicationEvents?if_exists as sentCommunicationEvent>
				              <@showMessage communicationEvent=sentCommunicationEvent isSentMessage=true index=sentCommunicationEvent_index/>
				            </#list>
				          </#if>
				        </table>
				    </div>
			</div>
		</div>
	</div>
</div>
