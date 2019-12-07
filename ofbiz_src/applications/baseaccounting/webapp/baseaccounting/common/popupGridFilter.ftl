<script type="text/javascript">
	var OLBGridFilter = function(){
		
	}
	OLBGridFilter.prototype.initWindow(window, source, title, theme, input){
		var header = $('<div>' + title + '</div>');
		window.append(header);
		var content = $('<div style="overflow: hidden;"></div>');
		window.append(content);
		var formContent = $('<div class="form-window-content"></div>');
		var formAction = $('<div class="form-action"></div>');
		content.append(formContent);
		content.append(formAction);
		formContent.append('<input type="hidden" id="jqxwindow' + input + 'key"/>');
		formContent.append('<input type="hidden" id="jqxwindow' + input + 'value"/>');
		var grid = $('<div id="jqxgrid' + input.toLowerCase() + '"></div>');
		formContent.append(grid);
		var btnCancel = $('<button id="cancel' + input + '" class="btn btn-danger form-action-button pull-right"></button>');
		btnCancel.append('<i class="fa-remove"></i>');
		btnCancel.append('${uiLabelMap.CommonCancel}');
		formAction.append(btnCancel);
		var btnSave = $('<button id="save' + input + '" class="btn btn-danger form-action-button pull-right"></button>');
		btnSave.append('<i class="fa-check"></i>');
		btnSave.append('${uiLabelMap.CommonSave}');
		formAction.append(btnSave);
		window.jqxWindow({
	        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: btnCancel, modalOpacity: 0.7, width: 1000, maxWidth: 1000, height: 800     
	    });
		window.on('open', function (event) {
    		var offset = grid.offset();
    		window.jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
		});
	}
	OLBGridFilter.prototype.initGrid(source){
		Grid.initGrid({url: source.url, 
		  autorowheight: false, 
		  filterable: true, 
		  width: '100%', 
		  height: autoheight, 
		  cache: true, 
		  source: {pagesize: 10, cache: true}, 
		  source.datafields, 
		  source.columns, 
		  null, 
		  grid
	  });	
	};
	OLBGridFilter.prototype.bindEvent = function(){
		cntrlIsPressed = false;
		btnSave.click(function () {
			saveFilter()
		});
		$(document).keydown(function(event){
		    if(event.ctrlKey)
		        cntrlIsPressed = true;
		});
		
		$(document).keyup(function(event){
			if(event.which=='17')
		    	cntrlIsPressed = false;
		});
	};
	var saveFilter = function(window, grid, key){
		var tIndex = grid.jqxGrid('selectedrowindex');
		var data = grid.jqxGrid('getrowdata', tIndex);
		key.val(data.partyId);
		window.jqxWindow('close');
		var e = jQuery.Event("change");
		key.trigger(e);
	};
	$(document).ready(function(){
		filter.init();
	});
</script>