//Create Context Menu
	$("#createProbAgreementWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 400, minWidth: '40%', width: "90%", isModal: true,modalZIndex: 10000,
        theme:theme, collapsed:false, cancelButton: '#alterCancelNewProbAgreement',
        initContent: function () {
        	//Get selected row in grid
    		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
    		var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
    		
        	//Party Grid
        	var sourcePeople =
        	{
        			datafields:
        				[
        				 { name: 'partyId', type: 'string' },
        				 { name: 'firstName', type: 'string' },
        				 { name: 'middleName', type: 'string' },
        				 { name: 'lastName', type: 'string' }
        				],
        			cache: false,
        			root: 'results',
        			datatype: "json",
        			updaterow: function (rowid, rowdata) {
        				// synchronize with the server - send update command   
        			},
        			beforeprocessing: function (data) {
        				sourcePeople.totalrecords = data.TotalRows;
        			},
        			filter: function () {
        				// update the grid and send a request to the server.
        				$("#jqxGridPartyIdFrom").jqxGrid('updatebounddata');
        			},
        			pager: function (pagenum, pagesize, oldpagenum) {
        				// callback called when a page or page size is changed.
        			},
        			sort: function () {
        				$("#jqxGridPartyIdFrom").jqxGrid('updatebounddata');
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
        			url: 'jqxGeneralServicer?sname=getListPeople',
        	};
        	var dataAdapterPeople = new $.jqx.dataAdapter(sourcePeople,{
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
	                if (!sourcePeople.totalRecords) {
	                	sourcePeople.totalRecords = parseInt(data['odata.count']);
	                }
		        }
		    });
        	$("#partyIdFrom").jqxDropDownButton({ width: 200, height: 25});
        	$("#jqxGridPartyIdFrom").jqxGrid({
        		source: dataAdapterPeople,
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
        	  { text: '${uiLabelMap.firstName}', datafield: 'firstName', filtertype: 'input'},
        	  { text: '${uiLabelMap.middleName}', datafield: 'middleName', filtertype: 'input'},
        	  { text: '${uiLabelMap.lastName}', datafield: 'lastName', filtertype: 'input'}
        	]
        	});
        	$("#jqxGridPartyIdFrom").on('rowselect', function (event) {
        		var args = event.args;
        		var row = $("#jqxGridPartyIdFrom").jqxGrid('getrowdata', args.rowindex);
        		selectedPartyId = row['partyId'];
        		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
        		$('#partyIdFrom').jqxDropDownButton('setContent', dropDownContent);
        		$.ajax({
        			url: 'jqxGetPartyRole',
        			type: "POST",
        			data: {partyId: row['partyId']},
        			dataType: 'json',
        			async: false,
        			success : function(data) {
        				if(data.responseMessage == 'success'){
        					var roleTypeData = data.listRoleTypes;
        					$("#roleTypeIdFrom").jqxDropDownList({source: roleTypeData});
        				}
        	        }
        		});
        		$('#partyIdFrom').jqxDropDownButton('close');
        	});
        	
        	//Create roleTypeIdFrom
        	$("#roleTypeIdFrom").jqxDropDownList({valueMember: 'roleTypeId', displayMember: 'description'});
        	
        	//Create jqxGridRepPartyIdFrom
        	var sourceGroup =
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
        				sourceGroup.totalrecords = data.TotalRows;
        			},
        			filter: function () {
        				// update the grid and send a request to the server.
        				$("#jqxGridRepPartyIdFrom").jqxGrid('updatebounddata');
        			},
        			pager: function (pagenum, pagesize, oldpagenum) {
        				// callback called when a page or page size is changed.
        			},
        			sort: function () {
        				$("#jqxGridRepPartyIdFrom").jqxGrid('updatebounddata');
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
        	var dataAdapterGroup = new $.jqx.dataAdapter(sourceGroup,{
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
		                if (!sourceGroup.totalRecords) {
		                	sourceGroup.totalRecords = parseInt(data['odata.count']);
		                }
		        }
		    });
        	$("#repPartyIdFrom").jqxDropDownButton({ width: 200, height: 25});
        	$("#jqxGridRepPartyIdFrom").jqxGrid({
        		source: dataAdapterGroup,
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
        	$("#jqxGridRepPartyIdFrom").on('rowselect', function (event) {
        		var args = event.args;
        		var row = $("#jqxGridRepPartyIdFrom").jqxGrid('getrowdata', args.rowindex);
        		selectedPartyId = row['partyId'];
        		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
        		$('#repPartyIdFrom').jqxDropDownButton('setContent', dropDownContent);
        		$('#repPartyIdFrom').jqxDropDownButton('close');
        	});
        	
        	//Create partyIdTo
        	$("#partyIdToLabel").text(rowData['lastName'] + ' ' + (rowData['middleName'] ? rowData['middleName'] : '') + ' ' + rowData['firstName']);
        	$("#partyIdTo").val(rowData['partyId']);
        }
	});
	
	$("#alterSaveNewProbAgreement").on('click', function(event){
		var submitData = {};
		submitData['partyIdFrom'] = $('#partyIdFrom').val();
		submitData['workEffortId'] = '${parameters.workEffortId}';
		submitData['roleTypeIdFrom'] = $('#roleTypeIdFrom').val();
		submitData['repPartyIdFrom'] = $('#repPartyIdFrom').val();
		submitData['partyIdTo'] = $('#partyIdTo').val();
		submitData['recruitmentTypeId'] = $('#recruitmentTypeId').val();
		submitData['emplPositionTypeId'] = $('#emplPositionTypeId').val();
		submitData['partyIdWork'] = $('#partyIdWork').val();
		submitData['basicSalary'] = $('#basicSalary').val();
		submitData['otherAllowance'] = $('#otherAllowance').val();
		submitData['trafficAllowance'] = $('#trafficAllowance').val();
		submitData['phoneAllowance'] = $('#phoneAllowance').val();
		submitData['percentBasicSalary'] = $('#percentBasicSalary').val();
		submitData['inductedStartDate'] = $("#inductedStartDate").val();
		submitData['inductedCompletionDate'] = $("#inductedCompletionDate").val();
		submitData['offerProbationId'] = offerProbationId;
		//Create Probation Agreement
    	$.ajax({
			url: 'createProbAgreement',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data.responseMessage == 'success'){
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#createProbAgreementWindow").jqxWindow('close');
				}else{
					bootbox.confirm("Tạo mới thỏa thuận thử việc Không thành công!", function(result) {
						return;
					});
				}
			}
		});
	});