package chris.davison.todoapp.ui.fragments;

import static android.content.DialogInterface.BUTTON_POSITIVE;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import chris.davison.todoapp.R;
import chris.davison.todoapp.interfaces.UploadCompleteCallback;
import chris.davison.todoapp.utils.ImageTransfer;

public class CreateTodoScreen extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener, UploadCompleteCallback {
    private final int PERMISSION_REQUEST_CAMERA_CODE = 101;
    private final int PERMISSION_REQUEST_MEDIA_IMAGES = 102;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();
    private String userID;
    private ImageView itemImage;
    private MaterialButton takeBtn;
    private MaterialButton uploadBtn;
    private MaterialButton addBtn;
    private RadioGroup useNewOrExistingList;
    private TextInputEditText toDoDescriptionInputField;
    private TextInputEditText newListNameInputField;
    private Spinner selectList;
    private TextInputLayout newListNameTIL;
    private String toDoDescription;
    private String listName;
    private Uri imageUri;
    private Bitmap imageBitmap;
    private DocumentReference documentRef;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> libraryActivityResultLauncher;
    private ActivityResultLauncher<String> cameraRequestPermissionLauncher;
    private ActivityResultLauncher<String> mediaImagesRequestPermissionLauncher;

    public CreateTodoScreen() {}

    public static CreateTodoScreen newInstance() {
        CreateTodoScreen fragment = new CreateTodoScreen();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setIntentLaunchers();
        setPermissionRequestLaunchers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_todo_screen, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }

        initViews(view);
        setUpListeners();
        setSpinnerAdapter();

