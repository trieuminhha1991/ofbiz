<style>
		#chat{
			height:500px;
		}
		#chatWrap{
			border: 2px solid #a1a1a1;
			padding: 10px 40px; 
			background: #dddddd;
			width: 300px;
			border-radius: 25px;
			float: left;
		}
		#listMember{
			color: blue;
		}
		#contentWrap{
		display: none;
		}
		.whisper{
			color: gray;
		}
		.error{
			color: red;
		}
	</style>

<div id="userName">
		<p id="userNameError"/>
		<form id="setNick">
			<input type="text" id="txtUserName" placeholder="User Name"/>
			<input type="submit"/>
		</form>
	</div>
	
	<div id="contentWrap">
		<div id="chatWrap">
			<div id="chat"></div>
			<form id="send-message">
				<input size="35" id="message"></input>
				<input type="submit"></input>
			</form>
		</div>
		<div id="listMember">
		
		</div>
	</div>
<script>
		jQuery(function($){
			var socket = io.connect();
			var $userNameForm = $('#setNick');
			var $userNameError = $('#userNameError');
			var $userBox = $('#txtUserName');
			var $listMember = $('#listMember');
			var $messageForm = $('#send-message');
			var $messageBox = $('#message');
			var $chat = $('#chat');
			
			$userNameForm.submit(function(e){
				e.preventDefault();
				socket.emit('new user', $userBox.val(), function(data){
					if(data){
						$('#userName').hide();
						$('#contentWrap').show();
					}else{
						$userNameError.html('This UserName is valid');
					}
				});
				$userBox.val('');
			});
			socket.on('new members', function(data){
				var mem = '';
				for(i = 0; i < data.length; i ++){
					mem += data[i] + '<br/>'
				}
				$listMember.html(mem);
			});
			$messageForm.submit(function(e){
				e.preventDefault();
				socket.emit('send message', $messageBox.val(), function(data){
					$chat.append("<span class='error'><b>" + data + "</span><br/>");
				});
				$messageBox.val('');
			});
			socket.on('new message', function(data){
				$chat.append('<b>' + data.username + ': </b>' + data.msg + "<br/>");
			});
			socket.on('new message private', function(data){
				$chat.append("<span class='whisper'><b>" + data.username + ": </b>" + data.msg + "</span><br/>");
			});
		});	
	</script>
