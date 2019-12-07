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
	
	|<a href="javascript:void(0);" onclick="submitdata('FISCAL_MONTH')">${uiLabelMap.accMonthly}</a>
	|<a href="javascript:void(0);" onclick="submitdata('FISCAL_QUARTER')">${uiLabelMap.accQuarterly}</a>
	|<a href="javascript:void(0);" onclick="submitdata('FISCAL_YEAR')">${uiLabelMap.accYearly}</a>
	
	<script type="text/javascript">
		function submitdata(inputdata){
			$('#periodtype').val(inputdata);
			document.balanceTrial.submit();
		}
	</script>
	<form action="balanceTrial" method="POST" name="balanceTrial" id="balanceTrial">
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
                columnsResize: true,
                source: dataAdapter,
                ready: function()
                {
                    $("#treeGrid").jqxTreeGrid('expandRow', '34');
                },
                <#assign maxIndex = (listKey?size - 1)/>
                columns:
                [
                  { text: 'accountName', dataField: "accountName", align: 'center', width: '20%' },	
                  { text: 'glAccountId', dataField: "glAccountId", align: 'center', width: '20%' },
                 
                  
				  <#list listKey as lj>
                 
	                  <#assign x= [1, 2, 3]>
	                   	<#list x as i>
	                   	
	                   		<#if i== 1>
	                   	
		                   	  {text: '${uiLabelMap.BalanceTrialDebit}', dataField: "${lj}_${i}_postedDebits",columnGroup: '${listHeader.get(maxIndex-lj_index)}', align: 'center', cellsrenderer:
		  					 	function(row, colum, value){
		                   		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
	  					 		var tmpCurr = formatcurrency(data.${listKey[maxIndex-lj_index]}.postedDebits,'${currencyUomId}');
	  					 		return "<span>" + tmpCurr + "</span>";
		  					 	}},
	                   		</#if>
		  					<#if i== 2>
			                   	
		                   	  {text: '${uiLabelMap.BalanceTrialCredit}', dataField: "${lj}_${i}_postedCredits",columnGroup: '${listHeader.get(maxIndex-lj_index)}', align: 'center', cellsrenderer:
		  					 	function(row, colum, value){
		                   		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
	  					 		var tmpCurr = formatcurrency(data.${listKey[maxIndex-lj_index]}.postedCredits,'${currencyUomId}');
	  					 		return "<span>" +tmpCurr+ "</span>";
		  					 	}},
		                   	</#if>
	  					 	<#if i== 3>
		                   	  {text: '${uiLabelMap.BalanceTrialBalance}', dataField: "${lj}_${i}_endingBalance",columnGroup: '${listHeader.get(maxIndex-lj_index)}', align: 'center', cellsrenderer:
		  					 	function(row, colum, value){
		                   		var data = $('#treeGrid').jqxTreeGrid('getRow', row);
	  					 		var tmpCurr = formatcurrency(data.${listKey[maxIndex-lj_index]}.endingBalance,'${currencyUomId}');
	  					 		return "<span>" + tmpCurr + "</span>";
		  					 	}},
	                   		</#if> 
	  					 	
		                   
	                   </#list>                	
                  	
                  </#list>
                ], 
                columnGroups: [
                <#list listKey as lj>
                   
                      { text: '${listHeader.get(maxIndex-lj_index)}', name: '${listHeader.get(maxIndex-lj_index)}', align: "center" },
                    
                </#list>
                 ],     
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