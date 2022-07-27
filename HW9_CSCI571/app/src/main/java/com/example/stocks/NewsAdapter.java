package com.example.stocks;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
//    List<JSONObject> companyNews = new ArrayList<JSONObject>();
    JSONArray companyNews = new JSONArray();
    public static boolean initializedPicasso = false;

    public NewsAdapter(JSONArray company_News) {

        this.companyNews = company_News;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View News_view;
        switch(viewType){
            case 0:
                News_view = layoutInflater.inflate(R.layout.first_news, parent, false);
                break;
            default:
                News_view = layoutInflater.inflate(R.layout.all_other_news, parent, false);
                break;
        }

        NewsViewHolder newsViewHolder = new NewsViewHolder(News_view);
        return newsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        try {
            Context newsContext = holder.newsView.getContext();
            JSONObject news = companyNews.getJSONObject(position);
            holder.news_source.setText(news.getString("source"));
            holder.news_title.setText(news.getString("headline"));
            String imgUrl = news.getString("image");
//            System.out.println("IMAGES"+imgUrl+" "+position);
//            Picasso.get().load(imgUrl).into(holder.news_image);
            Glide.with(newsContext)
                    .load(imgUrl)
                    .into(holder.news_image);
            String elapsed_time = calculateElapsedTime(news.getString("datetime"));
            holder.news_time_elapsed.setText(elapsed_time);
            holder.newsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View News_view) {

                    final Dialog dialog = new Dialog(newsContext);
                    dialog.setContentView(R.layout.news_dialog);
                    TextView newsSource = (TextView) dialog.findViewById(R.id.newsSource);
                    TextView newsHeadline = (TextView) dialog.findViewById(R.id.newsHeadline);
                    TextView newsDescription = (TextView) dialog.findViewById(R.id.newsDescription);
                    TextView newsDatetime = (TextView) dialog.findViewById(R.id.newsDatetime);
                    try {
                        newsSource.setText(news.getString("source"));
                        newsHeadline.setText(news.getString("headline"));
                        newsDescription.setText(news.getString("summary"));
                        String newsdate = formatDatetime(news.getString("datetime"));
                        String newsURL = news.getString("url");
                        newsDatetime.setText(newsdate);
                        ImageButton twitter = (ImageButton) dialog.findViewById(R.id.twitter_button);
                        ImageButton chrome = (ImageButton) dialog.findViewById(R.id.chrome_button);
                        ImageButton facebook = (ImageButton) dialog.findViewById(R.id.facebook_button);

                        chrome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View News_view) {
                                Intent openLinksIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsURL));
                                newsContext.startActivity(openLinksIntent);

                            }
                        });

                        twitter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View News_view) {
                                String tweetUrl = null;
                                try {
                                    tweetUrl = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode("Check out this Link:", "UTF-8")+ "&url=" + URLEncoder.encode(newsURL, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                                newsContext.startActivity(twitterIntent);

                            }
                        });

                        facebook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String facebookUrl = "https://www.facebook.com/sharer/sharer.php?u=" + newsURL;
                                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                                newsContext.startActivity(facebookIntent);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialog.show();

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
    public String formatDatetime(String datetime){
        String res = "";
        long t = Long.parseLong(datetime);
        Date date_time = new Date((long)t*1000);
        String pattern = "MMMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        res = simpleDateFormat.format(date_time);
//        System.out.println("dateee"+date_time);
        return res;
    }
    public String calculateElapsedTime(String datetime){
        Date curr_date = new Date();
        long t = Long.parseLong(datetime);
        Date elapsed_time = new Date((long)t*1000);
        long time_diff = curr_date.getTime() - elapsed_time.getTime();
        int news_day_diff = (int) time_diff / (1000*60*60*24);
        int news_hours_diff = (int) time_diff / (1000*60*60);
        int news_minutes_diff = (int) time_diff / (1000*60);
        int news_seconds_diff = (int) time_diff / (1000);
        System.out.println("diffHours"+time_diff);
        if(news_day_diff < 1){
            if(news_hours_diff > 1){
                return news_hours_diff+" hours ago";
            }
            else if(news_hours_diff == 1){
                return "1 hour ago";
            }
            else{
                if(news_minutes_diff > 1) {
                    return news_minutes_diff + " minutes ago";
                }
                else if(news_minutes_diff == 1){
                    return "1 minute ago";
                }
                else{
                    if(news_seconds_diff > 1) {
                        return news_seconds_diff + " seconds ago";
                    }
                    else if(news_seconds_diff == 1){
                        return "1 second ago";
                    }
                }
            }

        }
        else if(news_day_diff == 1){
            return "1 day ago";
        }
        else{
            return news_day_diff+" days ago";
        }

        return "";

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return 0;
        }
        else{
            return 1;
        }

//        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return companyNews.length();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView news_source, news_time_elapsed, news_title;
        ImageView news_image;
        View newsView;
        public NewsViewHolder(@NonNull View itemView) {

            super(itemView);
            newsView = itemView;
            news_image = (ImageView) itemView.findViewById(R.id.news_image);
            news_source = (TextView) itemView.findViewById(R.id.news_source);
            news_time_elapsed = (TextView) itemView.findViewById(R.id.news_time_elapsed);
            news_title = (TextView) itemView.findViewById(R.id.news_title);

        }
    }
}