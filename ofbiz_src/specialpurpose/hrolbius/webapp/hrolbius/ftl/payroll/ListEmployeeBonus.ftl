<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>


<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'department', type: 'string'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'value', type: 'number'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'periodTypeId', type: 'string'}]"/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign prevMonthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp, 0, -1)/>
<#assign prevMonthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
<script type="text/javascript">
	var codeArr = [
		<#if payrollParamBonus?has_content>
			<#list payrollParamBonus as param>
				{
					code: "${param.code}",
					description: '${StringUtil.wrapString(param.name)}',
					type: "${StringUtil.wrapString(param.type)}"
				},
			</#list>
		</#if>
	];
	
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', width: 120},
							{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 130},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', width: 140},
							{text: '${StringUtil.wrapString(uiLabelMap.Position)}', datafield: 'emplPositionType', width: 160},
							{text: '${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}', datafield: 'value', width: 120,
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}	
							},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput', width: 130},
							{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput' },
							{datafield:'periodTypeId', hidden: true}"/>
							
	<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
		var partyId = datarecord.partyId;
		var fromDate = datarecord.fromDate.getTime();
		var thruDate;
		if(datarecord.thruDate){
			thruDate = datarecord.thruDate.getTime(); 
		}
		var urlStr = 'getEmplParamCharacteristic';
		var id = datarecord.uid.toString();
		var grid = $($(parentElement).children()[0]);
		$(grid).attr('id','jqxgridDetail_' + id);
		var payrollEmplParameterSource = {
				datafield:[
					{name: 'code', type: 'string'},				           
					{name: 'value', type: 'number'}				           
				],
				cache: false,
				datatype: 'json',
				type: 'POST',
				data: {partyId: partyId, fromDate: fromDate, thruDate: thruDate, paramCharacteristicId: 'THUONG'},
				url: urlStr,
		        root: 'payrollEmplParamDetails',
		};
		var nestedGridAdapter = new $.jqx.dataAdapter(payrollEmplParameterSource);
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
		        	{text: '${StringUtil.wrapString(uiLabelMap.TypeBonus)}', width: 230, datafield: 'code',
		        		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		        			for(var i = 0; i < codeArr.length; i++){
		        				if(codeArr[i].code == value){
		        					return '<span title=\"' + value + '\">' + codeArr[i].description + '</span>';
		        				}
		        			}
		        			return '<span>' + value + '</span>';
		        		}
		        	},
		        	{text: '${StringUtil.wrapString(uiLabelMap.DAAmount)}', datafield: 'value',
			        	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
						}	
		        	}
				]
			});
		}
	}"/>							
</script>

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ListEmployeeBonus}</h4>
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
			<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist 
				clearfilteringbutton="false"
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
	});
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
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=getEmplListBonusInPeriod&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	}
	
	function setDropdownContent(element, jqxTree, dropdownBtn){
		var item = jqxTree.jqxTree('getItem', element);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	}
</script>				   
