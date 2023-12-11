package com.example.karyawanapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.karyawanapp.databinding.ItemBioskopBinding

class AdapterBioskop(private val data: List<ModelBioskop>) :
    RecyclerView.Adapter<AdapterBioskop.BioskopViewHolder>() {

    class BioskopViewHolder(private val binding: ItemBioskopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bioskop: ModelBioskop) {
            binding.tvNama.text = bioskop.nama
            binding.tvJadwal.text = bioskop.jadwal
            binding.tvStudio.text = bioskop.studio

            Glide.with(binding.root.context)
                .load(bioskop.photoUrl)
                .centerCrop()
                .into(binding.ivPhotos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BioskopViewHolder {
        val binding = ItemBioskopBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BioskopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BioskopViewHolder, position: Int) {
        val data = data[position]
        holder.bind(data)

    }

    override fun getItemCount(): Int = data.size
}