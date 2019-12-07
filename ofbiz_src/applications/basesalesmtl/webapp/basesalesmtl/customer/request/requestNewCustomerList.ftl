<script>
	var states = [<#list partyStatus?if_exists as status>
		{
			statusId : "${status.statusId}",
			description: "${StringUtil.wrapString(status.get('description',locale))}"
		},
	</#list>]
	<#assign partyTypeIds = delegator.findByAnd("PartyType", {"parentTypeId" : "PARTY_GROUP_CUSTOMER"}, null, false)!/>
	var partyTypeData = [
	<#if partyTypeIds?exists>
		<#list partyTypeIds as item>
		{	partyTypeId: '${item.partyTypeId}',
			description: '${StringUtil.wrapString(item.get("description", locale))}'
		},
		</#list>
	</#if>
	];
	var cellclassname = function (row, column, value, data) {
		var statusId = data.statusId;
		if (statusId == "PARTY_UPDATED") {
			return "background-running";
		} else {
			return "background-waiting";
		}
    };
</script>
<#assign id="CustomerRegistration"/>
<#assign dataField="[
				{ name: 'customerId', type: 'string'},
				{ name: 'customerName', type: 'string'},
				{ name: 'partyTypeId', type: 'string'},
				{ name: 'officeSiteName', type: 'string'},
				{ name: 'routeId', type: 'string'},
				{ name: 'routeName', type: 'string'},
				{ name: 'address', type: 'string'},
				{ name: 'stateProvinceGeoId', type: 'string'},
				{ name: 'stateProvinceGeoName', type: 'string'},
				{ name: 'stateProvinceGeoNameG', type: 'string'},
				{ name: 'districtGeoId', type: 'string'},
				{ name: 'districtGeoName', type: 'string'},
				{ name: 'districtGeoNameG', type: 'string'},
				{ name: 'wardGeoId', type: 'string'},
				{ name: 'wardGeoName', type: 'string'},
				{ name: 'wardGeoNameG', type: 'string'},
				{ name: 'latitude', type: 'string'},
				{ name: 'longitude', type: 'string'},
				{ name: 'url', type: 'string'},
				{ name: 'createdByUserLogin', type: 'string'},
				{ name: 'lastUpdatedByUserLogin', type: 'string'},
				{ name: 'note', type: 'string'},
				{ name: 'statusId', type: 'string'},
				{ name: 'gender', type: 'string'},
				{ name: 'productStoreId', type: 'string'},
				{ name: 'salesMethodChannelEnumId', type: 'string'},
				{ name: 'storeName', type: 'string'},
				{ name: 'phone', type: 'string'},
				{ name : 'salesmanId', type: 'string'},
				{ name: 'salesmanName', type: 'string'},
				{ name: 'createdDate', type: 'date', other: 'Timestamp'},
				{ name: 'startDate', type: 'date', other: 'Timestamp'},
				{ name: 'birthDate', type: 'date'}
			]"/>
