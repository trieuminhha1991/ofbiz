<script>	
	<#assign glAccountTypes = delegator.findList("GlAccountType", null, null, null, null, false) />
	var glAccountTypesData =  new Array();
	<#list glAccountTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['glAccountTypeId'] = "${item.glAccountTypeId?if_exists}";
		row['description'] = "${description}";
		glAccountTypesData[${item_index}] = row;
	</#list>
	
	<#assign glAccounts = delegator.findList("GlAccount", null, null, null, null, false) />
	var glAccountsData =  new Array();
	<#list glAccounts as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.accountName?if_exists)>
		row['glAccountId'] = "${item.glAccountId?if_exists}";
		row['description'] = "${description}";
		glAccountsData[${item_index}] = row;
	</#list>
	
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, false) />
	var statusItemsData =  new Array();
	<#list statusItems as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['statusId'] = "${item.statusId?if_exists}";
		row['description'] = "${description}";
		statusItemsData[${item_index}] = row;
	</#list>
	
	var debitCreditFlagData =  new Array();
	var row1 = {};
	row1['description'] = '${uiLabelMap.FormFieldTitle_credit}';
	row1['debitCreditFlag'] = 'C';
	debitCreditFlagData[0] = row1;
	
	var row2 = {};
	row2['description'] = '${uiLabelMap.FormFieldTitle_debit}';
	row2['debitCreditFlag'] = 'D';
	debitCreditFlagData[1] = row2;
	
	var summariesData =  new Array();
	var row1 = {};
	row1['description'] = '${uiLabelMap.FormFieldTitle_Yes}';
	row1['isSummary'] = 'Y';
	summariesData[0] = row1;
	
	var row2 = {};
	row2['description'] = '${uiLabelMap.FormFieldTitle_No}';
	row2['isSummary'] = 'N';
	summariesData[1] = row2;
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
	var uomsData =  new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists + "-" + item.abbreviation?if_exists)>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${description}";
		uomsData[${item_index}] = row;
	</#list>
	
	<#assign enums = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "CONVERSION_PURPOSE"), null, null, null, false) />
	var enumsData =  new Array();
	<#list enums as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['purposeEnumId'] = "${item.enumId?if_exists}";
		row['description'] = "${description}";
		enumsData[${item_index}] = row;
	</#list>
</script>
<#assign dataField="[   { name: 'acctgTransEntrySeqId', type: 'string' },
                 		{ name: 'glAccountTypeId', type: 'string' },
                 		{ name: 'glAccountId', type: 'string' }, 
                 		{ name: 'partyId', type: 'string'},                                             
                 		{ name: 'productId', type: 'string' },
 		 		 		{ name: 'reconcileStatusId', type: 'string' },
		 		 		{ name: 'debitCreditFlag', type: 'string' },
		 				{ name: 'description', type: 'string' },
						{ name: 'voucherRef', type: 'string' },
		 				{ name: 'isSummary', type: 'string' },
		 				{ name: 'amount', type: 'number' },
		 				{ name: 'origAmount', type: 'number' },
		 		 	]" />

