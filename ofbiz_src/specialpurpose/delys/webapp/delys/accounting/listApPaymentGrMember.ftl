<script>
	<#assign payTypes = delegator.findList("PaymentType", null, null, null, null, false) />
	var payData = new Array();
	<#list payTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description) />
		row['paymentTypeId'] = '${item.paymentTypeId}';
		row['description'] = '${item.description}';
		payData[${item_index}] = row;
	</#list>
	
	<#assign payMethodTypes = delegator.findList("PaymentMethodType", null, null, null, null, false) />
	var payMethodData = new Array();
	<#list payMethodTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description) />
		row['paymentMethodTypeId'] = '${item.paymentMethodTypeId}';
		row['description'] = '${description}';
		payMethodData[${item_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'paymentId', type: 'string' },
					 { name: 'paymentGroupId', type: 'string' },
					 { name: 'paymentRefNum', type: 'number'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyIdTo', type: 'string'},
					 { name: 'paymentTypeId', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'paymentMethodTypeId', type: 'string'},
					 { name: 'amount', type: 'number'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.paymentId}', datafield: 'paymentId', editable: false, width: 150,
						cellsrenderer: function(row, column, value){
							return '<span><a href=paymentOverview?paymentId='+ value +'>' + value + '</a></span>';
						}
					 },
					 { text: '${uiLabelMap.paymentRefNum}', datafield: 'paymentRefNum', width: 150, editable: false},
					 { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom', editable: false, width: 150,
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
						}
					 },
					 { text: '${uiLabelMap.partyIdTo}', datafield: 'partyIdTo', editable: false, width: 150,
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
						}
					 },
					 { text: '${uiLabelMap.paymentTypeId}', datafield: 'paymentTypeId', editable: false, width: 150,
						 cellsrenderer: function(row, column, value){
							 for(i = 0; i < payData.length; i++){
								 if(payData[i].paymentTypeId == value){
									 return '<span title=' + value +'>' + payData[i].description + '</span>'
								 }
							 }
							 return;
						 }
					 },
					 { text: '${uiLabelMap.paymentMethodTypeId}', datafield: 'paymentMethodTypeId', editable: false, width: 150,
						 cellsrenderer: function(row, column, value){
							 for(i = 0; i < payMethodData.length; i++){
								 if(payMethodData[i].paymentMethodTypeId == value){
									 return '<span title=' + value +'>' + payMethodData[i].description + '</span>'
								 }
							 }
							 return;
						 }
					 },
					 { text: '${uiLabelMap.amount}', datafield: 'amount', editable: false, width: 150, filterable: false, sortable: false,
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 return '<span>' + formatcurrency(data.amount,data.currencyUomId) +'</span>';
						 }
					 },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', editable: false, width: 150, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', editable: true, width: 150, cellsformat: 'dd/MM/yyyy'}
					"/>
<@jqGrid filtersimplemode="false" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="wdwNewPaymentMember" editable="true"
		 url="jqxGeneralServicer?sname=JQApListPaymentGroupMember&paymentGroupId=${parameters.paymentGroupId}"
		 removeUrl="jqxGeneralServicer?sname=expirePaymentGroupMember&jqaction=D&paymentGroupId=${parameters.paymentGroupId}"
		 deleteColumn="paymentGroupId;paymentId;fromDate(java.sql.Timestamp)"
		 createUrl="jqxGeneralServicer?sname=createPaymentGroupMember&jqaction=C&paymentGroupId=${parameters.paymentGroupId}"
		 addColumns="paymentGroupId[${parameters.paymentGroupId}];paymentId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);sequenceNum(java.lang.Long)"
		 updateUrl="jqxGeneralServicer?sname=updatePaymentGroupMember&jqaction=U&paymentGroupId=${parameters.paymentGroupId}"
		 editColumns="paymentGroupId;paymentId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		/>
