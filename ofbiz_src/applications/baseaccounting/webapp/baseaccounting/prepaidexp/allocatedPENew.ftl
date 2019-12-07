<div id="availabelPE" style="display:none">
	<div>${uiLabelMap.BACCAddPEToPeriod}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="availabelPEGrid"></div>
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
	var OLBAvailPE = function(){
	}
	OLBAvailPE.initWindow = function(){
		$("#availabelPE").jqxWindow({
			width: '1200', height: 480, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				//Data fields 
				var datafields = [{name:  'prepaidExpId', type: 'string'},
				                  { name: 'prepaidExpName', type: 'string' },
				                  { name: 'amount', type: 'string' },
		 	 		 		  ];
				//Column of grid
				var columnlist = [{ text: '${uiLabelMap.BACCPrepaidExpId}',filterable : false, datafield: 'prepaidExpId',  width: 250,
									cellsrenderer: function(row, column, value){
									},
			       		   		  },
				                  { text: '${uiLabelMap.BACCPrepaidExpName}', datafield: 'prepaidExpName', width: 200 },
					       	      { text: '${uiLabelMap.BACCAmount}', dataField: 'amount',
				                	  cellsrenderer: function(row, columns, value){
				                		  var data = $('#availabelPEGrid').jqxGrid('getrowdata',row);
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
			        url: "JqxGetListAvailabelPEs&customTimePeriodId=${parameters.customTimePeriodId}"
			   	};
				//Create grid
			   	Grid.initGrid(config, datafields, columnlist, null, $("#availabelPEGrid"));
			}
		});
	}
	
	OLBAvailPE.openWindow = function(){
		OLBAvailPE.initWindow();
		$("#availabelPE").jqxWindow('open');
		OLBAvailPE.bindEvent();
	}
	
	OLBAvailPE.closeWindow = function(){
		$("#availabelPE").jqxWindow('close');
	}
	
	OLBAvailPE.bindEvent = function(){
		$('#alterSave').on('click', function(){
			var submitedData = {};
			submitedData['customTimePeriodId'] = '${parameters.customTimePeriodId}';
			var rowindexes = $('#availabelPEGrid').jqxGrid('getselectedrowindexes');
			var peArray = new Array();
			for(var i = 0; i < rowindexes.length; i++){
				var data = $("#availabelPEGrid").jqxGrid('getrowdata', rowindexes[i]);
				var obj = {};
				obj['prepaidExpId'] = data.prepaidExpId;
				obj['amount'] = data.amount;
				peArray[i] = obj;
			}
			submitedData['listPEs'] = JSON.stringify(peArray);
			
			//Send Ajax Request
			$.ajax({
				url: 'addPEToPeriod',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						OLBAvailPE.closeWindow();
						$('#jqxgridPrepaidExp').jqxGrid('updatebounddata');
						$('#containerjqxgridPrepaidExp').empty();
                        $('#jqxNotificationjqxgridPrepaidExp').jqxNotification({ template: 'success'});
                        $("#notificationContentjqxgridPrepaidExp").text(wgaddsuccess);
                        $("#jqxNotificationjqxgridPrepaidExp").jqxNotification("open");
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