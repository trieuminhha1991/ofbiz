<#--Import LIB-->
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for Status data
	<#assign statusList = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "AGREEMENT_STATUS"), null, null, null, false) />
	statusData = [
	              <#list statusList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'statusId': '${item.statusId}', 'description': '${description}'},
				  </#list>
				];
	//Prepare for role type data
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) />
	roleTypeData = [
	              <#list roleTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'roleTypeId': '${item.roleTypeId}', 'description': '${description}'},
				  </#list>
				];
	//Prepare for agreement type data
	<#assign agreementTypeList = delegator.findList("AgreementType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "EMPLOYMENT_AGREEMENT"), null, null, null, false) />
	agreementTypeData = [
	              <#list agreementTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'agreementTypeId': '${item.agreementTypeId}', 'description': '${description}'},
				  </#list>
				];
	//Prepare for employment position type data
	<#assign emplPosTypeList = delegator.findList("EmplPositionType", null, null, null, null, false) />
	emplPositionTypeData = [
	              <#list emplPosTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'emplPositionTypeId': '${item.emplPositionTypeId}', 'description': '${description}'},
				  </#list>
				];
	
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'agreementId', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyIdTo', type: 'string'},
					 { name: 'roleTypeIdFrom', type: 'string'},
					 { name: 'roleTypeIdTo', type: 'string'},
					 { name: 'agreementTypeId', type: 'string'},
					 { name: 'agreementDate', type: 'date'},
					 { name: 'statusId', type: 'number'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'agreementId', width: 160, editable: false,
						cellsrenderer: function(row, column, value){
							return '<span><a href=AppendixList?agreementId=' + value + '>' + value + '</a></span>'
						}
					 },
                     { text: '${uiLabelMap.HRPartyIdFrom}', datafield: 'partyIdFrom', width: 150,
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
                     { text: '${uiLabelMap.HRPartyIdTo}', datafield: 'partyIdTo', width: 150,
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
                     { text: '${uiLabelMap.HRRoleTypeIdFrom}', datafield: 'roleTypeIdFrom', width: 150,
				    	 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
 							for(var i = 0; i < roleTypeData.length; i++){
 								if(value == roleTypeData[i].roleTypeId){
 									return '<span title=' + value + '>' + roleTypeData[i].description + '</span>';
 								}
 							}
 							return '<span> ' + value + '</span>';
 						}
                     },
                     { text: '${uiLabelMap.agreementTypeId}', datafield: 'agreementTypeId', width: 200,
				    	 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
 							for(var i = 0; i < agreementTypeData.length; i++){
 								if(value == agreementTypeData[i].agreementTypeId){
 									return '<span title=' + value + '>' + agreementTypeData[i].description + '</span>';
 								}
 							}
 							return '<span> ' + value + '</span>';
 						}
                     },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: 120,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
  							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
  					    }
                     },
                     { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', width: 120,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput',
                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
  							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
  					    }
                     },
                     { text: '${uiLabelMap.statusId}', datafield: 'statusId', columntype:'dropdownlist',width: 150,
                    	 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
 							for(var i = 0; i < statusData.length; i++){
 								if(value == statusData[i].statusId){
 									return '<span title=' + value + '>' + statusData[i].description + '</span>';
 								}
 							}
 							return '<span> ' + value + '</span>';
 						}
                      }
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrow="true" addrefresh="true" editable="false" addType="popup" alternativeAddPopup="wdwNewEmplAgreement" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplAgreement" dataField=dataField columnlist=columnlist customcontrol1="icon-save open-sans@${uiLabelMap.DAExportToPDF}@javascript: void(0);@JQXAction.printAgreement()"
			 customcontrol2="fa-times open-sans@${uiLabelMap.terminateAgreement}@javascript: void(0);@JQXAction.terminateAgreement()"	 
		/>
