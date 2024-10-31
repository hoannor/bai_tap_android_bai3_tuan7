package com.example.bai3_tuan7

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.widget.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var edtMSSV: EditText
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var btnDate: Button
    private lateinit var checkBoxSports: CheckBox
    private lateinit var checkBoxMovies: CheckBox
    private lateinit var checkBoxMusic: CheckBox
    private lateinit var checkBoxAgree: CheckBox
    private lateinit var btnSubmit: Button
    private lateinit var tvError: TextView
    private lateinit var addressHelper: AddressHelper
    private lateinit var spinnerProvince: Spinner
    private lateinit var spinnerDistrict: Spinner
    private lateinit var spinnerWard: Spinner

    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize AddressHelper
        addressHelper = AddressHelper(resources)

        // Ánh xạ các view
        edtMSSV = findViewById(R.id.edtMSSV)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        btnDate = findViewById(R.id.btnDate)
        spinnerWard = findViewById(R.id.spinnerWard)
        spinnerDistrict = findViewById(R.id.spinnerDistrict)
        spinnerProvince = findViewById(R.id.spinnerProvince)
        checkBoxSports = findViewById(R.id.checkBoxSports)
        checkBoxMovies = findViewById(R.id.checkBoxMovies)
        checkBoxMusic = findViewById(R.id.checkBoxMusic)
        checkBoxAgree = findViewById(R.id.checkBoxAgree)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvError = findViewById(R.id.tvError)

        // Load provinces
        loadProvinces()

        // Chọn ngày sinh
        btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                btnDate.text = selectedDate
            }, year, month, day)
            datePicker.show()
        }

        // Xử lý nút Submit
        btnSubmit.setOnClickListener {
            if (validateInput()) {
                // Thực hiện hành động khi dữ liệu hợp lệ
                tvError.visibility = TextView.GONE
                Toast.makeText(this, "Thông tin hợp lệ!", Toast.LENGTH_SHORT).show()
            } else {
                tvError.visibility = TextView.VISIBLE
                tvError.text = "Vui lòng điền đầy đủ thông tin và đồng ý với điều khoản."
            }
        }
    }

    private fun validateInput(): Boolean {
        // Kiểm tra xem các trường có được điền đầy đủ không
        if (edtMSSV.text.isEmpty() || edtName.text.isEmpty() || edtEmail.text.isEmpty() || edtPhone.text.isEmpty() || selectedDate == null) {
            return false
        }

        // Kiểm tra giới tính có được chọn không
        if (radioGroupGender.checkedRadioButtonId == -1) {
            return false
        }

        // Kiểm tra điều khoản đã được đồng ý
        if (!checkBoxAgree.isChecked) {
            return false
        }

        return true
    }

    private fun loadProvinces() {
        val provinces = addressHelper.getProvinces()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProvince.adapter = adapter

        // Set on item selected listener to load districts when province changes
        spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedProvince = provinces[position]
                loadDistricts(selectedProvince)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadDistricts(province: String) {
        val districts = addressHelper.getDistricts(province)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDistrict.adapter = adapter

        // Set on item selected listener to load wards when district changes
        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedDistrict = districts[position]
                loadWards(province, selectedDistrict)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadWards(province: String, district: String) {
        val wards = addressHelper.getWards(province, district)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wards)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWard.adapter = adapter
    }
}
