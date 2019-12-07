<#assign dataField="[{ name: 'reportId', type: 'string' },
					 { name: 'reportName', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp' },
					 { name: 'thruDate', type: 'date', other:'Timestamp' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.reportId}', datafield: 'reportId', hidden: false, editable: false,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			                return '<div style=\"text-align: center\"><a href=\"viewInsuranceReport?reportId='+ value +'\">' + value+ '</a></div>'
			            }   
					 },
 					 { text: '${uiLabelMap.InsuranceReportName}', datafield: 'reportName'},
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput', editable: false},
					 { text: '${uiLabelMap.thruDate}', datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', filtertype:'range',columntype: 'datetimeinput' , editable: false}
   "/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetInsuranceReport" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	showtoolbar = "true" deleterow="true"
	removeUrl="jqxGeneralServicer?sname=deleteInsuranceReport&jqaction=D" deleteColumn="reportId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createInsuranceReport" alternativeAddPopup="popupAddPartySkill" addrow="true" addType="popup" 
	addColumns="reportName;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateInsuranceReport"  editColumns="reportId;reportName;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	
/>
<!-- <button id="setDefault" style="float:left;  padding-bottom: 5px;"  onclick="setDefault();">${uiLabelMap.SetDefault}</button> -->

<!-- <button id="cancelRow" style="float:right; padding-bottom: 5px;"  onclick="cancelRow();">${uiLabelMap.Cancel}</button>
<button id="saveRow" style="float:right; padding-bottom: 5px;"  onclick="saveAccTarget();">${uiLabelMap.Save}</button>-->

<div id="popupAddPartySkill" >
    <div>${uiLabelMap.ConstructFormular}</div>
    <div style="overflow: hidden;">
    	<form name="popupAddRow" action="" class="form-horizontal">
			<div class="control-group">
				<label class="control-label" >${uiLabelMap.InsuranceReportName}</label>
				<div class="controls">
					<input type="text" name="reportNameAdd"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" >${uiLabelMap.fromDate}</label>
				<div class="controls">
					<div id="fromDateJQ"></div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" >${uiLabelMap.thruDate}</label>
				<div class="controls">
					<div id="thruDateJQ"></div>
				</div>
			</div>
			<div class="row-fluid wizard-actions pull-right">
				<button type="button" class='btn btn-primary' style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
    	</form>
    </div>
</div>  
<script type="text/javascript">
	var skills = [
		<#if skillTypes?exists>
		<#list skillTypes as skill>
		{
			skillTypeId : "${skill.skillTypeId}",
			description : "${skill.description}"
		},
		</#list>
		</#if>
	];
	
	$(document).ready(function(){
	   	// $("#setDefault").jqxButton({ template: "primary" });
		//$("#addNewTarget").jqxButton({ template: "primary" });
		// $("#alterSave").jqxButton({ template: "primary" });
		// $("#saveRow").jqxButton({ template: "primary" });
		// $("#cancelRow").jqxButton({ template: "primary" });	
		var skillJqx = $("#jqxgrid");
		var popup = $("#popupAddPartySkill");
		popup.jqxWindow({
	        width: 610, height: 250, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.01           
	    });
	    $("#fromDateJQ").jqxDateTimeInput({
		     height: '30px',
		     width: '218px',
		     theme: 'olbius',
		     value: null
		});
		$("#thruDateJQ").jqxDateTimeInput({
		     height: '30px',
		     width: '218px',
		     theme: 'olbius',
		     value: null
		});
		$("#fromDateJQ").on("change", function(event){
			var tmp = event.args.date;
			var date = new Date((tmp.getYear() + 1900), tmp.getMonth(), tmp.getDate());
			$("#thruDateJQ").jqxDateTimeInput('setMinDate', date);
		});
		$("#thruDateJQ").on("change", function(event){
			var tmp = event.args.date;
			var date = new Date((tmp.getYear() + 1900), tmp.getMonth(), tmp.getDate());
			$("#fromDateJQ").jqxDateTimeInput('setMaxDate', date);
		});
		// $('#popupAddRow').jqxValidator({ rules: [
            // { input: '#reportNameAdd', message: '', action: 'keyup', rule: 'minLength=3' }]
         // });
		$("#alterSave").click(function () {
			var fromDate = Utils.formatDateYMD($('#fromDateJQ').jqxDateTimeInput('getDate'));
			var thruDate = Utils.formatDateYMD($('#thruDateJQ').jqxDateTimeInput('getDate'));
	    	var row = { 
        		reportName: $("input[name='reportNameAdd']").val(),
        		fromDate: fromDate,
        		thruDate: thruDate
        	  };
		    skillJqx.jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        skillJqx.jqxGrid('clearSelection');                        
	        skillJqx.jqxGrid('selectRow', 0);  
	        popup.jqxWindow('close');
	    });
	});
    function openPopupCreatePartySkill(){
    	$("#popupAddPartySkill").jqxWindow('open');
    }
</script>