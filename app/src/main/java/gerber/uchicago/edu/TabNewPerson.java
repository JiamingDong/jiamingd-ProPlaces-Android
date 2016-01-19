package gerber.uchicago.edu;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import gerber.uchicago.edu.FavActionUtility;
import gerber.uchicago.edu.GoogleResultsData;
import gerber.uchicago.edu.JSONParser;
import gerber.uchicago.edu.MainActivity;
import gerber.uchicago.edu.R;
import gerber.uchicago.edu.YelpResultsData;
import gerber.uchicago.edu.db.PersonDbAdapter;
import gerber.uchicago.edu.db.RestosDbAdapter;
import gerber.uchicago.edu.Contact;
import gerber.uchicago.edu.ContactEmail;
import gerber.uchicago.edu.ContactFetcher;
import gerber.uchicago.edu.Person;

public class TabNewPerson extends Fragment {

    private ScrollView mRootViewGroup;
    private EditText mNameField, mCityField, mAddressField, mPhoneField, mEmailField, mAltPhoneField;
    private TextView mPhoneText, mAltPhoneText, mAddressText, mEmailText;
    private Button mExtractButton, mSaveButton, mCancelButton;
    private ImageView mPhotoView;
    private Spinner mSpinner;

    //the person passed into this activity during edit operation
    private Person mPerson;

    private String mStrImageUrl = "";

    //this is a proxy to our database
    private RestosDbAdapter mDbAdapter;
    private PersonDbAdapter mPersonAdapter;

    //gson model defined to store search results
    private YelpResultsData mYelpResultsData;
    private Contact contact;

    //create this interface for instrumentation testing with threads
    private YelpTaskCallback mYelpTaskCallback;

    public static interface YelpTaskCallback {
        void executionDone();
    }

    public void setYelpTaskCallback(YelpTaskCallback yelpTaskCallback) {
        this.mYelpTaskCallback = yelpTaskCallback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.frag_scroll_layout_new_person,container,false);

        mPersonAdapter = new PersonDbAdapter(getActivity());
        mPersonAdapter.open();

        //fetch the person that was passed into this activity upon edit.
        //if this activity was called from a new person request, the result assigned to mPerson will be null

        mRootViewGroup = (ScrollView) v.findViewById(R.id.data_root_view_group);
        mNameField = (EditText) v.findViewById(R.id.person_name);

        //each required field must monitor itself and other text field
        mNameField.addTextChangedListener(new RequiredEditWatcher(mNameField));

