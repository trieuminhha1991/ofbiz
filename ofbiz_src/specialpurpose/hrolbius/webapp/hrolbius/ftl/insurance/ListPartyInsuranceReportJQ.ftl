<script>
var listIPTypes = [<#if listIPTypes?exists><#list listIPTypes as type>{insuranceParticipateTypeId: "${type.insuranceParticipateTypeId}",parentTypeId: "${type.parentTypeId?if_exists}",description: "${type.description?if_exists}",},</#list></#if>];
var listInsuranceTypes = [<#if listInsuranceType?exists><#list listInsuranceType as type>{insuranceTypeId: "${type.insuranceTypeId}",description: "${type.description?if_exists}",},</#list></#if>];
var listSuspend = [<#if listSuspend?exists><#list listSuspend as type>{suspendReasonId: "${type.suspendReasonId}",description: "${type.description?if_exists}",},</#list></#if>];
</script>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	rowsExpaded = index;
	var grid = $($(parentElement).children()[0]);
    $(grid).attr('id','jqxgridDetail'+index);
    var tmp;
    if(datarecord.rowDetail && datarecord.rowDetail.length){
   		var dataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
	    var recordData = dataAdapter.records;
			var nestedGrids = new Array();
	        var id = datarecord.uid.toString();
	        
	         var grid = $($(parentElement).children()[0]);
	         $(grid).attr('id','jqxgridDetail'+index);
	         nestedGrids[index] = grid;
	       
	         var recordDataById = [];
	         for (var ii = 0; ii < recordData.length; ii++) {
	             recordDataById.push(recordData[ii]);
	         }
	         var recordDataSource = { datafields: [	
						{ name: 'insuranceTypeId', type: 'string' },
						{ name: 'description', type: 'string'},
						{ name: 'insuranceNumber', type: 'date', other: 'Timestamp'},
						{ name: 'partyHealthCareId', type: 'string'},
						{ name: 'fromDate', type: 'date', other:'Timestamp'},
						{ name: 'thruDate', type: 'date', other:'Timestamp'}
	         		],
	             	localdata: recordDataById
	         }
	         var nestedGridAdapter = new $.jqx.dataAdapter(recordDataSource);
	         tmp = nestedGridAdapter;
    }else{
    	tmp = new Array();
    }
     grid.jqxGrid({
            source: tmp,
            width: '800px', 
            height: '150',
            autoheight: true,
		  theme: 'energyblue',
            columns: [
			{ text: '${uiLabelMap.insuranceTypeId}', datafield: 'insuranceTypeId', width: '150px', hidden: true},
			{ text: '${uiLabelMap.InsuranceType}', datafield: 'description', width: '150px'},
			{ text: '${uiLabelMap.SocialInsuranceNbr}', datafield: 'insuranceNumber', columntype: 'datetimeinput',width: '150px', cellsformat: 'dd/MM/yyyy'},
			{ text: '${uiLabelMap.HealthInsuranceNbr}', datafield: 'partyHealthCareId', width: '200px'},
			{ text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy',width: '150px'},
			{ text: '${uiLabelMap.thruDate}', datafield: 'thruDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy',width: '150px'}
            ],
        });   
 }"/>
<#assign dataField="[{ name: 'reportId', type: 'string' },
					 { name: 'partyId', type: 'string' },
					 { name: 'firstName', type: 'string' },
					 { name: 'middleName', type: 'string' },
					 { name: 'lastName', type: 'string' },
					 { name: 'insuranceParticipateTypeId', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp' },
					 { name: 'thruDate', type: 'date', other:'Timestamp' },
					 { name: 'comments', type: 'string' },
					 { name: 'rowDetail', type: 'string'}]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.reportId}', datafield: 'reportId', hidden: true},
					 { text: '${uiLabelMap.partyId}', datafield: 'partyId', hidden: true},
					 { text: '${uiLabelMap.EmployeeName}', datafield: 'firstName', width: 250, 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#listPartyInsuranceReport').jqxGrid('getrowdata', row);
					 		return '<div style=\"margin-top: 4px; margin-left: 5px;\">'+data.lastName + ' ' + data.middleName + ' ' + data.firstName +'</div>';
					 	}
					 },
                     { text: '${uiLabelMap.InsuranceParticipateType}', datafield: 'insuranceParticipateTypeId', 
                     	cellsrenderer: function(row, column, value){
					 		for(var x in listIPTypes){
					 			if(listIPTypes[x].insuranceParticipateTypeId == value){
					 				return '<div style=\"margin-top: 4px; margin-left: 5px;\">' + listIPTypes[x].description + '</div>';
					 			}	
					 		}
					 		
					 	}
                     },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.HRApprovalNote}', datafield: 'comments'}"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPartyInsuranceReport&reportId=${parameters.reportId}" initrowdetailsDetail=initrowdetailsDetail initrowdetails = "true"  dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	autorowheight="true"
	showtoolbar = "true" deleterow="true"
	id="listPartyInsuranceReport"
	removeUrl="jqxGeneralServicer?sname=deletePartyInsuranceReport&jqaction=D" deleteColumn="reportId;partyId;insuranceParticipateTypeId;insuranceTypeId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createEmplParticipateInsurance" alternativeAddPopup="popupAddIncreasement" addrow="true" addType="popup" 
	addColumns="reportId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);insuranceTypeId(java.util.List);suspendReasonId;insuranceParticipateTypeId;comments" addrefresh="true"
