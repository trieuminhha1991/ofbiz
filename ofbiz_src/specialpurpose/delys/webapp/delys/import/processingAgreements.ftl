<#if !parameters.agreementId?exists>
   <#assign dataField="[{ name: 'agreementId', type: 'string'},
					   { name: 'attrValue', type: 'string'},
					   { name: 'agreementDate', type: 'date', other: 'Timestamp'},
					   { name: 'partyIdFrom', type: 'string'},
					   { name: 'partyIdTo', type: 'string'},
					   { name: 'description', type: 'string'},
					   { name: 'statusId', type: 'string'},
					   ]"/>
	<#assign columnlist="
								{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', width: 120, editable: false, cellsrenderer:
								   function(row, colum, value){
								        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								        var agreementId = data.agreementId;
								        var link = 'detailPurchaseAgreement?agreementId=' + agreementId;
								        return '<span><a href=\"' + link + '\">' + agreementId + '</a></span>';
								}},
								{ text: '${uiLabelMap.AgreementName}' ,filterable: true, sortable: true, datafield: 'attrValue', minwidth: 200, editable: false},
								{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', width: 200, editable: false, filtertype: 'date', cellsformat: 'dd/MM/yyyy - hh:mm:ss'},
								{ text: '${uiLabelMap.SlideA}', datafield: 'partyIdFrom', width: 150, filtertype: 'olbiusdropgrid', editable: false, cellsrenderer:
								function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var partyIdFrom = data.partyIdFrom;
								var partyFrom = getPartyNameView(partyIdFrom);
								return '<span>' + partyFrom + '</span>';
								},createfilterwidget: function (column, columnElement, widget) {
									widget.width(140);
								}},
								{ text: '${uiLabelMap.SlideB}', datafield: 'partyIdTo', width: 150, filtertype: 'olbiusdropgrid', editable: false, cellsrenderer:
								function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var partyIdTo = data.partyIdTo;
								var partyTo = getPartyNameView(partyIdTo);
								return '<span>' + partyTo + '</span>';
								},createfilterwidget: function (column, columnElement, widget) {
									widget.width(140);
								}},
								{ text: '${uiLabelMap.description}', datafield: 'description', minWidth: 150, editable: false, cellsrenderer:
								   function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var dataShow = data.description;
								var dataShort = executeMyData(dataShow);
								var id = data.agreementId;
								id = 'description' + id;
								return '<span id=\"' + id + '\" onmouseenter=\"showMore(' + \"'\" + dataShow + \"'\" + ',' + \"'\" + id  + \"'\" + ')\" >' + dataShort + '</span>';
								}},
								{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: false, columntype: 'dropdownlist', filtertype: 'checkedlist', createeditor: 
								function(row, column, editor){
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatus, displayMember: 'statusId', valueMember: 'statusId' ,
								    renderer: function (index, label, value) {
								        var datarecord = listStatus[index];
								        return datarecord.description;
								    } });
								}, cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
								if (newvalue == '') return oldvalue;
								},cellsrenderer:
								function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var statusId = data.statusId;
								var status = getStatus(statusId);
								return '<span>' + status + '</span>';
								},createfilterwidget: function (column, htmlElement, editor) {
								var oneTurn = true;
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatus, displayMember: 'statusId', valueMember: 'statusId' ,
								    renderer: function (index, label, value) {
								        var datarecord = listStatus[index];
								        if (index == '0') {
								        	if (oneTurn) {
								//        		editor.jqxDropDownList('removeAt', 0);
								            	oneTurn = false;
											}
										}
								        return datarecord.description;
								    } });
								editor.jqxDropDownList('checkAll');
								}}
								"/>

<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
							showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" 
								customcontrol1="icon-plus-sign open-sans@${uiLabelMap.CommonCreateNew}@getImportPlanToCreateAgreement"
							url="jqxGeneralServicer?sname=JQGetListProcessingAgreements&agreementId=${parameters.agreementId?if_exists}" updateUrl="jqxGeneralServicer?sname=updateAgreementStatus&jqaction=U"
							createUrl="jqxGeneralServicer?sname=createPurchaseAgreements&jqaction=C"
							addColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
							editColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
						/>
   </#if>

   <#if security.hasEntityPermission("AGREEMENT", "_PURCHASE_APPROVE", session) &&  parameters.agreementId?exists>
   <#assign dataField="[{ name: 'agreementId', type: 'string'},
					   { name: 'attrValue', type: 'string'},
					   { name: 'agreementDate', type: 'date', other: 'Timestamp'},
					   { name: 'partyIdFrom', type: 'string'},
					   { name: 'partyIdTo', type: 'string'},
					   { name: 'description', type: 'string'},
					   { name: 'statusId', type: 'string'},
					   ]"/>
