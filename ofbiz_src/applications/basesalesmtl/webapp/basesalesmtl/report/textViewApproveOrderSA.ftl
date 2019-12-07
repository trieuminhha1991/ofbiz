<link rel="stylesheet" type="text/css" href="../../salesresources/css/dashboard/reportStyle.css" />
<@jqOlbCoreLib />
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
<div class="test-approve-order row-fluid"  onclick="viewDetail()">
	<div class="icon span3">
		<i class="fa fa-exclamation-triangle content-icon"> </i>
	</div>
	<div class="content span9">
		<div class='content-text'>
			<span>${uiLabelMap.BSMOrderNeedApproveToday} </span><br/>
			<span class="approve_order"> </span>
			<script type="text/javascript">
				$( document ).ready(function() {
					$(function(){
						$.ajax({url: 'getApproveOrderSA',
						    type: 'post',
						    async: false,
						    success: function(data) {
						    	var listDatafield = data.listApproveOrder;
						    	if(listDatafield.length < 0 || listDatafield[0] == null){
						    		$(".approve_order").html("0");
						    	} else{
						    		$(".approve_order").html(formatnumber(listDatafield[0]));	
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

<div id="alterPopupWindowApproveDetail" style="display : none;">
	<div>
		${uiLabelMap.BSMOrderNeedApproveToday}
	</div>
	<div>
		<div id="approveOrderGrid"></div>
	</div>
</div>

<script>
	$("#alterPopupWindowApproveDetail").jqxWindow({ maxWidth: 700, minWidth: 550, height:270, width:600, minHeight: 100, maxHeight: 500, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'});
	function viewDetail(){
		prepareDataForGrid();
	};
	
	function prepareDataForGrid(){
		var position_type = "SA";
		$.ajax({
			url: "getListApproveOrderToday",
			type: "POST",
			data: {
				'positionType': position_type,
			},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var listApproveOrderToday = data["listApproveOrder"];
			for(var i in listApproveOrderToday){
				var orderDate = listApproveOrderToday[i].orderDate;
				if(orderDate){
					listApproveOrderToday[i].orderDate = orderDate.time;
				}
			}
			bindingDataCompleteOrderTodayView(listApproveOrderToday);
		});
	}
	
	function bindingDataCompleteOrderTodayView(listApproveOrderToday){
		var sourceApproveOrderItem =
		{
		     datafields:[{name: 'orderId', type: 'string'},
		                 {name: 'customerName', type: 'string'},
		 				 {name: 'customerAddress', type: 'string'}
		 				],
		     localdata: listApproveOrderToday,
		     datatype: "array",
		}; 
		var dataAdapter = new $.jqx.dataAdapter(sourceApproveOrderItem);
		$("#approveOrderGrid").jqxGrid({
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
            			var data = $('#approveOrderGrid').jqxGrid('getrowdata', row);
		        		return '<span><a target=\"_blank\" href=\"' + 'viewOrder?orderId=' + data.orderId + '\">' + data.orderId + '</a></span>';
					}
				},
				{text: '${uiLabelMap.BSCustomerName}', datafield: 'customerName', width: '30%'},
				{text: '${uiLabelMap.BSOrderAddress}', datafield: 'customerAddress', width: '50%'},
     		  ] 
		});
		
		$('#alterPopupWindowApproveDetail').jqxWindow('open');
	}
	
</script>
