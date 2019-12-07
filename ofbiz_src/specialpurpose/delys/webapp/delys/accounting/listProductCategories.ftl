<#assign dataField="[{ name: 'productCategoryId', type: 'string'}]"/>

<#assign columnlist="{ text: '${uiLabelMap.ProductCategories}', datafield: 'productCategoryId'}"/>
	
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow"
		 url="jqxGeneralServicer?sname=JQGetListTaxAuthorityCategories&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 createUrl="jqxGeneralServicer?sname=createTaxAuthorityCategory&jqaction=C&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}" addColumns="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];productCategoryId"
		 removeUrl="jqxGeneralServicer?sname=deleteTaxAuthorityCategory&jqaction=D&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}" deleteColumn="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];productCategoryId"
		 />
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.ProductCategoryId}:</td>
	 			<td align="left">
	 				<div id="productCategoryId">
						<div style="border-color: transparent;" id="jqxCategoryGrid"></div>	 				
	 				</div>
	 			</td>
    	 	</tr>
            <tr>
                <td align="right"></td>
                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>
<script>
//Create theme
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
// Create jqxCategoryGrid
var sourcePC = { datafields: [
						      { name: 'productCategoryId', type: 'string' },
						      { name: 'productCategoryTypeId', type: 'string' },
						      { name: 'categoryName', type: 'string' }
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourcePC.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxCategoryGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxCategoryGrid").jqxGrid('updatebounddata');
				},
				sortcolumn: 'productCategoryId',
               	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListProductCategories',
			};
var dataAdapterPC = new $.jqx.dataAdapter(sourcePC);

$("#productCategoryId").jqxDropDownButton({ width: 150, height: 25});
$("#jqxCategoryGrid").jqxGrid({
		width:400,
		source: dataAdapterPC,
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
		columns: 
		[
			{ text: 'productCategoryId', datafield: 'productCategoryId'},
			{ text: 'productCategoryTypeId', datafield: 'productCategoryTypeId'},
			{ text: 'categoryName', datafield: 'categoryName'}
		]
	});
$("#jqxCategoryGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxCategoryGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['productCategoryId'] + '</div>';
                $("#productCategoryId").jqxDropDownButton('setContent', dropDownContent);
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
        		productCategoryId:$('#productCategoryId').val(),
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>