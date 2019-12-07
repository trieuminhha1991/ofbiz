<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script>
	<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
	var statuses = [
		<#if statuses?exists>
			<#list statuses as status>
				{
					statusId : "${status.statusId}",
					description : "${status.description}",
				},
			</#list>
		</#if>
	];
	var periodTypes = [
		<#list periodTypes as period>
			{
				periodTypeId : "${period.periodTypeId}",
				description : "${StringUtil.wrapString(period.description?default(''))}"
			},
		</#list>	
	];
	
	var theme = 'olbius';
	var treePartyGroupArr = new Array();
	<#list treePartyGroup as tree>
		var row = {};
		row["id"] = "${tree.id}_partyGroupId";
		row["text"] = "${tree.text}";
		row["parentId"] = "${tree.parentId}_partyGroupId";
		row["value"] = "${tree.idValueEntity}"
		treePartyGroupArr[${tree_index}] = row;
	</#list>  
	 var sourceTreePartyGroup =
	 {
	     datatype: "json",
	     datafields: [
	         { name: 'id'},
	         { name: 'parentId'},
	         { name: 'text'} ,
	         { name: 'value'}
	     ],
	     id: 'id',
	     localdata: treePartyGroupArr
	 };
	 
	 var dataAdapter = new $.jqx.dataAdapter(sourceTreePartyGroup);
	 dataAdapter.dataBind();
	 var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
	 
	 var yearCustomTimePeriod = [
   		<#if customTimePeriodYear?has_content>
   			<#list customTimePeriodYear as customTimePeriod>
   				{
   					customTimePeriodId: "${customTimePeriod.customTimePeriodId}",
   					periodTypeId: "${customTimePeriod.periodTypeId}",
   					periodName: "${StringUtil.wrapString(customTimePeriod.periodName)}",
   					fromDate: ${customTimePeriod.fromDate.getTime()},
   					thruDate: ${customTimePeriod.thruDate.getTime()}
   				},
   			</#list>
   		</#if>
   	];
	 
	$(document).ready(function () {
		$("#dropDownButton").jqxDropDownButton({ width: 250, height: 25, theme: theme, autoOpen: true});
		 
		 $('#jqxTree').jqxTree({ source: records,width: "250px", height: "200px", theme: theme});
		 <#if expandedList?has_content>
		 	<#list expandedList as expandId>
		 		$('#jqxTree').jqxTree('expandItem', $("#${expandId}_partyGroupId")[0]);
		 	</#list>
		 </#if>    
		 
		 $('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
		    var item = $('#jqxTree').jqxTree('getItem', event.args.element);
	    	setDropdownContent(event.args.element);	        	        
	     });
		 initJqxNotify();
		 initContextMenu();
		 initJqxDropDownList();
	});
	
	function initJqxNotify(){
		$("#jqxNotify").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, 
			autoClose: false, template: "info", appendContainer: "#jqxNotifyContainer"});
	}
	
	function initContextMenu(){
		var liElement = $("#contextMenu>ul>li").length;
		var contextMenuHeight = 30 * liElement; 
		$("#contextMenu").jqxMenu({ width: 280, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var payrollTableId = dataRecord.payrollTableId;
            if($(args).attr("action") == 'sendProposalPayrollTable'){
           		var data = {payrollTableId: payrollTableId};
           		sendAjaxRequest('sendProposalPayrollTable', data, $("#jqxNotify"), $("#ntfContent"), $('#jqxgrid'));
            }else if($(args).attr("action") == 'approvalPayroll'){
            	<#if parameters.requestId?exists>
            		var data = {requestId: "${parameters.requestId}", actionTypeId: "APPROVE"};
            		sendAjaxRequest('approvalPayrollTable', data, $("#jqxNotify"), $("#ntfContent"), $('#jqxgrid'));
            	</#if>
            }else if($(args).attr("action") == 'recalculateRequest'){
            	<#if parameters.requestId?exists>
	            	var data = {requestId: "${parameters.requestId}", actionTypeId: "DENY"};
	        		sendAjaxRequest('approvalPayrollTable', data, $("#jqxNotify"), $("#ntfContent"), $('#jqxgrid'));
            	</#if>
            }
		});
		
		var liElement2 = $("#contextMenu2>ul>li").length;
		var contextMenuHeight2 = 30 * liElement2; 
		$("#contextMenu2").jqxMenu({ width: 250, height: contextMenuHeight2, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		$("#contextMenu2").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var payrollTableId = dataRecord.payrollTableId;
            if($(args).attr("action") == 'calculatePayrollTable'){
            	submitForm(rowindex);
            }else if($(args).attr("action") == 'createPayrollInvoice'){
            	window.location.href = 'approvePayroll?payrollTableId=' + payrollTableId;
            }
		});
	}
	
	function sendAjaxRequest(url, data, notifyEle, ntfContent, gridEle){
		gridEle.jqxGrid({'disabled': true});
		gridEle.jqxGrid('showloadelement');
		notifyEle.jqxNotification('closeLast');
		$.ajax({
   			url: url,
   			data: data,
   			type: 'POST',
   			success: function(data){
   				if(data._EVENT_MESSAGE_){
   					ntfContent.html(data._EVENT_MESSAGE_);
    				notifyEle.jqxNotification({template: 'info'})
    				notifyEle.jqxNotification("open");
   				}else{
   					ntfContent.html(data._ERROR_MESSAGE_);
    				notifyEle.jqxNotification({template: 'error'})
    				notifyEle.jqxNotification("open");
   				}
   			},
   			complete: function(){
   				gridEle.jqxGrid({'disabled': false});	
   				gridEle.jqxGrid('hideloadelement');
   			}
   		});
	}
	
	function setDropdownContent(element){
		var item = $("#jqxTree").jqxTree('getItem', element);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		$("#dropDownButton").jqxDropDownButton('setContent', dropDownContent);
	}
	
	function initJqxDropDownList(){
		var sourceCustomTimeYear = {
				localdata: yearCustomTimePeriod,
                datatype: "array"
		};
		var dataAdapter = new $.jqx.dataAdapter(sourceCustomTimeYear);
		$('#yearCustomTime').jqxDropDownList({source: dataAdapter, displayMember: "periodName", valueMember: "customTimePeriodId", 
			height: 25, width: '100%', theme: 'olbius'
        });
		$("#monthCustomTime").jqxDropDownList({ displayMember: "periodName", valueMember: "customTimePeriodId", height: 25, width: '100%', theme: 'olbius'});
		<#if (customTimePeriodYear?size < 8)>
			$("#yearCustomTime").jqxDropDownList({autoDropDownHeight: true});
		</#if>
		
		initJqxDropdownlistEvent();
		
		<#if selectYearCustomTimePeriodId?exists>
			$("#yearCustomTime").jqxDropDownList('selectItem', "${selectYearCustomTimePeriodId}");
		<#else>
			$("#yearCustomTime").jqxDropDownList('selectIndex', 0 ); 
		</#if>
	}
	
	function initJqxDropdownlistEvent(){
		$("#yearCustomTime").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$.ajax({
					url: "getCustomTimePeriodByParent",
					data: {parentPeriodId: value},
					type: 'POST',
					success: function(data){
						if(data.listCustomTimePeriod){
							var listCustomTimePeriod = data.listCustomTimePeriod;
							var selectItem = listCustomTimePeriod.filter(function(item, index, array){
								var nowTimestamp = ${startDate.getTime()};
								if(item.fromDate <= nowTimestamp && item.thruDate >= nowTimestamp){
									return item;
								}
							});
							var tempSource = {
									localdata: listCustomTimePeriod,
					                datatype: "array"
							}
							var tmpDataAdapter = new $.jqx.dataAdapter(tempSource);
							$("#monthCustomTime").jqxDropDownList('clearSelection');
							$("#monthCustomTime").jqxDropDownList({source: tmpDataAdapter});
							if(selectItem.length > 0){
								$("#monthCustomTime").jqxDropDownList('selectItem', selectItem[0].customTimePeriodId);
							}else{
								$("#monthCustomTime").jqxDropDownList({selectedIndex: 0 });
							}
						}
					},
					complete: function(jqXHR, textStatus){
						
					}
				});
			}
		});
	}
