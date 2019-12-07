var Dashboard = (function(){
	var self = {};
	self.gridStack, self.gridStackElement,self.items,self.buttonUpdate;
	self.options = {
	        cell_height: 80,
	        vertical_margin: 10,
	    };
	self.init = function(){
	    self.gridStack = $('.grid-stack');
	    if(self.gridStack.length){
		if(typeof self.gridStack.gridstack === "function") {
				self.gridStack.gridstack(self.options);
				self.gridStackElement = self.gridStack.data('gridstack');
			    self.disableEditable();
			    self.initUpdateButton();
			    self.onChangeAction();
		}
		  
	    }
	};
	self.initUpdateButton = function(){
		var bd = $('body');
		if(!$('#updateGridStack').length){
			var bt ='<button class="btn btn-primary form-action-button fixed-button-action" id="updateGridStack" style="display: none">'
					+ '<i class="fa fa-check"></i>&nbsp;'+commonLabelMap.confirmButton+'</button>';
			self.buttonUpdate = $(bt);
			self.buttonUpdate.click(self.updateGrid);
			bd.append(self.buttonUpdate);
			
			var containerBtns = $('<div class="fixed-container-editable"></div>');
			var btedit ='<button class="btn btn-app" id="editGridStack">'
					+ '<i class="fa fa-edit"></i></button>';
			var edit = $(btedit);
			var header = $('#nav');
			var br = $('.breadcrumb-inner');
			$(window).scroll(function(){
				var top = $(this).scrollTop();
				var height = header.height();
				var h2 = br.height();
				if(top >= height){
					containerBtns.css('top', height + "px");
				}else{
					containerBtns.css('top', (height + h2) + "px");
				}
			});
			// edit.click(self.enableEditable);
			var btcancel ='<button class="btn btn-app" id="cancelGridStack" style="display:none"><i class="fa fa-times"></i></button>';
			var cancel = $(btcancel);
			edit.click(function(){
				self.enableEditable();
				edit.hide();
				cancel.show();
			});
			cancel.click(function(){
				window.location.reload();
			});
			containerBtns.append(edit);
			containerBtns.append(cancel);
			bd.append(containerBtns);
		}
	};
	self.onChangeAction = function(){
		self.gridStack.on('change', function (e, items) {
			self.items = items;
			self.showUpdateButton();
		});
	};
	self.enableEditable = function(){
		if(self.gridStack && self.gridStack.length){
			self.gridStackElement.enable();
		}
	};
	self.disableEditable = function(){
		if(self.gridStack && self.gridStack.length){
			self.gridStackElement.disable();
		}
	};
	self.showUpdateButton = function(){
		if(self.buttonUpdate && self.buttonUpdate.length){
			self.gridStack.addClass('grid-stack-active');
			self.buttonUpdate.show();
		}
	};
	self.hideUpdateButton = function(){
		if(self.buttonUpdate && self.buttonUpdate.length){
			self.gridStack.removeClass('grid-stack-active');
			self.buttonUpdate.hide();
		}
	};
	self.updateGrid = function(){
		self.items = self.prepareDataBeforeSend();
		if(self.items && self.items.length){
			$.ajax({
				url : "updatePagePortletLocation",
				type: "POST",
				data: {
					data : JSON.stringify(self.items)
				},
				beforeSend: function(){
					if(Loading){
						Loading.show();
					}
				},
				complete: function(){
					if(Loading){
						Loading.hide();
					}
				},
				success: function(res){
					if(res['_ERROR_MESSAGE_']){
						self.buildAlert(commonLabelMap.UpdateError);
					}else{
						self.buildAlert(commonLabelMap.UpdateSuccess);
						self.hideUpdateButton();
						self.disableEditable();
						$("#cancelGridStack").hide();
						$("#editGridStack").show();
					}
				}
			});
		}

	};
	self.buildAlert = function(mes){
		bootbox.dialog(mes, [{
			"label" : "OK",
			"class" : "btn-mini btn-primary form-action-button",
		}]);
	};

	self.prepareDataBeforeSend = function(){
		if(self.gridStack && self.gridStack.length){
			var res = _.map(self.gridStack.find('.grid-stack-item:visible'), function (el) {
			    el = $(el);
			    var obj = el.data('_gridstack_node');
			    var portalPageId = el.find('input[name="portalPageId"]').val();
				var portalPortletId = el.find('input[name="portalPortletId"]').val();
				var porletSeqId = el.find('input[name="portletSeqId"]').val();
			    return {
			        columnSeqId: !isNaN(obj.x) ? (obj.x + 1) : 1,
					rowSeqId: !isNaN(obj.y) ? (obj.y + 1) : 1,
					colspan: !isNaN(obj.width) ? obj.width : 1,
					rowspan: !isNaN(obj.height) ? obj.height : 1,
			        portalPageId : portalPageId,
			        portalPortletId : portalPortletId,
			        porletSeqId : porletSeqId
			    };
			});
			return res;
		}
	};
	self.updateRowHeight = function(height){
		if(self.gridStack && self.gridStackElement){
			self.gridStackElement.cell_height(height);
		}
	};
	self.commit = function(){
		if(self.gridStack && self.gridStackElement){
			self.gridStackElement.commit();
		}
	};
	self.initUpdate = function(){
		if(self.gridStack && self.gridStackElement){
			self.gridStackElement.batch_update();
		}
	};
	$(document).ready(function(){
		self.init();
	});
	return self;
})();
