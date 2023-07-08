package chris.davison.todoapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import chris.davison.todoapp.interfaces.DownloadCompleteCallback;
import chris.davison.todoapp.utils.ImageTransfer;
import chris.davison.todoapp.R;
import chris.davison.todoapp.interfaces.TodoDeletedCallback;
import chris.davison.todoapp.ui.fragments.TodoItem;
import chris.davison.todoapp.ui.fragments.MyListScreen;

public class MyListAdapter extends FirestoreRecyclerAdapter<TodoItem, MyListAdapter.MyListHolder>
implements DownloadCompleteCallback {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    private final TodoDeletedCallback todoDeletedCallback;

    public MyListAdapter(@NonNull FirestoreRecyclerOptions<TodoItem> options, MyListScreen myListScreen) {
        super(options);
        todoDeletedCallback = myListScreen;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyListHolder holder, int position, @NonNull TodoItem model) {
        StorageReference imageReference = storageRef.child(model.getImageReference());
        ImageTransfer.imageDownload(this, holder, imageReference);

        holder.description.setText(model.getDescription());

        if (model.getMarkedDone().equals("true")) {
            holder.markedDone.setImageResource(R.drawable.mark_done_icon);
        }
        else {
            holder.markedDone.setImageResource(R.drawable.mark_not_done_icon);
        }
    }

    @NonNull
    @Override
    public MyListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_todo_item, parent, false);

        return new MyListHolder(view);
    }

    public void deleteItem(int position) {
        String imageID = Objects.requireNonNull(getSnapshots().getSnapshot(position)
                .get("imageReference")).toString();
        StorageReference itemImageRef = storageRef.child(imageID);
        itemImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        getSnapshots().getSnapshot(position).getReference().delete();
        todoDeletedCallback.deleteCallback();
    }

    public void markItemDone(int position) {
        String s = this.getItem(position).getMarkedDone();
        if (s.equals("false")) {
            getSnapshots().getSnapshot(position).getReference()
                    .update("markedDone", "true");
        }
        else {
            getSnapshots().getSnapshot(position).getReference()
                    .update("markedDone", "false");
        }
    }

    public void downloadComplete(Bitmap bitmap, RecyclerView.ViewHolder holder) {
        if (bitmap != null) {
            ((MyListHolder) holder).image.setImageBitmap(bitmap);
        }
    }

    class MyListHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView description;
        ImageView markedDone;

        public MyListHolder(@NonNull View itemView) {

            super(itemView);
            image = itemView.findViewById(R.id.listItemIv);
            description = itemView.findViewById(R.id.listItemDescriptionTv);
            markedDone = itemView.findViewById(R.id.listItemDoneIv);
        }
    }
}
