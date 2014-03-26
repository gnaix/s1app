package com.gnaix.app.s1.activity;

import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.app.s1.util.Encrypt;
import com.gnaix.common.util.PreferenceUtil;

public class LoginFragment extends PageFragment implements Stage1ApiClient.ClientCallback {
    @Override
    public void onRequestFinish(Stage1ApiClient.Result result) {
        hideLoadingIndicator();
        if (result.statueCode == Stage1ApiClient.SC_QEQUEST_SUCCESS && result.apiCode == Stage1ApiClient.API_REQUEST_LOGIN) {
            if (Boolean.valueOf(result.mData.toString())) {
                Toast.makeText(getActivity(), "登录成功", Toast.LENGTH_SHORT).show();
                mNavigationManager.goBack();
                return;
            }
        }
        showErrorIndicator(result.message);

    }

    @Override
    public void onRequestProgress(int i) {

    }

    @Override
    public void onRequestCancel(Stage1ApiClient.Result result) {

    }

    private EditText userNameEt, pwdEt;
    private CheckBox remberCbx;
    private Button loginBtn;


    @Override
    public void bindViews() {
        setHasOptionsMenu(true);
        rebindActionBar();
        PreferenceUtil.init(getActivity());
        userNameEt = (EditText) findViewById(R.id.user_name);
        pwdEt = (EditText) findViewById(R.id.password);
        remberCbx = (CheckBox) findViewById(R.id.rember_account);
        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        String histroy = PreferenceUtil.getString("ACCOUNT", "");
        if (!TextUtils.isEmpty(histroy)) {
            String[] data = histroy.split(",");
            if (data.length == 2) {
                userNameEt.setText(data[0]);
                pwdEt.setText(Encrypt.decrypt(data[1]));
            }
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public void rebindActionBar() {
        getPageFragmentHost().getHostActionBar().setDisplayHomeAsUpEnabled(true);
        getPageFragmentHost().getHostActionBar().setTitle(getText(R.string.text_login_stage1));
        getPageFragmentHost().getHostActionBar().setHomeButtonEnabled(true);
        getPageFragmentHost().getHostActionBar().setDisplayShowHomeEnabled(false);
        getPageFragmentHost().getHostActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getPageFragmentHost().getHostDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_login;
    }

    private void login() {
        String username = userNameEt.getText().toString().trim();
        Log.d(Constants.TAG, "username:" + username);
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), getString(R.string.msg_input_username), Toast.LENGTH_SHORT).show();
            userNameEt.requestFocus();
            return;
        }
        String password = pwdEt.getText().toString().trim();
        Log.d(Constants.TAG, "password:" + password);
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), getString(R.string.msg_input_pwd), Toast.LENGTH_SHORT).show();
            pwdEt.requestFocus();
            return;
        }
        if (remberCbx.isChecked()) {
            PreferenceUtil.commitString("ACCOUNT", username + "," + Encrypt.encrypt(password));
        }

        getPageFragmentHost().getS1Api().login(this, username, password);
    }


    @Override
    public void onRetry() {
        showLoadingIndicator();
        login();
    }
}
