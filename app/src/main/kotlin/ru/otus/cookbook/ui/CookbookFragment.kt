package ru.otus.cookbook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.otus.cookbook.data.RecipeListItem
import ru.otus.cookbook.databinding.FragmentCookbookBinding

class CookbookFragment : Fragment() {

    private val binding = FragmentBindingDelegate<FragmentCookbookBinding>(this)
    private val model: CookbookFragmentViewModel by viewModels { CookbookFragmentViewModel.Factory }

    private var adapter: RecipeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.bind(
        container, FragmentCookbookBinding::inflate
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.withBinding {
            btnClose.setOnClickListener {
                requireActivity().finish()
            }
        }
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            model.recipeList.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect(::onRecipeListUpdated)
        }
    }

    private fun setupRecyclerView() = binding.withBinding {
        adapter = RecipeAdapter { recipeId ->
            val action = CookbookFragmentDirections.actionCookbookFragmentToRecipeFragment(recipeId)
            findNavController().navigate(action)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    private fun onRecipeListUpdated(recipeList: List<RecipeListItem>) {
        adapter?.items = recipeList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}