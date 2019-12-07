var config =  {
	"pageSize" : 200,
	"index" : 0,
	listItem: 20,
	baseUrl : 'https://delys.olbius.com/mobileservices/control/',
	imageUrl : 'https://delys.olbius.com',
	timeout: 60000,
	splashDelay: 3000,
	defaultState : 'tab.setting',
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
		delayTime : 900000,
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
		leaveReasonType: "LeaveReasonType"

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