<#assign columnlist="{ text: '${uiLabelMap.acctgTransEntrySeqId}', dataField: 'acctgTransEntrySeqId', width: 200, editable: true },
                     { text: '${uiLabelMap.glAccountTypeId}', dataField: 'glAccountTypeId', width: 150, editable: true, columntype: 'dropdownlist',
						createeditor: function (row, column, editor) {
							editor.jqxDropDownList({source: glAccountTypesData, displayMember:\"glAccountTypeId\", valueMember: \"glAccountTypeId\",
								renderer: function (index, label, value) {
									var datarecord = glAccountTypesData[index];
									return datarecord.description;
								}
							});
						}
                     },
                     { text: '${uiLabelMap.glAccountId}', dataField: 'glAccountId', width: 200, editable: true, columntype: 'dropdownlist',
						createeditor: function (row, column, editor) {
							editor.jqxDropDownList({source: glAccountsData, displayMember:\"glAccountId\", valueMember: \"glAccountId\",
								renderer: function (index, label, value) {
									var datarecord = glAccountsData[index];
									return datarecord.description;
								}
							});
						} 					
                     },                     
					 { text: '${uiLabelMap.partyId}', datafield: 'partyId', width: 200, editable: true, columntype: 'template',
                     	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxGridParty\"></div>');
                            editor.jqxDropDownButton();
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
							updaterow: function (rowid, rowdata) {
							// synchronize with the server - send update command   
							},
							beforeprocessing: function (data) {
				    			sourceParty.totalrecords = data.TotalRows;
							},
							filter: function () {
				   				// update the grid and send a request to the server.
				   				$(\"#jqxGridParty\").jqxGrid('updatebounddata');
							},
							pager: function (pagenum, pagesize, oldpagenum) {
				  				// callback called when a page or page size is changed.
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
				            $(\"#jqxGridParty\").on('rowselect', function (event) {
	                                var args = event.args;
	                                var row = $(\"#jqxGridParty\").jqxGrid('getrowdata', args.rowindex);
	                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
	                                selectedParty = row.partyId;
	                                editor.jqxDropDownButton('setContent', dropDownContent);
	                            });
	                      },
	                        geteditorvalue: function (row, cellvalue, editor) {
	                            // return the editor's value.
	                            editor.jqxDropDownButton(\"close\");
	                            return selectedParty;
	                        }
                      },                          
		     		  { text: '${uiLabelMap.productId}', dataField: 'productId', width: 200, editable: true , columntype: 'template',
                     	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxGridProd\"></div>');
                            editor.jqxDropDownButton();
                            // prepare the data
						    var sourceProd = { datafields: [
						      { name: 'productId', type: 'string' },
						      { name: 'brandName', type: 'string' },
						      { name: 'internalName', type: 'string' },
						      { name: 'productTypeId', type: 'string' }
						    ],
							cache: false,
							root: 'results',
							datatype: 'json',
							updaterow: function (rowid, rowdata) {
							// synchronize with the server - send update command   
							},
							beforeprocessing: function (data) {
				    			sourceProd.totalrecords = data.TotalRows;
							},
							filter: function () {
				   				// update the grid and send a request to the server.
				   				$(\"#jqxGridProd\").jqxGrid('updatebounddata');
							},
							pager: function (pagenum, pagesize, oldpagenum) {
				  				// callback called when a page or page size is changed.
							},
							sort: function () {
				  				$(\"#jqxGridProd\").jqxGrid('updatebounddata');
							},
							sortcolumn: 'productId',
               				sortdirection: 'asc',
							type: 'POST',
							data: {
								noConditionFind: 'Y',
								conditionsFind: 'N',
							},
							pagesize:5,
							contentType: 'application/x-www-form-urlencoded',
							url: 'jqxGeneralServicer?sname=getListProduct',
							};
						    var dataAdapterProd = new $.jqx.dataAdapter(sourceProd,
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
				                       if (!sourceProd.totalRecords) {
				                            sourceProd.totalRecords = parseInt(data[\"odata.count\"]);
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
				            $(\"#jqxGridProd\").jqxGrid({
				            	width:400,
				                source: dataAdapterProd,
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
				                  { text: 'productId', datafield: 'productId'},
				                  { text: 'brandName', datafield: 'brandName'},
				                  { text: 'internalName', datafield: 'internalName'},
				                  { text: 'productTypeId', datafield: 'productTypeId'}
				                ]
				            });
				            $(\"#jqxGridProd\").on('rowselect', function (event) {
	                                var args = event.args;
	                                var row = $(\"#jqxGridProd\").jqxGrid('getrowdata', args.rowindex);
	                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
	                                selectedPro = row.productId;
	                                editor.jqxDropDownButton('setContent', dropDownContent);
	                            });
	                      },
	                        geteditorvalue: function (row, cellvalue, editor) {
	                            // return the editor's value.
	                            editor.jqxDropDownButton(\"close\");
	                            return selectedPro;
	                        }
                      },
		             { text: '${uiLabelMap.reconcileStatusId}', dataField: 'reconcileStatusId', width: 200, editable: true , columntype: 'dropdownlist',
						createeditor: function (row, column, editor) {
							editor.jqxDropDownList({source: statusItemsData, displayMember:\"statusId\", valueMember: \"statusId\",
								renderer: function (index, label, value) {
									var datarecord = statusItemsData[index];
									return datarecord.description;
								}
							});
						} 					
                     },	   		     
		             { text: '${uiLabelMap.debitCreditFlag}', dataField: 'debitCreditFlag', width: 250, editable: true , columntype: 'dropdownlist',
						createeditor: function (row, column, editor) {
							editor.jqxDropDownList({source: debitCreditFlagData, displayMember:\"debitCreditFlag\", valueMember: \"debitCreditFlag\",
								renderer: function (index, label, value) {
									var datarecord = debitCreditFlagData[index];
									return datarecord.description;
								}
							});
						} 					
                     },	
		             { text: '${uiLabelMap.description}', dataField: 'description', width: 150, editable: false },
		             { text: '${uiLabelMap.voucherRef}', dataField: 'voucherRef', width: 150, editable: false },
		             { text: '${uiLabelMap.isSummary}', dataField: 'isSummary', width: 150, editable: false },
		             { text: '${uiLabelMap.origAmount}', dataField: 'origAmount', width: 150, editable: false, cellsformat: 'c2' },
		             { text: '${uiLabelMap.amount}', dataField: 'amount', width: 150, cellsformat: 'c2', editable: false}
                      "/>
<style type="text/css">
	#jqxgrid2 .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgrid2 .jqx-grid-header-olbius{
		height:25px !important;
	}
	#jqxgrid2{
		width: calc(100% - 2px) !important;
	}
	#jqxgrid{
		width: calc(100% - 2px) !important;
	}
</style>			 		 
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist deleterow="false" clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
	 url="jqxGeneralServicer?sname=JQListAcctgTransAndEntries&acctgTransId=${parameters.acctgTransId}&organizationPartyId=${parameters.organizationPartyId}"	 
	 updateUrl="jqxGeneralServicer?sname=updateAcctgTransEntry&jqaction=U&acctgTransId=${parameters.acctgTransId}&organizationPartyId=${parameters.organizationPartyId}" 
	 editColumns="organizationPartyId[${parameters.organizationPartyId}];acctgTransId[${parameters.acctgTransId}];acctgTransEntrySeqId;glAccountTypeId;glAccountId;partyId;productId;reconcileStatusId;debitCreditFlag"	 
	 removeUrl="jqxGeneralServicer?sname=deleteAcctgTransEntry&jqaction=D&acctgTransId=${parameters.acctgTransId}&organizationPartyId=${parameters.organizationPartyId}"
	 deleteColumn="acctgTransId[${parameters.acctgTransId}];acctgTransEntrySeqId"
	 createUrl="jqxGeneralServicer?sname=createAcctgTransEntry&jqaction=C"	 
     addColumns="organizationPartyId[${parameters.organizationPartyId}];acctgTransEntryTypeId[_NA_];acctgTransId[${parameters.acctgTransId}];glAccountTypeId;glAccountId;debitCreditFlag;partyId;origAmount(java.math.BigDecimal);origCurrencyUomId;purposeEnumId;voucherRef;productId;reconcileStatusId;settlementTermId;isSummary;description"	 
/>

<div id="alterpopupWindow">
<div>${uiLabelMap.accCreateNew}</div>
<div style="overflow: hidden;">
    <table>
	 	<tr>	 	
		<td align="right">
			${uiLabelMap.glAccountTypeId}
		</td>
		<td align="left">
			<div id="glAccountTypeIdAdd"></div>
			</td>
	 	</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.glAccountId}:</td>
 			<td align="left">
 				<div id="glAccountIdAdd">
 				</div>
 			</td>
	 	</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.debitCreditFlag}:</td>
 			<td align="left">
 				<div id="debitCreditFlagAdd">
 				</div>
 			</td>
	 	</tr>
	 	<tr>
 			<td align="right">${uiLabelMap.partyId}:</td>
 			<td align="left">
					<div id="partyIdAdd">
						<div id="jqxGridPartyId"/>
					</div>
				</td>
			</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.origAmount}:</td>
 			<td align="left">
 				<input id="origAmountAdd">
 				</input>
 			</td>
	 	</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.origCurrencyUomId}:</td>
 			<td align="left">
 				<div id="origCurrencyUomIdAdd">
 				</div>
 			</td>
	 	</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.purposeEnumId}:</td>
 			<td align="left">
 				<div id="purposeEnumIdAdd">
 				</div>
 			</td>
	 	</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.voucherRef}:</td>
 			<td align="left">
 				<input id="voucherRefAdd">
 				</input>
 			</td>
	 	</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.productId}:</td>
 			<td align="left">
 				<div id="productIdAdd">
 					<div id="jqxGridProd"/>
 				</div>
 			</td>
	 	</tr>
	 	<tr>
	 		<td align="right">${uiLabelMap.reconcileStatusId}:</td>
 			<td align="left">
 				<div id="reconcileStatusIdAdd">
 				</div>
 			</td>
	 	</tr>
	 	<tr>
 			<td align="right">${uiLabelMap.settlementTermId}:</td>
 			<td align="left">
				<input id="settlementTermIdAdd">
				</input>
			</td>
		</tr>
		<tr>
			<td align="right">${uiLabelMap.isSummary}:</td>
			<td align="left">
				<div id="isSummaryAdd">
				</div>
			</td>
		</tr>
		<tr>
			<td align="right">${uiLabelMap.description}:</td>
			<td align="left">
				<input id="descriptionAdd">
				</input>
			</td>
		</tr>
        <tr>
            <td align="right"></td>
            <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
        </tr>
    </table>
