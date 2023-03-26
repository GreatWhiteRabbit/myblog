package com.user.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.SurfService;
import com.user.Utils.IPUtil;
import com.user.Utils.IPVO;
import com.user.Utils.Result;
import com.user.Vo.Constant;
import com.user.Vo.SurfVo;
import com.user.entity.Surf;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;


@Controller
@ResponseBody
@RequestMapping("browse")
public class IpController {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SurfService surfService;

    private Result result = new Result();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/user")
    public void getInfo(HttpServletRequest request) throws IOException {
        Surf surf = translateInfo(request);
        boolean insert = surfService.insert(surf);
        if (insert) {
            // 向Redis中插入博客访问量
            String browseSurfNumber = stringRedisTemplate.opsForValue().get(Constant.myBlogBrowse);
            // browseSurfNumber为null说明没有数据
            if(browseSurfNumber == null) {
                int size = surfService.getSize();
                stringRedisTemplate.opsForValue().set(Constant.myBlogBrowse, String.valueOf(size));
            } else {
                // 将访问量加1
                int number = Integer.parseInt(browseSurfNumber) + 1;
                // 将访问量存入到Redis中
                stringRedisTemplate.opsForValue().set(Constant.myBlogBrowse, String.valueOf(number));
            }
        }
    }

    // 获取网站的总访问人数
    @GetMapping("sum")
    public Result getSum() {
        // 首先从Redis中获取
        String json = stringRedisTemplate.opsForValue().get(Constant.myBlogBrowse);
        if(json == null) {
            // 如果Redis中不存在，那么从数据库表中获取访问的总人数
            int size = surfService.getSize();
            // 将size存入到Redis中
            stringRedisTemplate.opsForValue().set(Constant.myBlogBrowse,String.valueOf(size));
            return result.ok(size);
        } else {
            int number = Integer.parseInt(json);
            return result.ok(number);
        }

    }

    // 管理员获取网站访问者的基本情况
    @GetMapping("/baseInfo")
    public Result getBaseInfo(@RequestParam int page, @RequestParam int size) {
        IPage<Surf> baseInfoByPage = surfService.getBaseInfoByPage(page, size);
        return result.ok(baseInfoByPage);
    }

    // 管理员删除访问信息
    @GetMapping("/delete")
    public Result deleteInfo(@RequestParam long surf_id) {
        boolean b = surfService.deleteInfo(surf_id);
        return result.ok(b);
    }

    // 管理查看不同时间段访问网站的人数
    @GetMapping("/getSumGroupByTime")
    public Result getSumGroupByTime(@RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {

        List<SurfVo> sumGroupByTime = surfService.getSumGroupByTime(startTime, endTime);
        return result.ok(sumGroupByTime);
    }

    // 管理通过查看在时间间隔内不同访问设备的情况
    @GetMapping("/getSumGroupByDevice")
    public Result getSumGroupByDevice(@RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        List<SurfVo> getSumGroupByDevice = surfService.getSumGroupByDevice(startTime, endTime);
        return result.ok(getSumGroupByDevice);
    }

    // 管理通过查看在时间间隔内访问者的操作系统
    @GetMapping("/getSumGroupByOS")
    public Result getSumGroupByOS(@RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        List<SurfVo> getSumGroupByOS = surfService.getSumGroupByOS(startTime, endTime);
        return result.ok(getSumGroupByOS);
    }

    // 管理通过查看在时间间隔内访问者的浏览器类型
    @GetMapping("/getSumGroupByBrowse")
    public Result getSumGroupByBrowse(@RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        List<SurfVo> getSumGroupByBrowse = surfService.getSumGroupByBrowse(startTime, endTime);
        return result.ok(getSumGroupByBrowse);
    }

    // 管理通过查看在时间间隔内访问者的IP所在的省份
    @GetMapping("/getSumGroupByProvince")
    public Result getSumGroupByProvince(@RequestParam Timestamp startTime, @RequestParam Timestamp endTime) {
        List<SurfVo> getSumGroupByProvince = surfService.getSumGroupByProvince(startTime, endTime);
        return result.ok(getSumGroupByProvince);
    }


    // 解析网络请求
    public Surf translateInfo(HttpServletRequest request) throws IOException {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("user-agent"));
        // 获取设备信息，PC还是移动
        String clientType = userAgent.getOperatingSystem().getDeviceType().toString();
        // 获取IP地址
        String ipAddr = IPUtil.getIpAddr(request);

        // 获取IP地址所在的地理位置,高德太无语了，基本上没有几个IP能够供开发者查询出来，但是总比没有好吧
        IPVO ipInfo = getAddress(ipAddr);

        String province,city;
        if(ipInfo == null) {
            province = "未知";
        } else {
            province = ipInfo.getProvince();
        }
        if(ipInfo == null) {
            city = "未知";
        } else {
            city = ipInfo.getCity();
        }
        // 获取操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取浏览器设备
        String browser = userAgent.getBrowser().toString();

        Surf surf = new Surf();
        surf.setDevice(clientType);
        surf.setIp(ipAddr);
        surf.setBrowsername(browser);
        surf.setCity(city);
        surf.setProvince(province);
        surf.setDevice_system(os);
        return surf;
    }

    // 获取IP地址所在的地理位置
    public IPVO getAddress(String ip) throws IOException {
        RestTemplate restTemplate=new RestTemplate();
        String key = "0bcd02c19249a37f35da029388100dd1";
       String url = "https://restapi.amap.com/v3/ip?ip=" +
               ip +
               "&output=json&key=" +
               key;
        ResponseEntity<String> responseEntity=restTemplate.
                getForEntity(url, String.class);

        try {
            IPVO ipvo = objectMapper.readValue(responseEntity.getBody(), IPVO.class);
            return ipvo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
