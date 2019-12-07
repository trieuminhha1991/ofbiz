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
<#--<div class="test-value-total row-fluid">
	<div class="icon span3">
		<i class="fa fa-bar-chart content-icon"></i>
	</div>
	<div class="content span9">
		<div class='content-text'>
			<span>${uiLabelMap.BSTurnoverThisMonth} </span><br />
			<span class="value-content"></span>
			<script type="text/javascript">
			setTimeout(function(){ 
					$(function(){
						$.ajax({url: 'getValueFinal',
						    type: 'post',
						    async: false,
						    success: function(data) {
						    	var listDatafield = data.listValue;
						    	if(listDatafield.length < 0 || listDatafield[0] == null){
						    		$(".value-content").html("0 VND");
						    	} else{
						    		$(".value-content").html(formatnumber(listDatafield[0]) + " VND");	
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
</div>-->

<script type="text/javascript" id="testTextView">
	$(function() {
//		console.log('test');
		var test = OLBIUS.textView({
			id :'testTextView',
			url: 'getValueFinal',
			icon: 'fa fa-bar-chart',
			data: {},
			renderTitle: function(data) {
				return '${StringUtil.wrapString(uiLabelMap.BSTurnoverThisMonth)}'
			},
			renderValue: function(data) {
				var listDatafield = data.listValue;
		    	if(listDatafield.length < 0 || listDatafield[0] == null){
		    		return "0 VND";
		    	} else{
		    		return formatnumber(listDatafield[0]) + " VND";	
		    	}
			}
//			renderDescription: function(data) {}
		});
		test.init();
	});
</script>