</script>
<div class="row-fluid" id="jqxNotifyContainer">
	<div id="jqxNotify">
		<div id="ntfContent"></div>
	</div>
</div>
<#assign dataField="[{ name: 'payrollTableId', type: 'string' },
					 { name: 'payrollTableName', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp' },
					 { name: 'thruDate', type: 'date', other:'Timestamp' },
					 { name: 'customTimePeriodId', type: 'string'},					 
					 { name: 'groupName', type: 'string'},					 
					 { name: 'statusId', type: 'string'}]
					"/>				

<#assign columnlist="{datafield: 'customTimePeriodId', hidden: true},
					{ text: '${uiLabelMap.HRPayrollTableId}', datafield: 'payrollTableId', width: 120, hidden: false, editable: false,
						cellsrenderer: function(row, column, value){
							return '<a href=\"viewPayrollTable?payrollTableId='+ value + '\">' + value + '</a>';
						}
					 },
 					 { text: '${uiLabelMap.PayrollTableName}', datafield: 'payrollTableName', width: 200},
					 {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: 250, filterable: false, editable: false},
					 { text: '${uiLabelMap.PayrollTableTimePeriod}', datafield: 'fromDate',cellsformat: 'MM/yyyy', filtertype:'range', filterable: false, columntype: 'datetimeinput', editable: false},
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', hidden: true},
					 
                     { text: '${uiLabelMap.statusId}', datafield: 'statusId', width: 200, editable:false, filtertype: 'checkedlist',
                     	createfilterwidget: function(column, columnElement, widget){
						    var filterBoxAdapter = new $.jqx.dataAdapter(statuses, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'statusId', filterable:true, searchMode:'containsignorecase'});
						},
						cellsrenderer : function(row, column, value){
							var val = $('#jqxgrid').jqxGrid('getrowdata', row);
							for(var x in statuses){
								if(statuses[x].statusId  && val.statusId && statuses[x].statusId == val.statusId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+statuses[x].description+'</div>';		
								}
							}
						}
                     }
					 
					"/>

