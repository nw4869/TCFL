package com.nightwind.tcfl.tool;


import android.content.Intent;

import com.nightwind.tcfl.bean.ChatMsgEntity;
import com.nightwind.tcfl.bean.CommentItem;
import com.nightwind.tcfl.bean.MyListItem;
import com.nightwind.tcfl.bean.NewsClassify;
import com.nightwind.tcfl.bean.NewsEntity;
import com.nightwind.tcfl.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Dummy {
//	public static ArrayList<NewsClassify> getData() {
//		ArrayList<NewsClassify> newsClassify = new ArrayList<NewsClassify>();
//		NewsClassify classify = new NewsClassify();
//		classify.setId(0);
//		classify.setTitle("推荐");
//		newsClassify.add(classify);
//		classify = new NewsClassify();
//		classify.setId(1);
//		classify.setTitle("热点");
//		newsClassify.add(classify);
//		classify = new NewsClassify();
//		classify.setId(2);
//		classify.setTitle("数码");
//		newsClassify.add(classify);
//		classify = new NewsClassify();
//		classify.setId(3);
//		classify.setTitle("杭州");
//		newsClassify.add(classify);
//		classify = new NewsClassify();
//		classify.setId(4);
//		classify.setTitle("社会");
//		newsClassify.add(classify);
//		classify = new NewsClassify();
//		classify.setId(5);
//		classify.setTitle("娱乐");
//		newsClassify.add(classify);
//		classify = new NewsClassify();
//		classify.setId(6);
//		classify.setTitle("科技");
//		newsClassify.add(classify);
//		classify = new NewsClassify();
//		classify.setId(7);
//		classify.setTitle("汽车");
//		newsClassify.add(classify);
//		return newsClassify;
//	}
//
//	public static ArrayList<NewsEntity> getNewsList() {
//		ArrayList<NewsEntity> newsList = new ArrayList<NewsEntity>();
//		for(int i =0 ; i < 10 ; i++){
//			NewsEntity news = new NewsEntity();
//			news.setId(i);
//			news.setNewsId(i);
//			news.setCollectStatus(false);
//			news.setCommentNum(i + 10);
//			news.setInterestedStatus(true);
//			news.setLikeStatus(true);
//			news.setReadStatus(false);
//			news.setNewsCategory("推荐");
//			news.setNewsCategoryId(1);
//                news.setTitle("可以用谷歌眼镜做的10件酷事：导航、玩游戏");
//                List<String> url_list = new ArrayList<String>();
//                if(i%2 == 1){
//				String url1 = "http://infopic.gtimg.com/qq_news/digi/pics/102/102066/102066094_400_640.jpg";
//				String url2 = "http://infopic.gtimg.com/qq_news/digi/pics/102/102066/102066096_400_640.jpg";
//				String url3 = "http://infopic.gtimg.com/qq_news/digi/pics/102/102066/102066099_400_640.jpg";
//				news.setPicOne(url1);
//				news.setPicTwo(url2);
//				news.setPicThr(url3);
//				url_list.add(url1);
//				url_list.add(url2);
//				url_list.add(url3);
//			}else{
//				news.setTitle("AA用车:智能短租租车平台");
//				String url = "http://r3.sinaimg.cn/2/2014/0417/a7/6/92478595/580x1000x75x0.jpg";
//				news.setPicOne(url);
//				url_list.add(url);
//			}
//			news.setPicList(url_list);
//			news.setPublishTime(Long.valueOf(i));
//			news.setReadStatus(false);
//			news.setSource("手机腾讯网");
//			news.setSummary("腾讯数码讯（编译：Gin）谷歌眼镜可能是目前最酷的可穿戴数码设备，你可以戴着它去任何地方（只要法律法规允许或是没有引起众怒），作为手机的第二块“增强现实显示屏”来使用。另外，虽然它仍未正式销售，但谷歌近日在美国市场举行了仅限一天的开放购买活动，价格则为1500美元（约合人民币9330元），虽然仍十分昂贵，但至少可以满足一些尝鲜者的需求，也预示着谷歌眼镜的公开大规模销售离我们越来越近了。");
//			news.setMark(i);
//			if(i == 4){
//				news.setTitle("部落战争强势回归");
//				news.setLocal("推广");
//				news.setIsLarge(true);
//				String url = "http://imgt2.bdstatic.com/it/u=3269155243,2604389213&fm=21&gp=0.jpg";
//				news.setPicOne(url);
//				url_list.clear();
//				url_list.add(url);
//			}else{
//				news.setIsLarge(false);
//			}
//			if(i == 2){
//				news.setComment("评论部分，说的非常好。");
//			}
//			newsList.add(news);
//		}
//		return newsList;
//	}

    private static String[] IMGURLLIST;

    private static String[] SLIDEIMGURLLIST;

    public static String[] getImgURLList() {
        if (IMGURLLIST == null) {
            ArrayList<String> imgList = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                imgList.add("http://192.168.1.123/img/conan" + i + ".jpg");
            }
            IMGURLLIST = imgList.toArray(new String[imgList.size()]);
        }
        return IMGURLLIST;
    }
    public static String[] getSlideImgURLList() {
        if (SLIDEIMGURLLIST == null) {
            ArrayList<String> imgList = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                imgList.add("http://192.168.1.123/img/slide/" + i + ".jpg");
            }
            SLIDEIMGURLLIST = imgList.toArray(new String[imgList.size()]);
        }
        return SLIDEIMGURLLIST;
    }


    /**
     * 文章（帖子）
     */
    private static HashMap<Integer, ArrayList<MyListItem>> mNewsLists = new HashMap<>();
    public static ArrayList<MyListItem> getMyListItem(int index) {

        ArrayList<MyListItem> listItems = mNewsLists.get(index);

        if (listItems != null) {
            return listItems;
        } else if (index > 0) {
            listItems = new ArrayList<>(mNewsLists.get(0));
            mNewsLists.put(index, listItems);
            return listItems;
        }

        listItems = new ArrayList<>();

        final int NUM_ITEM = 30;
        Random random = new Random();

        //0可能是滚动图片
        listItems.add(null);
        for (int i = 1; i <= NUM_ITEM; i++) {
            User user;
            do {
                user = Dummy.getUser(random.nextInt(ORI_USER_NUM) + 1);
            } while(user == null);

            MyListItem listItem = new MyListItem();

            listItem.setTitle("Title " + (i));
//            listItem.setNewsAbstract("Abstract " + (i));
            listItem.setContent("Content " + (i));
            listItem.setUid(user.getUid());
//            listItem.setUsername("UserName " + (i));
            listItem.setUsername(user.getUsername());
            String date = String.format("2014-01-%02d 21:21", i);
            listItem.setDateTime(date);
//            listItem.setCommentNum((i + 1));

//                Bitmap bitmap;
//                if (i < 8) {
//                    bitmap = toRoundBitmap(BitmapFactory.decodeResource(getResources(), drawables[i % 8]));
//                } else {
////                bitmap = listItems[i % 8].getImg();
//                    bitmap = myListItems.get(i%8).getImg();
//                }
//                listItem.setImg(bitmap);

            //评论：
            int n = random.nextInt(10);
            ArrayList<CommentItem> commentItems = new ArrayList<>();
            //0是帖子内容
            commentItems.add(null);
            for (int j = 1; j <= n; j++) {
                CommentItem commentItem = new CommentItem();
                User user1;
                do {
                    user1 = Dummy.getUser(random.nextInt(ORI_USER_NUM) + 1);
                } while(user1 == null);
                commentItem.setUsername(user1.getUsername());
                String date1 = String.format("2014-02-%02d 21:21", j);
                commentItem.setDateTime(date1);
                commentItem.setContent("Hello World" + random.nextInt(10));
//                    commentItem.setImg(myListItems.get(random.nextInt(NUM_ITEM)).getImg());
                commentItems.add(commentItem);
            }
            listItem.setCommentNum(n);
            listItem.setCommentItems(commentItems);
            listItems.add(listItem);
        }
        mNewsLists.put(index, listItems);
        return listItems;
    }


    /**
     * User
     */
    static private final int ORI_USER_NUM = 8;

    static {
        uidMap  = new HashMap<>();
        usernameMap = new HashMap<>();
        getUsersList();
        getMyListItem(0);
        getMsg(1, 2);
    }

    static private ArrayList<User> usersList;
    static private HashMap<Integer, User> uidMap;
    static private HashMap<String, User> usernameMap;
    static public ArrayList<User> getUsersList() {
        if (usersList != null) {
            return usersList;
        } else {
            usersList = new ArrayList<>();

            Random random = new Random();
            for (int i = 1; i <= ORI_USER_NUM; i++) {
                int uid = i;
                User user = new User();
                user.setUid(uid);
                user.setUsername("user" + user.getUid());
                user.setLevel(random.nextInt(100) + 1);
                user.setAge(random.nextInt(60) + 1);
                user.setInfo("Hello World");
                user.setSex(random.nextInt(2));
                user.setEdu(random.nextInt(User.eduNum));
                user.setWork("IT");
                user.setHobby("Programming");

                //头像
                user.setAvaterUrl("http://192.168.1.123/img/conan" + uid + ".jpg");

                //添加好友
                int m = random.nextInt(ORI_USER_NUM / 2) + 2;
                for (int j = 0; j < m; j++) {
                    int friendUid = random.nextInt(ORI_USER_NUM) + 1;
                    if (friendUid == uid || !user.addFriend(friendUid)) {
                        j--;
                        continue;
                    }
                }

                //在线状态
                user.setOnline(uid % 2 == 0);

                usersList.add(user);
                uidMap.put(uid, user);
                usernameMap.put(user.getUsername(), user);
            }
        }
        return usersList;
    }

    static public User getUser(int uid) {
        return uidMap.get(uid);
    }
    static public User getUser(String username) {
        return usernameMap.get(username);
    }
    public static User getSelfUser() {
        return getUser(1);
    }

    private static ArrayList<ChatMsgEntity> msgs;
    public static ArrayList<ChatMsgEntity> getMsg(int mUid1, int mUid2) {
        if (msgs != null) {
            return msgs;
        } else {
            msgs = new ArrayList<>();

            String[] msgArray = new String[]{"  孩子们，要好好学习，天天向上！要好好听课，不要翘课！不要挂科，多拿奖学金！三等奖学金的争取拿二等，二等的争取拿一等，一等的争取拿励志！",
                    "姚妈妈还有什么吩咐...",
                    "还有，明天早上记得跑操啊，不来的就扣德育分！",
                    "德育分是什么？扣了会怎么样？",
                    "德育分会影响奖学金评比，严重的话，会影响毕业",
                    "哇！学院那么不人道？",
                    "你要是你不听话，我当场让你不能毕业！",
                    "姚妈妈，我知错了(- -我错在哪了...)"};

            String[]dataArray = new String[]{"2012-09-01 18:00", "2012-09-01 18:10",
                    "2012-09-01 18:11", "2012-09-01 18:12",
                    "2012-09-01 18:14", "2012-09-01 18:35",
                    "2012-09-01 18:40", "2012-09-01 18:50"};
            final int COUNT = 8;


            for(int i = 0; i < COUNT; i++) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setDate(dataArray[i]);
                if (i % 2 == 0)
                {
                    entity.setName(Dummy.getUser(2).getUsername());
                    entity.setMsgType(true);
                }else{
                    entity.setName(Dummy.getUser(1).getUsername());
                    entity.setMsgType(false);
                }

                entity.setText(msgArray[i]);
                msgs.add(entity);
            }
        }
        return msgs;
    }






	
	/** mark=0 ：推荐 */
	public final static int mark_recom = 0;
	/** mark=1 ：热门 */
	public final static int mark_hot = 1;
	/** mark=2 ：首发 */
	public final static int mark_frist = 2;
	/** mark=3 ：独家 */
	public final static int mark_exclusive = 3;
	/** mark=4 ：收藏 */
	public final static int mark_favor = 4;

}
