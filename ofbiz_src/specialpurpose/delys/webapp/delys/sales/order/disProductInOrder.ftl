<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>

<#--
<div id='contextMenu' class="hide">
    <ul>
        <li id="showProduct">${uiLabelMap.LogViewDetails}
	        <ul>
		        <li><a href="#">Brigita</a></li>
		        <li><a href="#">John</a></li>
		        <li><a href="#">Michael</a></li>
		        <li><a href="#">Peter</a></li>
		        <li><a href="#">Sarah</a></li>
		    </ul>
        </li>
    </ul>
</div>
-->
<div id='contextMenu' style='visibility: hidden; float: left;'>
</div>
<script>
	//Create theme
	$(document).ready(function () {
	});
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	var items = [];
	var source = [];
	$("#jqxgrid").on('rowClick', function (event) {
		source = [
	              	{ html: "<span style='position: relative; left: 3px; top: -2px;'>${uiLabelMap.ReceiveProduct}</span>", 
	              		items
	              	}
	             ];
		$("#contextMenu").jqxMenu({disabled: true, source: source, width: 110, height: 30, autoOpenPopup: false, mode: 'popup'});
		var args = event.args;
	    var boundIndex = args.rowindex;
	    var rightclick = args.rightclick; 
		
        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
        var statusId = dataRecord.statusId;
        var orderId = dataRecord.orderId;
		if(statusId == 'ORDER_APPROVED' || statusId == 'ORDER_COMPLETED'){
			loadDataToContextMenuItem(orderId);
			bindingDataToContextMenuItem(orderId);
		}
    });
	
	var listDelivery = [];
	function loadDataToContextMenuItem(orderId){
		listDelivery = [];
		$.ajax({
			url: "fecthDeliveryIdByOrderId",
			type: "POST",
			data: {
				orderId: orderId,
			},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			listDelivery = data["listDelivery"];
		});	
	}
	
	function bindingDataToContextMenuItem(orderId){
		var checkDisable = 1;
		source = [];
		items = [];
		for(var i in listDelivery){
			var deliveryIdTitle = { 
				html: "<span style='text-align:left;'>${uiLabelMap.DeliveryNote}:</span>" 
						+ "<b style='margin-left: 2px;' class='green-label'>" + listDelivery[i].deliveryId + "</b>"
			}
			items.push(deliveryIdTitle);
		}
		if(items.length == 0){
			$("#contextMenu").jqxMenu({disabled: true});
		}else{
			source = [
		              	{ html: "<span style='position: relative; left: 3px; top: -2px'>${uiLabelMap.ReceiveProduct}</span>", 
		              		items,
		              		subMenuWidth: '170px'
		              	}
		             ];
			$("#contextMenu").jqxMenu({source: source, disabled: false, width: 110, height: 30, autoOpenPopup: false, mode: 'popup' });
		}
	}
	
	$('#contextMenu').on('itemclick', function (event)
	{
		var rowIndex = $("#jqxgrid").jqxGrid('getSelectedRowindex');
        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowIndex);
        var orderId = dataRecord.orderId;
        var orderName = dataRecord.orderName;
        var element = $(event.args).text();
        var value = element.split(":");
        var deliveryId = value[1];
        loadProductList(orderId, deliveryId, orderName);
	});
	
	function loadProductList(orderId, deliveryId, orderName){
		if(deliveryId != undefined){
			window.location.href = "productListInOrder?orderId="+orderId+"&orderName="+orderName+"&deliveryId="+deliveryId;
		}
	}
</script>