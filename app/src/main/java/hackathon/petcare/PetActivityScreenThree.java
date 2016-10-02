package hackathon.petcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AutoCompleteTextView;
import android.widget.RatingBar;

import com.jaredrummler.materialspinner.MaterialSpinner;

import hackathon.petcare.R;

/**
 * Created by pradhumanswami on 10/2/16.
 */
public class PetActivityScreenThree extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_main_screen_three);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Medical Attention", "Housing Issues", "Behaviorial Issues","Pet Stores");
        sharedpreferences = getSharedPreferences("PetCare", Context.MODE_PRIVATE);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("filter",item);
                editor.commit();
                startActivity(new Intent(PetActivityScreenThree.this,PetMainActivity.class));
            }
        });

    }


}
