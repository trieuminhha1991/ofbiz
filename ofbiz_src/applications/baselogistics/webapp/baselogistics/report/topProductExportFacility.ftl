<link rel="stylesheet" type="text/css" href="../../logresources/css/reportStyle.css" />
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
<div>
	<div class='titleText'>
		<span>${uiLabelMap.LOGTopProductExportMost}</span>
	</div>
	<div class="valueTotal">
		<span class="mostExportProduct"></span>  
		<script type="text/javascript">
			$( document ).ready(function() {
				$(function(){
					$.ajax({url: 'getMostExportProductReportOlap',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var listDatafield = data.listValue;
					    	var productName = "";
					    	var quantity = null;
					    	var quantityUomId = "";
					    	for(var i in listDatafield){
					    		quantity = listDatafield[0];
					    		productName = listDatafield[1];
					    		quantityUomId = listDatafield[2];
					    	}
					    	if(quantity != null){
					    		if(quantity < 0){
						    		quantity = quantity * (-1);
						    	}
						    	$(".mostExportProduct").html(productName + ": " + formatnumber(quantity) + " ("+quantityUomId+")");	
					    	}else{
					    		$(".mostExportProduct").html(0);	
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
	<div class='distance'>
		<p>-------------------------------------------------------------------------------------------</p>
	</div>
	<div class='titleText'>
		<span>${uiLabelMap.LOGTopLeastProductExportMost}</span>
	</div>
	<div class="valueTotal">
		<span class="leastExportProduct"></span>
		<script type="text/javascript">
			$( document ).ready(function() {
				$(function(){
					$.ajax({url: 'getLeastExportProductReportOlap',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var listDatafield = data.listValue;
					    	var productName = "";
					    	var quantity = null;
					    	var quantityUomId = "";
					    	for(var i in listDatafield){
					    		quantity = listDatafield[0];
					    		productName = listDatafield[1];
					    		quantityUomId = listDatafield[2];
					    	}
					    	if(quantity != null){
					    		if(quantity < 0){
						    		quantity = quantity * (-1);
						    	}
						    	$(".leastExportProduct").html(productName + ": " + formatnumber(quantity) + " ("+quantityUomId+")");	
					    	}else{
					    		$(".leastExportProduct").html(0);	
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
