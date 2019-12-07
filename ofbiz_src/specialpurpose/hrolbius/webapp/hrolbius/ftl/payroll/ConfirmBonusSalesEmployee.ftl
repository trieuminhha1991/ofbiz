<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>

<#assign datafield = "[{name: 'salesCommissionId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'department', type: 'string'},
					   {name: 'amount', type: 'number'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'statusId', type: 'string'}]"/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign prevMonthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp, 0, -1)/>
<#assign prevMonthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>					   
<script type="text/javascript">
	var statusArr = [
		<#if statusList?has_content>
			<#list statusList as status>
				{
					statusId: "${status.statusId}",
					description: "${StringUtil.wrapString(status.description)}"
				},
			</#list>
		</#if>
	];
	<#assign columnlist = "{datafield: 'salesCommissionId', hidden: true},
							{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', width: 120},
							{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: 140},
							{text: '${uiLabelMap.CommonDepartment}', datafield: 'department', width: 140},
							{text: '${uiLabelMap.Position}', datafield: 'emplPositionType', width: 160},
							{text: '${uiLabelMap.HRCommonBonus}', datafield: 'amount', width: 120,
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}
							},
							{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput', width: 130 },
							{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', editable: false, 
								columntype: 'datetimeinput', width: 130 },
							{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', 
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									for(var i = 0; i < statusArr.length; i++){
										if(value == statusArr[i].statusId){
											return '<span title=' + value + '>' + statusArr[i].description + '</span>';
										}
									}
									return '<span title=' + value + '>' + value+ '</span>';				
								}
							}
							"/>
							
<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
	var salesCommissionId = datarecord.salesCommissionId;
	var urlStr = 'getSalesCommnissionAdj';
	var id = datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail_' + id);
	var salesCommissionAdjSource = {
		datafield:[
			{name: 'salesCommissionId', type: 'string'},
			{name: 'salesPolicyId', type: 'string'},
			{name: 'salesPolicyRuleId', type: 'string'},
			{name: 'salesPolicyActionSeqId', type: 'string'},
			{name: 'amount', type: 'number'},
			{name: 'description', type: 'string'},
			{name: 'inputParamEnumId', type: 'string'}
		],	
		cache: false,
		datatype: 'json',
		type: 'POST',
		data: {salesCommissionId: salesCommissionId},
        url: urlStr,
        root: 'salesCommissionDetail',
	};
	var nestedGridAdapter = new $.jqx.dataAdapter(salesCommissionAdjSource);
	if(grid != null){
		grid.jqxGrid({
			source: nestedGridAdapter, width: '96%', height: 170,
			showheader: true,
			showtoolbar: false,
			theme: 'olbius',
	 		pageSizeOptions: ['15', '30', '50', '100'],
	        pagerMode: 'advanced',
	        pageable: true,
	        columns:[
	        	{datafield: 'salesCommissionId', hidden: true},         
	        	{datafield: 'salesPolicyId', hidden: true},         
	        	{datafield: 'salesPolicyRuleId', hidden: true},         
	        	{datafield: 'salesPolicyActionSeqId', hidden: true},   
	        	{datafield: 'inputParamEnumId', text: '${StringUtil.wrapString(uiLabelMap.TypeBonus)}', width: 180},
	        	{datafield: 'amount', text: '${StringUtil.wrapString(uiLabelMap.DAAmount)}', width: 160,
	        		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
	        			return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
	        		}
	        	},
	        	{datafield: 'description', text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}'}
			]
		});
	}
}"/>							
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtfSalesCommission">
			<div id="jqxNtfSalesCommissionContent"></div>
		</div>
	</div>
</div>		
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ConfirmBonusEmplInSales}</h4>
		<div class="widget-toolbar none-content">
			<button id="confirmSalesCommission" class="grid-action-button icon-ok" style="margin-top: 8px">${uiLabelMap.HRCommonConfirm}</button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span2' style="text-align: center;">
							<b>${uiLabelMap.Time}</b>
						</div>
						<div class="span7">
							<div id="dateTimeInput"></div>						
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>						
						<div class="span12" style="margin-right: 15px">
							<div id="jqxDropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist selectionmode="checkbox"
				clearfilteringbutton="false" showtoolbar="true" sourceId="salesCommissionId"
				filterable="false" deleterow="false" editable="false" addrow="false" showtoolbar="false"
				url="" initrowdetails="true" initrowdetailsDetail=rowDetails
				id="jqxgrid" removeUrl="" deleteColumn="" updateUrl="" editColumns="" jqGridMinimumLibEnable="false" />
		</div>		
	</div>			   
