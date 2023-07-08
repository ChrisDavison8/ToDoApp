package chris.davison.todoapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import chris.davison.todoapp.R;

public class ConfirmSignOut extends Fragment implements View.OnClickListener {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MaterialButton confirmYesBtn;
    private MaterialButton confirmNoBtn;

    public ConfirmSignOut() {
        // Required empty public constructor
    }

    public static ConfirmSignOut newInstance() {
        ConfirmSignOut fragment = new ConfirmSignOut();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_sign_out, container, false);

        initViews(view);
        setUpListeners();

        return view;
    }

    public void initViews(View view) {
        confirmYesBtn = view.findViewById(R.id.confirmSignOutYesBtn);
        confirmNoBtn = view.findViewById(R.id.confirmSignOutNoBtn);
    }

    public void setUpListeners() {
        confirmYesBtn.setOnClickListener(this);
        confirmNoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.confirmSignOutYesBtn) {
            mAuth.signOut();
            Navigation.findNavController(v).navigate(R.id.action_confirmSignOut_to_splashScreen);
        } else if (id == R.id.confirmSignOutNoBtn) {
            Navigation.findNavController(v).navigate(R.id.action_confirmSignOut_to_myListScreen);
        }
    }
}