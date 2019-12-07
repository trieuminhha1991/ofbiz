<#include "../marco/olbius.ftl"/>
<#assign gridId = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>

<div id="add${gridId}" style="display: none">

    <#assign dataField = [
        {"name": "permissionId", "type": "string"},
        {"name": "description", "type": "string"}
    ]/>

    <#assign columnlist = [
        {"text": "permissionId", "dataField": "permissionId"},
        {"text": "description", "dataField": "description"}
    ]/>
    <@rowDropDownGrid url="jqxGeneralServicer?sname=viewEntity&entity=SecurityPermission" dataField=dataField
        columnlist=columnlist showDataField=["permissionId"] width="300" label="Permission" field="permissionId" event="overridePermission"/>

    <@rowDropDownGrid url="jqxGeneralServicer?sname=viewEntity&entity=SecurityPermission" dataField=dataField fieldRep="overridePermissionId"
        columnlist=columnlist showDataField=["permissionId"] width="300" label="Override permission" field="permissionId" event="overridePermission"/>

    <@rowButton labelOk="ADD" eventCancel="cancelPermission" eventOk="okPermission"/>
</div>

<#assign dataField = [
    {"name": "permissionId", "type": "string"},
    {"name": "overridePermissionId", "type": "string"}
]/>

<#assign columnlist = [
    {"text": "permissionId", "dataField": "permissionId"},
    {"text": "overridePermissionId", "dataField": "overridePermissionId"}
]/>

<#assign customcontrol="icon-plus open-sans@ADD@javascript: void(0);@window.olbiusFunc.get('addOverridePermission')()"/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=OlbiusOverridePermission&conditionField=applicationId&conditionValue=" + olbiusApp.applicationId/>

<#assign menuConfig=[
    mapMenuConfig("Remove", "fa-minus-square", "removeOverridePermission")
]/>

<@olbiusGrid id=gridId url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig customcontrol=customcontrol/>

<script type="text/javascript">
    $(function (window) {

        window.olbiusFunc.put('removeOverridePermission', function (gridId, menuId, itemId, data) {

            window.removeValue('OlbiusOverridePermission', {
                applicationId: '${olbiusApp.applicationId}',
                permissionId : data.permissionId
            }, gridId);

        });

        var permission = {};

        window.olbiusFunc.put('overridePermission', function(field, value, clear) {
            permission[field] = {
                value: value,
                clear: clear
            };
        });

        window.olbiusFunc.put('addOverridePermission', function() {
            $('#add${gridId}').show();
        });

        window.olbiusFunc.put('okPermission', function () {
            var tmp = {};

            for(var key in permission) {
                tmp[key] = permission[key].value;
            }
            tmp['applicationId'] = '${olbiusApp.applicationId}';
            window.createOrUpdateValue('OlbiusOverridePermission', tmp, '${gridId}');
            window.olbiusFunc.get('cancelPermission')();
        });

        window.olbiusFunc.put('cancelPermission', function () {
            for(var key in permission) {
                permission[key].clear();
                permission[key].value = '';
            }
            $('#add${gridId}').hide();
        });

        window.olbiusFunc.put('abc', function (gridId, menuId, itemId, data) {

        });
    }(window));
</script>