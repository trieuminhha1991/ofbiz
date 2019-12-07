<#--Import LIB-->
<#--/Import LIB-->
<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for uom data
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false)>
	var currencyUomData = [
		<#list uoms as item>
			{
				<#assign description = StringUtil.wrapString(item.description + "-" + item.abbreviation) />
				uomId : '${item.uomId}',
				description : '${description}',
			},
		</#list>
	]
	
	//Prepare for role type data
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) />
	roleTypeData = [
	              <#list roleTypeList as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'roleTypeId': '${item.roleTypeId}', 'description': '${description}'},
				  </#list>
				];
</script>
<#--===================================/Prepare Data=====================================================-->
<#--=================================Init Grid======================================================-->
<#assign dataField="[{ name: 'billingAccountId', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'roleTypeId', type: 'string'},
					 { name: 'fromDate', type: 'date', other: 'Timestamp'},
					 { name: 'thruDate', type: 'date', other: 'Timestamp'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.accPartyId}', datafield: 'partyId', editable: false, editable: 'false',
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
                     { text: '${uiLabelMap.PartyRoleTypeId}', datafield: 'roleTypeId', width: 250, filtertype: 'checkedlist', editable: 'false',
						 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
	 							for(var i = 0; i < roleTypeData.length; i++){
	 								if(value == roleTypeData[i].roleTypeId){
	 									return '<span title=' + value + '>' + roleTypeData[i].description + '</span>';
	 								}
	 							}
	 							return '<span> ' + value + '</span>';
 						},
 						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(roleTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'roleTypeId',
								renderer: function(index, label, value){
									for(var i = 0; i < roleTypeData.length; i++){
										if(roleTypeData[i].roleTypeId == value){
											return '<span>' + roleTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
                     },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput', filtertype: 'range', editable: 'false'},
                     { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', editable: 'true', width: 150,cellsformat: 'dd/MM/yyyy',columntype:'datetimeinput', filtertype: 'range',
                    	 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
  							editor.jqxDateTimeInput({width: '150', formatString:'dd/MM/yyyy'});
  					    }
                     }
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrow="true" addrefresh="true" editable="true" deleterow="true" addType="popup" alternativeAddPopup="wdwNewBillingAccRole" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListBillingAccountRoles&billingAccountId=${parameters.billingAccountId}" dataField=dataField columnlist=columnlist
		 createUrl="jqxGeneralServicer?sname=createBillingAccountRole&jqaction=C" 
		 addColumns="billingAccountId[${parameters.billingAccountId}];roleTypeId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 updateUrl="jqxGeneralServicer?sname=updateBillingAccountRole&jqaction=U"
		 editColumns="billingAccountId;roleTypeId;partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
		 removeUrl="jqxGeneralServicer?sname=removeBillingAccountRole&jqaction=D"
		 deleteColumn="billingAccountId[${parameters.billingAccountId}];roleTypeId;partyId;fromDate(java.sql.Timestamp)"
		 />
                     
<#--=================================/Init Grid======================================================-->
<div id="wdwNewBillingAccRole" style="display: none;">
	<div id="wdwHeader">
		<span>
		   ${uiLabelMap.NewBillingAccountRoles}
		</span>
	</div>
	<div id="wdwContentNew">
		<div class="basic-form form-horizontal" style="margin-top: 10px">
			<form name="formNew" id="formNew">	
				<div class="row-fluid" >
					<div class="span12">
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.AccountingPartyBilledTo}:</label>  
							<div class="controls">
								<div id="addPartyId">
									<div id="jqxGridParty"></div>
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label asterisk">${uiLabelMap.roleTypeId}:</label>  
							<div class="controls">
								<div id="roleTypeIdAdd"></div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.fromDate}:</label>  
							<div class="controls">
								<div id="fromDate">
								</div>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.thruDate}:</label>  
							<div class="controls">
								<div id="thruDate">
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<#--====================================================Setup JS======================================-->
<script>
	var JQXAction = function(){};
	JQXAction.prototype.theme = 'olbius';
	JQXAction.prototype.initWindow = function(){
		$('#wdwNewBillingAccRole').jqxWindow({showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "75%", height: 350, minWidth: '40%', width: "50%", isModal: true, modalZIndex: 10000,theme:this.theme, collapsed:false, cancelButton: '#alterCancel',
            initContent: function () {
            	$("#roleTypeIdAdd").jqxDropDownList({source: roleTypeData, valueMember: 'roleTypeId', displayMember: 'description'});
            	
            	$('#fromDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	
            	$('#thruDate').jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	
            	var sourceParty = { 
        			datafields: [
				      { name: 'partyId', type: 'string' },
				      { name: 'partyTypeId', type: 'string' },
				      { name: 'firstName', type: 'string' },
				      { name: 'lastName', type: 'string' },
				      { name: 'groupName', type: 'string' }
				    ],
					cache: false,
					root: 'results',
					datatype: 'json',
					
					beforeprocessing: function (data) {
		    			sourceParty.totalrecords = data.TotalRows;
					},
					filter: function () {
		   				// update the grid and send a request to the server.
		   				$('#jqxGridParty').jqxGrid('updatebounddata');
					},
					sort: function () {
		  				$('#jqxGridParty').jqxGrid('updatebounddata');
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
					url: 'jqxGeneralServicer?sname=getFromParty',
				};
			    var dataAdapterParty = new $.jqx.dataAdapter(sourceParty,
			    {
			    	formatData: function (data) {
				    	if (data.filterscount) {
                            var filterListFields = '';
                            for (var i = 0; i < data.filterscount; i++) {
                                var filterValue = data['filtervalue' + i];
                                var filterCondition = data['filtercondition' + i];
                                var filterDataField = data['filterdatafield' + i];
                                var filterOperator = data['filteroperator' + i];
                                filterListFields += '|OLBIUS|' + filterDataField;
                                filterListFields += '|SUIBLO|' + filterValue;
                                filterListFields += '|SUIBLO|' + filterCondition;
                                filterListFields += '|SUIBLO|' + filterOperator;
                            }
                            data.filterListFields = filterListFields;
                        }
                         data.$skip = data.pagenum * data.pagesize;
                         data.$top = data.pagesize;
                         data.$inlinecount = 'allpages';
                        return data;
                    },
                    loadError: function (xhr, status, error) {
	                    alert(error);
	                },
	                downloadComplete: function (data, status, xhr) {
	                        if (!sourceParty.totalRecords) {
	                            sourceParty.totalRecords = parseInt(data['odata.count']);
	                        }
	                }, 
	                beforeLoadComplete: function (records) {
	                	for (var i = 0; i < records.length; i++) {
	                		if(typeof(records[i])=='object'){
	                			for(var key in records[i]) {
	                				var value = records[i][key];
	                				if(value != null && typeof(value) == 'object' && typeof(value) != null){
	                					var date = new Date(records[i][key]['time']);
	                					records[i][key] = date;
	                				}
	                			}
	                		}
	                	}
	                }
			    });
			    $("#addPartyId").jqxDropDownButton({ width: 200, height: 25});
	            $('#jqxGridParty').jqxGrid({
	            	width:400,
	                source: dataAdapterParty,
	                filterable: true,
	                virtualmode: true, 
	                sortable:true,
	                editable: false,
	                autoheight:true,
	                pageable: true,
	                rendergridrows: function(obj)
					{
						return obj.data;
					},
	                columns: [
	                  { text: 'partyId', datafield: 'partyId'},
	                  { text: 'partyTypeId', datafield: 'partyTypeId'},
	                  { text: 'firstName', datafield: 'firstName'},
	                  { text: 'lastName', datafield: 'lastName'},
	                  { text: 'groupName', datafield: 'groupName'}
	                ]
	            });
            }
        });
	};
	
	JQXAction.prototype.bindEvent = function(){		
		$('#alterSave').on('click', function(){
			var row = {};
	    	row.partyId = $("#addPartyId").val();
			row.roleTypeId = $("#roleTypeIdAdd").val();
			row.fromDate = $("#fromDate").jqxDateTimeInput('getDate');
			row.thruDate = $("#thruDate").jqxDateTimeInput('getDate');
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        $("#wdwNewBillingAccRole").jqxWindow('close');
		});
		
		$("#jqxGridParty").on('rowselect', function (event) {
    		var args = event.args;
    		var row = $("#jqxGridParty").jqxGrid('getrowdata', args.rowindex);
    		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
    		$('#addPartyId').jqxDropDownButton('setContent', dropDownContent);
    		$('#addPartyId').jqxDropDownButton('close');
    	});
	};
	
	$(document).on('ready', function(){
		var jqxAction = new JQXAction();
		jqxAction.initWindow();
		jqxAction.bindEvent();
	});
</script>
<#--====================================================/Setup JS======================================-->