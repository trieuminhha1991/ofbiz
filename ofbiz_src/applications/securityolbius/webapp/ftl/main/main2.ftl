<#include "../marco/olbius.ftl"/>

<#assign dataField = [{"name": "userLoginId", "type": "string"}]/>

<#assign columnlist = {"text": "userLoginId", "dataField": "userLoginId"}/>

<#assign url = "jqxGeneralServicer?sname=viewEntity&entity=UserLogin"/>

<#assign menuConfig=[mapMenuConfig("test", "fa-folder-open-o", "test")]/>

<@olbiusGrid url=url dataField=dataField columnlist=columnlist menuConfig=menuConfig rightClick="test2"/>