<#assign columnlist="
								{ text: '${uiLabelMap.AgreementId}', datafield: 'agreementId', width: 120, editable: false, cellsrenderer:
								   function(row, colum, value){
								        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								        var agreementId = data.agreementId;
								        var link = 'detailPurchaseAgreement?agreementId=' + agreementId;
								        return '<span><a href=\"' + link + '\">' + agreementId + '</a></span>';
								}},
								{ text: '${uiLabelMap.AgreementName}' ,filterable: true, sortable: true, datafield: 'attrValue', minwidth: 200, editable: false},
								{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
								{ text: '${uiLabelMap.SlideA}', datafield: 'partyIdFrom', width: 150, filtertype: 'olbiusdropgrid', editable: false, cellsrenderer:
									function(row, colum, value){
										var data = $('#jqxgrid').jqxGrid('getrowdata', row);
										var partyIdFrom = data.partyIdFrom;
										var partyFrom = getPartyNameView(partyIdFrom);
										return '<span>' + partyFrom + '</span>';
									},createfilterwidget: function (column, columnElement, widget) {
										widget.width(140);
								}},
								{ text: '${uiLabelMap.SlideB}', datafield: 'partyIdTo', width: 150, filtertype: 'olbiusdropgrid', editable: false, cellsrenderer:
								function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var partyIdTo = data.partyIdTo;
								var partyTo = getPartyNameView(partyIdTo);
								return '<span>' + partyTo + '</span>';
								},createfilterwidget: function (column, columnElement, widget) {
									widget.width(140);
								}},
								{ text: '${uiLabelMap.description}', datafield: 'description', minWidth: 150, editable: false, cellsrenderer:
								   function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var dataShow = data.description;
								var dataShort = executeMyData(dataShow);
								var id = data.agreementId;
								id = 'description' + id;
								return '<span id=\"' + id + '\" onmouseenter=\"showMore(' + \"'\" + dataShow + \"'\" + ',' + \"'\" + id  + \"'\" + ')\" >' + dataShort + '</span>';
								}},
								{ text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: false, columntype: 'dropdownlist', filtertype: 'checkedlist', createeditor: 
								function(row, column, editor){
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatus, displayMember: 'statusId', valueMember: 'statusId' ,
								    renderer: function (index, label, value) {
								        var datarecord = listStatus[index];
								        return datarecord.description;
								    } });
								}, cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
								if (newvalue == '') return oldvalue;
								},cellsrenderer:
								function(row, colum, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var statusId = data.statusId;
								var status = getStatus(statusId);
								return '<span>' + status + '</span>';
								},createfilterwidget: function (column, htmlElement, editor) {
								var oneTurn = true;
								editor.jqxDropDownList({ autoDropDownHeight: true, source: listStatus, displayMember: 'statusId', valueMember: 'statusId' ,
								    renderer: function (index, label, value) {
								        var datarecord = listStatus[index];
								        if (index == '0') {
								        	if (oneTurn) {
								//        		editor.jqxDropDownList('removeAt', 0);
								            	oneTurn = false;
											}
										}
								        return datarecord.description;
								    } });
								editor.jqxDropDownList('checkAll');
								}},{ text: '${uiLabelMap.Approve}', minWidth: 150, editable: false, cellsrenderer:
								   function(row, colum, value){
								    var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								    if (data.statusId != 'AGREEMENT_CREATED') {
								    	var link = 'updateAgreementOnlyStatus?agreementId=' + data.agreementId + '&statusId=AGREEMENT_CREATED';
								    	link = '#';
								        return '<span><a href=\"' + link + '\">' + 'Unapproved' + '</a></span>';
									}else{
										var link = '#';
								        return '<span><a href=\"' + link + '\" onclick=\"MyFunction(' + \"'\" + data.agreementId + \"'\" + ');return false;\">' + 'Approved' + '</a></span>';
									}
								}}
								"/>

