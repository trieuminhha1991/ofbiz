<#if inProcess?exists>
<div id="transitions-tab" class="tab-pane">
	<h4 class="smaller lighter green" style="display:inline-block">
		<#-- <i class="fa-file"></i> -->
		${uiLabelMap.OrderProcessingStatus} - ${uiLabelMap.OrderProcessingTransitions}
	</h4>
		<div class="screenlet">
		  <div class="screenlet-title-bar">
		    <ul>
		      <li class="h3">${uiLabelMap.OrderProcessingStatus}</li>
		    </ul>
		    <br class="clear"/>
		  </div>
		  <div class="screenlet-body">
		    <table class="basic-table" cellspacing='0'>
		      <tr>
		        <td>
		          <!-- Suspended Processes -->
		          <#if workEffortStatus == "WF_SUSPENDED">
		            <form action="<@ofbizUrl>releasehold</@ofbizUrl>" method="post" name="activityForm">
		              <input type="hidden" name="workEffortId" value="${workEffortId}" />
		              <table class="basic-table" cellspacing='0'>
		                <tr>
		                  <td>${uiLabelMap.OrderProcessingInHold}&nbsp;${uiLabelMap.OrderProcessingInHoldNote}</td>
		                  <td align="right" valign="center">
		                    <a href="javascript:document.activityForm.submit()" class="buttontext">${uiLabelMap.OrderRelease}</a>
		                  </td>
		                </tr>
		              </table>
		            </form>
		          </#if>
		          <!-- Active Processes -->
		          <#if workEffortStatus == "WF_RUNNING">
		            <form action="<@ofbizUrl>holdorder</@ofbizUrl>" method="post" name="activityForm">
		              <input type="hidden" name="workEffortId" value="${workEffortId}" />
		              <table class="basic-table" cellspacing='0'>
		                <tr>
		                  <td>${uiLabelMap.OrderProcessingInActive}</td>
		                  <td align="right" valign="center">
		                    <a href="javascript:document.activityForm.submit()" class="buttontext">${uiLabelMap.OrderHold}</a>
		                  </td>
		                </tr>
		              </table>
		            </form>
		          </#if>
		        </td>
		      </tr>
		    </table>
		  </div>
		</div>
		</#if>
		<br />
		<#if wfTransitions?exists && wfTransitions?has_content>
		<div class="screenlet">
		  <div class="screenlet-title-bar">
		    <ul>
		      <li class="h3">${uiLabelMap.OrderProcessingTransitions}</li>
		    </ul>
		    <br class="clear"/>
		  </div>
		  <div class="screenlet-body">
		    <table class="basic-table" cellspacing='0'>
		      <tr>
		        <td>
		          <form action="<@ofbizUrl>completeassignment</@ofbizUrl>" method="post" name="transitionForm">
		            <input type="hidden" name="workEffortId" value="${workEffortId}" />
		            <input type="hidden" name="partyId" value="${assignPartyId}" />
		            <input type="hidden" name="roleTypeId" value="${assignRoleTypeId}" />
		            <input type="hidden" name="fromDate" value="${fromDate}" />
		            <table class="basic-table" cellspacing='0'>
		              <tr>
		                <td>
		                  <select name="approvalCode">
		                    <#list wfTransitions as trans>
		                      <#if trans.extendedAttributes?has_content>
		                        <#assign attrs = Static["org.ofbiz.base.util.StringUtil"].strToMap(trans.extendedAttributes)>
		                        <#if attrs.approvalCode?exists>
		                          <option value="${attrs.approvalCode}">${trans.transitionName}</option>
		                        </#if>
		                      </#if>
		                    </#list>
		                  </select>
		                </td>
		                <td valign="center">
		                  <a href="javascript:document.transitionForm.submit()" class="buttontext">${uiLabelMap.CommonContinue}</a>
		                </td>
		              </tr>
		            </table>
		          </form>
		        </td>
		      </tr>
		    </table>
</div><!--#transitions-tab-->
</#if>