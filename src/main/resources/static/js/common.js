// 左侧侧边栏

// function init() {
//
//     var index = parseInt(localStorage.getItem("activeIndex").replace("1-",""))
//     document.getElementById("topBar").getElementsByTagName("li")[index-1].style.background="#F56C6C"
// }

var sideBar = new Vue({
    el:"#sideBar",
    data(){
        return{
            home:"/home",  // 首页
            nanpin:"/frequency?sex=male",   // 男频
            nvpin:"/frequency?sex=female",    // 女频
            rank:"/rank",     // 排行榜
            shuku:"/bookstore",    // 书库
            shenji:"/selfInfo?toLevelUp=toLevelUp",   // 升级
            myBorrow:"/borrow",  // 我的借阅
            myBookMark:"/bookmark",  // 我的书架
            myInfo:"/selfInfo",   // 个人信息

            activeIndex:localStorage.getItem("activeIndex"),

        }
    },
    methods: {
        // sub-menu 展开的回调
        handleOpen(key, keyPath) {

        },
        // sub-menu 收起的回调
        handleClose(key, keyPath) {
            // console.log(key, keyPath);
        },

        // 选中菜单的回调
        sideBarMenuClick(key,keyPath) {
            localStorage.setItem("activeIndex",key);
            switch (key) {
                case '1-1':
                    keyPath = this.home;
                    break;
                case '1-2':
                    keyPath = this.nanpin
                    break;
                case '1-3':
                    keyPath = this.nvpin
                    break;
                case '1-4':
                    keyPath = this.rank
                    break;
                case '1-5':
                    keyPath = this.shuku
                    break;
                case '2':
                    keyPath = this.myBorrow
                    break;
                case '3':
                    keyPath = this.myBookMark
                    break;
                case '4':
                    keyPath = this.myInfo
                    break;
            }
            window.location.href=keyPath;
        }


    },
    created(){

    }
})


// 右顶部搜索框
 var searchBox = new Vue({
    el:"#searchBox",
    data(){
        return{
            input:localStorage.getItem("input")===null?"":localStorage.getItem("input"),
            bookInfos: {}, // 从数据库拿值
            defaultBookInfos: {}, // 无搜索建议时的默认推荐
            timeout:null
        }
    },
    methods:{
        // 返回输入建议的方法，只要获得焦点就触发，仅当输入建议数据 resolve 时，通过调用 callback(data:[]) 来返回它
        querySearchAsync(queryString, cb) {

            // 这里的逻辑规则是：对于搜索词长度，为0，返回默认搜索建议，大于0，则访问接口返回值
            if(queryString){
                // // 加个判断是为了当搜索词减为1时，值不会更新嘛，但不允许再次抖动，就得去掉延迟。
                // if(queryString.length!==1){
                    clearTimeout(this.timeout);
                    // 延迟一定要比debouncedGetAnswer中的延迟大，因此要等他更新bookInfos，否则展示的永远是是上一次更新的旧数据
                    this.timeout = setTimeout(() => {
                        // 一定要注意，cb接受的是数组、数组、数组！！！
                        cb([this.bookInfos]);

                    }, 600);
            }else {
                cb(this.defaultBookInfos);
            }


        },
        // // 搜索框失去焦点就清空bookinfos，为了使再次获得焦点且搜索词只有一个时，不会展示旧的数据。而是消除下拉框
        // autocompleteBlur(){
        //     window.alert("dfdf22")
        //     this.bookInfos={}
        // },
        loadAll() { // 没输入搜索条件时的默认建议
            return [
                // 必须要有value名的属性
                {"value":["逆天邪神","逆天魔祖","逆天毒妃：魔尊榻上来"],"authors":["潇逆天"]}
            ];
        }, // 根据输入建议去数据库拿值
        handleSubmit() {
            axios({
                method: "POST",
                url:'/selectBookBySearchWord',
                dataType:"application/x-www-form-urlencoded",
                // sortType不传，后台会默认clicks排序，传null，则表示不排序。
                data:{"bookName":this.input.trim(),"bookAuthor":this.input,"bookType":null,"finish":null,"tag":null,"isBorrowed":null,"initial":null,"minWordCount":null,"maxWordCount":null,"pageNum":0,"pageSize":20}
            }).then(response=>{
                // localStorage存储数组时需要先使用JSON.stringify()转成字符串，取的时候再字符串转数组JSON.parse()。
                localStorage.setItem("bookStoreShow",JSON.stringify(response.data.books))
                localStorage.removeItem("input")
                localStorage.setItem("input",this.input)
                localStorage.setItem("pageNum",response.data.pageNum) // 当前页，
                localStorage.setItem("totalShow",response.data.books.length) // 当前页实际展示条数
                localStorage.setItem("total",response.data.total) // 一共多少条数据
                localStorage.setItem("pageSize",response.data.pageSize)  // 每页显示个数
                localStorage.setItem("isSearch",true) // 此参数是判断书库页刷新是搜索触发的还是普通刷新

                window.location.href="/bookstore"
            })
        },
        // 访问selectAllBookNameAndBookAuthor接口的函数，拿搜索建议词
        getSuggestionWords(){
            var vm = this
            axios({
                //请求方法
                method:'POST',
                url:'/selectAllBookNameAndBookAuthor',
                dataType:"application/x-www-form-urlencoded",
                // 书籍名和作者名都传，表示全都搜索
                data:{"queryString":vm.input}
            }).then(response=>{
                // 必须是value
                vm.bookInfos['value']=response.data.bookNames
                vm.bookInfos['authors']=response.data.bookAuthors
            })
        },
        // 选中建议词填充到输入框
        searchWordSelect(searchString){
            this.input=searchString.trim();
        },
        // 解决加了清除后若不重新获得焦点，下拉框不出现的问题
        clearSelect() {
            this.$refs.el_auto.activated = true;
            this.input='';

        }
    }, // 组件控制的html渲染后触发
    mounted() {
        this.defaultBookInfos = this.loadAll();

    },
    watch: { // 监听搜索词，减少访问接口的频率
        input: function (newInput, oldInput) {
            // 搜索词长度变为0或1时，不再次请求接口去更新值
            // if(!(newInput.length===1 || newInput.length===0)){
            if(newInput!==''){
                this.debouncedGetAnswer()
            }
        }
    },
    created: function () {
        // `_.debounce` 是一个通过 Lodash 限制操作频率的函数。这有助于等待用户将搜索词填完，而不是改变一个就访问一次，减少了访问接口频率
        this.debouncedGetAnswer = _.debounce(this.getSuggestionWords, 500)
    },

})

