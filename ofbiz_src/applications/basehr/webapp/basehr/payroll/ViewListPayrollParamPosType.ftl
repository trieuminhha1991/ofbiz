<#include "script/ViewListPayrollParamPosTypeScript.ftl"/>
<script type="text/javascript" src="/hrresources/js/payroll/ViewListPayrollParamPosType.js"></script>
<#assign dataFields = "[{name: 'payrollParamPositionTypeId', type: 'string'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'code', type: 'string'},	
						{name: 'roleTypeGroupId', type: 'string'},						
						{name: 'periodTypeId', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
						{name: 'rateAmount', type: 'number'},
						{name: 'uomId', type: 'string'},
						]" />

<script type="text/javascript">
	<#assign columnlist = "{datafield: 'payrollParamPositionTypeId', hidden: true, editable: false},
	{text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId', filterable: true, editable: false, cellsalign: 'left', 
		 	width: 160,
			filtertype: 'checkedlist',
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < emplPosTypeArr.length; i++){
					if(emplPosTypeArr[i].emplPositionTypeId == value){
						return '<div style=\"\">' + emplPosTypeArr[i].description + '</div>';		
					}
				}
			},
			createfilterwidget: function(column, columnElement, widget){
				var sourceEmplPosType = {
			        localdata: emplPosTypeArr,
			        datatype: 'array'
			    };		
				var filterBoxAdapter = new $.jqx.dataAdapter(sourceEmplPosType, {autoBind: true});
			    var dataEmplPosTypeList = filterBoxAdapter.records;
			    //var selectAll = {'trainingTypeId': 'selectAll', 'description': '(Select All)'};
			    //dataEmplPosTypeList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			    widget.jqxDropDownList({ source: dataEmplPosTypeList,  displayMember: 'description', valueMember : 'emplPositionTypeId', 
			    	height: '25px', autoDropDownHeight: false, searchMode: 'containsignorecase', incrementalSearch: true, filterable:true,
					renderer: function (index, label, value) {
						for(i=0; i < emplPosTypeArr.length; i++){
							if(emplPosTypeArr[i].emplPositionTypeId == value){
								return emplPosTypeArr[i].description;
							}
						}
					    return value;
					}
				});									
			}
		},
		{text: '${uiLabelMap.parameters}', datafield: 'code', filterable: false, editable: false, cellsalign: 'left', width: 160,
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < parametersArr.length; i++){
					if(parametersArr[i].code == value){
						return '<div style=\"\">' + parametersArr[i].name + '</div>';		
					}
				}
				return '<div style=\"\">' + value + '</div>';
			}
		},
		{text: '${uiLabelMap.HRCommonChannel}', datafield: 'roleTypeGroupId', filterable: false, editable: false, cellsalign: 'left', hidden: true,
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < roleTypeGroupArr.length; i++){
					if(roleTypeGroupArr[i].roleTypeGroupId == value){
						return '<div style=\"\">' + roleTypeGroupArr[i].description + '</div>';		
					}
				}
				return '<div style=\"\">' + value + '</div>';
			}	
		},		
		{text: '${uiLabelMap.CommonPeriodType}', datafield:'periodTypeId' , filterable: false, editable: false, cellsalign: 'left', width: 140,
			cellsrenderer: function (row, column, value){
				for(var i = 0; i < periodTypeArr.length; i++){
					if(periodTypeArr[i].periodTypeId == value){
						return '<div style=\"\">' + periodTypeArr[i].description + '</div>';		
					}
				}
			}
		},
		{text: '${uiLabelMap.PayrollFromDate}', datafield:'fromDate', filterable: false,editable: false, cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
		{text: '${uiLabelMap.PayrollThruDate}', datafield: 'thruDate', filterable: false, editable: true, cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template',
			cellsrenderer: function (row, column, value) {
				if(!value){
					return '<div style=\"margin-left: 4px\">${uiLabelMap.HRCommonCurrent}</div>';
				}
			},
			createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		        editor.jqxDateTimeInput({width: 158, height: 28});
		        editor.val(cellvalue);
		    }
		}, 
		{text: '${uiLabelMap.HRCommonAmount}', datafield: 'rateAmount', filterable: false,editable: true, cellsalign: 'right',
			cellsrenderer: function (row, column, value) {
		 		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		 		 if (data && data.rateAmount){
		 		 	return \"<div style='margin-right: 2px; margin-top: 9.5px; text-align: right;'>\" + formatcurrency(data.rateAmount, data.uomId) + \"</div>\";
		 		 }
	 		 }								
		},
		{text: '', datafield: 'uomId', hidden: true}"/>
	
