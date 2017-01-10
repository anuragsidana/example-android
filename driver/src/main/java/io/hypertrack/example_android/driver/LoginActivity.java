package io.hypertrack.example_android.driver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import io.hypertrack.example_android.driver.util.BaseActivity;
import io.hypertrack.example_android.driver.util.PermissionUtils;
import io.hypertrack.example_android.driver.util.SharedPreferenceStore;
import io.hypertrack.lib.transmitter.model.HTShift;
import io.hypertrack.lib.transmitter.model.HTShiftParams;
import io.hypertrack.lib.transmitter.model.HTShiftParamsBuilder;
import io.hypertrack.lib.transmitter.model.callback.HTShiftStatusCallback;
import io.hypertrack.lib.transmitter.model.callback.TransmitterErrorCallback;
import io.hypertrack.lib.transmitter.service.HTTransmitterService;

/**
 * Created by piyush on 30/09/16.
 */
public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private final int REQUEST_CHECK_SETTINGS = 1;
    public static final long LOCATION_UPDATE_INTERVAL_TIME = 500;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextInputLayout userNameHeader, passwordHeader;
    private EditText userNameText, passwordText;
    private LinearLayout loginBtnLoader;
    private CheckBox startShiftCheckbox;

    // Boolean to attemptDriverLogin on Location settings grant, if Login Button was clicked
    private boolean loginButtonClicked = false;

    /**
     * DRIVER_ID is received when a Driver entity is created using HyperTrack APIs.
     * The same DRIVER_ID can be used to maintain the session in b/w Login & Logout on the app.
     * @NOTE: Add your DRIVER_ID here to connect DriverSDK to HyperTrack Server
     */
    private String driverID = "";

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Method to establish DriverSDK connection for a DriverID. Call this method when the DriverSDK connection
         * needs to established, to be able to implement backend-start calls. (Preferably in the onResume()
         * method of your app's Launcher Activity)
         * <p>
         * For more reliability in backend-start calls, call this method just before backend-start need to
         * happen in your app's workflow.
         *
         * For more info refer to the documentation at
         * <a href="https://docs.hypertrack.io/sdks/android/installing.html#connect-the-sdk">
         *     https://docs.hypertrack.io/sdks/android/installing.html#connect-the-sdk</a>.
         *
         * For {@link HTTransmitterService} API javadocs, refer to
         * <a href="https://hypertrack.github.io/android-docs/1.5.4/driver/io/hypertrack/lib/transmitter/service/HTTransmitterService.html">
         *     https://hypertrack.github.io/android-docs/1.5.4/driver/io/hypertrack/lib/transmitter/service/HTTransmitterService.html</a>
         */
        HTTransmitterService.connectDriver(getApplicationContext(), driverID, new TransmitterErrorCallback() {
            @Override
            public void onError(final int errorCode, final String errorMessage) {
                // Handle connectDriver Error
                onConnectDriverError(errorCode, errorMessage);
            }
        });
    }

    private void onConnectDriverError(final int errorCode, final String errorMessage) {
        // Log the error on logcat console
        Log.e(TAG, "HyperTrack connectDriver Error, Code: " + errorCode + ", Message: " + errorMessage);

        // Print the Error on Toast message
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "HyperTrack connectDriver Error, Code: "
                        + errorCode + ", Message: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize GoogleAPIClient to Request Location Permission/Enable Location
        initGoogleClient();
        createLocationRequest(LOCATION_UPDATE_INTERVAL_TIME);

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

        // CheckBox to indicate whether to startShift on Login
        startShiftCheckbox = (CheckBox) findViewById(R.id.login_start_shift_checkbox);

        // Initialize Login Btn Loader
        loginBtnLoader = (LinearLayout) findViewById(R.id.login_driver_login_btn_loader);
    }

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

    public void onLoginButtonClick(View view) {
        if (!validateDriverCredentials())
            return;

        // Set Login Button clicked to attemptDriverLogin, if Location Settings are enabled
        loginButtonClicked = true;

        // Check if Location Settings are enabled, if yes then attempt DriverLogin
        checkForLocationPermission();
    }

    private boolean validateDriverCredentials() {
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

    private void attemptDriverLogin() {
        // Reset Login Button clicked
        loginButtonClicked = false;

        // Show Login Button loader
        loginBtnLoader.setVisibility(View.VISIBLE);

        /**
         * Implement Network call for Driver Login here.
         */

        // On success, Save DriverID to SharedPreferences
        // driverID = "YOUR_DRIVER_ID";
        SharedPreferenceStore.setDriverID(getApplicationContext(), driverID);

        // Check if DriverID has been configured in DriverSDK
        if (TextUtils.isEmpty(driverID)) {
            Toast.makeText(LoginActivity.this, "Login Failed: DriverID not configured!", Toast.LENGTH_SHORT).show();
            loginBtnLoader.setVisibility(View.GONE);
        } else {
            onDriverLoginSuccess();
        }
    }

    private void onDriverLoginSuccess() {
        // Check if shift has to be started on Driver Login or not
        if (!startShiftCheckbox.isChecked()) {
            // Start Driver Session by starting MainActivity
            TaskStackBuilder.create(LoginActivity.this)
                    .addNextIntentWithParentStack(new Intent(LoginActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    .startActivities();
            finish();
            return;
        }

        HTShiftParams htShiftParams = new HTShiftParamsBuilder().setDriverID(driverID).createHTShiftParams();

        HTTransmitterService transmitterService = HTTransmitterService.getInstance(getApplicationContext());
        transmitterService.startShift(htShiftParams, new HTShiftStatusCallback() {
            @Override
            public void onOfflineSuccess() {
                // Do Nothing as Offline Handling is not enabled for Shifts yet
            }

            @Override
            public void onSuccess(HTShift htShift) {
                Toast.makeText(LoginActivity.this, R.string.login_success_msg, Toast.LENGTH_SHORT).show();

                // Save ShiftID to be used to end current shift
                SharedPreferenceStore.setShiftID(LoginActivity.this, htShift.getId());

                // Start Driver Session by starting MainActivity
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

    private void initGoogleClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0 /* clientId */, this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    private void createLocationRequest(long locationUpdateIntervalTime) {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(locationUpdateIntervalTime)
                .setFastestInterval(locationUpdateIntervalTime);
    }

    private void checkForLocationPermission() {
        // Check If LOCATION Permission is available & then if Location is enabled
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkIfLocationIsEnabled();
        } else {
            // Show Rationale & Request for LOCATION permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PermissionUtils.showRationaleMessageAsDialog(this, Manifest.permission.ACCESS_FINE_LOCATION,
                        getString(R.string.location_permission_rationale_msg,
                                getString(R.string.app_name)));
            } else {
                PermissionUtils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    /**
     * Method to check if the Location Services are enabled and in case not, request user to
     * enable them.
     */
    private void checkIfLocationIsEnabled() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        //Start Location Service here if not already active

                        if (loginButtonClicked) {
                            attemptDriverLogin();
                        }

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        // This happens when phone is in Airplane/Flight Mode
                        // Uncomment ErrorMessage to prevent this from popping up on AirplaneMode
                        Toast.makeText(LoginActivity.this, R.string.invalid_current_location, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                        checkIfLocationIsEnabled();

                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    PermissionUtils.showPermissionDeclineDialog(this, Manifest.permission.ACCESS_FINE_LOCATION,
                            getString(R.string.location_permission_never_allow,
                                    getString(R.string.app_name)));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CHECK_SETTINGS) {

            switch (resultCode) {
                case Activity.RESULT_OK:

                    Log.i(TAG, "User agreed to make required location settings changes.");
                    Log.d(TAG, "Fetching Location started!");
                    attemptDriverLogin();
                    break;

                case Activity.RESULT_CANCELED:
                    Toast.makeText(LoginActivity.this, R.string.enable_location_settings, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient onConnectionSuspended: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }
}
