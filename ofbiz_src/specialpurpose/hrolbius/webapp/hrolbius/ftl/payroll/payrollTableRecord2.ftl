<style>
.backgroundWhiteColor{
	background-color: #fff !important
}
</style>
<#if !payrollTimestampResult?exists>
	<#assign payrollTimestampResult = dispatcher.runSync("getPayrollTableRecordTimestamp", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollTableId", parameters.payrollTableId, "userLogin", userLogin))>	
</#if>
<#if payrollTimestampResult?exists>
	<#assign listTimestamp = payrollTimestampResult.get("listTimestamp")>
	<#if listTimestamp?has_content>
		<#if !tempFromDate?exists>		
			<#assign tempFromDate = listTimestamp.get(0).get("fromDate")>	
		</#if>
		<#assign tempThruDate = listTimestamp.get(0).get("thruDate")>
		<#assign formulaList = delegator.findByAnd("PayrollTableCode", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollTableId", parameters.payrollTableId), Static["org.ofbiz.base.util.UtilMisc"].toList("code"), false)>
		<#assign formulaListStr = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(formulaList, "code", true)>
		<#assign entityCondiontion = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("code", Static["org.ofbiz.entity.condition.EntityJoinOperator"].IN, formulaListStr)>
		<#assign incomeCondition = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", "INCOME")>
		<#assign otherFormulaConds =  Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("payrollCharacteristicId", null)>
		
		<#assign listFormulaIncomeOrdered = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondiontion, 
																						Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,
																						incomeCondition), null, Static["org.ofbiz.base.util.UtilMisc"].toList("payrollItemTypeId"), null, false)>
		<#assign listFormulaOthers = delegator.findList("PayrollFormula", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(entityCondiontion, 
																						Static["org.ofbiz.entity.condition.EntityJoinOperator"].AND,
																						otherFormulaConds), null, null, null, false)>				
		<#assign payrollItemTypeIdList = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(listFormulaIncomeOrdered, "payrollItemTypeId", true)>
		<div id="treeGrid"></div>
		<div id='partyPayrollDetailPopup' class="hide">
			<div>${StringUtil.wrapString(uiLabelMap.PayrollDetailParty)}</div>
			<div>
				 <div id="partyPayrollDataTable"></div>
			</div>
		</div>
		
		<script type="text/javascript">
		var refreshJqxDataTable = false;
		var payrollCharArr = [
			<#if payrollCharList?has_content> 		                      
				<#list payrollCharList as payrollChar>
					{
						payrollCharacteristicId: '${payrollChar.payrollCharacteristicId}',
						description: '${StringUtil.wrapString(payrollChar.description?if_exists)}'
					},
				</#list>
			</#if>
		];	
		var dataAdapter;
		$(document).ready(function () {		
			var source = initSourceJqxTreeGrid();			
			var columnlist = initColumnlistJqxTreeGrid();
			var columngroups = initJqxTreeGridColumngroup();
			createJqxTreeGrid($('#treeGrid'), source, columnlist, columngroups);	
			initJqxTreeGridEvent();
			createJqxDataTable();
			initJqxWindow();
		});
		function createJqxTreeGrid(treeEle, source, columnlist, columngroups){
			treeEle.jqxTreeGrid({	
            	virtualModeCreateRecords: function (expandedRecord, done) {
                    dataAdapter = new $.jqx.dataAdapter(source,{
                            formatData: function(data) {
                                if (expandedRecord == null) {
                                    data.payrollTableId = "${parameters.payrollTableId}";
                                }
                                else{
                                    data.partyId = expandedRecord.partyId;
                                    data.payrollTableId = "${parameters.payrollTableId}";
                                }
                                return data;
                            },
                            loadComplete: function(){
                                done(dataAdapter.records);
                            },
                            loadError: function (xhr, status, error) {
                                done(false);
                              //  console.log(error.toString());
                                throw new Error(error.toString());
                            }
                        }
                    );   
                    dataAdapter.dataBind();
                },
                virtualModeRecordCreating: function (record) {
                    
                },
            	theme: 'olbius',
                width: '100%',
                columnsResize: true,
                columns: columnlist,
                columnGroups: columngroups
            });
		}
		
		function initSourceJqxTreeGrid(){
			var source = {
	   				dataType: 'json',
	   				datafields:[
	   				    {name: 'partyName', type: 'string'},
	   				    {name: 'partyId', type:'string'},
	   				    {name: 'partyParentId', type:'string'},
	   				    {name: 'numberWorkDay', type: 'string'},
	   				 	<#list listFormulaOthers as formula>
	   				    	{name: '${formula.get("code")}', type: 'float'},
	   				    </#list>
	   				    <#list payrollItemTypeIdList as payrollItemTypeId>		   				 		
	   				 		{name: '${payrollItemTypeId}_payrollItemType', type: 'float'},
	   				    </#list>
	   				 	<#list payrollCharList as payrollChar>
	   				 		<#if payrollChar.payrollCharacteristicId != 'INCOME'>
	   				 			{name: '${payrollChar.payrollCharacteristicId}_payrollChar', type: 'float'},
	   				 		</#if>
	   				 	</#list>
	   				 	{name: 'INCOME_payrollChar', type: 'float'},
		   				{name: 'realSalaryPaid', type: 'float'},
	   				],
	   			 	timeout: 25000,
	   				hierarchy:
	   				{
	                    keyDataField: { name: 'partyId' },
	                    parentDataField: { name: 'partyParentId' }
	                },
                    id: 'partyId',
                    root: 'listReturn',
                    type: 'POST',
                    url: 'getPayrollTableRecordOfParty'
   			};
			return source;
		}
		
		function initColumnlistJqxTreeGrid(){
			var columnlist = [
					{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', pinned: true, datafield: 'partyName', cellsalign: 'left', editable: false, width: 170, filterable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId',cellsalign: 'left', editable: false,width: 100, filterable: false,
						cellsrenderer: function (row, column, value) {
							return '<a style = \"margin-left: 10px\" href=' + 'EmployeeProfile?partyId=' + value + '>' +  value + '</a>';
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.NumberWorkDay)}', datafield: 'numberWorkDay',cellsalign: 'right', editable: false,
						width: 90, filterable: false,
					},
					<#list listFormulaOthers as formula>
						{text: '${StringUtil.wrapString(formula.name)}',  datafield: '${formula.get("code")}',  cellsalign: 'right', width: '120px', filterable: false,
							cellsrenderer: function (row, column, value) {
								return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
			 		 		 }
						},
					</#list>
					<#list payrollItemTypeIdList as payrollItemTypeId>
						<#assign payrollItemType = delegator.findOne("PayrollItemType", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollItemTypeId", payrollItemTypeId), false)/>
						{text: '${StringUtil.wrapString(payrollItemType.description?if_exists)}', datafield: '${payrollItemTypeId}_payrollItemType', cellsalign: 'right',width: '135px', filterable: false,
							columngroup: 'INCOME_GROUP',
							cellsrenderer: function (row, column, value) {
								return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
			 		 		 }
						},
					</#list>
					{text: '${uiLabelMap.TotalIncome}', columngroup: "INCOME_GROUP", datafield: 'INCOME_payrollChar', cellsalign: 'right',width: 120, filterable: false,
						cellsrenderer: function (row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		 }
					},
					<#list payrollCharList as payrollChar>
						<#if payrollChar.payrollCharacteristicId != 'INCOME'>
							{text: '${StringUtil.wrapString(payrollChar.description?if_exists)}', datafield: '${payrollChar.payrollCharacteristicId}_payrollChar', width: '120px', filterable: false,
								cellsrenderer: function (row, column, value) {
									return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
				 		 		 }		
							},
						</#if>
					</#list>
					{text: '${uiLabelMap.RealSalaryPaid}', datafield: 'realSalaryPaid', cellsalign: 'right',width: 120, filterable: false,
						cellsrenderer: function(row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		 }
					}
			];
			return columnlist;
		}
		
		function initJqxTreeGridColumngroup(){
			var columngroup = [
   					{text: '${StringUtil.wrapString(uiLabelMap.CommonIncome)}', align: 'center', name: 'INCOME_GROUP'}
            	];
			return columngroup;
		}
		
		function createJqxDataTable(){
			var source = {
					dataType: "json",
					dataFields: [
	                    { name: 'value', type: 'number' },
	                    { name: 'code', type: 'string' },
	                    { name: 'name', type: 'string' },
	                    { name: 'partyId', type: 'string' },
	                    { name: 'payrollCharacteristicId', type: 'string' },
	                    { name: 'sequenceNum', type: 'number'}
	                ],
	                root: 'listReturn',
	                type: 'POST',	              
	                url: 'getPayrollTableDetailParty',
	                updateRow: function (rowId, rowData, commit) {	                	
	                	var data = {};
	                	data.partyId = rowData.partyId;
	                	data.value = rowData.value;
	                	data.code = rowData.code;
	                	data.payrollTableId = "${parameters.payrollTableId}";
	                	data.fromDate = "${tempFromDate.getTime()}";
	                	$('#partyPayrollDataTable').jqxDataTable({disabled:true });
	                	$.ajax({
	                		url: 'updatePayrollTableOfParty',
	                		data: data,
	                		type: 'POST',
	                		async: false,
	                		success: function(response){
	                			if(response.responseMessage == 'success'){
		                			refreshJqxDataTable = true;
		                			commit(true);
	                			}else{
	                				commit(false);
	                			}
	                		},
	                		error: function(jqXHR, textStatus, errorThrown){
	                			commit(false);
	                		},
	                		complete: function(jqXHR, textStatus){
	                			$('#partyPayrollDataTable').jqxDataTable({disabled:false});
	                			//FIXME commit function is not effect, so updateBoundData
	                			$('#partyPayrollDataTable').jqxDataTable('updateBoundData');
	                		}
	                	});
	                },
	                data:{}
			};
			
			var columns =  [
					{text: '${StringUtil.wrapString(uiLabelMap.PayrollItemType)}', dataField: 'name', cellClassName: 'backgroundWhiteColor', editable: false},
					{datafield: 'code', hidden: true, editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonCharacteristic)}', dataField: 'payrollCharacteristicId', width: 180, align: 'left',  
						cellsAlign: 'left', hidden: true, editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.DAAmount)}', dataField: 'value', align: 'left', width: 130, cellClassName: 'backgroundWhiteColor',
						editable: true, 
						cellsrenderer: function (row, column, value) {
			 		 		 return "<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>" + formatcurrency(value) + "</div>";
		 		 		},
		 		 		getEditorValue: function (row, cellvalue, editor) {
	                        // return the editor's value.
	                        return editor.val();
	                    },
		 		 		validation: function(cell, value){
                  			var pattern = /[^0-9]/;
                  			if(!value){
                  				return {result: false, message: '${StringUtil.wrapString(uiLabelMap.ValueIsNotEmpty)}'};
                  			}
                  		  	if(value.match(pattern)){
                  				return {result: false,  message: "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}"};	  
                  		  	}
                  		  	return true;
                      	},
					},
					{dataField: 'sequenceNum', hidden: true, editable: false},
					{dataField: 'partyId', hidden: true, editable: false},
			];
			
			var dataAdapter = new $.jqx.dataAdapter(source, {
                loadComplete: function () {
                    // data is loaded.
                },
                downloadComplete: function (data, status, xhr) {
                    if (data) {
                        source.totalRecords = data.totalRows;
                    }
                },
                loadError: function (xhr, status, error) {
                    throw new Error(error.toString());
                } 
            });
			
			$("#partyPayrollDataTable").jqxDataTable({
				width: 580,
				height: 440,
				pagerMode: "advanced",
	            pageable: true,
	            theme: 'olbius',
	            editable: true,
	            serverProcessing: true,
                source: dataAdapter,
                altRows: true,
                groups: ['payrollCharacteristicId'],
                columnsResize: true,
                groupsRenderer: function(value, rowData, level){             
                	for(var i = 0; i < payrollCharArr.length; i++){
                		if(payrollCharArr[i].payrollCharacteristicId == value){
                			return '<b>' + payrollCharArr[i].description + '</b>';
                		}
                	}
                    return '<b>' + value + '</b>';
                },
                columns:columns
			 });
		}
		
		function initJqxTreeGridEvent(){
			$('#treeGrid').on('rowDoubleClick', function(event){
				var data = event.args.row;
				var dataSource = {};
				dataSource.partyId = data.partyId;
				dataSource.payrollTableId = "${parameters.payrollTableId}";
				dataSource.fromDate = "${tempFromDate.getTime()}";
				var tempSource = $("#partyPayrollDataTable").jqxDataTable('source');
				tempSource._source.data = dataSource;
				$("#partyPayrollDataTable").jqxDataTable('source', tempSource);
				$("#partyPayrollDataTable").jqxDataTable('updateBoundData');
				$("#partyPayrollDetailPopup").jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.PayrollDetailParty)} ${StringUtil.wrapString(uiLabelMap.CommonOf)} ' + data.partyName);
				openJqxWindow($("#partyPayrollDetailPopup"));
			}); 
		}
		
		function initJqxWindow(){
			$("#partyPayrollDetailPopup").jqxWindow({showCollapseButton: false, autoOpen: false, height: 505, width: 600, isModal: true, 
		    	theme:'olbius', collapsed:false, theme: 'olbius',
			    initContent: function () {  
			    	
			    }
			});
			$("#partyPayrollDetailPopup").on('close', function(event){
				$("#partyPayrollDataTable").jqxDataTable('goToPage', 0);
				if(refreshJqxDataTable){
					///dataAdapter.dataBind();
					$("#treeGrid").jqxTreeGrid('refresh');
					$("#treeGrid").jqxTreeGrid('updateBoundData');
				}
				refreshJqxDataTable = false;
			});
		}
		
		function openJqxWindow(jqxWindowDiv){
			var wtmp = window;
			var tmpwidth = jqxWindowDiv.jqxWindow('width');
			jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
			jqxWindowDiv.jqxWindow('open');
		}
		</script>
	</#if>
</#if>