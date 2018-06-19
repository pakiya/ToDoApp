package singh.pk.todoappdemo.ui.add_todo_item.todo_info;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import singh.pk.todoappdemo.R;

public class TodoInfo extends AppCompatActivity {

    @BindView(R.id.todo_info_delete_btn) AppCompatButton deleteTodoInfoBtn;
    @BindView(R.id.todo_info_save_btn) AppCompatButton saveTodoInfoBtn;
    @BindView(R.id.add_image_btn) AppCompatButton addImageBtn;
    @BindView(R.id.category_name_top_tool_bar_text) TextView categoyNameTxt;
    @BindView(R.id.todo_item_image) ImageView todoImageView;
    @BindView(R.id.todo_item_name_edit) EditText todoItemNameEdit;
    @BindView(R.id.todo_item_description_edit) EditText todoItemDescription;
    @BindView(R.id.todo_complete_switch) Switch todoCompleteSwitch;
    @BindView(R.id.back_image_view_btn) ImageView backBtn;

    // ProgressDialog Object
    private ProgressDialog mRegProgress;

    private FirebaseAuth mAuth;

    // Firebase Database reference object.
    private DatabaseReference mDatabase;
    // Firebase Database reference object.
    private DatabaseReference mDatabaseCategory;

    private DatabaseReference mDatabaseTodoUpdate;

    private StorageReference mImageStorage;

    FirebaseUser current_user;
    String current_uid;

    String todo_id = null;
    String category_id = null;
    String category_id_for_todo = null;

    boolean todoCompleteStatus = false;

    String todoCount;
    String updateTodo;

    private static final int GALLERY_PICK = 1;
    Bitmap thumb_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_info);

        ButterKnife.bind(this);

        category_id = getIntent().getStringExtra("category_id");

        todoCount = getIntent().getStringExtra("todo_count");

        todo_id = getIntent().getStringExtra("todo_id");
        category_id_for_todo = getIntent().getStringExtra("todo_category_id");
        updateTodo = getIntent().getStringExtra("update_todo");


        try {
            if (updateTodo.equals("UPDATE_TODO")){

                deleteTodoInfoBtn.setVisibility(View.VISIBLE);
                todoImageView.setVisibility(View.VISIBLE);
                addImageBtn.setText("CHANGE PHOTO");
                categoyNameTxt.setText("Edit item");

            }
        } catch (RuntimeException t) {
            Log.e("TodoInfo", t.getMessage());
        }


        // Initialize ProgressDialog.
        mRegProgress = new ProgressDialog(this);

        // Create Instance of FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();

        mImageStorage = FirebaseStorage.getInstance().getReference();

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_uid =  current_user.getUid();

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid)
                    .child("todo").child(category_id).push();
        } catch (RuntimeException t){
            Log.d("DataReference", t.getMessage());
        }

        try {
            mDatabaseTodoUpdate = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid)
                    .child("todo").child(category_id_for_todo).child(todo_id);
        } catch (RuntimeException t) {
            Log.d("DataReference", t.getMessage());
        }



        Log.d("TodoStatus", " To Do default status :::: "+todoCompleteStatus);

        initViews();

    }

    private void initViews() {

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        todoCompleteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    todoCompleteStatus = true;
                    Log.d("TodoStatus", " To Do is Complete :::: "+todoCompleteStatus);
                } else {
                    todoCompleteStatus = false;
                    Log.d("TodoStatus", " To Do is Un Complete :::: "+todoCompleteStatus);
                }
            }
        });

        saveTodoInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String todoName = todoItemNameEdit.getText().toString();
                String todoDescription = todoItemDescription.getText().toString();

                if (!todoName.isEmpty()){

                    if ( todo_id != null ){


                        mRegProgress.setTitle("Todo Update");
                        mRegProgress.setMessage("Please wait while we update your todo !");
                        mRegProgress.setCanceledOnTouchOutside(false);
                        mRegProgress.show();


                        Map update_Todo_hashMap = new HashMap();
                        update_Todo_hashMap.put("todo_name",todoName);
                        update_Todo_hashMap.put("todo_description", todoDescription);
                        update_Todo_hashMap.put("todo_status", String.valueOf(todoCompleteStatus));
                        update_Todo_hashMap.put("todo_image", "default");
                        update_Todo_hashMap.put("thumb_image", "default");

                        mDatabaseTodoUpdate.updateChildren(update_Todo_hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if (task.isSuccessful()){
                                    mRegProgress.dismiss();
                                }
                            }
                        });

                    } else {

                        mRegProgress.setTitle("Todo Add");
                        mRegProgress.setMessage("Please wait while we create your todo !");
                        mRegProgress.setCanceledOnTouchOutside(false);
                        mRegProgress.show();

                        final HashMap<String, String> todoMap = new HashMap<>();
                        todoMap.put("todo_name", todoName);
                        todoMap.put("todo_description", todoDescription);
                        todoMap.put("todo_status", String.valueOf(todoCompleteStatus));
                        todoMap.put("todo_image", "default");
                        todoMap.put("thumb_image", "default");

                        mDatabase.setValue(todoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    mDatabaseCategory = FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(current_uid).child("categories").child(category_id);

                                    Map update_todoCountMap = new HashMap();
                                    update_todoCountMap.put("todo_count", todoCount);

                                    mDatabaseCategory.updateChildren(update_todoCountMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        mRegProgress.dismiss();
                                                    }
                                                }
                                            });

                                }
                            }
                        });


                    }

                } else {
                    Toast.makeText(TodoInfo.this, "Enter todo Name", Toast.LENGTH_SHORT).show();
                }

            }

        });

        deleteTodoInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mRegProgress.setTitle("Todo Delete");
                mRegProgress.setMessage("Please wait while we delete your todo !");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                mDatabaseTodoUpdate.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            mRegProgress.dismiss();
                        }
                    }
                });
            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mRegProgress.setTitle("Uploading Image...");
                mRegProgress.setMessage("Please wait while we upload and process the image.");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                String current_user_id = current_user.getUid();

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Todo Null point Exception of thumb_bitmap.
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("profile_images").child( current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("todo_image", download_url);
                                        update_hashMap.put("thumb_image", thumb_downloadUrl);

                                        mDatabaseTodoUpdate.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {

                                                if (task.isSuccessful()) {
                                                    mRegProgress.dismiss();
                                                    Toast.makeText(TodoInfo.this, "Success Uploading", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        mRegProgress.dismiss();
                                        Toast.makeText(TodoInfo.this, "Error in Uploading thumbnail.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            Toast.makeText(TodoInfo.this, "Working", Toast.LENGTH_SHORT).show();
                        } else {
                            mRegProgress.dismiss();
                            Toast.makeText(TodoInfo.this, "Error in Uploading", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = result.getError();

            }
        }
    }

}
