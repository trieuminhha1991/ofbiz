<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpasswordinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<@jqGridMinimumLib />
<script>
	//Prepare for status data
	<#assign listStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["PROB_STATUS"]), null, null, null, false)>
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
	//Prepare data for Role Type 
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) >
	var roleTypeData = new Array();
	<#list roleTypeList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['roleTypeId'] = '${item.roleTypeId}';
		row['description'] = '${description}';
		roleTypeData[${item_index}] = row;
	</#list>
	
	var partyColumnFilter = function () {
        var filtergroup = new $.jqx.filter();
        var filter_or_operator = 1;
        var filtervalue = '${parameters.partyId?if_exists}';
        var filtercondition = 'contains';
        var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
        filtergroup.addfilter(filter_or_operator, filter);
        return filtergroup;
    }();
</script>

<#assign dataField="[{ name: 'partyId', type: 'string'},
					{ name: 'firstName', type: 'string'},
					{ name: 'offerProbationId', type: 'string'},
					{ name: 'roleTypeId', type: 'string'},
					{ name: 'basicSalary', type: 'number'},
					{ name: 'trafficAllowance', type: 'number'},
					{ name: 'phoneAllowance', type: 'number'},
					{ name: 'otherAllowance', type: 'number'},
					{ name: 'inductedStartDate', type: 'date'},
					{ name: 'inductedCompletionDate', type: 'date'},
					{ name: 'percentBasicSalary', type: 'number'},
					{ name: 'lastName', type: 'string'},
					{ name: 'middleName', type: 'string'},
					{ name: 'comment', type: 'string'},
					{ name: 'statusId', type: 'string' },
					{ name: 'approverPartyId', type: 'string' },
					{ name: 'approverRoleTypeId', type: 'string' },
					{ name: 'workEffortId', type: 'string' },
					{ name: 'emplPositionTypeId', type: 'string' },
					{ name: 'partyIdWork', type: 'string' }
					]"/>
					
					<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'partyId', width: 80, editable: false,pinned : true,filter: partyColumnFilter,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							return '<span><a href=EmployeeProfile?partyId=' + value + '>' + value + '</a></span>';
						}
					},
					{ text: '${uiLabelMap.fullName}', width: 150, editable:false, pinned : true,
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						 	var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
						 	return '<span>' + rowData['lastName'] + ' ' + (rowData['middleName'] ? rowData['middleName'] : '') + ' ' + rowData['firstName'] + '</span>'
						}
					},
					{ text: '${uiLabelMap.comment}', datafield: 'comment', width: 150, pinned: false},
					{ text: '${uiLabelMap.statusId}', datafield: 'statusId', columntype:'dropdownlist',width: 100,pinned : true,
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							for(var i = 0; i < statusData.length; i++){
								if(value == statusData[i].statusId){
									return '<span title=' + value + '>' + statusData[i].description + '</span>';
								}
							}
							return '<span> ' + value + '</span>';
						}
					},
					{ text: '${uiLabelMap.approverPartyId}', datafield: 'approverPartyId', width: 150, pinned : false,
						 cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
							}
					},
					{ text: '${uiLabelMap.approverRoleTypeId}', datafield: 'approverRoleTypeId', width: 150, pinned : false,
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								for(var i = 0; i < roleTypeData.length; i++){
									if(value == roleTypeData[i].roleTypeId){
										return '<span title=' + value + '>' + roleTypeData[i].description + '</span>';
									}
								}
								return '<span> ' + value + '</span>';
							}
					},
					{ text: '${uiLabelMap.basicSalary}', datafield: 'basicSalary', width: 100},
					{ text: '${uiLabelMap.percentBasicSalary}', datafield: 'percentBasicSalary', width: 100},
					{ text: '${uiLabelMap.trafficAllowance}', datafield: 'trafficAllowance', width: 100},
					{ text: '${uiLabelMap.phoneAllowance}', datafield: 'phoneAllowance', width: 100},
					{ text: '${uiLabelMap.otherAllowance}', datafield: 'otherAllowance', width: 100},
					{ text: '${uiLabelMap.inductedStartDate}', datafield: 'inductedStartDate', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
						 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
					    }
					},
					{ text: '${uiLabelMap.inductedCompletionDate}', datafield: 'inductedCompletionDate', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
						 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
						    }
					}
					"/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" editable="false" addType="popup" alternativeAddPopup="alterpopupNewApplicant" addrow="true" showtoolbar="true" clearfilteringbutton="true"
			url="jqxGeneralServicer?sname=JQGetListInductedApplicant&workEffortId=${parameters.workEffortId}" dataField=dataField columnlist=columnlist mouseRightMenu="true" contextMenuId="contextMenu" jqGridMinimumLibEnable="false"
			updateUrl="jqxGeneralServicer?sname=updateApllStatus&jqaction=U" editColumns="roleTypeId;partyId;workEffortId;assignmentStatusId;basicSalary(java.lang.Long);inductedStartDate(java.sql.Timestamp);inductedCompletionDate(java.sql.Timestamp);percentBasicSalary(java.lang.Long);comment"
			/>
