<script>
	<#assign roleTypes = delegator.findList("RoleType", null, null, null, null, false) />
	var roleTypeData = new Array();
	<#list roleTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['roleTypeId'] = '${item.roleTypeId?if_exists}';
		row['description'] = '${description?if_exists}';
		roleTypeData[${item_index?if_exists}] = row;
	</#list>
</script>
<#assign columnlist="{ text: '${uiLabelMap.accPartyId}', dataField: 'partyId', editable:false,
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
					 { text: '${uiLabelMap.roleTypeId}', dataField: 'roleTypeId', editable:false, width: 150,
		              cellsrenderer: function(row, column, value){
		            	  for(i = 0; i < roleTypeData.length; i++){
		            		  if(roleTypeData[i].roleTypeId == value){
		            			  return '<span title' + value + '>' + roleTypeData[i].description +'</span>'
		            		  }
		            	  }
		            	  return ;
		               }
					 },
					 { text: '${uiLabelMap.fromDate}', dataField: 'fromDate', editable:false, cellsformat: 'dd/MM/yyyy', width: 150},
					 { text: '${uiLabelMap.thruDate}', dataField: 'thruDate', editable:true, cellsformat: 'dd/MM/yyyy',width: '150', columntype: 'template',
						 createeditor: function(row, cellvalue, editor){
							 editor.jqxDateTimeInput({width: '150'});
						 }
					 }
					" />
<#assign dataField="[{ name: 'finAccountId', type: 'string' },
                 	{ name: 'roleTypeId', type: 'string' },
                 	{ name: 'partyId', type: 'string' },
					{ name: 'fromDate', type: 'date' },
					{ name: 'thruDate', type: 'date' }
		 		 	]"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="false" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="true" 
		 url="jqxGeneralServicer?sname=JQListFinAccountRole&finAccountId=${parameters.finAccountId}" id="jqxgrid" updateUrl="jqxGeneralServicer?sname=updateFinAccountRole&jqaction=U" editColumns="finAccountId;roleTypeId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 createUrl="jqxGeneralServicer?sname=createFinAccountRole&jqaction=C&finAccountId=${parameters.finAccountId}" addColumns="roleTypeId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);finAccountId[${parameters.finAccountId}]"
		 addrefresh = "true" removeUrl="jqxGeneralServicer?sname=deleteFinAccountRole&jqaction=D&finAccountId=${parameters.finAccountId}" deleteColumn="finAccountId;partyId;roleTypeId;fromDate(java.sql.Timestamp)"
	/>

<!--HTML for add form -->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupWindow" style="display:none;">
			<div id="windowHeader">
	            <span>
	               ${uiLabelMap.NewFinAccountRole} [${parameters.finAccountId}]
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContent">
		        <div class="basic-form form-horizontal" style="margin-top: 10px">
					<form name="formNew" id="formNew">	
						<div class="row-fluid" >
							<div class="span12">
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.accPartyId}:</label>  
									<div class="controls">
										<div id="partyIdAdd">
											<div id="jqxPartyGrid"></div>
										</div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.roleTypeId}:</label>  
									<div class="controls">
										<div id="roleTypeIdAdd"></div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.fromDate}:</label>  
									<div class="controls">
										<div id="fromDateAdd">
										</div>
									</div>
								</div>
								<div class="control-group no-left-margin">
									<label class="control-label">${uiLabelMap.thruDate}:</label>  
									<div class="controls">
										<div id="thruDateAdd">
										</div>
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
<script>
	//Create theme
	$.jqx.theme='olbius';
	theme = $.jqx.theme;
	
	//Create Window popup
	$('#alterpopupWindow').jqxWindow({width: 600, height: 300, autoOpen: false, cancelButton: $('#alterCancel'), modalOpacity: 0.7, modalZIndex: 10000, isModal: true});
	
	//Create partyId
	var sourceP =
	{
			datafields:
				[
				 { name: 'partyId', type: 'string' },
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
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=getFromParty',
	};
	var dataAdapterP = new $.jqx.dataAdapter(sourceP);
	$("#partyIdAdd").jqxDropDownButton({ width: 200, height: 25});
	$("#jqxPartyGrid").jqxGrid({
		source: dataAdapterP,
		filterable: true,
		virtualmode: true,
		showfilterrow: true,
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
      { text: '${uiLabelMap.partyId}', datafield: 'partyId', width: 150},
      { text: '${uiLabelMap.firstName}', datafield: 'firstName', width: 150},
      { text: '${uiLabelMap.lastName}', datafield: 'lastName', width: 150},
      { text: '${uiLabelMap.groupName}', datafield: 'groupName', width: 150}
    ]
	});
	$("#jqxPartyGrid").on('rowselect', function (event) {
		var args = event.args;
		var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
		$("#partyIdAdd").jqxDropDownButton('close');
		$('#partyIdAdd').jqxDropDownButton('setContent', dropDownContent);
	});
	
	//Create roleType
	$('#roleTypeIdAdd').jqxDropDownList({width: 200, source: roleTypeData, selectedIndex: 0, valueMember: 'roleTypeId', displayMember: 'description', theme: theme});
	
	//Create fromDate
	$('#fromDateAdd').jqxDateTimeInput({width: '200', formatString: 'dd/MM/yyyy'});
	
	//Create thruDate
	$('#thruDateAdd').jqxDateTimeInput({width: '200', formatString: 'dd/MM/yyyy'});
	
	$('#alterSave').click(function(){
		var row = {
				partyId: $('#partyIdAdd').val(),
				roleTypeId: $('#roleTypeIdAdd').val(),
				fromDate: $('#fromDateAdd').jqxDateTimeInput('getDate').getTime(),
				thruDate: $('#thruDateAdd').jqxDateTimeInput('getDate').getTime()
		};
		$('#jqxgrid').jqxGrid("addRow", null, row, 'first');
		$('#jqxgrid').jqxGrid("clearSelection");
		$('#alterpopupWindow').jqxWindow('close');
	});
</script>