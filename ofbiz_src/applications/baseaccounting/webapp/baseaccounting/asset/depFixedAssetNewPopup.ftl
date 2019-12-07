<div id="newDepFixedAsset" style="display:none">
	<div>${uiLabelMap.BACCAddFixedAssetToDepPeriod}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="depFixedAssetGrid"></div>
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
<script>
	var THEME = 'olbius';
	var OLBNewDepFA = function(){
	}
	OLBNewDepFA.initWindow = function(){
		$("#newDepFixedAsset").jqxWindow({
			width: '1200', height: 480, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				//Data fields 
				var datafields = [{name:  'fixedAssetId', type: 'string'},
				                  { name: 'fixedAssetName', type: 'string' },
				                  { name: 'totalDep', type: 'string' },
				                  { name: 'depreciation', type: 'string' },
		 	 		 		  ];
				//Column of grid
				var columnlist = [{ text: '${uiLabelMap.BACCFixedAssetId}',filterable : false, datafield: 'fixedAssetId',  width: 250,
									cellsrenderer: function(row, column, value){
										var data = $('#depFixedAssetGrid').jqxGrid('getrowdata', row);
										return '<span><a href=ViewFixedAsset?fixedAssetId=' + data.fixedAssetId + '>' + data.fixedAssetId + '</a></span>';
									},
			       		   		  },
				                  { text: '${uiLabelMap.BACCFixedAssetName}', datafield: 'fixedAssetName', width: 200 },
				                  { text: '${uiLabelMap.BACCDepreciation}', dataField: 'totalDep',
				                	  cellsrenderer: function(row, columns, value){
				                		  var data = $('#depFixedAssetGrid').jqxGrid('getrowdata',row);
				                		  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
				                	  }  
					       	      },
				                  { text: '${uiLabelMap.BACCPeriodDepreciation}', dataField: 'depreciation',
				                	  cellsrenderer: function(row, columns, value){
				                		  var data = $('#depFixedAssetGrid').jqxGrid('getrowdata',row);
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
			        url: "JqxGetListAVAFixedAssets&customTimePeriodId=${parameters.customTimePeriodId?if_exists}"
			   	};
				//Create grid
			   	Grid.initGrid(config, datafields, columnlist, null, $("#depFixedAssetGrid"));
			}
		});
		OLBNewDepFA.bindEvent();
	}
	
	OLBNewDepFA.openWindow = function(){
		$("#newDepFixedAsset").jqxWindow('open');
	}
	
	OLBNewDepFA.closeWindow = function(){
		$("#newDepFixedAsset").jqxWindow('close');
	}
	
	OLBNewDepFA.bindEvent = function(){
		$('#alterSave').on('click', function(){
			var submitedData = {};
			submitedData['customTimePeriodId'] = '${parameters.customTimePeriodId?if_exists}';
			var rowindexes = $('#depFixedAssetGrid').jqxGrid('getselectedrowindexes');
			var faArray = new Array();
			for(var i = 0; i < rowindexes.length; i++){
				var data = $("#depFixedAssetGrid").jqxGrid('getrowdata', rowindexes[i]);
				var obj = {};
				obj['fixedAssetId'] = data.fixedAssetId;
				obj['fixedAssetName'] = data.fixedAssetName;
				obj['depreciation'] = data.depreciation;
				faArray[i] = obj;
			}
			submitedData['listFixedAssets'] = JSON.stringify(faArray);
			
			//Send Ajax Request
			$.ajax({
				url: 'addFAToPeriod',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						OLBNewDepFA.closeWindow();
						$('#jqxgridAsset').jqxGrid('updatebounddata');
						$('#depFixedAssetGrid').jqxGrid('updatebounddata');
					}else if(data._ERROR_MESSAGE_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}else if(data._ERROR_MESSAGE_LIST_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}
				}
			});
		});
	}
	
	$(function(){
		OLBNewDepFA.initWindow();
	})
</script>