<@jqGrid url="jqxGeneralServicer?sname=JQGetPayroll" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	showtoolbar = "true" deleterow="true" jqGridMinimumLibEnable="false" mouseRightMenu="true" contextMenuId="contextMenu2"
	removeUrl="jqxGeneralServicer?sname=deletePayrollTable&jqaction=D" deleteColumn="payrollTableId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPayrollTableRecord" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="payrollTableName;departmentList;customTimePeriodId;partyId" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePayrollTableRecord"  editColumns="payrollTableId;payrollTableName"
/>
	
<div id='contextMenu' style="display: none">
      <ul>
          <li action="sendProposalPayrollTable">
          	${uiLabelMap.sendProposalPayrollTable}
          </li>
          <#if parameters.requestId?exists>
	          <li action="approvalPayroll">
	          	${uiLabelMap.CommonOk}
	          </li>
	          <li action="recalculateRequest">
	          	${uiLabelMap.RequestRecalculate}
	          </li>
          </#if>
      </ul>
  </div>	
  
<div id='contextMenu2' style="display: none">
      <ul>
          <li action="calculatePayrollTable">
          	<i class="icon-play"></i>${uiLabelMap.PayrollTableCalculate}
          </li>
          <li action="createPayrollInvoice">
          	<i class="icon-file-text"></i>${uiLabelMap.CreatePayrollInvoice}
          </li>
          
      </ul>
  </div>	
	
<div id="popupAddRow" class='hide'>
    <div>${uiLabelMap.EditPayrollTable}</div>
    <div class='form-window-container'>
    	<div class='form-window-content'>
    		<form id="popupAddRowForm" action="" class="form-horizontal">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">
							${uiLabelMap.PayrollTableName}
						</label>
					</div>
					<div class="span7">
						<input type="text" name="payrollTableNameAdd" id="payrollTableNameAdd">
					</div>
				</div>		
								   	
		   		<div class='row-fluid margin-bottom10'>
		   			<div class='span5 text-algin-right'>
						<label class="control-label asterisk">
			   				${uiLabelMap.Department}
			   			</label>		   			
		   			</div>
		   			<div class="span7">
		   				<div id="dropDownButton" style="margin-top: 5px;">
							<div id="jqxTree">
		   					</div>
						</div>	
		   			</div>
		   		</div>	
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label asterisk">${uiLabelMap.CommonTime}</label>
					</div>
					<div class="span7">
						<div class="row-fluid">
							<div class="span12">
								<div class="span5">
									<div id="monthCustomTime"></div>
								</div>
								<div class="span6">
									<div id="yearCustomTime"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="control-label">&nbsp;</label>					
					</div>
					<div class="span7">
						<a href="javascript:void(0)" id="configPayrollFormula">${uiLabelMap.ConfigPayrollFormula}</a>
					</div>
				</div>
	    	</form>
    	</div>
    	<div class="form-action">
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
    	</div>
    </div>
