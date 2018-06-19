package singh.pk.todoappdemo.ui.onboarding.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.todoappdemo.R;

/**Start Activity for user Login and Registration.
 * @author Pankaj*/
public class Start extends AppCompatActivity {

    // Views
    @BindView(R.id.start_login_btn) Button mLoginBtn;
    @BindView(R.id.start_reg_btn) Button mRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize ButterKnife.
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        // Open Register Activity.
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(Start.this, Register.class);
                startActivity(reg_intent);
            }
        });

        // Open LogIn Activity.
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login_intent = new Intent(Start.this, Login.class);
                startActivity(login_intent);

            }
        });
    }
}
