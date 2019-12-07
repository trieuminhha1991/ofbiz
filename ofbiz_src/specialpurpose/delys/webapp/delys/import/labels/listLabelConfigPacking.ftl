<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<script>
<#assign quantityUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_LABEL_ITEM"), null, null, null, false)>
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
	
</script>

<#assign dataField="[{ name: 'productId', type: 'string'},
					{ name: 'uomFromId', type: 'string'},
					{ name: 'uomToId', type: 'string'},
					{ name: 'quantityConvert', type: 'number', other: 'BigDecimal'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
					{ name: 'description', type: 'string'}
					]"/>

<#assign columnlist="
			{ text: '${uiLabelMap.uomFromId}', datafield: 'uomFromId', align: 'center', width: 120, editable: false, 
				cellsrenderer: function(row, colum, value){
					mapQuantityUom[value]==undefined?mapQuantityUom[value]='Pallet':mapQuantityUom[value]=mapQuantityUom[value]
					return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
				}
			},
			{ text: '${uiLabelMap.uomToId}', datafield: 'uomToId', align: 'center', width: 120, editable: false, 
				cellsrenderer: function(row, colum, value){
					return '<span title=' + value + '>' + mapQuantityUom[value] + '</span>';
				}
	        },
			{ text: '${uiLabelMap.QuantityConvert}', datafield: 'quantityConvert', align: 'center', width: 150, editable: true, columntype: 'numberinput', cellsalign: 'right', editable: false,
	        	validation: function (cell, value) {
            		   if (value < 0) {
            			   return { result: false, message: '${StringUtil.wrapString(uiLabelMap.NotAllowNegative)}' };
            		   }
            		   return true;
            	   }
			},
			{ text: '${uiLabelMap.description}',  datafield: 'description', align: 'center', minwidth: 250, editable: false },
	        { text: '${uiLabelMap.AvailableFromDate}',  datafield: 'fromDate', align: 'center', width: 200, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
				validation: function (cell, value) {
					var thisRow = cell.row;
					var thruDate = $('#jqxgrid').jqxGrid('getCell', thisRow, 'thruDate').value;
					if (thruDate == null) {
     		    	   return true;
					}
					thruDate = thruDate.getTime();
					value = value.getTime();
					if (thruDate < value) {
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
            },
			"/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false"
			showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true"
			url="jqxGeneralServicer?sname=JQGetListProductConfigPacking&productId=${productId}" updateUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=U"
			createUrl="jqxGeneralServicer?sname=UpdateProductConfigPacking&jqaction=C"
			customcontrol1="icon-tasks open-sans@${uiLabelMap.LogLableItemList}@getListLabelItemQA"
			editColumns="quantityConvert;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);description;productId;uomFromId;uomToId"
			addColumns="quantityConvert;thruDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);description;productId;uomFromId;uomToId"
			/>
		        
<div id = "myEditor"></div>
<div id="hoanmCustom">
<div id="alterpopupWindow"  style="display:none;">
<div>${uiLabelMap.AddNewProductPacking}</div>
<div class="form-window-content" style="overflow-y: hidden;">
	<div class='row-fluid'>
		<div class='span6'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.uomFromId}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="uomFromId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.QuantityConvert}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="quantityConvert1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableFromDate}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="fromDate1"></div></div>
			</div>
		</div>
		
		<div class='span5'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.uomToId}<span style="color:red;"> *</span></div>
				<div class='span7'><div id="uomToId1"></div></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'></div>
				<div class='span7'></div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 align-right'>${uiLabelMap.AvailableThruDate}&nbsp;&nbsp;</div>
				<div class='span7'><div id="thruDate1"></div></div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12" style="margin-top: 8px;">
	 		<div class="span3" style="padding-left: 125px;">${uiLabelMap.description}&nbsp;&nbsp;&nbsp;</div>
 			<div class="span9 no-left-margin" style="margin-left: -21px!important;"><textarea id="tarDescription"></textarea></div>
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
		    
<div id="jqxwindowEditor" style="display:none;">
<div>${uiLabelMap.EditorDescripton}</div>
<div style="overflow-x: hidden;">
	<div class="row-fluid">
		<div class="span12">
			<textarea id="tarDescriptionEditor"></textarea>
		</div>
	</div>
	<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;">
	<div class="row-fluid">
		<div class="span12 margin-top10">
				<button id='cancelEdit' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
				<button id='saveEdit' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
</div>
		    
