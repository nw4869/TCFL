package com.nightwind.tcfl.controller;

import android.content.Context;
import android.util.Log;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.Comment;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.server.ServerConfig;
import com.nightwind.tcfl.tool.BaseTools;
import com.nightwind.tcfl.tool.encryptionUtil.RSAUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by wind on 2014/12/10.
 */
public class ArticleController {

    private final Context mContext;
    private final UserController mUserController;

    private static String[] SLIDEIMGURLLIST;

    static private int numPerPage = 20;
    //我的帖子
    static private ArrayList<Article> sMyArticleList = new ArrayList<>();
    //我的收藏
    static private ArrayList<Article> sCollectionList = new ArrayList<>() ;

    private static int sClassifyCount = 8;
    //分类帖子Array Map用于adapter的数据集
    private static HashMap<Integer, ArrayList<Article>> sArticleListsMap = new HashMap<>();
    //总帖子列表Array用于通过articleID索引查询
//    private static ArrayList<Article> mArticles = new ArrayList<>();
    private static HashMap<Integer, Article> sArticleMap = new HashMap<>();

    public ArticleController(Context context) {
        mContext = context;
        mUserController = new UserController(mContext);
//        if (sArticleMap.size() == 0) {
//            genRandArticle(6*sClassifyCount);
//        }
        if (sCollectionList.size() == 0) {
            sCollectionList.add(null);
        }
        if (sMyArticleList.size() == 0) {
            sMyArticleList.add(null);
        }
    }


    public static int getNumPerPage() {
        return numPerPage;
    }

    public static void setNumPerPage(int numPerPage) {
        ArticleController.numPerPage = numPerPage;
    }

//    private static String[] IMGURLLIST;


//    public static String[] getImgURLList() {
//        if (IMGURLLIST == null) {
//            ArrayList<String> imgList = new ArrayList<>();
//            for (int i = 1; i <= 8; i++) {
//                imgList.add(Auth.SERVER_REMOTE + "img/conan" + i + ".jpg");
//            }
//            IMGURLLIST = imgList.toArray(new String[imgList.size()]);
//        }
//        return IMGURLLIST;
//    }
    public static String[] getSlideImgURLList() {
        if (SLIDEIMGURLLIST == null) {
            ArrayList<String> imgList = new ArrayList<>();
            for (int i = 1; i <= 8; i++) {
                imgList.add( ServerConfig.getServer() + "img/slide/" + i + ".jpg");
            }
            SLIDEIMGURLLIST = imgList.toArray(new String[imgList.size()]);
        }
        return SLIDEIMGURLLIST;
    }



//    static {
//        genRandArticle(50);
//    }

    /**
     * 我的收藏
     */
    public ArrayList<Article> getCollectionList() {
        return sCollectionList;
    }
    public boolean addCollection(Article article) {
//        if (sCollectionList.contains(article)) {
//            return false;
//        } else {
//            sCollectionList.add(article);
//            article.setCollected(true);
//            return true;
//        }
        return collectionToServer(article.getId(), true);
    }
    public boolean removeCollection(Article article) {
//        if (!sCollectionList.contains(article)) {
//            return false;
//        } else {
//            sCollectionList.remove(article);
//            article.setCollected(false);
//            return true;
//        }
        return collectionToServer(article.getId(), false);
    }

    public boolean collectionToServer(int articleId, boolean isAdd) {

        Auth auth = new Auth(mContext);
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/Collection";
        HttpPost request = new HttpPost(urlStr);
        List<NameValuePair> params = new ArrayList<>();
        if (token == null) {
            System.out.println("本地token不存在");
            return false;
        }

        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return false;
        }

