<div id="newDecFixedAsset" style="display:none">
	<div>${uiLabelMap.BACCAddFixedAssetToDecrement}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="decFixedAssetGrid"></div>
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
<script src="/accresources/js/acc.utils.js"></script>	
<script>
	var THEME = 'olbius';
	var OLBNewDecFA = function(){
	}
	OLBNewDecFA.initWindow = function(){
		$("#newDecFixedAsset").jqxWindow({
			width: '1200', height: 480, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: THEME,
			initContent: function () {
				//Data fields 
				var datafields = [{name:  'fixedAssetId', type: 'string'},
				                  { name: 'fixedAssetName', type: 'string' },
				                  { name: 'remainValue', type: 'string' },
		 	 		 		  ];
				//Column of grid
				var columnlist = [{ text: '${uiLabelMap.BACCFixedAssetId}',filterable : false, datafield: 'fixedAssetId',  width: 250,
									cellsrenderer: function(row, column, value){
										var data = $('#decFixedAssetGrid').jqxGrid('getrowdata', row);
										if(data !== (undefined || null))
										{
											return '<span><a href=ViewFixedAsset?fixedAssetId=' + data.fixedAssetId + '>' + data.fixedAssetId + '</a></span>';
										}
										return '<span></span>';
									},
			       		   		  },
				                  { text: '${uiLabelMap.BACCFixedAssetName}', datafield: 'fixedAssetName', width: 200 },
					       	      { text: '${uiLabelMap.BACCRemainingValue}', dataField: 'remainValue',
				                	  cellsrenderer: function(row, columns, value){
				                		  var data = $('#decFixedAssetGrid').jqxGrid('getrowdata',row);
				                		  if(data !== (undefined || null))
				                		  {
				                			  return '<span>'+formatcurrency(value, data.uomId)+'</span>';
				                		  }
				                		  return '<span></span>';
				                	  }  
					       	      }
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
			        url: "JqxGetListAVLDecFAs&decrementId=${parameters.decrementId}"
			   	};
				//Create grid
			   	Grid.initGrid(config, datafields, columnlist, null, $("#decFixedAssetGrid"));
			}
		});
		
		OLBNewDecFA.bindEvent();
	}
	
	OLBNewDecFA.openWindow = function(){
		$("#newDecFixedAsset").jqxWindow('open');
	}
	
	OLBNewDecFA.closeWindow = function(){
		$("#newDecFixedAsset").jqxWindow('close');
	}
	
	OLBNewDecFA.bindEvent = function(){
		$('#alterSave').on('click', function(){
			var d = $('#decFixedAssetGrid').jqxGrid('getrows');
			if(Array.isArray(d) && d.length == 0)
			{
				bootbox.dialog("${uiLabelMap.NoAssetValid}", 
						[
							{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
								"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }
						]);
				return;
			}	
			
			var submitedData = {};
			submitedData['decrementId'] = '${parameters.decrementId}';
			var rowindexes = $('#decFixedAssetGrid').jqxGrid('getselectedrowindexes');
			var faArray = new Array();
			for(var i = 0; i < rowindexes.length; i++){
				var data = $("#decFixedAssetGrid").jqxGrid('getrowdata', rowindexes[i]);
				var obj = {};
				obj['fixedAssetId'] = data.fixedAssetId;
				faArray[i] = obj;
			}
			submitedData['listFixedAssets'] = JSON.stringify(faArray);
			
			//Send Ajax Request
			$.ajax({
				url: 'addFAToDecrement',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						OLBNewDecFA.closeWindow();
						$('#jqxgridAsset').jqxGrid('updatebounddata');
						$('#decFixedAssetGrid').jqxGrid('updatebounddata');
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
		OLBNewDecFA.initWindow();
	})
</script>