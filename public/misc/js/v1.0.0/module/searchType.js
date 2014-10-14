/**
 * @description: 搜索类型切换
 * @author: Young Foo (young.foo@helome.com)
 * @update:
 */
define('module/searchType', ['module/cookie'], function(cookie){

    var SearchTypeSwitch = function($searchParam, $searchTypeBtnParam, searchDataParam){

        var $search = $searchParam;

        var $searchTypePanel = $search.find(".search-type-panel"),
            $searchTypeBtn = $search.find(".search-type-btn"),
            $searchTypeBtnInner = $searchTypeBtnParam,
            $searchInput = $search.find(".J-header-search-input"),
            $searchBtn = $search.find(".search-btn");

        var curDate = new Date(),
            setDate = new Date((curDate/1000+43200)*1000),
            ph = $searchInput.attr("placeholder"),
            searchType = cookie.get("searchType");

        var userData = searchDataParam.userData,
            groupData = searchDataParam.groupData,
            serviceData = searchDataParam.serviceData,
            requireData = searchDataParam.requireData;

        var that = this;
        /*
         * param -- sd: search data
         */
        this.setActive = function(sd){
            $searchTypeBtnInner.attr("class", sd.btnClass);
            $search.attr("action", sd.searchUrl);
            $searchInput.attr("placeholder", sd.searchTip);
            cookie.set("searchType", sd.text, setDate);
        }

        if (!searchType) {
            that.setActive(userData);
            searchType = "user";
        };

        switch(searchType){
            case "user":
                that.setActive(userData);
                break;
            case "group":
                that.setActive(groupData);
                break;
            case "service":
                that.setActive(serviceData);
                break;
            case "require":
                that.setActive(requireData);
                break;
        }

        $searchTypeBtn.on("click", function(e){
            $searchTypePanel.slideToggle(100);
            e.stopPropagation();
        });

        $searchTypePanel.on("click", "li", function(e){
            var $this = $(this);
            var classType = $.trim($this.attr("class"));
            switch(classType){
                case "search-type-user":
                    that.setActive(userData);
                    break;
                case "search-type-group":
                    that.setActive(groupData);
                    break;
                case "search-type-service":
                    that.setActive(serviceData);
                    break;
                case "search-type-require":
                    that.setActive(requireData);
                    break;
            }

            $searchTypePanel.hide();
            e.stopPropagation();
        });

        $searchBtn.on('click', function(e){
            var keyword = $.trim($searchInput.val());
            if(keyword.length < 1){
                $searchInput.val('');
                e.preventDefault();
                return false;
            }
        });

        $(document).on("click", function(e){
            $searchTypePanel.hide();
            e.stopPropagation();
        });
    }

    return {
        SearchTypeSwitch: SearchTypeSwitch
    }

});