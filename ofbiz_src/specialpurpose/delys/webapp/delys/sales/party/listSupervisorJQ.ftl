<script src="/delys/images/js/generalUtils.js"></script>
<#assign dataField = "[
		{name : 'partyId', type : 'string'},
		{name : 'birthDate', type : 'date', other : 'Timestamp'},
		{name : 'fullName', type : 'string'},
		{name : 'fromDate', type : 'date', other : 'Timestamp'},
		{name : 'thruDate', type : 'date', other :'Timestamp'}
	]"/>
		
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.DASupId)}', datafield : 'partyId', width : '10%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DASupName)}', datafield : 'fullName', width : '35%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DABirthday)}', datafield : 'birthDate', width : '15%', cellsformat : 'dd/MM/yyyy'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', datafield : 'fromDate', width : '20%', cellsformat : 'dd/MM/yyyy HH:mm:ss'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', datafield : 'thruDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'}
	"/>	
<@jqGrid dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
		 deleterow="true" mouseRightMenu="true" contextMenuId="contextMenu"
		 url="jqxGeneralServicer?sname=JQGetListSupervisorsBySA"
	/>
<div id="contextMenu" style="display:none;">
	<ul>
		<li><i class="fa fa-bus"></i>${StringUtil.wrapString(uiLabelMap.DARepositionSalesman)}</li>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function(event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
        var tmpKey = $.trim($(args).text());
        if(tmpKey == '${StringUtil.wrapString(uiLabelMap.DARepositionSalesman)}'){
        	var partyId = data.partyId;
        	$("#alterpopupWindowlistSalesmanOfSup").jqxWindow('open');
        	$("#SupId").text(partyId);
        	var dataFieldSalesmanOfSup = [
        	                              {name : "partyId", type: "String"},
        	                              {name : "fullName", type : "String"},
        	                              {name : "birthDate", type : "date"},
        	                              {name : "fromDate", type : "date"},
        	                              {name : "thruDate", type : "date"}
        	                      ];
        	var columnSalesmanOfSup = [
                       {text : '${StringUtil.wrapString(uiLabelMap.DASalesmanId)}', width:'15%', datafield :'partyId'},
                       {text : '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield : 'fullName'},
                       {text : '${StringUtil.wrapString(uiLabelMap.DABirthday)}', width : '15%', datafield : 'birthDate', cellsformat : 'dd/MM/yyyy'},
                       {text : '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', width : '20%', datafield : 'fromDate', cellsformat : 'dd/MM/yyyy'},
                       {text : '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', width: '20%', datafield : 'thruDate',cellsformat : 'dd/MM/yyyy'}
               ];
        	GridUtils.initGrid({url : 'JQGetSalesmanOfSup&partyId='+partyId, width : '100%',showfilterrow : true,editable: false,pageable : true,columnsresize: true,selectionmode : 'checkbox',localization: getLocalization(),autoHeight : true},dataFieldSalesmanOfSup,columnSalesmanOfSup,null,$('#GridSalesmanOfSup'));
        	var dataFielListSups = [
                    {name : "SupGroupId", type :"String"},
                    {name : "ManagerId", type : "String"},
                    {name : "ManagerName", type : "String"}
            ];
        	var columnListSups = [
                      {text : "${StringUtil.wrapString(uiLabelMap.DASupId)}", width : "20%", datafield : "SupGroupId"},
                      {text : "${StringUtil.wrapString(uiLabelMap.DAMangagerId)}",width : "20%", datafield : "ManagerId"},
                      {text : "${StringUtil.wrapString(uiLabelMap.DASupName)}", datafield : "ManagerName"}
              ];
        	GridUtils.initGrid({url : 'JQGetSupToTransfer', width : 480, showfilterrow : true,editable: false,pageable : true,columnsresize: true,localization: getLocalization(),autoHeight : true},dataFielListSups,columnListSups,null,$('#GridListSups'));
        }
        $('#GridListSups').on('rowselect', function(event){
        	 var args = event.args;
	   		 var row = $("#GridListSups").jqxGrid('getrowdata', args.rowindex);
	   		 var SupGroupId = row.SupGroupId;
	   		 var partyId = row.ManagerId;
	   		 var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['SupGroupId'] +  '</div>';
	   		 $("#ListSups").jqxDropDownButton('setContent', dropDownContent);
	   		 var source = $('#GridSalesmanOfSupToTransfer').jqxGrid('source');
	   		 source._source.url = 'jqxGeneralServicer?sname=JQGetSalesmanOfSup&partyId='+partyId;
	//   		 source._source.url = "jqxGeneralServicer?sname=JQGetSalesmanOfSup$partyId="+partyId;
	   		 $("#GridSalesmanOfSupToTransfer").jqxGrid('updatebounddata');
        })
	});
