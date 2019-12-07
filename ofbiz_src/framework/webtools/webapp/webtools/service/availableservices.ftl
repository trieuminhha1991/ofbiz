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

<#--Dispatcher Name: ${dispatcherName?default(uiLabelMap.CommonNA)} -->

<#assign url='ServiceList'>
<#assign popupUrl='serviceEcaDetail'>

<#-- Selected Service is available -->
<#if selectedServiceMap?exists>
  <#if showWsdl?exists && showWsdl = true>
    <div class="widget-box transparent no-bottom-border">
      <div class="widget-header">
        <h4>${uiLabelMap.WebtoolsServiceWSDL} - ${uiLabelMap.WebtoolsService} ${selectedServiceMap.serviceName}</h4>
      </div>
      <div class="screenlet-body" align="center">
        <form><textarea rows="20" cols="85" name="wsdloutput">${selectedServiceMap.wsdl}</textarea></form>
        <br />
        <a class=" " href='<@ofbizUrl>${url}?sel_service_name=${selectedServiceMap.serviceName}</@ofbizUrl>' class='btn btn-mini btn-info'>${uiLabelMap.CommonBack}</a>
      </div>
    </div>
  <#else>
    <div class="widget-box transparent no-bottom-border">
      <div class="widget-header">
      <h4>${uiLabelMap.WebtoolsService} ${selectedServiceMap.serviceName}</h4>
        <span class="widget-toolbar">
          <li><a class=" margin-top5 open-sans icon-list" style="text-decoration: blink; font-size:14px;" href='<@ofbizUrl>${url}</@ofbizUrl>'>${uiLabelMap.CommonListAll}</a></li>
          <li><a class=" margin-top5 open-sans icon-calendar" style="text-decoration: blink; font-size:14px;" href='<@ofbizUrl>/scheduleJob?SERVICE_NAME=${selectedServiceMap.serviceName}</@ofbizUrl>'>${uiLabelMap.WebtoolsSchedule}</a></li>
          <li><a class=" margin-top5 open-sans icon-check" style="text-decoration: blink; font-size:14px;" href='<@ofbizUrl>/setSyncServiceParameters?SERVICE_NAME=${selectedServiceMap.serviceName}&amp;POOL_NAME=pool&amp;_RUN_SYNC_=Y</@ofbizUrl>'>${uiLabelMap.PageTitleRunService}</a></li>
        </span>
        <br class="clear"/>
      </div>
      <div class="widget-body transparent no-bottom-border">
        <table class="basic-table" cellspacing='0'>
          <tr>
            <td class="olbius2-label">${uiLabelMap.WebtoolsServiceName}</td>
            <td >${selectedServiceMap.serviceName}</td>
            <td class="olbius2-label">${uiLabelMap.WebtoolsEngineName}</td>
            <td ><a class=" " href='<@ofbizUrl>${url}?constraint=engine_name@${selectedServiceMap.engineName}</@ofbizUrl>'>${selectedServiceMap.engineName}</a></td>
          </tr>
          <tr>
            <td class="olbius2-label">${uiLabelMap.CommonDescription}</td>
            <td>${selectedServiceMap.description}</td>
            <td class="olbius2-label">${uiLabelMap.WebtoolsInvoke}</td>
            <td>${selectedServiceMap.invoke}</td>
          </tr>
          <tr>
            <td class="olbius2-label">${uiLabelMap.WebtoolsExportable}</td>
            <td>${selectedServiceMap.export}<#if selectedServiceMap.exportBool = "true">&nbsp;(<a class=" " href='<@ofbizUrl>${url}?sel_service_name=${selectedServiceMap.serviceName}&amp;show_wsdl=true</@ofbizUrl>'>${uiLabelMap.WebtoolsShowShowWSDL}</a>)</#if></td>
            <td class="olbius2-label">${uiLabelMap.WebtoolsLocation}</td>
            <td><a class=" " href='<@ofbizUrl>${url}?constraint=location@${selectedServiceMap.location}</@ofbizUrl>'>${selectedServiceMap.location}</a></td>
          </tr>
          <tr>
            <td style="width:135px;" class="olbius2-label">${uiLabelMap.WebtoolsDefinitionLocation}</td>
            <td style="width:330px;"><a class=" " href='<@ofbizUrl>${url}?constraint=definitionLocation@${selectedServiceMap.definitionLocation}</@ofbizUrl>'>${selectedServiceMap.definitionLocation}</a></td>
            <td style="width:170px;" class="olbius2-label">${uiLabelMap.WebtoolsDefaultEntityName}</td>
            <td><a class=" " href='<@ofbizUrl>${url}?constraint=default_entity_name@${selectedServiceMap.defaultEntityName}</@ofbizUrl>'>${selectedServiceMap.defaultEntityName}</a></td>
          </tr>
          <tr>
            <td class="olbius2-label">${uiLabelMap.WebtoolsArtifactInfo}</td>
            <td><a class="" href='<@ofbizUrl>ArtifactInfo?name=${selectedServiceMap.serviceName}&amp;type=service</@ofbizUrl>'>${uiLabelMap.WebtoolsArtifactInfo}</a></td>
            <td class="olbius2-label">${uiLabelMap.WebtoolsRequireNewTransaction}</td>
            <td>${selectedServiceMap.requireNewTransaction}</td>
          </tr>
          <tr>
            <td colspan="2">&nbsp;</td>
            <td class="olbius2-label">${uiLabelMap.WebtoolsUseTransaction}</td>
            <td>${selectedServiceMap.useTrans}</td>
          </tr>
          <tr>
            <td colspan="2">&nbsp;</td>
            <td class="olbius2-label">${uiLabelMap.WebtoolsMaxRetries}</td>
            <td>${selectedServiceMap.maxRetry}</td>
          </tr>
        </table>
      </div>
    </div>

    <div class="widget-box transparent no-bottom-border">
      <div class="widget-header">
        <h4>${uiLabelMap.SecurityGroups}</h4>
      </div>
      <div class="widget-body">
      <#if selectedServiceMap.permissionGroups != 'NA'>
        <table class="basic-table" cellspacing='0'>
          <tr class="header-row">
            <td>${uiLabelMap.WebtoolsNameOrRole}</td>
            <td>${uiLabelMap.WebtoolsPermissionType}</td>
            <td>${uiLabelMap.WebtoolsAction}</td>
          </tr>
          <#list selectedServiceMap.permissionGroups as permGrp>
            <tr>
              <td>${permGrp.nameOrRole?default(uiLabelMap.CommonNA)}</td>
              <td>${permGrp.permType?default(uiLabelMap.CommonNA)}</td>
              <td>${permGrp.action?default(uiLabelMap.CommonNA)}</td>
            </tr>
          </#list>
        </table>
      <#else>
        <div class="screenlet-body">
          <p class="alert alert-info">${selectedServiceMap.permissionGroups}</p>
        </div>
      </#if>
      </div>
    </div>

    <div class="widget-box transparent no-bottom-border">
      <div class="widget-header">
        <h4>${uiLabelMap.WebtoolsImplementedServices}</h4>
      </div>
      <div class="widget-body">
        <#if selectedServiceMap.implServices == 'NA'>
          <p class="alert alert-info">${selectedServiceMap.implServices}</p>
        <#elseif selectedServiceMap.implServices?has_content>
          <#list selectedServiceMap.implServices as implSrv>
            <a class=" " href='<@ofbizUrl>${url}?sel_service_name=${implSrv.getService()}</@ofbizUrl>'>${implSrv.getService()}</a><br />
          </#list>
        </#if>
      </div>
    </div>

    <#-- If service has ECA's -->
    <#if ecaMapList?exists && ecaMapList?has_content>
      <#-- add the javascript for modalpopup's -->
      <script language='javascript' type='text/javascript'>
          function detailsPopup(viewName){
              var lookupWinSettings = 'top=50,left=50,width=600,height=300,scrollbars=auto,status=no,resizable=no,dependent=yes,alwaysRaised=yes';
              var params = '';
              var lookupWin = window.open(viewName, params, lookupWinSettings);
              if(lookupWin.opener == null) lookupWin.opener = self;
              lookupWin.focus();
          }
      </script>
      <div class="widget-box transparent no-bottom-border">
        <div class="widget-header">
          <h4>${uiLabelMap.WebtoolsServiceECA}</h4>
        </div>
        <div class="screenlet-body">
        <table class="basic-table" cellspacing='0'>
          <tr class="header-row">
            <td>${uiLabelMap.WebtoolsEventName}</td>
            <#if ecaMapList.runOnError?exists>
              <td>${uiLabelMap.WebtoolsRunOnError}</td>
            </#if>
            <#if ecaMapList.runOnFailure?exists>
              <td>${uiLabelMap.WebtoolsRunOnFailure}</td>
            </#if>
            <td>${uiLabelMap.WebtoolsActions}</td>
            <td>${uiLabelMap.WebtoolsConditions}</td>
            <td>${uiLabelMap.WebtoolsSet}</td>
          </tr>
          <#list ecaMapList as ecaMap>
            <tr>
              <td>${ecaMap.eventName?if_exists}</td>
              <#if ecaMap.runOnError?exists>
                <td>${ecaMap.runOnError}</div></td>
              </#if>
              <#if ecaMap.runOnFailure?exists>
                <td>${ecaMap.runOnFailure}</div></td>
              </#if>
              <#if ecaMap.actions?has_content>
                <td>
                  <#list ecaMap.actions as action>
                    <table class="basic-table" cellspacing='0'>
                      <tr>
                        <td colspan="2"><a class=" " href='<@ofbizUrl>${url}?sel_service_name=${action.serviceName}</@ofbizUrl>'>${action.serviceName?default(uiLabelMap.CommonNA)}</a></td>
                      </tr>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsSecasIgnoreError}</b> ${action.ignoreError?default(uiLabelMap.CommonNA)}</td>
                        <td><b>${uiLabelMap.WebtoolsSecasIgnoreFailure}</b> ${action.ignoreFailure?default(uiLabelMap.CommonNA)}</td>
                      </tr>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsSecasPersist}</b> ${action.persist?default(uiLabelMap.CommonNA)}</td>
                        <td><b>${uiLabelMap.WebtoolsSecasResultMapName}</b> ${action.resultMapName?default(uiLabelMap.CommonNA)}</td>
                      </tr>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsSecasResultToContext}</b> ${action.resultToContext?default(uiLabelMap.CommonNA)}</td>
                        <td><b>${uiLabelMap.WebtoolsSecasResultToResult}</b> ${action.resultToResult?default(uiLabelMap.CommonNA)}</td>
                      </tr>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsSecasServiceMode}</b> ${action.serviceMode?default(uiLabelMap.CommonNA)}</td>
                        <td colspan="2">&nbsp;</td>
                      </tr>
                    </table>
                  </#list>
                </td>
              </#if>
              <#if ecaMap.conditions?has_content>
                <td>
                  <#list ecaMap.conditions as condition>
                    <table class='basic-table' cellspacing='0'>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsCompareType}</b> ${condition.compareType?default(uiLabelMap.CommonNA)}</td>
                        <td>
                          <b>${uiLabelMap.WebtoolsConditionService}</b>
                          <#if condition.conditionService?has_content>
                            <a class=" " href='<@ofbizUrl>${url}?sel_service_name=${condition.conditionService}</@ofbizUrl>'>${condition.conditionService?default(uiLabelMap.CommonNA)}</a>
                          <#else>
                            ${condition.conditionService?default(uiLabelMap.CommonNA)}
                          </#if>
                        </td>
                        <td><b>${uiLabelMap.WebtoolsFormat}</b> ${condition.format?default(uiLabelMap.CommonNA)}</td>
                      </tr>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsIsService}</b> ${condition.isService?default(uiLabelMap.CommonNA)}</td>
                        <td><b>${uiLabelMap.WebtoolsIsConstant}</b> ${condition.isConstant?default(uiLabelMap.CommonNA)}</td>
                        <td><b>${uiLabelMap.WebtoolsOperator}</b> ${condition.operator?default(uiLabelMap.CommonNA)}</td>
                      </tr>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsLHSMapName}</b> ${condition.lhsMapName?default(uiLabelMap.CommonNA)}</td>
                        <td><b>${uiLabelMap.WebtoolsLHSValueName}</b> ${condition.lhsValueName?default(uiLabelMap.CommonNA)}</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsRHSMapName}</b> ${condition.rhsMapName?default(uiLabelMap.CommonNA)}</td>
                        <td><b>${uiLabelMap.WebtoolsRHSValueName}</b> ${condition.rhsValueName?default(uiLabelMap.CommonNA)}</td>
                        <td>&nbsp;</td>
                      </tr>
                    </table><br />
                  </#list>
                </td>
              </#if>
              <#if ecaMap.sets?has_content>
                <td>
                  <#list ecaMap.sets as set>
                    <table class='basic-table' cellspacing='0'>
                      <tr>
                        <td><b>${uiLabelMap.WebtoolsFieldName}</b> ${set.fieldName?default(uiLabelMap.CommonNA)}</td>
                        <td colspan="2">&nbsp;</td>
                      </tr>
                      <tr>
                        <#if set.envName?has_content>
                          <td><b>${uiLabelMap.WebtoolsEnvName}</b> ${set.envName}</td>
                          <td colspan="2">&nbsp;</td>
                        </#if>
                      </tr>
                      <tr>
                        <#if set.value?has_content>
                          <td><b>${uiLabelMap.CommonValue}</b> ${set.value}</td>
                          <td colspan="2">&nbsp;</td>
                        </#if>
                      </tr>
                      <tr>
                        <#if set.format?has_content>
                          <td><b>${uiLabelMap.WebtoolsFormat}</b> ${set.format}</td>
                          <td colspan="2">&nbsp;</td>
                        </#if>
                      </tr>
                    </table><br />
                  </#list>
                </td>
              </#if>
            </tr>
            <tr><td colspan='5'><hr/></td></tr>
          </#list>
        </table>
        </div>
      </div>
    </#if>
    <#-- End if service has ECA's -->

    <#list selectedServiceMap.allParamsList?if_exists as paramList>
      <style type="text/css">
        .param-table tr td {
          width: 12.5%;
          vertical-align: top;
        }
      </style>
      <div class="widget-box transparent no-bottom-border">
        <div class="widget-header">
          <h4>${paramList.title}</h4>
        </div>
        <div class="widget-body">
        <#if paramList.paramList?exists && paramList.paramList?has_content>
        
          <table class="table table-striped table-hover table-bordered dataTable" style="width: 100%" cellspacing='0'>
              <tr class="header-row">
                <td style="font-weight: bold">${uiLabelMap.WebtoolsParameterName}</td>
                <td style="font-weight: bold">${uiLabelMap.CommonDescription}</td>
                <td style="font-weight: bold">${uiLabelMap.WebtoolsOptional}</td>
                <td style="font-weight: bold">${uiLabelMap.CommonType}</td>
                <#-- <td>Default Value</td> -->
                <td style="font-weight: bold">${uiLabelMap.WebtoolsMode}</td> 
                <td style="font-weight: bold">${uiLabelMap.WebtoolsIsSetInternally}</td>
                <td style="font-weight: bold">${uiLabelMap.WebtoolsEntityName}</td>
                <td style="font-weight: bold">${uiLabelMap.WebtoolsFieldName}</td>
              </tr>
              <#list paramList.paramList as modelParam>
                <tr>
                  <td>${modelParam.name?if_exists}</td>
                  <td>${modelParam.description?if_exists}</td>
                  <td>${modelParam.optional?if_exists}</td>
                  <td>${modelParam.type?if_exists}</td>
                  <#-- <td>[${modelParam.defaultValue?if_exists}]</td> -->
                  <td>${modelParam.mode?if_exists}</td>
                  <td>${modelParam.internal?if_exists}</td>
                  <td>
                    <#if modelParam.entityName?exists>
                      <a class=" " href='<@ofbizUrl>${url}?constraint=default_entity_name@${modelParam.entityName}</@ofbizUrl>'>${modelParam.entityName?if_exists}</a>
                    </#if>
                  </td>
                  <td>${modelParam.fieldName?if_exists}</td>
                </tr>
              </#list>
          </table>
        <#else>
          <div class="screenlet-body">
            <p class="alert alert-info">${uiLabelMap.WebtoolsNoParametersDefined}</p>
          </div>
        </#if>
      </div>
      </div>
    </#list>

    <#-- Show a little form for exportServiceEoModelBundle -->
    <div class="screenlet-body">
      <form name="exportServiceEoModelBundle" method="post" action="<@ofbizUrl>exportServiceEoModelBundle</@ofbizUrl>" class="basic-form">
        <input type="hidden" name="sel_service_name" value="${selectedServiceMap.serviceName}" />
        <input type="hidden" name="serviceName" value="${selectedServiceMap.serviceName}"/>
        Save eomodeld to Local Path: <input type="text" name="eomodeldFullPath" value="${parameters.eomodeldFullPath?if_exists}" size="60"/>
        <button class="btn btn-purple btn-small" type="submit" name="submitButton">
        <i class="icon-share-alt"></i>
        Export
        </button>
      </form>
      
    </div>
  </#if>
