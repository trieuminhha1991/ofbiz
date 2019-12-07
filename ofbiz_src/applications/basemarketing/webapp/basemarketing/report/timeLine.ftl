<div id="my-timeline"></div>

<script>
	var sourceDms = {
		    timeline:
		    {
		        headline:"The Main Timeline Headline Goes here",
		        type:"default",
		        text:"<p>Intro body text goes here, some HTML is ok</p>",
		        asset: {
		            media:"/poresources/logo/product_demo_large.png",
		            credit:"Credit Name Goes Here",
		            caption:"Caption text goes here"
		        },
		        date: [
		            {
		                startDate:"2011,12,10,07,02,10",
		                endDate:"2015,12,11,08,11",
		                headline:"Headline Goes Here",
		                text:"<p>Body text goes here, some HTML is OK</p>",
		                tag:"This is Optional",
		                classname:"optionaluniqueclassnamecanbeaddedhere",
		                asset: {
		                    media:"/poresources/logo/product_demo_large.png",
		                    thumbnail:"/poresources/logo/product_demo_large.png",
		                    credit:"Credit Name Goes Here",
		                    caption:"Caption text goes here"
		                }
		            },
		            {
		                startDate:"2015,12,10,07,02,10",
		                endDate:"2016,12,11,08,11",
		                headline:"Headline Goes Here",
		                text:"<p>Body text goes here, some HTML is OK</p>",
		                tag:"This is Optional",
		                classname:"optionaluniqueclassnamecanbeaddedhere",
		                asset: {
		                    media:"/poresources/logo/product_demo_large.png",
		                    thumbnail:"/poresources/logo/product_demo_large.png",
		                    credit:"Credit Name Goes Here",
		                    caption:"Caption text goes here"
		                }
		            }
		        ],
		        era: [
		            {
		                startDate:"2011,12,10",
		                endDate:"2015,12,11",
		                headline:"Headline Goes Here",
		                text:"<p>Body text goes here, some HTML is OK</p>",
		                tag:"This is Optional"
		            },
		            {
		                startDate:"2015,12,10",
		                endDate:"2016,12,11",
		                headline:"Headline Goes Here",
		                text:"<p>Body text goes here, some HTML is OK</p>",
		                tag:"This is Optional"
		            }
		        ]
		    }
		};
	var text = "<div class='row-fluid' style='font-family: Open Sans'>" +
			"<div class='span2'>" +
			"<label style='float: right!important'>aaaaaa</label></div>" +
			"<div class='span10'>123456</div></div>";
	var sourceDms2 = {
		    timeline:
		    {
		        headline:"The Main Timeline Headline Goes here",
		        type:"default",
		        text:"<p>Intro body text goes here, some HTML is ok</p>",
		        asset: {
		            media:"/poresources/logo/product_demo_large.png",
		            credit:"Credit Name Goes Here",
		            caption:"Caption text goes here"
		        },
		        date: [
		            {
				startDate:"2011,12,10",
		                endDate:"2015,12,11",
		                headline:"Headline Goes Here",
		                classname:"optionaluniqueclassnamecanbeaddedhere",
		                text: text,
		                asset: {
		                    caption:"Caption text goes here"
		                }
		            }
		        ],
		        era: [
		            {
				startDate:"2011,12,10",
		                endDate:"2015,12,11",
		                headline:"Headline Goes Here",
		                tag:"This is Optional"
		            }
		        ]
		    }
		};
	function getCampaignTimelineData() {
		$.ajax({
	        url: 'getcampaignTimelineData',
	        async: true,
	        type: 'POST',
	    }).done(function(res) {
	    	var data = res["campaignTimelineData"];
	    	if (data) {
	    		renderTimeline(data);
			}
		});
	}
	function renderTimeline(data) {
		if (data && data.timeline.date.length == 0) {
			data = null;
		}
		createStoryJS({
	        type: 'timeline',
	        width: '100%',
	        height: '570',
	        source: data,
	        embed_id: 'my-timeline',
	        lang: lang,
	        hash_bookmark: true
	    });
	}
	var lang = "${locale}";
	$(document).ready(function() {
		if (lang != "en") {
			lang = "vi";
		}
		getCampaignTimelineData();
	});
	function prepareData(data){
		for ( var x in data.timeline.date) {
			data.timeline.date[x].startDate = new Date(data.timeline.date[x].startDate);
			data.timeline.date[x].endDate = new Date(data.timeline.date[x].endDate);
		}
		for ( var x in data.timeline.era) {
			data.timeline.era[x].startDate = new Date(data.timeline.era[x].startDate);
			data.timeline.era[x].endDate = new Date(data.timeline.era[x].endDate);
		}
		return data;
	}
</script>