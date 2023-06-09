 package com.project.barbershop;

        import android.app.DatePickerDialog;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.net.Uri;
        import android.os.Bundle;
        import android.util.Base64;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.Toast;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.google.android.material.bottomnavigation.BottomNavigationView;
        import com.project.barbershop.apiConfig.apiConfig;
        import com.project.barbershop.servis.SharedPreferenceManager;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.ByteArrayOutputStream;
        import java.io.InputStream;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.Locale;
        import java.util.Map;

public class BookingActivity1 extends AppCompatActivity {

    private static final String url = apiConfig.URL_API+"/postBooking";

    private EditText etName, etNoTelpon, etJenisPelayanan, etHarga;
    private Button btnSubmit;
    private Button btnTglBooking;

    private Button choose;
    private Spinner spinner;
    private Spinner spinner2;
    String encodeImageString;
    private Calendar calendar;
    private String email;
    private SharedPreferenceManager sharedPreferenceManager;
    Bitmap bitmap;
    ImageView img;

    private Uri selectedImageUri;

    int PICK_IMAGE_REQUEST = 111;
    String imageString = ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_test);

        spinner = findViewById(R.id.jam_booking);
        // Buat array opsi jam
        String[] jamOptions = {"13:00", "14:00", "15:00"};



        //spinner jenis pelayanan
        spinner2 = findViewById(R.id.spinner_jenis_pelayanan);
        ArrayAdapter<CharSequence> adapterJenisPelayanan = ArrayAdapter.createFromResource(this,
                R.array.jenis_pelayanan_options, android.R.layout.simple_spinner_item);
        adapterJenisPelayanan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapterJenisPelayanan);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pilihan = parent.getItemAtPosition(position).toString();
                String harga = "";

                switch (pilihan) {
                    case "Gentlemen Cut":
                        harga = "100000";
                        break;
                    case "Smoothing":
                        harga = "200000";
                        break;
                    case "Black Hair Coloring":
                        harga = "300000";
                        break;
                    case "Grooming and Hair Tattoo":
                        harga = "400000";
                        break;
                    case "Hair Coloring":
                        harga = "500000";
                        break;
                }

                etHarga.setText(harga);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak ada yang dipilih, tidak ada tindakan yang diambil.
            }
        });






        // Buat adapter untuk Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jamOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Atur listener untuk item yang dipilih
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJam = jamOptions[position];
                // Lakukan tindakan sesuai dengan jam yang dipilih
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak ada jam yang dipilih
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_booking);

        etName = findViewById(R.id.username);
        etNoTelpon = findViewById(R.id.no_telpon);
        etHarga = findViewById(R.id.harga);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnTglBooking = findViewById(R.id.btnTglBooking);
        choose = findViewById(R.id.choose);
        ProgressDialog progressDialog;
        sharedPreferenceManager = new SharedPreferenceManager(this);
        email = sharedPreferenceManager.getEmail();

        fetchProfile();
        btnTglBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingUser();
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.bottom_booking:
                    startActivity(new Intent(getApplicationContext(), BookingActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.bottom_order:

                    return true;
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;

            }
            return false;
        });
    }

    private void fetchProfile() {
        String url = apiConfig.URL_API + "/profile?email=" + email;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String username = response.getString("nama");
                            String noTelepon = response.getString("no_telepon");

                            etName.setText("  " + username);
                            etNoTelpon.setText(" " + noTelepon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BookingActivity1.this, "Failed to parse profile data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(BookingActivity1.this, "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void showDatePicker() {
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity1.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String selectedDate = sdf.format(calendar.getTime());
                        btnTglBooking.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri filepath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                encodeBitmapImage(bitmap);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesofimage = byteArrayOutputStream.toByteArray();
        encodeImageString = Base64.encodeToString(bytesofimage, Base64.DEFAULT);
    }

    private void bookingUser() {
        final String nama = etName.getText().toString().trim();
        final String no_telpon = etNoTelpon.getText().toString().trim();
        final String jenis_pelayanan = spinner2.getSelectedItem().toString().trim();
        final String harga = etHarga.getText().toString().trim();
        final String tanggal_booking = btnTglBooking.getText().toString().trim();
        final String jam_booking = spinner.getSelectedItem().toString().trim();

        // Buat request POST ke URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Tanggapan dari server jika pendaftaran berhasil
                        Toast.makeText(BookingActivity1.this, "Booking Berhasil", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Tanggapan dari server jika terjadi kesalahan
                        Toast.makeText(BookingActivity1.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("nama", nama);
                params.put("no_telpon", no_telpon);
                params.put("jenis_pelayanan", jenis_pelayanan);
                params.put("harga", harga);
                params.put("tanggal_booking", tanggal_booking);
                params.put("jam_booking", jam_booking);
                params.put("upload", encodeImageString);

                return params;
            }
        };

        // Buat antrian permintaan Volley dan tambahkan permintaan ke antrian
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
