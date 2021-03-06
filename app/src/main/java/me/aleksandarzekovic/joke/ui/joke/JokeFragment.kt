package me.aleksandarzekovic.joke.ui.joke

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import me.aleksandarzekovic.joke.R
import me.aleksandarzekovic.joke.databinding.JokeFragmentBinding
import me.aleksandarzekovic.joke.utils.daggerawareviewmodel.DaggerAwareViewModelFactory
import me.aleksandarzekovic.joke.utils.Resource
import javax.inject.Inject

class JokeFragment : DaggerFragment() {

    companion object {
        fun newInstance() = JokeFragment()
    }

    @Inject
    lateinit var jokeViewModelFactory: DaggerAwareViewModelFactory

    @Inject
    lateinit var viewModel: JokeViewModel

    lateinit var binding: JokeFragmentBinding

    lateinit var categoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, jokeViewModelFactory)
            .get(JokeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.joke_fragment,
            container,
            false
        )
        categoryName = arguments?.get("category").toString()
        binding.category = categoryName
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getJokes(categoryName)
        viewModel.joke.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.jokeProgressBar.visibility = View.INVISIBLE
                    binding.joke = it.data
                }

                is Resource.Failure -> {
                    binding.jokeProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(this.context, "${it.throwable.message}", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Loading -> {
                    binding.jokeProgressBar.visibility = View.VISIBLE
                }
            }
        })
    }

}