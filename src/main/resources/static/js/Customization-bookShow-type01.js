// 自定义组件名不能包含大写字母
Vue.component('first-customcomponent',{
    props: {
        newBookRecommend: {
            type: Array,
            // 对象默认值
            default: () =>
                [
                    {
                        searchByType:"",
                        bookType:"",
                        bookDetail:"",
                        bookName:"",
                        searchByAuthor:"",
                        bookAuthor:""
                    }
                ]
        }
    },
    data() {
        return {

        }
    },
    template:'<div class="book-list">\n' +
        '                    <ul>\n' +
        '                        <li v-for="book in newBookRecommend">\n' +
        '                            <a :href="book.searchByType" target="_blank" class="left">\n' +
        '                                [{{book.bookType}}]\n' +
        '                            </a>\n' +
        '                            <strong class="medium">\n' +
        '                                <a :href="book.bookDetail" :title="book.bookName" target="_blank" class="line-limit-length">{{book.bookName}}</a>\n' +
        '                                <div class="hot"></div>\n' +
        '                            </strong>\n' +
        '                            <a :href="book.searchByAuthor" target="_blank" class="right">{{book.bookAuthor}}</a>\n' +
        '                        </li>\n' +
        '                    </ul>\n' +
        '                </div>',
    methods: {

    }
})

// 该组件直接返回book列表，不需转化，vue中字符串可以和参数拼接，设定好了。
Vue.component('second-customcomponent',{
    props:{
        bookInfo:{
            type:Object,
            // 对象默认值
            default: () =>({
                bookfaceName:'31261.jpg',
                bookName:'逆天邪神',
                bookAuthor:'火星引力',
                bookType:'东方玄幻',
                finish:'连载中',
                bookDesc:'亘古通今，传闻世有灵境。关于灵境的说法，历朝历代的名人雅士众说纷纭，诗中记载：“自齐至唐，兹山濅荒，灵境寂寥，罕有人游。”“灵境不可状，鬼工谅难求。” ',
                wordCount:'537.8',
                clicks:'4732738'
            })
        },
        imgPreUrl:{
            type:String,
            default:'images/bookface/',
            required: true // 必填字段
            },
        bookDetailPreUrl:{
            type:String,
            default:'/book/',
            required: true // 必填字段
        },
        searchByBookTypePreUrl:{
            type:String,
            default:'/bookstore?bookType=',
            required: true // 必填字段
        },
        searchByAuthorPreUrl:{
            type:String,
            default:'/bookstore?author=',
            required: true // 必填字段
        },
    },
    data() {
        return {
        }
    },
    template:'<div class="Customization-bookShow-type03">\n' +
        '                            <div class="book-img-box">\n' +
        '                                <a :href="bookDetailPreUrl+bookInfo.bookId" target="_blank">\n' +
        '                                    <el-image :src="imgPreUrl+bookInfo.bookfaceName" :alt="bookInfo.bookName" fit="fill"></el-image>\n' +
        '                                </a>\n' +
        '                            </div>\n' +
        '                            <div class="book-mid-info">\n' +
        '                                <h2>\n' +
        '                                    <a :href="bookDetailPreUrl+bookInfo.bookId" target="_blank"  style="color: #262626;">{{bookInfo.bookName}}</a>\n' +
        '                                </h2>\n' +
        '                                <p class="author">\n' +
        '                                    <span style="float: left;height: 16px;line-height: 16px;margin-right: 5px;">\n' +
        '                                        <el-avatar :size="14" shape="circle"></el-avatar>\n' +
        '                                    </span>\n' +
        '                                    <span style="float: left;height: 16px;line-height: 16px;max-width: 80px;">\n' +
        '                                        <a :href="searchByAuthorPreUrl+bookInfo.bookAuthor" target="_blank" style="color: #a6a6a6;">{{bookInfo.bookAuthor}}</a>\n' +
        '                                    </span>\n' +
        '                                    <span style="float: left;height: 16px;line-height: 16px;"><a :href="searchByBookTypePreUrl+bookInfo.bookType" target="_blank" style="color:#a6a6a6;" >\n' +
        '                                        <el-divider direction="vertical"></el-divider>\n' +
        '                                        {{bookInfo.bookType}}\n' +
        '                                        <el-divider direction="vertical"></el-divider></a>\n' +
        '                                    </span>\n' +
        '\n' +
        '                                    <span style="float:left;width: 40px;height: 16px;line-height:16px;text-align: center;color: #a6a6a6;">{{bookInfo.finish}}</span>\n' +
        '                                </p>\n' +
        '                                <p class="intro" v-html="bookInfo.bookDesc"></p>\n' +
        '                                <p class="update">\n' +
        '                                    <span style="color: #a6a6a6;padding-left: 2px;">\n' +
        '                                        <span class="pYFjtNLx" style="font-size: 10px">{{bookInfo.wordCount}}</span>万字\n' +
        '                                        <el-divider direction="vertical"></el-divider>\n' +
        '                                        <span class="pYFjtNLx" style="font-size: 10px">{{bookInfo.clicks}}</span>次点击\n' +
        '                                    </span>\n' +
        '                                </p>\n' +
        '                            </div>\n' +
        '                        </div>',
    methods: {

    }
})


