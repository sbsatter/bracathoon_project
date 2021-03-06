package com.sbsatter.findmycolleague;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayList<String> searchedNames= new ArrayList<>();
    public static JSONArray response= new JSONArray();
    private static final int StaffInfoByName = 0;
    private static final int StaffInfoByPIN = 01;
    private static final int GetStaffByProgram = 02;
    private Spinner spinner;
    protected static SoapSerializationEnvelope soapEnvelop;
    public static ArrayList<HashMap<String,String>> detail;
    ProgressDialog progressDialog;
    HashMap<String, String> staffInfoByName, staffInfoByPin, getStaffByProgram;
    String searchString="";
    int selectedItemSpinner=0;
    View viewToChange;


    @Bind(R.id.searchEt)EditText searchEt;
    private android.content.Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        spinner = (Spinner) findViewById(R.id.search_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemSpinner=position;
                if(selectedItemSpinner==1){
                    searchEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                }else{
                    searchEt.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ButterKnife.bind(this);
      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView t=(TextView)findViewById(R.id.nav_head_text);
        try{
            t.setText("");
        }catch (Exception e){
            Log.i("TAG",""+e);
        }
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String nav_head_user=(prefs.getString(getString(R.string.pref_loggedIn_name),"user"));




    }


    public void editProfile(){
        Intent i = new Intent(DrawerActivity.this, EditProfile.class);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.edit_profile) {
            Intent i = new Intent(DrawerActivity.this, EditProfile.class);
            startActivity(i);
        } else if (id == R.id.set_status) {
            Intent i = new Intent(DrawerActivity.this, Status.class);
            startActivity(i);

        } else if (id == R.id.log_out) {
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(getString(R.string.pref_loggedIn_name));
            editor.remove(getString(R.string.pref_loggedIn_username));
            editor.remove(getString(R.string.pref_loggedIn_password));
            editor.apply();
            Intent i = new Intent(DrawerActivity.this, Login.class);
            startActivity(i);
            finish();
        } else if (id == R.id.view_programs) {
            Intent i = new Intent(DrawerActivity.this, AllProgramsActivity.class);
            startActivity(i);

        }


        //else if (id == R.id.nav_manage) {

        // }
        //     else if (id == R.id.nav_share) {

        //   } else if (id == R.id.nav_send) {

        //  }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //onClick imageview (Search button)
    public void search(View view) {
        searchString= searchEt.getText().toString();
        viewToChange=view;
        if(searchString.length()==0){
            Toast.makeText(DrawerActivity.this, "Please enter a valid search term", Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedItemSpinner==StaffInfoByPIN && searchString.length()!=8){
            Toast.makeText(DrawerActivity.this, "PIN consist of 8 digits\nEnter them all", Toast.LENGTH_SHORT).show();
            return;
        }

//        HashMap<String, String> searchCriteria = new HashMap<>();
//        SOAPRequestHandler soapRequestHandler;
        if(selectedItemSpinner==StaffInfoByName){
//            searchCriteria.put("strStaffName", searchString);
//            soapRequestHandler = new SOAPRequestHandler(SOAPRequestHandler
//                    .StaffInfoByName, searchCriteria);
            searchByName(searchString);

        }else if(selectedItemSpinner==StaffInfoByPIN){
//            searchCriteria.put("strStaffPIN", searchString);
//            soapRequestHandler = new SOAPRequestHandler(SOAPRequestHandler
//                    .StaffInfoByPIN, searchCriteria);
            Toast.makeText(DrawerActivity.this, "Feature unavailable, our system does not assign pins" +
                    " to employees as of yet", Toast.LENGTH_SHORT).show();
            return;


        }
//        else{
//            searchCriteria.put("Program_ID", searchString);
//            soapRequestHandler = new SOAPRequestHandler(SOAPRequestHandler
//                    .GetStaffByProgram, searchCriteria);
//        }
        progressDialog= new ProgressDialog(this);
        progressDialog.setIndeterminate(false);

//        soapRequestHandler.getJsonObject();



    }

    private void searchByName(String name) {
        AsyncHttpClient asyncHttpClient= new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(5, 20000);
        RequestParams params= new RequestParams();
        params.put("Name",name);
        asyncHttpClient.post(this, AsyncHttpClientHelper.BASE_URL_SEARCHNAME, params, new
                JsonHttpResponseHandler() {
                    ProgressDialog dialog;


                    @Override
                    public void onStart() {
                        super.onStart();
                        dialog= new ProgressDialog(DrawerActivity.this);
                        dialog.setMessage("Loading");
                        dialog.setIndeterminate(true);
                        dialog.show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        displayResults(new JSONArray().put(response));
                    }

                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        displayResults(response);

                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                        super.onFinish();
                    }
                });


    }

    private void displayResults(JSONArray response) {
        this.response=response;
        ArrayList<String> names= new ArrayList<>();
        for(int i=0; i<response.length(); i++){
            try {
                JSONObject obj= response.getJSONObject(i);
                names.add(obj.getString("Name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        searchedNames=names;
        Log.i("TAG",searchedNames.toString());
        makeFragmentChanges(viewToChange);
    }

    public void makeFragmentChanges(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, new EmployeeInformation()).commit();
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//        Toast.makeText(this, "fragment commited", Toast.LENGTH_SHORT).show();
    }


//    class SOAPRequestHandler {
//        //    private static final int StaffInfoByName=0;
////    private static final int StaffInfoByName=0;
//        private int MODE;
//        private SoapSerializationEnvelope soapEnvelop;
//        HashMap<String, String> params, requestProperty;
//
//        public SOAPRequestHandler(int mode, HashMap<String, String> property) {
//            MODE = mode;
//            params = generateSOAPParameters();
//            requestProperty = property;
//        }
//
//        public SOAPRequestHandler(int mode) {
//            MODE = mode;
//            params = generateSOAPParameters();
//        }
//
//        private void getJsonObject() {
//
//            soapRequest();
//
//
//        }
//
//
//
//        private void soapRequest() {
//            SoapObject request = new SoapObject(params.get("NAMESPACE"), params.get("METHOD_NAME"));
//            soapEnvelop = new SoapSerializationEnvelope(SoapEnvelope.VER12);
//
//            if (MODE == StaffInfoByName) {
//                request.addProperty("strStaffName", requestProperty.get("strStaffName"));
//            } else if(MODE== StaffInfoByPIN){
//                request.addProperty("strStaffPIN", requestProperty.get("strStaffPIN"));
//            }else {
//                request.addProperty("Program_ID", requestProperty.get("Program_ID"));
//            }
//            soapEnvelop.dotNet = true;
//            soapEnvelop.setOutputSoapObject(request);
//
//            HttpTransportSE ht = new HttpTransportSE(params.get("URL"));
//            Log.i("TAG", "after ht declaration");
//            new ExecuteHTCall().execute(ht);
//
//        }
//
//
//        private HashMap<String, String> generateSOAPParameters() {
//            String SOAP_ACTION = "http://tempuri.org/StaffInfoByName";
//            String METHOD_NAME = "StaffInfoByName";
//            String NAMESPACE = "http://tempuri.org/";
//            String URL = "http://dataservice.brac.net:800/StaffInfo.asmx";
//            HashMap<String, String> staffInfoByName = new HashMap<>(4);
//            staffInfoByName.put("SOAP_ACTION", SOAP_ACTION);
//            staffInfoByName.put("METHOD_NAME", METHOD_NAME);
//            staffInfoByName.put("NAMESPACE", NAMESPACE);
//            staffInfoByName.put("URL", URL);
//
//
//            SOAP_ACTION = "http://tempuri.org/StaffInfoByPIN";
//            METHOD_NAME = "StaffInfoByPIN";
//            NAMESPACE = "http://tempuri.org/";
//            URL = "http://dataservice.brac.net:800/StaffInfo.asmx";
//            HashMap<String, String> staffInfoByPin = new HashMap<>(4);
//            staffInfoByPin.put("SOAP_ACTION", SOAP_ACTION);
//            staffInfoByPin.put("METHOD_NAME", METHOD_NAME);
//            staffInfoByPin.put("NAMESPACE", NAMESPACE);
//            staffInfoByPin.put("URL", URL);
//
//
//            SOAP_ACTION = "http://dss.brac.net/GetStaffByAnyInfo";
//            METHOD_NAME = "GetStaffByAnyInfo";
//            NAMESPACE = "http://dss.brac.net/";
//            URL = "http://dss.brac.net/bracstandingdata/Service.asmx";
//            HashMap<String, String> getStaffByProgram = new HashMap<>(4);
//            getStaffByProgram.put("SOAP_ACTION", SOAP_ACTION);
//            getStaffByProgram.put("METHOD_NAME", METHOD_NAME);
//            getStaffByProgram.put("NAMESPACE", NAMESPACE);
//            getStaffByProgram.put("URL", URL);
//
//
//            switch (MODE) {
//                case 0:
//                    return staffInfoByName;
//                case 1:
//                    return staffInfoByPin;
//                case 2:
//                    return getStaffByProgram;
//            }
//
//            return null;
//
//        }
//
//        class ExecuteHTCall extends AsyncTask<HttpTransportSE, Void, String> {
//            ProgressDialog progressDialog;
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                progressDialog= new ProgressDialog(context);
//                progressDialog.setIndeterminate(false);
//                progressDialog.show();
//            }
//
//            @Override
//            protected String doInBackground(HttpTransportSE... httpTransportSE) {
//                try {
//                    httpTransportSE[0].call(params.get("SOAP_ACTION"), soapEnvelop);
//                    return "" + (SoapPrimitive) soapEnvelop.getResponse();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                }
//
//                return "ERROR";
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//
////                Toast.makeText(DrawerActivity.this, s, Toast.LENGTH_SHORT).show();
//                detail= new ArrayList<>();
//                try {
//                    if (MODE != 2) {
//                        nameOrId(s);
//                    } else {
//                        programs(s);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
////                progressView.resetAnimation();
//                makeFragmentChanges(viewToChange);
//                progressDialog.dismiss();
//            }
//
//            private void programs(String s) throws JSONException {
//                JSONArray jsonArray = new JSONArray(s);
//                detail=new ArrayList<>();
//                Log.i("TAG","programs() & array length "+jsonArray.length());
////                detail.put("ProgramID")
//                String text = "";
//                HashMap<String,String> programDetail=new HashMap<String, String>();
//                programDetail.put("Type","Program");
//                detail.add(programDetail);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject obj = (JSONObject) jsonArray.get(i);
//                    programDetail=new HashMap<String, String>();
//
//
//                    String info = obj.getString("StaffName") + " ("+obj.getString("DesignationID")
//                            +") of " + obj.getString
//                            ("ProjectName").toUpperCase();
////                    response.add(info);
//                    programDetail.put("Program",info);
//                    detail.add(programDetail);
//                    text = text + "\n" + info + "\n====================================\n";
//                }
////                setTextToTextview(text);
//            }
//
//            private void nameOrId(String s) throws JSONException {
//                JSONArray jsonArray = new JSONArray(s);
//                String text = "";
//                HashMap<String,String> empInfo= new HashMap<>();
//                empInfo.put("Type","Employee");
//                detail.add(empInfo);
//                ArrayList<String> response = new ArrayList<>();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject obj = (JSONObject) jsonArray.get(i);
//                    String mname, mnameSpace = "";
//                    String name=obj.getString("Fname") + " " + (mname = obj.getString
//                            ("Mname")) +
//                            (mnameSpace = (!mname.equals("")) ? " " : "") + obj
//                            .getString("Lname");
//                    empInfo=new HashMap<String, String>();
//                    empInfo.put("Name",name);
//                    empInfo.put("Mobile",((mnameSpace=obj.getString("Mobile")).equals(""))?"N/A":
//                            mnameSpace);
//                    empInfo.put("Designation", obj.getString("Designation"));
//                    empInfo.put("Sex", obj.getString("Sex"));
//                    detail.add(empInfo);
//                }
////                setTextToTextview(text);
//            }
//        }
//
//    }

}