<#assign columnlist="
				{ text: '${StringUtil.wrapString(uiLabelMap.BSCreatedDate)}', datafield: 'createdDate', width: 150, cellclassname: cellclassname, cellsformat: 'dd/MM/yyyy', filtertype:'range',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSCreatedBy)}', datafield: 'createdByUserLogin', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSRequestId)}', datafield: 'customerId', width: 100, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerType)}', datafield: 'partyTypeId', width: 150, cellclassname: cellclassname, filtertype: 'checkedlist', 
					cellsrenderer: function(row, column, value){
						if (partyTypeData.length > 0) {
							for(var i = 0 ; i < partyTypeData.length; i++){
    							if (value == partyTypeData[i].partyTypeId){
    								return '<span title =\"' + partyTypeData[i].description +'\">' + partyTypeData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	}, 
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (partyTypeData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(partyTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'partyTypeId', valueMember: 'partyTypeId',
								renderer: function(index, label, value){
									if (partyTypeData.length > 0) {
										for(var i = 0; i < partyTypeData.length; i++){
											if(partyTypeData[i].partyTypeId == value){
												return '<span>' + partyTypeData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
		   			}
		   		},
				{ text: '${StringUtil.wrapString(uiLabelMap.DAOwnerStoreName)}', datafield: 'customerName', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSAgentName)}', datafield: 'officeSiteName', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSRouteName)}', datafield: 'routeName', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.PhoneNumber)}', datafield: 'phone', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSAddress)}', datafield: 'address', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSWard)}', datafield: 'wardGeoNameG', width: 150, cellclassname: cellclassname,
				    cellsrenderer: function(row, column, value){
				        if (OlbCore.isNotEmpty(value)) {
				            return '<span>' + value + '</span>';
				        }
                        var data = $('#${id}').jqxGrid('getrowdata', row);
                        if (OlbCore.isNotEmpty(data.wardGeoName)) {
				            return '<span>' + data.wardGeoName + '</span>';
				        }
                        return \"\";
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.District)}', datafield: 'districtGeoNameG', width: 150, cellclassname: cellclassname,
				    cellsrenderer: function(row, column, value){
				        if (OlbCore.isNotEmpty(value)) {
				            return '<span>' + value + '</span>';
				        }
                        var data = $('#${id}').jqxGrid('getrowdata', row);
                        if (OlbCore.isNotEmpty(data.districtGeoName)) {
				            return '<span>' + data.districtGeoName + '</span>';
				        }
                        return \"\";
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.CityProvince)}', datafield: 'stateProvinceGeoNameG', width: 150, cellclassname: cellclassname,
				    cellsrenderer: function(row, column, value){
				        if (OlbCore.isNotEmpty(value)) {
				            return '<span>' + value + '</span>';
				        }
				        var data = $('#${id}').jqxGrid('getrowdata', row);
				        if (OlbCore.isNotEmpty(data.stateProvinceGeoName)) {
				            return '<span>' + data.stateProvinceGeoName + '</span>';
				        }
                        return \"\";
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSLatitude)}', datafield: 'latitude', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSLongitude)}', datafield: 'longitude', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSLinkImage)}', datafield: 'url', width: 150, cellclassname: cellclassname,
					 cellsrenderer:  function (row, column, value, a, b, data){
					   var vl = '\"' + value + '\"';
					   var str = \"<div class='cell-custom-grid'><a href='javascript:ContexMenuCustomer.viewImageByUrl(\" + vl +\")' \";
					   if(!value){
							str += ' class=\"disabled\"' ;
					   }
					   str += \">${StringUtil.wrapString(uiLabelMap.BSViewImage)}</a></div>\"
					   return str;
				   }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}', datafield: 'gender', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSStatus)}', datafield: 'statusId', width: 150, filtertype: 'checkedlist', cellclassname: cellclassname,
					cellsrenderer: function(row, column, value){
						for(var i = 0; i < states.length; i++){
							if(value == states[i].statusId){
								return '<div class=\"cell-custom-grid\">' + states[i].description + '</div>';
							}
						}
						return '<span>' + value + '</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						widget.jqxDropDownList({ filterable:true,source: states, displayMember: 'description', valueMember : 'statusId'});
					}
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSUpdatedBy)}', datafield: 'lastUpdatedByUserLogin', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.DADistributor)}', datafield: 'productStoreId', width: 150, cellclassname: cellclassname},
				{ text: '${StringUtil.wrapString(uiLabelMap.BSNote)}', datafield: 'note', width: 150, cellclassname: cellclassname},
			"/>

<@jqGrid id=id dataField=dataField columnlist=columnlist clearfilteringbutton="true" selectionmode="singlerow"
	showtoolbar="true" url="jqxGeneralServicer?sname=JQGetCustomerRegistration" contextMenuId="Context${id}"  mouseRightMenu="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateRequestNewCustomer" editColumns="customerId;url;gender;customerName;officeSiteName;productStoreId;partyTypeId;salesmanId;routeId;stateProvinceGeoId;districtGeoId;wardGeoId;address;phone;note"
/>

<div id="Context${id}" class="hide">
	<ul>
		<li action="edit">
			<i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.Edit)}
		</li>
		<li action="approve">
			<i class="fa fa-check"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.HRCommonAccept)}
		</li>
		<li action="viewimage" id='viewimage'>
			<i class="fa fa-eye"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewImage)}
		</li>
	</ul>
</div>

<div id="ViewImagePopup" class='hide'>
	<div>
		${uiLabelMap.BSLinkImage}
	</div>
	<div class="form-window-container">
		<@loading id="ImageLoading" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/>
		<div id="image-container" class='image-preview'></div>
	</div>
</div>

<#include "requestNewCustomerUpdatePopup.ftl"/>

