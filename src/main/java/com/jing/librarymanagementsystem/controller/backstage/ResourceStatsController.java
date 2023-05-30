package com.jing.librarymanagementsystem.controller.backstage;

import com.jing.librarymanagementsystem.service.BackstageActuatorMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 资源统计
 * */
@Controller
@RequestMapping("/backstage")
public class ResourceStatsController {

    @Autowired
    BackstageActuatorMonitorService backstageActuatorMonitorService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/main")
    public String toResourceStats(){

        return "backstage/ResourceStats";
    }

    @ResponseBody
    @RequestMapping(value = "/getStats",method = RequestMethod.POST)
    public Map<String,String> getStats(){

        Map<String, String> result = new HashMap<>();
        // 我们不在德鲁伊取，全都在redis取访客量和在线人数
//        // 在线人数和访客量
//        Map<String, String> result = backstageActuatorMonitorService.userActiveAndVisitStats();
        // redis取借阅量
        String borrowedNum = stringRedisTemplate.opsForValue().get("borrowedNum");
        result.put("borrowedNum", borrowedNum);
        // redis取访问量
        String visitorNum = stringRedisTemplate.opsForValue().get("visitorNum");
        result.put("visitorNum", visitorNum);
        // 在线人数我们不在德鲁伊取，因为用户比如手动登出后redis自动删除了key，（但关闭浏览器都没有删除，即都当前还在线）因此在redis取更加准确
        int size = Objects.requireNonNull(stringRedisTemplate.keys("user:*")).size();
        result.put("userActiveNum",String.valueOf(size));
        return  result;
    }
}
