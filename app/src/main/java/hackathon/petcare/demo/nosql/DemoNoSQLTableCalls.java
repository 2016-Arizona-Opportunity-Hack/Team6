package hackathon.petcare.demo.nosql;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hackathon.petcare.R;
import hackathon.petcare.mobile.AWSMobileClient;
import hackathon.petcare.mobile.util.ThreadUtils;


public class DemoNoSQLTableCalls extends DemoNoSQLTableBase {
    private static final String LOG_TAG = DemoNoSQLTableCalls.class.getSimpleName();

    /** Inner classes use this value to determine how many results to retrieve per service call. */
    private static final int RESULTS_PER_RESULT_GROUP = 40;

    /** Removing sample data removes the items in batches of the following size. */
    private static final int MAX_BATCH_SIZE_FOR_DELETE = 50;

    /********* Primary Get Query Inner Classes *********/

    public class DemoGetWithPartitionKey extends DemoNoSQLOperationBase {
        private CallsDO result;
        private boolean resultRetrieved = true;

        private DemoGetWithPartitionKey(final Context context) {
            super(context.getString(R.string.nosql_operation_get_by_partition_text),
                String.format(context.getString(R.string.nosql_operation_example_get_by_partition_text),
                    "callId", "demo-callId-500000"));
        }

        /* Blocks until result is retrieved, should be called in the background. */
        @Override
        public boolean executeOperation() throws AmazonClientException {
            // Retrieve an item by passing the partition key using the object mapper.
            result = mapper.load(CallsDO.class, "demo-callId-500000");

            if (result != null) {
                resultRetrieved = false;
                return true;
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            if (resultRetrieved) {
                return null;
            }
            final List<DemoNoSQLResult> results = new ArrayList<>();
            results.add(new DemoNoSQLCallsResult(result));
            resultRetrieved = true;
            return results;
        }

        @Override
        public void resetResults() {
            resultRetrieved = false;
        }

    }

    /* ******** Secondary Named Index Query Inner Classes ******** */

    /********* Scan Inner Classes *********/

    public class DemoScanWithFilter extends DemoNoSQLOperationBase {

        private PaginatedScanList<CallsDO> results;
        private Iterator<CallsDO> resultsIterator;

        DemoScanWithFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_scan_with_filter),
                String.format(context.getString(R.string.nosql_operation_example_scan_with_filter),
                    "callPrice", "demo-callPrice-500000"));
        }

        @Override
        public boolean executeOperation() {
            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map<String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#callPrice", "callPrice");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":MincallPrice",
                new AttributeValue().withS("demo-callPrice-500000"));
            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#callPrice > :MincallPrice")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues);

            results = mapper.scan(CallsDO.class, scanExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
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
                if (resultsIterator.hasNext()) {
                    return true;
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

    /**
     * Helper Method to handle retrieving the next group of query results.
     * @param resultsIterator the iterator for all the results (makes a new service call for each result group).
     * @return the next list of results.
     */
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
        } while ((itemsRetrieved < RESULTS_PER_RESULT_GROUP) && resultsIterator.hasNext());
        return resultGroup;
    }

    /** The DynamoDB object mapper for accessing DynamoDB. */
    private final DynamoDBMapper mapper;

    public DemoNoSQLTableCalls() {
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    }

    @Override
    public String getTableName() {
        return "calls";
    }

    @Override
    public String getPartitionKeyName() {
        return "Artist";
    }

    public String getPartitionKeyType() {
        return "String";
    }

    @Override
    public String getSortKeyName() {
        return null;
    }

    public String getSortKeyType() {
        return "";
    }

    @Override
    public int getNumIndexes() {
        return 0;
    }

    @Override
    public void insertSampleData() throws AmazonClientException {
        Log.d(LOG_TAG, "Inserting Sample data.");
        final CallsDO firstItem = new CallsDO();

        firstItem.setCallId(
            DemoSampleDataGenerator.getRandomSampleString("callId"));
        firstItem.setCallPrice(
            DemoSampleDataGenerator.getRandomSampleString("callPrice"));
        firstItem.setCallTargetAmount(
            DemoSampleDataGenerator.getRandomSampleString("callTargetAmount"));
        firstItem.setCallType(DemoSampleDataGenerator.getRandomSampleNumber());
        firstItem.setStockName(
            DemoSampleDataGenerator.getRandomSampleString("stockName"));
        firstItem.setStockStatus(DemoSampleDataGenerator.getRandomSampleNumber());
        firstItem.setStopLossAmount(
            DemoSampleDataGenerator.getRandomSampleString("stopLossAmount"));
        firstItem.setTimeUpdated(
            DemoSampleDataGenerator.getRandomSampleString("timeUpdated"));
        AmazonClientException lastException = null;

        try {
            mapper.save(firstItem);
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }

        final CallsDO[] items = new CallsDO[SAMPLE_DATA_ENTRIES_PER_INSERT-1];
        for (int count = 0; count < SAMPLE_DATA_ENTRIES_PER_INSERT-1; count++) {
            final CallsDO item = new CallsDO();
            item.setCallId(DemoSampleDataGenerator.getRandomSampleString("callId"));
            item.setCallPrice(DemoSampleDataGenerator.getRandomSampleString("callPrice"));
            item.setCallTargetAmount(DemoSampleDataGenerator.getRandomSampleString("callTargetAmount"));
            item.setCallType(DemoSampleDataGenerator.getRandomSampleNumber());
            item.setStockName(DemoSampleDataGenerator.getRandomSampleString("stockName"));
            item.setStockStatus(DemoSampleDataGenerator.getRandomSampleNumber());
            item.setStopLossAmount(DemoSampleDataGenerator.getRandomSampleString("stopLossAmount"));
            item.setTimeUpdated(DemoSampleDataGenerator.getRandomSampleString("timeUpdated"));

            items[count] = item;
        }
        try {
            mapper.batchSave(Arrays.asList(items));
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item batch : " + ex.getMessage(), ex);
            lastException = ex;
        }

        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            throw lastException;
        }
    }

    @Override
    public void removeSampleData() throws AmazonClientException {
        // Scan for the sample data to remove it.

        // Use an expression names Map to avoid the potential for attribute names
        // colliding with DynamoDB reserved words.
        final Map<String, String> filterExpressionAttributeNames = new HashMap<>();
        filterExpressionAttributeNames.put("#hashAttribute", "callId");

        final Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":prefixVal", new AttributeValue().withS(
            DemoSampleDataGenerator.SAMPLE_DATA_STRING_PREFIX));

        final String hashKeyFilterCondition = "begins_with(#hashAttribute, :prefixVal)";

        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression(hashKeyFilterCondition)
            .withExpressionAttributeNames(filterExpressionAttributeNames)
            .withExpressionAttributeValues(expressionAttributeValues);

        PaginatedScanList<CallsDO> results = mapper.scan(CallsDO.class, scanExpression);

        Iterator<CallsDO> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final CallsDO item = resultsIterator.next();

            // Demonstrate deleting a single item.
            try {
                mapper.delete(item);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item : " + ex.getMessage(), ex);
                lastException = ex;
            }
        }

        final List<CallsDO> batchOfItems = new LinkedList<CallsDO>();
        while (resultsIterator.hasNext()) {
            // Build a batch of books to delete.
            for (int index = 0; index < MAX_BATCH_SIZE_FOR_DELETE && resultsIterator.hasNext(); index++) {
                batchOfItems.add(resultsIterator.next());
            }
            try {
                // Delete a batch of items.
                mapper.batchDelete(batchOfItems);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item batch : " + ex.getMessage(), ex);
                lastException = ex;
            }

            // clear the list for re-use.
            batchOfItems.clear();
        }


        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            // The logs contain all the exceptions that occurred during attempted delete.
            throw lastException;
        }
    }

    private List<DemoNoSQLOperationListItem> getSupportedDemoOperations(final Context context) {
        List<DemoNoSQLOperationListItem> noSQLOperationsList = new ArrayList<DemoNoSQLOperationListItem>();
            noSQLOperationsList.add(new DemoGetWithPartitionKey(context));

        /*noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_scan)));*/
        noSQLOperationsList.add(new DemoScanWithoutFilter(context));
        noSQLOperationsList.add(new DemoScanWithFilter(context));
        return noSQLOperationsList;
    }

    @Override
    public void getSupportedDemoOperations(final Context context,
                                           final SupportedDemoOperationsHandler opsHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<DemoNoSQLOperationListItem> supportedOperations = getSupportedDemoOperations(context);
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        opsHandler.onSupportedOperationsReceived(supportedOperations);
                    }
                });
            }
        }).start();
    }
}
