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
  <div class="widget-header">
      <h4>${uiLabelMap.WebtoolsMainPage}</h4>
    <br class="clear"/>
  </div>
  <div class="widget-body">
    <#if !userLogin?has_content>
      <div>
      <p class="alert alert-info">${uiLabelMap.WebtoolsForSomethingInteresting}.</p></div>
      <br />
      <div>
      <p class="alert alert-info">${uiLabelMap.WebtoolsNoteAntRunInstall}.</p></div>
      <br />
      <div><a class="" href="<@ofbizUrl>checkLogin</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></div>
    </#if>
    <div class="row-fluid">
    <div class="span12">
    <#if userLogin?has_content>
    <div class="span6 collepsed">
      <ul class="unstyled spaced">
        <li class="margin-left15"><h2 class="header smaller lighter blue" style="margin-top: -10px;">${uiLabelMap.WebtoolsCacheDebugTools}</h3></li>
        <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>FindUtilCache</@ofbizUrl>">${uiLabelMap.WebtoolsCacheMaintenance}</a></li>
        <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>LogConfiguration</@ofbizUrl>">${uiLabelMap.WebtoolsAdjustDebuggingLevels}</a></li>
        <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>LogView</@ofbizUrl>">${uiLabelMap.WebtoolsViewLog}</a></li>
        <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>ViewComponents</@ofbizUrl>">${uiLabelMap.WebtoolsViewComponents}</a></li>
        <#if security.hasPermission("ARTIFACT_INFO_VIEW", session)>
          <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsGeneralArtifactInfoTools}</h3></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>ArtifactInfo</@ofbizUrl>" target="_blank">${uiLabelMap.WebtoolsArtifactInfo}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>entityref</@ofbizUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReference} - ${uiLabelMap.WebtoolsEntityReferenceInteractiveVersion}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>ServiceList</@ofbizUrl>">${uiLabelMap.WebtoolsServiceReference}</a></li>
        </#if>
       	<#if security.hasPermission("LABEL_MANAGER_VIEW", session)>
          <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsLabelManager}</h3></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>SearchLabels</@ofbizUrl>">${uiLabelMap.WebtoolsLabelManager}</a></li>
        </#if>
        <#if security.hasPermission("ENTITY_MAINT", session)>
          <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsEntityEngineTools}</h3></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>entitymaint</@ofbizUrl>">${uiLabelMap.WebtoolsEntityDataMaintenance}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>entityref</@ofbizUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReference} - ${uiLabelMap.WebtoolsEntityReferenceInteractiveVersion}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>entityref?forstatic=true</@ofbizUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReference} - ${uiLabelMap.WebtoolsEntityReferenceStaticVersion}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>entityrefReport</@ofbizUrl>" target="_blank">${uiLabelMap.WebtoolsEntityReferencePdf}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntitySQLProcessor</@ofbizUrl>">${uiLabelMap.PageTitleEntitySQLProcessor}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntitySyncStatus</@ofbizUrl>">${uiLabelMap.WebtoolsEntitySyncStatus}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>view/ModelInduceFromDb</@ofbizUrl>" target="_blank">${uiLabelMap.WebtoolsInduceModelXMLFromDatabase}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntityEoModelBundle</@ofbizUrl>">${uiLabelMap.WebtoolsExportEntityEoModelBundle}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>view/checkdb</@ofbizUrl>">${uiLabelMap.WebtoolsCheckUpdateDatabase}</a></li>
          <li class="margin-left15"><h2 class="header smaller lighter blue">Selenium</h3></li>
        <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>selenium</@ofbizUrl>">Selenium</a></li>
          <#-- not using Minerva by default any more <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>minervainfo</@ofbizUrl>">Minerva Connection Info</a></li> -->
          <#-- want to leave these out because they are only working so-so, and cause people more problems that they solve, IMHO
            <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>view/EditEntity</@ofbizUrl>"  target="_blank">Edit Entity Definitions</a></li>
            <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>ModelWriter</@ofbizUrl>" target="_blank">Generate Entity Model XML (all in one)</a></li>
            <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>ModelWriter?savetofile=true</@ofbizUrl>" target="_blank">Save Entity Model XML to Files</a></li>
          -->
          <#-- not working right now anyway
            <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>ModelGroupWriter</@ofbizUrl>" target="_blank">Generate Entity Group XML</a></li>
            <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>ModelGroupWriter?savetofile=true</@ofbizUrl>" target="_blank">Save Entity Group XML to File</a></li>
          -->
          <#--
            <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>view/tablesMySql</@ofbizUrl>">MySQL Table Creation SQL</a></li>
            <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>view/dataMySql</@ofbizUrl>">MySQL Auto Data SQL</a></li>
          -->
          </ul>
          </div>
          <div class="span6">
          <ul class="unstyled spaced">
          <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsEntityXMLTools}</h3></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>xmldsdump</@ofbizUrl>">${uiLabelMap.PageTitleEntityExport}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntityExportAll</@ofbizUrl>">${uiLabelMap.PageTitleEntityExportAll}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntityImport</@ofbizUrl>">${uiLabelMap.PageTitleEntityImport}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntityImportDir</@ofbizUrl>">${uiLabelMap.PageTitleEntityImportDir}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntityImportReaders</@ofbizUrl>">${uiLabelMap.PageTitleEntityImportReaders}</a></li>
        </#if>
        <#if security.hasPermission("SERVICE_MAINT", session)>
          <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsServiceEngineTools}</h3></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>ServiceList</@ofbizUrl>">${uiLabelMap.WebtoolsServiceReference}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>scheduleJob</@ofbizUrl>">${uiLabelMap.PageTitleScheduleJob}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>runService</@ofbizUrl>">${uiLabelMap.PageTitleRunService}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>FindJob</@ofbizUrl>">${uiLabelMap.PageTitleJobList}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>threadList</@ofbizUrl>">${uiLabelMap.PageTitleThreadList}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>ServiceLog</@ofbizUrl>">${uiLabelMap.WebtoolsServiceLog}</a></li>
        </#if>
        <#if security.hasPermission("DATAFILE_MAINT", session)>
          <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsDataFileTools}</h3></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>viewdatafile</@ofbizUrl>">${uiLabelMap.WebtoolsWorkWithDataFiles}</a></li>
        </#if>
        <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsMiscSetupTools}</h3></li>
        <#if security.hasPermission("PORTALPAGE_ADMIN", session)>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>FindPortalPage</@ofbizUrl>">${uiLabelMap.WebtoolsAdminPortalPage}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>FindGeo</@ofbizUrl>">${uiLabelMap.WebtoolsGeoManagement}</a></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>WebtoolsLayoutDemo</@ofbizUrl>">${uiLabelMap.WebtoolsLayoutDemo}</a></li>
        </#if>
        <#if security.hasPermission("ENUM_STATUS_MAINT", session)>
          <#--
          <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>EditEnumerationTypes</@ofbizUrl>">Edit Enumerations</a></li>
          <li><a class="btn btn-info btn-mini" href="<@ofbizUrl>EditStatusTypes</@ofbizUrl>">Edit Status Options</a></li>
          -->
        </#if>
        <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsPerformanceTests}</h3></li>
        <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>EntityPerformanceTest</@ofbizUrl>">${uiLabelMap.WebtoolsEntityEngine}</a></li>
        <#if security.hasPermission("SERVER_STATS_VIEW", session)>
          <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsServerHitStatisticsTools}</h3></li>
          <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>StatsSinceStart</@ofbizUrl>">${uiLabelMap.WebtoolsStatsSinceServerStart}</a></li>
        </#if>
        <li class="margin-left15"><h2 class="header smaller lighter blue">${uiLabelMap.WebtoolsCertsX509}</h3></li>
        <li class="margin-left30"><i class="icon-caret-right blue"></i><a class="" href="<@ofbizUrl>myCertificates</@ofbizUrl>">${uiLabelMap.WebtoolsMyCertificates}</a></li>
      </ul>
      </div>
    </#if>
    </div>
    </div>
  </div>
</div>
