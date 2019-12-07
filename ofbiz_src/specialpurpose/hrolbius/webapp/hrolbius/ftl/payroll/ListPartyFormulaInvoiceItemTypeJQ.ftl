<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script type="text/javascript" src="/delys/images/js/marketing/utils.js"></script>
<style>
.backgroundWhiteColor{
	background-color: #fff !important
}
</style>
<script>
var codes = [
	<#list formulaList as formula>
		{
			code: "${formula.code}",
			name: "${StringUtil.wrapString(formula.name?default(''))}"
		},
	</#list>
];

function initSourceJqxDataTable(){
	var source = {
			dataType: "json",
			dataFields: [
				{name: 'code', type: 'string' },
				{name: 'groupName', type: 'string' },
				{name: 'description', type: 'string' },
				{name: 'partyId', type: 'string' },
				{name: 'invoiceItemTypeId', type: 'string'},
				{name: 'fromDate', type: 'date'},
				{name: 'thruDate', type: 'date'}			             
			],
			root: 'listReturn',
            type: 'POST',	   
            url: 'getPartyFormulaInvoiceItemType',  
            addRow: function (rowID, rowData, position, commit){
            	$('#jqxDataTable').jqxDataTable({disabled: true});
            	$.ajax({
            		url: 'createPartyFormulaInvoiceItemTypeJQ',
            		data: rowData,
            		type: 'POST',
            		async: false,
            		success: function(response){
            			$("#jqxNtf").jqxNotification('closeLast');            			
            			if(response.responseMessage == 'success'){            				
            				$("#jqxNtfContent").text(response.successMessage);
            				$("#jqxNtf").jqxNotification({template: 'info'});
            				$("#jqxNtf").jqxNotification("open");
                			//commit(true);
            			}else{
            				$("#jqxNtfContent").text(response.errorMessage);
            				$("#jqxNtf").jqxNotification({template: 'error'});
            				$("#jqxNtf").jqxNotification("open");
            				//commit(false);
            			}
            		},
            		error: function(jqXHR, textStatus, errorThrown){
            			commit(false);
            		},
            		complete: function(jqXHR, textStatus){
            			$('#jqxDataTable').jqxDataTable({disabled:false});            			
            			$('#jqxDataTable').jqxDataTable('updateBoundData');
            		}
            	});
            	//commit(true);
            },
            updateRow: function (rowId, rowData, commit) {
            	$('#jqxDataTable').jqxDataTable({disabled: true});
            	var dataSubmit = {};
        		var fromDate = rowData.fromDate.getTime();
        		var thruDate = rowData.thruDate;
        		if(thruDate){
        			dataSubmit.thruDate = thruDate.getTime();            			
        		}
        		dataSubmit.fromDate = fromDate;
        		dataSubmit.partyId = rowData.partyId;
        		dataSubmit.code = rowData.code;
        		dataSubmit.invoiceItemTypeId = rowData.invoiceItemTypeId;
        		
            	$.ajax({
            		url: 'updatePartyFormulaInvoiceItemType',
            		data: dataSubmit,
            		type: 'POST',
            		async: false,
            		success: function(response){
            			$("#jqxNtf").jqxNotification('closeLast');            			
            			if(response.responseMessage == 'success'){            				
            				$("#jqxNtfContent").text(response.successMessage);
            				$("#jqxNtf").jqxNotification({template: 'info'});
            				$("#jqxNtf").jqxNotification("open");
                			//commit(true);
            			}else{
            				$("#jqxNtfContent").text(response.errorMessage);
            				$("#jqxNtf").jqxNotification({template: 'error'});
            				$("#jqxNtf").jqxNotification("open");
            				//commit(false);
            			}
            		},
            		error: function(jqXHR, textStatus, errorThrown){
            			commit(false);
            		},
            		complete: function(jqXHR, textStatus){
            			$('#jqxDataTable').jqxDataTable({disabled:false});            			
            			$('#jqxDataTable').jqxDataTable('updateBoundData');
            		}
            	});
            },
            deleteRow: function (rowID, commit) {
            	commit(true);
            }
	}
	return source;
}

