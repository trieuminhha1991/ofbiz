<link rel="stylesheet" type="text/css" href="/accresources/css/reportStyle.css" />
<script>
function formatnumber(num){
    if(num == null){
        return "";
    }
    decimalseparator = ",";
    thousandsseparator = ".";

    var str = num.toString(), parts = false, output = [], i = 1, formatted = null;
    if(str.indexOf(".") > 0) {
        parts = str.split(".");
        str = parts[0];
    }
    str = str.split("").reverse();
    var c;
    for(var j = 0, len = str.length; j < len; j++) {
        if(str[j] != ",") {
        	if(str[j] == '-'){
        		if(output && output.length > 1){
        			if(output[output.length - 1] == '.'){
        				output.splice(output.length - 1,1);
        			}
            		c = true;
            		break;
        		}
        	} 
            output.push(str[j]);
            if(i%3 == 0 && j < (len - 1)) {
            	output.push(thousandsseparator);
            }
            i++;
        }
    }
    if(c) output.push("-");
    formatted = output.reverse().join("");
    return(formatted);
};
</script>


<script type="text/javascript">
var listDatafield = null;
</script>

<div class="testtest2">
	<i class="fa fa-line-chart"></i>
	<div class="valuee">
		<div class='titleText'>
			<span>${uiLabelMap.BACCGrossProfitThisMonth}</span>
		</div>
		<div class="valueTotal">
			<span class="grossProfit-content"></span>
			<script type="text/javascript">
				var dateCurrent = new Date();
				var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);	
				$( document ).ready(function() {
					$(function(){
						$.ajax({url: 'getIndexAccValue',
						    type: 'post',
						    async: false,
						    data: {
						    	'service': 'acctgTrans',
					    		'fromDate': OLBIUS.dateToString(currentFirstDay),
			                    'thruDate': OLBIUS.dateToString(dateCurrent),						    		
						    },
						    success: function(data) {
						    	listDatafield = data.data;	
						    	var cogs = 0;
						    	var saleIncome = 0;
						    	var deductions = 0;
						    	var netRevenue = 0;
						    	var grossProfit = 0;
						    	if(listDatafield == null || listDatafield.length <= 0 ){
						    		$(".grossProfit-content").html("0 VND");
						    		$(".netRevenue-content").html("0 VND");
						    		$(".cogs-content").html("0 VND");
						    		$(".saleIncome-content").html("0 VND");
						    		$(".deductions-content").html("0 VND");	
						    	} else{
				    			 	cogs = listDatafield[0].x632;
				    			 	saleIncome = listDatafield[0].x511;
				    			 	deductions = listDatafield[0].x521;
						    		 
						    		netRevenue = saleIncome - deductions;
						    		grossProfit = netRevenue - cogs;
						    				 
						    		$(".grossProfit-content").html(formatnumber(grossProfit) + " VND");
						    		$(".netRevenue-content").html(formatnumber(netRevenue) + " VND");
						    		$(".cogs-content").html(formatnumber(cogs) + " VND");
						    		$(".saleIncome-content").html(formatnumber(saleIncome) + " VND");
						    		$(".deductions-content").html(formatnumber(deductions) + " VND");	
						    	}						    	
						    },
						    error: function(data) {
						    	alert('Error !!');
						    }
						});
					});
				});							
			</script>
		</div>
	</div>
</div>
