package com.jems.sqlitedatabase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private EditText etUserName, etPassword, etSearch;
    private DatabaseAdapter databaseAdapter;
    private ListView listView;
    private static String uid, name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        etSearch = findViewById(R.id.etSearch);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        databaseAdapter = new DatabaseAdapter(this);
    }

    public void registerUser(View view) {
        String name = etUserName.getText().toString();
        String password = etPassword.getText().toString();

        long result = databaseAdapter.insertData(name, password);
        //if record inserted then it return the row id that inserted other wise it show the negative number means number less than zero
        if (result < 0) {
            ToastMessage.show(this, "Data Insertion Failed!!! : result Id : " + result);
        } else {
            ToastMessage.show(this, "Data Insertion Successfully!!! : result Id : " + result);
            showRecords(null);
        }
    }

    public void showRecords(View view) {

        ArrayList<String> arrayListRecords = databaseAdapter.fetchAllRecords();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListRecords);
        listView.setAdapter(arrayAdapter);

    }

    public void updateRecord(View view) {

        String uname = etUserName.getText().toString();
        String upassword = etPassword.getText().toString();

        int result = databaseAdapter.updateRecord(uid, uname, upassword);

        //result == 1 record updated successfully
        //result == 0 does not updated record
        if (result == 1) {
            ToastMessage.show(this, "Record Updated Successfully");
            showRecords(null);
        } else {
            ToastMessage.show(this, "Record Updation Failed");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String data = (String) listView.getItemAtPosition(position);

        //sample record  = 1 arslan 123
        uid = data.substring(0, data.indexOf(" ")); // we get id 1
        name = data.substring(data.indexOf(" ") + 1, data.lastIndexOf(" ")); //arslan
        password = data.substring(data.lastIndexOf(" ") + 1); // 123

        etUserName.setText(name);
        etPassword.setText(password);
    }

    public void searchUserRecord(View view) {
        String result = databaseAdapter.searchRecord(etSearch.getText().toString());
        ToastMessage.show(this, result);
    }

    public void deleteRecord(View view) {
        int count = databaseAdapter.deleteRecord(uid);
        //count == 1 Record DELETED successfully
        //count == 0 Record does not DELETED
        if (count == 1) {
            ToastMessage.show(this, "Record DELETED Successfully : " + count);
            showRecords(null);
        } else {
            ToastMessage.show(this, "Record Does NOT DELETED : " + count);
        }
    }
}
