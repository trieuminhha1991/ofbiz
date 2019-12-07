<#include "script/deliveryClusterDetailScript.ftl"/>
<#if deliveryClusters?has_content>
    <#assign deliveryCluster = deliveryClusters[0]?if_exists>
</#if>

<div class="row-fluid">
    <div class="span12">
        <div class="widget-box transparent" id="recent-box">
            <div class="widget-header" style="border-bottom:none">
                <div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
                    <div class="row-fluid">
                        <div class="span4">
                            <div class="tabbable">
                                <ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if deliveryCluster?has_content>
										<li <#if activeTab?exists && activeTab == "general-tab"> class="active"</#if>>
                                            <a data-toggle="tab" href="#general-tab">${uiLabelMap.GeneralInfo}</a>
                                        </li>
                                    </#if>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="widget-body" style="margin-top: -12px !important">
                <div class="widget-main padding-4">
                    <div class="tab-content overflow-visible" style="padding:8px 0">
                        <div id="general-tab"
                             class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">
                            <div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
                                <div class="row-fluid">
                                    <div class="form-horizontal form-window-content-custom label-text-left content-description"
                                         style="margin:10px">
                                        <div class="span12">
                                            <div class='row-fluid' style="margin-bottom: -10px !important">
                                                <div class="span6">
                                                    <div class='row-fluid'>
                                                        <div class="span5" style="text-align: right;">
                                                            <div>${uiLabelMap.BLDeliveryClusterCode}</div>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="deliveryClusterCodeView" style="color: #037C07;"
                                                                 class="green-label">${deliveryCluster.deliveryClusterCode}</div>
                                                        </div>
                                                    </div>
                                                    <div class='row-fluid'>
                                                        <div class="span5" style="text-align: right;">
                                                            <div>${uiLabelMap.BLDeliveryClusterName}</div>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="deliveryClusterNameView" style="color: #037C07;"
                                                                 class="green-label">${deliveryCluster.deliveryClusterName}</div>
                                                        </div>
                                                    </div>
                                                    <div class='row-fluid'>
                                                        <div class="span5" style="text-align: right;">
                                                            <div>${uiLabelMap.BLCreateDate}</div>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="createDateView" style="color: #037C07;"
                                                                 class="green-label">${(deliveryCluster.createdDate?string("dd-MM-yyyy HH:mm:ss"))?if_exists}</div>
                                                        </div>
                                                    </div>
                                                    <div class="row-fluid">
                                                        <div class="span5" style="text-align: right;">
                                                            <div>${uiLabelMap.Description}</div>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="descriptionView"
                                                                 class="green-label">
                                                                 <#if deliveryCluster.description?exists>
                                                                 	${deliveryCluster.description}
                                                                 </#if>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="span6">
                                                    <div class='row-fluid'>
                                                        <div class="span5" style="text-align: right;">
                                                            <div>${uiLabelMap.BLShipperName}</div>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="shipperView"
                                                                 class="green-label">${deliveryCluster.executorName}
                                                                [${deliveryCluster.executorCode}]
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <div class='row-fluid'>
                                                        <div class="span5" style="text-align: right;">
                                                            <div>${uiLabelMap.BLManagerName}</div>
                                                        </div>
                                                        <div class="span7">
                                                            <div id="managerView"
                                                                 class="green-label">${deliveryCluster.managerName}
                                                                [${deliveryCluster.managerCode}]
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div><!-- .form-horizontal -->
                                </div><!--.row-fluid-->
                                <div class="row-fluid margin-top10">
                                    <div class="span12">
                                        <div id="jqxgridCustomerSelected" style="width: 100%"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div><!--/widget-main-->
            </div><!--/widget-body-->
        </div>
    </div>
</div>

<script>

</script>
<#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'partyName', type: 'string'},
				{name: 'distributorName', type: 'string'},
				{name: 'distributorId', type: 'string'},
				{name: 'distributorCode', type: 'string'},
				{name: 'fullName', type: 'string'},
				{name: 'salesmanName', type: 'string'},
				{name: 'salesmanId', type: 'string'},
				{name: 'postalAddressName', type: 'string'},
				{name: 'telecomName', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'latitude', type: 'number'},
				{name: 'longitude', type: 'number'},
				{name: 'geoPointId', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'}]"/>
<#assign columnlist = "
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true,
					groupable: false, draggable: false, resizable: false, width: '5%',
				    cellsrenderer: function (row, column, value) {
				        return '<div>' + (row + 1) + '</div>';
				    }
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentId)}', datafield: 'partyCode', width: '10%',
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'partyName', width: '15%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'telecomName', width: '10%', cellsalign: 'right', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'postalAddressName', sortable: false},

			"/>
<@jqGrid filtersimplemode="true" id="jqxgridCustomerSelected" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="false" clearfilteringbutton="false"
url="jqxGeneralServicer?sname=JQGetListCustomerByCluster&deliveryClusterId=${deliveryCluster.deliveryClusterId}" initrowdetails = "false" rowdetailsheight="200" viewSize="10"
/>