package hackathon.petcare.demo.nosql;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.util.Set;

import hackathon.petcare.mobile.AWSMobileClient;


public class DemoNoSQLCallsResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final CallsDO result;

    public DemoNoSQLCallsResult(final CallsDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final String originalValue = result.getCallPrice();
        result.setCallPrice(DemoSampleDataGenerator.getRandomSampleString("callPrice"));
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setCallPrice(originalValue);
            throw ex;
        }
    }

    @Override
    public void deleteItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mapper.delete(result);
    }

    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public View getView(final Context context, final View convertView, int position) {
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView callIdKeyTextView;
        final TextView callIdValueTextView;
        final TextView callPriceKeyTextView;
        final TextView callPriceValueTextView;
        final TextView callTargetAmountKeyTextView;
        final TextView callTargetAmountValueTextView;
        final TextView callTypeKeyTextView;
        final TextView callTypeValueTextView;
        final TextView stockNameKeyTextView;
        final TextView stockNameValueTextView;
        final TextView stockStatusKeyTextView;
        final TextView stockStatusValueTextView;
        final TextView stopLossAmountKeyTextView;
        final TextView stopLossAmountValueTextView;
        final TextView timeUpdatedKeyTextView;
        final TextView timeUpdatedValueTextView;
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);


            callIdKeyTextView = new TextView(context);
            callIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(callIdKeyTextView, callIdValueTextView);
            layout.addView(callIdKeyTextView);
            layout.addView(callIdValueTextView);

            callPriceKeyTextView = new TextView(context);
            callPriceValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(callPriceKeyTextView, callPriceValueTextView);
            layout.addView(callPriceKeyTextView);
            layout.addView(callPriceValueTextView);

            callTargetAmountKeyTextView = new TextView(context);
            callTargetAmountValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(callTargetAmountKeyTextView, callTargetAmountValueTextView);
            layout.addView(callTargetAmountKeyTextView);
            layout.addView(callTargetAmountValueTextView);

            callTypeKeyTextView = new TextView(context);
            callTypeValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(callTypeKeyTextView, callTypeValueTextView);
            layout.addView(callTypeKeyTextView);
            layout.addView(callTypeValueTextView);

            stockNameKeyTextView = new TextView(context);
            stockNameValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(stockNameKeyTextView, stockNameValueTextView);
            layout.addView(stockNameKeyTextView);
            layout.addView(stockNameValueTextView);

            stockStatusKeyTextView = new TextView(context);
            stockStatusValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(stockStatusKeyTextView, stockStatusValueTextView);
            layout.addView(stockStatusKeyTextView);
            layout.addView(stockStatusValueTextView);

            stopLossAmountKeyTextView = new TextView(context);
            stopLossAmountValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(stopLossAmountKeyTextView, stopLossAmountValueTextView);
            layout.addView(stopLossAmountKeyTextView);
            layout.addView(stopLossAmountValueTextView);

            timeUpdatedKeyTextView = new TextView(context);
            timeUpdatedValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(timeUpdatedKeyTextView, timeUpdatedValueTextView);
            layout.addView(timeUpdatedKeyTextView);
            layout.addView(timeUpdatedValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            callIdKeyTextView = (TextView) layout.getChildAt(1);
            callIdValueTextView = (TextView) layout.getChildAt(2);

            callPriceKeyTextView = (TextView) layout.getChildAt(3);
            callPriceValueTextView = (TextView) layout.getChildAt(4);

            callTargetAmountKeyTextView = (TextView) layout.getChildAt(5);
            callTargetAmountValueTextView = (TextView) layout.getChildAt(6);

            callTypeKeyTextView = (TextView) layout.getChildAt(7);
            callTypeValueTextView = (TextView) layout.getChildAt(8);

            stockNameKeyTextView = (TextView) layout.getChildAt(9);
            stockNameValueTextView = (TextView) layout.getChildAt(10);

            stockStatusKeyTextView = (TextView) layout.getChildAt(11);
            stockStatusValueTextView = (TextView) layout.getChildAt(12);

            stopLossAmountKeyTextView = (TextView) layout.getChildAt(13);
            stopLossAmountValueTextView = (TextView) layout.getChildAt(14);

            timeUpdatedKeyTextView = (TextView) layout.getChildAt(15);
            timeUpdatedValueTextView = (TextView) layout.getChildAt(16);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        callIdKeyTextView.setText("callId");
        callIdValueTextView.setText(result.getCallId());
        callPriceKeyTextView.setText("callPrice");
        callPriceValueTextView.setText(result.getCallPrice());
        callTargetAmountKeyTextView.setText("callTargetAmount");
        callTargetAmountValueTextView.setText(result.getCallTargetAmount());
        callTypeKeyTextView.setText("callType");
        callTypeValueTextView.setText("" + result.getCallType().longValue());
        stockNameKeyTextView.setText("stockName");
        stockNameValueTextView.setText(result.getStockName());
        stockStatusKeyTextView.setText("stockStatus");
        stockStatusValueTextView.setText("" + result.getStockStatus().longValue());
        stopLossAmountKeyTextView.setText("stopLossAmount");
        stopLossAmountValueTextView.setText(result.getStopLossAmount());
        timeUpdatedKeyTextView.setText("timeUpdated");
        timeUpdatedValueTextView.setText(result.getTimeUpdated());
        return layout;
    }
}
