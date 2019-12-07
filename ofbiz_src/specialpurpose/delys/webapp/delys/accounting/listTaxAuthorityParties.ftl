<script>
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
				    sourcePG.totalrecords = data.TotalRows;
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

var isExData = new Array();
var yExData = {};
var nExData = {};
	yExData['isExempt'] = "Y";
	yExData['description'] = "Yes";
	nExData['isExempt'] = "N";
	nExData['description'] = "No";
	isExData[0] = yExData;
	isExData[1] = nExData;

var isNeData = new Array();
var yNeData = {};
var nNeData = {};
	yNeData['isNexus'] = "Y";
	yNeData['description'] = "Yes";
	nNeData['isNexus'] = "N";
	nNeData['description'] = "No";
	isNeData[0] = yNeData;
	isNeData[1] = nNeData;
</script>
<#assign dataField="[{ name: 'partyId', type: 'string'},
					 { name: 'fromDate', type: 'date'},
					 { name: 'thruDate', type: 'date'},
					 { name: 'partyTaxId', type: 'string'},
					 { name: 'isExempt', type: 'string'},
					 { name: 'isNexus', type: 'string'},
					]"/>

<#assign columnlist="{ text: '${uiLabelMap.partyId}', datafield: 'partyId',columntype: 'template', cellsrenderer:
                     	 function(row, colum, value) {
                        		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						dataParty = sourcePG.records;
        						for(i=0;i < dataParty.length; i++){
        						if(dataParty[i].partyId == data.partyId){
        							return \"<span>\" + dataParty[i].groupName + \"</span>\";
        						}
        					 }
        					return \"\";
                        	},
                          createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
                            editor.append('<div id=\"jqxPGGrid\"></div>');
                            editor.jqxDropDownButton();
                            // prepare the data
						    var dataAdapterPG = new $.jqx.dataAdapter(sourcePG,
						    {
						    	formatData: function (data) {
							    	if (data.filterscount) {
			                            var filterListFields = \"\";
			                            for (var i = 0; i < data.filterscount; i++) {
			                                var filterValue = data[\"filtervalue\" + i];
			                                var filterCondition = data[\"filtercondition\" + i];
			                                var filterDataField = data[\"filterdatafield\" + i];
			                                var filterOperator = data[\"filteroperator\" + i];
			                                filterListFields += \"|OLBIUS|\" + filterDataField;
			                                filterListFields += \"|SUIBLO|\" + filterValue;
			                                filterListFields += \"|SUIBLO|\" + filterCondition;
			                                filterListFields += \"|SUIBLO|\" + filterOperator;
			                            }
			                            data.filterListFields = filterListFields;
			                        }
			                         data.$skip = data.pagenum * data.pagesize;
			                         data.$top = data.pagesize;
			                         data.$inlinecount = \"allpages\";
			                        return data;
			                    },
			                    loadError: function (xhr, status, error) {
				                    alert(error);
				                },
				                downloadComplete: function (data, status, xhr) {
				                        if (!sourcePG.totalRecords) {
				                            sourcePG.totalRecords = parseInt(data[\"odata.count\"]);
				                        }
				                }, 
				                beforeLoadComplete: function (records) {
				                	for (var i = 0; i < records.length; i++) {
				                		if(typeof(records[i])==\"object\"){
				                			for(var key in records[i]) {
				                				var value = records[i][key];
				                				if(value != null && typeof(value) == \"object\" && typeof(value) != null){
				                					var date = new Date(records[i][key][\"time\"]);
				                					records[i][key] = date;
				                				}
				                			}
				                		}
				                	}
				                }
						    });
				            $(\"#jqxPGGrid\").jqxGrid({
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
				                columns: [
				                  { text: 'partyId', datafield: 'partyId'},
				                  { text: 'groupName', datafield: 'groupName'},
				                ]
				            });
				            $(\"#jqxPGGrid\").on('rowselect', function (event) {
				            	//$(\"#jqxPGGrid\").jqxGrid({ disabled: true});
                                var args = event.args;
                                var row = $(\"#jqxPGGrid\").jqxGrid('getrowdata', args.rowindex);
                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
                                //$('#dropDownButtonContentjqxDD').html(dropDownContent);
                                editor.jqxDropDownButton('setContent', dropDownContent);
                            });
                      },
                        geteditorvalue: function (row, cellvalue, editor) {
                            // return the editor's value.
                            editor.jqxDropDownButton(\"close\");
                            return cellvalue;
                     }},
					 { text: '${uiLabelMap.fromDate}', width: 150, datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', columntype: 'template',
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '300px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
                     	}
                     },
					 { text: '${uiLabelMap.thruDate}', width: 150, datafield: 'thruDate',cellsformat: 'dd/MM/yyyy', columntype: 'template',
                     	createeditor: function (row, column, editor) {
                     		editor.jqxDateTimeInput({ width: '300px', height: '25px',  formatString: 'dd-MM-yyyy hh:mm:ss' });
                     	},
                     },
					 { text: '${uiLabelMap.partyTaxId}', datafield: 'partyTaxId'},
					 { text: '${uiLabelMap.isExempt}', datafield: 'isExempt', columntype: 'dropdownlist', cellsrenderer:
                     	 function(row, colum, value) {
                        		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						for(i=0;i < isExData.length; i++){
        						if(isExData[i].isExempt == data.isExempt){
        							return \"<span>\" + isExData[i].description + \"</span>\";
        						}
        					 }
        					return \"\";
                        	},
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: isExData, displayMember:\"isExempt\", valueMember: \"description\",
                            renderer: function (index, label, value) {
			                    var datarecord = isExData[index];
			                    return datarecord.description;
			                  }
                        });
                        }
					 },
					 { text: '${uiLabelMap.isNexus}', datafield: 'isNexus', columntype: 'dropdownlist', cellsrenderer:
                     	 function(row, colum, value) {
                        		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						for(i=0;i < isNeData.length; i++){
        						if(isNeData[i].isNexus == data.isNexus){
        							return \"<span>\" + isNeData[i].description + \"</span>\";
        						}
        					 }
        					return \"\";
                        	},
					 		createeditor: function (row, column, editor) {
                            editor.jqxDropDownList({source: isNeData, displayMember:\"isNexus\", valueMember: \"description\",
                            renderer: function (index, label, value) {
			                    var datarecord = isNeData[index];
			                    return datarecord.description;
			                  }
                        });
                        }
                        }
					"/>
	
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" alternativeAddPopup="alterpopupWindow" deleterow="true" editable="true"
		 url="jqxGeneralServicer?sname=JQListTaxAuthorityParties&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 createUrl="jqxGeneralServicer?sname=createPartyTaxAuthInfo&jqaction=C&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 removeUrl="jqxGeneralServicer?sname=deletePartyTaxAuthInfo&jqaction=D&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 updateUrl="jqxGeneralServicer?sname=updatePartyTaxAuthInfo&jqaction=U&taxAuthPartyId=${parameters.taxAuthPartyId}&taxAuthGeoId=${parameters.taxAuthGeoId}"
		 addColumns="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);partyTaxId;isExempt;isNexus"
		 editColumns="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];partyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);partyTaxId;isExempt;isNexus"
		 deleteColumn="taxAuthGeoId[${parameters.taxAuthGeoId}];taxAuthPartyId[${parameters.taxAuthPartyId}];partyId;fromDate(java.sql.Timestamp)"
		 />
		 
<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.partyId}:</td>
	 			<td align="left">
	 				<div id="partyIdAdd">
	 					<div id="jqxPartyGrid" />
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.fromDate}:</td>
	 			<td align="left">
	 				<div id="fromDateAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
	 			<td align="right">${uiLabelMap.thruDate}:</td>
	 			<td align="left">
 					<div id="thruDateAdd">
 					</div>
 				</td>
 			</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.partyTaxId}:</td>
	 			<td align="left">
	 				<input id="partyTaxIdAdd">
	 				</input>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.isExempt}:</td>
	 			<td align="left">
	 				<div id="isExemptAdd">
	 				</div>
	 			</td>
    	 	</tr>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.isNexus}:</td>
	 			<td align="left">
	 				<div id="isNexusAdd">
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
<script>
//Create theme
 $.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
