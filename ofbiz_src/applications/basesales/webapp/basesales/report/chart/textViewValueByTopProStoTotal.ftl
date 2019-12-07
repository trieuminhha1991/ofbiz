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
<#--<div class="testTopStore">
	<i class="fa fa-line-chart"></i>
	<div class="valuee">
		<div class='titleText'>
			<span class="store_content"></span>
			<script type="text/javascript">
				$(function(){
					var bsStore = "${uiLabelMap.BSSalesChannel}";
					$.ajax({url: 'getTopValue',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var listDatafield = data.listTopStore;
					    	if(listDatafield.length == 0 || listDatafield[0] == null || listDatafield[0] == ""){
					    		$(".store_content").html(bsStore);
					    	} else{
					    		$(".store_content").html(listDatafield[0]);	
					    	}
					    },
					    error: function(data) {
					    	alert('Error !!');
					    }
					});
				});
		</script>
		</div>
		<div class="valueTotal">
			<span class="value-content2"></span>
			<script type="text/javascript">
				$(function(){
					$.ajax({url: 'getTopValue',
					    type: 'post',
					    async: false,
					    success: function(data) {
					    	var listDatafield2 = data.listTopValue;
					    	if(listDatafield2.length == 0 || listDatafield2[0] == null){
					    		$(".value-content2").html("0 VND");
					    	} else{
					    		$(".value-content2").html(formatnumber(listDatafield2[0]) + " VND");	
					    	}
					    },
					    error: function(data) {
					    	alert('Error !!');
					    }
					});
				});
		</script>
		</div>
		<div class="description">
			<span>${uiLabelMap.BSTurnoverByTopStoreThisMonth}</span>
		</div>
	</div>
</div>-->
<script type="text/javascript" id="testTopStore">
	$(function() {
		var bsStore = "${uiLabelMap.BSSalesChannel}";
		var test = OLBIUS.textView({
			id :'testTopStore',
			url: 'getTopValue',
			icon: 'fa fa-line-chart',
			data: {},
			renderTitle: function(data) {
				var listDatafield = data.listTopStore;
		    	if(listDatafield.length == 0 || listDatafield[0] == null || listDatafield[0] == ""){
		    		return bsStore;
		    	} else{
		    		return listDatafield[0];	
		    	}
			},
			renderValue: function(data) {
				var listDatafield2 = data.listTopValue;
		    	if(listDatafield2.length == 0 || listDatafield2[0] == null){
		    		return "0 VND";
		    	} else{
		    		return formatnumber(listDatafield2[0]) + " VND";	
		    	}
			},
			renderDescription: function(data) {
				return '${StringUtil.wrapString(uiLabelMap.BSTurnoverByTopStoreThisMonth)}'
			}
		});
		test.init();
	});
</script>
