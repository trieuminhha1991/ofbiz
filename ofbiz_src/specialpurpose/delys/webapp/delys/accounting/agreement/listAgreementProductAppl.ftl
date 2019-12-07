<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'productId', type: 'string'},
					 { name: 'price', type: 'number'},
					 { name: 'productName', type: 'string'}			 
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accProductName}', datafield: 'productId', width: '60%', editable: false, 
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return \"<span>\" + data.productId + '[' + data.productName + ']' + \"</span>\";
    					}										 		
					 },
					 { text: '${uiLabelMap.unitPrice}', width: '40%', datafield: 'price'},
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementProductAppl&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementProductAppl&agreementId=${parameters.agreementId}&jqaction=U&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementProductAppl&agreementId=${parameters.agreementId}&jqaction=C&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementProductAppl&agreementId=${parameters.agreementId}&jqaction=D&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productId;price"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productId;price"
		 otherParams="productName:S-getProductName(productId{productId})<productName>"		 
		 />
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accProductName}:</td>
	 			<td align="left">
	 				<div id="productIdAdd">
	 					<div id="jqxProductGrid" />
	 				</div>
	 			</td>
    	 	</tr>
	 		<tr>
    	 		<td align="right">${uiLabelMap.unitPrice}:</td>
	 			<td align="left">
	 				<input id="priceAdd">
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
//Create theme
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;

	var sourceP = { datafields: [
						      { name: 'productId', type: 'string' },
						      { name: 'productName', type: 'string' }
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
				   $("#jqxProductGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxProductGrid").jqxGrid('updatebounddata');
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
				url: 'jqxGeneralServicer?sname=JQGetListProducts',
			};
		    var dataAdapterP = new $.jqx.dataAdapter(sourceP,
		    {
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
		            }
		            return data;
		        },
		        loadError: function (xhr, status, error) {
		            alert(error);
		        },
		        downloadComplete: function (data, status, xhr) {
		                if (!sourceP.totalRecords) {
		                    sourceP.totalRecords = parseInt(data['odata.count']);
		                }
		        }
		    });	

	// Create productId
	$('#productIdAdd').jqxDropDownButton({ width: 215, height: 25});
	$("#jqxProductGrid").jqxGrid({
		width:400,
		source: dataAdapterP,
		filterable: true,
		showfilterrow : true,
		virtualmode: true, 
		sortable:true,
		editable: false,
		autoheight:true,
		pageable: true,
		ready:function(){
        },
		rendergridrows: function(obj)
		{	
			return obj.data;
		},
		columns: 
			[
				{ text: '${uiLabelMap.accProductId}', datafield: 'productId', width: '50%'},
				{ text: '${uiLabelMap.accProductName}', datafield: 'productName'},
			]
		});

	$("#jqxProductGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxProductGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['productId'] + '</div>';
                $("#productIdAdd").jqxDropDownButton('setContent', dropDownContent);
            });
	
//Create price
//$("#priceAdd").jqxInput({width: '210px'});
$("#priceAdd").jqxNumberInput({spinMode: 'simple', width: 210, height: 23, min: 0, decimalDigits: 0, spinButtons: true });

$('#alterpopupWindow').on('open', function (event) {
	$("#priceAdd").jqxNumberInput('val', null);	
	$("#productIdAdd").jqxDropDownButton('val', null);	   	
});

//Create Popup
$("#alterpopupWindow").jqxWindow({
        width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
    	    	
        row = { 
        		productId:$('#productIdAdd').val(), 
        		price:$('#priceAdd').val(),
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>