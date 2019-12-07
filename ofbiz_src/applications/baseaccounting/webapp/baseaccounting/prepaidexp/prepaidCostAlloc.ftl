<#--IMPORT LIB-->
<@jqGridMinimumLib/>
<#--/IMPORT LIB-->
<div id="containercostAllocGrid" style="background-color: transparent; overflow: auto; width: 100%;"></div>
<div id="jqxNotificationcostAllocGrid">
    <div id="notificationContentcostAllocGrid">
    </div>
</div>
<div id="costAllocGrid"></div>
<style>
	.highlight {
		background-color: #ffff4d !important;
	}
</style>
<script>
	function nextChar(c) {
	    return String.fromCharCode(c.charCodeAt(0) + 1);
	}

	var cellclassname = function (row, column, value, data) {
		var data = $('#costAllocGrid').jqxGrid('getrowdata',row);
		if (data['prepaidExpId'] == undefined) {
	        return 'highlight';
	    }
	}

	var data = new Array();
	//Send Ajax Request
	$.ajax({
		url: 'getPECostAlloc',
		type: "POST",
		dataType: 'json',
		async: false,
		success : function(res) {
			if(!res._ERROR_MESSAGE_ && !res._ERROR_MESSAGE_LIST_){
				var listCostAlloc = res['listCostAlloc'];
				var seqId = 1;
				var seqIdParty = 'A';
				var partyId = "";
				var index = 0;
				for(var i = 0; i < listCostAlloc.length; i++){
					if(partyId != listCostAlloc[i]['partyId']){
						var row = {};
						partyId = listCostAlloc[i]['partyId'];
						row['prepaidExpName'] = partyId;
						var amount = 0;
						var monthlyAllocAmount = 0;
						var preAccumulatedAllocAmount = 0;
						var accumulatedAllocAmount = 0;
						var allowAmount = 0;
						var remainingValue = 0;
						for(var j = 0; j < listCostAlloc.length; j++){
							if(listCostAlloc[j]['partyId'] == partyId){
								amount += listCostAlloc[j]['amount'];
								monthlyAllocAmount += listCostAlloc[j]['monthlyAllocAmount'];
								preAccumulatedAllocAmount += listCostAlloc[j]['preAccumulatedAllocAmount'];
								accumulatedAllocAmount += listCostAlloc[j]['accumulatedAllocAmount'];
								allowAmount += listCostAlloc[j]['allowAmount'];
								remainingValue += listCostAlloc[j]['remainingValue'];
							}
						}
						row['amount'] = amount;
						row['monthlyAllocAmount'] = monthlyAllocAmount;
						row['preAccumulatedAllocAmount'] = preAccumulatedAllocAmount;
						row['accumulatedAllocAmount'] = accumulatedAllocAmount;
						row['allowAmount'] = allowAmount;
						row['remainingValue'] = remainingValue;
						row['seqId'] = seqIdParty;
						data[index] = row;
						index++;
						seqIdParty = nextChar(seqIdParty);
						data[index] = listCostAlloc[i];
						data[index]['seqId'] = seqId;
						data[index]['allocDate'] = listCostAlloc[i]['allocDate'].time;
						index++;
						seqId++;
					}else{
						data[index] = listCostAlloc[i];
						data[index]['seqId'] = seqId;
						data[index]['allocDate'] = listCostAlloc[i]['allocDate'].time;
						seqId++;
						index++;
					}
				}
			}
		}
	});
	
	//Data fields 
	var datafields = [{ name: 'seqId', type: 'string'},
	                  { name: 'prepaidExpId', type: 'string'},
	                  { name: 'prepaidExpName', type: 'string'},
	                  { name: 'allocDate', type: 'date'},
	                  { name: 'amount', type: 'number'},
	                  { name: 'monthNumber', type: 'number'},
	                  { name: 'monthlyAllocAmount', type: 'number'},
	                  { name: 'preAccumulatedAllocAmount', type: 'number'},
	                  { name: 'accumulatedAllocAmount', type: 'number'},
	                  { name: 'allowAmount', type: 'number'},
	                  { name: 'remainingValue', type: 'number'},
	                  { name: 'note', type: 'string'},
	              ];
	//Column of grid
	var columnlist = [{ text: '${uiLabelMap.BACCSeqId}', datafield: 'seqId', width: 100,
						  cellclassname: cellclassname
					  },
	                  { text: '${uiLabelMap.BACCPrepaidExpId}', dataField: 'prepaidExpId', width: 150,
						  cellclassname: cellclassname
	                  },
	                  { text: '${uiLabelMap.BACCPrepaidExpName}', datafield: 'prepaidExpName', filtertype : 'number', width: 150,
	                	  cellclassname: cellclassname  
	                  },
	                  { text: '${uiLabelMap.BACCAllocDate}', datafield: 'allocDate', cellsformat:'dd/MM/yyyy', width: 150,
	                	  cellclassname: cellclassname  
	                  },
	                  { text: '${uiLabelMap.BACCAmount}', datafield: 'amount', filtertype : 'number', width: 150,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#costAllocGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  },
	                	  cellclassname: cellclassname
	                  },
	                  { text: '${uiLabelMap.BACCMonthNumber}', datafield: 'monthNumber', filtertype : 'number', width: 150,
	                	  cellclassname: cellclassname  
	                  },
	                  { text: '${uiLabelMap.BACCMonthlyAllocAmount}', datafield: 'monthlyAllocAmount', filtertype : 'number', width: 150,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#costAllocGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  },
	                	  cellclassname: cellclassname
	                  },
	                  { text: '${uiLabelMap.BACCPreAccumulatedAllocAmount}', datafield: 'preAccumulatedAllocAmount', filtertype : 'number', width: 150,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#costAllocGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  },
	                	  cellclassname: cellclassname
	                  },
	                  { text: '${uiLabelMap.BACCAccumulatedAllocAmount}', datafield: 'accumulatedAllocAmount', filtertype : 'number', width: 150,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#costAllocGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  },
	                	  cellclassname: cellclassname
	                  },
	                  { text: '${uiLabelMap.BACCAllocAmount}', datafield: 'allowAmount', filtertype : 'number', width: 150,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#costAllocGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  },
	                	  cellclassname: cellclassname
	                  },
	                  { text: '${uiLabelMap.BACCRemainingValue}', datafield: 'remainingValue', filtertype : 'number', width: 150,
	                	  cellsrenderer: function(row, columns, value){
	                		  var data = $('#costAllocGrid').jqxGrid('getrowdata',row);
	                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
	                	  },
	                	  cellclassname: cellclassname
	                  },
	                  { text: '${uiLabelMap.BACCNote}', datafield: 'note', filtertype : 'number', width: 150,
	                	  cellclassname: cellclassname  
	                  },
				];
	
	var source = {
	    localdata: data,
	    datatype: "array",
	    datafields: datafields
	};
	
	var dataAdapter = new $.jqx.dataAdapter(source, {
	    loadComplete: function (data) { },
	    loadError: function (xhr, status, error) { }    
	});
	
	 //Tool bar of grid
	var rendertoolbar = function (toolbar){
		var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
		container.append('<h4>${uiLabelMap.BACCCostAllocation}</h4>');
        toolbar.append(container);
   	}
	
	$("#costAllocGrid").jqxGrid({
		source: dataAdapter,
	    columns: columnlist,
	    showtoolbar: true,
	    width: '100%',
	    theme: 'olbius',
	    pageable: true,
	    autoheight: true,
	    rendertoolbar: rendertoolbar
	});
</script>