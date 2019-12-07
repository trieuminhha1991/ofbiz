<#include "../marco/olbius.ftl"/>
<@jqGridMinimumLib/>
<#--<#assign config = [mapTap("test", "component://securityolbius/widget/SecurityScreens.xml#test"), mapTap("test2", "component://securityolbius/widget/SecurityScreens.xml#test2")]/>

<@olbiusTab config=config/>-->

<#assign dataField = [{"name": "userLoginId", "type": "string"}]/>

<#assign columnlist = {"text": "userLoginId", "dataField": "userLoginId"}/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=UserLogin"/>

<#assign menuConfig=[mapMenuConfig("test", "fa-folder-open-o", "test")]/>

<@olbiusGrid url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig rightClick="test2"/>

<script type="text/javascript">
    $(function(window) {

        window.olbiusFunc.put('test', function(gridId, menuId, itemId) {
           console.log(gridId, menuId, itemId) ;
        });

        window.olbiusFunc.put('test2', function(gridId, menuId, data) {
            $('#0menuItem'+gridId).html('abc');
            console.log(gridId, menuId, data) ;
        });

    }(window));
</script>