package com.example.karyawanapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.karyawanapp.DetailActivity.Companion.DATA_GAJI
import com.example.karyawanapp.DetailActivity.Companion.DATA_JABATAN
import com.example.karyawanapp.DetailActivity.Companion.DATA_NAMA
import com.example.karyawanapp.DetailActivity.Companion.DATA_TGL_MASUK
import com.example.karyawanapp.databinding.ItemEmployeeeBinding

class Adapter(private val data: List<ModelKaryawan>) :
    RecyclerView.Adapter<Adapter.EmployeeViewHolder>() {

    class EmployeeViewHolder(private val binding: ItemEmployeeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: ModelKaryawan) {
            binding.tvNama.text = employee.employee_name
            binding.tvJabatan.text = employee.jabatan

            binding.root.setOnClickListener {
                val detailIntent = Intent(it.context, DetailActivity::class.java)
                detailIntent.putExtra(DATA_NAMA, employee.employee_name)
                detailIntent.putExtra(DATA_JABATAN, employee.jabatan)
                detailIntent.putExtra(DATA_TGL_MASUK, employee.tgl_kerja)
                detailIntent.putExtra(DATA_GAJI, employee.gaji)
                it.context.startActivity(detailIntent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val data = data[position]
        holder.bind(data)

    }

    override fun getItemCount(): Int = data.size
}