        return view;
    }

    private void initViews(View view) {
        takeBtn = view.findViewById(R.id.createTodoTakeImageBtn);
        uploadBtn = view.findViewById(R.id.createTodoimageUploadBtn);
        itemImage = view.findViewById(R.id.createTodoImageIv);
        useNewOrExistingList = view.findViewById(R.id.createTodoListChoiceRg);
        addBtn = view.findViewById(R.id.createTodoCreateBtn);
        toDoDescriptionInputField = view.findViewById(R.id.createToDoDescriptionTiet);
        newListNameTIL = view.findViewById(R.id.createTodoNewListNameTil);
        newListNameInputField = view.findViewById(R.id.createTodoNewListNameTiet);
        selectList = view.findViewById(R.id.createTodoListSpnr);
    }

    private void setUpListeners() {
        takeBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        toDoDescriptionInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                toDoDescription = editable.toString().trim();
            }
        });

        newListNameInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                listName = editable.toString().trim();
            }
        });

        selectList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listName = adapterView.getItemAtPosition(i).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                listName = "No selection";
            }
        });

        useNewOrExistingList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.createTodoExistingListRb) {
                    selectList.setVisibility(View.VISIBLE);
                    newListNameTIL.setVisibility(View.GONE);
                }
                else if (i == R.id.createTodoNewListRB) {
                    newListNameTIL.setVisibility(View.VISIBLE);
                    selectList.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setIntentLaunchers() {
        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bundle extras = data.getExtras();
                                imageBitmap = (Bitmap) extras.get("data");
                                itemImage.setImageBitmap(imageBitmap);
                            } else {
                                Toast.makeText(getContext(),
                                        "An error occurred loading the picture, " +
                                                "please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

        libraryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                imageUri = data.getData();
                                try {
                                    imageBitmap = MediaStore.Images.Media.getBitmap
                                            (requireContext().getContentResolver(), imageUri);
                                    itemImage.setImageBitmap(imageBitmap);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(),
                                            "An error occurred loading the image, " +
                                                    "please try again",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
    }

    /**
     * This setPermissionLaunchers method sets the launchers for permission requests with result
     * listeners.
     */
    public void setPermissionRequestLaunchers() {
        cameraRequestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraActivityResultLauncher.launch(intent);
                            } else {
                                Toast.makeText(getContext(), "Camera Permission required for " +
                                        "taking a photo", Toast.LENGTH_LONG).show();
                            }
                        });

        mediaImagesRequestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIntent.setType("image/*");
                                libraryActivityResultLauncher.launch(galleryIntent);
                            } else {
                                Toast.makeText(getContext(), "Storage Permission required for " +
                                        "selecting an image", Toast.LENGTH_LONG).show();
                            }
                        });
    }

    public void checkPermission(int permissionRequired) {
        switch (permissionRequired) {
            case PERMISSION_REQUEST_CAMERA_CODE:
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraActivityResultLauncher.launch(intent);
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    String message = getResources().getString(R.string.alert_dialog_camera);
                    new AlertDialog.Builder(requireContext())
                        .setMessage(message)
                        .setPositiveButton(getString(R.string.alert_dialog_ok_button), this)
                        .setNegativeButton(R.string.alert_dialog_cancel_button, null)
                        .create().show();
                } else {
                    cameraRequestPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
                break;
            case PERMISSION_REQUEST_MEDIA_IMAGES:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.READ_MEDIA_IMAGES) ==
                            PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        libraryActivityResultLauncher.launch(galleryIntent);
                    } else if (shouldShowRequestPermissionRationale(Manifest
                            .permission.READ_MEDIA_IMAGES)) {
                        String message = getResources().getString(R.string.alert_dialog_storage);
                        new AlertDialog.Builder(requireContext())
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.alert_dialog_ok_button), this)
                            .setNegativeButton(R.string.alert_dialog_cancel_button, null)
                            .create().show();
                    } else {
                        mediaImagesRequestPermissionLauncher
                                .launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        libraryActivityResultLauncher.launch(galleryIntent);
                    } else if (shouldShowRequestPermissionRationale(Manifest
                            .permission.READ_EXTERNAL_STORAGE)) {
                        String message = getResources().getString(R.string.alert_dialog_storage);
                        new AlertDialog.Builder(requireContext())
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.alert_dialog_ok_button), this)
                            .setNegativeButton(R.string.alert_dialog_cancel_button, null)
                            .create().show();
                    }else {
                        mediaImagesRequestPermissionLauncher
                                .launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.createTodoTakeImageBtn) {
            checkPermission(PERMISSION_REQUEST_CAMERA_CODE);
        } else if (id == R.id.createTodoimageUploadBtn) {
            checkPermission(PERMISSION_REQUEST_MEDIA_IMAGES);
        } else if (id == R.id.createTodoCreateBtn) {
            if (!validateInputFields()) {
                return;
            }
            addTodoToUsersList();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int i) {
        if (i == BUTTON_POSITIVE) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
            startActivity(intent);
        }
    }

    private void setSpinnerAdapter() {
        db.collection(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        if (queryDocumentSnapshots.isEmpty()) {
                            arrayList.add("No selection");
                        } else {
                            for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                if (!arrayList.contains(Objects.requireNonNull(documentSnapshot
                                        .get("list")).toString())) {
                                    arrayList.add(Objects.requireNonNull(documentSnapshot
                                            .get("list")).toString());
                                }
                            }
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                                (getContext(), R.layout.spinner_list, arrayList);
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_list);
                        selectList.setAdapter(arrayAdapter);
                    }
                });
    }

    private boolean validateInputFields() {
        if (toDoDescription == null || toDoDescription.equals("")) {
            Toast.makeText(getContext(), "ToDo description field cannot be empty",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else if (listName == null || listName.equals("") || listName.equals("No selection")) {
            Toast.makeText(getContext(), "No list has been selected for the ToDo",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            return true;
        }
    }

    private void addTodoToUsersList() {
        ProgressBar progressBar = requireView()
                .findViewById(R.id.createTodoUploadingPb);
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> myList = new HashMap<>();
        myList.put("owner", userID);
        myList.put("imageReference", "");
        myList.put("description", toDoDescription);
        myList.put("markedDone", "false");
        myList.put("list", listName);
        CreateTodoScreen createTodoScreen = this;
        db.collection(userID)
                .add(myList)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String imageStoragePath = userID + "/" + documentReference.getId();
                        ImageTransfer.imageUpload(itemImage, storageRef, imageStoragePath,
                                createTodoScreen);
                        documentRef = documentReference;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Something went wrong, please try again",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void uploadComplete(boolean b) {
        if (b) {
            String imageStoragePath = userID + "/" + documentRef.getId();
            documentRef.update("imageReference", imageStoragePath);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_createItemScreen_to_myListScreen);
        } else {
            ProgressBar progressBar = requireView()
                    .findViewById(R.id.createTodoUploadingPb);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "There was a problem uploading the image, please " +
                            "try again later",
                    Toast.LENGTH_LONG).show();
        }
    }
}