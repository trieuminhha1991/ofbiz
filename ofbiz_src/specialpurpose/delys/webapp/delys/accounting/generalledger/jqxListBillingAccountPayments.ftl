<#--Import LIB-->
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for Payment Method Type data
	<#assign paymentMethodTypeList = delegator.findList("PaymentMethodType", null, null, null, null, false) />
	paymentMethodTypeData = [
	              <#list paymentMethodTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'paymentMethodTypeId': '${item.paymentMethodTypeId}', 'description': '${description}'},
				  </#list>
				];
	
	//Prepare for Payment Type Data
	<#assign paymentTypeList = delegator.findList("PaymentType", null, null, null, null, false) />
	paymentTypeData = [
	              <#list paymentTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'paymentTypeId': '${item.paymentTypeId}', 'description': '${description}'},
				  </#list>
				];
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid===========================================================-->
<#assign dataField="[{ name: 'paymentId', type: 'string'},
					 { name: 'paymentMethodTypeId', type: 'string'},
					 { name: 'invoiceId', type: 'string'},
					 { name: 'invoiceItemSeqId', type: 'string'},
					 { name: 'effectiveDate', type: 'date', other: 'Timestamp'},
					 { name: 'amountApplied', type: 'string'},
					 { name: 'amount', type: 'string'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_paymentId}', datafield: 'paymentId', editable: false, editable: 'false',
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								return '<span title=' + value + '><a href=accArpaymentOverview?paymentId=' + value + '>' + value + '</a></span>';
						}
					 },
                     { text: '${uiLabelMap.FormFieldTitle_paymentMethodTypeId}', datafield: 'paymentMethodTypeId', width: 250, editable: 'false', filtertype: 'checkedlist',
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
 							for(var i = 0; i < paymentMethodTypeData.length; i++){
 								if(value == paymentMethodTypeData[i].paymentMethodTypeId){
 									return '<span title=' + value + '>' + paymentMethodTypeData[i].description + '</span>';
 								}
 							}
 							return '<span> ' + value + '</span>';
 						},
 						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(paymentMethodTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'paymentMethodTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < paymentMethodTypeData.length; i++){
										if(paymentMethodTypeData[i].paymentMethodTypeId == value){
											return '<span>' + paymentMethodTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
                     },
                     { text: '${uiLabelMap.FormFieldTitle_invoiceId}', datafield: 'invoiceId', width: 250, editable: 'false',
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
 							return '<span title=' + value + '><a href=accArinvoiceOverview?invoiceId=' + value + '>' + value + '</a></span>';
 						}
                     },
                     { text: '${uiLabelMap.FormFieldTitle_invoiceItemSeqId}', datafield: 'invoiceItemSeqId', width: 150, editable: 'false'},
                     { text: '${uiLabelMap.FormFieldTitle_effectiveDate}', datafield: 'effectiveDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable: 'false'},
                     { text: '${uiLabelMap.FormFieldTitle_amountApplied}', datafield: 'amountApplied', width: 150, editable: 'false',
                    	 cellsrenderer: function(row, column, value){
							  return '<span >' + formatcurrency(value,'${defaultCurrencyUomId?if_exists}') + '</span>';
                    	 }
                     },
                     { text: '${uiLabelMap.FormFieldTitle_amount}', datafield: 'amount', width: 150, editable: 'false',
                    	 cellsrenderer: function(row, column, value){
							  return '<span >' + formatcurrency(value,'${defaultCurrencyUomId?if_exists}') + '</span>';
                    	 }
                     }
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrow="true" addrefresh="true" editable="false" deleterow="false" addType="popup" alternativeAddPopup="wdwNewBillingAccPayment" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListBillingAccountPayments&billingAccountId=${parameters.billingAccountId}" dataField=dataField columnlist=columnlist
		 createUrl="jqxGeneralServicer?sname=createPaymentAndApplication&jqaction=C" 
		 addColumns="billingAccountId[${parameters.billingAccountId}];currencyUomId;statusId;partyIdFrom;partyIdTo;paymentTypeId;paymentMethodTypeId;amount(java.math.BigDecimal)"
		 />
                     
<#--=================================/Init Grid======================================================-->
<div id="wdwNewBillingAccPayment" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.NewBillingAccountPayment}
		</span>
	</div>
	<div id="wdwContentNew">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNew" id="formNew">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.partyIdFrom}:</label>  
							<div class="controls">
								<div id="partyIdFrom">
									<div id="jqxGridPartyFrom"></div>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.partyIdTo}:</label>  
							<div class="controls">
								<div id="partyIdTo">
									<div id="jqxGridPartyTo"></div>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.paymentTypeId}:</label>  
							<div class="controls">
								<div id="paymentTypeId">
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.paymentMethodTypeId}:</label>  
							<div class="controls">
								<div id="paymentMethodTypeId">
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.amount}:</label>  
							<div class="controls">
								<input id="amount">
								</input>
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
<#--====================================================Setup JS======================================-->
<script>
	var JQXAction = function(){};
	JQXAction.prototype.theme = 'olbius';
	JQXAction.prototype.initWindow = function(){
		$('#wdwNewBillingAccPayment').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 350, minWidth: '40%', width: "50%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
            initContent: function () {
            	var sourcePartyFrom = { 
        			datafields: [
				      { name: 'partyId', type: 'string' },
				      { name: 'partyTypeId', type: 'string' },
				      { name: 'firstName', type: 'string' },
				      { name: 'lastName', type: 'string' },
				      { name: 'groupName', type: 'string' }
				    ],
					cache: false,
					root: 'results',
					datatype: 'json',
					
					beforeprocessing: function (data) {
						sourcePartyFrom.totalrecords = data.TotalRows;
					},
					filter: function () {
		   				// update the grid and send a request to the server.
		   				$('#jqxGridPartyFrom').jqxGrid('updatebounddata');
					},
					sort: function () {
		  				$('#jqxGridPartyFrom').jqxGrid('updatebounddata');
					},
					sortcolumn: 'partyId',
					sortdirection: 'asc',
					type: 'POST',
					data: {
						noConditionFind: 'Y',
						conditionsFind: 'N',
					},
					pagesize:5,
					contentType: 'application/x-www-form-urlencoded',
					url: 'jqxGeneralServicer?sname=getFromParty',
				};
			    var dataAdapterParty = new $.jqx.dataAdapter(sourcePartyFrom,
			    {
			    	formatData: function (data) {
				    	if (data.filterscount) {
                            var filterListFields = '';
                            for (var i = 0; i < data.filterscount; i++) {
                                var filterValue = data['filtervalue' + i];
                                var filterCondition = data['filtercondition' + i];
                                var filterDataField = data['filterdatafield' + i];
                                var filterOperator = data['filteroperator' + i];
                                filterListFields += '|OLBIUS|' + filterDataField;
                                filterListFields += '|SUIBLO|' + filterValue;
                                filterListFields += '|SUIBLO|' + filterCondition;
                                filterListFields += '|SUIBLO|' + filterOperator;
                            }
                            data.filterListFields = filterListFields;
                        }
                         data.$skip = data.pagenum * data.pagesize;
                         data.$top = data.pagesize;
                         data.$inlinecount = 'allpages';
                        return data;
                    },
                    loadError: function (xhr, status, error) {
	                    alert(error);
	                },
	                downloadComplete: function (data, status, xhr) {
	                        if (!sourcePartyFrom.totalRecords) {
	                            sourcePartyFrom.totalRecords = parseInt(data['odata.count']);
	                        }
	                }, 
	                beforeLoadComplete: function (records) {
	                	for (var i = 0; i < records.length; i++) {
	                		if(typeof(records[i])=='object'){
	                			for(var key in records[i]) {
	                				var value = records[i][key];
	                				if(value != null && typeof(value) == 'object' && typeof(value) != null){
	                					var date = new Date(records[i][key]['time']);
	                					records[i][key] = date;
	                				}
	                			}
	                		}
	                	}
	                }
			    });
			    $("#partyIdFrom").jqxDropDownButton({ width: 200, height: 25});
			    $('#jqxGridPartyFrom').jqxGrid({
	            	width:400,
	                source: dataAdapterParty,
	                filterable: true,
	                showfilterrow: true,
	                virtualmode: true, 
	                sortable:true,
	                editable: false,
	                autoheight:true,
	                pageable: true,
	                rendergridrows: function(obj)
					{
						return obj.data;
					},
	                columns: [
	                  { text: '${uiLabelMap.partyId}', datafield: 'partyId', width: 150},
	                  { text: '${uiLabelMap.firstName}', datafield: 'firstName', width: 150},
	                  { text: '${uiLabelMap.lastName}', datafield: 'lastName', width: 150},
	                  { text: '${uiLabelMap.groupName}', datafield: 'groupName', width: 150}
	                ]
	            });
			    
			    var sourcePartyTo = { 
	        			datafields: [
					      { name: 'partyId', type: 'string' },
					      { name: 'groupName', type: 'string' }
					    ],
						cache: false,
						root: 'results',
						datatype: 'json',
						
						beforeprocessing: function (data) {
							sourcePartyTo.totalrecords = data.TotalRows;
						},
						filter: function () {
			   				// update the grid and send a request to the server.
			   				$('#jqxGridPartyTo').jqxGrid('updatebounddata');
						},
						sort: function () {
			  				$('#jqxGridPartyTo').jqxGrid('updatebounddata');
						},
						sortcolumn: 'partyId',
						sortdirection: 'asc',
						type: 'POST',
						data: {
							noConditionFind: 'Y',
							conditionsFind: 'N',
						},
						pagesize:5,
						contentType: 'application/x-www-form-urlencoded',
						url: 'jqxGeneralServicer?sname=getListPartyGroups',
					};
				    var dataAdapterPartyTo = new $.jqx.dataAdapter(sourcePartyTo,
				    {
				    	formatData: function (data) {
					    	if (data.filterscount) {
	                            var filterListFields = '';
	                            for (var i = 0; i < data.filterscount; i++) {
	                                var filterValue = data['filtervalue' + i];
	                                var filterCondition = data['filtercondition' + i];
	                                var filterDataField = data['filterdatafield' + i];
	                                var filterOperator = data['filteroperator' + i];
	                                filterListFields += '|OLBIUS|' + filterDataField;
	                                filterListFields += '|SUIBLO|' + filterValue;
	                                filterListFields += '|SUIBLO|' + filterCondition;
	                                filterListFields += '|SUIBLO|' + filterOperator;
	                            }
	                            data.filterListFields = filterListFields;
	                        }
	                         data.$skip = data.pagenum * data.pagesize;
	                         data.$top = data.pagesize;
	                         data.$inlinecount = 'allpages';
	                        return data;
	                    },
	                    loadError: function (xhr, status, error) {
		                    alert(error);
		                },
		                downloadComplete: function (data, status, xhr) {
		                        if (!sourcePartyTo.totalRecords) {
		                            sourcePartyTo.totalRecords = parseInt(data['odata.count']);
		                        }
		                }, 
		                beforeLoadComplete: function (records) {
		                	for (var i = 0; i < records.length; i++) {
		                		if(typeof(records[i])=='object'){
		                			for(var key in records[i]) {
		                				var value = records[i][key];
		                				if(value != null && typeof(value) == 'object' && typeof(value) != null){
		                					var date = new Date(records[i][key]['time']);
		                					records[i][key] = date;
		                				}
		                			}
		                		}
		                	}
		                }
				    });
				    $("#partyIdTo").jqxDropDownButton({ width: 200, height: 25});
				    $('#jqxGridPartyTo').jqxGrid({
		            	width:400,
		                source: dataAdapterPartyTo,
		                filterable: true,
		                showfilterrow: true,
		                virtualmode: true, 
		                sortable:true,
		                editable: false,
		                autoheight:true,
		                pageable: true,
		                rendergridrows: function(obj)
						{
							return obj.data;
						},
		                columns: [
		                  { text: '${uiLabelMap.partyId}', datafield: 'partyId', width: 150},
		                  { text: '${uiLabelMap.groupName}', datafield: 'groupName', width: 150}
		                ]
		            });
				    
				    $("#paymentMethodTypeId").jqxDropDownList({source: paymentMethodTypeData, valueMember: 'paymentMethodTypeId', displayMember: 'description'});
				    
				    $("#paymentTypeId").jqxDropDownList({source: paymentTypeData, valueMember: 'paymentTypeId', displayMember: 'description'});
				    
				    $("#amount").jqxInput({width: 195});
            	}
        });
	};
	
	JQXAction.prototype.bindEvent = function(){		
		$('#alterSave').on('click', function(){
			var row = {};
	    	row.partyIdFrom = $("#partyIdFrom").val();
			row.partyIdTo = $("#partyIdTo").val();
			row.paymentTypeId = $("#paymentTypeId").val();
			row.paymentMethodTypeId = $("#paymentMethodTypeId").val();
			row.amount = $("#amount").val();
			row.currencyUomId = '${billingAccount.accountCurrencyUomId}';
			row.statusId = 'PMNT_NOT_PAID';
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        $("#wdwNewBillingAccPayment").jqxWindow('close');
		});
		
		$("#jqxGridPartyFrom").on('rowselect', function (event) {
    		var args = event.args;
    		var row = $("#jqxGridPartyFrom").jqxGrid('getrowdata', args.rowindex);
    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
    		$('#partyIdFrom').jqxDropDownButton('setContent', dropDownContent);
    		$('#partyIdFrom').jqxDropDownButton('close');
    	});
		
		$("#jqxGridPartyTo").on('rowselect', function (event) {
    		var args = event.args;
    		var row = $("#jqxGridPartyTo").jqxGrid('getrowdata', args.rowindex);
    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
    		$('#partyIdTo').jqxDropDownButton('setContent', dropDownContent);
    		$('#partyIdTo').jqxDropDownButton('close');
    	});
	};
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction.bindEvent();
	});
</script>
<#--====================================================/Setup JS======================================-->