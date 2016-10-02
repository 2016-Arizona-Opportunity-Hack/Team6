package hackathon.petcare;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import hackathon.petcare.demo.nosql.CallsDO;
import hackathon.petcare.demo.nosql.DemoNoSQLCallsResult;
import hackathon.petcare.demo.nosql.DemoNoSQLOperationBase;
import hackathon.petcare.demo.nosql.DemoNoSQLResult;
import hackathon.petcare.demo.nosql.DemoNoSQLTableBase;
import hackathon.petcare.demo.nosql.DemoNoSQLTableFactory;
import hackathon.petcare.mobile.AWSMobileClient;
import hackathon.petcare.mobile.user.IdentityManager;

public class PetMainActivity extends AppCompatActivity {

    private IdentityManager identityManager;
    public DynamoDBMapper mapper;
    private DemoNoSQLTableBase demoTable;
    private Button bCat,bDog;
    public double dogFactor,catFactor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_main);
        initializeApplication();
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        demoTable = DemoNoSQLTableFactory.instance(getApplicationContext())
                .getNoSQLTableByTableName("places");
        bCat = (Button) findViewById(R.id.button_cat);
        bDog = (Button) findViewById(R.id.button_dog);
        bCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catFactor = 1;
                dogFactor = 0;
                SharedPreferences sharedpreferences = getSharedPreferences("PetCare", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putFloat("catFactor",(float)catFactor);
                editor.putFloat("dogFactor",(float)dogFactor);
                editor.commit();
                startActivity(new Intent(PetMainActivity.this,PetActivityScreenTwo.class));
            }
        });
        bDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dogFactor = 1;
                catFactor = 0;
                startActivity(new Intent(PetMainActivity.this,PetActivityScreenTwo.class));
                //new FetchData().execute();
            }
        });

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
    public class DemoScanWithoutFilter extends DemoNoSQLOperationBase {

        private PaginatedScanList<CallsDO> results;
        private Iterator<CallsDO> resultsIterator;

        DemoScanWithoutFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_scan_without_filter),
                    context.getString(R.string.nosql_operation_example_scan_without_filter));
        }

        @Override
        public boolean executeOperation() {
            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            results = mapper.scan(CallsDO.class, scanExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                int i=0;
                while (resultsIterator.hasNext()) {
                    if(results.size()<=i) {
                        return true;
                    }
                    CallsDO bean = results.get(i);

                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public boolean isScan() {
            return true;
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    private static List<DemoNoSQLResult> getNextResultsGroupFromIterator(final Iterator<CallsDO> resultsIterator) {
        if (!resultsIterator.hasNext()) {
            return null;
        }
        List<DemoNoSQLResult> resultGroup = new LinkedList<>();
        int itemsRetrieved = 0;
        do {
            // Retrieve the item from the paginated results.
            final CallsDO item = resultsIterator.next();
            // Add the item to a group of results that will be displayed later.
            resultGroup.add(new DemoNoSQLCallsResult(item));
            itemsRetrieved++;
        } while ((itemsRetrieved < 40) && resultsIterator.hasNext());
        return resultGroup;
    }

    private class FetchData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                new DemoScanWithoutFilter(PetMainActivity.this).executeOperation();
            } catch (final AmazonClientException ex) {

            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
