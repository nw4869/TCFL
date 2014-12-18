package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.Comment;
import com.nightwind.tcfl.controller.ArticleController;

import java.util.List;

/**
 * Created by wind on 2014/12/18.
 */
public class CommentLoader extends DataLoader<List<Comment>>{
    private final int mId;

    public CommentLoader(Context context, int id) {
        super(context);
        mId = id;
    }

    @Override
    public List<Comment> loadInBackground() {
        ArticleController ac = new ArticleController(getContext());
        List<Comment> commentList = ac.getArticleComment(mId);
        return commentList;
    }
}
