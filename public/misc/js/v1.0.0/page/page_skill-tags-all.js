/**
 * @description: 所有服务者
 * @author: TinTao(tintao.li@helome.com)
 * @update:
 */
require([
    'common/util',
    'common/interface',
    'module/pageCommon',
    'module/goTop',
    'module/filter'
],function(util, inter, common, goTop, filter){
    common.initLogin();
    common.searchTypeInstance.setActive(common.searchData.userData);
    filter.init();
    
    var expertTpl = [
            '<a href="#{linkUrl}" title="#{userName} 的名片" class="expert clearfix">',
                '<div class="expert-head">',
                    '<em></em>',
                    '<img src="#{headUrl}" alt="#{userName}"/>',
                '</div>',
                '<div class="expert-info">',
                    '<div class="expert-name">',
                        '<span>#{userName}</span>&nbsp;#{isOnline}',
                    '</div>',
                    '<div class="expert-job">',
                        '<span class="icon icon-star-base" title="平均分：#{averageScore}">',
                            '<span class="icon icon-star-on" style="width:#{width}px"></span>',
                        '</span>',
                        '<span class="star-num">(#{commentNum})</span>',
                    '</div>',
                    '<div class="expert-expense" title="#{expense}">#{expense}</div>',
                '</div>',
                '<div class="expert-tags clearfix">',
                    '#{skillsTags}',
                '</div>',
            '</a>'
        ].join(''),

        loadingTpl = '<div class="search-loading"><span class="loader"></span></div>',
        countryAllTpl = [
            '<span class="continent-country #{isHide}" data-continent="#{continentName}">',
            '#{countryList}',
            '</span>'
        ].join(''),
        countryTpl = '<span><a href="#{url}" title="#{countryNameFull}">#{countryName}</a></span>',
        bLoad = true,
        pageEnd = false;

     /**
     * 初始化洲和国家并绑定事件
     */
    var initCountry = function(){
        var $continent = $('.continent'),
            url = location.pathname,
            continent,countries,str = [],
            getCountry = function(countries){
                var arr = [];
                $.each(countries,function(i,e){
                    arr.push(util.template(countryTpl, {
                        url : (url + '?' + util.location('cf', e=='全部'?'':e)).replace('??','?'),
                        countryName : e.length>5? e.substring(0,5)+'..':e,
                        countryNameFull : e
                    }))
                });
                return arr.join('');
            };
        $.each(country,function(i,e){
            continent = e.continent;
            countries = e.country;
            str.push(util.template(countryAllTpl, {
                continentName : continent,
                isHide : i==0?'':'none',
                countryList : getCountry(countries)
            }));
        });
        $continent.append(str.join(''));
        $('.continent-name').on('mouseover',function(){
            var $this = $(this),
                $continentName = $this.siblings('.continent-name'),
                continent = $this.attr('data-continent'),
                $countries = $('.continent-country[data-continent="'+continent+'"]');
            $continentName.removeClass('current');
            $this.addClass('current');
            $countries.removeClass('none');
            $countries.siblings('.continent-country').addClass('none');
        });

    };

    /**
     * 绑定“换一批”点击事件
     */
    var initChange = function(){
        $('.J-change-tags').on('click', function(){
            var $this = $(this),
                i = util.location().i||2,
                seq = parseInt($this.attr('data-seq'))+1,
                tagsTemp = '<a href="#{tagUrl}">#{tagName}</a>',
                tagsList = $('.J-tags-list'),
                firstTags = tagsList.find('a:first').clone(),
                tagsListTemp = [],
                location = window.location.pathname + '?p=1';
            util.setAjax(inter.getApiUrl().getTypeTagsUrl, {i: i,seq: seq}, function(json){
                if( json ){
                    if( json.length ){
                        $this.attr('data-seq',seq);
                        $.each(json, function(j, n){
                            tagsListTemp.push(util.template(tagsTemp, {
                                tagUrl: n.href,
                                tagName: n.tagName
                            }));
                        });
                        tagsList.html(tagsListTemp.join(''));
                    } else {
                        util.setAjax(inter.getApiUrl().getTypeTagsUrl, {i: i,seq: 1}, function(json){
                            $this.attr('data-seq',1);
                            $.each(json, function(j, n){
                                tagsListTemp.push(util.template(tagsTemp, {
                                    tagUrl: n.href,
                                    tagName: n.tagName
                                }));
                            });
                            tagsList.html(tagsListTemp.join(''));
                        },function(){
                            $.alert('服务器繁忙，请稍后再试！');
                        }, 'GET')
                    }
                }
            }, function(){
                $.alert('服务器繁忙，请稍后再试！');
            }, 'GET')
        })
    };

    /**
     * 将瀑布流绑定到scroll
     */
    var scrollLoad = function(){
        var self = this;

        self.initPage = 2;
        self.pageIndex = 2;

        $(window).scrollTop(0);

        $(window).on('scroll', function(){
            var self = this;
            if(bLoad){
                if( $(window).scrollTop() + $(window).height() >= $(document).height()-100 ){
                    var self = this,
                        load = $('.search-loading'),
                        seaArg = util.location() || {};

                    if(!seaArg.i){
                        seaArg.i = $('#i').val();
                    }
                    if(!seaArg.s){
                        seaArg.s = $('#s').val();
                    }
                    bLoad = false;
                    self.data = seaArg;
                    self.data.p = self.pageIndex;
                    if(load.length<1){
                        load = $(loadingTpl);
                        $('.experts').append(load);
                    }
                    load.show();
                    util.setAjax(inter.getApiUrl().allExpertUrl, self.data, function(json){
                        load.hide();
                        sucSearch(json);
                    },function(){
                        pageEnd = true;
                    },'GET');
                }
            }
        });
    };

    /**
     * 向尾部追加数据
     */
    var sucSearch = function(data){
        var self = this;
        var aSearch = [],
            list = data.list;

        if(list.length){
            bLoad = true;
            for(var i in list){
                var object = list[i],
                    sSkills = "",
                    starWidth = object.averageScore/5 * 73;

                for(var j in object.skillsTags){
                    sSkills = sSkills + ('<span>'+ object.skillsTags[j].tag +'</span>');
                }

                aSearch.push(util.template(expertTpl, {
                    averageScore : object.averageScore,
                    linkUrl : object.linkUrl,
                    headUrl : object.headUrl,
                    userName : object.userName,
                    commentNum : object.commentNum == 0 ? '0' : object.commentNum,
                    width : starWidth == 0 ? '0' : starWidth,
                    country : object.country,
                    expense : object.expenses,
                    skillsTags : sSkills,
                    isOnline : object.isOnline ? '<em class="search-icon search-icon-online" title="在线"></em>' : '',
                    countryUrl : object.countryUrl
                }));
            }
            $('.expert-list').append(aSearch.join(''));
            self.pageIndex = self.pageIndex + 1;
        }else{
            pageEnd = true;
            bLoad = false;
        }
    };

    
    initChange();
    scrollLoad();
    initCountry();
    goTop.init();
});