<#include 'script/facilityNewFacilityInfoScript.ftl'/>
<form class="form-horizontal form-window-content-custom" id="initFacilityInfo" name="initFacilityInfo">
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div class="span5">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.FacilityName} </div>
					</div>
					<div class="span8">	
						<input id="facilityName"></input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.BLFacilityCode} </div>
					</div>
					<div class="span8">	
						<input id="facilityCode"></input>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.FacilityDirectlyUnder} </div>
					</div>
					<div class="span8">	
						<div id="parentFacilityId" style="width: 100%;"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div class="asterisk"> ${uiLabelMap.BSPSSalesChannel} </div>
					</div>
					<div class="span8">	
						<div id="productStoreId" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div> ${uiLabelMap.OpenedDate} </div>
					</div>
					<div class="span8">	
						<div id="openedDate" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
			</div>
			<div class="span7">
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.SquareFootage}</div>
					</div>
					<div class="span8">
						<div id="facilitySizeAdd"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.Unit}</div>
					</div>
					<div class="span8">
						<div id="facilitySizeUomId" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.Avatar}</div>
					</div>
					<div class="span7">
						<div id="idImagesPath">
							<input type="file" id="imagesPath" name="uploadedFile" class="green-label" accept="image/*"/>
						</div>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.BLUsingLocation}</div>
					</div>
					<div class="span7">
						<div id="requireLocation">
						</div>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.BLDateManagement}</div>
					</div>
					<div class="span7">
						<div id="requireDate">
						</div>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span4" style="text-align: right">
						<div>${uiLabelMap.Description}</div>
					</div>
					<div class="span8" style="text-align: left">
						<textarea id="description" name="description" data-maxlength="250" rows="3" style="resize: vertical;margin-top: 0px" class="span12"></textarea>
					</div>
				</div>
			</div>
		</div>
	</div>
</form>