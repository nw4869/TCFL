package com.nightwind.tcfl.controller;

import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.Comment;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.tool.BaseTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by wind on 2014/12/10.
 */
public class ArticleController {


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



    //我的帖子
    static private ArrayList<Article> sMyArticleList = new ArrayList<>();
    //我的收藏
    static private ArrayList<Article> sCollectionList = new ArrayList<>();

    private static int sClassifyCount = 8;
    //分类帖子Array Map用于adapter的数据集
    private static HashMap<Integer, ArrayList<Article>> sArticleListsMap = new HashMap<>();
    //总帖子列表Array用于通过articleID索引查询
//    private static ArrayList<Article> mArticles = new ArrayList<>();
    private static HashMap<Integer, Article> sArticleMap = new HashMap<>();

    static {
        for (int i = 0; i < getClassifyCount(); i++) {
            ArrayList<Article> articleList = new ArrayList<Article>();
            articleList.add(null);
            sArticleListsMap.put(i, articleList);
        }
        genRandArticle(50);
    }

    /**
     * 我的收藏
     */
    public static ArrayList<Article> getCollectionList() {
        return sCollectionList;
    }
    public static boolean addCollection(Article article) {
        if (sCollectionList.contains(article)) {
            return false;
        } else {
            sCollectionList.add(article);
            article.setCollected(true);
            return true;
        }
    }
    public static boolean removeCollection(Article article) {
        if (!sCollectionList.contains(article)) {
            return false;
        } else {
            sCollectionList.remove(article);
            article.setCollected(false);
            return true;
        }
    }

    /**
     *
     *我的帖子
     *
     */
    public static ArrayList<Article> getMyArticleList() {
        return sMyArticleList;
    }
    public static boolean addMyArticle(Article article) {
        if (sMyArticleList.contains(article)) {
            return false;
        } else {
            sMyArticleList.add(article);
            return true;
        }
    }
    public static boolean removeMyArticle(Article article) {
        if (!sMyArticleList.contains(article)) {
            return false;
        } else {
            sMyArticleList.remove(article);
            sArticleMap.remove(article);
            return true;
        }
    }

    /**
     * 文章（帖子）
     */
    public static void genRandArticle(int articleCount) {

        Random random = new Random();

        for (int i = 0; i < articleCount; i++) {
            User user;
            do {
                user = UserController.getUser(random.nextInt(UserController.getUserCount()) + 1);
            } while (user == null);


            Article article = new Article();


            article.setId(getArticleCount());
            article.setTitle("Title " + (i));
//            article.setNewsAbstract("Abstract " + (i));
            article.setContent("I am Content " + (i) + "  I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n " + "I am Content\n ");
//            article.setUid(user.getUid());
//            article.setUsername("UserName " + (i));
            article.setUsername(user.getUsername());
//            String date = String.format("2014-01-%02d 21:21", i);
            Calendar cld = Calendar.getInstance();
            cld.add(Calendar.YEAR, -1);
            cld.add(Calendar.DATE, i);
            String date = BaseTools.getDateFormat().format(cld.getTime());

            article.setDateTime(date);
//            article.setCommentNum((i + 1));

//                Bitmap bitmap;
//                if (i < 8) {
//                    bitmap = toRoundBitmap(BitmapFactory.decodeResource(getResources(), drawables[i % 8]));
//                } else {
////                bitmap = listItems[i % 8].getImg();
//                    bitmap = myListItems.get(i%8).getImg();
//                }
//                article.setImg(bitmap);

            //评论：
            int n = random.nextInt(10);
            ArrayList<Comment> commentEntities = new ArrayList<>();
            //0是帖子内容
            commentEntities.add(null);
            for (int j = 1; j <= n; j++) {
                Comment comment = new Comment();
                User user1;
                do {
                    user1 = UserController.getUser(random.nextInt(UserController.getUserCount()) + 1);
                } while (user1 == null);
                comment.setUsername(user1.getUsername());
//                String date1 = String.format("2014-02-%02d 21:21", j);
                Calendar cld1 = Calendar.getInstance();
                cld1.setTime(cld.getTime());
                cld1.add(Calendar.MINUTE, j);
                String date1 = BaseTools.getDateFormat().format(cld1.getTime());
                comment.setDateTime(date1);
                comment.setContent("Hello World" + random.nextInt(10));
//                    comment.setImg(myListItems.get(random.nextInt(NUM_ITEM)).getImg());
                commentEntities.add(comment);
            }
//            article.setCommentNum(n);
            article.setCommentEntities(commentEntities);

            //1/7的概率收藏该帖子
            if (random.nextInt(7) == 0) {
                addCollection(article);
            }

            //加入我的帖子
            if (user.getUid() == UserController.getSelfUser().getUid()) {
                addMyArticle(article);
            }
//            mArticles.add(article);
            sArticleMap.put(i, article);
            int classify = random.nextInt(getClassifyCount());
            sArticleListsMap.get(classify).add(article);
        }
    }

    public static int getClassifyCount() {
        return sClassifyCount;
    }

    public static void setClassifyCount(int sClassifyCount) {
        ArticleController.sClassifyCount = sClassifyCount;
    }

    public static int getArticleCount() {
//        return mArticles.size();
        return sArticleMap.size();
    }

    public static ArrayList<Article> getMyListItem(int index) {

        return sArticleListsMap.get(index);
    }

    static public Article getArticle(int articleId) {
//        return mArticles.get(articleId);
        return sArticleMap.get(articleId);
    }

    static public boolean addArticle(int classify, Article article) {
        sArticleListsMap.get(classify).add(article);
        article.getCommentEntities().add(null);

        //
        article.setId(getArticleCount());
//        mArticles.add(article);
        sArticleMap.put(getArticleCount(), article);
        addMyArticle(article);
        return true;
    }
}
