<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<#assign listPs = delegator.findList("Person",null,null,null,null,false) !>
<#assign listClaimType = delegator.findList("ClaimType",null,null,null,null,false) !>
<#assign listStt = delegator.findList("StatusItem",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId","EMPL_CLAIM_STATUS"),null,null,null,false) !>
<script type="text/javascript">
	var psName = [
		<#list listPs as ps>
		{
			partyId : "${ps.partyId?if_exists}",
			name : "${StringUtil.wrapString(ps.lastName?default(''))} ${StringUtil.wrapString(ps.middleName?default(''))} ${StringUtil.wrapString(ps.firstName?default(''))}"
		},	
		</#list>
	];
	var claimType = [
		<#list listClaimType as cl>
		{
			clTypeId : "${cl.claimTypeId?if_exists}",
			description : "${StringUtil.wrapString(cl.description?default(''))}"
		},	
		</#list>
	];
	var sttClaim = [
		<#list listStt as l>
		{
			sttId : "${l.statusId?if_exists}",
			description : "${StringUtil.wrapString(l.description?default(''))}"
		},	
		</#list>
	];
</script>
<#assign dataField = "[
			{name : 'emplClaimId',type : 'string'},
			{name : 'partyId', type : 'string'},
			{name : 'fullName', type : 'string'},
			{name : 'claimTypeId', type : 'string'},
			{name : 'createdDate', type : 'date',other : 'Timestamp'},
			{name : 'title', type : 'string'},
			{name : 'statusId', type : 'string'}
]"/>

<#assign columnlist = "
				{text : '${uiLabelMap.EmplClaimId}' , datafield : 'emplClaimId',width : '90px'},
				{text : '${uiLabelMap.EmployeeClaims}' , filtertype: 'olbiusdropgrid', datafield : 'partyId',width : '200px',
				 cellsrenderer:
					 	function(row, colum, value){
					 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 		return '<span>' + data.fullName + '[<a href=\"EmployeeProfile?partyId=' + data.partyId +'\">'+ data.partyId +'</a>]</span>';
					 	},
					 	createfilterwidget: function (column, columnElement, widget) {
			   				widget.width(90);
			   			}},
				{text : '${uiLabelMap.EmplClaimType}' , datafield : 'claimTypeId',filtertype : 'checkedlist',cellsrenderer : 
					function(row,columnfield,value){
						for(var i = 0 ;i < claimType.length;i ++){
							if(claimType[i].clTypeId == value){
								return '<span>' + claimType[i].description+'</span>';	
							}
						}
					},createfilterwidget : function(column,columnElement,widget){
								var source = {
									localdata : claimType,
									datatype : \"array\"
								};
								var dataAdapter = new $.jqx.dataAdapter(source,{
										autoBind : true									
									});
								var records = dataAdapter.records;
								records.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({selectedIndex : 0,source : records,displayMember : 'description' , valueMember : 'clTypeId',dropDownWidth : 250, filterable : false,
									renderer : function(column,label,value){
										for(var i = 0 ; i < claimType.length ; i++){
											if(claimType[i].clTypeId == value){
												return claimType[i].description;
											}
										}
										return value;
									}								
								});
								widget.jqxDropDownList('checkAll');
							}
					},
				{text : '${uiLabelMap.FormFieldTitle_createdDate}',width : '120px',datafield : 'createdDate',filtertype : 'range',cellsformat : 'dd/MM/yyyy'
				},
				{text : '${uiLabelMap.NotificationHeader}',width : '150px',datafield : 'title',filterable : false},
				{text : '${uiLabelMap.CommonStatus}',width : '150px',datafield : 'statusId',filtertype: 'checkedlist',cellsrenderer : 
					function(row,columnfield,value){
						for(var i = 0 ; i < sttClaim.length ; i++){
							if(sttClaim[i].sttId == value){
								return '<span>' + sttClaim[i].description+ '</span>';
							}
						}
					
					}
				,createfilterwidget :
						 function(column,columnElement,widget){
								var source = {
									localdata : sttClaim,
									datatype : \"array\"
								};
								var dataAdapter = new $.jqx.dataAdapter(source,{
										autoBind : true									
									});
								var records = dataAdapter.records;
								records.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({selectedIndex : 0,source : records,displayMember : 'description' , valueMember : 'sttId',dropDownWidth : 250, filterable : false,
									renderer : function(column,label,value){
										for(var i = 0 ; i < sttClaim.length ; i++){
											if(sttClaim[i].sttId == value){
												return sttClaim[i].description;
											}
										}
										return value;
									}								
								});
								widget.jqxDropDownList('checkAll');
							}},
				{text : '',width : '70px',filterable : false,cellsrenderer : 
					function(row,columnfield,value){
					var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
						return '<a class=\"icon-eye-open open-sans\" href=\"EmplClaimApproval?emplClaimId='+ data.emplClaimId +'\"></a>';
					}
				}
"/>
<@jqGrid filtersimplemode="true" filterable="true" addrow="true"  addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQgetListEmplClaims" otherParams="fullName:S-JQgetFullNameEmployee(inputValue{partyId})<outputValue>" jqGridMinimumLibEnable="false"
		createUrl="jqxGeneralServicer?sname=createEmplClaim&jqaction=C" addColumns="claimTypeId;title;partyClaimSettlement;description;observerIdList(java.util.List)"
		/>	