</script>
<div id="notification" style="display:none;"></div>
<div id="alterpopupWindowlistSalesmanOfSup" style="display:none;">
	<div>${uiLabelMap.DARouteSchedule}</div>
	<div style="overflow:hidden;">
		<form id="alterpopupWindowlistSalesmanOfSupForm" class="form-horizontal">
			<div class="row-fluid form-window-content">
				<div id="msgSalesmanOfSup"></div>
				<div class="span6" style="margin-left:-11px">
					<div class="row-fluid form-window-content">
						<div class='span3 align-right asterisk'>
							${uiLabelMap.DASupId}
				        </div>
				        <div class="span9">
				        	<div id="SupId"></div>
				        </div>
					</div>
				</div>
				<div class="span12" style="margin-left:0px;">
					<div class="row-fluid margin-bottom10">
						<div id="GridSalesmanOfSup"></div>
					</div>
				</div>
				<div class="span6" style="margin-left:-16px;">
					<div class="row-fluid form-window-content">
						<div class='span5 align-right asterisk'>
							${uiLabelMap.DARepositionToSup}
				        </div>
				        <div class='span7'>
				        	<div id="ListSups">
				        		<div id="GridListSups"></div>
				        	</div>
				        </div>
					</div>
				</div>
				<div class="span12" style="margin-left:0px;">
					<div class="row-fluid margin-bottom10">
						<div id="GridSalesmanOfSupToTransfer"></div>
					</div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<button id="cancelSalesmanOfSup" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.DACancel}</button>
			<button id="alterSaveSalesmanOfSup" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
//	create alterpopupWindowlistSalesmanOfSup
	$("#alterpopupWindowlistSalesmanOfSup").jqxWindow({width : 720, height : 480,isModal: true,autoOpen: false, modalOpacity : 0.8});
	$("#cancelSalesmanOfSup").click(function(){
		$("#alterpopupWindowlistSalesmanOfSup").jqxWindow('close');
		$("#GridSalesmanOfSup").jqxGrid('clear');
		$("#GridSalesmanOfSupToTransfer").jqxGrid('clear');
		$("#ListSups").jqxDropDownButton('setContent', null);
	})
	$("#ListSups").jqxDropDownButton({width : 125, height : 25});
	$("#GridSalesmanOfSupToTransfer")
	var dataFieldSalesmanOfSupToTransfer = [
              {name : "partyId", type: "String"},
              {name : "fullName", type : "String"},
              {name : "birthDate", type : "date"},
              {name : "fromDate", type : "date"},
              {name : "thruDate", type : "date"}
    ];
	var colummnSalesmanOfSupToTransfer = [
			{text : '${StringUtil.wrapString(uiLabelMap.DASalesmanId)}', width:'15%', datafield :'partyId'},
			{text : '${StringUtil.wrapString(uiLabelMap.DAFullName)}', datafield : 'fullName'},
			{text : '${StringUtil.wrapString(uiLabelMap.DABirthday)}', width : '15%', datafield : 'birthDate', cellsformat : 'dd/MM/yyyy'},
			{text : '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', width : '20%', datafield : 'fromDate', cellsformat : 'dd/MM/yyyy'},
			{text : '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', width : '20%', datafield : 'thruDate',cellsformat : 'dd/MM/yyyy'}
    ];
	GridUtils.initGrid({url : 'JQGetSalesmanOfSup', width : '100%', showfilterrow : true,editable: false,pageable : true,columnsresize: true,localization: getLocalization(),autoHeight : true},dataFieldSalesmanOfSupToTransfer,colummnSalesmanOfSupToTransfer,null,$('#GridSalesmanOfSupToTransfer'));
	$("#alterSaveSalesmanOfSup").on('click', function(){
		var data = new Array();
		var rowIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var datagrid = $("#jqxgrid").jqxGrid('getrowdata',rowIndex);
		var rowIndexSalesmanOfSups = $("#GridSalesmanOfSup").jqxGrid('getselectedrowindexes');
		for(var i=0;i<rowIndexSalesmanOfSups.length;i++){
			var dataSalesmanOfSups=$("#GridSalesmanOfSup").jqxGrid('getrowdata', rowIndexSalesmanOfSups[i]);
			var row = {
					ManagerId : datagrid.partyId,
					SalesmanId : dataSalesmanOfSups.partyId,
					SupGroupIdToTransfer : $("#ListSups").val()
			};
			data.push(row);
		}
		if(rowIndexSalesmanOfSups.length){
			$.ajax({
				type : "POST",
				url : "RepositionSalesman",
				datatype : "json",
				data : {
					data : JSON.stringify(data)
				},
				success: function(response){
					$("#notification").jqxNotification({width: "100%",appendContainer: "#msgSalesmanOfSup",opacity: 0.8,autoClose: true,template: "success"});
					$("#notification").text('${StringUtil.wrapString(uiLabelMap.DATransferSuccess)}');
					$("#notification").jqxNotification('open');
					$("#GridSalesmanOfSup").jqxGrid('updatebounddata');
					$("#GridSalesmanOfSupToTransfer").jqxGrid('updatebounddata');
				}
			})
		}
		else{
			$("#notification").jqxNotification({width: "100%",appendContainer: "#msgSalesmanOfSup",opacity: 0.8,autoClose: true,template: "error"});
			$("#notification").text('${StringUtil.wrapString(uiLabelMap.DAHaventChosenAnySalesman)}');
			$("#notification").jqxNotification('open');
		}
	})
	
	
// end
</script>
