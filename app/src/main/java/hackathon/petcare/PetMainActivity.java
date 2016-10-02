package hackathon.petcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hackathon.petcare.mobile.AWSMobileClient;
import hackathon.petcare.mobile.user.IdentityManager;

public class PetMainActivity extends AppCompatActivity {

    private IdentityManager identityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_main);
        initializeApplication();
    }

    private void initializeApplication() {
        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // Obtain a reference to the i  dentity manager.
        identityManager = awsMobileClient.getIdentityManager();

        // ...Put any application-specific initialization logic here...
    }
}
