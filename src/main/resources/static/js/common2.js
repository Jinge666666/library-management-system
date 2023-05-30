new Vue({
    el:"#sideBar",
    data(){
        return{

            resourceStatsPath:"/backstage/main", // 资源统计
            dataMonitorPath:"/backstage/toDataMonitor",  //数据监控
            cacheMonitorPath:"/backstage/toCacheMonitor", //缓存监控
            serviceMonitorPath:"/backstage/serviceMonitor", //服务监控
            setTimeTaskPath:"#",  //定时任务
            libraryInfoPath:"/backstage/toLibraryInfo",
            libraryTypePath:"/backstage/toLibraryType",
            borrowInfoPath:"/backstage/toLibraryBorrowInfo",
            userInfoPath:"/backstage/toUserInfo",
            adminInfoPath:"/backstage/showAdmins",
            permissionsPath:"/backstage/showPermissions",
            psdUpdatePath:"/backstage/toPsdUpdate",

            activeIndex:localStorage.getItem("activeIndex")

        }
    },
    methods:{
        handleOpen(key, keyPath) {

        },
        handleClose(key, keyPath) {
        },
        sideBarMenuClick(index,Path){
            localStorage.setItem("activeIndex",index);
            switch (index) {
                case "1-1":
                    Path=this.resourceStatsPath
                    localStorage.setItem("activeName","");
                    break;
                case "1-2":
                    Path=this.dataMonitorPath
                    localStorage.setItem("activeName",">mysql监控");
                    break;
                case "1-3":
                    Path=this.cacheMonitorPath
                    localStorage.setItem("activeName","缓存监控");
                    break;
                case "1-4":
                    Path=this.serviceMonitorPath
                    localStorage.setItem("activeName","服务监控");
                    break;
                case "1-5":
                    Path=this.setTimeTaskPath
                    localStorage.setItem("activeName","定时任务");
                    break;
                case "2-1":
                    Path=this.libraryInfoPath
                    localStorage.setItem("activeName","图书信息");
                    break;
                case "2-2":
                    Path=this.libraryTypePath
                    localStorage.setItem("activeName","图书类型");
                    break;
                case "2-3":
                    Path=this.borrowInfoPath
                    localStorage.setItem("activeName","借阅信息");
                    break;
                case "3":
                    Path=this.userInfoPath
                    localStorage.setItem("activeName","读者信息管理");
                    break;
                case "4":
                    Path=this.adminInfoPath
                    localStorage.setItem("activeName","管理员信息管理");
                    break;
                case "5":
                    Path=this.permissionsPath
                    localStorage.setItem("activeName","权限");
                    break;
                case "6":
                    Path=this.psdUpdatePath
                    localStorage.setItem("activeName","修改密码");
                    break;
            }
            window.location.href=Path;
        }
    },
    created() {

    }
})

var commonHeader =new Vue({
    el:"#commonHeader",
    data(){
        return{
            activeMenuFather:"",
            activeMenu:'',
        }
    },
    methods:{

    },
    created(){
        var index = localStorage.getItem("activeIndex")
        if(index==="1-1"){
            this.activeMenuFather=""
        }else {
            if(index<"2"){
                this.activeMenuFather="监控"
            }else {
                if(index<"3"){
                    this.activeMenuFather="图书管理"
                }
                if(index>="3"){
                    this.activeMenuFather=""
                }
            }
        }

        this.activeMenu=localStorage.getItem("activeName")


    }
})