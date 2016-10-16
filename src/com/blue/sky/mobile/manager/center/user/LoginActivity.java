package com.blue.sky.mobile.manager.center.user;import android.content.SharedPreferences;import android.os.Bundle;import android.view.View;import android.view.View.OnClickListener;import android.widget.*;import com.blue.sky.common.AppMain;import com.blue.sky.common.activity.BaseActivity;import com.blue.sky.common.utils.*;import com.blue.sky.mobile.manager.R;public class LoginActivity extends BaseActivity {    private static final String LOGIN_DATE = "LoginDate";    private EditText txtUserEmail;    private EditText txtPassword;    private TextView btnLogin;    private TextView btnRegister;    private TextView lblTitle;    private TextView forgot_password;    private TextView loginStatus;    private ImageView loginIcon;    private ProgressBar progressBar;    private SharedPreferences sharePrefer;    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.sky_activity_login);        setHeader("登陆", true);        lblTitle = (TextView) findViewById(R.id.tv_title);        loginIcon = (ImageView) findViewById(R.id.login_icon);        loginStatus = (TextView) findViewById(R.id.login_status);        btnLogin = (TextView) findViewById(R.id.btn_login);        btnLogin.setOnClickListener(loginClickListener);        btnRegister = (TextView) findViewById(R.id.btn_register);        btnRegister.setOnClickListener(registerClickListener);        // 忘记密码        forgot_password = (TextView) findViewById(R.id.forgot_password);        forgot_password.setOnClickListener(new OnClickListener() {            @Override            public void onClick(View v) {                if (NetWorkHelper.isConnect(LoginActivity.this)) {                    //startActivity(ChangePasswordActivity.class);                } else {                    showToast("亲,没有网络,请检查网络设置!");                }            }        });        txtUserEmail = (EditText) findViewById(R.id.editText_Login);        txtPassword = (EditText) findViewById(R.id.editText_Password);        progressBar = (ProgressBar) this.findViewById(R.id.progressBar);        setLoginStatus();    }    private void setLoginStatus() {        txtUserEmail.setText("");        txtPassword.setText("");        UserInfo userInfo = AppMain.Cookies.getUserInfo();        String loginType = userInfo.getLoginType();        String cacheLogo = userInfo.getUserIcon();        if (EnumUtil.Login.QQ.toString().equals(loginType)) {            loginStatus.setText("QQ帐号已登陆");            ImageLoadUtil.loadImageByResId(loginIcon, cacheLogo, R.drawable.icon_qq_logo);            findViewById(R.id.liner_login).setVisibility(View.VISIBLE);        } else if (EnumUtil.Login.Email.toString().equals(loginType)) {            loginStatus.setText("爱玩帐号已登陆");            txtUserEmail.setText(userInfo.getUserEmail());            txtPassword.setText(userInfo.getPassword());            ImageLoadUtil.loadImageByResId(loginIcon, cacheLogo, R.drawable.ic_launcher);            findViewById(R.id.liner_login).setVisibility(View.VISIBLE);        } else {            findViewById(R.id.liner_login).setVisibility(View.GONE);        }    }    private boolean loginValidate(String userEmail, String userPwd) {        if (!NetWorkHelper.isConnect(this)) {            lblTitle.setText("网络不可用,请检查网络!");            return false;        }        if (Strings.EMPTY_STRING.equals(userEmail)) {            lblTitle.setText("请输入帐号!");            return false;        }        if (!Strings.checkEmail(userEmail)) {            lblTitle.setText("帐号(Email)格式不对!");            return false;        }        if (Strings.isContainSpecialChar(userEmail)) {            lblTitle.setText("帐号包含特殊字符!");            return false;        }        if (Strings.EMPTY_STRING.equals(userPwd)) {            lblTitle.setText("请输入密码!");            return false;        }        return true;    }    private void login(final String userEmail, final String userPwd) {        final String encryptPwd = Encrypt.md5(userPwd);//        HttpRequestAPI.login(new User(userEmail, encryptPwd, Action.Login.Email), new HttpAsyncStringResult() {//            public void process(String response) {//                if (Strings.isNotEmpty(response)) {//                    JSONObject jsonObject = null;//                    try {//                        jsonObject = new JSONObject(response);//                        if (jsonObject != null) {//                            String resultFlag = jsonObject.getString("result");//                            if ("loginSuccess".equals(resultFlag)) {////                                UserInfo userInfo = MyApplication.Cookies.getUserInfo();//                                userInfo.setUserId(jsonObject.getString("Id"));//                                userInfo.setUserName(jsonObject.getString("UserName"));//                                userInfo.setUserEmail(userEmail);//                                userInfo.setPassword(userPwd);//                                userInfo.setUserIcon(Constants.BASE_USER_LOGO + jsonObject.getString("UserIcon"));//                                userInfo.setRoleId(jsonObject.getInt("RoleId"));//                                userInfo.setLoginType(Action.Login.Email);//                                LoginActivity.this.sendBroadcast(new Intent(ActionManager.ACTION_LOGIN));////                                showToast("登陆成功");//                                finish();//                            } else if ("loginError".equals(resultFlag) || "loginPwdError".equals(resultFlag)) {//                                lblTitle.setText("用户名密码错误,请重新输入!");//                            } else if ("loginNotUser".equals(resultFlag)) {//                                lblTitle.setText("帐号不存在，请检查你的用户名!");//                            } else {//                                lblTitle.setText("登陆未知异常,请重试!");//                            }//                        }//                    } catch (JSONException e) {//                        lblTitle.setText("登陆未知异常,请重试!");//                        Log.e("login", "login error:" + e.toString());//                    }//                } else {//                    lblTitle.setText("登陆未知异常,请重试!");//                }//                progressBar.setVisibility(View.GONE);//                btnLogin.setEnabled(true);//            }//        });    }    private OnClickListener registerClickListener = new OnClickListener() {        @Override        public void onClick(View v) {            startActivity(RegisterActivity.class);            finish();        }    };    private OnClickListener loginClickListener = new OnClickListener() {        @Override        public void onClick(View v) {            progressBar.setVisibility(View.VISIBLE);            String userEmail = txtUserEmail.getText().toString().trim();            String userPwd = txtPassword.getText().toString().trim();            if (loginValidate(userEmail, userPwd)) {                btnLogin.setEnabled(false);                //sleep(1);                login(userEmail, userPwd);            } else {                progressBar.setVisibility(View.GONE);            }        }    };}