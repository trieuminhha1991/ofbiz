<#include 'script/modernTradeEditSalesmanInfoScript.ftl'/>
<script type="text/javascript">

</script>
<form class="form-horizontal form-window-content-custom margin-top10 margin-bottom10" id="initChangeSalesman" name="initChangeSalesmanForm" method="post" action="<@ofbizUrl>showShipmentConfirmPage</@ofbizUrl>">
	<div class="row-fluid">
	<input id='defaultWeightUomId' value='WT_kg' type='hidden'></input>
		<div class="span11">
			<div class='row-fluid' style="margin-bottom: -10px !important">
				<div class="span6">
                    <div class='row-fluid'>
                        <div class='span5'>
                            <span class="asterisk">${uiLabelMap.BSSupervisor}</span>
                        </div>
                        <div class="span7">
                            <div id="salessup">
                                <div id="jqxGridSalessup"></div>
                            </div>
                        </div>
                    </div>
				</div>
				<div class="span6">
                    <div class='row-fluid'>
                        <div class='span5'>
                            <span class="asterisk">${uiLabelMap.BSSalesmanName}</span>
                        </div>
                        <div class="span7">
                            <div id="salesman">
                                <div id="jqxGridSalesman"></div>
                            </div>
                        </div>
                    </div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>