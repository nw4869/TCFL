package com.nightwind.tcfl.tool.localDB;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nightwind.tcfl.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wind on 2014/12/12.
 */
public class UserDBManager {

    private static final String TABLE_NAME_USER = "user";

    private DBHelper helper;
    private SQLiteDatabase db;

    public UserDBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    private ArrayList<User> getUserListByCursor(Cursor c) {
        ArrayList<User> users = new ArrayList<>();
        while (c.moveToNext()) {
            int uid = c.getInt(c.getColumnIndex("uid"));
            String username = c.getString(c.getColumnIndex("username"));
            String salt = c.getString(c.getColumnIndex("salt"));
            int level = c.getInt(c.getColumnIndex("level"));
            int age = c.getInt(c.getColumnIndex("age"));
            int sex = c.getInt(c.getColumnIndex("sex"));
            String info = c.getString(c.getColumnIndex("info"));
            int edu = c.getInt(c.getColumnIndex("edu"));
            String work = c.getString(c.getColumnIndex("work"));
            String email = c.getString(c.getColumnIndex("email"));
            String tel = c.getString(c.getColumnIndex("tel"));
            String school = c.getString(c.getColumnIndex("school"));
            String avatarUrl = c.getString(c.getColumnIndex("avatarUrl"));
            String hobby = c.getString(c.getColumnIndex("hobby"));
            boolean online = c.getInt(c.getColumnIndex("online")) == 1;


            User user = new User();
            user.setUid(uid);
            user.setUsername(username);
            user.setSalt(salt);
            user.setLevel(level);
            user.setAge(age);
            user.setInfo(info);
            user.setSex(sex);
            user.setEdu(edu);
            user.setWork(work);
            user.setEmail(email);
            user.setTel(tel);
            user.setSchool(school);
            user.setHobby(hobby);
            user.setAvatarUrl(avatarUrl);
            user.setOnline(online);

            users.add(user);
        }
        c.close();
        return users;
    }


//    public Cursor queryTheCursor(String selectionArg) {
//        Cursor c = db.rawQuery("SELECT * FROM person WHERE ", new String[]{selectionArg});
//        return c;
//    }

    public List<User> getAllUsers() {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER, null);
        return getUserListByCursor(c);
    }

    public User getUser(int uid) {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER + " WHERE uid = ? ", new String[] {String.valueOf(uid)});
        return getUserListByCursor(c).get(0);
    }

    private ArrayList<String> userInfo2Strings(User user) {
        ArrayList<String> binds = new ArrayList<>();
        binds.add(String.valueOf(user.getUid()));
        binds.add(user.getUsername());
        binds.add(user.getSalt());
        binds.add(String.valueOf(user.getLevel()));
        binds.add(user.getEmail());
        binds.add(user.getTel());
        binds.add(String.valueOf(user.getAge()));
        binds.add(String.valueOf(user.getSex()));
        binds.add(user.getInfo());
        binds.add(user.getAvatarUrl());
        binds.add(user.getHobby());
        binds.add(String.valueOf(user.isOnline() == true ? 1 : 0));
//        return binds.toArray(new String[binds.size()]);
        return binds;
    }

    public boolean insertUser(User user) {
        ArrayList<String> binds = userInfo2Strings(user);
        try {
            db.execSQL("INSERT INTO " + TABLE_NAME_USER + "(uid, username, salt, level, email, tel, age, sex, work, info, edu, school, avatarUrl, hobby, online)" + "values(?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)",
                    binds.toArray(new String[binds.size()]));
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean updateUser(User user) {
        ArrayList<String> binds = userInfo2Strings(user);
        binds.add(binds.get(0));

        try {
            db.execSQL("UPDATE " + TABLE_NAME_USER + " SET uid=?, username=?, salt=?, level=?, email=?, tel=?, age=?, sex=?, work=?, info=?, edu=?, school=?, avatarUrl=?, hobby=?, online=? WHERE uid=?",
                    binds.toArray(new String[binds.size()]));
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean delUsers() {
        try {
            db.execSQL("DELETE FROM " + TABLE_NAME_USER );
        } catch (SQLException e) {
            return false;
        }
        return true;
    }


    public void closeDB() {
        db.close();
    }

}
