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
	<a href="javascript:void(0);" onclick="submitdata('FISCAL_WEEK')">${uiLabelMap.accWeekly}</a>
	|<a href="javascript:void(0);" onclick="submitdata('FISCAL_MONTH')">${uiLabelMap.accMonthly}</a>
	|<a href="javascript:void(0);" onclick="submitdata('FISCAL_QUARTER')">${uiLabelMap.accQuarterly}</a>
	|<a href="javascript:void(0);" onclick="submitdata('FISCAL_YEAR')">${uiLabelMap.accYearly}</a>
	
	<script type="text/javascript">
		function submitdata(inputdata){
			$('#periodtype').val(inputdata);
			document.BalanceSheetStatistic.submit();
		}
	</script>
	<form action="BalanceSheetStatistic" method="POST" name="BalanceSheetStatistic" id="BalanceSheetStatistic">
		<input type="hidden" name="organizationPartyId" value="${parameters.organizationPartyId}"/>
		<input type="hidden" id="periodtype" name="periodtype" value=""/>
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
                    { name: "glAccountId", type: "string" },
					<#list listKey as lj>
                    	{ name: "${lj}", type: "string" },
                    </#list>
                    { name: "accountName", type: "string" },
                    { name: "children", type: "array" },
                    { name: "accountCode", type: "string" }
                ],
                hierarchy:
                {
                    root: "children"
                },
                id: 'glAccountId',
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
                    $("#treeGrid").jqxTreeGrid('expandRow', '100');
                },
                <#assign maxIndex = (listKey?size - 1)/>
                columns:
                [
                  { text: 'accountName', dataField: "accountName", align: 'center', width: '20%' },
                   { text: '', align: 'center', width: '10%', cellsrenderer: function(row, colum, value){
                  		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
						var str = "";<#--TODO when the number is less than zero --> 
						<#if listKey?size gt 1>
                  			var min =  data.${listKey.get(0)};
                  			var max = data.${listKey.get(0)};
							<#list listKey as lj>
                  				if(max < data.${lj}){
                  					max = data.${lj};
                  				}
                  				if(min > data.${lj}){
                  					min = data.${lj};
                  				}
							</#list>
							<#list listKey as lj>
                  				str += "<div style='margin-top:" + (20 - data.${listKey.get(maxIndex-lj_index)}*20/max) + "px;margin-left:1px;float:left;width:6px;height:" + (data.${listKey.get(maxIndex-lj_index)}*20/max + 1) + "px;background-color:blue;'></div>";
							</#list>
                  		<#else>
							str = "";
						</#if>
						return str;
                   }},
				  <#list listKey as lj>
                  	{ text: '${listHeader.get(maxIndex-lj_index)}', dataField: "${listKey[maxIndex-lj_index]}", align: 'center', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
					 		var tmpCurr = formatcurrency(data.${listKey[maxIndex-lj_index]},'${currencyUomId}');
					 		return "<span>" + tmpCurr + "</span>";
					 	}}<#if lj_index != (listKey?size - 1)>,</#if>
                  </#list>
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