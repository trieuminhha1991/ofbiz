<script>
	<#assign productPromoList = delegator.findList("ProductPromo", null, null, null, null, false) />
	var ppData = new Array();
	<#list productPromoList as pp>
		var row = {};
		<#assign promoName = StringUtil.wrapString(pp.promoName)/>
		row['promoName'] = '${promoName}';
		row['productPromoId'] = '${pp.productPromoId}';
		ppData[${pp_index}] = row;
	</#list> 
</script>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'productPromoId', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'sequenceNum', type: 'string'}				 
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accProductPromoId}', datafield: 'productPromoId', width: '30%', editable: false, 
						cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < ppData.length; i++){
					 			if(value == ppData[i].productPromoId){
					 				return \"<span>\" + '[' + ppData[i].productPromoId + '] ' + ppData[i].promoName + \"</span>\";
					 			}}
					 		}					 		
					 },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', columntype: 'template', width: '25%', editable: false,
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss' });
                     	}
                     },
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: '25%', cellsformat: 'dd/MM/yyyy', columntype: 'template',
					 	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd/MM/yyyy hh:mm:ss' });
                     	}
					 },
					 { text: '${uiLabelMap.SequenceId}', width: '20%', datafield: 'sequenceNum'},
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementPromoAppls&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementPromoAppl&agreementId=${parameters.agreementId}&jqaction=U&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementPromoAppl&agreementId=${parameters.agreementId}&jqaction=C&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementPromoAppl&agreementId=${parameters.agreementId}&jqaction=D&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productPromoId;fromDate(java.sql.Timestamp)"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productPromoId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];productPromoId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"		 
		 />
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accProductPromoId}:</td>
	 			<td align="left">
	 				<div id="productPromoIdAdd">
	 				</div>
	 			</td>
	 		</tr>
            <tr>
    	 		<td align="right">${uiLabelMap.fromDate}:</td>
	 			<td align="left">
	 				<div id="fromDateAdd">
	 				</div>
	 			</td>
	 		</tr>
	 		<tr>
    	 		<td align="right">${uiLabelMap.thruDate}:</td>
	 			<td align="left">
	 				<div id="thruDateAdd">
	 				</div>
	 			</td>
	 		</tr>
	 		<tr>
    	 		<td align="right">${uiLabelMap.SequenceId}:</td>
	 			<td align="left">
	 				<input id="sequenceNumAdd">
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
	
//Create productPromoId
$("#productPromoIdAdd").jqxDropDownList({ source: ppData, width: '215px', height: '25px', selectedIndex: 0, displayMember: "promoName", valueMember: "productPromoId"});;
//Create FromDate
$("#fromDateAdd").jqxDateTimeInput({width: '215px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});

//Create ThruDate
$("#thruDateAdd").jqxDateTimeInput({width: '215px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});

//Create sequenceNum
$("#sequenceNumAdd").jqxInput({width: '210px'});

$('#alterpopupWindow').on('open', function (event) {
	$("#thruDateAdd").jqxDateTimeInput('val', null);
	$("#sequenceNumAdd").jqxInput('val', null);	   	
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
    	var checkNull = $('#thruDateAdd').val();
    	if (checkNull != '')
    	{
    		checkNull = $('#thruDateAdd').jqxDateTimeInput('getDate').getTime();
    	}
    	    	
        row = { 
        		productPromoId:$('#productPromoIdAdd').val(), 
        		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate').getTime(),
        		thruDate:checkNull,
        		sequenceNum:$('#sequenceNumAdd').val(),
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>