        String selfUsername = auth.getUsername();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", selfUsername));
        params.add(new BasicNameValuePair("articleId", String.valueOf(articleId)));
        params.add(new BasicNameValuePair("isAdd", String.valueOf(isAdd)));

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(responseMsg);
                if(jsonObject.getBoolean("success")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     *我的帖子
     *
     */
    public ArrayList<Article> getMyArticleList() {
        return sMyArticleList;
    }
    public boolean addMyArticle(Article article) {
        if (sMyArticleList.contains(article)) {
            return false;
        } else {
            sMyArticleList.add(article);
            return true;
        }
    }
    public boolean removeMyArticle(Article article) {
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
    public void genRandArticle(int articleCount) {

        //添加空
        for (int i = 0; i < getClassifyCount(); i++) {
            ArrayList<Article> articleList = new ArrayList<Article>();
            articleList.add(null);
            sArticleListsMap.put(i, articleList);
        }

        Random random = new Random();

        ArrayList<User> userList = mUserController.getAllDBUser();

        for (int i = 0; i < articleCount; i++) {
            User user;
            user = userList.get(random.nextInt(userList.size()));


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

            article.setDate(date);
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

                user1 = userList.get(random.nextInt(userList.size()));
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
            if (mUserController.getSelfUser() != null && user.getUid() == mUserController.getSelfUser().getUid()) {
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

    public int getArticleCount() {
//        return mArticles.size();
        return sArticleMap.size();
    }

    public ArrayList<Article> getMyListItem(int index) {

        return sArticleListsMap.get(index);
    }

    static public final int GET_ARTICLE_TYPE_NORMAL = 0;
    static public final int GET_ARTICLE_TYPE_MY_ARTICLE = 1;
    static public final int GET_ARTICLE_TYPE_MY_COLLECTION = 2;

    /**
     *
     * @param beginPage
     * @param endPage
     * @param type GET_ARTICLE_TYPE_MY_ARTICLE, GET_ARTICLE_TYPE_MY_COLLECTION
     * @return
     */
    public ArrayList<Article> getMyArticleAbstracts(String requestUsername, int beginPage, int endPage, int type) {
        ArrayList<Article> articleList = new ArrayList<>();
        String urlStr = null;
            urlStr = ServerConfig.getServer() + "MyLogin/GetMyArticleAbstracts";
        HttpPost request = new HttpPost(urlStr);

        int begin = beginPage * getNumPerPage();
        int num = (endPage - beginPage) * getNumPerPage();

        List<NameValuePair> params = new ArrayList<>();

        final Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return null;
        }

        //身份认证
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("token", cipherToken));

        params.add(new BasicNameValuePair("requestUsername", requestUsername));
        params.add(new BasicNameValuePair("begin", String.valueOf(begin)));
        params.add(new BasicNameValuePair("num", String.valueOf(num)));
        params.add(new BasicNameValuePair("type", String.valueOf(type)));


        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
//                JSONObject jsonObject = new JSONObject(responseMsg);
//                if(jsonObject.getBoolean("success"))
                try {
                    articleList = Article.fromJsonArticles(responseMsg);
                    articleList.add(0, null);
                    for (Article article: articleList) {
                        if (article != null) {
                            article.getCommentEntities().add(0, null);
                            if (type == GET_ARTICLE_TYPE_MY_ARTICLE) {
                                //
                            }
                            if (type == GET_ARTICLE_TYPE_MY_COLLECTION) {
//                                article.setCollected(true);
                                article.setIsCollected(1);
                            }
                        }
                    }
                    //写入内存
                    sMyArticleList = articleList;
                } catch (Exception e) {
                    Log.e("getMyArticleAbstracts", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return articleList;
    }

    public ArrayList<Article> getArticleAbstracts(int classify, int beginPage, int endPage, int type) {
        //todo 完善分页
//        return getMyListItem(classify);
        ArrayList<Article> articleList = new ArrayList<>();

        String urlStr = ServerConfig.getServer() + "MyLogin/GetArticleList";
        HttpPost request = new HttpPost(urlStr);

        int begin = beginPage * getNumPerPage();
        int num = (endPage - beginPage) * getNumPerPage();

        List<NameValuePair> params = new ArrayList<>();
//        if (type == GET_ARTICLE_TYPE_NORMAL) {
            params.add(new BasicNameValuePair("classify", String.valueOf(classify)));
//        }
//        else if (type == ArticleRecyclerFragment.TYPE_MY_ARTICLE) {
//            params.add(new BasicNameValuePair("uid", String.valueOf(classify)));
//        } else if (type == ArticleRecyclerFragment.TYPE_COLLECTION) {
//
//        }
        params.add(new BasicNameValuePair("begin", String.valueOf(begin)));
        params.add(new BasicNameValuePair("num", String.valueOf(num)));

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
//                JSONObject jsonObject = new JSONObject(responseMsg);
//                if(jsonObject.getBoolean("success"))
                try {
                    articleList = Article.fromJsonArticles(responseMsg);
                    articleList.add(0, null);
                    for (Article article: articleList) {
                        if (article != null) {
                            article.getCommentEntities().add(0, null);
                        }
                    }
                    //写入内存
                    sArticleListsMap.put(classify, articleList);
                } catch (Exception e) {
                    Log.e("GetArticleList", "JSON ERROR");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleList;
    }

    public Article getArticle(int articleId) {
//        return mArticles.get(articleId);
        Article article =  sArticleMap.get(articleId);
        //内存为空或者已经过期
        if (article == null || article != null && article.isExpired()) {
            article = getArticleFromServer(articleId);
            sArticleMap.put(articleId, article);
        }
        return article;
    }

    /**
     *
     * @param classify
     * @param article
     * @return 添加的articleId
     */
    private int saveArticle(int classify, Article article) {
        sArticleListsMap.get(classify).add(article);
        article.getCommentEntities().add(null);

        //todo articleId
        int articleId = article.getId();
        if (articleId == -1) {
            articleId = getArticleCount();
            article.setId(articleId);
        }
        sArticleMap.put(articleId, article);
        addMyArticle(article);
        return articleId;
    }

    public Article addArticleToServer(int classify, String title, String content) {
        Article article = null;

        Auth auth = new Auth(mContext);
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/AddArticle";
        HttpPost request = new HttpPost(urlStr);
        List<NameValuePair> params = new ArrayList<>();
        if (token == null) {
            System.out.println("本地token不存在");
            return null;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return null;
        }

        String selfUsername = auth.getUsername();
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", selfUsername));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("content", content));
        params.add(new BasicNameValuePair("classify", String.valueOf(classify)));

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(responseMsg);
                if(jsonObject.getBoolean("success")) {
                    article = Article.fromJson(responseMsg);
                    //保存到内存
                    saveArticle(classify, article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }

    public Article getArticleFromServer(int articleId) {
        Article article = null;

        String urlStr = ServerConfig.getServer() + "MyLogin/GetArticle";
        HttpPost request = new HttpPost(urlStr);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("articleId", String.valueOf(articleId)));

        final Auth auth = new Auth(mContext);
        String username = auth.getUsername();
        String token = auth.getToken();

        if (username != null) {

            if (token == null) {
                System.out.println("本地token不存在");
                return null;
            }
            //RSA加密token
            String strPublicKey = auth.getPublicKey();
            String cipherToken;
            try {
                cipherToken = RSAUtils.encrypt(token, strPublicKey);
            } catch (Exception e) {
                System.out.println("RSA加密Token失败");
                return null;
            }

            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("token", cipherToken));
        }

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(responseMsg);
                if(jsonObject.getBoolean("success")) {
                    article = Article.fromJson(responseMsg);
                    //todo 评论部分
                    article.getCommentEntities().add(null);
                    //写入内存
                    sArticleMap.put(articleId, article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return article;
    }

    public void closeDB() {
        mUserController.closeDB();
    }

    public Comment addCommentToServer(int articleId, String content, int parentComment) {
        Comment comment = null;

        Auth auth = new Auth(mContext);
        String token = auth.getToken();

        String urlStr = ServerConfig.getServer() + "MyLogin/AddComment";
        HttpPost request = new HttpPost(urlStr);
        List<NameValuePair> params = new ArrayList<>();
        if (token == null) {
            System.out.println("本地token不存在");
            return null;
        }
        //RSA加密token
        String strPublicKey = auth.getPublicKey();
        String cipherToken;
        try {
            cipherToken = RSAUtils.encrypt(token, strPublicKey);
        } catch (Exception e) {
            System.out.println("RSA加密Token失败");
            return null;
        }

        String selfUsername = auth.getUsername();
        String strArticleId = String.valueOf(articleId);
        String strPrtCmt = String.valueOf(parentComment);
        params.add(new BasicNameValuePair("token", cipherToken));
        params.add(new BasicNameValuePair("username", selfUsername));
        params.add(new BasicNameValuePair("articleId", strArticleId));
        params.add(new BasicNameValuePair("content", content));
        params.add(new BasicNameValuePair("parentId", strPrtCmt));

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(responseMsg);
                if(jsonObject.getBoolean("success")) {
                    comment = Comment.fromJson(responseMsg);
                    //保存到内存
//                    saveArticle(classify, article);
                }
            } else {
                Log.d("getComment", "response != 200");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return comment;
    }

    public List<Comment> getArticleComment(int articleId) {
        List<Comment> commentList = new ArrayList<>();

        String urlStr = ServerConfig.getServer() + "MyLogin/GetComment";
        HttpPost request = new HttpPost(urlStr);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("articleId", String.valueOf(articleId)));

        try {
            //设置请求参数项
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient Client = ServerConfig.getHttpClient();
            //执行请求返回相应
            HttpResponse response = Client.execute(request);

            //判断是否请求成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //获得响应信息
                String responseMsg = EntityUtils.toString(response.getEntity());
//                JSONObject jsonObject = new JSONObject(responseMsg);
//                if(jsonObject.getBoolean("success"))
                try {
                    commentList = Comment.fromJsonCommentList(responseMsg);
                    if (commentList.size() == 0) {
                        //
                    }
                    commentList.add(0, null);
                    //debug
                    Log.d("getArticleCommentList", commentList.toString());
                    //todo 写入内存
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commentList;
    }

}