<#-- No Service selected , we list all-->
<#elseif servicesList?exists && servicesList?has_content>

  <#-- Show alphabetical index -->
  <#if serviceNamesAlphaList?exists && serviceNamesAlphaList?has_content>
    <form id='dispForm' method='post' action='<@ofbizUrl>${url}</@ofbizUrl>'>
      <div class="button-bar" style="text-align: center">
        <#assign isfirst=true>
        <#list serviceNamesAlphaList as alpha>
          <a class="btn btn-info btn-mini" href='<@ofbizUrl>${url}?constraint=alpha@${alpha}</@ofbizUrl>'>${alpha}</a>
          <#assign isfirst=false>
        </#list>
        <#if dispArrList?exists && dispArrList?has_content>
          &nbsp;&nbsp;&nbsp;&nbsp;
          <script language='javascript' type='text/javascript'>
            function submitDispForm(){
                selObj = document.getElementById('sd');
                var dispVar = selObj.options[selObj.selectedIndex].value;
                if(dispVar != ''){
                    document.getElementById('dispForm').submit();
                }
            }
          </script>
          <select id='sd' name='selDisp' onchange='submitDispForm();' >
            <option value='' selected="selected">${uiLabelMap.WebtoolsSelectDispatcher}</option>
            <option value='' ></option>
            <#list dispArrList as disp>
              <option value='${disp}'>${disp}</option>
            </#list>
          </select>
        </#if>
      </div>
    </form>
  </#if>

  <div class="widget-box transparent no-bottom-border">
  <div class="widget-header">
  <h4>${uiLabelMap.WebtoolsServicesListFor} ${dispatcherName?default(uiLabelMap.CommonNA)} (${servicesFoundCount} ${uiLabelMap.CommonFound})</h4>
  </div>
    <div class="screenlet-body">
      <div style="overflow-x: scroll !important; border: 0.5px solid rgb(204, 204, 204) !important; width: 100% !important">
      <table class="table table-striped table-hover table-bordered dataTable" cellspacing='0'>
        <tr class="header-row">
          <td style="font-weight: bold;">${uiLabelMap.WebtoolsServiceName}</td>
          <td style="font-weight: bold;">${uiLabelMap.WebtoolsEngineName}</td>
          <td style="font-weight: bold;">${uiLabelMap.WebtoolsDefaultEntityName}</td>
          <td style="font-weight: bold;">${uiLabelMap.WebtoolsInvoke}</td>
          <td style="font-weight: bold;">${uiLabelMap.WebtoolsLocation}</td>
          <td style="font-weight: bold;">${uiLabelMap.WebtoolsDefinitionLocation}</td>
        </tr>
        <#assign alt_row = false>
        <#list servicesList as service>
          <tr<#if alt_row> class="alternate-row" </#if>>
            <td><a class=" " href='<@ofbizUrl>${url}?sel_service_name=${service.serviceName}</@ofbizUrl>'>${service.serviceName}</a></td>
            <td><a class=" " href='<@ofbizUrl>${url}?constraint=engine_name@${service.engineName?default(uiLabelMap.CommonNA)}</@ofbizUrl>'>${service.engineName}</a></td>
            <td><a class=" " href='<@ofbizUrl>${url}?constraint=default_entity_name@${service.defaultEntityName?default(uiLabelMap.CommonNA)}</@ofbizUrl>'>${service.defaultEntityName}</a></td>
            <td>${service.invoke}</td>
            <td><a class=" " href='<@ofbizUrl>${url}?constraint=location@${service.location?default(uiLabelMap.CommonNA)}</@ofbizUrl>'>${service.location}</a></td>
            <td><a class=" " href='<@ofbizUrl>${url}?constraint=definitionLocation@${service.definitionLocation}</@ofbizUrl>'>${service.definitionLocation}</a></td>
          </tr>
          <#assign alt_row = !alt_row>
        </#list>
      </table>
      </div>
    </div>
  </div>
<#else>
  <p class="alert alert-info">${uiLabelMap.WebtoolsNoServicesFound}.</p>
  <a class=" " href='<@ofbizUrl>${url}</@ofbizUrl>'>${uiLabelMap.CommonListAll}</a>
</#if>
