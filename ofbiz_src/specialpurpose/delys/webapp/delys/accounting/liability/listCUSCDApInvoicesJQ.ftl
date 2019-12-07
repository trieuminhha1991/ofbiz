<script type="text/javascript">
	var dataInvoiceType = [];
	<#list listInvoiceType as item>
	    <#assign description = item.get("description", locale)/>
	    var tmpOb = new Object();
	    tmpOb.invoiceTypeId = "${item.invoiceTypeId}";
	    tmpOb.description = "${description}";
	    dataInvoiceType[${item_index}] = tmpOb;
	</#list>
	var dataStatusType = [];
	<#list listStatusItem as item>
	    <#assign description = item.get("description", locale)/>
	    var tmpOb = new Object();
	    tmpOb.statusId = "${item.statusId}";
	    tmpOb.description = "${description}";
	    dataStatusType[${item_index}] = tmpOb;
	</#list>
	$(document).ready(function () {
	    $("#agrdata").html("<img style='width:20px;heigth:20px;' src='/aceadmin/jqw/images/loader.gif'>");
	    $.ajax({
	        type: "POST",
	        url: "AggregateApCustomersData",
	        data: null,
	        async: true,
	        success: function(data){
	            $("#agrdata").html(data);
	        },
	        error: function(response){
	            $("#agrdata").html(response);
	        }
	    });
	});
</script>
<div id="agrdata">
</div>

<div id="jqxwindowpartyIdFrom" class='hide'>
	<div>${uiLabelMap.accList} ${uiLabelMap.accCustomers}</div>
	<div style="overflow: hidden;">
		<div class='form-window-content'>
			<input type="hidden" id="jqxwindowpartyIdFromkey" value=""/>
			<input type="hidden" id="jqxwindowpartyIdFromvalue" value=""/>
			<div id="jqxgridpartyidfrom"></div>	 				
		</div>
		<div class="form-action">
			<button id="alterCancel3" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave3" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<@jqGridMinimumLib/>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	$("#jqxwindowpartyIdFrom").jqxWindow({
		theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7, width: 820, maxWidth: 1000, height: 513        
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
	    pagesize:10,
        contentType: 'application/x-www-form-urlencoded',
        url: 'jqxGeneralServicer?sname=getPartyCustomer',
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
    		else{
    			data.filterListFields = "";
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
        height: 400,
        source: dataAdapterF,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        showfilterrow: true,
        theme: theme, 
        autorowheight:true,
        pageable: true,
        pagesizeoptions: ['5', '10', '15'],
        ready:function(){
        },
        rendergridrows: function(obj)
		{
			return obj.data;
		},
        columns: [
                  { text: '${uiLabelMap.accApInvoice_ToPartyId}', datafield: 'partyId', width:200},
                  { text: '${uiLabelMap.accAccountingToParty}', datafield: 'groupName', width:200},
                  { text: '${uiLabelMap.accApInvoice_ToPartyTypeId}', datafield: 'partyTypeId', width:200, 
                	  cellsrenderer: function(row, columns, value){
        					var group = "${uiLabelMap.PartyGroup}";
        					var person = "${uiLabelMap.Person}";
        					if(value == "PARTY_GROUP"){
        						return "<div class='custom-cell-grid'>"+group+"</div>";
        					}else if(value == "PERSON"){
        						return "<div class='custom-cell-grid'>"+person+"</div>";
        					}
        					return value;
        				}},
                  { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:200, 
        			cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
        				var first = rowdata.firstName ? rowdata.firstName : "";
        				var last = rowdata.lastName ? rowdata.lastName : "";
        				return "<div class='custom-cell-grid'>"+ first + " " + last +"</div>";
        			}},
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
<#assign dataField="[{ name: 'invoiceId', type: 'string' },
					 { name: 'invoiceTypeId', type: 'string'},
					 { name: 'invoiceDate', type: 'date', other:'Timestamp'},
					 { name: 'statusId', type: 'string'},
					 { name: 'description', type: 'string'},
					 { name: 'partyIdFrom', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'total', type: 'number'},
					 { name: 'amountToApply', type: 'number'},
					 { name: 'partyNameResultFrom', type: 'string'},
					 { name: 'partyNameResultTo', type: 'string'}
					 ]
					 "/>
