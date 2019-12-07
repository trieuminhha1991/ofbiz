/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('ProductSaleController', function($rootScope, $scope, $controller,$location,ProductService,SqlService) {
	$.extend(this, $controller('BaseController', {
		$scope : $scope
	}));

	$scope.$on('$viewContentLoaded',function(){
		$scope.setHeader('PromotionsPrescription', "/sale", false);
		$scope.inIt();
	});
	$scope.inIt = function(){
		$scope.getListPromotions();
	};
	$scope.checkRequest = false;
	$scope.listPromotions = new Array();
	$scope.getListPromotions = function(){
		$rootScope.showLoading();
		var currentCustomer =  $.parseJSON(localStorage.getItem('currentCustomer'));
		if(currentCustomer){
			if($rootScope.network.status){
					ProductService.getListPromotions(currentCustomer.partyIdTo).then(function(data){
						$rootScope.hideLoading();
					for(var x in data.listPromoEvent){
						$scope.listPromotions .push ({
								promoName : data.listPromoEvent[x].promoName,
								ruleName : data.listPromoEvent[x].ruleName,
								fromDate : data.listPromoEvent[x].fromDate,
								thruDate : data.listPromoEvent[x].thruDate,
							});
					}
				});
			}else{
				$rootScope.hideLoading();
				var listPromotionsOffline = {};
				var query = "SELECT * FROM promotions";
				SqlService.query(query).then(function(data){
					console.log('listPromotionsOffline' + JSON.stringify(data));
					listPromotionsOffline = data;
					if(listPromotionsOffline){
						for(var x in listPromotionsOffline){
							if($scope.listPromotions && $scope.listPromotions.length > 0){
								for(var pro in $scope.listPromotions){
									if(!($scope.listPromotions[pro].productPromoId == listPromotionsOffline[x].productPromoId)){
										$scope.listPromotions.push({
											productPromoId : listPromotionsOffline[x].productPromoId,
											promoName : listPromotionsOffline[x].promoName,
											ruleName : listPromotionsOffline[x].ruleName,
											fromDate : listPromotionsOffline[x].fromDate,
											thruDate : listPromotionsOffline[x].thruDate
										});
									};
								};
							}else {
								$scope.listPromotions.push({
								productPromoId : listPromotionsOffline[x].productPromoId,
								promoName : listPromotionsOffline[x].promoName,
								ruleName : listPromotionsOffline[x].ruleName,
								fromDate : listPromotionsOffline[x].fromDate,
								thruDate : listPromotionsOffline[x].thruDate
							});
							};
						};
					};
				},function(err){
					console.log('get list promotions offline failed');
				});
			};
		}else{
			$rootScope.hideLoading();
			BootstrapDialog.show({
								title : LanguageFactory.getLabel('Notification'),
					            message: LanguageFactory.getLabel('NotiNotSelectStore'),
					            type : BootstrapDialog.TYPE_SUCCESS,
					            closable : false,
					            spinicon : 'fa fa-spinner',
					            buttons: [{
					                icon: 'glyphicon glyphicon-ok',
					                label: LanguageFactory.getLabel('Ok'),
					                cssClass: 'btn-primary',
					                autospin: true,
					                action: function(dialogRef){
					                    dialogRef.enableButtons(false);
					                    dialogRef.setClosable(false);
					                    setTimeout(function(){
					                        dialogRef.close();
					                        $location.path('store');
					                        $scope.$apply();
					                    }, 1500);
					                }
					            }, {
							icon : 'fa fa-ban',
					                label: LanguageFactory.getLabel('Cancel'),
					                action: function(dialogRef){
					                    dialogRef.close();
					                }
					            }]
					        });

		};
	};

});
