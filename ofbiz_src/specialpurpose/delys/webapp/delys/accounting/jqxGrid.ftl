<#include "jqxRequiredLibs.ftl"/>
<@jqGridMinimumLib/>
<#macro jqGridMinimumLib url dataField columnlist
						 columnsresize="false" id="id">
   <script type="text/javascript">
        $(document).ready(function () {
        	$.jqx.theme = 'base';
            var source =
            {
                datatype: "json",
                datafields: [${dataField}],
                id: '${id}',
                type: 'POST',
                contentType: 'application/x-www-form-urlencoded',
                beforeprocessing: function (data) {
                    source.totalrecords = data.TotalRows;
                },
                url: ${url}
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#jqxgrid").jqxGrid(
            {
                width: 850,
                source: dataAdapter,
                columnsresize: ${columnsresize},
                columns: [${columnlist}]
            });
        });
    </script>
	<div id='jqxWidget' style="font-size: 13px; font-family: Verdana; float: left;">
	    <div id="jqxgrid"></div>
	</div>
</#macro>