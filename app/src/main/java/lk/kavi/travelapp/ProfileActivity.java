package lk.kavi.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import lk.kavi.travelapp.model.User;
import lk.kavi.travelapp.utils.ApiClient;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView name, age,nic,bGroup,email;
    ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImg = findViewById(R.id.profileImage);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        nic = findViewById(R.id.nic);
        bGroup = findViewById(R.id.bGroup);
        email = findViewById(R.id.email);


        DatabaseReference dbRef = ApiClient.getDBRef();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference users = dbRef.child("users").child(uid);

        users.get();

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot);

                User user = snapshot.getValue(User.class);
                System.out.println(user.getAddress());

                name.setText(user.getName());
                email.setText(user.getEmail());
                age.setText(user.getAge()+"Y");
                bGroup.setText(user.getB_group());
                nic.setText(user.getNic());
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        System.out.println(users.toString());
    }
}