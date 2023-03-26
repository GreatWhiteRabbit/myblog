package com.user.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.*;
import com.user.Utils.EntityChange;
import com.user.Utils.Result;
import com.user.Vo.*;
import com.user.entity.BlogData;
import com.user.entity.Category;
import com.user.entity.CategoryBlog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("category")
@Controller
@ResponseBody
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryBlogService categoryBlogService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private LabelService labelService;

    @Autowired
    private BlogVoService blogVoService;

    @Autowired
    private BlogDataService blogDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MyRedisService myRedisService;

    private Result result = new Result();

    // 获取category的名称和对应的category名称下有哪些blog
    @GetMapping("/list")
    public Result getCategoryList() throws JsonProcessingException {
        // 首先从Redis中获取label的集合，如果没有，再从数据库中查询存取到Redis中
        List<String> range = stringRedisTemplate.opsForList().range(Constant.categorys, 0, -1);
        assert range != null;
        if (range.size() == 0) {
            // 从数据库中获取categoryID和categoryname
            List<Category> categoryList = categoryService.list();
            List<CategoryListBlogs> categoryListBlogsList = new ArrayList<>();
            // 根据相对应的categoryID获取categoryID ==》 blog_id 的一对多的关系
            for (int i = 0; i < categoryList.size(); i++) {
                CategoryListBlogs categoryListBlogs = new CategoryListBlogs();
                String category_name = categoryList.get(i).getCategory_name();
                int category_id = categoryList.get(i).getCategory_id();
                categoryListBlogs.setCategory_id(category_id);
                categoryListBlogs.setCategory_name(category_name);
                // 根据categoryId获取所有的blogID
                List<CategoryBlog> blogIdByCategoryIdList = categoryBlogService.
                        getBlogIdByCategoryId(category_id);
                // 获取blogIdByCategoryIdList中的所有blogId
                List<Long> longList = new ArrayList<>();
                for (int j = 0; j < blogIdByCategoryIdList.size(); j++) {
                    longList.add(blogIdByCategoryIdList.get(j).getBlog_id());
                }
                // 将 longList存到categoryListBlogs中
                categoryListBlogs.setBlogList(longList);
                // 将categoryListBlogs存入到List<CategoryListBlogs>中
                categoryListBlogsList.add(categoryListBlogs);
            }
            // 将List<CategoryListBlogs>转换成List<String》
            List<String> stringList = new ArrayList<>();
            for (int k = 0; k < categoryListBlogsList.size(); k++) {
                String value = objectMapper.writeValueAsString(categoryListBlogsList.get(k));
                stringList.add(value);
            }
            // 将stringList存入到Redis中
            stringRedisTemplate.opsForList().leftPushAll(Constant.categorys, stringList);
            // 设置过期时间
            setExpireTime(Constant.categorys,Constant.categorysExpireTime);
            // 将获取到的数据返回
            return result.ok(categoryListBlogsList);

        }
        else {
            // 将json数据从range中获取出来并且解析成List<CategoryListBlogs>
            List<CategoryListBlogs> categoryListBlogsList = new ArrayList<>();
            for (int i = 0; i < range.size(); i++) {
                // 从range集合中获取字符串
                String s = range.get(i);
                // 将字符串解析成CategoryListBlogs对象
                CategoryListBlogs categoryListBlogs = objectMapper.readValue(s, CategoryListBlogs.class);
                // 将CategoryListBlogs对象存储到集合中以集合的形式返回
                categoryListBlogsList.add(categoryListBlogs);

            }
            return result.ok(categoryListBlogsList);
        }
    }

    // 通过category_id、页面大小、页数获取blog数据
    @GetMapping("/{category_id}/{page}/{size}")
    public Result getBlogByCategoryId_Page_Size(@PathVariable int category_id, @PathVariable int page,
                                             @PathVariable int size) throws JsonProcessingException{
        List<CategoryListBlogs> list = getList();
        List<BlogVo> blogVoList = new ArrayList<>();
        int listSize = list.size();
        if(listSize < (page-1) * size) {
            return result.fail("获取失败");
        }
        for(int i = 0; i < listSize; i++) {
            if(list.get(i).getCategory_id() == category_id) {
                // 获取label_id标签中的blog_id
                List<Long> blogIdList = list.get(i).getBlogList();
                int newSize =blogIdList.size();
                // 查询到的最后一个博客数据
                int lastIndex;
                if(newSize >= page * size) {
                    lastIndex = page * size;
                } else {
                    lastIndex = newSize;
                }
                for(int j = (page - 1) * size; j < lastIndex; j++) {
                    blogVoList.add(getBlogById(blogIdList.get(j)));
                }
                return result.ok(blogVoList);
            }
        }
        return result.fail("获取失败");
    }



    // 这个函数是getLabelList的内部调用方法，不需要将实体类转成object类，在本controller方法中还需要用到
    public List<CategoryListBlogs> getList() throws JsonProcessingException {
        // 首先从Redis中获取label的集合，如果没有，再从数据库中查询存取到Redis中
        List<String> range = stringRedisTemplate.opsForList().range(Constant.categorys, 0, -1);
        assert range != null;
        if (range.size() == 0) {
            // 从数据库中获取categoryID和categoryname
            List<Category> categoryList = categoryService.list();
            List<CategoryListBlogs> categoryListBlogsList = new ArrayList<>();
            // 根据相对应的categoryID获取categoryID ==》 blog_id 的一对多的关系
            for (int i = 0; i < categoryList.size(); i++) {
                CategoryListBlogs categoryListBlogs = new CategoryListBlogs();
                String category_name = categoryList.get(i).getCategory_name();
                int category_id = categoryList.get(i).getCategory_id();
                categoryListBlogs.setCategory_id(category_id);
                categoryListBlogs.setCategory_name(category_name);
                // 根据categoryId获取所有的blogID
                List<CategoryBlog> blogIdByCategoryIdList = categoryBlogService.
                        getBlogIdByCategoryId(category_id);
                // 获取blogIdByCategoryIdList中的所有blogId
                List<Long> longList = new ArrayList<>();
                for (int j = 0; j < blogIdByCategoryIdList.size(); j++) {
                    longList.add(blogIdByCategoryIdList.get(j).getBlog_id());
                }
                // 将 longList存到categoryListBlogs中
                categoryListBlogs.setBlogList(longList);
                // 将categoryListBlogs存入到List<CategoryListBlogs>中
                categoryListBlogsList.add(categoryListBlogs);
            }
            // 将List<CategoryListBlogs>转换成List<String》
            List<String> stringList = new ArrayList<>();
            for (int k = 0; k < categoryListBlogsList.size(); k++) {
                String value = objectMapper.writeValueAsString(categoryListBlogsList.get(k));
                stringList.add(value);
            }
            // 将stringList存入到Redis中
            stringRedisTemplate.opsForList().leftPushAll(Constant.categorys, stringList);
            setExpireTime(Constant.categorys,Constant.categorysExpireTime);
            // 将获取到的数据返回
            return categoryListBlogsList;

        }
        else {
            // 将json数据从range中获取出来并且解析成List<CategoryListBlogs>
            List<CategoryListBlogs> categoryListBlogsList = new ArrayList<>();
            for (int i = 0; i < range.size(); i++) {
                // 从range集合中获取字符串
                String s = range.get(i);
                // 将字符串解析成CategoryListBlogs对象
                CategoryListBlogs categoryListBlogs = objectMapper.readValue(s, CategoryListBlogs.class);
                // 将CategoryListBlogs对象存储到集合中以集合的形式返回
                categoryListBlogsList.add(categoryListBlogs);

            }
            return categoryListBlogsList;
        }
    }

    // 通过blog_id获取blog的相关数据，此方法只供controller内部使用，不提供外部接口
    public BlogVo getBlogById(long blog_id) throws JsonProcessingException {
        String json = stringRedisTemplate.opsForValue().get("blog" + blog_id);
        String blogDataJson = stringRedisTemplate.opsForValue().get("blogData" + blog_id);
        if(json == null) {
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
            stringRedisTemplate.opsForValue().set(Constant.blog + blog_id,s);
            setExpireTime(Constant.blog +blog_id,Constant.blogExpireTime);
            // 将博客的访问量加1
            BlogData blogData = new BlogData();
            blogData.setBlog_id(blog_id);
            blogData.setBlog_likes(blogVo.getBlog_likes());
            // 访问量加1
            int browse = blogVo.getBlog_browse();
            blogVo.setBlog_browse(browse+1);
            blogData.setBlog_browse(browse + 1);
            // 修改数据库
            updateBlogData(blogData);
            String data = objectMapper.writeValueAsString(blogData);
            stringRedisTemplate.opsForValue().set(Constant.blogData + blog_id,data);

            return blogVo;
        } else {
            // 将json字符串转成java
            BlogVo blogVo = objectMapper.readValue(json, BlogVo.class);
            BlogData blogData = objectMapper.readValue(blogDataJson, BlogData.class);

            // 将博客的访问量加1
            int browse = blogData.getBlog_browse();
            // 访问量加1
            blogVo.setBlog_browse(browse+1);
            blogVo.setBlog_likes(blogVo.getBlog_likes());
            blogData.setBlog_browse(browse + 1);
            // 修改数据库
            updateBlogData(blogData);
            String data = objectMapper.writeValueAsString(blogData);
            stringRedisTemplate.opsForValue().set(Constant.blogData + blog_id,data);

            return blogVo;
        }
    }

    // 修改博客的访问数据，此方法只供controller内部使用，不提供外部接口
    public void updateBlogData( BlogData blogData) throws JsonProcessingException {
        blogDataService.updateBlogData(blogData);
        String data = objectMapper.writeValueAsString(blogData);
        stringRedisTemplate.opsForValue().set(Constant.blogData + blogData.getBlog_id(),data);
    }

    // 设置过期时间
    public void setExpireTime(String keyName,String expireKeyName) {
        // 首先查找过期时间
        String expire = stringRedisTemplate.opsForValue().get(expireKeyName);
        int expireTime; // 过期时间
        // 如果expire为空，从数据库中查找
        if(expire == null) {
            expireTime = myRedisService.getByKeyName(expireKeyName);
            // 将过期时间存放到Redis中
            stringRedisTemplate.opsForValue().set(expireKeyName, String.valueOf(expireTime));
        } else {
            // 将expire 转成int
            expireTime = Integer.parseInt(expire);
        }
        stringRedisTemplate.expire(keyName,expireTime, TimeUnit.DAYS);
    }
}