<script>
	$(function(){
		ContexMenuCustomer.init();
	});
	var ContexMenuCustomer = (function(){
		var gridJQ = $('#${id}');
		var contextMenuJQ = $('#Context${id}');
		
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.contextMenu.create($("#Context${id}"));
			
			jOlbUtil.windowPopup.create($("#ViewImagePopup"), {width: 800, height: 600});
		};
		var initEvent = function(){
			$('#ViewImagePopup').on('close', function(){
				$('#image-container').html('');
			});
			
			contextMenuJQ.on('itemclick', function (event) {
		        var args = event.args;
		        var itemId = $(args).attr('action');
		        var rowIndex = gridJQ.jqxGrid('getSelectedRowindexes');
		        try {
					switch (itemId){
						case 'edit':
							var rowData = gridJQ.jqxGrid('getrowdata', rowIndex);
							RequestNewCust.editCustomer(rowData, rowIndex);
							break;
						case 'approve':
							approveCustomer(rowIndex);
							break;
						case 'viewimage':
							var data = gridJQ.jqxGrid('getrowdata', rowIndex);
							if(data.url){
								viewImage(rowIndex);
							}
							break;
						default:
							break;
					}
		        } catch(e) {
					contextMenuJQ.jqxMenu('close');
		        }
			});
			contextMenuJQ.on('shown', function(event){
				var args = event.args;
		        var itemId = $(args).attr('action');
		        var row = gridJQ.jqxGrid('getSelectedRowindex');
				var data = gridJQ.jqxGrid('getrowdata', row);
				if (data.url) {
					contextMenuJQ.jqxMenu('disable', 'viewimage', false);
				} else {
					contextMenuJQ.jqxMenu('disable', 'viewimage', true);
				}
			});
			
			$('#ViewImagePopup').on('close', function(){
				$('#image-container').html('');
			});
		};
		var viewImage = function(row){
			var rowData = gridJQ.jqxGrid('getrowdata', row);
			if (rowData) {
				loadImage(rowData.url);
			} else {
				loadImage(null);
			}
			$('#ViewImagePopup').jqxWindow('open');
		};
		var viewImageByUrl = function(imageUrl){
			loadImage(imageUrl);
			$('#ViewImagePopup').jqxWindow('open');
		};
		var loadImage = function(imageUrl){
			if (imageUrl) {
				var img = new Image();
				Loading.show('ImageLoading');
				img.onload = function() {
					var obj = $('#image-container');
					obj.html('');
					obj.append($(img));
					Loading.hide('ImageLoading');
				};
				img.src = imageUrl;
			} else {
				$('#image-container').html('');
			}
		};
		var approveCustomer = function(rowIndexs){
			if (rowIndexs && rowIndexs.length) {
				var arrData = [];
				var flagApprove = true;
				for (var x in rowIndexs) {
					var i = rowIndexs[x];
					var rowData = gridJQ.jqxGrid('getrowdata', i);
					arrData.push(rowData.customerId);
					if (OlbCore.isEmpty(rowData.districtGeoId) 
							|| OlbCore.isEmpty(rowData.stateProvinceGeoId) 
							|| OlbCore.isEmpty(rowData.customerName)
							|| OlbCore.isEmpty(rowData.phone)
							|| OlbCore.isEmpty(rowData.address)
							|| OlbCore.isEmpty(rowData.productStoreId)
							|| OlbCore.isEmpty(rowData.partyTypeId)
							|| OlbCore.isEmpty(rowData.salesmanId)) {
                        flagApprove = false;
                        bootbox.dialog("${uiLabelMap.BSUpdateDataBeforeApprove}",
                                [{
                                    "label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                                    "icon": 'fa fa-remove',
                                    "class": 'btn  btn-danger form-action-button pull-right',
                                    "callback": function () {
                                        bootbox.hideAll();
                                    }
                                },
                                {
                                    "label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                    "icon": 'fa-check',
                                    "class": 'btn btn-primary form-action-button pull-right',
                                    "callback": function () {
                                    	var okIsApproveNow = true;
                                        RequestNewCust.editCustomer(rowData, i, okIsApproveNow);
                                    }
                                }]);
					    break;
                    }
				}
				var obj = {
					customerId : JSON.stringify(arrData)
				};
				if (flagApprove) {
                    Request.post('approveRequestNewCustomer', obj, function(res){
                        if (res.partyId) {
                            reloadGrid();
                            Grid.renderMessage('${id}', '${StringUtil.wrapString(uiLabelMap.HRApprovalSuccessfully)}', 'success');
                        } else {
                            Grid.renderMessage('${id}', res._ERROR_MESSAGE_, 'error');
                        }
                    });
                }
			}
		};
		var reloadGrid = function(){
			$("#CustomerRegistration").jqxGrid('clearselection');
			$("#CustomerRegistration").jqxGrid('updatebounddata');
			$("#CustomerRegistration").removeData('approve');
		};
		return {
			init: init,
			viewImage: viewImage,
			viewImageByUrl: viewImageByUrl,
			loadImage: loadImage,
		};
	}());
</script>