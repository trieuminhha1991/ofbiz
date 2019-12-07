<div id="jqxgridTrans"></div>
<#--===================================Init Grid=====================================================--> 
<script>
	var cellclass = function (row, columnfield, value) {
		var data = $('#jqxgridTrans').jqxGrid('getrowdata', row);
		if (data.isCanceled == 'Y') {
			return 'is-canceled';
		} else {
			if (data.isPosted == 'Y') {
		        return 'posted';
		    } else if (data.isPosted == 'N') {
		        return 'not-posted';
		    }
		}
	}
</script>

<style>
	.posted {
	    background-color: #00b384 !important;
	}
	.not-posted {
	    background-color: #ff9999 !important;
	}
	.is-canceled {
	    background-color: #93909b !important;
	}
</style>
<#assign dataField="[{ name: 'acctgTransId', type: 'string' },
						 { name: 'transactionDate', type: 'date', other:'Timestamp' },
						 { name: 'acctgTransTypeId', type: 'string' },
						 { name: 'glFiscalTypeId', type: 'string' },
						 { name: 'invoiceId', type: 'string' },
						 { name: 'paymentId', type: 'string' },
						 { name: 'workEffortId', type: 'string' },
						 { name: 'shipmentId', type: 'string' },
						 { name: 'isPosted', type: 'string'},
						 { name: 'isCanceled', type: 'string'},
						 { name: 'postedDate', type: 'date', other:'Timestamp'},
						 { name: 'description', type: 'string'}]"/>
	
 <#assign columnlist="{ text: '${uiLabelMap.BACCAcctgTransId}', dataField: 'acctgTransId', width: '10%', cellclassname: cellclass},
			    	  { text: '${uiLabelMap.BACCAcctgTransTypeId}', dataField: 'acctgTransTypeId', width: '15%',columntype: 'dropdownlist',filtertype: 'checkedlist',
							cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
								for(i = 0; i < acctgTransTypesData.length; i++){
									if(value == acctgTransTypesData[i].acctgTransTypeId){
										return '<span>' + acctgTransTypesData[i].description + '</span>';
									}
								}
							},
							createfilterwidget: function (column, columnElement, widget) {
									var uniqueRecords2 = [] ;
									if(acctgTransTypesData && acctgTransTypesData.length > 0 ){
										var filterBoxAdapter2 = new $.jqx.dataAdapter(acctgTransTypesData,{autoBind: true});
					              		uniqueRecords2 = filterBoxAdapter2.records;
									}
									widget.jqxDropDownList({ filterable:true, source: uniqueRecords2, 
										displayMember: 'description', valueMember : 'acctgTransTypeId', autoDropDownHeight: false});			   				
								},
							cellclassname: cellclass
			    	  },
			    	  { text: '${uiLabelMap.BACCGlFiscalTypeId}', dataField: 'glFiscalTypeId', width: '14%', columntype: 'dropdownlist',filtertype: 'checkedlist',
							cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
								for(i = 0; i < glFiscalTypesData.length; i++){
									if(value == glFiscalTypesData[i].glFiscalTypeId){
										return '<span>' + glFiscalTypesData[i].description + '</span>';
									}
								}
							},
							createfilterwidget: function (column, columnElement, widget) {
									var uniqueRecords2 = [] ;
									if(glFiscalTypesData && glFiscalTypesData.length > 0 ){
										var filterBoxAdapter2 = new $.jqx.dataAdapter(glFiscalTypesData,
							                {
							                    autoBind: true
							                });
					              uniqueRecords2 = filterBoxAdapter2.records;
									}
									widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'glFiscalTypeId'});			   				
								},
							cellclassname: cellclass
			    	  },
			    	  { text: '${uiLabelMap.BACCPostedDate}',filtertype: 'range', dataField: 'transactionDate', width: '18%', cellsformat: 'dd/MM/yyyy HH:mm:ss',columntype: 'datetimeinput', cellclassname: cellclass},
			    	  { text: '${uiLabelMap.BACCInvoiceId}', dataField: 'invoiceId', width: '11%', cellclassname: cellclass},
			    	  { text: '${uiLabelMap.BACCPaymentId}', dataField: 'paymentId', width: '11%', cellclassname: cellclass},
			    	  { text: '${uiLabelMap.BACCPostTransaction}', dataField:'isPosted', filtertype: 'checkedlist', cellclassname: cellclass,
							cellsrenderer: function(row, columns, value){
								for(i = 0; i < isPostedData.length; i++){
									if(value == isPostedData[i].isPosted){
										return '<span>' + isPostedData[i].description + '</span>';
									}
								}
					     	},
					     	createfilterwidget: function (column, columnElement, widget) {
									var uniqueRecords2 = [] ;
									if(isPostedData && isPostedData.length > 0 ){
										var filterBoxAdapter2 = new $.jqx.dataAdapter(isPostedData,
							                {
							                    autoBind: true
							                });
					              uniqueRecords2 = filterBoxAdapter2.records;
									}
									widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'isPosted'});			   				
					     	},
			    	  },
			    	  { text: '${uiLabelMap.BACCDescription}', dataField: 'description', width: '10%', cellclassname: cellclass},
				    "/>
