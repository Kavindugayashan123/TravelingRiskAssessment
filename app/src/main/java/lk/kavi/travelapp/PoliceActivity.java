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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.util.List;

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

        Intent intent = getIntent();
        String datas = (String) intent.getExtras().getSerializable("data");

        Police data = new Gson().fromJson(datas, Police.class);

        policeName.setText(data.getName());
        mobileNo.setText(data.getMobile_no());
        imageView.setImageBitmap(data.getImgBitmap());

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference dbRef = ApiClient.getDBRef();
                String uid = mAuth.getCurrentUser().getUid();

                String s = helpText.getText().toString();



            }
        });
    }
}