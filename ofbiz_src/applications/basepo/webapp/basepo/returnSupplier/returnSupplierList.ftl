<style>
	.order{
		color: #037C07;
		font-weight: bold;
		vertical-align: bottom;
		line-height: 20px;
	}
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<@jqOlbCoreLib />

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<#assign dataField="[
				{ name: 'returnId', type: 'string' },
				{ name: 'entryDate', type: 'date', other: 'Timestamp' },
				{ name: 'toPartyId', type: 'string' },
				{ name: 'groupName', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'destinationFacilityId', type: 'string' }]"/>

<#assign columnlist = "
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},	
				{ text: '${uiLabelMap.POReturnId}', dataField: 'returnId', pinned: true, width: 150,
					cellsrenderer: function(row, colum, value) {
						var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
						return \"<span><a href='viewGeneralReturnSupplier?returnId=\" + data.returnId + \"'>\" + data.returnId + \"</a></span>\";
					}
				},
				{ text: '${uiLabelMap.DAEntryDateReturn}', dataField: 'entryDate', width: '20%', cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						var data = $('#listOrderCustomer').jqxGrid('getrowdata', row);
						var newDate = new Date(value);
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.POSupplier}', dataField: 'groupName'},
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '15%', filtertype: 'checkedlist',
					cellsrenderer: function(row, colum, value) {
						for(var i = 0; i < listStatusItem.length; i++){
							if(value == listStatusItem[i].statusId){
								return \"<span>\" + listStatusItem[i].description + \"</span>\";
							}
						}
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(listStatusItem, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'statusId',
							renderer: function(index, label, value){
								if (listStatusItem.length > 0) {
									for(var i = 0; i < listStatusItem.length; i++){
										if(listStatusItem[i].statusId == value){
											return '<span>' + listStatusItem[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
		   			}
				}"/>

<@jqGrid id="listOrderCustomer" url="jqxGeneralServicer?sname=JQListReturnSupplierSupplier"
	filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
	customTitleProperties="POListReturnSupplier" autoshowloadelement="false" viewSize="15" showdefaultloadelement="false" 
	customcontrol1="fa fa-plus@${uiLabelMap.DmsCreateNew}@createNewReturnSupplier"/>

<script type="text/javascript">

<#assign listStatusItem = delegator.findByAnd("StatusItem", {"statusTypeId" : "PORDER_RETURN_STTS"}, null, false)/>
	var listStatusItem = [<#list listStatusItem as item>{
		statusId: "${item.statusId?if_exists}",
		description: '${StringUtil.wrapString(item.get("description", locale))}'
	},</#list>];
</script>