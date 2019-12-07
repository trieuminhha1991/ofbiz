<#--IMPORT LIB-->
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#--/IMPORT LIB-->
<script>
	//Prepare finAccountType data
	<#assign finAccountTypes = delegator.findList("FinAccountType", null, null, null, null, false)/>
	var finAccTypeData = new Array();
	<#list finAccountTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
		row['finAccountTypeId'] = '${item.finAccountTypeId?if_exists}';
		row['description'] = '${description?if_exists}';
		finAccTypeData[${item_index?if_exists}] = row;
	</#list>
	
	//Prepare status data
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "FINACCT_STATUS"), null, null, null, false) />
	var statusData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index?if_exists}] = row;
	</#list>
	
	//Prepare uom data
	<#assign uoms = delegator.findList("Uom", null, null, null, null, false) />
	var uomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists + "-" + item.uomId?if_exists)/>
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description}';
		uomData[${item_index?if_exists}] = row;
	</#list>
	
	// Create Method Payment
	<#assign listPaymentMethods = delegator.findList("PaymentMethod", null, null, null, null, false) />
	 var dataPM = [
	           	<#list listPaymentMethods as lpm>
	       	        {
	       	        	paymentMethodId : "${lpm.paymentMethodId}",
	       		        description : "${lpm.description}"	
	       	        },
	       	    </#list>
	           ];
	
	//Yes No data
	var isRefundableData = new Array();
	var row0 = {};
	row0['isRefundable'] = 'Y';
	row0['description'] = '${uiLabelMap.FormFieldTitle_Yes}';
	isRefundableData[0] = row0;
	var row1 = {};
	row1['isRefundable'] = 'N';
	row1['description'] = '${uiLabelMap.FormFieldTitle_No}';
	isRefundableData[1] = row1;
