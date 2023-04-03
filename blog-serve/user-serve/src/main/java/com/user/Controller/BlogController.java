package com.user.Controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.*;
import com.user.Utils.Constant;
import com.user.Utils.EntityChange;
import com.user.Utils.Result;
import com.user.Vo.*;


import com.user.entity.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RequestMapping("blog")
@Controller
@ResponseBody
public class BlogController {
    private Result result = new Result();
    @Autowired
    private BlogService blogService;
    @Autowired
    BlogDataService blogDataService;
    @Autowired
    private LabelBlogService labelBlogService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private CategoryBlogService categoryBlogService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private mdService mdService;

    @Autowired
    private MyRedisService myRedisService;

    // redis
   @Autowired
   private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BlogVoService blogVoService;


    private ObjectMapper objectMapper = new ObjectMapper();

    // 添加文章
    @PostMapping
    public Result addBlog(@RequestBody BlogVo blogVo) throws IOException {

       long blogId =  blogService.addBlog(blogVo);
       int categoryId = categoryService.addCategory(blogVo);
       int labelId = labelService.addLabel(blogVo);

       boolean flag4 = labelBlogService.add(labelId,blogId);
       boolean flag5 = blogDataService.add(blogId,0,0);
       boolean flag6 = categoryBlogService.add(categoryId,blogId);
       boolean flag7 = mdService.addMarkDown(blogId,blogVo.getMd_content());

       if(blogId != 0 && categoryId != 0 && labelId != 0 && flag4 && flag5 && flag6 && flag7) {
           // 将新添加的博客添加到Redis中
           EntityChange entityChange = new EntityChange();
           blogVo.setBlog_id(blogId);
           blogVo.setLabel_id(labelId);
           blogVo.setCategory_id(categoryId);
           blogVo.setBlog_date(new Timestamp(System.currentTimeMillis()));

           // 将blogVo添加到elasticsearch中
          // addDocument(blogVo);

           BlogNoData blogNoData = entityChange.ChangeBlogVo_ToNoBlog(blogVo);
           String s = objectMapper.writeValueAsString(blogNoData);
           stringRedisTemplate.opsForValue().set(Constant.blog + blogId,s);
           // 设置过期时间
           setExpireTime(Constant.blog + blogId,Constant.blogExpireTime);
           // 将博客的浏览量和点赞量存到Redis中
           BlogData blogData = new BlogData();
           blogData.setBlog_id(blogId);
           blogData.setBlog_likes(0);
           blogData.setBlog_browse(0);
           String data = objectMapper.writeValueAsString(blogData);
           stringRedisTemplate.opsForValue().set(Constant.blogData + blogId,data);
           // 删除Redis中总的博客数
           stringRedisTemplate.delete(Constant.blogTotal);
           // 删除Redis中的label分类和category分类
           stringRedisTemplate.delete(Constant.labels);
           stringRedisTemplate.delete(Constant.categorys);
           // 删除blogIdList中的数据
           stringRedisTemplate.delete(Constant.blogIdList);
           return result.ok(blogId);
       } else {
           try {
               blogService.deleteById(blogId);
               categoryService.deleteById(categoryId);
               labelService.delete(labelId);
               labelBlogService.delete(labelId,blogId);
               categoryBlogService.delete(categoryId,blogId);
               blogDataService.deleteById(blogId);
               mdService.deleteById(blogId);
           } catch (Exception e) {
                e.printStackTrace();
           }
           return result.fail("博客发布失败");
       }

    }

    // 查询首页可以显示的文章
    @GetMapping("home")
    public Result getHomeBlog(@RequestParam int page, @RequestParam int size) throws JsonProcessingException {
        List<BlogVo> blogShow = blogVoService.getBlogShow();
        int listSize = blogShow.size();
        BlogVoPage blogVoPage = new BlogVoPage();
        List<BlogVo> blogVoList = new ArrayList<>();
        blogVoPage.setTotal(listSize);
        int first,last;
        if((page - 1) * size > listSize) return result.ok();
        first = (page - 1) * size;
        if(page * size < listSize) last = page * size;
        else last = listSize;
        for(int i = first; i < last; i++) {
            long blog_id = blogShow.get(i).getBlog_id();
           // Redis中查找，
            String json = stringRedisTemplate.opsForValue().get(Constant.blog + blog_id);
            String json2 = stringRedisTemplate.opsForValue().get(Constant.blogData + blog_id);
            if(json == null) {
                int label_id = blogShow.get(i).getLabel_id();
                int category_id = blogShow.get(i).getCategory_id();
                String label_name = labelService.getLabelById(label_id);
                String category_name = categoryService.getCategoryById(category_id);
                blogShow.get(i).setLabel_name(label_name);
                blogShow.get(i).setCategory_name(category_name);

                // 实体类转换
                EntityChange entityChange = new EntityChange();
                BlogNoData blogNoData = entityChange.ChangeBlogVo_ToNoBlog(blogShow.get(i));

                // 实体类存入Redis中
                String s = objectMapper.writeValueAsString(blogNoData);
                stringRedisTemplate.opsForValue().set(Constant.blog + blog_id,s);
                setExpireTime(Constant.blog +blog_id,Constant.blogExpireTime);
                blogVoList.add(blogShow.get(i));

            } else {
                // 将json转成blogNodata
                BlogVo blogVo = objectMapper.readValue(json, BlogVo.class);
                BlogData blogData = objectMapper.readValue(json2, BlogData.class);
                blogVo.setBlog_browse(blogData.getBlog_browse());
                blogVo.setBlog_likes(blogData.getBlog_likes());
                blogVoList.add(blogVo);
            }
        }
        blogVoPage.setBlogVoList(blogVoList);
        return result.ok(blogVoPage);
    }

