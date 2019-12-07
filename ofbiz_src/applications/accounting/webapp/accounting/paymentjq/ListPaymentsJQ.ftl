<div class="widget-box transparent no-bottom-border" id="screenlet_1"><div class="widget-header"><h4>Find Payment</h4><div class="loading-image" style="width:20px;height:20px;float:left;padding-top:8px;"></div>
	<span class="widget-toolbar none-content">
		<a href="/accounting/control/newPayment"><i class="icon-plus-sign open-sans">Create New Payment</i></a>
	</span>
</div>
<div class="widget-body">
<div id="screenlet_1_col" class="widget-body-inner">
<div id="search-options" style="display: none;">
</div><div id="search-results">
</div></div></div></div>
<br />
<#assign columnlist="{ text: 'Payment Id', dataField: 'paymentId', filtertype: 'date', width: 100},
					 { text: 'Payment type id', dataField: 'paymentTypeId', filtertype: 'date', width: 200},
					 { text: 'Status', dataField: 'statusId', width: 200},
					 { text: 'Comments', dataField: 'comments', width: 200},
					 { text: 'From party', dataField: 'partyIdFrom', width: 200, cellsrenderer: linkrenderer},
					 { text: 'To party', dataField: 'partyIdTo', width: 200},
					 { text: 'Effective date', dataField: 'effectiveDate', filtertype: 'date', width: 200, cellsformat: 'd'},
					 { text: 'Currency Uom Id', dataField: 'currencyUomId', width: 200},
					 { text: 'Amount', dataField: 'amount', width: 200},
					 { text: 'Amount to apply', dataField: 'amountToApply', width: 200}
					 "/>
<#assign dataField="{ name: 'paymentId', type: 'string'},
					{ name: 'paymentTypeId', type: 'string'},
					{ name: 'statusId', type: 'string'},
					{ name: 'comments', type: 'string'},
					{ name: 'partyIdFrom', type: 'string'},
					{ name: 'partyIdTo', type: 'string'},
					{ name: 'effectiveDate', type: 'date'},
					{ name: 'currencyUomId', type: 'string'},
					{ name: 'amount', type: 'string'},
					{ name: 'amountToApply', type: 'string'}
					"/>			 
<@jqGrid entityName="PaymentAndTypeAndCreditCard" url="/humanres/control/FindEmployeeJQ" defaultSortColumn="paymentId" columnlist=columnlist dataField=dataField height="400" 
		 editable="false" editpopup="false" filtersimplemode="false" filterable="true"/>           
		 
		 
		 
		 
		 <script type="text/javascript" src="/aceadmin/jqw/demos/jqxgrid/generatedata.js"></script>
		 
	    <script type="text/javascript">
	        $(document).ready(function () {
	            var data = generatedata(500);
	            var source =
	            {
	                localdata: data,
	                datafields:
	                [
	                    { name: 'firstname', type: 'string' },
	                    { name: 'lastname', type: 'string' },
	                    { name: 'productname', type: 'string' },
	                    { name: 'date', type: 'date' },
	                    { name: 'quantity', type: 'number' },
	                    { name: 'price', type: 'number' }
	                ],
	                datatype: "json",
	                filter: function () {
                    // update the grid and send a request to the server.
                    $("#jqxgrid").jqxGrid('updatebounddata');
                	}
	            };
	            var adapter2 = new $.jqx.dataAdapter(source);
	            $("#jqxgrid2").jqxGrid(
	            {
	                width: 850,
	                source: adapter2,
	                filterable: true,
	                sortable: true,
	                pageable:true,
	                virtualmode: true,
	                rendergridrows: function () {
                    return adapter2.records;
                },
	                columns: [
	                  { text: 'First Name', datafield: 'firstname', width: 160 },
	                  { text: 'Last Name', datafield: 'lastname', width: 160 },
	                  { text: 'Product', datafield: 'productname', filtertype: 'date', width: 170 },
	                  { text: 'Order Date', datafield: 'date', filtertype: 'date', width: 160, cellsformat: 'dd-MMMM-yyyy' },
	                  { text: 'Quantity', datafield: 'quantity', width: 80, cellsalign: 'right' },
	                  { text: 'Unit Price', datafield: 'price', cellsalign: 'right', cellsformat: 'c2' }
	                ]
	            });
	        });
    	</script>
    <div id='jqxWidget2' style="font-size: 13px; font-family: Verdana; float: left;">
        <div id="jqxgrid2">
        </div>
    </div>