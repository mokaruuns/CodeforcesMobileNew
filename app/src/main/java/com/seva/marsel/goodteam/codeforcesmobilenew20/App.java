package com.seva.marsel.goodteam.codeforcesmobilenew20;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;

import com.seva.marsel.goodteam.codeforcesmobilenew20.connectionAPI.BlogResult;
import com.seva.marsel.goodteam.codeforcesmobilenew20.connectionAPI.ContestResult;
import com.seva.marsel.goodteam.codeforcesmobilenew20.connectionAPI.funcsAPI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class App extends Application {
    ArrayList<String> resultList = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users (rank TEXT,handle TEXT,firstname TEXT, lastname TEXT,rating TEXT,maxrating TEXT,maxrank TEXT,contribution TEXT,friendOfCount TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS blogs (title TEXT,author TEXT,date TEXT,content TEXT,date_id INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS contests (id TEXT,name TEXT,startTimeSeconds INTEGER,duration TEXT,url TEXT, date TEXT)");
        //db.delete("contests", null, null);
        //db.execSQL("INSERT INTO users VALUES ('Tom Smith', 23);");
        db.close();

        //запись новой информации о соревнованиях
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    class MyAsyncTask extends AsyncTask<Void, Void, ArrayList> {
        ArrayList<String> newsList= new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            resultList.clear();
            Element doc = null;
            Log.d("JSOUP","log");
            try {
                doc = Jsoup.connect("http://codeforces.com/").userAgent("Mozilla").get().body();
            } catch (Exception e) {
                Log.d("JSOUP", e.getMessage());
            }
            if (doc != null) {
                Elements listNews = doc.getElementById("pageContent").getElementsByClass("title");

                for (Element element : listNews.select("a"))
                    resultList.add(element.attr("href").replace("/blog/entry/",""));
                // Log.d("JSOUP", element.attr("href").replace("/blog/entry/",""));
            }

            return resultList;
        }

        @Override
        protected void onPreExecute(){}

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);

            funcsAPI api = new funcsAPI();

            api.getBlog(resultList, blog -> {

                BlogResult blogResult = (BlogResult) blog;

                SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

                String Title = Jsoup.parse(blogResult.getTitle()).text();
                String author = blogResult.getAuthorHandle();
                Spannable formatedText = (Spannable) Html.fromHtml(blogResult.getContent());
                long millis =blogResult.getCreationTimeSeconds().longValue() * 1000;
                Date date = new Date(millis);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.ENGLISH);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String Date = sdf.format(date);
                //Toast.makeText(getApplicationContext(),Date,Toast.LENGTH_SHORT).show();
                //content = content.replace("'", " ");
                ContentValues values = new ContentValues();
                if (db.rawQuery("SELECT * FROM blogs WHERE title ='"+Title+"' AND author='"+author+"';", null).getCount() == 0){
                    values.put("title", Title);
                    values.put("author", author);
                    values.put("date", Date);
                    values.put("content", formatedText.toString());
                    values.put("date_id", (millis/1000));
                    db.insert("blogs",null,  values);
                }
                db.close();
            });
            api.getContests("false", contests -> {
                if (contests != null) {
                    SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
                    List<ContestResult> contestResults = (List<ContestResult>) contests;
                    Date currentDate = new Date(System.currentTimeMillis() / 1000); //получение времени в системе
                    long currentTime = currentDate.getTime();
                    for (int i = 0; i < 10; i++) {
                        long time =  contestResults.get(i).getStartTimeSeconds();
                        Date date = new Date(time*1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.ENGLISH);
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String Date = sdf.format(date);
                        if (contestResults.get(i).getStartTimeSeconds() > currentTime) {
                            String url = "codeforces.com/contestRegistration/" + contestResults.get(i).getId().toString();
                            ContentValues values = new ContentValues();
                            if (db.rawQuery("SELECT * FROM contests WHERE startTimeSeconds = " + time+";", null).getCount() == 0){
                                values.put("id", contestResults.get(i).getId());
                                values.put("name", contestResults.get(i).getName());
                                values.put("startTimeSeconds", time);
                                values.put("duration", (contestResults.get(i).getDurationSeconds()/3600));
                                values.put("url", url);
                                values.put("date", Date);
                                db.insert("contests", null, values);
                            }

                        }
                    }

                }
            });
        }
    }
}
