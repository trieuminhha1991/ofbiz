<div id="infoFormConfirm" class="margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5 align-right'>
						<span class=" ">${uiLabelMap.BIETestEventType}</span>
					</div>
					<div class="span7">
						<div id="productEventTypeDT"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5 align-right'>
						<span class="">${uiLabelMap.BIETestEventCode}</span>
					</div>
					<div class="span7">
						<div id="eventCodeDT"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5 align-right'>
						<span class="">${uiLabelMap.BIETestEventName}</span>
					</div>
					<div class="span7">	
						<div id="eventNameDT"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5 align-right'>
						<span class=" ">${uiLabelMap.BIEAgreement}</span>
					</div>
					<div class="span7">
						<div id="agreementDT">
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span5 align-right'>
						<span class=" asterisk">${uiLabelMap.BIEPackingList}</span>
					</div>
					<div class="span7">
						<div id="packingListDT">
						</div>
			   		</div>
				</div>
			</div>
			
			<div class="span6">
				<div class='row-fluid'>
					<div class='span3 align-right'>
						<span class=" asterisk">${uiLabelMap.BIEExecutedDate}</span>
					</div>
					<div class="span7">
						<div id="executedDateDT"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span3 align-right'>
						<span class="">${uiLabelMap.BIECompletedDate}</span>
					</div>
					<div class="span7">
						<div id="completedDateDT"></div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class="span3 align-right"><span class="">${uiLabelMap.Description}</span></div>
					<div class="span7">
						<div id="descriptionDT"></div>
			   		</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div class="span12">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
		<table id="tableProduct" width="100%" border="0" cellpadding="0" class="table table-striped table-bordered table-hover dataTable">
			<thead>
				<tr valign="bottom" style="height: 40px">
					<th width="3%"><span><b>${uiLabelMap.SequenceId}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.ProductId}</b></span></th>
					<th width="25%" class="align-center"><span><b>${uiLabelMap.ProductName}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.Unit}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.BLQuantityRegistered} </br></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.BLQuantityUse} </br></span></th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</div>
</div>
