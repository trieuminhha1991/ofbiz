<script language="JavaScript" type="text/javascript">
	var facilityId = '${parameters.facilityId}';
	var locationSeqId = '${parameters.locationSeqId?if_exists}';
	
    function quicklookup(func, locationelement, facilityelement, productelement) {
        
        var productId = productelement.value;
        if (productId.length == 0) {
          alert("${StringUtil.wrapString(uiLabelMap.ProductFieldEmpty)}");
          return;
        }
        var facilityId = facilityelement.value;
        var request = "LookupProductInventoryItemLocation?productId=" + productId + "&facilityId=" + facilityId;
        window[func](locationelement, request);
    }
    
    
    
    function myFunction(){
    	var selectedRow = $('#jqxgrid').jqxGrid('selectedrowindexes');
    	//Adding event listeners
            //Creating the demo window
            			var productTitle =  '${uiLabelMap.ProductProductId}';
                    	var locationTitle = '${uiLabelMap.FacilitylocationSeqIdCurrent}';
                    	var quantityTitle = '${uiLabelMap.QuantityTransferSum}';
                    	var quantity = '';
                    	quantity += '<table style="border-collapse: collapse; border: 1px solid black;>'
	                    				+ '<tr style="border: 1px solid black;">' 
	                    							+ '<td style="border: 1px solid black;" width=180px; height:50px;>'
	                    								+ productTitle
	                    							+ '</td>'
	                    							+ '<td style="border: 1px solid black;" width=100px; height:50px;>'
	                    								+ locationTitle
	                    							+ '</td>'
	                    							+ '<td style="border: 1px solid black;" width=100px; height:50px;>'
	                    								+ quantityTitle
	                    							+ '</td>'
	                    				+ '</tr>';
	                    
                    	for (var i = 0; i < selectedRow.length; i++){
                    		var data = $('#jqxgrid').jqxGrid('getrowdata', selectedRow[i]);
                    		quantity +=  	'<tr style="border: 1px solid black;">' 
                    							+ '<td style="border: 1px solid black;" width=250px; height:50px;>'
                    								+ data.productId
                    							+ '</td>'
                    							+ '<td style="border: 1px solid black;" width=125px; height:50px;>'
                    								+ data.locationSeqId
                    							+ '</td>'
                    							+ '<td style="border: 1px solid black;" width=125px; height:50px;>';
					                    		if (data.quantityTranfers != undefined) {
					    							quantity += data.quantityTranfers
					                    		}
        					quantity += '</td></tr>';
                    	}
                    	quantity += '</table>'				
                    				+ '<div><input style="margin-left:445px;"  type="button" id="save" value="Save" onclick="functionStockLocation()" /></div>'; 
        				$("#quantityContent").html(quantity);
        				$("#save").jqxButton();
               <#-- ,
                    initContent: function () {
                    	
        				$("#save").jqxButton();
        				
                    }
                });-->
    }
    
    
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