<div id="jqxwindowpartyId" style="display: none;">
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
       <div style="display : inline !important;" align="center"><input type="button" id="alterSave3" value="${uiLabelMap.CommonSave}" /><input id="alterCancel3" type="button" value="${uiLabelMap.CommonCancel}" /></div>
	</div>
</div>		
	
<div class="row-fluid" id="alterpopupWindow" style="display:none;">
 	<div>${uiLabelMap.accCreateNew}</div>
 	 <div style="overflow: hidden;">
	<form id="EditEmplClaim" class="basic-form form-horizontal" name="EditEmplClaim">
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">${uiLabelMap.EmplClaimType}</label>
			<div class="controls">
				<select name="claimTypeId" id="EditEmplClaim_claimTypeId">					
					<#list claimTypeList as claimType>
						<option value="${claimType.claimTypeId}">${claimType.description}</option>
					</#list>
				</select>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">${uiLabelMap.NotificationHeader}</label>
			<div class="controls">
				<input type="text" name="title" id="EditEmplClaim_title">
			</div>
		</div>
		<#assign observerIdList = Static["com.olbius.util.PartyUtil"].getAllManagerInOrg(delegator)>
		<div class="control-group no-left-margin">
			<label class="control-label asterisk">${uiLabelMap.ClaimSettlement}</label>
			<div class="controls">
				<@htmlTemplate.renderComboxBox name="partyClaimSettlement" id="partyClaimSettlement" emplData=observerIdList container="jqxComboBox1" multiSelect="false"/>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.ObserverClaimList}</label>
			<div class="controls">
				<@htmlTemplate.renderComboxBox name="ObserverIdList" id="observerIdList" emplData=observerIdList container="jqxComboBox2"/>
			</div>
		</div>
		
		<div class="control-group no-left-margin">
			<label class="control-label">${uiLabelMap.CommonDescription}</label>
			<div class="controls">
				<textarea class="note-area no-resize" name="description" id="EditEmplClaim_description" autocomplete="off"></textarea>
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label class="control-label">&nbsp;</label>
			<div class="controls">
				<button type="button" class="btn btn-primary btn-mini" name="submitButton" id="alterSave"><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
				<button type="button" class="btn btn-danger btn-mini" name="submitButton" id="alterCancel"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
			</div>
		</div>
	</form>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#alterpopupWindow").jqxWindow({
        width: 1200, height: 500, maxHeight: 500, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.9 ,initContent : function(){
        	$('#EditEmplClaim_description').jqxEditor({
				width : '500px',
				height : '150px',
				theme: 'energyblue'
	});
        }         
    });
    
     $('#alterpopupWindow').on('open', function (event) {
    	var offset = $("#jqxgrid").offset();
   		$("#alterpopupWindow").jqxWindow({ position: { x: parseInt(offset.left) - 20, y: parseInt(offset.top) - 20} });
	});
	$('#EditEmplClaim').jqxValidator({
                rules: [
	                       { input: '#EditEmplClaim_claimTypeId', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'keyup, blur', rule: function(){
	                       		var tmp = $('#EditEmplClaim_claimTypeId').val();
	                       		if(tmp){ return true;}else return false;
	                       		
	                       } },
	                     	{ input: '#EditEmplClaim_title', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'keyup, blur', rule: 'required' },
	                       { input: '#jqxComboBox1', message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', action: 'keyup, blur', rule: function(){
	                       		var value  = $('#jqxComboBox1').jqxComboBox('getSelectedItem');
	                       		if(value){
	                       			return true;
	                       		}
	                       		return false;
	                      	 }
	                       }
                       ]
           			 });
	$('#alterSave').click(function(){
		$('#EditEmplClaim').jqxValidator('validate');
	});
	$('#EditEmplClaim').on('validationSuccess',function(){
		var arr = $('#jqxComboBox2').jqxComboBox('getSelectedItems');
		var listOb = new Array();
		for(var key in arr){
			 var map = {};
			if(arr[key]){
					map['observer'+key] = arr[key].value;
					listOb.push(map);
				}
		}
		var dataObs;
		if(listOb && listOb.length > 0){
			 dataObs = JSON.stringify(listOb);
		}
		var row = {};
		row = {
			claimTypeId : $('#EditEmplClaim_claimTypeId').val(),
			title : $('#EditEmplClaim_title').val(),
			partyClaimSettlement : $('#partyClaimSettlement').val(),
			observerIdList : dataObs,
			description : $('#EditEmplClaim_description').jqxEditor('val')
		};
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	    // select the first row and clear the selection.
	    $("#jqxgrid").jqxGrid('clearSelection');                        
	    $("#jqxgrid").jqxGrid('selectRow', 0);  
	    $("#alterpopupWindow").jqxWindow('close');
	    $('#EditEmplClaim').trigger('reset');
      	$("#jqxComboBox1").jqxComboBox('clearSelection');
      	$("#jqxComboBox2").jqxComboBox('clearSelection');
      	$('#EditEmplClaim_description').jqxEditor('val','');
	});
	$('#alterCancel').click(function(){
		$('#EditEmplClaim').trigger('reset');
		$("#jqxComboBox1").jqxComboBox('clearSelection');
		$("#jqxComboBox2").jqxComboBox('clearSelection');
		$('#EditEmplClaim_description').jqxEditor('val','');
	});
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
		