    // 修改文章信息
    @PutMapping
    public Result updateBlog(@RequestBody BlogVo blogVo) throws IOException {
        boolean updateBlog = blogService.updateBlog(blogVo);
        boolean updateCategory = categoryService.
                updateCategory(blogVo.getCategory_id(),blogVo.getCategory_name());
        boolean updateLabel = labelService.updateLabel(blogVo.getLabel_id(),blogVo.getLabel_name());
        if(updateBlog && updateCategory && updateLabel) {
            // 全量更新elasticsearch中的字段
           // updateDocument(blogVo);

            // 将实体类序列化
            EntityChange entityChange = new EntityChange();
            BlogNoData blogNoData = entityChange.ChangeBlogVo_ToNoBlog(blogVo);
            String s = objectMapper.writeValueAsString(blogNoData);
            stringRedisTemplate.opsForValue().set(Constant.blog + blogNoData.getBlog_id(),s);
            // 设置过期时间
            setExpireTime(Constant.blog + blogNoData.getBlog_id(),Constant.blogExpireTime);
            // 删除Redis中的label和category
            stringRedisTemplate.delete(Constant.categorys);
            stringRedisTemplate.delete(Constant.labels);
            return result.ok();
        } else {
            return result.fail("修改失败");
        }

    }



    // 修改文章是否显示到首页
    @PutMapping("/updateShow/{blog_id}/{isShow}")
    public Result updataShow(@PathVariable long blog_id, @PathVariable boolean isShow) {
        UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("blog_id",blog_id);
        updateWrapper.set("blog_show",isShow);
        boolean update = blogService.update(null, updateWrapper);
        if(update) {
            stringRedisTemplate.delete(Constant.blog + blog_id);
            return result.ok();
        } else {
            return result.fail("修改失败");
        }

    }

    // 修改文章访问量和点赞量 只有当文章被点赞时才执行此操作
    @PutMapping("/data")
    public Result updateBlogData(@RequestBody BlogData blogData) throws IOException {
        blogDataService.updateBlogData(blogData);
        String data = objectMapper.writeValueAsString(blogData);
        stringRedisTemplate.opsForValue().set(Constant.blogData + blogData.getBlog_id(),data);

        // 更新elasticsearch中的数据
       // updatePart(blogData);
        return  result.ok();
    }
    // 分页查询
    @GetMapping("/getBlog/{page}/{size}")
    public Result getBlogByPage(@PathVariable Integer page,@PathVariable Integer size) {
        IPage<BlogVo> blogVoIPage = blogVoService.selectBlogByPage(page, size);
        for (int i = 0; i < blogVoIPage.getRecords().size(); i++) {
            BlogVo record = blogVoIPage.getRecords().get(i);
            int label_id = record.getLabel_id();
            int category_id = record.getCategory_id();
            String label = labelService.getLabelById(label_id);
            String category = categoryService.getCategoryById(category_id);
            blogVoIPage.getRecords().get(i).setCategory_name(category);
            blogVoIPage.getRecords().get(i).setLabel_name(label);
        }
       return  result.ok(blogVoIPage);
    }

