package hackathon.petcare.demo.nosql;


import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "places")

public class CallsDO {
    private Integer _placeID;
    private String _placeName;
    private Integer _AType;
    private Integer _breedType;
    private Double _catFactor;
    private Double _dogFactor;
    private Double _latitude;
    private Double _longitude;
    private Double _OExpFactor;

    @DynamoDBHashKey(attributeName = "placeID")
    @DynamoDBAttribute(attributeName = "placeID")
    public Integer getplaceID() {
        return _placeID;
    }

    public void setplaceID(final Integer _placeID) {
        this._placeID = _placeID;
}
    @DynamoDBAttribute(attributeName = "placeName")
    public String getplaceName() {
        return _placeName;
    }

    public void setplaceName(final String _placeName) {
        this._placeName = _placeName;
    }
    @DynamoDBAttribute(attributeName = "AType")
    public Integer getAType() {
        return _AType;
    }

    public void setAType(final Integer _AType) {
        this._AType = _AType;
    }
    @DynamoDBAttribute(attributeName = "breedType")
    public Integer getbreedType() {
        return _breedType;
    }

    public void setbreedType(final Integer _breedType) {
        this._breedType = _breedType;
    }
    @DynamoDBAttribute(attributeName = "catFactor")
    public Double getcatFactor() {
        return _catFactor;
    }

    public void setcatFactor(final Double _catFactor) {
        this._catFactor = _catFactor;
    }
    @DynamoDBAttribute(attributeName = "dogFactor")
    public Double getdogFactor() {
        return _dogFactor;
    }

    public void setdogFactor(final Double _dogFactor) {
        this._dogFactor = _dogFactor;
    }
    @DynamoDBAttribute(attributeName = "latitude")
    public Double getlatitude() {
        return _latitude;
    }

    public void setlatitude(final Double _latitude) {
        this._latitude = _latitude;
    }
    @DynamoDBAttribute(attributeName = "longitude")
    public Double getlongitude() {
        return _longitude;
    }

    public void setlongitude(final Double _longitude) {
        this._longitude = _longitude;
    }

    @DynamoDBAttribute(attributeName = "OExpFactor")
    public Double getOExpFactor() {
        return _longitude;
    }

    public void setOExpFactor(final Double _OExpFactor) {
        this._longitude = _longitude;
    }

}
