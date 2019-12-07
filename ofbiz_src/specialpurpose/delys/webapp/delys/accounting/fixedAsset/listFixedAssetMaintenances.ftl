<script type="text/javascript">	
	<#assign productMaintTypeList = delegator.findList("ProductMaintType", null, null, null, null, false) />
	var dataProductMaintTypeListView = new Array();
	var row = {};
	row['productMaintTypeId'] = '';
	row['description'] = '';
	dataProductMaintTypeListView[0] = row;
	<#list productMaintTypeList as productMaintType >
		var row = {};
		row['productMaintTypeId'] = '${productMaintType.productMaintTypeId?if_exists}';
		row['description'] = '${productMaintType.get('description',locale)?if_exists}';
		dataProductMaintTypeListView[${productMaintType_index} + 1] = row;
	</#list>	

	<#assign statusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "FIXEDAST_MNT_STATUS"), null, null, null, false) />
	var dataStatusListView = new Array();
	<#list statusList as status >
		var row = {};
		row['statusId'] = '${status.statusId?if_exists}';
		row['description'] = '${status.get('description',locale)?if_exists}';
		dataStatusListView[${status_index}] = row;
	</#list>	
	
	<#assign productMeterTypeList = delegator.findList("ProductMeterType", null, null, null, null, false) />
	var dataProductMeterTypeListView = new Array();
	var row = {};
	row['productMeterTypeId'] = '';
	row['description'] = '';
	dataProductMeterTypeListView[0] = row;
	<#list productMeterTypeList as productMeterType >
		var row = {};
		row['productMeterTypeId'] = '${productMeterType.productMeterTypeId?if_exists}';
		row['description'] = '${productMeterType.get('description',locale)?if_exists}';
		dataProductMeterTypeListView[${productMeterType_index} + 1] = row;
	</#list>
	
	<#assign intervalUomList = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "TIME_FREQ_MEASURE"), null, null, null, false) />
	var dataIntervalUomListView = new Array();
	var row = {};
	row['uomId'] = '';
	row['description'] = '';
	dataIntervalUomListView[0] = row;	
	<#list intervalUomList as intervalUom >
		var row = {};
		row['uomId'] = '${intervalUom.uomId?if_exists}';
		row['description'] = '${intervalUom.get('description',locale)?if_exists}';
		dataIntervalUomListView[${intervalUom_index} + 1] = row;
	</#list>		

	<#assign orderList = delegator.findList("OrderHeader", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "PURCHASE_ORDER"), null, null, null, false) />
	var dataOrderListView = new Array();
	var row = {};
	row['orderId'] = '';
	row['orderName'] = '';
	dataOrderListView[0] = row;	
	<#list orderList as order >
		var row = {};
		row['orderId'] = '${order.orderId?if_exists}';
		row['orderName'] = '${order.orderName?if_exists}' + '[' + '${order.orderId?if_exists}' + ']';
		dataOrderListView[${order_index} + 1] = row;
	</#list>
    
 	var cellclass = function (row, columnfield, value) {
 		var now = new Date();
		now.setHours(0,0,0,0);
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
            return 'background-red';
        }
    }
</script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign params="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=listFixedAssetMaintenancesJqx">
<#assign dataField="[{ name: 'maintHistSeqId', type: 'string'},
					 { name: 'statusId', type: 'string'},
					 { name: 'productMaintTypeId', type: 'string'},
					 { name: 'intervalMeterTypeId', type: 'string'},
					 { name: 'intervalQuantity', type: 'number'},
					 { name: 'intervalUomId', type: 'string'},
					 { name: 'scheduleWorkEffortId', type: 'string'},
					 { name: 'purchaseOrderId', type: 'string' }					 
				   ]"/>