</div>
</div>
<script type="text/javascript">
//Create theme
$.jqx.theme = 'olbius';  
theme = $.jqx.theme;

$("#alterpopupWindow").jqxWindow({
	maxWidth: 800, minWidth: 600, minHeight: 600, maxHeight: 1000, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
});
$("#alterCancel").jqxButton();
$("#alterSave").jqxButton();

//Create glAccountTypeIdAdd
$("#glAccountTypeIdAdd").jqxDropDownList({ theme: theme, source: glAccountTypesData, displayMember: "description", valueMember: "glAccountTypeId", selectedIndex: 0, width: '200', height: '25'});

//Create glAccountIdAdd
$("#glAccountIdAdd").jqxDropDownList({ theme: theme, source: glAccountsData, displayMember: "description", valueMember: "glAccountId", selectedIndex: 0, width: '200', height: '25'});

//Create debitCreditFlagAdd
$("#debitCreditFlagAdd").jqxDropDownList({ theme: theme, source: debitCreditFlagData, displayMember: "description", valueMember: "debitCreditFlag", selectedIndex: 0, width: '200', height: '25'});

//Create origAmountAdd
$("#origAmountAdd").jqxInput({width:195});

//Create origCurrencyUomId
$("#origCurrencyUomIdAdd").jqxDropDownList({ theme: theme, source: uomsData, displayMember: "description", valueMember: "uomId", selectedIndex: 0, width: '200', height: '25'});

