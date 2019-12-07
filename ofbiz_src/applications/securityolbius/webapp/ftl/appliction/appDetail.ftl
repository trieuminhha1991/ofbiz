<#include "../marco/olbius.ftl"/>

<div class="row-fluid">
    <div class="span3" style="text-align: right">Application Id</div>
    <div class="span9" style="text-align: left">
        <b>${olbiusApp.applicationId}</b>
    </div>
</div>

<@rowInput label="Application type" disabled=true width="300" value=olbiusApp.applicationType/>
<@rowInput label="Application" disabled=true width="300" value=olbiusApp.application/>
<@rowInput label="Name" width="300" value=olbiusApp.name event="applicationDetail" field="name"/>

<#assign dataField = [
    {"name": "permissionId", "type": "string"},
    {"name": "description", "type": "string"}
]/>

<#assign columnlist = [
    {"text": "permissionId", "dataField": "permissionId"},
    {"text": "description", "dataField": "description"}
]/>
<@rowDropDownGrid url="jqxGeneralServicer?sname=viewEntity&entity=SecurityPermission" dataField=dataField
    columnlist=columnlist showDataField=["permissionId"] width="306" label="Default permission" value={"permissionId": olbiusApp.permissionId!}
    event="applicationDetail" field="permissionId"/>

<div class="row-fluid">
    <div class="span3" style="text-align: right">Module Id</div>
    <div class="span9" style="text-align: left">
        <a href="application?applicationId=${olbiusApp.moduleId!}"><b>${olbiusApp.moduleId!}</b></a>
    </div>
</div>

<#if hasOlbEntityPermission("OlbiusApplication", "UPDATE")>
    <@rowButton labelOk="SAVE" eventCancel="cancelApplicationDetail" eventOk="okApplicationDetail"/>
</#if>

<script type="text/javascript">

    $(function(window){

        var applicationDetail = {};

        window.olbiusFunc.put('applicationDetail', function(field, value, clear) {
            applicationDetail[field] = {
                value: value,
                clear: clear
            };
        });

        window.olbiusFunc.put('okApplicationDetail', function () {
            var tmp = {};

            for(var key in applicationDetail) {
                tmp[key] = applicationDetail[key].value;
            }

            tmp['applicationId'] = '${olbiusApp.applicationId}';

            tmp['check'] = 'applicationType,application';

            window.createOrUpdateValue('OlbiusApplication', tmp);

            window.location.href = "application?applicationId=${olbiusApp.applicationId}"

        });

        window.olbiusFunc.put('cancelApplicationDetail', function () {
            for(var key in applicationDetail) {
                applicationDetail[key].clear();
                applicationDetail[key].value = '';
            }
        });

    }(window))

</script>