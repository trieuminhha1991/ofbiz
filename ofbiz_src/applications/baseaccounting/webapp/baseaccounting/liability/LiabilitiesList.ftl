<#--Import LIB-->
<@useLocalizationNumberFunction />
	<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
	<#include "component://widget/templates/jqwLocalization.ftl"/>
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for uom data
	<#assign listInvoiceType = delegator.findList("InvoiceType", null, null, null, null, false)>
	var dataInvoiceType = [
	   	<#list listInvoiceType as item>
	   		<#assign description = item.get("description", locale)/>
	   		{
	   			invoiceTypeId: '${item.invoiceTypeId}',
	   			description: "${description}"
	   		},
	   	</#list>                       
	];
	
	<#assign listPaymentType = delegator.findList("PaymentType", null, null, null, null, false)>
	var paymentTypeData = [
	   	<#list listPaymentType as item>
	   		<#assign description = item.get("description", locale)/>
	   		{
	   			paymentTypeId: '${item.paymentTypeId}',
	   			description: "${description}"
	   		},
	   	</#list>                       
	];
	
	<#assign statuses = delegator.findList("StatusItem", null, null, null, null, false)>
	var statusData = [
		<#if statuses?exists>
			<#list statuses as statusItem>
			{	statusId: '${statusItem.statusId}',
				description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
			},
			</#list>
		</#if>
	];
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[	{ name: 'partyIdFrom', type: 'string'},
						{ name: 'fullNameFrom', type: 'string'}, 
						{ name: 'orgName', type: 'string'}, 
						{ name: 'organizationPartyId', type: 'string'},
						{ name: 'totalLiability', type: 'number'},
						{ name: 'totalPayable', type: 'number'},
						{ name: 'totalReceivable', type: 'number'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCCustomerId}', datafield: 'partyIdFrom', editable: false
					 },
					 { text: '${uiLabelMap.BACCCustomer}', datafield: 'fullNameFrom', editable: false
					 },
                     { text: '${uiLabelMap.BACCOrganizationId}', datafield: 'orgName',filterable: false
                     },
                     { text: '${uiLabelMap.BACCtotalReceivable}', datafield: 'totalReceivable', width: 150, filterable: false,
                    	 cellsrenderer: function(row, colum, value){
                      		return '<span>' + formatcurrency(value) + '</span>';	
                  		} 
                     },
                     { text: '${uiLabelMap.BACCtotalPayable}', datafield: 'totalPayable', width: 150, filterable: false,
                    	 cellsrenderer: function(row, colum, value){
                      		return '<span>' + formatcurrency(value) + '</span>';	
                  		} 
                     },                     
                     { text: '${uiLabelMap.BACCtotalLiability}', datafield: 'totalLiability', width: 150, filterable: false,
                    	 cellsrenderer: function(row, colum, value){
                      		return '<span>' + formatcurrency(value) + '</span>';	
                  		} 
                     }                     
					 "/>
<#assign organizationPartyId = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator,userLogin.get('userLoginId'))/>
 <@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true" 
		 url="jqxGeneralServicer?organizationPartyId=${organizationPartyId?if_exists}&sname=JQGetListCustLiabilities" dataField=dataField columnlist=columnlist
		 />
<#--=====================================================/Init Grid======================================================================================-->
<#--=====================================================HTML POPUP=====================================================================================-->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupShowInvPay" style="display: none">
			<div id="windowHeader">
	            <span>
	               ${uiLabelMap.BACCListInvoicesAndPayments}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContent">
			    <div id='jqxTabs' style="position: relative;">
		            <ul>
		                <li>${uiLabelMap.BACCListInvoices}</li>
		                <li>${uiLabelMap.BACCListPayments}</li>
		            </ul>
		            <div id="listInvoices" style="margin:10px">
		            	<div id="jqxgridInvoices"></div>
		            </div>
		            <div id="listPayments" style="margin:10px">
		            	<div id="jqxgridPayments"></div>
		            </div>
		        </div>
	        </div>
		</div>
	</div>
