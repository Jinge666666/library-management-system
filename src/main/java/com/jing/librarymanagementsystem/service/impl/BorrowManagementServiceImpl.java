package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.BorrowMapper;
import com.jing.librarymanagementsystem.service.BorrowManagementService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BorrowManagementServiceImpl implements BorrowManagementService {
    @Autowired
    BorrowMapper mapper;

    @Override // 借书
    /**
     * 开启事务
     * 注意：默认spring事务只在发生未被捕获的 RuntimeException 时才回滚。  那么你不能手动去捕获异常，或者捕获异常时，catch
     * 必须抛出throw new RuntimeException()异常，aop才能捕获(类后面的异常也必须是RuntimeException)。或者说，你事务异常的回滚不交给aop去捕获回滚，而是手动捕获
     * 异常，那么catch中写TransactionAspectSupport.currentTransactionStatus().setRollbackOnly() 即可！！！
     * 注意：若controller层没打上@Transactional注解，那么若是controller层其他代码（非这个方法）发生错误，sql不会回滚，因此，若想回滚，只在service打上注解还不够
     * */
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(String bookId) {

        // 生成借阅信息
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        String userId = user.getUserId();
        // 生成当前借阅时间和还书时间，默认3天上限
        Calendar c = Calendar.getInstance();
        Date startDate = c.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String borrowTime = sf.format(startDate);
        // 当前时间增加三天
        c.add(Calendar.DAY_OF_MONTH, 3);
        String returnTime = sf.format(c.getTime());
        // 添加到借阅表
        mapper.saveBorrowInfo(userId,bookId,borrowTime,returnTime);
        // 修改book表借阅状态
        mapper.updateBookBorrowState(bookId,1);
    }

    // 还书
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(int borrowId,String bookId) {

        // 借阅表删除该借阅信息
        mapper.returnBook(borrowId);
        // 修改boo表借阅状态
        mapper.updateBookBorrowState(bookId,0);
    }

    // 展示用户借阅信息
    @Override
    public List<Map<String,Object>> showBookBorrowed(String userId,String searchValue) {

       return mapper.selectBorrowInfo(userId,searchValue);
    }

    // 显示已被借阅书籍的还书时间
    @Override
    public Map<String, Object> showBorrowReturnTime(String bookId) {
        return mapper.selectBorrowReturnTime(bookId);
    }

    @Override
    public List<Map<String, Object>> selectAllBorrowInfo(String bookName, String userName, boolean isOverdue) {

        List<Map<String, Object>> data=mapper.selectAllBorrowInfo(bookName, userName, isOverdue);
        // 时间格式化 + 0/1转boolean
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String,Object> s : data){
            Date date1 = (Date)s.get("borrowTime");
            Date date2 = (Date)s.get("returnTime");
            s.put("borrowTime",sf.format(date1));
            s.put("returnTime",sf.format(date2));
            s.put("isOverdue", String.valueOf(s.get("isOverdue")).equals("1"));

        }
        return data;
    }

// 批量还书
    @Transactional
    @Override
    public void batchReturnBook(List<Map<String,Object>> datas){
        for (Map<String, Object> data : datas) {
            // 直接调用还书接口一个个还
            String bookId = (String) data.get("bookId");
            int borrowId = (int)data.get("borrowId");
            this.returnBook(borrowId,bookId);
        }
    }
}
