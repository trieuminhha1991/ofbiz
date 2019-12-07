<#include "script/ViewListEmpPosTypeSalaryScript.ftl" />
<#assign dataFields = "[{name: 'emplPositionTypeId', type: 'string'},
						{name: 'emplPositionTypeRateId', type: 'string'},						
						{name: 'roleTypeGroupId', type: 'string'},
						{name: 'periodTypeId', type: 'string'},
						{name: 'fromDate', type: 'date', other: 'Timestamp'},
						{name: 'thruDate', type: 'date', other: 'Timestamp'},
						{name: 'rateAmount', type: 'number'},
						{name: 'rateCurrencyUomId', type: 'string'},
						]" />

<#if setupByGeo>
	<script type="text/javascript" src="/hrresources/js/payroll/ViewListEmpPosTypeSalaryGeoTree.js"></script>
</#if>
<script type="text/javascript">
	
	<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
					var emplPositionTypeId = datarecord.emplPositionTypeId;
					var fromDate = datarecord.fromDate;
					var periodTypeId = datarecord.periodTypeId;
					var urlStr = 'getEmplPositionTypeRateHistory';
					
					var id = datarecord.uid.toString();
					var grid = $($(parentElement).children()[0]);
			        $(grid).attr('id', 'jqxgridDetail_' + id);
	        		var emplPosTypeSalarySource = {datafields: [
           		            {name: 'fromDateDetail', type: 'date'},
           		            {name: 'thruDateDetail', type: 'date'},
           		            {name: 'rateAmountDetail', type: 'number'},
           		            {name: 'periodTypeIdDetail', type: 'string'},
           		            {name: 'rateCurrencyUomIdDetail', type: 'string'}           		            
           				],
           				cache: false,
           				//localdata: emplSalaryArr,
           				datatype: 'json',
           				type: 'POST',
           				data: {emplPositionTypeId: emplPositionTypeId},
           		        url: urlStr,
           		        deleterow: function(rowId, commit){
           		        	
           		        }
           	        };
			        var nestedGridAdapter = new $.jqx.dataAdapter(emplPosTypeSalarySource);
			        if (grid != null) {
			        	grid.jqxGrid({
			        		source: nestedGridAdapter,
			        		width: '100%', height: 170,
			                showtoolbar:false,
					 		editable: false,
					 		editmode:'selectedrow',
					 		showheader: true,
					 		 
					 		selectionmode:'singlecell',
					 		theme: 'energyblue',
					 		columns: [
												 		          
					 		 	{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDateDetail', cellsalign: 'left', width: '25%', cellsformat: 'dd/MM/yyyy ', columntype: 'template',
					 				 			
					 		 	},
					 		 	{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDateDetail', cellsalign: 'left', width: '24%', cellsformat: 'dd/MM/yyyy ', columntype: 'template',
					 		 		
					 		 	},
					 		 	{text: '${uiLabelMap.CommonAmount}',datafield: 'rateAmountDetail', filterable: false,editable: false, cellsalign: 'right', width: '23%', 
					 		 		 cellsrenderer: function (row, column, value) {
						 		 		 var data = grid.jqxGrid('getrowdata', row);
						 		 		 if (data && data.rateAmountDetail){
						 		 		 	return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(data.rateAmountDetail, data.rateCurrencyUomIdDetail) + \"</div>\";
						 		 		 }
					 		 		 }
					 		 	},
					 		 	
					 		 	{text: '${uiLabelMap.PeriodTypePayroll}', datafield: 'periodTypeIdDetail', filterable: false,editable: false, cellsalign: 'left', width: '23%',
									cellsrenderer: function (row, column, value){
										for(var i = 0; i < periodTypeArr.length; i++){
											if(periodTypeArr[i].periodTypeId == value){
												return '<div style=\"margin-top: 4px; margin-left: 2px\">' + periodTypeArr[i].description + '</div>';		
											}
										}
									}	
								},
								{text:'', datafield: 'rateCurrencyUomIdDetail', hidden: true},
		 		            ]
			        	});
			        }
				}">
	
	<#assign columnlist = "{datafield: 'emplPositionTypeRateId', hidden: true, editable: false},
						   {text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId', filterable: true, editable: false, cellsalign: 'left', 
								filtertype: 'checkedlist', width: 180,
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
								    //dataEmplPosTypeList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								    widget.jqxDropDownList({ source: dataEmplPosTypeList,  displayMember: 'description', valueMember : 'emplPositionTypeId'});	
								    if(dataEmplPosTypeList.length <= 8){
								    	widget.jqxDropDownList({autoDropDownHeight : true});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight : false});
								    }
								}
							},
							{text: '${uiLabelMap.HRCommonChannel}', datafield: 'roleTypeGroupId', filterable: false, editable: false, cellsalign: 'left', 
								width: 150, hidden: true,
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < roleTypeGroupArr.length; i++){
										if(roleTypeGroupArr[i].roleTypeGroupId == value){
											return '<div style=\"\">' + roleTypeGroupArr[i].description + '</div>';		
										}
									}
									return '<div style=\"\">' + value + '</div>';
								},
							},
							{text: '${uiLabelMap.PeriodTypePayroll}', datafield:'periodTypeId' , filterable: true, editable: false, cellsalign: 'left', width: 170,filterType : 'checkedlist',
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < periodTypeArr.length; i++){
										if(periodTypeArr[i].periodTypeId == value){
											return '<div style=\"\">' + periodTypeArr[i].description + '</div>';		
										}
									}
								},
								createfilterwidget : function(column, columnElement, widget){
									var source = {
									        localdata: periodTypeArr,
									        datatype: 'array'
									    };		
										var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
									    var dataFilter = filterBoxAdapter.records;
									    //dataFilter.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
									    widget.jqxDropDownList({ source: dataFilter,  displayMember: 'description', valueMember : 'periodTypeId'});	
									    if(dataFilter.length <= 8){
									    	widget.jqxDropDownList({autoDropDownHeight : true});
									    }else{
									    	widget.jqxDropDownList({autoDropDownHeight : false});
									    }
								}
							},
							{text: '${uiLabelMap.PayrollFromDate}', datafield:'fromDate',filterType : 'range',editable: false, cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
							{text: '${uiLabelMap.PayrollThruDate}', datafield: 'thruDate', filterType : 'range', editable: true, cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template',
								cellsrenderer: function (row, column, value) {
									if(!value){
										return '<div style=\"margin-left: 4px\">${uiLabelMap.HRCommonNotSetting}</div>';
									}
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							        editor.jqxDateTimeInput({width: 158, height: 28});
							        editor.val(cellvalue);
							    }
							}, 
							{text: '${uiLabelMap.HRCommonAmount}', datafield: 'rateAmount', filterType : 'number',editable: true, cellsalign: 'right',
								cellsrenderer: function (row, column, value) {
					 		 		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		 		 if (data && data.rateAmount){
					 		 		 	return \"<div style='margin-right: 2px; margin-top: 9.5px; text-align: right;'>\" + formatcurrency(data.rateAmount, data.rateCurrencyUomId) + \"</div>\";
					 		 		 }
				 		 		 }								
							},
							
							{text: '', datafield: 'rateCurrencyUomId', hidden: true}"/>

</script>
<div class="row-fluid">
	<div id="appendNotification">
		<div id="updateNotificationSalary">
			<span id="notificationText"></span>
		</div>
	</div>	
</div>	
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.SettingEmpPosTypeSalary}</h4>
		<div class="widget-toolbar none-content">
			<button id="addNew" class="grid-action-button icon-plus-sign open-sans">${uiLabelMap.accAddNewRow}</button>
			<button id="removeFilter" class="grid-action-button icon-filter open-sans">${StringUtil.wrapString(uiLabelMap.HRCommonRemoveFilter)}</button>
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
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
					 filterable="true" alternativeAddPopup="popupWindowEmplPosTypeRate"
					 deleterow="false" editable="false" addrow="true"
					 url="jqxGeneralServicer?hasrequest=Y&sname=JQListEmplPositionTypeRate" id="jqxgrid" 
					 createUrl="jqxGeneralServicer?jqaction=C&sname=createEmplPositionTypeRateAmount" functionAfterUpdate="refreshRowDetail()" functionAfterAddRow="enableAlterSave()"
					 addColumns="periodTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);rateAmount;emplPositionTypeId;includeGeoId;excludeGeoId;roleTypeGroupId" 
					 selectionmode="singlerow" addrefresh="true" jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>
					
