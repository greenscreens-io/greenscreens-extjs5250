$(document).on("ready", function () {
  
	var me = this;
    me.converter =  new Markdown.Converter();
    Markdown.Extra.init(me.converter, {
        extensions: "all"
    });
    Markdown.Toc.init(me.converter);    
        
	$.ajax({ url: 'Instructions.md'}).done(function( data ) {
		debugger;

		var html = me.converter.makeHtml(data);
		$(document.body).html(html);
	});
});
