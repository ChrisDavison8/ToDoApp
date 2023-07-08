package chris.davison.todoapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import chris.davison.todoapp.R;

public class WelcomeScreen extends Fragment implements View.OnClickListener {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MaterialButton signInBtn;
    private MaterialButton signUpBtn;

    public WelcomeScreen() {}

    public static WelcomeScreen newInstance() {
        WelcomeScreen fragment = new WelcomeScreen();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_screen, container, false);

        initViews(view);
        setUpListeners();

        return view;
    }

    public void initViews(View view) {
        MaterialToolbar materialToolbar = requireActivity().findViewById(R.id.mainActivityTlbr);
        materialToolbar.setNavigationIcon(null);
        materialToolbar.setVisibility(View.VISIBLE);
        signUpBtn = view.findViewById(R.id.welcomeSignUpBtn);
        signInBtn = view.findViewById(R.id.welcomeSignInBtn);
    }

    public void setUpListeners() {
        signUpBtn.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkForUser();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.welcomeSignInBtn) {
            Navigation.findNavController(view).navigate(R.id.action_welcomeScreen_to_signInScreen);
        }
        else if (id == R.id.welcomeSignUpBtn) {
            Navigation.findNavController(view).navigate(R.id.action_welcomeScreen_to_signUpScreen);
        }
    }

    public void checkForUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_welcomeScreen_to_myListScreen);
        }
    }
}