<script>
    	var newFromDate = 0;
    	$("#uomFromId1").jqxDropDownList({ source: quantityUomData, width: '220px', displayMember: "description", valueMember: "quantityUomId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true,
    		renderer: function (index, label, value) 
    		{
    		    var uomId = quantityUomData[index];
    		    return uomId.description + "  [" + uomId.quantityUomId + " ]";
    		}
    	});
    	$("#uomToId1").jqxDropDownList({ source: [], width: '220px', displayMember: "description", valueMember: "quantityUomId", theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', autoDropDownHeight: true , disabled: true });
    	
    	$('#uomFromId1').on('change', function (event){     
			    var args = event.args;
			    if (args) {
    			    var index = args.index;
    			    var item = args.item;
    			    var label = item.label;
    			    var value = item.value;
    			    var dataAvalible = mapRalationPacking[value];
    			    var soucreToId = [];
    			    for ( var x in quantityUomData) {
						if (_.indexOf(dataAvalible, quantityUomData[x].quantityUomId) == -1) {
							soucreToId.push(quantityUomData[x]);
						}
					}
    			    $("#uomToId1").jqxDropDownList({ source: soucreToId, disabled: false,
    			    	renderer: function (index, label, value) 
    		    		{
    		    		    var uomId = soucreToId[index];
    		    		    return uomId.description + "  [" + uomId.quantityUomId + " ]";
    		    		}
    			    });
    			}
		});
    	
    	$("#quantityConvert1").jqxNumberInput({ inputMode: 'simple', spinButtons: true, theme: "olbius", width: '220px', decimalDigits: 0, min: 0  });
    	$("#fromDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
    	$("#thruDate1").jqxDateTimeInput({theme: "olbius", width: '220px' });
    	
    	$('#thruDate1 ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
    	$('#fromDate1').on('valueChanged', function (event){  
    		var jsDate = event.args.date; 
    		$('#thruDate1 ').jqxDateTimeInput('setMinDate', jsDate);
    	});
    	
    	$("#alterpopupWindow").jqxWindow({
            width: 900, maxWidth: 1000, theme: "olbius", minHeight: 450, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
        });
    	$('#alterpopupWindow').on('open', function () {
    		$('#thruDate1').val(null);
    		$("#uomFromId1").jqxDropDownList('clearSelection');
    		$("#uomToId1").jqxDropDownList('clearSelection');
    		$("#quantityConvert1").jqxNumberInput('val', 0);
    		$('#tarDescription').jqxEditor({
    	        theme: 'olbiuseditor',
    	        width: '98%'
    	    });
    		$("#tarDescription").jqxEditor('val', "");
    	});
    	$('#alterpopupWindow').on('close', function () {
    		$('#alterpopupWindow').jqxValidator('hide');
    		$("#uomToId1").jqxDropDownList({disabled: true});
    	});
    	
        $("#alterSave").click(function () {
            if ($('#alterpopupWindow').jqxValidator('validate')) {
            	var row = {};
            	row.uomFromId = $("#uomFromId1").val();
        		row.uomToId = $("#uomToId1").val();
    			row.quantityConvert = $("#quantityConvert1").val();
				row.thruDate = $("#thruDate1").val().toMilliseconds();
				row.fromDate = $("#fromDate1").val().toMilliseconds();
				row.description = $("#tarDescription").val();
				row.productId = "${productId}";
            	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	            $("#jqxgrid").jqxGrid('clearSelection');
	            $("#jqxgrid").jqxGrid('selectRow', 0);
	            $("#alterpopupWindow").jqxWindow('close');
	            getListProductConfigPackingAjax();
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
	                { input: '#uomToId1', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
	                	rule: function (input, commit) {
	                		var value = $("#uomToId1").val();
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
        $("#jqxgrid").on("cellDoubleClick", function (event){
        		    var args = event.args;
        		    var rowBoundIndex = args.rowindex;
        		    var rowVisibleIndex = args.visibleindex;
        		    var rightClick = args.rightclick; 
        		    var ev = args.originalEvent;
        		    var columnIndex = args.columnindex;
        		    var dataField = args.datafield;
        		    var value = args.value;
        		    if (dataField == "description") {
        		    	openPoupEditDescription(rowBoundIndex, value);
					}
		});
        var rowIndexEditing;
        function openPoupEditDescription(rowBoundIndex, value) {
        	rowIndexEditing = rowBoundIndex;
        	$("#jqxwindowEditor").jqxWindow('open');
        	$('#tarDescriptionEditor').jqxEditor({
    	        theme: 'olbiuseditor'
    	    });
        	$("#tarDescriptionEditor").jqxEditor('val', value);
		}
        
        $("#jqxwindowEditor").jqxWindow({ theme: 'olbius',
    	    width: 550, maxWidth: 1845, minHeight: 330, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelEdit"), modalOpacity: 0.7
    	});
    	$("#saveEdit").click(function () {
    		var newValue = $('#tarDescriptionEditor').val();
    		$("#jqxgrid").jqxGrid('setCellValue', rowIndexEditing, "description", newValue);
    		$("#jqxwindowEditor").jqxWindow('close');
    	});
        $(document).ready(function() {
        	$('#thruDate1').val(null);
        	getListProductConfigPackingAjax();
//		        	var mytab = "<li><span class='divider'><i class='icon-angle-right'></i></span>${uiLabelMap.ListProductConfigPacking}</li>";
//		        	$(".breadcrumb").append(mytab);
        });
        var productId = "${productId}";
        var mapRalationPacking = {};
        function getListProductConfigPackingAjax() {
        	$.ajax({
      		  url: "getListProductConfigPackingAjax",
      		  type: "POST",
      		  data: {productId: productId},
      		  success: function(data) {
      			mapRalationPacking = data["mapRalationPacking"];
      		  }
      	  	}).done(function() {
      	  		
      	  	});
		}
</script>