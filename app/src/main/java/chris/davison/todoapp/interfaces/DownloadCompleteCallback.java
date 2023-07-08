package chris.davison.todoapp.interfaces;

import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

public interface DownloadCompleteCallback {
    void downloadComplete(Bitmap bitmap, RecyclerView.ViewHolder holder);
}
