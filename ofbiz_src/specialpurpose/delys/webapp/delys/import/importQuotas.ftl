<#assign dataField="[{ name: 'quotaId', type: 'string'},
				   { name: 'quotaName', type: 'string'},
				   { name: 'quotaTypeId', type: 'string'},
				   { name: 'description', type: 'string'},
				   { name: 'fromDate', type: 'date', other: 'Timestamp'},
				   { name: 'thruDate', type: 'date', other: 'Timestamp'}
				   ]"/>

<#assign columnlist="{text: '${uiLabelMap.SequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.QuotaId}', datafield: 'quotaId', width: 150, editable: false,
						cellsrenderer: function(row, colum, value){
					        var link = 'editQuota?quotaId=' + value;
					        return '<span><a href=\"' + link + '\">' + value + '</a></span>';
						}
					},
					{ text: '${uiLabelMap.QuotaName}', datafield: 'quotaName', width: 200, editable: true},
					{ text: '${uiLabelMap.QuotaType}', datafield: 'quotaTypeId', width: 150, editable: false,
						cellsrenderer: function(row, colum, value){
	    			        value?value=mapQuotaType[value]:value;
	    			        return '<span>' + value + '</span>';
						}
					},
					{ text: '${uiLabelMap.description}', datafield: 'description', minWidth: 250, editable: false},
	    		    { text: '${uiLabelMap.AvailableFromDate}', datafield: 'fromDate', width: 200, editable: true, cellsformat: 'dd/MM/yyyy - hh:mm:ss', columntype: 'datetimeinput'},
	    		    { text: '${uiLabelMap.AvailableThruDate}', datafield: 'thruDate', width: 200, editable: true, cellsformat: 'dd/MM/yyyy - hh:mm:ss', columntype: 'datetimeinput'}
					"/>
							
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
	url="jqxGeneralServicer?sname=JQGetListImportQuotas" updateUrl="jqxGeneralServicer?sname=updateQuota&jqaction=U"
	editColumns="quotaId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);description"
/>

<div id = "myEditor"></div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/ckeditor/ckeditor.js"></script>
<script>
    var ck = CKEDITOR.instances;
    
	var quotaType = [
				<#if quotaType?exists>
					<#list quotaType as item>
					{
						quotaTypeId: "${item.quotaTypeId?if_exists}",
						description: "${item.description?if_exists}"
					},
					</#list>
				</#if>
	                 ];
	var mapQuotaType = {
		<#if quotaType?exists>
			<#list quotaType as item>
				"${item.quotaTypeId?if_exists}": "${item.description?if_exists}",
			</#list>
		</#if>
	};
	
	$("#jqxgrid").on("cellDoubleClick", function (event){
    		    var args = event.args;
    		    var rowBoundIndex = args.rowindex;
    		    var rowVisibleIndex = args.visibleindex;
    		    var rightclick = args.rightclick;
    		    var ev = args.originalEvent;
    		    var columnindex = args.columnindex;
    		    var dataField = args.datafield;
    		    var value = args.value;
    		    var data = $('#jqxgrid').jqxGrid('getrowdata', rowBoundIndex);
		        var quotaId = data.quotaId;
    		    if (dataField == 'description') {
    		    	$("#description" + quotaId).jqxTooltip('destroy'); 
//				        		    	editDescription(value, rowBoundIndex);
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
