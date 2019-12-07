<#assign dataFields = "[{name: 'partyId', type: 'string'},
						{name: 'partyName', type: 'string'},
						{name: 'currDept', type: 'string'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'rateAmount', type: 'number'},
						{name: 'rateCurrencyUomId', type: 'string'},
						{name: 'periodTypeId', type: 'string'},
						{name: 'allEmplPositionTypeId', type: 'string'},	
						{name: 'editAction', type: 'string'}]"/>
<@jqGridMinimumLib/>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>						
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript">
<#--/* if(event.args.datafield == 'deleteRecord'){
if(rowDetailData.rateTypeIdDetail && rowDetailData.workEffortIdDetail){
	var dataSubmitDelete = new Array();
	dataSubmitDelete.push({name: 'partyId', value: partyId});
	dataSubmitDelete.push({name: 'emplPositionTypeId', value: rowDetailData.emplPositionTypeIdDetail});
	dataSubmitDelete.push({name: 'periodTypeId', value: rowDetailData.periodTypeIdDetail});
	dataSubmitDelete.push({name: 'rateCurrencyUomId', value: rowDetailData.rateCurrencyUomIdDetail});
	dataSubmitDelete.push({name: 'rateTypeId', value: rowDetailData.rateTypeIdDetail});
	dataSubmitDelete.push({name: 'workEffortId', value: rowDetailData.workEffortIdDetail});
	dataSubmitDelete.push({name: 'fromDate', value: rowDetailData.fromDateDetail.getTime()});
	jQuery.ajax({
		url: 'deletePartyRateAmount',
		data: dataSubmitDelete,
		type: 'POST',
		success:function(data){
			if(data._EVENT_MESSAGE_){
				$('#notificationText').html(data._EVENT_MESSAGE_);
				$('#updateNotificationSalary').jqxNotification({ template: 'info' });
				$('#updateNotificationSalary').jqxNotification('open');
				grid.jqxGrid('updatebounddata');
			}else{
				$('#notificationText').html(data._ERROR_MESSAGE_);
				$('#updateNotificationSalary').jqxNotification({ template: 'info' });
				$('#updateNotificationSalary').jqxNotification('open');
				
			}	
		}
	});
}else{
	$('#cannotDeleteNotify').jqxNotification('open');
}
} */-->
var emplPosTypeArr = new Array();	
<#list emplPosType as posType>
	var row = {};
	row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
	row["description"] = "${StringUtil.wrapString(posType.description)}";
	emplPosTypeArr[${posType_index}] = row;
</#list>

var periodTypeArr = new Array();
<#list periodTypeList as periodType>
	var row = {};
	row["periodTypeId"] = "${periodType.periodTypeId}";
	row["description"] = "${StringUtil.wrapString(periodType.description?if_exists)}";
	periodTypeArr[${periodType_index}] = row;