</script>
<div class="row-fluid" id="jqxNotifyContainer"></div>
<div id="jqxNotify">
	<div id="ntfContent"></div>
</div>	
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" 
		 filterable="true" alternativeAddPopup="popupWindowAddNew" deleterow="false" editable="false" addrow="true"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQListParamPosTypeGeo" id="jqxgrid" customControlAdvance="<div id='dateTimeInput'></div>"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=processPayrollParamPositionType" jqGridMinimumLibEnable="false"	showlist="false"
		 addColumns="periodTypeId;code;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);rateAmount;emplPositionTypeId;roleTypeGroupId" 
		 selectionmode="singlerow" addrefresh="true"/>

<div class="row-fluid">
	<div id="popupWindowEdit" class='hide'>
		<div>${uiLabelMap.EditPayrollParamPositionType}</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<form class="form-horizontal" id="editPayrollParamPositionTypeForm">
					<input type="hidden" name="payrollParamPositionTypeId" id="payrollParamPositionTypeId">
					<#if useRoleTypeGroup == "true">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.HRCommonChannel}</label>
							</div>
							<div class="span7">
								<div id="roleTypeGroupIdEdit"></div>
							</div>
						</div>
					</#if>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=" asterisk">${uiLabelMap.EmplPositionTypeId}</label>
						</div>
						<div class="span7">
							<#if useRoleTypeGroup == "true">
								<div id="emplPositionTypeIdEdit">
								</div>
							<#else>
								<input type="text" id="emplPositionTypeIdEdit">
							</#if>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.parameters}</label>
						</div>
						<div class="span7">
							<div id="codeEdit"></div>
						</div>
					</div>
					
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.AvailableFromDate}</label>
						</div>
						<div class="span7">
							<div id="fromDateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.CommonThruDate}</label>
						</div>
						<div class="span7">
							<div id="thruDateEdit"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=""><span id="amountLabelEdit">${uiLabelMap.HRCommonAmount}</span></label>
						</div>
						<div class="span7">
							<div id="amountValueEdit"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="editCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="editSave">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div id="popupWindowAddNew" class='hide'>
		<div id="EmplPosTypeRateWindowHeader">
			 ${uiLabelMap.AddEmpPosTypeSalary} 
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<form class="form-horizontal" id="createPayrollParamPositionTypeForm">
					<#if useRoleTypeGroup == "true">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class=" asterisk">${uiLabelMap.HRCommonChannel}</label>
							</div>
							<div class="span7">
								<div id="roleTypeGroupId"></div>
							</div>
						</div>
					<#else>
						<div class='row-fluid margin-bottom10'>		
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.HRCommonChannel}</label>
							</div>					
							<div class="span7" style="">
								<div id="jqxDropDownButton" class="">
									<div style="border: none;" id="jqxTree">
									</div>
								</div>
							</div>
						</div>	
					</#if>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=" asterisk">${uiLabelMap.EmplPositionTypeId}</label>
						</div>
						<div class="span7">
							<div id="emplPositionTypeIdAddNew">
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=" asterisk">${uiLabelMap.parameters}</label>
						</div>
						<div class="span7">
							<div id="codeAddNew"></div>
						</div>
					</div>
					
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeNew">${uiLabelMap.HRCommonNotSetting}</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.AvailableFromDate}</label>
						</div>
						<div class="span7">
							<div id="fromDateNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.CommonThruDate}</label>
						</div>
						<div class="span7">
							<div id="thruDateNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=""><span id="amountLabelNew">${uiLabelMap.HRCommonAmount}</span></label>
						</div>
						<div class="span7">
							<div id="amountValueNew"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>			 
