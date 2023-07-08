package chris.davison.todoapp.ui.fragments;

import static com.google.android.material.color.MaterialColors.ALPHA_FULL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import chris.davison.todoapp.R;
import chris.davison.todoapp.interfaces.TodoDeletedCallback;
import chris.davison.todoapp.adapters.MyListAdapter;

public class MyListScreen extends Fragment implements View.OnClickListener, TodoDeletedCallback {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userID;
    private RecyclerView recyclerView;
    private Spinner usersListsSpinner;
    private FloatingActionButton floatingActionButton;
    private String selectedList;
    private MyListAdapter adapter;

    public MyListScreen() {}

    public static MyListScreen newInstance() {
        MyListScreen fragment = new MyListScreen();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_list_screen, container, false);

        initViews(view);
        setUpListeners();
        initialiseSpinnerAdapter(view);
        setUpRecyclerView();

        return view;
    }

    public void initViews(View view) {
        MaterialToolbar materialToolbar = requireActivity().findViewById(R.id.mainActivityTlbr);
        materialToolbar.setNavigationIcon(R.drawable.menu_icon_24);
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.mainActivityMenuDl);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        userID = mAuth.getUid();
        recyclerView = view.findViewById(R.id.myListListItemsRv);
        usersListsSpinner = view.findViewById(R.id.myListTodoListsSpnr);
        floatingActionButton = view.findViewById(R.id.myListCreateTodoFab);
    }

    public void setUpListeners() {
        Animation fabAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce);
        floatingActionButton.startAnimation(fabAnim);
        floatingActionButton.setOnClickListener(this);

        usersListsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedList = adapterView.getItemAtPosition(i).toString().trim();
                if (!selectedList.equals("No selection")) {
                    updateRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    adapter.deleteItem(viewHolder.getBindingAdapterPosition());
                }
                else if (direction == ItemTouchHelper.RIGHT) {
                    adapter.markItemDone(viewHolder.getBindingAdapterPosition());
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState,
                                    boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
                    Bitmap bitmap;
                    if (dX > 0) {
                        bitmap = BitmapFactory.decodeResource(requireContext().getResources(),
                                R.drawable.done_item_45x45);
                        paint.setColor(ContextCompat.getColor(requireActivity().getBaseContext(),
                                R.color.green));
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), paint);
                        c.drawBitmap(bitmap, (float) itemView.getLeft() + 16,
                                (float) itemView.getTop() + ((float) itemView.getBottom() -
                                        (float) itemView.getTop() - bitmap.getHeight())/2, paint);
                    }
                    else if (dX < 0) {
                        bitmap = BitmapFactory.decodeResource(requireContext().getResources(),
                                R.drawable.trash_icon_35x35);
                        paint.setColor(ContextCompat.getColor(requireActivity().getBaseContext(),
                                R.color.red));
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                        c.drawBitmap(bitmap,
                                (float) itemView.getRight() - 16 - bitmap.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() -
                                        (float) itemView.getTop() - bitmap.getHeight())/2, paint);
                    }

                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.myListCreateTodoFab) {
            Navigation.findNavController(view).navigate(R.id.action_myListScreen_to_createItemScreen);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initialiseSpinnerAdapter(View view) {
        db.collection(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        if (queryDocumentSnapshots.isEmpty()) {
                            arrayList.add("No selection");
                            TextView textView = view.findViewById(R.id.myListNoListsTv);
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            TextView textView = view.findViewById(R.id.myListNoListsTv);
                            textView.setVisibility(View.GONE);
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (!arrayList.contains(Objects.requireNonNull(documentSnapshot
                                        .get("list")).toString())) {
                                    arrayList.add(Objects.requireNonNull(documentSnapshot
                                            .get("list")).toString());
                                }
                            }
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                                (view.getContext(), R.layout.spinner_list, arrayList);
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_list);
                        usersListsSpinner.setAdapter(arrayAdapter);
                    }
                });
    }

    public void setUpRecyclerView() {
        Query query = db.collection(userID)
                .whereEqualTo("list", selectedList);
        FirestoreRecyclerOptions<TodoItem> options =
                new FirestoreRecyclerOptions.Builder<TodoItem>().setQuery(query, TodoItem.class)
                        .build();
        adapter = new MyListAdapter(options, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void updateRecyclerView() {
        Query query = db.collection(userID)
                .whereEqualTo("list", selectedList);
        FirestoreRecyclerOptions<TodoItem> newOptions = new FirestoreRecyclerOptions.Builder<TodoItem>()
                .setQuery(query, TodoItem.class).build();
        adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
        adapter.updateOptions(newOptions);
    }

    @Override
    public void deleteCallback() {
        initialiseSpinnerAdapter(requireView());
    }
}