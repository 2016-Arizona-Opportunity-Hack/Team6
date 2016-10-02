package hackathon.petcare.demo.nosql;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "marketcalls-mobilehub-422580932-calls")

public class CallsDO {
    private String _callId;
    private String _callPrice;
    private String _callTargetAmount;
    private Double _callType;
    private String _stockName;
    private Double _stockStatus;
    private String _stopLossAmount;
    private String _timeUpdated;

    @DynamoDBHashKey(attributeName = "callId")
    @DynamoDBAttribute(attributeName = "callId")
    public String getCallId() {
        return _callId;
    }

    public void setCallId(final String _callId) {
        this._callId = _callId;
    }
    @DynamoDBAttribute(attributeName = "callPrice")
    public String getCallPrice() {
        return _callPrice;
    }

    public void setCallPrice(final String _callPrice) {
        this._callPrice = _callPrice;
    }
    @DynamoDBAttribute(attributeName = "callTargetAmount")
    public String getCallTargetAmount() {
        return _callTargetAmount;
    }

    public void setCallTargetAmount(final String _callTargetAmount) {
        this._callTargetAmount = _callTargetAmount;
    }
    @DynamoDBAttribute(attributeName = "callType")
    public Double getCallType() {
        return _callType;
    }

    public void setCallType(final Double _callType) {
        this._callType = _callType;
    }
    @DynamoDBAttribute(attributeName = "stockName")
    public String getStockName() {
        return _stockName;
    }

    public void setStockName(final String _stockName) {
        this._stockName = _stockName;
    }
    @DynamoDBAttribute(attributeName = "stockStatus")
    public Double getStockStatus() {
        return _stockStatus;
    }

    public void setStockStatus(final Double _stockStatus) {
        this._stockStatus = _stockStatus;
    }
    @DynamoDBAttribute(attributeName = "stopLossAmount")
    public String getStopLossAmount() {
        return _stopLossAmount;
    }

    public void setStopLossAmount(final String _stopLossAmount) {
        this._stopLossAmount = _stopLossAmount;
    }
    @DynamoDBAttribute(attributeName = "timeUpdated")
    public String getTimeUpdated() {
        return _timeUpdated;
    }

    public void setTimeUpdated(final String _timeUpdated) {
        this._timeUpdated = _timeUpdated;
    }

}
