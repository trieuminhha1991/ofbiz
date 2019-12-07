<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/accresources/js/acc.bootbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>

<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc">
					<div class="row-fluid">
						<div class="span10">
							<ul class="nav nav-tabs" id="recent-tab">
								<li class="active">
									<a data-toggle="tab" id="fixedasset-assignment" aria-expanded="true">
										${uiLabelMap.BACCAssignment}
									</a>
								</li>
								<li class="">
									<a data-toggle="tab" id="fixedasset-depreciation" aria-expanded="true">
										${uiLabelMap.BACCDepreciation}
									</a>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div id="notification">
					</div>
					<div id="container" style="width: 100%;"></div>
					<div class="tab-content">
						<#include "fixedAssetViewAssign.ftl">
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	var OLBFAView = function(){
		
	};
	OLBFAView.prototype.bindEvent = function(){
		$('#fixedasset-assignment').on('click', function(){
			//Send Request
			$.ajax({
				  url: "ListAssignedParties",
				  type: "POST",
				  data: {fixedAssetId: '${parameters.fixedAssetId}'},
				  async: false,
				  success: function(res) {
					  $('.tab-content').html(res);
				  }
		  	});
		});
		
		$('#fixedasset-depreciation').on('click', function(){
			//Send Request
			$.ajax({
				  url: "ListFixedAssetDeps",
				  type: "POST",
				  data: {fixedAssetId: '${parameters.fixedAssetId?if_exists}'},
				  async: false,
				  success: function(res) {
					  $('.tab-content').html(res);
				  }
		  	});
		});
	}
	$(document).on('ready', function(){
		var faView = new OLBFAView();
		faView.bindEvent();
	});
</script>				