<div>
        <div id="myTable">
				<#assign dataField="[{ name: 'productId', type: 'string'},
				   { name: 'locationSeqId', type: 'string'},
				   { name: 'quantity', type: 'string'},
				   { name: 'quantityTranfers', type: 'string'},
				   { name: 'uomId', type: 'string'},
				   { name: 'stockLocation', type: 'string'},	 
				   ]"/>

				<#assign columnlist="{ text: '${uiLabelMap.ProductProductId}', datafield: 'productId', filtertype: 'checkedlist'},
				   { text: '${uiLabelMap.FacilitylocationSeqIdCurrent}', datafield: 'locationSeqId'},
				   { text: '${uiLabelMap.QuantityCurrent}', datafield: 'quantity'},
				   { text: '${uiLabelMap.QuantityTransferSum}', datafield: 'quantityTranfers'},
				   { text: '${uiLabelMap.QuantityUomId}', datafield: 'uomId'},
				   { text: '${uiLabelMap.StockLocationInFacility}', datafield: 'stockLocation', editable:false,
				   		cellsrenderer: function (row, column, value) {
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					var productId = data.productId;
        					var quantity = data.quantity;
        					var locationSeqIdCurrent = data.locationSeqId;
        					return '<a style = \"margin-left: 10px\" onclick=\"stockLocation(&#39;' + productId + '&#39;, &#39;' + quantity + '&#39;, &#39;' + locationSeqIdCurrent + '&#39;)\" href=\"javascript:void(0)\" class=\"copyLink\">' +  'StockLocation' + '</a>'
    					}
				   }
				   "/>
			
				<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist  editable="true"  showtoolbar="true" 
			 		url="jqxGeneralServicer?sname=JQXgetInventoryItemByLocation&facilityId=${parameters.facilityId}&locationSeqId=${parameters.locationSeqId?if_exists}" 
			 		editmode="click" selectionmode="checkbox" />
		</div>
		
		
		
		<div style="text-align:right; margin-top:30px;">
			<input type="button" value="${uiLabelMap.StockLocationInFacility}" id='jqxStockLocation' onclick="myFunction()"/>
		</div>	

        <div id="window" style="width: 100%; height: 650px; margin-top: 50px;">
			<div style="overflow: hidden;" id="titleQuantity">
				<span>${uiLabelMap.StockLocationInFacility}</span>
            </div>
            <div style="overflow: hidden;" id="quantityContent">
            </div>
        </div>

		<div id="copyPopupWindow">
		    <div>${uiLabelMap.StockProductIdForLocationInFacility} ${parameters.locationSeqId?if_exists}</div>
		    <div style="overflow: hidden;">
		        	<div style='float: left; width: 400px;'>
		            	<table style = "margin: auto;">
		            		<tr>
					 			<td align="left"><input id="facilityId" type="hidden" value=${parameters.facilityId}></input></td>
				    	 	</tr>
				    	 	<tr>
					 			<td align="left"><input id="locationSeqIdTranfer" type="hidden" value=${parameters.locationSeqId?if_exists}></input></td>
				    	 	</tr>
							<tr>
            					<td align="right">${uiLabelMap.ProductProductId}:</td>
	 							<td align="left">
	 							<span id='productId' style='margin-left: 10px; float: left;'></span>
	 							</td>
    	 					</tr>
		            		<tr>
		            			<td align="right">${uiLabelMap.QuantityCurrent}:</td>
			 					<td align="left">
			 						<span id='quantityCurrent' style='margin-left: 10px; float: left;'></span>
			 					</td>
		    	 			</tr>
		    	 			<tr>
		    	 				<td align="right">${uiLabelMap.FacilitylocationSeqIdCurrent}:</td>
	 							<td align="left">
			 						<span id='locationSeqIdCurrent' style='margin-left: 10px; float: left;'></span>
			 					</td>
    	 					</tr>
		    	 			<tr>
		    	 				<td align="right">${uiLabelMap.QuantityTransferSum}:</td>
			 					<td align="left">
			 						<input id='quantityTransfer' style='margin-left: 10px; float: left;'>
									</input>
			 					</td>
		    	 			</tr>
		    	 			<tr>
		    	 				<td align="right">${uiLabelMap.QuantityUomId}: </td>
	 							<td align="left"><div id="uomId"></div></td>
		    	 			</tr>
							<tr>
	    	 				<td align="right">&nbsp</td>
		 					<td align="left">
		 						<div>
	 								<input style="margin-right: 5px;" type="button" id="alterCopy" value="${uiLabelMap.StockLocation}" />
	               					<input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />               	
	               	 			</div>
		 					</td>
    	 			</tr>
		    	 		</table>	
		        	</div>
		    </div>
		</div>
</div>
<script>
	$(document).ready (function(){
		$('#jqxStockLocation').click(function () {
                    $('#window').jqxWindow('open');
                });
	});
	
