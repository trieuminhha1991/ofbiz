<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<#assign stkEvent = delegator.findOne("StockEvent", {"eventId" : parameters.eventId?if_exists}, false)/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>

<div class="row-fluid">
<div class="span12">
	<div class="widget-box transparent" id="recent-box">
		<div class="widget-header" style="border-bottom:none">
			<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
				<div class="row-fluid">
					<div class="span6">
						<div class="tabbable">
							<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
								<li<#if !activeTab?exists || activeTab == "" || activeTab == "upload-tab"> class="active"</#if>>
									<a data-toggle="tab" href="#upload-tab">${uiLabelMap.Upload}</a>
								</li>
								<li<#if activeTab?exists && activeTab == "phieukk-tab"> class="active"</#if>>
									<a data-toggle="tab" href="#phieukk-tab">${uiLabelMap.DmsPhieuKiemKe}</a>
								</li>
								
								<li<#if activeTab?exists && activeTab == "aggregation-tab"> class="active"</#if>>
									<a data-toggle="tab" href="#aggregation-tab">${uiLabelMap.DmsAggregation}</a>
								</li>
							</ul>
						</div><!--.tabbable-->
					</div>
					<div class="span6" style="height:34px; text-align:right">
					 	<#if stkEvent?has_content && stkEvent.isClosed?has_content && stkEvent.isClosed == 'N'>
							<a style="cursor: pointer;" data-rel="tooltip" id="cancelEvent" class="button-action"
				    			title="${uiLabelMap.CommonCancel}" data-placement="bottom" >
				    			<i class="fa fa-trash red"></i>
				    		</a>
				    		<a style="cursor: pointer;" id="excel" class="button-action" href="/appbase/images/excels/KiemKe.xls" 
								download="KiemKe.xls" target="_blank" data-rel="tooltip" title="${uiLabelMap.DownLoadExcelTemplate}" 
								data-placement="bottom" data-original-title="${uiLabelMap.ExcelTemplate}">
								<i class="fa fa-file-excel-o"></i>
							</a>
						</#if>
					</div>
				</div>
			</div>
		</div>
		<div class="widget-body" style="margin-top: -12px !important">
			<div class="widget-main padding-4">
				<div class="tab-content overflow-visible" style="padding:8px 0">
					
					<#include "upload.ftl"/>
					
					<#include "phieukk.ftl"/>
					
					<#include "aggregation.ftl"/>
					
				</div>
			</div><!--/widget-main-->
		</div><!--/widget-body-->
	</div><!--/widget-box-->
</div><!-- /span12 -->
</div><!--/row-->

<script>
	$(function(){
		var eventId = '${parameters.eventId?if_exists}';
		$('#cancelEvent').on("click",function(){
			bootbox.dialog('${uiLabelMap.AreYouSureCancel}', 
			[{"label": '${uiLabelMap.CommonCancel}', 
				"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {bootbox.hideAll();}
	        }, 
	        {"label": '${uiLabelMap.OK}',
	            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
	            	$.ajax({
	        	    	url: "deleteStockEvent",
	        	    	type: "POST",
	        	    	data: {
	        	    		eventId: eventId,
	        	    	},
	        	    	async: false,
	        	    	success: function (res){
	        	    		window.location.reload();
	        	    	}
	        	    });
	            }
			}]);
		});
	});
</script>