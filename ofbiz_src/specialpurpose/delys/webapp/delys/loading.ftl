<style>
	.loading-container{
		width: 100%;
		height: 100%;
		top: 0px;
		left: 0px;
		position: absolute;
		opacity: 0.7;
		background-color: #fff;
		z-index: 9999;
		text-align: center;
	}
	.spinner-preview{
		margin-top: 0;
		position: absolute;
		left: 50%;
		top: 50%;
		margin-left: -50px;
		margin-top: -50px;
	}
</style>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<div class='loading-container' id="loading${loadingid?if_exists}">
	<div class="spinner-preview" id="spinner-preview${loadingid?if_exists}"></div>	
</div>
<script>
	var id = "${loadingid?if_exists}";
	var opts = {
		"lines" : 12,
		"length" : 7,
		"width" : 4,
		"radius" : 10,
		"corners" : 1,
		"rotate" : 0,
		"trail" : 60,
		"speed" : 1
	};
	spinner_update(opts, "spinner-preview" + id); 
	hideLoading("loading"+id);
</script>