<@jqGridMinimumLib/>
<div id="jqxgrid"></div>
<script type="text/javascript">
	$(document).ready(function () {
		var source =
	    {
	        dataType: 'json',
	        dataFields: 
	            [{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceDate', type: 'date' }]
	        ,
	        id: 'invoiceId',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'Y'
		    },
	        contentType: 'application/x-www-form-urlencoded',
	        url: 'jqxGeneralServicer?sname=JQGetListAPInvoice',
	        beforeprocessing: function (data) {
	            source.totalrecords = data.TotalRows;
	        },
	        filter: function () {
                    // update the grid and send a request to the server.
                },
	        root: 'results'
	    }
	    var dataadapter = new $.jqx.dataAdapter(source);
     	$("#jqxgrid").jqxGrid(
        {
            width: 850,
            source: dataadapter,                
            pageable: true,
            virtualmode: true,
            filterable:true,
            showfilterrow: true,
            rendergridrows: function (params) {
                    return params.data;
                },
            autoheight: true,
            columns: [
              { text: '1', datafield: 'invoiceId', filtertype:'range', width: 250 },
              { text: '2', datafield: 'invoiceDate', width: 300, cellsformat: 'dd-MM-yyyy',
			    createfilterwidget: function (column, columnElement, widget) {
			        widget.parents().append("<div id='rnk' style='float:left;' ><div id='jqxgrid2'></div></div>");
			        widget.width(20);
			        $('#rnk').on('click', function () { 
			        	widget.val('sss');
			        	$("#jqxgrid").jqxGrid('refreshfilterrow');
			        }); 
			        $("#jqxgrid2").jqxGrid(
			        {
			            width: 850,
			            source: source,
			            pageable: true, 
			            virtualmode: true,    
			            rendergridrows: function (params) {
		                    return params.data;
		                },
			             columns: [
              				{ text: '1', datafield: 'invoiceId', filtertype:'range', width: 250 }
              				]
		            });
			        $('#rnk').jqxDropDownButton({width:100,height:20});
			    }}
            ]
        });
	});        	
</script>
