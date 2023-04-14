package lk.kavi.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import lk.kavi.travelapp.utils.ApiClient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    EditText email, pass_word,age,nic,bGroup,address,name,mobileNo;

    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        email = findViewById(R.id.email);
        pass_word = findViewById(R.id.password);
        age = findViewById(R.id.age);
        nic = findViewById(R.id.nic);
        address = findViewById(R.id.address);
        bGroup = findViewById(R.id.b_group);
        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.mobileNo);
        btn = findViewById(R.id.regBtn);
        mAuth=FirebaseAuth.getInstance();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailTxt = email.getText().toString().trim();
                String password= pass_word.getText().toString().trim();
                String ageTxt= age.getText().toString().trim();
                String nameTxt= name.getText().toString().trim();
                String nicTxt= nic.getText().toString().trim();
                String adressTxt= address.getText().toString().trim();
                String bGroupTxt= bGroup.getText().toString().trim();
                String mobileNoTxt= mobileNo.getText().toString().trim();

                boolean isOk = false;
                if(!(validateFiled(nameTxt,name)&&
                validateFiled(ageTxt,age)&&
                validateFiled(nicTxt,nic)&&
                validateFiled(adressTxt,address)&&
                validateFiled(bGroupTxt,bGroup)&&
                validateFiled(mobileNoTxt,mobileNo))){
                    return;
                }


                if(emailTxt.isEmpty())
                {
                    email.setError("Email is empty");
                    email.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches())
                {
                    email.setError("Enter the valid email address");
                    email.requestFocus();
                    return;
                }
                if(password.isEmpty())
                {
                    pass_word.setError("Enter the password");
                    pass_word.requestFocus();
                    return;
                }
                if(password.length()<6)
                {
                    pass_word.setError("Length of the password should be more than 6");
                    pass_word.requestFocus();
                    return;
                }


                btn.setEnabled(false);
                mAuth.createUserWithEmailAndPassword(emailTxt,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        String uid = mAuth.getCurrentUser().getUid();
                        System.out.println(uid);

                        DatabaseReference db = ApiClient.getDBRef();
                        Map<String, Object> user = new HashMap<>();
                        user.put("uid", uid);
                        user.put("name", nameTxt);
                        user.put("age", ageTxt);
                        user.put("address", adressTxt);
                        user.put("nic", nicTxt);
                        user.put("b_group", bGroupTxt);
                        user.put("email", emailTxt);
                        user.put("mobileno", mobileNoTxt);
                        //Add other user data
                        DatabaseReference usersRef = db.child("users");
                        usersRef.child(uid).setValue(user).addOnCompleteListener(task1 -> {
                            Toast.makeText(RegisterActivity.this,
                                    "Success",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        });

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,
                                "Please Check Your login Credentials",
                                Toast.LENGTH_SHORT).show();
                    }

                });

            }
        });



//        mAuth.createUserWithEmailAndPassword();
    }

    private boolean validateFiled(String nameTxt, EditText name) {

        if(nameTxt.isEmpty()){
            name.setError("Please Fill");
            name.requestFocus();
            return false;
        }

        return true;
    }
}