$(function() {  
	ObjProduct.init();
});

var ObjProduct = (function() {
	var gridProduct = $("#jqxStockAndPlan");
	var store = {};
	var init = function() { 
		initEvent();
	};
	
	var listWeekHeader = [];
	
	var loadDataForGrid = function (){
		if (customTimePeriod && partySelected && currencyUom  
			&& (store.customTimePeriodId != customTimePeriod.customTimePeriodId || store.supplierPartyId != partySelected.partyId || store.currencyUomId != currencyUom.uomId)){
			$.ajax({
				url: "getDataPlanByPeriod",
				type: "POST",
				data: {
					customTimePeriodId: customTimePeriod.customTimePeriodId,
					supplierPartyId: partySelected.partyId,
					currencyUomId: currencyUom.uomId,
				},
				dataType: "json",
				success: function(data) {
			        if (data.listProducts && data.listProducts.length > 0) {
			        	salesForecastId = data.salesForecastId;
			        	listProductSelected = [];
			        	var listIterator = data.listProducts;
						listWeekHeader = data.listPeriods;
						listPeriods = data.listPeriods;
						periodTypeId = data.periodTypeId;
						loadProductDataSumToJqx(listIterator, listWeekHeader, periodTypeId);
						const listProductIds = [...new Set(listIterator.map(item => item.productId))];
						const paramKeys = [...new Set(listIterator.map(item => item.paramKey))];
						
						listPeriods.sort(function(a, b) {
						    return a.customTimePeriodId - b.customTimePeriodId;
						});
						for (var i in listProductIds) {
							var productId = listProductIds[i];
							var obj = {
									"productId": productId,
							}
							var productCode = null;
							var productName = null;
							var uomId = null;
							var listData = [];
							for (var k in listPeriods){
								var period = listPeriods[k];
								var data = {
									"periodId": period.customTimePeriodId,	
									"periodName": period.periodName,	
								};
								for (var j in paramKeys) {
									var key = paramKeys[j];
									for (var x in listIterator) {
										var pr = listIterator[x];
										if (pr.paramKey == key && pr.productId == productId) {
											data[key] = pr[period.customTimePeriodId];
											productCode = pr.productCode;
											productName = pr.productName;
											uomId = pr.uomId;
										}
									}
								}
								listData.push(data);
							}
							listData.sort(function(a, b) {
							    return a.periodId - b.periodId;
							});
							obj["data"] = listData;
							obj["productCode"] = productCode;
							obj["productName"] = productName;
							obj["uomId"] = uomId;
							listProductSelected.push(obj);
						}
						listProductSelected.sort(function(a, b) {
							if(a.productCode < b.productCode) return -1;
						    if(a.productCode > b.productCode) return 1;
						    return 0;
						});
			        } else {
			        	listProductSelected = [];
			        	gridProduct.jqxGrid('clear')
			        }
				}
			});
			store.customTimePeriodId = customTimePeriod.customTimePeriodId;
			store.supplierPartyId = partySelected.partyId;
			store.currencyUomId = currencyUom.uomId;
		}
	}

	function loadProductDataSumToJqx(listIterator, listWeekHeader, periodTypeId){
		var obj = initDataFields(listWeekHeader, periodTypeId);
		var sourceProduct =
	    {
	        datafields: obj.listHeader,
	        localdata: listIterator,
	        datatype: "array",
	    };
	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
	    gridProduct.jqxGrid({
	        source: dataAdapterProduct,
	        localization: getLocalization(),
	        filterable: true,
	        showfilterrow: true,
	        theme: 'olbius',
	        rowsheight: 30,
	        width: '100%',
	        autoheight: true,
	        columnsresize: true,
	        showaggregates: true,
	        showstatusbar: true,
	        statusbarheight: 70,
	        editmode: 'click',
	        enabletooltips: true,
	        autoheight: true,
	        selectionmode: 'singlecell',
	        pageable: true,
	        pagesize: 12,
	        pagesizeoptions: ['12', '24', '36'],
	        editable: true,
		    columns: obj.listColumns
	    });
	}

	function initDataFields(listWeekHeader, periodTypeId){
		var arrHeaders = [];
		var arrColumns = [];
		var h1 = {name: 'productId', type: 'string'};
		var h5 = {name: 'productCode', type: 'string'};
		var h2 = {name: 'productName', type: 'string'};
		var h3 = {name: 'convert', type: 'number'};
		var h7 = {name: 'pack', type: 'number'};
		var h6 = {name: 'uomId', type: 'string'};
		var h4 = {name: 'SalesCycle', type: 'string'};
		var cellclassname = function (row, column, value, data) {
		    if((row+1) % 4 == 0){
		    	return 'yellow1';
		    }
		};
		var c5 = {text: uiLabelMap.ProductId, datafield: 'productCode', editable: false,filterable: true, pinned: true, width: 110,
				cellclassname: cellclassname
		};
		var c2 = {text: uiLabelMap.ProductName, datafield: 'productName', minwidth: 150, editable: false, filterable: true, pinned: true,
				cellclassname: cellclassname
		};
		var c3 = {text: uiLabelMap.Unit, datafield: 'uomId', editable: false, filterable: false, width: '7%', pinned: true, hidden: false,
				cellclassname: cellclassname,
				cellsrenderer: function(row, column, value){
					return '<span class="align-right">' + getUomDesc(value) +'</span>';
				},
		};
		var c4 = {text: uiLabelMap.QC, datafield: 'convert', editable: false, filterable: false, width: '4%', pinned: true, hidden: false,
				cellsrenderer: function(row, column, value){
					return '<span class="align-right">' + formatnumber(value) +'</span>';
				},
				cellclassname: cellclassname
		};
		var c6 = {text: uiLabelMap.BIESalesCycle, datafield: 'SalesCycle', editable: false, width: 150, filterable: true, pinned: true,
			cellclassname: cellclassname,
			cellsrenderer: function(row, column, value){
			},
			aggregates: [{[uiLabelMap.BIEPalletTotal]:
				function (aggregatedValue, currentValue, column, record) {
					return aggregatedValue;
	            }
			}],
			aggregatesrenderer: function (aggregates) {
		        var renderstring = "";
		        $.each(aggregates, function (key, value) {
		        	if(key == uiLabelMap.BIEPalletTotal){
		        		renderstring += "<span style='font-size: 14px;'><b>" + uiLabelMap.BIEPalletTotal+"</b></span><hr style='margin: 2px !important;'/>" +
		        				"<span style='font-size: 14px; color: green;'><b>" + uiLabelMap.BIENumContainer +"</b></span><hr style='margin: 2px !important;'/>" +
		        				"<span style='font-size: 14px; color: orange;'><b>" + uiLabelMap.BIERemain + " (Pallet)</b></span>";
		        	}
		        });
		        return renderstring;
		    },
		};
		arrHeaders.push(h1);
		arrHeaders.push(h5);
		arrHeaders.push(h2);
		arrHeaders.push(h6);
		arrHeaders.push(h3);
		arrHeaders.push(h7);
		arrHeaders.push(h4);
		arrColumns.push(c5);
		arrColumns.push(c2);
		arrColumns.push(c3);
		arrColumns.push(c4);
		arrColumns.push(c6);
		var cellbeginedit = function (row, datafield, columntype, value) {
	        if ((row+1)%4 != 3) return false;
	        else{
	        	var dateColumn = '';
					for(var i=0; i<listWeekHeader.length; i++){
						if(listWeekHeader[i].customTimePeriodId == datafield){
							dateColumn = listWeekHeader[i].periodName;
							break;
						}
					}
	        	var arr = dateColumn.split('-');
	        	var today = new Date();
	        	var date = new Date(today);
	        	date.setFullYear(arr[2]);
	        	date.setMonth(arr[1]-1);
	        	date.setDate(arr[0]);
	        	var yesterday = new Date(today);
	        	
	        	var d = today.getDate();
	        	var m = today.getMonth() +1;
	        	var y = today.getFullYear();
	        	var t = today.getDay();
	        	if(t==0) yesterday.setDate(today.getDate() - 6);
	        	else yesterday.setDate(today.getDate() - (t-1));
	        	if(date < yesterday){
	        		$("#"+row +''+datafield).jqxTooltip({ disabled: false });
	        		$("#"+row +''+datafield).jqxTooltip({ content: '<span style="color: red;">' +uiLabelMap.DmsRestrictEditWeek + '</span>', theme: 'customtooltip', position: 'top', name: 'movieTooltip'});
			        	$("#"+row +''+datafield).jqxTooltip('open');
			        	$("#"+row +''+datafield).bind('close', function () {
			        		$("#"+row +''+datafield).jqxTooltip({ disabled: true });
			        	}); 
	        		return false;
	        	}
	        }
	    }
		
		for(var i=0; i<listWeekHeader.length; i++){
			var df = listWeekHeader[i].customTimePeriodId;
			var header = {name: listWeekHeader[i].customTimePeriodId, type: 'number'};
			var column = {text: listWeekHeader[i].periodName, datafield: listWeekHeader[i].customTimePeriodId, editable: true, filterable: false, columntype: 'numberinput', width: 80, cellsalign: 'right',
					createeditor: function (row, cellvalue, editor) {
		                editor.jqxNumberInput({inputMode: 'simple', spinMode: 'simple', groupSeparator: '.', min:0 });
		            },
		            cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
	  			        if(newvalue <0) return false;
		            	if((row+1)%4 == 3){
			            	if (newvalue != oldvalue){
			            		if(!oldvalue){
			            			oldvalue=0;
			            		}
		  			        	var cell = gridProduct.jqxGrid('getcellvalue', row+1, datafield);
		  			        	var newCell = parseFloat(cell) + (parseFloat(newvalue) - parseFloat(oldvalue));
		  			        	gridProduct.jqxGrid('setcellvalue', row+1, datafield, newCell);
		  			        	
		  			        	var cellOpen = newCell;
		  			        	var m = i;
		  			        	for(var k=0; k<listWeekHeader.length; k++){
		  							if(listWeekHeader[k].customTimePeriodId == datafield){
		  								m = k;
		  								break;
		  							}
		  						}
		  			        	
		  			        	for(var j=k+1; j<listWeekHeader.length; j++){
		  			        		var cellOpenNextOld = gridProduct.jqxGrid('getcellvalue', row-2, listWeekHeader[j].customTimePeriodId);
		  			        		gridProduct.jqxGrid('setcellvalue', row-2, listWeekHeader[j].customTimePeriodId, cellOpen);
		  			        		var cellEnding = gridProduct.jqxGrid('getcellvalue', row+1, listWeekHeader[j].customTimePeriodId);
		  			        		var cellEndingNext = parseFloat(cellOpen) - parseFloat(cellOpenNextOld) + parseFloat(cellEnding);
		  			        		gridProduct.jqxGrid('setcellvalue', row+1, listWeekHeader[j].customTimePeriodId, cellEndingNext);
		  			        		
		  			        		cellOpen = cellEndingNext;
		  			        	}
		  			        	
		  			        }
	  			        }
	  			    },
		            cellbeginedit: cellbeginedit,
		            cellsrenderer: function(row, column, value, defaulthtml, columnproperties){
		            	if((row+1) %4 == 3){
		            		return '<span style="text-align: right; font-size: 14px;" class="background-prepare">' + formatnumber(value) +'</span>';
		            	}
		            	return '<span class="align-right" >' + formatnumber(value) +'</span>';
	  				},
	  				aggregates: [{[uiLabelMap.BIEPalletTotal]:
	  					function (aggregatedValue, currentValue, column, record) {
	  						if(record.SalesCycle == uiLabelMap.BIEOrderQuantity){
	  							var convert = record.convert;
	  							var pack = record.pack;
	  							var x = currentValue/pack;
	  							aggregatedValue += x;
	  						}
	  						return aggregatedValue;
	  		            }
	  				}
	  				],
	  				aggregatesrenderer: function (aggregates) {
	  			        var renderstring = "";
	  			        $.each(aggregates, function (key, value) {
	  			        	if(key == uiLabelMap.BIEPalletTotal){
	  			        		var container = Math.floor(parseFloat(value)/33);
	  			        		var remain = parseFloat(value)%33;
	  			        		renderstring += "<span style='margin-right: 10px; font-size: 14px;'><b>"+formatnumber(value)+"</b></span><hr style='margin: 2px !important;'/>" +
	  			        				"<span style='margin-right: 10px; font-size: 14px; color: green'><b>"+formatnumber(container)+"</b></span><hr style='margin: 2px !important;'/>" +
	  			        				"<span style='margin-right: 10px; font-size: 14px; color: orange'><b>"+formatnumber(remain)+"</b></span>";
	  			        	}
	  			        });
	  			        return renderstring;
	  			    },
	  			    cellclassname: function (row, column, value, data) {
					    if((row+1) %4 == 0){
					    	return 'green1';
					    }
		  			  if((row+1)%4==4){
					    	return 'bluewhite';
					    }
					}
			};
			arrHeaders.push(header);
			arrColumns.push(column);
		}
		var arrRe = {listHeader: arrHeaders, listColumns: arrColumns};
		return arrRe;
	}
	
	var initEvent = function (){
		gridProduct.on("cellendedit", function(event) {
			var args = event.args;
			var dataField = event.args.datafield;
			var rowBoundIndex = event.args.rowindex;
			var value = args.value;
			var oldvalue = args.oldvalue;
			var rowData = args.row;
			if (rowData && listPeriods != undefined && listPeriods.length > 0){
				if (dataField != undefined){
					if (value >= 0){
						var item = {};
						$.each(listProductSelected, function(i){
			   				var olb = listProductSelected[i];
			   				if (olb.productId == rowData.productId ){
			   					item = $.extend({}, olb);
			   					listProductSelected.splice(i,1);
			   					return false;
			   				}
			   			});
						var listData = item.data;
						var dataNew = {};
						$.each(listData, function(i){
							var data = listData[i];
							if (data.periodId == dataField){
								dataNew = $.extend({}, data);
								dataNew.OrderQuantity = value;
								dataNew.EndInventory = data.OpenInventory + value - data.SalesForcast;
								listData.splice(i,1);
			   					return false;
			   				}
			   			});
						listData.push(dataNew);
						listData.sort(function(a, b) {
						    return a.periodId - b.periodId;
						});
						item["data"] = listData;
						listProductSelected.push(item);
						
						listProductSelected = updateQuantityPeriod(listProductSelected);
						
						listProductSelected.sort(function(a, b) {
							if(a.productCode < b.productCode) return -1;
						    if(a.productCode > b.productCode) return 1;
						    return 0;
						});
					} 
				} 
			}
		});
	}
	
	var updateQuantityPeriod = function (listProducts){
		var listReturns = [];
		for (var i in listProducts){
			var tmp = listProducts[i];
			if (tmp != undefined){
				var listData = tmp.data;
				var listDataNew = [];
				if (listData != undefined){
					var size = listData.length;
					listDataNew.push(listData[0]);
					for (var k = 0; k < size - 1; k++){
						var x = listData[k];
						//listDataNew.push(x);
						var y = listData[k+1];
						y.OpenInventory = x.EndInventory;
						y.EndInventory = y.OpenInventory + y.OrderQuantity - y.SalesForcast;
						listDataNew.push(y);
					}
				}
				listDataNew.sort(function(a, b) {
				    return a.periodId - b.periodId;
				});
				tmp.data = listDataNew;
				listReturns.push(tmp);
			}
		}
		return listReturns;
	}
	
	return {
		init : init,
		loadDataForGrid: loadDataForGrid,
	}
}());