</div>  
<div id="configPayrollFormulaWindow" style="display: none;">
	<div>${uiLabelMap.ConfigPayrollFormula}</div>
	<div class="row-fluid">	
		<div class="span12">
			<div id="treeFormula" class="row-fluid"></div>
			<div class="row-fluid" style="text-align: center; margin-top: 10px">
				<button class="btn btn-small btn-primary icon-ok" id="updateCalcFormula">${uiLabelMap.CommonUpdate}</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var nowDate = new Date();
	var previousFirstDate = new Date(nowDate.getFullYear(), nowDate.getMonth() - 1, 1);
	var previousLastDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), 0);
	var dataFormula = new Array();
	var checkedFormula = new Array();
	
	<#assign expandItemList = []> 
	<#list payrollChar as characteristic>
		var row = {};
		row["id"]= "${characteristic.payrollCharacteristicId}_parent";
		row["parentid"]= "-1";
		row["text"]= "${StringUtil.wrapString(characteristic.description?if_exists)}";
		row["value"] = "${characteristic.payrollCharacteristicId}";
		dataFormula.push(row);
	</#list>
	<#if payrollFormula?has_content>
		<#list payrollFormula as formula>
			var row = {};
			row["id"]= "${formula.code}_code";
			row["parentid"]= "${formula.payrollCharacteristicId}_parent";
			row["text"]= "${StringUtil.wrapString(formula.name)}";
			row["value"] = "${formula.code}";
			dataFormula.push(row);
			<#if formula.get("includedPayrollTable")?exists>
				<#if formula.get("includedPayrollTable") == "Y">
					checkedFormula.push("${formula.code}_code");
					<#if expandItemList?seq_index_of(formula.payrollCharacteristicId + "_parent") == -1>
						<#assign expandItemList = expandItemList + [formula.payrollCharacteristicId + "_parent"]> 
					</#if>
				</#if>
			<#else>
				<#if formula.payrollCharacteristicId?exists>
					checkedFormula.push("${formula.code}_code");			
					<#if expandItemList?seq_index_of(formula.payrollCharacteristicId + "_parent") == -1>
						<#assign expandItemList = expandItemList + [formula.payrollCharacteristicId + "_parent"]> 
					</#if>
				</#if>
			</#if>
		</#list>
	</#if>
	function submitForm(row){
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		var statusId = data.statusId;
		var payrollTableId = data.payrollTableId;
		if(statusId == 'PYRLL_TABLE_CREATED'){
			calculatePayrollRecord(payrollTableId);
			$('#jqxgrid').jqxGrid({ disabled: true});
		}else if(statusId == 'PYRLL_TABLE_CALC'){
			bootbox.dialog("${uiLabelMap.PayrollCalculated_Recalculated}",
				[
					{
					    "label" : "${uiLabelMap.CommonSubmit}",
					    "class" : "icon-ok btn btn-mini btn-primary",
					    "callback": function() {
					    	calculatePayrollRecord(payrollTableId);	
					    }
					},
					{
						  "label" : "${uiLabelMap.CommonCancel}",
			    		   "class" : "btn-danger icon-remove btn-mini",
			    		   "callback": function() {
			    		   
			    		   }
					}
				]		
			);
		}
	}
	
	function calculatePayrollRecord(payrollTableId){
		$('#jqxgrid').jqxGrid({ disabled: true});
		$('#jqxgrid').jqxGrid('showloadelement');
		$.ajax({
			url: 'calcPayroll',
			data: {payrollTableId: payrollTableId},
			type: 'POST',
			success: function(data){
				if(data._EVENT_MESSAGE_){
					window.location.href = 'viewPayrollTable?payrollTableId=' + payrollTableId;
				}else{
					$("#ntfContent").html(data._ERROR_MESSAGE_);
					$("#jqxNotify").jqxNotification({template: 'error'});
    				$("#jqxNotify").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$('#jqxgrid').jqxGrid({ disabled: false});
				$('#jqxgrid').jqxGrid('hideloadelement');
			}
		});
	}
    
    var periodTypeDd = $('#periodTypeIdDd');
	$(document).ready(function(){
		$.validator.setDefaults({ ignore: ":hidden:not(select)" })
		var gridElement = $("#jqxgrid");
		var popup = $("#popupAddRow");
		
		$("#alterCancel").click(function(){
			popup.jqxWindow('close');
		});
	    popup.on('close', function (event) { 
	    	$("#popupAddRowForm").jqxValidator('hide');
	    }); 
	    $("#configPayrollFormula").click(function(){
	    	$("#configPayrollFormulaWindow").jqxWindow('open');
	    });
	    var sourceFormula =
        {
            datatype: "json",
            datafields: [
                { name: 'id' },
                { name: 'parentid' },
                { name: 'text' },
                { name: 'value' }
            ],
            id: 'id',
            localdata: dataFormula
        };
	    var dataAdapterFormula = new $.jqx.dataAdapter(sourceFormula);
	    dataAdapterFormula.dataBind();
	    var records = dataAdapterFormula.getRecordsHierarchy('id', 'parentid', 'items', [{ name: 'text', map: 'label'}]);
	    $('#treeFormula').jqxTree({ source: records, width: '380px',height: '370px', hasThreeStates: true, checkboxes: true, hasThreeStates: true, theme:'energyblue'});
	    
	    for(var i = 0; i < checkedFormula.length; i++){
	    	 $("#treeFormula").jqxTree('checkItem', $("#"+ checkedFormula[i])[0], true);	
	    }
	    <#list expandItemList as expand>	    	
	    	$("#treeFormula").jqxTree('expandItem', $("#${expand}")[0]);
	    </#list>
	    $("#configPayrollFormulaWindow").jqxWindow({isModal: true, autoOpen: false, width: 400, height: 470, resizable: true});
	    
	    $("#updateCalcFormula").click(function(){
	    	$("#updateCalcFormula").attr('disabled','disabled');
	    	var items = $('#treeFormula').jqxTree('getCheckedItems');
	    	var codeSelected = new Array();
	    	for(var i = 0; i < items.length; i++){
	    		codeSelected.push({"code": items[i].value});
	    	}
	    	$.ajax({
	    		url: "updateFormulaIncludedPayrollTable",
	    		type: "POST",
	    		data: {codeSelected: JSON.stringify(codeSelected)},
	    		success: function(data){
	    			
	    		},
	    		complete: function(jqXHR, status){
	    			$("#configPayrollFormulaWindow").jqxWindow('close');
	    			$("#updateCalcFormula").removeAttr('disabled');
	    		}
	    	});
	    });
	    
	    popup.jqxWindow({
	        width: 500, height: 265, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#Cancel"),
	        initContent: function(){
	        	initPopupContent();
	        }
	    });
	    
		$("#popupAddRowForm").jqxValidator({
			rules: [
		    {
				input: "#dropDownButton", message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'blur',
			    rule: function (input, commit) {
					var items = $("#jqxTree").jqxTree('getSelectedItem');
					if(!items){
						return false;
					}
					return true;
			   }
		    },	        
		    {
				input: '#payrollTableNameAdd',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			}
		    ],
		 });
		
		$("#alterSave").click(function () {
			if(!$('#popupAddRowForm').jqxValidator('validate')){
				return;
			}
			bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ConfirmCreatePayrollTable)}",
				[{
					"label" : "${uiLabelMap.CommonSubmit}",
	    		    "class" : "btn-primary btn-mini icon-ok",
	    		    "callback": function() {
	    		 		createNewPayrollTableRecord();   	
	    		    }	
				},
				{
	    		    "label" : "${uiLabelMap.CommonClose}",
	    		    "class" : "btn-danger btn-mini icon-remove",
	    		    "callback": function() {
	    		    	
	    		    }
	    		}]		
			);
	    });
	});
	
	function createNewPayrollTableRecord(){
		var departmentSelected = $("#jqxTree").jqxTree('val');
		var gridElement = $("#jqxgrid");
		var departmentId = departmentSelected.value
    	var row = { 
    		payrollTableName: $("input[name='payrollTableNameAdd']").val(),
    		partyId: departmentId,
    		customTimePeriodId: $("#monthCustomTime").val()
    	  };
	    gridElement.jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        gridElement.jqxGrid('clearSelection');                        
        gridElement.jqxGrid('selectRow', 0);  
        $("#popupAddRow").jqxWindow('close');	
	}
	
    function initPopupContent(){
    	$("#payrollTableNameAdd").jqxInput({width: 245, height: 20, theme: 'olbius'});
 		
    } 
</script>