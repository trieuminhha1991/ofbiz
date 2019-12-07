<script>
	var stateData = new Array();
	var row = {};
	row['state'] = 'open';
	row['description'] = '${StringUtil.wrapString(uiLabelMap.CommonOpen)}';
	stateData[0] = row;
	var row = {};
	row['state'] = 'close';
	row['description'] = '${StringUtil.wrapString(uiLabelMap.CommonClose)}';
	stateData[1] = row;
</script>
<#assign jqxGridId = "jqxNotify"/>	
<#assign dataField="[{ name: 'ntfId', type: 'string' },
					 { name: 'ntfGroupId', type: 'string' },
					 { name: 'targetLink', type: 'string' },
					 { name: 'action', type: 'string' },
					 { name: 'ntfType', type: 'string' },
					 { name: 'header', type: 'string' },
					 { name: 'state', type: 'string' },
					 { name: 'dateTime', type: 'date', other:'Timestamp'}
					 ]"/>

<#assign columnlist="{text: '${uiLabelMap.STT}', datafield: '', resizable: false, width: 50,filterable : false,
				    	cellsrenderer: function (row, column, value) {
				        	return '<div style=margin:4px;>' + (row + 1) + '</div>';
				    	}
					 },
                     { text: '${uiLabelMap.NotificationHeader}', datafield: 'header',
                     	cellsrenderer: function (row, column, value) {
                     		var tmpValue = $('#jqxNotify').jqxGrid('getrowdata', row);
                     		return '<span><a href=\"javascript:void(0);\" onclick=\"closeNTF(' + \"'\" + tmpValue.state + \"'\" + ',' + \"'\" + tmpValue.ntfType + \"'\" + ',' + \"'\" + tmpValue.action + \"'\" + ',' + \"'\" + tmpValue.targetLink + \"'\" + ',' + \"'\" + tmpValue.ntfId + \"'\" + ')\">' + value + '</a></span>';
                     	}
                     },
                     { text: '${uiLabelMap.NotificationDateTime}', datafield: 'dateTime', width: 180, cellsformat: 'dd/MM/yyyy HH:mm:ss', filtertype:'range'},
                     { text: '${uiLabelMap.NotificationState}', datafield: 'state', width: 120, filtertype:'checkedlist',
						 cellsrenderer: function(column, row, value){
							 if(value == 'open'){
								 return '<span>${uiLabelMap.CommonOpen}</span>';
							 }else{
								 return '<span>${uiLabelMap.CommonClose}</span>'
							 }
						 },
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(stateData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							//records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({autoDropDownHeight: true, dropDownWidth: 'auto',source: records, displayMember: 'state', valueMember: 'state',
								renderer: function(index, label, value){
									for(var i = 0; i < stateData.length; i++){
										if(stateData[i].state == value){
											return '<span>' + stateData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
                     }
					 "/>
				 
<#assign customcontrol1 = "icon-remove open-sans@${uiLabelMap.CloseAllNotify}@javascript: void(0);@closeAllNotify()">
<@jqGrid addrow="false" addType="popup" id=jqxGridId filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListEmplNotification&partyId=${userLogin.partyId?if_exists}" dataField=dataField columnlist=columnlist
		 customcontrol1=customcontrol1 defaultSortColumn="-dateTime"
		 />
<script type="text/javascript">
	function closeAllNotify(){
		bootbox.dialog("${uiLabelMap.ConfirmCloseAllNotify}?", 
			[{"label": '${uiLabelMap.wgcancel}', 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll();}
	        }, 
	        {"label": '${uiLabelMap.wgok}',
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
					sendClosingRequest();
	            }
	        }]);
	}
	function sendClosingRequest(){
		$.ajax({
				beforeSend: function(){
		        },
		        complete: function(){
		        },
				url: "closeAllNotification",
				type: "POST",
				data: {},
				dataType: "json",
				success: function(res) {
					$('#${jqxGridId}').jqxGrid('updatebounddata');
					Grid.renderMessage('${jqxGridId}', '${StringUtil.wrapString(uiLabelMap.SuccessMessageWhenClose)}' + res.numberOfNotify, {
						autoClose : true,
						template : 'success',
						appendContainer : "#container${jqxGridId}",
						opacity : 0.9,
						icon : {
							width : 25,
							height : 25,
							url : '/aceadmin/assets/images/info.jpg'
						}});
					setTimeout(function () {
				        location.reload()
				    }, 2000);
				}
			});
	}
	function closeNTF(state, ntfType, action, parameters, ntfId){
		if(ntfType === 'ONE' && state === 'open'){
			$.ajax({
	  			url: 'closeNTF',
	  			type: 'POST',
	  			data: 'ntfId=' + ntfId,
	  			success: function(data) {
	  				postNTF(action, parameters);
	  			},
	  			error: function(e) {
					alert(e.message);
	  			}
			});
		}else{
			postNTF(action, parameters);
		}
	}
	function postNTF(action, parameters) {
	    var form = $('<form></form>');
	    form.attr("method", "post");
	    form.attr("action", action);
	    
	    var kv = parameters.split(";");
	    list = [];
	    
	    for(i=0; i < kv.length; i++){
	    	var tmpParam = kv[i].split('=');
	    	var tmpNode = {};
	    	tmpNode.key = tmpParam[0];
	    	tmpNode.value = tmpParam[1];
	    	list[i] = tmpNode;
	    }
	    for(i=0; i < list.length; i++){
	        var field = $('<input></input>');
	        field.attr("type", "hidden");
	        field.attr("name", list[i].key);
	        field.attr("value", list[i].value);
	        form.append(field);
	    };
	    $(document.body).append(form);
	    form.submit();
	}
</script>