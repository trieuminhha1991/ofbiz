<div id="ContainerForm" class="margin-top10">
	<div class="span12 no-left-margin boder-all-profile">
		<div class='row-fluid'>
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span5 align-right asterisk">${uiLabelMap.BIEContainerType}</div>
					<div class="span7"><div id="containerTypeId"></div></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5 align-right asterisk">${uiLabelMap.containerNumber}</div>
					<div class="span7"><input type='text' id="containerNumber" /></div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span5 align-right">${uiLabelMap.sealNumber}</div>
					<div class="span7"><input type='text' id="sealNumber" /></div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid margin-top10">
					<div class="span3 align-right">${uiLabelMap.ReceiveToFacility}</div>
					<div class="span7">
						<div id="facilityContainer">
							<div id="gridFacilityContainer">
							</div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span3 align-right">${uiLabelMap.Description}</div>
					<div class="span7"><textarea id="contDescription" name="contDescription" data-maxlength="250" rows="4" style="resize: vertical; margin-top:0px" class="span12"></textarea></div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/imexresources/js/import/createImportDocContainer.js"></script>