<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
							showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" 
								customcontrol1="icon-plus-sign open-sans@${uiLabelMap.CommonCreateNew}@getImportPlanToCreateAgreement"
							url="jqxGeneralServicer?sname=JQGetListProcessingAgreements&agreementId=${parameters.agreementId?if_exists}" updateUrl="jqxGeneralServicer?sname=updateAgreementStatus&jqaction=U"
							createUrl="jqxGeneralServicer?sname=createPurchaseAgreements&jqaction=C"
							addColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
							editColumns="agreementId;agreementDate(java.sql.Timestamp);partyIdFrom;partyIdTo;description;statusId"
						/>
   </#if>

    		        <div id = "myEditor"></div>
    		        
    		        <div id="jqxwindowpartyIdTo">
    		    	<div>${uiLabelMap.SelectPartyId}</div>
    		    	<div style="overflow: hidden;">
    		    		<table id="PartyId">
    		    			<tr>
    		    				<td>
    		    					<input type="hidden" id="jqxwindowpartyIdTokey" value=""/>
    		    					<input type="hidden" id="jqxwindowpartyIdTovalue" value=""/>
    		    					<div id="jqxgridpartyid"></div>
    		    				</td>
    		    			</tr>
    		    		    <tr>
    		    		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave2" value="${uiLabelMap.CommonSave}" /><input id="alterCancel2" type="button" value="${uiLabelMap.CommonCancel}" /></td>
    		    		    </tr>
	    		    	</table>
	    		    </div>
	    		    </div>
	    		    
	    		    <div id="jqxwindowpartyIdFrom">
	    			<div>${uiLabelMap.SelectPartyIdFrom}</div>
	    			<div style="overflow: hidden;">
	    				<table id="PartyIdFrom">
	    					<tr>
	    						<td>
	    							<input type="hidden" id="jqxwindowpartyIdFromkey" value=""/>
	    							<input type="hidden" id="jqxwindowpartyIdFromvalue" value=""/>
	    							<div id="jqxgridpartyidfrom"></div>
	    						</td>
	    					</tr>
	    				    <tr>
	    				        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave3" value="${uiLabelMap.CommonSave}" /><input id="alterCancel3" type="button" value="${uiLabelMap.CommonCancel}" /></td>
	    				    </tr>
	    				</table>
	    			</div>
	    		</div>
	    		
	    		<script type="text/javascript">
	    		$.jqx.theme = 'olbius';  
	    		theme = $.jqx.theme;  
	    		$("#jqxwindowpartyIdFrom").jqxWindow({
	    	        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
	    	    });
	    	    $('#jqxwindowpartyIdFrom').on('open', function (event) {
	    	    	var offset = $("#jqxgrid").offset();
	    	   		$("#jqxwindowpartyIdFrom").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	    		});
	    		$("#alterSave3").jqxButton({theme: theme});
	    		$("#alterCancel3").jqxButton({theme: theme});
	    		$("#alterSave3").click(function () {
	    			var tIndex = $('#jqxgridpartyidfrom').jqxGrid('selectedrowindex');
	    			var data = $('#jqxgridpartyidfrom').jqxGrid('getrowdata', tIndex);
	    			$('#' + $('#jqxwindowpartyIdFromkey').val()).val(data.partyId);
	    			$("#jqxwindowpartyIdFrom").jqxWindow('close');
	    			var e = jQuery.Event("keydown");
	    			e.which = 50; // # Some key code value
	    			$('#' + $('#jqxwindowpartyIdFromkey').val()).trigger(e);
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
	    	            $("#jqxgridpartyidfrom").jqxGrid('updatebounddata');
	    	        },
	    	        pager: function (pagenum, pagesize, oldpagenum) {
	    	            // callback called when a page or page size is changed.
	    	        },
	    	        sort: function () {
	    	            $("#jqxgridpartyidfrom").jqxGrid('updatebounddata');
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
	    	    $('#jqxgridpartyidfrom').jqxGrid(
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
	    	          { text: '${uiLabelMap.SlideA}', datafield: 'partyId', width:150},
	    	          { text: '${uiLabelMap.Type}', datafield: 'partyTypeId', width:200},
	    	          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
	    	          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
	    	          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:150}
	    	        ]
	    	    });
	    	</script>
	   <script type="text/javascript">
	    			$("#jqxwindowpartyIdTo").jqxWindow({
	    		        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
	    		    });
	    		    $('#jqxWindow').on('open', function (event) {
	    		    	var offset = $("#jqxgrid").offset();
	    		   		$("#jqxwindowpartyIdTo").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	    			});
	    			$("#alterSave2").jqxButton({theme: theme});
	    			$("#alterCancel2").jqxButton({theme: theme});
	    			$("#alterSave2").click(function () {
	    				var tIndex = $('#jqxgridpartyid').jqxGrid('selectedrowindex');
	    				var data = $('#jqxgridpartyid').jqxGrid('getrowdata', tIndex);
	    				$('#' + $('#jqxwindowpartyIdTokey').val()).val(data.partyId);
	    				$("#jqxwindowpartyIdTo").jqxWindow('close');
	    				var e = jQuery.Event("keydown");
	    				e.which = 50; // # Some key code value
	    				$('#' + $('#jqxwindowpartyIdTokey').val()).trigger(e);
	    			});
	    			// FromParty
	    		    var sourceP =
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
	    		            sourceP.totalrecords = data.TotalRows;
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
	    		    $('#jqxgridpartyid').jqxGrid(
	    		    {
	    		        width:800,
	    		        source: dataAdapterP,
	    		        filterable: true,
	    		        virtualmode: true, 
	    		        sortable:true,
	    		        theme: theme, 
	    		        editable: false,
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
	    					  { text: '${uiLabelMap.SlideB}', datafield: 'partyId', width:150},
	    			          { text: '${uiLabelMap.Type}', datafield: 'partyTypeId', width:200},
	    			          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
	    			          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
	    			          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:150}
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
	    		  
	    		    
  <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
  <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
  <script type="text/javascript" src="/delys/images/js/ckeditor/ckeditor.js"></script>
    <script>
    function MyFunction(agreementId) {
    	var jsonObject = { agreementId: agreementId, statusId: "AGREEMENT_APPROVED", };
		jQuery.ajax({
	        url: "updateAgreementOnlyStatus",
	        type: "POST",
	        data: jsonObject,
	        success : function(res) {
	        }
	    }).done(function() {
	    	var header = "Hop dong " + agreementId + " da duoc duyet";
	    	createQuotaNotification(agreementId, "importadmin", header);
		});
	}
    function createQuotaNotification(agreementId, partyId, messages) {
    		var targetLink = "agreementId=" + agreementId;
    		var action = "getProcessingAgreements";
    		var header = messages;
    		var d = new Date();
    		var newDate = d.getTime() - (0*86400000);
    		var dateNotify = new Date(newDate);
    		var getFullYear = dateNotify.getFullYear();
    		var getDate = dateNotify.getDate();
    		var getMonth = dateNotify.getMonth() + 1;
    		dateNotify = getFullYear + "-" + getMonth + "-" + getDate;
    		var jsonObject = {partyId: partyId,
    							header: header,
    							openTime: dateNotify,
    							action: action,
    							targetLink: targetLink,};
    		jQuery.ajax({
    	        url: "createQuotaNotification",
    	        type: "POST",
    	        data: jsonObject,
    	        success: function(res) {
    	        	
    	        }
    	    }).done(function() {
    	    	location.reload();
    		});
    	}
    var ck = CKEDITOR.instances;
    
			    var listStatus = new Array();
				var row1 = {};
				row1['description'] = '';
				row1['statusId'] = '';
				listStatus[0] = row1;
				<#if listStatus?exists>
				<#list listStatus as item>
					var row = {};
					row['description'] = '${item.description?if_exists}';
					row['statusId'] = '${item.statusId?if_exists}';
					listStatus[${item_index} + 1] = row;
				</#list>
				</#if>
    			function getStatus(statusId) {
    				if (statusId != null) {
    					for ( var x in listStatus) {
        					if (statusId == listStatus[x].statusId) {
        						return listStatus[x].description;
        					}
        				}
					} else {
						return "";
					}
    			}
    			
    			var partyNameView = new Array();
    			<#if partyNameView?exists>
    			<#list partyNameView as item>
    				var row = {};
    				row['description'] = '${item.firstName?if_exists} ' + '${item.middleName?if_exists}' + '${item.lastName?if_exists}' + '${item.groupName?if_exists}';
    				row['partyId'] = '${item.partyId?if_exists}';
    				partyNameView[${item_index}] = row;
    			</#list>
    			</#if>
    			function getPartyNameView(partyId) {
    				if (partyId != null) {
    					for ( var x in partyNameView) {
        					if (partyId == partyNameView[x].partyId) {
        						return partyNameView[x].description;
        					}
        				}
					} else {
						return "";
					}
    			}
    			
    			var myVar;
				function showMore(data, id) {
						$("#" + id).jqxTooltip('destroy');
						data = data.trim();
						var dataPart = data.replace("<p>", "");
						dataPart = dataPart.replace("</p>", "");
					    data = "<i onmouseenter='notDestroy()' onmouseleave='destroy(\"" + id + "\")'>" + dataPart + "</i>";
					    $("#" + id).jqxTooltip({ content: data, position: 'right', autoHideDelay: 3000, closeOnClick: false, autoHide: false});
					    myVar = setTimeout(function(){ 
							$("#" + id).jqxTooltip('destroy');
					    }, 1000);
				}
				function notDestroy() {
					clearTimeout(myVar);
				}
				function destroy(id) {
					clearTimeout(myVar);
					myVar = setTimeout(function(){
						$("#" + id).jqxTooltip('destroy');
					}, 2000);
				}
			   function executeMyData(dataShow) {
				   if (dataShow != null) {
					   var datalength = dataShow.length;
				        var dataShowShort = "";
				        if (datalength > 40) {
				        	dataShowShort = dataShow.substr(0, 40) + "...";
						}else {
							dataShowShort = dataShow;
						}
					   return dataShowShort;
				} else {
					 return '';
				}
			   }
			   
			   $("#jqxgrid").on("cellDoubleClick", function (event)
		        		{
		        		    var args = event.args;
		        		    var rowBoundIndex = args.rowindex;
		        		    var rowVisibleIndex = args.visibleindex;
		        		    var rightclick = args.rightclick;
		        		    var ev = args.originalEvent;
		        		    var columnindex = args.columnindex;
		        		    var dataField = args.datafield;
		        		    var value = args.value;
		        		    var data = $('#jqxgrid').jqxGrid('getrowdata', rowBoundIndex);
					        var agreementId = data.agreementId;
		        		    if (dataField == 'description') {
		        		    	$("#description" + agreementId).jqxTooltip('destroy'); 
		        		    	editDescription(value, rowBoundIndex);
							}
		        });
		        function editDescription(Value, rowBoundIndex) {
		        	var wd = "";
		        	wd += "<div id='window01'><div>Edit Description</div><div>";
		        	wd += "<textarea  class='note-area no-resize' id='myEDT' autocomplete='off'></textarea>";
		        	wd += "<input style='margin-right: 5px;' type='button' id='alterSave11' value='${uiLabelMap.CommonSave}' /><input id='alterCancel11' type='button' value='${uiLabelMap.CommonCancel}' />"
		        	wd += "</div></div>";
		        	$("#myEditor").html(wd);
		        	$("#alterCancel11").jqxButton();
			        $("#alterSave11").jqxButton();
			        $("#alterSave11").click(function () {
			        	var data = getDataEditor("myEDT");
	        			var dataPart = data.replace("<em>", "<i>");
	        			dataPart = dataPart.replace("</em>", "</i>");
	        			dataPart = dataPart.trim();
	        			$("#jqxgrid").jqxGrid('setCellValue', rowBoundIndex, "description", dataPart);
			            $("#jqxgrid").jqxGrid('clearSelection');
			            $("#jqxgrid").jqxGrid('selectRow', 0);
			            $("#window01").jqxWindow('destroy');
			        });
			        $("#alterCancel11").click(function () {
			            $("#window01").jqxWindow('destroy');
			        });
		        	CKEDITOR.replace('myEDT', {height: '100px', width: '440px', skin: 'office2013'});
		        	if (Value == null) {
		        		Value = "";
					}
		        	if (Value != "") {
		        		var dataPart = Value.replace("<i>", "<em>");
	        			dataPart = dataPart.replace("</i>", "</em>");
			        	setDataEditor("myEDT", dataPart);
					}
		        	$('#window01').jqxWindow({ height: 350, width: 450, isModal:true, modalOpacity: 0.7});
		        }
		        function setDataEditor(key, content) {
		        	if (ck[key]) {
		        		return ck[key].setData(content);
		        	}
		        }
		        function getDataEditor(key) {
		        	if (ck[key]) {
		        		return ck[key].getData();
		        	}
		        	return "";
		        }
    </script>
    <style type="text/css">
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
	</style>