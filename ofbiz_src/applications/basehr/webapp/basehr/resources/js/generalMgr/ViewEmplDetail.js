var emplDetailInfoObject = (function(){
	var _partyData;
	var loadData = {generalInfo: true, contactInfo: true, employeeWork: true, family: true};
	var init = function(){
		initJqxExpander();
		initJqxExpanderEvent();
		initJqxGrid();
		initJqxWindow();
		create_spinner($("#spinner-ajax_generalInfo"));
		create_spinner($("#spinner-ajax_contactInfo"));
		create_spinner($("#spinner-ajax_employeeWork"));
	};
	
	var initJqxExpander = function(){
		$("#emplDetail_generalInfo").jqxExpander({ width: '100%', theme: 'olbius', expanded: false});
		$("#emplDetail_contactInfo").jqxExpander({ width: '100%', theme: 'olbius', expanded: false});
		$("#emplDetail_employeeWork").jqxExpander({ width: '100%', theme: 'olbius', expanded: false});
		$("#emplDetail_family").jqxExpander({ width: '100%', theme: 'olbius', expanded: false});
	};
	
	var initJqxExpanderEvent = function(){
		$("#emplDetail_generalInfo").on('expanding', function(){
			collapseExpanded("emplDetail_generalInfo");
			$("#viewDetailEmployeeId").text(_partyData.partyCode);
			$("#viewDetailEmplFullName").text(_partyData.fullName);
			var genderDes = "";
			for(var i = 0; i < genderArr.length; i++){
				if(genderArr[i].genderId == _partyData.gender){
					genderDes = genderArr[i].description;
					break;
				}
			}
			var birthDate = _partyData.birthDate;
			if(birthDate){
				$("#viewDetailBirthDate").text(getDate(birthDate) + "/" + getMonth(birthDate) + "/" + birthDate.getFullYear());
			}
			$("#viewDetailGeneder").text(genderDes);
			if(loadData.generalInfo){
				$("#ajaxLoading_generalInfo").show();
				$.ajax({
					url: 'getPartyInformation',
					data: {partyId: _partyData.partyId},
					type: 'POST',
					success: function(response){
						if(response.partyInfo){
							var partyInfo = response.partyInfo;
							if(partyInfo.idNumber){
								$("#viewDetailCertProvisionId").text(partyInfo.idNumber);
							}
							if(partyInfo.idIssuePlace){
								$("#viewDetailIssuePlace").text(partyInfo.idIssuePlace);
							}
							if(partyInfo.idIssueDate){
								$("#viewDetailIssueDate").text(partyInfo.idIssueDate);
							}
							if(partyInfo.nativeLand){
								$("#viewDetailNativeLand").text(partyInfo.nativeLand);
							}
							if(partyInfo.ethnicOrigin){
								$("#viewDetailEthnicOrigin").text(partyInfo.ethnicOrigin);
							}
							if(partyInfo.religion){
								$("#viewDetailReligion").text(partyInfo.religion);
							}
							if(partyInfo.nationality){
								$("#viewDetailNationality").text(partyInfo.nationality);
							}
							if(partyInfo.maritalStatus){
								$("#viewDetailMaritalStatus").text(partyInfo.maritalStatus);
							}
							if(partyInfo.numberChildren){
								$("#viewDetailNumberChildren").text(partyInfo.numberChildren);
							}
							loadData.generalInfo = false;
						}
					},
					error: function(jqXHR, textStatus, errorThrown){
						
					},
					complete: function(jqXHR, textStatus){
						$("#ajaxLoading_generalInfo").hide();
					}
				});
			}
		});
		$("#emplDetail_contactInfo").on('expanding', function(){
			collapseExpanded("emplDetail_contactInfo");
			if(loadData.contactInfo){
				$("#ajaxLoading_contactInfo").show();
				$.ajax({
					url: 'getEmployeeContactInfo',
					data: {partyId: _partyData.partyId},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == 'success'){
							loadData.contactInfo = false;
							$("#viewDetailPermanentResidence").text(response.permanentResidence);
							$("#viewDetailCurrentResidence").text(response.currentResidence);
							$("#viewDetailEmail").text(response.emailAddress);
							$("#viewDetailPhoneMobile").text(response.phoneMobile);
						}
					},
					error: function(jqXHR, textStatus, errorThrown){
						
					},
					complete: function(jqXHR, textStatus){
						$("#ajaxLoading_contactInfo").hide();
					}
				});
			}
		});
		$("#emplDetail_employeeWork").on('expanding', function(){
			collapseExpanded("emplDetail_employeeWork");
			if(loadData.employeeWork){
				var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
				var from = selection.from;
				var to = selection.to;
				$("#viewDetailPartyIdFrom").text(_partyData.partyGroupName);
				$("#viewDetailPosition").text(_partyData.emplPositionType);
				var dateJoinCompany = _partyData.dateJoinCompany;
				if(dateJoinCompany){
					$("#viewDetailDateJoinCompany").text(getDate(dateJoinCompany) + "/" +  getMonth(dateJoinCompany) + "/" + dateJoinCompany.getFullYear());
				}
				if(_partyData.workingStatusId){
					var workingSttDesc = "";
					for(var i = 0; i < globalVar.statusWorkingArr.length; i++){
						if(globalVar.statusWorkingArr[i].statusId == _partyData.workingStatusId){
							workingSttDesc = globalVar.statusWorkingArr[i].description;
							break;
						}
					}
					$("#viewDetailWorkStatus").html(workingSttDesc);
				}else{
					$("#viewDetailWorkStatus").html("");
				}
				if(_partyData.terminationReasonId){
					var terminationReasonDesc = "";
					for(var i = 0; i < globalVar.terminationReasonArr.length; i++){
						if(globalVar.terminationReasonArr[i].terminationReasonId == _partyData.terminationReasonId){
							terminationReasonDesc = globalVar.terminationReasonArr[i].description;
							break;
						}
					}
					$("#viewDetailReasonResign").html(terminationReasonDesc);
				}else{
					$("#viewDetailReasonResign").html("");
				}
				if(_partyData.resignDate){
					$("#viewDetailResignDate").text(getDate(_partyData.resignDate) + "/" +  getMonth(_partyData.resignDate) + "/" + _partyData.resignDate.getFullYear());
				}else{
					$("#viewDetailResignDate").text("");
				}
			}
		});
		$("#emplDetail_family").on('expanding', function(){
			collapseExpanded("emplDetail_family");
			if(loadData.family){
				var tmpS = $("#viewDetailFamilyGrid").jqxGrid('source');
				tmpS._source.url = "jqxGeneralServicer?sname=JQGetListEmplFamily&partyId=" + _partyData.partyId;
				$("#viewDetailFamilyGrid").jqxGrid('source', tmpS);
			}
		});
	};
	
	var collapseExpanded = function(exceptedId){
		$(".expanedGroup").each(function(index, element){
			var id = $(element).attr('id');
			if(id != exceptedId){
				$(element).jqxExpander('collapse');
			}
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#viewEmplDetailsWindow"), 900, 530);
		$("#viewEmplDetailsWindow").on('close', function(event){
			$("label[id^='viewDetail']").each(function(index, element){
				$(element).text("");
			});
			loadData.generalInfo = true;
			loadData.contactInfo = true;
			loadData.employeeWork = true;
			loadData.family = true;
			collapseExpanded("");
		});
		$("#viewEmplDetailsWindow").on('open', function(event){
			$("#emplDetail_generalInfo").jqxExpander('expand');
		});
	};
	
	var initJqxGrid = function(){
		var dataField=[{name: 'relPartyId', type: 'string' },
						{ name: 'personFamilyBackgroundId', type: 'string' },
						{ name: 'partyId', type: 'string'},
						{ name: 'partyFamilyId', type: 'string' },
						{ name: 'familyFullName', type: 'string' },
						{ name: 'partyRelationshipTypeId', type: 'string' },
						{ name: 'occupation', type: 'string' },
						{ name: 'birthDate', type: 'date', other:'Timestamp' },
						{ name: 'placeWork', type: 'string'},
						{ name: 'phoneNumber', type: 'number'},
						{ name: 'isDependent', type: 'bool'},
						{ name: 'dependentStartDate', type: 'date'}
		 ];
		var columnlist = [
				{ datafield: 'personFamilyBackgroundId',hidden: true},	
				{ text: uiLabelMap.partyId, datafield: 'partyId', hidden: true, editable: false},	
				{ text: uiLabelMap.HRFullName, datafield: 'familyFullName', width: '15%', filtertype: 'input',
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},	
				{ text: uiLabelMap.HRRelationship, datafield: 'partyRelationshipTypeId', width: '11%', filtertype: 'list', columntype: 'dropdownlist', editable: true,
					cellsrenderer: function(row, column, value){
						for(var i = 0; i < partyRelationshipType.length; i++){
							if(value == partyRelationshipType[i].partyRelationshipTypeId){
								return '<span title=' + value + '>' + partyRelationshipType[i].partyRelationshipName + '</span>';
							}
						}
						return '<span>' + value + '</span>';
					},
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					},
				},
				{ text: uiLabelMap.BirthDate, datafield: 'birthDate', width: '15%', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDateTimeInput({width: '110px', height: '31px'});
					},
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: uiLabelMap.HROccupation, datafield: 'occupation', width: '16%',
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: uiLabelMap.placeWork, datafield: 'placeWork', width: '16%', filtertype: 'string',
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: uiLabelMap.PartyPhoneNumber, datafield: 'phoneNumber', width: '12%', columntype: 'numberinput', filtertype: 'number',
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxNumberInput({inputMode: 'simple', spinButtons: false});
					},
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{ text: uiLabelMap.PersonDependent, datafield: 'isDependent',filtertype: 'list', columntype: 'checkbox', editable: false,
					filterable: false,
					cellclassname: function (row, column, value, data) {
					    if (data.isDependent) {
					        return 'highlightCell';
					    }
					}
				},
				{
					datafield: 'dependentStartDate', hidden: true, cellsformat: 'dd/MM/yyyy',
				}           	
		];
		var config = {
				width: '100%', 
				height: 300,
	      		autoheight: false,
	      		showfilterrow: false,
	      		showtoolbar: false,
	      		pageable: true,
	      		sortable: false,
	      		filterable: false,
	      		editable: false,
	      		selectionmode: 'singlerow',
	      		url: '',
	      		source: {pagesize: 10}
		};
		Grid.initGrid(config, dataField, columnlist, null, $("#viewDetailFamilyGrid"));
	};
	
	var openView = function(){
		openJqxWindow($("#viewEmplDetailsWindow"));
	};
	
	var setPartyData = function(data){
		_partyData = data;
	};
	
	return{
		init: init,
		openView: openView,
		setPartyData: setPartyData
	}
}());

$(document).ready(function () {
	emplDetailInfoObject.init();
});