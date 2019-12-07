<div style="border-bottom:none">
    <div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
        <div class="row-fluid">
            <div class="span10">
                <div class="tabbable">
                    <ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
                        <li class="active">
                            <a data-toggle="tab" href="#reqdeliorder-tab">${uiLabelMap.BDByDelivery}</a>
                        </li>

                        <li>
                            <a data-toggle="tab" href="#reqdeliproduct-tab">${uiLabelMap.BSByProduct}</a>
                        </li>
                    </ul>
                </div><!--.tabbable-->
            </div>
        </div>
    </div>
</div>
<div class="widget-body" style="margin-top: -12px !important">
    <div class="widget-main padding-4">
        <div class="tab-content overflow-visible" style="padding:8px 0">
            <div id="reqdeliorder-tab" class="tab-pane active">
            <#include "viewTripItemTab1.ftl"/>
            </div><!--end tab1-->

            <div id="reqdeliproduct-tab" class="tab-pane">
            <#include "viewTripItemTab2.ftl"/>
            </div><!--end tab2-->
        </div><!--.tab-content-->
    </div><!--.widget-main-->
</div><!--.widget-body-->
