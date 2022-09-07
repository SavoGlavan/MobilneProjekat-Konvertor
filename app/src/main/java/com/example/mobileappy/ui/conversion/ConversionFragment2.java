package com.example.mobileappy.ui.conversion;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.mobileappy.ui.datePicker.DatePickerFragment;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

public class ConversionFragment2 extends Fragment implements DatePickerDialog.OnDateSetListener {
    RequestQueue q;
    String currencykey1 = null;
    String currencykey2 = null;
    private ConversionFragment2ViewModel mViewModel;
    String url="https://api.exchangerate.host/symbols";
    ArrayList <String> items;
    ArrayList <String> itemsCrypto;
    AutoCompleteTextView avt;
    AutoCompleteTextView avt2;
    AutoCompleteTextView avt3;
    ArrayAdapter<String> adapterMode;
    ArrayAdapter<String > adapter;
    boolean crypto;
    Button conversionButton,dateButton;
    ImageButton swapButton;
    String currency1, currency2;
    ArrayAdapter <String> adapterCrypto;
    EditText amount;
    String date;
    TextView result;
    String mode;
    String [] modes={"National-National","National-Crypto","Crypto-National","Crypto-Crypto"};
    TextView dateView;
    int year,month,day;
    View v;
    public static ConversionFragment2 newInstance() {
        return new ConversionFragment2();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_conversion, container, false);
        setReferences();
        getData();
        setButtonListeners();
        initDate();
        return v;
    }
    public void initDate(){
        Date currentDate=new Date();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        date=sdf.format(currentDate);
        dateView.setText(" Current date: "+date+" ");
    }

    public void setReferences(){
        items = new ArrayList<>();
        itemsCrypto = new ArrayList<>();
        avt=v.findViewById(R.id.auto_complete_txt);
        avt2=v.findViewById(R.id.auto_complete_txt2);
        avt3=v.findViewById(R.id.auto_complete_txt3);
        conversionButton=v.findViewById(R.id.conversionButton);
        swapButton=v.findViewById(R.id.swapButton);
        dateView=v.findViewById(R.id.dateTextView);
        amount=v.findViewById(R.id.amount);
        amount.setText(" 1.00 ");
        dateButton=v.findViewById(R.id.dateButton);
        result=v.findViewById(R.id.result);
        adapter=new ArrayAdapter<String>(getActivity(),R.layout.list_item,items);
        adapterCrypto=new ArrayAdapter<String>(getActivity(),R.layout.list_item,itemsCrypto);
        adapterMode=new ArrayAdapter<String>(getActivity(),R.layout.list_item,modes);
    }
    public void setButtonListeners(){
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

                            avt.setAdapter(adapterCrypto);
                            avt2.setAdapter(adapter);
                            avt.setText("");
                            avt2.setText("");
                            crypto=false;
                        }
                        else if(mode.equals("Crypto-National")){
                            mode="National-Crypto";
                            avt.setAdapter(adapter);
                            avt2.setAdapter(adapterCrypto);
                            avt.setText("");
                            avt2.setText("");
                            crypto=false;
                        }

                        avt.setText(currency1,false);
                        avt2.setText(currency2,false);
                        avt3.setText(mode,false);


                    }
                }
        );
        conversionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currencykey1==null || currencykey2==null){
                    Toast.makeText(v.getContext(), "Choose the currencies for conversion first!", Toast.LENGTH_SHORT).show();

                }
                else if(String.valueOf(amount.getText())==null || String.valueOf(amount.getText()).equals("")){
                    Toast.makeText(v.getContext(), "Type the amount you wish to convert!", Toast.LENGTH_SHORT).show();
                }
                else{

                    String urlresult="https://api.exchangerate.host/convert?from="+currencykey1+"&to="+currencykey2+"&amount="+String.valueOf(amount.getText())+"&date="+date;
                    if(crypto)
                        urlresult+="&source=crypto";
                    Log.d("url",urlresult);
                    //Toast.makeText(getActivity().getApplicationContext(), urlresult, Toast.LENGTH_LONG).show();
                    JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, urlresult,null , new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                DecimalFormat df=new DecimalFormat("0.000000");
                                Double conversionresult= response.getDouble("result");

                                result.setText(" Result:"+df.format(conversionresult)+" ");
                                result.setVisibility(View.VISIBLE);
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
                }
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker=DatePickerFragment.newInstance(0,"conversion");
                datePicker.setTargetFragment(ConversionFragment2.this,0);
                datePicker.show(datePicker.getTargetFragment().getFragmentManager(), "Date Picker");
            }
        });

    }
    public void getData(){
        q= Volley.newRequestQueue(v.getContext());
        Toast.makeText(v.getContext(), "getData called", Toast.LENGTH_SHORT).show();
        JsonObjectRequest req= new JsonObjectRequest(Request.Method.GET, url,null , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject symbols= response.getJSONObject("symbols");
                    Iterator <String> symbolkeys=symbols.keys();
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
        Collections.reverse(itemsCrypto);

        avt.setAdapter(adapter);
        avt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                       @Override
                                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                           String []data=adapterView.getItemAtPosition(i).toString().split("-");
                                           currencykey1=data[data.length-1];
                                           //avt.setText("");
                                           currency1=adapterView.getItemAtPosition(i).toString();
                                           //Toast.makeText(v.getContext(), currencykey1, Toast.LENGTH_SHORT).show();
                                       }
                                   }
        );
        avt2.setAdapter(adapter);
        avt2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            String []data=adapterView.getItemAtPosition(i).toString().split("-");

                                            currencykey2=data[data.length-1];
                                            currency2=adapterView.getItemAtPosition(i).toString();
                                            Toast.makeText(v.getContext(), currencykey2, Toast.LENGTH_SHORT).show();
                                        }
                                    }
        );
        avt3.setAdapter(adapterMode);
        avt3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            mode=adapterView.getItemAtPosition(i).toString();
                                            if(mode.equals("National-National")){
                                                avt.setAdapter(adapter);
                                                avt2.setAdapter(adapter);
                                                crypto=false;
                                            }
                                            else if (mode.equals("National-Crypto")){
                                                avt.setAdapter(adapter);
                                                avt2.setAdapter(adapterCrypto);
                                                crypto=true;

                                            }
                                            else if (mode.equals("Crypto-National")){
                                                avt.setAdapter(adapterCrypto);
                                                avt2.setAdapter(adapter);
                                                crypto=false;
                                            }
                                            else {
                                                avt.setAdapter(adapterCrypto);
                                                avt2.setAdapter(adapterCrypto);
                                                crypto=true;
                                            }

                                        }
                                    }
        );
        mode="National-National";
        avt3.setText(mode,false);
        crypto=false;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        v=null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        this.year=i;
        this.month=i1+1;
        this.day=i2;
        date=this.year+"-";
        if(month<10)
            date+="0"+month+"-";
        else date+=month+"-";
        if(day<10)
            date+="0"+day;
        else date+=day;
        dateView.setText(" Current date: "+date+" ");

    }
}