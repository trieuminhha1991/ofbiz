<#assign dataField="[
		{name : 'payGradeId', type : 'string'},
		{name : 'payGradeName', type : 'string'},
		{name : 'comments', type : 'string'}
]"/>
<#assign columnlist="
		{text : '${uiLabelMap.HumanResPayGradeID}' , datafield : 'payGradeId',editable : false,cellsrenderer :
			function(row,columnfield,value){
				var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
				return '<a href=\"EditPayGrade?payGradeId='+ data.payGradeId +'\">'+ data.payGradeId +'</a>';
			}
		},
		{text : '${uiLabelMap.FormFieldTitle_payGradeName}' , datafield : 'payGradeName'},
		{text : '${uiLabelMap.FormFieldTitle_comments}' , datafield : 'comments'}
"/>
<@jqGrid filtersimplemode="true" initrowdetails="true" initrowdetailsDetail="initrowdetails" deleterow="true" filterable="true" addrow="true"  addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		editrefresh ="true"
		editmode="click"
		 url="jqxGeneralServicer?sname=JQgetListPayGrade"
		 removeUrl="jqxGeneralServicer?sname=deletePayGrade&jqaction=D" deleteColumn="payGradeId;payGradeName;comments"
		 createUrl="jqxGeneralServicer?sname=createPayGrade&jqaction=C" addColumns="payGradeId;payGradeName;comments"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePayGrade"  editColumns="payGradeId;payGradeName;comments"
		/>	
		
		
<div class="row-fluid" id="alterpopupWindow" style="display:none;">
 	<div>${uiLabelMap.HROlbiusNewPayGrade}</div>
 	 <div style="overflow: hidden;">
 	 		<form  id="createPayGrade" class="form-horizontal">
				<input type="hidden" name="hasTable" value="N">				
				<div class="row-fluid" >
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
						<div class="controls">
							<input type="text" name="payGradeId" id="payGradeId" class="required">
						</div>
					</div>
										
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_payGradeName}</label>
						<div class="controls">
							<input type="text" name="payGradeName" id="payGradeName" class="required">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRNotes}</label>
						<div class="controls">
							<input type="text" name="comments" id="comments">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">
							&nbsp;  
						</label>
						<div class="controls">
							<button type="button" class="btn btn-small btn-primary" name="submitButton" id="alterSave"><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
							<button type="button" class="btn btn-small btn-primary" name="submitButton" id="alterCancel"><i class="icon-ok"></i>${uiLabelMap.CommonCancel}</button>
						</div>
					</div>
				</div>					
		</form>
 	 </div>
