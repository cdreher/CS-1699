package com.group10.photoeditor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageService extends Service {

    private final IBinder binder = new ImageBinder();
    private HashMap<String, Image> images;

    static String userID;

    @Override
    public void onCreate() {
        super.onCreate();
        readFromDisc();
        readFromFireBase();
    }

    private void readFromDisc() {
        images = new HashMap<>();
        try {
            ObjectInputStream objectIn = null;
            try {
                File file = new File(getFilesDir(), "images-" + userID);
                FileInputStream fileIn = new FileInputStream(file);
                objectIn = new ObjectInputStream(fileIn);
                while (true) {
                    Image image = (Image) objectIn.readObject();
                    images.put(image.getName(), image);
                }
            } catch (EOFException e) {
                if (objectIn != null) objectIn.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e("IOerror", e.toString());
            return;
        }
    }

    private void readFromFireBase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("images-" + userID);
        reference.getBytes(1024l * 1024l * 1024l).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                FileOutputStream outputStream = null;
                try {
                    File file = new File(getFilesDir(), "images-" + userID);
                    file.createNewFile();
                    outputStream = new FileOutputStream(file);
                    outputStream.write(bytes);
                    outputStream.close();
                } catch (IOException e) {
                    Log.e("IOerror", e.toString());
                    return;
                }
                readFromDisc();
            }
        });
    }

    private void writeToDisc() {
        try {
            File file = new File(getFilesDir(), "images-" + userID);
            file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file, false);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            for (String key : images.keySet()) {
                objectOut.writeObject(images.get(key));
            }
            objectOut.close();
        } catch (IOException e) {
            Log.e("IOerror", e.toString());
            return;
        }
        writeToFireBase();
    }

    private void writeToFireBase() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("images-" + userID);
        FileInputStream fileIn = null;
        try {
            File file = new File(getFilesDir(), "images-" + userID);
            fileIn = new FileInputStream(file);
        } catch (IOException e) {
            Log.e("IOerror", e.toString());
            return;
        }
        reference.putStream(fileIn);
    }

    public Image getImage(String name) {
        Image image = images.get(name);
        if (image == null) return null;
        return image;
    }

    public Image getRandomImage() {
        ArrayList<Image> imageList = new ArrayList<>();
        for (String key : images.keySet())
                imageList.add(images.get(key));
        if (imageList.size() == 0) return null;
        return imageList.get((int) (Math.random() * imageList.size()));
    }

    public boolean addImage(Image image) {
        if (images.containsKey(image.getName())) return false;
        images.put(image.getName(), image);
        writeToDisc();
        return true;
    }

    public boolean updateImage(String oldName, Image newImage) {
        if (!images.containsKey(oldName)) return false;
        images.remove(oldName);
        images.put(newImage.getName(), newImage);
        writeToDisc();
        return true;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ImageBinder extends Binder {
        ImageService getService() {
            return ImageService.this;
        }
    }
}