<#--=================================/Init Grid======================================================-->
<#include "jqxEditEmplAgreement.ftl" />
<#--====================================================Setup JS======================================-->
<script>
	var JQXAction = function(){};
	JQXAction.prototype.theme = 'olbius';
	JQXAction.printAgreement = function(){
		var selectedIndex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', selectedIndex);
		window.open('<@ofbizUrl>PrintAgreementPdf?agreementId=' + rowData["agreementId"] + '</@ofbizUrl>')
	};
	JQXAction.terminateAgreement = function(){
		var selectedIndex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', selectedIndex);
		var submitData = {};
		submitData['agreementId'] = rowData['agreementId'];
		var now = new Date();
		submitData['thruDate'] = now.getFullYear()+ "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds();
		//Send Ajax
		$.ajax({
			url: 'terminateAgreement',
			type: 'POST',
			data: submitData,
			dataType: 'json',
			async: 'false',
			success: function(data){
				$("#jqxgrid").jqxGrid('updatebounddata');
			}
		});
	};
	JQXAction.prototype.initWindow = function(){
		$('#wdwNewEmplAgreement').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 400, minWidth: '40%', width: "90%", isModal: true, modalZIndex: 1000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
            initContent: function () {
            	
            	//Create partyIdFrom
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
            	var sourcePeople2 =
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
            				sourcePeople2.totalrecords = data.TotalRows;
            			},
            			filter: function () {
            				// update the grid and send a request to the server.
            				$("#jqxGridPartyIdTo").jqxGrid('updatebounddata');
            			},
            			pager: function (pagenum, pagesize, oldpagenum) {
            				// callback called when a page or page size is changed.
            			},
            			sort: function () {
            				$("#jqxGridPartyIdTo").jqxGrid('updatebounddata');
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
            	var dataAdapterPeople2 = new $.jqx.dataAdapter(sourcePeople2,{
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
    	                if (!sourcePeople2.totalRecords) {
    	                	sourcePeople2.totalRecords = parseInt(data['odata.count']);
    	                }
    		        }
    		    });
            	$("#partyIdTo").jqxDropDownButton({ width: 200, height: 25});
            	$("#jqxGridPartyIdTo").jqxGrid({
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
            	$("#jqxGridPartyIdTo").on('rowselect', function (event) {
            		var args = event.args;
            		var row = $("#jqxGridPartyIdTo").jqxGrid('getrowdata', args.rowindex);
            		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
            		$('#partyIdTo').jqxDropDownButton('setContent', dropDownContent);
            		$('#partyIdTo').jqxDropDownButton('close');
            	});
            	
            	//Create jqxGridRepPartyIdFrom
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
            				$("#jqxGridPartyIdWork").jqxGrid('updatebounddata');
            			},
            			pager: function (pagenum, pagesize, oldpagenum) {
            				// callback called when a page or page size is changed.
            			},
            			sort: function () {
            				$("#jqxGridPartyIdWork").jqxGrid('updatebounddata');
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
            	$("#partyIdWork").jqxDropDownButton({ width: 200, height: 25});
            	$("#jqxGridPartyIdWork").jqxGrid({
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
            	$("#jqxGridPartyIdWork").on('rowselect', function (event) {
            		var args = event.args;
            		var row = $("#jqxGridPartyIdWork").jqxGrid('getrowdata', args.rowindex);
            		selectedPartyId = row['partyId'];
            		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
            		$('#partyIdWork').jqxDropDownButton('setContent', dropDownContent);
            		$('#partyIdWork').jqxDropDownButton('close');
            	});
            	
            	//Create Agreement Type
            	$('#agreementTypeId').jqxDropDownList({source: agreementTypeData, valueMember: 'agreementTypeId', displayMember: 'description'});
            	
            	//fromDate
            	$('#fromDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
            	
            	//thruDate
            	$('#thruDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	
            	//Create emplPositionTypeData
            	$("#emplPositionTypeId").jqxDropDownList({source: emplPositionTypeData, valueMember: 'emplPositionTypeId', displayMember: 'description'});
            	
            	//Create salary
            	$("#salary").jqxNumberInput({decimalDigits: 0, spinButtons: true});
            }
        });
	};
	
	JQXAction.prototype.bindEvent = function(){
		 $("#formNewEmplAgreement").on('validationSuccess', function (event) {
			var submitData = {};
			submitData['partyIdFrom'] = $('#partyIdFrom').val();
			submitData['roleTypeIdFrom'] = $('#roleTypeIdFrom').val();
			submitData['partyIdTo'] = $('#partyIdTo').val();
			submitData['partyIdWork'] = $('#partyIdWork').val();
			submitData['salary'] = $('#salary').val();
			submitData['emplPositionTypeId'] = $('#emplPositionTypeId').val();
			submitData['thruDate'] = $("#thruDate").jqxDateTimeInput('getDate').getTime();
			submitData['fromDate'] = $("#fromDate").jqxDateTimeInput('getDate').getTime();
			submitData['agreementTypeId'] = $('#agreementTypeId').val();
			submitData['repPartyIdFrom'] = $('#repPartyIdFrom').val();
			
			//Send Ajax
			$.ajax({
				url: 'createEmplAgreement',
				type: 'POST',
				data: submitData,
				dataType: 'json',
				async: 'false',
				success: function(data){
					if(data.errorMessage){
						bootbox.confirm(data.errorMessage, function(result) {
							return;
						});
					}else{
						bootbox.confirm(data.successMessage, function(result) {
							return;
						});
						$("#jqxgrid").jqxGrid('updatebounddata');
						$("#wdwNewEmplAgreement").jqxWindow('close');
					}
				}
			});
		 });
		
		$('#alterSave').on('click', function(){
			$("#formNewEmplAgreement").jqxValidator('validate');
		});
	};
	
	JQXAction.prototype.initValidator = function(){
		$('#formNewEmplAgreement').jqxValidator({
	        rules: [{ input: '#salary', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup',
	        			rule: function (input, commit) {
	        				var val = input.jqxNumberInput('getDecimal');
	                        if (val) {
	                            return true;
	                        }
	                        return false;
	                    }
	       			},
	       			{ input: '#fromDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	       				}
	       			},
					{input: '#fromDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
						rule: function (input, commit) {
		                    if(input.jqxDateTimeInput('getDate') > $("#thruDate").jqxDateTimeInput('getDate') && $("#thruDate").jqxDateTimeInput('getDate')){
		                    	return false;
		                    }else{
		                    	return true;
		                    }
		                    	
	                	}
					},
					{input: '#thruDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, close', 
						rule: function (input, commit) {
		                    if(input.val()){
		                    	return true;
		                    }else{
		                    	return false;
		                    }
		                    	
	                	}
					},
					{input: '#thruDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
						rule: function (input, commit) {
		                    if(input.jqxDateTimeInput('getDate') < $("#fromDate").jqxDateTimeInput('getDate')){
		                    	return false;
		                    }else{
		                    	return true;
		                    }
		                    	
	                	}
					},
	       			{ input: '#emplPositionTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#partyIdFrom', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#roleTypeIdFrom', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#partyIdTo', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#partyIdWork', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#agreementTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			},
	    			{ input: '#repPartyIdFrom', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
	    				}
	    			}
	               ]
	    });
	};
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction.bindEvent();
		jqxAction.initValidator();
	});
</script>
<#--====================================================/Setup JS======================================-->