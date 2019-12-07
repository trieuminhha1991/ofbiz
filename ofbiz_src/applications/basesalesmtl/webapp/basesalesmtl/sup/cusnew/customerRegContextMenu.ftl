<div id="Context${id}" class="hide">
	<ul>
		<li action="edit">
			<i class="fa fa-edit"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.Edit)}
		</li>
		<li action="approve">
			<i class="fa fa-check"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.HRCommonAccept)}
		</li>
		<li action="viewimage" id='viewimage'>
			<i class="fa fa-eye"></i>&nbsp;${StringUtil.wrapString(uiLabelMap.BSViewImage)}
		</li>
	</ul>
</div>
<script>
	var ContextMenuPromos = (function(){
		var self = {};
		self.grid = $('#${id}');;
		self.initContext = function(){
			var ct = $('#Context${id}');
			ct.jqxMenu({ theme: 'olbius', width: 200, autoOpenPopup: false, mode: 'popup' });
			ct.on('itemclick', function (event) {
		        var args = event.args;
		        var itemId = $(args).attr('action');
		        var row = self.grid.jqxGrid('getSelectedRowindexes');
		        try{
				switch (itemId) {
					case 'edit':
						var x = self.grid.jqxGrid('getSelectedRowindex');
						CustomerRegistration.editCustomer(x);
						break;
					case 'approve':
						CustomerRegistration.approveCustomer(row);
						break;
					case 'viewimage':
						var data = CustomerRegistration.getGridData(row);
						if(data.url){
							CustomerRegistration.viewImage(row);
						}
						break;
					default:
						break;
				}
		        }catch(e){
				console.log(e);
				ct.jqxMenu('close');
		        }
			});
			ct.on('shown', function(event){
				var args = event.args;
		        var itemId = $(args).attr('action');
		        var row = self.grid.jqxGrid('getSelectedRowindex');
				var data = CustomerRegistration.getGridData(row);
				if(data.url){
					ct.jqxMenu('disable', 'viewimage', false);
				}else{
					ct.jqxMenu('disable', 'viewimage', true);
				}
			})
		};
		$(document).ready(function(){
			self.initContext();
		});
	})();
</script>