</script>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_finAccountId}', dataField: 'finAccountId',width: '150px', editable:false, filterable: true,
						cellsrenderer: function(row, column, value){
							return '<span><a href=EditFinAccountRoles?finAccountId=' + value + '>' + value + '</a></span>'
							}
						},
					 { text: '${uiLabelMap.FormFieldTitle_finAccountType}', dataField: 'finAccountTypeId',width: '150px', editable:false, filtertype: 'checkedlist', columntype: 'template',filterable: true,
						cellsrenderer: function(row, column, value){
							for(i = 0; i < finAccTypeData.length; i++){
								if(finAccTypeData[i].finAccountTypeId == value){
									return '<span title=' + value + '>' + finAccTypeData[i].description + '</span>'
								}
							}
							return ;
						},
						createfilterwidget: function(column, columnElement, widget){
							var filterAccTypeDataAdapter = new $.jqx.dataAdapter(finAccTypeData, {
								autoBind: true
							});
							var records = filterAccTypeDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'finAccountTypeId', valueMember: 'finAccountTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < finAccTypeData.length; i++){
										if(finAccTypeData[i].finAccountTypeId == value){
											return '<span>' + finAccTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
					 },
					 { text: '${uiLabelMap.statusId}', dataField: 'statusId', editable:true, width: '150px', columntype: 'template',filterable: false,
						 cellsrenderer: function(row, column, value){
							 for(i = 0; i < statusData.length; i++){
								 if(statusData[i].statusId == value){
									 return '<span title=' + value + '>' + statusData[i].description + '</span>'
								 }
							 }
							 return ;
						 }
					 },
					 { text: '${uiLabelMap.FormFieldTitle_finAccountName}', dataField: 'finAccountName',width: '150px', editable: true, filterable: true},
					 { text: '${uiLabelMap.FormFieldTitle_finAccountCode}', dataField: 'finAccountCode',width: '150px', editable: true, filterable: false},
					 { text: '${uiLabelMap.FormFieldTitle_finAccountPin}', dataField: 'finAccountPin',width: '150px', editable: true, filterable: false},
					 { text: '${uiLabelMap.currencyUomId}', dataField: 'currencyUomId',width: '150px', editable: true, columntype: 'template', filterable: false},
					 { text: '${uiLabelMap.organizationPartyId}', dataField: 'organizationPartyId',width: '150px', editable: true, columntype: 'template', filterable: false,
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
						 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	                            editor.append('<div id=\"jqxGridParty\"></div>');
	                            editor.jqxDropDownButton({width: '150'});
	                            // prepare the data
							    var sourceParty = { datafields: [
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
					   				$(\"#jqxGridParty\").jqxGrid('updatebounddata');
								},
								sort: function () {
					  				$(\"#jqxGridParty\").jqxGrid('updatebounddata');
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
				                            var filterListFields = \"\";
				                            for (var i = 0; i < data.filterscount; i++) {
				                                var filterValue = data[\"filtervalue\" + i];
				                                var filterCondition = data[\"filtercondition\" + i];
				                                var filterDataField = data[\"filterdatafield\" + i];
				                                var filterOperator = data[\"filteroperator\" + i];
				                                filterListFields += \"|OLBIUS|\" + filterDataField;
				                                filterListFields += \"|SUIBLO|\" + filterValue;
				                                filterListFields += \"|SUIBLO|\" + filterCondition;
				                                filterListFields += \"|SUIBLO|\" + filterOperator;
				                            }
				                            data.filterListFields = filterListFields;
				                        }
				                         data.$skip = data.pagenum * data.pagesize;
				                         data.$top = data.pagesize;
				                         data.$inlinecount = \"allpages\";
				                        return data;
				                    },
				                    loadError: function (xhr, status, error) {
					                    alert(error);
					                },
					                downloadComplete: function (data, status, xhr) {
					                        if (!sourceParty.totalRecords) {
					                            sourceParty.totalRecords = parseInt(data[\"odata.count\"]);
					                        }
					                }, 
					                beforeLoadComplete: function (records) {
					                	for (var i = 0; i < records.length; i++) {
					                		if(typeof(records[i])==\"object\"){
					                			for(var key in records[i]) {
					                				var value = records[i][key];
					                				if(value != null && typeof(value) == \"object\" && typeof(value) != null){
					                					var date = new Date(records[i][key][\"time\"]);
					                					records[i][key] = date;
					                				}
					                			}
					                		}
					                	}
					                }
							    });
					            $(\"#jqxGridParty\").jqxGrid({
					            	width:400,
					                source: dataAdapterParty,
					                filterable: true,
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
					                  { text: 'partyId', datafield: 'partyId'},
					                  { text: 'partyTypeId', datafield: 'partyTypeId'},
					                  { text: 'firstName', datafield: 'firstName'},
					                  { text: 'lastName', datafield: 'lastName'},
					                  { text: 'groupName', datafield: 'groupName'}
					                ]
					            });
					            isSelectedOrg = false;
					            $(\"#jqxGridParty\").on('rowselect', function (event) {
		                                var args = event.args;
		                                var row = $(\"#jqxGridParty\").jqxGrid('getrowdata', args.rowindex);
		                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		                                isSelectedOrg = true;
		                                selectedorganizationPartyId = row.partyId;
		                                editor.jqxDropDownButton('setContent', dropDownContent);
		                            });
		                      },
		                      geteditorvalue: function (row, cellvalue, editor) {
		                          // return the editor's value.
		                          if(!isSelectedOrg){
		                        	  selectedorganizationPartyId = cellvalue;
		                          }
		                    	  editor.jqxDropDownButton(\"close\");
		                          return selectedorganizationPartyId;
		                      }
					 },
					 { text: '${uiLabelMap.FormFieldTitle_ownerPartyId}', dataField: 'ownerPartyId',width: '150px', editable: true, columntype: 'template', filterable: false,
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
						 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	                            editor.append('<div id=\"jqxGridOwnerParty\"></div>');
	                            editor.jqxDropDownButton({width: '150'});
	                            // prepare the data
							    var sourceParty = { datafields: [
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
					   				$(\"#jqxGridOwnerParty\").jqxGrid('updatebounddata');
								},
								sort: function () {
					  				$(\"#jqxGridOwnerParty\").jqxGrid('updatebounddata');
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
				                            var filterListFields = \"\";
				                            for (var i = 0; i < data.filterscount; i++) {
				                                var filterValue = data[\"filtervalue\" + i];
				                                var filterCondition = data[\"filtercondition\" + i];
				                                var filterDataField = data[\"filterdatafield\" + i];
				                                var filterOperator = data[\"filteroperator\" + i];
				                                filterListFields += \"|OLBIUS|\" + filterDataField;
				                                filterListFields += \"|SUIBLO|\" + filterValue;
				                                filterListFields += \"|SUIBLO|\" + filterCondition;
				                                filterListFields += \"|SUIBLO|\" + filterOperator;
				                            }
				                            data.filterListFields = filterListFields;
				                        }
				                         data.$skip = data.pagenum * data.pagesize;
				                         data.$top = data.pagesize;
				                         data.$inlinecount = \"allpages\";
				                        return data;
				                    },
				                    loadError: function (xhr, status, error) {
					                    alert(error);
					                },
					                downloadComplete: function (data, status, xhr) {
					                        if (!sourceParty.totalRecords) {
					                            sourceParty.totalRecords = parseInt(data[\"odata.count\"]);
					                        }
					                }, 
					                beforeLoadComplete: function (records) {
					                	for (var i = 0; i < records.length; i++) {
					                		if(typeof(records[i])==\"object\"){
					                			for(var key in records[i]) {
					                				var value = records[i][key];
					                				if(value != null && typeof(value) == \"object\" && typeof(value) != null){
					                					var date = new Date(records[i][key][\"time\"]);
					                					records[i][key] = date;
					                				}
					                			}
					                		}
					                	}
					                }
							    });
					            $(\"#jqxGridOwnerParty\").jqxGrid({
					            	width:400,
					                source: dataAdapterParty,
					                filterable: true,
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
					                  { text: 'partyId', datafield: 'partyId'},
					                  { text: 'partyTypeId', datafield: 'partyTypeId'},
					                  { text: 'firstName', datafield: 'firstName'},
					                  { text: 'lastName', datafield: 'lastName'},
					                  { text: 'groupName', datafield: 'groupName'}
					                ]
					            });
					            isSelectedOwner = false;
					            $(\"#jqxGridOwnerParty\").on('rowselect', function (event) {
		                                var args = event.args;
		                                var row = $(\"#jqxGridOwnerParty\").jqxGrid('getrowdata', args.rowindex);
		                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		                                selectedownerPartyId = row.partyId;
		                                isSelectedOwner = true;
		                                editor.jqxDropDownButton('setContent', dropDownContent);
		                            });
		                      },
		                      geteditorvalue: function (row, cellvalue, editor) {
		                          // return the editor's value.
		                          editor.jqxDropDownButton(\"close\");
		                          if(!isSelectedOwner){
		                        	  selectedownerPartyId = cellvalue;
		                          }
		                          return selectedownerPartyId;
		                      }
					 },
					 { text: '${uiLabelMap.FormFieldTitle_postToGlAccountId}', dataField: 'postToGlAccountId',width: '150px', editable: true, filterable: false, columntype: 'template',
						 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	                            editor.append('<div id=\"jqxGridGlAccount\"></div>');
	                            editor.jqxDropDownButton({width: '150'});
	                            // prepare the data
							    var sourceGlAcc = { datafields: [
							      { name: 'glAccountId', type: 'string' },
							      { name: 'accountName', type: 'string' },
							      { name: 'glAccountTypeId', type: 'string' },
							      { name: 'glAccountClassId', type: 'string' }
							    ],
								cache: false,
								root: 'results',
								datatype: 'json',
								
								beforeprocessing: function (data) {
									sourceGlAcc.totalrecords = data.TotalRows;
								},
								filter: function () {
					   				// update the grid and send a request to the server.
					   				$('#jqxGridGlAccount').jqxGrid('updatebounddata');
								},
								sort: function () {
					  				$('#jqxGridGlAccount').jqxGrid('updatebounddata');
								},
								sortcolumn: 'glAccountId',
	               				sortdirection: 'asc',
								type: 'POST',
								data: {
									noConditionFind: 'Y',
									conditionsFind: 'N',
								},
								pagesize:5,
								contentType: 'application/x-www-form-urlencoded',
								url: 'jqxGeneralServicer?sname=JQListGlAccount',
								};
							    var dataAdapterGlAcc = new $.jqx.dataAdapter(sourceGlAcc,
							    {
							    	formatData: function (data) {
								    	if (data.filterscount) {
				                            var filterListFields = \"\";
				                            for (var i = 0; i < data.filterscount; i++) {
				                                var filterValue = data[\"filtervalue\" + i];
				                                var filterCondition = data[\"filtercondition\" + i];
				                                var filterDataField = data[\"filterdatafield\" + i];
				                                var filterOperator = data[\"filteroperator\" + i];
				                                filterListFields += \"|OLBIUS|\" + filterDataField;
				                                filterListFields += \"|SUIBLO|\" + filterValue;
				                                filterListFields += \"|SUIBLO|\" + filterCondition;
				                                filterListFields += \"|SUIBLO|\" + filterOperator;
				                            }
				                            data.filterListFields = filterListFields;
				                        }
				                         data.$skip = data.pagenum * data.pagesize;
				                         data.$top = data.pagesize;
				                         data.$inlinecount = \"allpages\";
				                        return data;
				                    },
				                    loadError: function (xhr, status, error) {
					                    alert(error);
					                },
					                downloadComplete: function (data, status, xhr) {
					                        if (!sourceGlAcc.totalRecords) {
					                        	sourceGlAcc.totalRecords = parseInt(data[\"odata.count\"]);
					                        }
					                }, 
					                beforeLoadComplete: function (records) {
					                	for (var i = 0; i < records.length; i++) {
					                		if(typeof(records[i])==\"object\"){
					                			for(var key in records[i]) {
					                				var value = records[i][key];
					                				if(value != null && typeof(value) == \"object\" && typeof(value) != null){
					                					var date = new Date(records[i][key][\"time\"]);
					                					records[i][key] = date;
					                				}
					                			}
					                		}
					                	}
					                }
							    });
					            $('#jqxGridGlAccount').jqxGrid({
					            	width:400,
					                source: dataAdapterGlAcc,
					                filterable: true,
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
					                  { text: 'glAccountId', datafield: 'glAccountId'},
					                  { text: 'accountName', datafield: 'accountName'},
					                  { text: 'glAccountTypeId', datafield: 'glAccountTypeId'},
					                  { text: 'glAccountClassId', datafield: 'glAccountClassId'}
					                ]
					            });
					            isSelectedGlAcc = false;
					            $('#jqxGridGlAccount').on('rowselect', function (event) {
		                                var args = event.args;
		                                var row = $('#jqxGridGlAccount').jqxGrid('getrowdata', args.rowindex);
		                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['glAccountId'] +'</div>';
		                                isSelectedGlAcc = true;
		                                selectedGlAcc = row.glAccountId;
		                                editor.jqxDropDownButton('setContent', dropDownContent);
		                            });
		                      },
		                      geteditorvalue: function (row, cellvalue, editor) {
		                          // return the editor's value.
		                          editor.jqxDropDownButton(\"close\");
		                          if(!isSelectedGlAcc){
		                        	  selectedGlAcc = cellvalue;
		                          }
		                          return selectedGlAcc;
		                      }
		             },
					 { text: '${uiLabelMap.fromDate}', dataField: 'fromDate',width: '150px', filterable: false, cellsformat: 'dd/MM/yyyy', columntype: 'template',
		            	 createeditor: function(row, cellvalue, editor){
		            		 editor.jqxDateTimeInput({ width: '150px', height: '25px'});
		            	 }
					 },
					 { text: '${uiLabelMap.thruDate}', dataField: 'thruDate',width: '150px', filterable: false, cellsformat: 'dd/MM/yyyy', columntype: 'template',
		            	 createeditor: function(row, cellvalue, editor){
		            		 editor.jqxDateTimeInput({ width: '150px', height: '25px'});
		            	 }
					 },
					 { text: '${uiLabelMap.isRefundable}', dataField: 'isRefundable', filterable: false, width: '150px', columntype: 'template', hidden : true,
						 cellsrenderer: function(row, column, value){
							 for(i = 0; i < isRefundableData.length; i++){
								 if(isRefundableData[i].isRefundable == value){
									 return '<span title=' + value + '>' + isRefundableData[i].description + '</span>'
								 }
							 }
							 return ;
						 },
						 createeditor: function(row, cellvalue, editor){
							 editor.jqxDropDownList({width: '150px', source: isRefundableData, displayMember: 'isRefundable', valueMember: 'isRefundable'});
						 }
					 },
					 { text: '${uiLabelMap.replenishPaymentId}', dataField: 'replenishPaymentId', filterable: false, width: '150px',hidden : true },
					 { text: '${uiLabelMap.replenishLevel}', dataField: 'replenishLevel', filterable: false, width: '150px', hidden : true},
					 { text: '${uiLabelMap.FormFieldTitle_actualBalance}', dataField: 'actualBalance', filterable: false, width: '150px',},
					 { text: '${uiLabelMap.AccountingBillingAvailableBalance}', dataField: 'availableBalance', filterable: false, width: '150px'}
					"/>
<#assign dataField="[{ name: 'finAccountId', type: 'string' },
                 	{ name: 'finAccountTypeId', type: 'string' },
                 	{ name: 'statusId', type: 'string' },
					{ name: 'finAccountName', type: 'string' },
					{ name: 'finAccountCode', type: 'string' },
                 	{ name: 'finAccountPin', type: 'string' },
                 	{ name: 'currencyUomId', type: 'string' }, 
                 	{ name: 'organizationPartyId', type: 'string'},
                 	{ name: 'ownerPartyId', type: 'string'},
                 	{ name: 'postToGlAccountId', type: 'string'},
                 	{ name: 'fromDate', type: 'date'},
                 	{ name: 'thruDate', type: 'date'},
                 	{ name: 'isRefundable', type: 'string'},
                 	{ name: 'replenishPaymentId', type: 'string'},
                 	{ name: 'replenishLevel', type: 'number'},
                 	{ name: 'actualBalance', type: 'number'},
                 	{ name: 'availableBalance', type: 'number'}
		 		 	]"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupNewFinAccount" deleterow="true" editable="false" 
		 url="jqxGeneralServicer?sname=JQListFinAccount" id="jqxgrid" removeUrl="jqxGeneralServicer?sname=deleteFinAccount&jqaction=D" deleteColumn="finAccountId"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createFinAccount"
		 addColumns="finAccountTypeId;statusId;finAccountName;replenishPaymentId;replenishLevel(java.math.BigDecimal);currencyUomId;organizationPartyId;ownerPartyId;postToGlAccountId;isRefundable;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" addrefresh="true"
	/>
<#--=======================================================Create FinAccount Window===============================================================-->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupNewFinAccount" style="display:none;">
			<div id="windowHeaderNewFinAccount">
	            <span>
	               ${uiLabelMap.NewFinAccount}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewFinAccount">
		        <div class="basic-form form-horizontal" style="margin-top: 10px">
					<form name="formNewFinAccount" id="formNewFinAccount">	
						<div class="row-fluid" >
							<div class="span12">
								<div class='span6'>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_finAccountType}:</label>
										<div class="controls">
											<div id="finAccountTypeId"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.statusId}:</label>  
										<div class="controls">
											<div id="statusId"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_finAccountName}:</label>
										<div class="controls">
											<input id="finAccountName"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_finAccountCode}:</label>  
										<div class="controls">
											<div id="finAccountCode"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_finAccountPin}:</label>  
										<div class="controls">
											<div id="finAccountPin"></div>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.replenishPaymentId}:</label>  
										<div class="controls">
											<div id="replenishPaymentId">
											</div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.replenishLevel}:</label>  
										<div class="controls">
											<input id="replenishLevel">
											</input>
										</div>
									</div>
								</div>
								<div class='span6'>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.currencyUomId}:</label>  
										<div class="controls">
											<div id="currencyUomId"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.organizationPartyId}:</label>  
										<div class="controls">
											<div id="organizationPartyId">
												<div id="jqxOrgPartyId"></div>
											</div>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_ownerPartyId}:</label>  
										<div class="controls">
											<div id="ownerPartyId">
												<div id="jqxOwnerPartyId"></div>
											</div>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.FormFieldTitle_postToGlAccountId}:</label>  
										<div class="controls">
											<div id="postToGlAccountId">
												<div id="jqxGridGlAccount"></div>
											</div>
										</div>
									</div>
									
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.fromDate}:</label>  
										<div class="controls">
											<div id="fromDate">
											</div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.thruDate}:</label>  
										<div class="controls">
											<div id="thruDate">
											</div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.isRefundable}:</label>  
										<div class="controls">
											<div id="isRefundable">
											</div>
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
	</div>
</div>
<#--=======================================================/Create FinAccount Window===============================================================-->
<#--=======================================================JS Create FinAccount Window=============================================================-->
<script>
	var JQXAction = function(){};
	JQXAction.prototype.theme = 'olbius';
	JQXAction.prototype.initWindow = function(){
		$('#alterpopupNewFinAccount').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "95%", height: 450, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
	        initContent: function () {	        	
	        	//Create finAccountTypeId
	        	$('#finAccountTypeId').jqxDropDownList({source: finAccTypeData, valueMember: 'finAccountTypeId', displayMember: 'description'});
	        	
	        	//Create statusId
	        	$('#statusId').jqxDropDownList({source: statusData, valueMember: 'statusId', displayMember: 'description'});
	        	
	        	//Create finAccountName
	        	$('#finAccountName').jqxInput({width: 195});
	        	
	        	//Create replenishLevel
	        	$('#replenishLevel').jqxInput({width: 195});
	        	
	        	//Create finAccountCode
	        	$("#finAccountCode").jqxNumberInput({decimalDigits: 0, spinButtons: true});
	        	
	        	//Create finAccountPin
	        	$("#finAccountPin").jqxNumberInput({decimalDigits: 0, spinButtons: true});
	        	
	        	//Create currencyUomId
	        	$('#currencyUomId').jqxDropDownList({source: uomData, valueMember: 'currencyUomId', displayMember: 'description'});
	        	
	        	//Create isRefundable
	        	$('#isRefundable').jqxDropDownList({source: isRefundableData, valueMember: 'isRefundable', displayMember: 'description'});
	        	
	        	//Create replenishPaymentId
	        	$("#replenishPaymentId").jqxDropDownList({ theme: theme, source: dataPM, displayMember: "description", valueMember: "paymentMethodId"});
	        	
	        	//Create organizationPartyId
            	var sourceGroup =
            	{
            			datafields:
            				[
            				 { name: 'partyId', type: 'string' },
            				 { name: 'groupName', type: 'string' },
            				],
            			cache: false,
            			root: 'results',
            			datatype: "json",
            			updaterow: function (rowid, rowdata) {
            				// synchronize with the server - send update command   
            			},
            			beforeprocessing: function (data) {
            				sourceGroup.totalrecords = data.TotalRows;
            			},
            			filter: function () {
            				// update the grid and send a request to the server.
            				$("#jqxOrgPartyId").jqxGrid('updatebounddata');
            			},
            			pager: function (pagenum, pagesize, oldpagenum) {
            				// callback called when a page or page size is changed.
            			},
            			sort: function () {
            				$("#jqxOrgPartyId").jqxGrid('updatebounddata');
            			},
            			sortcolumn: 'partyId',
            			sortdirection: 'asc',
            			type: 'POST',
            			data: {
            				noConditionFind: 'Y',
            				conditionsFind: 'N',
            			},
            			pagesize:15,
            			contentType: 'application/x-www-form-urlencoded',
            			url: 'jqxGeneralServicer?sname=getListPartyGroups',
            	};
            	var dataAdapterGroup = new $.jqx.dataAdapter(sourceGroup,{
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
    		            }else{
    		            	data.filterListFields = null;
    		            }
    		            return data;
    		        },
    		        loadError: function (xhr, status, error) {
    		            alert(error);
    		        },
    		        downloadComplete: function (data, status, xhr) {
    		                if (!sourceGroup.totalRecords) {
    		                	sourceGroup.totalRecords = parseInt(data['odata.count']);
    		                }
    		        }
    		    });
            	$("#organizationPartyId").jqxDropDownButton({ width: 200, height: 25});
            	$("#jqxOrgPartyId").jqxGrid({
            		source: dataAdapterGroup,
            		filterable: true,
            		showfilterrow: true,
            		virtualmode: true, 
            		sortable:true,
            		theme: theme,
            		editable: false,
            		autoheight:true,
            		pageable: true,
            		width: 800,
            		rendergridrows: function(obj)
            		{
            			return obj.data;
            		},
            	columns: [
            	  { text: '${uiLabelMap.CommonId}', datafield: 'partyId', filtertype: 'input', width: 150},
            	  { text: '${uiLabelMap.groupName}', datafield: 'groupName', filtertype: 'input'},
            	]
            	});
	        	
            	//Create ownerPartyId
            	var sourceGroup =
            	{
            			datafields:
            				[
            				 { name: 'partyId', type: 'string' },
            				 { name: 'groupName', type: 'string' },
            				],
            			cache: false,
            			root: 'results',
            			datatype: "json",
            			updaterow: function (rowid, rowdata) {
            				// synchronize with the server - send update command   
            			},
            			beforeprocessing: function (data) {
            				sourceGroup.totalrecords = data.TotalRows;
            			},
            			filter: function () {
            				// update the grid and send a request to the server.
            				$("#jqxOwnerPartyId").jqxGrid('updatebounddata');
            			},
            			pager: function (pagenum, pagesize, oldpagenum) {
            				// callback called when a page or page size is changed.
            			},
            			sort: function () {
            				$("#jqxOwnerPartyId").jqxGrid('updatebounddata');
            			},
            			sortcolumn: 'partyId',
            			sortdirection: 'asc',
            			type: 'POST',
            			data: {
            				noConditionFind: 'Y',
            				conditionsFind: 'N',
            			},
            			pagesize:15,
            			contentType: 'application/x-www-form-urlencoded',
            			url: 'jqxGeneralServicer?sname=getListPartyGroups',
            	};
            	var dataAdapterGroup = new $.jqx.dataAdapter(sourceGroup,{
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
    		            }else{
    		            	data.filterListFields = null;
    		            }
    		            return data;
    		        },
    		        loadError: function (xhr, status, error) {
    		            alert(error);
    		        },
    		        downloadComplete: function (data, status, xhr) {
    		                if (!sourceGroup.totalRecords) {
    		                	sourceGroup.totalRecords = parseInt(data['odata.count']);
    		                }
    		        }
    		    });
            	$("#ownerPartyId").jqxDropDownButton({ width: 200, height: 25});
            	$("#jqxOwnerPartyId").jqxGrid({
            		source: dataAdapterGroup,
            		filterable: true,
            		showfilterrow: true,
            		virtualmode: true, 
            		sortable:true,
            		theme: theme,
            		editable: false,
            		autoheight:true,
            		pageable: true,
            		width: 800,
            		rendergridrows: function(obj)
            		{
            			return obj.data;
            		},
            	columns: [
            	  { text: '${uiLabelMap.CommonId}', datafield: 'partyId', filtertype: 'input', width: 150},
            	  { text: '${uiLabelMap.groupName}', datafield: 'groupName', filtertype: 'input'},
            	]
            	});
            	
            	// prepare the data
			    var sourceGlAcc = { 
				    datafields: [
				      { name: 'glAccountId', type: 'string' },
				      { name: 'accountName', type: 'string' },
				      { name: 'glAccountTypeId', type: 'string' },
				      { name: 'glAccountClassId', type: 'string' }
				    ],
					cache: false,
					root: 'results',
					datatype: 'json',
					
					beforeprocessing: function (data) {
						sourceGlAcc.totalrecords = data.TotalRows;
					},
					filter: function () {
		   				// update the grid and send a request to the server.
		   				$('#jqxGridGlAccount').jqxGrid('updatebounddata');
					},
					sort: function () {
		  				$('#jqxGridGlAccount').jqxGrid('updatebounddata');
					},
					sortcolumn: 'glAccountId',
	   				sortdirection: 'asc',
					type: 'POST',
					data: {
						noConditionFind: 'Y',
						conditionsFind: 'N',
					},
					pagesize:5,
					contentType: 'application/x-www-form-urlencoded',
					url: 'jqxGeneralServicer?sname=JQListGlAccount',
					};
			    var dataAdapterGlAcc = new $.jqx.dataAdapter(sourceGlAcc,
			    {
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
                         data.$skip = data.pagenum * data.pagesize;
                         data.$top = data.pagesize;
                         data.$inlinecount = "allpages";
                        return data;
                    },
                    loadError: function (xhr, status, error) {
	                    alert(error);
	                },
	                downloadComplete: function (data, status, xhr) {
	                        if (!sourceGlAcc.totalRecords) {
	                        	sourceGlAcc.totalRecords = parseInt(data["odata.count"]);
	                        }
	                }, 
			    });
            	$("#postToGlAccountId").jqxDropDownButton({ width: 200, height: 25});
	            $('#jqxGridGlAccount').jqxGrid({
	            	width:400,
	                source: dataAdapterGlAcc,
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
	                  { text: 'glAccountId', datafield: 'glAccountId'},
	                  { text: 'accountName', datafield: 'accountName'},
	                  { text: 'glAccountTypeId', datafield: 'glAccountTypeId'},
	                  { text: 'glAccountClassId', datafield: 'glAccountClassId'}
	                ]
	            });
            	
	        	//fromDate
	        	$('#fromDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
	        	
	        	//thruDate
	        	$('#thruDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
	        }
	    });
	};
	
	JQXAction.prototype.bindEvent = function(){
		$("#jqxOrgPartyId").on('rowselect', function (event) {
    		var args = event.args;
    		var row = $("#jqxOrgPartyId").jqxGrid('getrowdata', args.rowindex);
    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
    		$('#organizationPartyId').jqxDropDownButton('setContent', dropDownContent);
    		$('#organizationPartyId').jqxDropDownButton('close');
    	});
		
		$('#jqxGridGlAccount').on('rowselect', function (event) {
            var args = event.args;
            var row = $('#jqxGridGlAccount').jqxGrid('getrowdata', args.rowindex);
            var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['glAccountId'] +'</div>';
            $('#postToGlAccountId').jqxDropDownButton('setContent', dropDownContent);
    		$('#postToGlAccountId').jqxDropDownButton('close');
		});
		
		$("#jqxOwnerPartyId").on('rowselect', function (event) {
    		var args = event.args;
    		var row = $("#jqxOwnerPartyId").jqxGrid('getrowdata', args.rowindex);
    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
    		$('#ownerPartyId').jqxDropDownButton('setContent', dropDownContent);
    		$('#ownerPartyId').jqxDropDownButton('close');
    	});
		
		$("#formNewFinAccount").on('validationSuccess', function (event) {
			var submitData = {};
			submitData['finAccountTypeId'] = $('#finAccountTypeId').val();
			submitData['statusId'] = $('#statusId').val();
			submitData['finAccountName'] = $('#finAccountName').val();
			submitData['replenishPaymentId'] = $('#replenishPaymentId').val();
			submitData['replenishLevel'] = $('#replenishLevel').val();
			submitData['finAccountCode'] = $('#finAccountCode').val();
			submitData['finAccountPin'] = $('#finAccountPin').val();
			submitData['currencyUomId'] = $('#currencyUomId').val();
			submitData['isRefundable'] = $('#isRefundable').val();
			submitData['organizationPartyId'] = $('#organizationPartyId').val();
			submitData['ownerPartyId'] = $('#ownerPartyId').val();
			submitData['postToGlAccountId'] = $('#postToGlAccountId').val();
			submitData['thruDate'] = $("#thruDate").jqxDateTimeInput('getDate').getTime();
			submitData['fromDate'] = $("#fromDate").jqxDateTimeInput('getDate').getTime();
			$("#jqxgrid").jqxGrid('addRow', null, submitData, "first");
		    $("#jqxgrid").jqxGrid('clearSelection');                        
		    $("#jqxgrid").jqxGrid('selectRow', 0);  
		    $("#alterpopupNewFinAccount").jqxWindow('close');
		 });
		
		$('#alterSave').on('click', function(){
			$("#formNewFinAccount").jqxValidator('validate');
		});
	};
	
	JQXAction.prototype.initValidator = function(){
		$('#formNewFinAccount').jqxValidator({
	        rules: []
	    });
	};
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction.bindEvent();
		jqxAction.initValidator();
	});
</script>
<#--=======================================================/JS Create FinAccount Window=============================================================-->