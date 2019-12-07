/*global todomvc, angular */
'use strict';

/**
 * useful to take a photo from native camera
 */
// olbius.directive('camera', ['$compile', '$cordovaCamera', '$rootScope',
// function($compile, $cordovaCamera, $rootScope) {
olbius.directive('camera', ['$compile','$timeout', 'UploadService',
function($compile,$timeout, UploadService) {
	function init(scope, element, attrs) {
		scope.id = makeid(10);
		scope.image = null;
		if(!scope.path){
			scope.path = "";
		}

		if(!scope.parent){
			scope.parent = scope.$parent;
		}
		scope.loadingid = "CameraLoading-" + makeid(10);
		$timeout(function(){
			scope.imageElement = $('#'+scope.id);
			scope.inputSelect = $('#inputSelect'+scope.id);
		});
		scope.options = scope.options ? scope.options : {
			preview : true,
			createdContent: false,
		};

		scope.$watch("image", function() {
			if (scope.image) {
				scope.imageElement.attr('src', scope.image);
				scope.imageElement.show();
				scope.uploadPhoto(scope.image);
			}else{
				scope.imageElement = $('#'+scope.id);
				scope.imageElement.hide();
			}
		});
		scope.parent.getPath = function(){
			return scope.path;
		};
		scope.showLoading = function(){
			if(scope[scope.loadingid]){
				scope[scope.loadingid].show();
				scope.loading = scope[scope.loadingid];
			}else if(scope.loading){
				scope.loading.show();
			}
		};
		scope.hideLoading = function(){
			if(scope[scope.loadingid]){
				scope[scope.loadingid].hide();
				scope.loading = scope[scope.loadingid];
			}else if(scope.loading){
				scope.loading.hide();
			}
		};
		scope.$parent.setImageElement = function(img){
			scope.imageElement = img;
		};
		scope.takePicture = function() {
			if ( typeof (Camera) == 'undefined') {
				scope.inputSelect.click();
				return;
			}
			var optionsData = {
				quality : config.camera.quality,
				destinationType : Camera.DestinationType.FILE_URI,
				sourceType : Camera.PictureSourceType.CAMERA,
				allowEdit : config.camera.allowEdit,
				encodingType : Camera.EncodingType.JPEG,
				targetWidth : config.camera.targetWidth,
				targetHeight : config.camera.targetHeight,
				popoverOptions : CameraPopoverOptions,
				saveToPhotoAlbum : config.camera.saveToPhotoAlbum,
				correctOrientation : config.camera.saveToPhotoAlbum.correctOrientation
			};
			$cordovaCamera.getPicture(optionsData).then(function(imageData) {
				var image = document.getElementById('olImg');
				image.src = imageData;
				scope.image = image;
			}, function(err) {
				console.log("error", err);
			});
		};
		scope.uploadPhoto = function(imageData) {
			var name = "image-mobile-" + makeid(10);
			var url = baseUrl + '/uploadImage';
			if(scope.options.createdContent){
				url = baseUrl + '/uploadImageWithContent';
			}
			if ( typeof (FileUploadOptions) == "function") {
				var success = function(r) {
					var res = JSON.parse(r.response);
					scope.path = res.path;
				};
				var error = function(error) {
					console.log(error);
				};
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
			} else {
				var file = scope.imageElement.imageBlob('image/jpeg').blob();
				if(file){
					// var file = $('#inputSelect').prop('files')[0];
					var form_data= new FormData();
					form_data.append("uploadedFile", file);
					if(scope.options.createdContent){
						UploadService.uploadImageWithContent(form_data, scope.showLoading, scope.hideLoading).then(function(res){
							scope.path = res.contentUrl;
						});
					}else{
						UploadService.uploadImage(form_data, scope.showLoading, scope.hideLoading).then(function(res){
							scope.path = res.path;
							console.log(scope.parent);
						});
					}

				}
			}
		};
	}

	return {
		restrict : 'ACE',
		templateUrl : 'templates/item/camera.htm',
		scope: {
			path : "=",
			options : "=",
			image: "=",
			loading: "=",
			parent: "="
		},
		// transclude : true,
		link : init
	};
}]);
