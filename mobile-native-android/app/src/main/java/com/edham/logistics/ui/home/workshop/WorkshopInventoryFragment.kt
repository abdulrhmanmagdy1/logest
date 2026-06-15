package com.edham.logistics.ui.home.workshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edham.logistics.R
import com.edham.logistics.ui.home.workshop.adapter.InventoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkshopInventoryFragment : Fragment() {

    private val viewModel: WorkshopViewModel by viewModels()
    private lateinit var rvInventory: RecyclerView
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workshop_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        rvInventory = view.findViewById(R.id.rvInventory)
        rvInventory.layoutManager = LinearLayoutManager(requireContext())
        adapter = InventoryAdapter(emptyList())
        rvInventory.adapter = adapter
        
        observeViewModel(view)
        viewModel.loadInventory()
    }

    private fun observeViewModel(view: View) {
        viewModel.inventory.observe(viewLifecycleOwner) { parts ->
            adapter.updateData(parts)
            
            val lowStockCount = parts.count { it.quantity < it.minQuantity }
            view.findViewById<View>(R.id.cardLowStock).visibility = 
                if (lowStockCount > 0) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
