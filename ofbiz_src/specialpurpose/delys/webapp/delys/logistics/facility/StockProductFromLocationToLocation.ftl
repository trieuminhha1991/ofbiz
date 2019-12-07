<script>
	var facilityId = '${parameters.facilityId}';
	var locationSeqId = '${parameters.locationSeqId}';
	
	
	<#assign list = listUom.size()/>
    <#if listUom?size gt 0>
		<#assign uomId="var uomId = ['" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + "'"/>
		<#assign description="var description = ['" + StringUtil.wrapString(listUom.get(0).description?if_exists) + "'"/>
		<#if listUom?size gt 1>
			<#list 1..(list - 1) as i>
				<#assign uomId=uomId + ",'" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + "'"/>
				<#assign description=description + ",'" + StringUtil.wrapString(listUom.get(i).description?if_exists) + "'"/>
			</#list>
		</#if>
		<#assign uomId=uomId + "];"/>
		<#assign description=description + "];"/>
	<#else>
		<#assign uomId="var uomId = [];"/>
    	<#assign description="var description = [];"/>
    </#if>
	${uomId}
	${description}
	var adapter = new Array();
	for(var i = 0; i < ${list}; i++){
		var row = {};
		row['uomId'] = uomId[i];
		row['description'] = description[i];
		adapter[i] = row;
	}
	
	
	
	
