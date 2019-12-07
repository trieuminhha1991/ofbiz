<#include "../marco/olbius.ftl"/>

<#assign gridId = Static["java.util.UUID"].randomUUID().toString()?replace("-", "")/>
<div id="add${gridId}" style="display: none">

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

    <@rowDropDownGrid label="Application" columnlist=columnlist dataField=dataField showDataField=["applicationId", "applicationType", "application"]
        width="300" url="jqxGeneralServicer?sname=viewEntity&entity=OlbiusApplication" event="partyPermission" field="applicationId"/>

    <#assign dataField = [
        {"name": "permissionId", "type": "string"},
        {"name": "description", "type": "string"}
    ]/>

    <#assign columnlist = [
        {"text": "permissionId", "dataField": "permissionId"},
        {"text": "description", "dataField": "description"}
    ]/>
    <@rowDropDownGrid url="jqxGeneralServicer?sname=viewEntity&entity=SecurityPermission" dataField=dataField
        columnlist=columnlist showDataField=["permissionId"] width="300" label="Permission" event="partyPermission" field="permissionId"/>

    <@rowDateTimeInput label="From Date" width=300 event="partyPermission" field="fromDate"/>
    <@rowDateTimeInput label="Thru Date" width=300 event="partyPermission" field="thruDate"/>

    <@rowDropDownList label="Allow" source=["true", "false"] width="300" event="partyPermission" field="allow"/>

    <@rowButton labelOk="ADD" eventCancel="cancelPartyPerm" eventOk="okPartyPerm"/>
</div>

<#assign dataField = [
    {"name": "applicationId", "type": "string"},
    {"name": "permissionId", "type": "string"},
    {"name": "fromDate", "type": "date", "other": "Timestamp"},
    {"name": "thruDate", "type": "date", "other": "Timestamp"},
    {"name": "allow", "type": "string"}
]/>

<#assign columnlist = [
    {"text": "applicationId", "dataField": "applicationId"},
    {"text": "permissionId", "dataField": "permissionId"},
    {"text": "fromDate", "dataField": "fromDate", "cellsformat": "dd/MM/yyyy HH:mm:ss"},
    {"text": "thruDate", "dataField": "thruDate", "cellsformat": "dd/MM/yyyy HH:mm:ss"},
    {"text": "allow", "dataField": "allow", "sortable": false, "filterable": false}
]/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=OlbiusPartyPermission&conditionField=partyId&conditionValue="+party.partyId/>

<#assign menuConfig=[
    mapMenuConfig("ThurDate", "fa-ban", "thurDatePerm"),
    mapMenuConfig("Allow", "fa-eye", "allow"),
    mapMenuConfig("Application detail", "fa-eye", "appDetail"),
    mapMenuConfig("Remove", "fa-trash-o", "removePerm")
]/>

<#assign customcontrol="icon-plus open-sans@ADD@javascript: void(0);@window.olbiusFunc.get('addPartyPerm')()"/>

<@olbiusGrid id=gridId url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig rightClick="checkThruDate" customcontrol=customcontrol/>

<script type="text/javascript">
    $(function (window) {

        var partyPerm = {};

        window.olbiusFunc.put('partyPermission', function(field, value, clear) {
            partyPerm[field] = {
                value: value,
                clear: clear
            };
        });

        window.olbiusFunc.put('addPartyPerm', function () {
            $('#add${gridId}').show();
        });

        window.olbiusFunc.put('okPartyPerm', function () {
            var tmp = {};

            for(var key in partyPerm) {
                tmp[key] = partyPerm[key].value;
                partyPerm[key].clear();
            }
            partyPerm = {};
            $('#add${gridId}').hide();
            tmp['partyId'] = '${party.partyId}';
            window.createOrUpdateValue('OlbiusPartyPermission', tmp, '${gridId}');
        });

        window.olbiusFunc.put('cancelPartyPerm', function () {
            for(var key in partyPerm) {
                partyPerm[key].clear();
            }
            partyPerm = {};
            $('#add${gridId}').hide();
        });

        window.olbiusFunc.put('thurDatePerm', function (gridId, menuId, itemId, data) {
            window.thruDateValue('OlbiusPartyPermission', {
                applicationId: data.applicationId,
                partyId: '${party.partyId}',
                permissionId: data.permissionId,
                fromDate: data.fromDate.getTime().toString()
            }, gridId);
        });

        window.olbiusFunc.put('removePerm', function (gridId, menuId, itemId, data) {
            window.removeValue('OlbiusPartyPermission', {
                applicationId: data.applicationId,
                partyId: '${party.partyId}',
                permissionId: data.permissionId,
                fromDate: data.fromDate.getTime().toString()
            }, gridId);
        });

        window.olbiusFunc.put('allow', function (gridId, menuId, itemId, data) {

            var value = {
                applicationId: data.applicationId,
                partyId: '${party.partyId}',
                permissionId: data.permissionId,
                fromDate: data.fromDate.getTime().toString()
            };

            if (data.allow == false) {
                value['allow'] = "true";
            } else {
                value['allow'] = "false";
            }

            window.createOrUpdateValue('OlbiusPartyPermission', value, gridId);
        });

        window.olbiusFunc.put('checkThruDate', function (gridId, menuId, data) {
            if (!data.thruDate) {
                $('#' + menuId).jqxMenu('disable', '0menuItem' + gridId, false);
            } else {
                $('#' + menuId).jqxMenu('disable', '0menuItem' + gridId, true);
            }
            if (data.allow == false) {
                $('#1menuItem' + gridId).html('<i class="fa fa-check"></i>Allow');
            } else {
                $('#1menuItem' + gridId).html('<i class="fa fa-ban"></i>Deny');
            }
        });

        window.olbiusFunc.put('appDetail', function (gridId, menuId, itemId, data) {
           window.location.href = 'application?applicationId=' + data.applicationId;
        });

    }(window));
</script>