        mAddressField = (EditText) v.findViewById(R.id.person_address);
        mPhoneField = (EditText) v.findViewById(R.id.person_phone);
        mAltPhoneField = (EditText) v.findViewById(R.id.person_alt_phone);
        mEmailField = (EditText) v.findViewById(R.id.person_email);
        mExtractButton = (Button) v.findViewById(R.id.extract_email_button);
        mExtractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ContactSearchTask().execute(mNameField.getText().toString());
                //hide soft keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mNameField.getWindowToken(), 0);
            }
        });

        mSaveButton = (Button) v.findViewById(R.id.save_data_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check to see if required fields are populated - this is a constraint in the db
                if (mNameField.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getActivity(),
                            "You must populate Search Name fields",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    //if no data was passed into this Activity, mPerson will be null. Create a new person
                    //no id is required because the sqlite database manages the ids for us
                    Person personNew = new Person(
                            0,
                            mNameField.getText().toString(),
                            "no city",
                            mAddressField.getText().toString(),
                            mPhoneField.getText().toString(),
                            mAltPhoneField.getText().toString(),
                            "http://image.baidu.com/i?ct=503316480&z=&tn=baiduimagedetail&ipn=d&word=%E8%A9%B9%E5%A7%86%E6%96%AF%20%E6%90%9E%E7%AC%91&step_word=&ie=utf-8&in=9943&cl=2&lm=-1&st=-1&cs=751568165,2973549119&os=3321128543,1559010319&pn=1&rn=1&di=169919567510&ln=1000&fr=&fr=&fmq=1433737138078_R&ic=0&s=undefined&se=1&sme=0&tab=0&width=&height=&face=undefined&is=0,0&istype=0&ist=&jit=&objurl=http%3A%2F%2Fimg1.gtimg.com%2Fjoke%2Fpics%2F20235%2F20235871.jpg&bdtype=0",
                            System.currentTimeMillis(),
                            mSpinner.getSelectedItem().toString(),
                            mEmailField.getText().toString()
                    );
                    long a = mPersonAdapter.createResto(personNew);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).
                            setMessage("New Person Saved!").
                            setTitle("Message").setCancelable(true).
                            setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    MainActivity mainActivity = (MainActivity) getActivity();
                                    mainActivity.pager.setCurrentItem(0);
                                }
                            });
                    builder.create().show();
                }
            }
        });

        mCancelButton = (Button) v.findViewById(R.id.cancel_action_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.pager.setCurrentItem(0);
            }
        });

        mPhoneText = (TextView) v.findViewById(R.id.text_phone);
        mEmailText = (TextView) v.findViewById(R.id.text_email);
        mAddressText = (TextView) v.findViewById(R.id.text_address);


        mPhoneText.setEnabled(false);
        mPhoneText.setOnClickListener(new DetailsEditWatcher());
        mPhoneField.addTextChangedListener(new DetailsEditWatcher(mPhoneText));

        mAddressText.setEnabled(false);
        mAddressText.setOnClickListener(new DetailsEditWatcher());
        mAddressField.addTextChangedListener(new DetailsEditWatcher(mAddressText));

        mEmailText.setEnabled(false);
        mEmailText.setOnClickListener(new DetailsEditWatcher());
        mEmailField.addTextChangedListener(new DetailsEditWatcher(mEmailText));

        mPhotoView = (ImageView) v.findViewById(R.id.person_image_view);

        //default behavior is to create a new person which is indicated by green
        mRootViewGroup.setBackgroundColor(getResources().getColor(R.color.light_green));

        // find category spinner
        mSpinner = (Spinner) v.findViewById(R.id.view_spin);

        //if this is a new record then set the save button to disabled and extract button to gone
        mSaveButton.setEnabled(false);
        mExtractButton.setVisibility(View.GONE);


        if (mPerson != null) {
            //populate the fields from the Person we passed into the intent
            mNameField.setText(mPerson.getName());
            mCityField.setText(mPerson.getCity());
            mAddressField.setText(mPerson.getAddress());
            mPhoneField.setText(PhoneNumberUtils.formatNumber(mPerson.getPhone()));
            mEmailField.setText(mPerson.getEmail());
            //change the "save" button label to "update"
            mSaveButton.setText("Update");
            mSpinner.setSelection(0);

            //set the root view group to light blue to indicate editing
            mRootViewGroup.setBackgroundColor(getResources().getColor(R.color.light_blue));

            //if this is a edit record then set the save button to enabled and extract button to visible
            mSaveButton.setEnabled(true);
            mExtractButton.setVisibility(View.VISIBLE);

            mStrImageUrl = mPerson.getImageUrl();

            fetchPhoto(mPhotoView);
        }

        return v;
    }


    private void updateButtons(CharSequence charSequence, String strOther) {
        if (!charSequence.toString().trim().equalsIgnoreCase("") && !strOther.trim().equalsIgnoreCase("")) {

            mExtractButton.setVisibility(View.VISIBLE);
            mSaveButton.setEnabled(true);


        } else {
            mExtractButton.setVisibility(View.GONE);
            mSaveButton.setEnabled(false);

        }

    }

    private class RequiredEditWatcher implements TextWatcher {

        private EditText mOtherEdit;

        private RequiredEditWatcher(EditText otherEdit) {
            mOtherEdit = otherEdit;
        }

        //the following three methods satisfy the TextWatcher interface
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            updateButtons(s, mOtherEdit.getText().toString());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }


    private class DetailsEditWatcher implements TextWatcher, View.OnClickListener {

        private TextView mTextTarget;

        //consturctor used with EditText
        private DetailsEditWatcher(TextView textView) {
            mTextTarget = textView;

        }

        //constructor used with Textview
        private DetailsEditWatcher() {
        }

        //the following three methods satisfy the TextWatcher interface
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            //if the text inside the corresponding EditText is not empty, then change
            if (s.toString().equalsIgnoreCase("")) {
                mTextTarget.setTextColor(getResources().getColor(R.color.black));
                mTextTarget.setEnabled(false);
            } else {
                mTextTarget.setTextColor(getResources().getColorStateList(R.color.pop_back_color));
                mTextTarget.setEnabled(true);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        //this satisfies the OnClickListener interface
        @Override
        public void onClick(View v) {

            FavActionUtility favActionUtility = new FavActionUtility(getActivity());

            try {
                switch (v.getId()) {
                    case R.id.text_phone:
                        favActionUtility.dial(mPhoneField.getText().toString());
                        break;
                    case R.id.text_address:
                        favActionUtility.mapOf(mAddressField.getText().toString(), mCityField.getText().toString());
                        break;
                    case R.id.text_email:
                        //favActionUtility.yelpSite(mEmailField.getText().toString());
                        break;
                }
            } catch (Exception e) {
                favActionUtility.showErrorMessageInDialog(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void fetchPhoto(ImageView imageView) {

        String strUrl = String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s%s&imgsz=small&imgtype=photo",
                mNameField.getText().toString() + "%20person%20", mCityField.getText().toString());
        strUrl = strUrl.replaceAll("\\s+", "%20");
        new DownloadImageTask(imageView).execute(strUrl);

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView mImageView;

        public DownloadImageTask(ImageView imageViewParam) {
            this.mImageView = imageViewParam;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImageView.setImageResource(R.drawable.gear);
        }

        protected Bitmap doInBackground(String... urls) {


            GoogleResultsData googleResultsData = null;
            Bitmap bitmap = null;

            try {

                if (mStrImageUrl != null && !mStrImageUrl.equals("")){
                    InputStream in = new java.net.URL(mStrImageUrl).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } else {
                    JSONObject jsonRaw = new JSONParser().getSecureJSONFromUrl(urls[0]);
                    googleResultsData = new Gson().fromJson(jsonRaw.toString(), GoogleResultsData.class);
                    mStrImageUrl = googleResultsData.responseData.results.get(0).unescapedUrl;
                    InputStream in = new java.net.URL(mStrImageUrl).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null){
                mImageView.setImageResource(R.drawable.gear);
                Toast.makeText(getActivity(), "Associated image not found on google", Toast.LENGTH_SHORT).show();
            } else {
                mImageView.setImageBitmap(result);
            }


        }
    }

    private class ContactSearchTask extends AsyncTask<String, Void, String> {


        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Fetching data");
            progressDialog.setMessage("One moment please...");
            progressDialog.setCancelable(true);

            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContactSearchTask.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
            progressDialog.show();

        }


        protected String doInBackground(String... params) {
            String name = params[0];
            return name;
        }

        @Override
        protected void onPostExecute(String name) {
            progressDialog.dismiss();

            ContactFetcher fetcher = new ContactFetcher((MainActivity)getActivity());

            ArrayList<Contact> contactList = fetcher.fetchAll();
            for (Contact c : contactList) {
                if (c.name.equals(name)) {
                    Toast.makeText(getActivity(), c.name, Toast.LENGTH_LONG).show();
                    contact = c;
                    break;
                }
                else {
                    Toast.makeText(getActivity(), "no result found", Toast.LENGTH_LONG).show();
                    mNameField.setText("");
                    mEmailField.setText("");
                    mPhoneField.setText("");
                    mAltPhoneField.setText("");
                }
            }
            if (contact != null) {
                mNameField.setText(contact.name);
                mEmailField.setText(((ContactEmail) contact.emails.get(0)).address);
                mPhoneField.setText(contact.numbers.get(0).number);
                if (contact.numbers.get(1) != null) {
                    mAltPhoneField.setText(contact.numbers.get(1).number);
                }
            }
            //contact = c;
//            mYelpResultsData = yelpResultsData;
//
//            if (mYelpResultsData == null){
//                Toast.makeText(getActivity(), "No data for that search term", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            ArrayList<String> stringArrayList = mYelpResultsData.getSimpleValues();
//            if (stringArrayList.size() == 0) {
//                Toast.makeText(getActivity(), "No data for that search term", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (contact == null) {
//                return;
//            }
//            Bundle bundle = new Bundle();
//            bundle.putString("email", ((ContactEmail)contact.emails.get(0)).address);
//            Intent intent = new Intent(getActivity(), ResultsDialogActivity.class);
//            intent.putExtras(bundle);
//            startActivityForResult(intent, 1001);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


}
