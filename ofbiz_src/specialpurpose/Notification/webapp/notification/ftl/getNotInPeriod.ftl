<#--
//TODO use websocket or anything else to solve this issue
<script type="text/javascript">
	$(document).ready(function() {
		setInterval(function(){
			$.ajax({
			  url: "NotificationListBarUpdate",
			  cache: false,
			  success: function(html){
				if(html.indexOf("org.ofbiz.webapp.control.RequestHandlerException") > -1){
					window.location = "main";
				}else{
					$('#ntfarea').html($.parseHTML(html));
				}
			  },
			  error: function(html){
				  
			  }
			}); 
		}, 600000);	
	});
</script>
-->