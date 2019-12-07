<div id="ViewImagePopup" class='hide'>
	<div>
		${uiLabelMap.BSLinkImage}
	</div>
	<div class="form-window-container">
		<p class='align-center' id="emptyImage" style="display: none">
			${uiLabelMap.BSEmptyImage}
		</p>
		<@loading id="ImageLoading" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/>
		<div id="image-container" class='image-preview'></div>
	</div>
</div>
<div id="updatePopup" class='hide'>
	<div id="updatePopup-title">
	${StringUtil.wrapString(uiLabelMap.BSEvaluationPromos)}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label>${uiLabelMap.BSChooseImage}</label>
				</div>
				<div class="span7">
					<input type="file" accept="image/*" id="ava" class='file-upload'/>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class='asterisk'>${uiLabelMap.BSResult}</label>
				</div>
				<div class="span7">
					<div id="marking"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelMarking" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveMarking" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script>
	var PromosRegistration = (function(){
		var theme = 'olbius';
		var self = {};
		self.grid = $('#${id}');
		self.popup = $('#ViewImagePopup');
		self.updatePopup = $('#updatePopup');
		self.image = "";
		self.currentParty = null;
		self.initImageWindow = function() {
			self.popup.jqxWindow({
				width : 800,
				height : 600,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				theme : theme
			});
			self.popup.on('open', function(){
				self.loadImage(self.image);
			});
			self.popup.on('close', function(){
				$('#image-container').html('');
			});
			$('#ava').change(self.uploadImage);
		};
		self.initUpdateWindow = function() {
			self.updatePopup.jqxWindow({
				width : 400,
				height : 180,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				cancelButton: '#cancelMarking',
				initContent : function(){
					$('#marking').jqxDropDownList({
						width: 200,
						autoDropDownHeight: true,
						source: marks,
						displayMember: "description",
						valueMember: "enumId",
						placeHolder: "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}"
					});
					self.validateUpdateForm();
				},
				theme : theme
			});
			self.updatePopup.on('open', function() {
				if (self.updatePopup.data("action") == "update") {
					self.updatePopup.find("#updatePopup-title").html("${StringUtil.wrapString(uiLabelMap.BSEvaluationPromos)}");
				} else {
					self.updatePopup.find("#updatePopup-title").html("${StringUtil.wrapString(uiLabelMap.DAMarkValue)}");
				}
			});
			self.updatePopup.on('close', function() {
				self.currentParty = null;
				Grid.clearForm(self.updatePopup);
			});
			$('#saveMarking').click(self.updateAction);
		};
		self.validateUpdateForm = function(){
			self.updatePopup.jqxValidator({
				rules: [
					<#--{input: '#ava', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur, change',
						rule: function(input, commit){
							var data = $('#ava').prop('files')[0];
							if(data){
								return true;
							}
							return false;
						}
					},-->
					{input: '#marking', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
		                    var index = input.jqxDropDownList('getSelectedIndex');
		                    return index != -1;
		                }
					}
				]
		});
		};
		self.acceptCustomerReg = function(row){
			var data = self.getGridData(row);
			var obj = {
				partyId : data.partyId,
				productPromoId : data.productPromoId,
				productPromoRuleId: data.productPromoRuleId
			};
			self.grid.jqxGrid('updaterow', row, obj);
		};
		self.cancelCustomerReg = function(row){
			var data = self.getGridData(row);
			var dataMap = {
				partyId : data.partyId,
				productPromoId : data.productPromoId,
				productPromoRuleId: data.productPromoRuleId,
				statusId: "PROMO_REGISTRATION_CANCELLED",
			};
			Request.post("changeStatusRegPromotion", dataMap, function(res){
				if(!res._ERROR_MESSAGE_ && !res._ERROR_MESSAGE_LIST_){
					setTimeout(function(){
						self.grid.jqxGrid('clearSelection');
						self.grid.jqxGrid('updatebounddata');
					}, 100);
				}
			});
		};
		self.updateResult = function(row){
			self.updatePopup.data("action", "update");
			var data = self.getGridData(row);
			self.currentParty = {
				partyId : data.partyId,
				productPromoId : data.productPromoId,
				productPromoRuleId : data.productPromoRuleId,
			};
			if(data.fromDate){
				self.currentParty.fromDate = data.fromDate.getTime();
			}
			if(self.updatePopup && self.updatePopup.length){
				self.updatePopup.jqxWindow('open');
			}
		};
		self.uploadImage = function(){
			var data = $('#ava').prop('files')[0];
			if(data){
				Request.uploadFile(data, function(res){
					Loading.hide('loadingMacro');
					var path = res["path"];
					if(path){
						self.currentParty.url = path;
					}
				}, function(){
					Loading.show('loadingMacro');
				});
			}
		};
		self.updateAction = function(){
			if(self.currentParty && self.updatePopup.jqxValidator('validate')){
				var index = $("#marking").jqxDropDownList('getSelectedItem');
				var res =  index ? index.value : "";
				self.currentParty.resultEnumId = res;
				if (self.updatePopup.data("action") == "update") {
					Request.post("updateRegistrationPromotion", self.currentParty, function(res){
						if(!res._ERROR_MESSAGE_ && !res._ERROR_MESSAGE_LIST_){
							self.updatePopup.jqxWindow('close');
							setTimeout(function(){
								self.grid.jqxGrid('clearSelection');
								self.grid.jqxGrid('updatebounddata');
							}, 100);
						}
					});
				} else {
					Request.post("createRegistrationEvaluation", self.currentParty, function(res){
						if(!res._ERROR_MESSAGE_ && !res._ERROR_MESSAGE_LIST_){
							self.updatePopup.jqxWindow('close');
							setTimeout(function(){
								var gridId = self.updatePopup.data("gridId");
								if (gridId) {
									$("#" + gridId).jqxGrid('clearSelection');
									$("#" + gridId).jqxGrid('updatebounddata');
								}
							}, 100);
						}
					});
				}
			}
		};
		self.viewImage = function(row){
			if(!isNaN(row)){
				var data = self.getGridData(row);
				self.image = data.url;
			}else{
				self.image = row;
			}
			if(self.popup && self.popup.length){
				self.popup.jqxWindow('open');
			}
		};
		self.getGridData = function(row){
			if(self.grid && self.grid.length){
				return self.grid.jqxGrid('getrowdata', row);
			}
		};
		self.loadImage = function(image){
			if(image){
				$('#emptyImage').hide();
				var img = new Image();
				Loading.show('ImageLoading');
				img.onload = function() {
					var obj = $('#image-container');
					obj.html('');
					obj.append($(img));
					Loading.hide('ImageLoading');
				};
				img.src = image;
			}else{
				self.emptyImage();
			}
		};
		self.emptyImage = function(){
			$('#emptyImage').show();
			Loading.hide('ImageLoading');
		};
		self.openMarkValue = function(gridId, index){
			self.updatePopup.data("action", "create");
			self.updatePopup.data("gridId", gridId);
			var data = self.getGridData(index);
			self.currentParty = {
				partyId : data.partyId,
				productPromoId : data.productPromoId,
				productPromoRuleId : data.productPromoRuleId,
			};
			if(data.fromDate){
				self.currentParty.fromDate = data.fromDate.getTime();
			}
			if(self.updatePopup && self.updatePopup.length){
				self.updatePopup.jqxWindow('open');
			}
		};
		$(document).ready(function(){
			self.initImageWindow();
			self.initUpdateWindow();
		});
		return self;
	})();
</script>