<#assign dataField="[{ name: 'quotaId', type: 'string'},
						   { name: 'quotaItemSeqId', type: 'string'},
						   { name: 'productId', type: 'string'},
						   { name: 'productName', type: 'string'},
						   { name: 'quotaQuantity', type: 'string'},
						   { name: 'availableQuantity', type: 'string'},
						   { name: 'quantityUomId', type: 'string'},
						   { name: 'fromDate', type: 'date', other: 'Timestamp'},
						   { name: 'thruDate', type: 'date', other: 'Timestamp'},
						   ]"/>
						   
						   
						   <#assign columnlist="
							{ text: '${uiLabelMap.QuotaItemSeqId}', datafield: 'quotaItemSeqId', width: 150, editable: true},
							{ text: '${uiLabelMap.ProductId}', datafield: 'productId', minwidth: 200, editable: true},
							{ text: '${uiLabelMap.ProductName}', datafield: 'productName', width: 200, editable: true},
							{ text: '${uiLabelMap.QuotaQuantity}', datafield: 'quotaQuantity', width: 200, editable: true},
							{ text: '${uiLabelMap.AvailableQuantity}', datafield: 'availableQuantity', width: 200, editable: true},
							{ text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', width: 120, editable: true, cellsrenderer:
		     			       function(row, colum, value){
		    			        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		    			        var quantityUomId = data.quantityUomId;
		    			        var quantityUom = getUom(quantityUomId);
		    			        return '<span>' + quantityUom + '</span>';
		    		        }},
			    		    { text: '${uiLabelMap.AvailableFromDate}', datafield: 'fromDate', width: 170, editable: true, cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
			    		    { text: '${uiLabelMap.AvailableThruDate}', datafield: 'thruDate', width: 170, editable: true, cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
							"/>
							
		<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false"
									showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
									url="jqxGeneralServicer?sname=JQGetListQuotaItems&quotaId=${quotaId}"
								/>
		    		        
		    <script>
		    			
		    			var listUomW = new Array();
		    			<#if listUomW?exists>
		    			<#list listUomW as item>
		    				var row = {};
		    				row['abbreviation'] = '${item.abbreviation?if_exists}';
		    				row['uomId'] = '${item.uomId?if_exists}';
		    				listUomW[${item_index}] = row;
		    			</#list>
		    			</#if>
		    			function getUom(uomId) {
		    				if (uomId != null) {
		    					for ( var x in listUomW) {
		        					if (uomId == listUomW[x].uomId) {
		        						return listUomW[x].abbreviation;
		        					}
		        				}
							} else {
								return "";
							}
		    			}
		    </script>
