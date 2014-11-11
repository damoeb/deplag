var deplag = new function () {
};
deplag.urls = {
    MATCH:'http://localhost:8080/deplag/rest/deplag/match',
    MATCH_FILE:'http://localhost:8080/deplag/rest/deplag/match-file'
};
deplag.core = new function () {

    $('#btt-match').live('click', function () {
        deplag.core.match();
    });

    this.match = function () {

        var sourceText = $('#txt-match').val();
        var markedText = sourceText;
        var sourceLength = markedText.length;
        var markedLength = 0;

        $.ajax({
            url:deplag.urls.MATCH,
            cache:false,
            data:sourceText,
            contentType:"text/html; charset=utf-8",
            type:'POST',
            success:function (response) {
                var fragments = response.fragments;
                $.each(fragments, function (id, documents) {
                    for (var j = 0; j < documents.length; j++) {
                        var document = documents[j];
                        var range = document.hit.rangeOfNeedle;
                        var needle = sourceText.substring(range.position, range.position + range.length);
                        var pre = document.pre.length <= 50 ? '' : '...';
                        var post = document.post.length <= 50 ? '' : '...';
//                        markedText = markedText.replace(needle, '<span title="' + pre + document.pre + document.text + document.post + post + '">' + needle + '</span>');
                        markedText = markedText.replace(needle, '<div style="margin-left:800px; position:absolute">' + pre + document.pre + +'<span class="hit">' + document.text + '</span>' + document.post + post + '</div><span class="needle">' + needle + '</span>');
//                        markedText = markedText.replace(needle, '<span title="...">' + needle + '</span>');
                        markedLength += range.length;
                    }
                });
                var markedPercent = Math.round(markedLength / sourceLength * 100);
                $('#marked-summary').html(markedPercent + '% matched');
                $('#marked-text').html(markedText);

                $('#marked-text SPAN')
//                    .qtip({
//                        content: { text: 'My tooltip content' },
//                        position: {
//                            my: 'left bottom',
//                            at: 'right top'
//                        }
//                    })
                    .qtip({
                        position:{
                            my:'bottom left',
                            at:'top left',
                            viewport:$(window),
                            effect:true
                        },
                        show:{
                            solo:true
                        },
                        hide:'unfocus'
                    })
                ;
            },
            error:function (jqXHR, textStatus, errorThrown) {

            }
        });
    };
};