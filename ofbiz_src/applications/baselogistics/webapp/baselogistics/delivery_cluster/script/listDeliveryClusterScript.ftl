<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/salesmtlresources/js/common/map.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.StartDateMustBeAfterEndDate = '${StringUtil.wrapString(uiLabelMap.StartDateMustBeAfterEndDate)}';
	uiLabelMap.CommonCancel = '${StringUtil.wrapString(uiLabelMap.CommonCancel)}';
	uiLabelMap.OK = '${StringUtil.wrapString(uiLabelMap.OK)}';
	uiLabelMap.BSCustomerId = '${StringUtil.wrapString(uiLabelMap.BSCustomerId)}';
	uiLabelMap.FullName = '${StringUtil.wrapString(uiLabelMap.FullName)}';
	uiLabelMap.BLShipperCode = '${StringUtil.wrapString(uiLabelMap.BLShipperCode)}';
	uiLabelMap.BSVietNam = '${StringUtil.wrapString(uiLabelMap.BSVietNam)}';
	uiLabelMap.BLDeliveryClusterCode = '${StringUtil.wrapString(uiLabelMap.BLDeliveryClusterCode)}';

    var OlbDeliveryClusterUtil = (function () {
        var init = function () {
        };
        var createIcon = function(color) {
            var homeIcon = {
                path: getSvgIcon(),
                fillColor: '#'+color,
                fillOpacity: 0.8,
                scale: 0.055,
                strokeColor: '#'+color,
                strokeWeight: 1
            };
            return homeIcon;
        };
        var getSvgIcon = function() {
            return 'M352.163,163.115L198.919,9.871c-10.449-10.449-27.389-10.449-37.838,0L7.837,163.115c-7.652,7.652-9.94,19.16-5.8,29.158\n' +
                    '\tc4.142,9.998,13.898,16.516,24.719,16.516h20.762v114.574c0,19.112,15.493,34.603,34.603,34.603h195.758\n' +
                    '\tc19.11,0,34.603-15.492,34.603-34.603V208.789h20.762c10.821,0,20.578-6.519,24.719-16.516\n' +
                    '\tC362.103,182.275,359.815,170.767,352.163,163.115z M220.431,307.785h-80.862v-45.583c0-22.33,18.102-40.431,40.431-40.431\n' +
                    '\ts40.431,18.1,40.431,40.431V307.785z';
        };
        return {
            init: init,
            getSvgIcon: getSvgIcon,
            createIcon: createIcon,
        }
    }());
</script>
<style>
    .custom-control-toolbar {
        margin-top: 6px;
    }
</style>
<script type="text/javascript" src="/logresources/js/delivery_cluster/listDeliveryCluster.js?v=1.0.0"></script>