</#list>
var periodTypeSource = {
        localdata: periodTypeArr,
        datatype: "array"
};
var filterBoxAdapter = new $.jqx.dataAdapter(periodTypeSource, {autoBind: true});
var dataSoureList = filterBoxAdapter.records;
 
	<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
			var partyId = datarecord.partyId;
			var urlStr = 'getPartyPayrollHistory';
			var id = datarecord.uid.toString();
			var grid = $($(parentElement).children()[0]);
	        $(grid).attr(\"id\",\"jqxgridDetail\" + \"_\" + id);
	        
	        //nestedGrids[index] = grid;
	        //var emplSalaryArr = [];
	        /* for(var k = 0; k < emplSalary.length; k++){
	        	emplSalaryArr.push(emplSalary[k]);
	        } */
	        var emplSalarySource = {datafields: [
		            {name: 'fromDateDetail', type: 'date'},
		            {name: 'thruDateDetail', type: 'date'},
		            {name: 'rateAmountDetail', type: 'number'},
		            {name: 'emplPositionTypeIdDetail', type: 'string'},
		            {name: 'periodTypeIdDetail', type: 'string'},
		            {name: 'rateCurrencyUomIdDetail', type: 'string'},
		            {name: 'isBasedOnPosType', type: 'string'},
		            /* {name: 'deleteRecord', type: 'string'}, */
		            {name: 'rateTypeIdDetail', type: 'string'},
		            {name: 'workEffortIdDetail', type: 'string'}
				],
				cache: false,
				//localdata: emplSalaryArr,
				datatype: 'json',
				type: 'POST',
				data: {partyId: partyId},
		        url: urlStr,
		        root: 'rowDetail',
		        deleterow: function(rowId, commit){
		        	
		        }
	        };
	        var nestedGridAdapter = new $.jqx.dataAdapter(emplSalarySource);
	        if (grid != null) {
	        	grid.on('cellselect', function (event){
	        		var rowBoundIndex = event.args.rowindex;
	        		var rowDetailData = grid.jqxGrid('getrowdata', rowBoundIndex);
	        		
	        		//delete row is temporary remove
	        		
	        	});
	        	grid.jqxGrid({
	        		source: nestedGridAdapter, width: '96%', height: 170,
			 		editable: false,
			 		editmode:'selectedrow',
			 		showheader: true,
			 		showtoolbar: true,
			 		rendertoolbar: function (toolbar) {
						var container = $(\"<div id='toolbarcontainer' class='widget-header'><h4>\" + \"</h4></div>\");
						toolbar.append(container);
	        		},
			 		selectionmode:'singlecell',
			 		theme: 'olbius',
			 		pageSizeOptions: ['15', '30', '50', '100'],
	    	        pagerMode: 'advanced',
	    	        pageable: true,
			 		 columns: [
			 		 	{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDateDetail', cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
			 		 	{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDateDetail', cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
			 		 	{text: '${uiLabelMap.DAAmount} ',datafield: 'rateAmountDetail', filterable: false,editable: false, cellsalign: 'right', width: 130, 
			 		 		 cellsrenderer: function (row, column, value) {
				 		 		 var data = grid.jqxGrid('getrowdata', row);
				 		 		 if (data && data.rateAmountDetail){
				 		 		 	return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(data.rateAmountDetail, data.rateCurrencyUomIdDetail) + \"</div>\";
				 		 		 }
			 		 		 }
			 		 	},
			 		 	{text: '${uiLabelMap.EmplPositionTypeId}',  datafield: 'emplPositionTypeIdDetail', filterable: false,editable: false, cellsalign: 'left',
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < emplPosTypeArr.length; i++){
									if(emplPosTypeArr[i].emplPositionTypeId == value){
										return '<div style=\"margin-top: 2px; margin-left: 3px\">' + emplPosTypeArr[i].description + '</div>';		
									}
								}
							}
						},
			 		 	{text: '${uiLabelMap.PeriodTypePayroll}', datafield: 'periodTypeIdDetail', filterable: false,editable: false, cellsalign: 'left', width: 130,
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < periodTypeArr.length; i++){
									if(periodTypeArr[i].periodTypeId == value){
										return '<div style=\"margin-top: 4px; margin-left: 2px\">' + periodTypeArr[i].description + '</div>';		
									}
								}
							}	
						},
						{ text: '${uiLabelMap.IsBasedOnPosType}', columntype: 'template', datafield: 'isBasedOnPosType',  width: '150px', filterable: false,editable: false, cellsalign: 'center',
							cellsrenderer: function(row, column, value){
								if(value == 'Y'){
									return \"<label style='text-align: center; margin-top: 6px'><input type='checkbox' disabled='disabled' checked='checked'><span class='lbl'></span></label>\";
								}else{
									return \"<label style='text-align: center; margin-top: 6px'><input type='checkbox' disabled='disabled'><span class='lbl'></span></label>\";
								}
								
							}
						},
						/* {text: '', datafield:'deleteRecord', filterable: false,editable: false, cellsalign: 'center', width: '50px',
							 cellsrenderer: function(row, column, value){
								 //var gridId = 'jqxgridDetail_' + id;
								 return '<div style=\"text-align: center; margin-bottom: 2px\"><button class=\"btn btn-mini btn-danger icon-trash\" ></button></div>';
			                  }	
						}, */
						{text:'', datafield: 'rateCurrencyUomIdDetail', hidden: true},
						{text:'', datafield: 'rateTypeId', hidden: true},
						{text:'', datafield: 'workEffortId', hidden: true}
 		            ]
	        	});
	        }
		  }">
	<#assign columnlist = "{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', filterable: true,  editable: false, cellsalign: 'left', width: 95},
							{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', filterable: true, editable: false, cellsalign: 'left', width: 125},
							{text: '${uiLabelMap.CommonDepartment}', datafield: 'currDept', filterable: false,editable: false, cellsalign: 'left', width: 160},
							{text: '${uiLabelMap.EmplPositionTypeId}',  datafield: 'emplPositionTypeId', filterable: false,editable: false, cellsalign: 'left', width: 200,
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < emplPosTypeArr.length; i++){
										if(emplPosTypeArr[i].emplPositionTypeId == value){
											return '<div style=\"\">' + emplPosTypeArr[i].description + '</div>';		
										}
									}
								}
							},
							{text: '${uiLabelMap.PayrollFromDate}', datafield: 'fromDate', filterable: false,editable: false, cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
							{text: '${uiLabelMap.DAAmount}', datafield: 'rateAmount', filterable: false,editable: false, cellsalign: 'right', width: 130,
								cellsrenderer: function (row, column, value) {
					 		 		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		 		 if (data && data.rateAmount){
					 		 		 	return \"<div style='margin-top: 9.5px; text-align: right;'>\" + formatcurrency(data.rateAmount, data.rateCurrencyUomId) + \"</div>\";
					 		 		 }
				 		 		 }	
							},
							{text: '${uiLabelMap.PeriodTypePayroll}', datafield: 'periodTypeId', filterable: false,editable: false, cellsalign: 'left',
								cellsrenderer: function (row, column, value){
									for(var i = 0; i < periodTypeArr.length; i++){
										if(periodTypeArr[i].periodTypeId == value){
											return '<div style=\"\">' + periodTypeArr[i].description + '</div>';		
										}
									}
								}	
							},
							{ text: '',columntype: 'template', editable: false,  width: 50, filterable: false, datafield: 'editAction',
			                	   cellsrenderer: function(){
			                		return '<div style=\"text-align: center; margin-bottom: 2px\"><button class=\"btn btn-mini btn-primary icon-edit\" ></button></div>';
			                	   }			                   
			                },
			                {text: '${uiLabelMap.allEmplPositionType}',  datafield: 'allEmplPositionTypeId',filterable: false,editable: false, hidden: true,
			                },
							{text:'', datafield: 'rateCurrencyUomId', hidden: true}">;
							
 
</script>	
	
<div id="editSalaryWindow" class="hide">
	 <div id="windowHeader">
     	${uiLabelMap.EditEmpPosTypeSalary}
     </div>
     <div class="form-window-container">
     	<div class='form-window-content'>
   			<form method="post" id="editSalaryWindowForm">
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label class="control-label">${uiLabelMap.EmployeeName}</label>
   					</div>
   					<div class="span7">
   						<span id="emplNameSalary"></span>
   						<input type="hidden" name="partyId" id="partyId">
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label>${uiLabelMap.CommonDepartment}</label>
   					</div>
   					<div class="span7">
   						<span id="currDeptSalary"></span>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label>${uiLabelMap.EmplPositionTypeId}</label>
   					</div>
   					<div class="span7">
   						<div id="emplPosTypeSalary"></div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label>${uiLabelMap.PayrollFromDate}</label>
   					</div>
   					<div class="span7">
   						<div id='payrollFromDate'>
      					</div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label>${uiLabelMap.CommonThruDate}</label>
   					</div>
   					<div class="span7">
   						<div id='payrollThruDate'>
      					</div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label>${uiLabelMap.CurrencyUomId}</label>
   					</div>
   					<div class="span7">
   						<div id="CurrencyUomIdSalary">
   						</div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label>${uiLabelMap.PeriodTypePayroll}</label>
   					</div>
   					<div class="span7">
   						<div id="periodTypeSalary"></div>
   					</div>
   				</div>
   				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
	   					<label>${uiLabelMap.DAAmount}</label>
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
			<button id="submitForm" type="button" class="btn btn-primary form-action-button pull-right icon-ok">${uiLabelMap.CommonSubmit}</button>
     	</div>
     </div>
</div>				
<div class="row-fluid">
	<div id="appendNotification">
		<div id="updateNotificationSalary">
			<span id="notificationText"></span>
		</div>
	</div>
	<div id="cannotDeleteNotify">
		<span style="font-weight: bold;">${uiLabelMap.CannotDeleteSalarySetBaseOnPosType}</span>
	</div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.EmplSalaryBaseFlatList}</h4>
		<div class="widget-toolbar none-content">
				
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
							<div id="dropDownButton" style="margin-top: 5px;" class="pull-right">
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
				 filterable="true" alternativeAddPopup="popupWindowAddPartyAttend" deleterow="false" editable="false" addrow="false"
				 url="" id="jqxgrid" initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail 
				 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlecell" jqGridMinimumLibEnable="false" />	
		</div>
	</div>	
