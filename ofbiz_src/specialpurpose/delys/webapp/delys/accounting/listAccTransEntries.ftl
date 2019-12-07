<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<script>
	<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, false) />
	var acctgTransTypesData =  [<#list acctgTransTypes as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>'acctgTransTypeId' : "${item.acctgTransTypeId?if_exists}",'description' : "${description}"},</#list>];
	<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, false) />
	var glFiscalTypesData =  [<#list glFiscalTypes as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>'glFiscalTypeId' : "${item.glFiscalTypeId?if_exists}",'description' : "${description}"	},</#list>];
	<#assign roleTypes = delegator.findList("RoleType", null, null, null, null, false) />
	var roleTypesData =  [<#list roleTypes as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>'roleTypeId' : "${item.roleTypeId?if_exists}",'description' : "${description}"},</#list>];	
	<#assign glJournals = delegator.findList("GlJournal", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	var glJournalsData =  [<#list glJournals as item>{<#assign description = StringUtil.wrapString(item.get("glJournalName", locale)?if_exists + "[" + item.glJournalId?if_exists +"]")>'glJournalId' : "${item.glJournalId?if_exists}",'description' : "${description?if_exists}"	},</#list>];
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, false) />
	var statusItemsData =  [<#list statusItems as item>{<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />'statusId' : "${item.statusId?if_exists}",'description' : "${description?if_exists}"},</#list>		];
	var isPostedData = [{isPosted : 'Y',description : 'Yes'},{isPosted : 'N',description : 'No'}];
	<#assign listCurrency = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "CURRENCY_MEASURE")), null, null, null, false) />
	var currency = [<#list listCurrency as item>{uomId : "${item.uomId}",description : "${StringUtil.wrapString(item.uomId + ' : '+ item.get("description", locale)?default(""))}"},</#list>];
	<#assign purpose = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("enumTypeId", "CONVERSION_PURPOSE")), null, null, null, false) />
	var purpose = [<#list purpose as item>{enumId : "${item.enumId}",description : "${StringUtil.wrapString(item.enumId + ' : '+ item.get("description", locale)?default(""))}"},</#list>];
	<#assign glAccountOACs = delegator.findList("GlAccountOrganizationAndClass", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("organizationPartyId", "company"), null, null, null, false) />
	var glAccountOACsData =  [<#list glAccountOACs as item>{<#assign description = StringUtil.wrapString(item.accountCode?if_exists + " - " + item.get("accountName", locale)?if_exists + "[" + item.glAccountId?if_exists +"]")>'glAccountId' : "${item.glAccountId?if_exists}",'description' : "${description?if_exists}"},</#list>];
	<#assign fixedAssetTypeList = delegator.findList("FixedAssetType",  null, null, null, null, false) />
	<#assign glAccountTypes = delegator.findList("GlAccountType", null, null, null, null, true)/>
	<#assign glAccountClasses = delegator.findList("GlAccountClass", null, null, null, null, true)/>	
	var glAccountTypes = [
		<#list glAccountTypes as type>
			{
				glAccountTypeId : "${type.glAccountTypeId}",
				description: "${StringUtil.wrapString(type.description?default(""))}"	
			},
		</#list>
	];
	var glAccountClasses = [
		<#list glAccountClasses as type>
			{
				glAccountClassId : "${type.glAccountClassId}",
				description: "${StringUtil.wrapString(type.description?default(""))}"	
			},
		</#list>
	];	
	var debit = "${StringUtil.wrapString(uiLabelMap.DEBIT)}";
	var credit = "${StringUtil.wrapString(uiLabelMap.CREDIT)}";
	var debitCreditFlagData = [{value : "C", description: credit},{value : "D", description: debit}];
	var currentAccTg = "${parameters.acctgTransId?if_exists}";
</script>
<#include "initGeneralDropdown.ftl"/>
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
                     { text: '${uiLabelMap.glAccountTypeId}', dataField: 'glAccountTypeId', width: 300, editable: true, columntype: 'dropdownlist',
						createeditor: function (row, column, editor) {
							editor.jqxDropDownList({source: glAccountTypes, displayMember:\"glAccountTypeId\", valueMember: \"glAccountTypeId\",
								renderer: function (index, label, value) {
									var datarecord = glAccountTypes[index];
									return datarecord.description;
								}
							});
						}
                     },
                     { text: '${uiLabelMap.accountCode}', dataField: 'glAccountId', width: 200, editable: true, columntype: 'dropdownlist',
						createeditor: function (row, column, editor) {
							editor.jqxDropDownList({source: glAccountOACsData, displayMember:\"description\", valueMember: \"glAccountId\",
								renderer: function (index, label, value) {
									var datarecord = glAccountOACsData[index];
									return datarecord.description;
								}
							});
						} 					
                     },                     
					 { text: '${uiLabelMap.organizationName}', datafield: 'partyId', width: 200, editable: true, columntype: 'template',
                     	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxGridParty\"></div>');
                            initPartySelect(editor, $('#jqxGridParty'));
                            editor.jqxDropDownButton('setContent', cellvalue);
	                      },
	                      geteditorvalue: function (row, cellvalue, editor) {
	                          // return the editor's value.
	                          editor.jqxDropDownButton(\"close\");
	                           var ini = $('#jqxGridParty').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxGridParty').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.partyId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
	                      }
                      },                          
		     		  { text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 200, editable: true , columntype: 'template',
                     	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxGridProd\"></div>');
                            initProductSelect(editor, $('#jqxGridProd'));
                            editor.jqxDropDownButton('setContent', cellvalue);
	                      },
	                        geteditorvalue: function (row, cellvalue, editor) {
	                            // return the editor's value.
	                            editor.jqxDropDownButton(\"close\");
	                            var ini = $('#jqxGridProd').jqxGrid('getselectedrowindex');
	                            if(ini != -1){
		                            var item = $('#jqxGridProd').jqxGrid('getrowdata', ini);
		                            var selectedPro = item.productId;
		                            return selectedPro;	
	                            }
	                            return cellvalue;
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
                     { text: '${uiLabelMap.origAmount}', dataField: 'origAmount', width: 150, editable: false, cellsformat: 'c2' },
		             { text: '${uiLabelMap.amount}', dataField: 'amount', width: 150, cellsformat: 'c2', editable: false},
		             { text: '${uiLabelMap.description}', dataField: 'description', width: 150, editable: false },
		             { text: '${uiLabelMap.FormFieldTitle_voucherRef}', dataField: 'voucherRef', width: 150, editable: false },
		             { text: '${uiLabelMap.FormFieldTitle_isSummary}', dataField: 'isSummary', width: 150, editable: false }
                      "/>
			 		 
<@jqGrid id="listTransEntriesDetail" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist deleterow="true" clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="popupAddTransactionEntry" editable="true" 
	 url="jqxGeneralServicer?sname=JQListAcctgTransAndEntries&acctgTransId=${parameters.acctgTransId}&organizationPartyId=${parameters.organizationPartyId}"	 
	 updateUrl="jqxGeneralServicer?sname=updateAcctgTransEntry&jqaction=U&acctgTransId=${parameters.acctgTransId}&organizationPartyId=${parameters.organizationPartyId}" 
	 editColumns="organizationPartyId[${parameters.organizationPartyId}];acctgTransId[${parameters.acctgTransId}];acctgTransEntrySeqId;glAccountTypeId;glAccountId;partyId;productId;reconcileStatusId;debitCreditFlag"	 
	 removeUrl="jqxGeneralServicer?sname=deleteAcctgTransEntry&jqaction=D&acctgTransId=${parameters.acctgTransId}&organizationPartyId=${parameters.organizationPartyId}"
	 deleteColumn="acctgTransId[${parameters.acctgTransId}];acctgTransEntrySeqId"
	 createUrl="jqxGeneralServicer?sname=createAcctgTransEntry&jqaction=C" autorowheight="true"
     addColumns="organizationPartyId[${parameters.organizationPartyId}];acctgTransEntryTypeId[_NA_];acctgTransId[${parameters.acctgTransId}];glAccountTypeId;glAccountId;debitCreditFlag;partyId;origAmount(java.math.BigDecimal);origCurrencyUomId;purposeEnumId;voucherRef;productId;reconcileStatusId;settlementTermId;isSummary;description"	 
/>
<#include "popup/popupAccTransactionEntry.ftl"/>