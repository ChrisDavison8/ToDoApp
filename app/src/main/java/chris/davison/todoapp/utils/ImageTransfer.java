package chris.davison.todoapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import chris.davison.todoapp.interfaces.DownloadCompleteCallback;
import chris.davison.todoapp.interfaces.UploadCompleteCallback;

public class ImageTransfer {

    private ImageTransfer() {}

    static public void imageUpload(ImageView imageView, StorageReference storageRef,
                                   String imageName, Fragment fragment) {
        StorageReference imageStorageRef = storageRef.child(imageName);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } catch (IllegalArgumentException e) {
            ((UploadCompleteCallback) fragment).uploadComplete(false);
        }

        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageStorageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ((UploadCompleteCallback) fragment).uploadComplete(true);
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    ((UploadCompleteCallback) fragment).uploadComplete(false);
                }
            });
    }

    static public void imageDownload(FirestoreRecyclerAdapter<?, ?> firestoreRecyclerAdapter,
                                     RecyclerView.ViewHolder holder,
                                     StorageReference imageStorageRef) {
        final long oneMegabyte = 1024 * 1024;
        imageStorageRef.getBytes(oneMegabyte).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IllegalArgumentException ignored) {

                }
                ((DownloadCompleteCallback) firestoreRecyclerAdapter)
                        .downloadComplete(bitmap, holder);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Bitmap bitmap = null;
                ((DownloadCompleteCallback) firestoreRecyclerAdapter)
                        .downloadComplete(null, holder);
            }
        });
    }
}