<@jqGrid filtersimplemode="true" id="jqxgridTrans" dataField=dataField columnlist=columnlist url="jqxGeneralServicer?sname=JqxGetListAcctgTrans"
			 clearfilteringbutton="true" filterable="true" initrowdetails="true" initrowdetailsDetail="JQXGridTrans.initrowdetails"
			 addrow="true" alternativeAddPopup="CreateAcctgTransWindow" addType="popup"
			 mouseRightMenu="true" contextMenuId="contextMenu"
		 />

<div id="contextMenu" class="hide">
	<ul>
		<li action="edit">
			<i class="icon-edit open-sans"></i>${StringUtil.wrapString(uiLabelMap.CommonEdit)}
        </li>
        <#if hasOlbPermission("MODULE", "ACC_TRANSACTION_POSTED", "")>
        <li action="posted" id="posted">
			<i class="icon-check-square-o open-sans"></i>${StringUtil.wrapString(uiLabelMap.BACCPosting)}
        </li>
        </#if>        
        <#if hasOlbPermission("MODULE", "ACC_TRANSACTION_DELETE", "")>
        <li action="remove" id="remove">
			<i class="icon-remove open-sans"></i>${StringUtil.wrapString(uiLabelMap.CommonCancel)}
        </li>
        </#if>         
	</ul>
</div>
<script type="text/javascript" src="/accresources/js/transaction/transGrid.js?v=0.0.3"></script>
<script>
	JQXGridTrans = function(){
	}
	JQXGridTrans.initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var jqxgridChild = $(parentElement.children);
	
		//Data fields 
		var datafields = [{ name: 'acctgTransId', type: 'string' },
		 	         	 { name: 'acctgTransEntrySeqId', type: 'string' },
			         	 { name: 'glAccountClassId', type: 'string' },
						 { name: 'reconcileStatusId', type: 'string' },
						 { name: 'glAccountId', type: 'string' },
						 { name: 'partyId', type: 'string' },
						 { name: 'productId', type: 'string' },
						 { name: 'productCode', type: 'string' },
			         	 { name: 'glAccountTypeId', type: 'string' },
			         	 { name: 'productId', type: 'string' },
			         	 { name: 'debitCreditFlag', type: 'string'},
			         	 { name: 'origAmount', type: 'number'},
			         	 { name: 'amount', type: 'number'},
			         	 { name: 'transDescription', type: 'transDescription'}
 	 		 		  ];
		//Column of grid
		var columnlist = [{ text: '${uiLabelMap.BACCAcctgTransEntrySeqId}', dataField: 'acctgTransEntrySeqId', width: 150},
		                  { text: '${uiLabelMap.BACCProductId}', dataField: 'productCode', width: 150},
							{ text: '${uiLabelMap.BACCGlAccountClassId}', dataField: 'glAccountClassId', width: 300,
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
							{ text: '${uiLabelMap.BACCAmount}', dataField:'amount',width : 250,
								cellsrenderer: function(row, columns, value){
					         		return '<span>'+formatcurrency(value)+'</span>';
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
	        url: "JqxGetListAcctgTransAndEntries&acctgTransId=" + datarecord.acctgTransId
	   	};
		
		//Create grid
	   	Grid.initGrid(config, datafields, columnlist, null, jqxgridChild);
	}
</script>