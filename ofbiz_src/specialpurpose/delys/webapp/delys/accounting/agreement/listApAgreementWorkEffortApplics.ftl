<#assign dataField="[{ name: 'agreementItemSeqId', type: 'string' },
					 { name: 'workEffortId', type: 'string'}
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.agreementItemSeqId}', datafield: 'agreementItemSeqId'},
					 { text: '${uiLabelMap.workEffortId}', datafield: 'workEffortId'}"/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false"
		 url="jqxGeneralServicer?sname=JQListAgreementWorkEffortApplics&agreementId=${parameters.agreementId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementWorkEffortApplic&jqaction=C&agreementId=${parameters.agreementId}"
		 removeUrl="jqxGeneralServicer?sname=deleteAgreementWorkEffortApplic&jqaction=D&agreementId=${parameters.agreementId}"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId;workEffortId" 
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId;workEffortId" 
		 />
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.agreementItemSeqId}:</td>
	 			<td align="left">
	 				<div id="agreementItemSeqIdAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.workEffortId}:</td>
	 			<td align="left">
	 				<div id="workEffortIdAdd">
	 					<div style="border-color: transparent;" id="jqxWEGrid"></div>
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
	
	var sourceWE = { datafields:
						[{ name: 'workEffortId', type: 'string' },
						{ name: 'workEffortName', type: 'string' }],
		cache: false,
		root: 'results',
		datatype: "json",
			updaterow: function (rowid, rowdata) {
			// synchronize with the server - send update command   
		},
		beforeprocessing: function (data) {
			sourceWE.totalrecords = data.TotalRows;
		},
		filter: function () {
			// update the grid and send a request to the server.
			$("#jqxWEGrid").jqxGrid('updatebounddata');
		},
		pager: function (pagenum, pagesize, oldpagenum) {
			// callback called when a page or page size is changed.
		},
		sort: function () {
			$("#jqxWEGrid").jqxGrid('updatebounddata');
		},
		sortcolumn: 'workEffortId',
        sortdirection: 'asc',
		type: 'POST',
		data: {
			noConditionFind: 'Y',
			conditionsFind: 'N',
		},
		pagesize:5,
		contentType: 'application/x-www-form-urlencoded',
		url: 'jqxGeneralServicer?sname=JQGetListWorkEfforts',
	};
	var dataAdapterWE = new $.jqx.dataAdapter(sourceWE);
	$("#workEffortIdAdd").jqxDropDownButton({ width: 215, height: 25});
	$("#jqxWEGrid").jqxGrid({
		width:400,
		source: dataAdapterWE,
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
			{ text: 'workEffortId', datafield: 'workEffortId'},
			{ text: 'workEffortName', datafield: 'workEffortName'},
		]
	});
	$("#jqxWEGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxWEGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['workEffortId'] + '</div>';
                $("#workEffortIdAdd").jqxDropDownButton('setContent', dropDownContent);
    });

	<#assign agreementItemList = delegator.findList("AgreementItem", null, null, null, null, false) />
	var aiData = new Array();
	var row = {};
		row['agreementItemSeqId'] = '_NA_';
		aiData[0] = row;
		
	<#list agreementItemList as item>
		var row = {};
		row['agreementItemSeqId'] = ${item.agreementItemSeqId};
		aiData[${item_index} + 1] = row;
	</#list>
	$("#agreementItemSeqIdAdd").jqxDropDownList({ selectedIndex: 0,  source: aiData, width: 215, displayMember: "agreementItemSeqId", valueMember: "agreementItemSeqId"});
	
	//Create Popwindow
	$("#alterpopupWindow").jqxWindow({
         width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7           
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		agreementItemSeqId:$('#agreementItemSeqIdAdd').val(),
        		workEffortId:$('#workEffortIdAdd').val()           
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>