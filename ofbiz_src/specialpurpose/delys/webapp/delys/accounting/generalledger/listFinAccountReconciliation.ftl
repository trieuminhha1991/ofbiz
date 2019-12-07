<script type="text/javascript">
	<#assign glAccounts = delegator.findList("GlAccount", null, null, null, null, false)>
	var glAccountData = new Array();
	<#list glAccounts as item>
		<#assign description = StringUtil.wrapString(item.accountName?if_exists + "[" + item.accountCode?if_exists + "]")>
		var row = {};
		row['glAccountId'] = '${item.glAccountId?if_exists}';
		row['description'] = '${description?if_exists}';
		glAccountData[${item_index}] = row;
	</#list>
</script>

<!-- HTML for Party Lookup -->
<div id="jqxwindoworganizationPartyId">
	<div>${uiLabelMap.LookupTitle_Party}</div>
	<div style="overflow: hidden;">
		<table>
			<tr>
				<td>
					<input type="hidden" id="jqxwindoworganizationPartyIdkey" value=""/>
					<input type="hidden" id="jqxwindoworganizationPartyIdvalue" value=""/>
					<div id="jqxgridorganizationPartyId"></div>
				</td>
			</tr>
			<tr>
	        	<td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave1" value="${uiLabelMap.CommonSave}" /><input id="alterCancel1" type="button" value="${uiLabelMap.CommonCancel}" /></td>
	        </tr>
	   </table>
	</div>
</div>

<@jqGridMinimumLib/>