// 搜索按钮
new Vue({
    el:"#searchButton",
    data(){
        return{

        }
    },
    methods:{
        buttonClick(){
            searchBox.handleSubmit()
        }
    }
})



// 右顶部导航栏
new Vue({
    el:"#topBar",
    data(){
        return{
            home:sideBar.home,
            nanpin:sideBar.nanpin,
            nvpin:sideBar.nvpin,
            rank:sideBar.rank,
            shuku:sideBar.shuku,
            shenji:sideBar.shenji,

        }
    },
    methods: {
        topBarMenuClick(key, keyPath) {
            localStorage.setItem("activeIndex",key);
            switch (key) {
                case '1-1':
                    // this.location.href = this.home
                    keyPath = this.home;
                    break;
                case '1-2':
                    keyPath = this.nanpin
                    break;
                case '1-3':
                    keyPath = this.nvpin
                    break;
                case '1-4':
                    keyPath = this.rank
                    break;
                case '1-5':
                    keyPath = this.shuku
                    break;
                case '1-6':
                    keyPath = this.shenji
                    break;
            }
            window.location.href=keyPath;
        }
    }

})

// 下拉菜单
new Vue({
    el:"#dropDown",
    data(){
        return{
            aUrl:'/selfInfo', //资料设置
            bUrl:'/logout', // 退出登录
            cUrl:'#',  // 注销账号
            dialogVisible:false
        }
    },
    // 别给我写成method，是复数！！！
    methods:{
        handleCommand(command) {
            var toUrl = ''
            switch (command) {
                case 'a':
                    toUrl  = this.aUrl
                    break;
                case 'b':
                    toUrl = this.bUrl
                    break;
                case 'c':
                    toUrl = this.cUrl
                    break
            }
            if(command!=="c"){
                window.location.href=toUrl;
            }else {
                this.zhuxiao();
            }
        },
        // 注销账号申请
        zhuxiao(){
            const loading = this.$loading({
                lock: true,  // 锁定屏幕的滚动
                text: '稍等...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            setTimeout(()=>{
                loading.close();
                this.dialogVisible=true;
            },2000)
        },
        zhuxiaoOK(){
            this.dialogVisible=false;
            const loading = this.$loading({
                lock: true,  // 锁定屏幕的滚动
                text: '正在提交申请。。。',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            setTimeout(()=>{
                loading.close();
                axios({
                    method:"POST",
                    url:"/zhuxiao",
                    dataType:"json",
                    data:"lock" // 传个标识，防止用户意外访问该路径而销号
                }).then(response=>{
                    switch (response.data) {
                        case 1:
                            this.$alert('<h3 style="font-weight: bolder;color: red">非正常的访问方式，请停止！！！</h3>', '警告', {
                                confirmButtonText: '确定',
                                dangerouslyUseHTMLString: true,
                                callback: action => {
                                    this.$message({
                                        type: 'info',
                                    });
                                }
                            });
                            break;
                        case 2:
                            this.$message({
                                dangerouslyUseHTMLString: true,
                                message: '<div style="color: #FFFFFF;background: #000000;width: 300px;height: 50px;line-height: 50px;text-align: center;"><strong>系统繁忙。。。</strong></div>'
                            });
                            break;
                        case 3:
                            const loading = this.$loading({
                                lock: true,  // 锁定屏幕的滚动
                                text: '注销成功，再见。。。。。。',
                                spinner: 'el-icon-loading',
                                background: 'rgba(0, 0, 0, 0.7)'
                            });
                            setTimeout(()=> {
                                loading.close();
                                window.location.href="/toLogin"
                            },3000)
                    }
                })
            },3000)
        }

    }
})

// 消息弹出框
new Vue({
    el:"#newsPopover",
    data(){
        return{
        }
    },
    methods:{


    }
})