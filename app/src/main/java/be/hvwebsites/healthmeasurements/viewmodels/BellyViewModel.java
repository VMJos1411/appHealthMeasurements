package be.hvwebsites.healthmeasurements.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.healthmeasurements.entities.Belly;
import be.hvwebsites.healthmeasurements.repositories.BellyRepository;

public class BellyViewModel extends AndroidViewModel {
    private BellyRepository repository;
    private File bellyFile;
    private LiveData<List<Belly>> bellyList;
    private List<Belly> tBellyList = new ArrayList<>();
    private Belly latestBelly;

    public BellyViewModel(Application application){
        super(application);
        repository = new BellyRepository(application);
    }

    public void initializeBellyViewModel(File bellyFile){
        repository.initializeRepository(bellyFile);
        bellyList = repository.getBellyList();
        tBellyList = repository.getTBellyList();
        latestBelly = repository.getLatestBelly();
    }

    public void setBellyFile(File bellyFile) {
        this.bellyFile = bellyFile;
    }

    public List<Belly> gettBellyList() {
        return tBellyList;
    }

    public LiveData<List<Belly>> getBellyList() {
        return bellyList;
    }

    public void insertBelly(Belly belly){
        try {
            repository.insertBelly(belly);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public void deleteBelly(Belly belly){
        repository.deleteBelly(belly);
    }

    public void updateBelly(Belly belly){
        repository.updateBelly(belly);
    }

    public Belly getLatestBelly(){
        return latestBelly;
    }
}
