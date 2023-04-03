package com.user.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.user.Mapper.UsersMapper;
import com.user.Service.SendEmailCodeIpService;
import com.user.Service.UserInfoService;
import com.user.Service.UsersService;
import com.user.Utils.IPUtil;
import com.user.Utils.Result;
import com.user.Utils.Constant;
import com.user.Vo.Email;
import com.user.Vo.User;
import com.user.entity.UserInfo;
import com.user.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/users")
@ResponseBody
public class UsersController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SendEmailCodeIpService sendEmailCodeIpService;
    private   Result result =new Result();

    // 管理员注册
    @PostMapping
   public Result addUser(@RequestBody User user) throws InterruptedException {
      Users users = new Users();
      UserInfo userInfo = new UserInfo();

      AtomicBoolean exit = new AtomicBoolean(false);
      Thread thread = new Thread(() -> {
          users.setUser_email(user.getUser_email());
          users.setUser_status(1);
          users.setUser_name(user.getUser_name());
          users.setUser_creattime(new Timestamp(System.currentTimeMillis()));
          QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
          usersQueryWrapper.eq("user_email",users.getUser_email());
          Users one = usersService.getOne(usersQueryWrapper);
          if(one == null) {
              usersService.addUser(users);
          }else {
              // 返回错误
            exit.set(true);
          }
      });
      thread.start();
      Thread.sleep(1000);
      if(exit.get()) {
          return result.fail("邮箱已注册，请登录");
      }
      AtomicLong userID = new AtomicLong(0);
      Thread thread1 = new Thread(() -> {
          try {
              thread.join();
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          if(!exit.get()) {
              QueryWrapper<Users> wrapper = new QueryWrapper<>();
              wrapper.eq("user_email",user.getUser_email());
              Users one = usersService.getOne(wrapper);
              userID.set(one.getUser_id());
              userInfo.setUser_id(one.getUser_id());
              userInfo.setUser_account(user.getUser_account());
              userInfo.setUser_password(user.getUser_password());
              userInfo.setUser_birthday(user.getUser_birthday());
              userInfo.setUser_sex(1);
              userInfoService.addUserInfo(userInfo);
          }
      });
      thread1.start();
       return result.ok(userID.get());

  }

  // 访客输入邮箱验证用户是否存在并且存储验证码
    @GetMapping("/sendEmail")
    public Result sendEmail(@RequestParam String user_email, HttpServletRequest request) {
        // 首先验证邮箱是否存在于数据库中
        Users exist = usersService.isExist(user_email);
        if(exist != null) {
            // 用户已存在，直接返回
            return result.ok(exist);
        } else {
            // 首先记住操作用户IP地址，防止有人恶意消耗邮件资源
            String ipAddr = IPUtil.getIpAddr(request);
            // 根据条件判断是否禁用此IP
            boolean b = sendEmailCodeIpService.banIp(ipAddr);
            // 将记录插入数据库中
            sendEmailCodeIpService.addInfo(user_email,ipAddr);
            if(b) {
                // 禁用IP
                // 修改数据库表中的allowed字段
                sendEmailCodeIpService.updateInfo(ipAddr,0);
                return result.ok("false");
            }
            // 通过邮箱发送验证码
            String testCode = sendCode(user_email);
            // 设置三分钟的邮件验证码过期时间
            stringRedisTemplate.opsForValue()
                    .set(Constant.emailCode + user_email,testCode,180, TimeUnit.SECONDS);
            return result.ok();
        }
    }

    @Autowired
    private UsersMapper usersMapper;
    // 验证验证码是否正确并注册
    @PostMapping("/testCode")
    public Result testCode(@RequestBody Email userEmail) {
        String testCode = userEmail.getTestCode();
        String code = stringRedisTemplate.opsForValue().get(Constant.emailCode + userEmail.getUser_email());
        if(testCode.equals(code)) {
            Users users = new Users();
            users.setUser_email(userEmail.getUser_email());
            users.setUser_name(userEmail.getUser_name());
            users.setUser_status(1);
            users.setUser_creattime(new Timestamp(System.currentTimeMillis()));
            usersMapper.insert(users);
            return result.ok(users);
        } else {
            return  result.fail("验证码错误");
        }
    }

    // 普通用户登录
    @PostMapping("login")
    public Result simpleUserLogin(@RequestBody Users users) {
        String user_email = users.getUser_email();
        Users userByEmail = usersService.getUserByEmail(user_email);
        if (userByEmail.getUser_status() == 3) {
            if(users.getUser_name().equals("GreatWhiteRabbit")) {
                return result.ok(userByEmail);
            } else {
                return result.fail("用户不存在");
            }
        }
        if(userByEmail == null) {
            return result.fail("用户不存在");
        } else {
            return result.ok(userByEmail);
        }
    }



    // 管理员获取所有的用户
    @GetMapping("admin/getAll/{page}/{size}")
    public Result getAll(@PathVariable("page") int page, @PathVariable("size") int size) {
        IPage<Users> all = usersService.getAll(page, size);
        return result.ok(all);
    }

    // 管理员添加用户
    @PostMapping("admin/add")
    public Result adminInsert(@RequestBody Users users) {
        users.setUser_creattime(new Timestamp(System.currentTimeMillis()));
        users.setUser_status(1);
        usersService.addUser(users);
        return result.ok();
    }

    // 管理员删除用户
    @DeleteMapping("admin/{user_id}")
    public Result deteteUser(@PathVariable("user_id") long user_id) {
        boolean b = usersService.deteteUser(user_id);
        return result.ok(b);
    }


    // 邮件发送
    @Autowired
    private JavaMailSender javaMailSender;
    //    发送人
    private String sender = "1826311175@qq.com";
    //    标题
    private  String title = "大白兔的个人博客验证码";

    public String sendCode(String email)  {
        MimeMessage message = javaMailSender.createMimeMessage();
        String testCode = "";
        Random random = new Random();
        for(int i = 0; i < 6; i++) {
            testCode = testCode + random.nextInt(10);
        }
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(sender + "(大白兔)");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(title);
            String text = "<p>验证码  " + testCode + " 180秒内有效</p>" + "<br/>" +
            "<a href =\"https://www.db-rabbit.work\">大白兔的个人博客</a>";
            mimeMessageHelper.setText(text,true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        /*SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(sender + "(大白兔)");
        simpleMailMessage.setTo(email);

       String context = "验证码 " + testCode + " " +
                "博客网址" + "<a href=http://www.w3school.com.cn>W3School</a>";
        simpleMailMessage.setText(context);

        simpleMailMessage.setSubject(title);
        try {
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return testCode;
    }
}
