package edu.uw.jyinouye.assassin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class JoinGroupActivity extends AppCompatActivity implements ValueEventListener {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mGroupView;
    private Button mJoinGroup;
    private EditText mPasswordView;
    private EditText mPasswordConfimView;
    private View mProgressView;
    private View mLoginFormView;

    // application class for performing firebase operations
    private Assassin assassin;
    private List<String> groupNames;
    private ArrayAdapter<String> groupAutoCompleteAdapter;
    private boolean toggleCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_activity_login));

        assassin = ((Assassin)getApplicationContext()).getInstance();
        groupNames = new ArrayList<>();

        ((TextView) findViewById(R.id.user_name)).setText(assassin.getPlayer().getUserName());
        setProfileImage();

        // Set up the login form.
        mJoinGroup = (Button) findViewById(R.id.join_group);
        mGroupView = (AutoCompleteTextView) findViewById(R.id.group_name);
        mGroupView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // if group doesn't exist, change input fields to create new group
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText mGroupConfirmPassword = (EditText) findViewById(R.id.group_password_confirm);
                    if(!groupNames.contains(mGroupView.getText().toString())) {
                        mJoinGroup.setText(R.string.action_create_group);
                        mGroupConfirmPassword.setVisibility(View.VISIBLE);
                        toggleCreateGroup = true;
                    } else {
                        mJoinGroup.setText(R.string.action_join_group);
                        mGroupConfirmPassword.setVisibility(View.GONE);
                        toggleCreateGroup = false;
                    }
                }
            }
        });

        mPasswordView = (EditText) findViewById(R.id.group_password);
        mPasswordConfimView = (EditText) findViewById(R.id.group_password_confirm);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptJoinGroup();
                    return true;
                }
                return false;
            }
        });

        addGroupsToAutoComplete();

        Button mEmailSignInButton = (Button) findViewById(R.id.join_group);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptJoinGroup();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    //sets profile avatar
    public void setProfileImage() {
        int selectedAvator = assassin.getPlayer().getAvatar();
        ImageView profile_image = (ImageView) findViewById(R.id.profile_image);
        if (selectedAvator == 1) {
            profile_image.setImageResource(R.drawable.avator1);
        } else if (selectedAvator == 2) {
            profile_image.setImageResource(R.drawable.avator2);
        } else if (selectedAvator == 3) {
            profile_image.setImageResource(R.drawable.avator3);
        } else if (selectedAvator == 4) {
            profile_image.setImageResource(R.drawable.avator4);
        } else if (selectedAvator == 5) {
            profile_image.setImageResource(R.drawable.avator5);
        } else if (selectedAvator == 6) {
            profile_image.setImageResource(R.drawable.avator6);
        } else if (selectedAvator == 7) {
            profile_image.setImageResource(R.drawable.avator7);
        } else if (selectedAvator == 8) {
            profile_image.setImageResource(R.drawable.avator8);
        }
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptJoinGroup() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mGroupView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mGroupView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfimView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(email)) {
            mGroupView.setError(getString(R.string.error_field_required));
            focusView = mGroupView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if(toggleCreateGroup && !password.equals(passwordConfirm)) {
            mPasswordConfimView.setError(getString(R.string.error_password_mismatch));
            focusView = mPasswordConfimView;
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
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void addGroupsToAutoComplete() {
        assassin.setGroupListener(this);
        groupAutoCompleteAdapter =
                new ArrayAdapter<String>(JoinGroupActivity.this,
                        android.R.layout.simple_dropdown_item_1line, groupNames);
        mGroupView.setAdapter(groupAutoCompleteAdapter);
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

    // Callbacks for handling responses from firebase
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        groupNames.clear();
        for(DataSnapshot group: dataSnapshot.getChildren()) {
            groupNames.add(group.getKey().toString());
        }
        groupAutoCompleteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> implements Assassin.OnJoinGroupListener {

        private final String mGroupName;
        private final String mGroupPassword;

        UserLoginTask(String groupName, String groupPassword) {
            mGroupName = groupName;
            mGroupPassword = groupPassword;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            assassin.setOnJoinGroupListener(this);
            // join group
            if(toggleCreateGroup) {
                assassin.createGroup(mGroupName, mGroupPassword);
            }
            assassin.joinGroup(mGroupName, mGroupPassword);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
        }

        private void startMapsActivity() {
            startActivity(new Intent(JoinGroupActivity.this, MainActivity.class));
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        @Override
        public void onJoinGroupSuccess() {
            showProgress(false);
            startMapsActivity();
        }

        @Override
        public void onJoinGroupError(String error) {
            showProgress(false);
            mPasswordView.setError(error);
            mPasswordView.requestFocus();
        }
    }
}

