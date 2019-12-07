<#include "../marco/olbius.ftl"/>

<#assign dataField = [
{"name": "partyId", "type": "string"},
{"name": "partyTypeId", "type": "string"},
{"name": "description", "type": "string"}
]/>

<#assign columnlist = [
{"text": "partyId", "dataField": "partyId"},
{"text": "partyTypeId", "dataField": "partyTypeId"},
{"text": "description", "dataField": "description"}
]/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=Party"/>

<#assign menuConfig=[
mapMenuConfig("View party", "fa-eye", "viewParty")
]/>

<@olbiusGrid url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig />

<script type="text/javascript">
    $(function (window) {

        window.olbiusFunc.put('viewParty', function (gridId, menuId, itemId, data) {
            window.location.href = 'party?partyId=' + data.partyId;
        });

    }(window));
</script>