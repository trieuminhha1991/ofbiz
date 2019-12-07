<script>
	var sourceP = { datafields: [
						      { name: 'functionId', type: 'string' },
						      { name: 'code', type: 'string' },
						      { name: 'description', type: 'string' },
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
				   $("#jqxFunctionGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxFunctionGrid").jqxGrid('updatebounddata');
				},
				sortcolumn: 'functionId',
               	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListAccReportFunction',
			};
	
</script>
<script type="text/javascript" src="/delys/images/js/accTargetReport.js"></script>
<div id="jqxPanel" style="width:100%;">
	
	<#if accReportTypes?exists>	
		<select name="reportTypeId" id="reportTypeId" onChange="updateReportConfig();">
			<#list accReportTypes as accReportType>	
				<option value="${accReportType.reportTypeId}">${accReportType.name}</option>
			</#list>
		</select>
	</#if>
	
</div>
<#assign dataField="[{ name: 'targetId', type: 'string' },
					 { name: 'reportId', type: 'string' },
					 { name: 'parentTargetId', type: 'string' },
					 { name: 'name', type: 'string' },
					 { name: 'formula', type: 'string'},
					 { name: 'code', type: 'string' },
					 { name: 'description', type: 'string' },
					 { name: 'demonstration', type: 'string' },
					 { name: 'displaySign', type: 'string' },
					 { name: 'unionSign', type: 'string' },
					 { name: 'displayStyle', type: 'string' },
					 { name: 'orderIndex', type: 'number' },
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.targetCode}', datafield: 'code', width: 100},
					 { text: '${uiLabelMap.targetName}', datafield: 'name', width: 190},
					 { text: '', datafield: 'targetId', hidden: 'true'},
					 { text: '', datafield: 'unionSign', hidden: 'true'},
                     { text: '${uiLabelMap.parentTarget}', datafield: 'parentTargetId', width: 80 },
                     { text: '${uiLabelMap.demonstration}', datafield: 'demonstration',width: 100},
                     { text: '${uiLabelMap.displayOrder}', datafield: 'orderIndex', columntype: 'number', width: 50 },
                     { text: '${uiLabelMap.displayStyle}', datafield: 'displayStyle', width: 80},
					 { text: '${uiLabelMap.description}', datafield: 'description'},
					 { text: '${uiLabelMap.formular}', datafield: 'formula', width: 190, editable: false},
					 { text: 'Edit', datafield: 'Edit', columntype: 'button', cellsrenderer: function () {
                     	return \"Edit\";
                 	 }, buttonclick: function (row) {
                 		 // open the popup window when the user clicks a button.
                     editrow = row;
                  
                     var offset = $(\"#jqxgrid\").offset();
                     $(\"#popupWindowFormula\").jqxWindow({theme: 'olbius', height: 'auto', minHeight: 200,position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
                     // get the clicked row's data and initialize the input fields.
                     var dataRecord = $(\"#jqxgrid\").jqxGrid('getrowdata', editrow);
                   
                     //editrow = $(\"#jqxgrid\").jqxGrid('getrowid', editrow);
                     
                 
                     $(\"#editFormular\").val(dataRecord.formula);
                     
                     // show the popup window.
                     $(\"#popupWindowFormula\").jqxWindow('open');
               		$(\"#jqxFunctionGrid\").off('rowselect').on('rowselect', function (event) {		
					       var args = event.args;
					       
					        var row = $(\"#jqxFunctionGrid\").jqxGrid('getrowdata', args.rowindex);
							var formular = $('#editFormular').val();
							addRowCode(row.code);
							//formular = formular + row.code;
							//$('#editFormular').val(formular);
							
                        
					     });
					  
					
						$('#jqxFunctionGrid').jqxGrid('clearselection');
						$('#jqxgrid').jqxGrid('clearselection');
                	}}
                	
                	
   "/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQListAccReportTarget" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"

	showtoolbar = "true" deleterow="true"
	
	removeUrl="jqxGeneralServicer?sname=removeAccReportTarget&jqaction=D" deleteColumn="targetId;reportId"
/>
<input type="hidden" id="organizationPartyId" name="organizationPartyId" value="${organizationPartyId}"/>
<button id="setDefault" style="float:left;  padding-bottom: 5px;"  onclick="setDefault();">${uiLabelMap.SetDefault}</button>

<button id="cancelRow" style="float:right; padding-bottom: 5px;"  onclick="cancelRow();">${uiLabelMap.Cancel}</button>
<button id="saveRow" style="float:right; padding-bottom: 5px;"  onclick="saveAccTarget();">${uiLabelMap.Save}</button>

<button id="addNewTarget" style="float:right; padding-bottom: 5px;" onclick="addNewTarget();">${uiLabelMap.AddNewRow}</button>


<div id="popupWindowFormulaValidate" >
            <div>${uiLabelMap.ConstructFormular}</div>
            <div style="overflow: hidden;">
            	${uiLabelMap.FormularValid}
            </div>
</div> 
<div id="popupWindowFormulaNotValidate" >
            <div>${uiLabelMap.ConstructFormular}</div>
            <div style="overflow: hidden;">
            	${uiLabelMap.FormularNotValid}
            </div>
  </div> 
<div id="popupWindowFormula" >
            <div>${uiLabelMap.ConstructFormular}</div>
            <div style="overflow: hidden;">
            	<div style="padding-bottom:10px">
            	<textarea name="editFormular" id="editFormular">
            		
            	</textarea>
				<button id="addButton"  onclick="add(); ">${uiLabelMap.AddOperator}</button>
				<button id="minusButton"  onclick="minus(); ">${uiLabelMap.MinusOperator}</button>
				<button id="CancelFormular" style="float:right;" onclick="cancelFormular();">${uiLabelMap.Cancel}</button>
				
            	<button id="validateButton" style="float:right;"  onclick="validate() ;">${uiLabelMap.Validate}</button>
				<button id="AcceptFormular" style="float:right; "  onclick="acceptFormular();">${uiLabelMap.Accept}</button>
            	</div>
                <div id="jqxFunctionGrid"/>
					
            </div>
      
  </div> 
 
<script type="text/javascript">
	var editrow = -1;
	var alterData = new Object();
	var reportTypeId = $('#reportTypeId').val();
	
	function updateReportConfig(){
	 alterData.pagenum = "0";
     alterData.pagesize = "20";
     alterData.noConditionFind = "Y";
     alterData.conditionsFind = "N";	
	 alterData.reportTypeId = $('#reportTypeId').val();
	 $('#jqxgrid').jqxGrid('updatebounddata');
	}
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#validateButton").jqxButton({ template: "primary" });
	
	$("#addButton").jqxButton({ template: "info" });
	$("#minusButton").jqxButton({ template: "info" });
	$("#setDefault").jqxButton({ template: "primary" });
	
	$("#saveRow").jqxButton({ template: "primary" });
	$("#cancelRow").jqxButton({ template: "primary" });
	$("#addNewTarget").jqxButton({ template: "primary" });
	// initialize the popup window and buttons.
    $("#popupWindowFormula").jqxWindow({
        width: 610, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.5           
    });
	$("#popupWindowFormulaNotValidate").jqxWindow({
        width: 610, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.01           
    });
    $("#popupWindowFormulaValidate").jqxWindow({
        width: 610, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.01           
    });
	
	  $("#CancelFormular").jqxButton({ theme: 'olbius' });
      $("#AcceptFormular").jqxButton({ theme: 'olbius' });
    $("#editFormular").jqxInput({
	    height: 100,
	    width: 600
	});  
	//
	
	$("#jqxFunctionGrid").jqxGrid({
		width:600,
		source: sourceP,
		filterable: true,
		virtualmode: true, 
		sortable:true,
		editable: false,
		autoheight:true,
		pageable: true,
		selectionmode: 'singlerow',
		rendergridrows: function(obj)
		{	
			return obj.data;
		},
		columns: 
			[
				{ text: 'Code', datafield: 'code'},
				{ text: 'Description', datafield: 'description'},
			]
		});


</script>