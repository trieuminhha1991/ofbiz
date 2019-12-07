<div class="widget-box" style="border-bottom: none !important;">
	<#assign fileProperties = parameters.fileProperties/>
	<#assign entries = parameters.entries/>
	<#assign created = Static["com.olbius.util.DateUtil"].parseAndConvertDate(fileProperties.created?if_exists)/>
	<#assign lastModified = Static["com.olbius.util.DateUtil"].parseAndConvertDate(fileProperties.lastModified?if_exists)/>
	<div class="widget-body" >
		<table style="width:100%" class="table table-striped table-hover table-bordered dataTable">
			<thead>
				<tr>
					<td>T&ecirc;n tệp</td>
					<td>Th&ocirc;ng tin tạo</td>
					<td>Thay đổi</td>
					<td>Quyền</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>${fileProperties.fileName}</td>
					<td>${fileProperties.createdBy} - ${created}</td>
					<td>${fileProperties.lastModifiedBy} - ${lastModified}</td>
					<td>
						<a href="#modal-table" role="button" class="green" data-toggle="modal">${uiLabelMap.Permission}</a>
						</a>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>

<div id="modal-table" class="modal hide fade" tabindex="-1" aria-hidden="true" style="display: none;">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">×</button>
			${uiLabelMap.TableTitle_Permission}
		</div>
	</div>

	<div class="modal-body no-padding">
		<div class="row-fluid">
			<table class="table table-striped table-bordered table-hover no-margin-bottom no-border-top">
				<thead>
					<tr>
						<th>${uiLabelMap.User}</th>
						<th>${uiLabelMap.Permission}</th>
						<th>
							<i class="icon-time bigger-110"></i>
							${uiLabelMap.Allow}
						</th>
					</tr>
				</thead>

				<tbody>
					<#list entries as entry>
						<tr>
							<td>${entry.user}</td>
							<td>${entry.privileges}</td>
							<td>${entry.allow}</td>
						</tr>
					</#list>
				</tbody>
			</table>
		</div>
	</div>

	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
			<i class="icon-remove"></i>
			Close
		</button>

	</div>
</div>