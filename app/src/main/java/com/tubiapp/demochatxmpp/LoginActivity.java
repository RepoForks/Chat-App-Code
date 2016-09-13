package com.tubiapp.demochatxmpp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tubiapp.demochatxmpp.abstracts.BaseActivity;
import com.tubiapp.demochatxmpp.service.XmppConnectionService;
import com.tubiapp.demochatxmpp.utils.Utility;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEdtEmail, edit_chat_person;
    private EditText mEdtPassword;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    protected void initDatas() {

    }

    @Override
    protected void initRootViews() {

    }

    @Override
    protected void initUIComponents() {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void loadData() {

    }

    private void initComponents() {
        mEdtEmail = (EditText) findViewById(R.id.edt_email);
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        edit_chat_person = (EditText) findViewById(R.id.edt_chat);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEdtEmail.setText("");
        mEdtPassword.setText("");
        edit_chat_person.setText("");

        if (Utility.getUSER_STATUS(getApplicationContext())) {
            Intent chatIntent = new Intent(LoginActivity.this, UserList.class);
            startActivity(chatIntent);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        String email = mEdtEmail.getText().toString().trim();
        String password = mEdtPassword.getText().toString();
        // String chat_username = edit_chat_person.getText().toString();
       /* final User user = new User(email, password);
        Api.getInstance().login(user, new Api.APICallback<MyResponse>() {
            @Override
            public void success(MyResponse myResponse, Response response) {
                Log.i("API", myResponse.toString());
                Toast.makeText(LoginActivity.this, myResponse.toString(), Toast.LENGTH_LONG).show();
                gotoChatActivity(user);
            }



            private void gotoChatActivity(User user) {*/

        if (email.trim().length() > 0) {
            if (password.trim().length() > 0) {
                //if (chat_username.trim().length() > 0) {

                Utility.writeSharedPreferencesBool(getApplicationContext(), true, email, password);

                Intent service = new Intent(LoginActivity.this, XmppConnectionService.class);
                startService(service);
                Intent chatIntent = new Intent(LoginActivity.this, UserList.class);
                startActivity(chatIntent);
                finish();
              /*  } else {
                    edit_chat_person.setError("Plz enter Username");
                }*/
            } else {
                mEdtPassword.setError("Plz enter password");
            }
        } else {
            mEdtEmail.setError("Plz enter email");
        }
          /*  }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(LoginActivity.this, "Error" + error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });*/
    }
}
