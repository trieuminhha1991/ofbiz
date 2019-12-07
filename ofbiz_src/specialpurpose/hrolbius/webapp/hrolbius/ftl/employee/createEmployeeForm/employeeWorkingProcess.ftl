<div id="jqxgridWP"></div>
<div id="createNewWorkingProcessWindow" style="display: none;">
	<div id="windowHeaderNewWorkingProcess">
		<span>
		   ${uiLabelMap.NewWorkingProcess}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewWorkingProcess" id="createNewWorkingProcess">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CompanyName}:</label>
						<div class="controls">
							<input id="wpCompanyName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.EmplPositionTypeId}:</label>
						<div class="controls">
							<input id="wpEmplPositionTypeId">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.JobDescription}:</label>  
						<div class="controls">
							<input id="wpJobDescription"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRSalary}:</label>   
						<div class="controls">
							<input id="wpPayroll"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.TerminationReason}:</label>   
						<div class="controls">
							<input id="wpTerminationReasonId">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRRewardAndDisciplining}:</label>   
						<div class="controls">
							<input id="wpRewardDiscrip">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonFromDate}:</label>   
						<div class="controls">
							<div id="wpFromDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.CommonThruDate}:</label>   
						<div class="controls">
							<div id="wpThruDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-primary" id="alterSaveWP"><i class="icon-ok"></i>${uiLabelMap.CommonSubmit}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelWP">
								<i class="icon-remove">${uiLabelMap.CommonCancel}</i></button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function () {
	var wpData = new Array();
	/******************************************************Edit Working Process**************************************************************************/
	var theme = "olbius";
	//Handle alterSaveFamily
	$("#createNewWorkingProcess").jqxValidator({
		rules: [
			{input: '#wpFromDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if(input.jqxDateTimeInput('getDate') > $("#wpThruDate").jqxDateTimeInput('getDate')){
                    	return false;
                    }else{
                    	return true;
                    }
                    	
            	}
			},
			{input: '#wpThruDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
				rule: function (input, commit) {
                    if(input.jqxDateTimeInput('getDate') < $("#wpFromDate").jqxDateTimeInput('getDate')){
                    	return false;
                    }else{
                    	return true;
                    }
                    	
            	}
			},
		]
	});
	$("#alterSaveWP").click(function () {
		$("#createNewWorkingProcess").jqxValidator('validate');
	});

	//Handle alterSaveWP
	$("#createNewWorkingProcess").on('validationSuccess', function (event) {
		var row;
	        row = {
	        		companyName:$('#wpCompanyName').val(),
	        		emplPositionTypeId:$("#wpEmplPositionTypeId").val(),
	        		jobDescription:$("#wpJobDescription").val(),
	        		payroll:$("#wpPayroll").val(),
	        		terminationReasonId:$("#wpTerminationReasonId").val(),
	        		rewardDiscrip:$("#wpRewardDiscrip").val(),
	        		fromDate:$("#wpFromDate").jqxDateTimeInput('getDate'),
	        		thruDate:$("#wpThruDate").jqxDateTimeInput('getDate')
			  };
	        
	        $("#jqxgridWP").jqxGrid('addrow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgridWP").jqxGrid('clearSelection');
	        $("#jqxgridWP").jqxGrid('selectRow', 0);
	        $("#createNewWorkingProcessWindow").jqxWindow('close');
	    });
	//Create NewWorkingProcessWindow
	$("#createNewWorkingProcessWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 450, autoOpen: false, maxWidth: "80%", height: 450, minWidth: '40%', width: "50%", isModal: true,
        theme:'olbius', collapsed:false
    });
	
	//Create wpCompanyName
	$("#wpCompanyName").jqxInput({width: 195, theme: theme});
	
	//Create wpEmplPositionTypeId
	$("#wpEmplPositionTypeId").jqxInput({width: 195, theme: theme});
	
	//Create wpJobDescription
	$("#wpJobDescription").jqxInput({width: 195, theme: theme});
	
	//Create wpPayroll
	$("#wpPayroll").jqxInput({width: 195, theme: theme});
	
	//Create wpTerminationReasonId
	$("#wpTerminationReasonId").jqxInput({width: 195, theme: theme});
	
	//Create wpRewardDiscrip
	$("#wpRewardDiscrip").jqxInput({width: 195, theme: theme});
	
	//Create wpFromDate
	$("#wpFromDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', theme: theme});
	
	//Create wpThruDate
	$("#wpThruDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', theme: theme});
	
	/******************************************************End Working Process**********************************************************************/
	var source =
    {
        localdata: wpData,
        datatype: "array",
        datafields:
        [
        	{ name: 'companyName', type: 'string' },
			{ name: 'fromDate', type: 'date' },
			{ name: 'thruDate', type: 'date' },
			{ name: 'emplPositionTypeId', type: 'string' },
			{ name: 'jobDescription', type: 'string' },
			{ name: 'payroll', type: 'string' },
			{ name: 'terminationReasonId', type: 'string'},
			{ name: 'rewardDiscrip', type: 'string'}
        ]
    };
	var dataAdapter = new $.jqx.dataAdapter(source);
    
	$("#jqxgridWP").jqxGrid(
    {
        width: "99%",
        source: dataAdapter,
        columnsresize: true,
        pageable: true,
        theme: 'olbius',
        autoheight: true,
        showtoolbar: true,
        rendertoolbar: function (toolbar) {
            var container = $("<div id='toolbarcontainer' class='widget-header'>");
            toolbar.append(container);
            container.append('<h4></h4>');
            container.append('<button id="wpAddrowbutton" class="grid-action-button"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
            container.append('<button id="wpDelrowbutton" class="grid-action-button"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
            
            // create new row.
            $("#wpAddrowbutton").on('click', function () {
            	$("#createNewWorkingProcessWindow").jqxWindow('open');
            });
            
            // create new row.
            $("#wpDelrowbutton").on('click', function () {
            	var selectedrowindex = $('#jqxgridWP').jqxGrid('selectedrowindex'); 
            	wpData.splice(selectedrowindex, 1);
            	$('#jqxgridWP').jqxGrid('updatebounddata'); 
            	
            });
        },
        columns: [
      	  { text: '${uiLabelMap.CompanyName}', datafield: 'companyName', width: 150},
          { text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId'},
          { text: '${uiLabelMap.JobDescription}', datafield: 'jobDescription', width: 150},
          { text: '${uiLabelMap.HRSalary}', datafield: 'payroll', width: 150},
          { text: '${uiLabelMap.TerminationReason}', datafield: 'terminationReasonId', width: 150},
          { text: '${uiLabelMap.HRRewardAndDisciplining}', datafield: 'rewardDiscrip', width: 150},
          { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy'},
          { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy'}
        ]
    });
});
function getEmployeeWorkingProcess(){
	var rows = $("#jqxgridWP").jqxGrid('getrows');
	return rows;
}
</script>