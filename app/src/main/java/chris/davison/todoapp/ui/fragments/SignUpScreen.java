package chris.davison.todoapp.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import chris.davison.todoapp.models.User;
import chris.davison.todoapp.utils.AccountValidation;
import chris.davison.todoapp.R;

public class SignUpScreen extends Fragment implements View.OnClickListener {
    private final User user = new User();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MaterialButton signUpBtn;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText passwordConfirmationInput;

    public SignUpScreen() {}

    public static SignUpScreen newInstance() {
        return new SignUpScreen();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_screen, container, false);

        initViews(view);
        setUpListeners();

        return view;
    }

    public void initViews(View view) {
        signUpBtn = view.findViewById(R.id.signUpBtn);
        emailInput = view.findViewById(R.id.signUpEmailTiet);
        passwordInput = view.findViewById(R.id.signUpPasswordTiet);
        passwordConfirmationInput = view.findViewById(R.id.signUpConfirmPasswTiet);
    }

    public void setUpListeners() {
        signUpBtn.setOnClickListener(this);

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                user.setEmail(editable.toString().trim().toLowerCase());
            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                user.setPassword(editable.toString().trim());
            }
        });

        passwordConfirmationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                user.setPasswordConfirmation(editable.toString().trim());
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.signUpBtn) {
            newUser();
        }
    }

    private void newUser() {
        String isEmailValid = AccountValidation.validateEmail(user.getEmail());
        if (!isEmailValid.isEmpty()) {
            Toast.makeText(getContext(), isEmailValid,
                    Toast.LENGTH_LONG).show();
            return;
        }

        String isPasswordValid = AccountValidation.validatePassword(user.getPassword(),
                user.getPasswordConfirmation());
        if (!isPasswordValid.isEmpty()) {
            Toast.makeText(getContext(), isPasswordValid,
                    Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Navigation.findNavController(requireView())
                                    .navigate(R.id.action_signUpScreen_to_myListScreen);
                        }
                        else {
                            Toast.makeText(getContext(), "Authentication Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}