// 注意：1，引用时，如参数类型是Boolean，需要绑定，字符串才会被解析成表达式,但不是布尔值，比如是String，那么不能绑定，否则解析报错
//2 不是":isTotalShow"这种写法，而是":is-total-show"
//3 该组件使用时宽高都可自动调节。
Vue.component('third-customcomponent',{
    props: {
        infos:{
            type:Array,
            // 对象默认值
            default: () =>
              [
                    {
                        id:1,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:2,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:3,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:4,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:5,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:6,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:7,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:8,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:9,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    },
                    {
                        id:10,
                        bookName:'逆天邪神',
                        toUrl:'#',  // 书籍详情url
                        total:"23493",
                        typeUrl:'#',        //搜索此类型的书籍
                        bookType:'东方玄幻', // 书籍类型
                        authorUrl:'#',      //搜索此作者全部作品
                        author:'火星引力',    // 作者名
                        imgUrl:'../../static/images/bookface/2293.jpg' // 图片地址
                    }]

        },
        isTotalShow:{  // 是否展示数量,默认开启
            type:Boolean,
            default:true
        },
        isShowBookInfo:{  // 是否展示(除第一个）被隐藏的书籍详情 默认开启
            type:Boolean,
            default:true
        },
        isEmTotalShow:{  // 隐藏区域的总量是否展示  默认开启
            type:Boolean,
            default:true
        },
        totalDesc:{ // 隐藏区域总量的类型
            type:String,
            default:'',
            required: true // 必填字段
        },


    },
    methods: {
        // 用js控制li:hover触发，用于禁用li的hover，而click不受影响
        rankLiOver(e,index){
            if(this.isShowBookInfo && index!==0){
                var li = e.currentTarget
                li.style.height="122px"
                li.style.lineHeight="122px"
                li.getElementsByClassName("num-box")[0].style.display="none"
                li.getElementsByClassName("name-box")[0].style.display="none"
                li.getElementsByClassName("book-hidden")[0].style.display="block"
            }


        },
        rankLiLeave(e,index){
            if(this.isShowBookInfo && index!==0){
                var li = e.currentTarget
                li.style.height="32px"
                li.style.lineHeight="32px"
                li.getElementsByClassName("num-box")[0].style.display="block"
                li.getElementsByClassName("name-box")[0].style.display="block"
                li.getElementsByClassName("book-hidden")[0].style.display="none"
            }
        }
    },
    template:'<div class="Customization-bookShow-type04">\n' +
        '                <ul>\n' +
        '                    <li v-for="(info,index) in infos" @mouseover="rankLiOver($event,index)" @mouseleave="rankLiLeave($event,index)" :key="info.id">\n' +
        '                        <div class="num-box">\n' +
        '                            <span style="background: #e67225;" v-if="index===1">{{index+1}}</span>\n' +
        '                            <span style="background: #e6bf25;" v-else-if="index===2">{{index+1}}</span>\n' +
        '                            <span style="background: #ededed;color: #666;" v-else>{{index+1}}</span>\n' +
        '                        </div>\n' +
        '                        <div class="name-box">\n' +
        '                            <a style="color: #1a1a1a;" :title="info.bookName"\n' +
        '                               :href="info.toUrl" target="_blank">\n' +
        '                                {{info.bookName}}\n' +
        '                            </a>\n' +
        '                            <span class="total" v-if="isTotalShow">{{info.total}}</span>\n' +
        '                        </div>\n' +
        '                        <div class="book-hidden" v-if="isShowBookInfo || index==0">\n' +
        '                            <div class="left">\n' +
        '                                <h3>NO.{{index+1}}</h3>\n' +
        '                                <h2 style="font-weight: 700">\n' +
        '                                    <a :href="info.toUrl" :title="info.bookName"\n' +
        '                                       target="_blank">{{info.bookName}}</a>\n' +
        '                                    <p class="digital"  :style="{\'font-size\' : isEmTotalShow ? \'12px\':\'16px\',\'font-family\': \'Arial,serif\'}">\n' +
        '                                        <em v-if="isEmTotalShow">{{info.total}}</em>{{totalDesc}}\n' +
        '                                    </p>\n' +
        '                                </h2>\n' +
        '                                <p class="author">\n' +
        '                                    <a class="type" :href="info.typeUrl" target="_blank">{{info.bookType}}</a>\n' +
        '                                    <i>·</i>\n' +
        '                                    <a class="writer" :href="info.authorUrl" target="_blank">{{info.author}}</a>\n' +
        '                                </p>\n' +
        '                            </div>\n' +
        '                            <div class="right">\n' +
        '                                <a :href="info.toUrl" target="_blank">\n' +
        '                                    <img :src="info.imgUrl" :alt="info.bookName">\n' +
        '                                </a>\n' +
        '                                <span></span>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                    </li>\n' +
        '                </ul>\n' +
        '            </div>'

})