selectedApplData = new Array();
$("#sendEmailWindow").jqxWindow({
    showCollapseButton: false, maxHeight: 1000,modalZIndex: 1000, autoOpen: false, maxWidth: "90%", height: 580, minWidth: '40%', width: "70%", isModal: true,
    theme:theme, collapsed:false,
    initContent: function () {
    	// Create jqxTabs.
        $('#jqxSendEmailTabs').jqxTabs({ width: '98%',theme: theme, height: 480, position: 'top',disabled:true,
        	initTabContent:function (tab) {
        		if(tab == 0){
        			var source =
                    {
                        localdata: selectedApplData,
                        datatype: "array",
                        datafields:
                        [
                        	{ name: 'firstName', type: 'string' },
							{ name: 'middleName', type: 'string' },
							{ name: 'lastName', type: 'string' },
							{ name: 'partyId', type: 'string'},
							{ name: 'emailAddress', type: 'string'},
                        ]
                    };
                	var dataAdapter = new $.jqx.dataAdapter(source);
                    
                	$("#jqxSelectedApplGrid").jqxGrid(
                    {
                        width: 845,
                        source: dataAdapter,
                        columnsresize: true,
                        pageable: true,
                        autoheight: true,
                        selectionmode: 'checkbox',
                        columns: [
                      	  { text: '${uiLabelMap.CommonId}', datafield: 'partyId'},
                      	  { text: '${uiLabelMap.fullName}',
                      		 cellsrenderer: function(row, column, value){
                      			 var rowData = $("#jqxSelectedApplGrid").jqxGrid('getrowdata', row);
                      			 return '<span>' + rowData['lastName'] + ' ' + rowData['middleName'] + ' ' + rowData['firstName'] + '</span>'
                      		 }
                      	  },
                      	  { text: '${uiLabelMap.emailAddress}', datafield: 'emailAddress'}
                        ]
                    });
        		}else{
        			$("#titleEmail").jqxInput({width: 695});
        			//Party Grid
                	var sourceEmailTemplate =
                	{
                			datafields:
                				[
                				 { name: 'emailTemplateSettingId', type: 'string' },
                				 { name: 'subject', type: 'string' },
                				 { name: 'description', type: 'string' }
                				],
                			cache: false,
                			root: 'results',
                			datatype: "json",
                			updaterow: function (rowid, rowdata) {
                				// synchronize with the server - send update command   
                			},
                			beforeprocessing: function (data) {
                				sourceEmailTemplate.totalrecords = data.TotalRows;
                			},
                			filter: function () {
                				// update the grid and send a request to the server.
                				$("#jqxgridTemplateEmail").jqxGrid('updatebounddata');
                			},
                			pager: function (pagenum, pagesize, oldpagenum) {
                				// callback called when a page or page size is changed.
                			},
                			sort: function () {
                				$("#jqxgridTemplateEmail").jqxGrid('updatebounddata');
                			},
                			sortcolumn: 'emailTemplateSettingId',
                			sortdirection: 'asc',
                			type: 'POST',
                			data: {
                				noConditionFind: 'Y',
                				conditionsFind: 'N',
                				workEffortTypeId: '${currentWorkEffort.workEffortTypeId}',
                				statusId: '${currentWorkEffort.workEffortTypeId}'
                			},
                			pagesize:15,
                			contentType: 'application/x-www-form-urlencoded',
                			url: 'jqxGeneralServicer?sname=getListEmailTemplates',
                	};
                	var dataAdapterParty = new $.jqx.dataAdapter(sourceEmailTemplate);
                	$("#templateEmail").jqxDropDownButton({ width: 400, height: 25});
                	$("#jqxgridTemplateEmail").jqxGrid({
                		source: dataAdapterParty,
                		filterable: true,
                		showfilterrow: true,
                		virtualmode: true, 
                		sortable:true,
                		theme: theme,
                		editable: false,
                		autoheight:true,
                		pageable: true,
                		width: 800,
                		rendergridrows: function(obj)
                		{
                			return obj.data;
                		},
                	columns: [
                	  { text: '${uiLabelMap.CommonId}', datafield: 'emailTemplateSettingId', filtertype: 'input', width: 150},
                	  { text: '${uiLabelMap.subject}', datafield: 'subject', filtertype: 'input'},
                	  { text: '${uiLabelMap.description}', datafield: 'description', filtertype: 'input'},
                	]
                	});
                	$("#jqxgridTemplateEmail").on('rowselect', function (event) {
                		var args = event.args;
                		var row = $("#jqxgridTemplateEmail").jqxGrid('getrowdata', args.rowindex);
                		selectedtemplateEmail = row['emailTemplateSettingId'];
                		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['description'] +'</div>';
                		$('#templateEmail').jqxDropDownButton('setContent', dropDownContent);
                		$("#titleEmail").val(row['subject']);
                		var templateUrl = "getFirstInterviewInvitionEmail";
                		if(row['emailTemplateSettingId'] == 'FIRST_INT_INVIT'){
                			templateUrl = "getFirstInterviewInvitionEmail";
                		}else if(row['emailTemplateSettingId'] == 'SECOND_INT_INVIT'){
                			templateUrl = "getSecondInterviewInvitionEmail";
                		}else if(row['emailTemplateSettingId'] == 'INTERVIEW_NOTI'){
                			templateUrl = "getResultNotificationEmail";
                		}
                		
                		$.ajax({
                			url: templateUrl,
                			type: "POST",
                			async: false,
                			success : function(data) {
                				$("#contentEmail").val(data);
                			}
                		});
                		$("#templateEmail").jqxDropDownButton('close');
                		
                	});
        			$("#contentEmail").jqxEditor({
                        height: "300px",
                        width: '800px',
                        theme: theme
                    });
        		}
        	}
        });
        $("#jqxSendEmailTabs").jqxTabs('enableAt', 0);
    }
});
$('#jqxWindow').on('open', function (event) {
	$("#jqxSelectedApplGrid").jqxGrid('updatebounddata');
});
$(".email-next").click(function(){
	var selectedItem = $("#jqxSendEmailTabs").jqxTabs('selectedItem');
	$("#jqxSendEmailTabs").jqxTabs('disableAt', selectedItem);
	$("#jqxSendEmailTabs").jqxTabs('enableAt', selectedItem + 1);
	$("#jqxSendEmailTabs").jqxTabs('next');
});

$(".email-back").click(function(){
	var selectedItem = $("#jqxSendEmailTabs").jqxTabs('selectedItem');
	$("#jqxSendEmailTabs").jqxTabs('enableAt', selectedItem - 1);
	$("#jqxSendEmailTabs").jqxTabs('disableAt', selectedItem);
	$('#jqxSendEmailTabs').jqxTabs('previous');
});

$("#emailSubmit").click(function(){
	var rowindexes = $('#jqxSelectedApplGrid').jqxGrid('getselectedrowindexes');
	var listApplicant = new Array();
	for(var i = 0; i < rowindexes.length; i++){
		var rowData = $('#jqxSelectedApplGrid').jqxGrid('getrowdata', rowindexes[i]);
		var row = {};
		row['partyId'] = rowData['partyId'];
		row['emailAddress'] = rowData['emailAddress'];
		listApplicant[i] = row;
	}
	$.ajax({
		url: 'sendEmailToAppl',
		type: "POST",
		data: {listApplicant: JSON.stringify(listApplicant), workEffortId:'${parameters.workEffortId}', emailTemplateSettingId:selectedtemplateEmail},
		dataType: 'json',
		async: false,
		success : function(data) {
			if(data.responseMessage == 'success'){
				$("#sendEmailWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
			}
		}
	});
});
            