function initColumnlistJqxDataTable(){
	var columnlist = [
			{text: '${uiLabelMap.FormulaCode}',  filtertype: 'checkedlist', datafield: 'code', width: 290, editable: false, cellClassName: 'backgroundWhiteColor',
				createfilterwidget: function(column, columnElement, widget){
					//var filterBoxAdapter = new $.jqx.dataAdapter(invoiceItems, {autoBind: true});
					var filterBoxAdapter = new $.jqx.dataAdapter(codes, {autoBind: true});
					var dataSoureList = filterBoxAdapter.records;
				    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					widget.jqxDropDownList({
						theme: 'olbius',
						source: dataSoureList,
						width: '98%',
						dropDownHeight: 200,
						autoDropDownHeight: false,
						displayMember: 'name',
						valueMember: 'code'
					});
				},
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < codes.length; i++){
						if(value == codes[i].code){
							return '<span>' + codes[i].name + ' [' + value +']'+ '</span>';
						}
					}
					return '<span>' + value + '</span>';
				}
			},
			{text: '${uiLabelMap.Department}', filterable: false, datafield: 'partyId', hidden: true, editable: false},
			{datafield: 'groupName', hidden: true, editable: false},
			{text: '${uiLabelMap.FormFieldTitle_perfReviewItemTypeId}', filtertype: 'checkedlist', datafield: 'invoiceItemTypeId', 
				cellClassName: 'backgroundWhiteColor',
				 width: 500, editable: false,
				 createfilterwid00get: function(column, columnElement, widget){
				    var filterBoxAdapter = new $.jqx.dataAdapter(invoiceItems, {autoBind: true});
					var dataSoureList = filterBoxAdapter.records;
				    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', dropDownHeight: 300, autoDropDownHeight: false, valueMember : 'invoiceItemTypeId',  filterable:true, searchMode:'containsignorecase'});
				},
				cellsrenderer : function(row, column, value, rowData){
					//var val = $('#jqxDataTable').jqxDataTable('getrowdata', row);
					return '<div style="margin-top: 6px; margin-left: 4px;">' + rowData.description +' [' + rowData.invoiceItemTypeId + ']' +'</div>';
				}
			},
			{text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', 
				 editable: false, width: 130, cellClassName: 'backgroundWhiteColor'},
			{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', editable: true,
				cellClassName: 'backgroundWhiteColor', columntype: 'custom',
				createEditor: function (row, cellValue, editor, width, height) {
					var fromDate = $("#jqxDataTable").jqxDataTable('getCellValue', row, 'fromDate');					
					editor.jqxDateTimeInput({width: '95%', height: '100%', theme: 'olbius', min: fromDate, dropDownHorizontalAlignment: 'right'});
				},
				initEditor: function (row, cellValue, editor) {
                    // set jqxInput editor's initial value.
                    if(cellValue){
                    	editor.val(cellValue);
                    }else{
                    	editor.val(null);
                    }
                },
                getEditorValue: function (index, value, editor) {
                    // get jqxInput editor's value.
                    return editor.jqxDateTimeInput('val', 'date');
                },
                
			}                 
	];
	return columnlist;
}

function createJqxDataTable(){
	var source = initSourceJqxDataTable();
	var columns = initColumnlistJqxDataTable();
	var dataAdapter = new $.jqx.dataAdapter(source, {
	        loadComplete: function () {	        	
	            // data is loaded.
	        },
	        downloadComplete: function (data, status, xhr) {
	            if (data){	            	
	                source.totalRecords = data.totalRows;
	            }
	        },
	        loadError: function (xhr, status, error) {
	            throw new Error(error.toString());
	        } 
	});
	$("#jqxDataTable").jqxDataTable({
			width: '100%',		
			pagerMode: "advanced",
		    pageable: true,
		    theme: 'olbius',
		    editable: true,
		    serverProcessing: true,
		    source: dataAdapter,
		    altRows: true,
		    pageSize: 15,
		    groups: ['partyId'],		    
		    columnsResize: true,
		    editSettings: { saveOnPageChange: true, saveOnBlur: true, saveOnSelectionChange: true,
		    		cancelOnEsc: true, saveOnEnter: true, editSingleCell: true, editOnDoubleClick: true, editOnF2: true},
		    groupsRenderer: function(value, rowData, level){                     	
		        return '<b>${StringUtil.wrapString(uiLabelMap.Department)}: ' + rowData.data.groupName + '</b>';
		    },
		    columns:columns
	 });
}

