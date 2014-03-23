package com.gnaix.app.s1.activity;

import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gnaix.app.s1.R;
import com.gnaix.common.util.DesUtil;
import com.gnaix.common.util.PreferenceUtil;

public class LoginFragment extends PageFragment {

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
        if(!TextUtils.isEmpty(histroy)){
            String[] data = histroy.split(",");
            userNameEt.setText(data[0]);
            pwdEt.setText(DesUtil.decodeValue(KEY, data[1]));
        }
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

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
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), getString(R.string.msg_input_username), Toast.LENGTH_SHORT).show();
            userNameEt.requestFocus();
            return;
        }
        String password = pwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), getString(R.string.msg_input_pwd), Toast.LENGTH_SHORT).show();
            pwdEt.requestFocus();
            return;
        }
        if(remberCbx.isChecked()){
            PreferenceUtil.commitString("ACCOUNT", username+","+DesUtil.encode(KEY, password));
        }
    }
    
    private static final String KEY = "bbs.stage1st.com";
}