</div>		
<script type="text/javascript">
	$(document).ready(function(){
		initJqxDateTime();
		initJqxTreeDropDownBtn();
		addBtnEvent();
		initJqxNotification();
	});
	
	function addBtnEvent(){
		$("#confirmSalesCommission").click(function(event){
			var rowindexes = $('#jqxgrid').jqxGrid('getselectedrowindexes');
			if(rowindexes.length <= 0){
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.NoDataRecordSelectedToConfirm)}",
						[{
			    		    "label" : "${uiLabelMap.CommonClose}",
			    		    "class" : "btn-primary btn-mini icon-ok",
			    		    "callback": function() {
			    		    	
			    		    }
			    		}]
				);	
			}else{
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSure)}",
						[{
			    		    "label" : "${uiLabelMap.CommonSubmit}",
			    		    "class" : "btn-primary btn-mini icon-ok",
			    		    "callback": function() {
			    		    	createBonusParamEmplSales(rowindexes);		
			    		    }
			    		},
			    		{
			    		    "label" : "${uiLabelMap.CommonCancel}",
			    		    "class" : "btn-danger icon-remove btn-mini",
			    		    "callback": function() {
			    		    }
			    		}]
				);
			}
		});
	}
	
	function initJqxNotification(){
		$("#jqxNtfSalesCommission").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
	}
	
	function createBonusParamEmplSales(rowindexes){
		$("#confirmSalesCommission").attr("disabled", "disabled");
		$('#jqxgrid').jqxGrid({disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		var salesCommissionIdArr = new Array();
		for(var i = 0; i < rowindexes.length; i++){
			var data = $('#jqxgrid').jqxGrid('getrowdata', rowindexes[i]);
			salesCommissionIdArr.push({salesCommissionId: data.salesCommissionId});
		}
		$.ajax({
			url: 'createBonusParamEmplSales',
			type: 'POST',
			data: {salesCommissionId: JSON.stringify(salesCommissionIdArr)},
			success: function(data){
				if(data.responseMessage == "success"){
					$('#jqxgrid').jqxGrid('updatebounddata');
					$('#jqxgrid').jqxGrid('clearselection')
					$("#jqxNtfSalesCommissionContent").text(data.successMessage);
					$("#jqxNtfSalesCommission").jqxNotification({template: 'info'});
					$("#jqxNtfSalesCommission").jqxNotification("open");
				}else{
					$("#jqxNtfSalesCommissionContent").text(data.errorMessage);
					$("#jqxNtfSalesCommission").jqxNotification({template: 'error'});
					$("#jqxNtfSalesCommission").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$("#confirmSalesCommission").removeAttr("disabled");
				$('#jqxgrid').jqxGrid('hideloadelement');
				$('#jqxgrid').jqxGrid({disabled: false});
			}
		});
	}
	
	function initJqxDateTime(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(${prevMonthStart.getTime()});
		var thruDate = new Date(${prevMonthEnd.getTime()});
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    var item = $("#jqxTree").jqxTree('getSelectedItem');
		    var partyId = item.value;
		    refreshGridData(partyId, fromDate, thruDate);
		});
	}
	
	function initJqxTreeDropDownBtn(){
		var dropdownButton = $("#jqxDropDownButton");
		var jqxTreeDiv = $("#jqxTree");
		var idSuffix = "partyId";
		dropdownButton.jqxDropDownButton({ width: '300px', height: 25, theme: 'olbius', autoOpen: true});
		var dataTree = new Array();
		<#list treePartyGroup as tree>
			var row = {};
			row["id"] = "${tree.id}_" + idSuffix;
			row["text"] = "${tree.text}";
			row["parentId"] = "${tree.parentId}_" + idSuffix;
			row["value"] = "${tree.idValueEntity}";
			dataTree[${tree_index}] = row;
		</#list>
		var sourceTree =
		{
		    datatype: "json",
		    datafields: [
		    	{ name: 'id'},
		        { name: 'parentId'},
		        { name: 'text'} ,
		        { name: 'value'}
		    ],
		    id: 'id',
		    localdata: dataTree
		};
		var dataAdapterTree = new $.jqx.dataAdapter(sourceTree);
		dataAdapterTree.dataBind();
		var recordsTree = dataAdapterTree.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
		jqxTreeDiv.jqxTree({source: recordsTree,width: "300px", height: "240px", theme: 'olbius'});
		
		jqxTreeDiv.on('select', function(event){
			var id = event.args.element.id;
	    	var item = $("#jqxTree").jqxTree('getItem', args.element);
	    	setDropdownContent(item, jqxTreeDiv, dropdownButton);
	    	var partyId = item.value;
	    	var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
	    	var fromDate = selection.from.getTime();
	    	var thruDate = selection.to.getTime();
	    	refreshGridData(partyId, fromDate, thruDate);
		});
		
		<#if rootPartyId?has_content>
			jqxTreeDiv.jqxTree('expandItem', $("#${rootPartyId}_" + idSuffix)[0]);
			jqxTreeDiv.jqxTree('selectItem', $("#${rootPartyId}_" + idSuffix)[0]);
		</#if>
	}
	
	function refreshGridData(partyGroupId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=getSalesCommissionData&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	}
	
	function setDropdownContent(element, jqxTree, dropdownBtn){
		var item = jqxTree.jqxTree('getItem', element);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	}
	
</script>