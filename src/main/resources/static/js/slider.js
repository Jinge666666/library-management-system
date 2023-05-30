$(function() {

    var Page = (function() {

        var $navArrows = $( '#nav-arrows' ).hide(),
            $shadow = $( '#shadow' ).hide(),
            slicebox = $( '#sb-slider' ).slicebox( {
                onReady : function() {

                    $navArrows.show();
                    $shadow.show();

                },
                orientation : 'r',
                cuboidsRandom : true,
                disperseFactor : 30
            } ),

            init = function() {

                initEvents();

            },
            // slicebox的jump方法指定跳转到图，索引从1开始
            initEvents = function() {

                // add navigation events
                $navArrows.children( ':first' ).on( 'click', function() {
                    slicebox.next();
                    return false;

                } );

                $navArrows.children( ':last' ).on( 'click', function() {
                    slicebox.previous();
                    return false;

                } );
                // 給id为shadow节点绑定点击事件，目的是跳转到指定轮播页,可以加个obj接受参数，触发事件写法：$('#shadow').trigger('click',obj)
                $('#shadow').on('click',function (event,obj) {
                    slicebox.jump(obj)
                        return false;
                    })

            };

        return { init : init };

    })();

    Page.init();

});
