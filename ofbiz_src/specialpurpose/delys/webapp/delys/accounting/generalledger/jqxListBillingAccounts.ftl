<#--Import LIB-->
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for uom data
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
	var currencyUomData = [
		<#list uoms as item>
			{
				<#assign description = StringUtil.wrapString(item.description + "-" + item.abbreviation) />
				uomId : '${item.uomId}',
				description : '${description}',
			},
		</#list>
	]
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'billingAccountId', type: 'string'},
					 { name: 'accountLimit', type: 'number'},
					 { name: 'description', type: 'string'},
					 { name: 'accountCurrencyUomId', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_billingAccountId}', datafield: 'billingAccountId', width: 160, editable: false,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							return '<span><a href=EditBillingAccountRoles?billingAccountId=' + value + '>' + value + '</a></span>';
						}
					 },
                     { text: '${uiLabelMap.FormFieldTitle_accountLimit}', datafield: 'accountLimit', width: 150,
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								return '<span>' + formatcurrency(value) + '</span>';
						}
                     },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: 120,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',filtertype:'range',
                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
  							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
  					    }
                     },
                     { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: 120,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',filtertype:'range',
                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
  							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
  					    }
                     },
                     { text: '${uiLabelMap.FormFieldTitle_description}', datafield: 'description'},
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrow="true" addrefresh="true" editable="false" addType="popup" alternativeAddPopup="wdwNewBillingAcc" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListBillingAccount" dataField=dataField columnlist=columnlist
		 createUrl="jqxGeneralServicer?sname=createBillingAccount&jqaction=C" 
		 addColumns="roleTypeId;accountCurrencyUomId;contactMechId;description;externalAccountId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);accountLimit(java.math.BigDecimal)"
		 updateUrl="jqxGeneralServicer?sname=updateBillingAccount&jqaction=U"
		 editColumns="billingAccountId;roleTypeId;accountCurrencyUomId;contactMechId;description;externalAccountId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);accountLimit(java.math.BigDecimal)"
		 />
                     
