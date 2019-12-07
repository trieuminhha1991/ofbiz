var fmData = new Array();
	var fmIndex = 0;
	
	var eduData = new Array();
	var eduIndex = 0;
	
	var wpData = new Array();
	var wpIndex = 0;
	
	var skillData = new Array();
	var skillIndex = 0;
	
	var aqcData = new Array();
	var aqcIndex = 0;
	/******************************************************Init Window**************************************************************************/
	$("#alterpopupNewApplicant").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "90%", height: 580, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 10000,
        theme:theme, collapsed:false,
        initContent: function () {
        	// Create jqxTabs.
            $('#jqxTabs').jqxTabs({ width: '98%', height: 480, position: 'top',disabled:true,
            	initTabContent:function (tab) {
	                if(tab == 2){
	                	var source =
	                    {
	                        localdata: fmData,
	                        datatype: "array",
	                        datafields:
	                        [
                            	{ name: 'firstName', type: 'string' },
								{ name: 'middleName', type: 'string' },
								{ name: 'lastName', type: 'string' },
								{ name: 'partyRelationshipTypeId', type: 'string' },
								{ name: 'birthDate', type: 'date' },
								{ name: 'occupation', type: 'string' },
								{ name: 'placeWork', type: 'string'},
								{ name: 'phoneNumber', type: 'string'},
								{ name: 'emergencyContact', type: 'string'}
	                        ]
	                    };
	                	var dataAdapter = new $.jqx.dataAdapter(source);
	                    
	                	$("#jqxgridFamily").jqxGrid(
	                    {
	                        width: 1040,
	                        source: dataAdapter,
	                        columnsresize: true,
	                        pageable: true,
	                        autoheight: true,
	                        showtoolbar: true,
	                        rendertoolbar: function (toolbar) {
	                            var container = $("<div id='toolbarcontainer' class='widget-header'>");
	                            toolbar.append(container);
	                            container.append('<h4></h4>');
	                            container.append('<button id="fmAddrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
	                            container.append('<button id="fmDelrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
			                    $("#fmAddrowbutton").jqxButton();
			                    $("#fmDelrowbutton").jqxButton();
	                            // create new row.
	                            $("#fmAddrowbutton").on('click', function () {
	                            	$("#createNewFamilyWindow").jqxWindow('open');
	                            });
	                            
	                            // create new row.
	                            $("#fmDelrowbutton").on('click', function () {
	                            	var selectedrowindex = $('#jqxgridFamily').jqxGrid('selectedrowindex'); 
	                            	fmData.splice(selectedrowindex, 1);
	                            	$('#jqxgridFamily').jqxGrid('updatebounddata'); 
	                            	
	                            });
	        	            },
	                        columns: [
                          	  { text: '${uiLabelMap.fullName}',
                          		 cellsrenderer: function(column, row, value){
                          			 var rowData = $("#jqxgridFamily").jqxGrid('getrowdata', row);
                          			 return '<span>' + rowData['lastName'] + ' ' + rowData['middleName'] + ' ' + rowData['firstName'] + '</span>'
                          		 }
                          	  },
	                          { text: '${uiLabelMap.HRRelationship}', datafield: 'partyRelationshipTypeId',
                          		cellsrenderer: function(column, row, value){
                         			 for(var i = 0; i < partyRelaTypeData.length; i++){
                         				 if(value == partyRelaTypeData[i].partyRelationshipTypeId){
                         					 return '<span>' + partyRelaTypeData[i].description + '<span>'
                         				 }
                         			 }
                         			 return '<span>' + value + '<span>';
                         		 }
	                          },
	                          { text: '${uiLabelMap.BirthDate}', datafield: 'birthDate', width: 150, cellsformat: 'dd/MM/yyyy'},
	                          { text: '${uiLabelMap.HROccupation}', datafield: 'occupation', width: 150},
	                          { text: '${uiLabelMap.HRPlaceWork}', datafield: 'placeWork', width: 150},
	                          { text: '${uiLabelMap.PhoneNumber}', datafield: 'phoneNumber', width: 150},
	                          { text: '${uiLabelMap.EmergencyContact}', datafield: 'emergencyContact', width: 150,
	                        	  cellsrenderer: function(column, row, value){
	                        		  if(value == 'Y') return '<span>' + '${uiLabelMap.CommonYes}' + '</span>'
	                        		  else return '<span>' + '${uiLabelMap.CommonNo}' + '</span>';
	                        	  }
	                          }
	                        ]
	                    });
	                }else if(tab == 3){
	                	var source =
	                    {
	                        localdata: eduData,
	                        datatype: "array",
	                        datafields:
	                        [
                            	{ name: 'schoolId', type: 'string' },
								{ name: 'majorId', type: 'string' },
								{ name: 'studyModeTypeId', type: 'string' },
								{ name: 'classificationTypeId', type: 'string' },
								{ name: 'educationSystemTypeId', type: 'date' },
								{ name: 'fromDate', type: 'date', other:'Timestamp' },
								{ name: 'thruDate', type: 'date', other:'Timestamp' }
	                        ]
	                    };
	                	var dataAdapter = new $.jqx.dataAdapter(source);
	                    
	                	$("#jqxgridEdu").jqxGrid(
	                    {
	                        width: 1040,
	                        source: dataAdapter,
	                        columnsresize: true,
	                        pageable: true,
	                        autoheight: true,
	                        showtoolbar: true,
	                        rendertoolbar: function (toolbar) {
	                            var container = $("<div id='toolbarcontainer' class='widget-header'>");
	                            toolbar.append(container);
	                            container.append('<h4></h4>');
	                            container.append('<button id="eduAddrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
	                            container.append('<button id="eduDelrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
			                    $("#eduAddrowbutton").jqxButton();
			                    $("#eduDelrowbutton").jqxButton();
	                            // create new row.
	                            $("#eduAddrowbutton").on('click', function () {
	                            	$("#createNewEduWindow").jqxWindow('open');
	                            });
	                            
	                            // create new row.
	                            $("#eduDelrowbutton").on('click', function () {
	                            	var selectedrowindex = $('#jqxgridEdu').jqxGrid('selectedrowindex'); 
	                            	eduData.splice(selectedrowindex, 1);
	                            	$('#jqxgridEdu').jqxGrid('updatebounddata'); 
	                            	
	                            });
	        	            },
	                        columns: [
                          	  { text: '${uiLabelMap.HRCollegeName}', datafield: 'schoolId', width: 150,
                          		 cellsrenderer: function(row, column, value){
                          			 for(var i = 0; i < schoolData.length; i++){
                          				 if(value == schoolData[i].schoolId){
                          					 return '<span title=' + value + '>' + schoolData[i].description + '</span>'
                          				 }
                          			 }
                          			 return '<span>' + value + '</span>'
                          		 }
                          	  },
	                          { text: '${uiLabelMap.HRSpecialization}', datafield: 'majorId', width: 150,
                          		 cellsrenderer: function(row, column, value){
                          			 for(var i = 0; i < majorData.length; i++){
                          				 if(value == majorData[i].majorId){
                          					 return '<span title=' + value + '>' + majorData[i].description + '</span>'
                          				 }
                          			 }
                          			 return '<span>' + value + '</span>'
                          		 }  
	                          },
	                          { text: '${uiLabelMap.HROlbiusTrainingType}', datafield: 'studyModeTypeId', width: 150,
                          		 cellsrenderer: function(row, column, value){
                          			 for(var i = 0; i < studyModeTypeData.length; i++){
                          				 if(value == studyModeTypeData[i].studyModeTypeId){
                          					 return '<span title=' + value + '>' + studyModeTypeData[i].description + '</span>'
                          				 }
                          			 }
                          			 return '<span>' + value + '</span>'
                          		 }
	                          },
	                          { text: '${uiLabelMap.HRCommonClassification}', datafield: 'classificationTypeId',
	                        	  cellsrenderer: function(row, column, value){
                          			 for(var i = 0; i < classificationTypeData.length; i++){
                          				 if(value == classificationTypeData[i].classificationTypeId){
                          					 return '<span title=' + value + '>' + classificationTypeData[i].description + '</span>'
                          				 }
                          			 }
                          			 return '<span>' + value + '</span>'
                          		 }
	                          },
	                          { text: '${uiLabelMap.HRCommonSystemEducation}', datafield: 'educationSystemTypeId', width: 150,
	                        	  cellsrenderer: function(row, column, value){
                          			 for(var i = 0; i < educationSystemTypeData.length; i++){
                          				 if(value == educationSystemTypeData[i].educationSystemTypeId){
                          					 return '<span title=' + value + '>' + educationSystemTypeData[i].description + '</span>'
                          				 }
                          			 }
                          			 return '<span>' + value + '</span>'
                          		 }
	                          },
	                          { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy'},
	                          { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy'}
	                        ]
	                    });
	                }else if(tab == 4){
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
								{ name: 'jobDescription', type: 'date' },
								{ name: 'payroll', type: 'string' },
								{ name: 'terminationReasonId', type: 'string'},
								{ name: 'rewardDiscrip', type: 'string'}
	                        ]
	                    };
	                	var dataAdapter = new $.jqx.dataAdapter(source);
	                    
	                	$("#jqxgridWP").jqxGrid(
	                    {
	                        width: 1040,
	                        source: dataAdapter,
	                        columnsresize: true,
	                        pageable: true,
	                        autoheight: true,
	                        showtoolbar: true,
	                        rendertoolbar: function (toolbar) {
	                            var container = $("<div id='toolbarcontainer' class='widget-header'>");
	                            toolbar.append(container);
	                            container.append('<h4></h4>');
	                            container.append('<button id="wpAddrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
	                            container.append('<button id="wpDelrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
			                    $("#wpAddrowbutton").jqxButton();
			                    $("#wpDelrowbutton").jqxButton();
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
	                }else if(tab == 5){
	                	var source =
	                    {
	                        localdata: skillData,
	                        datatype: "array",
	                        datafields:
	                        [
                            	{ name: 'skillTypeId', type: 'string' },
								{ name: 'skillLevel', type: 'string' }
	                        ]
	                    };
	                	var dataAdapter = new $.jqx.dataAdapter(source);
	                    
	                	$("#jqxgridSkill").jqxGrid(
	                    {
	                        width: 1040,
	                        source: dataAdapter,
	                        columnsresize: true,
	                        pageable: true,
	                        autoheight: true,
	                        showtoolbar: true,
	                        rendertoolbar: function (toolbar) {
	                            var container = $("<div id='toolbarcontainer' class='widget-header'>");
	                            toolbar.append(container);
	                            container.append('<h4></h4>');
	                            container.append('<button id="skillAddrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
	                            container.append('<button id="skillDelrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
			                    $("#skillAddrowbutton").jqxButton();
			                    $("#skillDelrowbutton").jqxButton();
	                            // create new row.
	                            $("#skillAddrowbutton").on('click', function () {
	                            	$("#createNewSkillWindow").jqxWindow('open');
	                            });
	                            
	                            // create new row.
	                            $("#skillDelrowbutton").on('click', function () {
	                            	var selectedrowindex = $('#jqxgridSkill').jqxGrid('selectedrowindex'); 
	                            	skillData.splice(selectedrowindex, 1);
	                            	$('#jqxgridSkill').jqxGrid('updatebounddata'); 
	                            	
	                            });
	        	            },
	                        columns: [
                          	  { text: '${uiLabelMap.skillTypeId}', datafield: 'skillTypeId', width: 150},
	                          { text: '${uiLabelMap.skillLevel}', datafield: 'skillLevel'}
	                        ]
	                    });
	                }else if(tab == 6){
	                	// Create jqxSpecialSkillEditor
	                	$("#jqxSpecialSkillEditor").jqxEditor({
	                        width: 1040,
	                        tools: "bold italic underline | font size | left center right | outdent indent"
	                    });
	                }else if(tab == 7){
	                	$("#badHealth").jqxCheckBox({ width: 50, height: 25});
	                	$("#badHealthDetail").jqxEditor({
	                		width: 780,
	                        tools: "bold italic underline | font size | left center right | outdent indent"
	                    });
	                	
	                	$("#badInfo").jqxCheckBox({ width: 50, height: 25});
	                	$("#badInfoDetail").jqxEditor({
	                        width: 780,
	                        tools: "bold italic underline | font size | left center right | outdent indent"
	                    });
	                }else if(tab == 8){
	                	var source =
	                    {
	                        localdata: aqcData,
	                        datatype: "array",
	                        datafields:
	                        [
                            	{ name: 'firstName', type: 'string' },
								{ name: 'middleName', type: 'string' },
								{ name: 'lastName', type: 'string' },
								{ name: 'partyRelationshipTypeId', type: 'string' },
								{ name: 'birthDate', type: 'date' },
								{ name: 'occupation', type: 'string' },
								{ name: 'placeWork', type: 'string'},
								{ name: 'phoneNumber', type: 'string'},
								{ name: 'knowFor', type: 'string'}
	                        ]
	                    };
	                	var dataAdapter = new $.jqx.dataAdapter(source);
	                    
	                	$("#jqxgridAcq").jqxGrid(
	                    {
	                        width: 1040,
	                        source: dataAdapter,
	                        columnsresize: true,
	                        pageable: true,
	                        autoheight: true,
	                        showtoolbar: true,
	                        rendertoolbar: function (toolbar) {
	                            var container = $("<div id='toolbarcontainer' class='widget-header'>");
	                            toolbar.append(container);
	                            container.append('<h4></h4>');
	                            container.append('<button id="aqcAddrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
	                            container.append('<button id="aqcDelrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
			                    $("#aqcAddrowbutton").jqxButton();
			                    $("#aqcDelrowbutton").jqxButton();
	                            // create new row.
	                            $("#aqcAddrowbutton").on('click', function () {
	                            	$("#createNewAcquaintanceWindow").jqxWindow('open');
	                            });
	                            
	                            // create new row.
	                            $("#aqcDelrowbutton").on('click', function () {
	                            	var selectedrowindex = $('#jqxgridAcq').jqxGrid('selectedrowindex'); 
	                            	aqcData.splice(selectedrowindex, 1);
	                            	$('#jqxgridAcq').jqxGrid('updatebounddata'); 
	                            	
	                            });
	        	            },
	                        columns: [
                          	  { text: '${uiLabelMap.fullName}',
                          		 cellsrenderer: function(column, row, value){
                          			 var rowData = $("#jqxgridAcq").jqxGrid('getrowdata', row);
                          			 return '<span>' + rowData['lastName'] + ' ' + rowData['middleName'] + ' ' + rowData['firstName'] + '</span>'
                          		 }
                          	  },
                          	{ text: '${uiLabelMap.HRRelationship}', datafield: 'partyRelationshipTypeId',
                            		cellsrenderer: function(column, row, value){
                           			 for(var i = 0; i < friendTypeData.length; i++){
                           				 if(value == friendTypeData[i].partyRelationshipTypeId){
                           					 return '<span>' + friendTypeData[i].description + '<span>'
                           				 }
                           			 }
                           			 return '<span>' + value + '<span>';
                           		 }
  	                          },
  	                          { text: '${uiLabelMap.BirthDate}', datafield: 'birthDate', width: 150, cellsformat: 'dd/MM/yyyy'},
	                          { text: '${uiLabelMap.HROccupation}', datafield: 'occupation', width: 150},
	                          { text: '${uiLabelMap.HRPlaceWork}', datafield: 'placeWork', width: 150},
	                          { text: '${uiLabelMap.PhoneNumber}', datafield: 'phoneNumber', width: 150},
	                          { text: '${uiLabelMap.knowFor}', datafield: 'knowFor', width: 150}
	                        ]
	                    });
	                }else if(tab == 9){
	                	if($("#jqxOverviewEditor").length > 0){
	                		$("#jqxOverviewEditor").jqxEditor({});
	                	}else{
	                		$("#jiInductedStartDate").jqxDateTimeInput({});
	                		$("#jiInductedCompletionDate").jqxDateTimeInput({});
	                		$("#jiEmplPositionTypeId").jqxDropDownList({source: allPositionTypeData, valueMember: 'emplPositionTypeId', displayMember: 'description'});
	                		$("#jiBasicSalary").jqxNumberInput({spinButtons: true, decimalDigits: 0});
	                		$("#jiTrafficAllowance").jqxNumberInput({spinButtons: true, decimalDigits: 0});
	                		$("#jiPhoneAllowance").jqxNumberInput({spinButtons: true, decimalDigits: 0});
	                		$("#jiOtherAllowance").jqxNumberInput({spinButtons: true, decimalDigits: 0});
	                		$("#jiPercentBasicSalary").jqxNumberInput({spinButtons: true, decimalDigits: 0, inputMode: 'simple'});
	                		//Create jiJqxGridRepPartyIdFrom
	                    	var sourceGroup2 =
	                    	{
	                    			datafields:
	                    				[
	                    				 { name: 'partyId', type: 'string' },
	                    				 { name: 'groupName', type: 'string' },
	                    				],
	                    			cache: false,
	                    			root: 'results',
	                    			datatype: "json",
	                    			updaterow: function (rowid, rowdata) {
	                    				// synchronize with the server - send update command   
	                    			},
	                    			beforeprocessing: function (data) {
	                    				sourceGroup2.totalrecords = data.TotalRows;
	                    			},
	                    			filter: function () {
	                    				// update the grid and send a request to the server.
	                    				$("#jqxJiGridPartyIdWork").jqxGrid('updatebounddata');
	                    			},
	                    			pager: function (pagenum, pagesize, oldpagenum) {
	                    				// callback called when a page or page size is changed.
	                    			},
	                    			sort: function () {
	                    				$("#jqxJiGridPartyIdWork").jqxGrid('updatebounddata');
	                    			},
	                    			sortcolumn: 'partyId',
	                    			sortdirection: 'asc',
	                    			type: 'POST',
	                    			data: {
	                    				noConditionFind: 'Y',
	                    				conditionsFind: 'N',
	                    			},
	                    			pagesize:15,
	                    			contentType: 'application/x-www-form-urlencoded',
	                    			url: 'jqxGeneralServicer?sname=getListPartyGroups',
	                    	};
	                    	var dataAdapterGroup2 = new $.jqx.dataAdapter(sourceGroup2,{
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
	            		            }else{
	            		            	data.filterListFields = null;
	            		            }
	            		            return data;
	            		        },
	            		        loadError: function (xhr, status, error) {
	            		            alert(error);
	            		        },
	            		        downloadComplete: function (data, status, xhr) {
	            		                if (!sourceGroup2.totalRecords) {
	            		                	sourceGroup2.totalRecords = parseInt(data['odata.count']);
	            		                }
	            		        }
	            		    });
	                    	$("#jiPartyIdWork").jqxDropDownButton({ width: 200, height: 25});
	                    	$("#jqxJiGridPartyIdWork").jqxGrid({
	                    		source: dataAdapterGroup2,
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
	                    	  { text: '${uiLabelMap.CommonId}', datafield: 'partyId', filtertype: 'input', width: 150},
	                    	  { text: '${uiLabelMap.groupName}', datafield: 'groupName', filtertype: 'input'},
	                    	]
	                    	});
	                    	$("#jqxJiGridPartyIdWork").on('rowselect', function (event) {
	                    		var args = event.args;
	                    		var row = $("#jqxJiGridPartyIdWork").jqxGrid('getrowdata', args.rowindex);
	                    		selectedPartyId = row['partyId'];
	                    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
	                    		$('#jiPartyIdWork').jqxDropDownButton('setContent', dropDownContent);
	                    		$('#jiPartyIdWork').jqxDropDownButton('close');
	                    	});
	                	}
	                }
	            }
            });
            $("#jqxTabs").jqxTabs('enableAt', 0);
        }
    });
	
	$(".next").click(function(){
		var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
		if(selectedItem == 0){
			$("#createNewApplicant").jqxValidator('validate');
		}else if(selectedItem == 1){
			$("#createNewContact").jqxValidator('validate');
		}else if(selectedItem == 2){
			if($("#maritalStatus").val() == 'MARRIED'){
				var count = 0;
				for(var i = 0; i < fmData.length; i++){
					if(fmData[i].fmRelationshipTypeId == 'FAMILY_WIFE' || fmData[i].fmRelationshipTypeId == 'FAMILY_HUSBAND'){
						count += 1;
						break;
					}
				}
				if(count == 0){
					bootbox.confirm("Thông tin về vợ/chồng ứng viên chưa được nhập!", function(result) {
						return ;
					});
				}else{
					$("#jqxTabs").jqxTabs('enableAt', selectedItem + 1);
				}
				
			}else if($("#maritalStatus").val() == 'HADCHILD'){
				var count = 0;
				for(var i = 0; i < fmData.length; i++){
					if(fmData[i].partyRelationshipTypeId == 'FAMILY_CHILD'){
						count += 1;
					}
				}
				if(count != $("#numberChildren").jqxNumberInput('getDecimal')){
					bootbox.confirm("Số con không đúng với khai báo!", function(result) {
						return ;
					});
				}else{
					$("#jqxTabs").jqxTabs('enableAt', selectedItem + 1);
				}
			}else {
				$("#jqxTabs").jqxTabs('enableAt', selectedItem + 1);
			}
		}else{
			$("#jqxTabs").jqxTabs('enableAt', selectedItem + 1);
		}
		$("#jqxTabs").jqxTabs('disableAt', selectedItem);
		$("#jqxTabs").jqxTabs('next');
	});
	
	$(".back").click(function(){
		var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
		$("#jqxTabs").jqxTabs('enableAt', selectedItem - 1);
		$("#jqxTabs").jqxTabs('disableAt', selectedItem);
		$('#jqxTabs').jqxTabs('previous');
	});
	/******************************************************End Init Window**************************************************************************/