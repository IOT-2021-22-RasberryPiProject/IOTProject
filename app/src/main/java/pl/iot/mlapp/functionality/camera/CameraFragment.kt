package pl.iot.mlapp.functionality.camera

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.iot.mlapp.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CameraFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCameraBitmap()
    }

    private fun observeCameraBitmap() = viewModel.bitmapLiveData.observe(viewLifecycleOwner) { updateCameraView(it) }

    private fun updateCameraView(bitmap: Bitmap) =
        binding.cameraView.apply {
            setImageBitmap(bitmap)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}