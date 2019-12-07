<#macro jqxTreeLib>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
</#macro>
<#macro jqxTree id="jqxTree" width="auto" height="auto" hasThreeStates=false checkboxes=false dataFields="" idSource="id" labelSource="label" localData="[]" dataUnFormat=false dataAjaxFunc="" dataUnFormat2=false>
	<@jqxTreeLib/>
    <script type="text/javascript">
        $(document).ready(function () {
        	<#if dataUnFormat && !dataUnFormat2 && localData?has_content>
	            // Create jqxTree
	            <#if localData?exists>
		            var data_${id} = [
		            	<#list localData as item>
			        	{<#if item?exists>
			        		<#assign hasItem = false>
			        		<#list item?keys as key>
				        		"${key}": "${item[key]}",
				        		<#if key == "hasItem" && item[key] == "true"><#assign hasItem = true></#if>
			                </#list>
			                <#if hasItem>"items": [{"label": "Loading..."}]</#if>
		                </#if>},
			        	</#list>
		            ];
	            <#else>
	            	var data_${id} = [];
	            </#if>
	            // prepare the data
	            var source_${id} = {
	                datatype: "json",
	                datafields: ${dataFields},
	                id: "${idSource}",
	                localdata: data_${id}
	            };
	            
	            var dataAdapter_${id} = new $.jqx.dataAdapter(source_${id});
	            dataAdapter_${id}.dataBind();
	            var records_${id} = dataAdapter_${id}.getRecordsHierarchy('${idSource}', 'parentId', 'items', [{ name: '${labelSource}', map: 'label'}, { name: '${idSource}', map: 'value'}]);
            <#elseif dataUnFormat && dataUnFormat2 && localData?has_content>
	            // Create jqxTree
	            <#if localData?exists>
		            var data_${id} = <@buildLocalData localData/>;
	            <#else>
	            	var data_${id} = [];
	            </#if>
	            // prepare the data
	            var source_${id} = {
	                datatype: "json",
	                datafields: ${dataFields},
	                id: "${idSource}",
	                localdata: data_${id}
	            };
	            
	            var dataAdapter_${id} = new $.jqx.dataAdapter(source_${id});
	            dataAdapter_${id}.dataBind();
	            var records_${id} = dataAdapter_${id}.getRecordsHierarchy('${idSource}', 'parentId', 'items', [{ name: '${labelSource}', map: 'label'}, { name: '${idSource}', map: 'value'}]);
            <#elseif localData?has_content && "[]" != localData>
            	var data_${id} = jQuery.parseJSON("${StringUtil.wrapString(localData)}");
	            var source_${id} = {
	                datatype: "json",
	                datafields: ${dataFields},
	                id: "${idSource}",
	                localdata: data_${id}
	            };
	            var dataAdapter_${id} = new $.jqx.dataAdapter(source_${id});
	            dataAdapter_${id}.dataBind();
	            var records_${id} = dataAdapter_${id}.getRecordsHierarchy('${idSource}', 'parentId', 'items', [{ name: '${labelSource}', map: 'label'}, { name: '${idSource}', map: 'value'}]);
            </#if>
            <#if dataAjaxFunc?has_content>
            	var records_${id} = ${dataAjaxFunc};
            </#if>
            
            $('#${id}').jqxTree({ 
            	source: records_${id}, 
            	theme:'energyblue',
            	<#if width?exists>width: '${width}', </#if>
            	<#if height?exists>height: '${height}', </#if>
            	<#if hasThreeStates?is_boolean && hasThreeStates>hasThreeStates: ${hasThreeStates?string}, </#if>
            	<#if checkboxes?is_boolean && checkboxes>checkboxes: ${checkboxes?string}</#if>
            });
            $('#${id}').jqxTree('selectItem', null);
            $('#${id}').css('visibility', 'visible');
        });
    </script>
    
    <div id='${id}'></div>
</#macro>

<#global jqxTree=jqxTree/>
<#macro buildLocalData localData>
	[
	<#list localData as item>
		<#if item?exists>
			{<#list item.entrySet() as entry>
	    		<#if entry.key == "items">
	    			"items" : <@buildLocalData entry.value/>
	    		<#else>
	    			"${entry.key}" : "${StringUtil.wrapString(entry.value?string)}",
	    		</#if>
	        </#list>},
	    </#if>
	</#list>
	]
</#macro>
<#--
<#function buildLocalData localData>
	<#assign returnValue = "[">
	<#list localData as item>
		<#assign itemValue = "{">
		<#if item?exists>
			<#list item.entrySet() as entry>
	    		<#if entry.key == "items">
	    			<#assign tmp = buildLocalData(entry.value)/>
	    			<#if tmp?has_content>
	    				<#assign itemValue = itemValue + "'${entry.key}' : "/>
	    				<#assign itemValue = itemValue + tmp/>
	    			</#if>
	    		<#else>
	    			<#assign itemValue = itemValue + "'${entry.key}' : '${entry.value}'"/>
	    		</#if>
	        </#list>
	    </#if>
	    <#assign itemValue = itemValue + "}, "/>
	    <#if itemValue?has_content>
	    	<#assign returnValue = returnValue + itemValue/>
	    </#if>
	</#list>
	<#assign returnValue = returnValue + "]">
  	<#return returnValue>
</#function>
-->
<#--
// prepare the data
var source =
{
    datatype: "json",
    datafields: [
        { name: 'customTimePeriodId' },
        { name: 'parentPeriodId' },
        { name: 'periodName' },
        { name: 'isClosed' }
    ],
    id: 'Id',
    type: 'POST',
    cache: false,
    data: {
       	periodTypeModule : 'sales', 
    	periodTypeId : 'SALES_YEAR',
    	sname : 'getListCustomTimePeriod'
    },
    contentType: 'application/x-www-form-urlencoded',
    url: 'getCustomTimePeriodJson',
    root: 'listCustomTimePeriod'
};
-->