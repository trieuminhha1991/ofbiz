<link rel="stylesheet" href="/aceadmin/jqw/jqwidgets/styles/jqx.base.css" type="text/css" />
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<div style="margin-top:5px;margin-bottom:5px;">
	<#if parameters.periodtype?has_content && parameters.periodtype != "FISCAL_MONTH">
		<a href="javascript:void(0);" onclick="submitdata('FISCAL_MONTH')">${uiLabelMap.accMonthly}</a>
	<#else>
		${uiLabelMap.accMonthly}
	</#if>
	<#if parameters.periodtype?has_content && parameters.periodtype != "FISCAL_QUARTER">
		|<a href="javascript:void(0);" onclick="submitdata('FISCAL_QUARTER')">${uiLabelMap.accQuarterly}</a>
	<#else>
		|${uiLabelMap.accQuarterly}
	</#if>
	<#if !parameters.periodtype?has_content || parameters.periodtype == "FISCAL_YEAR">
		|${uiLabelMap.accYearly}
	<#else>
		|<a href="javascript:void(0);" onclick="submitdata('FISCAL_YEAR')">${uiLabelMap.accYearly}</a>
	</#if>
	|<a href="DemonstrationReport.pdf?organizationPartyId=company">>>PDF</a>
	<script type="text/javascript">
		function submitdata(inputdata){
			$('#periodtype').val(inputdata);
			document.BalanceSheetReport.submit();
		}
	</script>
	<form action="FinancialStatement" method="POST" name="BalanceSheetReport" id="BalanceSheetReport">
		<input type="hidden" name="organizationPartyId" value="${parameters.organizationPartyId}"/>
		<input type="hidden" id="periodtype" name="periodtype" value=""/>
		<input type="hidden" id="reportTypeId" name="reportTypeId" value="9003"/>
		<input type="hidden" id="mType" name="mType" value="demostrationFS"/>
	</form>
</div>
<script type="text/javascript">
        $(document).ready(function () {    
        	$.jqx.theme = 'olbius';        
 			var data = ${StringUtil.wrapString(context.testJson)}
            // prepare the data
            var source =
            {
                dataType: "json",
                dataFields: [
                    { name: "targetId", type: "string" },
                    { name: "name", type: "string" },
                    { name: "demonstration", type: "string" },
                    { name: "value1", type: "string" },
                    { name: "value2", type: "string" },
                    { name: "children", type: "array" },
                    { name: "code", type: "string" }
                ],
                hierarchy:
                {
                    root: "children"
                },
                id: 'targetId',
                localData: data
            };
            var dataAdapter = new $.jqx.dataAdapter(source, {
                loadComplete: function () {
                }
            });
            // create Tree Grid
            $("#treeGrid").jqxTreeGrid(
            {
                width: 1200,
                altRows: true,
                theme:theme,
                source: dataAdapter,
                ready: function()
                {
                    $("#treeGrid").jqxTreeGrid('expandRow', '34');
                },
                columns:
                [
                  { text: '${uiLabelMap.target}', dataField: "name", align: 'center', width: '45%' },
                  { text: '${uiLabelMap.code}', cellsAlign: "center", dataField: "code", align: 'center', width: '7.5%' },
                  { text: '${uiLabelMap.demonstration}', cellsAlign: "center", dataField: "demonstration", align: 'center', width: '7.5%' },
                  { text: '${context.strCurrentYear}', dataField: "value1", cellsAlign: "right", align: 'center', width: '20%', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
					 		var tmpCurr = formatcurrency(data.value1,'${currencyUomId}');
					 		return "<span>" + tmpCurr + "</span>";
					 	}},
                  { text: '${context.strPreviousYear}', dataField: "value2", cellsAlign: "right", align: 'center', width: '20%', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
					 		var tmpCurr = formatcurrency(data.value2,'${currencyUomId}');
					 		return "<span>" + tmpCurr + "</span>";
					 	}}
                ]
            });
        });
		function formatcurrency(num, uom){
				decimalseparator = ",";
		     	thousandsseparator = ".";
		     	currencysymbol = "đ";
		     	if(typeof(uom) == "undefined" || uom == null){
		     		uom = "${currencyUomId?if_exists}";
		     	}
				if(uom == "USD"){
					currencysymbol = "$";
					decimalseparator = ".";
		     		thousandsseparator = ",";
				}else if(uom == "EUR"){
					currencysymbol = "€";
					decimalseparator = ".";
		     		thousandsseparator = ",";
				}
			    var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
			    if(str.indexOf(".") > 0) {
			        parts = str.split(".");
			        str = parts[0];
			    }
			    str = str.split("").reverse();
			    for(var j = 0, len = str.length; j < len; j++) {
			        if(str[j] != ",") {
			            output.push(str[j]);
			            if(i%3 == 0 && j < (len - 1)) {
			                output.push(thousandsseparator);
			            }
			            i++;
			        }
			    }
			    formatted = output.reverse().join("");
			    return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
			};
    </script>
    
    <div id="treeGrid">
    </div>