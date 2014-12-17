package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.controller.ArticleController;

/**
 * Created by wind on 2014/12/17.
 */
public class ArticleLoader extends DataLoader<Article> {

    private final int mId;

    public ArticleLoader(Context context, int id) {
        super(context);
        mId = id;
    }

    @Override
    public Article loadInBackground() {
        ArticleController ac = new ArticleController(getContext());
        Article article = ac.getArticle(mId);
        return article;
    }
}
