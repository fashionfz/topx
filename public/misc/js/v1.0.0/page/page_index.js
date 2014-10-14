/**
 * @description: 首页
 * @author: YoungFoo(young.foo@helome.com)
 * @update: YoungFoo(young.foo@helome.com)
 * @data: 2014/1/15
 */

require(['module/pageCommon', 'module/cookie', 'module/searchType'],function(common, cookie, searchType){

    var tabChange = function(){

        var tableItem = $(".tab-table").find("ul.expert-box"),
            currentTable = tableItem.eq(0);

        currentTable.show();

        $(".dev-nav li a").eq(0).addClass("active").find(".dev-nav-mark").show();

        $(".dev-nav li a").on("click", function(){
            showTab($(this));
        });
    }

    var showTab = function(e){
        var self = e,
            li = self.parent("li"),
            index = li.index(),
            table = self.closest(".dev-tab").find(".tab-table"),
            tableItem = table.find("ul.expert-box"),
            currentTable = tableItem.eq(index);

        self.addClass("active").parent().siblings().find("a").removeClass("active");
        li.find(".dev-nav-mark").show().end().siblings().find(".dev-nav-mark").hide();
        currentTable.show(200).siblings().hide();
    }

    /*
     * 请不要删除
     * 搜索类型切换tab选项卡
     * 请不要删除
     */
    var searchTypeChange = function(){
        // var $search = $(".search-box"),
        //     $searchForm = $search.parent(".search-form"),
        //     $searchTypePanel = $searchForm.find(".search-type"),
        //     $searchInput = $search.find(".search-input"),

        //     $userItem = $searchTypePanel.find(".search-type-user"),
        //     $groupItem = $searchTypePanel.find(".search-type-group"),
        //     $serviceItem = $searchTypePanel.find(".search-type-service"),
        //     $requireItem = $searchTypePanel.find(".search-type-require");

        // var curDate = new Date(),
        //     setDate = new Date((curDate/1000+43200)*1000),
        //     ph = $searchInput.attr("placeholder"),
        //     searchType = cookie.get("searchType");

        // var userData = {
        //     obj: $userItem,
        //     text: "user",
        //     searchUrl: "/expertsearch",
        //     searchTip: "请输入服务者姓名或服务标签关键字"
        // }
        // var groupData = {
        //     obj: $groupItem,
        //     text: "group",
        //     searchUrl: "/searchgroup",
        //     searchTip: "请输入群组名称或群组标签关键字"
        // }
        // var serviceData = {
        //     obj: $serviceItem,
        //     text: "service",
        //     searchUrl: "/services/search",
        //     searchTip: "请输入服务名称或服务标签关键字"
        // }
        // var requireData = {
        //     obj: $requireItem,
        //     text: "require",
        //     searchUrl: "/require/search",
        //     searchTip: "请输入需求名称或需求标签关键字"
        // }

        // var setActive = function(searchData){
        //     $searchTypePanel.find("li").removeClass("active");
        //     searchData.obj.addClass("active");
        //     $searchForm.attr("action", searchData.searchUrl);
        //     $searchInput.attr("placeholder", searchData.searchTip);
        //     cookie.set("searchType", searchData.text, setDate);
        // }

        // if (!searchType) {
        //     setActive(userData);
        //     searchType = "user";
        // };

        // $searchTypePanel.find("li").removeClass("active");

        // switch(searchType){
        //     case "user":
        //         setActive(userData);
        //         break;
        //     case "group":
        //         setActive(groupData);
        //         break;
        //     case "service":
        //         setActive(serviceData);
        //         break;
        //     case "require":
        //         setActive(requireData);
        //         break;
        // }

        // $searchTypePanel.on("click", "li", function(e){
        //     var $this = $(this);
        //     var classType = $.trim($this.attr("class"));
        //     switch(classType){
        //         case "search-type-user":
        //             setActive(userData);
        //             break;
        //         case "search-type-group":
        //             setActive(groupData);
        //             break;
        //         case "search-type-service":
        //             setActive(serviceData);
        //             break;
        //         case "search-type-require":
        //             setActive(requireData);
        //             break;
        //     }
        //     e.stopPropagation();
        // });

        // $('.search-btn').on('click', function(e){
        //     var ft = $.trim($searchInput.val());
        //     if( ft.length < 1 ){
        //         $searchInput.val('');
        //         return false;
        //     }
        // });
    }

    var $searchCenter = $(".search-form");
        $searchCenterBtn = $searchCenter.find(".search-type-btn").find("i");

    var userData = {
        btnClass: "icon icon-user-white",
        text: "user",
        searchUrl: "/expertsearch",
        searchTip: "请输入服务者姓名或服务标签关键字"
    }

    var groupData = {
        btnClass: "icon icon-group-white",
        text: "group",
        searchUrl: "/searchgroup",
        searchTip: "请输入群组名称或群组标签关键字"
    }

    var serviceData = {
        btnClass: "icon icon-service-white",
        text: "service",
        searchUrl: "/services/search",
        searchTip: "请输入服务名称或服务标签关键字"
    }

    var requireData = {
        btnClass: "icon icon-require-white",
        text: "require",
        searchUrl: "/require/search",
        searchTip: "请输入需求名称或需求标签关键字"
    }

    var searchData = {
        userData: userData,
        groupData: groupData,
        serviceData: serviceData,
        requireData: requireData
    }
    
    var searchType = new searchType.SearchTypeSwitch($searchCenter, $searchCenterBtn, searchData);
    tabChange();
    common.initLogin();

});