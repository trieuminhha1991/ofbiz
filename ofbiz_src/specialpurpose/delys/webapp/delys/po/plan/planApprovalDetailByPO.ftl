<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>

<script>
	var customTimePeriodId = '${parameters.customTimePeriodId}';
	var productPlanId = '${parameters.productPlanId}'; 
	var internalPartyId = '${parameters.internalPartyId}';
	var statusId = '${parameters.statusId}';
	
	<#assign listPeriodType = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("groupPeriodTypeId", "PERIOD_TYPE_PO"), null, null, null, false)>
	var periodTypeData = 
	[
		<#list listPeriodType as periodType>
		{
			periodTypeId: "${periodType.periodTypeId}",  
			description: "${StringUtil.wrapString(periodType.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	
	function getDescriptionByPeriodTypeId(periodTypeId) {
		for ( var x in periodTypeData) {
			if (periodTypeId == periodTypeData[x].periodTypeId) {
				return periodTypeData[x].description;
			}
		}
	}
	
	function getDateFormatInJava(dateData){
		var dateFomat = new Date(dateData);
		var dateSource = Date.parse(dateFomat);
		return dateSource.toString('dd/MM/yyyy');
	}
	
	<#assign productList = delegator.findList("Product", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productTypeId", "FINISHED_GOOD"), null, null, null, false) />
	var productData = 
	[
		<#list productList as product>
		{
			'productId': "${product.productId}",
			'description':  "${StringUtil.wrapString(product.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	var mapProductData = {
		<#list productList as product>
			"${product.productId}": "${StringUtil.wrapString(product.get('description', locale)?if_exists)}",
		</#list>
	};
	
	function getDescriptionByProductId(productId) {
		for ( var x in mapProductData) {
			if (productId == mapProductData[x].productId) {
				return mapProductData[x].description;
			}
		}
	}
</script>
<style>
.cell-green-color {
    color: black !important;
    background-color: #33CCFF !important;
}
.cell-gray-color {
	color: black !important;
	background-color: #87CEEB !important;
}
</style>
<div id="contentNotificationUpdateProductPlanItemSuccess">
</div>
<div id="jqxgrid">
</div>

<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.AddNewPackingUom}</div>
	<div class='form-window-container'>
		<div class='row-fluid'>
			<div class="form-action">
		        <div class='span12' class="margin-bottom10">
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.Product)}</label>
					</div>  
					<div class="span7">
						<div id="productId" style="width: 100%" class="green-label">
							<div id="jqxgridProductId">
				            </div>
						</div>
			   		</div>
				</div>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="alterCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
		    </div>
		</div> 
	</div>
</div>

<div id="jqxNotificationUpdateProductPlanItemSuccess" >
	<div id="notificationUpdateProductPlanItemSuccess">
	</div>
</div>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id="viewDetailPlanByProductPlanId"><i class="ace-icon fa fa-trash-o"></i>&nbsp;&nbsp;${uiLabelMap.DSDeleteLocationFacilityType}</li>
	</ul>
</div>

