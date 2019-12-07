<script src="/crmresources/js/generalUtils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
    $(function () {
        var $validation = false;
        $('#fuelux-wizard').ace_wizard().on('change', function (e, info) {
            return false;
        }).on('finished', function (e) {
            return false;
        }).on('stepclick', function (e) {
            return false;
        });
    <#if hasStepCustomerConfirmOrder?exists && hasStepCustomerConfirmOrder>
        $("#step3").css("min-width", "16%", "important");
    </#if>
    });

    var productPricesMap = [];
    var dataSelected = [];
</script>

<div class="row-fluid">
    <div class="span12">
        <div class="widget-box transparent" id="recent-box">
            <div class="widget-header" style="border-bottom:none">
                <div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
                    <div class="row-fluid">
                        <div class="span10">
                            <div class="tabbable">
                                <ul class="nav nav-tabs" id="recent-tab">
                                    <li class="active">
                                        <a data-toggle="tab"
                                           href="#orderoverview-tab">${uiLabelMap.BSOverview}</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="span2" style="height:34px; text-align:right">
                            <a href="<@ofbizUrl>editDistributorGroup?productStoreGroupId=${productStoreGroupId}</@ofbizUrl>"
                               data-rel="tooltip"
                               title="${uiLabelMap.BSEdit}" data-placement="left"
                               class="button-action">
                                <i class="icon-edit open-sans"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="tripInfo">
    <div style="position:relative"><!-- class="widget-body"-->
        <div><!--class="widget-main"-->
            <div class="row-fluid">
                <div class="form-horizontal form-window-content-custom label-text-left content-description"
                     style="margin:10px">
                    <div class="row-fluid">
                        <div class="span6">
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.BDProductStoreGroupName}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span><i>${productStoreGroup.productStoreGroupName?if_exists}</i></span>
                                </div>
                            </div>
                        </div><!--.span6-->
                        <div class="span6">
                            <div class="row-fluid">
                                <div class="div-inline-block">
                                    <label>${uiLabelMap.BSDescription}:</label>
                                </div>
                                <div class="div-inline-block">
                                    <span id="strDescription"></span>
                                </div>
                            </div>
                        </div><!--.span6-->
                    </div>
                </div>
            </div><!-- .form-horizontal -->
            <div class="row-fluid">
                <div class="span12">
                <#--<#include "viewTripItems.ftl"/>-->
                </div>
            </div><!--.form-horizontal-->

        </div><!--.row-fluid-->
    </div><!--.widget-main-->
</div><!--.widget-body-->
</div>

<div class="row-fluid">
    <div class="span12">
    <#assign dataField="[
				{name: 'partyId', type: 'string'},
				{name: 'partyCode', type: 'string'},
				{name: 'statusId', type: 'string'},
				{name: 'groupName', type: 'string'},
				{name: 'supervisorId', type: 'string'},
				{name: 'supervisor', type: 'string'},
				{name: 'address1', type: 'string'},
				{name: 'contactNumber', type: 'string'},
				{name: 'emailAddress', type: 'string'},
				{name: 'officeSiteName', type: 'string'},
				{name: 'preferredCurrencyUomId', type: 'string'},
				{name: 'productStoreGroupName', type: 'string'}
			]"/>

    <#assign columnlistConfirm = "

				{text: '${StringUtil.wrapString(uiLabelMap.BSId)}', datafield: 'partyCode', width: '10%',
					cellsrenderer: function(row, column, value, a, b, data){
				        var link = 'DistributorDetail?me=distributorDSA&sub=distributorList&partyId=' + data.partyId;
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'groupName', minwidth: '20%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: '10%', cellsalign: 'right', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', width: '20%', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: '8%', sortable: false},
			"/>

        <@jqGrid  id="jqxgridOrder"  url="jqxGeneralServicer?sname=JQGetListDistributorsByDistributorGroupId&productStoreGroupId=${productStoreGroupId}" dataField=dataField columnlist=columnlistConfirm filterable="true" clearfilteringbutton="true"
    showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
    <#--groups="productStoreGroupName" groupsexpanded="true"-->
    defaultSortColumn="partyId" sortdirection="desc" customcontrol1=customcontrol1 contextMenuId="contextMenu" mouseRightMenu="true" bindresize="true"/>
    </div>
    </div>
</div>

<div style="position:relative">
    <div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
        <div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
            <div>
                <div class="jqx-grid-load"></div>
                <span>${uiLabelMap.BSLoading}...</span>
            </div>
        </div>
    </div>
</div>