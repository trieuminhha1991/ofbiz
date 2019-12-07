<script>
	<#assign facilityList = delegator.findList("Facility", null, null, null, null, false) />
	var facilityData = new Array();
	<#list facilityList as facility>
		var row = {};			
		row['facilityId'] = '${facility.facilityId?if_exists}';
		row['facilityName'] = '${facility.facilityName?if_exists}';
		facilityData[${facility_index}] = row; 
	</#list>
</script>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'facilityId', type: 'string'}
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accFacilityName}', datafield: 'facilityId', editable: false, 
					   cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < facilityData.length; i++){
					 			if(value == facilityData[i].facilityId){
					 				return \"<span>\" + facilityData[i].facilityId + '[' + facilityData[i].facilityName + ']' + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}	
					 }
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false"
		 url="jqxGeneralServicer?sname=JQListAgreementFacilityAppl&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementFacilityAppl&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementFacilityAppl&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementFacilityAppl&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];facilityId"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];facilityId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];facilityId"
		 />

<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accFacilityName}:</td>
	 			<td align="left">
	 				<div id="facilityIdAdd">
	 					<div id="jqxFacilityGrid" />
	 				</div>
	 			</td>	 				 			
    	 	</tr>
            <tr>
                <td align="right"></td>
                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
//Create theme
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;

	var sourceP = { datafields: [
						      { name: 'facilityId', type: 'string' },
						      { name: 'facilityName', type: 'string' }
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourceP.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxFacilityGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxFacilityGrid").jqxGrid('updatebounddata');
				},
				sortcolumn: 'facilityId',
               	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListFacilitys',
			};
		    var dataAdapterP = new $.jqx.dataAdapter(sourceP,
		    {
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
		                if (!sourceP.totalRecords) {
		                    sourceP.totalRecords = parseInt(data['odata.count']);
		                }
		        }
		    });	

	// Create productId
	$('#facilityIdAdd').jqxDropDownButton({ width: 215, height: 25});
	$("#jqxFacilityGrid").jqxGrid({
		width:400,
		source: dataAdapterP,
		filterable: true,
		showfilterrow : true,
		virtualmode: true, 
		sortable:true,
		editable: false,
		autoheight:true,
		pageable: true,
		ready:function(){
        },
		rendergridrows: function(obj)
		{	
			return obj.data;
		},
		columns: 
			[
				{ text: '${uiLabelMap.accFacilityId}', datafield: 'facilityId', width: '50%'},
				{ text: '${uiLabelMap.accFacilityName}', datafield: 'facilityName'},
			]
		});

	$("#jqxFacilityGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxFacilityGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['facilityId'] + '</div>';
                $("#facilityIdAdd").jqxDropDownButton('setContent', dropDownContent);
            });
	
$('#alterpopupWindow').on('open', function (event) {		
	$("#facilityIdAdd").jqxDropDownButton('val', null);	   	
});

//Create Popup
$("#alterpopupWindow").jqxWindow({
        width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
    });
    $("#alterCancel").jqxButton();
    $("#alterSave").jqxButton();

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
    	    	
        row = { 
        		facilityId:$('#facilityIdAdd').val(), 
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>