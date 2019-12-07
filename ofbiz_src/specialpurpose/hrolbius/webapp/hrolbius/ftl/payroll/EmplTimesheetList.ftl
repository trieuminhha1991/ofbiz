<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdatatable.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<#include "/hrolbius/webapp/hrolbius/ftl/js/commonUtil.ftl"/>
<#assign datafield = "[{name: 'emplTimesheetId', type: 'string'},
					   {name: 'emplTimesheetName', type: 'string'},
					   {name: 'fromDate', type: 'date', other:'Timestamp'},
					   {name: 'thruDate', type: 'date', other:'Timestamp'},
					   {name: 'statusId', type: 'string'}]"/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
<script type="text/javascript">
	var statusArray = new Array();
	<#if statusList?has_content>
		<#list statusList as status> 
			var row = {};
			row["statusId"] = "${status.statusId}";
			row["description"] = "${StringUtil.wrapString(status.description)}";
			statusArray[${status_index}] = row;			
		</#list>
	</#if>
	var emplTimekeepingSignArr = new Array();
	<#if listEmplTimekeepingSign?has_content>
		<#list listEmplTimekeepingSign as emplTimekeepingSign>
			var row = {};
			row["emplTimekeepingSignId"] = "${emplTimekeepingSign.emplTimekeepingSignId}";
			row["description"] = "${StringUtil.wrapString(emplTimekeepingSign.description?if_exists)}";
			row["sign"] = "${StringUtil.wrapString(emplTimekeepingSign.sign)}";
			emplTimekeepingSignArr[${emplTimekeepingSign_index}] = row;
		</#list>
	</#if>
	
	<#assign columnlist = "{text: '${uiLabelMap.EmplTimesheetId}', datafield: 'emplTimesheetId', width: 150, editable: false},
							{text: '${uiLabelMap.EmplTimesheetName}', datafield: 'emplTimesheetName', width: 200},
							{ text: '${uiLabelMap.fromDate}', datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', filtertype:'range', columntype: 'datetimeinput', editable: false, width: 170},
							{ text: '${uiLabelMap.thruDate}', datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', filtertype:'range',columntype: 'datetimeinput' , editable: false, width: 170},
							{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', editable: false,
								cellsrenderer : function(row, column, value){
									for(var i = 0; i < statusArray.length; i++){
										if(value == statusArray[i].statusId){
											return '<div style=\"margin-top: 6px; margin-left: 4px;\">' +  statusArray[i].description + '</div>'; 
										}
									}
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">' +  value + '</div>';
								}	
							}">
	var sourceTimesheet = {
			datatype: "json",
			type: 'POST',
			data: {},
			datafields: [],
			url: 'getEmplTimesheetAttendance',
			root: 'listIterator',
			beforeprocessing: function (data) {
				sourceTimesheet.totalrecords = data.TotalRows;
	        },
	        id: 'partyId',
	        pagenum: 0,
	        pagesize: 15,
	        pager: function (pagenum, pagesize, oldpagenum) {
	            //callback called when a page or page size is changed.
	        }
	};
	var dataAdapterTimesheet = new $.jqx.dataAdapter(sourceTimesheet);
	
	var nowDate = new Date();
	var previousFirstDate = new Date(nowDate.getFullYear(), nowDate.getMonth() - 1, 1);
	var previousLastDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), 0);
	
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
	
	$(document).ready(function(){
		var popup = $("#popupAddRow");
		var emplTimesheetAttendancePopup = $("#emplTimesheetAttendancePopup");
		var jqxWindowEmplTimesheetInDay = $("#jqxWindowEmplTimesheetInDay");
		var jqxGridEmplTimekeepingSign = $("#jqxGridEmplTimekeepingSign");
		initJqxWindow();
		initBtnEvent();
		
		$("#jqxNotifyEmplTimesheets").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#jqxNotifyEmplTimesheetContainer"});
		
		$("#jqxTimesheetAtt").jqxGrid({
			width: '1005px', 
			autoheight: false,
			height: 455,
	        source: dataAdapterTimesheet,
            columnsheight: 30,
            pagesize: 15,
            pageSizeOptions: ['15', '30', '50', '100', '200'],
            pagerMode: 'advanced',
            columnsResize: true,
            pageable: true,
            columns: [],
            selectionmode: 'singlecell',
            virtualmode: true,
            rendergridrows: function () {
                return dataAdapterTimesheet.records;
            },
            theme: 'olbius'
		});
		
		
		var emplTimekeepingSignDatafield = [{name: 'emplTimekeepingSignId', type: 'string'},
		                                    {name: 'emplTimesheetId', type: 'string'},
		                                    {name: 'description', type: 'string'},
		                                    {name: 'sign', type: 'string'},
		                                    {name: 'dateAttendance', type: 'date'},
		                                    {name: 'partyId', type: 'string'},
		                                    {name: 'workday', type: 'number'},
		                                    {name: 'hours', type: 'number'}];
		
		var emplTimekeepingSignColumns = [{datafiled: 'partyId', hidden: true},
		                                  {datafiled: 'emplTimekeepingSignId', hidden: true},
		                                  {datafiled: 'emplTimesheetId', hidden: true},
		                                  {datafield: 'dateAttendance', hidden: true, cellsformat: 'dd/MM/yyyy', editable: false},
		                                  {datafield: 'hours', text: '${uiLabelMap.CommonHoursNumber}', width: 65, editable: true,
		                                	 cellsrenderer: function(row, column, value){
		                                		 if(value){
		                                			return '<div style="text-align:right">' + value + '</div>'; 
		                                		 }
		                                	 },
		                                	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		                                		 editor.jqxInput({width: '100%', height: '100%'});
		                                	 }, 
		                                	 cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
		         	                            // return the old value, if the new value is empty.
		         	                            if (!newvalue || newvalue == "") return oldvalue;
		         	                        },
		         	                       	validation: function(cell, value){
	                                		  var pattern = /^([0-9]+\.?[0-9]*)$/;
	                                		  if(value && !value.match(pattern)){
	                                			  return {result: false,  message: "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}"};	  
	                                		  }
	                                		  return true;
		                                	},
		                                	cellbeginedit: function (row, datafield, columntype){
		                                		var rowselectedIndexs = $("#jqxGridEmplTimekeepingSign").jqxGrid('getselectedrowindexes');
		                                		if(rowselectedIndexs.indexOf(row) < 0){
		                                			return false;
		                                		}
		                                		return true;
		                                	}
		                                  },
		                                  {datafield: 'workday', text: '${StringUtil.wrapString(uiLabelMap.Workday)}', width: 80, editable: true,
		                                	  createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight){
		                                		  editor.jqxInput({width: '100%', height: '100%'});
		                                	  },
		                                	  validation: function(cell, value){
		                                		  var pattern = /^([0-9]+\.?[0-9]*)$/;
		                                		  if(value && !value.match(pattern)){
		                                			  return {result: false,  message: "${StringUtil.wrapString(uiLabelMap.OnlyInputNumberGreaterThanZero)}"};	  
		                                		  }
		                                		  return true;
		                                	  },
		                                	  cellbeginedit: function (row, datafield, columntype){
			                                		var rowselectedIndexs = $("#jqxGridEmplTimekeepingSign").jqxGrid('getselectedrowindexes');
			                                		if(rowselectedIndexs.indexOf(row) < 0){
			                                			return false;
			                                		}
			                                		return true;
			                                	}
		                                  },
		                                  {datafield: 'sign', text: '${StringUtil.wrapString(uiLabelMap.EmplTimekeepingSign)}', width: 70, editable: false},
		                                  {datafield: 'description', text: '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', editable: false}
		                                  ];
		
		/* var emplTimekeepingSignSource = {
			 localdata: emplTimekeepingSignArr,
		     datatype: 'array'		
		}
		
		var emplTimekeepingSignAdapter = new $.jqx.dataAdapter(emplTimekeepingSignSource, {autoBind: true}); */
		
		var emplTimekeepingSignSource = {
				localdata: [],
				datafields: emplTimekeepingSignDatafield,
				datatype: 'array',				
		};
		
		var emplTimekeepingSignAdapter = new $.jqx.dataAdapter(emplTimekeepingSignSource, {autoBind: true});
		var rowsheight = 25;
		var maxHeight = 380;
		jqxGridEmplTimekeepingSign.jqxGrid({
			width: 500,
			height: 364,
			editable: true,
			
			editmode: 'selectedcell',
	        source: emplTimekeepingSignAdapter,
            showtoolbar: false,
            rendertoolbar: function (toolbar) {
            	 var container = $("<div style='margin: 5px; text-align: right'></div>");
                 toolbar.append(container);
                 container.append('<i class="icon-ok"></i><input id="updateEmplTimekeepingSign" type="button" value="${uiLabelMap.CommonUpdate}" />');
                 $("#updateEmplTimekeepingSign").jqxButton({theme: 'olbius'});
            },
            pageSizeOptions: ['15','20', '30'],
            pagerMode: 'advanced',
            columnsresize: true,
            pageable: true,
            pagesize: 15,
            columns: emplTimekeepingSignColumns,
            selectionmode: 'checkbox',
            theme: 'olbius'
		});
		
		initContextMenu();
		initJqxDropDownTreeBtn();
		initJqxDropDownList();
		
		/*========================= event define =============================================*/
		emplTimesheetAttendancePopup.on('close', function (event){
			$("#jqxTimesheetAtt").jqxGrid("clear");
			
		});
		emplTimesheetAttendancePopup.on('open', function (event){
			
		});
		
		jqxWindowEmplTimesheetInDay.on('close', function (event){
			jqxGridEmplTimekeepingSign.jqxGrid("clear");
			$("#jqxNotificationTimesheetInDay").jqxNotification('closeLast');
			$("#updateEmplTimekeepingSignBtn").removeAttr("disabled");
			$("#jqxGridEmplTimekeepingSign").jqxGrid('clearselection');
		});
			
		$("#jqxTimesheetAtt").on("celldoubleclick", function(event){
			var args = event.args;
		    var rowBoundIndex = args.rowindex;
		    var columnIndex = args.columnindex;
		    //dataField have format like: dd/MM/yyyy
		    var dataField = args.datafield;
		    var data = $("#jqxTimesheetAtt").jqxGrid('getrowdata', rowBoundIndex);
		    var emplTimesheetId = data.emplTimesheetId;
		    var partyId = data.partyId;
		    var dateStr = dataField.split("/");
		    var date = new Date(dateStr[2], dateStr[1], dateStr[0]);
		    //get startTime, endTime of party in day
		    $("#startTimeIn").text("");	
		    $("#endTimeOut").text("");	
		    $.ajax({
		    	url: "getPartyAttendance",
		    	type: "POST",
		    	data: {partyId: partyId, dateKeeping: date.getTime()},
		    	success: function(data){
		    		$("#startTimeIn").text(data.startTime);	
		    		$("#endTimeOut").text(data.endTime);	
		    	},
		    	error:function(){
		    		
		    	}
		    });
		    $("#partyName").text(data.partyName);
		    //$("#jqxGridEmplTimekeepingSign").jqxGrid({disabled: true});
		    $("#jqxGridEmplTimekeepingSign").jqxGrid('showloadelement');
		    
		    $.ajax({
		    	url: 'getEmplTimesheetAttendanceWorkdayAndHours',
		    	data: {partyId: partyId, dateAttendance: date.getTime(), emplTimesheetId: emplTimesheetId},
		    	type: 'POST',
		    	success: function(response){
		    		var listReturn = response.listReturn;
		    		var localData = new Array();
				    var selectedrowindexes = new Array();
				    for(var i = 0; i < emplTimekeepingSignArr.length; i++){
						var row = new Array();
						row["emplTimesheetId"] = emplTimesheetId;
						row["emplTimekeepingSignId"] = emplTimekeepingSignArr[i].emplTimekeepingSignId; 
						for(var j = 0; j < listReturn.length; j++){
							if(listReturn[j].emplTimekeepingSignId == row["emplTimekeepingSignId"]){
								selectedrowindexes.push(i);
								row["hours"] = listReturn[j].hours;
								row["workday"] = listReturn[j].workday;
							}
						} 
						row["sign"] = emplTimekeepingSignArr[i].sign;
						row["partyId"] = partyId;
						row['emplTimesheetId'] = emplTimesheetId;
						row['description']= emplTimekeepingSignArr[i].description;
						row['dateAttendance'] = date; 
						localData.push(row);
					}
					
					var sourceEmplTimekeepingSign = jqxGridEmplTimekeepingSign.jqxGrid('source');	
					sourceEmplTimekeepingSign._source.localdata = localData;
					jqxGridEmplTimekeepingSign.jqxGrid('source', sourceEmplTimekeepingSign);
					jqxGridEmplTimekeepingSign.jqxGrid({selectedrowindexes: selectedrowindexes});
		    	},
		    	complete: function(jqXHR, textStatus){
		    		//$("#jqxGridEmplTimekeepingSign").jqxGrid({disabled: false});
				    $("#jqxGridEmplTimekeepingSign").jqxGrid('hideloadelement');
		    	}
		    });
			jqxWindowEmplTimesheetInDay.jqxWindow('setTitle', '${uiLabelMap.EmplTimesheetInDay}: ' + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear());
			jqxWindowEmplTimesheetInDay.jqxWindow('open');
		});
		
		$('#jqxgrid').on('rowDoubleClick', function (event){
			var args = event.args;
			var boundIndex = args.rowindex;
			var data = $('#jqxgrid').jqxGrid('getrowdata', boundIndex);
			
		});
		
	    popup.on('close', function (event) { 
	    	popup.jqxValidator('hide');
	    });
	    
	    $("#updateEmplTimekeepingSignBtn").click(function(){
	    	 var rowindexes = jqxGridEmplTimekeepingSign.jqxGrid('getselectedrowindexes');
        	 if(rowindexes.length > 0){
        		 $("#updateEmplTimekeepingSignBtn").attr("disabled", "disabled");
        		 var dataSubmit = new Array();
        		 var partyIdSubmit = jqxGridEmplTimekeepingSign.jqxGrid('getrowdata', rowindexes[0]).partyId;
        		 var dateAttendanceSubmit = jqxGridEmplTimekeepingSign.jqxGrid('getrowdata', rowindexes[0]).dateAttendance;
        		 var emplTimesheetId = jqxGridEmplTimekeepingSign.jqxGrid('getrowdata', rowindexes[0]).emplTimesheetId;
        		 for(var i = 0; i < rowindexes.length; i++){
            		 var rowData = jqxGridEmplTimekeepingSign.jqxGrid('getrowdata', rowindexes[i]);
            		 var timekeepingSignUpdateInfo = {};
            		 timekeepingSignUpdateInfo["emplTimekeepingSignId"] = rowData.emplTimekeepingSignId; 
            		 if(rowData.hours){
            			 timekeepingSignUpdateInfo["hours"] = rowData.hours;	 
            		 }
            		 if(rowData.workday){
            			 timekeepingSignUpdateInfo["workday"] = rowData.workday 
            		 }
            		 dataSubmit.push(timekeepingSignUpdateInfo);
            	 }	
        		 
        		 $("#jqxGridEmplTimekeepingSign").jqxGrid('showloadelement');
        		 $.ajax({
        			url: "updateEmplTimesheetAttendance",
        			data: {partyId: partyIdSubmit, dateAttendance: dateAttendanceSubmit.getTime(), emplTimekeepingSignList: JSON.stringify(dataSubmit), emplTimesheetId: emplTimesheetId},
        			type: 'POST',
        			success: function(data){
        				$("#jqxNotificationTimesheetInDay").jqxNotification('closeLast');
        				if(data._EVENT_MESSAGE_){
        					$("#jqxTimesheetAtt").jqxGrid('updatebounddata');
        					$("#jqxNotificationTimesheetInDayContent").text(data._EVENT_MESSAGE_);
        					$("#jqxNotificationTimesheetInDay").jqxNotification({template: 'info'});
        					$("#jqxNotificationTimesheetInDay").jqxNotification("open");
        					$("#jqxGridEmplTimekeepingSign").jqxGrid('updatebounddata');
        				}else{
        					$("#jqxNotificationTimesheetInDayContent").text(data._ERROR_MESSAGE_);
        					$("#jqxNotificationTimesheetInDay").jqxNotification({template: 'error'});
        					$("#jqxNotificationTimesheetInDay").jqxNotification("open");	
        				}
        			},
        			complete: function(jqXHR, status){
        				$("#updateEmplTimekeepingSignBtn").removeAttr("disabled");
        				$("#jqxGridEmplTimekeepingSign").jqxGrid('hideloadelement');
        			}
        		 });
        	 }
	    });
	   
	    /*=========================./end event define ===============================*/
	    initJqxInput();
	    initJqxCheckBox();	    
	    initJqxValidator();
	});
	
	function initJqxCheckBox(){
	    $("#checkImportData").jqxCheckBox({width: 50, height: 25, checked: false, theme: 'olbius', checked: true});
	}
	
	function initJqxInput(){
	    $("#emplTimesheetNameAdd").jqxInput({width: 247, height: 22, theme: 'olbius'});
	}
	
	function initJqxValidator(){
		$("#popupAddRow").jqxValidator({
		   	rules: [{
				input: '#emplTimesheetNameAdd',
				message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
				action: 'blur',
				rule: 'required'
			}],
		 });
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
	
	
	function initJqxWindow(){
		$("#PartyReCalcTimesheetWindow").jqxWindow({
			showCollapseButton: false, autoOpen: false, maxWidth: 400, minWidth: 400, maxHeight: 400, height: 400, width: 400, isModal: true, 
		    theme:'olbius', collapsed:false, theme: 'olbius',
		    initContent: function () {  
		    	
		    }
		});
		$("#PartyReCalcTimesheetWindow").on('close', function(event){
			$('#partyGroupReCalcTree').jqxTree('collapseAll');
		});
		var popup = $("#popupAddRow");
		var emplTimesheetAttendancePopup = $("#emplTimesheetAttendancePopup");
		var jqxWindowEmplTimesheetInDay = $("#jqxWindowEmplTimesheetInDay");
		popup.jqxWindow({
	        width: 500, height: 240, resizable: true, isModal: true, autoOpen: false, 
	        theme: 'olbius', modalZIndex: 11000   
	    });
		popup.on("open", function(event){
			$("#emplTimesheetNameAdd").val("");
			$("#fromDateJQ").val(previousFirstDate);
			$("#thruDateJQ").val(previousLastDate);
		});
		emplTimesheetAttendancePopup.jqxWindow({
			minWidth: "1024px",  width: "1024px", height: "545px",  maxHeight: "545px", resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
	    });
		//var jqxWindowEmplTimesheetInDayHeight = jqxGridEmplTimekeepingSign.jqxGrid('height') + 140;
		jqxWindowEmplTimesheetInDay.jqxWindow({
			width: 520, height: '533px',  maxHeight: '533px', resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
			initContent: function(){
				$("#jqxNotificationTimesheetInDay").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
			}
	    });
		
		$("#proposalApprvalTimesheet").jqxWindow({width: '400px', height: 125,  maxHeight: 125, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
			initContent: function(){
				
			}
		});
	}
	
	function initBtnEvent(){
		$("#btnCancel").click(function(event){
			$("#PartyReCalcTimesheetWindow").jqxWindow('close');
		});
		
		$("#btnSave").click(function(event){
			$("#PartyReCalcTimesheetWindow").jqxWindow('close');
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
	        var emplTimesheetId = dataRecord.emplTimesheetId;
	        var itemTreeSelected = $("#partyGroupReCalcTree").jqxTree('getSelectedItem');
	        reCalcEmplTimesheet(emplTimesheetId, itemTreeSelected.value);
		});
		
		$("#alterSave").click(function(){
	    	if(!$('#popupAddRow').jqxValidator('validate')){
				return;
			}
	    	$(this).attr("disabled", "disabled");
	    	bootbox.dialog("${StringUtil.wrapString(uiLabelMap.CreateEmpTimesheetRemind)}",
	    			[
					{
					    "label" : "${uiLabelMap.CommonSubmit}",
					    "class" : "btn-primary btn-mini icon-ok",
					    "callback": function() {
					    	createEmplTimesheet();
					    	$("#alterSave").removeAttr("disabled");
					    }
					},
					{
					    "label" : "${uiLabelMap.CommonCancel}",
					    "class" : "btn-danger icon-remove btn-mini",
					    "callback": function() {
					    	$("#alterSave").removeAttr("disabled");
					    }
					}		
	    			]
	    	);
	    });
		
		$("#cancelSendTimesheet").click(function(){
	    	$("#proposalApprvalTimesheet").jqxWindow('close');
	    });
		
		$("#cancelUpdateBtn").click(function(){
			$("#jqxWindowEmplTimesheetInDay").jqxWindow('close');
	    });
	    $("#sendTimesheet").click(function(){
	    	var emplTimesheetId = $("#emplTimesheetId").val();
	    	if(emplTimesheetId){
	    		$("#sendTimesheet").attr('disabled','disabled');	
	    		jqxWindowEmplTimesheetInDay.jqxWindow('close');
	    		$.ajax({
	    			url: "proposalApprovalTimesheets",
	    			data:{emplTimesheetId: emplTimesheetId},
	    			type: 'POST',
	    			success: function(data){
//	    				console.log(1111);
	    				if(data._EVENT_MESSAGE_){
	    					$("#jqxNotifyEmplTimesheets").text(data._EVENT_MESSAGE_);
            				$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'info'})
            				$("#jqxNotifyEmplTimesheets").jqxNotification("open");
	    				}else{
	    					$("#jqxNotifyEmplTimesheets").text(data._ERROR_MESSAGE_);
            				$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'error'})
            				$("#jqxNotifyEmplTimesheets").jqxNotification("open");
	    				}
	    			},
	    			complete: function(){
	    				$("#sendTimesheet").removeAttr("disabled");	
	    				$("#proposalApprvalTimesheet").jqxWindow('close');
	    			}
	    		});
	    	}
	    });
	    $("#alterCancel").click(function(){
	    	$("#popupAddRow").jqxWindow('close');
		});
	}
	
	function createEmplTimesheet(){
		var check = $("#checkImportData").jqxCheckBox('checked');
    	var importDataTimeRecord = "N";
    	if(check){
    		importDataTimeRecord = "Y";
    	}
    	if(importDataTimeRecord == "N"){
    		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.TimesheetNotImportData)}. ${StringUtil.wrapString(uiLabelMap.AreYouSure)}?", 
    		[{
    		    "label" : "${uiLabelMap.CommonSubmit}",
    		    "class" : "btn-primary btn-mini icon-ok",
    		    "callback": function() {
    		    	addNewRowTimesheets(importDataTimeRecord, $("#alterSave"));
    		    	popup.jqxWindow('close');
    		    }
    		},
    		{
    		    "label" : "${uiLabelMap.CommonCancel}",
    		    "class" : "btn-danger icon-remove btn-mini",
    		    "callback": function() {
    		    	$("#alterSave").removeAttr("disabled");
    		    }
    		}]);
    	}else{
    		addNewRowTimesheets(importDataTimeRecord);
    		$("#popupAddRow").jqxWindow('close');
    	}
	}
	
	function initContextMenu(){
		var liElement = $("#contextMenu>ul>li").length;
		var contextMenuHeight = 30 * liElement; 
		$("#contextMenu").jqxMenu({ width: 280, height: contextMenuHeight, autoOpenPopup: false, mode: 'popup' , theme:'olbius'});
		
		$("#contextMenu").on('itemclick', function (event) {
			//var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            checkEmplTimesheetStatus(dataRecord, event);
		});
	}
	
	function initJqxDropDownTreeBtn(){
		var dropdownButton = $("#jqxDropDownButton");
		var jqxTreeDiv = $("#jqxTree");
		var idSuffix = "jqxTree";
		dropdownButton.jqxDropDownButton({ width: '350px', height: 25, theme: 'olbius'});
		var dataTree = new Array();
		<#list treePartyGroup as tree>
			var row = {};
			row["id"] = "${tree.id}_" + idSuffix;
			row["text"] = "${tree.text}";
			row["parentId"] = "${tree.parentId}_" + idSuffix;
			row["value"] = "${tree.idValueEntity}";
			dataTree[${tree_index}] = row;
		</#list>
		var sourceTree =
		{
		    datatype: "json",
		    datafields: [
		    	{ name: 'id'},
		        { name: 'parentId'},
		        { name: 'text'} ,
		        { name: 'value'}
		    ],
		    id: 'id',
		    localdata: dataTree
		};
		var dataAdapterTree = new $.jqx.dataAdapter(sourceTree);
		// perform Data Binding.
		dataAdapterTree.dataBind();
		var recordsTree = dataAdapterTree.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
		jqxTreeDiv.jqxTree({source: recordsTree,width: "350px", height: "240px", theme: 'olbius'});
		<#if expandedList?has_content>
		 	<#list expandedList as expandId>
		 		jqxTreeDiv.jqxTree('expandItem', $("#${expandId}_" + idSuffix)[0]);
		 		jqxTreeDiv.jqxTree('selectItem', $("#${expandId}_" + idSuffix)[0]);
		 	</#list>
		 </#if>    
		 <#if expandedList?has_content>
		 	<#assign defaultPartyId = expandedList.get(expandedList?size - 1)>
		 	var initElement = $("#${expandedList.get(0)}_" + idSuffix)[0];
		 	setDropdownContent(initElement, jqxTreeDiv, dropdownButton);
		 </#if>
		 jqxTreeDiv.on('select', function(event){
	    	var id = event.args.element.id;
	    	var item = jqxTreeDiv.jqxTree('getItem', args.element);
	    	setDropdownContent(item, jqxTreeDiv, dropdownButton);
	        
			var tmpS = $("#jqxTimesheetAtt").jqxGrid('source');
			var emplTimesheetId = tmpS._source.data.emplTimesheetId;
			var value = jqxTreeDiv.jqxTree('getItem', $("#"+id)[0]).value;
			tmpS._source.data = {emplTimesheetId: emplTimesheetId, partyGroupId: value};			 
			$("#jqxTimesheetAtt").jqxGrid('source', tmpS);
	     });
	}
	
	
	function executeTimesheetActInMenu(dataRecord, event, status){
		var args = event.args;
		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
        var emplTimesheetId = dataRecord.emplTimesheetId;
        var fromDate = new Date(dataRecord.fromDate); 
        var thruDate = new Date(dataRecord.thruDate); 
        if(status && status == "EMPL_TS_CALC"){
        	bootbox.dialog("${StringUtil.wrapString(uiLabelMap.EmplTimesheetIsCalculating)}",
       			[
					{
					    "label" : "${uiLabelMap.CommonSubmit}",
					    "class" : "btn-primary btn-mini icon-ok",
					    "callback": function() {
					    }
					}, 
				]
        	);
        	return;
        }
       	if($(args).attr("action") == 'refresh'){
       		openJqxWindow($("#PartyReCalcTimesheetWindow")); 	  
        }else if($(args).attr("action") == 'approvalWorkOvertime'){
        	updateApprovalWorkOvertime(fromDate, thruDate);<#--//function is defined file hrmMacroLib.ftl in <#macro renderEmplWorkOverTime />-->
        }else if($(args).attr("action") == 'overallEmplTimesheet'){
        	overallEmplTimesheets(emplTimesheetId);<#--// function is defined in file hrmMacroLib.ftl in <#macro renderEmplTimesheetOverview-->
        }else if($(args).attr("action") == 'emplWorkingLate'){
        	overallEmplWorkingLate(emplTimesheetId);<#--//function is defined file hrmMacroLib.ftl in <#macro renderEmplWorkingLate />-->
        }else if($(args).attr("action") == 'sendApprovalTimesheets'){
        	sendApprovalTimesheets(emplTimesheetId);
        }else if($(args).attr("action") == 'timeRecorderDetails'){
        	showTimesheetDetails(dataRecord, dataAdapterTimesheet, $("#emplTimesheetAttendancePopup"));
        }
	}
	
	function reCalcEmplTimesheet(emplTimesheetId, partyGroupId){
		$('#jqxgrid').jqxGrid({'disabled': true});
   		$('#jqxgrid').jqxGrid('showloadelement');
   		$("#jqxNotifyEmplTimesheets").jqxNotification('closeLast');
    	$.ajax({
    		url: "reimportTimesheetDataFromTimeRecord",
    		data: {emplTimesheetId: emplTimesheetId, partyGroupId: partyGroupId},
    		type: 'POST',
    		success:function(data){
    			if(data._EVENT_MESSAGE_){
    				$("#jqxNotifyEmplTimesheets").html(data._EVENT_MESSAGE_);
    				$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'info'})
    				$("#jqxNotifyEmplTimesheets").jqxNotification("open");
    				//will call service update of jqx, need fix this
    				$("#jqxgrid").jqxGrid('setcellvaluebyid', emplTimesheetId, "statusId", "EMPL_TS_CALC");
    			}else{
    				$("#jqxNotifyEmplTimesheets").html(data._ERROR_MESSAGE_);
    				$("#jqxNotifyEmplTimesheets").jqxNotification({template: 'error'})
    				$("#jqxNotifyEmplTimesheets").jqxNotification("open");
    			}
    		},
    		complete: function(jqXHR, status){
    			$('#jqxgrid').jqxGrid({'disabled': false});	
    			$('#jqxgrid').jqxGrid('hideloadelement');
    		}
    	});  
	}
	
	function checkEmplTimesheetStatus(dataRecord, event){
		var emplTimesheetId = dataRecord.emplTimesheetId;
		var statusId = dataRecord.statusId;
		$.ajax({
			url: 'getEmplTimesheetStatus',
			data: {emplTimesheetId: emplTimesheetId},
			type: 'POST',
			success: function(data){
				if(data.responseMessage == "success"){
					var newstatusId = data.statusId;
					if(newstatusId != status){
						$("#jqxgrid").jqxGrid('setcellvaluebyid', emplTimesheetId, "statusId", newstatusId);
						statusId = newstatusId
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				
			},
			complete: function (jqXHR, status){
				executeTimesheetActInMenu(dataRecord, event, statusId);
			}
		});
	}
	
	function addNewRowTimesheets(importDataTimeRecord){
		var row={
	   		emplTimesheetName: $("#emplTimesheetNameAdd").val(),
	   		customTimePeriodId: $("#monthCustomTime").val(),
	   		importDataTimeRecord: importDataTimeRecord
	   	};
	   /* 	$('#jqxgrid').jqxGrid({'disabled': true});
	   	$("#jqxgrid").jqxGrid('showloadelement'); */
	   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	}
	
	function functionAfterRowComplete(){
		$('#jqxgrid').jqxGrid({'disabled': false});
		$('#jqxgrid').jqxGrid('hideloadelement');
		$("#alterSave").removeAttr("disabled");
	}
	 
	function sendApprovalTimesheets(emplTimesheetId){
		$("#emplTimesheetId").val(emplTimesheetId);
		$("#proposalApprvalTimesheet").jqxWindow('open');
	}
	function setDropdownContent(element, jqxTree, dropdownBtn){
		var item = jqxTree.jqxTree('getItem', element);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	}
	
	function showTimesheetDetails(data, dataAdapterTimesheet, emplTimesheetAttendancePopup){
		var emplTimesheetId = data.emplTimesheetId;
		var fromDate = new Date(data.fromDate);
		var thruDate = new Date(data.thruDate);
		var dataFieldTimesheet = new Array();
		var columnsTimesheet = new Array();
		dataFieldTimesheet.push({"name": "partyId", "type": "string"});
		dataFieldTimesheet.push({"name": "emplTimesheetId", "type": "string"});
		dataFieldTimesheet.push({"name": "partyName", "type": "string"});
		columnsTimesheet.push({'datafield': 'emplTimesheetId', 'width': 100, 'cellsalign': 'left', 'hidden': true});
		columnsTimesheet.push({'text': '${uiLabelMap.EmployeeId}', 'datafield': 'partyId', 'width': 100, 'cellsalign': 'left', 'editable': false, 'pinned': true});
		columnsTimesheet.push({'text': '${uiLabelMap.EmployeeName}', 'datafield': 'partyName', 'cellsalign': 'left', 'editable': false, 'pinned': true});
		
		while(fromDate.getTime() <= thruDate.getTime()){
			var date = fromDate.getDate() + "/" + fromDate.getMonth() + "/" + fromDate.getFullYear();
			var textDate = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + " - " + weekday[fromDate.getDay()];
			dataFieldTimesheet.push({"name": date, "type": "string"});
			dataFieldTimesheet.push({"name": date + "_hours", "type": "string"});
		
			columnsTimesheet.push({datafield: date + "_hours", hidden: true});
			columnsTimesheet.push({"text": textDate, datafield: date, width: 85, cellsalign: 'center',
										cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
											var valueRet = "";
											//"value" is array type and have format like that [emplTimekeepingSignId1, emplTimekeepingSignId2, ...] 
											if(value){
												//console.log(value);
												for(var j = 0; j < value.length; j++){
													for(var i = 0; i < emplTimekeepingSignArr.length; i++){
														if(emplTimekeepingSignArr[i].emplTimekeepingSignId == value[j]){
															valueRet += emplTimekeepingSignArr[i].sign;
														}
													}
													if(j < value.length - 1){
														valueRet += ", ";
													}
												}
												return '<div style="text-align:center">' +valueRet + '</div>';
											}else{
												return value;
											}
										}
								  });
			fromDate.setDate(fromDate.getDate() + 1); 
		}
		var itemTreeSelect = $("#jqxTree").jqxTree('getSelectedItem');
		if(itemTreeSelect){
			var partyGroupId = itemTreeSelect.value; 
			$("#jqxTimesheetAtt").jqxGrid('columns', columnsTimesheet);
			var sourceTimesheetAtt = $("#jqxTimesheetAtt").jqxGrid('source');
			sourceTimesheetAtt._source.data = {emplTimesheetId: emplTimesheetId, partyGroupId: partyGroupId};
			sourceTimesheetAtt._source.datafields = dataFieldTimesheet;
			$("#jqxTimesheetAtt").jqxGrid('source', dataAdapterTimesheet);
		}
		//$("#jqxTimesheetAtt").jqxGrid('updatebounddata');
		emplTimesheetAttendancePopup.jqxWindow('open');
	}
	
	function openJqxWindow(jqxWindowDiv){
		var wtmp = window;
		var tmpwidth = jqxWindowDiv.jqxWindow('width');
		jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
		jqxWindowDiv.jqxWindow('open');
	}
	
	function jqxTreePartyListSelect(event){
		
	}
</script>

<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="partyGroupReCalcTree" setDropdownContentJsFunc="setDropdownContent"
	jqxTreeSelectFunc="jqxTreePartyListSelect" isDropDown="false" width="99%" height="300" expandAll="false"/>

<div class="row-fluid" id="jqxNotifyEmplTimesheetContainer">
	<div id="jqxNotifyEmplTimesheets"></div>
</div>
<@jqGrid url="jqxGeneralServicer?sname=JQGetEmplTimesheets" dataField=datafield columnlist=columnlist
	editable="true" 
	editrefresh ="true"
	editmode="click" id="jqxgrid" mouseRightMenu="true" contextMenuId="contextMenu"
	showtoolbar = "true" deleterow="true" jqGridMinimumLibEnable="false" sourceId="emplTimesheetId"
	removeUrl="jqxGeneralServicer?sname=deleteEmplTimesheet&jqaction=D" deleteColumn="emplTimesheetId" functionAfterRowComplete="functionAfterRowComplete()"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createEmplTimesheets" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="emplTimesheetName;customTimePeriodId;importDataTimeRecord" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplTimesheets" editColumns="emplTimesheetId;emplTimesheetName"
/>

<@htmlTemplate.renderEmplWorkOverTime id="jqxgridEmplWorkOvertime" updaterow="false" jqxNotifyId="jqxNotifyEmplWorkOvertime" jqxGridInWindow="true" width="1005px" height="476"/> 
<@htmlTemplate.renderEmplWorkingLate id="jqxgridEmplWorkingLate" updaterow="false" height="97%" jqxNotifyId="jqxNotifyEmplWorkingLate" jqxGridInWindow="true"/> 
<@htmlTemplate.renderEmplTimesheetOverview id="jqxEmplTimesheetGeneral" updaterow="false" jqxGridInWindow="true" height="476px"/>
<script type="text/javascript">
$(document).ready(function () {
	$("#jqxgrid").on("bindingcomplete", function (event){
		<#if parameters.emplTimesheetId?has_content>
			
		</#if>
	});
});
</script>
<div id="proposalApprvalTimesheet" style="display: none;">
	<div>${uiLabelMap.SendApprovalTimesheets}</div>
	<div class="row-fluid">
		<div class="span12">
			<input type="hidden" name="emplTimesheetId" id="emplTimesheetId">
			<span><b>${uiLabelMap.CannotEditDataAfterSend}. ${uiLabelMap.AreYouSureSent}</b></span>
		</div>
		<div class="span11" style="margin-top: 15px; text-align: center; margin-bottom: 0px;">
			<button class="btn btn-mini btn-primary icon-ok" id="sendTimesheet">${uiLabelMap.CommonSubmit}</button>
			<button class="btn btn-mini btn-danger icon-remove" id="cancelSendTimesheet">${uiLabelMap.CommonCancel}</button>
		</div>
	</div>
</div>
<div id="emplTimesheetAttendancePopup" style="display: none;">
	<div>${uiLabelMap.EmplTimesheetAttendance}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="span7" style="text-align: right; margin-top: 4px">
					<b>${uiLabelMap.CommonDepartment}</b>
				</div>
				<div class="span5">
					<div id="jqxDropDownButton">
						<div style="border: none;" id="jqxTree">
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div id="jqxTimesheetAtt"></div>
				</div>
			</div>
		</div>
		
	</div>
</div>
<div id="jqxWindowEmplTimesheetInDay" style="display: none;">
	<div>${uiLabelMap.EmplTimesheetInDay}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid form-window-content" style="height: 430px; overflow-y: auto; overflow-x: hidden; ">
			<div class="row-fluid">
				<div id="notifyContainer">
					<div id="jqxNotificationTimesheetInDay">
						<div id="jqxNotificationTimesheetInDayContent"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid form-horizontal">
				<div class="row-fluid">
					<div class="span12">
						<span><b>${uiLabelMap.EmployeeName}</b></span>: <span id="partyName"></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="span12">
						<div class="span5">
							<span><b>${uiLabelMap.HRCommonInTime}</b></span>: <span id="startTimeIn"></span>
						</div>
						<div class="span5">
							<span><b>${uiLabelMap.HRCommonOutTime}</b></span>: <span id="endTimeOut"></span>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid" id="jqxGridEmplTimekeepingSign"></div>
		</div>
		<div class="form-action">
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="cancelUpdateBtn" class="btn btn-danger btn-mini icon-remove form-action-button pull-right">${uiLabelMap.CommonCancel}</button>
					<button id="updateEmplTimekeepingSignBtn" class="btn btn-primary btn-mini icon-ok form-action-button pull-right">${uiLabelMap.CommonUpdate}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id='popupAddRow' class="hide">
	<div>${uiLabelMap.AddEmplTimesheet}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form name="popupAddRow" action="" class="form-horizontal">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.EmplTimesheetName}
						</label>
					</div>
					<div class="span7">
						<input type="text" name="emplTimesheetNameAdd" id="emplTimesheetNameAdd">
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
						<label>${uiLabelMap.ImportDataFromTimeRecorder}</label>
					</div>
		   			<div class="span7">
		   				<div style="margin-left: 16px; margin-top: 4px">
			   				<div id="checkImportData"></div>
		   				</div>
		   			</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div id="PartyReCalcTimesheetWindow" class="hide">
		<div>
			${StringUtil.wrapString(uiLabelMap.PartyGroupReCalcTimesheet)}
		</div>
		<div>
			<div class="form-window-container">
				<div id="partyGroupReCalcTree">
				</div>
			</div>
			<div class="form-action">
				<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSubmit}</button>
			</div>
		</div>	
	</div>
</div>
<div id='contextMenu' style="display: none">
      <ul>
          <li action="overallEmplTimesheet">
          	${uiLabelMap.EmplTimekeepingReportTilte}
          </li>
          <li action="timeRecorderDetails">
          	${uiLabelMap.EmplTimesheetAttendance}
          </li>
          <li action="emplWorkingLate">
          	${uiLabelMap.HREmplWorkingLateList}
          </li>
          <li action="approvalWorkOvertime">
          	${uiLabelMap.ListEmplWorkOvertime}
          </li>
          <!-- <li action="sendApprovalTimesheets">
          	${uiLabelMap.SendApprovalTimesheets}
          </li> -->
          <li action="refresh">
          	${uiLabelMap.RefreshEmplTimesheets}
          </li>          
     </ul>
  </div>