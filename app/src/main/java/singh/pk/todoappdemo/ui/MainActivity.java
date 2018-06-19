package singh.pk.todoappdemo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.todoappdemo.R;
import singh.pk.todoappdemo.ui.add_todo_item.AddTodoItem;
import singh.pk.todoappdemo.ui.onboarding.login.Start;
import singh.pk.todoappdemo.ui.pojo.Category;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.category_recycler_view) RecyclerView categoryRecyvlerView;
    @BindView(R.id.add_category_btn) FloatingActionButton addCategoryBtn;
    @BindView(R.id.main_page_toolbar) Toolbar mToolbar;
    @BindView(R.id.progress_bar_main_activity) ProgressBar mProgressBar;

    // ProgressDialog Object
    private ProgressDialog mRegProgress;

    // Firebase Auth
    private FirebaseAuth mAuth;

    // Firebase Database reference object.
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseForFetch;
    private DatabaseReference mDatabaseTodos;

    FirebaseUser current_user;
    String current_uid;

    boolean categoryAble = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DI inject
        ButterKnife.bind(this);

        // Initialize ProgressDialog.
        mRegProgress = new ProgressDialog(this);


        // Create Instance of FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            current_uid =  current_user.getUid();
        } catch (RuntimeException t) {
            Log.e("MainActivity", t.getMessage());
        }
        try {
            mDatabaseForFetch = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid)
                    .child("categories");
        } catch (RuntimeException t) {
            Log.e("MainActivity", t.getMessage());
        }

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid)
                    .child("categories");



        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Category");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBar));

        // RecyclerView
        categoryRecyvlerView.setHasFixedSize(true);
        categoryRecyvlerView.setLayoutManager(new LinearLayoutManager(this));

        showProgress();

        initView();


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get current login user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            sendToStart();
        }
        if (currentUser != null) {
        FirebaseRecyclerAdapter<Category, CategoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.item_category,
                CategoryViewHolder.class,
                mDatabaseForFetch
        ) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, final Category model, int position) {
                viewHolder.setCategoryName(model.getCategoryName());
                viewHolder.setTodoCount(model.getTodo_count());

                removeProgress();

                final String categoryName = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent todos = new Intent(MainActivity.this, AddTodoItem.class);
                        todos.putExtra("category_id", categoryName);
                        startActivity(todos);
                    }
                });


            }
        };

        removeProgress();
        categoryRecyvlerView.setAdapter(firebaseRecyclerAdapter);

    }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        View mView;
        @BindView(R.id.category_name) TextView categoryName;
        @BindView(R.id.todos_item_count) TextView totalTodo;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }

        public void setCategoryName(String categoryName){
            this.categoryName.setText(categoryName);
        }

        public void setTodoCount(String countTodo) {
            this.totalTodo.setText(countTodo);
        }
    }

    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, Start.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        return true;
    }

    private void initView() {

        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
                final EditText addCategoryEdit = mView.findViewById(R.id.add_category_edit);
                AppCompatButton cencelCategoryBtn = mView.findViewById(R.id.cancel_category_btn);
                AppCompatButton addCategoryBtn = mView.findViewById(R.id.add_category_btn);

                addCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!addCategoryEdit.getText().toString().isEmpty()) {
                            final String categoryName = addCategoryEdit.getText().toString();

                            showProgress();
                            mDatabaseForFetch.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    holderCategoryName((Map<String, Object>) dataSnapshot.getValue(), categoryName);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

//                            Toast.makeText(MainActivity.this, "No save", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cencelCategoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();

            }
        });
    }

    private void holderCategoryName(Map<String, Object> value, String categoryNameEdit) {

        ArrayList<String> categoryName = new ArrayList<>(0);

        if (categoryName.size()!=0){
            for (Map.Entry<String, Object> entry : value.entrySet()) {
                Map categary = (Map) entry.getValue();
                categoryName.add((String) categary.get("category_name"));
            }

            for (int i = 0; i<categoryName.size(); i++) {
                if (categoryNameEdit.equals(categoryName.get(i))){
                    categoryAble = false;
                    removeProgress();
                    break;
                }
            }
        } else if (categoryAble){
            HashMap<String, String> categoryMap = new HashMap<>();
            categoryMap.put("category_name", categoryNameEdit);
            categoryMap.put("todo_count", "Places add todo.");
            mDatabase.push().setValue(categoryMap);
        }
        categoryAble = true;




        removeProgress();
    }

    public void showProgress() { mProgressBar.setVisibility(View.VISIBLE); }
    public void removeProgress() { mProgressBar.setVisibility(View.GONE); }
}
