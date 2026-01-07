package ru.otus.cookbook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import ru.otus.cookbook.data.Recipe
import ru.otus.cookbook.databinding.FragmentRecipeBinding
import coil.load
import ru.otus.cookbook.R

class RecipeFragment : Fragment() {

    private val args: RecipeFragmentArgs by navArgs()
    private val recipeId: Int get() = args.recipeId

    private val binding = FragmentBindingDelegate<FragmentRecipeBinding>(this)
    private val model: RecipeFragmentViewModel by viewModels(extrasProducer = {
        MutableCreationExtras(defaultViewModelCreationExtras).apply {
            set(RecipeFragmentViewModel.RECIPE_ID_KEY, recipeId)
        }
    }, factoryProducer = { RecipeFragmentViewModel.Factory })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.bind(container, FragmentRecipeBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            model.recipe.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect(::displayRecipe)
        }

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.recipeFragment)
        navBackStackEntry.savedStateHandle.getLiveData<Boolean>("DELETE_CONFIRMED")
            .observe(viewLifecycleOwner) { isConfirmed ->
                if (isConfirmed == true) {
                    navBackStackEntry.savedStateHandle.remove<Boolean>("DELETE_CONFIRMED")
                    model.delete()
                    if (!findNavController().popBackStack(R.id.cookbookFragment, false)) {
                        findNavController().popBackStack()
                    }
                }
            }

        binding.withBinding {
            btnDelete.setOnClickListener {
                val action =
                    RecipeFragmentDirections.actionRecipeFragmentToDeleteRecipeDialogFragment(
                        getTitle()
                    )
                findNavController().navigate(action)
            }
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun displayRecipe(recipe: Recipe) = binding.withBinding {
        toolbarRecipeTitle.text = recipe.title
        recipeTitle.text = recipe.title
        recipeSubhead.text = recipe.description
        recipeDescription.text = recipe.steps.joinToString("\n\n") { "• $it" }
        recipeImage.load(recipe.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_placeholder_cookbook)
            error(R.drawable.ic_placeholder_cookbook)
        }
    }

    private fun getTitle(): String = model.recipe.value.title
}