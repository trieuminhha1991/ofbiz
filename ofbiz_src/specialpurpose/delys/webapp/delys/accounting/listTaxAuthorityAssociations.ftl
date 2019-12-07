<script type="text/javascript">
	<#assign taalength = listTaxAuthAssocTypes.size()/>
    <#if listTaxAuthAssocTypes?size gt 0>
	    <#assign taaType="var taaType = ['" + StringUtil.wrapString(listTaxAuthAssocTypes.get(0).taxAuthorityAssocTypeId?if_exists) + "'"/>
		<#assign taaTypeDes="var taaTypeDes = ['" + StringUtil.wrapString(listTaxAuthAssocTypes.get(0).description?if_exists) +"'"/>
		<#if listTaxAuthAssocTypes?size gt 1>
			<#list 1..(taalength - 1) as i>
				<#assign taaType=taaType + ",'" + StringUtil.wrapString(listTaxAuthAssocTypes.get(i).taxAuthorityAssocTypeId?if_exists) + "'"/>
				<#assign taaTypeDes=taaTypeDes + ",\"" + StringUtil.wrapString(listTaxAuthAssocTypes.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign taaType=taaType + "];"/>
		<#assign taaTypeDes=taaTypeDes + "];"/>
	<#else>
    	<#assign taaType="var taaType = [];"/>
    	<#assign taaTypeDes="var taaTypeDes = [];"/>
    </#if>
	${taaType}
	${taaTypeDes}
	var taaData = new Array();
	for(i = 0; i < ${taalength}; i++){
		var row = {};
		row["taxAuthorityAssocTypeId"] = taaType[i];
		row["description"] = taaTypeDes[i];
		taaData[i] = row;
	}
</script>
<#assign dataField="[{ name: 'toTaxAuthGeoId', type: 'string'},
					 { name: 'toTaxAuthPartyId', type: 'string'},
					 { name: 'fromDate', type: 'datetime'},
					 { name: 'thruDate', type: 'datetime'},
					 { name: 'taxAuthorityAssocTypeId', type: 'string'},
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_toTaxAuthGeoId}', datafield: 'toTaxAuthGeoId'},
					 { text: '${uiLabelMap.FormFieldTitle_toTaxAuthPartyId}', datafield: 'toTaxAuthPartyId'},
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'range', width: 250, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.accThruDate}', datafield: 'thruDate',cellsformat: 'd', filtertype: 'range', width: 250, cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.accTaxAuthorityAssocTypeId}', datafield: 'taxAuthorityAssocTypeId'}
					"/>
	
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow"
		 url="jqxGeneralServicer?sname=JQListTaxAuthorityAssocs&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 createUrl="jqxGeneralServicer?sname=createTaxAuthorityAssoc&jqaction=C&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}" addColumns="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];toTaxAuthGeoId;toTaxAuthPartyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);taxAuthorityAssocTypeId"
		 removeUrl="jqxGeneralServicer?sname=deleteTaxAuthorityAssoc&jqaction=D&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}" deleteColumn="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];toTaxAuthGeoId;toTaxAuthPartyId;fromDate(java.sql.Timestamp)"
		 />
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_toTaxAuthGeoId}:</td>
	 			<td align="left">
	 				<div id="toTaxAuthGeoId">
						<div style="border-color: transparent;" id="jqxGeoGrid"></div>	 				
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_toTaxAuthPartyId}:</td>
	 			<td align="left">
	 				<div id="toTaxAuthPartyId">
	 					<div style="border-color: transparent;" id="jqxPartyGrid"></div>
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.fromDate}:</td>
	 			<td align="left"><div id="fromDate"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accThruDate}:</td>
	 			<td align="left"><div id="thruDate"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.accTaxAuthorityAssocTypeId}:</td>
	 			<td align="left"><div id="taxAuthorityAssocTypeId"></div></td>
    	 	</tr>
            <tr>
                <td align="right"></td>
                <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>
<script>
//Create theme
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
// Create toTaxAuthGeoId
var sourceP = { datafields: [
						      { name: 'geoId', type: 'string' },
						      { name: 'geoTypeId', type: 'string' },
						      { name: 'geoCode', type: 'string' },
						      { name: 'geoName', type: 'string' },
						      { name: 'geoSecCode', type: 'string' },
						      { name: 'abbreviation', type: 'string' },
						      { name: 'wellKnowText', type: 'string' }
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
				url: 'jqxGeneralServicer?sname=JQGetListGeo',
			};
var dataAdapterP = new $.jqx.dataAdapter(sourceP);

$("#toTaxAuthGeoId").jqxDropDownButton({ width: 150, height: 25});
$("#jqxGeoGrid").jqxGrid({
		width:400,
		source: dataAdapterP,
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
		columns: 
		[
			{ text: 'geoId', datafield: 'geoId'},
			{ text: 'geoTypeId', datafield: 'geoTypeId'},
			{ text: 'geoCode', datafield: 'geoCode'},
			{ text: 'geoName', datafield: 'geoName'},
			{ text: 'geoSecCode', datafield: 'geoSecCode'},
			{ text: 'abbreviation', datafield: 'abbreviation'},
			{ text: 'wellKnowText', datafield: 'wellKnowText'}
		]
	});
$("#jqxGeoGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxGeoGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['geoId'] + '</div>';
                $("#toTaxAuthGeoId").jqxDropDownButton('setContent', dropDownContent);
            });
            
// Create taxAuthPartyId
var sourcePG = { datafields: [
						      { name: 'partyId', type: 'string' },
						      { name: 'partyTypeId', type: 'string' },
						      { name: 'lastName', type: 'string' },
						      { name: 'firstName', type: 'string' },
						      { name: 'groupName', type: 'string' },
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
				   $("#jqxPartyGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxPartyGrid").jqxGrid('updatebounddata');
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
				url: 'jqxGeneralServicer?sname=JQGetListParties',
			};
var dataAdapterPG = new $.jqx.dataAdapter(sourcePG);
$("#toTaxAuthPartyId").jqxDropDownButton({ width: 150, height: 25});
$("#jqxPartyGrid").jqxGrid({
		width:400,
		source: dataAdapterPG,
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
		columns: 
		[
			{ text: 'partyId', datafield: 'partyId'},
			{ text: 'partyTypeId', datafield: 'partyTypeId'},
			{ text: 'lastName', datafield: 'lastName'},
			{ text: 'firstName', datafield: 'firstName'},
			{ text: 'groupName', datafield: 'groupName'}
		]
	});
$("#jqxPartyGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
                $("#toTaxAuthPartyId").jqxDropDownButton('setContent', dropDownContent);
            });

//Create FromDate
$("#fromDate").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});

//Create ThruDate
$("#thruDate").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});

//Create TaxAuthorityAssocType

var sourceTAA = {
	localdata: taaData,
	datatype: "array"
};
var dataAdapterTAA = new $.jqx.dataAdapter(sourceTAA);
$("#taxAuthorityAssocTypeId").jqxDropDownList({ selectedIndex: 0,  source: dataAdapterTAA, displayMember: "description", valueMember: "taxAuthorityAssocTypeId"});

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
        		toTaxAuthGeoId:$('#toTaxAuthGeoId').val(), 
        		toTaxAuthPartyId:$('#toTaxAuthPartyId').val(),
        		fromDate:"Date(" + $('#fromDate').jqxDateTimeInput('getDate').getTime() + ")",
        		thruDate:"Date(" + $('#thruDate').jqxDateTimeInput('getDate').getTime() + ")",
        		taxAuthorityAssocTypeId:$('#taxAuthorityAssocTypeId').val()           
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>