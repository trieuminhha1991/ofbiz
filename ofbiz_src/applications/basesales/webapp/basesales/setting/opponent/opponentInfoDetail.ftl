<div>
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSOpponentId}:</label>
						</div>
						<div class="div-inline-block">
							<span><i>${dataOpponentInfo.partyId?if_exists}</i></span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSOPUserCreated}:</label>
						</div>
						<div class="div-inline-block">
							<span>${dataOpponentInfo.createdByPartyId?if_exists}</span>
						</div>
					</div>
				    <div class="row-fluid margin-top10">
					    <div class="logo-company">
						    <img width="300px" src="${dataOpponentInfo.image?default("/salesmtlresources/logo/LOGO_demo.png")}"/>
					    </div>
				    </div>
               	</div><!--.span6-->
				<div class="span6">
					<div class="row-fluid">
						<div class="div-inline-block">
					        <label>${uiLabelMap.BSOpponentName}:</label>
					    </div>
					    <div class="div-inline-block">
							<span>${dataOpponentInfo.groupName?if_exists}</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
					    	<label>${uiLabelMap.BSOPComment}:</label>
						</div>
						<div class="div-inline-block">
							<span>${dataOpponentInfo.comment?if_exists}</span>
						</div>
					</div>
					<div class="row-fluid">
						<div class="div-inline-block">
							<label>${uiLabelMap.BSDescription}:</label>
						</div>
						<div class="div-inline-block">
							<span>${dataOpponentInfo.description?if_exists}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>