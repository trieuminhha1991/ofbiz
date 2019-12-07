<div id='timeline-promo' style="width: 100%"></div>

<script type="text/javascript">
	var windowHeight = $(window).height();
	$("#timeline-promo").css("height", windowHeight - 120);
</script>

<!-- 3 -->
<#--<script type="text/javascript">
  // The TL.Timeline constructor takes at least two arguments:
  // the id of the Timeline container (no '#'), and
  // the URL to your JSON data file or Google spreadsheet.
  // the id must refer to an element "above" this code,
  // and the element must have CSS styling to give it width and height
  // optionally, a third argument with configuration options can be passed.
  // See below for more about options.
  timeline = new TL.Timeline('timeline-promo',
    'https://docs.google.com/spreadsheets/d/1cWqQBZCkX9GpzFtxCWHoqFXCHg-ylTVUWlnrdYMzKUI/pubhtml');
</script>
-->
<#include 'component://widget/templates/jqwLibraryUiLabel.ftl'/>

<!-- 1 -->
<link title="timeline-styles" rel="stylesheet" href="/salesresources/css/timeline.css">

<!-- 2 -->
<script type="text/javascript" src="/salesresources/js/timeline.js"></script>

<script type="text/javascript" src="/salesresources/js/promotion/promotionTimeline.js?v=001"></script>
