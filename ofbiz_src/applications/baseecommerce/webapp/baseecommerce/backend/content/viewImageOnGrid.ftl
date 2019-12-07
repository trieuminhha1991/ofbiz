<div id="ViewImagePopup" class='hide'>
	<div>
		${uiLabelMap.BSLinkImage}
	</div>
	<div class="form-window-container">
		<@loading id="ImageLoading" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/>
		<div id="image-container" class='image-preview'></div>
	</div>
</div>

<script>
	String.prototype = $.extend(String.prototype, {
		picturable: function() {
			var url = this.toString();
			var vl = '\"' + url + '\"';
			var str = "<div class='cell-custom-grid'><a href='javascript:PhotoViewer.viewImage(" + vl + ")'";
			if (!url) {
				str += " class='disabled' ";
			}
			str += ">${StringUtil.wrapString(uiLabelMap.BSViewImage)}</a></div>";
			return str;
		}
	});
	if (typeof (PhotoViewer)) {
		var PhotoViewer = (function() {
			var self = {};
			self.popup = $('#ViewImagePopup');
			self.image = "";
			self.initImageWindow = function() {
				self.popup.jqxWindow({
					width : 800,
					height : 600,
					isModal : true,
					autoOpen : false,
					modalOpacity : 0.7,
					theme : theme
				});
				self.popup.on('open', function(){
					self.loadImage(self.image);
				});
				self.popup.on('close', function(){
					$('#image-container').html('');
				});
			};
			self.viewImage = function(imgUrl){
				self.image = imgUrl;
				if(self.popup && self.popup.length){
					self.popup.jqxWindow('open');
				}
			};
			self.loadImage = function(image){
				if(image){
					var img = new Image();
					Loading.show('ImageLoading');
					img.onload = function() {
						var obj = $('#image-container');
						obj.html('');
						obj.append($(img));
						Loading.hide('ImageLoading');
					};
					img.src = image;
				}
			};
			$(document).ready(function(){
				self.initImageWindow();
			});
			return self;
		})();
	}
</script>