 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 <#assign listPS = delegator.findList("Person",null,null,null,null,false) !>
 <#assign listType = delegator.findList("EmplLeaveType",null,null,null,null,false) !>
 <#assign listReasonType = delegator.findList("EmplLeaveReasonType",null,null,null,null,false) !>
 <#assign listStatus = delegator.findList("StatusItem",null,null,null,null,false) !>
<#assign dataField="[
						{ name: 'partyId', type: 'string'},
						{ name: 'thruDate', type: 'date',other : 'Timestamp'},
						{ name: 'leaveTypeId', type: 'string'},
						{ name: 'fromDate', type: 'date',other : 'Timestamp' },
						{ name: 'emplLeaveReasonTypeId', type: 'string'},
						{ name: 'leaveStatus', type: 'string'},
						{ name: 'dateApplication', type: 'date' ,other : 'Timestamp'}
					]"/>
<#assign columnlist= " {
						text : '${uiLabelMap.EmployeeName}',	dataField : 'partyId',filtertype: 'olbiusdropgrid',width: '200px',cellsrenderer : 
							function(row,columnfield,value){
								for(var i = 0 ;i < data.length; i++){
									if(value == data[i].partyId){
										return '<span>'+ data[i].name  +'</span>';
									}
								}
							}
						},
						{ 
						text : '${uiLabelMap.leaveTypeId}' ,dataField : 'leaveTypeId' ,filtertype: 'checkedlist', cellsrenderer :
							function(row,columnfield,value){
								for(var i = 0 ;i < type.length ; i++){
									if(value == type[i].typeId){
										return '<span>' + type[i].description +'</span>';
									}
								}
							},createfilterwidget: function (column, columnElement, widget) {
							  		var sourceOrd =
								    {
								        localdata: type,
								        datatype: \"array\"
								    };
					   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceOrd,
					                {
					                    autoBind: true
					                });
					                var uniqueRecords = filterBoxAdapter.records;
					   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					   				
		        					widget.jqxDropDownList({selectedIndex: 0, source: uniqueRecords, displayMember: 'description', valueMember: 'typeId', dropDownWidth: 250,
		        						renderer: function (index, label, value) {
		                    				for(i = 0; i < type.length; i++){
												if(type[i].typeId == value){
													return '<span>' + type[i].description + '</span>'
												}
		                    				}
		                    			return value;
		        					}});
									widget.jqxDropDownList('checkAll');
    						}
						},{
						text : '${uiLabelMap.HrThruDate}' , dataField : 'thruDate' , filtertype : 'range' , cellsformat : 'dd/MM/yyyy',
						},
						{
							text : '${uiLabelMap.HrFromDate}' , dataField : 'fromDate', filtertype : 'range' , cellsformat : 'dd/MM/yyyy'
						},{
							text : '${uiLabelMap.emplLeaveReasonTypeId}' ,dataField : 'emplLeaveReasonTypeId',width: '130px',filtertype : 'checkedlist',cellsrenderer : 
							function(row,columnfield,value){
								for(var i = 0 ;i < reasonType.length ; i++){
									if(value == reasonType[i].typeId){
										return '<span>' + reasonType[i].description +'</span>';
									}
								}
							},createfilterwidget : function(column,columnElement,widget){
								var sourceOrd =
								    {
								        localdata: reasonType,
								        datatype: \"array\"
								    };
					   				var filterBoxAdapter = new $.jqx.dataAdapter(sourceOrd,
					                {
					                    autoBind: true
					                });
					                var uniqueRecords = filterBoxAdapter.records;
					   				uniqueRecords.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
					   				
		        					widget.jqxDropDownList({selectedIndex: 0, source: uniqueRecords, displayMember: 'description', valueMember: 'typeId', dropDownWidth: 250,
		        						renderer: function (index, label, value) {
		                    				for(i = 0; i < reasonType.length; i++){
												if(reasonType[i].typeId == value){
													return '<span>' + reasonType[i].description + '</span>'
												}	
		                    				}
		                    			return value;
		        					}});
									widget.jqxDropDownList('checkAll');
							}
						},{
							text : '${uiLabelMap.leaveStatus}',dataField : 'leaveStatus',width : '90px',filterable : false,cellsrenderer : 
							function(row,columnfield,value){
								for(var i = 0 ;i < listStt.length ; i++){
									if(value == listStt[i].statusId){
										return '<span>' + listStt[i].description +'</span>';
									}
								}
							}
						},{
							text : '${uiLabelMap.DateApplication}' , dataField : 'dateApplication',width : '150px',filterable: false , cellsformat : 'dd/MM/yyyy'
						}" />
	<div id="jqxwindowpartyId">
	<div>${uiLabelMap.HrEmployeeList}</div>
	<div style="overflow: hidden;">
		<table id="PartyIdFrom">
			<tr>
				<td>
					<input type="hidden" id="jqxwindowpartyIdkey" value=""/>
					<input type="hidden" id="jqxwindowpartyIdvalue" value=""/>
					<div id="jqxgridpartyid"></div>
				</td>
			</tr>
		</table>
       <div class="center"><input type="button" id="alterSave3" value="${uiLabelMap.CommonSave}" /><input id="alterCancel3" type="button" value="${uiLabelMap.CommonCancel}" /></div>
	</div>
</div>	   			
<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQgetListEmployeeLeave" />
<script language="JavaScript" type="text/javascript">
		var data = [
			<#list listPS as ps>
			<#assign name = StringUtil.wrapString(ps.firstName)?default("") + " " + StringUtil.wrapString(ps.middleName)?default("") + " " +StringUtil.wrapString(ps.lastName)?default("")/>
					{
						partyId : "${ps.partyId?if_exists}",
						name : "${StringUtil.wrapString(name?if_exists)}"
					},
			</#list>	
		];
		var type = [
			<#list listType as type>
				{
					typeId : "${type.leaveTypeId?if_exists}",
					description : "${StringUtil.wrapString(type.description?if_exists)}"
				},
			</#list>
		
		];
		
		var listStt = [
			<#list listStatus as status>
				{
					statusId : "${status.statusId?if_exists}",
					description : "${StringUtil.wrapString(status.description?if_exists)}"
				},
			</#list>
		];
		
		var reasonType = [
			<#list listReasonType as type>
				{
					typeId : "${type.emplLeaveReasonTypeId?if_exists}",
					description : "${StringUtil.wrapString(type.description?if_exists)}"
				},
			</#list>
		];
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
		//form lookup party
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
            { name: 'firstName', type: 'string' },
            { name: 'middleName', type: 'string' },
            { name: 'lastName', type: 'string' }
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
        url: 'jqxGeneralServicer?sname=JQgetEmployeeInOrg',
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
            }else{
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
    $('#jqxgridpartyid').jqxGrid(
    {
        width:800,
        source: dataAdapterF,
        filterable: true,
        virtualmode: true, 
        sortable:true,
        editable: false,
        showfilterrow: true,
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
          { text: '${uiLabelMap.EmployeePartyIdTo}', datafield: 'partyId',width : '25%'},
          { text: '${uiLabelMap.HRolbiusEmployeeFirstName}', datafield: 'firstName', width:'25%'},
          { text: '${uiLabelMap.HRolbiusEmployeeMiddleName}', datafield: 'middleName', width:'25%'},
          { text: '${uiLabelMap.HRolbiusEmployeeLastName}', datafield: 'lastName', width:'25%'}
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
