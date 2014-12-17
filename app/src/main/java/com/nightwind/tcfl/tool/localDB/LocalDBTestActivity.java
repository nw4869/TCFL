package com.nightwind.tcfl.tool.localDB;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.controller.UserController;

public class LocalDBTestActivity extends ActionBarActivity {

    private DBManager mgr;
    private UserDBManager umgr;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_dbtest);

        listView = (ListView) findViewById(R.id.listView);
        //初始化DBManager
        mgr = new DBManager(this);
        umgr = new UserDBManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //应用的最后一个Activity关闭时应释放DB
        mgr.closeDB();
        umgr.closeDB();
    }


    public void add(View view) {
        ArrayList<Person> persons = new ArrayList<Person>();

        Person person1 = new Person("Ella", 22, "lively girl");
        Person person2 = new Person("Jenny", 22, "beautiful girl");
        Person person3 = new Person("Jessica", 23, "sexy girl");
        Person person4 = new Person("Kelly", 23, "hot baby");
        Person person5 = new Person("Jane", 25, "a pretty woman");

        persons.add(person1);
        persons.add(person2);
        persons.add(person3);
        persons.add(person4);
        persons.add(person5);

        mgr.add(persons);
    }

    public void update(View view) {
        Person person = new Person();
        person.name = "Jane";
        person.age = 30;
        mgr.updateAge(person);
    }

    public void delete(View view) {
        Person person = new Person();
        person.age = 30;
        mgr.deleteOldPerson(person);
    }

    public void query(View view) {
//        List<Person> persons = mgr.query();
//        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
//        for (Person person : persons) {
//            HashMap<String, String> map = new HashMap<String, String>();
//            map.put("name", person.name);
//            map.put("info", person.age + " years old,,,,, " + person.info);
//            list.add(map);
//        }
        List<User> users = umgr.getAllUsers();
        ArrayList<Map<String, String>> list = new ArrayList<>();
        for (User user : users) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", user.getUsername());
            map.put("info", user.getUid() + " salt =  " + user.getSalt());
            list.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
                new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    }

    public void queryTheCursor(View view) {
        Cursor c = mgr.queryTheCursor();
        startManagingCursor(c); //托付给activity根据自己的生命周期去管理Cursor的生命周期
        CursorWrapper cursorWrapper = new CursorWrapper(c) {
            @Override
            public String getString(int columnIndex) {
                //将简介前加上年龄
                if (getColumnName(columnIndex).equals("info")) {
                    int age = getInt(getColumnIndex("age"));
                    return age + " years old, " + super.getString(columnIndex);
                }
                return super.getString(columnIndex);
            }
        };
        //确保查询结果中有"_id"列
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                cursorWrapper, new String[]{"name", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public void test1(View v) {

        TextView tv = (TextView) findViewById(R.id.et_articleId);
        int articleId = Integer.parseInt(String.valueOf(tv.getText()));

        new AsyncTask<Integer, Void, String>() {
            @Override
            protected String doInBackground(Integer... params) {
                int articleId = params[0];
                ArticleController ac = new ArticleController(LocalDBTestActivity.this);
                Article article = ac.getArticleFromServer(articleId);
                Gson gson = new Gson();
//                System.out.println(user.getUid() + " " + user.getUsername() );
                String result = gson.toJson(article);
                System.out.println(result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.result)).setText(result);
            }
        }.execute(articleId);

    }

    public void test(View v) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                UserController uc = new UserController(LocalDBTestActivity.this);
//                TextView tv = (TextView) findViewById(R.id.et_username);
//                String username = String.valueOf(tv.getText());
//                User user = uc.getUser(username);
//                Gson gson = new Gson();
////                System.out.println(user.getUid() + " " + user.getUsername() );
//                System.out.println(gson.toJson(user) );
//            }
//        }).start();

        TextView tv = (TextView) findViewById(R.id.et_username);
        String username = String.valueOf(tv.getText());

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                String username = params[0];
                UserController uc = new UserController(LocalDBTestActivity.this);
                User user = uc.getUser(username);
                Gson gson = new Gson();
//                System.out.println(user.getUid() + " " + user.getUsername() );
                String result = gson.toJson(user);
                System.out.println(result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.result)).setText(result);
            }
        }.execute(username);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_local_dbtest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
