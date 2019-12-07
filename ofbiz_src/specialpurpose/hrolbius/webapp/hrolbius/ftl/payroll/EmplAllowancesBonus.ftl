<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>		
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>				
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>	
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>			
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<#assign dataFields = "[{name: 'partyId', type: 'string'},
						{name: 'partyName', type: 'string'},
						{name: 'department', type: 'string'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'emplBonusAmount', type: 'number'},
						{name: 'emplAllowances', type: 'number'}
						]" />
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>						
<script type="text/javascript">
	var periodTypeArr = [
		<#if periodTypeList?has_content> 	                     	
			<#list periodTypeList as periodType>
				{
					periodTypeId: "${periodType.periodTypeId}",
					description: "${StringUtil.wrapString(periodType.description?if_exists)}" 
				},
			</#list>
		</#if>	
	];

	var codeArr = [
       		<#if payrollParam?has_content>
       			<#list payrollParam as param>
       				{
       					code: "${param.code}",
       					description: '${StringUtil.wrapString(param.name)}',
       					type: "${StringUtil.wrapString(param.type)}",
       					periodTypeId: '${param.periodTypeId}'
       				},
       			</#list>
       		</#if>
       	];
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', filterable: false, editable: false, cellsalign: 'left', width: 130},
						   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 160},
						   {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', cellsalign: 'left', width: 200},
						   {text: '${StringUtil.wrapString(uiLabelMap.EmplPositionTypeId)}', datafield: 'emplPositionTypeId', width: 180},
						   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}', datafield: 'emplBonusAmount', width: 180,
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}   
						   },
						   {text: '${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}', datafield: 'emplAllowances',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}   
						   }
	"/>	
	
	<#assign rowDetails = "function (index, parentElement, gridElement, datarecord){
		var partyId = datarecord.partyId;
		var id = datarecord.uid.toString();
		var urlStr = 'getEmplParamCharacteristic';
	 	var tabsdiv = $($(parentElement).children()[0]);
	 	if(tabsdiv != null){
	 		var bonusEmpl = tabsdiv.find('.bonusEmpl');
	 		var allowance = tabsdiv.find('.allowanceEmpl');
	 		var selection = $('#dateTimeInput').jqxDateTimeInput('getRange');
	 		var fromDate = selection.from.getTime();
	    	var thruDate = selection.to.getTime();
	    	var bonusDataSource = {
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
	    	
	    	var allowanceDataSource = {
    			datafield:[
					{name: 'code', type: 'string'},				           
					{name: 'value', type: 'number'}				           
				],
				cache: false,
				datatype: 'json',
				type: 'POST',
				data: {partyId: partyId, fromDate: fromDate, thruDate: thruDate, paramCharacteristicId: 'PHU_CAP'},
				url: urlStr,
		        root: 'payrollEmplParamDetails',	
	    	};
	    	
	    	var nestedBonusGridAdapter = new $.jqx.dataAdapter(bonusDataSource);
	    	var nestedAllowanceGridAdapter = new $.jqx.dataAdapter(allowanceDataSource);
	    	bonusEmpl.jqxGrid({
				source: nestedBonusGridAdapter, width: '100%', height: 170,
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
	    	allowance.jqxGrid({
				source: nestedAllowanceGridAdapter, width: '100%', height: 170,
				showheader: true,
				showtoolbar: false,
				theme: 'olbius',
		 		pageSizeOptions: ['15', '30', '50', '100'],
		        pagerMode: 'advanced',
		        pageable: true,
		        columns:[
		        	{text: '${StringUtil.wrapString(uiLabelMap.AllowancesType)}', width: 230, datafield: 'code',
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
	    	$(tabsdiv).jqxTabs({ width: '950px', height: 220});
	 	}
	}
	"/>
	
	<#assign rowdetailstemplateAdvance = "<ul style='margin-left: 30px;'><li class='title'>${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}</li><li class='title'>${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}</li></ul><div class='bonusEmpl'></div><div class='allowanceEmpl'></div>"/>
	<#if expandedList?has_content>
		<#assign expandTreeId=expandedList[0]>
	<#else>
		<#assign expandTreeId="">
	</#if>
</script>

<script>	
	$(document).ready(function () {
		initJqxDateTime();
		initBtnEvent();
		initJqxWindow();
		createJqxGridSearchEmpl();
		initJqxNotification();
		initJqxValidator();
	});
	<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
	<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp, 0, -1)/>
	<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(monthStart, timeZone, locale)/>
	
	function initJqxNotification(){
		$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
	}
	
	function initJqxDropDownList(){
		var sourceParameters =
	    {
	        localdata: codeArr,
	        datatype: "array"
	    };
		var dataAdapterParameter = new $.jqx.dataAdapter(sourceParameters);
		
		$('#parameterCodeNew').jqxDropDownList({ source: dataAdapterParameter, 	    	
	    	displayMember: "description", valueMember: "code", itemHeight: 25, height: 25, width: '98%', theme: 'olbius',
	        renderer: function (index, label, value) {
	        	
	        		if(value == codeArr[index].code){
	        			return codeArr[index].description;
	        		}
	        	
	            return value;
	        }
	    });
		$('#parameterCodeNew').on('select', function(event){
			 var args = event.args;
			 if(args){
				 var index = args.index;
				 var datarecord = codeArr[index];
				 var periodTypeId = datarecord.periodTypeId;
				 for(var i = 0; i < periodTypeArr.length; i++){
					 if(periodTypeId == periodTypeArr[i].periodTypeId){
						 $("#periodTypeParamNew").html(periodTypeArr[i].description);
						 break;
					 }
				 }
				 if(datarecord.type == 'CONSTPERCENT'){
		    		$("#parameterValueNew").jqxNumberInput({digits: 3, symbolPosition: 'right', symbol: '%'});
		    	}else{
		    		$("#parameterValueNew").jqxNumberInput({decimalDigits:0, digits: 9, max: 999999999, theme: 'olbius', min: 0, symbol: ''});
		    	}
			 }
		});
	}
	
	function initJqxValidator(){
		$("#popupWindowPayrollEmplParams").jqxValidator({
			rules:[
				{input: '#searchEmpl', message: '${uiLabelMap.CommonRequired}', action: 'blur',
					rule: function (input, commit){
						var value = $("#partyIdPayrollParam").val();
						if(!value){
							return false
						}
						return true;
					}	
				},        
				{input: '#parameterCodeNew', message: '${uiLabelMap.CommonRequired}', action: 'blur',
					rule: function (input, commit){
						var value = input.val();
						if(!value){
							return false
						}
						return true;
					}	
				}        
			]
		});
	}
	
	function initBtnEvent(){
		$("#addNew").click(function(event){
			openJqxWindow(jQuery("#popupWindowPayrollEmplParams"));
		});
		$("#searchEmpl").click(function(event){
			openJqxWindow($("#popupWindowEmplList"));
		});
		$("#btnCancel").click(function(event){
			$("#popupWindowPayrollEmplParams").jqxWindow('close');
		});
		$("#btnSave").click(function(event){
			var valid = $("#popupWindowPayrollEmplParams").jqxValidator('validate');
			if(!valid){
				return false;
			}
			$("#btnSave").attr("disabled", "disabled");
			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.CreateEmplPayrollParametersWarning)}",
					[{
		    		    "label" : "${uiLabelMap.CommonSubmit}",
		    		    "class" : "btn-primary btn-mini icon-ok",
		    		    "callback": function() {
		    		    	createEmplPayrollParameters();
		    		    }
		    		},
					{
		    		    "label" : "${uiLabelMap.CommonClose}",
		    		    "class" : "btn-danger btn-mini icon-remove",
		    		    "callback": function() {
		    		    	$("#btnSave").removeAttr("disabled");
		    		    }
		    		}
					
					]
			);
		});
	}
	
	function createEmplPayrollParameters(){
		$('#jqxgrid').jqxGrid({disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		var thruDate = $("#thruDateParamNew").jqxDateTimeInput('getDate');
		var dataSubmit = {};
		dataSubmit["partyId"] = $("#partyIdPayrollParam").jqxInput('val').value;
		dataSubmit["code"] = $('#parameterCodeNew').val();
		dataSubmit["value"] = $("#parameterValueNew").val();
		dataSubmit["fromDate"] = $("#fromDateParamNew").jqxDateTimeInput('getDate').getTime();
		if(thruDate){
			dataSubmit["thruDate"] = thruDate.getTime();
		}
		$.ajax({
			url: 'createEmplPayrollParameters',
			data: dataSubmit,
			type: 'POST',
			success: function(data){
				$("#jqxNtf").jqxNotification('closeLast');
				if(data.responseMessage == "success"){
					$('#jqxgrid').jqxGrid('updatebounddata');
					$("#jqxNtfContent").text(data.successMessage);
					$("#jqxNtf").jqxNotification({template: 'info'});
					$("#jqxNtf").jqxNotification("open");
				}else{
					$("#jqxNtfContent").text(data.errorMessage);
					$("#jqxNtf").jqxNotification({template: 'error'});
					$("#jqxNtf").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$("#btnSave").removeAttr("disabled");
				$('#jqxgrid').jqxGrid('hideloadelement');
				$('#jqxgrid').jqxGrid({disabled: false});
				$("#popupWindowPayrollEmplParams").jqxWindow('close');
				//clearDataInWindow();
			}
		});
	}
	
	function initJqxDateTime(){
		$("#dateTimeInput").jqxDateTimeInput({ width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
		var fromDate = new Date(${monthStart.getTime()});
		var thruDate = new Date(${monthEnd.getTime()});
		$("#dateTimeInput").jqxDateTimeInput('setRange', fromDate, thruDate);
		$("#dateTimeInput").on('change', function(event){
			var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
			var fromDate = selection.from.getTime();
		    var thruDate = selection.to.getTime();
		    var item = $("#jqxTree").jqxTree('getSelectedItem');
		    var partyId = item.value;
			refreshGridData(partyId, fromDate, thruDate);
		});
		$("#fromDateParamNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	    $("#thruDateParamNew").jqxDateTimeInput({width: '98%', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	    $("#thruDateParamNew").val(null);
	}
	
	function setDropdownContent(element){
		var item = $("#jqxTree").jqxTree('getItem', element);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		$("#jqxDropDownButton").jqxDropDownButton('setContent', dropDownContent);
	}
	
	function jqxTreeSelectFunc(event){
		var dataField = event.args.datafield;
		var rowBoundIndex = event.args.rowindex;
		var id = event.args.element.id;
		var item = $('#jqxTree').jqxTree('getItem', event.args.element);
		setDropdownContent(event.args.element);
		var partyId = item.value;
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
    	var fromDate = selection.from.getTime();
    	var thruDate = selection.to.getTime();
    	refreshGridData(partyId, fromDate, thruDate);
	}
	
	function jqxTreeEmplListSelect(event){
		var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
		var partyId = item.value;
		var tmpS = $("#EmplListInOrg").jqxGrid('source');
		tmpS._source.data = {partyGroupId: partyId};
		$("#EmplListInOrg").jqxGrid('source', tmpS);
	}
	
	function refreshGridData(partyGroupId, fromDate, thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=getEmplBonusAllowances&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	}	
	
	function initJqxWindow(){
		jQuery("#popupWindowPayrollEmplParams").jqxWindow({showCollapseButton: false,autoOpen: false,
			maxWidth: 520, minWidth: 520, height: 350, width: 520, isModal: true, theme:theme,
			initContent: function(){
				initJqxDropDownList();
				initJqxInput();
				$("#parameterValueNew").jqxNumberInput({decimalDigits: 0, width: '98%', height: '25px', spinButtons: false, digits: 9, max: 999999999, theme: 'olbius', min: 0});
			}	
		});
		jQuery("#popupWindowPayrollEmplParams").on('close', function(event){
			clearDataInWindow();
		});
		
		$('#popupWindowEmplList').jqxWindow({
		    showCollapseButton: true, autoOpen: false, maxWidth: "80%", minWidth: "50%", maxHeight: 500, height: 500, width: "80%", isModal: true, 
		    theme:'olbius', collapsed:false,
		    initContent: function () {  
		    	initJqxSplitter();
		    }
		});
		$('#popupWindowEmplList').on('open', function(event){
			<#if expandTreeId?has_content>
				$("#jqxTreeEmplList").jqxTree('expandItem', $("#${expandTreeId}_jqxTreeEmplList")[0]);
				$('#jqxTreeEmplList').jqxTree('selectItem', $("#${expandTreeId}_jqxTreeEmplList")[0]);
			</#if>
		});
		$('#popupWindowEmplList').on('close', function(event){
			$("#EmplListInOrg").jqxGrid('clearselection');
		});
	}
	
	function initJqxSplitter(){
		$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
	}
	
	function initJqxInput(){
		var source = function(query, response){
			var dataApdapter = new $.jqx.dataAdapter(
				{
					datatype: "json",
					datafields:
	                [
	                    { name: 'partyId' },
	                    { name: 'partyName'},
	                ],
	                url: "searchPartyId",
	                data:{
	                	maxRows: 12,
	                },
				},
				{
					autoBind: true,
					formatData: function (data) {
                        data.partyId_startsWith = query;
                        return data;
                    },
                    loadComplete: function (data) {
                        if (data.listParty.length > 0) {
                            response($.map(data.listParty, function (item) {
                                return {
                                    label: item.partyName + ' [' + item.partyId + ']',
                                    value: item.partyId
                                }
                            }));
                        }
                    }
				}
			);
		};
		$("#partyIdPayrollParam").jqxInput({ placeHolder: "${StringUtil.wrapString(uiLabelMap.EnterEmployeeId)}", source: source,
			height: 23, width: '86%', minLength: 1, theme: 'olbius', valueMember: 'partyId', displayMember:'partyName', items: 12});
	}
	
	function createJqxGridSearchEmpl(){
		var source = {
				datafield: [
					{name: 'partyId', type: 'string'},
					{name: 'partyName', type: 'string'},
					{name: 'emplPositionType', type: 'string'},
					{name: 'department', type: 'string'},
				],
				cache: false,
        		datatype: 'json',
        		type: 'POST',
        		url: 'getEmplListInOrg',
        		id: 'id',
        		data: {},
        		beforeprocessing: function (data) {
        			source.totalrecords = data.TotalRows;
		        },
		        pagenum: 0,
		        pagesize: 15,
		        root: 'listReturn'
		}
		var dataAdapter = new $.jqx.dataAdapter(source);
		var columnlist = [
          {text: '${uiLabelMap.EmployeeId}', datafield: 'partyId' , editable: false, cellsalign: 'left', width: 130},
		  {text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', editable: false, cellsalign: 'left', width: 130},
		  {text: '${uiLabelMap.Position}', datafield: 'emplPositionType', editable: false, cellsalign: 'left', width: 130},
		  {text: '${uiLabelMap.CommonDepartment}', datafield: 'department', editable: false, cellsalign: 'left'}
		];
		initJqxGrid($("#EmplListInOrg"), dataAdapter, columnlist, false, "");
		$("#EmplListInOrg").on('rowdoubleclick', function(event){
			var args = event.args;
		    var boundIndex = args.rowindex;
		    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
		    $('#popupWindowEmplList').jqxWindow('close');
		    $("#partyIdPayrollParam").jqxInput('val', {value: data.partyId, label: data.partyName + ' [' + data.partyId + ']'});  	
		});
	}
	
	function initJqxGrid(gridEle, gridAdapter, gridColumn, showToolbar, title){
		gridEle.jqxGrid({
			source: gridAdapter, 
    		width: '100%', 
    		height: 440,
    		autoheight: false,
    		virtualmode: true,
    		showtoolbar: showToolbar,
    		rendertoolbar: function (toolbar) {
    			var container = $("<div id='toolbarcontainer' class='widget-header'><h4>" + title + "</h4></div>");
    			toolbar.append(container);
    		},
    		rendergridrows: function () {
	            return gridAdapter.records;
	        },
	        pageSizeOptions: ['15', '30', '50', '100'],
	        pagerMode: 'advanced',
	        columnsResize: true,
	        pageable: true,
	        editable: false,
	        columns: gridColumn,
	        selectionmode: 'singlerow',
	        theme: 'olbius'
		});
	}
	
	function clearDataInWindow(){
		var nowDate = new Date(${nowTimestamp.getTime()});
		$("#periodTypeParamNew").html("${uiLabelMap.HRCommonNotSetting}");
		$("#partyIdPayrollParam").val('');
		$("#parameterCodeNew").jqxDropDownList('clearSelection');
		$("#parameterValueNew").val(0);
		$("#fromDateParamNew").val(nowDate);
		$("#thruDateParamNew").val(null);
	}
	
	function openJqxWindow(jqxWindowDiv){
		var wtmp = window;
		var tmpwidth = jqxWindowDiv.jqxWindow('width');
		jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
		jqxWindowDiv.jqxWindow('open');
	}
	
</script>	
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>

<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>					
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.BonusAndAllowancesStaff}</h4>
		<div class="widget-toolbar none-content">
			<button id="addNew" class="grid-action-button icon-plus-sign">${uiLabelMap.accAddNewRow}</button>
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
			<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
				 filterable="false" alternativeAddPopup="popupWindowAddPartyAttend" deleterow="false" editable="false" addrow="false"
				 url="" id="jqxgrid" jqGridMinimumLibEnable="false"
				 initrowdetails="true" initrowdetailsDetail=rowDetails rowdetailsheight="250" rowdetailstemplateAdvance=rowdetailstemplateAdvance
				 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlerow" 
				 />					
		</div>
	</div>
</div>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup expandTreeId=expandTreeId id="jqxTree" dropdownBtnId="jqxDropDownButton"/>	
<div class="row-fluid">
	<div id="popupWindowPayrollEmplParams" class='hide'>
		<div id="PayrollEmplParamsWindowHeader">
			${StringUtil.wrapString(uiLabelMap.SetEmplAllowancesAndBonus)}
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonEmployee}</label>
					</div>
					<div class="span7">
						<input type="text" id="partyIdPayrollParam">
						<img alt="search" id="searchEmpl" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
						style="
						   border: #d5d5d5 1px solid;
						   padding: 5.5px;
						   border-bottom-right-radius: 3px;
						   border-top-right-radius: 3px;
						   margin-left: -4px;
						   background-color: #f0f0f0;
						   border-left: 0px;
						   cursor: pointer;
						"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.parameterCode}</label>
					</div>
					<div class="span7">
						<div id="parameterCodeNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeParamNew">${uiLabelMap.HRCommonNotSetting}</div>
						</div>
					</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.parameterValue}</label>
					</div>
					<div class="span7">
						<div id="parameterValueNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.AvailableFromDate}</label>
					</div>
					<div class="span7">
						<div id="fromDateParamNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span7">
						<div id="thruDateParamNew"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>					
<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div class="">
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div id="ContentPanel" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>