    // 根据博客ID获取博客相关信息,获取一次浏览量增加一次
    @GetMapping("/getBlogById/{blog_id}")
    public Result getBlogById(@PathVariable long blog_id) throws IOException {
        String json = stringRedisTemplate.opsForValue().get("blog" + blog_id);
       String blogDataJson = stringRedisTemplate.opsForValue().get("blogData" + blog_id);
        if(json == null) {
            // 从数据库中查询
            BlogVo blogVo = blogVoService.selectBlogById(blog_id);
            if(blogVo == null) {
                return result.fail("服务器异常");
            }
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
            stringRedisTemplate.opsForValue().set("blog" + blog_id,s);
            // 设置过期时间
            setExpireTime(Constant.blog + blog_id, Constant.blogExpireTime);

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

            return result.ok(blogVo);
        } else {
            // 将json字符串转成java
            BlogVo blogVo = objectMapper.readValue(json, BlogVo.class);
            BlogData blogData = objectMapper.readValue(blogDataJson, BlogData.class);

            // 将博客的访问量加1
            int browse = blogData.getBlog_browse();
            // 访问量加1
            blogVo.setBlog_browse(browse+1);
            blogVo.setBlog_likes(blogData.getBlog_likes());
            blogData.setBlog_browse(browse + 1);
            // 修改数据库
            updateBlogData(blogData);
            String data = objectMapper.writeValueAsString(blogData);
            stringRedisTemplate.opsForValue().set(Constant.blogData + blog_id,data);

            return result.ok(blogVo);
        }
    }

    // 获取博客总数
    @GetMapping("/getBlogSum")
    public Result getBlogSum() {
        // 先从Redis中查询所有blog的总数，如果Redis中没有，那么再从数据库中查询
        // 1.从Redis中获取blog总数
        String blogTotal = stringRedisTemplate.opsForValue().get(Constant.blogTotal);
        // 如果Redis中不存在，从数据库中查询之后存入到Redis
        if(blogTotal == null) {
            // 从数据库中查询总数
            int size = blogService.list().size();
            String stringSize = size + "";
            // 将总数存到Redis中
            stringRedisTemplate.opsForValue().set("blogTotal", stringSize);
            return result.ok(size);
        } else {
            int size = Integer.parseInt(blogTotal);
            return result.ok(size);
        }

    }

