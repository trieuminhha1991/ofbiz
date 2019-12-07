<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<div class="widget-box transparent no-bottom-border">
<div class="widget-body padding-top12 padding-bottom12 padding-left12">
<#if sessionAttributes.recentArtifactInfoList?has_content>
  <div class="right">
    <h3>Recently Viewed Artifacts:</h3>
    <#assign highRef = sessionAttributes.recentArtifactInfoList.size() - 1/>
    <#if (highRef > 19)><#assign highRef = 19/></#if>
    <#list sessionAttributes.recentArtifactInfoList[0..highRef] as recentArtifactInfo>
        <div>${recentArtifactInfo_index + 1} - ${recentArtifactInfo.type}: <@displayArtifactInfoLink type=recentArtifactInfo.type uniqueId=recentArtifactInfo.uniqueId displayName=recentArtifactInfo.displayName/></div>
    </#list>
  </div>
</#if>

<#if !artifactInfo?exists>

    <#-- add form here to specify artifact info name. -->
    <div style="margin-top:10px;">
      <form name="ArtifactInfoByName" method="post" action="<@ofbizUrl>ArtifactInfo</@ofbizUrl>" class="basic-form">
        Search Names/Locations: <input type="text" name="name" value="${parameters.name?if_exists}" size="40"/>
        <select name="type" style="margin-left: 23px;">
          <option></option>
          <option>entity</option>
          <option>service</option>
          <option>form</option>
          <option>screen</option>
          <option>request</option>
          <option>view</option>
        </select>
        <button type="submit" name="submitButton" class="btn btn-small btn-purple" style="">
        <i class="icon-search"></i>
        Find
        </button>
        <br/>
        <input type="hidden" name="findType" value="search"/>
        
      </form>
    </div>
    <hr/>
    <div>
      <form name="ArtifactInfoByNameAndType" method="post" action="<@ofbizUrl>ArtifactInfo</@ofbizUrl>" class="basic-form">
        Name: <input type="text" name="name" value="${parameters.name?if_exists}" size="60" style=""/>
        Location: <input type="text" name="location" value="${parameters.location?if_exists}" size="60"/>
        Type:
          <select name="type">
            <option>entity</option>
            <option>service</option>
            <option>form</option>
            <option>screen</option>
            <option>request</option>
            <option>view</option>
          </select>
         <button type="submit" name="submitButton" class="btn btn-small btn-purple" style="">
          <i class="icon-search"></i>
          Lookup
          </button>
      </form>
    </div>

    <#-- add set of ArtifactInfo if there is not a single one identified, with link to each -->
    <#if artifactInfoSet?has_content>
    <div>
        <h3>Multiple Artifacts Found:</h3>
        <#list artifactInfoSet as curArtifactInfo>
            <div>${curArtifactInfo.getDisplayType()}: <@displayArtifactInfo artifactInfo=curArtifactInfo/></div>
        </#list>
    </div>
    </#if>

