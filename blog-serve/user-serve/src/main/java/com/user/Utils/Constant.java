package com.user.Utils;

public  class  Constant {

    public static final String labels = "labels"; // 存储所有的labels  需要设置过期时间
    public static final String labelsExpireTime = "labelsExpireTime"; // 存储labels的过期时间
    public static final String blogData = "blogData"; // 博客浏览量数据  不占用内存很小，而且需要经常访问，需要设置过期时间

    public static final String categorys = "categorys"; // 存储所有的category包含的博客 需要设置过期时间
    public static final String categorysExpireTime = "categorysExpireTime"; // 存储categorys的过期时间
    public static final String blogTotal = "blogTotal"; //所有博客的总数  不需要设置过期时间，占用内存很小
    public static final String blog = "blog"; // 博客 需要设置过期时间，因为当管理员博客发布的越来越多，
    // 占用内存也会越来越大
    public static final String blogExpireTime = "blogExpireTime"; // 存储博客的过期时间
    public static final String adminInfo = "adminInfo"; // 管理员信息，不需要设置过期时间，只占用几B内存

    public static final String emailCode = "emailCode"; // 邮箱验证码，在程序中已经设置了180m的过期时间

    public static final String blogReply = "blogReply"; // 博文下面的回复信息，需要设置过期时间，博文下面的
    // 回复也会越来多，程序中没有设置删除的功能，所以需要设置过期时间
    public static final String blogReplyExpireTime = "blogReplyExpireTime"; // 存储博客回复消息的过期时间

    public static final String message = "message" ; // 留言区下面的回复信息，需要设置过期时间，因为当留言过多的
    // 时候，由于没有设置手动删除功能，所以如果不设置过期时间，那么信息会一直保存在Redis中，占用极大地内存。

    public static final String messageExpireTime = "messageExpireTime" ; // 存储message的过期时间
    public static final String deletedMessage = "deletedMessage"; // 已经被删除的留言ID，不需要设置过
    // 期时间，因为当一条留言被删除之后，当查询留言ID的与此条被删除的留言ID相等时，那么这个被删除的留言ID就可以从R
    // edis中被删除了，不占用Redis很大的空间

    public static final String newAdminMessage = "newAdminMessage"; // 将新添加的
    // 留言推送给管理员，不需要设置过期时间，因为当管理员获取到一些新添加的留言之后，该留言就被删除了，不会很影响内存

    public static final String replyMessage = "replyMessage"; // 存储被回复ID的相关留言 需要设置过期时间

    public static final String replyMessageExpireTime = "replyMessageExpireTime"; // 存储replyMessage过期时间

    public static final String beenReplyed = "beenReplyed"; // 存储有哪些留言的ID被回复了

    public static final String links = "links"; // 存储友链

    public static final String systemInfo = "systemInfo"; // 存储网站系统公告

    public static final String systemInfoExpireTime = "systemInfoExpireTime"; // 存储网站系统公告过期时间

    public static final String homeImage = "homeImage"; // 存储首页轮播图片信息

    public static final String homeImageExpireTime = "homeImageExpireTime"; // 存储首页轮播图片过期时间

    public static final String myBlogBrowse = "myBlogBrowse"; // 博客访问量

    public static final String Essay = "essay"; // 随笔

    public static final String EssayExpireTime = "EssayExpireTime"; // 随笔过期时间

    public static final String project = "project"; // 随笔

    public static final String projectExpireTime = "projectExpireTime"; // 随笔过期时间

    public static final String blogIdList = "blogIdList"; // 记录博客的ID

    public static final String suggestion = "suggestion"; // 网站用户记录

    public static final String updateInfo = "updateInfo"; // 网站更新记录

    public static final String homeShowBlogListId = "homeShowBlogListId"; // 博客首页固定
}
