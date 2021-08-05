package com.dev.testproject.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dev.testproject.model.PhotoItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PhotoRepository {
    ArrayList<PhotoItem> photoItems ;
    FirebaseFirestore firestore;
    ReturnValueListener returnValueListener;
    public PhotoRepository() {
        photoItems = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        fetchList();
    }

    public ArrayList<PhotoItem> getPhotoList(){
        return photoItems;
    }

    public interface ReturnValueListener{
         void returnList(ArrayList<PhotoItem> list);
    }

    public void setOnReturnValueListener(ReturnValueListener returnValueListener){
        this.returnValueListener = returnValueListener;
    }

    public void fetchList(){
        firestore.collection("photos").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                Log.d("wewe", "toObject image: "+documentSnapshot.toObject(PhotoItem.class).getImage());
                                Log.d("wewe", "toObject location: "+documentSnapshot.toObject(PhotoItem.class).getLocation());
                                Log.d("wewe", "toObject photographer: "+documentSnapshot.toObject(PhotoItem.class).getPhotographer());
                                Log.d("wewe", "toObject ________________: ");
                                PhotoItem photoItem = documentSnapshot.toObject(PhotoItem.class);
                                photoItems.add(photoItem);
                            }
                            returnValueListener.returnList(photoItems);

                        }else {

                        }
                    }
                });
    }
}
