<#assign dataField="[
	{name : 'invoiceId',type : 'string'},
	{name : 'statusId',type : 'string'},
	{name : 'statusDate',type : 'date',other : 'Timestamp'},
	{name : 'description',type : 'string'}
]"/>

<script>
	<#assign listStt = delegator.findByAnd("StatusItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId","INVOICE_STATUS"),null,false) !>
	var listStt = [
		<#list listStt as stt>
			{
				'statusId' : '${stt.statusId?if_exists}',
				'description' : '${stt.get('description',locale)?default('')}'
			},
		</#list>
	]
</script>
<#assign columnlist="
	{text : '${uiLabelMap.FormFieldTitle_invoiceId}',datafield : 'invoiceId',hidden: true},
	{text : '${uiLabelMap.FormFieldTitle_statusDate}',datafield : 'statusDate',filtertype : 'range',cellsformat : 'dd/MM/yyyy'},
	{text : '${uiLabelMap.CommonStatus}',datafield : 'statusId',editable : false,filtertype : 'checkedlist',cellsrenderer : function(row){
		var data = $('#jqxgrid').jqxGrid('getrowdata',row);
		if(data.description) return '<span>'+ data.description +'</span>';
		return data.statusId;
	},createfilterwidget : function(row,cellvalue,widget){
		var source = {
			localdata : listStt,
			datatype : 'array'
		};
		var filterBox = new $.jqx.dataAdapter(source,{autoBind : true});
		var records  = filterBox.records;
		records.splice(0,0,'(${StringUtil.wrapString(uiLabelMap.filterselectallstring)?default('')})');
		widget.jqxDropDownList({source : records,displayMember : 'statusId',valueMember : 'statusId',
		renderer : function(index,label,value){
			for(var i= 0;i < listStt.length;i++){
				if(listStt[i].statusId == value){
					return listStt[i].description;
				}
			}
			return label;
		}
		,selectionRenderer : function(){
			var data = widget.jqxDropDownList('getSelectedItem');
			if(data) return data.label;
			return '${StringUtil.wrapString(uiLabelMap.filterchoosestring)?default('')}'
		}})	
	}}
"/>

<@jqGrid dataField=dataField columnlist=columnlist clearfilteringbutton="true" url="jqxGeneralServicer?sname=jqGetListInvoiceStatus&invoiceId=${parameters.invoiceId?if_exists}"/>