package com.user.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.*;
import com.user.Utils.EntityChange;
import com.user.Utils.Result;
import com.user.Vo.BlogNoData;
import com.user.Vo.BlogVo;
import com.user.Vo.Constant;
import com.user.Vo.LabelListBlogs;
import com.user.entity.BlogData;
import com.user.entity.label;
import com.user.entity.labelBlog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("label")
@Controller
@ResponseBody
public class LabelController {
    @Autowired
    private LabelService labelService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private LabelBlogService labelBlogService;

    @Autowired
    private BlogVoService blogVoService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MyRedisService myRedisService;
    @Autowired
    private BlogDataService blogDataService;

    private Result result = new Result();

    @Autowired
    private ObjectMapper objectMapper;

    // 获取label的名称和对应的label名称下有哪些blog
    @GetMapping("/list")
    public Result getLabelList() throws JsonProcessingException {
        // 首先从Redis中获取label的集合，如果没有，再从数据库中查询存取到Redis中
        List<String> range = stringRedisTemplate.opsForList().range(Constant.labels, 0, -1);
        assert range != null;
        if (range.size() == 0) {
            // 从数据库中获取相对的数据
            List<label> list = labelService.list();
            List<LabelListBlogs> labelListBlogsList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                // 将blog存入对应的labelListBlogs中
                LabelListBlogs labelListBlogs = new LabelListBlogs();
                int label_id = list.get(i).getLabel_id();
                String label_name = list.get(i).getLabel_name();
                labelListBlogs.setLabel_id(label_id);
                labelListBlogs.setLabel_name(label_name);
                List<labelBlog> blogIdByLabelIdList = labelBlogService.getBlogIdByLabelId(label_id);
                List<Long> longList = new ArrayList<>();
                for (int j = 0; j < blogIdByLabelIdList.size(); j++) {
                    longList.add(blogIdByLabelIdList.get(j).getBlog_id());
                }
                labelListBlogs.setBlogIdList(longList);
                // 将 labelListBlogs存入到List<LabelListBlogs>集合中
                labelListBlogsList.add(labelListBlogs);
            }
            // 将数据库中获取到的数据存放到Redis中
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < labelListBlogsList.size(); i++) {
                String string = objectMapper.writeValueAsString(labelListBlogsList.get(i));
                stringList.add(string);
            }
            stringRedisTemplate.opsForList().leftPushAll(Constant.labels, stringList);
            setExpireTime(Constant.labels,Constant.labelsExpireTime);
            return result.ok(labelListBlogsList);
        } else {
            // 将数据从List取出来然后解析返回
            List<LabelListBlogs> labelListBlogsList = new ArrayList<>();
            for (int i = 0; i < range.size(); i++) {
                String s = range.get(i);
                LabelListBlogs labelListBlogs = objectMapper.readValue(s, LabelListBlogs.class);
                labelListBlogsList.add(labelListBlogs);
            }
            return result.ok(labelListBlogsList);
        }
    }


    // 这个函数是getLabelList的内部调用方法，不需要将实体类转成object类，在本controller方法中还需要用到
    public List<LabelListBlogs> getList() throws JsonProcessingException {
        // 首先从Redis中获取label的集合，如果没有，再从数据库中查询存取到Redis中
        List<String> range = stringRedisTemplate.opsForList().range(Constant.labels, 0, -1);
        assert range != null;
        if (range.size() == 0) {
            // 从数据库中获取相对的数据
            List<label> list = labelService.list();
            List<LabelListBlogs> labelListBlogsList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                // 将blog存入对应的labelListBlogs中
                LabelListBlogs labelListBlogs = new LabelListBlogs();
                int label_id = list.get(i).getLabel_id();
                String label_name = list.get(i).getLabel_name();
                labelListBlogs.setLabel_id(label_id);
                labelListBlogs.setLabel_name(label_name);
                List<labelBlog> blogIdByLabelIdList = labelBlogService.getBlogIdByLabelId(label_id);
                List<Long> longList = new ArrayList<>();
                for (int j = 0; j < blogIdByLabelIdList.size(); j++) {
                    longList.add(blogIdByLabelIdList.get(j).getBlog_id());
                }
                labelListBlogs.setBlogIdList(longList);
                // 将 labelListBlogs存入到List<LabelListBlogs>集合中
                labelListBlogsList.add(labelListBlogs);
            }
            // 将数据库中获取到的数据存放到Redis中
            List<String> stringList = new ArrayList<>();
            for (int i = 0; i < labelListBlogsList.size(); i++) {
                String string = objectMapper.writeValueAsString(labelListBlogsList.get(i));
                stringList.add(string);
            }
            stringRedisTemplate.opsForList().leftPushAll(Constant.labels, stringList);
            setExpireTime(Constant.labels,Constant.labelsExpireTime);
            return labelListBlogsList;
        } else {
            // 将数据从List取出来然后解析返回
            List<LabelListBlogs> labelListBlogsList = new ArrayList<>();
            for (int i = 0; i < range.size(); i++) {
                String s = range.get(i);
                LabelListBlogs labelListBlogs = objectMapper.readValue(s, LabelListBlogs.class);
                labelListBlogsList.add(labelListBlogs);
            }
            return labelListBlogsList;
        }
    }

    // 通过label_id、页面大小、页数获取blog数据
    @GetMapping("/{label_id}/{page}/{size}")
    public Result getBlogByLabelId_Page_Size(@PathVariable int label_id, @PathVariable int page,
                                             @PathVariable int size) throws JsonProcessingException {
        List<LabelListBlogs> list = getList();
        List<BlogVo> blogVoList = new ArrayList<>();
        int listSize = list.size();
        if (listSize < (page - 1) * size) {
            return result.fail("获取失败");
        }
        for (int i = 0; i < listSize; i++) {
            if (list.get(i).getLabel_id() == label_id) {
                // 获取label_id标签中的blog_id
                List<Long> blogIdList = list.get(i).getBlogIdList();
                int newSize = blogIdList.size();
                // 查询到的最后一个博客数据
                int lastIndex;
                if (newSize >= page * size) {
                    lastIndex = page * size;
                } else {
                    lastIndex = newSize;
                }
                for (int j = (page - 1) * size; j < lastIndex; j++) {
                    blogVoList.add(getBlogById(blogIdList.get(j)));
                }
                return result.ok(blogVoList);
            }
        }
        return result.fail("获取失败");
    }


    // 通过blog_id获取blog的相关数据，此方法只供controller内部使用，不提供外部接口
    public BlogVo getBlogById(long blog_id) throws JsonProcessingException {
        String json = stringRedisTemplate.opsForValue().get(Constant.blog + blog_id);
        String blogDataJson = stringRedisTemplate.opsForValue().get(Constant.blogData + blog_id);
        if (json == null) {
            // 从数据库中查询
            BlogVo blogVo = blogVoService.selectBlogById(blog_id);
            // 将查询到的结果存放到Redis中
            int category_id = blogVo.getCategory_id();
            int label_id = blogVo.getLabel_id();
            String category_name = categoryService.getCategoryById(category_id);
            String label_name = labelService.getLabelById(label_id);

            // 将blogVo转成blogNoData
            EntityChange entityChange = new EntityChange();
            BlogNoData blogNoData = entityChange.ChangeBlogVo_ToNoBlog(blogVo);
            blogNoData.setCategory_name(category_name);
            blogNoData.setLabel_name(label_name);
            // 将实体类序列化
            String s = objectMapper.writeValueAsString(blogNoData);
            stringRedisTemplate.opsForValue().set(Constant.blog + blog_id, s);
            setExpireTime(Constant.blog + blog_id, Constant.blogExpireTime);
            // 将博客的访问量加1
            BlogData blogData = new BlogData();
            blogData.setBlog_id(blog_id);
            blogData.setBlog_likes(blogVo.getBlog_likes());
            // 访问量加1
            int browse = blogVo.getBlog_browse();
            blogVo.setBlog_browse(browse + 1);
            blogData.setBlog_browse(browse + 1);
            // 修改数据库
            updateBlogData(blogData);
            String data = objectMapper.writeValueAsString(blogData);
            stringRedisTemplate.opsForValue().set(Constant.blogData + blog_id, data);

            return blogVo;
        } else {
            // 将json字符串转成java
            BlogVo blogVo = objectMapper.readValue(json, BlogVo.class);
            BlogData blogData = objectMapper.readValue(blogDataJson, BlogData.class);

            // 将博客的访问量加1
            int browse = blogData.getBlog_browse();
            // 访问量加1
            blogVo.setBlog_browse(browse + 1);
            blogVo.setBlog_likes(blogVo.getBlog_likes());
            blogData.setBlog_browse(browse + 1);
            // 修改数据库
            updateBlogData(blogData);
            String data = objectMapper.writeValueAsString(blogData);
            stringRedisTemplate.opsForValue().set(Constant.blogData + blog_id, data);

            return blogVo;
        }
    }

    // 修改博客的访问数据，此方法只供controller内部使用，不提供外部接口
    public void updateBlogData(BlogData blogData) throws JsonProcessingException {
        blogDataService.updateBlogData(blogData);
        String data = objectMapper.writeValueAsString(blogData);
        stringRedisTemplate.opsForValue().set(Constant.blogData + blogData.getBlog_id(), data);
    }

    // 设置过期时间
    public void setExpireTime(String keyName, String expireKeyName) {
        // 首先查找过期时间
        String expire = stringRedisTemplate.opsForValue().get(expireKeyName);
        int expireTime; // 过期时间
        // 如果expire为空，从数据库中查找
        if (expire == null) {
            expireTime = myRedisService.getByKeyName(expireKeyName);
            // 将过期时间存放到Redis中
            stringRedisTemplate.opsForValue().set(expireKeyName, String.valueOf(expireTime));
        } else {
            // 将expire 转成int
            expireTime = Integer.parseInt(expire);
        }
        stringRedisTemplate.expire(keyName, expireTime, TimeUnit.DAYS);
    }
}
