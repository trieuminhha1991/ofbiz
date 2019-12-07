<@jqGridMinimumLib/>
<div id="errorNotification" style="width: 100%" class="row-fluid">
		<span id="errorNotificationContent"></span>	
	</div>	

<div id="container" style="width: 100%" class="row-fluid">
	<div id="notification" style="width: 100%" class="row-fluid">
		<span id="notificationContent"></span>	
	</div>	
</div>

<form name="EmplBussinessTripProposal" id="EmplBussinessTripProposal" method="post" action="<@ofbizUrl>createEmplBussinessTripProposal</@ofbizUrl>" class="basic-form form-horizontal">
	<div class="row-fluid">
		<input type="hidden" name="partyId" value="${emplId?if_exists}">
		<div class="control-group no-left-margin">
			<label>
				<label for="proposer" id="proposer_title">
					${uiLabelMap.HREmplProposer}
				</label>
			</label>
			<div class="controls">
				<span class="ui-widget">
					${employeeName?if_exists} [${emplId?if_exists}]
				</span>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label>
				<label>
					${uiLabelMap.EmployeeCurrentDept}
				</label>
			</label>
			<div class="controls">	
				${currDept?if_exists}
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<h4>I. ${uiLabelMap.BussinessTripPlan}</h4>
		<div style="margin-top: 10px;margin-bottom : 10px;float:right;">
		   	<button class="btn btn-mini btn-info" type="button" id="addNewRowPlan">
				<i class="icon-plus "></i>
				${uiLabelMap.AddNewRow}
			</button>
			<button class="btn btn-mini btn-danger" type="button" id="deleleSelectedRowPlan">
				<i class="icon-remove"></i>
				${uiLabelMap.DeleteSelectedRow}
			</button>
		</div>
		<div id="bussinessTripPlan" style="width: 100%">
		</div>
	</div>
	<div class="row-fluid" style="margin-top: 30px">
		<h4>II. ${uiLabelMap.VerhicleRegis}</h4>
		<div style="margin-top: 10px;margin-bottom : 10px;float : right;">
		   	<button class="btn btn-mini btn-info" type="button" id="addNewRowVeh">
				<i class="icon-plus "></i>
				${uiLabelMap.AddNewRow}
			</button>
			<button class="btn btn-mini btn-danger" type="button" id="deleleSelectedRowVeh">
				<i class="icon-remove"></i>
				${uiLabelMap.DeleteSelectedRow}
			</button>
		</div>
		<div id="VehicleRegis" style="width: 100%">
		</div>
	</div>
	<div class="space-4"></div>
	<div class="row-fluid">
		<div class="form-actions" style="padding-left: 0; text-align: center;">
			<button class="btn btn-small btn-primary" id="submitForm" type="button" id="submitBtn">
				<i class="icon-ok"></i>
				${uiLabelMap.CommonSubmit}
			</button>
			<!-- <button class="btn btn-small" type="button" id="resetBtn">
				<i class="icon-undo bigger-110"></i>
				${uiLabelMap.CommonReset}
			</button> -->
		</div>
		
	</div>
	<div id="messageNotification" style="width : 100%;display : none;">
        <div>
        	Thời gian làm việc không hợp lệ!
        </div>
	</div>
	<div id="messageNotification1" style="width : 100%;display : none;">
        <div>
        	Thời gian đăng ký phương tiện không hợp lệ!
        </div>
	</div>
</form>

