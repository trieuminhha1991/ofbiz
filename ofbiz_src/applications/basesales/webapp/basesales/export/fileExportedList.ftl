<#assign dataField = "[
			{name: 'exportName', type: 'string'}, 
			{name: 'exportTime', type: 'date', other: 'Timestamp'}, 
			{name: 'description', type: 'string'}, 
			{name: 'fileName', type: 'string'}, 
			{name: 'fileSize', type: 'string'}, 
			{name: 'path', type: 'string'}, 
			{name: 'module', type: 'string'}, 
			{name: 'createdByUserLogin', type: 'string'}, 
			{name: 'mimeType', type: 'string'}, 
			{name: 'statusId', type: 'string'}, 
		]"/>
<#assign columnlist = "
			{ text: '${uiLabelMap.CommonSTT}', dataField: '', width: '5%', columntype: 'number', 
	              cellsrenderer: function (row, column, value) {
	                  return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
	              }
			},
			{text: '${uiLabelMap.CommonFileName}', dataField: 'fileName', minwidth: 120},
			{text: '${uiLabelMap.BSDescription}', dataField: 'description', width: 160},
			{text: '${uiLabelMap.CommonCreateDate}', dataField: 'exportTime', width: 140, cellsformat: 'dd/MM/yyyy', filtertype:'range', 
				cellsrenderer: function(row, colum, value) {
					return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
				}
			},
			{text: '${uiLabelMap.CommonFileSize} (KB)', dataField: 'fileSize', width: 110, cellsalign:'right'},
			{text: '${uiLabelMap.CommonModule}', dataField: 'module', width: 80},
			{text: '${uiLabelMap.CommonPersonCreate}', dataField: 'createdByUserLogin', width: 120},
			{text: '${uiLabelMap.CommonStatusId}', dataField: 'statusId', width: 120},
			{text: '${uiLabelMap.CommonDownload}', dataField: 'path', width: 80,
				cellsrenderer: function(row, colum, value) {
					var data = $('#jqxFileExported').jqxGrid('getrowdata', row);
					if (value) {
						return '<a href=\"'+ value +'\" download=\"' + data.fileName + '\"/><span><i class=\"fa fa-download\"></i></span></a>';
					} else {
						if (data.statusId == \"EXPORTING\") {
							return '<span><i class=\"fa fa-spinner fa-pulse fa-fw\"></i></span>';
						} else {
							return '';
						}
					}
				}
			},
  		"/>
<#assign permitDelete = true>
<@jqGrid id="jqxFileExported" url="jqxGeneralServicer?sname=JQGetListFileExported" columnlist=columnlist dataField=dataField 
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		deleterow="${permitDelete?string}" removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteFileExported" deleteColumn="exportName;exportTime(java.sql.Timestamp)" 
		bindresize="true" enabletooltips="true"/>

<script type="text/javascript">
	var isProcessing = false;
	$(function(){
		$("#jqxFileExported").on('bindingcomplete', function (event) {
			isProcessing = false;
			var dataRow = $("#jqxFileExported").jqxGrid("getboundrows");
			if (typeof(dataRow) != 'undefined') {
				for (var i = 0; i < dataRow.length; i++) {
					var dataItem = dataRow[i];
					if (dataItem != window) {
						if ("EXPORTING" == dataItem.statusId) {
							isProcessing = true;
							break;
						}
					}
				}
			}
			if (isProcessing) {
				setTimeout(function(){
					$("#jqxFileExported").jqxGrid('updatebounddata');
				}, 15000);
			}
	    });
	});
</script>