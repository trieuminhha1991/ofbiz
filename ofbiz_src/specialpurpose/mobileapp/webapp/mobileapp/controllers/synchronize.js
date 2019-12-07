/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('SynchronizeController', function($rootScope, $scope, $controller,$timeout,$location,SynchronizeService, ProductService,LoadRoadService, SqlService,OrderService,DashboardServices ,CategoryService ,InventoryService ,aSynchronizeService ,CustomerService,CustomerOpinion ,EmployeeService, LanguageFactory.) {
	$.extend(this, $controller('BaseController', {
		$scope : $scope
	}));
	$scope.hide = false;
	$scope.total = 0;
	$scope.current = 0;
	$scope.notification = "";
	$scope.support = {
		condition: ["PROMO_GWP", "PROMO_PROD_DISC"],
		action: ["PPIP_PRODUCT_QUANT", "PPIP_PRODUCT_TOTAL"]
	};
	$scope.infoSynchronize = {
		total : 0,
		totalSuccess : 0,
		totalError : 0,
		percent : 0,
		totalOrder : 0,
		totalOrderSuccess : 0,
		totalOrderError : 0,
		percentOrder : 0,
		totalInventory : 0,
		totalInventorySuccess : 0,
		totalInventoryError : 0,
		percentInventory : 0,
		totalAccumulate : 0,
		totalAccumulateSuccess : 0,
		totalAccumulateError : 0,
		percentAccumulate  : 0,
		totalEx : 0,
		totalExSuccess : 0,
		totalExError : 0,
		percentEx : 0,
		totalLeave : 0,
		totalLeaveSuccess : 0,
		totalLeaveError : 0,
		percentLeave : 0,
		totalEM : 0,
		totalEMSuccess : 0,
		totalEMError : 0,
		percentEM : 0,
		infoDetail : []
	};
	$scope.$on("$viewContentLoaded", function(){
		$scope.setHeader('Synchronize', "/main", false);
		$scope.getInfoFromSQLite();
		if (!localStorage.customers) {
			// $location.path("store");
		} else {
			$scope.currentStore = JSON.parse(localStorage.getItem("currentCustomer"));
		}
	});

	$scope.initData = function(flag) {
		SynchronizeService.getPromotions().then(function(res) {
			var data = res.promotions;
			// var id = getLastInsertId("lastPromotionId");
			if (data && data.length) {
				var fields = ["productPromoId", "inputParamEnumId", "productPromoActionEnumId", "amount", "quantity", "productId", "fromDate", "thruDate", "productPromoRuleId", "orderAdjustmentTypeId", "operatorEnumId", "productPromoCondSeqId", "productPromoActionSeqId", "productPromoApplEnumId", "condValue", "partyId", "otherValue","ruleName"];
				var input = Array();
				for (var x in data) {
					var obj = data[x];
					var tmp = [];
					if(obj.fromDate && obj.thruDate){
						tmp = [ obj.productPromoId, obj.inputParamEnumId, obj.productPromoActionEnumId, obj.amount, obj.quantity, obj.productId, formatDateDMY(obj.fromDate.time), formatDateDMY(obj.thruDate.time), obj.productPromoRuleId, obj.orderAdjustmentTypeId, obj.operatorEnumId, obj.productPromoCondSeqId, obj.productPromoActionSeqId, obj.productPromoApplEnumId, obj.condValue, obj.partyId, obj.otherValue,obj.ruleName];
					}else if(!obj.fromDate && obj.thruDate){
						tmp = [ obj.productPromoId, obj.inputParamEnumId, obj.productPromoActionEnumId, obj.amount, obj.quantity, obj.productId, null, formatDateDMY(obj.thruDate.time), obj.productPromoRuleId, obj.orderAdjustmentTypeId, obj.operatorEnumId, obj.productPromoCondSeqId, obj.productPromoActionSeqId, obj.productPromoApplEnumId, obj.condValue, obj.partyId, obj.otherValue,obj.ruleName];
					}else if(!obj.thruDate && obj.fromDate){
						tmp = [ obj.productPromoId, obj.inputParamEnumId, obj.productPromoActionEnumId, obj.amount, obj.quantity, obj.productId, formatDateDMY(obj.fromDate.time), null, obj.productPromoRuleId, obj.orderAdjustmentTypeId, obj.operatorEnumId, obj.productPromoCondSeqId, obj.productPromoActionSeqId, obj.productPromoApplEnumId, obj.condValue, obj.partyId, obj.otherValue,obj.ruleName];
					}else{
						tmp = [ obj.productPromoId, obj.inputParamEnumId, obj.productPromoActionEnumId, obj.amount, obj.quantity, obj.productId,null, null, obj.productPromoRuleId, obj.orderAdjustmentTypeId, obj.operatorEnumId, obj.productPromoCondSeqId, obj.productPromoActionSeqId, obj.productPromoApplEnumId, obj.condValue, obj.partyId, obj.otherValue,obj.ruleName];
						}
					input.push(tmp);
					// id++;
				}
				SqlService.deleteRow("promotions").then(function() {
					SqlService.insert("promotions", fields, input).then(function(res) {
						/*SqlService.select("promotions").then(function(res){
							console.log(" promotions" + JSON.stringify(res));
						});*/
						// localStorage.setItem("lastPromotionId", id);
					}, function() {
						log("error" + JSON.stringify(res));
					});
				});
			}
		}, function(err) {
			console.log("get promotions" + JSON.stringify(err));
		});
		ProductService.getAll().then(function(res) {
			var data = res.listProduct;
			console.log('prprpr' + JSON.stringify(data));
			if (data && data.length) {
				localStorage.setItem('listProducts',JSON.stringify(data));
				var fields = ["productId", "productName", "unitPrice","productCategoryId"];
				var input = Array();
				for (var x in data) {
					var obj = data[x];
					var tmp = [obj.productId, obj.productName, obj.unitPrice,obj.productCategoryId];
					input.push(tmp);
				}
				SqlService.deleteRow("products").then(function() {
					SqlService.insert("products", fields, input).then(function(res) {
						console.log('insert products success' + JSON.stringify(res));
					}, function() {
						log("error" + JSON.stringify(res));
					});
				});
			}
		});
		CustomerService.getStoreByRoad(0,100,'Y',null).then(function(data){
			$scope.customers= data.customers;
			var key = 'listStore';
			var input = [];
			var value = [];
			var fields = ["productId","productName","qtyInInventory","orderId","orderDate","customerId","lastCheck","status"];
				SqlService.deleteAll('inventories').then(function(){
					for(var key in data.listInventory){
						for(var key2 in data.listInventory[key].inventoryCusInfo){
							value = [data.listInventory[key].inventoryCusInfo[key2].productId,data.listInventory[key].inventoryCusInfo[key2].productName,data.listInventory[key].inventoryCusInfo[key2].qtyInInventory,data.listInventory[key].inventoryCusInfo[key2].orderId,data.listInventory[key].inventoryCusInfo[key2].orderDate,data.listInventory[key].inventoryCusInfo[key2].partyId," ","init"];
							input.push(value);
							SqlService.insert('inventories',fields,input).then(function(data){
							},function(err){
								console.log(JSON.stringify('err' + err));
							});
							input = new Array();
							value = new Array();
						}
					}
				},function(){
				});
			if(!localStorage.getItem(key)){
				localStorage.setItem(key,JSON.stringify(data.customers));
			}
		});
		//init listRoad
		LoadRoadService.getListRouteAndSalesMan($rootScope.showLoading).then(function(data){
					$rootScope.hideLoading();
					localStorage.setItem('listRoad',JSON.stringify(data.data.result.listRoute));
			},function(err){
			});
		CategoryService.getAll().then(function(res) {
				if(res.completedTreeCat){
					localStorage.setItem('category',JSON.stringify(res.completedTreeCat));
				}
            });

		//get ListExhibited for Store
		ProductService.getExhibitedDetail().then(function(data){
			var fields = ["productPromoId","promoName","fromDate","thruDate","quantity","amount","productId","operatorEnumId","condValue","condExhibited","productPromoRuleId","ruleName","productName"];
			var obj  = {};
			if(data.listExhibited){
				obj  = data.listExhibited;
			};
			var input = [];
			for(var key in obj){
				var value = [obj[key].productPromoId,obj[key].promoName,formatDateDMY(obj[key].fromDate.time),formatDateDMY(obj[key].thruDate.time),obj[key].quantity,obj[key].amount,obj[key].productId,obj[key].operatorEnumId,obj[key].condValue,obj[key].condExhibited,obj[key].productPromoRuleId,obj[key].ruleName,obj[key].productName];
				input.push(value);
			};
			SqlService.deleteAll('exhibite').then(function(){
				SqlService.insert('exhibite',fields,input).then(function(data){
					console.log('INSERT EXHIBITE SUCCESS' + JSON.stringify(data));
				},function(err){
					console.log('INSERT EXHIBITE ERROR' + JSON.stringify(err));
				});
			},function(){
			});
		},function(err){
					console.log('get list exhibited failed' + JSON.stringify(err));
		});
		//get list accumulate for Store
		ProductService.getAccumulateStore().then(function(data){
				var fields = ["customerId","productPromoId","promoName","productPromoRuleId","promoSalesTargets","quantity","productName","DayInterval","monthInterval","fromDate","thruDate"];
				var input = [];
				var obj = {};
				if(data.listAccumulate){
					obj = data.listAccumulate;
				}
				for(var key in obj){
					var value = [obj[key].partyId,obj[key].productPromoId,obj[key].promoName,obj[key].productPromoRuleId,obj[key].promoSalesTargets,obj[key].quantity,obj[key].productName,obj[key].DayInterval,obj[key].monthInterval,obj[key].fromDate,obj[key].thruDate];
					input.push(value);
				}
				SqlService.deleteAll('accumulate').then(function(){
					SqlService.insert('accumulate',fields,input).then(function(data){
						console.log('INSERT ACCUMULATE SUCCESS' + JSON.stringify(data));
					},function(err){
						console.log('INSERT ACCUMULATE ERROR' + JSON.stringify(err));
					});
				},function(){
				});
			});
		CustomerOpinion.getListOpponent().then(function(data) {
			console.log('opponent' + JSON.stringify(data.data.listOpponent));
			if(data.data.listOpponent){
				localStorage.setItem('listOpponent',JSON.stringify(data.data.listOpponent));
			}
		},function(err){
			console.log('get list opponent error');
		});

		EmployeeService.getType().then(function(res) {
			var types = {};
               types = res.leaveType;
               if(types && !localStorage.getItem('types')){
			localStorage.setItem('types',JSON.stringify(types));
               }
            });

		EmployeeService.getReason().then(function(res) {
	                var reasons = res.leaveTypeReason;
	                if(reasons && !localStorage.getItem('reasons')){
				localStorage.setItem('reasons',JSON.stringify(reasons));
	                }
			 });

		ProductService.getExhibitedForMark('getData').then(function(data) {
			var input = [];
			var fields = ["promoName","productPromoId","productPromoRuleId","createdDate","createdBy","productPromoRegisterId","groupName","partyId"];
			if (data.result) {
				for (var rs in data.result) {
					var value = [data.result[rs].promoName,data.result[rs].productPromoId,data.result[rs].productPromoRuleId,formatDateYMD(data.result[rs].createdDate.time),data.result[rs].createdBy,data.result[rs].productPromoRegisterId,data.result[rs].groupName,data.result[rs].partyId];
					input.push(value);
				}
				if(input && input.length > 0 ){
					SqlService.deleteAll('exhibitedForMark').then(function(res){
						log('delete all exhibited for mark success');
						SqlService.insert('exhibitedForMark',fields,input).then(function(){
							console.log('insert exhibitedForMark success');
						},function(){
							console.log('insert exhibitedForMark error');
						});
					},function(err){

					});
				}
			}
		});
		//get list order
		OrderService.getList($rootScope.showLoading,$rootScope.configPage.index,$rootScope.configPage.pageSize,'thisweek').then(function(data) {
			$rootScope.hideLoading();
			var input = [];
			var fields = ["orderId","orderDate","grandTotal","groupName","statusId"];
			for (var x in data.orderHeaderList) {
				var value = [];
				value = [data.orderHeaderList[x].orderId,data.orderHeaderList[x].orderDate,data.orderHeaderList[x].grandTotal,data.orderHeaderList[x].groupName,data.orderHeaderList[x].statusId];
				input.push(value);
			}
			if(input && input.length > 0 ){
				SqlService.deleteAll('orderView').then(function(res){
					SqlService.insert('orderView',fields,input).then(function(res){
						console.log('insert orderView Success');
					},function(){
						console.log('insert orderView Error');
					});
				},function(){

				});
			}
		}, function() {
			setTimeout(function(){
				$rootScope.hideLoading();
				$rootScope.openDialog(LanguageFactory.getLabel('ReanswerSynchronizeNetwork'));
			},10000);
			$scope.checkSyncComplete.Synchronize = false;
		});


		//Dash board Data
		DashboardServices.orderAmountPerMonth().then(function(res) {
			console.log('11productSalesSum' + JSON.stringify(res));
			if(res) {
				localStorage.setItem('orderAmountPerMonth',JSON.stringify(res));
			}
		});
		DashboardServices.getPolicySalesMan().then(function(res) {
			if(res){
				localStorage.setItem('PolicySalesMan',JSON.stringify(res));
			}
		});
		DashboardServices.customerSumAmount().then(function(res) {
			if(res){
				localStorage.setItem('customerSumAmount',JSON.stringify(res));
			}
		});
		DashboardServices.productSalesSum('thisMonth').then(function(res) {
			if(res){
				localStorage.setItem('productSalesSum',JSON.stringify(res));
			}
		});
		// console.log('11productSalesSum' + $.parseJSON(localStorage.getItem('productSalesSum'))
		// + 'customerSumAmount' +  $.parseJSON(localStorage.getItem('customerSumAmount'))
		// + 'PolicySalesMan' + $.parseJSON(localStorage.getItem('PolicySalesMan'))
		// + 'orderAmountPerMonth' +$.parseJSON(localStorage.getItem('orderAmountPerMonth'))
		// );
	};
	$scope.getInfoFromSQLite = function(){
		var query = "SELECT count(customerId) from customer";
		SqlService.query(query).then(function(data){
			if(data[0]['count(customerId)']){
				$scope.infoSynchronize.total = data[0]['count(customerId)'];
			}
		},function(){

		});
		var query =  "SELECT count(oridInfoId) FROM orderinfo";
		SqlService.query(query).then(function(data){
			if(data[0]['count(oridInfoId)']){
				$scope.infoSynchronize.totalOrder = data[0]['count(oridInfoId)'];
			}
		},function(){

		});
		var query = "SELECT count(ivid) FROM inventories WHERE status='modified'";
		SqlService.query(query).then(function(data){
			if(data[0]['count(ivid)']){
				$scope.infoSynchronize.totalInventory = data[0]['count(ivid)'];
			}
		},function(){

		});
		var query = "SELECT count(accumulateRegisterId) from accumulateRegister";
		SqlService.query(query).then(function(data){
			if(data[0]['count(accumulateRegisterId)']){
				$scope.infoSynchronize.totalAccumulate = data[0]['count(accumulateRegisterId)'];
			}
		},function(){
		});
		var query = "SELECT count(productPromoRegisterId) from exhibitedRegister";
		SqlService.query(query).then(function(data){
			if(data[0]['count(productPromoRegisterId)']){
				$scope.infoSynchronize.totalEx = data[0]['count(productPromoRegisterId)'];
			}
		},function(){
		});
		var query = "SELECT count(leaveId) from forLeave";
		SqlService.query(query).then(function(data){
			if(data[0]['count(leaveId)']){
				$scope.infoSynchronize.totalLeave = data[0]['count(leaveId)'];
			}
		},function(){
		});
		var query = "SELECT count(id) from exhibitedMark";
		SqlService.query(query).then(function(data){
			if(data[0]['count(id)']){
				$scope.infoSynchronize.totalEM = data[0]['count(id)'];
			}
		},function(){
		});
	};
	$scope.SynchronizeAccumulate = function(){
		var query = "SELECT * FROM accumulateRegister";
		SqlService.query(query).then(function(data){
			aSynchronizeService.loadSynAccumulate(data).then(function(data){
				if(data){
					$scope.infoSynchronize.totalAccumulateSuccess = data.success;
					$scope.infoSynchronize.totalAccumulateError = $scope.infoSynchronize.totalAccumulate - $scope.infoSynchronize.totalAccumulateSuccess;
					$scope.infoSynchronize.percentAccumulate = $scope.infoSynchronize.totalAccumulateSuccess/ $scope.infoSynchronize.totalAccumulate * 100;
				};
			},function(){

			});
		},function(){

		});
	};
	$scope.SynchronizeExhibited = function(){
		var query = "SELECT * FROM exhibitedRegister";
		SqlService.query(query).then(function(data){
			aSynchronizeService.loadSynExhibited(data).then(function(data){
				if(data){
					$scope.infoSynchronize.totalExSuccess = data.success;
					$scope.infoSynchronize.totalExError = $scope.infoSynchronize.totalEx - $scope.infoSynchronize.totalExSuccess;
					$scope.infoSynchronize.percentEx = $scope.infoSynchronize.totalExSuccess/ $scope.infoSynchronize.totalEx * 100;
				};
			},function(){

			});
		},function(){

		});
	};
	$scope.SynchronizeExhibitedMarked = function(){
		var query = "SELECT * FROM exhibitedMark";
		SqlService.query(query).then(function(data){
			console.log('vl controller' + JSON.stringify(data));
			if(data && data.length > 0){
				aSynchronizeService.loadSyncExhibitedMarked(data).then(function(res){
					console.log('vl return ' + JSON.stringify(res));
					if(res) {
						$scope.infoSynchronize.totalEMSuccess = res.success;
						$scope.infoSynchronize.totalEMError =  $scope.infoSynchronize.totalEM - res.success;
						$scope.infoSynchronize.percentEM = $scope.infoSynchronize.totalEMSuccess  /  $scope.infoSynchronize.totalEM *100;
					}
				},function(err){

				});
			}
		},function(){


		});
	};
	$scope.SyncchornizeCustomer = function(){
		var query = "SELECT * from customer";
		SqlService.query(query).then(function(data){
			aSynchronizeService.loadSyncCustomer(data).then(function(data){
				if(data.total || data.totalSuccess){
					$scope.infoSynchronize.total = data.total;
					$scope.infoSynchronize.totalSuccess = data.totalSucess;
					$scope.infoSynchronize.totalError = $scope.infoSynchronize.total - $scope.infoSynchronize.totalSuccess;
					$scope.infoSynchronize.percent = $scope.infoSynchronize.totalSuccess / $scope.infoSynchronize.total *100;
					for(var key in data.info){
						$scope.infoSynchronize.infoDetail.push(data.info[key]);
						}
				};
			},function(data){
				console.log('SSS' + JSON.stringify(data));
			});
			console.log('get customer info error');
		});
	};
	$scope.SynchronizeInventory = function(){
		var query = "SELECT * FROM inventories WHERE status='modified' ORDER BY customerId";
		var currentCustomer = '';
		var listInventory = [];
		var inventory = [];
		var currentid = '';
		SqlService.query(query).then(function(data){
			if(data && data.length >0 ){
				for(var key in data){
					if(currentCustomer && currentCustomer != data[key].customerId){
						listInventory.push({
							party_id : currentCustomer,
							inventory : inventory,
							ivid : currentid
						});
						inventory = [];
						currentCustomer = data[key].customerId;
						currentid = data[key].ivid;
					}else {
						currentCustomer = data[key].customerId;
						currentid = data[key].ivid;
						inventory.push({productId : data[key].productId , qtyInInventory : data[key].qtyInInventory , orderId : data[key].orderId});
					}
					if(!currentCustomer){
						currentCustomer = data[key].customerId;
					}
					if(!currentid){
						currentid = data[key].ivid;
					}
				}
				listInventory.push({
					party_id : currentCustomer,
					inventory : inventory,
					ivid : currentid
				});
			//sync inventory
				aSynchronizeService.loadSyncInventory(listInventory).then(function(data){
					if(data){
						$scope.infoSynchronize.totalInventorySuccess = data.success;
						$scope.infoSynchronize.totalInventoryError = $scope.infoSynchronize.totalInventory - $scope.infoSynchronize.totalInventorySuccess;
						$scope.infoSynchronize.percentInventory = $scope.infoSynchronize.totalInventorySuccess / $scope.infoSynchronize.totalInventory *100;
					};
					},function(err){
					});
			};
			},function(err){
				console.log('error get data Inventory');
			});

	};
	$scope.checkSyncComplete = {
		getData : false,
		Synchronize : false
	};
	$scope.showDialog = function(){
		BootstrapDialog.show({
					title : LanguageFactory.getLabel('Notification'),
		            message: LanguageFactory.getLabel('ReanswerSynchronize'),
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
		                    if($rootScope.network.status){
					 $scope.checkSyncComplete.Synchronize = true;
								$scope.SynchronizeFinalize();
								dialogRef.getModalBody().html(LanguageFactory.getLabel('SynchornizeSuccess'));
		                    }else {
								$scope.checkSyncComplete.Synchronize = false;
					dialogRef.getModalBody().html(LanguageFactory.getLabel('ReanswerSynchronizeNetwork'));
		                    }
		                    setTimeout(function(){
					dialogRef.close();
		                    },1500);
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
	$scope.conFirm = function(){
		$scope.showDialog();
	};
	$scope.SynchronizeDataFinalize = function(){
		// var isFinish = false;
			if($scope.infoSynchronize.totalOrder > 0){
				$scope.SynchronizeOrder();
			};
			if($scope.infoSynchronize.total > 0){
				$scope.SyncchornizeCustomer();
			};
			if($scope.infoSynchronize.totalEx > 0){
				$scope.SynchronizeExhibited();
			}
			if($scope.infoSynchronize.totalInventory > 0){
				$scope.SynchronizeInventory();
			}
			if($scope.infoSynchronize.totalAccumulate > 0){
				$scope.SynchronizeAccumulate();
			}
			if($scope.infoSynchronize.totalLeave > 0){
				$scope.SynchronizeEmployeeLeave();
			}
			if($scope.infoSynchronize.totalEM > 0){
				$scope.SynchronizeExhibitedMarked();
			}
			// isFinish = true;
			// $location.path('/inventory/' + isFinish);
			setTimeout(function(){
				$scope.checkSyncComplete.Synchronize = false;
				$scope.infoSynchronize = {};
			},7000);
	};
	$scope.SynchronizeFinalize = function(){
		var flag = false;
		if(!($scope.infoSynchronize.totalOrder > 0) && !($scope.infoSynchronize.total > 0) && !($scope.infoSynchronize.totalEx > 0) && !($scope.infoSynchronize.totalInventory > 0) && !($scope.infoSynchronize.totalAccumulate > 0) && !($scope.infoSynchronize.totalLeave > 0) && !($scope.infoSynchronize.totalEM > 0)){
			flag =  true;
			$scope.initData(flag);
		}else {
			flag = false;
			$scope.SynchronizeDataFinalize();
			$scope.initData(flag);
		}
	};
	$scope.SynchronizeExhibitedRegister = function(){
		var query = "SELECT * FROM exhibitedRegister";
		SqlService.query(query).then(function(data){
			var infoExhibitedRegister = {};
			log('data exhibited REgis' + JSON.stringify(data));
			aSynchronizeService.loadSyncCommon(data,'exhibitedRegister').then(function(data){
				infoExhibitedRegister = data;
			},function(err){
				infoExhibitedRegister = err;
			});
		},function(){
			console.log("can't get data exhibited Register");
		});
	};
	$scope.SynchronizeOrder = function(){
		var query = "SELECT * FROM orderinfo as oi, orderlist as o WHERE o.oridInfoId = oi.oridInfoId ORDER BY oridInfoId";
		SqlService.query(query).then(function(res){
			var listOrder = [];
			var currentOrder = '';
			var currentCustomer = "";
			var pro = [];
			var products = "";
			var currentOrid = "";
			var latitude;
			var longitude;
			for(var x in res){
				if(currentOrder && currentOrder != res[x].oridInfoId){
					listOrder.push({
						products: pro,
						customerId : currentCustomer,
						orid : currentOrid ,
						oridInfoId : currentOrder,
						latitude : latitude,
						longitude : longitude
					});
					pro = [];
					pro.push({quantity: res[x].quantity, productId: res[x].productId });
					currentOrder = res[x].oridInfoId;
					currentCustomer = res[x].customerId;
					currentOrid  = res[x].orid;
					latitude : res[x].latitude;
					longitude : res[x].longitude;
				}else{
					if(res[x].productId){
						pro.push({quantity: res[x].quantity, productId: res[x].productId});
					}
				}
				if(!currentOrder){
					currentOrder = res[x].oridInfoId;
				}
				if(!currentCustomer){
					currentCustomer = res[x].customerId;
				}
				if(!currentOrid){
					currentOrid = res[x].orid;
				}
				if(!latitude){
					latitude = res[x].latitude;
				}
				if(!longitude){
					longitude = res[x].longitude;
				}
			}
			listOrder.push({
				products: pro,
				customerId : currentCustomer,
				orid : currentOrid,
				oridInfoId : currentOrder,
				latitude : latitude,
				longitude : longitude
			});


			if(listOrder[0].customerId && listOrder.length > 0 ){
				aSynchronizeService.loadSync(listOrder).then(function(data){
					if(data){
						$scope.infoSynchronize.totalOrderSuccess = data.totalSuccess;
						$scope.infoSynchronize.totalOrderError = data.totalOrder - data.totalSuccess;
						$scope.infoSynchronize.percentOrder = $scope.infoSynchronize.totalOrderSuccess / $scope.infoSynchronize.totalOrderSuccess *100;
					}
				},function(err){
				});
			}
		},function(){
		});
	};
	$scope.SynchronizeEmployeeLeave = function(){
		var query = "SELECT * FROM forLeave";
		SqlService.query(query).then(function(datas){
			if(datas){
				aSynchronizeService.loadSyncEmployeeLeave(datas).then(function(data){
					if(data) {
						$scope.infoSynchronize.totalLeaveSuccess = data.totalSuccess;
						$scope.infoSynchronize.totalLeaveError = $scope.infoSynchronize.totalLeave - data.totalSuccess;
						$scope.infoSynchronize.percentLeave = ($scope.infoSynchronize.totalLeaveSuccess / $scope.infoSynchronize.totalLeave)*100;
					}
				},function(){
				});
			}
		},function(err){

		});

	};
	$scope.deleteOldData = function(){
		for(var x in db){
			SqlService.deleteAll(db[x]);
		}
		localStorage.clear();
	};
	$scope.notSupportPromotions = function(data){
		var cond = data.inputParamEnumId;
		var action = data.productPromoActionEnumId;
		var flag1 = _.contain($scope.support.condition, cond);
		var flag2 = _.contain($scope.support.action, action);
		if(!flag1 || !flag2){
			var str = "<div class='row'><div class='col-xs-12 col-sm-12'>"
					+ data.promoName
					+ "</div></div>";
			$("#result-not-support");
		}else{
			$("#result-support");
		}
	};

});
