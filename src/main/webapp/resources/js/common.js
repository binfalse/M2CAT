
function shortLinkText() {
	// shorten link text
	$("a:not(.img-link)").each(function () {
		var text = $(this).html();
		if( text.length > 32 ) {
			$(this).html( text.substr(0, 32) + "..." ).attr("title", text);
		}
	});
}

function shortText(element) {
	
	var text = $(element).html();
	if( element == undefined || text == undefined || text.length <= 128 )
		return;
	
	var short = text.substr(0, 128);
	var more = text.substr(128);
	
	var moreElem = $("<span></span>").addClass("more").html( more ).hide();
	var linkElem = $("<a href=\"#\"></a>").html("[more]").click(function() {
		$(this).parent().hide().parent().find(".more").show();
	});
	var shortElem = $("<span></span>").addClass("short").html("...&nbsp;&nbsp;").append(linkElem);
	$(element).html(short).append(moreElem).append(shortElem);
	
}