/>

<div id='popupAddIncreasement' class='hide'>
	<div>
		${uiLabelMap.EditParticipateInsuranceReport}
	</div>
	<div>
		<form class='form-horizontal'>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk" for="">${uiLabelMap.EmployeeId}</label>
				<div class="controls">
					<div id='partyIdFromAdd'>
						<div id="jqxPartyFromGrid"></div>
					</div>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk" for="">${uiLabelMap.InsuranceType}</label>
				<div class="controls">
					<div id='insuranceTypes'></div>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk" for="">${uiLabelMap.InsuranceParticipateType}</label>
				<div class="controls">
					<div id='insuranceParticipateTypes'></div>
				</div>
			</div>
			<div class="control-group no-left-margin hide" id='suspendTypeContainer'>
				<label class="control-label" for="">${uiLabelMap.SuspendParticipateInsuranceReason}</label>
				<div class="controls">
					<div id='suspendTypeAdd'></div>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.AvailableFromDate}</label>
				<div class="controls">
					<div id="fromDateJQ"></div>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">${uiLabelMap.AvailableThruDate}</label>
				<div class="controls">
					<div id="thruDateJQ"></div>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label" for="">${uiLabelMap.HRApprovalNote}</label>
				<div class="controls">
					<textarea rows="3" cols="10" id="commentsAdd"></textarea>
				</div>
			</div>
			<div class="row-fluid wizard-actions pull-right">
				<button type="button" class='btn btn-primary btn-save' id="alterSaveIncrease"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</form>
	</div>
	
