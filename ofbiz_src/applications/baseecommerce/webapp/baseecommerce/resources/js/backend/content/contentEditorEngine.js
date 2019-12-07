$(document).ready(function() {
	$("#sidebar-collapse").click();
	Editor.init();
	Images.init();
	if (contentId) {

	}
});
if (typeof (Images) == "undefined") {
	var Images = (function() {
		var handleEvents = function() {
			$("#imgTitleImage").click(function() {
				$("#txtTitleImage").click();
			});
			$("#txtTitleImage").change(function(){
				Images.readURL(this);
			});
		};
		var readURL = function(input) {
			if (input.files && input.files[0]) {
		        var reader = new FileReader();
		        reader.onload = function (e) {
		            $('#imgTitleImage').attr('src', e.target.result);
		        }
		        reader.readAsDataURL(input.files[0]);
		    }
		};
		return {
			init: function() {
				handleEvents();
			},
			readURL: readURL
		};
	})();
}
if (typeof (Editor) == "undefined") {
	var Editor = (function() {
		var ckEditor;
		var handleEvents = function() {

		};
		var initEditor = function() {
			ckEditor = CKEDITOR.replace('editor', {
				extraPlugins: 'uploadimage,image2,save,wordcount',
				uploadUrl: 'quickUpload',
				height: 400,
				filebrowserImageUploadUrl: 'browserImageUpload',
				stylesSet: [
					{ name: 'Narrow image', type: 'widget', widget: 'image', attributes: { 'class': 'image-narrow' } },
					{ name: 'Wide image', type: 'widget', widget: 'image', attributes: { 'class': 'image-wide' } }
				],
				image2_alignClasses: [ 'image-align-left', 'image-align-center', 'image-align-right' ],
				image2_disableResizer: true
			});
		};
		var initCollapse = function() {
			var list = $('.collapse');
			for(var x = 0 ; x < list.length; x++){
				(function(x){
					var obj = $(list[x]);
					obj.on('show.bs.collapse', function(){
						var id = obj.attr('id');
						$('a[href="#'+id+'"] .fa-chevron-left').addClass('fa-chevron-down');
						$('a[href="#'+id+'"] .fa-chevron-left').removeClass('fa-chevron-left');
					});
					obj.on('hide.bs.collapse', function(){
						var id = obj.attr('id');
						$('a[href="#'+id+'"] .fa-chevron-down').addClass('fa-chevron-left');
						$('a[href="#'+id+'"] .fa-chevron-down').removeClass('fa-chevron-down');
					});
				})(x);
			}
		};
		var getValue = function() {
			return ckEditor.getData();
		};
		var setValue = function(data) {
			if (data) {
				ckEditor.setData(data);
			}
		};
		return {
			init: function() {
				initEditor();
				initCollapse();
				handleEvents();
			},
			getValue: getValue,
			setValue: setValue
		};
	})();
}