    // 删除博客，当初没写，现在累死自己
    @DeleteMapping("deleteBlog")
    public Result deleteBlog(@RequestParam long blog_id,@RequestParam int category_id,
                             @RequestParam int label_id) throws IOException {
        // 首先删除blog表中的相关数据
        blogService.deleteById(blog_id);

        // 删除blogData中的相关数据
        blogDataService.deleteById(blog_id);
        // 删除label_blog中的相关数据
        // 查找label_id对应多少个blog，如果对应一个，删除label表中的数据
        int labelSum = labelBlogService.getLabelSum(label_id);
        if(labelSum == 1) {
            labelService.delete(label_id);
        }
        labelBlogService.delete(label_id,blog_id);
        // 删除category_blog中的相关数据
        // 查找category_id对应多少个blog，如果对应一个，删除category表中的数据
        int categorySum = categoryBlogService.getCategorySum(category_id);
        if(categorySum == 1) {
            categoryService.deleteById(category_id);
        }
        categoryBlogService.delete(category_id,blog_id);
        // 删除索引
     //   deleteDocument(blog_id);
        // 删除Redis中的数据
        stringRedisTemplate.delete(Constant.blog + blog_id);
        stringRedisTemplate.delete(Constant.blogData +blog_id);
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constant.labels))) {
            stringRedisTemplate.delete(Constant.labels);
        }
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Constant.categorys))) {
            stringRedisTemplate.delete(Constant.categorys);
        }
        // 删除blogIdList中的数据
        stringRedisTemplate.delete(Constant.blogIdList);
        return result.ok(true);
    }

    // 获取此博客的上一篇博客和下一篇博客
    @GetMapping("preAndNextBlog")
    public Result preAndNextBlog(@RequestParam long blog_id) {
        // 首先从Redis中blogIdList
        int size = stringRedisTemplate.opsForList().size(Constant.blogIdList).intValue();
        PreAndNextBlog preAndNextBlog = new PreAndNextBlog();
        if(size == 0) {
            // Redis中不存在，从MySQL中查询
            List<Long> allBlogId = blogService.getAllBlogId();
            // 将数据存入到Redis中
            List<String> stringList = new ArrayList<>();
            int listSize = allBlogId.size();
            for(int i = 0; i < listSize; i++) {
                String json = allBlogId.get(i).toString();
                stringList.add(json);
                if (allBlogId.get(i) == blog_id) {
                    // 获取当前ID的上一条ID
                    int preIndex = (i - 1 + listSize) % listSize;
                    preAndNextBlog.setPreBlogId(allBlogId.get(preIndex));
                    // 获取当前ID的下一条ID
                    int nextIndex = ( i + 1) % listSize;
                    preAndNextBlog.setNextBlogId(allBlogId.get(nextIndex));
                }
            }
            // 根据ID获取标题
            preAndNextBlog.setPreTitle(blogService.getTitleById(preAndNextBlog.getPreBlogId()));
            preAndNextBlog.setNextTitle(blogService.getTitleById(preAndNextBlog.getNextBlogId()));
            // 将数据层存入到Redis中
            stringRedisTemplate.opsForList().leftPushAll(Constant.blogIdList,stringList);
            // 返回数据
            return result.ok(preAndNextBlog);
        }
        else {
            // 从Redis中获取数据
            List<String> range = stringRedisTemplate.opsForList().range(Constant.blogIdList, 0, -1);
            for(int i = 0; i < size; i++) {
                if(blog_id == Long.parseLong(range.get(i))) {
                    // 获取上一条数据
                    int preIndex = (i - 1 + size) % size;
                    preAndNextBlog.setPreBlogId(Long.parseLong(range.get(preIndex)));
                    // 获取下一条数据
                    int nextIndex =(i + 1) % size;
                    preAndNextBlog.setNextBlogId(Long.parseLong(range.get(nextIndex)));
                    break;
                }
            }
            // 根据ID获取标题
            preAndNextBlog.setPreTitle(blogService.getTitleById(preAndNextBlog.getPreBlogId()));
            preAndNextBlog.setNextTitle(blogService.getTitleById(preAndNextBlog.getNextBlogId()));
            return result.ok(preAndNextBlog);
        }


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

    // 只能使用MySQL代替了，elasticsearch太重了，服务器带不动
    @GetMapping("/query")
    public Result queryFromMysql(@RequestParam String info, @RequestParam int page,
                              @RequestParam int size)  {
        IPage<Blog> blogIPage = blogService.queryFromMysql(info, page, size);
        return result.ok(blogIPage);
    }






    // 下面的部分不能够使用了，因为elasticsearch所占用的内存实在是太大了，2G服务器跑不动

   /* RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.26.129:9200")
    ));*/

    // 更新部分字段(浏览量和点赞量)
    /*public void updatePart(BlogData blogData) throws IOException {
        UpdateRequest request = new UpdateRequest("blog", String.valueOf(blogData.getBlog_id()));
        request.doc(
                "blog_browse",blogData.getBlog_browse(),
                        "blog_likes",blogData.getBlog_likes()
        );
        client.update(request,RequestOptions.DEFAULT);
    }*/


    // 全量更新文档
   /* public void updateDocument(BlogVo blogVo) throws IOException {
        BlogDoc blogDoc  = new BlogDoc(blogVo);
        IndexRequest request = new IndexRequest("blog").id(String.valueOf(blogDoc.getId()));
        String json = objectMapper.writeValueAsString(blogDoc);
        request.source(json, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }*/
    // 插入文档
   /* public void addDocument(BlogVo blogVo) throws IOException {
        BlogDoc blogDoc  = new BlogDoc(blogVo);
        IndexRequest request = new IndexRequest("blog").id(String.valueOf(blogDoc.getId()));
        String json = objectMapper.writeValueAsString(blogDoc);
        request.source(json, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }*/

    // 删除文档
   /* public void deleteDocument(long blog_id) throws IOException{
        DeleteRequest request = new DeleteRequest("blog", String.valueOf(blog_id));
        DeleteResponse response = client.delete(request,RequestOptions.DEFAULT);
    }*/
    // 全文检索功能
    /*@GetMapping("/query")
    public Result queryFormES(@RequestParam String info, @RequestParam int page,
                              @RequestParam int size) throws IOException {
        SearchRequest request = new SearchRequest("blog");
        request.source()
                .query(QueryBuilders.matchQuery("all",info));
        request.source().sort("blog_likes", SortOrder.DESC);
        request.source().sort("blog_browse", SortOrder.DESC);
        request.source().from((page - 1) * size).size(size);
        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        // 文档数据
        SearchHit[] hits = searchHits.getHits();
        List<BlogDoc> blogDocList = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            BlogDoc blogDoc = objectMapper.readValue(json, BlogDoc.class);
            blogDocList.add(blogDoc);
        }
        BlogDocPage blogDocPage = new BlogDocPage(blogDocList, total);
        return result.ok(blogDocPage);

    }*/

    // 自动补全功能
   /* @GetMapping("/suggestion")
    public Result getSuggestion(@RequestParam String info, @RequestParam int size) throws
            IOException {
        SearchRequest request = new SearchRequest("blog");
        request.source().suggest(new SuggestBuilder().addSuggestion(
                "MySuggestions",
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix(info)
                        .skipDuplicates(true)
                        .size(size)
        ));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析结果
        Suggest suggest = response.getSuggest();
        // 根据补全查询名称,
       CompletionSuggestion suggestion =  suggest.getSuggestion("MySuggestions");
        List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();
        List<String> stringList = new ArrayList<>();
        for (CompletionSuggestion.Entry.Option option : options) {
            String s = option.getText().toString();
            stringList.add(s);
        }
        return result.ok(stringList);
    }*/



}