<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	//Create Popup Window
	$("#jqxwindoworganizationPartyId").jqxWindow({
		theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
	});
	
	//Create Button
	$('#alterCancel1').jqxButton({theme: theme, width: 100});
	$('#alterSave1').jqxButton({theme: theme, width: 100});
	
	$('#jqxgridorganizationPartyId').on('open', function (event) {
		var offset = $("#jqxgrid").offset();
		$("#jqxgridorganizationPartyId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});

	$("#alterSave1").click(function () {
		var tIndex = $('#jqxgridorganizationPartyId').jqxGrid('selectedrowindex');
		var data = $('#jqxgridorganizationPartyId').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindoworganizationPartyIdkey').val()).val(data.partyId);
		$("#jqxwindoworganizationPartyId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindoworganizationPartyIdkey').val()).trigger(e);
	});
	// Prepare party source
	var sourceF =
	{
			datafields:
				[
				 { name: 'partyId', type: 'string' },
				 { name: 'partyTypeId', type: 'string' },
				 { name: 'firstName', type: 'string' },
				 { name: 'lastName', type: 'string' },
				 { name: 'groupName', type: 'string' }
				],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
				sourceF.totalrecords = data.TotalRows;
			},
			filter: function () {
				// update the grid and send a request to the server.
				$("#jqxgridorganizationPartyId").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
				// callback called when a page or page size is changed.
			},
			sort: function () {
				$("#jqxgridorganizationPartyId").jqxGrid('updatebounddata');
			},
			sortcolumn: 'partyId',
			sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:15,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getListParty',
	};
	var dataAdapterF = new $.jqx.dataAdapter(sourceF,
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
					if (!sourceF.totalRecords) {
						sourceF.totalRecords = parseInt(data['odata.count']);
					}
				}
			});
	$('#jqxgridorganizationPartyId').jqxGrid(
			{
				width:800,
				source: dataAdapterF,
				pagesize: 15,
				filterable: true,
				virtualmode: true, 
				sortable:true,
				editable: false,
				showfilterrow: false,
				theme: theme, 
				autoheight:true,
				pageable: true,
				pagesizeoptions: ['15', '30', '50'],
				ready:function(){
				},
				rendergridrows: function(obj)
				{
					return obj.data;
				},
				columns: [
				          { text: '${uiLabelMap.partyId}', datafield: 'partyId', width:150},
				          { text: '${uiLabelMap.partyTypeId}', datafield: 'partyTypeId', width:200},
				          { text: '${uiLabelMap.firstName}', datafield: 'firstName', width:150},
				          { text: '${uiLabelMap.lastName}', datafield: 'lastName', width:150},
				          { text: '${uiLabelMap.groupName}', datafield: 'groupName', width:150}
				         ]
			});

	$(document).keydown(function(event){
		if(event.ctrlKey)
			cntrlIsPressed = true;
	});

	$(document).keyup(function(event){
		if(event.which=='17')
			cntrlIsPressed = false;
	});
	var cntrlIsPressed = false;
</script>
<#assign dataField="[{ name: 'glReconciliationId', type: 'string' },
					 { name: 'glReconciliationName', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'createdDate', type: 'date'},
					 { name: 'lastModifiedDate', type: 'date'},
					 { name: 'glAccountId', type: 'string'},
					 { name: 'organizationPartyId', type: 'string'},
					 { name: 'reconciledBalance', type: 'string'},
					 { name: 'openingBalance', type: 'string'},
					 { name: 'reconciledDate', type: 'date'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_glReconciliationId}', datafield: 'glReconciliationId', width: 150, filtertype:'input',
						cellsrenderer: function (row, column, value){
							return '<span> <a href=' + 'EditGlReconciliation?glReconciliationId='+ value + '&organizationPartyId=company' + '>' + value + '</a></span>'
						}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_glReconciliationName}', datafield: 'glReconciliationName', width: 150, filtertype: 'input'},
					 { text: '${uiLabelMap.FormFieldTitle_glAccountId}', datafield: 'glAccountId', width: 150, filtertype:'checkedlist',filterable: true,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < glAccountData.length; i++){
								 if(glAccountData[i].glAccountId == value){
									 return '<span>' + glAccountData[i].description + '</span>';
								 }
							 }
							 return value;
						 },
						createfilterwidget: function(column, columnElement, widget){
							var filterBoxAdapter = new $.jqx.dataAdapter(glAccountData,{
						            autoBind: true
						        });
						    var uniqueRecords = filterBoxAdapter.records;
						   	uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: uniqueRecords, displayMember: 'glAccountId', valueMember: 'glAccountId',
							renderer: function(index, label, value){
								for(var i = 0; i < uniqueRecords.length; i++){
									if(uniqueRecords[i].glAccountId == value){
										return uniqueRecords[i].description;
									}
								}
								return value;
							}
						});
							widget.jqxDropDownList('checkAll');
						}
					 },
					 { text: '${uiLabelMap.FormFieldTitle_organizationPartyId}', datafield: 'organizationPartyId', width: 150, filtertype: 'olbiusdropgrid',
						 cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
						 }
					 },
					 { text: '${uiLabelMap.description}', datafield: 'description', width: 150, filterable: false},
					 { text: '${uiLabelMap.FormFieldTitle_createdDate}', datafield: 'createdDate', width: 150, filterable: false, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.FormFieldTitle_lastModifiedDate}', datafield: 'lastModifiedDate', width: 150, filterable: false, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.FormFieldTitle_reconciledBalance}', datafield: 'reconciledBalance', width: 150, filterable: false},
					 { text: '${uiLabelMap.FormFieldTitle_openingBalance}', datafield: 'openingBalance', width: 150, filterable: false},
					 { text: '${uiLabelMap.FormFieldTitle_reconciledDate}', datafield: 'reconciledDate', width: 150, filterable: false, cellsformat:'dd/MM/yyyy'}"
					 />
<@jqGrid url="jqxGeneralServicer?sname=JQListFinAccountRecon&finAccountId=${parameters.finAccountId}" dataField=dataField columnlist=columnlist id="jqxgrid" jqGridMinimumLibEnable="true"
		 addrow="true" filtersimplemode="true" addrefresh="true" addType="popup" alternativeAddPopup="alterpopupWindow" clearfilteringbutton="true" showtoolbar="true" filterable="true"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createGlReconciliation" 
		 addColumns="statusId[GLREC_CREATED];finAccountId[${parameters.finAccountId}];glReconciliationName;description;createdDate(java.sql.Timestamp);lastModifiedDate(java.sql.Timestamp);organizationPartyId;openingBalance(java.math.BigDecimal);reconciledDate(java.sql.Timestamp)"
		/>

