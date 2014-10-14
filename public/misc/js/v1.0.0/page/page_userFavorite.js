require([
	'common/interface',
	'common/util',
    'module/pageCommon'
],function(inter, util, common){

	common.initLogin();

	var page = 0;
	var pageSize = 5;
	var totalCount = 0;
	var favListTmp = ['<tr>',
            '<td>',
                '<div class="fav-list-main clearfix">',
                    '<div class="search-head">',
                        '<a href="/expert/detail/#{userId}" target="_blank"><img src="#{headUrl}" class="search-head-img" /></a>',
                        '<div class="search-country">',
                            '<span class="country-flag flag #{countryUrl}" title="#{#country}"></span>#{gender}',
                        '</div>',
                    '</div>',

                    '<div class="search-center">',
                        '<div class="name clearfix">',
                            '<a href="/expert/detail/#{userId}" target="_blank" title="">#{name}</a>',
                            '<span class="job" title="#{job}">#{job}</span>',
                        '</div>',
                        '<p class="introduce">#{introduction}</p>',
                        '<p class="skill clearfix">#{skill}</p>',
                    '</div>',
                '</div>',
            '</td>',
            '<td>',
                '<a href="javascript:;" data-id="#{userId}" class="icon btn-delete"></a>',
            '</td>',
        '</tr>'].join('');

    var bindDeleteEvent = function(){
    	$(".fav-list").on("click", ".btn-delete", function(e){

	        var btn = $(this),
	            id = btn.attr("data-id"),
	            deleteURL = util.strFormat(inter.getApiUrl().deleteFavorite, [id]);

	        var deleteFav = function(){
	            $.ajax({
	                url: deleteURL,
	                success: function(){
                        getTotalCount();
	                	btn.closest("tr").remove();
	                },
	                error: function(){
	                    $.alert('服务器繁忙，请稍后再试。');
	                }
	            });
	        }

	        $.confirm("确定要取消收藏吗？", deleteFav);

    	});	
    }

    var bindPagination = function(){
        $('.page').pagination(totalCount, {
            prev_text: '上一页',
            next_text: '下一页',
            link_to: 'javascript:',
            items_per_page: pageSize,
            num_display_entries: 6,
            current_page: page,
            num_edge_entries: 2,
            callback: function(page_id, jq){
                page = page_id;
                getFavoriteList();
            }
        });
    };

    var getTotalCount = function(){
		$.ajax({
			url: inter.getApiUrl().queryFavorite,
			data: { "pageSize": pageSize, "page": page },
			success: function(json){
				if (json.error) {
					$.alert(json.error);
				}else{
					totalCount = json.totalRowCount;
					bindPagination();
				};
			},
			error: function(){
				$.alert('服务器繁忙，请稍后再试。');
			}
		});
    };

    var getFavoriteList = function(){
		$.ajax({
			url: inter.getApiUrl().queryFavorite,
			data: { "pageSize": pageSize, "page":page },
			success: function(json){

				if (json.error) {
					$.alert(json.error);
				}else{
					var favListStr = "";
                    if(json.list && json.list.length){
                        $.each(json.list, function(i, e){
                            var item = json.list[i];

                            var gender = "";
                            if (item.gender.toLowerCase() == "man") {
                                gender = '<img alt="男" title="男" src="' + ued_conf.root + 'images/male.png" class="country-sex" width="13" height="13">';
                            }else{
                                gender = '<img alt="女" title="女" src="' + ued_conf.root + 'images/female.png" class="country-sex" width="13" height="13">';
                            }
                            var skillTag = "";
                            $.each(item.skillsTags, function(i, e){
                                var tag = item.skillsTags[i].tag;
                                skillTag = skillTag + "<a href='/expertsearch?ft="+ tag +"'>" + tag + "</a>";
                            });

                            var personalInfoLen = 130;
                            var introduction = "";
                            if (item.personalInfo.length > personalInfoLen) {
                                introduction = item.personalInfo.substring(0, personalInfoLen) + "...";
                            }else{
                                introduction = item.personalInfo;
                            };

                            var data = {
                                headUrl: 		item.headUrl,
                                country: 		item.country,
                                countryUrl: 	item.countryUrl,
                                name: 			item.userName,
                                job: 			item.job,
                                introduction: 	introduction,
                                gender: 		gender,
                                skill: 			skillTag,
                                userId: 		item.userId
                            };

                            favListStr = favListStr + util.template(favListTmp, data);
                        });
                    }else{
                        favListStr = '<tr><td align="center" height="60">还没有任何收藏</td></tr>';
                    }
					$(".fav-list").empty().append(favListStr);
					bindDeleteEvent();
				}

			},

			error: function(){

				$.alert('服务器繁忙，请稍后再试。');
			}
		});
    }

    //getFavoriteList();
    getTotalCount();

});