</div> 
<script type="text/javascript">
var theme = 'olbius';
var uomArray = new Array();
<#list uomList as uom>
	var row = {};
	row["uomId"] = "${uom.uomId}";
	row["description"] = "${uom.description?if_exists}";
	uomArray[${uom_index}] = row;
</#list>
var filterUomAdapter = new $.jqx.dataAdapter(uomArray, {autoBind: true});
var dataSoureUomList = filterUomAdapter.records;
var rowSelectedIndex;

$(document).ready(function () {
	initJqxDateTime();
	initJqxTreeButton();
	initBtnEvent();
	initJqxDropDownlist();
	bindingGridEvent();
	 
	 $("#amountSalary").jqxNumberInput({ width: '250px', height: '25px', spinButtons: false, decimalDigits: 0, digits: 9, max: 999999999, theme: 'olbius'});
	 $('#editSalaryWindow').jqxWindow({
	        showCollapseButton: false, maxHeight: 440, maxWidth: 500, minWidth: 500, height: 440, width: 500, theme:'olbius',
	        autoOpen: false, isModal: true, 
	        initContent: function () {
	            
	        }
	  });
	 $('#editSalaryWindow').on("open", function(event){
	 });
	 
	 initJqxNotification();
	 initJqxValidator();
});

function bindingGridEvent(){
	$("#jqxgrid").on('cellSelect', function (event){
		var dataField = event.args.datafield;
		var rowBoundIndex = event.args.rowindex;
		if(dataField == 'editAction'){
			var data = $('#jqxgrid').jqxGrid('getrowdata', rowBoundIndex);
			//var emplPosDes = '';
			rowSelectedIndex = rowBoundIndex;
			var emplPosTypeArrSalary = new Array();
			var currEmplPos = data.allEmplPositionTypeId;
			for(var i = 0; i < currEmplPos.length; i++){
				for(var j = 0; j < emplPosTypeArr.length; j++){
					if(emplPosTypeArr[j].emplPositionTypeId == currEmplPos[i]){
						emplPosTypeArrSalary.push(emplPosTypeArr[j]); 
						break;
					}
				}	
			}
			//var emplPosTypeSalaryAdapter = new  
			if(emplPosTypeArrSalary.length > 8){
				jQuery("#emplPosTypeSalary").jqxDropDownList({autoDropDownHeight: false});
			}
			jQuery("#emplPosTypeSalary").jqxDropDownList({source: emplPosTypeArrSalary});
			
			jQuery("#emplNameSalary").text(data.partyName);
			//jQuery("#emplPosTypeSalary").text(emplPosDes);
			jQuery("#currDeptSalary").text(data.currDept);
			jQuery("#partyId").val(data.partyId);
			jQuery("#emplPositionTypeId").val(data.emplPositionTypeId);
			if(data.rateAmount){
				jQuery("#amountSalary").val(data.rateAmount);	
			}else{
				jQuery("#amountSalary").val(0);
			}
			
			if(data.thruDate){
				$("#payrollThruDate").val(data.thruDate);
			}else{
				$("#payrollThruDate").val(null);
			}
			openJqxWindow($('#editSalaryWindow'));
		}
	}); 
	
}