<#--=================================/Init Grid======================================================-->
<div id="wdwNewBillingAcc" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.NewBillingAccount}
		</span>
	</div>
	<div id="wdwContentNew">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNew" id="formNew">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_accountLimit}:</label>  
								<div class="controls">
									<div id="accountLimit"></div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_accountCurrencyUomId}:</label>  
								<div class="controls">
									<div id="accountCurrencyUomIdAdd">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_contactMechId}:</label>  
								<div class="controls">
									<input id="contactMechIdAdd" />
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.fromDate}:</label>  
								<div class="controls">
									<div id="fromDate">
									</div>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.thruDate}:</label>  
								<div class="controls">
									<div id="thruDate">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_externalAccountId}:</label>  
								<div class="controls">
									<input id="externalAccountIdAdd">
									</input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingPartyBilledTo}:</label>  
								<div class="controls">
									<div id="addPartyId">
										<div id="jqxGridParty"></div>
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_description}:</label>  
								<div class="controls">
									<textarea rows="4" cols="45" style="width: 190px; height: 60px;" id="description">
									</textarea>
								</div>
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
<div id="wdwEditBillingAcc" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.EditBillingAccount}
		</span>
	</div>
	<div id="wdwContentEdit">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formEdit" id="formEdit">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="span6">
							<input id="editBillingAccountId" type="hidden"></input>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_accountLimit}:</label>  
								<div class="controls">
									<div id="editAccountLimit"></div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_accountCurrencyUomId}:</label>  
								<div class="controls">
									<div id="editAccountCurrencyUomId">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_contactMechId}:</label>  
								<div class="controls">
									<div id="editContactMechId" ></div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.fromDate}:</label>  
								<div class="controls">
									<div id="editFromDate">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingBillingAvailableBalance}:</label>  
								<div class="controls">
									<div id="availableBalance">
									</div>
									<input id="availableBalanceInput" type="hidden"></input>
								</div>
							</div>
						</div>
						<div class="span6">
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.thruDate}:</label>  
								<div class="controls">
									<div id="editThruDate">
									</div>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_externalAccountId}:</label>  
								<div class="controls">
									<input id="editExternalAccountId">
									</input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.AccountingPartyBilledTo}:</label>  
								<div class="controls">
									<div id="editPartyId">
									</div>
									<input id="editPartyIdInput" type="hidden"></input>
								</div>
							</div>
							<div class="control-group no-left-margin">
								<label class="control-label">${uiLabelMap.FormFieldTitle_description}:</label>  
								<div class="controls">
									<textarea rows="4" cols="45" style="width: 190px; height: 60px;" id="editDescription">
									</textarea>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="editAlterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="editAlterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
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
		$('#wdwNewBillingAcc').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 400, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
            initContent: function () {
            	$("#accountLimit").jqxNumberInput({spinButtons: false});
            	
            	$("#accountCurrencyUomIdAdd").jqxDropDownList({source: currencyUomData, valueMember: 'uomId', displayMember: 'description'});
            	
            	$("#contactMechIdAdd").jqxInput({width: 195});
            	
            	$('#fromDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	
            	$('#thruDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	
            	$("#externalAccountIdAdd").jqxInput({width: 195});
            	
            	var sourceParty = { 
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
		    			sourceParty.totalrecords = data.TotalRows;
					},
					filter: function () {
		   				// update the grid and send a request to the server.
		   				$('#jqxGridParty').jqxGrid('updatebounddata');
					},
					sort: function () {
		  				$('#jqxGridParty').jqxGrid('updatebounddata');
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
			    var dataAdapterParty = new $.jqx.dataAdapter(sourceParty,
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
	                        if (!sourceParty.totalRecords) {
	                            sourceParty.totalRecords = parseInt(data['odata.count']);
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
			    $("#addPartyId").jqxDropDownButton({ width: 200, height: 25});
	            $('#jqxGridParty').jqxGrid({
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
            }
        });
		
		$('#wdwEditBillingAcc').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 400, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#editAlterCancel',
            initContent: function () {
            	$("#editAccountLimit").jqxNumberInput({spinButtons: false});
            	
            	$("#editAccountCurrencyUomId").jqxDropDownList({source: currencyUomData, valueMember: 'uomId', displayMember: 'description'});
            	if(!$('#editFromDate').hasClass('jqx-datetimeinput')){
            		$('#editFromDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	}
            	if(!$('#editThruDate').hasClass('jqx-datetimeinput')){
            		$('#editThruDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	}
            	
            	$("#editExternalAccountId").jqxInput({width: 195});
            }
        });
	};
	
	JQXAction.prototype.bindEvent = function(){		
		$('#alterSave').on('click', function(){
			var row = {};
	    	row.accountLimit = $("#accountLimit").val();
			row.roleTypeId = "BILL_TO_CUSTOMER";
			row.accountCurrencyUomId = $("#accountCurrencyUomIdAdd").val();
			row.contactMechId = $("#contactMechIdAdd").val();
			row.externalAccountId = $("#externalAccountIdAdd").val();
			row.partyId = $("#addPartyId").val();
			row.fromDate = $("#fromDate").jqxDateTimeInput('getDate');
			row.thruDate = $("#thruDate").jqxDateTimeInput('getDate');
	    	row.description = $("#description").val();
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        $("#wdwNewBillingAcc").jqxWindow('close');
		});
		
		$('#editAlterSave').on('click', function(){
			var row = {};
			row.accountLimit = $("#editAccountLimit").val();
	    	row.billingAccountId = $("#editBillingAccountId").val();
			row.roleTypeId = "BILL_TO_CUSTOMER";
			row.accountCurrencyUomId = $("#editAccountCurrencyUomId").val();
			row.contactMechId = $("#editContactMechId").val();
			row.externalAccountId = $("#editExternalAccountId").val();
			row.partyId = $("#editPartyIdInput").val();
			row.fromDate = $("#editFromDate").jqxDateTimeInput('getDate');
			row.thruDate = $("#editThruDate").jqxDateTimeInput('getDate');
	    	row.description = $("#editDescription").val();
	    	row.availableBalance = $("#availableBalanceInput").val();
	    	$("#jqxgrid").jqxGrid('updaterow', boundIndex, row);
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        $("#wdwEditBillingAcc").jqxWindow('close');
		});
		
		$("#jqxGridParty").on('rowselect', function (event) {
    		var args = event.args;
    		var row = $("#jqxGridParty").jqxGrid('getrowdata', args.rowindex);
    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
    		$('#addPartyId').jqxDropDownButton('setContent', dropDownContent);
    		$('#addPartyId').jqxDropDownButton('close');
    	});
		
		$('#jqxgrid').on('rowDoubleClick', function (event) { 
		    var args = event.args;
		    boundIndex = args.rowindex;
		    var row = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
		    $("#editBillingAccountId").val(row.billingAccountId);
		    $("#editAccountLimit").val(row.accountLimit);
		    for(var i = 0; i < currencyUomData.length; i++){
		    	if(currencyUomData[i].uomId == row.accountCurrencyUomId){
		    		$("#editAccountCurrencyUomId").jqxDropDownList({selectedIndex: i});
		    		break;
		    	}
		    }
		    if(!$('#editFromDate').hasClass('jqx-datetimeinput')){
        		$('#editFromDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
        	}
        	if(!$('#editThruDate').hasClass('jqx-datetimeinput')){
        		$('#editThruDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
        	}
		    
	    	$("#editFromDate").jqxDateTimeInput('setDate', row.fromDate);
	    	$("#editThruDate").jqxDateTimeInput('setDate', row.thruDate);
	    	
		    $("#editExternalAccountId").val(row.externalAccountId);
    		$("#editDescription").val(row.description);
    		$.ajax({
                url: "getBillingAccountBalance",
                type: "POST",
                cache: false,
                datatype: 'json',
                async: false,
                data: {billingAccountId: row.billingAccountId},
                success: function (data, status, xhr) {
                	$("#availableBalance").text(data.availableBalance);
                	$("#availableBalanceInput").text(data.availableBalance);
                }
            });
    		
    		//Prepare for address data
    		<#assign billAddresses = delegator.findList("BillingAccountRoleAndAddress", null, null, null, null, false)>
			var billAddressData = new Array();
			var index = 0;
    		<#list billAddresses as item>
    			if('${item.billingAccountId}' == row.billingAccountId){
    				var address = {};
    				address['contactMechId'] = '${item.contactMechId?if_exists}';
    				address['description'] = "[${item.partyId?if_exists}][${item.contactMechId?if_exists}] ${item.toName?if_exists}, ${item.attnName?if_exists}, ${item.address1?if_exists}, ${item.stateProvinceGeoId?if_exists} ${item.postalCode?if_exists}";
    				billAddressData[index++] = address;
    			}
			</#list>
			
			//Prepare for BillingAccountRole data
    		<#assign billingAccountRoles = delegator.findByAnd("BillingAccountRole", null, null, false)>
    		<#list billingAccountRoles as item>
				if('${item.billingAccountId}' == row.billingAccountId && '${item.roleTypeId}' == 'BILL_TO_CUSTOMER'){
					var partyId = '${item.partyId}';
				}
			</#list>
			$("#editPartyId").text(partyId ? partyId : '_NA_');
			$("#editPartyIdInput").val(partyId);
    		$("#editContactMechId").jqxDropDownList({selectedIndex: 0, source: billAddressData, valueMember: 'contactMechId', displayMember: 'description'});
		    $('#wdwEditBillingAcc').jqxWindow('open');
		});
	};
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction.bindEvent();
	});
</script>
<#--====================================================/Setup JS======================================-->