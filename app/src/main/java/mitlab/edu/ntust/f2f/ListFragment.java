package mitlab.edu.ntust.f2f;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 勇霆 on 2016/3/21.
 */
public class ListFragment extends Fragment {
    private Button btnHome,btnRefresh,btnRemove;
    private ListView listView;
    private ListViewAdapter2 adapter;
    private ArrayList<HashMap<Integer, String>> list;
    private ProgressDialog progress;
    private String removeData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBackground();
        btnHome = (Button) this.getView().findViewById(R.id.btnHome);
        btnRefresh = (Button) this.getView().findViewById(R.id.btnRefresh2);
        btnRemove = (Button) this.getView().findViewById(R.id.btnRemoveAll);
        listView = (ListView)this.getView().findViewById(R.id.addResult);
        list = new ArrayList<>();
        adapter = new ListViewAdapter2(getActivity(),android.R.layout.simple_list_item_1,list);
        getData();//Get database data

        btnHome.setOnClickListener(btnListener);
        btnRefresh.setOnClickListener(btnListener);
        btnRemove.setOnClickListener(btnListener);
        listView.setOnItemClickListener(listViewListener);
    }

    Button.OnClickListener btnListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnHome:
                    getActivity().finish();
                    break;
                case R.id.btnRefresh2:
                    //Get data
                    list.clear();
                    adapter.clear();
                    getData();
                    break;
                case R.id.btnRemoveAll:
                    if(list.size()!=0) {
                        new AlertDialog.Builder(getActivity()).setTitle("Remove all the date/time").setMessage("Are you sure?")
                        .setIcon(R.drawable.favicon).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeData = "dataAll[]=" + list.get(0).get(R.string.Date).toString() + ","
                                        + list.get(0).get(R.string.Time).toString() + ",1";
                                for (int i = 1; i < list.size(); i++) {
                                    removeData += "&dataAll[]=" + list.get(i).get(R.string.Date).toString() + ","
                                            + list.get(i).get(R.string.Time).toString() + ",1";
                                }
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Loading data
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress = new ProgressDialog(getActivity());
                                                progress.setTitle("Removing data");
                                                progress.setMessage("Please wait a moment...");
                                                progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                progress.show();
                                            }
                                        });
                                        try{
                                            Thread.sleep(500);
                                        }catch (InterruptedException e){
                                            e.printStackTrace();
                                        }
                                        //Remove data
                                        removeText(removeData);
                                    }
                                });
                                thread.start();
                                list.clear();
                                adapter.clear();
                            }
                        }).setNegativeButton("No", null).show();
                    }
                    break;
                default:
            }
        }
    };
    ListView.OnItemClickListener listViewListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertDialog.Builder(getActivity()).setTitle("Remove this date/time").setMessage("Are you sure?")
            .setIcon(R.drawable.favicon).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            removeData = "dataAll[]=" + list.get(position).get(R.string.Date).toString() + ","
                                    + list.get(position).get(R.string.Time).toString() + ",1";
                            //Remove data
                            removeText(removeData);
                            //Get data
                            list.clear();
                            getData();
                        }
                    });
                    thread.start();
                }
            }).setNegativeButton("No",null).show();
        }
    };
    private void setBackground(){
        LinearLayout lin = (LinearLayout) this.getView().findViewById(R.id.linLayout3);
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
    public void getData(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Loading data
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress = new ProgressDialog(getActivity());
                        progress.setTitle("Loading data");
                        progress.setMessage("Please wait a moment...");
                        progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        progress.show();
                    }
                });
                try{
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                //Get data
                post();
            }
        });
        thread.start();
    }

    private void post(){
        try {
            URL url = new URL("http://140.118.122.246/f2f/app/showAdd.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setConnectTimeout(30000);
            //Get Response
            InputStream input = http.getInputStream();
            byte[] data = new byte[2048];
            int size = input.read(data);
            String str="";
            if(size>=0) {
                str = new String(data, 0, size);
            }
            input.close();
            http.disconnect();
            String[] message = str.split(" ");
            String[] detail;
            for (int i = 0; i < message.length && !str.equals(""); i++){
                HashMap<Integer, String> temp = new HashMap<>();
                detail = message[i].split(",");
                temp.put(R.string.Date, detail[0]);
                temp.put(R.string.Time, detail[1]);
                list.add(temp);
            }
            progress.dismiss();
            if(list.size()!=0){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(adapter);
                    }
                });
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void removeText(String removeData){
        try {
            URL url = new URL("http://140.118.122.246/f2f/app/deleteData.php");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");

            //Send request
            OutputStream output = http.getOutputStream();
            output.write(removeData.getBytes());
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
                        if (!str.equals("0")) { //Add successfully
                            Toast.makeText(getActivity(), "Succeed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            http.disconnect();
            progress.dismiss();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}