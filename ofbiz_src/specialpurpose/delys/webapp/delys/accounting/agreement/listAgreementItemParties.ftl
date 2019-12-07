<script type="text/javascript">
	<#assign partyNameList = delegator.findList("PartyNameView", null, null, null, null, false) />
	var dataPartyNameView = new Array();

	<#list partyNameList as item >
		var row = {};
		row['partyId'] = '${item.partyId?if_exists}';
		row['partyName'] = '${item.groupName?if_exists} ${item.firstName?if_exists} ${item.lastName?if_exists}';
		dataPartyNameView[${item_index}] = row;
	</#list>
</script>

<div id="jqxwindowpartyId" style="display:none;">
	<div>${uiLabelMap.SelectPartyId}</div>
	<div style="overflow: hidden;">
		<table id="PartyIdFrom">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowpartyIdkey" value=""/>
					<input type="hidden" id="jqxwindowpartyIdvalue" value=""/>
					<div id="jqxgridpartyid"></div>
				</td>
			</tr>
		    <tr>
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave3" value="${uiLabelMap.CommonSave}" /><input id="alterCancel3" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>

<@jqGridMinimumLib/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	$("#jqxwindowpartyId").jqxWindow({
        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
    });
    $('#jqxwindowpartyId').on('open', function (event) {
    	var offset = $("#jqxgrid").offset();
   		$("#jqxwindowpartyId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});
	$("#alterSave3").jqxButton({theme: theme});
	$("#alterCancel3").jqxButton({theme: theme});
	$("#alterSave3").click(function () {
		var tIndex = $('#jqxgridpartyid').jqxGrid('selectedrowindex');
		var data = $('#jqxgridpartyid').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowpartyIdkey').val()).val(data.partyId);
		$("#jqxwindowpartyId").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowpartyIdkey').val()).trigger(e);
	});
	// From party
    var sourceF =
    {
        datafields:
        [
            { name: 'partyId', type: 'string' },
            { name: 'partyTypeId', type: 'string' },
            { name: 'firstName', type: 'string' },
            { name: 'lastName', type: 'string' },
            { name: 'groupName', type: 'string' }
        ],
        cache: false,
        root: 'results',
        datatype: "json",
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        },
        beforeprocessing: function (data) {
            sourceF.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridpartyid").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridpartyid").jqxGrid('updatebounddata');
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
    var dataAdapterF = new $.jqx.dataAdapter(sourceF,
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
                if (!sourceF.totalRecords) {
                    sourceF.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
    $('#jqxgridpartyid').jqxGrid(
    {
        width:800,
        source: dataAdapterF,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        showfilterrow: false,
        theme: theme, 
        autoheight:true,
        pageable: true,
        pagesizeoptions: ['5', '10', '15'],
        ready:function(){
        },
        rendergridrows: function(obj)
		{
			return obj.data;
		},
         columns: [
          { text: '${uiLabelMap.accPartyId}', datafield: 'partyId', width:'20%'},
          { text: '${uiLabelMap.accPartyTypeId}', datafield: 'partyTypeId', width:'20%'},
          { text: '${uiLabelMap.accFirstName}', datafield: 'firstName', width:'20%'},
          { text: '${uiLabelMap.accLastName}', datafield: 'lastName', width:'20%'},
          { text: '${uiLabelMap.accGroupName}', datafield: 'groupName'}
        ]
    });    
    
    $(document).keydown(function(event){
	    if(event.ctrlKey)
	        cntrlIsPressed = true;
	});
	
	$(document).keyup(function(event){
		if(event.which=='17')
	    	cntrlIsPressed = false;
	});
	var cntrlIsPressed = false;
    
</script>

<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'agreementItemSeqId', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.accPartyName}', filtertype: 'olbiusdropgrid', datafield: 'partyId', editable: false,
                     	 cellsrenderer: function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	for(i=0; i<dataPartyNameView.length;i++){
                        		if(dataPartyNameView[i].partyId==value){                        			
									return '<a style = \"margin-left: 10px\" href=' + '/delys/control/EditAgreementItemParty?partyId=' + dataPartyNameView[i].partyId + '&' + 'agreementId=' + data.agreementId + '&' + 'agreementItemSeqId=' + data.agreementItemSeqId + '>' + \"[\" + dataPartyNameView[i].partyId + \"]\" + \" \" + dataPartyNameView[i].partyName + '</a>';
                    			}
                        	}
                        	return \"<span>\" + value + \"</span>\";
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				widget.width('100%');
			   			}},
					 "/>

<@jqGrid  filterable="true" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" deleterow="true" alternativeAddPopup="alterpopupWindow" editable="true" jqGridMinimumLibEnable="false"
		 	url="jqxGeneralServicer?sname=JQListAgreementItemParties&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 	createUrl="jqxGeneralServicer?sname=createAgreementPartyApplic&jqaction=C&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 	removeUrl="jqxGeneralServicer?sname=removeAgreementPartyApplic&jqaction=D&agreementId=${parameters.agreementId}&agreementItemSeqId=${parameters.agreementItemSeqId}"
		 	addColumns="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];partyId"
		 	deleteColumn="agreementId[${parameters.agreementId}];agreementItemSeqId[${parameters.agreementItemSeqId}];partyId"
		 />
					
	<div id="alterpopupWindow" style="display:none;">
	    <div>${uiLabelMap.accCreateNew}</div>
	    <div style="overflow: hidden;">
			<table>
				<tr>
					<td align="left">
						${uiLabelMap.accPartyName}
					</td>
					<td align="left">
				       <div id="jqxdropdownbuttonToParty">
				       	 <div id="jqxgridToParty"></div>
				       </div>
				    </td>
				</tr>
				<tr>
			        <td align="right"></td>
			        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave4" value="${uiLabelMap.CommonSave}" /><input id="alterCancel4" type="button" value="${uiLabelMap.CommonCancel}" /></td>
			    </tr>
			</table>
		</div>
	</div>		
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	var dataPAGA = new Array();
    var row;
    <#assign partyNameList = delegator.findList("PartyNameView", null, null, null, null, false) />
    <#list partyNameList as lpaga>
        row = {};
		row['partyId'] = '${lpaga.partyId?if_exists}';
		row['partyName'] = '${lpaga.groupName?if_exists} ${lpaga.firstName?if_exists} ${lpaga.lastName?if_exists}';
        dataPAGA[${lpaga_index}] = row;
    </#list>
    
    
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var outFilterCondition = "";
	$("#alterpopupWindow").jqxWindow({
        width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel4"), modalOpacity: 0.7, theme:theme           
    });
    $("#alterSave4").jqxButton({theme: theme});
	$("#alterCancel4").jqxButton({theme: theme});
    // ToParty
    var sourceP2 =
    {
        datafields:
        [
            { name: 'partyId', type: 'string' },
            { name: 'partyTypeId', type: 'string' },
            { name: 'firstName', type: 'string' },
            { name: 'lastName', type: 'string' },
            { name: 'groupName', type: 'string' }
        ],
        cache: false,
        root: 'results',
        datatype: "json",
        updaterow: function (rowid, rowdata) {
            // synchronize with the server - send update command   
        },
        beforeprocessing: function (data) {
            sourceP2.totalrecords = data.TotalRows;
        },
        filter: function () {
            // update the grid and send a request to the server.
            $("#jqxgridToParty").jqxGrid('updatebounddata');
        },
        pager: function (pagenum, pagesize, oldpagenum) {
            // callback called when a page or page size is changed.
        },
        sort: function () {
            $("#jqxgridToParty").jqxGrid('updatebounddata');
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
    
    
    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2,
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
                if (!sourceP2.totalRecords) {
                    sourceP2.totalRecords = parseInt(data['odata.count']);
                }
        }
    });
        
    $("#jqxdropdownbuttonToParty").jqxDropDownButton({ theme: theme, width: 400, height: 25});
    $("#jqxgridToParty").jqxGrid({
    	width:400,
        source: dataAdapterP2,
        filterable: true,
        virtualmode: true, 
        showfilterrow: true,
        sortable:true,
        theme: theme,
        editable: false,
        autoheight:true,
        pageable: true,
        rendergridrows: function(obj)
		{
			return obj.data;
		},
        columns: [
          { text: '${uiLabelMap.accPartyId}', datafield: 'partyId', width :'20%'},
          { text: '${uiLabelMap.accPartyTypeId}', datafield: 'partyTypeId', width :'20%'},
          { text: '${uiLabelMap.accFirstName}', datafield: 'firstName', width :'20%'},
          { text: '${uiLabelMap.accLastName}', datafield: 'lastName', width :'20%'},
          { text: '${uiLabelMap.accGroupName}', datafield: 'groupName'}
        ]
    });
    $("#jqxgridToParty").on('rowselect', function (event) {
        var args = event.args;
        var row = $("#jqxgridToParty").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['partyId'] +'</div>';
        $('#jqxdropdownbuttonToParty').jqxDropDownButton('setContent', dropDownContent);
    });
    $("#alterCancel4").jqxButton({theme: theme});
    $("#alterSave4").jqxButton({theme: theme});

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave4").click(function () {    
	    	var row;
	        row = { 
	        		partyId:$('#jqxdropdownbuttonToParty').val(),
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');        
    });
</script>		