<#assign columnlist=" { text: '${uiLabelMap.FormFieldTitle_maintHistSeqId}', width: '10%', datafield: 'maintHistSeqId', editable: false},				    
					  { text: '${StringUtil.wrapString(uiLabelMap.Status)}', datafield: 'statusId', width: '10%', filterable: false, cellclassname: cellclass, columntype: 'dropdownlist',
							 	cellsrenderer: function (row, column, value) {
									var data = $('#jqxgrid').jqxGrid('getrowdata', row);
										for(i = 0 ; i < dataStatusListView.length; i++){
											if(data.statusId == dataStatusListView[i].statusId){
												return '<span title=' + value +'>' + dataStatusListView[i].description + '</span>';
											}
										}
										
										return '<span title=' + value +'>' + value + '</span>';
									},
		    						createeditor: function (row, column, editor) {
			                            editor.jqxDropDownList({source: dataStatusListView, displayMember:\"description\", valueMember: \"statusId\",
				                            renderer: function (index, label, value) {
							                    var datarecord = dataStatusListView[index];
							                    return datarecord.description;
							                  }
				                        });}									
						 },
						{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_productMaintTypeId)}', datafield: 'productMaintTypeId', width: '10%', filterable: false, cellclassname: cellclass, columntype: 'dropdownlist',
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataProductMaintTypeListView.length; i++){
	        							if(data.productMaintTypeId == dataProductMaintTypeListView[i].productMaintTypeId){
	        								return '<span title=' + value +'>' + dataProductMaintTypeListView[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    						createeditor: function (row, column, editor) {
		                            editor.jqxDropDownList({source: dataProductMaintTypeListView, displayMember:\"description\", valueMember: \"productMaintTypeId\",
			                            renderer: function (index, label, value) {
						                    var datarecord = dataProductMaintTypeListView[index];
						                    return datarecord.description;
						                  }
			                        });}	    						
					 	},
						 { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetMaintIntervalMeterType)}', datafield: 'intervalMeterTypeId', width: '15%', filterable: false, cellclassname: cellclass, columntype: 'dropdownlist',
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataProductMeterTypeListView.length; i++){
	        							if(data.intervalMeterTypeId == dataProductMeterTypeListView[i].productMaintTypeId){
	        								return '<span title=' + value +'>' + dataProductMeterTypeListView[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    						createeditor: function (row, column, editor) {
		                            editor.jqxDropDownList({source: dataProductMeterTypeListView, displayMember:\"description\", valueMember: \"productMeterTypeId\",
			                            renderer: function (index, label, value) {
						                    var datarecord = dataProductMeterTypeListView[index];
						                    return datarecord.description;
						                  }
			                        });}
					 	},					 			
					 	 {
		                      text: '${uiLabelMap.AccountingFixedAssetMaintIntervalQuantity}', datafield: 'intervalQuantity', width: '15%', align: 'right', cellsalign: 'right', columntype: 'numberinput',
		                      createeditor: function (row, cellvalue, editor) {
		                          editor.jqxNumberInput({ decimalDigits: 0 });
		                      }
		                  },					 	
	                    { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetMaintIntervalUom)}', datafield: 'intervalUomId', width: '15%', cellclassname: cellclass, columntype: 'dropdownlist',
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < dataIntervalUomListView.length; i++){
	        							if(data.intervalUomId == dataIntervalUomListView[i].uomId){
	        								return '<span title=' + value +'>' + dataIntervalUomListView[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},	    					   			
				   				createeditor: function (row, column, editor) {
	                            editor.jqxDropDownList({source: dataIntervalUomListView, displayMember:\"description\", valueMember: \"uomId\",
		                            renderer: function (index, label, value) {
					                    var datarecord = dataIntervalUomListView[index];
					                    return datarecord.description;
					                  }
		                        });}
				   			}, 	
				   			{ text: '${uiLabelMap.FormFieldTitle_scheduleWorkEffortId}', width: '15%', datafield: 'scheduleWorkEffortId', editable: false},	
				   			{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_purchaseOrderId)}', datafield: 'purchaseOrderId', width: '15%', cellclassname: cellclass, columntype: 'dropdownlist',
							 	cellsrenderer: function (row, column, value) {
									var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		        						for(i = 0 ; i < dataOrderListView.length; i++){
		        							if(data.purchaseOrderId == dataOrderListView[i].orderId){
		        								return '<span title=' + value +'>' + dataOrderListView[i].orderName + '</span>';
		        							}
		        						}
		        						
		        						return '<span title=' + value +'>' + value + '</span>';
		    						},	    					   			
					   				createeditor: function (row, column, editor) {					   				
					   					editor.jqxDropDownList({source: dataOrderListView, displayMember:\"orderName\", valueMember: \"orderId\",
			                            renderer: function (index, label, value) {
						                    var datarecord = dataOrderListView[index];
						                    return datarecord.orderName;
						                  }
			                        });}
					   			}				   			
					"/>
	
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" addrefresh="true"	 deleterow="true"	
		url=params addColumns="fixedAssetId[${parameters.fixedAssetId}];statusId;productMaintTypeId;intervalMeterTypeId;intervalQuantity;intervalUomId;purchaseOrderId"
		createUrl="jqxGeneralServicer?sname=createFixedAssetMaint&jqaction=C" 
		updateUrl="jqxGeneralServicer?sname=updateFixedAssetMaint&fixedAssetId=${parameters.fixedAssetId}&jqaction=U"
		editColumns="fixedAssetId[${parameters.fixedAssetId}];maintHistSeqId;statusId;productMaintTypeId;intervalMeterTypeId;intervalMeterTypeId;intervalUomId;purchaseOrderId"
		removeUrl="jqxGeneralServicer?fixedAssetId=${parameters.fixedAssetId}&sname=deleteFixedAssetMaint&jqaction=D"
		deleteColumn="fixedAssetId[${parameters.fixedAssetId}];maintHistSeqId"		
		showlist="true"
	/>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
			
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.Status}
    				</div>
    				<div class='span7'>
    					<div id="statusIdAdd"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_productMaintTypeId}
    				</div>
    				<div class='span7'>
    					<div id="productMaintTypeIdAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.AccountingFixedAssetMaintIntervalMeterType}
    				</div>
    				<div class='span7'>
    					<div id="intervalMeterTypeIdAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.AccountingFixedAssetMaintIntervalQuantity}
    				</div>
    				<div class='span7'>
    					<div id="intervalQuantityAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.AccountingFixedAssetMaintIntervalUom}
    				</div>
    				<div class='span7'>
    					<div id="intervalUomIdAdd">
	 					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_purchaseOrderId}
    				</div>
    				<div class='span7'>
    					<div id="purchaseOrderIdAdd">
	 					</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
	</div>
