package com.dev.testproject.ui;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dev.testproject.R;
import com.dev.testproject.adapter.PhotoAdapter;
import com.dev.testproject.model.PhotoItem;
import com.dev.testproject.util.BitmapUtile;
import com.dev.testproject.viewmodel.MainActivityViewModel;
import com.dev.testproject.views.RecyclerDecoration;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.floating_button)
    FloatingActionButton floatingActionButton;
    PhotoAdapter photoAdapter;
    FirebaseFirestore db;
    MainActivityViewModel viewModel;
    StorageReference storageReference;
    private Uri mUri;
    UploadTask uploadTask;
    String mDownloadUri;
    MainListener mainListener;

    ActivityResultLauncher<Intent> loadPhotoResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDecoration(2, 10, true));
        viewModel = new ViewModelProvider(MainActivity.this).get(MainActivityViewModel.class);
        viewModel.getPhotoList();
        viewModel.photoItemMutableLiveData.observe(this, new Observer<List<PhotoItem>>() {
            @Override
            public void onChanged(List<PhotoItem> photoItems) {
                photoAdapter = new PhotoAdapter(getBaseContext(), (ArrayList<PhotoItem>) photoItems);
                GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(),2);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(photoAdapter);
                photoAdapter.notifyDataSetChanged();
            }
        });
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("photos");
        floatingActionButton.setOnClickListener(view -> { showAddDialog(); });

        loadPhotoResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mainListener.onGalleryResult(result);
                    }
                }
        );
    }

    private void showAddDialog(){
        final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MainActivity.this);
        View Alertview = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_photo_item_dialog,null);

        alertDialogBuilder.setView(Alertview);
        final AlertDialog alertDialog=alertDialogBuilder.show();

        EditText photographerET = Alertview.findViewById(R.id.photographer_et);
        EditText locationET = Alertview.findViewById(R.id.location_et);
        TextView loadImageTV = Alertview.findViewById(R.id.load_image);
        TextView addPhotoTV = Alertview.findViewById(R.id.add_photo_btn);
        ImageView closeDialog = Alertview.findViewById(R.id.close_dialog);
        ShapeableImageView dialogPhoto = Alertview.findViewById(R.id.photo);

        setOnMaiinListener(new MainListener() {
            @Override
            public void onGalleryResult(ActivityResult result) {
                Intent intent2 = result.getData();
                Uri uri = intent2.getData();
                mUri = uri;
                if (dialogPhoto != null) Glide.with(getBaseContext()).load(uri).fitCenter().into(dialogPhoto);
            }
        });
        loadImageTV.setOnClickListener(view -> {
            if (checkPermission()) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                loadPhotoResultLauncher.launch(intent);
            }
        });

        addPhotoTV.setOnClickListener(view -> {
            if (mUri != null) {
                final StorageReference ref = storageReference.child(mUri.getLastPathSegment()+"images" + ".jpg");

                uploadTask = ref.putBytes(BitmapUtile.convertIMGtoBYTEbyIV(dialogPhoto));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()){
                                    throw task.getException();
                                }
                                mDownloadUri = ref.getDownloadUrl().toString();
                                return ref.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()){
                                    mDownloadUri = task.getResult().toString();
                                    applyData(photographerET, locationET);
                                }else{

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            alertDialog.dismiss();
            viewModel.getPhotoList();
            photoAdapter.notifyDataSetChanged();
        });

        closeDialog.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void applyData(TextView photographer, TextView location){
        photoAdapter.add(new PhotoItem(mDownloadUri, location.getText().toString(), photographer.getText().toString()));
        Map<String, Object> addImageMap = new HashMap<>();
        if (mDownloadUri != null)addImageMap.put("image", mDownloadUri);
        addImageMap.put("location", location.getText().toString());
        addImageMap.put("photographer", photographer.getText().toString());
        Log.d("weweadd", "mDownloadUri" + mDownloadUri);
        db.collection("photos").add(addImageMap);
    }

    interface MainListener{
        void onGalleryResult(ActivityResult result);
    }

    public void setOnMaiinListener(MainListener mainListener){
        this.mainListener = mainListener;
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {

                }
            });

    public boolean checkPermission(){
        int storage = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (storage != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch( Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return false;
        }else {
            return true;
        }
    }
}