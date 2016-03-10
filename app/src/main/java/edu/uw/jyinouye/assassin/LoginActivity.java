package edu.uw.jyinouye.assassin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "LoginActivity";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mUserNameView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean toggleSignUp;

    private int selectedAvatar;
    private ImageButton[] avatarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_login));

        toggleSignUp = false;

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mUserNameView = (EditText) findViewById(R.id.user_name_field);

        //initialize avators
        initializeAvators();

        mPasswordView = (EditText) findViewById(R.id.user_password);
        mPasswordConfirmView = (EditText) findViewById(R.id.confirm_user_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //login button
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //allows user to easily switch between
        Button mSignUpButton = (Button) findViewById(R.id.toggle_sign_up);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSignUp();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void initializeAvators() {
        ImageButton avator1 = (ImageButton) findViewById(R.id.avator1);
        ImageButton avator2 = (ImageButton) findViewById(R.id.avator2);
        ImageButton avator3 = (ImageButton) findViewById(R.id.avator3);
        ImageButton avator4 = (ImageButton) findViewById(R.id.avator4);
        ImageButton avator5 = (ImageButton) findViewById(R.id.avator5);
        ImageButton avator6 = (ImageButton) findViewById(R.id.avator6);
        ImageButton avator7 = (ImageButton) findViewById(R.id.avator7);
        ImageButton avator8 = (ImageButton) findViewById(R.id.avator8);
        avatarList = new ImageButton[]{avator1, avator1, avator2, avator3, avator4, avator5, avator6, avator7, avator8};

        selectedAvatar = 1;
        avatarList[1].setColorFilter(Color.argb(155, 185, 185, 185));

        avator1.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 1;
                                           highlightButton();
                                       }
                                   }
        );

        avator2.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 2;
                                           highlightButton();
                                       }
                                   }
        );

        avator3.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 3;
                                           highlightButton();
                                       }
                                   }
        );

        avator4.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 4;
                                           highlightButton();
                                       }
                                   }
        );
        avator5.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 5;
                                           highlightButton();
                                       }
                                   }
        );
        avator6.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 6;
                                           highlightButton();
                                       }
                                   }
        );
        avator7.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 7;
                                           highlightButton();
                                       }
                                   }
        );
        avator8.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           selectedAvatar = 8;
                                           highlightButton();
                                       }
                                   }
        );
    }

    //highlights selected avator
    public void highlightButton() {
        resetFilter();
        if (selectedAvatar == 1) {
            avatarList[1].setColorFilter(Color.argb(155, 185, 185, 185));
        } else if (selectedAvatar == 2) {
            avatarList[2].setColorFilter(Color.argb(155, 185, 185, 185));
        } else if (selectedAvatar == 3) {
            avatarList[3].setColorFilter(Color.argb(155, 185, 185, 185));
        } else if (selectedAvatar == 4) {
            avatarList[4].setColorFilter(Color.argb(155, 185, 185, 185));
        } else if (selectedAvatar == 5) {
            avatarList[5].setColorFilter(Color.argb(155, 185, 185, 185));
        } else if (selectedAvatar == 6) {
            avatarList[6].setColorFilter(Color.argb(155, 185, 185, 185));
        } else if (selectedAvatar == 7) {
            avatarList[7].setColorFilter(Color.argb(155, 185, 185, 185));
        } else if (selectedAvatar == 8) {
            avatarList[8].setColorFilter(Color.argb(155, 185, 185, 185));
        }
    }

    //makes sure only 1 filter is highlighted at a time
    public void resetFilter() {
        avatarList[1].setColorFilter(Color.argb(0, 185, 185, 185));
        avatarList[2].setColorFilter(Color.argb(0, 185, 185, 185));
        avatarList[3].setColorFilter(Color.argb(0, 185, 185, 185));
        avatarList[4].setColorFilter(Color.argb(0, 185, 185, 185));
        avatarList[5].setColorFilter(Color.argb(0, 185, 185, 185));
        avatarList[6].setColorFilter(Color.argb(0, 185, 185, 185));
        avatarList[7].setColorFilter(Color.argb(0, 185, 185, 185));
        avatarList[8].setColorFilter(Color.argb(0, 185, 185, 185));
    }

    private void toggleSignUp() {
        Button mSignUpButton = (Button) findViewById(R.id.toggle_sign_up);
        Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        EditText mConfirmUserPass = (EditText) findViewById(R.id.confirm_user_password);
        // if user is viewing sign in form
        if(toggleSignUp) {
            mSignUpButton.setText(R.string.toggle_sign_up);
            mSignInButton.setText(R.string.action_sign_in);
            mConfirmUserPass.setVisibility(View.GONE);
            mUserNameView.setVisibility(View.GONE);
            toggleSignUp = false;
        } else {
            mSignUpButton.setText(R.string.toggle_sign_in);
            mSignInButton.setText(R.string.action_sign_up);
            mConfirmUserPass.setVisibility(View.VISIBLE);
            mUserNameView.setVisibility(View.VISIBLE);
            toggleSignUp = true;
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String userName = mUserNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // if user is signing up, check that passwords match
        if(toggleSignUp) {
            String confirmPassword = mPasswordConfirmView.getText().toString();
            if(!password.equals(confirmPassword)) {
                focusView = mPasswordConfirmView;
                mPasswordConfirmView.setError(getString(R.string.error_password_mismatch));
                cancel = true;
            }
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, userName, selectedAvatar);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void startJoinGroupActivity() {
        Intent intent = new Intent(this, JoinGroupActivity.class);
        startActivity(intent);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> implements Assassin.OnAuthenticateListener {

        private final String mEmail;
        private final String mPassword;
        private final String mUserName;
        private final Assassin assassin;
        private boolean success;

        private final int mAvator;

        UserLoginTask(String email, String password, String userName, Integer selectedAvator) {
            mEmail = email;
            mPassword = password;
            mUserName = userName;
            mAvator = selectedAvator;
            assassin = ((Assassin)getApplicationContext()).getInstance();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // set listener for authentication callbacks
            assassin.setOnAuthenticateListener(this);

            // sign up user
            if(toggleSignUp) {
                assassin.signup(mEmail, mPassword, mUserName, mAvator);
            } else {
                assassin.login(mEmail, mPassword, mAvator);
            }

            // returns true, allowing login, since user registered
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }

        @Override
        public void onSignUpSuccess(String uid) {
            assassin.login(mEmail, mPassword, mAvator);
        }

        @Override
        public void onSignUpError(FirebaseError error) {
            mEmailView.setError(getString(R.string.error_signup));
        }

        @Override
        public void onLoginSuccess() {
            showProgress(false);
            Log.v(TAG, "Successfully logged in");
            startJoinGroupActivity();
        }

        @Override
        public void onLoginError(FirebaseError error) {
            showProgress(false);
            mEmailView.requestFocus();
            mEmailView.setError(getString(R.string.error_login));
        }
    }
}

