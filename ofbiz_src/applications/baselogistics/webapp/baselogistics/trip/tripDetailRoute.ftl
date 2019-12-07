<style>
    #omnibox button {
          	border: none;
    		outline: none;
    		background: none;
          }

          #omnibox {
          	margin: 15px;
          	display: flex;
          	height: 30px;
          	align-items: stretch;
          	border-bottom: 1px solid #ddd;
    		background: #fff;
    		border-bottom: 1px solid #ddd;
    		box-shadow: 0 3px 12px rgba(27,31,35,0.15);
          }

          #pac-input {
    		width: 250px;
       		margin: 0;
       		border: none;
    		outline: none;
    		background: none;
    		height: 100%;
        	padding: 0px 10px;
    	}
</style>

<div class="tab-pane<#if activeTab?exists && activeTab == " route-tab"> active</#if>" id="route-tab">

    <#if !trip.isHasOptimalRoute?has_content && trip.statusId !="TRIP_COMPLETED">
        <div class="row-fluid">
            <button id="optimizeRouteShippingTrip" class='btn btn-primary form-action-button pull-left'>
                <i class='fa fa-map-marker'></i> ${uiLabelMap.BLOptimizeRoute}
            </button>
        </div>
        <div class="row-fluid" id="displayRouteCtn">
            <button id="displayRoute" class='btn btn-primary form-action-button pull-left'>
                <i class='fa fa-map-marker'></i> ${uiLabelMap.BLRoute}
            </button>
        </div>
        <div class="row-fluid " id="mapViewRoute">
            <div class='span12'>
                <div id="mapRoute" style=" height: 500px; width: 100%;"></div>
                <div id="omnibox">
                    <input id="pac-input" class="form-control" type="text" placeholder="Type city, zip or address here..">
                    <button class="search-box-button" onClick="TripDetailRouteObj.searchAdress()" title="search">
                        <span class="fa fa-search"></span>
                    </button>
                </div>
            </div>

        </div>
        <#else>
            <div class="row-fluid" id="displayRouteCtn">
                <button id="displayRoute" class='btn btn-primary form-action-button pull-left'>
                    <i class='fa fa-map-marker'></i> ${uiLabelMap.BLRoute}
                </button>
            </div>
            <div class="row-fluid " id="mapViewRoute">
                <div class='span12'>
                    <div id="mapRoute" style=" height: 500px; width: 100%;"></div>
                    <div id="omnibox">
                        <input id="pac-input" class="form-control" type="text" placeholder="Type city, zip or address here..">
                        <button class="search-box-button" onClick="TripDetailRouteObj.searchAdress()" title="search">
                            <span class="fa fa-search"></span>
                        </button>
                    </div>
                </div>
            </div>
    </#if>
</div>