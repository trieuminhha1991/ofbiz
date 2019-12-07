<#assign dataField="[{ name: 'organizationPartyId', type: 'string'},
					 { name: 'glAccountId', type: 'string'}
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.AccountingOrganizationPartyId}', datafield: 'organizationPartyId'},
					 { text: '${uiLabelMap.AccountingGlAccountId}', datafield: 'glAccountId'}
					"/>
	
<@jqGrid  filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow"
		 url="jqxGeneralServicer?sname=JQListTaxAuthorityGLAcounts&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 createUrl="jqxGeneralServicer?sname=createTaxAuthorityGlAccount&jqaction=C&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}" addColumns="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];organizationPartyId;glAccountId"
		 removeUrl="jqxGeneralServicer?sname=deleteTaxAuthorityGlAccount&jqaction=D&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}" deleteColumn="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];organizationPartyId"
		 />
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.AccountingOrganizationPartyId}:</td>
	 			<td align="left">
	 				<div id="organizationPartyId">
	 					<div id="jqxPartyGrid" />
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.AccountingGlAccountId}:</td>
	 			<td align="left">
	 				<div id="glAccountId">
	 					<div id="jqxAccountGrid"/>
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
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
// Create organizationPartyId
var sourcePG = { datafields: [
						      { name: 'partyId', type: 'string' },
						      { name: 'partyTypeId', type: 'string' },
						      { name: 'lastName', type: 'string' },
						      { name: 'firstName', type: 'string' },
						      { name: 'groupName', type: 'string' },
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourcePG.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxPartyGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxPartyGrid").jqxGrid('updatebounddata');
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
				url: 'jqxGeneralServicer?sname=JQGetListParties',
			};
var dataAdapterPG = new $.jqx.dataAdapter(sourcePG);
$("#organizationPartyId").jqxDropDownButton({ width: 150, height: 25});
$("#jqxPartyGrid").jqxGrid({
		width:400,
		source: dataAdapterPG,
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
			{ text: 'partyId', datafield: 'partyId'},
			{ text: 'partyTypeId', datafield: 'partyTypeId'},
			{ text: 'lastName', datafield: 'lastName'},
			{ text: 'firstName', datafield: 'firstName'},
			{ text: 'groupName', datafield: 'groupName'}
		]
	});
$("#jqxPartyGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
                $("#organizationPartyId").jqxDropDownButton('setContent', dropDownContent);
            });
//Create GLAccount
var sourceGLA = { datafields: [
						      { name: 'glAccountId', type: 'string' },
						      { name: 'glAccountTypeId', type: 'string' },
						      { name: 'accountName', type: 'string' },
						      { name: 'accountCode', type: 'string' }
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourceGLA.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxAccountGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxAccountGrid").jqxGrid('updatebounddata');
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
				url: 'jqxGeneralServicer?sname=JQGetListGLAccounts',
			};
var dataAdapterGLA = new $.jqx.dataAdapter(sourceGLA);
$("#glAccountId").jqxDropDownButton({ width: 150, height: 25});
$("#jqxAccountGrid").jqxGrid({
		width:400,
		source: dataAdapterGLA,
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
			{ text: 'glAccountId', datafield: 'glAccountId'},
			{ text: 'glAccountTypeId', datafield: 'glAccountTypeId'},
			{ text: 'accountName', datafield: 'accountName'},
			{ text: 'accountCode', datafield: 'accountCode'}
		]
	});
$("#jqxAccountGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxAccountGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['glAccountId'] + '</div>';
                $("#glAccountId").jqxDropDownButton('setContent', dropDownContent);
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
        		glAccountId:$('#glAccountId').val(),
        		organizationPartyId:$('#organizationPartyId').val(),
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>