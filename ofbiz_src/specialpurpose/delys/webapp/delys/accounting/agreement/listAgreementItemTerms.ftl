<script>
	<#assign invItemTypeList = delegator.findList("InvoiceItemType", null, null, null, null, false) />
	var iitData = new Array();
	var row = {};
	row['invoiceItemTypeId'] = '';
	row['description'] = '';
	iitData[0] = row;
	<#list invItemTypeList as item >
		var row = {};
		row['invoiceItemTypeId'] = '${item.invoiceItemTypeId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		iitData[${item_index} + 1] = row;
	</#list>
	
	<#assign termTypeList = delegator.findList("TermType", null, null, null, null, false) />
	var ttData = new Array();
	<#list termTypeList as item >
		var row = {};
		row['termTypeId'] = '${item.termTypeId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		ttData[${item_index}] = row;
	</#list>
	
</script>
<#assign dataField="[{ name: 'agreementTermId', type: 'string' },
					 { name: 'termTypeId', type: 'string'},
					 { name: 'agreementId', type: 'string'},
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'invoiceItemTypeId', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date', width: 150},
					 { name: 'termValue', type: 'string', width: 150},
					 { name: 'termDays', type: 'string', width: 150},
					 { name: 'textValue', type: 'string', width: 150},
					 { name: 'minQuantity', type: 'number', width: 150},
					 { name: 'maxQuantity', type: 'number', width: 150},
					 { name: 'description', type: 'string', width: 150},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.termTypeId}', datafield: 'termTypeId', editable: false, width: 150,
					 	cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < ttData.length; i++){
					 			if(value == ttData[i].termTypeId){
					 				return \"<span>\" + ttData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.agreementItemSeqId}', datafield: 'agreementItemSeqId', editable: false, width: 180},
					 { text: '${uiLabelMap.invoiceItemTypeId}', datafield: 'invoiceItemTypeId', columntype: 'template', width: 180, 
					 	createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({width: 150, source: iitData, displayMember:\"description\", valueMember: \"invoiceItemTypeId\",
                            renderer: function (index, label, value) {
			                    var datarecord = iitData[index];
			                    return datarecord.description;
			                  }
                        });
                        },
                      cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < iitData.length; i++){
					 			if(value == iitData[i].invoiceItemTypeId){
					 				return \"<span>\" + iitData[i].description + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}
					 },
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', columntype: 'template', width: 150,
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
                     	}
                     },
					 { text: '${uiLabelMap.thruDate}',datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', columntype: 'template', width: 150,
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '150px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
                     	}
                     },
					 { text: '${uiLabelMap.termValue}',datafield: 'termValue', width: 150},
					 { text: '${uiLabelMap.termDays}',datafield: 'termDays', width: 150},
					 { text: '${uiLabelMap.textValue}',datafield: 'textValue', width: 150},
					 { text: '${uiLabelMap.FormFieldTitle_minQuantity}',datafield: 'minQuantity', width: 150},
					 { text: '${uiLabelMap.FormFieldTitle_maxQuantity}',datafield: 'maxQuantity', width: 150},
					 { text: '${uiLabelMap.description}',datafield: 'description', width: 250}
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQListAgreementItemTerms&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementTerm&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementTerm&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=deleteAgreementTerm&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 editColumns="agreementId[${parameters.agreementId}];agreementTermId;termTypeId;agreementItemSeqId[${parameters.agreementItemSeqId}];invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];termTypeId;invoiceItemTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);termValue;termDays;textValue;minQuantity;maxQuantity;description"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];agreementTermId"
		 />
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.termTypeId}:</td>
	 			<td align="left">
	 				<div id="termTypeIdAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.invoiceItemTypeId}:</td>
	 			<td align="left">
	 				<div id="invoiceItemTypeIdAdd">
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
    	 		<td align="right">${uiLabelMap.termValue}:</td>
	 			<td align="left">
	 				<input id="termValueAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.termDays}:</td>
	 			<td align="left">
	 				<input id="termDaysAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.textValue}:</td>
	 			<td align="left">
	 				<input id="textValueAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_minQuantity}:</td>
	 			<td align="left">
	 				<input id="minQuantityAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_maxQuantity}:</td>
	 			<td align="left">
	 				<input id="maxQuantityAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.description}:</td>
	 			<td align="left">
	 				<input id="descriptionAdd">
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
	
	//Create FromDate
	$("#fromDateAdd").jqxDateTimeInput({width: '230px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});
	
	//Create ThruDate
	$("#thruDateAdd").jqxDateTimeInput({width: '230px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});
	$("#thruDateAdd").jqxDateTimeInput('val', null);
	
	//Create termTypeId
	$("#termTypeIdAdd").jqxDropDownList({ selectedIndex: 0,  source: ttData, displayMember: "description", valueMember: "termTypeId", height: 25, width: 230 });
	
	//Create invoiceItemTypeId
	$("#invoiceItemTypeIdAdd").jqxDropDownList({ selectedIndex: 0,  source: iitData, displayMember: "description", valueMember: "invoiceItemTypeId", height: 25, width: 230 });
	
	//Create termValue
	$("#termValueAdd").jqxInput({width: 225});
	
	//Create termDays
	$("#termDaysAdd").jqxInput({width: 225});
	
	//Create textValue
	$("#textValueAdd").jqxInput({width: 225});
	
	//Create minQuantity
	$("#minQuantityAdd").jqxInput({width: 225});
	
	//Create maxQuantity
	$("#maxQuantityAdd").jqxInput({width: 225});
	
	//Create description
	$("#descriptionAdd").jqxInput({width: 225});
	
	$("#alterpopupWindow").jqxWindow({
        width: 550, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    $('#alterpopupWindow').on('open', function (event) {
    	$("#thruDateAdd").jqxDateTimeInput('val', null);
		$("#termValueAdd").jqxInput('val', null);	
		$("#termDaysAdd").jqxInput('val', null);		
		$("#textValueAdd").jqxInput('val', null);
		$("#minQuantityAdd").jqxInput('val', null);
		$("#maxQuantityAdd").jqxInput('val', null);
		$("#descriptionAdd").jqxInput('val', null);    	
	});

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
    	var checkNull = $('#thruDateAdd').val();
    	if (checkNull != '')
    	{
    		checkNull = $('#thruDateAdd').jqxDateTimeInput('getDate').getTime();
    	}
        row = { 
        		termTypeId:$('#termTypeIdAdd').val(),         		
        		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate').getTime(),  			        		
        		thruDate:checkNull,   	
        		invoiceItemTypeId:$('#invoiceItemTypeIdAdd').val(),
        		termValue:$('#termValueAdd').val(),
        		termDays:$('#termDaysAdd').val(),
        		textValue:$('#textValueAdd').val(),
        		minQuantity:$('#minQuantityAdd').val(),
        		maxQuantity:$('#maxQuantityAdd').val(),
        		description:$('#descriptionAdd').val(),
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>