function initJqxValidator(){
	 $("#editSalaryWindowForm").jqxValidator({
		 rules:[
				{input: '#amountSalary', message: '${uiLabelMap.AmountValueGreaterThanZero}', action: 'blur',
					rule: function (input, commit){
						var value = input.val();
						if(value < 0){
							return false
						}
						return true;
					}	
				}
	        ] 
	 });
}

function initJqxNotification(){
	 $("#updateNotificationSalary").jqxNotification({
        width: "100%", position: "top-left", opacity: 1, appendContainer: "#appendNotification",
        autoOpen: false, autoClose: false
    });
	 $("#cannotDeleteNotify").jqxNotification({
        width: "300px", position: "top-right", opacity: 1, template: 'info', appendContainer: "#appendNotification",
        autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 10000
    });
}

function initJqxDropDownlist(){
	 jQuery("#CurrencyUomIdSalary").jqxDropDownList({ width: '250px', source: dataSoureUomList, displayMember: 'uomId', valueMember : 'uomId', 
		 	height: '25px', theme: 'olbius', searchMode: 'contains', dropDownHeight: 190,
			renderer: function (index, label, value) {
				for(i=0; i < uomArray.length; i++){
					if(uomArray[i].uomId == value){
						return uomArray[i].description;
					}
				}
			    return value;
			}
		});
	 
	 <#if rateCurrencyUomId?exists>
	 	jQuery("#CurrencyUomIdSalary").jqxDropDownList('selectItem', "${rateCurrencyUomId}");
	 </#if>
	 
	 $("#periodTypeSalary").jqxDropDownList({ width: '250px',source: dataSoureList, displayMember: 'description', 
		 	valueMember : 'periodTypeId', height: '25px', theme: 'olbius', searchMode: 'contains', dropDownHeight: 100,
			renderer: function (index, label, value) {
				for(i=0; i < periodTypeArr.length; i++){
					if(periodTypeArr[i].periodTypeId == value){
						return periodTypeArr[i].description;
					}
				}
			    return value;
			}
	 });
	 <#if defaultPeriodTypeId?exists>
		 $("#periodTypeSalary").jqxDropDownList('selectItem', "${defaultPeriodTypeId}");
	 </#if>
	 
	 jQuery("#emplPosTypeSalary").jqxDropDownList({ width: '250', height: '25', autoDropDownHeight: true, selectedIndex: 0, 
		 theme:'olbius', displayMember: "description", valueMember: "emplPositionTypeId"})
}

