package com.example.mobileappy.ui.graphs;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobileappy.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GraphFragment extends Fragment {
    LineGraphSeries<DataPoint> series;
    private GraphViewModel mViewModel;
    private  final DecimalFormat df = new DecimalFormat("0.00");

    GraphView gw;
    public static GraphFragment newInstance() {
        return new GraphFragment();
    }
    View v;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_graph, container, false);

        gw=v.findViewById(R.id.graphView);

        getParentFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                TextView txt=v.findViewById(R.id.testText);
                ArrayList<String> list=result.getStringArrayList("data");
                String currencykey1=result.getString("key1");
                String currencykey2=result.getString("key2");
                ArrayList<String> list2=result.getStringArrayList("dates");
                Double initValue=Double.parseDouble(list.get(0));
                Double finalValue=Double.parseDouble(list.get(list.size()-1));

                String analysis=" ";
                if(initValue>finalValue){
                    String percentage=df.format(100-finalValue/initValue*100);
                    analysis+=currencykey1+" has fallen in value compared to "+currencykey2+" by "+percentage+"% ";

                }
                else if(initValue<finalValue){
                    String percentage=df.format(finalValue/initValue*100-100);
                    analysis+=currencykey1+" has increased in value compared to "+currencykey2+" by "+percentage+"% ";
                }
                else{
                    analysis+=currencykey1+" has retained the same value compared to "+currencykey2;
                }
                txt.setText(analysis);
                int n=list.size();
                series=new LineGraphSeries<>();
                series.setColor(ContextCompat.getColor(v.getContext(),R.color.teal200_dark));
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                for(int i=0;i<n;i++){
                    try {
                        Date date=sdf.parse(list2.get(i));
                        DataPoint d=new DataPoint(date,Double.parseDouble(list.get(i)));
                        Log.d("dates",date.toString());
                        series.appendData(d,true,n);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
               /* gw.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if(isValueX)
                        {
                            return sdf.format(new Date((long)value));
                        }
                        else
                        return super.formatLabel(value, isValueX);
                    }
                });*/
                //gw.getViewport().setScalableY(true);
                //gw.getGridLabelRenderer().setVerticalLabelsVAlign();
                //gw.getViewport().setXAxisBoundsManual(true);
               /* gw.getGridLabelRenderer().setHorizontalAxisTitle("Date");
                gw.getGridLabelRenderer().setHorizontalAxisTitleTextSize(60);
                gw.getGridLabelRenderer().setVerticalAxisTitle("Value");
                gw.getGridLabelRenderer().setVerticalAxisTitleTextSize(60);*/
                //gw.getViewport().setScalable(true);
                //gw.getViewport().setScrollable(true);
                gw.getGridLabelRenderer().setHorizontalLabelsColor(ContextCompat.getColor(v.getContext(),R.color.teal200_dark));
                gw.getGridLabelRenderer().setVerticalLabelsColor(ContextCompat.getColor(v.getContext(),R.color.teal200_dark));
                gw.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(v.getContext()));
                gw.addSeries(series);
            }
        });
        return v;
    }

   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GraphViewModel.class);

    }*/

}