function initBtnEvent(){
	$("#addNewBtn").click(function(event){
		$("#popupAddRow").jqxWindow('open');
	});
	$("#cancelBtn").click(function(){
		popup.jqxWindow('close');
	});
	$("#alterSave").click(function () {
		if(!$('#popupAddRow').jqxValidator('validate')){
			return;
		}
		var fromDate = $('#fromDateJQ').jqxDateTimeInput('val', 'date').getTime();
		var i = $('#invoiceItemTypeIdJQ').jqxDropDownList('getSelectedItem');
		var j = $('#codeadd').jqxDropDownList('getSelectedItem');
		var invoiceItemTypeId = i ? i.value : "";
		var code = j.value;
		var partyIdChoose = $("#jqxTree").jqxTree('getSelectedItem');
		var partyIdSubmitArr = new Array();
		partyIdSubmitArr.push({partyId: partyIdChoose.value});
		
    	var row = { 
       		invoiceItemTypeId: invoiceItemTypeId,
       		partyListId: JSON.stringify(partyIdSubmitArr),
       		fromDate: fromDate,
       		code: code,
       	  };
    	$("#jqxDataTable").jqxDataTable('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxDataTable").jqxDataTable('clearSelection');                        
        //$("#jqxDataTable").jqxDataTable('selectRow', 0);  
        $("#popupAddRow").jqxWindow('close');
    });
	
	$("#deleteBtn").click(function(event){
		var selectionArr = $("#jqxDataTable").jqxDataTable('getSelection');
		if(selectionArr.length > 0){
			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.NotifyDelete)}",
				[{
					"label" : "${uiLabelMap.CommonSubmit}",
	    		    "class" : "btn-primary btn-mini icon-ok",
	    		    "callback": function() {
	    		 		deletePartyFormulaInvoiceItemType(selectionArr[0]);   	
	    		    }	
				},
				{
	    		    "label" : "${uiLabelMap.CommonClose}",
	    		    "class" : "btn-danger btn-mini icon-remove",
	    		    "callback": function() {
	    		    	
	    		    }
	    		}]		
			);
			
		}
	});
}

function deletePartyFormulaInvoiceItemType(rowData){
	//var rowData = selectionArr[0];
	var rowId = rowData.uid;
	var dataSubmit = {};
	var fromDate = rowData.fromDate.getTime();
	dataSubmit.partyId = rowData.partyId;
	dataSubmit.code = rowData.code;
	dataSubmit.invoiceItemTypeId = rowData.invoiceItemTypeId;
	dataSubmit.fromDate = fromDate;
	$.ajax({
		url: 'deletePartyFormulaInvoiceItemTypeJQ',
		data: dataSubmit,
		type: 'POST',
		async: false,
		success: function(response){
			$("#jqxNtf").jqxNotification('closeLast');            			
			if(response.responseMessage == 'success'){            				
				$("#jqxNtfContent").text(response.successMessage);
				$("#jqxNtf").jqxNotification({template: 'info'});
				$("#jqxNtf").jqxNotification("open");
				$("#jqxDataTable").jqxDataTable('updateBoundData');            			
			}else{
				$("#jqxNtfContent").text(response.errorMessage);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			commit(false);
		},
		complete: function(jqXHR, textStatus){
			$('#jqxDataTable').jqxDataTable({disabled:false});            			
			//$('#jqxDataTable').jqxDataTable('updateBoundData');
		}
	});
}

function initJqxNotification(){
	$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
}

function initJqxValidator(){
	$("#popupAddRow").jqxValidator({
	   	rules: [{
		    input: '#inputfromDateJQ',
			message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
			action: 'blur',
			rule: 'required'
		},{
               input: "#codeadd", 
               message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
               action: 'blur', 
               rule: function (input, commit) {
                   var index = $("#codeadd").jqxDropDownList('getSelectedIndex');
                   return index != -1;
               }
           }, {
               input: "#invoiceItemTypeIdJQ", 
               message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
               action: 'blur', 
               rule: function (input, commit) {
                   var index = $("#invoiceItemTypeIdJQ").jqxDropDownList('getSelectedIndex');
                   return index != -1;
               }
           }, {
               input: "#jqxDropDownButton", 
               message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
               action: 'blur', 
               rule: function (input, commit) {
                   var val = $("#jqxTree").jqxTree('getSelectedItem');
                   if(!val){
                   	return false;
                   }
                   return true; 
               }
           }],
	 });
}

$(document).ready(function(){
	var popup = $("#popupAddRow");
	popup.jqxWindow({
        width: 550, height: 260, resizable: true, isModal: true, autoOpen: false, theme: 'olbius'         
    });
    popup.on('close', function (event) { 
    	popup.jqxValidator('hide');
    }); 
    var invoiceItemTypes = $('#invoiceItemTypeIdJQ');
	invoiceItemTypes.jqxDropDownList({
		theme: 'olbius',
		source: invoiceItems,
		width: '99%',
		displayMember: "description",
		valueMember: "invoiceItemTypeId"
	});
	
	var codeadd = $('#codeadd');
	codeadd.jqxDropDownList({
		theme: 'olbius',
		source: codes,
		width: "99%",
		displayMember: "name",
		valueMember: "code"
	});
	$("#fromDateJQ").jqxDateTimeInput({
	     height: '24px',
	     width: '99%',
	     theme: 'olbius'		    
	});
	createJqxDataTable();
	initBtnEvent();
	initJqxNotification();
	initJqxValidator();
});

</script>
<#-- <@jqGrid url="jqxGeneralServicer?sname=JQGetPartyFormulaInvoiceItemType" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	autorowheight="true" jqGridMinimumLibEnable="false"
	showtoolbar = "true" deleterow="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyFormulaInvoiceItemType" editColumns="partyId;invoiceItemTypeId;code;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	removeUrl="jqxGeneralServicer?sname=deletePartyFormulaInvoiceItemType&jqaction=D" deleteColumn="partyId;invoiceItemTypeId;code;fromDate(java.sql.Timestamp)"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyFormulaInvoiceItemType" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="invoiceItemTypeId;code;partyListId;fromDate(java.sql.Timestamp)" editmode="selectedcell" addrefresh="true"
/> -->
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ListPartyFormulaInvoiceItemType}</h4>
		<div class="widget-toolbar none-content" style="width: 550px">
			<button id="deleteBtn" class="grid-action-button icon-trash" style="float: right;">${uiLabelMap.accDeleteSelectedRow}</button>
			<button id="addNewBtn" class="grid-action-button icon-plus-sign" style="float: right;">${uiLabelMap.accAddNewRow}</button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div id="jqxDataTable"></div>
		</div>
	</div>
