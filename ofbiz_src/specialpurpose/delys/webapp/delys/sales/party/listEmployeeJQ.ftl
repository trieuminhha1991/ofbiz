<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.base.css" type="text/css" />
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript"  src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>

<#assign dataFields = "[{name:'id', type:'string'},
						{name:'parentId', type:'string'},
						{name:'child', type:'array'},
						{name:'expanded', type: 'bool'},
						{name:'fullName', type:'string'},
						{name:'birthday', type: 'date', other: 'Timestamp'},
						{name:'statusId', type:'string'},
						{name:'description', type:'string'},
					]"/>
<#assign columnList = "[{text:'${uiLabelMap.DAName}', datafield: 'fullName', align: 'center'},
						{text:'${uiLabelMap.DABirthday}', datafield: 'birthday', width:'14%', cellsformat: 'dd/MM/yyyy', cellsalign: 'right'},
						{text:'${uiLabelMap.DADescription}', datafield: 'description', width:'25%'},
					]"/>
<#--<#if productList?exists>
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
<#assign columnList=columnList+"{text:'${uiLabelMap.totalAmount}',datafield: 'grandTotal',cellsFormat: 'c0',width:150, align: 'center'}]"/>-->

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.DAExpand)}</li>
	    <li><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.DACollapse)}</li>
	</ul>
</div>

<script type="text/javascript">
    function getDataLocal() {
        var jsc = Array();
        $.ajax({
            url: '<@ofbizUrl>getListEmployeeDSA</@ofbizUrl>',
            type: 'POST',
            dataType: 'json',
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
        var data = getDataLocal();
        var source =
        {
            dataType: "json",
            dataFields: ${dataFields},
            hierarchy: {
                keyDataField: { name: 'id' },
                parentDataField: { name: 'parentId' }
            },
            id:'id',
            localData: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        // create jqxTreeGrid.
        $("#treeGrid").jqxTreeGrid({
            source: dataAdapter,
            width:'100%',
            altRows: true,
            columnsResize: true,
            localization: getLocalization(),
            ready: function () {
                $("#treeGrid").jqxTreeGrid('expandRow', '1');
            },
            columns: ${columnList},
        });
		// create context menu
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
        var contextMenu = $("#contextMenu").jqxMenu({ width: 200, height: 87, autoOpenPopup: false, mode: 'popup', theme: theme});
        $("#treeGrid").on('contextmenu', function () {
            return false;
        });
        $("#treeGrid").on('rowClick', function (event) {
            var args = event.args;
            if (args.originalEvent.button == 2) {
                var scrollTop = $(window).scrollTop();
                var scrollLeft = $(window).scrollLeft();
                contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                return false;
            }
        });
        $("#contextMenu").on('itemclick', function (event) {
            var args = event.args;
	        var tmpKey = $.trim($(args).text());
	        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
	        	$("#treeGrid").jqxTreeGrid('updateBoundData');
	        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAExpand)}") {
	        	var rowData;
	        	var selection = $("#treeGrid").jqxTreeGrid('getSelection');
	        	if (selection.length > 0) {
	        		rowData = selection[0];
	        	}
				if (rowData != undefined && rowData != null) {
					var id = rowData.id;
					$("#treeGrid").jqxTreeGrid('expandRow', id);
				}
	        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DACollapse)}") {
	        	var rowData;
	        	var selection = $("#treeGrid").jqxTreeGrid('getSelection');
	        	if (selection.length > 0) {
	        		rowData = selection[0];
	        	}
				if (rowData != undefined && rowData != null) {
					var id = rowData.id;
					$("#treeGrid").jqxTreeGrid('collapseRow', id);
				}
	        }
        });
    });
</script>
<#--columnGroups: [
                {text: "Product", name: "Product", align: "center"}
            ]-->
<div id="treeGrid">
</div>