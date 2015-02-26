package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.fragment.ArticleRecyclerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wind on 2014/12/15.
 */
public class ArticleAbstractsLoader extends DataLoader<ArrayList<Article>> {

    private int mClassify = -1;
    private int mBeginPage = -1;
    private int mEndPage = -1;
    private int mType = -1;

    public ArticleAbstractsLoader(Context context, int classify, int beginPage, int endPage, int type) {
        super(context);
        mClassify = classify;
        mBeginPage = beginPage;
        mEndPage = endPage;
        mType = type;
    }

    public ArticleAbstractsLoader(Context context, int type) {
        super(context);
        mType = type;
    }

    @Override
    public ArrayList<Article> loadInBackground() {
        ArticleController ac = new ArticleController(getContext());
        ArrayList<Article> result = null;

        if (mType == ArticleRecyclerFragment.TYPE_NORMAL || mType == ArticleRecyclerFragment.TYPE_WITH_SLIDE_IMAGE) {
//            result = articleController.getMyListItem(mClassify);
            result =  ac.getArticleAbstracts(mClassify, mBeginPage, mEndPage, ArticleController.GET_ARTICLE_TYPE_NORMAL);
        } else if (mType == ArticleRecyclerFragment.TYPE_COLLECTION) {
//            result = ac.getCollectionList();
            String username = new Auth(getContext()).getUsername();
            result =  ac.getMyArticleAbstracts(username, mBeginPage, mEndPage, ArticleController.GET_ARTICLE_TYPE_MY_COLLECTION);
        } else if (mType == ArticleRecyclerFragment.TYPE_MY_ARTICLE) {
//            result = ArticleController.getMyArticleList();
            String username = new Auth(getContext()).getUsername();
            result =  ac.getMyArticleAbstracts(username, mBeginPage, mEndPage, ArticleController.GET_ARTICLE_TYPE_MY_ARTICLE);
        } else if (mType == ArticleRecyclerFragment.TYPE_REVIEW) {
            String username = new Auth(getContext()).getUsername();
            result =  ac.getMyArticleAbstracts(username, mBeginPage, mEndPage, ArticleController.GET_ARTICLE_TYPE_REVIEW);
        }
        return result;
    }
}