<div id="wdwNewPaymentMember" style="display:none;">
	<div id="wdwHeader">
	    <span>
	       ${uiLabelMap.NewPayment}
	    </span>
	</div>
	<div style="overflow: hidden; padding: 5px; margin-left: 10px" id="wdwContent">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNewEmplAgreement" id="formNewEmplAgreement">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.paymentId}:</label>
							<div class="controls">
								<div id="paymentIdAdd">
									<div id="jqxPayGrid"/>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.fromDate}:</label>  
							<div class="controls">
								<div id="fromDateAdd">
					       	 	</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.thruDate}:</label>
							<div class="controls">
								<div id="thruDateAdd"></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.sequenceNum}:</label>  
							<div class="controls">
								<input id="sequenceNumAdd"></input>
							</div>
						</div>
					</div>
				</div>
			</form>
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
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	//Create Popup Window
	$("#wdwNewPaymentMember").jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 350, minWidth: '40%', width: "50%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
        initContent: function () {
        	//Create fromDate
        	$("#fromDateAdd").jqxDateTimeInput({width: '200px', height: '25px'});
        	
        	//Create thruDate
        	$("#thruDateAdd").jqxDateTimeInput({width: '200px', height: '25px'});
        	
        	$("#sequenceNumAdd").jqxInput({width: '195px'});
        	//Create paymentId
        	var sourcePayment =
        		{
        			datafields:
        				[
        				 { name: 'paymentId', type: 'string' },
        				 { name: 'partyIdFrom', type: 'string' },
        				 { name: 'partyIdTo', type: 'string' },
        				 { name: 'effectiveDate', type: 'date'},
        				 { name: 'amount', type: 'number' },
        				 { name: 'currencyUomId', type: 'string' }
        				],
        			cache: false,
        			root: 'results',
        			datatype: "json",
        			updaterow: function (rowid, rowdata) {
        				// synchronize with the server - send update command   
        			},
        			beforeprocessing: function (data) {
        				sourcePayment.totalrecords = data.TotalRows;
        			},
        			filter: function () {
        				// update the grid and send a request to the server.
        				$("#jqxPayGrid").jqxGrid('updatebounddata');
        			},
        			pager: function (pagenum, pagesize, oldpagenum) {
        				// callback called when a page or page size is changed.
        			},
        			sort: function () {
        				$("#jqxPayGrid").jqxGrid('updatebounddata');
        			},
        			sortcolumn: 'paymentId',
        			sortdirection: 'asc',
        			type: 'POST',
        			data: {
        				noConditionFind: 'Y',
        				conditionsFind: 'N',
        			},
        			pagesize:5,
        			contentType: 'application/x-www-form-urlencoded',
        			url: 'jqxGeneralServicer?sname=getListPayment',
        		};
        	var dataAdapterPayment = new $.jqx.dataAdapter(sourcePayment);
        	$("#paymentIdAdd").jqxDropDownButton({theme: theme,  width: 200, height: 25});
        	$("#jqxPayGrid").jqxGrid({
        	width:800,
            source: dataAdapterPayment,
            filterable: false,
            virtualmode: true, 
            sortable:true,
            editable: false,
            theme: theme,
            autoheight:true,
            pageable: true,
            rendergridrows: function(obj)
        		{
        			return obj.data;
        		},
            columns: [
              { text: '${uiLabelMap.paymentId}', datafield: 'paymentId'},
              { text: '${uiLabelMap.partyIdFrom}', datafield: 'partyIdFrom'},
              { text: '${uiLabelMap.partyIdTo}', datafield: 'partyIdTo'},
              { text: '${uiLabelMap.effectiveDate}', datafield: 'effectiveDate', cellsformat: 'dd/MM/yyyy'},
              { text: '${uiLabelMap.amount}', datafield: 'amount'},
              { text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId'}
            ]
        	});
        	$("#jqxPayGrid").on('rowselect', function (event) {
        		var args = event.args;
        		var row = $("#jqxPayGrid").jqxGrid('getrowdata', args.rowindex);
        		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['paymentId'] +'</div>';
        		$('#paymentIdAdd').jqxDropDownButton('setContent', dropDownContent);
        	});
        }
	});
	
	// update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
	    var row;
	    row = { 
	    		paymentId:$('#paymentIdAdd').val(),
	    		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate'),
	    		thruDate:$('#thruDateAdd').jqxDateTimeInput('getDate'),
	    		sequenceNum:$('#sequenceNumAdd').val()
	        };
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	    // select the first row and clear the selection.
	    $("#jqxgrid").jqxGrid('clearSelection');                        
	    $("#jqxgrid").jqxGrid('selectRow', 0);  
	    $("#wdwNewPaymentMember").jqxWindow('close');
    });
</script>