<script type="text/javascript">
$.jqx.theme = 'olbius';  
theme = $.jqx.theme;
$(document).ready(function () {
	var source = {
			dataType: "array",
			datafields:[
			   {name: 'bussinessTripContent', type: 'string'},
			   {name: 'bussinessDest', type:'string'},
			   {name: 'fromDate', type:'date', format: "dd-MM-yyyy"},
			   {name: 'thruDate', type:'date'},
			   {name: 'notes', type:'string'}
			   ]
		};
	var dataAdapter = new jQuery.jqx.dataAdapter(source);
	jQuery("#bussinessTripPlan").jqxGrid({
		width: '100%',
		source:dataAdapter,
		pageable: true,
        autoheight: true,
        sortable: false,
        altrows: true,
        enabletooltips: true,
        editable: true,
        selectionmode: 'singlerow',
        editmode: 'selectedrow',
        theme: theme,
        columns:[
            {text: '${uiLabelMap.BussinessTripContent}', datafield:'bussinessTripContent', width: "30%", align: 'center',
            		
            },
            {text: '${uiLabelMap.BussinessDest}', datafield:'bussinessDest', width: "20%", align: 'center',
            		
            },
            {text: '${uiLabelMap.CommonFromDate}', columngroup: 'timeWorking', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: "10%", align: 'center',
            	createeditor: function (row, column, editor) {
             		editor.jqxDateTimeInput({ formatString: 'dd-MM-yyyy' });
             	}
            },
            {text: '${uiLabelMap.CommonThruDate}', columngroup: 'timeWorking', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', width: "10%", align: 'center',
            	createeditor: function (row, column, editor) {
             		editor.jqxDateTimeInput({  formatString: 'dd-MM-yyyy' });
             	}	
            },
            {text: '${uiLabelMap.HRNotes}', datafield:'notes', width: "30%", align: 'center'}
        ],
        columngroups: [
           { text: '${uiLabelMap.TimeWorking}', align: 'center', name: 'timeWorking' }
       ]
	});
	
	jQuery("#addNewRowPlan").click(function(){
		var newRow = jQuery("#bussinessTripPlan").jqxGrid('addrow', null, {});
		//jQuery("#bussinessTripPlan").jqxGrid("beginrowedit", 0);
		var rows = $('#bussinessTripPlan').jqxGrid('getrows');
		
		var paginginformation = jQuery('#bussinessTripPlan').jqxGrid('getpaginginformation');
		var curPage = paginginformation.pagenum;
		if(curPage < paginginformation.pagescount - 1){
			jQuery('#bussinessTripPlan').jqxGrid('gotopage', paginginformation.pagescount - 1);	
		}
		jQuery('#bussinessTripPlan').jqxGrid("beginrowedit", (rows.length - 1));
	});
	
	jQuery("#deleleSelectedRowPlan").click(function(){
		var selectedrowindex = $("#bussinessTripPlan").jqxGrid('getselectedrowindex');
	    var rowscount = $("#bussinessTripPlan").jqxGrid('getdatainformation').rowscount;
	    if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
	         var id = $("#bussinessTripPlan").jqxGrid('getrowid', selectedrowindex);
	         var commit = $("#bussinessTripPlan").jqxGrid('deleterow', id);
	     }
	});
	
	var vehicleArray = new Array();
	<#if vehicleType?exists>
		<#list vehicleType as type>
			temp = {}; 
			temp["vehicleTypeId"] = "${type.vehicleTypeId}";
			temp["description"]	 = "${type.description}";
			vehicleArray.push(temp);
		</#list>
	</#if>
	
	 var vehicleTypeSource = {
			datatype: "array",
            datafields: [
                { name: 'vehicleTypeId', type: 'string' },
                { name: 'description', type: 'string' }
            ],
            localdata: vehicleArray	
		};
	 var vehicleAdapter = new $.jqx.dataAdapter(vehicleTypeSource, {
         autoBind: true
     }); 
	 
	var localdata = new Array();
	<#if vehicleType?exists>
		var tempData = {};
		tempData["vehicleTypeId"] = "${vehicleType[0].vehicleTypeId}";
		localdata.push(tempData);
	</#if>
	var souceVeh = {
			localdata: localdata,
			dataType: 'array',
			datafields: [
				 { name: 'vehicle', value: "vehicleTypeId", values: { source: vehicleAdapter.records, value: 'vehicleTypeId', name: 'description' }},
				 /* { name: 'vehicle', value: "string"}, */
                 /* { name: 'registerUse', type: 'bool' }, */
                 {name: 'vehicleTypeId', type:'string'},
                 { name: 'journey', type: 'string' },
                 { name: 'dateDeparture', type: 'date', format: "dd-MM-yyyy HH:mm:ss"},
                 { name: 'dateArrival', type: 'date', format: "dd-MM-yyyy HH:mm:ss"},
                 { name: 'notes', type: 'string' },
            ]
		};
	var dataVehAdapter = new jQuery.jqx.dataAdapter(souceVeh);
	
	jQuery("#VehicleRegis").jqxGrid({
		width: '100%',
		source: dataVehAdapter,
		pageable: true,
        autoheight: true,
        sortable: false,
        altrows: true,
        enabletooltips: true,
        editable: true,
        selectionmode: 'singlerow',
        editmode: 'selectedrow',
        columns:[
        	{text: '${uiLabelMap.Vehicle}',  columntype: 'dropdownlist', datafield: 'vehicleTypeId', displayfield: 'vehicle',align: 'center', width: "15%",
        		createeditor: function(row, column, editor){        			
        			editor.jqxDropDownList({
        				autoDropDownHeight: true, source: vehicleAdapter, valueMember: "vehicleTypeId", displayMember: "description",        				
        			});
        		}	
        	},
        	{text: '${uiLabelMap.JourneyBussiness}', datafield:'journey', align: 'center', columntype: 'textbox', width: "30%"},
        	{text: '${uiLabelMap.DateDeparture}', datafield:'dateDeparture', align: 'center', columntype: 'datetimeinput', columngroup: 'Time', cellsformat: 'dd/MM/yyyy HH:mm', width:"16%",
        		createeditor: function (row, column, editor) {
             		editor.jqxDateTimeInput({ formatString: 'dd-MM-yyyy HH:mm' });
             	}	
        	},
        	{text: '${uiLabelMap.DateArrival}', datafield:'dateArrival', align: 'center', columntype: 'datetimeinput', columngroup: 'Time', cellsformat: 'dd/MM/yyyy HH:mm', width: "16%",
        		 createeditor: function (row, column, editor) {
             		editor.jqxDateTimeInput({ formatString: 'dd-MM-yyyy HH:mm' });
             	}
        	},
        	{text: '${uiLabelMap.HRNotes}', datafield:'notes', align: 'center', columntype: 'text', width: "23%"}
        ],
        columngroups: [
            { text: '${uiLabelMap.CommonTime}', align: 'center', name: 'Time' }
        ]
	});
	
	jQuery("#addNewRowVeh").click(function(){
		jQuery("#VehicleRegis").jqxGrid('addrow', null, {});
		//jQuery("#VehicleRegis").jqxGrid("beginrowedit", 0);
		var rows = $('#VehicleRegis').jqxGrid('getrows');
		
		var paginginformation = jQuery('#VehicleRegis').jqxGrid('getpaginginformation');
		var curPage = paginginformation.pagenum;
		if(curPage < paginginformation.pagescount - 1){
			jQuery('#VehicleRegis').jqxGrid('gotopage', paginginformation.pagescount - 1);	
		}
		jQuery('#VehicleRegis').jqxGrid("beginrowedit", (rows.length - 1));
	});		

	jQuery("#deleleSelectedRowVeh").click(function(){
		var selectedrowindex = $("#VehicleRegis").jqxGrid('getselectedrowindex');
	    var rowscount = $("#VehicleRegis").jqxGrid('getdatainformation').rowscount;
	    if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
	         var id = $("#VehicleRegis").jqxGrid('getrowid', selectedrowindex);
	         var commit = $("#VehicleRegis").jqxGrid('deleterow', id);         
	     }
	});
	jQuery("#VehicleRegis").on("beginrowedit", function(event){
	});
	
	jQuery("#submitForm").click(function(){
		var dataForm = jQuery("#EmplBussinessTripProposal").serializeArray();
		var planRows = jQuery('#bussinessTripPlan').jqxGrid('getrows');
		var vehicleRows = jQuery('#VehicleRegis').jqxGrid('getrows');
		var flag1;
		var flag2;
		var flagCheckSubmit;
		for(var i = 0; i < planRows.length; i++){
			var fromDateValue = jQuery("#bussinessTripPlan").jqxGrid("getcellvalue", planRows[i].uid, "fromDate");
			var thruDateValue = jQuery("#bussinessTripPlan").jqxGrid("getcellvalue", planRows[i].uid, "thruDate");
			
			if(fromDateValue && fromDateValue !== 'undefined' && fromDateValue != null ){
				flag1 = true;		
			}else flag1 = false;
			if(thruDateValue && thruDateValue !== 'undefined' && thruDateValue != null ){
				flag2 = true;		
			}else flag2 = false;
			if(flag1 && flag2){
				var valueValidate = dates.compare(fromDateValue,thruDateValue);
				if(valueValidate == 0 || valueValidate == 1 ){
					flagCheckSubmit = true;
				}else flagCheckSubmit = false;
			};
				
			dataForm.push({name: "busTripContent", value: jQuery("#bussinessTripPlan").jqxGrid("getcellvalue", planRows[i].uid, "bussinessTripContent")});
			dataForm.push({name: "busDest", value: jQuery("#bussinessTripPlan").jqxGrid("getcellvalue", planRows[i].uid, "bussinessDest")});
			if(fromDateValue){
				dataForm.push({name: "busFromDate", value: (new Date(fromDateValue)).getTime()}); 
			}else{
				dataForm.push({name:"busFromDate"});
			}
			if(thruDateValue){
				dataForm.push({name:"busThruDate", value: (new Date(thruDateValue)).getTime()});
			}else{
				dataForm.push({name:"busThruDate"});
			}
			dataForm.push({name:"busNotes", value: jQuery("#bussinessTripPlan").jqxGrid("getcellvalue", planRows[i].uid, "notes")});
		}
		if(flagCheckSubmit){
			jQuery("#messageNotification").jqxNotification({ template: 'error' , opacity : 1,autoClose: false ,appendContainer : '#container'});
			jQuery("#messageNotification").jqxNotification("open");
		}else {
			for(var i = 0; i < vehicleRows.length; i++){
				var fromDateValue = jQuery("#VehicleRegis").jqxGrid("getcellvalue", vehicleRows[i].uid, "dateDeparture");
				var thruDateValue = jQuery("#VehicleRegis").jqxGrid("getcellvalue", vehicleRows[i].uid, "dateArrival");
				
					if(fromDateValue && fromDateValue !== 'undefined' && fromDateValue != null ){
						flag1 = true;		
					}else flag1 = false;
					if(thruDateValue && thruDateValue !== 'undefined' && thruDateValue != null ){
						flag2 = true;		
					}else flag2 = false;
					if(flag1 && flag2){
						var valueValidate = dates.compare(fromDateValue,thruDateValue);
						if(valueValidate == 0 || valueValidate == 1 ){
							flagCheckSubmit = true;
						}else flagCheckSubmit = false;
					};
				
				dataForm.push({name: "vehTypeId", value: jQuery("#VehicleRegis").jqxGrid("getcellvalue", vehicleRows[i].uid, "vehicleTypeId")});
				dataForm.push({name: "journey", value: jQuery("#VehicleRegis").jqxGrid("getcellvalue", vehicleRows[i].uid, "journey")});
				if(fromDateValue){
					dataForm.push({name: "dateDeparture", value: (new Date(fromDateValue)).getTime()});	
				}else{
					dataForm.push({name: "dateDeparture"});
				}
				if(thruDateValue){
					dataForm.push({name: "dateArrival", value: (new Date(thruDateValue)).getTime()});	
				}else{
					dataForm.push({name: "dateArrival"});	
				}
				
				dataForm.push({name: "vehNotes", value: jQuery("#VehicleRegis").jqxGrid("getcellvalue", vehicleRows[i].uid, "notes")});
			}
		}
		
		if(flagCheckSubmit){
			jQuery("#messageNotification1").jqxNotification({ template: 'error',opacity : 1,autoClose: false,appendContainer : '#container'});
			jQuery("#messageNotification1").jqxNotification("open");
		}
		
		if(!flagCheckSubmit){
					jQuery.ajax({
					url: "<@ofbizUrl>createBussinessTripProposal</@ofbizUrl>",
					type: "POST",
					data: dataForm,
					dataType: 'json',
					success: function(data){
						if(data._EVENT_MESSAGE_){
							bootbox.dialog({
								message: data._EVENT_MESSAGE_,
								 buttons: {
									 success: {
										 label: '${uiLabelMap.CommonSubmit}',
										 className: "btn-primary btn-mini btn",
									 }
								 }
							});
						}else if(data._ERROR_MESSAGE_){
							$("#container").empty();
							$("#errorNotification").jqxNotification({ template: 'error',opacity : 1,autoClose: false,appendContainer : '#container'});
							$("#errorNotification").text("${StringUtil.wrapString(uiLabelMap.ErrorOccur)}: " + data._ERROR_MESSAGE_);
							$("#errorNotification").jqxNotification("open");
						}
					},
					error: function(){
						
					}
				});
		}
		
	});
	jQuery("#notification").jqxNotification({
		opacity: 0.9, autoOpen: false, animationOpenDelay: 800, autoClose: false,  template: "info",
		appendContainer: "#container",
		width: "100%"
		
    });
	
});
			


