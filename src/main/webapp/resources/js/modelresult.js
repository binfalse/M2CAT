_.templateSettings =  {
		evaluate: /\{\{#[^=-](.+?)\}\}/g,
		interpolate: /\{\{(#=.*?)\}\}/g,
		escape: /\{\{(#-.*?)\}\}/g
};

$(document).ready( function() {
	
	var template = _.template( $("#model-result").html() );
	
	function checkFinished() {
		$.get(restUrl + "result/" + modelId + "/status", function(data) {
			console.log("got status:");
			console.log(data);
			
			if( data.state == "processing" ) {
				$("#model-wait-progress").css("width", data.process*100 + "%")
				setTimeout(function() { checkFinished(); }, 500);
			}
			else if( data.state == "finished" ) {
				console.log("finished!");
				$("#model-wait").fadeOut();
				setTimeout(function () { getResult(); }, 0);
			}
			else {
				// TODO display error
			}
		});
	}
	
	function getResult() {
		$.get(restUrl + "result/" + modelId + "/info", function(data) {
			console.log("got info:");
			console.log(data);
			
//			var str = syntaxHighlight(data);
//			$("#model-result-json").html(str);
//			$("#model-json").fadeIn();
			
			var res = template({ "info": data });
			$("#model-result").html(res).fadeIn();
			
			var name = null;
			if( data.model != null )
				name = data.model.modelName != null ? data.model.modelName : data.model.modelId;
			if( name != null )
				document.title = "M2CAT - " + name;
			
			// keep things short
			shortLinkText();
			$(".abstract-text").each( function() {
				shortText(this);
			});
		});
	}
	
	setTimeout( function() { checkFinished(); }, 500 );
	
});


// source: http://stackoverflow.com/questions/4810841/how-can-i-pretty-print-json-using-javascript
function syntaxHighlight(json) {
    if (typeof json != 'string') {
         json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}