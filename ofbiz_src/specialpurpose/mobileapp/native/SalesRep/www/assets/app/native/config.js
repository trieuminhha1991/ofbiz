var config =  {
	baseUrl : 'https://dms.olbius.com/mobileservices/control/',
	imageUrl : 'https://dms.olbius.com',
	"pageSize" : 200,
	"index" : 0,
	listItem: 100,
	minimumDuration : 30000,
	timeout: 60000,
	splashDelay: 1000,
	defaultState : 'tab.setting',
	pullRefresh : {
		default : 80,
		max : 80,
		acc : 0.6,
		delayHidden: 800,
	},
	date: {
		max : new Date(4133955599000),
		min : new Date(-2209014000000)
	},
	map:{
		frequency : 300000,
		timeout : 60000,
		maximumAge: 900000,
		enableHighAccuracy: true,
		zoom : 12,
		delay: 500
	},
	gps: {
		required: true,
		delayTime : 900000,
	},
	inventory:{
		expiredDate : true
	},
	event: {
		holdRangeDelay : 50,
		leaveTimeout : 200,
		notificationDelay: 500
	},
	storage:{
		cartItem : 'CartItem',
		others: 'Customers-other',
		'other' : 'Other',
		currentCustomer : 'currentCustomer',
		currentCustomerOut : 'currentCustomerOut',
		customers: 'Customers',
		customer: 'Customer',
		products : 'ListProduct',
		product: 'Product',
		productStores : "ListProductStore",
		promotions: "ListPromotion",
		promotion: "Promotion",
		promotionType: "PromotionType",
		customerCreatingLocation: "customerCreatingLocation",
		opponents: "Opponents",
		district : "District",
		province : "Province",
		routes: "Routes",
		route: "Route",
		routetoday : "RouteToday",
		currentRoute: "CurrentRoute",
		kpi : "CurrentKpi",
		lastCheckLocation: "lastCheckLocation",
		currentLocation: "CurrentLocation",
		isUpdateLocation : 'isUpdateLocation',
		isCreateCart: "isCreateCart",
		cartOwner : "CartOwner",
		employee: "Employee",
		login: "Login",
		language: "Language",
		locale: "Locale",
		lastLocation: "LastLocation",
		workingShift: "WorkingShift",
		leaveReasonType: "LeaveReasonType",
		startVisitingTime: "StartVisitingTime"
	},
	remote: {

	},
	countryGeoId: "VNM",
	passwordLength: 6,
	distance : 5000000000,
	maxdistance: 100000,
	camera: {
		targetWidth: 1024,
		targetHeight: 768,
		quality : 50,
		allowEdit : true,
		saveToPhotoAlbum : false,
		correctOrientation : true
	}
};