/* jQuery("#VehicleRegis").on('cellselect', function (event) {
    var column = jQuery("#VehicleRegis").jqxGrid('getcolumn', event.args.datafield);
    var value = jQuery("#VehicleRegis").jqxGrid('getcellvalue', event.args.rowindex, column.datafield);
    var displayValue = jQuery("#VehicleRegis").jqxGrid('getcellvalue', event.args.rowindex, column.displayfield);
}); */
//compare two date value
var dates = {
    convert:function(d) {
        return (
            d.constructor === Date ? d :
            d.constructor === Array ? new Date(d[0],d[1],d[2]) :
            d.constructor === Number ? new Date(d) :
            d.constructor === String ? new Date(d) :
            typeof d === "object" ? new Date(d.year,d.month,d.date) :
            NaN
        );
    },
    compare:function(a,b) {
        return (
            isFinite(a=this.convert(a).valueOf()) &&
            isFinite(b=this.convert(b).valueOf()) ?
            (a>b)-(a<b) :
            NaN
        );
    },
    inRange:function(d,start,end) {
       return (
            isFinite(d=this.convert(d).valueOf()) &&
            isFinite(start=this.convert(start).valueOf()) &&
            isFinite(end=this.convert(end).valueOf()) ?
            start <= d && d <= end :
            NaN
        );
    }
}
</script> 