//Create partyId
var dataAdapterPG = new $.jqx.dataAdapter(sourcePG);
$("#partyIdAdd").jqxDropDownButton({ width: 150, height: 25});
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
                $("#partyIdAdd").jqxDropDownButton('setContent', dropDownContent);
            });
//Create FromDate
$("#fromDateAdd").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});

//Create ThruDate
$("#thruDateAdd").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd-MM-yyyy hh:mm:ss'});

//Create partyTaxId
$("#partyTaxIdAdd").jqxInput();

//Create isExempt
$("#isExemptAdd").jqxDropDownList({ source: isExData, selectedIndex: 0, displayMember: "description", valueMember: "isExempt"});

//Create isNexus
$("#isNexusAdd").jqxDropDownList({ source: isNeData, selectedIndex: 0, displayMember: "description", valueMember: "isNexus"});
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
        		partyId:$('#partyIdAdd').val(), 
        		fromDate:"Date(" + $('#fromDateAdd').jqxDateTimeInput('getDate').getTime() + ")",
        		thruDate:"Date(" + $('#thruDateAdd').jqxDateTimeInput('getDate').getTime() + ")",
        		partyTaxId:$('#partyTaxIdAdd').val(),
        		isExempt:$('#isExemptAdd').val(),
        		isNexus:$('#isNexusAdd').val()           
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>