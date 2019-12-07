<div id="jqxgridTrans"></div>

<#--===================================Init Grid=====================================================--> 
<script>
	$(document).ready(function () {
		var jqxGridTrans = new JQXGridTrans();
		jqxGridTrans.initGrid();
	});
	
	JQXGridTrans = function(){
	}
	JQXGridTrans.initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var jqxgridChild = $(parentElement.children);
		
		var isPostedData = [
	        {isPosted : 'Y',description : '${uiLabelMap.BACCPostted}'},
	        {isPosted : 'N',description : '${uiLabelMap.BACCNotPostted}'}
	    ];
		
		<#assign glAccountClasses = delegator.findList("GlAccountClass", null, null, null, null, true)/>
		var glAccountClasses = [
			<#list glAccountClasses as type>
				{
					<#assign description = StringUtil.wrapString(type.get("description", locale)?if_exists) />
					glAccountClassId : "${type.glAccountClassId}",
					description: "${description}"	
				},
			</#list>
		];
		
		<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "ACCTG_ENREC_STATUS"), null, null, null, true) />
		var statusItemAccTransData =  [
	        <#list statusItems as item>
	        	{
	        		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists) />
	        		'statusId' : "${item.statusId?if_exists}",
	        		'description' : "${description?if_exists}"
				},
			</#list>
		];
		
		<#assign glAccountOACs = delegator.findList("GlAccountOrganizationAndClass", null, null, null, null, true) />
		var glAccountOACsData =  [
	          <#list glAccountOACs as item>
	          	{
	          		<#assign description = StringUtil.wrapString(item.get("accountName", locale)?if_exists + " [" + item.glAccountId?if_exists +"]")>
	          		'glAccountId' : "${item.glAccountId?if_exists}",
	          		'description' : "${description?if_exists}"
	  			},
			  </#list>
		];
		
		<#assign glAccountTypes = delegator.findList("GlAccountType", null, null, null, null, true)/>
		var glAccountTypes = [
	  		<#list glAccountTypes as type>
	  			{
	  				<#assign description = StringUtil.wrapString(type.get("description", locale)?if_exists) />
	  				glAccountTypeId : "${type.glAccountTypeId}",
	  				description: "${description}"	
	  			},
	  		</#list>
	  	];
		
		//Data fields 
		var datafields = [{ name: 'acctgTransId', type: 'string' },
		 	         	 { name: 'acctgTransEntrySeqId', type: 'string' },
			         	 { name: 'glAccountClassId', type: 'string' },
						 { name: 'reconcileStatusId', type: 'string' },
						 { name: 'glAccountId', type: 'string' },
						 { name: 'partyId', type: 'string' },
			         	 { name: 'glAccountTypeId', type: 'string' },
			         	 { name: 'productId', type: 'string' },
			         	 { name: 'debitCreditFlag', type: 'string'},
			         	 { name: 'origAmount', type: 'number'},
			         	 { name: 'amount', type: 'number'},
                         { name: 'currencyUomId', type: 'string'},
                         { name: 'origCurrencyUomId', type: 'string'},
			         	 { name: 'transDescription', type: 'transDescription'}
 	 		 		  ];
		//Column of grid
		var columnlist = [{ text: '${uiLabelMap.BACCAcctgTransEntrySeqId}', dataField: 'acctgTransEntrySeqId', width: 150},
							{ text: '${uiLabelMap.BACCGlAccountClassId}', dataField: 'glAccountClassId', width: 280,
								cellsrenderer: function(row, columns, value){
					         		for(var i = 0; i < glAccountClasses.length; i++){
					         			if(glAccountClasses[i].glAccountClassId == value){
					         				return '<span>' + glAccountClasses[i].description + '</span>';
					         			}
					         		}
					         		return value;
					         	}
							},
							{ text: '${uiLabelMap.BACCGlAccountId}', dataField: 'glAccountId', width: 350,
								cellsrenderer: function(row, columns, value){
					         		for(var i = 0; i < glAccountOACsData.length; i++){
					         			if(glAccountOACsData[i].glAccountId == value){
					         				return '<span>' + glAccountOACsData[i].description + '</span>';	
					         			}
					         		}
					         		return '<span>' + value + '</span>';
					         	}
							},
							{ text: '${uiLabelMap.BACCDebitCreditFlag}', dataField: 'debitCreditFlag', width: 100,
								cellsrenderer: function(row, columns, value){
									if(value == 'C'){
										des = '${uiLabelMap.BACCCREDIT}';
									}else{
										des = '${uiLabelMap.BACCDEBIT}';
									}
					         		return '<span>'+ des +'</span>';
					         	}
							},
							{ text: '${uiLabelMap.BACCAmount}', dataField:'origAmount',width : 170,
								cellsrenderer: function(row, columns, value){
                                    var data = jqxgridChild.jqxGrid('getrowdata', row);
									return '<span>'+formatcurrency(value, data.origCurrencyUomId)+'</span>';
					         	},
							},
							{ text: '${uiLabelMap.BACCAmountChange}', dataField: 'amount', width : 170,
								cellsrenderer: function(row, columns, value){
				             			return '<span>' + formatcurrency(value, 'VND') + '</span>';
					         	},
							},
							{ text: '${uiLabelMap.BACCDescription}', width: 150, dataField:'transDescription'},
					    ];
		//Configuration for grid
		var config = {
			width: 'calc(100% - 30px)',
			autoheight: false,
			height: 180,
	 	    columnsresize: true,
	 	    showtoolbar: false,
	 	    pageable : true,
	 	    virtualmode: true,
	 	    editable: false,
	        localization: getLocalization(),
	        selectionmode: 'singlerow',
	        url: "JqxGetListAcctgTransAndEntries&paymentId=${parameters.paymentId}&acctgTransId=" + datarecord.acctgTransId
	   	};
		
		//Create grid
	   	Grid.initGrid(config, datafields, columnlist, null, jqxgridChild);
	}
	JQXGridTrans.prepareData = function(){
		var dataTrans  = [
      	 	<#if acctgTrans?exists>
      	 		<#list acctgTrans as item>
      	 			{
      					'acctgTransId' : '${item.acctgTransId?if_exists}',
      					'transactionDate' : '${item.transactionDate?if_exists}',
      					'acctgTransTypeId' : '${item.acctgTransTypeId?if_exists}',
      					'glFiscalTypeId' : '${item.glFiscalTypeId?if_exists}',
      					'invoiceId' : '${item.invoiceId?if_exists}',
      					'paymentId' : '${item.paymentId?if_exists}',
      					'workEffortId' : '${item.workEffortId?if_exists}',
      					'shipmentId' : '${item.shipmentId?if_exists}',
      					'isPosted' : '${item.isPosted?if_exists}',
      					'postedDate' : '${item.postedDate?if_exists}',
      	 			},
      	 		</#list>
      	 	</#if>
      	];
		return dataTrans;
	};
	
	JQXGridTrans.prototype.initGrid = function(){
		//Prepare Data
		<#assign acctgTransTypes = delegator.findList("AcctgTransType", null, null, null, null, true) />
		var acctgTransTypesData =  [
	        <#list acctgTransTypes as item>
	        	{
	        		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>
	        		'acctgTransTypeId' : "${item.acctgTransTypeId?if_exists}",
	        		'description' : "${description}"
				},
			</#list>
		];
		
		<#assign glFiscalTypes = delegator.findList("GlFiscalType", null, null, null, null, true) />
		var glFiscalTypesData =  [
		      <#list glFiscalTypes as item>
		      	{
		      		<#assign description = StringUtil.wrapString(item.get("description", locale)?if_exists)>
		      		'glFiscalTypeId' : "${item.glFiscalTypeId?if_exists}",
		      		'description' : "${description}"	
		      	},
		  	  </#list>
	  	];
		
		//Data fields 
		var datafields = [{ name: 'acctgTransId', type: 'string' },
              	 		  { name: 'transactionDate', type: 'date', other:'Timestamp' },
              	 		  { name: 'acctgTransTypeId', type: 'string' },
              	 		  { name: 'glFiscalTypeId', type: 'string' },
              	 		  { name: 'invoiceId', type: 'string' },
              	 		  { name: 'paymentId', type: 'string' },
              	 		  { name: 'workEffortId', type: 'string' },
              	 		  { name: 'shipmentId', type: 'string' },
              	 		  { name: 'isPosted', type: 'string'},
              	 		  { name: 'postedDate', type: 'date', other:'Timestamp'}
 	 		 		  ];
		//Data Source
		var source = {
            localdata: JQXGridTrans.prepareData(),
            datatype: "array",
            datafields:datafields
        };
		//Column of grid
		var columnlist = [{ text: '${uiLabelMap.BACCAcctgTransId}', dataField: 'acctgTransId', width: 100},
		                  { text: '${uiLabelMap.BACCTransactionDate}', dataField: 'transactionDate', width: 200, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput'},
		                  { text: '${uiLabelMap.BACCAcctgTransTypeId}', dataField: 'acctgTransTypeId', width: 200,columntype: 'dropdownlist',filtertype: 'checkedlist',
		      				cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		      					for(i = 0; i < acctgTransTypesData.length; i++){
		      						if(value == acctgTransTypesData[i].acctgTransTypeId){
		      							return '<span>' + acctgTransTypesData[i].description + '</span>';
		      						}
		      					}
		      				}
		                  },
		                  { text: '${uiLabelMap.BACCGlFiscalTypeId}', dataField: 'glFiscalTypeId', width: 200, columntype: 'dropdownlist',filtertype: 'checkedlist',
		      				cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		      					for(i = 0; i < glFiscalTypesData.length; i++){
		      						if(value == glFiscalTypesData[i].glFiscalTypeId){
		      							return '<span>' + glFiscalTypesData[i].description + '</span>';
		      						}
		      					}
		      				}
		                  },
		                  /*{ text: '${uiLabelMap.BACCInvoiceId}', dataField: 'invoiceId', width: 200, columntype: 'template',
		      				cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		      				},
		                  },
		                  { text: '${uiLabelMap.BACCPaymentId}', dataField: 'paymentId', width: 200,columntype: 'template',
		      				cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
		      				},
		                  },*/
		                  { text: '${uiLabelMap.BACCPostedDate}',filtertype: 'range', dataField: 'postedDate', width: 200, cellsformat: 'dd/MM/yyyy',columntype: 'datetimeinput'},
		                  { text: '${uiLabelMap.BACCPostTransaction}', dataField:'isPosted', 
		      				cellsrenderer: function(row, columns, value){
		      					if(value == 'Y'){
		      						des = '${uiLabelMap.BACCPostted}';
		      					}else{
		      						des = '${uiLabelMap.BACCNotPostted}';
		      					}
		      	         		return '<span>'+ des +'</span>';
		      	         	}
		                  },
		                ];
		//Tool bar of grid
		var rendertoolbar = function (toolbar){
			var container = $("<div id='toolbarcontainer' class='widget-header'></div>");
            toolbar.append(container);
            container.append('<h4>${uiLabelMap.BACCListTransactions}</h4>');
	   	}
		//Configuration for grid
		var config = {
	   		width: '100%', 
	   		virtualmode: false,
	   		showfilterrow: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: false,
	        filterable: false,
	        editable: false,
	        rowsheight: 26,
	        localization: getLocalization(),
	        selectionmode: 'singlerow',
	        rowdetails: true,
	        source: source,
	        rowdetailstemplate: { rowdetails: "<div style='margin: 10px;'></div>", rowdetailsheight: 200 },
	 	    initrowdetails: JQXGridTrans.initrowdetails,
	   	};
		//Create grid
	   	Grid.initGrid(config, datafields, columnlist, null, $("#jqxgridTrans"));
	}
</script>