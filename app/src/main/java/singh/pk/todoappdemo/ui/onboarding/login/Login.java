package singh.pk.todoappdemo.ui.onboarding.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.todoappdemo.R;
import singh.pk.todoappdemo.ui.MainActivity;

public class Login extends AppCompatActivity {

    // Views
    @BindView(R.id.login_email) TextInputLayout mLoginEmail;
    @BindView(R.id.login_password) TextInputLayout mLoginPassword;
    @BindView(R.id.login_btn) Button mLogin_btn;
    @BindView(R.id.login_toolbar) Toolbar mToolbar;
    // ProgressDialog object.
    private ProgressDialog mLoginProgress;
    // Firebase Auth Object.
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ButterKnife.
        ButterKnife.bind(this);
        // Create Firebase Auth Instance.
        mAuth = FirebaseAuth.getInstance();
        // Initialize ProgressDialog.
        mLoginProgress = new ProgressDialog(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBar));

        initViews();

    }

    private void initViews() {

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    mLoginProgress.dismiss();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    mLoginProgress.hide();
                    Toast.makeText(Login.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
