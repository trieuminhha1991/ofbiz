<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.base.css" type="text/css" />
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript"  src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>



<#assign dataFields="[{name:'id',type:'number'},{name:'name',type:'string'},{name:'child',type:'array'}," />
<#assign columnList="[{text:'${uiLabelMap.organizationName}',datafield: 'name',width:250, align: 'center'},"/>
<#if productList?exists>
	<#list productList  as item>
	    <#assign dataFields=dataFields+"{name:'${item.productId}',type:'string'},"/>
	    <#if item.internalName?has_content>
	        <#assign name=item.internalName/>
	    <#else>
	        <#assign name=item.productId/>
	    </#if>
	    <#assign columnList=columnList+"{text:'${name}',columngroup:'Product',datafield: '${item.productId}',width:100, cellsalign: 'right'},"/>
	</#list>
</#if>
<#assign dataFields=dataFields+"{name:'grandTotal',type:'string'}]" />
<#assign columnList=columnList+"{text:'${uiLabelMap.totalAmount}',datafield: 'grandTotal',cellsFormat: 'c0',width:150, align: 'center'}]"/>

<script type="text/javascript">
    function getDataSalesIn(dsaId) {
        var jsc = Array();
        $.ajax({
            url: '<@ofbizUrl>getDataSalesIn</@ofbizUrl>',
            type: 'POST',
            dataType: 'json',
            data: {dsaId: dsaId},
            async: false,
            success: function (data) {
                jsc = data;
            }
        });
        return jsc;
    }
    $(document).ready(function () {
        $.jqx.theme = 'olbius';
        theme = $.jqx.theme;
        var getLocalization = function () {
            var localizationobj = {};
            localizationobj.currencySymbol = "đ";
            localizationobj.currencySymbolPosition = "after";
            return localizationobj;
        }
        var data = getDataSalesIn("DSA");
        var source =
        {
            dataType: "json",
            dataFields: ${dataFields},
            hierarchy: {
                root: "child"
            },
            id:'id',
            localData: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        // create jqxTreeGrid.
        $("#treeGrid").jqxTreeGrid(
                {
                    source: dataAdapter,
                    altRows: true,
                    columnsResize: true,
                    localization: getLocalization(),
                    ready: function () {
                        $("#treeGrid").jqxTreeGrid('expandRow', '3');
                        $("#treeGrid").jqxTreeGrid('expandRow', '5');
                    },
                    columns: ${columnList},
                    columnGroups: [
                        {text: "Product", name: "Product", align: "center"}
                    ]
                });
    });


</script>

<div id="treeGrid">
  
</div>