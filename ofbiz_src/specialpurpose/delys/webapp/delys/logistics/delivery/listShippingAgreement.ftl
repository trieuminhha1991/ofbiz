<script>
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;

	<#assign roleTypeList = delegator.findList("PartyRoleDetail",  null, null, null, null, false) />
	var roleTypeData = new Array();
	<#list roleTypeList as roleType>
		<#assign description = StringUtil.wrapString(roleType.description?if_exists) />
		var row = {};
		row['description'] = "${description?if_exists}";
		row['partyId'] = "${roleType.partyId?if_exists}";
		row['roleTypeId'] = "${roleType.roleTypeId?if_exists}";
		roleTypeData[${roleType_index}] = row;
	</#list>
	
	<#assign empPosTypeList = delegator.findList("EmplPositionType",  null, null, null, null, false) />
	var emplPosTypeData = new Array();
	<#list empPosTypeList as item>
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		var row = {};
		row['description'] = '${description?if_exists}';
		row['emplPositionTypeId'] = '${item.emplPositionTypeId?if_exists}';
		emplPosTypeData[${item_index}] = row;
	</#list>

	<#assign agreementTypeList = delegator.findList("AgreementType", null, null, null, null, false) />
	var agreementTypeData = new Array();
	<#list agreementTypeList as agreementType>
		<#assign description = StringUtil.wrapString(agreementType.description) />
		var row = {};
		row['description'] = "${description}";
		row['agreementTypeId'] = '${agreementType.agreementTypeId}';
		agreementTypeData[${agreementType_index}] = row;
	</#list>
	
	<#assign Parties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyData =  new Array();
	<#list Parties as item>
		var row = {};
		<#assign firstName = StringUtil.wrapString(item.firstName?if_exists)>
		<#assign middleName = StringUtil.wrapString(item.middleName?if_exists)>
		<#assign lastName = StringUtil.wrapString(item.lastName?if_exists)>
		<#assign groupName = StringUtil.wrapString(item.groupName?if_exists)>

		row['partyId'] = '${item.partyId?if_exists}';
		row['firstName'] = "${firstName}";
		row['middleName'] = "${middleName}";
		row['lastName'] = "${lastName}";
		row['groupName'] = "${groupName}";
		partyData[${item_index}] = row;
	</#list>
	
	<#assign listPostAddress = Static["com.olbius.util.PartyUtil"].getPostalAddressByPurpose(delegator, "PRIMARY_LOCATION")>
     var postAddressData = new Array();
     <#list listPostAddress as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.address1?if_exists) >
	row['contactMechId'] = '${item.contactMechId}';
	row['description'] = '${description}';
	row['partyId'] = '${item.partyId}';
	postAddressData[${item_index}] = row;
     </#list>

     <#assign listPhoneNumber = Static["com.olbius.util.PartyUtil"].getTelecomNumberByPurpose(delegator, "PRIMARY_PHONE")>
	 var phoneNumberData = new Array();
	 <#list listPhoneNumber as item>
	     var row = {};
	     <#assign description = StringUtil.wrapString(item.contactNumber) >
	     row['contactMechId'] = '${item.contactMechId}';
	     row['description'] = '${description}';
	     row['partyId'] = '${item.partyId}';
	     phoneNumberData[${item_index}] = row;
	 </#list>

	 <#assign listFaxNumber = Static["com.olbius.util.PartyUtil"].getFaxNumberByPurpose(delegator, "FAX_NUMBER")>
	 var faxNumberData = new Array();
	 <#list listFaxNumber as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.faxNumber) >
		row['contactMechId'] = '${item.contactMechId}';
		row['description'] = '${description}';
		row['partyId'] = '${item.partyId}';
		faxNumberData[${item_index}] = row;
	 </#list>

	 <#assign listTax = Static["com.olbius.util.PartyUtil"].getPartyTax(delegator)>
	  var taxData = new Array();
	  <#list listTax as item>
	     var row = {};
	     row['partyTaxId'] = '${item.partyTaxId?if_exists}';
	     row['partyId'] = '${item.partyId?if_exists}';
	     taxData[${item_index}] = row;
	  </#list>

	  <#assign listFinAccount = Static["com.olbius.util.PartyUtil"].getFinAccount(delegator)>
	  var finAccountData = new Array();
	  <#list listFinAccount as item>
	     var row = {};
	     <#assign description = item.finAccountName?if_exists + "[" + item.finAccountCode?if_exists + "]">
	     row['finAccountId'] = '${item.finAccountId?if_exists}';
	     row['partyId'] = '${item.ownerPartyId?if_exists}';
	     row['description'] = '${description?if_exists}';
	     finAccountData[${item_index}] = row;
	  </#list>
	 var sourceParty = { datafields: [
						      { name: 'partyId', type: 'string' },
						      { name: 'firstName', type: 'string' },
						      { name: 'lastName', type: 'string' },
						      { name: 'middleName', type: 'string' },
						      { name: 'groupName', type: 'string' },
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourceParty.totalrecords = data.TotalRows;
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

	


</script>
<!-- partyIdFrom is a datafield -->
<!-- HTML for Lookup partyIdFrom -->
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
		        <td style="padding-top: 10px;" align="right"><input style="margin-right: 5px;" type="button" id="alterSave4" value="${uiLabelMap.CommonSave}" /><input id="alterCancel4" type="button" value="${uiLabelMap.CommonCancel}" /></td>
		    </tr>
		</table>
	</div>
</div>
<!-- HTML for Lookup partyIdFrom-->
<div id="jqxwindowpartyIdTo">
	<div>${uiLabelMap.SelectPartyIdTo}</div>
	<div style="overflow: hidden;">
		<table id="PartyIdTo">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowpartyIdTokey" value=""/>
					<input type="hidden" id="jqxwindowpartyIdTovalue" value=""/>
					<div id="jqxgridpartyidto"></div>
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
	//Create Theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	
	//Create Window Lookup partyIdTo
	$("#jqxwindowpartyIdTo").jqxWindow({
        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
    });
	
	//Create Button 
    $('#alterSave3').jqxButton({theme: theme, width: 100});
    $('#alterCancel3').jqxButton({theme: theme, width: 100});
    
    //Bind event open to Window
    $('#jqxwindowpartyIdTo').on('open', function (event) {
    	var offset = $("#jqxgrid").offset();
   		$("#jqxwindowpartyIdTo").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});

	$("#alterSave3").click(function () {
		var tIndex = $('#jqxgridpartyidto').jqxGrid('selectedrowindex');
		var data = $('#jqxgridpartyidto').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowpartyIdTokey').val()).val(data.partyId);
		$("#jqxwindowpartyIdTo").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowpartyIdTokey').val()).trigger(e);
	});
	
	//Create Window Lookup partyIdFrom
	$("#jqxwindowpartyIdFrom").jqxWindow({
        theme: theme, isModal: true, autoOpen: false, cancelButton: $("#alterCancel4"), modalOpacity: 0.7, minWidth: 820, maxWidth: 1200, height: 'auto', minHeight: 515        
    });
	
	//Create Button 
    $('#alterSave4').jqxButton({theme: theme, width: 100});
    $('#alterCancel4').jqxButton({theme: theme, width: 100});
    
    //Bind event open to Window
    $('#jqxwindowpartyIdFrom').on('open', function (event) {
    	var offset = $("#jqxgrid").offset();
   		$("#jqxwindowpartyIdFrom").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
	});

	$("#alterSave4").click(function () {
		var tIndex = $('#jqxgridpartyidfrom').jqxGrid('selectedrowindex');
		var data = $('#jqxgridpartyidfrom').jqxGrid('getrowdata', tIndex);
		$('#' + $('#jqxwindowpartyIdFromkey').val()).val(data.partyId);
		$("#jqxwindowpartyIdFrom").jqxWindow('close');
		var e = jQuery.Event("keydown");
		e.which = 50; // # Some key code value
		$('#' + $('#jqxwindowpartyIdFromkey').val()).trigger(e);
	});
    
    //Create partyIdTo Lookup Grid
    $('#jqxgridpartyidto').jqxGrid(
    {
        width:800,
        source: sourceParty,
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
          { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId', width:150},
          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId', width:200},
          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
          { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:150}
        ]
    });
    
    //Create partyIdFrom Lookup Grid
    $('#jqxgridpartyidfrom').jqxGrid(
    	    {
    	        width:800,
    	        source: sourceParty,
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
    	          { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId', width:150},
    	          { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId', width:200},
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
<#assign dataField="[{ name: 'agreementId', type: 'string' },
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyIdTo', type: 'string'},
					 { name: 'roleTypeIdFrom', type: 'string'},
					 { name: 'roleTypeIdTo', type: 'string'},
					 { name: 'agreementDate', type: 'date', other:'Timestamp'},
					 { name: 'fromDate', type: 'date', other:'Timestamp'},
					 { name: 'thruDate', type: 'date', other:'Timestamp'},
					 { name: 'description', type: 'string'},
					 { name: 'textData', type: 'string'},
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.DAAgreementId}', width:150, datafield: 'agreementId',
					   cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					return '<a style = \"margin-left: 10px\" href=' + 'accApEditAgreementTerms?agreementId=' + data.agreementId + '>' +  data.agreementId + '</a>'
    					}
					 },
					 { text: '${uiLabelMap.DAPartyFrom}', width:150, datafield: 'partyIdFrom', filtertype: 'olbiusdropgrid',
					 	cellsrenderer: function (row, column, value) {
							for(i = 0; i < partyData.length; i++){
								if(partyData[i].partyId == value){
									var result = '<a title=\"' + value + '\"' + ' style = \"margin-left: 10px\" ' +  ' href=\"' + '/partymgr/control/viewprofile?partyId=' + value + '\">' + partyData[i].firstName + '&nbsp' + partyData[i].middleName + '&nbsp' + partyData[i].lastName + '&nbsp' + partyData[i].groupName + '&nbsp' + '</a>';
									return result;
								}
							}
        					return '<a title= \"' + value  + '\"' +' style = \"margin-left: 10px\" href=' + '/partymgr/control/viewprofile?partyId=' + value + '>' + value + '</a>'
    					},
					 	createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(490);
			   			}
					 },
					 { text: '${uiLabelMap.DAPartyTo}', width:150, datafield: 'partyIdTo', filtertype: 'olbiusdropgrid',
					 	cellsrenderer: function (row, column, value) {
							for(i = 0; i < partyData.length; i++){
								if(partyData[i].partyId == value){
									return '<a title=\"' + value  + '\"' + ' style = \"margin-left: 10px\" href=' + '/partymgr/control/viewprofile?partyId=' + value + '>' + partyData[i].firstName + '&nbsp' + partyData[i].middleName + '&nbsp' + partyData[i].lastName + '&nbsp' + partyData[i].groupName + '&nbsp' + '</a>';
								}
							}
        					return '<a title=\"' + value  + '\"' + ' style = \"margin-left: 10px\" href=' + '/partymgr/control/viewprofile?partyId=' + value + '>' + value + '</a>'
    					},
					 	createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(490);
			   			}
					 },
					 { text: '${uiLabelMap.DARoleTypeIdFrom}', width:150, datafield: 'roleTypeIdFrom',filterable:false, filtertype: 'checkedlist',
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						for(i = 0 ; i < roleTypeData.length; i++){
        							if(data.roleTypeIdFrom == roleTypeData[i].roleTypeId){
        								return '<span title=' + value +'>' + roleTypeData[i].description + '</span>';
        							}
        						}
        						
        						return '<span title=' + value +'>' + value + '</span>';
    						},
    					createfilterwidget: function (column, columnElement, widget) {
			   				var filterBoxAdapter2 = new $.jqx.dataAdapter(roleTypeData,
			                {
			                    autoBind: true
			                });
			                var empty = {roleTypeId: '', description: 'Empty'};
			   				var uniqueRecords2 = filterBoxAdapter2.records;
			   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				uniqueRecords2.splice(1, 0, empty);
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'roleTypeId', valueMember : 'roleTypeId', renderer: function (index, label, value) 
							{
								for(i=0;i < uniqueRecords2.length; i++){
									if(uniqueRecords2[i].roleTypeId == value){
										return uniqueRecords2[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}
					 },
					 { text: '${uiLabelMap.DARoleTypeIdTo}', width:150, datafield: 'roleTypeIdTo',filterable:false, filtertype: 'checkedlist',
					 	cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						for(i = 0 ; i < roleTypeData.length; i++){
        							if(data.roleTypeIdTo == roleTypeData[i].roleTypeId){
        								return '<span title=' + value +'>' + roleTypeData[i].description + '</span>';
        							}
        						}
        						
        						return '<span title=' + value +'>' + value + '</span>';
    						},
    					createfilterwidget: function (column, columnElement, widget) {
			   				var filterBoxAdapter2 = new $.jqx.dataAdapter(roleTypeData,
			                {
			                    autoBind: true
			                });
			   				var empty = {roleTypeId: '', description: 'Empty'};
			   				var uniqueRecords2 = filterBoxAdapter2.records;
			   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				uniqueRecords2.splice(1, 0, empty);
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'roleTypeId', valueMember : 'roleTypeId', renderer: function (index, label, value) 
							{
								for(i=0;i < uniqueRecords2.length; i++){
									if(uniqueRecords2[i].roleTypeId == value){
										return uniqueRecords2[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}
					 },
					 { text: '${uiLabelMap.DAAgreementTypeId}', width:150, datafield: 'agreementTypeId', filtertype: 'checkedlist',
					 	cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        						for(i = 0 ; i < agreementTypeData.length; i++){
        							if(value == agreementTypeData[i].agreementTypeId){
        								return '<span title = ' + agreementTypeData[i].description +'>' + agreementTypeData[i].description + '</span>';
        							}
        						}
        						
        						return '<span title=' + value +'>' + value + '</span>';
    						},
    					createfilterwidget: function (column, columnElement, widget) {
			   				var filterBoxAdapter2 = new $.jqx.dataAdapter(agreementTypeData,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords2 = filterBoxAdapter2.records;
			   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'agreementTypeId', valueMember : 'agreementTypeId', renderer: function (index, label, value) 
							{
								for(i=0;i < agreementTypeData.length; i++){
									if(agreementTypeData[i].agreementTypeId == value){
										return agreementTypeData[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}
					 },
					 { text: '${uiLabelMap.DAAgreementDate}', width:150, datafield: 'agreementDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.DAFromDate}', width:150, datafield: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.DAThruDate}', width:150, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
					 { text: '${uiLabelMap.DADescription}', width:150, datafield: 'description'},
					 { text: '${uiLabelMap.textValue}', width:150, datafield: 'textData'},
					 { text: '${uiLabelMap.copyAgreement}', width:150, 
					 	cellsrenderer: function (row, column, value) {
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					var agreementId = data.agreementId;
        					return '<span><a style = \"margin-left: 10px\" onclick=\"copyAgreement(' + agreementId + ')\" href=\"javascript:void(0)\" class=\"copyLink\"><i class=\"fa fa-files-o\"></i></a></span>'
    					}
					 },
					 { text: '${uiLabelMap.PDF}', width:150, 
						cellsrenderer: function (row, column, value) {
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        					var agreementId = data.agreementId;
	        				return '<span><a style = \"margin-left: 10px\" href=\"/delys/control/shippingAgreement.pdf?agreementId=' + agreementId + '\" class=\"copyLink\"><i class=\"fa fa-file-pdf-o\"></i></a></span>'
	    				}
					 }
					 "/>		

<@jqGrid url="jqxGeneralServicer?sname=JQGetListShippingAgreement" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addrow="true" addType="popup" addrow="true" addType="popup" deleterow="true"
		 createUrl="jqxGeneralServicer?sname=createShippingAgreement&jqaction=C"
		 removeUrl="jqxGeneralServicer?sname=cancelAgreement&jqaction=D" deleteColumn="agreementId" jqGridMinimumLibEnable="false"
		 addColumns="partyIdFrom;partyIdTo;roleTypeIdFrom;roleTypeIdTo;repIdFrom;repIdTo;repToPos;repFromPos;addressFrom;addressTo;faxNumberFrom;faxNumberTo;taxIdFrom;taxIdTo;finAccountIdTo;phoneNumberTo;phoneNumberFrom;agreementDate(java.sql.Timestamp);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);description;textData"
		 />

<div id="copyPopupWindow">
    <div>${uiLabelMap.PageTitleCopyAgreement}</div>
    <div style="overflow: hidden;">
        	<div style='float: left; width: 400px;'>
            	<table style = "margin: auto;">
            		<tr>
            			<td align="right">${uiLabelMap.agreementId}:</td>
	 					<td align="left">
	 						<input id='agreementIdCopy' style='margin-left: 10px; float: left;'>
							</input>
	 					</td>
    	 			</tr>
    	 			
    	 			<tr>
    	 				<td align="right">${uiLabelMap.AccountingAgreementTerms}:</td>
	 					<td align="left">
	 						<div id='copyAgreementTerms' style='margin-left: 10px; float: left; margin-left: 10px !important;'>
							</div>
	 					</td>
    	 			</tr>
    	 			
    	 			<tr>
    	 				<td align="right">${uiLabelMap.ProductProducts}:</td>
	 					<td align="left">
	 						<div id='copyAgreementProducts' style='margin-left: 10px; float: left; margin-left: 10px !important;'>
							</div>
	 					</td>
    	 			</tr>
    	 			
    	 			<tr>
    	 				<td align="right">${uiLabelMap.Party}:</td>
	 					<td align="left">
	 						<div id='copyAgreementParties' style='margin-left: 10px; float: left; margin-left: 10px !important;'>
                			</div>
	 					</td>
    	 			</tr>
    	 			
    	 			<tr>
    	 				<td align="right">${uiLabelMap.ProductFacilities}:</td>
	 					<td align="left">
	 						<div id='copyAgreementFacilities' style='margin-left: 10px; float: left; margin-left: 10px !important;'>
               		 		</div>
	 					</td>
    	 			</tr>
    	 			
    	 			<tr>
    	 				<td align="right">&nbsp</td>
	 					<td align="left" >
	 						<div style="width:300px">
 								<input style="margin-right: 5px;" type="button" id="alterCopy" value="${uiLabelMap.CommonCopy}" />
               					<input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" />               	
               	 			</div>
	 					</td>
    	 			</tr>
    	 		</table>	
        	</div>
    </div>
</div>

<div id="alterpopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
			<td align="right">${uiLabelMap.DAPartyFrom}:</td>
	 			<td align="left">
	 				<div id="partyIdFromAdd">
	 					<div id="jqxPartyFromGrid" />
	 				</div>
	 			</td>
	 			
			<td align="right">${uiLabelMap.DAPartyTo}:</td>
	 			<td align="left">
	 				<div id="partyIdToAdd">
	 					<div id="jqxPartyToGrid"/>
	 				</div>
	 			</td>
	 			<td>
	 				<a href="javascript:void(0)" onclick="openAddPartyPopup();" style="margin-left: 30px">
	 					<i class="fa fa-plus"></i>
	 				</a>
	 			</td>
    	 	</tr>
    	 	<tr>
			<td align="right">${uiLabelMap.DARoleTypeIdFrom}:</td>
	 			<td align="left"><div id="roleTypeIdFromAdd"></div></td>
    	 		
				<td align="right">${uiLabelMap.DARoleTypeIdTo}:</td>
	 			<td align="left"><div id="roleTypeIdToAdd"></div></td>
    	 	</tr>
	    	<tr>
				<td align="right">${uiLabelMap.AddressFrom}:</td>
				<td align="left"><div id="addressFromAdd"></div></td>

				<td align="right">${uiLabelMap.AddressTo}:</td>
				<td align="left"><div id="addressToAdd"></div></td>
			</tr>
			<tr>
				<td align="right">${uiLabelMap.PhoneNumberFrom}:</td>
				<td align="left"><div id="phoneNumberFromAdd"></div></td>

				<td align="right">${uiLabelMap.PhoneNumberTo}:</td>
				<td align="left"><div id="phoneNumberToAdd"></div></td>
			</tr>
			<tr>
				<td align="right">${uiLabelMap.FaxNumberFrom}:</td>
				<td align="left"><div id="faxNumberFromAdd"></div></td>

				<td align="right">${uiLabelMap.FaxNumberTo}:</td>
				<td align="left"><div id="faxNumberToAdd"></div></td>
			</tr>
			<tr>
				<td align="right">${uiLabelMap.TaxIdFrom}:</td>
				<td align="left"><div id="taxIdFromAdd"></div></td>

				<td align="right">${uiLabelMap.TaxIdTo}:</td>
				<td align="left"><div id="taxIdToAdd"></div></td>
			</tr>
		<tr>
		 		<td align="right">${uiLabelMap.repIdFrom}:</td>
	 			<td align="left">
	 				<div id="repIdFromAdd">
	 					<div id="jqxRepIdFromGrid"></div>
	 				</div>
	 			</td>
	 			
	 			<td align="right">${uiLabelMap.repIdTo}:</td>
	 			<td align="left">
	 				<div id="repIdToAdd">
	 					<div id="jqxRepIdToGrid"></div>
	 				</div>
	 			</td>
		 	</tr>
		 	
		 	<tr>
			 	<td align="right">${uiLabelMap.repFromPos}:</td>
	 			<td align="left">
	 				<div id="repFromPos">
	 				</div>
	 			</td>
		 	
		 		<td align="right">${uiLabelMap.repToPos}:</td>
	 			<td align="left">
	 				<div id="repToPos">
	 				</div>
	 			</td>
		 	</tr>
    	 	<tr>
			<td align="right">${uiLabelMap.DAFromDate}:</td>
	 			<td align="left"><div id="fromDateAdd"></div></td>
    	 		
				<td align="right">${uiLabelMap.DAThruDate}:</td>
	 			<td align="left"><div id="thruDateAdd"></div></td>
    	 	</tr>
    	 	<tr>
			<td align="right">${uiLabelMap.DADescription}:</td>
	 			<td align="left"><input id="descriptionAdd"></input></td>
    	 		
				<td align="right">${uiLabelMap.textValue}:</td>
	 			<td align="left"><input id="textDataAdd"></input></td>
    	 	</tr>
	    	 <tr>
				<td align="right">${uiLabelMap.DAAgreementDate}:</td>
	 			<td align="left"><div id="agreementDateAdd"></div></td>
				<td align="right">${uiLabelMap.finAccountIdTo}:</td>
				<td align="left"><div id="finAccountIdToAdd"></div></td>
		 	</tr>
            <tr>
                <td align="right"></td>
                <td align="left"></td>
                <td style="padding-top: 30px; max-width: 300px" align="right"><input style="margin-right: 5px;" type="button" id="alterSave" value="${uiLabelMap.CommonSave}" /><input id="alterCancel" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>

<div id="addPartyPopupWindow">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
        <table>
    	 	<tr>
    	 		<td align="right">${uiLabelMap.groupName}:</td>
	 			<td align="left">
	 				<input name="groupName" id="groupName"></input>
	 			</td>
    	 	</tr>
	    	<tr>
		 		<td align="right">${uiLabelMap.address}:</td>
	 			<td align="left">
	 				<input name="address" id="address"></input>
	 			</td>
		 	</tr>
    	 	<tr>
	    	 	<td align="right">${uiLabelMap.RepName}:</td>
	 			<td align="left" style="max-width: none">
	 				<span><input name="firstName" id="firstName"></input></span>
	 				<span><input name="middleName" id="middleName"></input></span>
	 				<span><input name="lastName" id="lastName"></input></span>
	 			</td>
    	 	</tr>
	    	<tr>
	    	 	<td align="right">${uiLabelMap.positionName}:</td>
	 			<td align="left">
					<div id="positionName"></div>
	 			</td>
		 	</tr>
		 	<tr>
	    	 	<td align="right">${uiLabelMap.phoneNumber}:</td>
	 			<td align="left">
	 				<input id="phoneNumber"></input>
	 			</td>
		 	</tr>
		 	<tr>
	    	 	<td align="right">${uiLabelMap.faxNumber}:</td>
	 			<td align="left">
	 				<input id="faxNumber"></input>
	 			</td>
		 	</tr>
		 	<tr>
	    	 	<td align="right">${uiLabelMap.taxId}:</td>
	 			<td align="left">
	 				<input id="taxId"></input>
	 			</td>
		 	</tr>
			<tr>
	    	 	<td align="right">${uiLabelMap.accountNumber}:</td>
	 			<td align="left">
	 				<input id="accountNumber"></input>
	 			</td>
		 	</tr>
	 		<tr>
	 			<td align="right">${uiLabelMap.bankName}:</td>
				<td align="left">
					<input id="bankName"></input>
				</td>
		 	</tr>
            <tr>
                <td align="right"></td>
                <td style="padding-top: 30px; max-width: 300px" align="left"><input style="margin-right: 5px;" type="button" id="alterSaveAddParty" value="${uiLabelMap.CommonSave}" /><input id="alterCancelAddParty" type="button" value="${uiLabelMap.CommonCancel}" /></td>
            </tr>
        </table>
    </div>
</div>

<script>
	//Create Add Party Popup Window
	$("#addPartyPopupWindow").jqxWindow({
       maxWidth: 1000, width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAddParty"), modalOpacity: 0.7         
    });
    
	//Create groupName
	$("#groupName").jqxInput({width: 230, height: 25, theme: theme});
	
	//Create Address
	$("#address").jqxInput({width: 230, height: 25, theme: theme});
	
	//Create firstName, middleName, lastName
	<#assign firstName = StringUtil.wrapString(uiLabelMap.firstName) >
	<#assign middleName = StringUtil.wrapString(uiLabelMap.middleName) >
	<#assign lastName = StringUtil.wrapString(uiLabelMap.lastName) >
	$("#firstName").jqxInput({width: 70, height: 25, theme: theme, placeHolder: '${firstName}'});
	$("#middleName").jqxInput({width: 70, height: 25, theme: theme, placeHolder: '${middleName}'});
	$("#lastName").jqxInput({width: 70, height: 25, theme: theme, placeHolder: '${lastName}'});
    
	//Create positionName
	$("#positionName").jqxDropDownList({source: emplPosTypeData, width: 235, height: 25, displayMember:"description", selectedIndex: 0 ,valueMember: "emplPositionTypeId"});
	
	//Create phoneNumber
	$("#phoneNumber").jqxInput({width: 230, height: 25, theme: theme});
	
	//Create taxId
	$("#taxId").jqxInput({width: 230, height: 25, theme: theme});
	
	//Create accountNumber
	$("#accountNumber").jqxInput({width: 230, height: 25, theme: theme});
	
	//Create bankName
	$("#bankName").jqxInput({width: 230, height: 25, theme: theme});
	
	//Create faxNumber
	$("#faxNumber").jqxInput({width: 230, height: 25, theme: theme});
	
	//Create button
	$("#alterSaveAddParty").jqxButton();
	$("#alterCancelAddParty").jqxButton();
	
	//Open Add Party Popup
    function openAddPartyPopup(){
    	$("#addPartyPopupWindow").jqxWindow('open');
    }
    
    $("#alterSaveAddParty").on('click', function (event){
    	var data = {};
    	data['groupName'] = $("#groupName").val();
    	data['firstName'] = $("#firstName").val();
    	data['middleName'] = $("#middleName").val();
    	data['lastName'] = $("#lastName").val();
    	data['positionName'] = $("#positionName").val();
    	data['phoneNumber'] = $("#phoneNumber").val();
    	data['taxId'] = $("#taxId").val();
    	data['accountNumber'] = $("#accountNumber").val();
    	data['bankName'] = $("#bankName").val();
    	data['faxNumber'] = $("#faxNumber").val();
    	data['address'] = $("#address").val();
    	
    	//Send ajax request create party quickly
    	$.ajax({
			url: "createPartyQuickly",
			type: "POST",
			data: data,
			success: function(res){

				//Set data for partyIdToAdd
				var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + res.partyIdTo  + '</div>';
			    $("#partyIdToAdd").jqxDropDownButton('setContent', dropDownContent);
			    
			    //Set data for roleTypeIdToAdd
			    var data = new Array();
			    index = 0;
			    for(var i = 0; i < roleTypeData.length; i++){
				if(res.roleTypeIdTo == roleTypeData[i].roleTypeId){
					var row = {};
					row['roleTypeId'] = roleTypeData[i].roleTypeId;
					row['description'] = roleTypeData[i].description;
					data[index] = row;
					break;
				}
			    }
			    $("#roleTypeIdToAdd").jqxDropDownList('clear');
			    $("#roleTypeIdToAdd").jqxDropDownList({source: data, width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "roleTypeId"});

			    //Set data for repIdToAdd
			    var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + res.repIdTo  + '</div>';
			    $("#repIdToAdd").jqxDropDownButton('setContent', dropDownContent);

			    //Set data for empPositionTypeIdToAdd
			    var data = new Array();
			    index = 0;
			    for(var i = 0; i < emplPosTypeData.length; i++){
				if(res.emplPositionTypeIdTo == emplPosTypeData[i].emplPositionTypeId){
					var row = {};
					row['emplPositionTypeId'] = emplPosTypeData[i].emplPositionTypeId;
					row['description'] = emplPosTypeData[i].description;
					data[index] = row;
					break;
				}
			    }
			    $("#repToPos").jqxDropDownList('clear');
			    $("#repToPos").jqxDropDownList({source: data, width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "emplPositionTypeId"});

			    //Set data for addressToAdd
			    var data = new Array();
			    var row = {};
			    row['contactMechId'] = res.postalAddressIdTo;
			    row['description'] = $("#address").val();
			    data[0] = row;
			    $("#addressToAdd").jqxDropDownList('clear');
			    $("#addressToAdd").jqxDropDownList({source: data, selectedIndex: 0});

			    //Set data for phoneNumber
			    var data = new Array();
			    var row = {};
			    row['contactMechId'] = res.telecomNumberIdTo;
			    row['description'] = $("#phoneNumber").val();
			    data[0] = row;
			    $("#phoneNumberToAdd").jqxDropDownList('clear');
			    $("#phoneNumberToAdd").jqxDropDownList({source: data, selectedIndex: 0});
			    
			    //Set data for Fax Number
			    var data = new Array();
			    var row = {};
			    row['contactMechId'] = res.faxNumberIdTo;
			    row['description'] = $("#faxNumber").val();
			    data[0] = row;
			    $("#faxNumberToAdd").jqxDropDownList('clear');
			    $("#faxNumberToAdd").jqxDropDownList({source: data, selectedIndex: 0});

			    //Set data for Tax
			    var data = new Array();
			    var row = {};
			    row['partyTaxId'] = $("#taxId").val();
			    row['description'] = $("#taxId").val();
			    data[0] = row;
			    $("#taxIdToAdd").jqxDropDownList('clear');
			    $("#taxIdToAdd").jqxDropDownList({source: data, selectedIndex: 0});

			    //Set data for finAccountId
			    var data = new Array();
			    var row = {};
			    row['finAccountId'] = res.finAccountIdTo;
			    row['description'] = $("#bankName").val() + '[' + $("#accountNumber").val() + ']';
			    data[0] = row;
			    $("#finAccountIdToAdd").jqxDropDownList('clear');
			    $("#finAccountIdToAdd").jqxDropDownList({source: data, selectedIndex: 0});
			    
			    //Close Add Party Lookup
			    $("#addPartyPopupWindow").jqxWindow('close');
				}
		});
    });
</script>

<script>
	
	var outFilterCondition = "";
	
	//Create agreementIdCopy
	$("#agreementIdCopy").jqxInput({ width: 120, height: 25, disabled: true});
	
	//Create copyAgreementTerms
	$("#copyAgreementTerms").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create copyAgreementProducts
	$("#copyAgreementProducts").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create copyAgreementParties
	$("#copyAgreementParties").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create copyAgreementFacilities
	$("#copyAgreementFacilities").jqxCheckBox({ width: 120, height: 25, checked: true});
	
	//Create Copy popup
	$("#copyPopupWindow").jqxWindow({
       width: 600, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7         
    });
    
    $("#alterCancel").jqxButton();
    $("#alterCopy").jqxButton();
    
    function copyAgreement(agreementId){
    	$('#agreementIdCopy').val(agreementId);
    	$('#copyPopupWindow').jqxWindow('open');
    }
    
    // update the edited row when the user clicks the 'Save' button.
    $("#alterCopy").click(function () {
    
    	var agreementId = $('#agreementIdCopy').val();
    	var copyAgreementTerms = $('#copyAgreementTerms').val();
    	var copyAgreementProducts = $('#copyAgreementProducts').val();
    	var copyAgreementParties = $('#copyAgreementParties').val();
    	var copyAgreementFacilities = $('#copyAgreementFacilities').val();
    	var request = $.ajax({
			  url: "copyAgreement",
			  type: "POST",
			  data: {agreementId : agreementId, copyAgreementTerms: copyAgreementTerms, copyAgreementProducts: copyAgreementProducts, copyAgreementParties: copyAgreementParties, copyAgreementFacilities: copyAgreementFacilities},
			  dataType: "html",
	           success: function(data){
	        	   if(data.responseMessage == "error"){
		            	$('#jqxNotification').jqxNotification({ template: 'error'});
		            	$("#jqxNotification").text(data.errorMessage);
		            	$("#jqxNotification").jqxNotification("open");
		            }else{
		            	$('#container').empty();
		            	$('#jqxNotification').jqxNotification({ template: 'info'});
		            	$("#jqxNotification").text("Thuc thi thanh cong!");
		            	$("#jqxNotification").jqxNotification("open");
		            }
	        	   $("#jqxgrid").jqxGrid('updatebounddata');
	        	   $("#copyPopupWindow").jqxWindow('close');
	           }
			});
    });
</script>

<script>
	//Create AddressFrom
	$("#addressFromAdd").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "contactMechId"});

	//Create AddressFrom
	$("#addressToAdd").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "contactMechId"});

	//Create Phone Num From
	$("#phoneNumberFromAdd").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "contactMechId"});

	//Create Phone Num To
	$("#phoneNumberToAdd").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "contactMechId"});

	//Create Fax Num From
	$("#faxNumberFromAdd").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "contactMechId"});

	//Create Fax Num To
	$("#faxNumberToAdd").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "contactMechId"});

	//Create Tax Id From
	$("#taxIdFromAdd").jqxDropDownList({width: 200 , displayMember:"partyTaxId", selectedIndex: 0 ,valueMember: "partyTaxId"});

	//Create Tax Id To
	$("#taxIdToAdd").jqxDropDownList({width: 200 , displayMember:"partyTaxId", selectedIndex: 0 ,valueMember: "partyTaxId"});

	//Create findAccountIdAdd
	$("#finAccountIdToAdd").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "finAccountId"});

    //Create repIdFromAdd
	$('#repIdFromAdd').jqxDropDownButton({ width: 200, height: 25});
	$("#jqxRepIdFromGrid").jqxGrid({
		width:400,
		source: sourceParty,
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
				{ text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '20%'},
				{ text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '20%'},
				{ text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '20%'},
				{ text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '20%'},
				{ text: '${uiLabelMap.DAGroupName}', datafield: 'groupName', width: '20%'},
			]
		});
	$("#jqxRepIdFromGrid").on('rowselect', function (event) {
         var args = event.args;
         var row = $("#jqxRepIdFromGrid").jqxGrid('getrowdata', args.rowindex);
         var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
         $("#repIdFromAdd").jqxDropDownButton('setContent', dropDownContent);

         <#assign listEmplPos = Static["com.olbius.util.PartyUtil"].getCurrPositionTypeOfEmpl(delegator) >
         var emplPosData = new Array();
         var index = 0;
         <#list listEmplPos as item>
		if('${item.employeePartyId}' == row['partyId']){
			var row = {};
			<#assign description = StringUtil.wrapString(item.description)>
			row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
			row['description'] = '${description}';
			emplPosData[index] = row;
		}
         </#list>
         $("#repFromPos").jqxDropDownList({source: emplPosData, selectedIndex: 0});
    });

    //Create repFromPos
    $("#repFromPos").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "emplPositionTypeId"});
    
	//Create repIdToAdd
	$('#repIdToAdd').jqxDropDownButton({ width: 200, height: 25});
	$("#jqxRepIdToGrid").jqxGrid({
		width:400,
		source: sourceParty,
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
				{ text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '20%'},
				{ text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '20%'},
				{ text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '20%'},
				{ text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '20%'},
				{ text: '${uiLabelMap.DAGroupName}', datafield: 'groupName', width: '20%'},
			]
		});
	$("#jqxRepIdToGrid").on('rowselect', function (event) {
         var args = event.args;
         var row = $("#jqxRepIdToGrid").jqxGrid('getrowdata', args.rowindex);
         var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
         $("#repIdToAdd").jqxDropDownButton('setContent', dropDownContent);

         <#assign listEmplPos = Static["com.olbius.util.PartyUtil"].getCurrPositionTypeOfEmpl(delegator) >
         var emplPosData = new Array();
         var index = 0;
         <#list listEmplPos as item>
		if('${item.employeePartyId}' == row['partyId']){
			var row = {};
			<#assign description = StringUtil.wrapString(item.description)>
			row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
			row['description'] = '${description}';
			emplPosData[index] = row;
		}
         </#list>
         $("#repToPos").jqxDropDownList({source: emplPosData, selectedIndex: 0});
	});
	
	//Create repToPos
	$("#repToPos").jqxDropDownList({width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "emplPositionTypeId"});
	
	//Create Party From
	$('#partyIdFromAdd').jqxDropDownButton({ width: 200, height: 25});
	$("#jqxPartyFromGrid").jqxGrid({
		width:400,
		source: sourceParty,
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
				{ text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '20%'},
				{ text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '20%'},
				{ text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '20%'},
				{ text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '20%'},
				{ text: '${uiLabelMap.DAGroupName}', datafield: 'groupName', width: '20%'},
			]
		});
	$("#jqxPartyFromGrid").on('rowselect', function (event) {
         var args = event.args;
         var selectedRow = $("#jqxPartyFromGrid").jqxGrid('getrowdata', args.rowindex);
         var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + selectedRow['partyId'] + '</div>';
         $("#partyIdFromAdd").jqxDropDownButton('setContent', dropDownContent);

         //Update for Role Type From
	     var index = 0;
	     var data = new Array();
	     for(var i = 0; i < roleTypeData.length; i++){
		if(selectedRow['partyId'] == roleTypeData[i].partyId){
			var row = {};
			row['roleTypeId'] = roleTypeData[i].roleTypeId;
			row['description'] = roleTypeData[i].description;
			data[index] = row;
			index++;
		}
	     }
	     $("#roleTypeIdFromAdd").jqxDropDownList('clear');
	     $("#roleTypeIdFromAdd").jqxDropDownList({source: data, width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "roleTypeId"});

	     //Update for Address From
	     var index = 0;
	     var data = new Array();
	     for(var i = 0; i < postAddressData.length; i++){
		 if(selectedRow['partyId'] == postAddressData[i].partyId){
			 data[index] = postAddressData[i];
			 index++;
		 }
	     }
	    $("#addressFromAdd").jqxDropDownList({source:data, selectedIndex: 0})

	    //Update for Phone Number From
	     var index = 0;
	     var data = new Array();
	     for(var i = 0; i < phoneNumberData.length; i++){
		 if(selectedRow['partyId'] == phoneNumberData[i].partyId){
			 data[index] = phoneNumberData[i];
			 index++;
		 }
	     }
	    $("#phoneNumberFromAdd").jqxDropDownList({source:data, selectedIndex: 0})

	    //Update for fax Number From
		var index = 0;
		var data = new Array();
		for(var i = 0; i < faxNumberData.length; i++){
		   if(selectedRow['partyId'] == faxNumberData[i].partyId){
			data[index] = faxNumberData[i];
			index++;
		    }
		}
		$("#faxNumberFromAdd").jqxDropDownList({source:data, selectedIndex: 0})

		//Update for Tax Id From
		var index = 0;
		var data = new Array();
		for(var i = 0; i < taxData.length; i++){
		  if(selectedRow['partyId'] == taxData[i].partyId){
			data[index] = taxData[i];
			index++;
		    }
		}

		$("#taxIdFromAdd").jqxDropDownList({source:data, selectedIndex: 0})
	});
	
	$('#partyIdToAdd').jqxDropDownButton({ width: 200, height: 25});
	$("#jqxPartyToGrid").jqxGrid({
		width:400,
		source: sourceParty,
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
			{ text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '20%', filtertype:'input'},
			{ text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '20%', filtertype:'input'},
			{ text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '20%', filtertype:'input'},
			{ text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '20%', filtertype:'input'},
			{ text: '${uiLabelMap.DAGroupName}', datafield: 'groupName', width: '20%', filtertype:'input'},
		]
		});

	//Handle for rowselect event
	$("#jqxPartyToGrid").on('rowselect', function (event) {
        var args = event.args;
        var selectedRow = $("#jqxPartyToGrid").jqxGrid('getrowdata', args.rowindex);
        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + selectedRow['partyId'] + '</div>';
        $("#partyIdToAdd").jqxDropDownButton('setContent', dropDownContent);

        //Update for Role Type Id To
        var index = 0;
	    var data = new Array();
	    for(var i = 0; i < roleTypeData.length; i++){
		if(selectedRow['partyId'] == roleTypeData[i].partyId){
	    		var row = {};
	    		row['roleTypeId'] = roleTypeData[i].roleTypeId;
	    		row['description'] = roleTypeData[i].description;
	    		data[index] = row;
	    		index++;
	    	}
	    }
	    $("#roleTypeIdToAdd").jqxDropDownList('clear');
	    $("#roleTypeIdToAdd").jqxDropDownList({source: data, width: 200 , displayMember:"description", selectedIndex: 0 ,valueMember: "roleTypeId"});

	    //Update for Address To
	     var index = 0;
	     var data = new Array();
	     for(var i = 0; i < postAddressData.length; i++){
		 if(selectedRow['partyId'] == postAddressData[i].partyId){
			 data[index] = postAddressData[i];
			 index++;
		 }
	     }
	    $("#addressToAdd").jqxDropDownList({source:data, selectedIndex: 0});

	    //Update for Phone Number To
		  var index = 0;
		  var data = new Array();
		  for(var i = 0; i < phoneNumberData.length; i++){
		    if(selectedRow['partyId'] == phoneNumberData[i].partyId){
			data[index] = phoneNumberData[i];
			index++;
		    }
		  }

	  $("#phoneNumberToAdd").jqxDropDownList({source:data, selectedIndex: 0})

	  //Update for fax Number To
	  var index = 0;
	  var data = new Array();
	  for(var i = 0; i < faxNumberData.length; i++){
	    if(selectedRow['partyId'] == faxNumberData[i].partyId){
		data[index] = faxNumberData[i];
		index++;
	    }
	  }

	  $("#faxNumberToAdd").jqxDropDownList({source:data, selectedIndex: 0})

	  //Update for Tax Id To
	  var index = 0;
	  var data = new Array();
	  for(var i = 0; i < taxData.length; i++){
	    if(selectedRow['partyId'] == taxData[i].partyId){
		data[index] = taxData[i];
		index++;
	    }
	  }

	  $("#taxIdToAdd").jqxDropDownList({source:data, selectedIndex: 0})

	  //Update for Fin Account
	  var index = 0;
	  var data = new Array();
	  for(var i = 0; i < taxData.length; i++){
	    if(selectedRow['partyId'] == finAccountData[i].partyId){
		data[index] = finAccountData[i];
		index++;
	    }
	  }

	  $("#finAccountIdToAdd").jqxDropDownList({source:data, selectedIndex: 0})
    });

	$("#roleTypeIdFromAdd").jqxDropDownList({width: 200,autoDropDownHeight: true, displayMember:"description", selectedIndex: 0 ,valueMember: "roleTypeId"});
	$("#roleTypeIdToAdd").jqxDropDownList({width: 200,autoDropDownHeight: true, displayMember:"description",selectedIndex: 0, valueMember: "roleTypeId"});
	$("#agreementDateAdd").jqxDateTimeInput({height: '25px', width: 200,  formatString: 'dd-MM-yyyy : HH:mm:ss' });
	$("#fromDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy : HH:mm:ss' });
	$("#thruDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy : HH:mm:ss' });
	$("#descriptionAdd").jqxInput({height: 20, width: 195});
	$("#textDataAdd").jqxInput({height: 20, width: 195});
	//Create alterpopupWindow
	$("#alterpopupWindow").jqxWindow({
        maxWidth: '1000px', width: 930, height: 550, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
    });
    $("#alterCancel").jqxButton({});
    $("#alterSave").jqxButton({});

    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	var row;
        row = { 
        		partyIdFrom:$('#partyIdFromAdd').val(),
        		partyIdTo:$('#partyIdToAdd').val(),
        		roleTypeIdFrom:$('#roleTypeIdFromAdd').val(),
        		roleTypeIdTo:$('#roleTypeIdToAdd').val(),
        		repIdFrom:$('#repIdFromAdd').val(),
        		repIdTo:$('#repIdToAdd').val(),
			repFromPos:$('#repFromPos').val(),
			repToPos:$('#repToPos').val(),
        		description:$('#descriptionAdd').val(),
			addressFrom:$("#addressFromAdd").val(),
			addressTo:$("#addressToAdd").val(),
			phoneNumberFrom:$("#phoneNumberFromAdd").val(),
			phoneNumberTo:$("#phoneNumberToAdd").val(),
			faxNumberFrom:$("#faxNumberFromAdd").val(),
			faxNumberTo:$("#faxNumberToAdd").val(),
			taxIdFrom:$("#taxIdFromAdd").val(),
			taxIdTo:$("#taxIdToAdd").val(),
        		textData:$('#textDataAdd').val(),
			finAccountIdTo:$('#finAccountIdToAdd').val(),
        		agreementDate:$('#agreementDateAdd').jqxDateTimeInput('getDate').getTime(),
        		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate').getTime(),
        		thruDate:$('#thruDateAdd').jqxDateTimeInput('getDate').getTime(),
        	  };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
    });
</script>
<style type="text/css">
	#jqxgridFromParty .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgridToParty .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxgridBillingAccountIdy .jqx-grid-header-olbius{
		height:25px !important;
	}	
	#jqxPanel td{
		padding:5px;
	}
	#addrowbutton{
		margin:0 !important;
		border-radius:0 !important;
	}
</style>