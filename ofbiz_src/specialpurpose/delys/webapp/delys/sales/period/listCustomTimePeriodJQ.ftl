
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
<#assign listPeriodType = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].LIKE ,"SALES_%"), null, null, null, false)>

var periodTypeData = new Array();
<#list listPeriodType as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	row['periodTypeId'] = '${item.periodTypeId}';
	row['description'] = '${description}';
	periodTypeData[${item_index}] = row;
</#list>
<#--
{text: '${StringUtil.wrapString(uiLabelMap.AccountingPeriodType)}', dataField: 'periodTypeId',  filtertype: 'list', columntype: 'dropdownlist', editable: true,
	cellsrenderer: function(column, row, value){
		for(var i = 0;  i < periodTypeData.length; i++){
			if(periodTypeData[i].periodTypeId == value){
				return '<span title=' + value + '>' + periodTypeData[i].description + '</span>'
			}
		}
		return '<span>' + value + '</span>'
	},
	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
        editor.jqxDropDownList({source: periodTypeData, valueMember: 'periodTypeId', displayMember:'description' });
    },
    createfilterwidget: function (column, htmlElement, editor) {
        editor.jqxDropDownList({ source: fixSelectAll(periodTypeData), displayMember: 'description', valueMember: 'periodTypeId' ,
        	renderer: function (index, label, value) {
        		if (index == 0) {
        			return value;
        		}
                for(var i = 0; i < periodTypeData.length; i++){
                	if(value == periodTypeData[i].periodTypeId){
                		return periodTypeData[i].description; 
                	}
                }
            }});
        editor.jqxDropDownList('checkAll');
    },
},
-->
</script>
<#assign dataField = "[{name: 'customTimePeriodId', type: 'string'}, 
						{name: 'parentPeriodId', type: 'string'}, 
						{name: 'organizationPartyId', type: 'string'}, 
						{name: 'periodTypeId', type: 'string'},
						{name: 'periodNum', type: 'number'},
						{name: 'periodName', type: 'string'}, 
						{name: 'fromDate', type: 'date', other: 'Date'},
						{name: 'thruDate', type: 'date', other: 'Date'}
				]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.CommonId)}', dataField: 'customTimePeriodId', width: '7%', editable: false,
							cellsrenderer: function(row, colum, value) {
						    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						    	return \"<span><a href='/delys/control/viewCustomTimeSalesPeriod?customTimePeriodId=\" + data.customTimePeriodId + \"'>\" + data.customTimePeriodId + \"</a></span>\";
						    }	
						}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAParentCustomTimePeriod)}', dataField: 'parentPeriodId',
							cellsrenderer: function(column, row, value){
								for(var i = 0;  i < parentPeriod.length; i++){
									if(parentPeriod[i].parentPeriodId == value){
										return '<span title=' + value + '>' + parentPeriod[i].periodName + ' - ['+ parentPeriod[i].parentPeriodId +']' + '</span>'
									}
								}
								return '<span>' + value + '</span>'
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.AccountingPeriodName)}', dataField: 'periodName'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAPartyGroupId)}', dataField: 'organizationPartyId',},
						{text: '${StringUtil.wrapString(uiLabelMap.AccountingPeriodType)}', dataField: 'periodTypeId', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < periodTypeData.length; i++){
	    							if (value == periodTypeData[i].periodTypeId){
	    								return '<span title = ' + periodTypeData[i].description +'>' + periodTypeData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(periodTypeData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'periodTypeId', valueMember: 'periodTypeId',
									renderer: function(index, label, value){
										for(var i = 0; i < periodTypeData.length; i++){
											if(periodTypeData[i].periodTypeId == value){
												return '<span>' + periodTypeData[i].description + '</span>';
											}
										}
										return value;
									}
								});
								widget.jqxDropDownList('checkAll');
				   			}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonNbr)}', dataField: 'periodNum'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', dataField: 'fromDate', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxDateTimeInput({ });
						}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', dataField: 'thruDate', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxDateTimeInput({ });
							}
						},
				"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true"  addType="popup"
		url="jqxGeneralServicer?sname=JQGetListCustomTimePeriod" mouseRightMenu="true" contextMenuId="contextMenu"
		createUrl="jqxGeneralServicer?sname=createCustomTimePeriod&jqaction=C" addColumns="customTimePeriodId;parentPeriodId;organizationPartyId;periodTypeId;periodName;periodNum(java.lang.Long);fromDate(java.sql.Date);thruDate(java.sql.Date)"	
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCustomTimePeriod" editColumns="customTimePeriodId;parentPeriodId;organizationPartyId;periodTypeId;periodName;periodNum(java.lang.Long);fromDate(java.sql.Date);thruDate(java.sql.Date)"
		/>

