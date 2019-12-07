if(app !== undefined)
	app.config(function($stateProvider, $urlRouterProvider) {
		  
		$stateProvider.state('login',{
			url : '/login',
			templateUrl : 'templates/login.htm',
			controller : 'LoginController'
		})
		.state('menu', {
		    url: '/menu',
		    abstract: true,
		    templateUrl: 'templates/menu.html',
		    controller: 'MenuController'
		  })
		.state('menu.profile',{
			url  : '/profile',
			views : {
				'menuContent' : {
					templateUrl : 'templates/profile.htm',	
					controller : 'ProfileController'
				}
			}
		})
		.state('menu.leave', {
			    url: '/leave',
			    views: {
			      'menuContent': {
			        templateUrl: 'templates/leave.htm',
			        controller : 'LeaveController'
			      }
			    }
			  })
		.state('menu.routelist', {
			    url: '/routelist',
			    cache : true,
			    views: {
			      'menuContent': {
			        templateUrl: 'templates/routelist.htm',
			        controller : 'RouteController'
			      }
			    }
			  })	
	  .state('menu.dashboard', {
	    url: '/dashboard',
	    cache : true,
	    views: {
	      'menuContent': {
	        templateUrl: 'templates/dashboard.htm',
	        controller : 'DashboardController'
	      }
	    }
	  })	
	  .state('menu.salesmanlist', {
	    url: '/salesmanlist',
	    params : {act : null},
	    cache : true,
	    views: {
	      'menuContent': {
	        templateUrl: 'templates/salesmanlist.htm',
	        controller : 'SalesmanController'
	      }
	    }
	  })
	.state('menu.routedetail',{
			url : '/routedetail',
			cache : false,
			params : {'route' : null,'type' : null},
			views: {
		      'menuContent': {
			        templateUrl: 'templates/item/routedetail.htm',
			        controller : 'RouteDetailController'
			      }
			    }
		}).state('menu.addroute',{
			url : '/routeroute',
			params : {act : null,route : null,person : null,callback : null,isCallback : null},
			cache : true,
			views: {
		      'menuContent': {
			        templateUrl: 'templates/item/addRoute.htm',
			        controller : 'RouteController'
			      }
			    }
		}).state('menu.updateAddress',{
			url : '/updateAddress',
			params : {route : null},
			cache : false,
			views: {
		      'menuContent': {
			        templateUrl: 'templates/item/updateAddress.htm',
			        controller : 'UpdateAddressController'
			      }
			    }
		}).state('menu.employeeDetail',{
			url : '/employeeDetail',
			params : {empl : null},
			cache : false,
			views: {
		      'menuContent': {
			        templateUrl: 'templates/item/employeeDetail.htm',
			        controller : 'EmployeeDetailController'
			      }
			    }
		}).state('menu.programExhAcc',{
			url : '/programExhAcc',
			cache : true,
			views: {
		      'menuContent': {
			        templateUrl: 'templates/programExhAcc.htm',
			        controller : 'ProgramExhAccController'
			      }
		    }
		}).state('menu.programExhAccDetail',{
			url : '/programExhAccDetail',
			params : {programId : null},
			cache : false,
			views: {
		      'menuContent': {
			        templateUrl: 'templates/item/programExhAccDetail.htm',
			        controller : 'ProgramExhAccDetailController'
			      }
		    }
		}).state('menu.commonmap',{
			url : '/commonmap',
			params : {title : null},
			cache : false,
			views: {
		      'menuContent': {
			        templateUrl: 'templates/commonMaps.htm',
			        controller : 'CommonMapController'
			      }
		    }
		}).state('menu.storeList',{
			url : '/storeList',
			cache : true,
			params : {partyId : null,name : null},
			views: {
		      'menuContent': {
			        templateUrl: 'templates/storeList.htm',
			        controller : 'StoreListController'
			      }
		    }
		}).state('menu.storeDetail',{
			url : '/storeDetail',
			cache : false,
			params : {store : null},
			views: {
		      'menuContent': {
			        templateUrl: 'templates/storeDetail.htm',
			        controller : 'StoreDetailController'
			      }
		    }
		}).state('menu.inventoryStoreList',{
			url : '/inventoryStoreList',
			cache : false,
			views: {
		      'menuContent': {
			        templateUrl: 'templates/inventoryStore.htm',
			        controller : 'InventoryStoreController'
			      }
		    }
		}).state('menu.inventoryList',{
			url : '/inventoryList',
			cache : false,
			params : {storeid : null},
			views: {
		      'menuContent': {
			        templateUrl: 'templates/inventoryList.htm',
			        controller : 'InventoryListController'
			      }
		    }
		}).state('menu.inventoryStoreDetail',{
			url : '/inventoryStoreDetail',
			cache : false,
			params : {store : null},
			views: {
		      'menuContent': {
			        templateUrl: 'templates/item/inventoryStoreDetail.htm',
			        controller : 'InventoryStoreDetailController'
			      }
		    }
		}).state('menu.leaveDetail',{
			url : '/leaveDetail',
			cache : false,
			params : {leave : null},
			views: {
		      'menuContent': {
			        templateUrl: 'templates/item/leaveDetail.htm',
			        controller : 'LeaveDetailController'
			      }
		    }
		});
		
	  $urlRouterProvider.otherwise('/login');
	});
