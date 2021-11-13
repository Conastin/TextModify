package indi.conastin.textmodify.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import indi.conastin.textmodify.adapter.AppMethodAdapter
import indi.conastin.textmodify.databinding.GlobalMethodBinding
import indi.conastin.textmodify.databse.HookMethodDatabase
import indi.conastin.textmodify.viewmodel.InventoryViewModel

class GlobalMethodActivity : AppCompatActivity() {

    private lateinit var binding: GlobalMethodBinding
    private lateinit var viewModel: InventoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GlobalMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val database = HookMethodDatabase.getDatabase(this)
        viewModel = InventoryViewModel(database.HookMethodDao())
        val adapter = AppMethodAdapter {
            var intent = Intent(binding.root.context, AddRule::class.java)
            intent.putExtra("id", it.id)
            intent.putExtra("packageName", "global")
            intent.putExtra("originText", it.originText)
            intent.putExtra("newText", it.newText)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        viewModel.globalMethod.observe(this) {
            it.let {
                adapter.submitList(it)
            }
        }

        binding.addRule.setOnClickListener {
            var intent = Intent(binding.root.context, AddRule::class.java)
            intent.putExtra("packageName", "global")
            startActivity(intent)
        }
    }
}