<script>
var listIPTypes = [<#if listIPTypes?exists><#list listIPTypes as type>{insuranceParticipateTypeId: "${type.insuranceParticipateTypeId}",parentTypeId: "${type.parentTypeId?if_exists}",description: "${type.description?if_exists}",},</#list></#if>];
var listInsuranceTypes = [<#if listInsuranceType?exists><#list listInsuranceType as type>{insuranceTypeId: "${type.insuranceTypeId}",description: "${type.description?if_exists}",},</#list></#if>];
</script>


<#assign dataField="[{ name: 'reportId', type: 'string' },
					 { name: 'partyId', type: 'string' },
					 { name: 'firstName', type: 'string' },
					 { name: 'middleName', type: 'string' },
					 { name: 'lastName', type: 'string' },
					 { name: 'insuranceTypeId', type: 'string' },
					 { name: 'insurancePaymentId', type: 'string' },
					 { name: 'insuranceParticipateTypeId', type: 'string' },
					 { name: 'fromDate', type: 'date', other:'Timestamp' },
					 { name: 'thruDate', type: 'date', other:'Timestamp' },
					 { name: 'paymentAmount', type: 'date', other:'string' },
					 { name: 'suspendDescription', type: 'date', other:'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.EmployeeName}', datafield: 'firstName', width: 250, 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#listPartyInsurancePayment').jqxGrid('getrowdata', row);
					 		return '<div style=\"margin-top: 4px; margin-left: 5px;\">'+data.lastName + ' ' + data.middleName + ' ' + data.firstName +'</div>';
					 	}
					 },
                     { text: '${uiLabelMap.InsuranceType}', datafield: 'insuranceTypeId', 
                     	cellsrenderer: function(row, column, value){
					 		for(var x in listInsuranceTypes){
					 			if(listInsuranceTypes[x].insuranceTypeId == value){
					 				return '<div style=\"margin-top: 4px; margin-left: 5px;\">' + listInsuranceTypes[x].description + '</div>';
					 			}	
					 		}
					 		
					 	}
                     },
                     { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', filtertype: 'range', width: 150, cellsformat: 'dd/MM/yyyy'},
                     { text: '${uiLabelMap.PaymentAmount}', datafield: 'paymentAmount'},
                     { text: '${uiLabelMap.SuspendParticipateInsuranceReason}', datafield: 'suspendDescription'}"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPartyInsurancePayment&insurancePaymentId=${parameters.insurancePaymentId?if_exists}"  dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	autorowheight="true"
	showtoolbar = "true" addrow="true" deleterow="true"
	id="listPartyInsurancePayment"
	selectionmode="checkbox"
	removeUrl="jqxGeneralServicer?sname=deletePartyInsurancePayment&jqaction=D" deleteColumn="insurancePaymentId;reportId;partyId;insuranceParticipateTypeId;insuranceTypeId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyInsurancePayment" alternativeAddPopup="popupAddrow" addrow="true" addType="popup"
	addColumns="insurancePaymentId;reportId;partyId;insuranceParticipateTypeId;insuranceTypeId" addrefresh="true"
/>

<div id='popupAddrow' class='hide'>
	<div>
		${uiLabelMap.EditParticipateInsuranceReport}
	</div>
	<div>
		<div id="listPartyReduce"></div>
	</div>
</div>
<script>
	$(document).ready(function(){
		var insurancePaymentId = "${parameters.insurancePaymentId?if_exists}"
		var popup = $("#popupAddrow");
		popup.jqxWindow({
		    width: 820, height: 530, resizable: true,  isModal: true, autoOpen: false, theme:'olbius'
		});
		popup.on('close', function (event) { 
			popup.jqxValidator('hide');
		}); 
		var sourcePartyFrom = {
			datafields:[{ name: 'reportId', type: 'string' },
			            { name: 'partyId', type: 'string' },
						 { name: 'firstName', type: 'string' },
						 { name: 'middleName', type: 'string' },
						 { name: 'lastName', type: 'string' },
						 { name: 'insuranceParticipateTypeId', type: 'string' },
						 { name: 'fromDate', type: 'date', other:'Timestamp' },
						 { name: 'thruDate', type: 'date', other:'Timestamp' },
						 { name: 'comments', type: 'string' },
						 { name: 'insuranceTypeId', type: 'string' },
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
			url: 'jqxGeneralServicer?sname=JQGetPartyInsuranceReport&parentTypeId=REDUCE_PARTICIPATE&insurancePaymentId=${parameters.insurancePaymentId?if_exists}'
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
		var grid = $("#listPartyReduce");
		grid.jqxGrid({
			width:800,
			source: dataAdapterPF,
			filterable: true,
			virtualmode: true, 
			sortable:true,
			editable: false,
			autoheight:true,
			pageable: true,
			showfilterrow: true,
			selectionmode: 'checkbox',
			theme: 'olbius',
			rendergridrows: function(obj) {	
				return obj.data;
			},
			columns:[{text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '100px'},
					{text: '${uiLabelMap.EmployeeName}', datafield: 'firstName', width: '200px', 
						cellsrenderer: function(row, column, value){
					 		var data = grid.jqxGrid('getrowdata', row);
					 		return '<div style=\"margin-top: 4px; margin-left: 5px;\">'+data.lastName + ' ' + data.middleName + ' ' + data.firstName +'</div>';
					 	}		
					},
                    { text: '${uiLabelMap.InsuranceParticipateType}', datafield: 'insuranceParticipateTypeId', width: '200px',
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
					]
			});
		var flag = false;
		grid.on('rowselect', function (event) {
			var args = event.args;
			var arr = args.rowindex;
			var container = $("#listPartyInsurancePayment");
			if(arr && arr.length){
				for(var x in arr){
					var row = grid.jqxGrid('getrowdata', arr[x]);
					var data = { 
						insurancePaymentId: insurancePaymentId,
						reportId: row.reportId,
				   		partyId: row.partyId,
				   		insuranceParticipateTypeId: row.insuranceParticipateTypeId,
				   		insuranceTypeId: row.insuranceTypeId
				   	  };
				   container.jqxGrid('addRow', null, data, "first");
				}
				flag = true;
			    container.jqxGrid('clearSelection');                        
		        container.jqxGrid('selectRow', 0);  
		        popup.jqxWindow('close');
			}
		});
		popup.on("open", function(){
			grid.jqxGrid("updatebounddata");
		});
		popup.on("close", function(){
			grid.jqxGrid("updatebounddata");
		});
	});
</script>