function initBtnEvent(){
	 $("#submitForm").click(function(){
		 $("#submitForm").attr("disabled", "disabled");
		 bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AddRowDataConfirm)}?",
			 [
			 {
    		    "label" : "${uiLabelMap.CommonSubmit}",
    		    "class" : "btn-primary btn-mini icon-ok",
    		    "callback": function() {
    		    	submitEmplSalaryBaseFlat();	    		
    		    	$("#submitForm").removeAttr("disabled");
    		    }
    		 },
    		 {
    		    "label" : "${uiLabelMap.CommonCancel}",
    		    "class" : "btn-danger icon-remove btn-mini",
    		    "callback": function() {
    		    	$("#submitForm").removeAttr("disabled");
    		    }
    		 }
    		 ]
		 );		 		
	 });
	 $("#cancelSubmit").click(function(){
		 $('#editSalaryWindow').jqxWindow('close');
	 });
}

function submitEmplSalaryBaseFlat(){
	 $('#editSalaryWindow').jqxWindow('close');
 	 var dataSubmit = new Array();
	 var partyId = $("#partyId").val();
	 var emplPositionTypeId = $("#emplPositionTypeId").val();
	 var fromDate = $("#payrollFromDate").jqxDateTimeInput('getDate').getTime();
	 var uomId = jQuery("#CurrencyUomIdSalary").jqxDropDownList('getSelectedItem').value;
	 var periodTypeId = $("#periodTypeSalary").jqxDropDownList('getSelectedItem').value;
	 var amount = $("#amountSalary").val();
	 var emplPositionTypeId = $("#emplPosTypeSalary").jqxDropDownList('getSelectedItem').value;
	 dataSubmit.push({name: "partyId", value: partyId});
	 dataSubmit.push({name: "fromDate", value: fromDate});
	 dataSubmit.push({name: "uomId", value: uomId});
	 dataSubmit.push({name: "periodTypeId", value: periodTypeId});
	 dataSubmit.push({name: "amount", value: amount});
	 dataSubmit.push({name: "emplPositionTypeId", value: emplPositionTypeId});
	 if($("#payrollThruDate").val()){
		 dataSubmit.push({name: "thruDate", value: $("#payrollThruDate").jqxDateTimeInput('getDate').getTime()});
	 }
	 $.ajax({
		url: "<@ofbizUrl>createPartyRateAmount</@ofbizUrl>",
		type: 'POST',
		data: dataSubmit,
		success: function(data){
			$("#updateNotificationSalary").jqxNotification('closeLast');
			if(data._EVENT_MESSAGE_){
				$("#notificationText").text(data._EVENT_MESSAGE_);
				$("#updateNotificationSalary").jqxNotification({ template: 'info' });
				$("#updateNotificationSalary").jqxNotification('open');
				var rowId = $('#jqxgrid').jqxGrid('getrowid', rowSelectedIndex);
				var dataEditSuccess = $('#jqxgrid').jqxGrid('getrowdata', rowSelectedIndex);
				if($("#jqxgridDetail_" + rowId).length){
					$("#jqxgridDetail_" + rowId).jqxGrid('updatebounddata');
				}else{
					$('#jqxgrid').jqxGrid('showrowdetails', rowSelectedIndex);
				}
				var rowEdit = {
						partyId: partyId,
						partyName: dataEditSuccess.partyName,
						currDept: dataEditSuccess.currDept,
						emplPositionTypeId: emplPositionTypeId,
						fromDate: $("#payrollFromDate").jqxDateTimeInput('getDate'),
						rateAmount: amount,
						rateCurrencyUomId: uomId,
						periodTypeId: periodTypeId,
						allEmplPositionTypeId: dataEditSuccess.allEmplPositionTypeId
				}
				$('#jqxgrid').jqxGrid("updaterow", rowId, rowEdit);
			}else{
				$("#notificationText").text(data._ERROR_MESSAGE_);
				$("#updateNotificationSalary").jqxNotification({ template: 'error' });
				$("#updateNotificationSalary").jqxNotification('open');
			}	
		}
	 });
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
}

