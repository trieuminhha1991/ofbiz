<@jqGridMinimumLib/>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.EmplInsuranceList_title}</h4>
		<div class="widget-toolbar none-content">
			<div id="dropDownButton" style="margin-top: 5px;">
				<div style="border: none;" id="jqxTree">
						
				</div>
			</div>	
		</div>
	</div>
	<div class="widget-body">
		<div id="emplInsuranceData">
			<#--<!-- ${screens.render("component://hrolbius/widget/InsuranceScreens.xml#EmplInsuranceListData")} -->
		</div>
			
	</div>	
</div> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>

<#assign dataFields = "[{name: 'partyId', type: 'string'},
						{name: 'partyName', type: 'string'},
						{name: 'partyGroupId', type: 'string'},
						{name: 'salaryInsurance', type: 'number'},
						{name: 'socialInsuranceNbr', type: 'string'},
						{name: 'socialRate', type: 'string'},
						{name: 'healthInsuranceNbr', type:'string'},
						{name: 'healthRate', type: 'string'},
						{name: 'salaryInsuranceUomId', type: 'string'}]" />
 <script type="text/javascript">
 	var treePartyGroupArr = new Array();
 	<#list treePartyGroup as tree>
		var row = {};
		row["id"] = "${tree.id}_partyGroupId";
		row["text"] = "${tree.text}";
		row["parentId"] = "${tree.parentId}_partyGroupId";
		row["value"] = "${tree.idValueEntity}"
		treePartyGroupArr[${tree_index}] = row;
	</#list>  
	 var source =
	 {
	     datatype: "json",
	     datafields: [
	         { name: 'id'},
	         { name: 'parentId'},
	         { name: 'text'} ,
	         { name: 'value'}
	     ],
	     id: 'id',
	     localdata: treePartyGroupArr
	 };
	 
	 var dataAdapter = new $.jqx.dataAdapter(source);
	 // perform Data Binding.
	 dataAdapter.dataBind();
	 // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
	 // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
	 // specifies the mapping between the 'text' and 'label' fields.  
	 var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
	 <#assign columnlist = "{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', cellsalign: 'left', width: 120, editable: false, filterable: true},
		{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', cellsalign: 'left', width: 130, editable: false, filterable: true},
		{text: '${uiLabelMap.EmployeeCurrentDept}', datafield: 'partyGroupId', cellsalign: 'left', editable: false, filterable: false},
		{text: '${uiLabelMap.InsuranceSalary}', datafield: 'salaryInsurance', cellsalign: 'left', width: 130, editable: false, filterable: false,
			cellsrenderer: function (row, column, value) {
		 		 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		 		 if (data && data.salaryInsurance){
		 		 	return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(data.salaryInsurance, data.salaryInsuranceUomId) + \"</div>\";
		 		 }
	 		 }	
		},
		{text: '${uiLabelMap.SocialInsuranceNbr}', datafield: 'socialInsuranceNbr', cellsalign: 'left', width: 130, editable: false, filterable: false},
		{text: '${uiLabelMap.InsuranceRate}', datafield: 'socialRate', cellsalign: 'right', width: 110, editable: false, filterable: false,
			cellsrenderer: function (row, column, value) {
				 return '<div style=\"margin-top: 3px; text-align: right\">' + (value * 100) + '%</div>';
	 		 }		
		},
		{text: '${uiLabelMap.HealthInsuranceNbr}', datafield: 'healthInsuranceNbr', cellsalign: 'left', width: 130, editable: false, filterable: false},
		{text: '${uiLabelMap.InsuranceRate}', datafield: 'healthRate', cellsalign: 'right', width: 110, editable: false, filterable: false,
			cellsrenderer: function (row, column, value) {
		 		 return '<div style=\"margin-top: 3px; text-align: right\">' + (value * 100) + '%</div>';
	 		 }	
		},
		{text: '',  datafield: 'salaryInsuranceUomId', cellsalign: 'left', width: 130, editable: false, filterable: false, hidden: true}
		"/>
	 $(document).ready(function () {
		var theme = 'olbius';
		$('#jqxWindowEmplInsuranceDetails').jqxWindow({
	        showCollapseButton: false, isModal: true, minWidth: '800px', height: 500, width: '800px',
	        autoOpen: false, theme: 'olbius',
	        initContent: function () {
	           
	        }
	    });
		jQuery("#jqxgrid").on('rowDoubleClick', function(event){
			var boundIndex = event.args.rowindex;
			var data = $('#jqxgrid').jqxGrid('getrowdata', boundIndex);
			var partyId = data.partyId;
			
			$.ajax({
				url: "getEmplInsuranceDetails",
				data: {partyId: partyId},
				type: 'POST',
				success: function(data){
					$("#EmplInsuranceDetailsContent").html(data);
				}
			});
			$('#jqxWindowEmplInsuranceDetails').jqxWindow("setTitle", "${uiLabelMap.EmplInsuranceDetailOf} " + data.partyName);
			$('#jqxWindowEmplInsuranceDetails').jqxWindow('open');
		});
		
		$("#dropDownButton").jqxDropDownButton({ width: 250, height: 25, theme: theme});
		$('#jqxTree').on('select', function(event){
	    	var id = event.args.element.id;
	    	var item = $('#jqxTree').jqxTree('getItem', event.args.element);
	    	/* var value = jQuery("#jqxTree").jqxTree('getItem', $("#"+id)[0]).value; */
	    	setDropdownContent(event.args.element);
	        var tmpS = $("#jqxgrid").jqxGrid('source');
	        var partyId = item.value;
	        
	        tmpS._source.url = "jqxGeneralServicer?hasrequest=Y&sname=JQListEmplInsurance&partyGroupId=" + partyId;
	        $("#jqxgrid").jqxGrid('source', tmpS);
	        	       
	     });
	     $('#jqxTree').jqxTree({ source: records,width: "250px", height: "200px", theme: theme});
	     <#if expandedList?has_content>
		 	<#list expandedList as expandId>
		 		$('#jqxTree').jqxTree('expandItem', $("#${expandId}_partyGroupId")[0]);
		 	</#list>
		 </#if>    
		 <#if expandedList?has_content>
		 	var initElement = $("#${expandedList.get(0)}_partyGroupId")[0];
		 	setDropdownContent(initElement);
		 </#if>
		  
		 $('div[id^="dialog-delete"]').bind("dialogopen", function( event, ui ) {
			$("#jqxWindowEmplInsuranceDetails").jqxWindow('disable');
		} );
	});
	 function setDropdownContent(element){
		 var item = $("#jqxTree").jqxTree('getItem', element);
		 var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
	     $("#dropDownButton").jqxDropDownButton('setContent', dropDownContent);
	 }
	 
	 function refreshGrid(){
		 $("#jqxgrid").jqxGrid('updatebounddata');
	 }
 </script>
 <@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
		 filterable="true" deleterow="false" editable="false" addrow="false" jqGridMinimumLibEnable="false"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQListEmplInsurance&partyGroupId=${partyIdDefault}" id="jqxgrid" 
		 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlerow" />
 
 <div id="treeGrid">
 </div>
 <div class="row-fluid">
	<div id="jqxWindowEmplInsuranceDetails">
		<div id="EmplInsuranceDetailsHeader"></div>
		<div id="EmplInsuranceDetailsContent" style="overflow: hidden;">
		
		</div>
	</div>
</div>		 			