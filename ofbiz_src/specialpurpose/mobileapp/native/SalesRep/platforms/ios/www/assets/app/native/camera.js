app.directive('camera', ['$rootScope', '$compile', '$timeout', 'UploadService', 'LoadingFactory', '$cordovaImagePicker',
function($rootScope, $compile, $timeout, UploadService, loading, picker) {
	// app.directive('camera', ['$rootScope', '$compile', '$timeout', 'UploadService', 'LoadingFactory',
	// function($rootScope, $compile, $timeout, UploadService, loading) {
	function init(scope, element, attrs) {
		scope.id = makeid(10);
		scope.image = null;
		scope.self = scope;
		if (!scope.path) {
			scope.path = "";
		}
		scope.imageUrl = $rootScope.imageUrl;
		if (!scope.parent) {
			scope.parent = scope.$parent;
		}
		scope.loadingid = "CameraLoading-" + scope.id;

		scope.image = "";
		scope.options = scope.options ? scope.options : {
			editable : true,
			preview : true,
			createdContent : false,
		};
		scope.$watch("url", function() {
			if (scope.url) {
				scope.rotate(scope.url);
			}
		});
		scope.$watch('image', function() {
			if (scope.image) {
				scope.uploadPhoto(scope.image);
			}
		})
		scope.parent.getPath = function() {
			return scope.path;
		};
		scope.showLoading = loading.showLoading;
		scope.hideLoading = loading.hideLoading;
		scope.chooseImage = function() {
			if (!scope.options.editable)
				return;
			if ( typeof (picker) != 'undefined') {
				var options = {
					maximumImagesCount : 1,
					quality: 70
				};
				picker.getPictures(options).then(function(results) {
					if(results.length){
						scope.image = results[0];
					}
				});

			} else {
				var id = 'InputSelect' + scope.id;
				$('#' + id).click();
			}
		};
		scope.rotate = function(url) {
			var canvas = document.createElement('canvas');
			var ctx = canvas.getContext("2d");
			var image = new Image();
			image.onload = function() {
				canvas.width = image.width;
				canvas.height = image.height;
				ctx.drawImage(image, 0, 0);
				ctx.rotate(90 * Math.PI / 180);
				ctx.drawImage(image, 0, 0);
				ctx.fill();
				scope.image = canvas.toDataURL("image/jpeg");
				scope.$apply();
			};
			image.src = url;
		};
		scope.processImage = function(url) {
			url = url.replace("data:image/jpeg;base64,", "");
			url = url.replace("data:image/png;base64,", "");
			return url;
		};
		scope.uploadPhoto = function(imageData) {
			var name = "image-mobile-" + makeid(10) + '.jpg';
			var url = baseUrl + '/uploadImage';
			if (scope.options.createdContent) {
				url = baseUrl + '/uploadImageWithContent';
			}
			if ( typeof (FileUploadOptions) == "function") {
				var success = function(r) {
					var res = JSON.parse(r.response);
					if(res.contentUrl){
						scope.path = res.contentUrl;
					}else if(res.path){
						scope.path = res.path;
					}
				};
				var error = function(error) {
					console.log("ERROR" + JSON.stringify(error));
				};
				imageData = scope.processImage(imageData);
				var optionsUpload = new FileUploadOptions();
				optionsUpload.fileKey = "uploadedFile";
				optionsUpload.fileName = name;
				optionsUpload.mimeType = "image/jpeg";
				var params = {
					_uploadedFile_fileName : name,
					_uploadedFile_contentType : "image/jpeg",
					folder : "mobileimage"
				};
				optionsUpload.params = params;
				var ft = new FileTransfer();
				ft.upload(imageData, url, success, error, optionsUpload, true);
			} else if ( typeof (picker) == 'undefined') {
				var form_data = new FormData();
				var file = $('#InputSelect' + scope.id)[0].files[0];
				form_data.append("uploadedFile", file);
				if (scope.options.createdContent) {
					UploadService.uploadImageWithContent(form_data).then(function(res) {
						scope.path = res.contentUrl;
					});
				} else {
					UploadService.uploadImage(form_data).then(function(res) {
						scope.path = res.path;
					});
				}
			}
		};
	}

	return {
		restrict : 'ACE',
		templateUrl : 'templates/item/camera.htm',
		scope : {
			path : "=",
			self : "=",
			image : "=",
			options : "=",
			loading : "=",
			parent : "=",
		},
		// transclude : true,
		link : init
	};
}]);
