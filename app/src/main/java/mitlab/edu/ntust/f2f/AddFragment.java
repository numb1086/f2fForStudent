package mitlab.edu.ntust.f2f;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by 勇霆 on 2016/3/21.
 */

public class AddFragment  extends Fragment {
    private Button btnAdd;
    private EditText editDate;
    private ListView listView,listView2;
    private String[] timeSlot,timeSlot2;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_add, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBackground();
        btnAdd = (Button) this.getView().findViewById(R.id.btnAdd);
        editDate = (EditText) this.getView().findViewById(R.id.edit_date);
        listView = (ListView)this.getView().findViewById(R.id.listTime);
        listView2 = (ListView)this.getView().findViewById(R.id.listTime2);
        calendar = Calendar.getInstance();
        //Set time
        timeSlot = new String[9];
        timeSlot2 = new String[9];
        timeSlot[0] = "09:00~09:30";
        timeSlot2[0] = "09:30~10:00";
        for(int i=1,j=10;i<timeSlot.length;i++,j++){
            timeSlot[i] = j + ":00~" + j + ":30";
            timeSlot2[i] = j + ":30~" + (j + 1) + ":00";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),R.layout.simple_list_item_custom,timeSlot);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(),R.layout.simple_list_item_custom,timeSlot2);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);
        btnAdd.setOnClickListener(btnListener);
        editDate.setOnClickListener(editListener);
    }

    private Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String date,time="";
            date = "date=" + editDate.getText().toString();
            for(int i=0;i<timeSlot.length;i++){
                if(listView.isItemChecked(i)) time += "&time[]="+timeSlot[i];
                if(listView2.isItemChecked(i)) time += "&time[]="+timeSlot2[i];
            }
            if(editDate.length()!=0 && time.length()!=0) {
                final String postData = date + time;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postText(postData);
                    }
                });
                thread.start();
                FragmentTabHost tabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
                tabHost.setCurrentTab(1);
            }else{
                new AlertDialog.Builder(getActivity())
                        .setTitle("Hint message").setIcon(R.drawable.favicon)
                        .setMessage("Please select at lease one date/time.")
                        .setPositiveButton("Confirm",null).show();
            }
        }
    };
    private EditText.OnClickListener editListener = new EditText.OnClickListener(){
        @Override
        public void onClick(View v) {
            datePickerDialog = new DatePickerDialog(getActivity(), datepicker,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    };
    private DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy-MM-dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
            editDate.setText(sdf.format(calendar.getTime()));
        }
    };
    private void setBackground(){
        LinearLayout lin = (LinearLayout) this.getView().findViewById(R.id.linLayout2);
        int[] bg = new int[8];
        bg[0] = R.drawable.bg1;
        bg[1] = R.drawable.bg2;
        bg[2] = R.drawable.bg3;
        bg[3] = R.drawable.bg4;
        bg[4] = R.drawable.bg5;
        bg[5] = R.drawable.bg6;
        bg[6] = R.drawable.bg7;
        bg[7] = R.drawable.bg8;
        lin.setBackgroundResource(bg[(int)(Math.random()*8)]);
    }
    // This will post our text data
    private void postText(String postData){
        try {
            URL url = new URL("http://140.118.122.246/f2f/app/addData.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setConnectTimeout(30000);
            //Send request
            OutputStream output = http.getOutputStream();
            output.write(postData.getBytes());
            output.flush();
            output.close();

            //Get Response
            int responseCode = http.getResponseCode();
            if(responseCode == 200) {
                InputStream input = http.getInputStream();
                byte[] data = new byte[2048];
                int size = input.read(data);
                final String str = new String(data, 0, size);
                input.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (str.equals("1")){ //Add successfully
                            Toast.makeText(getActivity(), "Succeed!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            http.disconnect();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}