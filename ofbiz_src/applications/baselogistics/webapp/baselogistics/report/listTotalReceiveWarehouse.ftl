<link rel="stylesheet" type="text/css" href="../../accresources/css/reportStyle.css" />
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
		<span>${uiLabelMap.LOGTotalImport}</span>
	</div>
	<div class="valueTotal">
		<span class="totalReceive"></span>
		<script type="text/javascript">
			$( document ).ready(function() {
				$(function(){
					$.ajax({url: 'getTotalReceiveReportOlap',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var listDatafield = data.listValue;
					    	if(listDatafield.length < 0 || listDatafield[0] == null){
					    		$(".totalReceive").html("0");
					    	} else{
					    		$(".totalReceive").html(formatnumber(listDatafield));	
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
