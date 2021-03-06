package com.example.kouveepetshop.Pengelolaan.Produk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kouveepetshop.API.Rest_API;
import com.example.kouveepetshop.MainActivity;
import com.example.kouveepetshop.Pengelolaan.KeteranganDAO;
import com.example.kouveepetshop.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Produk_Tambah extends AppCompatActivity {
    private String nama;
    private String satuan;
    private Integer harga, jumlah, jumlah_minimal, id_jenis;
    private final String id_pegawai = "Yosafat9204";
    private ArrayList<String> mItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ProgressDialog pd;
    private String ip = MainActivity.getIp();
    private Spinner kategori_spinner;
    private Button tambah;
    private ArrayList<KeteranganDAO> kategori_produk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produk_add);

        init();

        loadjson();

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduk();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

    }

    private void addProduk(){
        getValue();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://" + ip + "/rest_api-kouvee-pet-shop-master/index.php/Produk";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  request = new HashMap<String, String>();
                request.put("nama", nama);
                request.put("id_kategori_produk",  String.valueOf(id_jenis));
                request.put("harga", String.valueOf(harga));
                request.put("satuan", String.valueOf(satuan));
                request.put("jmlh", String.valueOf(jumlah));
                request.put("jmlh_min", String.valueOf(jumlah_minimal));
                request.put("created_by", id_pegawai);
                return request;
            }
        };
        queue.add(postRequest);
    }

    private void loadjson() {
        pd.setMessage("Mengambil Data");
        pd.setCancelable(false);
        pd.show();
        String url = "http://" + ip + "/rest_api-kouvee-pet-shop-master/index.php/KategoriProduk/";

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("volley", "response : " + response.toString());

                try {
                    JSONArray massage = response.getJSONArray("message");

                    for (int i = 0; i < massage.length(); i++) {
                        JSONObject massageDetail = massage.getJSONObject(i);
                        mItems.add(massageDetail.getString("keterangan"));

                        KeteranganDAO keterangan = new KeteranganDAO();
                        keterangan.setId(massageDetail.getInt("id"));
                        keterangan.setKeterangan(massageDetail.getString("keterangan"));
                        kategori_produk.add(keterangan);

                        adapter.notifyDataSetChanged();
                    }
                    pd.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pd.cancel();
                Log.d("volley", "error : " + error.getMessage());
            }
        });
        Rest_API.getInstance(this).addToRequestQueue(arrayRequest);
    }

    private void init(){
        pd = new ProgressDialog(this);
        mItems = new ArrayList<>();
        kategori_produk = new ArrayList<>();

        tambah = findViewById(R.id.produk_tambah_add);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategori_spinner = findViewById(R.id.produk_add_spinner);
        kategori_spinner.setAdapter(adapter);
    }

    private void getValue(){
        EditText nama_text = findViewById(R.id.produk_tambah_nama);
        EditText harga_text = findViewById(R.id.produk_tambah_harga);
        EditText satuan_text = findViewById(R.id.produk_tambah_satuan);
        EditText jumlah_text = findViewById(R.id.produk_tambah_jmlh);
        EditText jumlah_minimal_text = findViewById(R.id.produk_tambah_jmlh_min);

        nama = nama_text.getText().toString();
        String jenis = kategori_spinner.getSelectedItem().toString();
        KeteranganDAO keterangan = new KeteranganDAO();

        for (int i = 0 ; i < kategori_produk.size(); i++) {
            keterangan = kategori_produk.get(i);
            if(keterangan.getKeterangan().equals(jenis)){
                break;
            }
        }
        id_jenis = keterangan.getId();
        harga = Integer.parseInt(harga_text.getText().toString());
        satuan = satuan_text.getText().toString();
        jumlah = Integer.parseInt(jumlah_text.getText().toString());
        jumlah_minimal = Integer.parseInt(jumlah_minimal_text.getText().toString());
    }
}
