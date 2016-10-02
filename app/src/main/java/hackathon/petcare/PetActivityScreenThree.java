package hackathon.petcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by pradhumanswami on 10/2/16.
 */
public class PetActivityScreenThree extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    public int problemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedpreferences = getSharedPreferences("PetCare", Context.MODE_PRIVATE);
        String last = sharedpreferences.getString("last","");
        if(!last.equalsIgnoreCase("")) {
            startActivity(new Intent(PetActivityScreenThree.this,PetActivityScreenFour.class));
        }
        setContentView(R.layout.activity_pet_main_screen_three);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setItems("Medical Attention", "Housing Issues", "Behavioral Issues","Pet Stores");

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("filter",item);
                editor.commit();
                SharedPreferences sharedpreferences = getSharedPreferences("PetCare", Context.MODE_PRIVATE);
                if (item.equals("Medical Attention")) {
                    problemType = 1;
                } else if (item.equals("Housing Issues"))

                {
                    problemType = 2;
                } else if (item.equals("Behavioral Issues"))

                {
                    problemType = 3;
                } else if (item.equals("Pet Stores"))

                {
                    problemType = 4;
                } else

                {
                    problemType = -1;
                }

                editor.putInt("problemType", problemType);
                editor.commit();
                startActivity(new Intent(PetActivityScreenThree.this,PetMainActivity.class));
            }
        });

    }


}
