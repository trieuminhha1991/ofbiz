<link rel="stylesheet" type="text/css" href="../../salesresources/css/dashboard/reportStyle.css" />
<@jqOlbCoreLib />
<style>
	#row00completeOrderGrid > div > input {
		margin: 0!important;
		width: 100%!important;
		height:100%!important;
	}
</style>
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
<div class="test-complete-order row-fluid"  onclick="viewDetail()">
	<div class="icon span3">
		<i class="fa fa-thumbs-o-up content-icon"> </i>
	</div>
	<div class="content span9">
		<div class='content-text'>
			<span>${uiLabelMap.BSMOrderCompleteToday} </span><br />
			<span class="order-complete"> </span>
			<script type="text/javascript">
				var positionType = "SADM";
				$( document ).ready(function() {
					$(function(){
						$.ajax({url: 'getOrderCompleteSA',
						    type: 'post',
						    async: false,
						    data: {
						    	positionType: positionType,
						    },
						    success: function(data) {
						    	var listDatafield = data.listOrderCompleted;
						    	if(listDatafield.length < 0 || listDatafield[0] == null){
						    		$(".order-complete").html("0");
						    	} else{
						    		$(".order-complete").html(formatnumber(listDatafield[0]));	
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

<div id="alterPopupWindowDetail" style="display : none;">
	<div>
		${uiLabelMap.BSMOrderCompleteToday}
	</div>
	<div>
		<div id="completeOrderGrid"></div>
	</div>
</div>

<script>
	$("#alterPopupWindowDetail").jqxWindow({ maxWidth: 700, minWidth: 550, height:270, width:600, minHeight: 100, maxHeight: 500, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'});
	function viewDetail(){
		prepareDataForGrid();
	};
	
	function prepareDataForGrid(){
		$.ajax({
			url: "getListCompleteOrderToday",
			type: "POST",
			data: {
			},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var listCompleteOrderToday = data["listCompleteOrder"];
			for(var i in listCompleteOrderToday){
				var orderDate = listCompleteOrderToday[i].orderDate;
				if(orderDate){
					listCompleteOrderToday[i].orderDate = orderDate.time;
				}
			}
			bindingDataCompleteOrderTodayView(listCompleteOrderToday);
		});
	}
	
	function bindingDataCompleteOrderTodayView(listCompleteOrderToday){
		var sourceCompleteOrderItem =
		{
		     datafields:[{name: 'orderId', type: 'string'},
		                 {name: 'customerName', type: 'string'},
		 				 {name: 'customerAddress', type: 'string'}
		 				],
		     localdata: listCompleteOrderToday,
		     datatype: "array",
		}; 
		var dataAdapter = new $.jqx.dataAdapter(sourceCompleteOrderItem);
		$("#completeOrderGrid").jqxGrid({
			source: dataAdapter,
			 filterable: true,
			 showfilterrow: true,
			 theme: 'olbius',
			 pageable: true, 
			 localization: getLocalization(),
			 sortable: true,
			 pagesize: 5,
			 width: '100%',
			 height: '215px',
			 columns: [
				{text: '${uiLabelMap.BSOrderId}', datafield: 'orderId', width: '20%',
					cellsrenderer: function(row, colum, value) {
            			var data = $('#completeOrderGrid').jqxGrid('getrowdata', row);
		        		return '<span><a target=\"_blank\" href=\"' + 'viewOrder?orderId=' + data.orderId + '\">' + data.orderId + '</a></span>';
					}
				},
				{text: '${uiLabelMap.BSCustomerName}', datafield: 'customerName', width: '30%'},
				{text: '${uiLabelMap.BSOrderAddress}', datafield: 'customerAddress', width: '50%'},
     		  ] 
		});
		
		$('#alterPopupWindowDetail').jqxWindow('open');
	}
	
</script>