<#assign organizationId = Static['com.olbius.util.SalesPartyUtil'].getCompanyInProperties(delegator)/>
<div id="alterpopupWindow1" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="CustomTimePeriodForm" class="form-horizontal">
			<div class="row-fluid no-left-margin">
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input type="text" id="customTimePeriodIdAdd"/>
						</div>
					</div>
				
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAParentCustomTimePeriod}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="parentPeriodIdAdd"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAFormFieldTitle_organizationPartyId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input id="organizationPartyIdAdd" value="${organizationId?if_exists}"/>
						</div>
					</div>
		
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.AccountingPeriodType}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodTypeIdAdd"></div>
						</div>
					</div>
				</div>
				
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodName}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input type="text" id="periodNameAdd"/>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodNumber}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodNumAdd"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonFromDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="fromDateNewAdd"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonThruDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="thruDateNewAdd"></div>
						</div>
					</div>		
				</div>
			</div>		
				
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
						<button type="button" id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<div id="alterpopupEditCTP" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="CustomTimePeriodEditForm" class="form-horizontal">
			<input type="hidden" value="${parameters.customTimePeriodId?if_exists}" id="customTimePeriodId" name="customTimePeriodId" />
			<div class="row-fluid no-left-margin">
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAParentCustomTimePeriod}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="parentPeriodIdEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.DAFormFieldTitle_organizationPartyId}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="organizationPartyIdEdit">
								<#--<div id="jqxOrganizationPartyGrid2" ></div>-->
							</div>
						</div>
					</div>
		
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.AccountingPeriodType}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodTypeIdEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodName}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<input type="text" id="periodNameEdit"/>
						</div>
					</div>
				</div>
				
				<div class="span6">
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right asterisk">${uiLabelMap.AccountingPeriodNumber}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="periodNumEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonFromDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="fromDateNewEdit"></div>
						</div>
					</div>
			
					<div class="row-fluid no-left-margin">
						<label class="span5 align-right">${uiLabelMap.CommonThruDate}</label>
						<div class="span7" style="margin-bottom: 10px;">
							<div id="thruDateNewEdit"></div>
						</div>
					</div>		
				</div>
			</div>		
				
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
						<button type="button" id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-pencil'></i> ${uiLabelMap.Edit}</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-pencil"></i>${StringUtil.wrapString(uiLabelMap.DAEdit)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
	</ul>
</div>

