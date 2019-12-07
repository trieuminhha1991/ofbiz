<#if !sync?exists || sync == 'N'>
<script type="text/javascript" src="/salesmtlresources/js/common/map.js"></script>
<script>
	var sync = false;
	function initmap(){
		$(document).ready(function(){
			$('body').trigger('mapinit');
		})
	};
	initmap();
</script>

<#else>
<script>
	var sync = true;
</script>
<script type="text/javascript" src="/salesmtlresources/js/common/map.js"></script>

</#if>