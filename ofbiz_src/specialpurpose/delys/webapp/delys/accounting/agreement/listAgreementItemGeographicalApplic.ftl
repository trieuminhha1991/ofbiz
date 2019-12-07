<script>
	<#assign geoList = delegator.findList("Geo", null, null, null, null, false) />
	var geoData = new Array();
	<#list geoList as geo>
		var row = {};			
		row['geoId'] = '${geo.geoId?if_exists}';
		row['geoName'] = '${geo.geoName?if_exists}';
		geoData[${geo_index}] = row; 
	</#list>
</script>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'geoId', type: 'string'}
					 ]"/>
<#assign columnlist="{ text: '${uiLabelMap.accGeoName}', datafield: 'geoId', editable: false, 
					   cellsrenderer: function(row, colum, value){
					 		for(i = 0; i < geoData.length; i++){
					 			if(value == geoData[i].geoId){
					 				return \"<span>\" + geoData[i].geoId + '[' + geoData[i].geoName + ']' + \"</span>\";
					 			}
					 		}
					 		return \"<span>\" + value + \"</span>\";
					 	}	
					 }
					 "/>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="false"
		 url="jqxGeneralServicer?sname=JQListAgreementGeographicalApplic&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 createUrl="jqxGeneralServicer?sname=createAgreementGeographicalApplic&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 removeUrl="jqxGeneralServicer?sname=removeAgreementGeographicalApplic&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 updateUrl="jqxGeneralServicer?sname=updateAgreementGeographicalApplic&jqaction=U&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 editColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];geoId"
		 />

<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accGeoName}:</td>
	 			<td align="left">
	 				<div id="geoIdAdd">
	 					<div id="jqxGeoGrid" />
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
						      { name: 'geoId', type: 'string' },
						      { name: 'geoName', type: 'string' }
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
				   $("#jqxGeoGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxGeoGrid").jqxGrid('updatebounddata');
				},
				sortcolumn: 'geoId',
               	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListGeos',
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
	$('#geoIdAdd').jqxDropDownButton({ width: 215, height: 25});
	$("#jqxGeoGrid").jqxGrid({
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
				{ text: '${uiLabelMap.accGeoId}', datafield: 'geoId', width: '50%'},
				{ text: '${uiLabelMap.accGeoName}', datafield: 'geoName'},
			]
		});

	$("#jqxGeoGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxGeoGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['geoId'] + '</div>';
                $("#geoIdAdd").jqxDropDownButton('setContent', dropDownContent);
            });
	
$('#alterpopupWindow').on('open', function (event) {		
	$("#geoIdAdd").jqxDropDownButton('val', null);	   	
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
        		geoId:$('#geoIdAdd').val(), 
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);          		 
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>