<#include "../marco/olbius.ftl"/>

<#assign dataField = [
    {"name": "applicationId", "type": "string"},
    {"name": "applicationType", "type": "string"},
    {"name": "application", "type": "string"},
    {"name": "name", "type": "string"},
    {"name": "permissionId", "type": "string"}
]/>

<#assign columnlist = [
    {"text": "applicationId", "dataField": "applicationId"},
    {"text": "applicationType", "dataField": "applicationType"},
    {"text": "application", "dataField": "application"},
    {"text": "name", "dataField": "name"},
    {"text": "permissionId", "dataField": "permissionId"}
]/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=OlbiusApplication&conditionField=moduleId&conditionValue=" + olbiusApp.applicationId/>

<#assign menuConfig=[
    mapMenuConfig("App detail", "fa-eye", "appDetail"),
    mapMenuConfig("Remove member", "fa-minus-square", "removeAppMember")
]/>

<@olbiusGrid url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig/>

<script type="text/javascript">
    $(function (window) {
        window.olbiusFunc.put('removeAppMember', function (gridId, menuId, itemId, data) {

            window.createOrUpdateValue('OlbiusApplication', {
                applicationId: data.applicationId,
                moduleId : ' '
            }, gridId);

        });
    }(window));
</script>