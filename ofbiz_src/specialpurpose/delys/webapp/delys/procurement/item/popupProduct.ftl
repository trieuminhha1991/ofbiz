<@useLocalizationNumberFunction />
<div id="jqxwindowproduct" class='hide'>
	<div>${uiLabelMap.addProductToRequirement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.productName}</label>
				</div>  
				<div class="span7">
					<div id="productchosen">
						<div id="jqxgridProduct" class='hide'></div>
					</div>
		   		</div>		
			</div>
			<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.Quantity}</label>
				</div>  
				<div class="span7">
					<div id="quantity"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.QuantityUom}</label>
				</div>  
				<div class="span7">
					<div id="quantityUom"></div>
		   		</div>
		   	</div>
			<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.OrderAmount}</label>
				</div>  
				<div class="span7">
					<div id="unitCostContainer"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.Unit}</label>
				</div>  
				<div class="span7">
					<div id="currencyUom"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label>${uiLabelMap.Description}</label>
				</div>  
				<div class="span7">
					<textarea id="description" rows="4" style="margin-top: 0;width: 190px;resize: none"></textarea>
		   		</div>
		   	</div>
		</div>
	   	<div class="form-action">
			<button id="cancelProduct" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="saveProductAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="saveProduct" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>
<script>
	var baseCurrencyUomId = "${baseUomId?if_exists}";
	var currentGrid = null;
	var popupproduct = $("#jqxwindowproduct");
	initPopupProduct();
	$("#cancelProduct").click(function(){
		popupproduct.jqxWindow('close');
		resetFormProduct();
	});
	$("#saveProduct").click(function(){
		addNewProduct();
	    popupproduct.jqxWindow('close');
	    currentGrid = null;
	});
	$("#saveProductAndContinue").click(function(){
		if(!popupproduct.jqxValidator("validate")){
			return;
		}
		if(!currentGrid){
	    	currentGrid = productChosenGrid;
	    }
	    addNewProduct();
	    resetFormProduct();
	});
	function addNewProduct(currentGrid){
		if(!popupproduct.jqxValidator("validate")){
			return;
		}
		if(!currentGrid){
	    	currentGrid = productChosenGrid;
	    }
		var index = $("#jqxgridProduct").jqxGrid("getselectedrowindex");
		var selected = $("#jqxgridProduct").jqxGrid("getrowdata", index);
		var row = { 
	    	productId: selected.productId,
	    	productName: selected.productName,
	   		quantity:$("#quantity").jqxNumberInput("val"),
	   		quantityUomId : $("#quantityUom").jqxDropDownList("val"),
	   		unitCost: $("#unitCostContainer").jqxNumberInput("val"),
	   		currencyUomId : $("#currencyUom").jqxDropDownList("val"),
	   		reason: $("#description").val()
	   	  };
	    renderGridProduct(currentGrid, row);
	}
	function renderGridProduct(currentGrid, row){
    	currentGrid.jqxGrid('addRow', null, row, "first");
	    // select the first row and clear the selection.
	    currentGrid.jqxGrid('clearSelection');                        
	    currentGrid.jqxGrid('selectRow', 0);  
    }
	function resetFormProduct(){
		$("#quantity").jqxNumberInput("val", 0);
		$("#unitCostContainer").jqxNumberInput("val", 0);
		$("#quantityUom").jqxDropDownList('clearSelection', true);
		for(var x in dataLC){
			if(dataLC[x].uomId == "VND"){
				$('#currencyUom').jqxDropDownList("selectIndex", parseInt(x));
			}
		}
		$("#productchosen").jqxDropDownButton("setContent", "");
		$("#jqxgridProduct").jqxGrid("selectRow", -1);
		$("#description").val("");
		popupproduct.jqxValidator("hide");
	}
	function initGridProduct(){
    	var sourceProduct =
	    {
	        datafields:[{name: 'productId', type: 'string'},
	            		{name: 'productName', type: 'string'},
	            		{name: 'productTypeId', type: 'string'},
        			],
	        cache: false,
	        root: 'results',
	        datatype: "json",
	        updaterow: function (rowid, rowdata) {
	            // synchronize with the server - send update command   
	        },
	        beforeprocessing: function (data) {
	        	sourceProduct.totalrecords = data.TotalRows;
	        },
	        filter: function () {
	            // update the grid and send a request to the server.
	            $("#jqxgridProduct").jqxGrid('updatebounddata');
	        },
	        pager: function (pagenum, pagesize, oldpagenum) {
	            // callback called when a page or page size is changed.
	        },
	        sort: function () {
	            $("#jqxgridProduct").jqxGrid('updatebounddata');
	        },
	        sortcolumn: 'productId',
			sortdirection: 'asc',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N'
		    },
		    pagesize:5,
	        contentType: 'application/x-www-form-urlencoded',
	        url: 'jqxGeneralServicer?sname=JQListProductForProcurementProposal&productCateogryId=${procurementCategory?if_exists}',
	    };
	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct,
	    {
	    	autoBind: true,
	    	formatData: function (data) {
		    	if (data.filterscount) {
	                var filterListFields = "";
	                for (var i = 0; i < data.filterscount; i++) {
	                    var filterValue = data["filtervalue" + i];
	                    var filterCondition = data["filtercondition" + i];
	                    var filterDataField = data["filterdatafield" + i];
	                    var filterOperator = data["filteroperator" + i];
	                    filterListFields += "|OLBIUS|" + filterDataField;
	                    filterListFields += "|SUIBLO|" + filterValue;
	                    filterListFields += "|SUIBLO|" + filterCondition;
	                    filterListFields += "|SUIBLO|" + filterOperator;
	                }
	                data.filterListFields = filterListFields;
	            }
	            return data;
	        },
	        loadError: function (xhr, status, error) {
	            alert(error);
	        },
	        downloadComplete: function (data, status, xhr) {
	                if (!sourceProduct.totalRecords) {
	                    sourceProduct.totalRecords = parseInt(data["odata.count"]);
	                }
	        }, 
	        beforeLoadComplete: function (records) {
	        	for (var i = 0; i < records.length; i++) {
	        		if(typeof(records[i])=="object"){
	        			for(var key in records[i]) {
	        				var value = records[i][key];
	        				if(value != null && typeof(value) == "object" && typeof(value) != null){
	        					//var date = new Date(records[i][key]["time"]);
	        					//records[i][key] = date;
	        				}
	        			}
	        		}
	        	}
	        }
	    });
	    /* grid dropdownbutton */
	    $("#jqxgridProduct").jqxGrid({
	    	width:450,
	        source: dataAdapterProduct,
	        filterable: true,
	        showfilterrow: true,
	        virtualmode: true, 
	        autorowheight: true,
	        sortable:true,
	        theme: theme,
	        editable: false,
	        autoheight:true,
	        pageable: true,
	        rendergridrows: function(obj){
				return obj.data;
			},
	        columns: [{text: '${uiLabelMap.ProductId}', datafield: 'productId', width:'100px'},
	          			{text: '${uiLabelMap.ProductName}', datafield: 'productName', width:'200px'},
	          			{text: '${uiLabelMap.ProductTypeId}', datafield: 'productTypeId', width:'180px'}
	        		]
	    });
	    
	    $("#jqxgridProduct").on('rowselect', function (event) {
	        var args = event.args;
	        if(args.rowindex != -1){
	        	var row = $("#jqxgridProduct").jqxGrid('getrowdata', args.rowindex);
		        var dr = $('#productchosen'); 
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+row['productName'] +'</div>';
		        dr.jqxDropDownButton('setContent', dropDownContent);
		        dr.jqxDropDownButton("close");	
	        }
	    });
    }
    function initPopupProduct(){
    	$("#productchosen").jqxDropDownButton({ theme: theme, width:  200, height: 25});
    	initGridProduct();
    	$("#productchosen").on("open", function(){
    		$("#jqxgridProduct").show();
    	});
    	renderQuantityUom($('#quantityUom'));
    	renderCurrencyUom($("#currencyUom"));
    	renderQuantityInput($("#quantity"));
    	renderUnitCostInput($("#unitCostContainer"));
    	initProductChosen();
    	popupproduct.jqxWindow({
            width: 380,height:390, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
        });
    	
    	initProductRule(popupproduct);
    	
    }
    function renderQuantityUom(obj){
    	var sourceLC =
    	{
    	    localdata: dataLU,
    	    datatype: "array"
    	};
    	var dataAdapterLC = new $.jqx.dataAdapter(sourceLC);
    	obj.jqxDropDownList({theme:theme, source: dataAdapterLC,  width:  200, displayMember: "description", valueMember: "uomId", filterable: true});
		var source =
    	{
    	    localdata: dataLC,
    	    datatype: "array"
    	};
    }
    function renderCurrencyUom(obj){
    	var source =
    	{
    	    localdata: dataLC,
    	    datatype: "array"
    	};
    	var dataAdapter = new $.jqx.dataAdapter(source);
    	obj.jqxDropDownList({theme:theme, source: dataAdapter,  width: 200, displayMember: "description", valueMember: "uomId", filterable: true});
    	for(var x in dataLC){
			if(dataLC[x].uomId == "VND"){
				$('#currencyUom').jqxDropDownList("selectIndex", parseInt(x));
			}
		}
    }
    function renderQuantityInput(obj){
    	obj.jqxNumberInput({ width:  200, height: 24, decimalDigits: 0, spinButtons: false});
    }
    function renderUnitCostInput(obj){
    	obj.jqxNumberInput({ width: 200, height: 24, max : 999999999999, digits: 12, decimalDigits:2, spinButtons: false});
    }
    function initProductRule(popup){
       	popup.jqxValidator({
       	   	rules: [{
                   input: "#quantity", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                   action: 'blur', 
                   rule: function (input, commit) {
                       var val = input.jqxNumberInput('val');
                       if(!val){
                    	   return false;
                       }
                       return true;
                   }
               },{
                   input: "#quantityUom", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                   action: 'blur', 
                   rule: function (input, commit) {
                       var val = input.jqxDropDownList('getSelectedIndex');
                       return val != -1;
                   }
               },{
                   input: "#unitCostContainer", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                   action: 'blur', 
                   rule: function (input, commit) {
                       var val = input.jqxNumberInput('val');
                       if(!val){
                    	   return false;
                       }
                       return true;
                   }
               },{
                   input: "#currencyUom", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                   action: 'blur', 
                   rule: function (input, commit) {
                       var val = input.jqxDropDownList('getSelectedIndex');
                       return val != -1;
                   }
               },{
                   input: "#productchosen", 
                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
                   action: 'blur', 
                   rule: function (input, commit) {
                       var index = $("#jqxgridProduct").jqxGrid("getselectedrowindex");
                       return index != -1;
                   }
               }]
       	 });
    }
    /*init product chosen for requirement Item*/
    function initProductChosen(){
    	var data = [];
        var source =
        {
            localdata: data,
            datatype: "local",
            pager: function (pagenum, pagesize, oldpagenum) {
                // callback called when a page or page size is changed.
            },
            datafields:
            [
				{ name: 'productId', type: 'string' },
                { name: 'productname', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'quantityUomId', type: 'string' },
                { name: 'unitCost', type: 'number' },
                { name: 'currencyUomId', type: 'string' },
                { name: 'reason', type: 'string' },
            ],
            addrow: function (rowid, rowdata, position, commit) {
                commit(true);
                calculateBudget();
            },
            deleterow: function (rowid, commit) {
                commit(true);
                calculateBudget();
            },
            updaterow: function (rowid, newdata, commit) {
                commit(true);
                calculateBudget();
            }
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        // initialize jqxGrid
        var grid = $("#jqxgridProductChosen");
        grid.jqxGrid(
        {
            width: 550,
            source: dataAdapter,
            enabletooltips: true,
            showtoolbar: true,
            selectionmode: 'multiplerowsextended',
            editable: true,
            sortable: true,
            pageable: true,
            autoheight: true,
            autorowheight: true,
            columnsresize: true,
            rendertoolbar: function (toolbar) {
                var me = this;
                var container = $("<div style='margin: 5px; float: right'></div>");
                toolbar.append(container);
                
                container.append('<button id="addrowproduct" class=\'grid-action-button\'><i class=\'fa fa-plus-circle\'></i>${uiLabelMap.DAAddNewRow}</button>');
                <#if planTypeId?exists && planTypeId == "ORGANIZATION_PLAN">
                	container.append('<button id="processProduct" class=\'grid-action-button\'><i class=\'fa fa-calculator\'></i>${uiLabelMap.ProcessTotalProduct}</button>');	
                	$("#processProduct").on('click', function () {
                		if(typeof(processTotalProduct) != "undefined"){
                			processTotalProduct();	
                		}
	                });
                </#if>
                container.append('<button id="deleterowproduct" class=\'grid-action-button\'><i class=\'fa fa-trash\'></i>${uiLabelMap.DADeleteSelectedRow}</button>');
                // create new row.
                $("#addrowproduct").on('click', function () {
                    $("#jqxwindowproduct").jqxWindow("open");
                    /* var commit = grid.jqxGrid('addrow', null, datarow); */
                });
                // delete row.
                $("#deleterowproduct").on('click', function () {
                    var selectedrowindex = grid.jqxGrid('getselectedrowindex');
                    var rowscount = grid.jqxGrid('getdatainformation').rowscount;
                    if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
                        var id = grid.jqxGrid('getrowid', selectedrowindex);
                        var commit = grid.jqxGrid('deleterow', id);
                    }
                });
            },
            columns: [
              { text: '${uiLabelMap.productName}', datafield: 'productName', width: 200},
              { text: '${uiLabelMap.Quantity}', datafield: 'quantity', width: 100, cellsalign: 'right', columntype: 'numberinput',
              	  cellsrenderer: function(row, colum, value){
			 		  var str = convertLocalNumber(value);
			 		  return '<div class=\"custom-cell-grid align-right\">' + str + '</div>';
              	  },
              	  createeditor: function (row, cellvalue, editor) {
                      renderQuantityInput(editor);
                 },
                 validation: function (cell, value) {
                      if (value < 0) {
                          return { result: false, message: "Price should be in the 0-15 interval" };
                      }
                      return true;
                 },	
              },
              { text: '${uiLabelMap.QuantityUom}', datafield: 'quantityUomId', width: 150, columntype: 'dropdownlist',
            	  cellsrenderer: function(row, colum, value){
			 		var uom = "";
			 		for(var x in dataLU){
			 			if(dataLU[x].uomId == value){
			 				uom = dataLU[x].description;
			 			}
			 		}
			 		if(!uom){uom = value;}
			 		return "<span>" + uom + "</span>";
              	 },
              	 createeditor: function (row, cellvalue, editor) {
                      renderQuantityUom(editor);
                 }
              },
              { text: '${uiLabelMap.OrderAmount}', datafield: 'unitCost', width: 100, cellsalign: 'right',  columntype: 'numberinput',
              	 cellsrenderer: function(row, colum, value){
			 		  var str = convertLocalNumber(value);
			 		  return '<div class=\"custom-cell-grid align-right\">' + str + '</div>';
              	  },
              	 createeditor: function (row, cellvalue, editor) {
                      renderUnitCostInput(editor);
                 },
                 validation: function (cell, value) {
                      if (value < 0) {
                          return { result: false, message: "Price should be in the 0-15 interval" };
                      }
                      return true;
                  },
              },
              { text: '${uiLabelMap.Unit}', datafield: 'currencyUomId', width: 200, columntype: 'dropdownlist',
            	  cellsrenderer: function(row, colum, value){
			 		var uom = "";
			 		for(var x in dataLC){
			 			if(dataLC[x].uomId == value){
			 				uom = dataLC[x].description;
			 			}
			 		}
			 		if(!uom){uom = value;}
			 		return "<span>" + uom + "</span>";
              	  },
              	  createeditor: function (row, cellvalue, editor) {
              	      renderCurrencyUom(editor);
                  }
              },
              { text: '${uiLabelMap.Reason}', datafield: 'reason', width: 300 },
            ]
        });   
    }
    function calculateBudget(){
    	var data = $("#jqxgridProductChosen").jqxGrid("getboundrows");
    	var obj;
    	var total = 0;
    	var arr = [];
    	for(var x in data){
    		obj = data[x];
    		if(obj.unitCost && obj.quantity){
    			arr.push({
    				unitCost : obj.unitCost,
    				quantity: obj.quantity,
    				currencyUomId: obj.currencyUomId
    			});
    		}
    	}
    	convertCurrencyUom({
    		baseCurrencyUomId: baseCurrencyUomId,
    		data: JSON.stringify(arr)
    	}, function(total){
    		$("#estimatedBudget").jqxNumberInput("val", total);	
    	})
    }
    function convertCurrencyUom(data, action){
    	$.ajax({
    		url: "calculateTotalByUom",
    		type: "POST",
    		data : data,
    		success: function(res){
    			if(res && res.convertedValue){
    				action(res.convertedValue);
    			}
    		}
    	});
    }
</script>