<#else/>

    <h3>${uiLabelMap.WebtoolsArtifactInfo} (${artifactInfo.getDisplayType()}): ${artifactInfo.getDisplayName()}</h3>
    <#if artifactInfo.getLocationURL()?exists>
        <div>Defined in: <a href="${artifactInfo.getLocationURL()}">${artifactInfo.getLocationURL()}</a></div>
    </#if>

    <#if artifactInfo.getType() == "entity">
        <div><a href="<@ofbizUrl>FindGeneric?entityName=${artifactInfo.modelEntity.getEntityName()}&amp;find=true&amp;VIEW_SIZE=50&amp;VIEW_INDEX=0</@ofbizUrl>">All Entity Data</a></div>
        <h3>Entity Fields</h3>
        <table class="table table-striped table-hover table-bordered dataTable">
        <#list artifactInfo.modelEntity.getFieldsUnmodifiable() as modelField>
            <tr><td>${modelField.getName()}<#if modelField.getIsPk()>*</#if></td><td>${modelField.getType()}</td><td>${modelField.getDescription()?if_exists}</td></tr>
        </#list>
        </table>

        <div>
        <h3>Entities Related (One)</h3>
        <#list artifactInfo.getEntitiesRelatedOne()?if_exists as entityArtifactInfo>
            <@displayEntityArtifactInfo entityArtifactInfo=entityArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Entities Related (Many)</h3>
        <#list artifactInfo.getEntitiesRelatedMany()?if_exists as entityArtifactInfo>
            <@displayEntityArtifactInfo entityArtifactInfo=entityArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Services Using This Entity</h3>
        <#list artifactInfo.getServicesUsingEntity()?if_exists as serviceArtifactInfo>
            <@displayServiceArtifactInfo serviceArtifactInfo=serviceArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Forms Using This Entity</h3>
        <#list artifactInfo.getFormsUsingEntity()?if_exists as formWidgetArtifactInfo>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=formWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Screens Using This Entity</h3>
        <#list artifactInfo.getScreensUsingEntity()?if_exists as screenWidgetArtifactInfo>
            <@displayScreenWidgetArtifactInfo screenWidgetArtifactInfo=screenWidgetArtifactInfo/>
        </#list>
        </div>

    <#elseif artifactInfo.getType() == "service"/>
        <h3>Service Info</h3>
        <div>&nbsp;Description: ${artifactInfo.modelService.description}</div>
        <div>&nbsp;Run (${artifactInfo.modelService.engineName}): ${artifactInfo.modelService.location} :: ${artifactInfo.modelService.invoke}</div>
        <div>&nbsp;Impl Location: <a href="${artifactInfo.getImplementationLocationURL()?if_exists}">${artifactInfo.getImplementationLocationURL()?if_exists}</a></div>
        <h3>Service Parameters</h3>
        <table class="table table-striped table-hover table-bordered dataTable">
            <tr><td>Name</td><td>Type</td><td>Optional</td><td>Mode</td><td>Entity.field</td></tr>
        <#list artifactInfo.modelService.getAllParamNames() as paramName>
            <#assign modelParam = artifactInfo.modelService.getParam(paramName)/>
            <tr><td>${modelParam.getName()}<#if modelParam.getInternal()> (internal)</#if></td><td>${modelParam.getType()}</td><td><#if modelParam.isOptional()>optional<#else/>required</#if></td><td>${modelParam.getMode()}</td><td>${modelParam.getEntityName()?if_exists}.${modelParam.getFieldName()?if_exists}</td></tr>
        </#list>
        </table>

        <div>
        <h3>Entities Used By This Service</h3>
        <#list artifactInfo.getEntitiesUsedByService()?if_exists as entityArtifactInfo>
            <@displayEntityArtifactInfo entityArtifactInfo=entityArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Services Calling This Service</h3>
        <#list artifactInfo.getServicesCallingService()?if_exists as serviceArtifactInfo>
            <@displayServiceArtifactInfo serviceArtifactInfo=serviceArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Services Called By This Service</h3>
        <#list artifactInfo.getServicesCalledByService()?if_exists as serviceArtifactInfo>
            <@displayServiceArtifactInfo serviceArtifactInfo=serviceArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Service ECA Rules Triggered By This Service</h3>
        <#list artifactInfo.getServiceEcaRulesTriggeredByService()?if_exists as serviceEcaArtifactInfo>
            <@displayServiceEcaArtifactInfo serviceEcaArtifactInfo=serviceEcaArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Service ECA Rules Calling This Service</h3>
        <#list artifactInfo.getServiceEcaRulesCallingService()?if_exists as serviceEcaArtifactInfo>
            <@displayServiceEcaArtifactInfo serviceEcaArtifactInfo=serviceEcaArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Forms Calling This Service</h3>
        <#list artifactInfo.getFormsCallingService()?if_exists as formWidgetArtifactInfo>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=formWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Forms Based On This Service</h3>
        <#list artifactInfo.getFormsBasedOnService()?if_exists as formWidgetArtifactInfo>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=formWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Screens Calling This Service</h3>
        <#list artifactInfo.getScreensCallingService()?if_exists as screenWidgetArtifactInfo>
            <@displayScreenWidgetArtifactInfo screenWidgetArtifactInfo=screenWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Requests with Events That Call This Service</h3>
        <#list artifactInfo.getRequestsWithEventCallingService()?if_exists as controllerRequestArtifactInfo>
            <@displayControllerRequestArtifactInfo controllerRequestArtifactInfo=controllerRequestArtifactInfo/>
        </#list>
        </div>

    <#elseif artifactInfo.getType() == "form"/>
        <div>
        <h3>Form Extended by This Form</h3>
        <#if artifactInfo.getFormThisFormExtends()?exists>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=artifactInfo.getFormThisFormExtends()/>
        </#if>
        </div>

        <div>
        <h3>Entities Used in This Form</h3>
        <#list artifactInfo.getEntitiesUsedInForm()?if_exists as entityArtifactInfo>
            <@displayEntityArtifactInfo entityArtifactInfo=entityArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Services Used in This Form</h3>
        <#list artifactInfo.getServicesUsedInForm()?if_exists as serviceArtifactInfo>
            <@displayServiceArtifactInfo serviceArtifactInfo=serviceArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Forms Extending This Form</h3>
        <#list artifactInfo.getFormsExtendingThisForm()?if_exists as formWidgetArtifactInfo>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=formWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Screens Including This Form</h3>
        <#list artifactInfo.getScreensIncludingThisForm()?if_exists as screenWidgetArtifactInfo>
            <@displayScreenWidgetArtifactInfo screenWidgetArtifactInfo=screenWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Controller Requests That Are Linked to in This Form</h3>
        <#list artifactInfo.getRequestsLinkedToInForm()?if_exists as controllerRequestArtifactInfo>
            <@displayControllerRequestArtifactInfo controllerRequestArtifactInfo=controllerRequestArtifactInfo/>
        </#list>
        </div>
        <div>
        <h3>Controller Requests That Are Targeted By This Form</h3>
        <#list artifactInfo.getRequestsTargetedByForm()?if_exists as controllerRequestArtifactInfo>
            <@displayControllerRequestArtifactInfo controllerRequestArtifactInfo=controllerRequestArtifactInfo/>
        </#list>
        </div>

    <#elseif artifactInfo.getType() == "screen"/>
        <div>
        <h3>Entities Used in This Screen</h3>
        <#list artifactInfo.getEntitiesUsedInScreen()?if_exists as entityArtifactInfo>
            <@displayEntityArtifactInfo entityArtifactInfo=entityArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Services Used in This Screen</h3>
        <#list artifactInfo.getServicesUsedInScreen()?if_exists as serviceArtifactInfo>
            <@displayServiceArtifactInfo serviceArtifactInfo=serviceArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Forms Included in This Screen</h3>
        <#list artifactInfo.getFormsIncludedInScreen()?if_exists as formWidgetArtifactInfo>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=formWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Screens Include in This Screen</h3>
        <#list artifactInfo.getScreensIncludedInScreen()?if_exists as screenWidgetArtifactInfo>
            <@displayScreenWidgetArtifactInfo screenWidgetArtifactInfo=screenWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Screens Including This Screen</h3>
        <#list artifactInfo.getScreensIncludingThisScreen()?if_exists as screenWidgetArtifactInfo>
            <@displayScreenWidgetArtifactInfo screenWidgetArtifactInfo=screenWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Controller Requests That Are Linked to in This Screen</h3>
        <#list artifactInfo.getRequestsLinkedToInScreen()?if_exists as controllerRequestArtifactInfo>
            <@displayControllerRequestArtifactInfo controllerRequestArtifactInfo=controllerRequestArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Controller Views Referring to This Screen</h3>
        <#list artifactInfo.getViewsReferringToScreen()?if_exists as controllerViewArtifactInfo>
            <@displayControllerViewArtifactInfo controllerViewArtifactInfo=controllerViewArtifactInfo/>
        </#list>
        </div>

    <#elseif artifactInfo.getType() == "request"/>
        <#if artifactInfo.getServiceCalledByRequestEvent()?exists>
            <div>
            <h3>Service Called by Request Event</h3>
            <@displayServiceArtifactInfo serviceArtifactInfo=artifactInfo.getServiceCalledByRequestEvent()/>
            </div>
        </#if>

        <div>
        <h3>Forms Referring to This Request</h3>
        <#list artifactInfo.getFormInfosReferringToRequest()?if_exists as formWidgetArtifactInfo>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=formWidgetArtifactInfo/>
        </#list>
        </div>
        <div>
        <h3>Forms Targeting This Request</h3>
        <#list artifactInfo.getFormInfosTargetingRequest()?if_exists as formWidgetArtifactInfo>
            <@displayFormWidgetArtifactInfo formWidgetArtifactInfo=formWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Screens Referring to This Request</h3>
        <#list artifactInfo.getScreenInfosReferringToRequest()?if_exists as screenWidgetArtifactInfo>
            <@displayScreenWidgetArtifactInfo screenWidgetArtifactInfo=screenWidgetArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Requests That Are Responses to This Request</h3>
        <#list artifactInfo.getRequestsThatAreResponsesToThisRequest()?if_exists as controllerRequestArtifactInfo>
            <@displayControllerRequestArtifactInfo controllerRequestArtifactInfo=controllerRequestArtifactInfo/>
        </#list>
        </div>
        
        <div>
        <h3>Requests That This Request is a Responses To</h3>
        <#list artifactInfo.getRequestsThatThisRequestIsResponsTo()?if_exists as controllerRequestArtifactInfo>
            <@displayControllerRequestArtifactInfo controllerRequestArtifactInfo=controllerRequestArtifactInfo/>
        </#list>
        </div>

        <div>
        <h3>Controller Views That Are Responses to This Request</h3>
        <#list artifactInfo.getViewsThatAreResponsesToThisRequest()?if_exists as controllerViewArtifactInfo>
            <@displayControllerViewArtifactInfo controllerViewArtifactInfo=controllerViewArtifactInfo/>
        </#list>
        </div>

    <#elseif artifactInfo.getType() == "view"/>
        <div>
        <h3>Requests That This View is a Responses To</h3>
        <#list artifactInfo.getRequestsThatThisViewIsResponseTo()?if_exists as controllerRequestArtifactInfo>
            <@displayControllerRequestArtifactInfo controllerRequestArtifactInfo=controllerRequestArtifactInfo/>
        </#list>
        </div>

        <#if artifactInfo.getScreenCalledByThisView()?exists>
            <div>
            <h3>Screen Called by This View</h3>
            <@displayScreenWidgetArtifactInfo screenWidgetArtifactInfo=artifactInfo.getScreenCalledByThisView()/>
            </div>
        </#if>

    </#if>
