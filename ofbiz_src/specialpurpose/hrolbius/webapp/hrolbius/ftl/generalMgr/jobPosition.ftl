<@jqGridMinimumLib/>
<#assign datafield ="[{name: 'partyId', type: 'string'},
					  {name: 'partyName', type: 'string'},
					  {name: 'emplPositionTypeId', type: 'string'},
					  {name: 'emplPositionTypeDesc', type: 'string'},
					  {name: 'totalEmplPositionId', type: 'number'},
					  {name: 'totalEmplPosNotFulfill', type: 'number'}]"/>

<script type="text/javascript">
	<#assign rowsDetails = "function (index, parentElement, gridElement, datarecord){
		var partyId = datarecord.partyId;
		var emplPositionTypeId = datarecord.emplPositionTypeId;
		var url = 'getPositionByPositionTypeAndParty';
		var id = datarecord.uid.toString();
		var grid = $($(parentElement).children()[0]);
        $(grid).attr(\"id\",\"jqxgridDetail\" + \"_\" + id);
        var jqxGridDetailsSource = {
        		datafields: [
        			{name: 'emplPositionId', type: 'string'},
        			{name: 'employeePartyId', type: 'string'},
        			{name: 'employeePartyName', type: 'string'},
        			{name: 'fromDate', type: 'date', other: 'Timestamp'},
        			{name: 'thruDate', type: 'date', other: 'Timestamp'},
        			{name: 'actualFromDate', type: 'date', other: 'Timestamp'}
        		],
        		cache: false,
        		datatype: 'json',
				type: 'POST',
				data: {partyId: partyId, emplPositionTypeId: emplPositionTypeId},
		        url: url,
		        id: 'emplPositionId',
		        beforeprocessing: function (data) {
		        	jqxGridDetailsSource.totalrecords = data.TotalRows;
		        },
		        pagenum: 0,
		        pagesize: 15,
		        root: 'listReturn'
        };
        var nestedGridColums = [
			{datafield: 'emplPositionId', hidden: true},
			{text: '${uiLabelMap.EmployeeId}', datafield: 'employeePartyId', width: 150},
			{text: '${uiLabelMap.EmployeeName}', datafield: 'employeePartyName', width: 200},
			{text: '${uiLabelMap.FromDateFulfillment}', datafield: 'fromDate', cellsalign: 'left', width: 200, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
			{text: '', datafield: 'thruDate', hidden: true},
			{text: '${uiLabelMap.PositionActualFromDate}', datafield: 'actualFromDate', cellsalign: 'left', cellsformat: 'dd/MM/yyyy ', columntype: 'template'}
		];
        var nestedGridAdapter = new $.jqx.dataAdapter(jqxGridDetailsSource);
        if (grid != null) {
        	grid.jqxGrid({
        		source: nestedGridAdapter, 
        		width: '96%', 
        		height: 180,
        		autoheight: false,
        		//autoheight: true,
        		virtualmode: true,
        		showtoolbar: true,
        		rendertoolbar: function (toolbar) {
					var container = $(\"<div id='toolbarcontainer' class='widget-header'><h4>\" + \"</h4></div>\");
					toolbar.append(container);
					container.append('<button id=\"viewInWindow\" class=\"grid-action-button\" style=\"margin-left:20px;\">${uiLabelMap.ViewInWindow}</button>');
					var buttonView = $('#viewInWindow');
					buttonView.click(function(){
						initOpenJqxWindow(partyId, emplPositionTypeId, datarecord.emplPositionTypeDesc);
					});
        		},
        		rendergridrows: function () {
    	            return nestedGridAdapter.records;
    	        },
    	        pageSizeOptions: ['15', '30', '50', '100'],
    	        pagerMode: 'advanced',
    	        columnsResize: true,
    	        pageable: true,
    	        editable: false,
    	        columns: nestedGridColums,
    	        selectionmode: 'singlerow',
    	        theme: 'olbius'
        	});
        }
	}
	"/> 
	<#assign columnlist = "{text: '${uiLabelMap.EmployeePositionId}', datafield: 'emplPositionTypeId', width: 200},
	   					   {text: '${uiLabelMap.CommonEmplPositionType}', datafield: 'emplPositionTypeDesc', width: 250},
						   {text: '${uiLabelMap.HROrganization}', datafield: 'partyName', width: 220},
						   {datafield: 'partyId',hidden: true},
						   {text: '${uiLabelMap.TotalEmplPositionId}', datafield: 'totalEmplPositionId', width: 140, cellsalign: 'right'},
						   {text: '${uiLabelMap.TotalEmplPosNotFulfill}', datafield: 'totalEmplPosNotFulfill', cellsalign: 'right'}" />
   	function openJqxWindow(jqxWindowDiv){
		var wtmp = window;
		var tmpwidth = jqxWindowDiv.jqxWindow('width');
		jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
		jqxWindowDiv.jqxWindow('open');
	}
   	
	function initOpenJqxWindow(partyId, emplPositionTypeId, emplPositionTypeDesc){
	   var source = $("#jqxGridEmplPosition").jqxGrid("source");
	   source._source.data = {partyId: partyId, emplPositionTypeId: emplPositionTypeId};
	   $("#jqxGridEmplPosition").jqxGrid("source", source);
	   var title = "${uiLabelMap.EmplListHavePositionType} " + emplPositionTypeDesc;
	   setWindowTitle($("#jqxWindowPositionDetail"), title);
	   openJqxWindow($("#jqxWindowPositionDetail"));
   }
   function setWindowTitle(jqxWindowDiv, title){
	   jqxWindowDiv.jqxWindow('setTitle', title);
   }
</script>					  
<@jqGrid url="jqxGeneralServicer?sname=getListEmplPositionInOrg&hasrequest=Y" dataField=datafield columnlist=columnlist
	clearfilteringbutton="true" id="jqxgrid" initrowdetails="true" initrowdetailsDetail=rowsDetails
	editable="false" groupable="true"  groupsexpanded="false" rowdetailsheight="200"
	showtoolbar = "true" deleterow="true" jqGridMinimumLibEnable="false" 
/>
<script type="text/javascript">
	$(document).ready(function () {
		$("#jqxgrid").on('rowDoubleClick', function(event){
			 var args = event.args;
		    // row's bound index.
		    var boundIndex = args.rowindex;
		    var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
		    
		});
		var url = 'getPositionByPositionTypeAndParty';
		var jqxGridSource = {
        		datafields: [
        			{name: 'emplPositionId', type: 'string'},
        			{name: 'employeePartyId', type: 'string'},
        			{name: 'employeePartyName', type: 'string'},
        			{name: 'fromDate', type: 'date', other: 'Timestamp'},
        			{name: 'thruDate', type: 'date', other: 'Timestamp'},
        			{name: 'actualFromDate', type: 'date', other: 'Timestamp'}
        		],
        		cache: false,
        		datatype: 'json',
				type: 'POST',
				data: {},
		        url: url,
		        id: 'emplPositionId',
		        beforeprocessing: function (data) {
		        	jqxGridSource.totalrecords = data.TotalRows;
		        },
		        pagenum: 0,
		        pagesize: 15,
		        root: 'listReturn'
        }; 
		var jqxGridAdapter = new $.jqx.dataAdapter(jqxGridSource);
		var jqxGridColums = [
  			{datafield: 'emplPositionId', hidden: true},
  			{text: '${uiLabelMap.EmployeeId}', datafield: 'employeePartyId', width: 150},
  			{text: '${uiLabelMap.EmployeeName}', datafield: 'employeePartyName', width: 200},
  			{text: '${uiLabelMap.FromDateFulfillment}', datafield: 'fromDate', cellsalign: 'left', width: 200, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
  			{text: '', datafield: 'thruDate', hidden: true},
  			{text: '${uiLabelMap.PositionActualFromDate}', datafield: 'actualFromDate', cellsalign: 'left', cellsformat: 'dd/MM/yyyy ', columntype: 'template'}
  		];
		$("#jqxGridEmplPosition").jqxGrid({
    		source: jqxGridAdapter, 
    		width: 700, 
    		height: 438,
    		autoheight: false,
    		virtualmode: true,
    		rendergridrows: function () {
	            return jqxGridAdapter.records;
	        },
	        pageSizeOptions: ['15', '30', '50', '100'],
	        pagerMode: 'advanced',
	        columnsResize: true,
	        pageable: true,
	        editable: false,
	        columns: jqxGridColums,
	        selectionmode: 'singlerow',
	        theme: 'olbius'
    	});
		
		$("#jqxWindowPositionDetail").jqxWindow({
			showCollapseButton: false, maxHeight: 490, maxWidth: 720, minHeight: 490, minWidth: 720, height: 490, width: 720,
			theme: 'olbius', isModal: true, autoOpen: false, 
            initContent: function () {
            	  
            }
		});
	});
</script>
<div id="jqxWindowPositionDetail" style="display: none">
	<div id="windowHeader"></div>
	<div style="overflow: hidden;">
	 	<div id="jqxGridEmplPosition"></div>
	</div>
</div>