app.controller('OpponentController', function($rootScope, $scope, $controller, $timeout, $stateParams, $ionicModal, CustomerOpinion, StoreFactory) {
	var self = $scope;
	var root = $rootScope;
	self.self = self;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.opponents = [];
	self.currentopponent = self.getLocalItem(config.storage.currentopponent);
	self.initLoad = function() {
		$ionicModal.fromTemplateUrl('templates/item/createopponent.htm', {
			scope : self,
			animation : 'slide-in-right'
		}).then(function(modal) {
			self.modal = modal;
		});
		self.opponent = {};
		self.newopponent = {};
		self.getDataType('listOpponent', self.initOpponents, CustomerOpinion.getListOpponent, null, config.storage.opponents, self.showLoading, self.hideLoading);
	};
	self.$watch('opponents', function() {
		if (!_.isEmpty(self.opponents)) {
			self.opponent.partyId = self.opponents[0].partyId;
		}
	});
	self.initOpponents = function(data) {
		self.opponents = data;
	};
	self.checkConditionBefore = function() {
		if (self.isValid())
			self.buildConfirm(self.getLabel('Notification'), self.getLabel('ConfirmSendOpponentInfo'), self.submitInformation);
	};
	self.isValid = function() {
		return self.opponentform.validate();
	};
	self.submitInformation = function() {
		if (self.isValid()) {
			CustomerOpinion.submitInfoOpponent(self.opponent, self.showLoading, self.hideLoading).then(function(data) {
				if (data.opponentEventId) {
					$timeout(function() {
						self.showNotification(self.getLabel('NotificationSuccess'));
					}, 500);
				} else {
					$timeout(function() {
						self.showError(self.getLabel('NotificationError'));
					}, 500);
				}
				self.resetForm();
				self.opponentform.reset();
				self.image = "";
				self.opponent.image = "";
			}, self.hideLoading);
		}
	};
	self.resetForm = function() {
		self.opponent = {
			partyId : "",
			description : "",
			comment : "",
			image : ""
		};
		if (self.camera) {
			self.camera.image = "";
			self.marking.url = "";
		}
	};
	self.openPopupCreate = function() {
		self.modal.show();
	};
	self.saveNewOpponent = function(){
		if(self.addopponent.validate()){
			var partyCode = removeUnicodeVietnamese(self.newopponent.groupName);
			self.newopponent.partyCode = partyCode ? partyCode.toUpperCase() : new Date().getTime();
			CustomerOpinion.createnewopponent(self.newopponent, self.showLoading, self.hideLoading).then(function(data) {
				if((!data['_ERROR_MESSAGE_'] && !data['_ERROR_MESSAGE_LIST_'])){
					self.newopponent = {};
					self.cameranew.image = "";
					self.addopponent.reset();
					self.removeLocalItem(config.storage.opponents);
					self.modal.hide();
					self.getDataType('listOpponent', self.initOpponents, CustomerOpinion.getListOpponent, null, config.storage.opponents, self.showLoading, self.hideLoading);
				}
			}, self.hideLoading);
		}
	};
	self.$on('$ionicView.enter', self.initLoad);
});
