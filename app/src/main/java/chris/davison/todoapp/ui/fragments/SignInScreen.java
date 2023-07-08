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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import chris.davison.todoapp.models.User;
import chris.davison.todoapp.utils.AccountValidation;
import chris.davison.todoapp.R;

public class SignInScreen extends Fragment implements View.OnClickListener {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private final User user = new User();
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton signInBtn;
    private TextView forgotPass;

    public SignInScreen() {}

    public static SignInScreen newInstance() {
        return new SignInScreen();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in_screen, container, false);

        initViews(view);
        setUpListeners();

        return view;
    }

    public void initViews(View view) {
        emailEditText = view.findViewById(R.id.signInEmailTiet);
        passwordEditText = view.findViewById(R.id.signInPasswordTiet);
        signInBtn = view.findViewById(R.id.signInBtn);
        forgotPass = view.findViewById(R.id.signInForgotPassTv);
    }

    public void setUpListeners() {
        emailEditText.addTextChangedListener(new TextWatcher() {
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

        passwordEditText.addTextChangedListener(new TextWatcher() {
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

        signInBtn.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.signInBtn) {
            signIn();
        }
        else if (id == R.id.signInForgotPassTv) {
            sendPasswordResetEmail();
        }
    }

    private void signIn() {
        String isEmailValid = AccountValidation.validateEmail(user.getEmail());
        if (!isEmailValid.isEmpty()) {
            Toast.makeText(getContext(), isEmailValid,
                    Toast.LENGTH_LONG).show();
            return;
        }

        String isPasswordValid = AccountValidation.validatePassword(user.getPassword());
        if (!isPasswordValid.isEmpty()) {
            Toast.makeText(getContext(), isPasswordValid,
                    Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Navigation.findNavController(requireView()).
                                    navigate(R.id.action_signInScreen_to_myListScreen);
                        }
                        else {
                            Toast.makeText(getContext() ,"Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendPasswordResetEmail() {
        String isEmailValid = AccountValidation.validateEmail(user.getEmail());
        if (!isEmailValid.isEmpty()) {
            Toast.makeText(getContext(), "A valid email address is required for password reset",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.sendPasswordResetEmail(user.getEmail())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Bundle bundle = new Bundle();
                bundle.putString("message",
                        getResources()
                        .getString(R.string.password_reset_email_success));
                Navigation.findNavController(requireView()).
                        navigate(R.id.action_signInScreen_to_userActionConfirmationScreen,
                                bundle);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Bundle bundle = new Bundle();
                bundle.putString("message",
                        getResources()
                        .getString(R.string.password_reset_email_failure));
                Navigation.findNavController(requireView()).
                        navigate(R.id.action_signInScreen_to_userActionConfirmationScreen,
                                bundle);
            }
        });
    }
}