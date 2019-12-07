<style>
	.loader {
		background: url('/aceadmin/jqw/jqwidgets/styles/images/loader.gif') 50% 50% no-repeat;
		background-color: rgba(255, 255, 255, 0.7);
		position: fixed;
		left: 0px;
		top: 0px;
		width: 100%;
		height: 100%;
		z-index: 99999999;
	}
	
	.hidden-loading {
		background: transparent;
		visibility: hidden;
	}
</style>
<div class="loader hidden-loading"></div>

<script>
	function openLoader() {
		if($(".loader").hasClass("hidden-loading")) {
			$(".loader").removeClass("hidden-loading");
		}
	}
	
	function closeLoader() {
		if(!$(".loader").hasClass("hidden-loading")) {
			$(".loader").addClass("hidden-loading");
		}
	}
</script>