<!-- //BOTTOM SPOTLIGHT -->
<div id="jm-mass-bottom" class="jm-mass-bottom wrap clearfix">
	<div class="main col3-set">
		<div class="brand">
			<#if partnerBanners?exists>
				<#list partnerBanners as partnerBanner>
					<div><div class="brand-item"><a href="${StringUtil.wrapString((partnerBanner.url)?if_exists)}" target="_blank"><img src="${StringUtil.wrapString((partnerBanner.originalImageUrl)?if_exists)}"></a></div></div>
				</#list>
			</#if>
		</div>
	</div>
</div>
<script>
	jQuery(document).ready(function($){
		$('.brand').slick({
		  dots: true,
		  infinite: true,
		  speed: 300,
		  slidesToShow: 1,
		  centerMode: true,
		  variableWidth: true,
		  dots: false
		});
	});

</script>
<!-- //BOTTOM SPOTLIGHT -->