<@jqGridMinimumLib/>
<#include "../marco/olbius.ftl"/>

<#assign dataField = [
{"name": "userLoginId", "type": "string"},
{"name": "partyId", "type": "string"},
{"name": "isSystem", "type": "string"},
{"name": "enabled", "type": "string"}
]/>

<#assign columnlist = [
{"text": "userLoginId", "dataField": "userLoginId"},
{"text": "enabled", "dataField": "enabled"},
{"text": "isSystem", "dataField": "isSystem"},
{"text": "partyId", "dataField": "partyId"}
]/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=UserLogin"/>

<#assign menuConfig=[
mapMenuConfig("enabled", "fa-ban", "enabled"),
mapMenuConfig("View party", "fa-eye", "viewParty")
]/>

<@olbiusGrid url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig rightClick="checkEnabled"/>

<script type="text/javascript">
    $(function (window) {

        window.olbiusFunc.put('viewParty', function (gridId, menuId, itemId, data) {
            window.location.href = 'party?partyId=' + data.partyId;
        });

        window.olbiusFunc.put('enabled', function (gridId, menuId, itemId, data) {
            var tmp = '';
            if (!data.enabled || data.enabled == 'Y') {
                tmp = 'N';
            } else {
                tmp = 'Y';
            }
            jQuery.ajax({
                url: 'createOrUpdateValue',
                async: false,
                type: 'POST',
                data: {
                    entity : 'UserLogin',
                    value : window.convertMap({
                        'userLoginId': data.userLoginId,
                        'enabled': tmp
                    })
                },
                success: function (data) {
                    $('#' + gridId).jqxGrid('updatebounddata');
                }
            });
        });

        window.olbiusFunc.put('checkEnabled', function (gridId, menuId, data) {
            if (!data.enabled || data.enabled == 'Y') {
                $('#0menuItem' + gridId).html('<i class="fa fa-ban"></i>Disable');
            } else {
                $('#0menuItem' + gridId).html('<i class="fa fa-check"></i>Enable');
            }
            if(!data.partyId) {
                $('#' + menuId).jqxMenu('disable', '1menuItem' + gridId, true);
            } else {
                $('#' + menuId).jqxMenu('disable', '1menuItem' + gridId, false);
            }
        });

    }(window));
</script>