<#assign columnlist="{ text: '${uiLabelMap.FormFieldTitle_invoiceId}', width:150, filtertype:'input', datafield: 'invoiceId', cellsrenderer:
                     	 function(row, colum, value)
                        {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='/delys/control/accApinvoiceOverview?invoiceId=\" + data.invoiceId + \"'>\" + data.invoiceId + \"</a></span>\";
                        }},
                        
					 	{ text: '${uiLabelMap.FormFieldTitle_invoiceTypeId}', filtertype: 'checkedlist', width:150, datafield: 'invoiceTypeId', cellsrenderer:
	                     	function(row, colum, value)
	                        {
	                        	for(i=0; i < dataInvoiceType.length;i++){
	                        		if(value==dataInvoiceType[i].invoiceTypeId){
	                        			return \"<span>\" + dataInvoiceType[i].description + \"</span>\";
	                        		}
	                        	}
	                        	return \"<span>\" + value + \"</span>\";
	                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				var sourceIT =
						    {
						        localdata: dataInvoiceType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceIT,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords = filterBoxAdapter.records;
			   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords, displayMember: 'invoiceTypeId', valueMember : 'invoiceTypeId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataInvoiceType.length; i++){
									if(dataInvoiceType[i].invoiceTypeId == value){
										return dataInvoiceType[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},
                        
                     	{ text: '${uiLabelMap.FormFieldTitle_invoiceDate}', filtertype: 'range', width:150, datafield: 'invoiceDate', cellsformat: 'dd/MM/yyyy'},   					 
					 	{ text: '${uiLabelMap.CommonStatus}', filtertype: 'checkedlist', width:120, datafield: 'statusId', cellsrenderer:
                     	function(row, colum, value)
                        {
                        	for(i=0; i < dataStatusType.length;i++){
                        		if(value==dataStatusType[i].statusId){
                        			return \"<span>\" + dataStatusType[i].description + \"</span>\";
                        		}
                        	}
                        	return value;
                        },
			   			createfilterwidget: function (column, columnElement, widget) {
			   				var sourceST =
						    {
						        localdata: dataStatusType,
						        datatype: \"array\"
						    };
			   				var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST,
			                {
			                    autoBind: true
			                });
			                var uniqueRecords2 = filterBoxAdapter2.records;
			   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'statusId', valueMember : 'statusId', renderer: function (index, label, value) 
							{
								for(i=0;i < dataStatusType.length; i++){
									if(dataStatusType[i].statusId == value){
										return dataStatusType[i].description;
									}
								}
							    return value;
							}});
							//widget.jqxDropDownList('checkAll');
			   			}},
					 { text: '${uiLabelMap.description}', width:150, filtertype:'input', datafield: 'description'},
					 { text: '${uiLabelMap.accCustomers}',filtertype: 'olbiusdropgrid', width:500, datafield: 'partyIdFrom', cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return \"<span>\" + data.partyNameResultFrom + '[' + data.partyIdFrom + ']' + \"</span>\";
					 	},
					 	createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(490);
			   			}},
					 { text: '${uiLabelMap.FormFieldTitle_total}', width:200, filterable: false, sortable: false, datafield: 'total', aggregates: ['sum'],
					 	aggregatesrenderer: 
					 	function (aggregates, column, element, summaryData) 
					 	{
                          var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
                           $.each(aggregates, function (key, value) {
                              renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.accTotalInvoicesValue}:<\\/b>' + '<br>' +  formatcurrency(value,'${defaultOrganizationPartyCurrencyUomId}') + '</div>';
                              });                          
                          	  renderstring += \"</div>\";
                          return renderstring; 
                          } , 
					 cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);					 		
					 		return \"<span>\" + formatcurrency(data.total,data.currencyUomId) + \"</span>\";							 		
					 	}},
					 { text: '${uiLabelMap.FormFieldTitle_amountToApply}', width:180, sortable: false, filterable: false, datafield: 'amountToApply', aggregates: ['sum'],
					 	aggregatesrenderer: 
					 	function (aggregates, column, element, summaryData) 
					 	{
                          var renderstring = \"<div class='jqx-widget-content jqx-widget-content-\" + theme + \"' style='float: left; width: 100%; height: 100%;'>\";
                           $.each(aggregates, function (key, value) {
                              renderstring += '<div style=\"color: ' + 'red' + '; position: relative; margin: 6px; text-align: right; overflow: hidden;\">' + '<b>${uiLabelMap.accPayableToApplyTotal}:<\\/b>' + '<br>' +  formatcurrency(value,'${defaultOrganizationPartyCurrencyUomId}') + '</div>';
                              });                          
                          	  renderstring += \"</div>\";
                          return renderstring; 
                          } ,
					 cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return \"<span>\" + formatcurrency(data.amountToApply,data.currencyUomId) + \"</span>\";					 		
					 	}}"
					   />		
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<@jqGrid url="jqxGeneralServicer?sname=JQGetListCUSCDAPInvoice" dataField=dataField columnlist=columnlist sortdirection="desc" defaultSortColumn="invoiceDate" jqGridMinimumLibEnable="false" filterable="true" filtersimplemode="true" addType="popup" showstatusbar="true"  alternativeAddPopup="alterpopupWindow"  showtoolbar="false"
		 id="jqxgrid" otherParams="total:S-getInvoiceTotal(inputValue{invoiceId})<outputValue>;amountToApply:S-getInvoiceNotApplied(inputValue{invoiceId})<outputValue>;partyNameResultFrom:S-getPartyNameForDate(partyId{partyIdFrom},compareDate{invoiceDate},lastNameFirst*Y)<fullName>;partyNameResultTo:S-getPartyNameForDate(partyId,compareDate{invoiceDate},lastNameFirst*Y)<fullName>"
		 />
