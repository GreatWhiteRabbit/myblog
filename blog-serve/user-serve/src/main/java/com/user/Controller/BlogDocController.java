package com.user.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.*;
import com.user.Vo.BlogDoc;
import com.user.Vo.BlogVo;
import com.user.entity.*;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("index")
@Controller
@ResponseBody
public class BlogDocController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogDataService blogDataService;
    @Autowired
    private LabelBlogService labelBlogService;

    @Autowired
    private LabelService labelService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryBlogService categoryBlogService;

    @Autowired
     private mdService mdService;

    @Autowired
            private ObjectMapper objectMapper;

    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://192.168.26.129:9200")
    ));

    // 批量添加文档
    @GetMapping()
    public void bulkRequest() throws IOException {
        List<Blog> list = blogService.list();
        BulkRequest request = new BulkRequest();
        for(int i = 0; i < list.size(); i++) {
            // 通过blog_id查找访问量，label_name,category_name
            long blog_id = list.get(i).getBlog_id();
            BlogData blogData = blogDataService.getByBlog(blog_id);
            // 从label中查找对应的blog_id对应的label_name
            labelBlog labelId = labelBlogService.getLabelId(blog_id);
            int label_id = labelId.getLabel_id();
            String label_name = labelService.getLabelById(label_id);

            CategoryBlog categoryId = categoryBlogService.getCategoryId(blog_id);
            // 通过category_id查找category_name
            int category_id = categoryId.getCategory_id();
            String category_name = categoryService.getCategoryById(category_id);

            String content = mdService.getContent(blog_id);
            // 将数据封装到blogVo中

            BlogVo blogVo = change(list.get(i), blogData, label_id, category_id, label_name, category_name, content);

            BlogDoc  blogDoc  = new BlogDoc(blogVo);

            String json = objectMapper.writeValueAsString(blogDoc);

            request.add(new IndexRequest("blog")
                    .id(String.valueOf(blogDoc.getId()))
                    .source(json, XContentType.JSON)
            );

        }
        String s = client.bulk(request, RequestOptions.DEFAULT).buildFailureMessage();
        System.out.println(s);
    }

    // 删除索引
    @DeleteMapping()
    public void deleteIndex() throws IOException {
        AcknowledgedResponse acknowledgedResponse = client.indices().delete(
                new DeleteIndexRequest("blog"),RequestOptions.DEFAULT
        );
    }

    // 创建索引
    @GetMapping("addIndex")
    public void add() {
        CreateIndexRequest request = new CreateIndexRequest("blog");
        request.mapping(mapping,XContentType.JSON);
    }

    String mapping = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": {\n" +
            "        \"text_anlyzer\": {     \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": \"py\"\n" +
            "        },\n" +
            "        \"completion_analyzer\": {  \n" +
            "          \"tokenizer\": \"keyword\",\n" +
            "          \"filter\": \"py\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"py\": {\n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"blog_title\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"text_anlyzer\",\n" +
            "        \"search_analyzer\": \"ik_smart\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"blog_content\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"blog_cover\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"blog_date\":{\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"blog_decription\":{\n" +
            "        \"type\": \"text\",\n" +
            "         \"analyzer\": \"text_anlyzer\",\n" +
            "        \"search_analyzer\": \"ik_smart\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"user_id\":{\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"blog_show\":{\n" +
            "        \"type\": \"boolean\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"blog_likes\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"blog_browse\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"category_id\":{\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category_name\":{\n" +
            "        \"type\": \"text\"\n" +
            "        , \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"lable_id\":{\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"label_name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"md_content\":{\n" +
            "         \"type\": \"text\",\n" +
            "         \"analyzer\": \"text_anlyzer\",\n" +
            "        \"search_analyzer\": \"ik_smart\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"all\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"text_anlyzer\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"suggestion\":{   \n" +
            "          \"type\": \"completion\",\n" +
            "          \"analyzer\": \"completion_analyzer\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";

    public BlogVo change(Blog blog,BlogData blogData, int label_id,
                         int category_id,String label_name,String category_name,String content) {
        BlogVo blogVo = new BlogVo();
        blogVo.setBlog_id(blog.getBlog_id());
        blogVo.setBlog_title(blog.getBlog_title());
        blogVo.setBlog_date(blog.getBlog_date());
        blogVo.setBlog_content(blog.getBlog_content());
        blogVo.setBlog_cover(blog.getBlog_cover());
        blogVo.setBlog_description(blog.getBlog_description());
        blogVo.setUser_id(blog.getUser_id());
        blogVo.setBlog_show(blog.isBlog_show());

        blogVo.setBlog_likes(blogData.getBlog_likes());
        blogVo.setBlog_browse(blogData.getBlog_browse());

        blogVo.setCategory_id(category_id);
        blogVo.setCategory_name(category_name);

        blogVo.setLabel_id(label_id);
        blogVo.setLabel_name(label_name);

        blogVo.setMd_content(content);
        return blogVo;
    }
}
