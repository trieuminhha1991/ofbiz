<#if security.hasEntityPermission("PRODUCT", "_ADMIN", session)>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
<#assign listProducts = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false)>
<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "SHIPMENT_PACKING"), null, null, null, false)>
<script>
	var listUoms = [
					<#if listUoms?exists>
						<#list listUoms as item>
						{
							uomId: "${item.uomId?if_exists}",
							description: "${item.description?if_exists}"
						},
						</#list>
					</#if>
	                ];
	var quantityUomData = [
						<#if quantityUoms?exists>
							<#list quantityUoms as item> 
							{
								quantityUomId: "${item.uomId?if_exists}",
								description: "${StringUtil.wrapString(item.description?if_exists)}"
							},
							</#list>
						</#if>
	                    ];
	
	var mapQuantityUom = {
						<#if quantityUoms?exists>
							<#list quantityUoms as item>
								"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
								</#list>
							</#if>
							};
	var listProducts = [
                       <#if listProducts?exists>
	                       <#list listProducts as item> 
	                       {
	                    	   productId: "${item.productId?if_exists}",
	                    	   internalName: "${StringUtil.wrapString(item.internalName?if_exists)}"
	                       },
	                       </#list>
                       </#if>
                       ];
	
	var mapProducts = {
			<#if listProducts?exists>
				<#list listProducts as item>
					"${item.productId?if_exists}": "${StringUtil.wrapString(item.internalName?if_exists)}",
				</#list>
			</#if>
	};
	
</script>

<#assign dataField="[{ name: 'productId', type: 'string'},
					{ name: 'uomFromId', type: 'string'},
					{ name: 'uomToId', type: 'string'},
					{ name: 'quantityConvert', type: 'number', other: 'BigDecimal'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
					]"/>

<#assign columnlist="
			{ text: '${uiLabelMap.ProductProduct}',  datafield: 'productId', align: 'center', minwidth: 250, editable: false },
			{ text: '${uiLabelMap.uomFromId}', datafield: 'uomFromId', align: 'center', width: 150, editable: false, 
				cellsrenderer: function(row, colum, value){
					mapQuantityUom[value]==undefined?mapQuantityUom[value]='Pallet':mapQuantityUom[value]=mapQuantityUom[value]
					return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
				}
			},
			{ text: '${uiLabelMap.uomToId}', datafield: 'uomToId', align: 'center', width: 150, editable: false, 
				cellsrenderer: function(row, colum, value){
					return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
				}
	        },
			{ text: '${uiLabelMap.QuantityConvert}', datafield: 'quantityConvert', align: 'center', width: 150, editable: true, columntype: 'numberinput', cellsalign: 'right',
	        	validation: function (cell, value) {
            		   if (value < 0) {
            			   return { result: false, message: '${StringUtil.wrapString(uiLabelMap.NotAllowNegative)}' };
            		   }
            		   return true;
            	   }
			},
	        { text: '${uiLabelMap.AvailableFromDate}',  datafield: 'fromDate', align: 'center', width: 200, editable: true, columntype: 'datetimeinput', editable: false, cellsformat: 'dd/MM/yyyy',
				validation: function (cell, value) {
					var thisRow = cell.row;
					var thruDate = $('#jqxgrid').jqxGrid('getCell', thisRow, 'thruDate').value;
					if (thruDate == null) {
     		    	   return true;
					}
					thruDate = thruDate.getTime();
					value = value.getTime();
					if (thruDate < value) {
                 	   $('#inputdatetimeeditorjqxgridthruDate').val('');
                    }
					newFromDate = value;
					return true;
         	    }
	        },
			{ text: '${uiLabelMap.AvailableThruDate}',  datafield: 'thruDate', align: 'center', width: 200, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', 
	        	validation: function (cell, value) {
	        		if (value == null){
                    	return true;
                    }
	        		value = value.getTime();
		        	var thisRow = cell.row;
		        	var fromDate = $('#jqxgrid').jqxGrid('getCell', thisRow, 'fromDate').value.getTime();
		        	newFromDate == 0 ? fromDate = fromDate : fromDate = newFromDate;
                    if (fromDate > value) {
                         return { result: false, message: '${StringUtil.wrapString(uiLabelMap.ThruDateNotValid)}' };
                    }
                    return true;
                 }
             }	
			"/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false"
			showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
			url="jqxGeneralServicer?sname=JQGetListUomTypeAndConfigPacking"
			updateUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=U"
			createUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=C"
			editColumns="quantityConvert;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);productId;uomFromId"
			addColumns="quantityConvert;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);productId;uomFromId"
			removeUrl="jqxGeneralServicer?sname=removeProductCapacitys&jqaction=D" deleteColumn="productId;uomFromId;uomToId"
			/>
		        
<div id="alterpopupWindow"  style="display:none;">
<div>${uiLabelMap.ConfigProductCapacity}</div>
<div class="form-window-content" style="overflow-y: hidden;">
	<div class='row-fluid'>
		<div class='span6'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.ProductProduct}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="productId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.ShipmentPackingUom}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="uomFromId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableFromDate}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="fromDate1"></div></div>
			</div>
		</div>
		
		<div class='span5'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'></div>
				<div class='span7'></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.QuantityConvert}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="quantityConvert1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableThruDate}&nbsp;&nbsp;&nbsp;</div>
				<div class='span7'><div id="thruDate1"></div></div>
			</div>
		</div>
	</div>
    <div class="form-action">
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
			</div>
		</div>
	</div>
