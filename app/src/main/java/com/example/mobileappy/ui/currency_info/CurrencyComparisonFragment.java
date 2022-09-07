package com.example.mobileappy.ui.currency_info;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileappy.R;
import com.example.mobileappy.ui.conversion.ConversionFragment2;
import com.example.mobileappy.ui.datePicker.DatePickerFragment;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class CurrencyComparisonFragment extends Fragment implements DatePickerDialog.OnDateSetListener{
    AutoCompleteTextView atvFirst,atvSecond,atvMode;
    boolean callFromStart,callFromEnd;
    RequestQueue q;
    boolean crypto=false;
    String picker;
    DatePickerFragment datePickerStart,datePickerEnd;
    String url,url2,startDate,endDate;
    String currencykey1,currencykey2,currency1,currency2;
    private CurrencyComparisonViewModel mViewModel;
    TextView startDateView, endDateView;
    ArrayList<String> items = new ArrayList<>();
    ArrayList <String> itemsCrypto = new ArrayList<>();
    ArrayAdapter<String> adapterCrypto,adapterNational,adapterMode;
    ArrayList<String> rates;
    String mode;
    String [] modes={"National-National","National-Crypto","Crypto-National","Crypto-Crypto"};
    Button startDateButton,endDateButton,compareButton;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    ImageButton swapButton;
    View v;
    public static CurrencyComparisonFragment newInstance() {
        return new CurrencyComparisonFragment();
    }
    int startyear,startmonth,startday,endyear,endmonth,endday;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_currency_comparison, container, false);
        setReferences();
        getData();
        setInitDate();
        setButtonListeners();
        return v;
    }
    public void setReferences(){

        atvFirst=v.findViewById(R.id.auto_complete_first_currency);
        atvSecond=v.findViewById(R.id.auto_complete_second_currency);
        atvMode=v.findViewById(R.id.auto_complete_mode_comparison);
        startDateView=v.findViewById(R.id.startDateView);
        endDateView=v.findViewById(R.id.endDateView);
        startDateButton=v.findViewById(R.id.startDateButton);
        endDateButton=v.findViewById(R.id.endDateButton);
        compareButton=v.findViewById(R.id.compareButton);
        swapButton=v.findViewById(R.id.swapButtonComparison);
    }
    public void getData(){
        q=Volley.newRequestQueue(v.getContext());
        url="https://api.exchangerate.host/symbols";
        JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, url,null , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject symbols= response.getJSONObject("symbols");
                    Iterator<String> symbolkeys=symbols.keys();
                    while (symbolkeys.hasNext()){
                        String key=symbolkeys.next();
                        items.add(symbols.getJSONObject(key).get("description")+"-"+key);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(v.getContext(), "Server error try again later!", Toast.LENGTH_LONG).show();

            }
        });
        String url2="https://api.exchangerate.host/cryptocurrencies";
        JsonObjectRequest req2= new JsonObjectRequest(Request.Method.GET, url2,null , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject symbols= response.getJSONObject("cryptocurrencies");
                    Iterator <String> symbolkeys=symbols.keys();
                    while (symbolkeys.hasNext()){
                        String key=symbolkeys.next();
                        itemsCrypto.add(symbols.getJSONObject(key).get("name")+"-"+symbols.getJSONObject(key).get("symbol"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        q.add(req);
        q.add(req2);
        adapterNational=new ArrayAdapter<String>(v.getContext(),R.layout.list_item,items);
        adapterCrypto=new ArrayAdapter<String>(v.getContext(),R.layout.list_item,itemsCrypto);
        atvFirst.setAdapter(adapterNational);
        atvSecond.setAdapter(adapterNational);
        atvFirst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                       @Override
                                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                           String []data=adapterView.getItemAtPosition(i).toString().split("-");
                                           currencykey1=data[data.length-1];
                                           //avt.setText("");
                                           currency1=adapterView.getItemAtPosition(i).toString();
                                           Toast.makeText(v.getContext(), currencykey1, Toast.LENGTH_SHORT).show();
                                       }
                                   }
        );

        atvSecond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String []data=adapterView.getItemAtPosition(i).toString().split("-");

                                            currencykey2=data[data.length-1];
                                            currency2=adapterView.getItemAtPosition(i).toString();
                                            Toast.makeText(v.getContext(), currencykey2, Toast.LENGTH_SHORT).show();
                                        }
                                    }
        );
        adapterMode=new ArrayAdapter<String>(v.getContext(),R.layout.list_item,modes);
        atvMode.setAdapter(adapterMode);
        atvMode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            mode=adapterView.getItemAtPosition(i).toString();
                                            if(mode.equals("National-National")){
                                                atvFirst.setAdapter(adapterNational);
                                                atvSecond.setAdapter(adapterNational);
                                                crypto=false;
                                            }
                                            else if (mode.equals("National-Crypto")){
                                                atvFirst.setAdapter(adapterNational);
                                                atvSecond.setAdapter(adapterCrypto);
                                                crypto=true;

                                            }
                                            else if (mode.equals("Crypto-National")){
                                                atvFirst.setAdapter(adapterCrypto);
                                                atvSecond.setAdapter(adapterNational);
                                                crypto=false;
                                            }
                                            else {
                                                atvFirst.setAdapter(adapterCrypto);
                                                atvSecond.setAdapter(adapterCrypto);
                                                crypto=true;
                                            }

                                        }
                                    }
        );
        mode="National-National";
        atvMode.setText(mode,false);

    }
    public void setButtonListeners(){
        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currencykey1==null || currencykey2==null){
                    Toast.makeText(v.getContext(), "Choose the currencies for comparison first!", Toast.LENGTH_SHORT).show();

                }
                else{

                    String urlresult="https://api.exchangerate.host/timeseries?start_date="+startDate+"&end_date="+endDate+"&symbols="+currencykey2+"&base="+currencykey1;
                    if(crypto)
                        urlresult+="&source=crypto";
                    Log.d("url",urlresult);
                    //Toast.makeText(getActivity().getApplicationContext(), urlresult, Toast.LENGTH_LONG).show();
                    JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, urlresult,null , new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dailyRates=response.getJSONObject("rates");
                                Iterator<String> keys=dailyRates.keys();
                                //Log.d("length",)
                                ArrayList<String> dates=new ArrayList<>();
                                rates=new ArrayList<String>();
                                while (keys.hasNext()){
                                    String key=keys.next();
                                    String s=(dailyRates.getJSONObject(key).getDouble(currencykey2))+"";
                                    rates.add(s);
                                    dates.add(key);

                                }
                                Bundle b=new Bundle();
                                b.putStringArrayList("data",rates);
                                b.putStringArrayList("dates",dates);
                                b.putString("key1",currencykey1);
                                b.putString("key2",currencykey2);
                                getParentFragmentManager().setFragmentResult("key",b);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    q.add(req);
                    Navigation.findNavController(view).navigate(R.id.action_nav_currency_info_to_graphFragment);
                }

            }
        });
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFromEnd=false;
                callFromStart=true;
                picker="startPicker";
                try {
                    datePickerStart=DatePickerFragment.newInstance(sdf.parse(endDate).getTime(),"start");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerStart.setTargetFragment(CurrencyComparisonFragment.this,0);
                datePickerStart.show(datePickerStart.getTargetFragment().getFragmentManager(), "start");

            }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callFromEnd=true;
                callFromStart=false;
                picker="endPicker";

                try {
                    datePickerEnd=DatePickerFragment.newInstance(sdf.parse(startDate).getTime(),"end");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerEnd.setTargetFragment(CurrencyComparisonFragment.this,0);
                datePickerEnd.show(datePickerEnd.getTargetFragment().getFragmentManager(), "endPicker");

            }
        });
        swapButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String temp,temp2;
                        temp=currency2;
                        currency2=currency1;
                        currency1=temp;
                        temp2=currencykey1;
                        currencykey1=currencykey2;
                        currencykey2=temp2;

                        if(mode.equals("National-Crypto")){
                            mode="Crypto-National";

                            atvFirst.setAdapter(adapterCrypto);
                            atvSecond.setAdapter(adapterNational);
                            atvFirst.setText("");
                            atvSecond.setText("");
                            crypto=false;
                        }
                        else if(mode.equals("Crypto-National")){
                            mode="National-Crypto";
                            atvFirst.setAdapter(adapterNational);
                            atvSecond.setAdapter(adapterCrypto);
                            atvFirst.setText("");
                            atvSecond.setText("");
                            crypto=false;
                        }

                        atvFirst.setText(currency1,false);
                        atvSecond.setText(currency2,false);
                        atvMode.setText(mode,false);


                    }
                }
        );
    }
    public void setInitDate(){
        Date currentDate=new Date();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date todate1 = cal.getTime();
        startDate = sdf.format(todate1);
        endDate=sdf.format(currentDate);
        startDateView.setText(" Start date: "+startDate+" ");
        endDateView.setText(" End date: "+endDate+" ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v=null;
    }

    @Override
   public void onSaveInstanceState(@NonNull Bundle outState) {
       super.onSaveInstanceState(outState);
   }
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.d("picker",picker);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        if(picker.equals("startPicker")){

            this.startyear=i;
            this.startmonth=i1+1;
            this.startday=i2;
            startDate=this.startyear+"-";
            if(startmonth<10)
                startDate+="0"+startmonth+"-";
            else startDate+=startmonth+"-";
            if(startday<10)
                startDate+="0"+startday;
            else startDate+=startday;
            startDateView.setText(" Start Date:"+startDate+" ");
        }
        else if(picker.equals("endPicker")){

            this.endyear=i;
            this.endmonth=i1+1;
            this.endday=i2;
            endDate=this.endyear+"-";
            if(endmonth<10)
                endDate+="0"+endmonth+"-";
            else endDate+=endmonth+"-";
            if(endday<10)
                endDate+="0"+endday;
            else endDate+=endday;
            endDateView.setText(" End date:"+endDate+" ");
        }

    }
}