</script>
<script>
	//Create theme
 	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$('#window').jqxWindow({autoOpen: false, showCollapseButton: true, maxHeight: 400, maxWidth: 700, minHeight: 200, minWidth: 200, height: 300, width: 500});
	
	//Create quantityTransfer
	$("#quantityTransfer").jqxInput();
	
	//Create uomId
	$("#uomId").jqxDropDownList({ selectedIndex: 0,  source: adapter, displayMember: "description", valueMember: "uomId"});
	
	//Create Copy popup
	$("#copyPopupWindow").jqxWindow({
       width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
    
    function stockLocation(productId, quantity, locationSeqIdCurrent, uomIdCurrent){
    	//productId
    	//$('#productId').val(productId);
    	$('#productId').text(productId);
    	$('#quantityCurrent').text(quantity);
    	$('#locationSeqIdCurrent').text(locationSeqIdCurrent);
    	$('#uomIdCurrent').val(uomIdCurrent);
    	$('#copyPopupWindow').jqxWindow('open');
    }
	
    $("#alterCancel").jqxButton();
    $("#alterCopy").jqxButton();

	$("#alterCopy").click(function () {
    
    	var productId = $('#productId').text();
    	var facilityId = $('#facilityId').val();
    	var locationSeqIdTranfer = $('#locationSeqIdTranfer').val();
    	var quantityCurrent = $('#quantityCurrent').text();
    	var quantityTransfer = $('#quantityTransfer').val();
    	var locationSeqIdCurrent = $('#locationSeqIdCurrent').text();
    	var uomId = $('#uomId').val();
    	var request = $.ajax({
			  url: "stockLocation",
			  type: "POST",
			  data: {productId : productId, quantityCurrent: quantityCurrent, quantityTransfer: quantityTransfer, uomId: uomId, facilityId: facilityId, locationSeqIdTranfer: locationSeqIdTranfer, locationSeqIdCurrent: locationSeqIdCurrent},
			  dataType: "html"
			});
			
			request.done(function(data) {
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
	

	function functionStockLocation() {
	
		var selectedRow = $('#jqxgrid').jqxGrid('selectedrowindexes');
		var productId = new Array();
        var quantityCurrent = new Array();
        var quantityTranfers = new Array();
        var facilityId = '${parameters.facilityId}';
		var locationSeqIdTranfer = '${parameters.locationSeqId?if_exists}';
		var locationSeqIdCurrent = new Array();
        var uomId = new Array();
        for (var i = 0; i < selectedRow.length; i++){
        	var data = $('#jqxgrid').jqxGrid('getrowdata', selectedRow[i]);
        	productId[i]= data.productId;
            quantityCurrent[i]= data.quantity;
            quantityTranfers[i] = data.quantityTranfers;
            uomId[i] = data.uomId;
            locationSeqIdCurrent[i] = data.locationSeqId;
       	}	  
       	
       	var request = $.ajax({
			  url: "stockLocationMany",
			  type: "POST",
			  data: {productId : productId, quantityCurrent: quantityCurrent, quantityTranfers: quantityTranfers, uomId: uomId, facilityId: facilityId, locationSeqIdTranfer: locationSeqIdTranfer, locationSeqIdCurrent: locationSeqIdCurrent},
			  dataType: "html"
			});
			
			request.done(function(data) {
			  	if(data.responseMessage == "error"){
	            	$('#jqxNotification').jqxNotification({ template: 'error'});
	            	$("#jqxNotification").text(data.errorMessage);
	            	$("#jqxNotification").jqxNotification("open");
	            }else{
	            	$('#container').empty();
	            	$('#jqxNotification').jqxNotification({ template: 'info'});
	            	$("#jqxNotification").text("Thuc thi thanh cong!");
	            	$("#jqxNotification").jqxNotification("open");
	            }
			});
			
			request.fail(function(jqXHR, textStatus) {
			  alert( "Request failed: " + textStatus );
			});
        $("#window").jqxWindow('close');
               	
    }
	

</script>