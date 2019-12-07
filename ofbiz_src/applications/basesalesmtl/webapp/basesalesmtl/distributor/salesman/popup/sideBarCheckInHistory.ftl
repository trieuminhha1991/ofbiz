<div class="hide-side-bar" id="menu-side-bar-check-in-history">

	<button class="closeSideBar" onClick="closeSideBarCheckInHistory()">
		<span class="fa fa-times"></span>
	</button>
	<h3>
		Menu
	</h3>
	<hr class="side-bar-separator">
	<div class="side-bar-container">
		<div class="side-bar-selection" id="side-bar-selection-check-in-items"></div>
	</div>
</div>

<style>
	#menu-side-bar-check-in-history {
	    position: absolute;
	    top: 0px!important;
	    bottom: 0px;
	    left: 0px!important;
	    width: 250px;
	    
	    flex-flow: column nowrap;
	    align-items: center;
	    
	    overflow-y: auto;
	
	    padding: 30px 20px 20px 20px;
	
	    margin-left: 0;
	
	    background: white;
	    visibility: visible;
	
	    box-shadow: 0 5px 10px #999999;
	
	    z-index: 50;
	    transition: all 0.7s;
	    box-sizing: border-box;
	    
	}
	
	.jqx-item {
		padding: 0!important;
	}
	
	.hide-side-bar {
	    margin-left: -50%!important;
	    visibility: hidden;
	}
	
	.side-bar-selection {
		margin: 5px 0;
	}
	
	.side-bar-item-container ul {
		margin: 0;
		padding: 0;
	}
	
	.side-bar-item-container ul li{
		display: block;
		padding: 5px;
	    font-size: 14px;
	}
	
	
	.side-bar-item-container .nav-header:hover{
		background-color: hsla(0, 0%, 93.3%, .4);
	}
	
	.side-bar-item-container ul li span {
	    margin-right: 10px;
	}
	
	ul.nav-list li {
		padding-left: 10px;
	}
	
	ul.nav-list:before {
		border: none!important;
	}
	
	ul.nav-list:hover {
	    background: none!important;
	}
	
	.side-bar-selection .side-bar-item-container ul li a {
		text-decoration: none;
		color: inherit;
	}
	
	.side-bar-selection .side-bar-item-container ul li :hover {
		text-decoration: none;
	}
	
	.nav-header {
	    text-transform: inherit;
	    font-size: 13px;
	    color: #595959;
	}
	
	
	.closeSideBar {
		border: none;
	    outline: none;
	    background: none;
	    
	    color: #4d4d4d;
	    
	    padding: 3px;
	    position: absolute;
	    top: 5px;
	    right: 5px;
	}
	
	.closeSideBar:hover {
		color: #333;
	}
	
	.side-bar-item-container ul li.active {
		background: #cc181e;
	    color: #fff;
	}
	
	.side-bar-item-container ul li.active:hover {
		background: #cc181e;
	    color: #fff;
	}
	
	.side-bar-item-container .nav-header {
		width: 100%;
	    position: inherit;
	    padding: 5px 0px 5px 0px;
		cursor: pointer;
	}
	
	.side-bar-item-container .nav-header .glyphicon{
	    margin-right: 5px;
	    font-size: 12px;
	}

</style>

<script type="text/javascript">

	function openSideBarCheckInHistory() {
		if($("#menu-side-bar-check-in-history").hasClass("hide-side-bar")) {
			$("#menu-side-bar-check-in-history").removeClass("hide-side-bar");
		} else {
			closeSideBarCheckInHistory();
		}
	}
	
	function closeSideBarCheckInHistory() {
		if(!$("#menu-side-bar-check-in-history").hasClass("hide-side-bar")) {
			$("#menu-side-bar-check-in-history").addClass("hide-side-bar");
		}
	}
</script>