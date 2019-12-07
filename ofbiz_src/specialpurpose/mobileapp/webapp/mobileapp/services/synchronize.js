/*list of service synchronize*/
/*
 *
 *
 * CREATE TABLE
 checklist (
 _id INTEGER PRIMARY KEY AUTOINCREMENT,
 checklist_title TEXT,
 description TEXT,
 created_on INTEGER,
 modified_on INTEGER
 );

 CREATE TABLE
 item (
 _id INTEGER PRIMARY KEY AUTOINCREMENT,
 checklist_id INTEGER,
 item_text TEXT,
 item_hint TEXT,
 item_order INTEGER,
 created_on INTEGER,
 modified_on INTEGER,
 FOREIGN KEY(checklist_id) REFERENCES checklist(_id)
 );
 */

olbius.factory('SynchronizeService', function($http, $location) {
	var sync = {};
	/* get all promotions today from server */
	sync.getPromotions = function() {
		return $http({
			url : baseUrl + 'getPromotions',
			dataType : "json",
			method : "POST",
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			}
		}).then(function(res) {
			log(res.data);
			return res.data;
		});
	};
	return sync;
});
//transform object data to request
function transformRequest(obj, callback) {
	if (callback) {
		callback();
	}
	var str = [];
	for (var p in obj)
	str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
	return str.join("&");
};
function checkLogin(obj, $location) {
	if (obj.login === "FALSE") {
		localStorage.setItem("login", "false");
		$location.path('login');
	} else {
		localStorage.setItem("login", "true");
	}
};
olbius.service("aSynchronizeService",function($http,$location,$q,SqlService,$rootScope){
	var rs = {};
	var defer = $q.defer();
		rs.loadSync = function(datas){
			var dataRequest = [];
			var products = [""];
			var i = 0;
			var info = {
				totalOrder : 0,
				totalSuccess : 0,
				error : false
			};
			angular.forEach(datas,function(data){
				info.totalOrder++;
				for(var product in data.products){
					if (product != 0) {
						products[i] += "||";
					}
					products[i] += data.products[product].productId + "%%" + data.products[product].quantity;
				};
				if(data.customerId){
					dataRequest.push(
						$http({
						url : baseUrl + "submitOrder",
						method : "POST",
						transformRequest : function(obj) {
							return transformRequest(obj);
						},
						async : true,
						data : {
							customerId : data.customerId,
							products : products[i],
							latitude : data.latitude,
							longitude : data.longitude
						},
						dataType : "json",
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
						// timeout : setTimeout(function(){
							// $rootScope.hideLoading();
							// BootstrapDialog.show({
								// title : LanguageFactory.getLabel('Notification'),
					            // message: LanguageFactory.getLabel('ReanswerSynchronizeNetwork'),
					            // type : BootstrapDialog.TYPE_WARNING,
					            // closable : false,
					            // spinicon : 'fa fa-spinner',
					            // buttons: [{
					                // icon: 'glyphicon glyphicon-ok',
					                // label: LanguageFactory.getLabel('Ok'),
					                // cssClass: 'btn-primary',
					                // autospin: true,
					                // action: function(dialogRef){
					                    // dialogRef.enableButtons(false);
					                    // dialogRef.setClosable(false);
				                        // dialogRef.close();
					                // }
					            // }]
					        // });
							// console.log('Server not response');
						// }, 10000)
					}).then(function(res){
						console.log('loading request to server ' + JSON.stringify(res));
						info.totalSuccess++;
						var where = "oridInfoId ='"+ data.oridInfoId +"'";
						SqlService.deleteRow('orderinfo',where).then(function(){
							console.log('delete order sync success');
							SqlService.deleteRow('orderlist',where).then(function(){
								console.log('delete orderlist success');
							},function(){
								console.log('delete orderlist error');
							});
						},function(){
							console.log('delete order sync error');
						});
					})
					);
				}
				i++;
			});
			return $q.all(dataRequest).then(function(results){
				defer.resolve(console.log('results load async Ajax : '+JSON.stringify(results)));
				i = 0;
				return info;
			},function(errs){
				info.error = true;
				return info;
			});
		};
		//sync customer
		rs.loadSyncCustomer = function(dataCust){
			var infoSucess = {
				info : [{}],
				total : 0,
				totalSucess : 0
			};
			var total = 0;
			var numberError = 0;
			var promiseCust = [];
			angular.forEach(dataCust,function(cust){
				infoSucess.total++;
				total++;
				promiseCust.push(
							$http({
								url : baseUrl + "createCustomerSalesMan",
								method : "POST",
								transformRequest : function(obj) {
									return transformRequest(obj);
								},
								async : true,
								data : cust,
								dataType : "json",
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								}
							}).then(function(res) {
								infoSucess.info.push(res.config.data);
								infoSucess.totalSucess++;
								var id = res.config.data.customerId;
								var where = 'customerId =' + id;
								 SqlService.deleteRow('customer',where);
								checkLogin(res.data, $location);
							})
						);
				});
				return $q.all(promiseCust).then(function(rs){
					defer.resolve(JSON.stringify(rs));
					return infoSucess;
				},function(err){
					console.log('errrr' + JSON.stringify(err.config.data));
					// for(var key in err.config.data){
						// numberError++;
					// }
					// var tmp = {
						// success : total - numberError,
						// error : numberError,
						// infoError : err.config.data
					// };
					// numberError = 0 ;
					// total = 0 ;
					return  err.config.data;
				});
		};
		//service check inventory
		rs.loadSyncInventory = function(data){
			var promiseInventory = [];
			var info = {
				totalInventory : 0,
				success : 0,
				error : 0,
				infoInventory : {}
			};
			console.log('ivv into' + JSON.stringify(data));
			angular.forEach(data,function(dt){
				info.totalInventory++;
				promiseInventory.push(
					$http({
						url : baseUrl + 'updateInventoryCus',
						transformRequest : function(obj) {
							return transformRequest(obj);
						},
						dataType : "json",
						method : "POST",
						data : {
							inventory : JSON.stringify(dt.inventory),
							party_id : dt.party_id
						},
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded'
						}
					}).then(function(res) {
						info.success++;
						console.log('ivv success' + JSON.stringify(res.config.data));
						var where = 'ivid = '+ dt.ivid;
						SqlService.deleteRow('inventories',where);
						checkLogin(res.data, $location);
						return res.data;
					})
				);
			});
			return $q.all(promiseInventory).then(function(data){
					console.log('ivv all' + JSON.stringify(info));
					defer.resolve(data);
					return info;
				},function(err){
					defer.reject(err);
					info.error = info.totalInventory - info.totalInventorySuccess;
					return info;
				});

			};
			rs.loadSynExhibited = function(data){
					var promiseAcc = [];
					var info = {
						totalAcc : 0,
						success : 0,
						error : 0,
						infoAcc : {}
					};
					console.log('Acc into' + JSON.stringify(data));
					angular.forEach(data,function(dt){
						info.totalAcc++;
						promiseAcc.push(
							$http({
								url: baseUrl + 'exhibitedRegister',
								method : "POST",
								dataType : "json",
								transformRequest : function(obj){
									return transformRequest(obj);
								},
								data : dt,
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								}
							}).then(function(res){
								info.success++;
								var where = 'productPromoRegisterId = '+res.config.data.productPromoRegisterId;
								checkLogin(res.data,$location);
								return res.data;
							})
						);
					});
					return $q.all(promiseAcc).then(function(data){
							console.log('promiseAcc all' + JSON.stringify(info));
							defer.resolve(data);
							return info;
						},function(err){
							defer.reject(err);
							info.error = info.totalAcc - info.success;
							return info;
						});
				};
				rs.loadSynAccumulate = function(data){
					var promiseAcc = [];
					var info = {
						totalAcc : 0,
						success : 0,
						error : 0,
						infoAcc : {}
					};
					console.log('Acc into' + JSON.stringify(data));
					angular.forEach(data,function(dt){
						info.totalAcc++;
						promiseAcc.push(
							$http({
								url: baseUrl + 'accumulateRegister',
								method : "POST",
								dataType : "json",
								transformRequest : function(obj){
									return transformRequest(obj);
								},
								data : dt,
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								}
							}).then(function(res){
								info.success++;
								var where = 'accumulateRegisterId = '+res.config.data.accumulateRegisterId;
								checkLogin(res.data,$location);
								return res.data;
							})
						);
					});
					return $q.all(promiseAcc).then(function(data){
							console.log('promiseAcc all' + JSON.stringify(info));
							defer.resolve(data);
							return info;
						},function(err){
							defer.reject(err);
							info.error = info.totalAcc - info.success;
							return info;
						});
				};
		rs.loadSyncCommon = function(data,subUrl){
				var promise = [];
				var info = {
					total  : 0,
					totalSuccess : 0
				};
				angular.forEach(data,function(d){
					console.log('dd' + JSON.stringify(d));
					info.total++;
					promise.push(
						$http({
							url : baseUrl + subUrl,
							method : "POST",
							dataType : "json",
							transformRequest : function(obj) {
								return transformRequest(obj);
							},
							data : d,
							headers : {
								'Content-Type' : 'application/x-www-form-urlencoded'
							}
						}).then(function(success){
							info.totalSuccess++;
						},function(err){
						})
					);
				});
				return $q.all(promise).then(function(success){
					return info;
				},function(error){
					console.log('synchronize error');
					return info;//
				});
			};

			rs.loadSyncEmployeeLeave = function(datas){
				var promise = [];
				var info = {
						total : 0,
						totalSuccess : 0
					};
				angular.forEach(datas,function(data){
					info.total ++;
					promise.push(
						$http({
								url : baseUrl + 'createEmplLeaveSalesman',
								transformRequest : function(obj) {
									return transformRequest(obj);
								},
								dataType : "json",
								method : "POST",
								data : {
									leaveTypeId : data.type,
									fromDate : data.fromDate,
									thruDate : data.thruDate,
									emplLeaveReasonTypeId : data.reason
								},
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								}
							}).then(function(res) {
								info.totalSuccess++;
								checkLogin(res.data, $location);
								var where = "leaveId=" + data.leaveId;
								SqlService.deleteRow("forLeave",where);
							},function(err){
							})
						);
					});
				return $q.all(promise).then(function(sc){
					return info;
				},function(err){
					return info;
				});
			};
			rs.loadSyncExhibitedMarked = function(datas){
				var promise = [];
				var info = {
						total : 0,
						success : 0
					};
				angular.forEach(datas,function(data){
					info.total ++;
					promise.push(
						$http({
								url : baseUrl + 'sendMark',
								transformRequest : function(obj) {
									return transformRequest(obj);
								},
								dataType : "json",
								method : "POST",
								data : data,
								headers : {
									'Content-Type' : 'application/x-www-form-urlencoded'
								}
							}).then(function(res) {
								info.success++;
								var where = "id ='" + data.id + "'";
								SqlService.deleteRow('exhibitedMark',where).then(function(){
									log('delete success');
								},function(){

								});
								checkLogin(res.data, $location);
							},function(err){
							})
						);
					});
				return $q.all(promise).then(function(sc){
					return info;
				},function(err){
					return info;
				});
			};
	return rs;
});