</#if>

<#-- ==================== MACROS ===================== -->
<#macro displayEntityArtifactInfo entityArtifactInfo>
    <div>&nbsp;-&nbsp;<@displayArtifactInfo artifactInfo=entityArtifactInfo/></div>
</#macro>

<#macro displayServiceArtifactInfo serviceArtifactInfo>
    <div>&nbsp;-&nbsp;<@displayArtifactInfo artifactInfo=serviceArtifactInfo/></div>
</#macro>

<#macro displayServiceEcaArtifactInfo serviceEcaArtifactInfo>
    <h3>Service ECA Rule: ${serviceEcaArtifactInfo.getDisplayPrefixedName()}</h3>
    <#if serviceEcaArtifactInfo.serviceEcaRule.getEcaConditionList()?has_content>
        <h3>ECA Rule Conditions</h3>
        <#list serviceEcaArtifactInfo.serviceEcaRule.getEcaConditionList() as ecaCondition>
            <div>&nbsp;-&nbsp;${ecaCondition.getShortDisplayDescription(true)}</div>
        </#list>
    </#if>
    <#if serviceEcaArtifactInfo.serviceEcaRule.getEcaActionList()?has_content>
        <h3>ECA Rule Actions</h3>
        <table class="table table-striped table-hover table-bordered dataTable">
        <#list serviceEcaArtifactInfo.serviceEcaRule.getEcaActionList() as ecaAction>
            <tr>
                <td><a href="<@ofbizUrl>ArtifactInfo?type=${artifactInfo.getType()}&amp;uniqueId=${ecaAction.getServiceName()}</@ofbizUrl>">${ecaAction.getServiceName()}</a></td>
                <td>${ecaAction.getServiceMode()}<#if ecaAction.isPersist()>-persisted</#if></td>
            </tr>
        </#list>
        </table>
    </#if>

    <#-- leaving this out, will show service links for actions
    <#if serviceEcaArtifactInfo.getServicesCalledByServiceEcaActions()?has_content>
        <h3>Services Called By Service ECA Actions</h3>
        <#list serviceEcaArtifactInfo.getServicesCalledByServiceEcaActions() as serviceArtifactInfo>
            <@displayServiceArtifactInfo serviceArtifactInfo=serviceArtifactInfo/>
        </#list>
    </#if>
    -->
    <#if serviceEcaArtifactInfo.getServicesTriggeringServiceEca()?has_content>
        <h3>Services Triggering Service ECA</h3>
        <#list serviceEcaArtifactInfo.getServicesTriggeringServiceEca() as serviceArtifactInfo>
            <@displayServiceArtifactInfo serviceArtifactInfo=serviceArtifactInfo/>
        </#list>
    </#if>
