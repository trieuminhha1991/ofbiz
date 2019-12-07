<#include "../marco/olbius.ftl"/>

<#assign gridId = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>

<div id="add${gridId}" style="display: none">

<@rowDropDownList label="Type" source=["ENTITY", "MENU", "MODULE", "SCREEN", "SERVICE"] width="300" field="applicationType" event="application"/>

<@rowInput label="Application" width="295" field="application" event="application"/>

<@rowInput label="Name" width="295" field="name" event="application"/>

<#assign dataField = [
{"name": "permissionId", "type": "string"},
{"name": "description", "type": "string"}
]/>

    <#assign columnlist = [
{"text": "permissionId", "dataField": "permissionId"},
{"text": "description", "dataField": "description"}
]/>
<@rowDropDownGrid url="jqxGeneralServicer?sname=viewEntity&entity=SecurityPermission" dataField=dataField
columnlist=columnlist showDataField=["permissionId"] width="300" label="Default permission" field="permissionId" event="application"/>

    <#assign dataField = [
{"name": "applicationId", "type": "string"},
{"name": "applicationType", "type": "string"},
{"name": "application", "type": "string"},
{"name": "name", "type": "string"},
{"name": "permissionId", "type": "string"},
{"name": "moduleId", "type": "string"}
]/>

    <#assign columnlist = [
{"text": "applicationId", "dataField": "applicationId"},
{"text": "applicationType", "dataField": "applicationType"},
{"text": "application", "dataField": "application"},
{"text": "name", "dataField": "name"},
{"text": "permissionId", "dataField": "permissionId"},
{"text": "moduleId", "dataField": "moduleId"}
]/>
<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=OlbiusApplication&conditionField=applicationType&conditionValue=MODULE"/>
    <@rowDropDownGrid label="Module" columnlist=columnlist dataField=dataField showDataField=["applicationId", "applicationType", "application"]
width="300" url=url field="applicationId" fieldRep="moduleId" event="application"/>

    <@rowButton labelOk="ADD" eventCancel="cancelApplication" eventOk="okApplication"/>
</div>

<#assign dataField = [
    {"name": "applicationId", "type": "string"},
    {"name": "applicationType", "type": "string"},
    {"name": "application", "type": "string"},
    {"name": "name", "type": "string"},
    {"name": "permissionId", "type": "string"},
    {"name": "moduleId", "type": "string"}
]/>

<#assign columnlist = [
    {"text": "applicationId", "dataField": "applicationId"},
    {"text": "applicationType", "dataField": "applicationType"},
    {"text": "application", "dataField": "application"},
    {"text": "name", "dataField": "name"},
    {"text": "permissionId", "dataField": "permissionId"},
    {"text": "moduleId", "dataField": "moduleId"}
]/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=OlbiusApplication"/>

<#assign customcontrol="icon-plus open-sans@ADD@javascript: void(0);@window.olbiusFunc.get('addApplication')()"/>

<#assign menuConfig=[
    mapMenuConfig("App detail", "fa-eye", "appDetail"),
    mapMenuConfig("Remove", "fa-trash-o", "removeApp")
]/>

<#if olbiusApp??>
    <#assign menuConfig = menuConfig + [
        mapMenuConfig("Add member", "fa-plus", "addAppMember"),
        mapMenuConfig("Set modulde", "fa-check-square-o", "setAppModule")
    ]/>
</#if>

<@olbiusGrid id=gridId url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig customcontrol=customcontrol rightClick="checkAppModule"/>

<script type="text/javascript">
    $(function (window) {

        window.olbiusFunc.put('checkAppModule', function (gridId, menuId, data) {
        <#if olbiusApp??>
            if(data.applicationId == '${olbiusApp.applicationId}') {
                $('#' + menuId).jqxMenu('disable', '2menuItem' + gridId, true);
                $('#' + menuId).jqxMenu('disable', '3menuItem' + gridId, true);
            } else {
                $('#' + menuId).jqxMenu('disable', '2menuItem' + gridId, false);
                $('#' + menuId).jqxMenu('disable', '3menuItem' + gridId, false);
            }
            if (data.applicationType != 'MODULE') {
                $('#' + menuId).jqxMenu('disable', '3menuItem' + gridId, true);
            }
        </#if>
        });

<#if olbiusApp??>
        window.olbiusFunc.put('addAppMember', function (gridId, menuId, itemId, data) {

            window.createOrUpdateValue('OlbiusApplication', {
                applicationId: data.applicationId,
                moduleId : '${olbiusApp.applicationId}'
            }, gridId);

        });

        window.olbiusFunc.put('setAppModule', function (gridId, menuId, itemId, data) {

            window.createOrUpdateValue('OlbiusApplication', {
                applicationId: '${olbiusApp.applicationId}',
                moduleId : data.applicationId
            }, gridId);

        });
</#if>
        window.olbiusFunc.put('appDetail', function (gridId, menuId, itemId, data) {
            window.location.href = 'application?applicationId=' + data.applicationId;
        });

        window.olbiusFunc.put('removeApp', function (gridId, menuId, itemId, data) {
            window.removeValue('OlbiusApplication', {applicationId: data.applicationId}, gridId);
            window.olbiusFunc.get('cancelApplication')();
        });

        window.olbiusFunc.put('addApplication', function () {
            $('#add${gridId}').show();
        });

        var application = {};

        window.olbiusFunc.put('application', function(field, value, clear) {
            application[field] = {
                value: value,
                clear: clear
            };
        });

        window.olbiusFunc.put('okApplication', function () {
            var tmp = {};

            for(var key in application) {
                tmp[key] = application[key].value;
            }
            tmp['check'] = 'applicationType,application';
            window.createOrUpdateValue('OlbiusApplication', tmp, '${gridId}');
            window.olbiusFunc.get('cancelApplication')();
        });

        window.olbiusFunc.put('cancelApplication', function () {
            for(var key in application) {
                application[key].clear();
                application[key].value = '';
            }
            application = {};
            $('#add${gridId}').hide();
        });

    }(window));
</script>