<div class="row-fluid">
	<div id="popupWindowEmplPosTypeRate" class="hide">
		<div id="EmplPosTypeRateWindowHeader">
			 ${uiLabelMap.AddEmpPosTypeSalary}
		</div>
		<div class="form-window-container" id="EmplPosTypeRateWindowContent">
			<div class='form-window-content'>
				<form id="createEmplPosTypeRateForm">
					<#if useRoleTypeGroup == "true">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.HRCommonChannel}</label>
							</div>
							<div class="span7">
								<div id="roleTypeGroupDropDown"></div>
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
					<#if setupByGeo>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class=" asterisk">${uiLabelMap.HRAreaApply}</label>
							</div>
							<div class="span7">
								<div id="jqxBtnIncludeGeo">
									<div style="border: none;" id="jqxTreeIncludeGeo"></div>
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.HRAreaExclude}</label>						
							</div>
							<div class="span7">
								<div id="jqxBtnExcludeGeo">
									<div style="border: none;" id="jqxTreeExcludeGeo"></div>
								</div>
							</div>
						</div>		
					</#if>	
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.EmplPositionTypeId}</label>
						</div>
						<div class="span7">
							<div id="setSalaryEmplPositionTypeId">
							</div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeNew"></div>
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
							<label class="">${uiLabelMap.HRCommonAmount}</label>
						</div>
						<div class="span7">
							<div id="amountValueNew"></div>
						</div>
					</div>
				</form>
			</div>
			<div class="form-action">
				<button id="alterCancel" type="button" class="btn btn-danger form-action-button pull-right icon-remove">${uiLabelMap.CommonCancel}</button>
				<button id="alterSave" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>	