</script>
		<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
			if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
				return 'Not Data';
			}
			
			var ordersDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
		    var orders = ordersDataAdapter.records;
		    
				var nestedGrids = new Array();
		        var id = datarecord.uid.toString();
		        
		         var grid = $($(parentElement).children()[0]);
		         $(grid).attr(\"id\",\"jqxgridDetail\");
		         nestedGrids[index] = grid;
		       
		         var ordersbyid = [];
		        
		         for (var ii = 0; ii < orders.length; ii++) {
		                 ordersbyid.push(orders[ii]);
		         }
		         var orderssource = { datafields: [	
		         	 { name: \'inventoryItemId\', type:\'string\' },
		         	 { name: \'productId\', type:\'string\' },
		         	 { name: \'quantity\', type:\'string\' },
		         	 { name: \'quantityTranfers\', type: \'string\' },
		         	 { name: \'uomId\', type:\'string\' },
		         	 { name: \'locationSeqIdCurrent\', type:\'string\' },
		         	 { name: \'stockLocationClick\', type: \'string\'},
		         	],
		           
		             localdata: ordersbyid
		         }
		         var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
		        
		         if (grid != null) {
		         	 var quantityTranfers;
		         	 grid.on(\'cellendedit\', function (event)
					 {	
					 	var rowBoundIndex = event.args.rowindex;
						var data = grid.jqxGrid('getrowdata', rowBoundIndex);
					 });
		         	 
		             grid.jqxGrid({
		                 source: nestedGridAdapter, width: 1050,
		                 showtoolbar:false,
				 		 editmode:\"dblclick\",
				 		 showheader: true,
				 		 selectionmode:\"singlecell\",
				 		 theme: 'energyblue',
				 		 editable: true,
		                 columns: [
		                   { text: \'${uiLabelMap.InventoryItemId}\', datafield: \'inventoryItemId\',editable: false},
		                   { text: \'${uiLabelMap.ProductId}\', datafield: \'productId\',editable: false},
		                   { text: \'${uiLabelMap.Quantity}\', datafield: \'quantity\',editable: false},
		                   { text: \'${uiLabelMap.QuantityTransferSum}\', dataField: \'quantityTranfers\', editable: true,
		                	   validation: function (cell, value) {
		                          if (value == '') {
		                              return { result: false, message: 'No' };
		                          }
		                          return true;
		                   },},
		                   { text: \'${uiLabelMap.QuantityUomId}\', datafield: \'uomId\',editable: false},
		                   { text: '${uiLabelMap.StockLocationInFacility}', datafield: 'stockLocationClick', editable:false,
						   		cellsrenderer: function (row, column, value) {
							 		var data = grid.jqxGrid('getrowdata', row);
		        					var productId = data.productId;
		        					var quantity = data.quantity;
		        					var locationSeqIdCurrent = data.locationSeqIdCurrent;
		        					var uomId = data.uomId;
		        					quantityTranfers = args.value;
		        					var quantityRemain = quantity - quantityTranfers;
		        					var locationSeqIdTranfer = ${locationSeqId};
		        					return '<a style = \"margin-left: 10px\" onclick=\"stockLocationClick(&#39;' + row + '&#39;, &#39;' +  productId + '&#39;, &#39;' + quantity + '&#39;, &#39;' + locationSeqIdCurrent + '&#39;, &#39;' + quantityTranfers + '&#39;, &#39;' + uomId + '&#39;, &#39;' + quantityRemain + '&#39;, &#39;' + locationSeqIdTranfer + '&#39;)\" href=\"javascript:void(0)\" class=\"copyLink\">' +  '${uiLabelMap.StockLocation}' + '</a>'
		    				   }
						   }
		                 ]
		             });
		         }
		         
		 }"/>
		<#assign dataField1="[
					 	{ name: 'locationSeqId', type: 'string' },
					 	{ name: 'rowDetail', type: 'string' }
		 		 	 ]"/>
		<#assign columnlist1="{ text: '${uiLabelMap.FacilitylocationSeqIdCurrent}', dataField: 'locationSeqId', editable: false}
							 "/>
		<@jqGrid id="jqgrid2" filtersimplemode="true"  usecurrencyfunction="true" addType="popup" dataField=dataField1 
				columnlist=columnlist1 clearfilteringbutton="true" showtoolbar="false" addrow="true" filterable="true" editmode="dblclick" editable="true" 
				url="jqxGeneralServicer?sname=JQXgetLocationAndQuantityByFacilityId&facilityId=${parameters.facilityId}&locationSeqId=${parameters.locationSeqId}" 
				 initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"
		/>
		
		
		
		<div id="copyPopupWindow">
		    <div>${uiLabelMap.StockProductIdForLocationInFacility} ${parameters.locationSeqId?if_exists}</div>
		    <div class="row-fluid">
		    	<div class="span9">
		    		<div class="span5">
						<input id="facilityId" type="hidden" value=${parameters.facilityId}></input>
					</div>
				</div>	
				<div class="span9">
		    		<div class="span5">
            			${uiLabelMap.ProductProductId}:
    	 			</div>
    	 			<div class="span4"> 
	 					<span id='productId' style='margin-left: 10px; float: left;'></span>
    	 			</div>
    	 		</div>	
    	 		<div class="span9">
		    		<div class="span5">
            			${uiLabelMap.QuantityCurrent}:
    	 			</div>
    	 			<div class="span4"> 
	 					<span id='quantityCurrent' style='margin-left: 10px; float: left;'></span>
    	 			</div>
    	 		</div>
    	 		<div class="span9">
		    		<div class="span5">
            			${uiLabelMap.QuantityTransferSum}:
    	 			</div>
    	 			<div class="span4"> 
						<span id='quantityTransfer' style='margin-left: 10px; float: left;'></span>
    	 			</div>
    	 		</div>
    	 		<div class="span9">
		    		<div class="span5">
            			${uiLabelMap.QuantityRemain}:
    	 			</div>
    	 			<div class="span4"> 
						<span id='quantityRemain' style='margin-left: 10px; float: left;'></span>
    	 			</div>
    	 		</div>
    	 		<div class="span9">
		    		<div class="span5">
            			${uiLabelMap.FacilitylocationSeqIdCurrent}:
    	 			</div>
    	 			<div class="span4"> 
	 					<span id='locationSeqIdCurrent' style='margin-left: 10px; float: left;'></span>
    	 			</div>
    	 		</div>	
    	 		<div class="span9">
		    		<div class="span5">
            			${uiLabelMap.ToLocationSumLocation}:
    	 			</div>
    	 			<div class="span4"> 
	 					<span id='locationSeqIdTranfer' style='margin-left: 10px; float: left;'></span>
    	 			</div>
    	 		</div>	
    	 		<div class="span9">
		    		<div class="span5">
            			${uiLabelMap.QuantityUomId}:
    	 			</div>
    	 			<div class="span4"> 
    	 				<span id='uomId' style='margin-left: 10px; float: left;'></span>
    	 			</div>
    	 		</div>
		    	<div class="span9">
		 			<input style="margin-right: 5px;" type="button" id="alterCopy" value="${uiLabelMap.StockLocation}" />
	               	<input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />            	
		        </div> 		
		    </div>
		</div>
		
	
<script>
	var quantityJqGird = new Array();
	var locationSeqIdTranfer = [];
	var quantityTranfers = [];
	var args;
    $('#jqgrid2').on('cellEndEdit', function (event) {
	    	args = event.args;
	    	var rowBoundIndex = args.rowindex;
	    	quantityJqGird = $("#jqgrid2").jqxGrid("getrowdata", rowBoundIndex);
	    	locationSeqIdTranfer.push(quantityJqGird.locationSeqId);
	    	quantityTranfers.push(args.value); 
	});
</script>		
		
<script>
	//Create theme
 	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	
	
	//Create quantityTransfer
	$("#quantityTransfer").jqxInput();
	
	//Create Copy popup
	$("#copyPopupWindow").jqxWindow({
       width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });


	$("#alterCancel").jqxButton();
    $("#alterCopy").jqxButton();
	
	function stockLocationClick(row, productId, quantity, locationSeqIdCurrent, quantityTranfers, uomId, quantityRemain, locationSeqIdTranfer){
    	//productId
    	$('#productId').text(productId);
    	$('#quantityCurrent').text(quantity);
    	$('#locationSeqIdCurrent').text(locationSeqIdCurrent);
    	$('#quantityTransfer').text(quantityTranfers);
    	$('#uomId').text(uomId);
    	$('#quantityRemain').text(quantityRemain);
    	$('#locationSeqIdTranfer').text(locationSeqIdTranfer);
    	if (quantityTranfers == 'undefined') {
    		$("#contentjqxgridDetail").jqxGrid('begincelledit', row, "quantityTranfers");
		}else {
			$('#copyPopupWindow').jqxWindow('open');
		}
    }
    
    $("#alterCopy").click(function () {
    
    	var productId = $('#productId').text();
    	var facilityId = $('#facilityId').val();
    	var locationSeqIdTranfer = $('#locationSeqIdTranfer').text();
    	var quantityCurrent = $('#quantityCurrent').text();
    	var quantityTransfer = $('#quantityTransfer').text();
    	var locationSeqIdCurrent = $('#locationSeqIdCurrent').text();
    	var uomId = $('#uomId').text();
    	var request = $.ajax({
			  url: "stockLocation",
			  type: "POST",
			  data: {productId : productId, quantityCurrent: quantityCurrent, quantityTransfer: quantityTransfer, uomId: uomId, facilityId: facilityId, locationSeqIdTranfer: locationSeqIdTranfer, locationSeqIdCurrent: locationSeqIdCurrent},
			  dataType: "html"
			});
			
			request.done(function(data) {
				$('#jqgrid2').jqxGrid('updatebounddata');
				if(data.responseMessage == "error"){
	            	$('#jqxNotification').jqxNotification({ template: 'error'});
	            	$("#jqxNotification").text(data.errorMessage);
	            	$("#jqxNotification").jqxNotification("open");
	            }else{
	            	$('#container').empty();
	            	$('#jqxNotification').jqxNotification({ template: 'info'});
	            	$("#jqxNotification").text("Thuc thi thanh cong!");
	            	$("#jqxNotification").jqxNotification("open");
	            	$('#jqxGrid').jqxGrid('updatebounddata');
	            }
			});
			
			request.fail(function(jqXHR, textStatus) {
			  alert( "Request failed: " + textStatus );
			});
        $("#copyPopupWindow").jqxWindow('close');
    });
</script>