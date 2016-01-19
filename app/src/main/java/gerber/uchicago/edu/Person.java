package gerber.uchicago.edu;

/**
 * Created by ag on 5/18/2015.
 */
public class Person {

    private long mId;
    private String mName;
    private String mCity;
    private String mAddress;
    private String mPhone;
    private String mAltPhone;
    private String mImageUrl;
    private long mTimeStamp;
    private String mCategory;
    private String mEmail;

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public Person(long id, String name, String city, String address, String phone,
                  String altPhone, String imageUrl, long timeStamp, String category,
                  String email)
    {
        mId = id;
        mName = name;
        mCity = city;
        mAddress = address;
        mPhone = phone;
        mAltPhone = altPhone;
        mImageUrl = imageUrl;
        mTimeStamp = timeStamp;
        mCategory = category;
        mEmail = email;
    }

    public Person(String name, String city, String address, String phone, String altPhone,
                  String imageUrl,long timeStamp, String category, String email)
    {
        mName = name;
        mCity = city;
        mAddress = address;
        mPhone = phone;
        mAltPhone = altPhone;
        mImageUrl = imageUrl;
        mTimeStamp = timeStamp;
        mCategory = category;
        mEmail = email;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getAltPhone() {
        return mAltPhone;
    }

    public void setAltPhone(String altPhone) {
        this.mAltPhone = altPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }
}
