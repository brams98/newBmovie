package com.example.tb_laota.Binusmovie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String url = "https://api.androidhive.info/json/movies.json";
    private ProgressDialog dialog;
    private List<Item> array = new ArrayList<Item>();
    private ListView listView;
    private Adapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    DB data = new DB(getApplicationContext());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_item);
        adapter=new Adapter(this,array);
        listView.setAdapter(adapter);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(MainActivity.this, loginActivity.class));
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        //Creat volley request obj
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                hideDialog();
                //parsing json
                for(int i=0;i<response.length();i++){
                    try{
                        JSONObject obj=response.getJSONObject(i);
                        Item item=new Item();
                        item.setTitle(obj.getString("title"));
                        item.setImage(obj.getString("image"));
                        item.setRate(((Number) obj.get("rating")).doubleValue());
                        item.setYear(obj.getInt("releaseYear"));

                        //genre is json array
                        JSONArray genreArray=obj.getJSONArray("genre");
                        ArrayList<String> genre=new ArrayList<String>();
                        for(int j=0;j<genreArray.length();j++){
                            genre.add((String) genreArray.get(j));
                        }
                        item.setGenre(genre);

                        //add to array
                        array.add(item);
                    }catch(JSONException ex){
                        ex.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);


//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mAuth.signOut();
//            }
//        });


    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }





    public void hideDialog(){
        if(dialog !=null){
            dialog.dismiss();
            dialog=null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            break;

            case R.id.btnLogout:
                mAuth.signOut();
                break;

            case R.id.btnProfile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public class Adapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Activity activity;
        private List<Item> items;


        ImageLoader imageLoader=AppController.getmInstance().getmImageLoader();
        public Adapter(Activity activity,List<Item> items){
            this.activity=activity;
            this.items=items;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(inflater==null){
                inflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if(convertView ==null){
                convertView=inflater.inflate(R.layout.custom_layout,null);
            }
            if(imageLoader==null)
                imageLoader=AppController.getmInstance().getmImageLoader();
            NetworkImageView imageView= (NetworkImageView) convertView.findViewById(R.id.image_view);
            TextView title= (TextView) convertView.findViewById(R.id.tv_title);
            TextView rate= (TextView) convertView.findViewById(R.id.tv_rate);
            TextView genre= (TextView) convertView.findViewById(R.id.tv_genre);
            final TextView year= (TextView) convertView.findViewById(R.id.tv_year);
            Button btn= (Button) convertView.findViewById(R.id.btnn);
            //getting data for row
            Item item=items.get(position);
            imageView.setImageUrl(item.getImage(), imageLoader);
            //title
            title.setText(item.getTitle());
            //rate
            rate.setText(String.valueOf(item.getRate()));
            String genreStr="";
            for(String str: item.getGenre()){
                genreStr +=str + ",";
            }
            genreStr = genreStr.length() >0 ? genreStr.substring(0, genreStr.length() - 2) : genreStr;
            genre.setText(genreStr);
            //year
            year.setText(String.valueOf(item.getYear()));
            final String finalGenreStr = genreStr;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Toast.makeText(MainActivity.this,"movie is downloaded to your device",Toast.LENGTH_SHORT).show();
                        data.insert(getTitle(), finalGenreStr,year.getText().toString());
                }
            });

            return convertView;
        }

    }


}
