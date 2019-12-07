var renderPreview = function() {
	SlidePreview.render(DataAccess.getData({
				url: "JQGetMainSlide?type=preview",
				data: {},
				source: "listSlide"}));
};
var url = "JQGetMainSlide";
var addUrl = "addImageToMainSlide";