</div>
<script>
	$(document).ready(function(){
		var reportId = "${parameters.reportId}";
		var skillJqx = $("#listPartyInsuranceReport");
		var popup = $("#popupAddIncreasement");
		popup.jqxWindow({
		    width: 600, height: 530, resizable: true,  isModal: true, autoOpen: false, theme:'olbius'
		});
		popup.on('close', function (event) { 
			popup.jqxValidator('hide');
		}); 
		var increase = true;
		var dea = getInsuranceType(listIPTypes, "PARTICIPATE", "REDUCE_PARTICIPATE");
		var insurancePTypeDd = $('#insuranceParticipateTypes');
		insurancePTypeDd.jqxDropDownList({
			theme: 'olbius',
			source: dea,
			width: 218,
			dropDownHeight: 100,
			displayMember: "description",
			valueMember: 'insuranceParticipateTypeId'
		});
		insurancePTypeDd.on("change", function(){
			var selected = $(this).jqxDropDownList("getSelectedItem");
			var key = selected ? selected.value : "";
			if(key){
				for(var x in dea){
					if(dea[x].insuranceParticipateTypeId == key && dea[x].parentTypeId == "REDUCE_PARTICIPATE"){
						$('#suspendTypeContainer').show();
						return;
					}
				}
			}
			$('#suspendTypeContainer').hide();
		});
		var insuranceTypeDd = $('#insuranceTypes');
		insuranceTypeDd.jqxListBox({ source: listInsuranceTypes, displayMember:"description", valueMember:"insuranceTypeId", multiple: true, width: 218, height: 110, theme:'olbius'});
		var suspendDd = $('#suspendTypeAdd');
		suspendDd.jqxDropDownList({
			theme: 'olbius',
			source: listSuspend,
			width: 218,
			dropDownHeight: 100,
			displayMember: "description",
			valueMember: 'suspendReasonId'
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
		var sourcePartyFrom = {
			datafields:[{name: 'partyId', type: 'string'},
				   		{name: 'firstName', type: 'string'},
				      	{name: 'lastName', type: 'string'},
				      	{name: 'middleName', type: 'string'},
				      	{name: 'groupName', type: 'string'},
		    ],
			cache: false,
			root: 'results',
			datatype: "json",
			beforeprocessing: function (data) {
			    sourcePartyFrom.totalrecords = data.TotalRows;
			},
			filter: function () {
			   	$("#jqxPartyFromGrid").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
			  	// callback called when a page or page size is changed.
			},
			sort: function () {
			  	$("#jqxPartyFromGrid").jqxGrid('updatebounddata');
			},
			sortcolumn: 'partyId',
	       	sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=JQGetListParties'
		};

		var dataAdapterPF = new $.jqx.dataAdapter(sourcePartyFrom, {
			autoBind: true,
			formatData: function (data) {
				if (data.filterscount) {
			        var filterListFields = "";
			        for (var i = 0; i < data.filterscount; i++) {
			            var filterValue = data["filtervalue" + i];
			            var filterCondition = data["filtercondition" + i];
			            var filterDataField = data["filterdatafield" + i];
			            var filterOperator = data["filteroperator" + i];
			            filterListFields += "|OLBIUS|" + filterDataField;
			            filterListFields += "|SUIBLO|" + filterValue;
			            filterListFields += "|SUIBLO|" + filterCondition;
			            filterListFields += "|SUIBLO|" + filterOperator;
			        }
			        data.filterListFields = filterListFields;
			    }
			    return data;
			},
			loadError: function (xhr, status, error) {
			    alert(error);
			},
			downloadComplete: function (data, status, xhr) {
			        if (!sourcePartyFrom.totalRecords) {
			            sourcePartyFrom.totalRecords = parseInt(data['odata.count']);
			        }
			}
		});	
		// create Party From
		var partyIdFromAdd = $('#partyIdFromAdd');
		partyIdFromAdd.jqxDropDownButton({ width: 218, height: 30, theme:'olbius'});
		$("#jqxPartyFromGrid").jqxGrid({
			width:600,
			source: dataAdapterPF,
			filterable: true,
			virtualmode: true, 
			sortable:true,
			editable: false,
			autoheight:true,
			pageable: true,
			showfilterrow: true,
			theme: 'olbius',
			rendergridrows: function(obj) {	
				return obj.data;
			},
			columns:[{text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '20%'},
						{text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '20%'},
						{text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '20%'},
						{text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '20%'},
						{text: '${uiLabelMap.DAGroupName}', datafield: 'groupName'},
					]
			});
			$("#jqxPartyFromGrid").on('rowselect', function (event) {
			var args = event.args;
			var row = $("#jqxPartyFromGrid").jqxGrid('getrowdata', args.rowindex);
			var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
			partyIdFromAdd.jqxDropDownButton('setContent', dropDownContent);
		});
		popup.jqxValidator({
		   	rules: [{
	               input: "#partyIdFromAdd", 
	               message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
	               action: 'blur', 
	               rule: function (input, commit) {
	                   var index = input.val();
	                   return  index? true: false;
	               }
	           },{
	               input: "#insuranceParticipateTypes", 
	               message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
	               action: 'blur', 
	               rule: function (input, commit) {
	                   var index = input.jqxDropDownList('getSelectedIndex');
	                   return index != -1;
	               }
	           },{
	               input: "#insuranceTypes", 
	               message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
	               action: 'blur', 
	               rule: function (input, commit) {
	                   var index = input.jqxListBox('getSelectedItems');
	                   return index.length ? true : false;
	               }
	           },{
	               input: "#fromDateJQ", 
	               message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
	               action: 'blur', 
	               rule: function (input, commit) {
	                   var date = input.jqxDateTimeInput('getDate');
	                   return date ? true : false;
	               }
	           }],
		 })
		 $("#addIncrease").click(function(){
			 popup.jqxWindow("open"); 
		 });
		$("#alterSaveIncrease").click(function () {
			if(!popup.jqxValidator('validate')){
				return;
			}
			var fromDate = $('#fromDateJQ').jqxDateTimeInput('getDate') ? Utils.formatDateYMD($('#fromDateJQ').jqxDateTimeInput('getDate')) : "";
			var thruDate = $('#thruDateJQ').jqxDateTimeInput('getDate') ? Utils.formatDateYMD($('#thruDateJQ').jqxDateTimeInput('getDate')) : "";
			var partyId = partyIdFromAdd.val();
			var i = insurancePTypeDd.jqxDropDownList('getSelectedItem');
			var insuranceParticipateTypeId = i ? i.value : "";
			i = insuranceTypeDd.jqxDropDownList('getSelectedItem');
			var insuranceTypeId = i ? i.value : "";
			i = suspendDd.jqxDropDownList('getSelectedItem');
			var suspendReasonId = i ? i.value : "";
			var comments = $("#commentsAdd").val();
			var tmp = insuranceTypeDd.jqxListBox("getSelectedItems");
			insuranceTypeId = new Array();
			for (var x in tmp){
				insuranceTypeId.push(tmp[x].value);
			}
		   	var row = { 
		   		reportId: reportId,
		   		partyId: partyId,
		   		insuranceParticipateTypeId: insuranceParticipateTypeId,
		   		suspendReasonId: suspendReasonId,
		   		comments : comments,
		   		fromDate: fromDate,
		   		thruDate: thruDate,
		   		insuranceTypeId: JSON.stringify(insuranceTypeId)
		   	  };
		    skillJqx.jqxGrid('addRow', null, row, "first");
		       // select the first row and clear the selection.
	       skillJqx.jqxGrid('clearSelection');                        
	       skillJqx.jqxGrid('selectRow', 0);  
	       popup.jqxWindow('close');
		   });
		function getInsuranceType(list, type1, type2){
			var arr = new Array();
			for(var x in list){
				if(list[x].parentTypeId == type1 || list[x].parentTypeId == type2){
					arr.push(list[x]);
				}
			}
			return arr;
		}
	});
</script>