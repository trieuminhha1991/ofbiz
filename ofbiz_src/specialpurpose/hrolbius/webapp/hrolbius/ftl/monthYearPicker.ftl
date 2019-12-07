<form method="post" action="<@ofbizUrl>EmplTimekeepingReport</@ofbizUrl>" class="basic-form form-horizontal">
	<div class="row-fluid">
		<div class="control-group no-left-margin">
			<label>
				<label>${uiLabelMap.HRCommonMonth}</label>
			</label>			
			<div class="controls">
				<select name="month">
					<#list monthDisplay.entrySet() as entry>
						<option value="${entry.key}" <#if (month?exists && month == entry.key)>selected="selected"</#if>>${entry.value}</option>
					</#list>
				</select>
				
			</div>
		</div>
		<div class="control-group no-left-margin">
			<label>
				<label>${uiLabelMap.CommonYear}</label>
			</label>
			<div class="controls">
				<select name="year">
					<#list listYear as tempYear>
						<option value="${tempYear}" <#if (year?exists && year==tempYear)>selected="selected"</#if>>${tempYear}</option>
					</#list>
				</select>
				
			</div>
		</div>		
	</div>
	<div class="control-group no-left-margin">
			<label>
				&nbsp;
			</label>
			<div class="controls">
				<button type="submit" class="btn btn-small btn-primary">
					${uiLabelMap.CommonFind}
				</button>
			</div>
		</div>
	</div>
</form>
<script type="text/javascript">
	/* jQuery(document).ready(function(){
		jQuery(".year").datepicker({
			 changeMonth: false,
			  changeYear: true,
			  yearRange:'-90:+0'
		});
		
	}); */
</script>