</div>
	
<div id="popupAddRow" style="display: none;">
    <div>${uiLabelMap.CommonAddSetting}</div> 
    <div class='form-window-container'>
    	<div class='form-window-content'>
	    	<form name="popupAddRow" action="" id="popupAddRowForm" class="form-horizontal">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">${uiLabelMap.formulaName}</label>
					</div>
					<div class="span7">
						<div id="codeadd">
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">${uiLabelMap.AccountingInvoicePurchaseItemType}</label>
					</div>
					<div class="span7">
						<div id='invoiceItemTypeIdJQ'>
							<script>
								var invoiceItems = [
									<#list invoiceItemTypeList as invoiceItemType>	
										{
											invoiceItemTypeId: "${invoiceItemType.invoiceItemTypeId}",
											description : "${StringUtil.wrapString(invoiceItemType.description?default(''))}"
										},
									</#list>
								];
							</script>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">${uiLabelMap.Department}</label>
					</div>
					<div class="span7">
						<div id="jqxDropDownButton" class="pull-right">
							<div style="border: none;" id="jqxTree">
							</div>
						</div>
											
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">${uiLabelMap.AvailableFromDate}</label>
					</div>
					<div class="span7">
						<div id="fromDateJQ"></div>
					</div>
				</div>
	    	</form>
    	</div>
    	<div class="form-action">
			<button id="cancelBtn" type="button" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave"><i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
    </div>
</div> 
<#if expandedList?has_content>
		<#assign expandTreeId=expandedList[0]>
	<#else>
		<#assign expandTreeId="">
	</#if> 
<@htmlTemplate.renderJqxTreeDropDownBtn expandTreeId=expandTreeId treeDropDownSource=treePartyGroup id="jqxTree" 
	jqxTreeSelectFunc="jqxTreeSelectFunc" isDropDown="true" dropdownBtnId="jqxDropDownButton"  expandAll="false"/>
<script type="text/javascript">
	
function jqxTreeSelectFunc(event){
	var dataField = event.args.datafield;
	var rowBoundIndex = event.args.rowindex;
	var id = event.args.element.id;
	var item = $('#jqxTree').jqxTree('getItem', event.args.element);
	setDropdownContent(event.args.element);
}	
	
function setDropdownContent(element){
	var item = $("#jqxTree").jqxTree('getItem', element);
	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
	$("#jqxDropDownButton").jqxDropDownButton('setContent', dropDownContent);
}
	
$(document).ready(function(){	
	
	
});
function openPopupCreatePartySkill(){
	$("#popupAddRow").jqxWindow('open');
}
</script>