package singh.pk.todoappdemo.ui.add_todo_item;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import singh.pk.todoappdemo.R;
import singh.pk.todoappdemo.ui.MainActivity;
import singh.pk.todoappdemo.ui.add_todo_item.todo_info.TodoInfo;

public class AddTodoItem extends AppCompatActivity {

    @BindView(R.id.add_todo_btn) FloatingActionButton addTodoInfo;
    @BindView(R.id.todo_recycler_view) RecyclerView todoRecyclerView;
    @BindView(R.id.back_image_view_btn) ImageView backImageBtn;
    @BindView(R.id.category_name_top_tool_bar_text) TextView categoryNameTopToolBar;
    // ProgressDialog Object
    private ProgressDialog mRegProgress;

    // Firebase Auth
    private FirebaseAuth mAuth;

    // Firebase Database reference object.
    private DatabaseReference mDatabase;

    FirebaseUser current_user;
    String current_uid;

    String category_id;
    String categoryName;

    int totalTodoCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_item);

        //DI inject
        ButterKnife.bind(this);

        category_id = getIntent().getStringExtra("category_id");
        categoryName = getIntent().getStringExtra("category_name");

        categoryNameTopToolBar.setText(categoryName);

        // Initialize ProgressDialog.
        mRegProgress = new ProgressDialog(this);

        // Create Instance of FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_uid =  current_user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid)
                .child("todo").child(category_id);

        // RecyclerView
        todoRecyclerView.setHasFixedSize(true);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<TodoPojo, TodoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<TodoPojo, TodoViewHolder>(
                TodoPojo.class,
                R.layout.item_todo_list,
                TodoViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(TodoViewHolder viewHolder, TodoPojo model, int position) {
                viewHolder.setTodoName(model.getTodo_name());
                viewHolder.setTodoStatus(model.getTodo_status());
                viewHolder.setTodoImage(model.getThumb_image());
                todoCount(position);

                final String todoUid = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent todos = new Intent(AddTodoItem.this, TodoInfo.class);
                        todos.putExtra("todo_id", todoUid);
                        todos.putExtra("todo_category_id", category_id);
                        todos.putExtra("update_todo", "UPDATE_TODO");
                        startActivity(todos);
                    }
                });

            }
        };


        todoRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void todoCount(int position) {
        totalTodoCount = position + 2;
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {

        View mView;
        @BindView(R.id.todo_item_image) ImageView todoImage;
        @BindView(R.id.todo_item_name) TextView todoName;
        @BindView(R.id.todo_item_status) TextView todoStatus;

        public TodoViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            ButterKnife.bind(this, itemView);

        }

        public void setTodoName(String todoName){
            this.todoName.setText(todoName);
        }

        public void setTodoStatus(String todoStatus) {

            if (todoStatus.equals("true")){
                this.todoStatus.setText("Done");
                this.todoStatus.setTextColor(Color.parseColor("#C51162"));
            } else {
                this.todoStatus.setText("Pending");
            }

        }

        public void setTodoImage(String todoImageUrl) {
            Picasso.get().load(todoImageUrl).placeholder(R.color.colorImageView).into(todoImage);
        }


    }

    private void initViews() {
        addTodoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTodoItem.this, TodoInfo.class);
                intent.putExtra("category_id", category_id);
                intent.putExtra("todo_count", String.valueOf(totalTodoCount));
                startActivity(intent);

                Toast.makeText(AddTodoItem.this, ""+totalTodoCount, Toast.LENGTH_SHORT).show();


            }
        });

        backImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
