package com.jing.librarymanagementsystem.controller.backstage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.service.BorrowManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/backstage")
public class BackstageBookBorrowController {

    @Autowired
    BorrowManagementService borrowManagementService;

    @RequestMapping("toLibraryBorrowInfo")
    public String toLibraryBorrowInfo(){

        return "/backstage/library-borrowing-management";
    }

    // 搜索借阅记录
    @ResponseBody
    @RequestMapping(value ="/borrowInfo", method = RequestMethod.POST)
    public Map<String,Object> getBorrowInfo(@RequestBody Map<String,Object>body){

        String bookName = "%"+(String) body.get("bookName")+"%";
        String userName = "%"+(String) body.get("userName")+"%";
        Boolean isOverdue = (Boolean) body.get("isOverdue");
        int pageNum = (int) body.get("pageNum");
        int pageSize = (int) body.get("pageSize");
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String, Object>> data = borrowManagementService.selectAllBorrowInfo(bookName, userName, isOverdue);
        PageInfo<Map<String, Object>> bookPageInfo = new PageInfo<>(data);
        int total = (int) bookPageInfo.getTotal();
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("borrows", data);
        System.out.println(result.toString());
        return result;
    }


    // 还书
    @ResponseBody
    @RequestMapping(value ="/returnBook",method=RequestMethod.POST)
    public Boolean returnBook(@RequestBody List<Map<String,Object>> bodys){

        System.out.println(bodys.toString());
        try {
            borrowManagementService.batchReturnBook(bodys);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