</div>
<#--=====================================================/HTML POPUP=====================================================================================-->
<script>
	var JQXAction = function(){};
	JQXAction.prototype.theme = "olbius";
	
	JQXAction.prototype = {
			bindEvent : function(){
				$('#jqxgrid').on('rowdoubleclick', function (event) { 
				    var args = event.args;
				    var rowindex = args.rowindex;
				    var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
				    var bindedInvData = getInvoiceData(data.organizationPartyId, data.partyIdFrom);
				    var bindedPayData = getPaymentData(data.organizationPartyId, data.partyIdFrom);
				    $("#alterpopupShowInvPay").jqxWindow('open');
				});
			},
			_initGridPaymentAndInvoice : function(){
				
				var datafields_inv =  [
				        { name: 'invoiceId', type: 'string' },
				        { name: 'invoiceTypeId', type: 'string' },
				        { name: 'invoiceDate', type: 'date'},
				        { name: 'statusId', type: 'string' },
				        { name: 'total', type: 'number' },
				        { name: 'amountToApply', type: 'number' }
				    ]
				
				var columns_inv = [
						      { text: '${uiLabelMap.FormFieldTitle_invoiceId}', datafield: 'invoiceId', width: 150},
						      { text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', datafield: 'invoiceTypeId',
						    	  cellsrenderer: function(row, colum, value){
						    		  for(var i = 0; i < dataInvoiceType.length; i++){
						    			  if(dataInvoiceType[i].invoiceTypeId == value){
						    				  return '<span>' + dataInvoiceType[i].description + '</span>';
						    			  }
						    		  }
						    		  return '<span>' + value + '</span>';	
				          		  } 
						      },
						      { text: '${uiLabelMap.FormFieldTitle_invoiceDate}', datafield: 'invoiceDate', width: 150, cellsformat: 'dd/MM/yyyy'},
						      { text: '${uiLabelMap.FormFieldTitle_statusId}', datafield: 'statusId', width: 150,
						    	  cellsrenderer: function(row, colum, value){
						    		  for(var i = 0; i < statusData.length; i++){
						    			  if(statusData[i].statusId == value){
						    				  return '<span>' + statusData[i].description + '</span>';
						    			  }
						    		  }
						    		  return '<span>' + value + '</span>';	
				          		  }  
						      },
						      { text: '${uiLabelMap.FormFieldTitle_total}', datafield: 'total', width: 150,
						    	  cellsrenderer: function(row, colum, value){
				                  		return '<span>' + formatcurrency(value) + '</span>';	
				              	  }  
						      },
						      { text: '${uiLabelMap.FormFieldTitle_amountToApply}', datafield: 'amountToApply', width: 350,
						    	  cellsrenderer: function(row, colum, value){
						    		  if(value < 0) value = -value;
				                  		return '<span>' + formatcurrency(value) + '</span>';	
				              	  }  
						      },
						    ]
				
				Grid.initGrid({url:'',width:'100%'}, datafields_inv, columns_inv, null, $("#jqxgridInvoices"));
				
				var columns = [
							      { text: '${uiLabelMap.FormFieldTitle_paymentId}', datafield: 'paymentId', width: 150},
							      { text: '${uiLabelMap.FormFieldTitle_paymentTypeId}', datafield: 'paymentTypeId',
							    	  cellsrenderer: function(row, colum, value){
							    		  for(var i = 0; i < paymentTypeData.length; i++){
							    			  if(paymentTypeData[i].paymentTypeId == value){
							    				  return '<span>' + paymentTypeData[i].description + '</span>';
							    			  }
							    		  }
							    		  return '<span>' + value + '</span>';	
					          		  } 
							      },
							      { text: '${uiLabelMap.FormFieldTitle_effectiveDate}', datafield: 'effectiveDate', width: 150, cellsformat: 'dd/MM/yyyy'},
							      { text: '${uiLabelMap.FormFieldTitle_statusId}', datafield: 'statusId', width: 150,
							    	  cellsrenderer: function(row, colum, value){
							    		  for(var i = 0; i < statusData.length; i++){
							    			  if(statusData[i].statusId == value){
							    				  return '<span>' + statusData[i].description + '</span>';
							    			  }
							    		  }
							    		  return '<span>' + value + '</span>';	
					          		  }  
							      },
							      { text: '${uiLabelMap.FormFieldTitle_amount}', datafield: 'amount', width: 150,
							    	  cellsrenderer: function(row, colum, value){
					                  		return '<span>' + formatcurrency(value) + '</span>';	
					              	  }  
							      },
							      { text: '${uiLabelMap.FormFieldTitle_amountToApply}', datafield: 'amountToApply', width: 350,
							    	  cellsrenderer: function(row, colum, value){
							    		  if(value < 0) value = -value;
					                  		return '<span>' + formatcurrency(value) + '</span>';	
					              	  }  
							      },
							    ];
				
				var datafields = [
				      	        { name: 'paymentId', type: 'string' },
				    	        { name: 'paymentTypeId', type: 'string' },
				    	        { name: 'statusId', type: 'string'},
				    	        { name: 'effectiveDate', type: 'date' },
				    	        { name: 'amount', type: 'number' },
				    	        { name: 'amountToApply', type: 'number' },
				    	        { name: 'currencyUomId', type: 'number' }
				    	    ]
				
				Grid.initGrid({url:'',width:'100%'}, datafields, columns, null, $("#jqxgridPayments"));
			},
			initWindow : function(){
				$("#alterpopupShowInvPay").jqxWindow({
				    showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "90%", height: 530, minWidth: '40%', width: "90%", isModal: true,
				    theme:theme, collapsed:false,
				    initContent: function () {
				    	// Create jqxTabs.
				        $('#jqxTabs').jqxTabs({ width: '98%', height: 430, position: 'top',
				        	initTabContent:function (tab) {
				        		if(tab == 0){
				        		}else{
				        		}
				        	}
				        });
				    }
				});
			}
	}
	
	var getInvoiceData = function(organizationPartyId, customerPartyId){
		$("#jqxgridInvoices").jqxGrid('source')._source.url = 'jqxGeneralServicer?sname=getLiabilityInvoiceData&organizationPartyId=' + organizationPartyId + '&partyId=' + customerPartyId;
		$("#jqxgridInvoices").jqxGrid('updatebounddata');
	}
	
	var getPaymentData = function(organizationPartyId, customerPartyId){
		$("#jqxgridPayments").jqxGrid('source')._source.url = 'jqxGeneralServicer?sname=getLiabilityPaymentData&organizationPartyId=' + organizationPartyId + '&partyId=' + customerPartyId;
		$("#jqxgridPayments").jqxGrid('updatebounddata');
	}
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction._initGridPaymentAndInvoice();
		jqxAction.bindEvent();
	});
</script>