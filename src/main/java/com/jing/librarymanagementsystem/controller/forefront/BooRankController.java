package com.jing.librarymanagementsystem.controller.forefront;


import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.service.CommonTimerTaskService;
import com.jing.librarymanagementsystem.service.RankPageService;
import com.jing.librarymanagementsystem.timerTask.CommonTask01;
import com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 排行榜controller
* */
@Controller
@Slf4j
@RequestMapping("/rank")
public class BooRankController {

    @Autowired
    RankPageService rankPageService;

    @Autowired
    CommonTimerTaskService commonTimerTaskService;

    @RequestMapping
    public String toRankPage() {

        return "forefront/book-rank";
    }

    @ResponseBody
    @RequestMapping(value = "/bookRankShow", method = RequestMethod.POST)
    public Map<String,List<Map<String, String>>> bookRankShow(@RequestParam("rankByBookType") String bookRankByType){
        // 为四个排行榜指定定时更新任务 由于计算最大偏移量太过麻烦，我们利用mysql的SQL_CALC_FOUND_ROWS，自动对每次分类查询得到总数，来计算最大偏移量

        CommonTask01 task01 = commonTimerTaskService.commonTimerTaskService01("borrowedRankData", 0, 10, 2000);
        CommonTask01 task02 = commonTimerTaskService.commonTimerTaskService01("clickRankData", 0, 10, 2000);
        CommonTask01 task03 = commonTimerTaskService.commonTimerTaskService01("favoriteRankData", 0, 10, 2000);
        CommonTask01 task04 = commonTimerTaskService.commonTimerTaskService01("hotReviewRankData", 0, 10, 2000);

        List<Map<String, String>> borrowedRankData = rankPageService.getBookRankPageData(bookRankByType, BookSearchOrderConditionEnum.BORROWEDTIMES.toString(), task01.getStart(), task01.getNum());
        // 刷新最大偏移量，及时保存到对应任务中。校准管理员更新书籍时偏移量误差
        task01.setMaxOffset(rankPageService.FOUND_ROWS());
        List<Map<String, String>> clickRankData = rankPageService.getBookRankPageData(bookRankByType, BookSearchOrderConditionEnum.CLICK.toString(), task02.getStart(), task02.getNum());
        task01.setMaxOffset(rankPageService.FOUND_ROWS());
        List<Map<String, String>> favoriteRankData = rankPageService.getBookRankPageData(bookRankByType, BookSearchOrderConditionEnum.FAVORITES.toString(), task03.getStart(), task03.getNum());
        task01.setMaxOffset(rankPageService.FOUND_ROWS());
        List<Map<String, String>> hotReviewRankData = rankPageService.getBookRankPageData(bookRankByType, BookSearchOrderConditionEnum.COMMENTTIMES.toString(), task04.getStart(), task04.getNum());
        task01.setMaxOffset(rankPageService.FOUND_ROWS());

        Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();
        result.put("borrowedRankData",borrowedRankData);
        result.put("clickRankData",clickRankData);
        result.put("favoriteRankData",favoriteRankData);
        result.put("hotReviewRankData",hotReviewRankData);
        // 得到最近一次更新时的时间，任意一个任务都行，因为这些任务的更新周期都一样的，可代表统一更新时间
        String  latestTime = task01.getCurrentDate();
        Map<String, String> stringStringMap = new HashMap<String, String>();
        stringStringMap.put("lastUpdatedTime", latestTime);
        ArrayList<Map<String, String>> maps = new ArrayList<Map<String, String>>();
        maps.add(stringStringMap);
        result.put("lastUpdatedTime",maps);
        return result;
    }
}