<div id='contextMenu' style="display: none;">
	<ul>
		<li id="approveOfferPro"><i class="fa fa-paper-plane"></i>&nbsp;${uiLabelMap.approveOfferPro}</li>
		<li id="proposeOfferPro"><i class="fa fa-star"></i>&nbsp;${uiLabelMap.proposeOfferPro}</li>
		<li id="createProbAgreement"><i class="fa fa-file"></i>&nbsp;${uiLabelMap.createProbAgreement}</li>
	</ul>
</div>
<#include "jqxEditProbAgreement.ftl" />
<#include "jqxApprove.ftl" />
<#include "jqxEditUserLogin.ftl" />
<script>
	<#include "jsDirectedContextMenu.ftl" />
	<#include "jsApprove.ftl" />
	$("#alterSaveAppr").click(function(){
		var submitData = {};
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		submitData['comment'] = $("#apprComment").val();
		submitData['statusId'] = $("#jqxAccepted").jqxRadioButton('val') ? "PROB_ACCEPTED" : "PROB_REJECTED";
		submitData['offerProbationId'] = rowData['offerProbationId'];
		
		//Send Request
		$.ajax({
			url: 'approveOfferProbation',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(!data._ERROR_MESSAGE_){
					$("#wdwApproveProb").jqxWindow('close');
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					bootbox.confirm("${uiLabelMap.offerPob_approveFail}", function(result) {
						return;
					});
				}
	        }
		});
	});
	//Create createNewUserLoginWindow
	$("#createNewUserLoginWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 300, minWidth: '40%', width: "50%", isModal: true,modalZIndex: 1000,
        theme:theme, collapsed:false, cancelButton: $('#alterCancelUserLogin'),
        initContent: function () {
        	$("#userLoginId").jqxInput({});
        	$("#currentPassword").jqxPasswordInput({showStrength: true, showStrengthPosition: "right" });
        	$("#currentPasswordVerify").jqxPasswordInput({});
        	$("#requirePasswordChange").jqxCheckBox({checked: true});
        	 // Create jqxValidator.
            $("#createNewUserLogin").jqxValidator({
                rules: [
                        { input: "#userLoginId", message: "${uiLabelMap.FieldRequired}", action: 'keyup, blur', rule: 'required' },
                        { input: "#currentPassword", message: "${uiLabelMap.FieldRequired}", action: 'keyup, blur', rule: 'required' },
                        { input: "#currentPasswordVerify", message: "${uiLabelMap.FieldRequired}", action: 'keyup, blur', rule: 'required' },
                        {
                            input: "#currentPasswordVerify", message: "${uiLabelMap.PasswordsMatch}", action: 'keyup, blur', rule: function (input, commit) {
                                var firstPassword = $("#currentPassword").jqxPasswordInput('val');
                                var secondPassword = $("#currentPasswordVerify").jqxPasswordInput('val');
                                return firstPassword == secondPassword;
                            }
                        }
                ]
            });
            // Validate the Form.
            $("#alterSaveUserLogin").click(function () {
                $('#createNewUserLogin').jqxValidator('validate');
            });
            // Update the jqxExpander's content if the validation is successful.
            $('#createNewUserLogin').on('validationSuccess', function (event) {
            	
            });
        }
    });
	//Create Context Menu
	$("#createProbAgreementWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 400, minWidth: '40%', width: "90%", isModal: true,modalZIndex: 1000,
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
		submitData['percentBasicSalary'] = $('#percentBasicSalary').val();
		submitData['trafficAllowance'] = $('#trafficAllowance').val();
		submitData['phoneAllowance'] = $('#phoneAllowance').val();
		submitData['otherAllowance'] = $('#otherAllowance').val();
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
	
	$("#alterSaveUserLogin").on('click', function(event){
		var submitData = {};
		submitData['userLoginId'] = $('#userLoginId').val();
		submitData['partyId'] = staticPartyId;
		submitData['currentPassword'] = $('#currentPassword').val();
		submitData['currentPasswordVerify'] = $('#currentPasswordVerify').val();
		submitData['requirePasswordChange'] = $('#requirePasswordChange').val() == true ? 'Y' : 'N';
		//Create createUserLogin
    	$.ajax({
			url: 'createUserLogin',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				if(data._ERROR_MESSAGE_){
					bootbox.confirm("Tạo mới tài khoản đăng nhập không thành công!", function(result) {
						return;
					});
				}else{
					$("#createNewUserLoginWindow").jqxWindow('close');
					bootbox.confirm("Tạo mới tài khoản đăng nhập thành công!", function(result) {
						return;
					});
				}
			}
		});
	});
</script>