</#macro>

<#macro displayFormWidgetArtifactInfo formWidgetArtifactInfo>
    <div>&nbsp;-&nbsp;<@displayArtifactInfo artifactInfo=formWidgetArtifactInfo/></div>
</#macro>

<#macro displayScreenWidgetArtifactInfo screenWidgetArtifactInfo>
    <div>&nbsp;-&nbsp;<@displayArtifactInfo artifactInfo=screenWidgetArtifactInfo/></div>
</#macro>

<#macro displayControllerRequestArtifactInfo controllerRequestArtifactInfo>
    <div>&nbsp;-&nbsp;<@displayArtifactInfo artifactInfo=controllerRequestArtifactInfo/></div>
</#macro>

<#macro displayControllerViewArtifactInfo controllerViewArtifactInfo>
    <div>&nbsp;-&nbsp;<@displayArtifactInfo artifactInfo=controllerViewArtifactInfo/></div>
</#macro>

<#macro displayArtifactInfo artifactInfo>
    <@displayArtifactInfoLink type=artifactInfo.getType() uniqueId=artifactInfo.getUniqueId() displayName=artifactInfo.getDisplayName()/>
</#macro>

<#macro displayArtifactInfoLink type uniqueId displayName>
<a href="<@ofbizUrl>ArtifactInfo?type=${type}&amp;uniqueId=${uniqueId?url('ISO-8859-1')}</@ofbizUrl>">${displayName}</a>
</#macro>
</div>
</div>