<div id="editEmplPosTypeRateWindow" class="hide">
	<div id="windowHeader">
     	${uiLabelMap.EditEmpPosTypeRate}
     </div>	
     <div class='form-window-container' id="windowContent">
     	<div class='form-window-content'>
   			<form method="post" id="editEmplPosTypeRateForm">
   				<input type="hidden" name="emplPositionTypeRateId" id="emplPositionTypeRateId">
   				<#if useRoleTypeGroup == "true">
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.HRCommonChannel}</label>     					
   					</div>
					<div class="span7">
						<div id="roleTypeGroupEdit"></div>						
					</div>
				</div>
				</#if>
				<#if setupByGeo>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class=" asterisk">${uiLabelMap.HRAreaApply}</label>						
						</div>
						<div class="span7">
							<div id="jqxBtnIncludeGeoEdit">
								<div style="border: none;" id="jqxTreeIncludeGeoEdit"></div>
							</div>								
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.HRAreaExclude}</label>						
						</div>
						<div class="span7">
							<div id="jqxBtnExcludeGeoEdit">
								<div style="border: none;" id="jqxTreeExcludeGeoEdit"></div>
							</div>
						</div>
					</div>
				</#if>
				<#if useRoleTypeGroup == "true">
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
   						<label class="asterisk">${uiLabelMap.EmplPositionTypeId}</label>
   					</div>
   					<div class="span7">
   						<div id="emplPositionTypeIdEdit"></div>
   					</div>
   				</div>
   				<#else>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
   						<label class="asterisk">${uiLabelMap.EmplPositionTypeId}</label>
   					</div>
   					<div class="span7">
   						<input type="text" id="emplPositionTypeIdEdit">
   					</div>
   				</div>
   				</#if>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.PayrollFromDate}</label>	     					
   					</div>
   					<div class="span7">
   						<div id='payrollFromDate'>
      					</div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
   						<label class="">${uiLabelMap.CommonThruDate}</label>
   					</div>
   					<div class="span7">
   						<div id='payrollThruDate'>
      					</div>
   					</div>
   				</div>
   				<!-- <div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.CurrencyUomId}</label>     					
   					</div>
   					<div class="span7">
   						<div id="CurrencyUomIdSalary">
   						</div>
   					</div>
   				</div> -->
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
   						<label class="">${uiLabelMap.PeriodTypePayroll}</label>
   					</div>
   					<div class="span7">
   						<div id="periodTypeSalary"></div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.HRCommonAmount}</label>     					
   					</div>
   					<div class="span7">
   						<div id="amountSalary">
   						</div>
   					</div>
   				</div>
   			</form>
     	</div>
     	<div class="form-action">
     		<button id="cancelSubmit" type="button" class="btn btn-danger form-action-button pull-right icon-remove">${uiLabelMap.CommonCancel}</button>
     		<button id="submitForm" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.CommonSave}</button>
     	</div>	
     </div>
</div>	
<script type="text/javascript" src="/hrresources/js/payroll/ViewListEmpPosTypeSalary.js"></script>