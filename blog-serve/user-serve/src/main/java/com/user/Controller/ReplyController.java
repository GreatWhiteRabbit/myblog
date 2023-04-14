package com.user.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Service.DeleteReplyService;
import com.user.Service.MyRedisService;
import com.user.Service.ReplyService;
import com.user.Utils.EntityChange;
import com.user.Utils.Result;
import com.user.Utils.Constant;
import com.user.Vo.ReplyList;
import com.user.Vo.ReplyListVo;
import com.user.entity.DeleteReply;
import com.user.entity.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequestMapping("reply")
@Controller
@ResponseBody
public class ReplyController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReplyService replyService;

    private Result result = new Result();

    @Autowired
    private DeleteReplyService deleteReplyService;

    @Autowired
    private MyRedisService myRedisService;

    // 在博客下面添加留言或者回复
    @PostMapping("blog")
    public Result addBlogReply(@RequestBody Reply reply) throws JsonProcessingException {
        // 如果 reply.replyed_id不为0，说明是回复，首先判断被回复的留言是否已经删除，如果删除，不能回复
        // 如果没有被删除，那么将留言添加到被回复队列中并且设置过期时间
        if (beDeleted(reply.getReplyed_id())) {
            return result.fail("false");
        }
        // 将数据插入到MySQL中
        Reply reply1 = replyService.addBlogReply(reply);
        if(reply1 == null) {
            return result.fail("回复失败");
        }
        String json = objectMapper.writeValueAsString(reply1);
        // 如果是被回复的，那么插入到被回复的队列中
        if(reply1.getReplyed_id() != 0) {
            insertRedis(json,Constant.replyMessage + reply1.getReplyed_id(),
                    Constant.replyMessageExpireTime);
            // 将相应的被回复的留言ID永久插入到zset中方便查找
            stringRedisTemplate.opsForZSet().add(Constant.beenReplyed,
                    String.valueOf(reply1.getReplyed_id()), reply.getReplyed_id());
        }
        // 否则将消息插入到blog_id list中
        else {
            insertRedis(json,Constant.blogReply +reply1.getBlog_id(),
                    Constant.blogReplyExpireTime);
        }
        // 将新添加的留言推送给管理员,不需要设置过期时间，用list存储，取数据用pop
        stringRedisTemplate.opsForList().leftPush(Constant.newAdminMessage,json);
        return result.ok();
    }

    // 在留言区发表留言或者回复留言
    @PostMapping()
    public Result addReply(@RequestBody Reply reply) throws JsonProcessingException {
        // 如果 reply.replyed_id不为0，说明是回复，首先判断被回复的留言是否已经删除，如果删除，不能回复
        // 如果没有被删除，那么将留言添加到被回复队列中并且设置过期时间
        if (beDeleted(reply.getReplyed_id())) {
            return result.fail("false");
        }
        // 将数据插入到MySQL中
        Reply reply1 = replyService.addReply(reply);
        if(reply1 == null) {
            return result.fail("回复失败");
        }
        String json = objectMapper.writeValueAsString(reply1);
        // 如果是被回复的，那么插入到被回复的队列中
        if(reply1.getReplyed_id() != 0) {
            insertRedis(json,Constant.replyMessage + reply1.getReplyed_id(),
                    Constant.replyMessageExpireTime);
            // 将相应的被回复的留言ID永久插入到zset中方便查找
            stringRedisTemplate.opsForZSet().add(Constant.beenReplyed,
                    String.valueOf(reply1.getReplyed_id()), reply.getReplyed_id());
        }
        // 将消息插入到留言区队列中
        else {
            insertRedis(json,Constant.message,
                    Constant.messageExpireTime);
        }
        // 将新添加的留言推送给管理员,不需要设置过期时间，用list存储，取数据用pop
        stringRedisTemplate.opsForList().leftPush(Constant.newAdminMessage,json);
        return result.ok();
    }

    //获取相对应的博文下面的留言
    @GetMapping("/getBlogReply")
    public Result getBlogReply(@RequestParam long blog_id, @RequestParam int page,@RequestParam int size) throws JsonProcessingException {
        //首先从Redis中获取对应博文下面的留言，如果Redis中没有，再从数据库中查找
        //首先获取对应的博文下面留言的条数
        int listSize =stringRedisTemplate.opsForList().size(Constant.blogReply + blog_id).intValue();
        // 如果Redis中不存在，从MySQL中获取
        if(listSize == 0) {
            // 从MySQL中获取全部满足条件的数据
            List<Reply> replyList = replyService.getListByBlogId(blog_id);
            int getSize = replyList.size(); // 得到获取replyList的长度
            if(getSize == 0) return result.ok();
            if((page - 1) * size > getSize) {
                return result.fail("已经没有数据了");
            }
            int firstData = (page-1) * size; // 获取第一个元素
            int lastData; // 获取到的最后一个元素
            // 比较MySQL中获取的数据长度和所需要的数据长度
            if(getSize >= page * size) {
                lastData = page * size;
            } else {
                lastData = getSize;
            }
            // 将数据插入到Redis中并获取返回网络请求的数据
            ReplyListVo replyListVo = insertDataIntoRedis(Constant.blogReply + blog_id,
                    firstData, lastData, Constant.blogReplyExpireTime, getSize, replyList);
            return result.ok( replyListVo );
        }
        // 将Redis中获取到的数据返回
        else {
            // range中0表示第一个元素，-1表示最后一个元素，1表示第二个元素
            // 判断要请求的数据是否超过list的限制
            if ((page - 1) * size > listSize) {
                return result.fail("已经没有数据了");
            }
            int firstData = (page-1) * size; // 获取第一个元素
            int lastData; // 获取到的最后一个元素
            if (listSize >= page * size) {
                lastData = page * size;
            } else {
                lastData = listSize;
            }
            // 从Redis中获取相对应的数据
            ReplyListVo replyListVo = getRangeFromRedisList(Constant.blogReply + blog_id,
                    firstData, lastData, listSize);


            return result.ok(replyListVo);
        }
    }

    //获取留言板块中的留言
    @GetMapping("/getReply")
    public Result getReply(@RequestParam int page,@RequestParam int size) throws JsonProcessingException {
        // 先从Redis中获取相对应的数据
        int listSize = stringRedisTemplate.opsForList().size(Constant.message).intValue();
        // 如果Redis中没有，那么从数据库中查询
        if(listSize == 0) {
            // 从MySQL中获取所有与博文ID无关的留言且被回复的留言ID为0
            List<Reply> replyList = replyService.getListReply();
            // 获取replyList的长度，作为返回值之一
            int getSize = replyList.size();
            // 如果网络请求的数据长度范围超出了replyList的长度，返回错误信息
            if(getSize == 0) return result.ok();
            if(getSize < (page - 1) * size) {
                return result.fail("没有更多数据了");
            }
            int firstData = (page-1) * size; // 获取第一个元素
            int lastData; // 获取到的最后一个元素
            if(getSize >= page * size) {
                lastData = page * size;
            } else {
                lastData = getSize;
            }
            // 将数据插入到Redis中并获取返回网络请求的数据
            ReplyListVo replyListVo = insertDataIntoRedis(Constant.message,
                    firstData, lastData, Constant.messageExpireTime, getSize, replyList);
            return result.ok( replyListVo );
        }
        // 如果Redis中存在数据，返回Redis中的数据
        else {
            if ((page - 1) * size > listSize) {
                return result.fail("已经没有数据了");
            }
            int firstData = (page-1) * size; // 获取第一个元素
            int lastData; // 获取到的最后一个元素
            if (listSize >= page * size) {
                lastData = page * size;
            } else {
                lastData = listSize;
            }
            // 从Redis中抽取数据
            ReplyListVo replyListVo = getRangeFromRedisList(Constant.message,
                    firstData, lastData, listSize);
            return result.ok(replyListVo);
        }

    }

    //删除留言（用户可以删除自己发表过的留言）
    @DeleteMapping()
    public Result deleteReply(@RequestBody Reply reply) {

        if(reply.getReply_id() == 0) {
            return result.fail("留言已被删除，不能执行删除操作");
        }
        // 删除留言
        replyService.deleteReply(reply);
        // 将删除的留言保留到删除留言表中
        EntityChange entityChange = new EntityChange();
        DeleteReply deleteReply = entityChange.ChangeReply_ToDeleteReply(reply);
        deleteReplyService.add(deleteReply);
        // 将被删除的留言ID存放到Redis中
        stringRedisTemplate.opsForZSet().add(Constant.deletedMessage,
                String.valueOf(reply.getReply_id()), reply.getReply_id());
        return result.ok();
    }

    // 管理员删除留言（管理员可以删除任何留言）
    @DeleteMapping("/{authorization}")
    public Result adminDeleteReply(@PathVariable String authorization,@RequestBody Reply reply) {
        // 管理员删除用户留言需要向验证管理员的权限
        if(authorization.equals("3039632334")) {
            // 删除留言
            boolean isDelete = replyService.deleteReply(reply);
            if(isDelete == false) {
                return result.fail("删除失败");
            }
            // 将删除的留言保留到删除留言表中
            EntityChange entityChange = new EntityChange();
            DeleteReply deleteReply = entityChange.ChangeReply_ToDeleteReply(reply);
            deleteReplyService.add(deleteReply);
            // 将被删除的留言ID存放到Redis中
            stringRedisTemplate.opsForValue().set(Constant.deletedMessage +reply.getReply_id(),"delete");
            return result.ok();

        } else {
            return result.fail("删除失败");
        }
    }

    // 管理员查看最新发表的留言，查看之后Redis中相应信息被删除了
    @GetMapping("/getNewMessage/{page}/{size}")
    public Result getNewMessage(@PathVariable("page") int page, @PathVariable("size") int size) throws JsonProcessingException {
        // 从新添加的留言队列中获取数据
        int messageSize = stringRedisTemplate.opsForList().size(Constant.newAdminMessage).intValue();
        if(messageSize == 0) {
            return result.ok();
        } else {
            ReplyListVo replyListVo = new ReplyListVo();
            replyListVo.setTotal(messageSize);
            ReplyList replyList = new ReplyList();
            List<ReplyList> replyLists = new ArrayList<>();
            int lastSize ;
            if(messageSize >= 10) {
                lastSize = 10;
            } else {
                lastSize = messageSize;
            }
            for(int i = 0; i < lastSize; i++) {
                String json = stringRedisTemplate.opsForList().rightPop(Constant.newAdminMessage);
                Reply reply = objectMapper.readValue(json, Reply.class);
                replyList.setMessage(reply);
                replyLists.add(replyList);
            }
            replyListVo.setReplyList(replyLists);
            return result.ok(replyListVo);
        }
    }

    // 查找被回复的留言
    public List<Reply> getReplyedId(@RequestParam long replyed_id) throws JsonProcessingException {
        Double score = stringRedisTemplate.opsForZSet().
                score(Constant.beenReplyed, String.valueOf(replyed_id));
        // 如果score为null，说明zset中没有这个值，直接返回null
        if(score == null) {
            return null;
        }
        // 如果有值，先从Redis被回复的队列中查找，如果找不到再从MySQL中查找存放到Redis中并返回
        else {
            // 从Redis中查找
            int listSize = stringRedisTemplate.opsForList().
                    size(Constant.replyMessage + replyed_id).intValue();

            if(listSize == 0) {
                // 从MySQL中获取数据
                List<Reply> listByReplyedId = replyService.getListByReplyedId(replyed_id);
                int size = listByReplyedId.size();
                // 将数据插入到Redis中
                List<String> stringList = new ArrayList<>();
                for(int i = 0; i < size; i++) {
                    String json = objectMapper.writeValueAsString(listByReplyedId.get(i));
                    stringList.add(json);
                }
                stringRedisTemplate.opsForList().leftPushAll(Constant.replyMessage + replyed_id,stringList);
                // 设置过期时间
                setExpireTime(Constant.replyMessage + replyed_id,Constant.replyMessageExpireTime);
                // 将数据返回
                return  listByReplyedId;
            } else {
                // 从Redis中获取数据并返回
                List<String> range = stringRedisTemplate.opsForList().
                        range(Constant.replyMessage + replyed_id, 0, -1);
                List<Reply> replyList = new ArrayList<>();
                for(int i = 0; i < listSize; i++) {
                    Reply reply = objectMapper.readValue(range.get(i),Reply.class);
                    if(beDeleted(reply.getReply_id())) {
                        reply.setReply_id(0);
                        reply.setReply_user(0);
                        reply.setReply_content("内容已被删除");
                        reply.setFirst_name("");
                        reply.setSecond_name("");
                    }
                    replyList.add(reply);
                }
                return replyList;
            }
        }

    }

    // 判断要回复的留言是否已经被删除,如果被删除了，返回true
    public boolean beDeleted(long replyed_id) {
        Double score = stringRedisTemplate.opsForZSet().
                score(Constant.deletedMessage, String.valueOf(replyed_id));
        // 拿不到数据说明没有被删除
        if(score == null) {
            return false;
        }
        return true;
    }

    // 将新添加的留言数据插入到Redis队列中
    public void insertRedis(String json,String keyName,String expireKeyName) {
        stringRedisTemplate.opsForList().leftPush(keyName,json);
        setExpireTime(keyName,expireKeyName);
    }

    // 从Redis队列中获取指定范围的元素并返回
    public ReplyListVo getRangeFromRedisList(String keyName, int firstData,
                                      int lastData, int listSize) throws JsonProcessingException {
        // 从Redis中的list队列中获取key为keyName的指定范围的数据
        List<String > range = stringRedisTemplate.opsForList().range(keyName,firstData,lastData-1);
        // 将字符串转换成实体类
       List<ReplyList> replyLists = new ArrayList<>();
        for(int i = 0; i < range.size(); i++) {
            ReplyList replyList = new ReplyList();
            Reply reply = objectMapper.readValue(range.get(i), Reply.class);
            long reply_id = reply.getReply_id();
            // 从Redis中查看此条留言是否被删除
           // 如果留言已经被删除
            if(beDeleted(reply_id)) {
                reply.setReply_id(0);
                reply.setReply_user(0);
                reply.setReply_content("内容已被删除");
                reply.setFirst_name("");
                reply.setSecond_name("");
                replyList.setReplyed(null);
            } else {
                List<Reply> replyedId = getReplyedId(reply_id);
                replyList.setReplyed(replyedId);
            }

            replyList.setMessage(reply);
            replyLists.add(replyList);
        }
        ReplyListVo replyListVo = new ReplyListVo();
        replyListVo.setTotal(listSize);
        replyListVo.setReplyList(replyLists);
        return replyListVo;
    }

    // 将从数据库中获取到的留言数据插入到Redis中
    public ReplyListVo insertDataIntoRedis(String keyName, int firstData, int lastData,
                                           String expireKeyName, int getSize, List<Reply> replyList)
            throws JsonProcessingException {
        // 将从MySQL中获取到的数据存储到Redis中
        List<String> stringList = new ArrayList<>();
        ReplyListVo replyListVo = new ReplyListVo();
        // 网络请求需要返回的数据
        ReplyList returnList = new ReplyList();
        List<ReplyList> lists = new ArrayList<>();
        for(int i = 0; i < getSize; i++) {
            // 将实体类转成json字符串
            String json = objectMapper.writeValueAsString(replyList.get(i));
            stringList.add(json);
            // 将网络请求需要返回的数据封装到returnList中
            if( i >= firstData && i < lastData) {
                returnList.setMessage(replyList.get(i));
                returnList.setReplyed(getReplyedId(replyList.get(i).getReply_id()));
                lists.add(returnList);
            }
        }
        // 将json字符串存到Redis中
        stringRedisTemplate.opsForList().leftPushAll(keyName,stringList);
        // 为键值设置过期时间
        setExpireTime(keyName,expireKeyName);
        // 返回数据
        replyListVo.setTotal(getSize);
        replyListVo.setReplyList(lists);
        return replyListVo;
    }

    // 设置过期时间
    public void setExpireTime(String keyName,String expireKeyName) {

        // 首先查找过期时间
        String expire = stringRedisTemplate.opsForValue().get(expireKeyName);
        int expireTime; // 过期时间
        // 如果expire为空，从数据库中查找
        if(expire == null) {
            expireTime = myRedisService.getByKeyName(expireKeyName);
            System.out.println(expireTime);
            // 将过期时间存放到Redis中
            stringRedisTemplate.opsForValue().set(expireKeyName, String.valueOf(expireTime));
        } else {
            // 将expire 转成int
            expireTime = Integer.parseInt(expire);
        }

        stringRedisTemplate.expire(keyName,expireTime,TimeUnit.DAYS);
    }
}
