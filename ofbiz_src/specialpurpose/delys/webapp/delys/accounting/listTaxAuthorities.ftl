<script>
	var linkRequireTaxRenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if(data.requireTaxIdForExemption == "Y"){
        	return "<span>" + "${uiLabelMap.CommonYes}" + "</span>";
        }else{
        	return "<span>" + "${uiLabelMap.CommonNo}" + "</span>";
        }
    }
    
    var linkIncludeTaxRenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if(data.includeTaxInPrice == "Y"){
        	return "<span>" + "${uiLabelMap.CommonYes}" + "</span>";
        }else{
        	return "<span>" + "${uiLabelMap.CommonNo}" + "</span>";
        }
    }
    
    var linkEditRenderer = function (row, column, value) {
		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        return "<a style = \"margin-left: 10px\" href=" + "EditTaxAuthorityCategories?taxAuthPartyId=" + data.taxAuthPartyId + "&taxAuthGeoId=" + data.taxAuthGeoId + "> ${uiLabelMap.CommonEdit}" + "</a>"
    }
 //Prepare for requireTaxIdForExemption
  var rtData = new Array();
	var row = {};
	row["name"] = "${uiLabelMap.CommonYes}";
	row["requireTaxIdForExemption"] = "Y";
	rtData[0] = row;
	var row = {};
	row["name"] = "${uiLabelMap.CommonNo}";
	row["requireTaxIdForExemption"] = "N";
	rtData[1] = row;
 var rtDataSource =
    {
        localdata: rtData,
        datatype: "array"
    };

 //Prepare for includeTaxInPrice
var itData = new Array();
	var row = {};
	row["name"] = "${uiLabelMap.CommonYes}";
	row["includeTaxInPrice"] = "Y";
	itData[0] = row;
	var row = {};
	row["name"] = "${uiLabelMap.CommonNo}";
	row["includeTaxInPrice"] = "N";
	itData[1] = row;
 var itDataSource =
    {
        localdata: itData,
        datatype: "array"
    };

</script>

<#assign dataField="[{ name: 'taxAuthGeoId', type: 'string' },
					 { name: 'taxAuthPartyId', type: 'string'},
					 { name: 'requireTaxIdForExemption', type: 'string'},
					 { name: 'taxIdFormatPattern', type: 'string'},
					 { name: 'formulaCode', type: 'string'},
					 { name: 'includeTaxInPrice', type: 'string'},
					 { name: 'edit', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.AccountingTaxAuthorityGeo}', datafield: 'taxAuthGeoId', width: 180},
					 { text: '${uiLabelMap.AccountingTaxAuthority}', datafield: 'taxAuthPartyId'},
					 { text: '${uiLabelMap.FormFieldTitle_requireTaxIdForExemption}', datafield: 'requireTaxIdForExemption', cellsrenderer:linkRequireTaxRenderer,columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
				            var adaptRTDataSource = new $.jqx.dataAdapter(rtDataSource);
                            editor.jqxDropDownList({source: adaptRTDataSource, displayMember:\"requireTaxIdForExemption\", valueMember: \"requireTaxIdForExemption\",
                            renderer: function (index, label, value) {
			                    var datarecord = rtData[index];
			                    return datarecord.name;
			                } 
                        });
					 }, width: 250},
					 { text: '${uiLabelMap.FormFieldTitle_taxIdFormatPattern}', datafield: 'taxIdFormatPattern'},
					 { text: '${uiLabelMap.FormulaCode}', datafield: 'formulaCode'},
					 { text: '${uiLabelMap.FormFieldTitle_includeTaxInPrice}', datafield: 'includeTaxInPrice', cellsrenderer:linkIncludeTaxRenderer, columntype: 'dropdownlist',
					 		createeditor: function (row, column, editor) {
				            var adaptITDataSource = new $.jqx.dataAdapter(itDataSource);
                            editor.jqxDropDownList({source: adaptITDataSource, displayMember:\"includeTaxInPrice\", valueMember: \"includeTaxInPrice\",
                            renderer: function (index, label, value) {
			                    var datarecord = itData[index];
			                    return datarecord.name;
			                } 
                        });
					 }, width: 150},
					 {text: '${uiLabelMap.CommonUpdate}',datafield: 'edit', cellsrenderer:linkEditRenderer, width: 80}
					 "/>
	
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=JQGetListTaxAuthorities"
		 createUrl="jqxGeneralServicer?sname=createTaxAuthority&jqaction=C" addColumns="taxAuthGeoId;taxAuthPartyId;requireTaxIdForExemption;taxIdFormatPattern;formulaCode;includeTaxInPrice"
		 />
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.AccountingTaxAuthorityGeo}:</td>
	 			<td align="left">
	 				<div id="taxAuthGeoId">
						<div style="border-color: transparent;" id="jqxGeoGrid"></div>	 				
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.AccountingTaxAuthority}:</td>
	 			<td align="left">
	 				<div id="taxAuthPartyId">
	 					<div style="border-color: transparent;" id="jqxPartyGrid"></div>
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_requireTaxIdForExemption}:</td>
	 			<td align="left"><div id="requireTaxIdForExemption"></div></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_taxIdFormatPattern}':</td>
	 			<td align="left"><input id="taxIdFormatPattern"></input></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormulaCode}:</td>
	 			<td align="left"><input id="formulaCode"></input></td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.FormFieldTitle_includeTaxInPrice}:</td>
	 			<td align="left"><div id="includeTaxInPrice"></div></td>
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
// Create taxAuthGeoId
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

$("#taxAuthGeoId").jqxDropDownButton({ width: 150, height: 25});
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
                $("#taxAuthGeoId").jqxDropDownButton('setContent', dropDownContent);
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
$("#taxAuthPartyId").jqxDropDownButton({ width: 150, height: 25});
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
                $("#taxAuthPartyId").jqxDropDownButton('setContent', dropDownContent);
            });

//Create requireTaxIdForExemption
var adaptRTDataSource = new $.jqx.dataAdapter(rtDataSource);
$("#requireTaxIdForExemption").jqxDropDownList({ selectedIndex: 0,  source: adaptRTDataSource, displayMember: "name", valueMember: "requireTaxIdForExemption"});

//Create includeTaxInPrice
var adaptITDataSource = new $.jqx.dataAdapter(itDataSource);
$("#includeTaxInPrice").jqxDropDownList({ selectedIndex: 0,  source: adaptITDataSource, displayMember: "name", valueMember: "includeTaxInPrice"});

//Create taxIdFormatPattern
$("#taxIdFormatPattern").jqxInput();

//Create formulaCode
$("#formulaCode").jqxInput();

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
        		taxAuthGeoId:$('#taxAuthGeoId').val(),
        		taxAuthPartyId:$('#taxAuthPartyId').val(),
        		requireTaxIdForExemption:$('#requireTaxIdForExemption').val(),
        		taxIdFormatPattern:$('#taxIdFormatPattern').val(),
        		formulaCode:$('#formulaCode').val(),
        		includeTaxInPrice:$('#includeTaxInPrice').val()           
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>