<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<#assign parentPeriod = delegator.findByAnd("CustomTimePeriod", null, null, false)>
<#assign periodTypePT = delegator.findList("PeriodType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("periodTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].LIKE ,"SALES_%"), null, null, null, false)>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;

	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
	    var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    var tmpKey = $.trim($(args).text());
	    if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
	    	$("#jqxgrid").jqxGrid('updatebounddata');
	    } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
	    	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var customTimePeriodId = data.customTimePeriodId;
				var url = 'viewCustomTimeSalesPeriod?customTimePeriodId=' + customTimePeriodId;
				var win = window.open(url, '_self');
				win.focus();
			}
	    } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
	    	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var customTimePeriodId = data.customTimePeriodId;
				var url = 'viewCustomTimeSalesPeriod?customTimePeriodId=' + customTimePeriodId;
				var win = window.open(url, '_blank');
				win.focus();
			}
	    } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAEdit)}") {
	    		var wtmp = window;
	    	   var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	   var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
	    	   var tmpwidth = $('#alterpopupEditCTP').jqxWindow('width');
	    	   $('#alterpopupEditCTP').jqxWindow({ width: 860, height : 250,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7 });
	    	   $("#alterpopupEditCTP").jqxWindow('open');
	    	   $("#parentPeriodIdEdit").jqxDropDownList({ source: parentPeriod, width: '198px', height: '25px',displayMember: "periodName", valueMember : "parentPeriodId"});
	    	   $("#periodTypeIdEdit").jqxDropDownList({ source: periodTypePT, width: '198px', height: '25px',displayMember: "description", valueMember : "periodTypeId"});
	    	   $('#periodNameEdit').jqxInput({width : '193px',height : '19px', value: data.periodName});
	    	   $('#periodNumEdit').jqxNumberInput({width : '198px',height : '25px', value: data.periodNum,spinButtons: false, inputMode: 'simple', decimalDigits: 0});
	    	   $("#fromDateNewEdit").jqxDateTimeInput({width: '198px', height: '25px', value: data.fromDate});
	    	   $("#thruDateNewEdit").jqxDateTimeInput({width: '198px', height: '25px', value: data.thruDate});
	    	   $('#organizationPartyIdEdit').jqxDropDownButton({ width: 198, height: 25});
	    	   $("#parentPeriodIdEdit").jqxDropDownList('val', data.parentPeriodId);
	    	   $("#periodTypeIdEdit").jqxDropDownList('val', data.periodTypeId);
	    	   $("#organizationPartyIdEdit").jqxDropDownButton('val', data.organizationPartyId);
	    	   $("#organizationPartyIdEdit").jqxDropDownButton({ disabled: true});
	    	   $("#periodTypeIdEdit").jqxDropDownList({autoDropDownHeight: true});
	    		
	    	   $("#alterSave2").click(function () {
	    		   $('#CustomTimePeriodEditForm').jqxValidator('validate');
	    		   $('#CustomTimePeriodEditForm').jqxValidator({
	    			   rules : [
	    			            {input: '#periodNameEdit', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule: 
				        			function (input, commit) {
				        				var value = $(input).val();
										if(/^\s*$/.test(value)){
											return false;
										}
										return true;
									}
								},
								{input: '#thruDateNewEdit', message: '${uiLabelMap.DAThruDateMustNotBeEmpty}', action: 'blur', rule: 
									function (input, commit) {
										if($('#thruDateNewEdit').jqxDateTimeInput('getDate') == null || $('#thruDateNewEdit').jqxDateTimeInput('getDate') == ''){
											return false;
										}
										return true;
									}
								},
								{input: '#fromDateNewEdit', message: '${uiLabelMap.DAFromDateNotBeEmpty}', action: 'blur', rule: 
									function (input, commit) {
										if($('#fromDateNewEdit').jqxDateTimeInput('getDate') == null || $('#fromDateNewEdit').jqxDateTimeInput('getDate') == ''){
											return false;
										}
										return true;
									}
								},
						]
					});
	    				
	    		   $('#CustomTimePeriodEditForm').on('validationSuccess',function(){
	    			   var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    			   if (rowindex >= 0) {
	    				   var data1 = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
	    				   var row = {
			            		customTimePeriodId: data1.customTimePeriodId,
			            		parentPeriodId : $('#parentPeriodIdEdit').val(),
			        			organizationPartyId: $('#organizationPartyIdEdit').val(),
			        			periodTypeId : $('#periodTypeIdEdit').val(),
			        			periodName : $('#periodNameEdit').val(),
			        			periodNum : $('#periodNumEdit').val(),
			        			fromDate : $('#fromDateNewEdit').jqxDateTimeInput('getDate'),
			        			thruDate : $('#thruDateNewEdit').jqxDateTimeInput('getDate'),
	    				   };
	    				   var rowID = $('#jqxgrid').jqxGrid('getrowid', rowindex);
				           $('#jqxgrid').jqxGrid('updaterow', rowID, row);
				           $('#CustomTimePeriodEditForm').jqxValidator('hide');
				           $("#alterpopupEditCTP").jqxWindow('hide');
				           $("#alterpopupEditCTP").jqxWindow('close');
	    			   }
				});
		    });
    	}
	});

	function fixSelectAll(dataList) {
		var sourceST = {
		        localdata: dataList,
		        datatype: "array"
	    };
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
	    var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		return uniqueRecords2;
	}

	var parentPeriod = [
	                     <#list parentPeriod as pP>
	                     {
	                    	 parentPeriodId : "${pP.parentPeriodId?if_exists}",
	                    	 periodName : "${StringUtil.wrapString(pP.periodName?if_exists)}"
	                     },
	                     </#list>	
	                 ];
	
	var periodTypePT = [
	                    <#list periodTypePT as pT>
	                    {
	                    	periodTypeId : "${pT.periodTypeId?if_exists}",
	                 	   description : "${StringUtil.wrapString(pT.description?if_exists)}"
	                    },
	                    </#list>	
	                ];

	$('#alterpopupWindow1').jqxWindow({ width: 860, height : 250,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7 });
	$("#parentPeriodIdAdd").jqxDropDownList({ source: parentPeriod, width: '198px', height: '25px',displayMember: "periodName", valueMember : "parentPeriodId"});
	$("#periodTypeIdAdd").jqxDropDownList({ source: periodTypePT, width: '198px', height: '25px',displayMember: "description", valueMember : "periodTypeId"});
	$('#periodNameAdd').jqxInput({width : '193px',height : '19px'});
	$('#periodNumAdd').jqxNumberInput({width : '198px',height : '25px',spinButtons: false, inputMode: 'simple', decimalDigits: 0});
	$("#fromDateNewAdd").jqxDateTimeInput({width: '198px', height: '25px'});
	$("#thruDateNewAdd").jqxDateTimeInput({width: '198px', height: '25px', allowNullDate: true, value: null});
	$('#customTimePeriodIdAdd').jqxInput({width : '193px',height : '19px'});
	$('#periodNumAdd').jqxNumberInput('val', 1);
	$("#organizationPartyIdAdd").jqxInput({ disabled: true});
	$("#periodTypeIdAdd").jqxDropDownList({autoDropDownHeight: true}); 

