package be.hvwebsites.healthmeasurements;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.hvwebsites.healthmeasurements.entities.Belly;

public class UpdateBellyMActivity extends AppCompatActivity {
    private TextView dateView;
    private EditText radiusView;
    public static final String EXTRA_INTENT_KEY_ACTION =
            "be.hvwebsites.healthmeasurements.INTENT_KEY_ACTION";
    public static final String EXTRA_INTENT_KEY_DATE =
            "be.hvwebsites.healthmeasurements.INTENT_KEY_DATE";
    public static final String EXTRA_INTENT_KEY_RADIUS =
            "be.hvwebsites.healthmeasurements.INTENT_KEY_RADIUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_belly_m);

        // Scherm velden definieren
        dateView = findViewById(R.id.updateBellyDateValue);
        radiusView = findViewById(R.id.updateNumberRadius);

        // Data uit intent halen
        Intent bellyIntent = getIntent();
        if (bellyIntent.hasExtra(EXTRA_INTENT_KEY_DATE)){
            Belly oldBelly = new Belly(
                    bellyIntent.getStringExtra(EXTRA_INTENT_KEY_DATE),
                    bellyIntent.getFloatExtra(EXTRA_INTENT_KEY_RADIUS, 0));
            dateView.setText(oldBelly.getFormatDate());
            radiusView.setText(String.valueOf(oldBelly.getBellyRadius()));
        }

        // Scherm velden vervolg
        final Button addbutton = findViewById(R.id.updateBelly);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // replyintent vr startActivity vn BellyActivity
                Intent replyIntent = new Intent(
                        UpdateBellyMActivity.super.getApplicationContext(),
                        BellyActivity.class);
                if (TextUtils.isEmpty(dateView.getText()) ||
                        TextUtils.isEmpty(radiusView.getText())){
                    Toast.makeText(UpdateBellyMActivity.this,
                            "Nothing entered, nothing saved !", Toast.LENGTH_LONG).show();
                }else{
                    // update velden terug sturen nr BellyActivity
                    String dateString = dateView.getText().toString();
                    Belly belly = new Belly(dateString,
                            Float.parseFloat(String.valueOf(radiusView.getText())));
                    Toast.makeText(UpdateBellyMActivity.this,
                            "Updated belly measurement saved ! " + belly.toString(),
                            Toast.LENGTH_LONG).show();
                    replyIntent.putExtra(EXTRA_INTENT_KEY_DATE, dateString);
                    replyIntent.putExtra(EXTRA_INTENT_KEY_RADIUS,
                            Float.parseFloat(String.valueOf(radiusView.getText())));
                    replyIntent.putExtra(EXTRA_INTENT_KEY_ACTION, "update");
                    startActivity(replyIntent);
                }
            }
        });

    }
}