function setDropdownContent(element){
	 var item = $("#jqxTree").jqxTree('getItem', element);
	 var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
    $("#dropDownButton").jqxDropDownButton('setContent', dropDownContent);
}

function initJqxTreeButton(){
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
	// perform Data Binding.
	dataAdapter.dataBind();
	var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
	$("#dropDownButton").jqxDropDownButton({ width: 280, height: 25, theme: theme});
	$('#jqxTree').jqxTree({ source: records,width: "280px", height: "200px", theme: theme});
	  $('#jqxTree').on('select', function(event){
		var id = event.args.element.id;
	    var item = $('#jqxTree').jqxTree('getItem', event.args.element);
		    /* var value = jQuery("#jqxTree").jqxTree('getItem', $("#"+id)[0]).value; */
	   	setDropdownContent(event.args.element);
	    var tmpS = $("#jqxgrid").jqxGrid('source');
	    var partyId = item.value;
	    var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
	   	var fromDate = selection.from.getTime();
	   	var thruDate = selection.to.getTime();
		refreshGridData(partyId, fromDate, thruDate);
       /* tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQListEmplSalaryBaseFlat&partyGroupId=" + partyId;
       $("#jqxgrid").jqxGrid('source', tmpS); */
    });
	<#if expandedList?has_content>
		<#list expandedList as expandId>
			$('#jqxTree').jqxTree('expandItem', $("#${expandId}_partyGroupId")[0]);
		</#list>
		$('#jqxTree').jqxTree('selectItem', $("#${expandedList.get(0)}_partyGroupId")[0]);
	</#if>
	
}
/* function deleteEmplSalaryRecord(gridId){
	 jQuery("#" + gridId).
} */

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
	$("#payrollFromDate").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#payrollThruDate").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#payrollThruDate").val(null);
}

function refreshGridData(partyGroupId, fromDate, thruDate){
	var tmpS = $("#jqxgrid").jqxGrid('source');
	tmpS._source.url = "jqxGeneralServicer?sname=JQListEmplSalaryBaseFlat&hasrequest=Y&partyGroupId=" + partyGroupId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
	$("#jqxgrid").jqxGrid('source', tmpS);
}
</script>
<style>
	.ui-dialog { z-index: 999999 !important ;}
</style>	
<div id="warningAddRecord" title="${StringUtil.wrapString(uiLabelMap.AddRowRecord)}?">
	
</div>