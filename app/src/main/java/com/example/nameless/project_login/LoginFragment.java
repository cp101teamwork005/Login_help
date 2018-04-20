package com.example.nameless.project_login;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by nameless on 2018/4/16.
 */

public class LoginFragment extends Fragment{

    private MyTask loginTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.textLogin);
        View view = inflater.inflate(R.layout.login, container, false);
        final EditText etName = view.findViewById(R.id.etName);
        final EditText etPassword = view.findViewById(R.id.etPassword);
        Button btlogin = view.findViewById(R.id.btLogin);
        Button btRegister = view.findViewById(R.id.btRegister);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragmentRegister = new RegisterFragment();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.replace(R.id.content,fragmentRegister);
                fragmentTransaction.commit();


            }
        });

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (user.length()<=0 || password.length()<=0){
                    Toast.makeText(getActivity(),"wrong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isUserValid(user,password)){
                    SharedPreferences pref = getActivity().getSharedPreferences(Common.PREF_FILE,MODE_PRIVATE);
                    pref.edit().putBoolean("login",true).apply();
                    pref.edit().putString("user",user).apply();
                    pref.edit().putString("password",password).apply();
                    Toast.makeText(getActivity(),"ok", Toast.LENGTH_SHORT).show();
                    getActivity().setResult(Activity.RESULT_OK);
                }else{
                    Toast.makeText(getActivity(),"nononono", Toast.LENGTH_SHORT).show();
                }


            }
        });



        return view;

    }
    private boolean isUserValid(String name,String password){
            String url = Common.URL+"/LoginServerlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name",name);
            jsonObject.addProperty("password",password);
            loginTask = new MyTask(url, jsonObject.toString());
            boolean isUserValid = false;
            try{
                String jsonIN = loginTask.execute().get();
                jsonObject = new Gson().fromJson(jsonIN,JsonObject.class);
                isUserValid = jsonObject.get("isUserValid").getAsBoolean();

            }catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return isUserValid;
        }
}
