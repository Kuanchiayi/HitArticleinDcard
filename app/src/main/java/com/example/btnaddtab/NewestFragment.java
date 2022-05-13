package com.example.btnaddtab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class NewestFragment extends Fragment {

    View view;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    HashMap<String,String> hashMap;
    String title, result, excerpt, forumName, like, comment, school, gender, post_avatarUrl, img_thumbnail, thumbnailUrl, forumAlias;
    int img_avatar_value, img_thumbnail_value, id;
    boolean thumbnailValue;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_newest, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendGet();
    }

    public void sendGet(){
        ProgressDialog dialog = ProgressDialog.show(getActivity(),"Loading" ,"wait for a minute",true);

        /*   建立連線   */
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))  //Log資訊
                .build();
        /*   傳送需求  */
        Request request = new Request.Builder()
                .url("https://www.dcard.tw/service/api/v2/posts")
                .build();

        /*   設置回傳  */
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                /*如果傳送過程有發生錯誤*/
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                /*取得回傳*/
                result = response.body().string();
                System.out.println(result);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        forumName = jsonObject.getString("forumName");
                        title = jsonObject.getString("title");
                        excerpt = jsonObject.getString("excerpt");
                        like = jsonObject.getString("likeCount");
                        comment = jsonObject.getString("commentCount");
                        forumAlias = jsonObject.getString("forumAlias");
                        id = jsonObject.getInt("id");

                        /*  判斷是男生女生還是有自己的頭貼  */
                        gender = jsonObject.getString("gender");
                        if(gender.equals("M")){
                            img_avatar_value = R.drawable.male;
                        }else if(gender.equals("F")) {
                            img_avatar_value = R.drawable.female;
                        }else{
                            /*    "postAvatar" - 抓頭貼圖片的網址    */
                            post_avatarUrl = jsonObject.getString("postAvatar");
                        }

                        /*   判斷是否為匿名   */
                        if(jsonObject.has("school")){
                            school = jsonObject.getString("school");
                        }else{
                            school = "匿名";
                        };

//                        img_thumbnail = jsonObject.getString("mediaMeta");
//                        JSONArray mediaMeta = new JSONArray(img_thumbnail);
//                        for(int j=0; j<mediaMeta.length(); j++){
//                            JSONObject thumbnailObject = mediaMeta.getJSONObject(j);
//                            if(thumbnailObject.has("thumbnail")){
//                                thumbnailUrl = thumbnailObject.getString("thumbnail");
//                                thumbnailValue = true;
//                            }else thumbnailValue = false;
//                        }

                        hashMap = new HashMap<>();
                        hashMap.put("title", title);
                        hashMap.put("forumName", forumName);
                        hashMap.put("excerpt", excerpt);
                        hashMap.put("like", like);
                        hashMap.put("comment", comment);
                        hashMap.put("gender", gender);
                        hashMap.put("postAvatar", post_avatarUrl);
                        hashMap.put("school", school);
                        hashMap.put("thumbnail", thumbnailUrl);
                        arrayList.add(hashMap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(() -> {
                    recyclerView = getView().findViewById(R.id.recyclerView_new);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(new NewAdapter(getActivity(), arrayList));
                    dialog.dismiss();
                });
            }
        });
    }

    /*            RecyclerAdapter           */
    public class NewAdapter extends RecyclerView.Adapter<NewAdapter.viewHolder>{

        private Context context;
        public ArrayList<HashMap<String,String>> arrayList;
        public NewAdapter(Context context, ArrayList<HashMap<String,String>> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        class viewHolder extends RecyclerView.ViewHolder{
            TextView tv_title, tv_forumName, tv_excerpt, favorite_count, comment_count;
            ImageView iv_avatar, iv_thumbnail;

            public viewHolder(View itemsView){
                super(itemsView);
                tv_title = itemsView.findViewById(R.id.tv_title);
                tv_forumName = itemsView.findViewById(R.id.tv_forumName);
                tv_excerpt = itemsView.findViewById(R.id.tv_excerpt);
                favorite_count = itemsView.findViewById(R.id.favorite_count);
                comment_count = itemsView.findViewById(R.id.comment_count);
                iv_avatar = itemsView.findViewById(R.id.img_1);
//              iv_thumbnail = itemsView.findViewById(R.id.img_thumbnail);

                /*       點選事件放在adapter中使用，也可以寫個介面在activity中呼叫        */
                itemsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ArticleActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("forumAlias", forumAlias);
                        bundle.putInt("id", id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        }

        @NonNull
        @Override
        public NewAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = View.inflate(context, R.layout.recycler_items, null);
            return new NewAdapter.viewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NewAdapter.viewHolder holder, int position) {
            HashMap<String,String> data = arrayList.get(position);
            holder.tv_forumName.setText(String.format("%s ・ %s", data.get("forumName"), data.get("school")));
            holder.tv_title.setText(data.get("title"));
            holder.tv_excerpt.setText(data.get("excerpt"));
            holder.favorite_count.setText(data.get("like"));
            holder.comment_count.setText(data.get("comment"));

            if(data.get("gender").equals("M")){
                holder.iv_avatar.setImageResource(R.drawable.male);
            }else if(data.get("gender").equals("F")){
                holder.iv_avatar.setImageResource(R.drawable.female);
            }else{
                Glide.with(getActivity()).load(post_avatarUrl).into(holder.iv_avatar);
            }

//            if(thumbnailValue){
//                Glide.with(getActivity()).load(data.get("thumbnail")).into(holder.iv_thumbnail);
//            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}