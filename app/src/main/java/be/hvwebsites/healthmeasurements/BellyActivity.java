package be.hvwebsites.healthmeasurements;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.healthmeasurements.adapters.BellyListAdapter;
import be.hvwebsites.healthmeasurements.entities.Belly;
import be.hvwebsites.healthmeasurements.returnInfo.ReturnInfo;
import be.hvwebsites.healthmeasurements.viewmodels.BellyViewModel;

public class BellyActivity extends AppCompatActivity {
    private BellyViewModel bellyViewModel;
    private String baseDirectory;
    private List<Belly> bellyList = new ArrayList<>();
    public static final int INTENT_REQUEST_CODE = 1;
    public static final String EXTRA_INTENT_KEY_ACTION =
            "be.hvwebsites.healthmeasurements.INTENT_KEY_ACTION";
    public static final String BELLY_FILE = "test.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belly);
        //Toolbar toolbar = findViewById(R.id.toolbarr);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BellyActivity.this,
                        NewBellyMeasurementActivity.class);
//                startActivityForResult(intent, INTENT_REQUEST_CODE);
                intent.putExtra(EXTRA_INTENT_KEY_ACTION, "insert");
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BellyListAdapter adapter = new BellyListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Belly File declareren
        baseDirectory = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
        File bellyFile = new File(baseDirectory, BELLY_FILE);
        // Get a viewmodel from the viewmodelproviders
        bellyViewModel = ViewModelProviders.of(this).get(BellyViewModel.class);

        // Initialize viewmodel
        ReturnInfo bellyToestand = bellyViewModel.initializeBellyViewModel(bellyFile);
        if (bellyToestand.getReturnCode() == 0) {
            // BellyFile lezen is gelukt
            // Get tBellyList
            bellyList = bellyViewModel.gettBellyList();
            adapter.setBellyList(bellyList);
        } else if (bellyToestand.getReturnCode() == 100){
            Toast.makeText(BellyActivity.this,
                    bellyToestand.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(BellyActivity.this,
                    "Loading Belly Measurements failed",
                    Toast.LENGTH_LONG).show();
        }

        // om te kunnen swipen in de recyclerview
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Belly sBelly = adapter.getBellyAtPosition(position);
                        Toast.makeText(BellyActivity.this,
                                "Deleting belly measurement on " + sBelly.getFormatDate(),
                                Toast.LENGTH_LONG).show();
                        // Delete Belly measurement
                        // Verwijder belly uit bellylist
                        if (bellyList.remove(sBelly)) {
                            // belly is uit de belly list
                            // Wegschrijven nr file
                            if (bellyViewModel.storeBellies(bellyFile, bellyList)) {
                                // Wegschrijven gelukt
                            } else {
                                // Wegschrijven mislukt
                            }
                        } else {
                            // belly niet in bellylist
                        }
                        // Refresh recyclerview
                        adapter.setBellyList(bellyList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);

        // verwerken replyIntent vn UpdateBelly of NewBelly
        Intent newBellyIntent = getIntent();
        if (newBellyIntent.hasExtra(EXTRA_INTENT_KEY_ACTION)) {
            String action = newBellyIntent.getStringExtra(NewBellyMeasurementActivity.EXTRA_INTENT_KEY_ACTION);
            if (action.equals("update")){
                Belly newBelly = new Belly(
                        newBellyIntent.getStringExtra(UpdateBellyMActivity.EXTRA_INTENT_KEY_DATE),
                        newBellyIntent.getFloatExtra(UpdateBellyMActivity.EXTRA_INTENT_KEY_RADIUS,
                                0));
                int indexToUpdate = newBellyIntent.getIntExtra(UpdateBellyMActivity.EXTRA_INTENT_KEY_INDEX, 0);
                bellyList.set(indexToUpdate, newBelly);
                // oude waarde vervangen door nieuwe
            } else if (action.equals("insert")){
                // Controleren of datum reeds bestaat
                Belly newBelly = new Belly(
                        newBellyIntent.getStringExtra(NewBellyMeasurementActivity.EXTRA_INTENT_KEY_DATE),
                        newBellyIntent.getFloatExtra(NewBellyMeasurementActivity.EXTRA_INTENT_KEY_RADIUS,
                                0));
                if (bestaatBelly(newBelly.getDate(), bellyList)){
                    Toast.makeText(BellyActivity.this,
                            "Voor deze datum is er reeds een measurement !",
                            Toast.LENGTH_LONG).show();
                }else {
                    // Toevoegen aan bellyList
                    bellyList.add(newBelly);
                }
            }
            // Wegschrijven nr file
            if (bellyViewModel.storeBellies(bellyFile, bellyList)) {
                // Wegschrijven gelukt
            } else {
                // Wegschrijven mislukt
            }
            // Refresh recyclerview
            adapter.setBellyList(bellyList);
        }
        if (bellyToestand.getReturnCode() == 100){
            // Er bestaan nog geen bellies, ga naar new BellyActivity
            Intent intent = new Intent(BellyActivity.this,
                    NewBellyMeasurementActivity.class);
            // als je antwoord terug verwacht, het antwoord wordt verwerkt in onActivityResult
            startActivityForResult(intent, INTENT_REQUEST_CODE);
        }
    }

    public boolean bestaatBelly(String date, List<Belly> bellies){
        for (int i = 0; i < bellies.size(); i++) {
            if (bellies.get(i).getDate().equals(date)){
                // belly bestaat reeds
                return true;
            }
        }
        return false;
    }
}