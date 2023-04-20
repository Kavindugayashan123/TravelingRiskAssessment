package lk.kavi.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import lk.kavi.travelapp.model.Hotel;
import lk.kavi.travelapp.model.Police;
import lk.kavi.travelapp.utils.ApiClient;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PoliceActivity extends AppCompatActivity {


    TextView policeName, mobileNo;
    ImageView imageView;
    EditText helpText;
    Button btnHelp;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);

        mAuth=FirebaseAuth.getInstance();

        imageView = findViewById(R.id.policeImage);
        policeName = findViewById(R.id.policeName);
        mobileNo = findViewById(R.id.mobileNo);
        btnHelp = findViewById(R.id.btnHelp);
        helpText = findViewById(R.id.helpText);

        Police data = null;

        Intent intent = getIntent();
        try{
            String datas = (String) intent.getExtras().getSerializable("data");

            data = new Gson().fromJson(datas, Police.class);

            getSupportActionBar().setTitle(data.getName());
            policeName.setText(data.getName());
            mobileNo.setText(data.getMobile_no());
            imageView.setImageBitmap(data.getImgBitmap());
        }catch (NullPointerException e){

        }


        Police finalData = data;
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid = mAuth.getCurrentUser().getUid();
                String s = helpText.getText().toString();

                DatabaseReference usersRef = ApiClient.getDBRef().child("help");
                Map<String, Object> help = new HashMap<>();

                help.put("policeName", finalData.getName());
                help.put("helpTxt",s);
                help.put("userId",uid);
                usersRef.child(String.valueOf(new Random().nextLong()*5000)).setValue(help).addOnCompleteListener(task1 -> {
                    Toast.makeText(PoliceActivity.this,
                            "Success",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PoliceActivity.this, PoliceListActivity.class));
                });

            }
        });
    }
}