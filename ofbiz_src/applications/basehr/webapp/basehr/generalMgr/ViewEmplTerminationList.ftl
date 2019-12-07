<#include "script/ViewEmplTerminationListScript.ftl"/>
<#assign datafield = "[{name: 'partyIdFrom', type: 'string'},
					   {name: 'roleTypeIdFrom', type: 'string'},
					   {name: 'roleTypeIdTo', type: 'string'},
					   {name: 'partyIdTo', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'fromDate', type: 'date', other : 'Timestamp'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'workingStatusId', type: 'string'},
					   {name: 'terminationReasonId', type: 'string'}]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: 130},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'fullName', width: 150},
						{text: '${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}', datafield: 'fromDate', width: 150, editable: false, cellsformat: 'dd/MM/yyyy', filterType : 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.HREmplResignDate)}', datafield: 'thruDate', width: 150, editable: false, cellsformat: 'dd/MM/yyyy', filterType : 'range'},
						{text: '${StringUtil.wrapString(uiLabelMap.HREmplReasonResign)}', datafield: 'terminationReasonId', width: 200,filterType : 'checkedlist',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0; i < globalVar.terminationReasonArr.length; i++){
									if(value == globalVar.terminationReasonArr[i].terminationReasonId){
										return '<span title=' + value + '>' + globalVar.terminationReasonArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : globalVar.terminationReasonArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source : dataFilter, valueMember : 'terminationReasonId', displayMember : 'description'});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'workingStatusId', editable: false,filterType : 'checkedlist',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(value == globalVar.statusArr[i].statusId){
										return '<span title=' + value + '>' + globalVar.statusArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : globalVar.statusArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source : dataFilter, valueMember : 'statusId', displayMember : 'description'});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
								}
							}
						},
						{datafield: 'partyIdFrom', hidden: true},
						{datafield: 'roleTypeIdFrom', hidden: true},
						{datafield: 'roleTypeIdTo', hidden: true}"/>
</script>				

<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.HREmplTerminationList}</h4>
		<div class="widget-toolbar none-content">
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div id="dateTimeInput"></div>						
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>						
						<div class="span12" style="margin-right: 15px">
							<div id="jqxDropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
							<button id="removeFilter" class="grid-action-button icon-filter open-sans pull-right" style="margin-top: 0px;">${uiLabelMap.HRCommonRemoveFilter}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid url="" dataField=datafield columnlist=columnlist
				clearfilteringbutton="true" id="jqxgrid" 
				editable="false" width="100%" filterable="true"
				showtoolbar="false" deleterow="false" jqGridMinimumLibEnable="false"
			/>			
		</div>
	</div>	
</div>	
<script type="text/javascript" src="/hrresources/js/generalMgr/ViewEmplTerminationList.js"></script>   