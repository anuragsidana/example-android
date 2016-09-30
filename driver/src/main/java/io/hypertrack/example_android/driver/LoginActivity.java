package io.hypertrack.example_android.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.TaskStackBuilder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import io.hypertrack.example_android.driver.util.BaseActivity;
import io.hypertrack.example_android.driver.util.SharedPreferenceStore;
import io.hypertrack.lib.transmitter.model.HTShift;
import io.hypertrack.lib.transmitter.model.HTShiftParams;
import io.hypertrack.lib.transmitter.model.HTShiftParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTShiftStatusCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

/**
 * Created by piyush on 30/09/16.
 */
public class LoginActivity extends BaseActivity {

    private TextInputLayout userNameHeader, passwordHeader;
    private EditText userNameText, passwordText;
    private LinearLayout loginBtnLoader;

    private String driverID;

    private TextWatcher userNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && s.length() > 0) {
                userNameHeader.setError(null);
            }
        }
    };

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && s.length() > 0) {
                passwordHeader.setError(null);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Toolbar
        initToolbar(getString(R.string.login_activity_title), false);

        // Initialize UI Views
        initUIViews();
    }

    private void initUIViews() {
        // Initialize UserName Views
        userNameHeader = (TextInputLayout) findViewById(R.id.login_username_header);
        userNameText = (EditText) findViewById(R.id.login_username);
        if (userNameText != null)
            userNameText.addTextChangedListener(userNameTextWatcher);

        // Initialize Password Views
        passwordHeader = (TextInputLayout) findViewById(R.id.login_password_header);
        passwordText = (EditText) findViewById(R.id.login_password);
        if (passwordText != null)
            passwordText.addTextChangedListener(passwordTextWatcher);

        // Initialize Login Btn Loader
        loginBtnLoader = (LinearLayout) findViewById(R.id.login_driver_login_btn_loader);
    }

    public void onLoginButtonClick(View view) {
        if (!validateUserCredentials())
            return;

        attemptDriverLogin();
    }

    private void attemptDriverLogin() {
        // Show Login Button loader
        loginBtnLoader.setVisibility(View.VISIBLE);

        // Implement Network call for Driver Login here.
        driverID = "YOUR_DRIVER_ID";

        // Save DriverID
        SharedPreferenceStore.setDriverID(getApplicationContext(), driverID);

        // On success
        onDriverLoginSuccess();
    }

    private void onDriverLoginSuccess() {
        HTShiftParams htShiftParams = new HTShiftParamsBuilder().setDriverID(driverID).createHTShiftParams();

        HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
        transmitterService.startShift(htShiftParams, new HTShiftStatusCallback() {
            @Override
            public void onSuccess(HTShift htShift) {
                Toast.makeText(LoginActivity.this, R.string.login_success_msg, Toast.LENGTH_SHORT).show();

                TaskStackBuilder.create(LoginActivity.this)
                        .addNextIntentWithParentStack(new Intent(LoginActivity.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        .startActivities();
                finish();

                loginBtnLoader.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(LoginActivity.this, R.string.login_shift_start_error_msg + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loginBtnLoader.setVisibility(View.GONE);
            }
        });
    }

    private boolean validateUserCredentials() {
        boolean valid = true;

        if (TextUtils.isEmpty(userNameText.getText())) {
            userNameHeader.setError(getString(R.string.login_username_empty_error));
            valid = false;
        }

        if (TextUtils.isEmpty(passwordText.getText())) {
            passwordHeader.setError(getString(R.string.login_password_empty_error));
            valid = false;
        }

        return valid;
    }
}