</div>	
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
<script type="text/javascript">
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		
		var tmpLcl = '${locale}';
		if(tmpLcl=='vi'){
			tmpLcl = 'vi-VN';
		}else{
			tmpLcl = 'en-EN';
		}
    
	var action = (function(){
		 var initElement = function(){
			$("#statusIdAdd").jqxDropDownList({source: dataStatusListView, width: '208px', displayMember:"description",valueMember: "statusId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#productMaintTypeIdAdd").jqxDropDownList({source: dataProductMaintTypeListView,  width: '208px', displayMember:"description",valueMember: "productMaintTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#intervalMeterTypeIdAdd").jqxDropDownList({source: dataProductMeterTypeListView, width: '208px', displayMember:"description",valueMember: "productMeterTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#intervalUomIdAdd").jqxDropDownList({source: dataIntervalUomListView,  width: '208px', displayMember:"description",valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#purchaseOrderIdAdd").jqxDropDownList({source: dataOrderListView,  filterable: true,  width: '208px', displayMember:"orderName" ,valueMember: "orderId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#intervalQuantityAdd").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0,min : 0, spinButtons: true });
			$("#intervalQuantityAdd").jqxNumberInput("val",null);
		    $("#alterpopupWindow").jqxWindow({
			        width: 470, height: 350, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme         
			    });
		 }
		
		var clear = function(){
			$("#productMaintTypeIdAdd").jqxDropDownList('clearSelection');
			$("#intervalMeterTypeIdAdd").jqxDropDownList('clearSelection');
			$("#intervalUomIdAdd").jqxDropDownList('clearSelection');
			$("#purchaseOrderIdAdd").jqxDropDownList('clearSelection');
			$("#intervalQuantityAdd").jqxNumberInput('clear');
			$("#statusIdAdd").jqxDropDownList('clearSelection');
		}
		
		var save = function(){
			var row;
		        row = { 
		        		productMaintTypeId:$('#productMaintTypeIdAdd').val(),
		        		intervalMeterTypeId:$('#intervalMeterTypeIdAdd').val(),
		        		intervalUomId:$('#intervalUomIdAdd').val(),	        		
		        		purchaseOrderId:$('#purchaseOrderIdAdd').val(),
		        		statusId:$('#statusIdAdd').val(),
		        		purchaseOrderId:$('#purchaseOrderIdAdd').val(),
		        		intervalQuantity:$('#intervalQuantityAdd').val()
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
			   return true;
		}
		var bindEvent = function(){
			  $('#alterpopupWindow').on('close', function (event) {
					clear();
				});
		    $("#save").click(function () {	    	
		    	if(save())  $("#alterpopupWindow").jqxWindow('close');
		    });
		
			$("#saveAndContinue").click(function () {
				if(save()) return;
			});
			
		}
	
		return {
			init : function(){
				initElement();
				bindEvent();
			}
		}
	}())
	$(document).ready(function(){
		action.init();
	})
</script>	  	