</div>
</div>
		    
<script>
    	var newFromDate = 0;
    	$("#productId1").jqxDropDownList({ source: listProducts, width: '220px', displayMember: "internalName", valueMember: "productId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
    	$("#uomFromId1").jqxDropDownList({ source: listUoms, width: '220px', displayMember: "description", valueMember: "uomId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true });
    	
    	$("#quantityConvert1").jqxNumberInput({ inputMode: 'simple', spinButtons: true, theme: "olbius", width: '220px', decimalDigits: 0, min: 0  });
    	$("#fromDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
    	$("#thruDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
    	$("#alterpopupWindow").jqxWindow({
            width: 950, maxWidth: 1000, theme: "olbius", minHeight: 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
        });
    	$('#alterpopupWindow').on('open', function () {
    		$('#thruDate1').val(null);
    		$("#productId1").jqxDropDownList('clearSelection');
    		$("#uomFromId1").jqxDropDownList('clearSelection');
    		$("#quantityConvert1").jqxNumberInput('val', 0);
    	});
    	$('#alterpopupWindow').on('close', function () {
    		$('#alterpopupWindow').jqxValidator('hide');
    	});
    	$('#productId1').on('change', function (event){
    			var uomFromId = $('#uomFromId1').val();
    			if (!uomFromId) {
					return;
				}
			    var args = event.args;
			    if (args) {
				    var index = args.index;
				    var item = args.item;
				    var label = item.label;
				    var value = item.value;
				    getInfoConfigPackingAjax({productId: value, uomFromId: uomFromId });
    			} 
		});
    	$('#uomFromId1').on('change', function (event){
    		var productId = $('#productId1').val();
    		if (!productId) {
    			return;
    		}
    		var args = event.args;
    		if (args) {
    			var index = args.index;
    			var item = args.item;
    			var label = item.label;
    			var value = item.value;
    			getInfoConfigPackingAjax({productId: productId, uomFromId: value });
    		} 
    	});
    	function getInfoConfigPackingAjax(dataIn) {
    		var info;
    		$.ajax({
    	  		  url: "getInfoConfigPackingAjax",
    	  		  type: "POST",
    	  		  data: dataIn,
    	  		  success: function(data) {
    	  			info = data["Info"];
    	  		  }
    	  	  	}).done(function() {
    	  	  		if (info) {
    	  	  			info.fromDate?info.fromDate = info.fromDate["time"]:info.fromDate=null;
    	  	  			info.thruDate?info.thruDate = info.thruDate["time"]:info.thruDate=null;
    	  	  			$('#fromDate1').jqxDateTimeInput('val', info.fromDate);
    	  	  			$('#thruDate1').jqxDateTimeInput('val', info.thruDate);
    	  	  			$("#quantityConvert1").jqxNumberInput('val', info.quantityConvert);
					}
    	  	  	});
		}
        $("#alterSave").click(function () {
            if ($('#alterpopupWindow').jqxValidator('validate')) {
            	var row = {};
            	row.uomFromId = $("#uomFromId1").val();
    			row.quantityConvert = $("#quantityConvert1").val();
				row.thruDate = $("#thruDate1").val().toMilliseconds();
				row.fromDate = $("#fromDate1").val().toMilliseconds();
				row.productId = $("#productId1").val();
            	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	            $("#jqxgrid").jqxGrid('clearSelection');
	            $("#jqxgrid").jqxGrid('selectRow', 0);
	            $("#alterpopupWindow").jqxWindow('close');
	            setTimeout(function(){
	            	$("#jqxgrid").jqxGrid('updatebounddata');
	            }, 500);
            }
        });
        $('#alterpopupWindow').jqxValidator({
	        rules: [
	                { input: '#uomFromId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
	                	rule: function (input, commit) {
	                		var value = $("#uomFromId1").val();
	                		if (value) {
	                			return true;
	                		}
	                		return false;
	                	}
	                },
	                { input: '#productId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
	                	rule: function (input, commit) {
	                		var value = $("#productId1").val();
	                		if (value) {
	                			return true;
	                		}
	                		return false;
	                	}
	                },
	                { input: '#quantityConvert1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'valueChanged', 
	                	rule: function (input, commit) {
	                		var value = $("#quantityConvert1").val();
	                		if (value > 0) {
	                			return true;
							}
	                		return false;
	                	}
	                },
	                { input: '#fromDate1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
	                	rule: function (input, commit) {
	                		var value = $("#fromDate1").val().toMilliseconds();
	                		if (value > 0) {
	                			return true;
							}
	                		return false;
	                	}
	                },
	                { input: '#thruDate1', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
	                	rule: function (input, commit) {
	                		var thruDate = $("#thruDate1").val().toMilliseconds();
	                		if (!thruDate) {
	                			return true;
							}
	                		var fromDate = $("#fromDate1").val().toMilliseconds();
	                		if (fromDate <= thruDate) {
	                			return true;
							}
	                		return false;
	                	}
	                }
	               ]
	    });
</script>
	<#else>   
		<h2> You do not have permission</h2>
</#if>