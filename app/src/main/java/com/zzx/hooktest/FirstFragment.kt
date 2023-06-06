package com.zzx.hooktest

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.zzx.hooktest.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button1.setOnClickListener {
            val intent = Intent(context, RegisterdActivity::class.java)
            intent.putExtra("test","你好");
            startActivity(intent)
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.button2.setOnClickListener {
            val intent = Intent(context, PluginTestActivity::class.java)
            intent.putExtra("test","你好");
            startActivity(intent)
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.button3.setOnClickListener {
//            val ScrollingActivityClass = Class.forName("com.zzx.plugin.ScrollingActivity")
//            val intent = Intent(context, ScrollingActivityClass)
//            intent.putExtra("plugName", "pluginapk.apk")
            val intent = Intent()
            intent.component = ComponentName("com.zzx.hooktest", "com.zzx.plugin.ScrollingActivity")
            startActivity(intent)
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}