//Create origCurrencyUomId
$("#purposeEnumIdAdd").jqxDropDownList({ theme: theme, source: enumsData, displayMember: "description", valueMember: "purposeEnumId", selectedIndex: 0, width: '200', height: '25'});

//Create voucherRefAdd
$("#voucherRefAdd").jqxInput({width:195});

//Create statusItemsData
$("#reconcileStatusIdAdd").jqxDropDownList({ theme: theme, source: statusItemsData, displayMember: "description", valueMember: "statusId", selectedIndex: 0, width: '200', height: '25'});

//Create settlementTermIdAdd
$("#settlementTermIdAdd").jqxInput({width:195});

//Create settlementTermIdAdd
$("#isSummaryAdd").jqxDropDownList({ theme: theme, source: summariesData, displayMember: "description", valueMember: "isSummary", selectedIndex: 0, width: '200', height: '25'});

//Create descriptionAdd
$("#descriptionAdd").jqxInput({width:195});

//Party Grid
var sourceP =
{
		datafields:
			[
			 { name: 'partyId', type: 'string' },
			 { name: 'partyTypeId', type: 'string' },
			 { name: 'firstName', type: 'string' },
			 { name: 'lastName', type: 'string' },
			 { name: 'groupName', type: 'string' }
			],
		cache: false,
		root: 'results',
		datatype: "json",
		updaterow: function (rowid, rowdata) {
			// synchronize with the server - send update command   
		},
		beforeprocessing: function (data) {
			sourceP.totalrecords = data.TotalRows;
		},
		filter: function () {
			// update the grid and send a request to the server.
			$("#jqxGridPartyId").jqxGrid('updatebounddata');
		},
		pager: function (pagenum, pagesize, oldpagenum) {
			// callback called when a page or page size is changed.
		},
		sort: function () {
			$("#jqxGridPartyId").jqxGrid('updatebounddata');
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
var dataAdapterP = new $.jqx.dataAdapter(sourceP);
$("#partyIdAdd").jqxDropDownButton({ width: 200, height: 25});
$("#jqxGridPartyId").jqxGrid({
	source: dataAdapterP,
	filterable: false,
	virtualmode: true, 
	sortable:true,
	theme: theme,
	editable: false,
	autoheight:true,
	pageable: true,
	rendergridrows: function(obj)
	{
		return obj.data;
	},
columns: [
  { text: '${uiLabelMap.partyId}', datafield: 'partyId'},
  { text: '${uiLabelMap.partyTypeId}', datafield: 'partyTypeId'},
  { text: '${uiLabelMap.firstName}', datafield: 'firstName'},
  { text: '${uiLabelMap.lastName}', datafield: 'lastName'},
  { text: '${uiLabelMap.groupName}', datafield: 'groupName'}
]
});
$("#jqxGridPartyId").on('rowselect', function (event) {
	var args = event.args;
	var row = $("#jqxGridPartyId").jqxGrid('getrowdata', args.rowindex);
	var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
	$('#partyIdAdd').jqxDropDownButton('setContent', dropDownContent);
});

//Product Grid
var sourceProd =
	{	
		datafields:
			[
			 { name: 'productId', type: 'string' },
			 { name: 'brandName', type: 'string' },
			 { name: 'internalName', type: 'string' },
			 { name: 'productTypeId', type: 'string' }
			],
		cache: false,
		root: 'results',
		datatype: "json",
		updaterow: function (rowid, rowdata) {
			// synchronize with the server - send update command   
		},
		beforeprocessing: function (data) {
			sourceProd.totalrecords = data.TotalRows;
		},
		filter: function () {
			// update the grid and send a request to the server.
			$("#jqxGridProd").jqxGrid('updatebounddata');
		},
		pager: function (pagenum, pagesize, oldpagenum) {
			// callback called when a page or page size is changed.
		},
		sort: function () {
			$("#jqxGridProd").jqxGrid('updatebounddata');
		},
		sortcolumn: 'productId',
		sortdirection: 'asc',
		type: 'POST',
		data: {
			noConditionFind: 'Y',
			conditionsFind: 'N',
		},
		pagesize:5,
		contentType: 'application/x-www-form-urlencoded',
		url: 'jqxGeneralServicer?sname=getListProduct',
	};
var dataAdapterProd = new $.jqx.dataAdapter(sourceProd);
$("#productIdAdd").jqxDropDownButton({ width: 200, height: 25});
$("#jqxGridProd").jqxGrid({
	source: dataAdapterProd,
	filterable: false,
	virtualmode: true, 
	sortable:true,
	theme: theme,
	editable: false,
	autoheight:true,
	pageable: true,
	rendergridrows: function(obj)
	{
		return obj.data;
	},
	columns: [
	          { text: '${uiLabelMap.productId}', datafield: 'productId' },
			  { text: '${uiLabelMap.brandName}', datafield: 'brandName' },
			  { text: '${uiLabelMap.internalName}', datafield: 'internalName' },
			  { text: '${uiLabelMap.productTypeId}', datafield: 'productTypeId' }
	         ]
		});
$("#jqxGridProd").on('rowselect', function (event) {
	var args = event.args;
	var row = $("#jqxGridProd").jqxGrid('getrowdata', args.rowindex);
	var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
	$('#productIdAdd').jqxDropDownButton('setContent', dropDownContent);
});
// update the edited row when the user clicks the 'Save' button.
$("#alterSave").click(function () {
	var row;
    row = { 
    		glAccountTypeId:$('#glAccountTypeIdAdd').val(), 
    		glAccountId:$('#glAccountIdAdd').val(),
    		debitCreditFlag:$('#debitCreditFlagAdd').val(),
    		debitCreditFlag:$('#debitCreditFlagAdd').val(),
    		debitCreditFlag:$('#debitCreditFlagAdd').val(),
    		partyId:$('#partyIdAdd').val(),
    		origAmount:$('#origAmountAdd').val(),
    		origCurrencyUomId:$('#origCurrencyUomIdAdd').val(),
    		purposeEnumId:$('#purposeEnumIdAdd').val(),
    		voucherRef:$('#voucherRefAdd').val(),
    		productId:$('#productIdAdd').val(),
    		reconcileStatusId:$('#reconcileStatusIdAdd').val(),
    		settlementTermId:$('#settlementTermIdAdd').val(),
    		isSummary:$('#isSummaryAdd').val(),
    		description:$('#descriptionAdd').val()
    	  };
   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
    // select the first row and clear the selection.
    $("#jqxgrid").jqxGrid('clearSelection');                        
    $("#jqxgrid").jqxGrid('selectRow', 0);  
    $("#alterpopupWindow").jqxWindow('close');
});
</script>