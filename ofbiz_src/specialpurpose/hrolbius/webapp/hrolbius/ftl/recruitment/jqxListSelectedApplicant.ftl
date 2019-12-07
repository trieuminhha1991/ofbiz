<script>
	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["PROB_STATUS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
</script>

<#assign dataField="[{ name: 'partyId', type: 'string'},
					{ name: 'firstName', type: 'string'},
					{ name: 'offerProbationId', type: 'string'},
					{ name: 'roleTypeId', type: 'string'},
					{ name: 'basicSalary', type: 'number'},
					{ name: 'trafficAllowance', type: 'number'},
					{ name: 'phoneAllowance', type: 'number'},
					{ name: 'otherAllowance', type: 'number'},
					{ name: 'inductedStartDate', type: 'date'},
					{ name: 'inductedCompletionDate', type: 'date'},
					{ name: 'percentBasicSalary', type: 'number'},
					{ name: 'lastName', type: 'string'},
					{ name: 'middleName', type: 'string'},
					{ name: 'comment', type: 'string'},
					{ name: 'statusId', type: 'string' },
					{ name: 'workEffortId', type: 'string' }
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'offerProbationId', width: 100, editable: false},
					{ text: '${uiLabelMap.fullName}', width: 150, editable:false, 
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						 	var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						 	return '<span>' + rowData['lastName'] + ' ' + (rowData['middleName'] ? rowData['middleName'] : '') + ' ' + rowData['firstName'] + '</span>'
						}
					},
					{ text: '${uiLabelMap.basicSalary}', datafield: 'basicSalary', width: 100},
					{ text: '${uiLabelMap.percentBasicSalary}', datafield: 'percentBasicSalary', width: 100},
					{ text: '${uiLabelMap.trafficAllowance}', datafield: 'trafficAllowance', width: 100},
					{ text: '${uiLabelMap.phoneAllowance}', datafield: 'phoneAllowance', width: 100},
					{ text: '${uiLabelMap.otherAllowance}', datafield: 'otherAllowance', width: 100},
					{ text: '${uiLabelMap.inductedStartDate}', datafield: 'inductedStartDate', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
						 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
					    }
					},
					{ text: '${uiLabelMap.inductedCompletionDate}', datafield: 'inductedCompletionDate', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
						 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
						    }
					},
					{ text: '${uiLabelMap.comment}', datafield: 'comment', width: 150},
					{ text: '${uiLabelMap.statusId}', datafield: 'statusId', columntype:'dropdownlist',width: 150,
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							for(var i = 0; i < statusData.length; i++){
								if(value == statusData[i].statusId){
									return '<span title=' + value + '>' + statusData[i].description + '</span>';
								}
							}
							return '<span> ' + value + '</span>';
						},
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							var dataSource = new Array();
							var item = {statusId:'APPL_PROPOSED', description: 'Đề xuất thử việc'};
							dataSource[0] = item;
							var item = {statusId:'APPL_PASSED', description: 'Trúng tuyển'};
							dataSource[2] = item;
							var item = {statusId:'APPL_APPROVED', description: 'Phê duyệt đề xuất thử việc'};
							dataSource[1] = item;
							editor.jqxDropDownList({source: dataSource, valueMember: 'statusId', displayMember: 'description' });
					    }
					 }
					"/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" editable="true" addType="popup" alternativeAddPopup="alterpopupNewApplicant" addrow="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListSelectedApplicant&workEffortId=${parameters.workEffortId}" dataField=dataField columnlist=columnlist
		 updateUrl="jqxGeneralServicer?sname=updateOfferProbation&jqaction=U" editColumns="offerProbationId;statusId"
		/>
