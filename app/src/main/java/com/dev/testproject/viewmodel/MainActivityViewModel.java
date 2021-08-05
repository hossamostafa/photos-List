package com.dev.testproject.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dev.testproject.model.PhotoItem;
import com.dev.testproject.repository.PhotoRepository;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    public MutableLiveData<List<PhotoItem>> photoItemMutableLiveData ;
    PhotoRepository repository;

    public MainActivityViewModel() {
        repository = new PhotoRepository();
    }

    public MutableLiveData<List<PhotoItem>> getPhotoList(){
        if (photoItemMutableLiveData == null){
            photoItemMutableLiveData = new MutableLiveData<>();
            repository.setOnReturnValueListener(new PhotoRepository.ReturnValueListener() {
                @Override
                public void returnList(ArrayList<PhotoItem> list) {
                    getPhotoData();
                }
            });
        }
        return photoItemMutableLiveData;
    }

    private void getPhotoData(){
        List<PhotoItem> list= repository.getPhotoList();
        if (list != null && list.size() > 0) photoItemMutableLiveData.setValue(list);

    }
}
