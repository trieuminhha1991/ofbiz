<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'partyGroupId', type: 'string'},
					   {name: 'partyGroupName', type: 'string'},
					   {name: 'fromDate', type: 'date', other: 'Timestamp'},
					   {name: 'thruDate', type: 'date', other: 'Timestamp'},
					   {name: 'salaryActualPaid', type: 'number'}]"/>
					   
<script type="text/javascript">
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', width: 130},
							 {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 190},
							 {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'partyGroupName', width: 220},
							 {datafield: 'partyGroupId', hidden: true},
							 {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput', width: 130},
							 {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', editable: false, columntype: 'datetimeinput', width: 130},
							 {text: '${StringUtil.wrapString(uiLabelMap.RealSalaryPaid)}', datafield: 'salaryActualPaid', cellsalign: 'right', filterable: false,
								 cellsrenderer: function (row, column, value) {
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
				 		 		 }																																																												 
							 }"/>
							 
	<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord){
		var partyId = datarecord.partyId;
		var payrollTableId = '${parameters.payrollTableId}';
		var fromDate = datarecord.fromDate.getTime();
		var urlStr = 'getPartyPayrollFormulaDetail';
		//var emplSalaryDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
		//emplSalary = emplSalaryDataAdapter.records;
		
		//var nestedGrids = new Array();
		var id = datarecord.uid.toString();
		var grid = $($(parentElement).children()[0]);
	    $(grid).attr('id','jqxgridDetail_'  + id);	
   		var sourceRowDetails = {datafields: [
	            {name: 'partyId', type: 'string'},
	            {name: 'code', type: 'string'},
	            {name: 'codeName', type: 'string'},
	            {name: 'payrollCharacteristicId', type: 'string'},
	            {name: 'amount', type: 'number'},
	            {name: 'statusId', type: 'string'},
	            {name: 'invoiceItemTypeId', type: 'string'}
			],
			cache: false,
			//localdata: emplSalaryArr,
			datatype: 'json',
			type: 'POST',
			data: {partyId: partyId, payrollTableId: payrollTableId, fromDate: fromDate},
	        url: urlStr,
	        pagenum: 0,
	        pagesize: 15,
	        root: 'listData'
        };
	    var nestedGridAdapter = new $.jqx.dataAdapter(sourceRowDetails);
	    if (grid != null) {
	    	grid.jqxGrid({
	    		source: nestedGridAdapter, width: '96%', height: 440,
                showtoolbar:false,
		 		editable: false,
		 		theme: 'olbius', 
		 		showtoolbar: false,
		 		rendertoolbar: function (toolbar) {
					var container = $(\"<div id='toolbarcontainer' class='widget-header'><h4>\" + \"</h4></div>\");
					toolbar.append(container);
        		},
		 		columns: [
					{datafield: 'partyId', hidden: true},		 		          
					{text: '${StringUtil.wrapString(uiLabelMap.HRPayrollCode)}', datafield: 'code', width: 160},		 		          
					{text: '${StringUtil.wrapString(uiLabelMap.CommonName)}', datafield: 'codeName', width: 230},		 		          
					{text: '${StringUtil.wrapString(uiLabelMap.CommonCharacteristic)}', datafield: 'payrollCharacteristicId', width: 130},
					{text:'${StringUtil.wrapString(uiLabelMap.InvoiceItemTypeId)}', datafield: 'invoiceItemTypeId', width: 130, hidden: true},
					{text: '${StringUtil.wrapString(uiLabelMap.DAAmount) }', datafield: 'amount', width: 130, cellsalign: 'right',
						cellsrenderer: function (row, column, value) {
							return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
		 		 		 }	
					},
					{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId'}
				],
				pageSizeOptions: ['15', '30', '50', '100'],
    	        pagerMode: 'advanced',
    	        columnsResize: true,
    	        pageable: true,
	    	});
	    }
	}"/>								 
</script>		
	
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.PageTitleApprovePayroll} ${uiLabelMap.CommonOf} ${payrollTableRecord.payrollTableName} [${parameters.payrollTableId}]</h4>
		<div class="widget-toolbar none-content" style="width: 500px">
			<div id="dropdownlist" style="margin-top: 5px; float: right;"></div>
			<button class="grid-action-button" style="float: right;" id="createInvoice">
				<i class="icon-hand-right open-sans">${uiLabelMap.InvAndPaym}</i>
			</button>
			<form action="CreatePayrollInvoiceAndPayment" method="post" id="invoiceForm">
				<input type="hidden" name="currencyUomId" value="${currencyUomId}">
				<input type="hidden" name="payrollTableId" value="${parameters.payrollTableId}">
				<input type="hidden" name="fromDate" id="fromDate">
			</form>				
		</div>
	</div>
	
	<div class="widget-body">
		<#if payrollTableRecord.statusId != "PYRLL_TABLE_CREATED">
			<#assign payrollTimestampResult = dispatcher.runSync("getPayrollTableRecordTimestamp", Static["org.ofbiz.base.util.UtilMisc"].toMap("payrollTableId", parameters.payrollTableId, "userLogin", userLogin))>
			<#assign listTimestamp = payrollTimestampResult.get("listTimestamp")>
			<#if listTimestamp?has_content>
				<div id="payrollTableData">
					<#assign fromDate = listTimestamp.get(0).get("fromDate")/>
					<@jqGrid url="jqxGeneralServicer?sname=getListEmplSalaryPaidActual&hasrequest=Y&payrollTableId=${parameters.payrollTableId}&fromDate=${fromDate.getTime()}" 
						dataField=datafield columnlist=columnlist
						id="jqxgrid" initrowdetails="true" initrowdetailsDetail=initrowdetailsDetail
						clearfilteringbutton="true" rowdetailsheight=465
						editable="false" 
						editrefresh ="true"
						editmode="click"
						jqGridMinimumLibEnable="false"
						showtoolbar="false" deleterow="false"
					/>
				</div>
				<script type="text/javascript">
					var theme = 'olbius';
					var dataTimestamp = new Array();
					<#list listTimestamp as timestamp>
						dataTimestamp.push({"date": '${uiLabelMap.CommonFromDate} ${timestamp.get("fromDate")?string["dd/MM/yyyy"]} ${uiLabelMap.CommonThruDate} ${timestamp.get("thruDate")?string["dd/MM/yyyy"]}', 'valueParam': '${timestamp.get("fromDate").getTime()}'});
					</#list>
					var source = {
			           localdata: dataTimestamp,
			           datatype: "array"
			        };
					var dataAdapter = new $.jqx.dataAdapter(source);
					
					$(document).ready(function () {
						$('#dropdownlist').jqxDropDownList({ selectedIndex: 0,  source: dataAdapter, displayMember: "date", 
							valueMember: "valueParam", height: 25, width: 275, autoDropDownHeight:true, theme: theme});
						$('#dropdownlist').on('select', function (event){
						    var args = event.args;
						    if (args) {
							    // index represents the item's index.                
							    var index = args.index;
							    var item = args.item;
							    // get item's label and value.
							    var label = item.label;
							    var value = item.value;
							    var tempSource = $("#jqxgrid").jqxGrid('source');
							    tempSource._source.url = "jqxGeneralServicer?sname=getListEmplSalaryPaidActual&hasrequest=Y&payrollTableId=${parameters.payrollTableId}&fromDate=" + value;
							    $("#jqxgrid").jqxGrid('source', tempSource);
							}                        
						});
						$("#createInvoice").click(function(){
							var date = $('#dropdownlist').jqxDropDownList('val');
							$("#fromDate").val(date);
							$("#invoiceForm").submit();
						});
					});
				</script>
			</#if>
		<#else>
			<div>
		       <p class="alert alert-info">${uiLabelMap.PayrollTableNotCalculated}</p>
		   </div>			
		</#if>	
	</div>	
</div>
		   																		