<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$(document).ready(function () {
		listProductByProductId();
		loadDataProductPlanItem();
		
	});
	$("#jqxNotificationUpdateProductPlanItemSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationUpdateProductPlanItemSuccess", opacity: 0.9, autoClose: true, template: "success" });
	function loadDataProductPlanItem(){
		$.ajax({
			url: "loadProductPlanItemByCustomTimePeriodId",
			type: "POST",
			data: {customTimePeriodId: customTimePeriodId, productPlanId: productPlanId},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			var listProductPlanItem = data["listProductPlanItem"];
			var listCustomTimePeriodMonth = data["listCustomTimePeriodMonth"];
			bingDataToJqxGird(listProductPlanItem, listCustomTimePeriodMonth);
		});
	}
	var productPlanItemSeqIdData = [];
	function bingDataToJqxGird(listProductPlanItem, listCustomTimePeriodMonth){
		var data = [];
		var index = 0;
		if(listProductPlanItem.length != 0){
			var productIdData = [];
			for(var y in listProductPlanItem){
				var productId = listProductPlanItem[y].productId;
				productIdData.push(productId);
			}
			
			var resultsData = [];
			    
		    for (var i = 0; i < productIdData.length; i++)
		    {
		        if ((jQuery.inArray(productIdData[i], resultsData)) == -1)
		        {
		        	resultsData.push(productIdData[i]);
		        }
		    }
		    <#assign customTimePeriods = delegator.findList("CustomTimePeriod", null, null, null, null, true)/>
    		var customTimeData = [
    		  		    			<#list customTimePeriods as item>
    		  		    				{customTimePeriodId: '${item.customTimePeriodId}', periodTypeId: '${item.periodTypeId}'},
    			    				</#list>
    		                      ]
		    for(var i = 0; i < resultsData.length; i++){
		    	var row = {};
		    	row["productId"] = resultsData[i];
		    	var sumProduct = 0;
	    		for(var x = 0; x < listProductPlanItem.length; x++){
		    		var customTimePeriodId = listProductPlanItem[x].customTimePeriodId;
		    		var periodTypeId = '';
		    		for(var j = 0; j < customTimeData.length; j++){
		    			if(customTimeData[j].customTimePeriodId == customTimePeriodId){
		    				periodTypeId = customTimeData[j].periodTypeId;
		    				break;
		    			}
		    		}
		    		productPlanItemSeqIdData.push(listProductPlanItem[x].productPlanItemSeqId)
					if(periodTypeId == "PO_FIRST_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
						row["firstMonth"] = listProductPlanItem[x].planQuantity;
						sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_SECOND_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["secondMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_THIRD_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["thirdMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_FOURTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["fourthMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_FIFTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["fifthMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_SIXTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["sixthMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_SEVENTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["seventhMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_EIGHTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["eighthMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_NINTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["ninthMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_TENTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["tenthMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_ELEVENTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		row["eleventhMonth"] = listProductPlanItem[x].planQuantity;
		        		sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        	if(periodTypeId == "PO_TWELFTH_MONTH" && resultsData[i] == listProductPlanItem[x].productId){
		        		 row["twelfthMonth"] = listProductPlanItem[x].planQuantity;
		        		 sumProduct += listProductPlanItem[x].planQuantity;
		        	}
		        }
	    		row["partyId"] = internalPartyId;
	    		row["productSumNumber"] = sumProduct;
	    		data[index++] = row;
			}
		}
		
		var source =
	    {
	        localdata: data,
	        datatype: "array",
	        datafields:
	        [
	            { name: 'productId', type: 'string'},
	            { name: 'firstMonth', type: 'number' },
	            { name: 'secondMonth', type: 'number' },
	            { name: 'thirdMonth', type: 'number' },
	            { name: 'fourthMonth', type: 'number' },
	            { name: 'fifthMonth', type: 'number' },
	            { name: 'sixthMonth', type: 'number' },
	            { name: 'seventhMonth', type: 'number' },
	            { name: 'eighthMonth', type: 'number' },
	            { name: 'ninthMonth', type: 'number' },
	            { name: 'tenthMonth', type: 'number' },
	            { name: 'eleventhMonth', type: 'number' },
	            { name: 'twelfthMonth', type: 'number' },
	            { name: 'partyId', type: 'string' },
	            { name: 'productSumNumber', type: 'number' },
	        ],
			updaterow: function (rowid, newdata, commit) {
	        	$("#jqxgrid").jqxGrid('updatebounddata');
	        }
	    };
	    var dataAdapter = new $.jqx.dataAdapter(source);
	
	    $("#jqxgrid").jqxGrid(
	    {
	        source: dataAdapter,
	        columnsresize: true,
	        columnsresize: true,
            theme: this.theme,
            autoheight: true,
            pageable: true,
            width: '100%',
            pagesize: 100,
            editable: true,
	        theme: 'olbius', 
	        showfilterrow: true,
            filterable: true,
	        columns: [
	          { text: 'Sản phẩm', datafield: 'productId', width: 150, editable: false, pinned: true, filtertype: 'input',
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
	          },
	          { text: 'Tháng 1', datafield: 'firstMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
	        	  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].firstMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  }
	          },
	          { text: 'Tháng 2', datafield: 'secondMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].secondMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  }
	          },
	          { text: 'Tháng 3', datafield: 'thirdMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].thirdMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 4', datafield: 'fourthMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].fourthMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 5', datafield: 'fifthMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].fifthMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 6', datafield: 'sixthMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  }, 
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].sixthMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 7', datafield: 'seventhMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].seventhMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 8', datafield: 'eighthMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].eighthMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 9', datafield: 'ninthMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].ninthMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 10', datafield: 'tenthMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].tenthMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 11', datafield: 'eleventhMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].eleventhMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Tháng 12', datafield: 'twelfthMonth', columntype: 'numberinput',
	        	  createeditor: function (row, cellvalue, editor) {
	        		  editor.jqxNumberInput({ decimalDigits: 0, digits: 10, min:0 });
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                      var rows = $('#jqxgrid').jqxGrid('getboundrows');
                      if(rowData['productId'] ==  internalPartyId){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  if(rowData['productId'] ==  internalPartyId){
                		  return false;
                	  }
                	  if(statusId == "PLAN_APPROVED"){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgrid').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgrid").jqxGrid('getrowdata', row);
                	  for(var i = 0; i < rows.length; i++){
                		  if(rows[i].productId  ==  internalPartyId){
                			  rows[i].twelfthMonth += newvalue - oldvalue;
                		  }
            		  }
                	  rowData.productSumNumber += newvalue - oldvalue
                	  source.localdata = rows;
                  },
	          },
	          { text: 'Phòng Ban', datafield: 'partyId', editable: false,
	        	  cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';
	        	  },
	        	  cellClassName: function (row, columnfield, value) {
	        		  return 'cell-green-color';
                  },
              },
              { text: 'Tổng số', datafield: 'productSumNumber', editable: false,
            	  cellClassName: function (row, columnfield, value) {
	        		  return 'cell-gray-color';
                  },
              },
	        ]
	    });
	}
	
	var contextMenu = $("#contextMenu").jqxMenu({ theme: 'olbius', width: 100, height: 30, autoOpenPopup: false, mode: 'popup'});
	$("#jqxgrid").on('contextmenu', function () {
	    return false;
	});
	
	$("#jqxgrid").on('rowclick', function (event) {
		if (event.args.rightclick) {
            $("#jqxgrid").jqxGrid('selectrow', event.args.rowindex);
            var scrollTop = $(window).scrollTop();
            var scrollLeft = $(window).scrollLeft();
            contextMenu.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
            return false;
        }
    });
	
	$("#contextMenu").on('itemclick', function (event) {
    	var args = event.args; 
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var dataRow = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
        var productId = dataRow.productId;
        bootbox.confirm("${uiLabelMap.LogNotificationBeforeDelete}", function(result) {
            if(result) {
            	deleteProductPlanItemByProductId(productId);
            }
		});    
    });
	
	function deleteProductPlanItemByProductId(productId){
		$.ajax({
			url: "deleteProductPlanItemByProductId",
			type: "POST",
			data: {productId: productId, productPlanId: productPlanId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			loadDataProductPlanItem();
			$("#notificationUpdateProductPlanItemSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiDeleteSucess)}');
			$("#jqxNotificationUpdateProductPlanItemSuccess").jqxNotification('open');
		});
	}
	
	$("#alterpopupWindow").jqxWindow({
	    width: 500, maxWidth: 1000, theme: "olbius", height: 150, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
	});
	
	$('#alterpopupWindow').jqxValidator({
        rules: [
	               { input: '#productId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var productId = $('#productId').val();
		            	    if(productId == '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}'){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               }
	           ]
    });
	
	function listProductByProductId(){
    	var listProduct;
    	$.ajax({
			url: "loadListProduct",
			type: "POST",
			data: {},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			listProduct = data["listProduct"];
			bindingDataToJqxGirdProductList(listProduct);
		});
    }
    
    function bindingDataToJqxGirdProductList(listProduct){
 	    var sourceP2 =
 	    {
 	        datafields:[{name: 'productId', type: 'string'},
 	            		{name: 'productName', type: 'string'},
         				],
 	        localdata: listProduct,
 	        datatype: "array",
 	    };
 	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2);
 	    $("#jqxgridProductId").jqxGrid({
 	        source: dataAdapterP2,
 	        filterable: true,
	        showfilterrow: true,
	        theme: theme,
	        autoheight:true,
	        pageable: true,
 	        columns: [{text: '${uiLabelMap.DAProductId}', datafield: 'productId'},
 	          			{text: '${uiLabelMap.DAProductName}', datafield: 'productName'},
 	        		]
 	    });
    }
    
    $("#productId").jqxDropDownButton({width: 210});
    $('#productId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}');
    var productIdBySelectGird;
    $("#jqxgridProductId").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridProductId").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
        $('#productId').jqxDropDownButton('setContent', dropDownContent);
        productIdBySelectGird = row['productId'];
    });
	
	function addProductPlanItem(){
		if(statusId == "PLAN_APPROVED"){
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.POCheckPlanApprovedCanNotAddNewProductToPlan)}");
		}else{
			$('#alterpopupWindow').jqxWindow('open');
		}
	}
	
	$('#alterpopupWindow').on('open', function (event) { 
		listProductByProductId();
	});
	
	$('#alterpopupWindow').on('close', function (event) { 
		$('#productId').jqxDropDownButton('setContent', '${StringUtil.wrapString(uiLabelMap.LogPleaseSelect)}');	
	});
	
	$("#alterSave").click(function (){
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			var productId = $('#productId').val();
			$.ajax({
				url: "addProductPlanItem",
				type: "POST",
				data: {productId: productId, productPlanId: productPlanId, customTimePeriodId: customTimePeriodId},
				dataType: "json",
				async: false,
				success: function(data) {
				}
			}).done(function(data) {
				loadDataProductPlanItem();
				$('#alterpopupWindow').jqxWindow('close');
			});
		}
	});
	
	var checkUpdateRowGird = 0;
	$("#jqxgrid").on('cellvaluechanged', function (event) 
	{
		checkUpdateRowGird = 1;
	});

	
	function updateProductPlanItem(){
		if(checkUpdateRowGird == 1){
			var rowsData = $('#jqxgrid').jqxGrid('getrows');
			var firstMonth = [];
			var secondMonth = [];
			var thirdMonth = [];
			var fourthMonth	 = [];
			var fifthMonth = [];
			var sixthMonth = [];
			var seventhMonth = [];
			var eighthMonth = [];
			var ninthMonth = [];
			var tenthMonth = [];
			var eleventhMonth = [];
			var twelfthMonth = [];
			var productId = [];
			for(var i in rowsData){
				productId.push(rowsData[i].productId);
				firstMonth.push(rowsData[i].firstMonth);
				secondMonth.push(rowsData[i].secondMonth);
				thirdMonth.push(rowsData[i].thirdMonth);
				fourthMonth.push(rowsData[i].fourthMonth);
				fifthMonth.push(rowsData[i].fifthMonth);
				sixthMonth.push(rowsData[i].sixthMonth);
				seventhMonth.push(rowsData[i].seventhMonth);
				eighthMonth.push(rowsData[i].eighthMonth);
				ninthMonth.push(rowsData[i].ninthMonth);
				tenthMonth.push(rowsData[i].tenthMonth);
				eleventhMonth.push(rowsData[i].eleventhMonth);
				twelfthMonth.push(rowsData[i].twelfthMonth);
			}
			bootbox.confirm("${uiLabelMap.LogUpdateBootboxConfirmSure}", function(result) {
	            if(result) {
	            	updateDataToProductPlanItemByProductId(productId, firstMonth, secondMonth, thirdMonth, fourthMonth, fifthMonth, sixthMonth, seventhMonth, eighthMonth, ninthMonth, tenthMonth, eleventhMonth, twelfthMonth);
	            }
			});    
		}else{
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.POCheckEnterNumberUpdateData)}");
		}
	}
	
	function updateDataToProductPlanItemByProductId(productId, firstMonth, secondMonth, thirdMonth, fourthMonth, fifthMonth, sixthMonth, seventhMonth, eighthMonth, ninthMonth, tenthMonth, eleventhMonth, twelfthMonth){
		$.ajax({
			url: "updateDataToProductPlanItemByProductId",
			type: "POST",
			data: {productPlanId: productPlanId, customTimePeriodId: customTimePeriodId, productId: productId, firstMonth: firstMonth, secondMonth: secondMonth, thirdMonth: thirdMonth, fourthMonth: fourthMonth, fifthMonth: fifthMonth, sixthMonth: sixthMonth, seventhMonth: seventhMonth, eighthMonth: eighthMonth, ninthMonth: ninthMonth, tenthMonth: tenthMonth, eleventhMonth: eleventhMonth, twelfthMonth: twelfthMonth},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			$("#notificationUpdateProductPlanItemSuccess").text('${StringUtil.wrapString(uiLabelMap.DSNotifiUpdateSucess)}');
			$("#jqxNotificationUpdateProductPlanItemSuccess").jqxNotification('open');
			checkUpdateRowGird = 0;
			loadDataProductPlanItem();
		});
	}
	
	function approvalPlanByPartyGroup(){
		if(statusId == "PLAN_APPROVED"){
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.POPlanApproved)}");
		}else{
			bootbox.confirm("${uiLabelMap.PODefinitelyPlanApproved}", function(result) {
	            if(result) {
	            	approvalPlan();
	            }
			});    
		}
	}
	
	function approvalPlan(){
		$.ajax({
			url: "approvalPlanByPartyGroup",
			type: "POST",
			data: {productPlanId: productPlanId},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		}).done(function(data) {
			window.location.href = "getListPlanProposalByPO";
		});
	}
</script>