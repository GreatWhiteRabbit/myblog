package com.user.Utils;

import com.user.Vo.BlogNoData;
import com.user.Vo.BlogVo;
import com.user.Vo.HomeShowBlogVo;
import com.user.Vo.LinkVo;
import com.user.entity.DeleteReply;
import com.user.entity.Link;
import com.user.entity.Reply;


public class EntityChange {
    public EntityChange(){}
    public BlogNoData ChangeBlogVo_ToNoBlog(BlogVo BlogVo) {
        BlogNoData blogNoData = new BlogNoData();
        blogNoData.setBlog_id(BlogVo.getBlog_id());
        blogNoData.setBlog_title(BlogVo.getBlog_title());
        blogNoData.setBlog_date(BlogVo.getBlog_date());
        blogNoData.setBlog_cover(BlogVo.getBlog_cover());
        blogNoData.setBlog_description(BlogVo.getBlog_description());
        blogNoData.setUser_id(BlogVo.getUser_id());
        blogNoData.setBlog_show(BlogVo.isBlog_show());
        blogNoData.setCategory_id(BlogVo.getCategory_id());
        blogNoData.setLabel_id(BlogVo.getLabel_id());
        blogNoData.setBlog_content(BlogVo.getBlog_content());
        blogNoData.setMd_content(BlogVo.getMd_content());
        blogNoData.setLabel_name(BlogVo.getLabel_name());
        blogNoData.setCategory_name(BlogVo.getCategory_name());
        return  blogNoData;
    }
    // 将Reply中的内容复制到DeleteReply中
    public DeleteReply ChangeReply_ToDeleteReply(Reply reply) {
        DeleteReply deleteReply = new DeleteReply();
        deleteReply.setReply_content(reply.getReply_content());
        deleteReply.setReply_time(reply.getReply_time());
        deleteReply.setReply_user(reply.getReply_user());
        deleteReply.setResponse_user(reply.getResponse_user());
        deleteReply.setSecond_name(reply.getSecond_name());
        deleteReply.setFirst_name(reply.getFirst_name());
        deleteReply.setBlog_id(reply.getBlog_id());
        deleteReply.setReplyed_id(reply.getReplyed_id());
        return deleteReply;
    }

    public LinkVo changeLink_ToLinkVo(Link link) {
        LinkVo linkVo = new LinkVo();
        linkVo.setLink(link.getLink());
        linkVo.setName(link.getName());
        linkVo.setInfo(link.getInfo());
        linkVo.setImgurl(link.getImgurl());
        linkVo.setId(link.getId());
        return linkVo;
    }

    public static HomeShowBlogVo changeBlogVo_ToHomeShoBlogVo(BlogVo blogVo) {
        HomeShowBlogVo homeShowBlogVo = new HomeShowBlogVo();
        homeShowBlogVo.setBlog_id(blogVo.getBlog_id());
        homeShowBlogVo.setBlog_cover(blogVo.getBlog_cover());
        homeShowBlogVo.setBlog_date(blogVo.getBlog_date());
        homeShowBlogVo.setBlog_title(blogVo.getBlog_title());
        homeShowBlogVo.setBlog_description(blogVo.getBlog_description());
        homeShowBlogVo.setCategory_name(blogVo.getCategory_name());
        homeShowBlogVo.setLabel_name(blogVo.getLabel_name());

        return homeShowBlogVo;
    }

    public static BlogVo changeHomeShowBlogVo_ToBlogVo(HomeShowBlogVo homeShowBlogVo) {
        BlogVo blogVo = new BlogVo();
        blogVo.setBlog_id(homeShowBlogVo.getBlog_id());
        blogVo.setBlog_cover(homeShowBlogVo.getBlog_cover());
        blogVo.setBlog_title(homeShowBlogVo.getBlog_title());
        blogVo.setBlog_description(homeShowBlogVo.getBlog_description());
        blogVo.setLabel_name(homeShowBlogVo.getLabel_name());
        blogVo.setBlog_date(homeShowBlogVo.getBlog_date());
        blogVo.setCategory_name(homeShowBlogVo.getCategory_name());

        return blogVo;

    }
}