//$("#thruDateNewAdd").jqxDateTimeInput('val', null);
	$('#organizationPartyIdAdd').jqxInput({width : '193px',height : '19px'});

	$('#CustomTimePeriodForm').jqxValidator({
		rules : [
		         {input: '#organizationPartyIdAdd', message: '${uiLabelMap.DANotYetChooseItem}', action: 'blur', rule: 
	    			function (input, commit) {
	    				var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#periodNameAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule: 
	    			function (input, commit) {
	    				var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#periodNumAdd', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur', rule: 
	    			function (input, commit) {
	    				var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#thruDateNewAdd', message: '${uiLabelMap.DAThruDateMustNotBeEmpty}', action: 'blur', rule: 
					function (input, commit) {
						if($('#thruDateNewAdd').jqxDateTimeInput('getDate') == null || $('#thruDateNewAdd').jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDateNewAdd', message: '${uiLabelMap.DAFromDateNotBeEmpty}', action: 'blur', rule: 
					function (input, commit) {
						if($('#fromDateNewAdd').jqxDateTimeInput('getDate') == null || $('#fromDateNewAdd').jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#periodTypeIdAdd', message: '${uiLabelMap.DAPeriodTypeNotBeEmpty}', action: 'blur', rule: 
	    			function (input, commit) {
						if($('#periodTypeIdAdd').val() == null || $('#periodTypeIdAdd').val() == ''){
							return false;
						}
						return true;
					}
				},
		]
	});


	$('#alterSave1').click(function(){
		$('#CustomTimePeriodForm').jqxValidator('validate');
	});
	
	$('#CustomTimePeriodForm').on('validationSuccess',function(){
		var row = {};
		row = {
				customTimePeriodId : $('#customTimePeriodIdAdd').val(),
				parentPeriodId : $('#parentPeriodIdAdd').val(),
				organizationPartyId: $('#organizationPartyIdAdd').val(),
				periodTypeId : $('#periodTypeIdAdd').val(),
				periodName : $('#periodNameAdd').val(),
				periodNum : $('#periodNumAdd').val(),
				fromDate : $('#fromDateNewAdd').jqxDateTimeInput('getDate'),
				thruDate : $('#thruDateNewAdd').jqxDateTimeInput('getDate'),
		};
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	// select the first row and clear the selection.
		$("#jqxgrid").jqxGrid('clearSelection');                        
		$("#jqxgrid").jqxGrid('selectRow', 0);  
		$("#alterpopupWindow1").jqxWindow('close');
	});

	$('#alterpopupWindow1').on('close',function(){
		$('#CustomTimePeriodForm').jqxValidator('hide');
		$('#customTimePeriodIdAdd').val('');
		$('#parentPeriodIdAdd').val('');
		$('#periodTypeId').val('');
		$('#periodName').val('');
		$('#periodNum').val('');
		$('#fromDateNewAdd').val(new Date('${nowTimestamp}'));
		$('#thruDateNewAdd').val('');
		$('#CustomTimePeriodForm').trigger('reset');
	});


</script>