<!-- HTML Add New -->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupWindow" style="display:none;">
			<div id="windowHeader">
	            <span>
	               ${uiLabelMap.NewFinAccountRecon}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContent">
		        <div class="basic-form form-horizontal" style="margin-top: 10px">
					<form name="formNew" id="formNew">	
						<div class="row-fluid" >
							<div class="span12">
								<div class='span6'>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.glReconciliationName}:</label>
										<div class="controls">
											<input id="glReconciliationName"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.description}:</label>  
										<div class="controls">
											<input id="description"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.createdDate}:</label>
										<div class="controls">
											<div id="createdDate"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.lastModifiedDate}:</label>  
										<div class="controls">
											<div id="lastModifiedDate"></div>
										</div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label asterisk">${uiLabelMap.organizationPartyId}:</label>  
									<div class="controls">
										<div id="organizationPartyId">
											<div id="jqxPartyGrid" />
										</div>
									</div>
								</div>
								
								<div class="control-group no-left-margin">
									<label class="control-label asterisk">${uiLabelMap.openingBalance}:</label>  
									<div class="controls">
										<input id="openingBalance"></input>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label asterisk">${uiLabelMap.reconciledDate}:</label>  
									<div class="controls">
										<div id="reconciledDate"></div>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
							<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
						</div>
					</div>
				</div>
	        </div>
		</div>
	</div>
</div>
<script type="text/javascript">
	//Create theme
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	$('#alterpopupWindow').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "95%", height: 400, minWidth: '40%', width: "55%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel'});
	
	//Create glReconciliationName
	$('#glReconciliationName').jqxInput({width: '195px'});

	//Create description
	$('#description').jqxInput({width: '195px'});

	//Create createdDate
	$('#createdDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy'});

	//Create lastModifiedDate
	$('#lastModifiedDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy'});

	//Create organizationPartyId
	var sourceP =
	{
		datafields:
			[
			 { name: 'partyId', type: 'string' },
			 { name: 'partyTypeId', type: 'string' },
			 { name: 'firstName', type: 'string' },
			 { name: 'lastName', type: 'string' },
			 { name: 'groupName', type: 'string' }
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
		pagesize:15,
		contentType: 'application/x-www-form-urlencoded',
		url: 'jqxGeneralServicer?sname=getListParty',
	};
	var dataAdapterP = new $.jqx.dataAdapter(sourceP,{
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
            }else{
            	data.filterListFields = null;
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
	$("#organizationPartyId").jqxDropDownButton({height: 25});
	$("#jqxPartyGrid").jqxGrid({
		source: dataAdapterP,
		filterable: true,
		showfilterrow: true,
		virtualmode: true, 
		sortable:true,
		theme: theme,
		editable: false,
		autoheight:true,
		pageable: true,
		rendergridrows: function(obj)
		{
			return obj.data;
		},
		columns: [
		          { text: '${uiLabelMap.accPartyId}', datafield: 'partyId', width: 150},
		          { text: '${uiLabelMap.firstName}', datafield: 'firstName', width: 150},
		          { text: '${uiLabelMap.lastName}', datafield: 'lastName', width: 150},
		          { text: '${uiLabelMap.groupName}', datafield: 'groupName', width: 150}
		          ]
		});
	$("#jqxPartyGrid").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		$("#organizationPartyId").jqxDropDownButton('close');
		$("#organizationPartyId").jqxDropDownButton('setContent', dropDownContent);
	});
	
	//Create openingBalance
	$("#openingBalance").jqxInput({width: 195});
	
	//Create reconciledDate
	$('#reconciledDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy'});

	// update the edited row when the user clicks the 'Save' button.
	$("#alterSave").click(function () {
		var row;
		row = { 
				glReconciliationName:$('#glReconciliationName').val(),
				description:$('#description').val(),
				createdDate:$('#createdDate').jqxDateTimeInput('getDate').getTime(),
				lastModifiedDate:$('#lastModifiedDate').jqxDateTimeInput('getDate').getTime(),
				organizationPartyId:$('#organizationPartyId').val(),
				openingBalance:$('#openingBalance').val(),
				reconciledDate:$('#reconciledDate').jqxDateTimeInput('getDate').getTime(),
	  	};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		// select the first row and clear the selection.
		$("#jqxgrid").jqxGrid('clearSelection');                        
		$("#jqxgrid").jqxGrid('selectRow', 0);  
		$("#alterpopupWindow").jqxWindow('close');
	});
</script>