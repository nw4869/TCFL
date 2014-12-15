package com.nightwind.tcfl.server;

import android.content.Context;

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

        ArticleController articleController = new ArticleController(getContext());
        if (mType == ArticleRecyclerFragment.TYPE_NORMAL || mType == ArticleRecyclerFragment.TYPE_WITH_SLIDE_IMAGE) {
//            result = articleController.getMyListItem(mClassify);
            result =  ac.getArticleAbstracts(mClassify, mBeginPage, mEndPage);
        } else if (mType == ArticleRecyclerFragment.TYPE_COLLECTION) {
            result = ArticleController.getCollectionList();
        } else if (mType == ArticleRecyclerFragment.TYPE_MY_ARTICLE) {
            result = ArticleController.getMyArticleList();
        }
        return result;
    }
}