</div> 	 

	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/marketing/utils.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#alterpopupWindow").jqxWindow({
        width: 550, height : 300,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
	$('#createPayGrade').jqxValidator({
                rules: [
                			{ input: '#payGradeId', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'keyup, blur', rule: 'required' },
	                     	{ input: '#payGradeName', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'keyup, blur', rule: 'required' },
                       ]
           			 });
	$('#alterSave').click(function(){
		$('#createPayGrade').jqxValidator('validate');
	});
	$('#createPayGrade').on('validationSuccess',function(){
		var row = {};
		row = {
			payGradeId : $('#payGradeId').val(),
			payGradeName : $('#payGradeName').val(),
			comments : $('#comments').val()
		};
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	    // select the first row and clear the selection.
	    $("#jqxgrid").jqxGrid('clearSelection');                        
	    $("#jqxgrid").jqxGrid('selectRow', 0);  
	    $("#alterpopupWindow").jqxWindow('close');
	    $('#createPayGrade').trigger('reset');
	});
	$('#alterCancel').click(function(){
		$('#createPayGrade').trigger('reset');
	});
	//init row details
	var initrowdetails = function (index, parentElement, gridElement, datarecord) { 
		payId = datarecord.payGradeId;
		var jqxGridChild = $($(parentElement).children()[0]);
		var id = datarecord.uid.toString();
		var idTmp = "jqxGridChild_" + id;
		$(jqxGridChild).attr('id',idTmp);
		if(payId) {
			var sourcePayGradeDetail =
			    {
			        datafield:
			        [
			            { name: 'salaryStepSeqId', type: 'string' },
			            { name: 'dateModified', type: 'date'},
			            { name: 'amount', type: 'number'}
			        ],
			        cache: false,
			        root: 'results',
			        datatype: "json",
			        beforeprocessing: function (data) {
			            sourcePayGradeDetail.totalrecords = data.TotalRows;
			        },
			        filter: function () {
			            // update the grid and send a request to the server.
			            jqxGridChild.jqxGrid('updatebounddata');
			        },
			        pager: function (pagenum, pagesize, oldpagenum) {
			            // callback called when a page or page size is changed.
			        },
			        sort: function () {
			            jqxGridChild.jqxGrid('updatebounddata');
			        },
			        sortcolumn: 'salaryStepSeqId',
					sortdirection: 'asc',
			        type: 'POST',
			        data: {
				        noConditionFind: 'Y',
				        conditionsFind: 'N'
				    },
				    pagesize:5,
			        contentType: 'application/x-www-form-urlencoded',
			        url: 'jqxGeneralServicer?sname=JQgetListSalarySteps&payGradeId=' + payId,
			        updaterow : function(rowid,rowdata,commit){
			        	var data = {};
			        	data.salaryStepSeqId = rowdata.salaryStepSeqId;
			        	if(rowdata.dateModified){
			        		data.dateModified = Utils.formatDateYMD(rowdata.dateModified);
			        	}
			        	data.amount = rowdata.amount;
			        	data.payGradeId = payId;
			        	$.ajax({
			        		url : 'creatOrUpdateSalaryStep',
			        		data : data,
			        		cache : false,
			        		datatype : 'json',
			        		type : 'POST',
			        		success : function(data,status,xhr){
			        			if(data.responseMessage == 'error'){
			        				commit(false);
			        			}else commit(true);
			        			jqxGridChild.jqxGrid('updatebounddata');
			        		}
			        	});
			        },
			        deleterow : function(rowid,commit){
			        	var datarow = jqxGridChild.jqxGrid('getrowdata',rowid);
			        	var data = {};
			        	data.salaryStepSeqId = datarow.salaryStepSeqId;
			        	data.payGradeId  = payId;
			        	$.ajax({
			        		url : 'deleteSalaryStep',
			        		data : data,
			        		cache : false,
			        		datatype : 'json',
			        		type : 'POST',
			        		success : function(data,status,xhr){
			        			if(data.responseMessage == 'error'){
			        				commit(false);
			        			}else commit(true);
			        			jqxGridChild.jqxGrid('updatebounddata');
			        		}
			        	});
			        }
			    };
		    }
		    var dataAdapterG = new $.jqx.dataAdapter(sourcePayGradeDetail);
			    jqxGridChild.jqxGrid({
			    	width : 900,
			    	height : 170,
			    	source : dataAdapterG,
			    	showtoolbar : true,
			    	pageable: true,
			    	sortable : true,
	            	editable: true,
	            	columnsresize: true,
	            	columnsresize: true,
			    	rendertoolbar : function(toolbar){
			    		var container = $("<div style='margin: 5px;'></div>");
			    		toolbar.append(container);
			    		container.append('<input id="addrowbutton" type="button" value="${uiLabelMap.accAddNewRow}" />');
			    		container.append('<input id="deleterowbutton" type="button" value="${uiLabelMap.accDeleteSelectedRow}" />');
	                    $("#addrowbutton").jqxButton();
	                    $("#deleterowbutton").jqxButton();
	                    // create new row.
	                    $("#addrowbutton").on('click', function () {
	                    	jqxGridChild.jqxGrid('addrow', null, {});
	                    });
	                    $("#deleterowbutton").on('click', function () {
	                     	 var selectedrowindex = jqxGridChild.jqxGrid('getselectedrowindex');
						     var rowscount = jqxGridChild.jqxGrid('getdatainformation').rowscount;
						     if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
						         var id = jqxGridChild.jqxGrid('getrowid', selectedrowindex);
						         var commit = jqxGridChild.jqxGrid('deleterow', id);
						     }
	                    });
			    	},
			    	columns: [
                      { text: '${uiLabelMap.HROlbiusSalaryStepSeqId}', datafield: 'salaryStepSeqId', width: 300},
                      { text: '${uiLabelMap.FormFieldTitle_dateModified}', datafield: 'dateModified', width: 300 ,cellsformat : 'dd/MM/yyyy',columntype : 'datetimeinput',createeditor : 
                      	function(row,value,editor){
                      		editor.jqxDateTimeInput({height: '25px', width: 300,  formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true});
                      	},cellsrenderer : 
	                      	function(row,columnfield,value){
	                      	var data = jqxGridChild.jqxGrid('getrowdata',row);
	                      	var time ='';
	                      	if(data.dateModified != null && data.dateModified !== 'undefined' ){
	                      		time = Utils.formatDateDMY(value);
	                      	}
	                      		return '<span>' + time +'</span>';
	                      	}
                      },
                      { text: '${uiLabelMap.HrolbiusAmountSalary}', datafield: 'amount', width: 300,cellsformat : 'c2' }
                  ]
			    });
	} 
		
	
	</script>