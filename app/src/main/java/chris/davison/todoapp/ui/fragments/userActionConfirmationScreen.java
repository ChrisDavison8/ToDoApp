package chris.davison.todoapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import chris.davison.todoapp.R;

public class userActionConfirmationScreen extends Fragment implements View.OnClickListener {

    private TextView confirmMessageTextView;
    private String actionMessage;
    private MaterialButton userActionButton;

    public userActionConfirmationScreen() {}

    public static userActionConfirmationScreen newInstance() {
        return new userActionConfirmationScreen();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actionMessage = getArguments().getString("message");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_action_confirmation_screen,
                container, false);

        initViews(view);
        setUpListeners();

        return view;
    }

    public void initViews(View view) {
        confirmMessageTextView = view.findViewById(R.id.userActionConfTv);
        confirmMessageTextView.setText(actionMessage);
        userActionButton = view.findViewById(R.id.userActionConfBtn);
    }

    public void setUpListeners() {
        userActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.userActionConfBtn) {
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_userActionConfirmationScreen_to_welcomeScreen);
        }
    }
}