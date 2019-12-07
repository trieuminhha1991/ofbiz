app.controller('CustomerDetailController', function($scope, $ionicModal, $timeout, AppFactory, StorageFactory, LoadingFactory, CustomerService) {
	var self = $scope;
	self.selectedId = "";
	$ionicModal.fromTemplateUrl('templates/item/customerdetail.htm', {
		scope : self,
		animation : 'slide-in-up'
	}).then(function(modal) {
		self.customerdetail = modal;
	});
	self.openDetailPopup = function(detail){
		if(self.customerdetail){
			self.customerdetail.show();
		}
	};
	self.getPartyDetail = function(partyId, force){
		self.selectedId = partyId;
		var key = config.storage.customer + "-" + partyId;
		self.openDetailPopup();
		AppFactory.getDataType('agentInfo', function(data){
			self.selectedCustomer = data;
		}, CustomerService.getCustomerInfo, {partyId : partyId, detail: 'Y'}, key, LoadingFactory.showLoading, LoadingFactory.hideLoading, force);
	};
	self.editCustomer = function(partyId){
		self.customerdetail.hide();
		$timeout(function(){
			AppFactory.changeState('createCustomer', {partyId : partyId});
		}, 500);
	};
});
