package ru.otus.cookbook.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.otus.cookbook.R

class DeleteRecipeDialogFragment : DialogFragment() {

    private val args: DeleteRecipeDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).setTitle("Удалить рецепт?")
            .setMessage("Вы уверены, что хотите удалить рецепт «${args.recipeTitle}»?")
            .setPositiveButton("Удалить") { _, _ ->
                findNavController().getBackStackEntry(R.id.recipeFragment).savedStateHandle["DELETE_CONFIRMED"] =
                    true
            }.setNegativeButton("Отмена", null).create()
    }
}