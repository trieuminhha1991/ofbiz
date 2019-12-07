<link rel="stylesheet" type="text/css" href="../../salesresources/css/dashboard/reportStyle.css" />
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


<script type="text/javascript" id="getOrderQuantityThisMonth">
$(function() {
	var textView = OLBIUS.textView({
		id :'getOrderQuantityThisMonth',
		url: 'getOrderQuantity',
		icon: 'fa fa-shopping-cart',
		data: {},
		renderTitle: function(data) {
			return '${StringUtil.wrapString(uiLabelMap.BSOrderThisMonth)}'
		},
		renderValue: function(data) {
	    	if(data){
	    		var listDatafield3 = data.listOrderQuantity;
		    	if(listDatafield3.length < 0 || listDatafield3[0] == null || listDatafield3[0] == ""){
		    		return "0";
		    	} else{
		    		return formatnumber(listDatafield3[0]);
		    	}
	    	} else {
	    		return "0";
	    	}
		}
	}).init();
});
</script>