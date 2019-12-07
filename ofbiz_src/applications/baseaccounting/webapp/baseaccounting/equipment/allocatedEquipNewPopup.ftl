<div id="selEqipment" style="display:none">
	<div>${uiLabelMap.BACCAddEquipmentToPeriod}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="selEquipGrid"></div>
			</div>
		</div>
		<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.BACCSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.BACCCancel}</button>
	   		</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<script>
	var THEME = 'olbius';
	var OLBSelEquip = function(){
	}
	OLBSelEquip.initWindow = function(){
		$("#selEqipment").jqxWindow({
			width: '1200', height: 480, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				//Data fields 
				var datafields = [{name:  'equipmentId', type: 'string'},
				                  { name: 'equipmentName', type: 'string' },
				                  { name: 'amount', type: 'string' },
		 	 		 		  ];
				//Column of grid
				var columnlist = [{ text: '${uiLabelMap.BACCEquipmentId}',filterable : false, datafield: 'equipmentId',  width: 250,
									cellsrenderer: function(row, column, value){
									},
			       		   		  },
				                  { text: '${uiLabelMap.BACCEquimentName}', datafield: 'equipmentName', width: 200 },
					       	      { text: '${uiLabelMap.BACCAmount}', dataField: 'amount',
				                	  cellsrenderer: function(row, columns, value){
				                		  var data = $('#selEquipGrid').jqxGrid('getrowdata',row);
				                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
				                	  }  
					       	      },
				                ];
				//Configuration for grid
				var config = {
			   		width: '100%', 
			   		virtualmode: true,
			   		showfilterrow: false,
			   		showtoolbar: false,
			   		selectionmode: 'checkbox',
			   		pageable: true,
			   		sortable: false,
			        filterable: false,
			        editable: false,
			        rowsheight: 26,
			        localization: getLocalization(),
			        url: "JqxGetListAVLEquips&customTimePeriodId=${parameters.customTimePeriodId}"
			   	};
				//Create grid
			   	Grid.initGrid(config, datafields, columnlist, null, $("#selEquipGrid"));
			}
		});
	}
	
	OLBSelEquip.openWindow = function(){
		OLBSelEquip.initWindow();
		$("#selEqipment").jqxWindow('open');
		OLBSelEquip.bindEvent();
	}
	
	OLBSelEquip.closeWindow = function(){
		$("#selEqipment").jqxWindow('close');
	}
	
	OLBSelEquip.bindEvent = function(){
		$('#alterSave').on('click', function(){
			var submitedData = {};
			submitedData['customTimePeriodId'] = '${parameters.customTimePeriodId}';
			var rowindexes = $('#selEquipGrid').jqxGrid('getselectedrowindexes');
			var equipArray = new Array();
			for(var i = 0; i < rowindexes.length; i++){
				var data = $("#selEquipGrid").jqxGrid('getrowdata', rowindexes[i]);
				var obj = {};
				obj['equipmentId'] = data.equipmentId;
				obj['amount'] = data.amount;
				equipArray[i] = obj;
			}
			submitedData['listEquips'] = JSON.stringify(equipArray);
			
			//Send Ajax Request
			$.ajax({
				url: 'addEquipToAlloc',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						OLBSelEquip.closeWindow();
						$('#equipGrid').jqxGrid('updatebounddata');
						$('#containerequipGrid').empty();
                        $('#jqxNotificationequipGrid').jqxNotification({ template: 'success'});
                        $("#notificationContentequipGrid").text(wgaddsuccess);
                        $("#jqxNotificationequipGrid").jqxNotification("open");
					}else if(data._ERROR_MESSAGE_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}else if(data._ERROR_MESSAGE_LIST_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}
				}
			});
		});
	}
</script>